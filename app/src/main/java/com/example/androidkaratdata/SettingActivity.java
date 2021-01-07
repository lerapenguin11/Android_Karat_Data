package com.example.androidkaratdata;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
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

public class SettingActivity extends MainActivity {
    ImageButton imageButton;
    EditText port;
    EditText ip;
    EditText adr;
    TextView ip_text;
    TextView usb_text;
    Spinner spinner_usb;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        imageButton = findViewById(R.id.image_back_button);
        port = findViewById(R.id.editText_name);
        ip = findViewById(R.id.editText_ID);
        adr = findViewById(R.id.editText_a);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.putExtra("port", port.getText().toString());
                intent.putExtra("ip", ip.getText().toString());
                intent.putExtra("adr", adr.getText().toString());
                Toast toast = Toast.makeText(getApplicationContext(),
                        ("Setting - Address: "+ip.getText().toString()+":"+port.getText().toString()+"/"
                                +adr.getText().toString()+"\n"), Toast.LENGTH_LONG);
                toast.show();
                startActivity(intent);
            }
        });

        ip_text = findViewById(R.id.textView2);
        ip_text.setVisibility(View.GONE);
        ip.setVisibility(View.GONE);
        usb_text = findViewById(R.id.textView_usb);
        usb_text.setVisibility(View.GONE);

        spinner_usb = findViewById(R.id.spinner_usb);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array_usb, R.layout.spinner_item);
        spinner_usb.setAdapter(adapter);

        spinner_usb.setVisibility(View.GONE);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.TCP:
                ip_text.setVisibility(View.VISIBLE);
                ip.setVisibility(View.VISIBLE);
                break;
            case R.id.usb:
                usb_text.setVisibility(View.VISIBLE);
                spinner_usb.setVisibility(View.VISIBLE);
                break;
        }
    }
}
