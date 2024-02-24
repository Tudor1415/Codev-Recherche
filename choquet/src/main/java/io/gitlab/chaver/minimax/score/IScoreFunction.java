package io.gitlab.chaver.minimax.score;

public interface IScoreFunction<T> {

    double computeScore(T t);
}
