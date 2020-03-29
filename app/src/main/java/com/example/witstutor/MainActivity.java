package com.example.witstutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClicklectlogin(View view) {
        Intent intent2 = new Intent(this, LecturerLogin.class);
        Button lectBtn =  findViewById(R.id.lectBtn);
        startActivity(intent2);
    }

    public void onClickstulogin(View view) {
        Intent intent = new Intent(this, StudentLogin.class);
        Button stuBtn =  findViewById(R.id.stuBtn);
        startActivity(intent);
    }

}
