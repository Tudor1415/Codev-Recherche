package io.gitlab.chaver.minimax.learn.cli;

import com.google.gson.Gson;
import io.gitlab.chaver.minimax.score.FunctionParameters;
import io.gitlab.chaver.minimax.score.LinearScoreFunction;
import io.gitlab.chaver.minimax.util.RandomUtil;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineTest {

    private String rulesPath = "results/iris_zaki_fmin_2_cmin_0.7.json";

    /**
     * Test the whole process
     * 1 : Map rules to alternative
     * 2 : Split the set of alternatives in K sets
     * 3 : Learn a function using K folds
     * 4 : Test the accuracy of the function
     */
    @Test
    void testPipeline() throws Exception {
        // 1 : Map rules to alternative
        /*File alternativesFile = File.createTempFile("alt", ".json");
        int exitCode = new CommandLine(new MapRulesToAlternative())
                .execute("-r", rulesPath, "-m",
                        "yuleQ:cosine:kruskal:pavillon:certainty", "--json", alternativesFile.getAbsolutePath());
        assertEquals(0, exitCode);
        // 2 : Split the set of alternatives in K sets
        int nbFolds = 3;
        exitCode = new CommandLine(new SplitKAlternatives()).execute("-a", alternativesFile.getAbsolutePath(),
                "-k", Integer.toString(nbFolds));
        assertEquals(0, exitCode);
        // 3 : Learn a function using K folds
        FunctionParameters params = new FunctionParameters();
        params.setFunctionType(LinearScoreFunction.TYPE);
        params.setWeights(RandomUtil.getInstance().generateRandomWeights(5));
        File oracleFile = File.createTempFile("oracle", ".json");
        BufferedWriter writer = new BufferedWriter(new FileWriter(oracleFile));
        new Gson().toJson(params, writer);
        writer.close();
        for (int i = 0; i < nbFolds; i++) {
            String trainPath = RandomUtil.getFoldPath(alternativesFile.getAbsolutePath(), "_train", i);
            String testPath = RandomUtil.getFoldPath(alternativesFile.getAbsolutePath(), "_test", i);
            // Get normalization parameters
            File normalisationParams = File.createTempFile("norm", ".json");
            exitCode = new CommandLine(new AlternativeNormalizer()).execute("-r", rulesPath, "-a",
                    alternativesFile.getAbsolutePath(), "--sub", trainPath, "--json", normalisationParams.getAbsolutePath());
            assertEquals(0, exitCode);
            File trainFunc = File.createTempFile("train_func", ".json");
            exitCode = new CommandLine(new AHPRulesRanking()).execute("-r", rulesPath,
                    "-a", alternativesFile.getAbsolutePath(), "--sub", trainPath, "-o", oracleFile.getAbsolutePath(),
                    "--json", trainFunc.getAbsolutePath(), "-n", normalisationParams.getAbsolutePath());
            assertEquals(0, exitCode);
            // 4 : Test the accuracy of the function
            File testResult = File.createTempFile("test_func", ".json");
            exitCode = new CommandLine(new PredictRulesRanking()).execute("-r", rulesPath,
                    "-a", alternativesFile.getAbsolutePath(), "--sub", testPath, "-o", oracleFile.getAbsolutePath(),
                    "-f", trainFunc.getAbsolutePath(), "--rm", "spearman:rec20", "--json", testResult.getAbsolutePath(),
                    "-n", normalisationParams.getAbsolutePath());
            assertEquals(0, exitCode);
        }*/
    }
}
