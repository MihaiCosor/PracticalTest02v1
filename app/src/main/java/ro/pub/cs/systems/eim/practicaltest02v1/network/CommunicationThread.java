package ro.pub.cs.systems.eim.practicaltest02v1.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02v1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v1.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02v1.model.SearchResult;

public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client!");
            String searchText = bufferedReader.readLine();
            if (searchText == null || searchText.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client!");
                return;
            }

            HashMap<String, SearchResult> data = serverThread.getData();
            SearchResult searchResult;
            String result;
            if (data.containsKey(searchText)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                searchResult = data.get(searchText);
                result = "cache empty";
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");

                URL urlAddress = new URL(Constants.WEB_SERVICE_ADDRESS + searchText);
                URLConnection urlConnection = urlAddress.openConnection();
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String pageSourceCode;
                StringBuilder stringBuilder = new StringBuilder();
                String currentLine;
                while ((currentLine = bufferedReader1.readLine()) != null) {
                    stringBuilder.append(currentLine);
                }
                bufferedReader1.close();
                pageSourceCode = stringBuilder.toString();
                result = pageSourceCode;

                int firstBracketIndex = pageSourceCode.indexOf('[');
                int secondBracketIndex = pageSourceCode.indexOf('[', firstBracketIndex + 1);

                // Găsește indexul primei apariții a caracterului ']' după a doua '['
                int closingBracketIndex = pageSourceCode.indexOf(']', secondBracketIndex);

                // Extrage subșirul dintre al doilea '[' și primul ']'
                result = pageSourceCode.substring(secondBracketIndex + 1, closingBracketIndex);

//                JSONObject content = new JSONObject(pageSourceCode);
//
//                JSONArray weatherArray = content.getJSONArray(Constants.WEATHER);
//                JSONObject weather;
//                StringBuilder condition = new StringBuilder();
//                for (int i = 0; i < weatherArray.length(); i++) {
//                    weather = weatherArray.getJSONObject(i);
//                    condition.append(weather.getString(Constants.MAIN)).append(" : ").append(weather.getString(Constants.DESCRIPTION));
//
//                    if (i < weatherArray.length() - 1) {
//                        condition.append(";");
//                    }
//                }
//
//                JSONObject main = content.getJSONObject(Constants.MAIN);
//                String temperature = main.getString(Constants.TEMP);
//                String pressure = main.getString(Constants.PRESSURE);
//                String humidity = main.getString(Constants.HUMIDITY);
//
//                JSONObject wind = content.getJSONObject(Constants.WIND);
//                String windSpeed = wind.getString(Constants.SPEED);
//
//                weatherForecastInformation = new WeatherForecastInformation(
//                        temperature, windSpeed, condition.toString(), pressure, humidity
//                );
//                serverThread.setData(city, weatherForecastInformation);
            }
//            if (weatherForecastInformation == null) {
//                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
//                return;
//            }
//            String result;

            printWriter.println(result);
            printWriter.flush();
            // | JSONException
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }

}