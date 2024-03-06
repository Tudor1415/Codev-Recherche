package io.gitlab.chaver.minimax.learn.train;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.gitlab.chaver.chocotools.io.JsonLinesResultReader;
import io.gitlab.chaver.chocotools.io.ProblemResult;
import io.gitlab.chaver.chocotools.io.ProblemResultReader;
import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput2;
import io.gitlab.chaver.minimax.kappalab.io.KappalabMethod;
import io.gitlab.chaver.minimax.learn.util.MapAlternativeRules;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.ranking.RankingMetric;
import io.gitlab.chaver.minimax.rules.io.RuleMeasures;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.minimax.svmrank.io.SvmQuery;
import io.gitlab.chaver.minimax.svmrank.io.SvmQueryInput;
import io.gitlab.chaver.minimax.util.RandomUtil;
import io.gitlab.chaver.minimax.util.RuleWithLabels;
import io.gitlab.chaver.mining.rules.io.ArMeasuresView;
import io.gitlab.chaver.mining.rules.io.AssociationRule;
import io.gitlab.chaver.mining.rules.io.IRule;
import io.gitlab.chaver.mining.rules.measure.RuleMeasure;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LearnUtil {

    public static Ranking<IAlternative> computeRankingWithOracle(Comparator<IAlternative> oracle,
                                                                 IAlternative[] alternatives) {
        int[] ranking = IntStream.range(0, alternatives.length)
                .boxed()
                .sorted((i, j) -> oracle.compare(alternatives[i], alternatives[j]))
                .mapToInt(i -> i)
                .toArray();
        return new Ranking<>(ranking, alternatives);
    }

    public static Ranking<IAlternative> computeRankingWithOracle(Comparator<IAlternative> oracle,
                                                                 List<IAlternative> alternatives) {
        int[] ranking = IntStream.range(0, alternatives.size())
                .boxed()
                .sorted((i, j) -> oracle.compare(alternatives.get(i), alternatives.get(j)))
                .mapToInt(i -> i)
                .toArray();
        return new Ranking<>(ranking, alternatives.stream().toArray(IAlternative[]::new));
    }


    public static ProblemResult<AssociationRule, ArMeasuresView> readRules(String rulesPath) throws Exception {
        ProblemResultReader<AssociationRule, ArMeasuresView> reader = new JsonLinesResultReader<>(rulesPath, AssociationRule.class);
        return reader.readResult(AssociationRule[].class,
                ArMeasuresView.class);
    }

    public static Map<IAlternative, List<IRule>> mapAlternativeToRules(String rulesPath, RuleMeasure[] measures)
            throws Exception {
        ProblemResultReader<AssociationRule, ArMeasuresView> reader = new JsonLinesResultReader<>(rulesPath, AssociationRule.class);
        ProblemResult<AssociationRule, ArMeasuresView> res = reader.readResult(AssociationRule[].class,
                ArMeasuresView.class);
        int nbTransactions = res.getProperties().getNbTransactions();
        int nbMeasures = measures.length;
        Map<IAlternative, List<IRule>> mapAlternativeToRule = new HashMap<>();
        int j = 0;
        for (IRule rule : res.getSolutions()) {
            double[] values = Arrays.stream(measures).mapToDouble(m -> m.compute(rule, nbTransactions)).toArray();
            Alternative a = new Alternative(values);
            if (!mapAlternativeToRule.containsKey(a)) {
                mapAlternativeToRule.put(a, new LinkedList<>());
            }
            mapAlternativeToRule.get(a).add(rule);
        }
        return mapAlternativeToRule;
    }

    public static Map<IAlternative, List<Integer>> mapAlternativeToRulesIdx(String rulesPath, String[] measureNames)
            throws Exception {
        ProblemResultReader<AssociationRule, ArMeasuresView> reader = new JsonLinesResultReader<>(rulesPath, AssociationRule.class);
        ProblemResult<AssociationRule, ArMeasuresView> res = reader.readResult(AssociationRule[].class,
                ArMeasuresView.class);
        int nbTransactions = res.getProperties().getNbTransactions();
        Map<IAlternative, List<Integer>> mapAlternativeToRule = new HashMap<>();
        int i = 0;
        for (IRule rule : res.getSolutions()) {
            double[] values = new RuleMeasures(rule, nbTransactions, 0.1).computeMeasures(measureNames);
            Alternative a = new Alternative(values);
            if (!mapAlternativeToRule.containsKey(a)) {
                mapAlternativeToRule.put(a, new LinkedList<>());
            }
            mapAlternativeToRule.get(a).add(i++);
        }
        return mapAlternativeToRule;
    }

    public static MapAlternativeRules convertToJSONMap(Map<IAlternative, List<Integer>> mapAlternativeToRulesIdx) {
        double[][] alternatives = new double[mapAlternativeToRulesIdx.size()][];
        int[][] rulesIdx = new int[mapAlternativeToRulesIdx.size()][];
        int i = 0;
        for (IAlternative alt : mapAlternativeToRulesIdx.keySet()) {
            alternatives[i] = alt.getVector();
            rulesIdx[i] = mapAlternativeToRulesIdx.get(alt).stream().mapToInt(j -> j).toArray();
            i++;
        }
        return new MapAlternativeRules(alternatives, rulesIdx);
    }

    public static IAlternative[] getAlternatives(Map<IAlternative, List<IRule>> mapAlternativeToRules) {
        IAlternative[] alternatives = new IAlternative[mapAlternativeToRules.size()];
        int i = 0;
        for (IAlternative alt : mapAlternativeToRules.keySet()) {
            alternatives[i++] = alt;
        }
        return alternatives;
    }

    public static FunctionParameters readFunction(String path) throws IOException {
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        FunctionParameters func = gson.fromJson(reader, FunctionParameters.class);
        reader.close();
        return func;
    }

    /**
     * Given a list of rules, return a new list such that all rules are distinct
     * @param rules
     * @return
     */
    public static List<IRule> getDistinctRules(List<IRule> rules) {
        List<IRule> distinctRules = new ArrayList<>();
        Set<List<Integer>> distinctVals = new HashSet<>();
        for (IRule rule : rules) {
            if (distinctVals.add(Arrays.asList(rule.getFreqX(), rule.getFreqY(), rule.getFreqZ()))) {
                distinctRules.add(rule);
            }
        }
        return distinctRules;
    }

    /**
     * Read a file of association rules where each line represents a rule in JSON format
     * @param path
     * @return
     * @throws IOException
     */
    public static List<IRule> readAssociationRules(String path) throws IOException {
        Gson gson = new Gson();
        List<IRule> rules = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = reader.readLine()) != null) {
            rules.add(gson.fromJson(line, AssociationRule.class));
        }
        reader.close();
        return rules;
    }

    /**
     * Read a file of rules extracted with Coron platform
     * See <a href="http://coron.wikidot.com/core:assrulex">assrulex</a>
     * @param path
     * @return
     * @throws IOException
     */
    public static List<IRule> readAssociationRulesCoron(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        List<IRule> rules = new LinkedList<>();
        String line;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{(.*?)\\}");
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.isEmpty()) continue;
            Matcher matchPatterns = pattern.matcher(line);
            matchPatterns.find();
            int[] x = Arrays.stream(matchPatterns.group(1).replace(" ", "").split(","))
                    .mapToInt(s -> Integer.parseInt(s))
                    .toArray();
            matchPatterns.find();
            int[] y = Arrays.stream(matchPatterns.group(1).replace(" ", "").split(","))
                    .mapToInt(s -> Integer.parseInt(s))
                    .toArray();
            String[] lineSplit = line.split(" ");
            int freqX = -1;
            int freqY = -1;
            int freqZ = -1;
            for (String s : lineSplit) {
                if (s.startsWith("(supp=")) freqZ = Integer.parseInt(s.split("=")[1]);
                else if (s.startsWith("suppL=")) freqX = Integer.parseInt(s.split("=")[1]);
                else if (s.startsWith("suppR=")) freqY = Integer.parseInt(s.split("=")[1]);
            }
            assert freqX != -1 && freqY != -1 && freqZ != -1;
            rules.add(new AssociationRule(x, y, freqX, freqY, freqZ));

        }
        reader.close();
        return rules;
    }

    public static int getNbTransactions(String path) throws IOException {
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        JsonObject obj = gson.fromJson(reader, JsonObject.class);
        reader.close();
        return obj.get("nbTransactions").getAsInt();
    }

    public static List<IAlternative> computeAlternatives(List<IRule> rules, String[] measureNames,
                                                         int nbTransactions, double smoothCounts) {
        List<IAlternative> alternatives = new ArrayList<>(rules.size());
        int i = 0;
        for (IRule rule : rules) {
            alternatives.add(new Alternative(new RuleMeasures(rule, nbTransactions, smoothCounts).computeMeasures(measureNames), i++));
        }
        return alternatives;
    }

    public static <T> List<List<T>> splitList(List<T> list, int k) {
        ArrayList<T> arrayList = new ArrayList<>(list);
        List<List<T>> splitList = new ArrayList<>(k);
        int[][] kFolds = RandomUtil.getInstance().kFolds(k, list.size());
        for (int i = 0; i < k; i++) {
            splitList.add(Arrays.stream(kFolds[i]).mapToObj(arrayList::get).collect(Collectors.toList()));
        }
        return splitList;
    }

    public static <T> List<T> mergeLists(List<T>... lists) {
        return Stream.of(lists).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> List<List<T>> getTrainingData(List<T> data, int[][] folds) {
        List<List<T>> trainingData = new ArrayList<>(folds.length);
        for (int i = 0; i < folds.length; i++) {
            trainingData.add(Arrays
                    .stream(folds[i])
                    .mapToObj(data::get)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return trainingData;
    }

    public static <T> List<List<T>> getTestData(List<T> data, int[][] folds) {
        List<List<T>> testData = new ArrayList<>(folds.length);
        for (int i = 0; i < folds.length; i++) {
            Set<Integer> trainFold = Arrays.stream(folds[i]).boxed().collect(Collectors.toSet());
            testData.add(IntStream
                    .range(0, data.size())
                    .filter(j -> !trainFold.contains(j))
                    .mapToObj(data::get)
                    .collect(Collectors.toCollection(ArrayList::new)));
        }
        return testData;
    }

    public static double[] computeRankingMetrics(List<IAlternative> alternatives, Comparator<IAlternative> expected,
                                                 Comparator<IAlternative> predicted, List<RankingMetric> metrics) {
        double[] metricValues = new double[metrics.size()];
        Ranking<IAlternative> expectedRanking = computeRankingWithOracle(expected, alternatives.stream().toArray(IAlternative[]::new));
        Ranking<IAlternative> predictedRanking = computeRankingWithOracle(predicted, alternatives.stream().toArray(IAlternative[]::new));
        for (int i = 0; i < metrics.size(); i++) {
            metricValues[i] = metrics.get(i).compute(expectedRanking, predictedRanking);
        }
        return metricValues;
    }

    public static <T> List<T> selectKFirst(List<List<T>> l, int k) {
        List<T> topK = new ArrayList<>(k);
        for (List<T> l2 : l) {
            for (T t : l2) {
                topK.add(t);
                if (--k == 0) return topK;
            }
        }
        return topK;
    }

    public static List<IAlternative> vectorsToAlternatives(double[][] vectors) {
        return Arrays
                .stream(vectors)
                .map(Alternative::new)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public static <T> List<List<T>> splitMaxSize2(List<T> l, int maxSize) {
        List<Integer> index = IntStream
                .range(0, l.size())
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        RandomUtil.getInstance().shuffle(index);
        int n = l.size() / maxSize + 1;
        List<List<T>> splitList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            List<T> split = new ArrayList<>(maxSize);
            for (int j = 0; j < maxSize; j++) {
                if (i * maxSize + j == l.size()) break;
                split.add(l.get(index.get(i * maxSize + j)));
            }
            splitList.add(split);
        }
        return splitList;
    }

    /**
     * Split a list of list such that each list has a size of maxSize at most
     * Ex : if you have a list l = [l1, l2, l3] such that size(l1) = 7, size(l2) = 4, size(l3) = 12 with maxSize = 5
     * Then l1 will be split in 2 lists and l3 in 3 lists
     * @param l list of list
     * @param maxSize maxSize of each contained list
     * @return a list of list such that each contained list has a size of at most maxSize
     * @param <T> type of the list
     */
    public static <T> List<List<T>> splitMaxSize(List<List<T>> l, int maxSize) {
        List<List<T>> res = new ArrayList<>();
        for (List<T> l2 : l) {
            if (l.size() <= maxSize) {
                res.add(l2);
            }
            else {
                res.addAll(splitMaxSize2(l2, maxSize));
            }
        }
        return res;
    }

    public static SvmQuery convertRankingToSvmQuery(Ranking<IAlternative> ranking, int qid) {
        List<SvmQueryInput> inputs = new ArrayList<>(ranking.getRanking().length);
        List<IAlternative> rankAlternatives = ranking.rankObjects();
        for (int i = 0; i < rankAlternatives.size(); i++) {
            inputs.add(new SvmQueryInput(rankAlternatives.get(i), rankAlternatives.size() - i));
        }
        return new SvmQuery(qid, inputs);
    }

    public static void addRankingToKappalabInput(Ranking<IAlternative> ranking, KappalabInput2 input, double delta) {
        List<IAlternative> rankAlternatives = ranking.rankObjects();
        for (int i = 0; i < rankAlternatives.size() - 1; i++) {
            input.addPreference(rankAlternatives.get(i), rankAlternatives.get(i+1), delta);
        }
    }

    public static KappalabInput convertToKappalabInput(KappalabInput2 input, int kAdditivity, KappalabMethod approach) {
        List<double[]> alternatives = input
                .getAlternatives()
                .stream()
                .map(a -> a.getVector())
                .collect(Collectors.toCollection(ArrayList::new));
        return new KappalabInput(alternatives, input.getPreferences(), kAdditivity, approach);
    }

    public static IAlternative computeBestAlternative(List<IAlternative> alternatives, IScoreFunction<IAlternative> func) {
        IAlternative best = alternatives.get(0);
        double scoreBest = -Double.MAX_VALUE;
        for (IAlternative a : alternatives) {
            double score = func.computeScore(a);
            if (score > scoreBest) {
                best = a;
                scoreBest = score;
            }
        }
        return best;
    }

    public static FunctionParameters errorFunction(String[] errorMessages) {
        FunctionParameters params = new FunctionParameters();
        params.setErrorMessages(errorMessages);
        return params;
    }

    /**
     * Select subset of criteria in alternatives
     * @param alternatives
     * @param criteriaIdx indexes of the criteria
     * @return list of alternatives with the selected index of criteria
     */
    public static List<IAlternative> selectSubsetCriteria(List<IAlternative> alternatives, int[] criteriaIdx) {
        List<IAlternative> subsetAlternatives = new ArrayList<>(alternatives.size());
        for (IAlternative a : alternatives) {
            subsetAlternatives.add(new Alternative(Arrays.stream(criteriaIdx).mapToDouble(j -> a.getVector()[j]).toArray()));
        }
        return subsetAlternatives;
    }

    public static IAlternative[] selectSubsetCriteria(IAlternative[] alternatives, int[] criteriaIdx) {
        IAlternative[] subsetAlternatives = new IAlternative[alternatives.length];
        for (int i = 0; i < alternatives.length; i++) {
            int finalI = i;
            subsetAlternatives[i] = new Alternative(Arrays.stream(criteriaIdx).mapToDouble(j -> alternatives[finalI].getVector()[j]).toArray());
        }
        return subsetAlternatives;
    }

    public static Ranking<IAlternative> getRanking(SvmQuery query) {
        IAlternative[] objects = query
                .getInputs()
                .stream()
                .sorted((o1, o2) -> -Integer.compare(o1.getTarget(), o2.getTarget()))
                .map(input -> input.getAlternative())
                .toArray(IAlternative[]::new);
        int[] ranking = IntStream
                .range(0, objects.length)
                .toArray();
        return new Ranking<>(ranking, objects);

    }

    public static Ranking<IAlternative> getRanking(SvmQuery query, int[] criteriaIdx) {
        IAlternative[] objects = query
                .getInputs()
                .stream()
                .sorted((o1, o2) -> -Integer.compare(o1.getTarget(), o2.getTarget()))
                .map(input -> input.getAlternative())
                .toArray(IAlternative[]::new);
        objects = selectSubsetCriteria(objects, criteriaIdx);
        int[] ranking = IntStream
                .range(0, objects.length)
                .toArray();
        return new Ranking<>(ranking, objects);
    }

    public static Map<IAlternative, IRule> mapAlternativeToRule(List<IAlternative> alternatives, List<IRule> rules) {
        Map<IAlternative, IRule> map = new HashMap<>();
        for (int i = 0; i < alternatives.size(); i++) {
            map.put(alternatives.get(i), rules.get(i));
        }
        return map;
    }

    public static List<IRule> getTopK(Map<IAlternative, IRule> mapAlternativeToRule, int k, Ranking<IAlternative> ranking) {
        return ranking
                .rankObjects()
                .subList(0, k)
                .stream()
                .map(a -> mapAlternativeToRule.get(a))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<RuleWithLabels> convertToRulesWithLabels(List<IRule> rules, Map<Integer, Integer> itemsMap,
                                                                String[] itemLabels) {
        List<RuleWithLabels> ruleWithLabels = new ArrayList<>(rules.size());
        for (int i = 0; i < rules.size(); i++) {
            IRule rule = rules.get(i);
            String[] antLabels = Arrays.stream(rule.getX()).mapToObj(j -> itemLabels[itemsMap.get(j)]).toArray(String[]::new);
            String[] conLabels = Arrays.stream(rule.getY()).mapToObj(j -> itemLabels[itemsMap.get(j)]).toArray(String[]::new);
            ruleWithLabels.add(new RuleWithLabels(antLabels, conLabels, null));
        }
        return ruleWithLabels;
    }

    public static void writeQueriesToFile(List<SvmQuery> queries, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (SvmQuery query : queries) {
                for (SvmQueryInput input : query.getInputs()) {
                    writer.write(input.getTarget() + " qid:" + query.getQid() + " ");
                    double[] features = input.getAlternative().getVector();
                    for (int i = 0; i < features.length; i++) {
                        writer.write((i+1) + ":" + features[i] + " ");
                    }
                    writer.write("\n");
                }
            }
        }
    }

    public static Ranking<IAlternative> computeRankingWithScore(List<IAlternative> alternatives, double[] scores) {
        IAlternative[] altArray = alternatives.stream().toArray(IAlternative[]::new);
        int[] ranking = IntStream
                .range(0, altArray.length)
                .boxed()
                .sorted((i, j) -> -Double.compare(scores[i], scores[j]))
                .mapToInt(i -> i)
                .toArray();
        return new Ranking<>(ranking, altArray);
    }
}
