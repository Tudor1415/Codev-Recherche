package io.gitlab.chaver.minimax.util;

import io.gitlab.chaver.minimax.io.Alternative;
import picocli.CommandLine.ITypeConverter;

import java.util.Arrays;

public class AlternativeConverter implements ITypeConverter<Alternative> {

    @Override
    public Alternative convert(String arg) throws Exception {
        double[] vector = Arrays.stream(arg.split(",")).mapToDouble(Double::parseDouble).toArray();
        return new Alternative(vector);
    }
}
