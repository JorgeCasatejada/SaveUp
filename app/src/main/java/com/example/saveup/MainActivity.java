package com.example.saveup;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.saveup.ui.MainScreenFragment;


public class MainActivity extends AppCompatActivity {
    private MainScreenFragment mainScreenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainScreenFragment = MainScreenFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mainScreenFragment).commit();
    }

}