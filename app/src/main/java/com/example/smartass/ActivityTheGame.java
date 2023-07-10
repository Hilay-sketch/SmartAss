package com.example.smartass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActivityTheGame extends AppCompatActivity implements QuestionFragment.AnswerSelectionListener {

    ArrayList<String> allQa = new ArrayList<>();
    int numberOfQuestionsAnswered = 0;
    TextView numberOfCorrectAnswer,strikeText;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String subject;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> userData = new HashMap<>();
    int strikes = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_game);
        allQa = getIntent().getStringArrayListExtra("qa");
        subject = getIntent().getStringExtra("subject");
        Log.d("GM",allQa.toString());
        db.collection("scores").document(mAuth.getCurrentUser().getEmail()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userData = documentSnapshot.getData();
                    } else {
                        Toast.makeText(this, "Failed doc dosent exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        numberOfCorrectAnswer = findViewById(R.id.num_of_correct_answer);
        strikeText = findViewById(R.id.strike);
        strikeText.setText("Strikes:"+String.valueOf(strikes));
        numberOfCorrectAnswer.setText(String.valueOf(numberOfQuestionsAnswered));
        createNextQuestions(allQa.get(0));
        Collections.shuffle(allQa);
    }



    public void createNextQuestions(String nextQuestion)
    {
        ArrayList<String> answers = new ArrayList<>();
        String questionUpperCase = nextQuestion.toUpperCase();
        Log.d("GM","----232------------------");
        Log.d("GM",questionUpperCase);
        int endqa = questionUpperCase.indexOf('?');
        String question = nextQuestion.substring(0,endqa+1);
        questionUpperCase.replace("A.","A)");
        questionUpperCase.replace("B.","B)");
        questionUpperCase.replace("C.","C)");
        questionUpperCase.replace("D.","D)");

        questionUpperCase.replace("1.","A)");
        questionUpperCase.replace("2.","B)");
        questionUpperCase.replace("3.","C)");
        questionUpperCase.replace("4.","D)");

        questionUpperCase.replace("1)","A)");
        questionUpperCase.replace("2)","B)");
        questionUpperCase.replace("3)","C)");
        questionUpperCase.replace("4)","D)");

        int a1 = questionUpperCase.indexOf("A) ");
        int b2 = questionUpperCase.indexOf("B) ");
        int c3 = questionUpperCase.indexOf("C) ");
        int d4 = questionUpperCase.indexOf("D) ");
        int endD4 = questionUpperCase.indexOf("CORRECT");
        Log.d("GM","----------------------");
        Log.d("GM",questionUpperCase);
        Log.d("GM", String.valueOf(a1)+" "+String.valueOf(b2)+" "+String.valueOf(c3)+" "+String.valueOf(d4)+" "+String.valueOf(endD4));
        answers.add(nextQuestion.substring(a1+2,b2-1).trim()) ;
        answers.add(nextQuestion.substring(b2+2,c3-1).trim());
        answers.add(nextQuestion.substring(c3+2,d4-1).trim());
        answers.add(nextQuestion.substring(d4+2,endD4-1).trim());

        int startCorrectAnswer = questionUpperCase.indexOf("ANSWER:");
        String correctAns = nextQuestion.substring(startCorrectAnswer+10).trim();

        Log.d("GM",question+" "+answers.toString()+" corr:"+correctAns);

        QuestionFragment quizFragment = new QuestionFragment();

        quizFragment.setQuestion(question);
        quizFragment.setAnswers(answers.get(0), answers.get(1), answers.get(2), answers.get(3));
        quizFragment.setCorrectAnswer(correctAns);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, quizFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onAnswerSelected(boolean isCorrect) {
        // Handle the answer selection here
        if (isCorrect) {
            numberOfQuestionsAnswered = numberOfQuestionsAnswered+1;
            numberOfCorrectAnswer.setText(String.valueOf(numberOfQuestionsAnswered));
            if (numberOfQuestionsAnswered < allQa.size())
                createNextQuestions(allQa.get(numberOfQuestionsAnswered));
            else
            {
                Intent backToMain = new Intent(ActivityTheGame.this,MainActivity.class);
                ArrayList<Integer> gameScores = (ArrayList<Integer>) userData.get("gameScores");
                gameScores.add(numberOfQuestionsAnswered); // Example: Add a new game score
                ArrayList<String> gameSubject = (ArrayList<String>) userData.get("gameSubject");
                gameSubject.add(subject); // Example: Add a new game subject

                db.collection("scores").document(mAuth.getCurrentUser().getEmail()).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Game recorded", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Game didn't recorded", Toast.LENGTH_SHORT).show();
                        });
                startActivity(backToMain);
            }
        }
        else
        {
            if (strikes == 0)
            {
                Intent backToMain = new Intent(ActivityTheGame.this,MainActivity.class);
                ArrayList<Integer> gameScores = (ArrayList<Integer>) userData.get("gameScores");
                gameScores.add(numberOfQuestionsAnswered); // Example: Add a new game score

                ArrayList<String> gameSubject = (ArrayList<String>) userData.get("gameSubject");
                gameSubject.add(subject); // Example: Add a new game subject

                db.collection("scores").document(mAuth.getCurrentUser().getEmail()).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Game recorded", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Game didn't recorded", Toast.LENGTH_SHORT).show();
                        });
                startActivity(backToMain);
            }
            else{
                strikes = strikes-1;
                strikeText.setText("Strikes:"+String.valueOf(strikes));}
        }
    }


}