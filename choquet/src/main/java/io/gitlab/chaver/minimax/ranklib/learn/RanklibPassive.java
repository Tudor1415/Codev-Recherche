package io.gitlab.chaver.minimax.ranklib.learn;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.ranklib.call.RanklibCall;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public class RanklibPassive {

    private Ranking<IAlternative> expectedRanking;
    @Getter
    private RanklibCall.Ranker ranker;
    private String metric2Optimize;
    private RanklibCall ranklibCall = new RanklibCall();

    public RanklibPassive(Ranking<IAlternative> expectedRanking, RanklibCall.Ranker ranker, String metric2Optimize) {
        this.expectedRanking = expectedRanking;
        this.ranker = ranker;
        this.metric2Optimize = metric2Optimize;
    }

    public RankLibResult learn() throws Exception {
        File trainingDataFile = File.createTempFile("training_ranklib", ".txt");
        writeQueriesToFile(Arrays.asList(convertRankingToSvmQuery(expectedRanking, 1)), trainingDataFile);
        File resultModel = File.createTempFile("model", ".txt");
        double start = System.currentTimeMillis();
        ranklibCall.trainModel(trainingDataFile, null, ranker, metric2Optimize, resultModel, new ArrayList<>());
        return new RankLibResult(resultModel, (System.currentTimeMillis() - start) / 1000d);
    }

    public double[] computeScore(File resultModel, List<IAlternative> testAlternatives) throws Exception {
        File testFile = File.createTempFile("test_ranklib", ".txt");
        File scoreFile = File.createTempFile("score_ranklib", ".txt");
        Ranking<IAlternative> testRanking = new Ranking<>(
                IntStream.range(0, testAlternatives.size()).toArray(),
                testAlternatives.stream().toArray(IAlternative[]::new));
        writeQueriesToFile(Arrays.asList(convertRankingToSvmQuery(testRanking, 1)), testFile);
        return ranklibCall.computeScores(resultModel, testFile, scoreFile);
    }


}
