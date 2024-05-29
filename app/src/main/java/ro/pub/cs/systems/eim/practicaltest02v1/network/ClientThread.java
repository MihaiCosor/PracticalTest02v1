package ro.pub.cs.systems.eim.practicaltest02v1.network;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02v1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v1.general.Utilities;

public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String searchText;
    private final TextView searchResultTextView;

    private Socket socket;

    public ClientThread(String address, int port, String searchText, TextView searchResultTextView) {
        this.address = address;
        this.port = port;
        this.searchText = searchText;
        this.searchResultTextView = searchResultTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            printWriter.println(searchText);
            printWriter.flush();
            String searchResult;
            while ((searchResult = bufferedReader.readLine()) != null) {
                final String finalizedSearchResult = searchResult;
                searchResultTextView.post(() -> searchResultTextView.setText(finalizedSearchResult));
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}