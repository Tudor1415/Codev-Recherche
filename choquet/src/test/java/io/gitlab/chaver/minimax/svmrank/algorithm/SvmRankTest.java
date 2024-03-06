package io.gitlab.chaver.minimax.svmrank.algorithm;

import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;
import io.gitlab.chaver.minimax.svmrank.io.SvmQuery;
import io.gitlab.chaver.minimax.svmrank.io.SvmQueryInput;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

class SvmRankTest {

    private SvmQuery createQuery(int qid, int[] targets, double[][] features) {
        List<SvmQueryInput> inputs = new ArrayList<>();
        for (int i = 0; i < targets.length; i++) {
            inputs.add(new SvmQueryInput(new Alternative(features[i]), targets[i]));
        }
        return new SvmQuery(qid, inputs);
    }

    @Test
    void testSVM() throws Exception {
        List<SvmQuery> queries = new ArrayList<>();
        int[] query1Target = {3, 2, 1, 1};
        double[][] query1Features = {
                {1, 1, 0, 0.2, 0},
                {0, 0, 1, 0.1, 1},
                {0, 1, 0, 0.4, 0},
                {0, 0, 1, 0.3, 0}
        };
        queries.add(createQuery(1, query1Target, query1Features));
        int[] query2Target = {1, 2, 1, 1};
        double[][] query2Features = {
                {0, 0, 1, 0.2, 0},
                {1, 0, 1, 0.4, 0},
                {0, 0, 1, 0.1, 0},
                {0, 0, 1, 0.2, 0}
        };
        queries.add(createQuery(2, query2Target, query2Features));
        int[] query3Target = {2, 3, 4, 1};
        double[][] query3Features = {
                {0, 0, 1, 0.1, 1},
                {1, 1, 0, 0.3, 0},
                {1, 0, 0, 0.4, 1},
                {0, 1, 1, 0.5, 0}
        };
        queries.add(createQuery(3, query3Target, query3Features));
        File trainingDataFile = File.createTempFile("train", ".txt");
        File modelFile = File.createTempFile("model", ".txt");
        SvmRank svmRank = new SvmRank(queries, trainingDataFile, modelFile, 3d);
        double[] weights = svmRank.call();
        assertArrayEquals(new double[]{1.521512, -0.057497051, -0.52151203, -0.17125149, 0.96401501}, weights);
        IScoreFunction<IAlternative> f = new LinearScoreFunction(weights);
        List<IAlternative> test = vectorsToAlternatives(new double[][]{
                {1, 0, 0, 0.2, 1},
                {1, 1, 0, 0.3, 0},
                {0, 0, 0, 0.2, 1},
                {0, 0, 1, 0.2, 0}
        });
        double[] expectedScores = {2.45127674, 1.41263953, 0.92976471, -0.55576233};
        for (int i = 0; i < test.size(); i++) {
            assertEquals(expectedScores[i], f.computeScore(test.get(i)), 0.001);
        }
    }

}