package com.example.pobieranie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        fetchMessages();
    }

    private void fetchMessages() {
        new Thread(() -> {
            try {
                URL url = new URL("http://json.itmargen.com/5TP/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );
                    StringBuilder jsonBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }
                    reader.close();

                    parseJSON(jsonBuilder.toString());
                } else {
                    showError("Server error: " + responseCode);
                }
            } catch (Exception e) {
                showError("Connection error: " + e.getMessage());
                Log.e("MainActivity", "Error", e);
            }
        }).start();
    }

    private void parseJSON(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            messageList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String title = obj.getString("title");
                String description = obj.getString("description");
                String date = obj.getString("date");
                String author = obj.getString("author");
                String content = obj.getString("content");

                messageList.add(new Message(title, description, date, author, content));
            }

            // Update UI on main thread
            new Handler(Looper.getMainLooper()).post(() -> adapter.notifyDataSetChanged());
        } catch (Exception e) {
            showError("Error parsing JSON: " + e.getMessage());
        }
    }

    private void showError(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
