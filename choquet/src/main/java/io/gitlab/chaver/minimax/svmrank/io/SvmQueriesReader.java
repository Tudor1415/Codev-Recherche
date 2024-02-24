package io.gitlab.chaver.minimax.svmrank.io;

import io.gitlab.chaver.minimax.io.Alternative;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Read files with queries data
 * See <a href="https://www.microsoft.com/en-us/research/project/letor-learning-rank-information-retrieval/letor-4-0/">letor</a>
 */
public class SvmQueriesReader {

    private String path;

    public SvmQueriesReader(String path) {
        this.path = path;
    }

    private void checkVectorSize(List<SvmQuery> queries) {
        int vectorSize = queries.get(0).getInputs().get(0).getAlternative().getVector().length;
        for (SvmQuery query : queries) {
            for (SvmQueryInput input : query.getInputs()) {
                if (vectorSize != input.getAlternative().getVector().length) {
                    throw new RuntimeException("Vectors must all have the same size");
                }
            }
        }
    }

    public List<SvmQuery> readData() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            Map<Integer, SvmQuery> queryMap = new HashMap<>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(" ");
                int target = Integer.parseInt(splitLine[0]);
                int qid = Integer.parseInt(splitLine[1].split(":")[1]);
                List<Double> vector = new LinkedList<>();
                int i = 2;
                while (i < splitLine.length && splitLine[i].charAt(0) != '#') {
                    vector.add(Double.parseDouble(splitLine[i].split(":")[1]));
                    i++;
                }
                if (!queryMap.containsKey(qid)) {
                    queryMap.put(qid, new SvmQuery(qid, new LinkedList<>()));
                }
                queryMap
                        .get(qid)
                        .getInputs()
                        .add(new SvmQueryInput(new Alternative(vector.stream().mapToDouble(v -> v).toArray()), target));
            }
            List<SvmQuery> queries = new ArrayList<>(queryMap.values());
            checkVectorSize(queries);
            return queries;
        }
    }
}
