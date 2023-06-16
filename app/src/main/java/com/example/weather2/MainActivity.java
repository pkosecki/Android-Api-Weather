package com.example.weather2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private Button fetchButton;
    private TextView weatherTextView;
    private ProgressBar progressBar;

    private static final String API_KEY = "cf3e1eb9f1f3180d285477a75e3ebae1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        fetchButton = findViewById(R.id.fetchButton);
        weatherTextView = findViewById(R.id.weatherTextView);
        progressBar = findViewById(R.id.progressBar);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityEditText.getText().toString();
                String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY;

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            URL url = new URL(apiUrl);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");

                            int responseCode = connection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }
                                reader.close();
                                return response.toString();
                            } else {
                                return "Error: " + responseCode;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "Error: " + e.getMessage();
                        }
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                            weatherTextView.setText("Pogoda: " + weather);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            weatherTextView.setText("Error: Failed to parse JSON");
                        }
                    }
                }.execute();
            }
        });
    }
}
