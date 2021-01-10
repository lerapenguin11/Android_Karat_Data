package com.example.androidkaratdata;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    ImageButton imageButton;
    EditText port;
    EditText ip;
    EditText adr;
    TextView ip_text;
    TextView usb_text;
    Spinner spinner_usb;
    RadioButton radioTCP, radioUSB;
    Boolean mode = true;
    public static final String APP_PREFERENCES = "settingsMemory";
    SharedPreferences mSettings;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        imageButton = findViewById(R.id.image_back_button);
        port = findViewById(R.id.editText_name);
        ip = findViewById(R.id.editText_ID);
        adr = findViewById(R.id.editText_a);
        ip_text = findViewById(R.id.textView2);
        ip_text.setVisibility(View.GONE);
        ip.setVisibility(View.GONE);
        usb_text = findViewById(R.id.textView_usb);
        usb_text.setVisibility(View.GONE);

        spinner_usb = findViewById(R.id.spinner_usb);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array_usb, R.layout.spinner_item);
        spinner_usb.setAdapter(adapter);

        spinner_usb.setVisibility(View.GONE);

        radioTCP = (RadioButton) findViewById(R.id.TCP);
        radioUSB = (RadioButton) findViewById(R.id.usb);

        if(mSettings.contains("IP")) ip.setText(mSettings.getString("IP", ""));
        if(mSettings.contains("Port")) port.setText(mSettings.getString("Port", ""));
        if(mSettings.contains("Adr")) adr.setText(mSettings.getString("Adr", ""));
        if(mSettings.contains("Mode")) {
            if (mSettings.getBoolean("Mode", true))
                enableTCP();
            else enableUSB();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("IP", ip.getText().toString());
                editor.putString("Port", port.getText().toString());
                editor.putString("Adr", adr.getText().toString());
                editor.putBoolean("Mode", mode);
                editor.apply();
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.putExtra("port", port.getText().toString());
                intent.putExtra("ip", ip.getText().toString());
                intent.putExtra("adr", adr.getText().toString());
                intent.putExtra("mode", mode ? "TCP" : "USB");
                /*Toast toast = Toast.makeText(getApplicationContext(),
                        ("Setting - Address: "+ip.getText().toString()+":"+port.getText().toString()+"/"
                                +adr.getText().toString()+"\n"), Toast.LENGTH_LONG);
                toast.show();*/
                startActivity(intent);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.TCP:
                enableTCP();
                break;
            case R.id.usb:
                enableUSB();
                break;
        }
    }

    private void enableUSB() {
        radioTCP.setChecked(true);
        usb_text.setVisibility(View.VISIBLE);
        spinner_usb.setVisibility(View.VISIBLE);
        ip_text.setVisibility(View.GONE);
        ip.setVisibility(View.GONE);
        radioTCP.setChecked(false);
        mode = false;
    }

    private void enableTCP() {
        radioUSB.setChecked(true);
        ip_text.setVisibility(View.VISIBLE);
        ip.setVisibility(View.VISIBLE);
        usb_text.setVisibility(View.GONE);
        spinner_usb.setVisibility(View.GONE);
        radioUSB.setChecked(false);
        mode = true;
    }
}
