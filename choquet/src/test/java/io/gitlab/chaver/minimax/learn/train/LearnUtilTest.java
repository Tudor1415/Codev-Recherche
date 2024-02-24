package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.io.Alternative;
import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.util.RandomUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

class LearnUtilTest {

    private RandomUtil random = RandomUtil.getInstance();

    /*@Test
    void test2() throws Exception {
        int n = 100000;
        double[][] vectors = random.generateRandomVectors(n, 5);
        List<IAlternative> alternatives = vectorsToAlternatives(vectors);
        List<List<IAlternative>> skylineLists = computeSkylines(alternatives);
        System.out.println("ok");
    }*/

}