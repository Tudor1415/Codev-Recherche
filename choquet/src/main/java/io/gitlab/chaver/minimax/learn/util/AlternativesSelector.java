package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.train.LearnStep;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface AlternativesSelector {

    List<IAlternative> selectAlternatives(LearnStep step);
}
