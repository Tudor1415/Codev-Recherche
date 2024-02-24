package io.gitlab.chaver.minimax.ranklib.call;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Call RankLib (see <a href="https://sourceforge.net/p/lemur/wiki/RankLib%20How%20to%20use/#eval">this</a>)
 */
public class RanklibCall {

    public static String JAVA_HOME = System.getProperty("java.home") + "/bin/java";
    public static String RANKLIB_PATH = "RankLib/RankLib-2.18.jar";

    public enum Ranker {

        MART(0),
        RankNet(1),
        RankBoost(2),
        AdaRank(3),
        CoordinateAscent(4),
        LambdaMART(6),
        ListNet(7),
        RandomForests(8);
        private int rankerIdx;

        Ranker(int rankerIdx) {
            this.rankerIdx = rankerIdx;
        }

        public int getRankerIdx() {
            return rankerIdx;
        }
    }

    public void trainModel(File trainFile, File validationFile, Ranker rankingAlgorithm, String metric2Optimize,
                           File resultModel, List<String> otherArgs) throws Exception {
        List<String> args = new LinkedList<>(Arrays.asList(JAVA_HOME, "-jar", RANKLIB_PATH,
                "-train", trainFile.getAbsolutePath(), "-ranker",
                Integer.toString(rankingAlgorithm.getRankerIdx()), "-metric2t", metric2Optimize, "-save",
                resultModel.getAbsolutePath()));
        if (validationFile != null) {
            args.addAll(Arrays.asList("-validate", validationFile.getAbsolutePath()));
        }
        args.addAll(otherArgs);
        //System.out.println(args);
        ProcessBuilder builder = new ProcessBuilder(args);
        Process process = builder.start();
        process.waitFor();
    }

    private double[] parseScoreFile(File scoreFile) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
            List<Double> scores = new ArrayList<>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                scores.add(Double.parseDouble(line.split("\t")[2]));
            }
            return scores.stream().mapToDouble(d -> d).toArray();
        }
    }

    public double[] computeScores(File resultModel, File testFile, File scoreFile) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(JAVA_HOME, "-jar", RANKLIB_PATH, "-load",
                resultModel.getAbsolutePath(), "-rank", testFile.getAbsolutePath(), "-score", scoreFile.getAbsolutePath());
        Process process = builder.start();
        process.waitFor();
        return parseScoreFile(scoreFile);
    }


}
