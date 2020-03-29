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

public class LectRegistration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectreg);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.SCHOOL_NAME,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button confBtn = findViewById(R.id.conBtn);
        confBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                EditText fnameET = findViewById(R.id.fnameET);
                EditText lnameET = findViewById(R.id.lnameET);
                EditText perNoET = findViewById(R.id.perNoET);
                EditText lecEmailET = findViewById(R.id.lecEmailET);
                Spinner spinner = findViewById(R.id.spinner);
                EditText adminCodeET = findViewById(R.id.adminCodeET);
                EditText passwET = findViewById(R.id.passwET);

                String fname = fnameET.getText().toString();
                String lname = lnameET.getText().toString();
                String personNo = perNoET.getText().toString();
                String lectEmail = lecEmailET.getText().toString();
                int schoolid = spinner.getSelectedItemPosition();
                String adminCode = adminCodeET.getText().toString();
                String password = passwET.getText().toString();

                ContentValues params = new ContentValues();

                params.put("LECTURER_NO",personNo);
                params.put("LECTURER_FNAME",fname);
                params.put("LECTURER_LNAME",lname);
                params.put("LECTURER_EMAIL",lectEmail);
                params.put("LECTURER_PASSWORD",password);
                params.put("SCHOOL_ID",schoolid);
                params.put("ADMIN_CODE_VALUE",adminCode);

                @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://witstutor.herokuapp.com/lecRegCheckEmail.php",params) {
                    @Override
                    protected void onPostExecute(String output) {

                        if(validate() == false){
                            Toast.makeText(getApplicationContext(),"one or more fields missing!",Toast.LENGTH_LONG).show();
                        } else {
                            registerLecturer(output);
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
        EditText perNoET = findViewById(R.id.perNoET);
        EditText lecEmailET = findViewById(R.id.lecEmailET);
        EditText adminCodeET = findViewById(R.id.adminCodeET);
        EditText passwET = findViewById(R.id.passwET);
        EditText cpasswET = findViewById(R.id.cpasswET);

        String fname = fnameET.getText().toString().trim();
        String lname = lnameET.getText().toString().trim();
        String personNo = perNoET.getText().toString().trim();
        String lectEmail = lecEmailET.getText().toString().trim();
        String adminCode = adminCodeET.getText().toString().trim();
        String password = passwET.getText().toString().trim();
        String cpassword = cpasswET.getText().toString().trim();

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

        if(personNo.isEmpty()) {
            perNoET.setError("Field can't be empty!");
            perNoET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        if(lectEmail.isEmpty()) {
            lecEmailET.setError("Field can't be empty!");
            lecEmailET.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(lectEmail).matches()) {
            lecEmailET.setError("check that your email is entered correctly!");
            lecEmailET.requestFocus();
            return false;
        } /*else {
            flag = false;
        }*/

        if(adminCode.isEmpty()) {
            adminCodeET.setError("Field can't be empty!");
            adminCodeET.requestFocus();
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
            cpasswET.setError("Field can't be empty!");
            cpasswET.requestFocus();
            return false;
        } else if (!cpassword.equals(password)) {
            cpasswET.setError("password doesn't match the above entered password!");
            cpasswET.requestFocus();
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

    private void registerLecturer(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message = jo.getString("message");

            if(success.equals("1")) {
                //Registration successful!
                Toast.makeText(LectRegistration.this,message,Toast.LENGTH_LONG).show();
                lecturerProfile();

            } else if(success.equals("-2")){
                //email already exists!, Please enter an alternate email address.
                Toast.makeText(LectRegistration.this,message,Toast.LENGTH_LONG).show();

            } else if(success.equals("-6")){
                //incorrect admin code!, Please try again.
                Toast.makeText(LectRegistration.this,message,Toast.LENGTH_LONG).show();
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

}
