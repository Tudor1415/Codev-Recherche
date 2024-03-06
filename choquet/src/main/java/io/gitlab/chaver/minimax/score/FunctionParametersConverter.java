package io.gitlab.chaver.minimax.score;

import com.google.gson.Gson;
import picocli.CommandLine.ITypeConverter;

import java.io.BufferedReader;
import java.io.FileReader;

public class FunctionParametersConverter implements ITypeConverter<FunctionParameters> {

    @Override
    public FunctionParameters convert(String path) throws Exception {
        return new Gson().fromJson(new BufferedReader(new FileReader(path)), FunctionParameters.class);
    }
}
