package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    String name = getIntent().getStringExtra("name");
    int position = getIntent().getIntExtra("position", -1);


    ArrayList<Item> notes;

    noteadapter adapter = new noteadapter(notes,this)   ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditText nameEditText = findViewById(R.id.textView7);
        nameEditText.setText(name);

        Button saveButton = findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the edited name and finish the activity
                String editedName = nameEditText.getText().toString();
                notes.get(position).contenu = editedName;
                adapter.notifyDataSetChanged();
                finish();
            }
        });
    }
}