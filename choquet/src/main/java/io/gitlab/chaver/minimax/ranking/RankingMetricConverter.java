package io.gitlab.chaver.minimax.ranking;

import picocli.CommandLine.ITypeConverter;

public class RankingMetricConverter implements ITypeConverter<RankingMetric> {

    @Override
    public RankingMetric convert(String s) throws Exception {
        if (s.equals(KendallConcordanceCoeff.TYPE)) {
            return new KendallConcordanceCoeff();
        }
        if (s.equals(SpearmanRankCorrelationCoefficient.TYPE)) {
            return new SpearmanRankCorrelationCoefficient();
        }
        if (s.startsWith(RecallMetric.TYPE)) {
            return new RecallMetric(Integer.parseInt(s.substring(RecallMetric.TYPE.length())));
        }
        return null;
    }
}
