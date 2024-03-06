package io.gitlab.chaver.minimax.normalizer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormalizerParameters {

    private String type;
    private double[] minValues;
    private double[] maxValues;

    /**
     * Get the normalizer according to the parameters
     * @return the normalizer
     */
    public INormalizer getNormalizer() {
        if (type.equals(MinMaxNormalizer.TYPE)) {
            return new MinMaxNormalizer(minValues, maxValues);
        }
        return null;
    }

}
