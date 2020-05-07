package com.example.remed;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

class EoeIntent extends AppCompatActivity {

    //class to be opened when the user clicks ono the notification, no needed for ReMed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eoe);

    }
}