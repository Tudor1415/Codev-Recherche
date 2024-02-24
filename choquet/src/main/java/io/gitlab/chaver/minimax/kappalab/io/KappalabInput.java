package io.gitlab.chaver.minimax.kappalab.io;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KappalabInput {

    /** Vectors of the alternatives */
    private List<double[]> alternatives;
    /** ex: [2,1,0.5] means that the alternative of idx 2 is preferred to alternative of idx 1 with a delta = 0.5 */
    private List<Number[]> preferences;
    /** k-additivity of the model */
    private int k;
    /** ex: mv(Minimum Variance), gls(Generalized Least Squares) */
    private KappalabMethod approachType;
    /** Precision (number of significant figures) */
    private int sigf;

    public KappalabInput(List<double[]> alternatives, List<Number[]> preferences, int k, KappalabMethod approachType) {
        this.alternatives = alternatives;
        this.preferences = preferences;
        this.k = k;
        this.approachType = approachType;
        sigf = 3;
    }
}
