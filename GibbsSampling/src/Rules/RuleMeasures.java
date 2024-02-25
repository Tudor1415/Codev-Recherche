package Rules;
import static java.lang.Math.*;

/**
 * Class to compute different measures for a rule (inspired by R code : <a href="https://github.com/mhahsler/arules/blob/master/R/interestMeasures.R">arules</a>)
 */
public class RuleMeasures {

    // For rounding errors
    public static double epsilon = 0.01;

    private double n;
    private double n11;
    private double n1x;
    private double nx1;
    private double n0x;
    private double nx0;
    private double n10;
    private double n01;
    private double n00;

    // Measure names

    public static final String confidence = "confidence";
    public static final String lift = "lift";
    public static final String cosine = "cosine";
    public static final String phi = "phi";
    public static final String kruskal = "kruskal";
    public static final String yuleQ = "yuleQ";
    public static final String addedValue = "pavillon";
    public static final String certainty = "certainty";
    public static final String support = "support";
    public static final String revsupport = "revsup";


    public RuleMeasures(IRule rule, int nbTransactions, double smoothCounts) {
        if (rule == null) {
            throw new RuntimeException("Rule must not be null");
        }
        n = nbTransactions;
        n11 = rule.getFreqZ();
        n1x = rule.getFreqX();
        nx1 = rule.getFreqY();
        n0x = n - n1x;
        nx0 = n - nx1;
        n10 = n1x - n11;
        n01 = nx1 - n11;
        n00 = n0x - n01;
        if (smoothCounts > 0) {
            n = n + 4 * smoothCounts;
            n11 = n11 + smoothCounts;
            n10 = n10 + smoothCounts;
            n01 = n01 + smoothCounts;
            n00 = n00 + smoothCounts;
            n0x = n0x + 2 * smoothCounts;
            nx0 = nx0 + 2 * smoothCounts;
            n1x = n1x + 2 * smoothCounts;
            nx1 = nx1 + 2 * smoothCounts;
        }
    }

    private void checkMeasure(double value, double lb, double ub, String measureName) {
        if (value > (ub + epsilon) || value < (lb - epsilon)) {
            throw new RuntimeException("Illegal value for measure " + measureName + ": value=" + value + ", should be between " +
                    lb + " and " + ub);
        }
    }

    private double confidence() {
        double value = n11 / n1x;
        checkMeasure(value, 0, 1, confidence);
        return value;
    }

    private double lift() {
        double value = n * n11 / (n1x * nx1);
        checkMeasure(value, 0, Double.MAX_VALUE, lift);
        return value;
    }

    private double cosine() {
        double value = n11 / sqrt(n1x * nx1);
        checkMeasure(value, 0, 1, cosine);
        return value;
    }

    private double phi() {
        double value = (n * n11 - n1x * nx1) / sqrt(n1x * nx1 * n0x * nx0);
        checkMeasure(value, -1, 1, phi);
        return value;
    }

    private double kruskal() {
        double max_x0x1 = max(nx1, nx0);
        double value = (max(n11, n10) + max(n01, n00) - max_x0x1) / (n - max_x0x1);
        checkMeasure(value, 0, 1, kruskal);
        return value;
    }

    private double OR() {
        return n11 * n00 / (n10 * n01);
    }

    private double yuleQ() {
        double OR = OR();
        double value = (OR - 1) / (OR + 1);
        checkMeasure(value, -1, 1, yuleQ);
        return value;
    }

    private double addedValue() {
        double value = n11 / n1x - nx1 / n;
        checkMeasure(value, -0.5, 1, addedValue);
        return value;
    }

    private double certainty() {
        double value1 = (n11 / n1x - nx1 / n) / (1 - nx1 / n);
        double value2 = (n11 / nx1 - n1x / n) / (1 - n1x / n);
        double value = max(value1, value2);
        checkMeasure(value, -1, 1, certainty);
        return value;
    }

    private double support() {
        double value = n11 / n;
        checkMeasure(value, 0, 1, support);
        return value;
    }

    private double revsupport() {
        double value = 1 - support();
        checkMeasure(value, 0, 1, revsupport);
        return value;
    }

    public double[] computeMeasures(String[] measureNames) {
        double[] measures = new double[measureNames.length];
        for (int i = 0; i < measureNames.length; i++) {
            measures[i] = compute(measureNames[i]);
        }
        return measures;
    }

    private double compute(String measureName) {
        if (measureName.equals(confidence)) return confidence();
        if (measureName.equals(lift)) return lift();
        if (measureName.equals(cosine)) return cosine();
        if (measureName.equals(phi)) return phi();
        if (measureName.equals(kruskal)) return kruskal();
        if (measureName.equals(yuleQ)) return yuleQ();
        if (measureName.equals(addedValue)) return addedValue();
        if (measureName.equals(certainty)) return certainty();
        if (measureName.equals(support)) return support();
        if (measureName.equals(revsupport)) return revsupport();
        throw new RuntimeException("This measure doesn't exist : " + measureName);
    }

}
