package io.gitlab.chaver.minimax.learn.util;

import io.gitlab.chaver.minimax.io.IAlternative;
import io.gitlab.chaver.minimax.learn.oracle.ScoreFunctionOracle;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.ranking.RankingMetric;
import io.gitlab.chaver.minimax.score.IScoreFunction;
import lombok.Getter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import static io.gitlab.chaver.minimax.learn.train.LearnUtil.*;

public class RankingMetricsDashboard implements PropertyChangeListener {

    private List<IAlternative> alternatives;
    private List<RankingMetric> rankingMetrics;
    private Ranking<IAlternative> refRanking;
    @Getter
    private Map<String, List<Double>> metricValues = new HashMap<>();
    private Map<String, String> metricLabels;

    public RankingMetricsDashboard(List<RankingMetric> rankingMetrics, Ranking<IAlternative> refRanking, Map<String, String> metricLabels) {
        this.rankingMetrics = rankingMetrics;
        this.metricLabels = metricLabels;
        for (RankingMetric metric : rankingMetrics) {
            metricValues.put(metricLabels.get(metric.getName()), new LinkedList<>());
        }
        this.refRanking = refRanking;
        alternatives = Arrays.asList(refRanking.getObjects());
    }

    public void computeMetrics(Ranking<IAlternative> predictedRanking) {
        for (RankingMetric metric : rankingMetrics) {
            metricValues.get(metricLabels.get(metric.getName())).add(metric.compute(refRanking, predictedRanking));
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        IScoreFunction<IAlternative> func = (IScoreFunction<IAlternative>) evt.getNewValue();
        Ranking<IAlternative> predictedRanking = computeRankingWithOracle(new ScoreFunctionOracle(func), alternatives);
        computeMetrics(predictedRanking);
    }
}
