package io.gitlab.chaver.minimax.learn.train;

import io.gitlab.chaver.minimax.score.FunctionParameters;
import lombok.Setter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Learn ranking of alternatives
 */
public abstract class AbstractRankingLearning {

    /** Time limit of the learning process (in seconds, 0 means no limit) */
    @Setter
    protected int timeLimit;
    /** Number of measures to evaluate each alternative */
    @Setter
    protected int nbMeasures;
    /** To notify observers */
    protected PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Learn parameters of a function to rank the alternatives
     * @return parameters of the function
     * @throws Exception
     */
    public abstract FunctionParameters learn() throws Exception;

    /**
     * Parameters of the function in case of Timeout
     * @return Timeout function params
     */
    protected FunctionParameters timeOut() {
        FunctionParameters params = new FunctionParameters();
        params.setTimeToLearn(timeLimit);
        params.setTimeOut(true);
        return params;
    }

    public void addObserver(PropertyChangeListener observer) {
        support.addPropertyChangeListener(observer);
    }
}
