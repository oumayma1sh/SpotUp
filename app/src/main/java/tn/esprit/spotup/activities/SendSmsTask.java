package tn.esprit.spotup.activities;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendSmsTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "SendSmsTask";
    private static final String API_URL = "https://1gx1rk.api.infobip.com/sms/2/text/advanced"; // Corrected URL
    private static final String API_KEY = "753f1290e34febd6354c3057e16a151c-fa4b783d-532f-41f4-9413-96a06b86cd20"; // Your API Key

    private String eventTitle;

    // Constructor to accept eventTitle
    public SendSmsTask(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        // Create the message with the event title
        String jsonBody = "{\"messages\":[{\"destinations\":[{\"to\":\"+21621845071\"}],\"from\":\"BONPLAN✨\",\"text\":\"We apologize, but the event \\\""
                + eventTitle + "\\\" has been deleted.🥹 We understand this may be inconvenient and are sorry for the disruption.\"}]}";

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "App " + API_KEY) // Appended "App" to the API key as required by Infobip
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "No Response Body";
                return responseBody; // Return the actual response body for further inspection
            } else {
                return "Request Failed: " + response.message();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error sending SMS", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "SMS Send Result: " + result);

        // Log specific details if available
        if (result.contains("status") || result.contains("messageId")) {
            Log.d(TAG, "Check the messageId and status for delivery confirmation.");
        } else {
            Log.e(TAG, "No message status found. Please verify your request format.");
        }
    }
}
