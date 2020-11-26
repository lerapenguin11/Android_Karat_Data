package com.example.androidkaratdata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText editText_name;
    TextView textView_device;
    Spinner spinnerDevice;
    CalendarView calendarView;
    TextView textView;
    ImageButton imageButtonSetting;

    String[] device = {"2-213/223", "306/7/8"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        toolbar = findViewById(R.id.myToolBar);

        textView_device = findViewById(R.id.tView_device);
        textView_device.setText(R.string.tView_device);

        //проверить
        textView = findViewById(R.id.textView_date);

        setSupportActionBar(toolbar);

        //editText
        editText_name = findViewById(R.id.editText_name);

        //spinner activity_main
        spinnerDevice = findViewById(R.id.spinner_device);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, device);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(adapter);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int cYear = year;
                int cMonth = month;
                int cDay = dayOfMonth;
                String selectedDate = new StringBuilder().append(cMonth + 1)
                        .append("-").append(cDay).append("-").append(cYear)
                        .append(" ").toString();
                //Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
                textView.setText("Начать с " + selectedDate);
            }
        });

        //обработчик кнопки "настройки"
        imageButtonSetting = findViewById(R.id.imageButton_setting);

        imageButtonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
    }
