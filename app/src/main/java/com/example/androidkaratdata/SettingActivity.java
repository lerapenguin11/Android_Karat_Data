package com.example.androidkaratdata;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class SettingActivity extends MainActivity {
    ImageButton imageButton;
    EditText port;
    EditText ip;
    EditText adr;

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
    }
}
