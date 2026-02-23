package com.vibruchu.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = new Button(this);
        button.setText("Start Vibruchu Service");

        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, VibrationService.class);
            startForegroundService(intent);
        });

        setContentView(button);
    }
}
