package com.example.smartass;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PrevouisGamesAdapter extends ArrayAdapter<Game> {
    Context context;
    TextView score,subject;
    public PrevouisGamesAdapter(@NonNull Context context, @NonNull ArrayList<Game> items) {
        super(context, 0, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_game_layout, parent, false);
        }
        score = convertView.findViewById(R.id.ls_score);
        subject = convertView.findViewById(R.id.ls_subject);
        score.setText(getItem(position).getScore());
        subject.setText(getItem(position).getSubject());
        return convertView;
    }
}
