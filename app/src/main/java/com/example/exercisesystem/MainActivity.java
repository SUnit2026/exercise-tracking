package com.example.exercisesystem;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("Exercise logic module is ready.");
        textView.setTextSize(20);
        textView.setPadding(40, 60, 40, 40);
        setContentView(textView);
    }
}
