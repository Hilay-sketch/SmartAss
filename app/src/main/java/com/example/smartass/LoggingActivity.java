package com.example.smartass;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoggingActivity extends AppCompatActivity implements View.OnClickListener {

    EditText prePassword,preEmail,preName;
    String email,password;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button login;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_logging);
        preEmail = findViewById(R.id.mail);
        preName = findViewById(R.id.name);
        prePassword = findViewById(R.id.password);
        login = findViewById(R.id.bt_create_account);
        login.setOnClickListener(this);
    }

    public void createUserScore()
    {
        // Create a new user with a first, middle, and last name
        Map<String, Object> user = new HashMap<>();
        ArrayList<Integer> userScores = new ArrayList<>();
        ArrayList<String> userGameSubject = new ArrayList<>();
        if(!preName.getText().toString().isEmpty()){
            user.put("name", preName.getText().toString());
            user.put("gameScores", userScores);
            user.put("gameSubject", userGameSubject);
            user.put("mail",email);
// Add a new document with a generated ID
            db.collection("scores").document(email).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(LoggingActivity.this, "created", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoggingActivity.this, "Failed:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            startActivity(new Intent(LoggingActivity.this,MainActivity.class));
    }

    @Override
    public void onClick(View v) {
        if(v == login)
        {
            email = preEmail.getText().toString();
            password = prePassword.getText().toString();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCustomToken:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                createUserScore();
                                startActivity(new Intent(LoggingActivity.this,MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                                Toast.makeText(LoggingActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}