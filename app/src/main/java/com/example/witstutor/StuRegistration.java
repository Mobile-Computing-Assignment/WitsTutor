package com.example.witstutor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class StuRegistration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stureg);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.SCHOOL_NAME,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button confBtn = findViewById(R.id.confBtn);
        confBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                EditText stuNoET = findViewById(R.id.stuNoET);
                EditText fnameET = findViewById(R.id.fnameET);
                EditText lnameET = findViewById(R.id.lnameET);
                EditText degEnrolledET = findViewById(R.id.degEnrolledET);
                EditText yosET = findViewById(R.id.yosET);
                EditText stuEmailET = findViewById(R.id.stuEmailET);
                EditText passwET = findViewById(R.id.passwET);
                Spinner spinner = findViewById(R.id.spinner);

                String studentNo = stuNoET.getText().toString();
                String fname = fnameET.getText().toString();
                String lname = lnameET.getText().toString();
                String degEnrolled = degEnrolledET.getText().toString();
                String yos = yosET.getText().toString();
                String stuEmail = stuEmailET.getText().toString();
                String password = passwET.getText().toString();
                int schoolid = spinner.getSelectedItemPosition();

                ContentValues params = new ContentValues();

                params.put("STUDENT_NO",studentNo);
                params.put("STUDENT_FNAME",fname);
                params.put("STUDENT_LNAME",lname);
                params.put("STUDENT_DEG_ENROLLED",degEnrolled);
                params.put("STUDENT_YOS",yos);
                params.put("STUDENT_EMAIL",stuEmail);
                params.put("STUDENT_PASSWORD",password);
                params.put("SCHOOL_ID",schoolid);

                @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://witstutor.herokuapp.com/stuRegCheckEmail.php",params) {
                    @Override
                    protected void onPostExecute(String output) {

                        if(validate() == false){

                            Toast.makeText(getApplicationContext(),"one or more fields missing!",Toast.LENGTH_LONG).show();
                        } else {
                            registerStudent(output);
                        }
                    }

                };
                asyncHttpPost.execute();
            }
        });
    }

    //handles the case when nothing is entered or incorrect details in one or more fields
    private boolean validate() {

        EditText fnameET = findViewById(R.id.fnameET);
        EditText lnameET = findViewById(R.id.lnameET);
        EditText stuNoET = findViewById(R.id.stuNoET);
        EditText stuEmailET = findViewById(R.id.stuEmailET);
        EditText degEnrolledET = findViewById(R.id.degEnrolledET);
        EditText yosET = findViewById(R.id.yosET);
        EditText passwET = findViewById(R.id.passwET);
        EditText cpassET = findViewById(R.id.cpassET);

        String fname = fnameET.getText().toString().trim();
        String lname = lnameET.getText().toString().trim();
        String studentNo = stuNoET.getText().toString().trim();
        String stuEmail = stuEmailET.getText().toString().trim();
        String degEnrolled = degEnrolledET.getText().toString().trim();
        String yos = yosET.getText().toString().trim();
        String password = passwET.getText().toString().trim();
        String cpassword = cpassET.getText().toString().trim();

        //boolean flag;

        if(fname.isEmpty()) {
            fnameET.setError("Field can't be empty!");
            fnameET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        if(lname.isEmpty()) {
            lnameET.setError("Field can't be empty!");
            lnameET.requestFocus();
            return false;
        } /*else {
             flag = false;
        }*/

        if(studentNo.isEmpty()) {
            stuNoET.setError("Field can't be empty!");
            stuNoET.requestFocus();
            return false;
        } else if (studentNo.length() > 7) {
            stuNoET.setError("invalid student number!");
            stuNoET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        if(stuEmail.isEmpty()) {
            stuEmailET.setError("Field can't be empty!");
            stuEmailET.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(stuEmail).matches()) {
            stuEmailET.setError("check that your student email is entered correctly!");
            stuEmailET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        if(degEnrolled.isEmpty()) {
            degEnrolledET.setError("Field can't be empty!");
            degEnrolledET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        if(yos.isEmpty()) {
            yosET.setError("Field can't be empty!");
            yosET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        if(password.isEmpty()) {
            passwET.setError("Field can't be empty!");
            passwET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        if(cpassword.isEmpty()) {
            cpassET.setError("Field can't be empty!");
            cpassET.requestFocus();
            return false;
        } else if (!cpassword.equals(password)) {
            cpassET.setError("password doesn't match the above entered password!");
            cpassET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        return true;
    }

    public boolean isEnabled(int position) {

        if (position == 0) {
            // Disable the first item from Spinner
            // First item will be used for hint
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String text = parent.getItemAtPosition(position).toString();
        Spinner sp = findViewById(R.id.spinner);
        TextView tv = (TextView) view;

        if (position == 0) {
            isEnabled(position);
            ((TextView)sp.getSelectedView()).setError("selected item is invalid!");

            // Set the hint text color gray
            tv.setTextColor(Color.rgb(112,128,144));
        } else {
            tv.setTextColor(Color.BLACK);
        }

        if (position > 0) {
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void registerStudent(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message = jo.getString("message");

            if(success.equals("1")) {
                //Registration successful!
                Toast.makeText(StuRegistration.this,message,Toast.LENGTH_LONG).show();
                stuProfile();
            } else if(success.equals("-1")){
                // you didn't send the required values!
                Toast.makeText(StuRegistration.this,message,Toast.LENGTH_LONG).show();
            } else if(success.equals("0")){
                // email already exists!, Please enter your student email.
                Toast.makeText(StuRegistration.this,message,Toast.LENGTH_LONG).show();
            } else if(success.equals("2")){
                // student number in email doesn't match one given above!
                Toast.makeText(StuRegistration.this,message,Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void stuProfile() {

        EditText stuNoET = findViewById(R.id.stuNoET);
        String studentNo = stuNoET.getText().toString();
        Intent intent = new Intent(this, StudentProfile.class);
        intent.putExtra("STUDENT_NO", studentNo);
        startActivity(intent);
    }

}
