package io.gitlab.chaver.minimax.kappalab.algorithm;

import com.google.gson.Gson;
import io.gitlab.chaver.minimax.kappalab.io.KappalabInput;
import io.gitlab.chaver.minimax.kappalab.io.KappalabOutput;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;

public class KappalabSocket implements Callable<KappalabOutput> {

    public static int KAPPALAB_SOCKET_PORT = 6011;
    private KappalabInput input;
    private Gson gson = new Gson();

    public KappalabSocket(KappalabInput input) {
        this.input = input;
    }

    @Override
    public KappalabOutput call() throws Exception {
        Socket socket = new Socket("localhost", KAPPALAB_SOCKET_PORT);
        // Create output stream at the client (to send data to the server)
        BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        // Input stream at Client (Receive data from the server).
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        os.write(gson.toJson(input));
        os.newLine();
        os.flush();
        String response = is.readLine();
        KappalabOutput output = gson.fromJson(response, KappalabOutput.class);
        os.close();
        is.close();
        socket.close();
        return output;
    }
}
