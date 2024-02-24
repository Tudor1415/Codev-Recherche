package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.score.IScoreFunction;

import java.util.List;
import java.util.Set;

/**
 * Useful for heuristics to know the current state of the learning algorithm
 */
public interface LearnStep {

    /**
     * Get current score function
     * @return current score function for this step
     */
    IScoreFunction<IAlternative> getCurrentScoreFunction();

    /**
     * Get already selected pairs
     * @return the pairs of alternatives idx that have been already selected for this step
     */
    Set<List<Integer>> getSelectedPairs();

    /**
     * Get the list of selected alternatives
     * @return the list of alternatives
     */
    List<IAlternative> getCurrentAlternatives();
}
