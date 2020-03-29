package com.example.witstutor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stulogin);

        Button stuLoginBtn = findViewById(R.id.stuLoginBtn);
        stuLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                EditText stuNoET = findViewById(R.id.stuNoET);
                EditText passwET = findViewById(R.id.passwET);

                String studentNo = stuNoET.getText().toString();
                String password = passwET.getText().toString();

                ContentValues params = new ContentValues();

                params.put("STUDENT_NO",studentNo);
                params.put("STUDENT_PASSWORD",password);

                @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://witstutor.herokuapp.com/stuLogin.php",params) {
                    @Override
                    protected void onPostExecute(String output) {

                        if(validateStudent() == false){
                            Toast.makeText(StudentLogin.this,"one or more fields are missing!", Toast.LENGTH_SHORT).show();
                        }else {
                            loginStudent(output);
                        }
                    }

                };
                asyncHttpPost.execute();
            }
        });
    }

    private boolean validateStudent() {

        EditText stuNoET = findViewById(R.id.stuNoET);
        EditText passwET = findViewById(R.id.passwET);

        String studentNo = stuNoET.getText().toString().trim();
        String password = passwET.getText().toString().trim();

        if (studentNo.isEmpty()) {
            stuNoET.setError("Field can't be empty!");
            stuNoET.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwET.setError("Field can't be empty!");
            passwET.requestFocus();
            return false;
        }

        return true;
    }

    private void loginStudent(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message = jo.getString("message");

            if (success.equals("1")) {
                Toast.makeText(StudentLogin.this,message,Toast.LENGTH_SHORT).show();
                studentProfile();
            } else {
                Toast.makeText(StudentLogin.this,message,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void studentProfile() {

        EditText stuNoET = findViewById(R.id.stuNoET);
        String studentNo = stuNoET.getText().toString();
        Intent intent = new Intent(this, StudentProfile.class);
        intent.putExtra("STUDENT_NO", studentNo);
        startActivity(intent);
    }

    public void onClickStuRegBtn(View v) {
        Intent intent = new Intent(this, StuRegistration.class);
        startActivity(intent);
    }

}
