package com.example.smartass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import info.debatty.java.stringsimilarity.Levenshtein;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    TextView userEmail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String format2 =
            "Generate a unique,creative, 4-choice question based on a specific subject." +
                    "Ensure that the answers provided are relevant to " +
                    "the subject and refrain from including any additional information." +
                    "be strict to the format in your answer! FIRST THE QUESTION THAN THE POSSIBLE ANSWERS AND  IN THE END ADD THE CORRECT ANSWER " +
                    "The subject for the question is: ";
    Button send,signOut,userLastGames;
    EditText userSubject;
    ArrayList<String> qaReadyForUi = new ArrayList<>();
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        send = findViewById(R.id.bt_send);
        signOut = findViewById(R.id.sign_out);
        userLastGames = findViewById(R.id.bt_to_see_my_resuelt);
        userSubject = findViewById(R.id.ed_prompt);
        userEmail = findViewById(R.id.user_mail);
        send.setOnClickListener(this);
        userLastGames.setOnClickListener(this);
        signOut.setOnClickListener(this);
    }


    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!= null)
            updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        Log.d("CH",currentUser.getEmail());
        String mail = currentUser.getEmail();
        db.collection("scores").document(mail).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        userEmail.setText(userData.get("name").toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userEmail.setText("Null");
                    }
                });
        userEmail.setText(mail);
    }

    public void callAPI(String userMessage) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        int num = (int) (Math.random() * 2) ;
        Log.d("CH","ran"+num);
        try {
            JSONArray messagesArray = new JSONArray();

            JSONObject userMessageObj = new JSONObject();
            userMessageObj.put("role", "user");
            userMessageObj.put("content", format2 + userMessage);

            messagesArray.put(userMessageObj);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messagesArray);
            jsonBody.put("n", 15);//Added
            jsonBody.put("temperature", 1); // Adjust the temperature value as needed

            RequestBody body = RequestBody.create(mediaType, jsonBody.toString());

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer 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") // Replace YOUR_API_KEY with your actual API key
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("HTTP", "Failed request: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            send.setText("send");
                            send.setClickable(true);
                            Toast.makeText(MainActivity.this, "Elon has some problems in his servers;)" +
                                    "please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONArray choices = jsonObject.getJSONArray("choices");
                            ArrayList<String> allQA = new ArrayList<String>();
                            Log.d("CH", String.valueOf(choices.length()));
                            for (int i = 0; i < choices.length(); i++) {
                                JSONObject generatedMessageObject = choices.getJSONObject(i);
                                String generatedMessage = generatedMessageObject.getString("message");
                                // Process each generated message individually
                                // Extract the content from the generated message
                                JSONObject contentObject = new JSONObject(generatedMessage);
                                String content = contentObject.getString("content");
                                content = content.replace("Question:","");
                                content = content.replace("QUESTION:","");

                                Log.d("CH", content);
                                allQA.add(content.trim());
                            }
                            // Retrieve the number of tokens used
                            JSONObject usage = jsonObject.getJSONObject("usage");
                            int totalTokens = usage.getInt("total_tokens");
                            Log.d("Token Count", "Total Tokens: " + totalTokens);

                            requestAnswered(allQA);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d("CH", response.body().string());
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //After we got the answer here we will handle it
    public void requestAnswered(ArrayList<String> response) {
        if (!response.isEmpty()) {
            ArrayList<String> onlyTheQA = new ArrayList<>();

            // Extract only the questions from the response
            for (String originalString : response) {
                int delimiterIndex = originalString.indexOf('?');
                if (delimiterIndex != -1) {
                    String question = originalString.substring(0, delimiterIndex);
                    onlyTheQA.add(question);
                }
            }

            // Use Levenshtein distance to remove similar questions
            Set<String> uniqueQuestions = new HashSet<>();
            Levenshtein levenshtein = new Levenshtein();
            double threshold = 0.4; // Set your desired threshold

            for (String question : onlyTheQA) {
                boolean isSimilar = false;
                for (String uniqueQuestion : uniqueQuestions) {
                    double similarity = levenshtein.distance(question, uniqueQuestion);
                    if (similarity <= threshold) {
                        isSimilar = true;
                        break;
                    }
                }
                if (!isSimilar) {
                    uniqueQuestions.add(question);
                }
            }
            qaReadyForUi = new ArrayList<>();
            for (String qa:uniqueQuestions)
            {
                for (int k = 0; k < response.size(); k++)
                {
                    if(response.get(k).contains(qa)){
                        qaReadyForUi.add(response.get(k));
                        break;}
                }
            }
            // Print the deduplicated questions
            int s = 0;
            for (String uniqueQuestion : qaReadyForUi) {
                s = s+1;
                Log.d("CH",  uniqueQuestion+"->"+s);
            }
            Log.d("CH", "Total unique questions: " + uniqueQuestions.size());
            Intent toTheGame = new Intent(MainActivity.this,ActivityTheGame.class);
            toTheGame.putExtra("subject",userSubject.getText().toString());
            toTheGame.putStringArrayListExtra("qa",qaReadyForUi);
            startActivity(toTheGame);
        } else {
            Toast.makeText(MainActivity.this, "Response is empty", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        if(v == send)
        {
            String editSubject = userSubject.getText().toString();
            if(!editSubject.isEmpty()){
                send.setText("Now wait");
                send.setClickable(false);
                callAPI(editSubject);
            }
            else
                Toast.makeText(this, "First you need to type something", Toast.LENGTH_SHORT).show();
        }
        else if(v == signOut){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoggingActivity.class));
        }
        else if(userLastGames == v)
        {
            startActivity(new Intent(MainActivity.this,PreviousGameActivity.class));
        }
    }
}