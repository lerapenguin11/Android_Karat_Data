package com.example.androidkaratdata;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidkaratdata.queryclass.DeviceQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText editText_name;
    TextView textView_device;
    Spinner spinnerDevice;
    DatePicker datePicker;
    TextView textView;
    ImageButton imageButtonSetting;
    Button buttonRead;

    String[] device = {"2-213/223", "306/7/8"};

    String port, ip, adr;
    int cYear, cMonth, cDay;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        //забираем данные из настроек
        port = intent.getStringExtra("port");
        ip = intent.getStringExtra("ip");
        adr = intent.getStringExtra("adr");

        if (port != null && ip != null && adr != null)
            Toast.makeText(getApplicationContext(),
                ("Main - Address: "+ip+":"+port+"/"
                        +adr+"\n"), Toast.LENGTH_LONG).show();
        else Toast.makeText(getApplicationContext(),
                "Адрес прибора неизвестен", Toast.LENGTH_LONG).show();

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

        datePicker = findViewById(R.id.datePicker);
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cYear = year;
                cMonth = monthOfYear;
                cDay = dayOfMonth;
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

        buttonRead = findViewById(R.id.button_read);

        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date start = new Date(cYear, cMonth, cDay);
                DeviceQuery query = new DeviceQuery(
                        port, ip, adr, start,
                        getArchivesTypes(), editText_name.getText().toString()
                );
                Toast.makeText(getApplicationContext(),
                        query.toString(), Toast.LENGTH_LONG).show();
                /*Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);*/
            }
        });
    }

    private ArrayList<String> getArchivesTypes() {
        ArrayList<String> res = new ArrayList<>();
        CheckBox h = findViewById(R.id.checkBox3);
        CheckBox d = findViewById(R.id.checkBox2);
        CheckBox m = findViewById(R.id.checkBox4);
        CheckBox avar = findViewById(R.id.checkBox6);
        CheckBox integ = findViewById(R.id.checkBox5);
        CheckBox zasch = findViewById(R.id.checkBox8);
        CheckBox sobyt = findViewById(R.id.checkBox7);
        if (h.isChecked())
            res.add("Почасовой");
        if (d.isChecked())
            res.add("Посуточный");
        if (m.isChecked())
            res.add("Помесячный");
        if (avar.isChecked())
            res.add("Аварийный");
        if (integ.isChecked())
            res.add("Интегральный");
        if (zasch.isChecked())
            res.add("Защитный");
        if (sobyt.isChecked())
            res.add("Событий");
        return res;
    }
}
