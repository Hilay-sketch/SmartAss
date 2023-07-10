package com.example.smartass;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class QuestionFragment extends Fragment implements View.OnClickListener {

    private TextView questionTextView;
    Button answer1Button, answer2Button, answer3Button, answer4Button;
    private String question, answer1, answer2, answer3, answer4, correctAnswer;
    private AnswerSelectionListener answerSelectionListener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        // Initialize views
        questionTextView = view.findViewById(R.id.theqa);
        final long DELAY_MS = 50; // Delay between each character (adjust as needed)

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                questionTextView.setText(question.substring(0, index++));

                if (index <= question.length()) {
                    handler.postDelayed(this, DELAY_MS);
                }
            }
        };

        handler.postDelayed(runnable, DELAY_MS);
        answer1Button = view.findViewById(R.id.bt_answer1);
        answer2Button = view.findViewById(R.id.bt_answer2);
        answer3Button = view.findViewById(R.id.bt_answer3);
        answer4Button = view.findViewById(R.id.bt_answer4);

        // Set the question and answers
        answer1Button.setText(answer1);
        answer2Button.setText(answer2);
        answer3Button.setText(answer3);
        answer4Button.setText(answer4);

// Set click listeners for the answer buttons
        answer1Button.setOnClickListener(this);
        answer2Button.setOnClickListener(this);
        answer3Button.setOnClickListener(this);
        answer4Button.setOnClickListener(this);
        if (getActivity() instanceof AnswerSelectionListener) {
            answerSelectionListener = (AnswerSelectionListener) getActivity();
        }
        return view;
    }

    // Setter methods to set the question and answers
    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswers(String answer1, String answer2, String answer3, String answer4) {
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }
    public void setCorrectAnswer(String correctAnswer)
    {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public void onClick(View v) {
        boolean isCorrect = false;

        if (v == answer1Button) {
            if (answer1.equals(correctAnswer)) {
                Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
                isCorrect = true;
            }
            else
            {
                answer1Button.setBackgroundColor(0xFFE57373);
            }
        }
        else if (v == answer2Button) {
            if (answer2.equals(correctAnswer)) {
                Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
                isCorrect = true;
            }
            else
            {
                answer2Button.setBackgroundColor(0xFFE57373);
            }
        } else if (v == answer3Button) {
            if (answer3.equals(correctAnswer)) {
                Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
                isCorrect = true;
            }
            else
            {
                answer3Button.setBackgroundColor(0xFFE57373);
            }
        } else if (v == answer4Button) {
            if (answer4.equals(correctAnswer)) {
                Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
                isCorrect = true;
            }
            else
            {
                answer4Button.setBackgroundColor(0xFFE57373);
            }
        }

        if (answerSelectionListener != null) {
            answerSelectionListener.onAnswerSelected(isCorrect);
        }
    }

    public interface AnswerSelectionListener {
        void onAnswerSelected(boolean isCorrect);
    }

}