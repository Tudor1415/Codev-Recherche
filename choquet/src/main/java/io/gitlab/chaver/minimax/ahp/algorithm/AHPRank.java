package io.gitlab.chaver.minimax.ahp.algorithm;

import io.gitlab.chaver.minimax.ranking.KendallConcordanceCoeff;
import io.gitlab.chaver.minimax.ranking.Ranking;
import io.gitlab.chaver.minimax.io.IAlternative;
import lombok.Getter;
import org.apache.commons.math3.util.Precision;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static io.gitlab.chaver.minimax.ahp.util.AHPUtil.*;

public class AHPRank implements Callable<double[]> {

    /** List of rankings by the user */
    private List<Ranking<IAlternative>> userRankings;
    /** Sample size theta (active mode) */
    private int sampleSize;
    /** Number of iterations (active mode) */
    private int nbIterations;

    private int nbMeasures;
    private double[][] pairsComparisons;
    private double[][] ahpMatrix;
    private int l;
    @Getter
    private double[] weights;
    private KendallConcordanceCoeff kendallComputer = new KendallConcordanceCoeff();

    public AHPRank(List<Ranking<IAlternative>> userRankings) {
        this.nbMeasures = userRankings.get(0).getObjects()[0].getVector().length;
        this.userRankings = userRankings;
        this.pairsComparisons = new double[nbMeasures][nbMeasures];
        this.ahpMatrix = new double[nbMeasures][nbMeasures];
        this.weights = new double[nbMeasures];
    }
    // Passive mode

    // Active mode
    /*public AHPRank(List<Integer> measuresIdx, List<MeasureProvider> patterns, int sampleSize, int nbIterations) {
        this.measuresIdx = measuresIdx;
        this.nbMeasures = measuresIdx.size();
        this.patterns = patterns;
        this.sampleSize = sampleSize;
        this.nbIterations = nbIterations;
    }*/

    private void learnWeights(Ranking<IAlternative> userRank) {
        for (int i = 0; i < nbMeasures; i++) {
            for (int j = 0; j < nbMeasures; j++) {
                ahpMatrix[i][j] = 1;
            }
        }
        double[] kendall = new double[nbMeasures];
        for (int i = 0; i < nbMeasures; i++) {
            kendall[i] = kendallComputer.compute(userRank, getRanking(userRank.getObjects(), i));
        }
        for (int i = 0; i < nbMeasures; i++) {
            for (int j = 0; j < nbMeasures; j++) {
                pairsComparisons[i][j] = ((double) l / (l + 1)) * pairsComparisons[i][j];
                pairsComparisons[i][j] += (1d / (l + 1)) * (kendall[i] - kendall[j]);
            }
        }
        scaleValuesAHPMatrix();
        computeWeights();
        l++;
    }

    private void scaleValuesAHPMatrix() {
        for (int i = 0; i < nbMeasures; i++) {
            for (int j = 0; j < nbMeasures; j++) {
                double value = Precision.round(pairsComparisons[i][j], 1);
                if (Math.abs(value) < 0.15) ahpMatrix[i][j] = 1d;
                else {
                    if (value > 0) ahpMatrix[i][j] = value * 10;
                    else ahpMatrix[i][j] = -1 / (value * 10);
                }
            }
        }
    }

    private void computeWeights() {
        AHP ahp = new AHP(nbMeasures);
        ahp.computeWeights(ahpMatrix);
        weights = ahp.getWeights();
    }


    @Override
    public double[] call() throws Exception {
        if (userRankings != null) {
            for (Ranking<IAlternative> ranking : userRankings) {
                learnWeights(ranking);
            }
        }
        // TODO: active mode
        return weights;
    }
}
