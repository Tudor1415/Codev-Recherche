package io.gitlab.chaver.minimax.learn.oracle;

import io.gitlab.chaver.minimax.io.IAlternative;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class ErrorOracleTest {

    @Test
    void test() {
        Comparator<IAlternative> comparator = new Comparator<IAlternative>() {
            @Override
            public int compare(IAlternative o1, IAlternative o2) {
                return -1;
            }
        };
        Comparator<IAlternative> errorOracle = new ErrorOracle(comparator, 0.9);
        int errors = 0;
        for (int i = 0; i < 100; i++) {
            if (comparator.compare(null, null) != errorOracle.compare(null, null)) {
                errors++;
            }
        }
        System.out.println(errors);
    }

}