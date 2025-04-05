package ru.myitschool.finalproject;

import android.widget.ProgressBar;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIAssistant {
    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your API key
    
    private final OkHttpClient client;
    private final Gson gson;
    private final ExecutorService executor;
    
    public interface ResponseCallback {
        void onResponse(String response);
    }

    public AIAssistant() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void getCodeAssistance(String code, String prompt, ResponseCallback callback) {
        String fullPrompt = String.format(
            "<s>[INST] I am looking at this code:\n\n```\n%s\n```\n\n%s [/INST]</s>",
            code, prompt
        );

        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"),
            gson.toJson(new AIRequest(fullPrompt))
        );

        Request request = new Request.Builder()
            .url(API_URL)
            .post(body)
            .addHeader("Authorization", "Bearer " + API_KEY)
            .build();

        executor.execute(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    AIResponse[] responses = gson.fromJson(
                        response.body().string(),
                        AIResponse[].class
                    );
                    if (responses.length > 0) {
                        String aiResponse = responses[0].generated_text
                            .replace(fullPrompt, "")
                            .trim();
                        callback.onResponse(aiResponse);
                        return;
                    }
                }
                callback.onResponse("Sorry, I couldn't process your request. Please try again.");
            } catch (IOException e) {
                callback.onResponse("Error: " + e.getMessage());
            }
        });
    }

    private static class AIRequest {
        final String inputs;
        AIRequest(String inputs) {
            this.inputs = inputs;
        }
    }

    private static class AIResponse {
        String generated_text;
    }
} 