package com.example.androidkaratdata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    //EditText editText;
    TextView textView_device;
    private Spinner spinnerDevice;
    String[] device = {"2-213/223","306/7/8"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        toolbar=findViewById(R.id.myToolBar);

        textView_device = findViewById(R.id.tView_device);
        textView_device.setText(R.string.tView_device);

        setSupportActionBar(toolbar);
        //обработка editText
        //editText = findViewById(R.id.editText);

        //spinner
        spinnerDevice = findViewById(R.id.spinner_device);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, device);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(adapter);
    }
}
