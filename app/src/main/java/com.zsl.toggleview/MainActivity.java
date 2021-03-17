package com.zsl.toggleview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author zsl
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleView toggleView = findViewById(R.id.toggle);
        toggleView.setChecked(true)
                .setData("开","关")
                .build();

        toggleView.setOnCheckedListener(isChecked -> Log.i(TAG, "onCreate: "+isChecked));
    }
}
