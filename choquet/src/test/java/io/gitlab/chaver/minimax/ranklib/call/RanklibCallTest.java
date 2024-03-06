package io.gitlab.chaver.minimax.ranklib.call;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

class RanklibCallTest {

    @Test
    void test() throws Exception {
        File trainingFile = new File("RankLib/examples/ex_passive.txt");
        RanklibCall ranklibCall = new RanklibCall();
        File resultModel = File.createTempFile("model", ".txt");
        ranklibCall.trainModel(trainingFile, null, RanklibCall.Ranker.MART, "RR@10", resultModel, new ArrayList<>());
        assertTrue(Files.readAllLines(Paths.get(resultModel.getAbsolutePath())).size() > 0);
    }

}