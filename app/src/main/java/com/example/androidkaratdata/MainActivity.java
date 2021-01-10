package com.example.androidkaratdata;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidkaratdata.models.DeviceQuery;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText editText_name;
    TextView textView_device;
    Spinner spinnerDevice;
    //DatePicker datePicker;
    CalendarView calendarView;
    TextView textView;
    ImageButton imageButtonSetting, openBtn;
    Button buttonRead;
    DeviceQuery query;

    /*String device = {"2-213/223", "306/7/8"};*/

    String port, ip, adr, mode;
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
        mode = intent.getStringExtra("mode");

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

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, R.layout.spinner_item);
        spinnerDevice.setAdapter(adapter);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year,
                                            int month, int dayOfMonth) {
                cYear = year;
                cMonth = month;
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
                Date start = new Date(cYear - 1900, cMonth, cDay);
                if (mode.equals("TCP")) {
                    query = new DeviceQuery(
                            port, ip, adr, start,
                            getArchivesTypes(), editText_name.getText().toString()
                    );
                    if (port == null || ip == null || adr == null)
                        Toast.makeText(getApplicationContext(), "Определите параметры соединения в настройках (⚙)", Toast.LENGTH_LONG).show();
                    else if (getArchivesTypes().size() == 0)
                        Toast.makeText(getApplicationContext(), "Выберите хотя бы один архив", Toast.LENGTH_LONG).show();
                    else showDialog(1);
                } else Toast.makeText(getApplicationContext(), "Подключение по USB в разработке", Toast.LENGTH_LONG).show();
            }
        });

        openBtn = (ImageButton) findViewById(R.id.imageButton_open);
        openBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getExternalFilesDir("Karat");
                Uri uri = Uri.fromFile(directory);
                intent.setDataAndType(uri, "text/csv");
                startActivity(Intent.createChooser(intent, "Open folder"));
            }
        });
    }

    private ArrayList<String> getArchivesTypes() {
        ArrayList<String> res = new ArrayList<>();
        CheckBox h = findViewById(R.id.checkBox3);
        CheckBox d = findViewById(R.id.checkBox2);
        CheckBox m = findViewById(R.id.checkBox4);
        CheckBox emer = findViewById(R.id.checkBox6);
        CheckBox integ = findViewById(R.id.checkBox5);
        CheckBox prot = findViewById(R.id.checkBox8);
        CheckBox event = findViewById(R.id.checkBox7);
        if (h.isChecked())
            res.add(getString(R.string.hourly));
        if (d.isChecked())
            res.add(getString(R.string.daily));
        if (m.isChecked())
            res.add(getString(R.string.monthly));
        if (emer.isChecked())
            res.add(getString(R.string.emergency));
        if (integ.isChecked())
            res.add(getString(R.string.integral));
        if (prot.isChecked())
            res.add(getString(R.string.protective));
        if (event.isChecked())
            res.add(getString(R.string.eventful));
        return res;
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        // заголовок
        adb.setTitle("Подтвердите запрос");
        // сообщение
        adb.setMessage(query.toString());
        // иконка
        adb.setIcon(android.R.drawable.ic_dialog_info);
        // кнопка положительного ответа
        adb.setPositiveButton("Да", myClickListener);
        // кнопка отрицательного ответа
        adb.setNegativeButton("Нет", myClickListener);
        // создаем диалог
        return adb.create();
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    //Toast.makeText(getApplicationContext(),
                    //       "Тут должно начаться чтение", Toast.LENGTH_LONG).show();
                    Intent toTerm = new Intent(MainActivity.this, TCPTerminalActivity.class);
                    toTerm.putExtra("query", query);
                    startActivity(toTerm);
                    break;
                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    Toast.makeText(getApplicationContext(),
                            "Исправьте поля", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
