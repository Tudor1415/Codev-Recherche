package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;
import io.gitlab.chaver.minimax.util.RandomUtil;

import java.util.List;

/**
 * Given several batches of alternatives, return a random one
 */
public class BatchAlternativesSelector implements AlternativesSelector {

    private List<List<IAlternative>> batches;
    private final RandomUtil random = RandomUtil.getInstance();

    public BatchAlternativesSelector(List<List<IAlternative>> batches) {
        this.batches = batches;
    }

    @Override
    public List<IAlternative> selectAlternatives(LearnStep step) {
        return batches.remove(random.nextInt(batches.size()));
    }
}
