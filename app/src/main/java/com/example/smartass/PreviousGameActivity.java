package com.example.smartass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PreviousGameActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ListView userListView;
    ArrayList<Integer> userScores;
    ArrayList<String> userSubjects;
    TextView userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_game);
        userListView = findViewById(R.id.ls_scoresListView);
        userName = findViewById(R.id.tx_userName);
        userSubjects = new ArrayList<String>();
        userScores = new ArrayList<Integer>();
        db.collection("scores")
                .document(mAuth.getCurrentUser().getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userName.setText(documentSnapshot.get("name").toString());
                        userSubjects = (ArrayList<String>) documentSnapshot.get("gameSubject");
                        userScores = (ArrayList<Integer>) documentSnapshot.get("gameScores");
                        addToListView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PreviousGameActivity.this, "Couldn't load your info", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToListView() {
        ArrayList<Game> games = new ArrayList<Game>();
        for (int i = 0; i < userScores.size();i++) {
            Game game1= new Game(String.valueOf(userScores.get(i)),userSubjects.get(i));
            games.add(game1);
        }
        PrevouisGamesAdapter adapter = new PrevouisGamesAdapter(this, games);
        // Set the adapter to the ListView
        userListView.setAdapter(adapter);
    }
}