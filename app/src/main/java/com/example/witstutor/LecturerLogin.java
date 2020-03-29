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

public class LecturerLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectlogin);

        Button lecLoginBtn = findViewById(R.id.lecLoginBtn);
        lecLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                EditText perNoET = findViewById(R.id.perNoET);
                EditText passwET = findViewById(R.id.passwET);

                String personNo = perNoET.getText().toString();
                String password = passwET.getText().toString();

                ContentValues params = new ContentValues();

                params.put("LECTURER_NO",personNo);
                params.put("LECTURER_PASSWORD",password);

                @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://witstutor.herokuapp.com/LecLogin.php",params) {
                    @Override
                    protected void onPostExecute(String output) {

                        if(validateLecturer() == false){
                            Toast.makeText(LecturerLogin.this,"one or more fields are missing!", Toast.LENGTH_SHORT).show();
                        }else {
                            loginLecturer(output);
                        }
                    }

                };
                asyncHttpPost.execute();
            }
        });
    }

    private boolean validateLecturer() {

        EditText perNoET = findViewById(R.id.perNoET);
        EditText passwET = findViewById(R.id.passwET);

        String personNo = perNoET.getText().toString().trim();
        String password = passwET.getText().toString().trim();

        if (personNo.isEmpty()) {
            perNoET.setError("Field can't be empty!");
            perNoET.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwET.setError("Field can't be empty!");
            passwET.requestFocus();
            return false;
        }

        return true;
    }

    private void loginLecturer(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message =jo.getString("message");

            if (success.equals("1")) {
                Toast.makeText(LecturerLogin.this,message,Toast.LENGTH_SHORT).show();
                lecturerProfile();
            } else {
                Toast.makeText(LecturerLogin.this,message,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void lecturerProfile() {

        EditText personET = findViewById(R.id.perNoET);
        String personNo = personET.getText().toString();
        Intent intent = new Intent(this, LecturerProfile.class);
        intent.putExtra("LECTURER_NO", personNo);
        startActivity(intent);
    }

    public void onClickLecRegBtn(View v) {
        Intent intent = new Intent(this, LectRegistration.class);
        startActivity(intent);
    }

}
