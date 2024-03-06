package io.gitlab.chaver.minimax.kappalab.algorithm;

import com.google.gson.Gson;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput;
import io.gitlab.chaver.minimax.kappalab.io.KappalabOutput;
import lombok.AllArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class Kappalab implements Callable<KappalabOutput> {

    private File inputFile;
    private File outputFile;
    private KappalabInput input;

    @Override
    public KappalabOutput call() throws Exception {
        Gson gson = new Gson();
        BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile));
        writer.write(gson.toJson(input));
        writer.close();
        ProcessBuilder builder = new ProcessBuilder("Rscript", "scripts/call_kappalab.R",
                inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        Process process = builder.start();
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new RuntimeException("Error in R script");
        }
        return gson.fromJson(new FileReader(outputFile), KappalabOutput.class);
    }
}
