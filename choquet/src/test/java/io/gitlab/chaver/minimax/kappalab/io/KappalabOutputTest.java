package io.gitlab.chaver.minimax.kappalab.io;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KappalabOutputTest {

    @Test
    void testOutput() {
        String json = "{\"capacities\":[0.0,1.0]}";
        KappalabOutput output = new Gson().fromJson(json, KappalabOutput.class);
        assertArrayEquals(new double[]{0.0,1.0}, output.getCapacities());
    }

}