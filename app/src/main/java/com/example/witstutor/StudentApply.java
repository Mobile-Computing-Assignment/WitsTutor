package com.example.witstutor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentApply extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentapply);

        Intent in = getIntent();
        final String studentNo = in.getStringExtra("STUDENT_NO");
        final String course_name = in.getStringExtra("COURSE_NAME");

        Button applyBtn = findViewById(R.id.applyBtn);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues params = new ContentValues();
                params.put("STUDENT_NO",studentNo);
                params.put("COURSE_NAME",course_name);

                @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://witstutor.herokuapp.com/studentApply.php",params) {
                    @Override
                    protected void onPostExecute(String output) {

                        submittedAppl(output);
                    }

                };
                asyncHttpPost.execute();
            }

        });
    }

    private void submittedAppl(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message =jo.getString("message");

            if (success.equals("0")){
                //already submitted an application for this studentAppl!
                Toast.makeText(StudentApply.this,message,Toast.LENGTH_SHORT).show();

            } else {
                //successfully submitted application!
                Toast.makeText(StudentApply.this,message,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
