package com.example.androidkaratdata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button toChat = (Button)findViewById(R.id.button_to_chat);
    }

    public void goToChat (View view){
        Intent intent = new Intent(this, InterlocutorActivity.class);
        startActivity(intent);

    }
}
