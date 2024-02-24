package io.gitlab.chaver.minimax.normalizer;

import com.google.gson.Gson;
import picocli.CommandLine.ITypeConverter;

import java.io.BufferedReader;
import java.io.FileReader;

public class NormalizerParametersConverter implements ITypeConverter<NormalizerParameters> {

    @Override
    public NormalizerParameters convert(String path) throws Exception {
        return new Gson().fromJson(new BufferedReader(new FileReader(path)), NormalizerParameters.class);
    }
}
