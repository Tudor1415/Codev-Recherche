package io.gitlab.chaver.minimax.svmrank.algorithm;

import io.gitlab.chaver.minimax.svmrank.io.SvmQuery;
import io.gitlab.chaver.minimax.svmrank.io.SvmQueryInput;
import lombok.AllArgsConstructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.writeQueriesToFile;

/**
 * SVMRank calling from Java (see https://www.cs.cornell.edu/people/tj/svm_light/svm_rank.html)
 */
@AllArgsConstructor
public class SvmRank implements Callable<double[]> {

    private List<SvmQuery> queries;
    private File trainingDataFile;
    private File modelFile;
    private double regularisationParameter;

    /** path of the svm_rank_learn command */
    public static String SVM_RANK_LEARN = "svm_rank_linux64/svm_rank_learn";

    private double[] parseModelFile(int nbCriteria) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(modelFile.getAbsolutePath()));
        String[] lastLine = lines.get(lines.size() - 1).split(" ");
        double[] weights = new double[nbCriteria];
        for (int i = 0; i < lastLine.length; i++) {
            if (lastLine[i].startsWith("#") || lastLine[i].split(":").length < 2) {
                continue;
            }
            String[] split = lastLine[i].split(":");
            weights[Integer.parseInt(split[0]) - 1] = Double.parseDouble(split[1]);
        }
        return weights;
    }

    @Override
    public double[] call() throws Exception {
        writeQueriesToFile(queries, trainingDataFile);
        ProcessBuilder builder = new ProcessBuilder(SVM_RANK_LEARN, "-c", Double.toString(regularisationParameter),
                trainingDataFile.getAbsolutePath(), modelFile.getAbsolutePath());
        Process process = builder.start();
        process.waitFor();
        return parseModelFile(queries.get(0).getInputs().get(0).getAlternative().getVector().length);
    }
}
