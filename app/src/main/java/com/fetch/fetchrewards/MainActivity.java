package com.fetch.fetchrewards;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MainActivity is the main entry point for the Fetch Rewards application.
 * This activity is responsible for fetching JSON data from a specified URL,
 * parsing the data, and displaying it in a ListView.
 */
public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ListView listView;
    private List<Item> itemList = new ArrayList<>();
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ListView and executor service for background tasks
        listView = findViewById(R.id.listView1);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Start the process to fetch JSON data
        fetchJsonData("https://fetch-hiring.s3.amazonaws.com/hiring.json");
    }

    /**
     * Initiates the fetching of JSON data from the specified URL.
     *
     * @param urlString The URL to fetch data from.
     */
    private void fetchJsonData(String urlString) {
        // Display a progress dialog while data is being fetched
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        executorService.execute(() -> {
            String jsonResponse = null;
            try {
                jsonResponse = fetchDataFromUrl(urlString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Update the UI on the main thread after data is fetched
            String finalJsonResponse = jsonResponse;
            mainHandler.post(() -> {
                progressDialog.dismiss();
                if (finalJsonResponse != null) {
                    parseJsonData(finalJsonResponse);
                }
            });
        });
    }

    /**
     * Fetches JSON data from the specified URL.
     *
     * @param urlString The URL to fetch data from.
     * @return The JSON response as a string.
     * @throws IOException If an error occurs during the network operation.
     */
    private String fetchDataFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String inputLine;

            // Read the response line by line
            while ((inputLine = reader.readLine()) != null) {
                responseBuilder.append(inputLine);
            }
            reader.close();
            return responseBuilder.toString();
        } else {
            System.err.println("GET request failed. Response Code: " + responseCode);
        }
        return null;
    }

    /**
     * Parses the JSON data and populates the item list with valid entries.
     *
     * @param jsonData The JSON data as a string.
     */
    private void parseJsonData(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.optString("name", null);

                // Validate and extract data only if the name is valid
                if (isValidName(name)) {
                    int id = jsonObject.optInt("id", -1);
                    int listId = jsonObject.optInt("listId", -1);
                    int itemNum = extractItemNum(name);

                    if (id != -1 && listId != -1) {
                        itemList.add(new Item(id, listId, itemNum));
                    }
                }
            }

            sortItemList(); // Sort the items after parsing
            displayItems(); // Display the parsed items in the ListView
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates the provided name string.
     *
     * @param name The name to validate.
     * @return True if the name is valid, false otherwise.
     */
    private boolean isValidName(String name) {
        return name != null && !name.isEmpty() && !"null".equals(name);
    }

    /**
     * Extracts the item number from the name string.
     *
     * @param name The name string containing the item number.
     * @return The extracted item number, or 0 if parsing fails.
     */
    private int extractItemNum(String name) {
        try {
            return Integer.parseInt(name.split(" ")[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
            return 0; // Return 0 if parsing fails
        }
    }

    /**
     * Sorts the item list by listId and itemNum in ascending order.
     */
    private void sortItemList() {
        Collections.sort(itemList, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                int listIdComparison = Integer.compare(item1.getListId(), item2.getListId());
                return (listIdComparison != 0) ? listIdComparison : Integer.compare(item1.getItemNum(), item2.getItemNum());
            }
        });
    }

    /**
     * Displays the items in the ListView using a sectioned adapter.
     */
    private void displayItems() {
        ArrayList<Object> sortedList = new ArrayList<>();
        TreeMap<Integer, ArrayList<String>> groupedItems = new TreeMap<>();

        // Group items by listId and prepare the display list
        for (Item item : itemList) {
            int listId = item.getListId();
            if (!groupedItems.containsKey(listId)) {
                groupedItems.put(listId, new ArrayList<>());
                sortedList.add("ListId group: " + listId); // Add group header
            }
            groupedItems.get(listId).add(item.toString());
            sortedList.add(item);
        }

        SectionListAdapter adapter = new SectionListAdapter(MainActivity.this, sortedList);
        listView.setAdapter(adapter); // Set the adapter to the ListView
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the executor service to avoid memory leaks
    }
}
