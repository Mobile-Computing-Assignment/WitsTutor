package com.example.witstutor;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Calendar;

public class LectCreateApplication extends AppCompatActivity {

    private static final String TAG = "LectCreateApplication";
    private TextView mDisplayopenDate;
    private TextView mDisplaydueDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lectcreateappl);

        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");
        final String courseName = intent.getStringExtra("COURSE_NAME");

        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView opendateTV = findViewById(R.id.opendateTV);
                TextView duedateTV = findViewById(R.id.duedateTV);
                EditText maxtutor = findViewById(R.id.maxtutorET);

                String open_date = opendateTV.getText().toString();
                String due_date = duedateTV.getText().toString();
                String maxt = maxtutor.getText().toString();
                int appl_status = 1;// when they create an application the default is 1 (true)

                ContentValues params = new ContentValues();

                params.put("COURSE_APPL_OPENDATE",open_date);
                params.put("COURSE_APPL_DUEDATE",due_date);
                params.put("COURSE_APPL_MAXNO",maxt);
                params.put("COURSE_NAME",courseName);
                params.put("LECTURER_NO",personNo);
                params.put("COURSE_APPL_STATUS",appl_status);


                @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://witstutor.herokuapp.com/createCourseAppl.php",params) {
                    @Override
                    protected void onPostExecute(String output) {
                        if( validateApplication() == false){

                            Toast.makeText(getApplicationContext(),"one or more fields missing!",Toast.LENGTH_LONG).show();
                        } else {
                            createCourseAppl(output);
                            Intent in = new Intent(LectCreateApplication.this,LectApplication.class);
                            in.putExtra("LECTURER_NO", personNo);
                            startActivity(in);
                          //  finish();
                        }
                    }

                };
                asyncHttpPost.execute();
            }

        });

        mDisplayopenDate = findViewById(R.id.opendateTV);

        mDisplayopenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        LectCreateApplication.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day +  "/"  +month + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayopenDate.setText(date);
            }
        };

        mDisplaydueDate = findViewById(R.id.duedateTV);

        mDisplaydueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        LectCreateApplication.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener2,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day +  "/"  +month + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplaydueDate.setText(date);
            }
        };

    }
    private boolean validateApplication() {

        TextView opendateTV = findViewById(R.id.opendateTV);
        TextView duedateTV = findViewById(R.id.duedateTV);

        String open_date = opendateTV.getText().toString();
        String due_date = duedateTV.getText().toString();

        EditText maxtutor = findViewById(R.id.maxtutorET);
        String maxt = maxtutor.getText().toString().trim();

        if (open_date.equals("dd/mm/yyy")) {
            opendateTV.setError("invalid date!");
            opendateTV.requestFocus();
            return false;
        }

        if (due_date.equals("dd/mm/yyy")) {
            duedateTV.setError("invalid date!");
            duedateTV.requestFocus();
            return false;
        }
        if (maxt.isEmpty()) {
            maxtutor.setError("Field can't be empty!");
            maxtutor.requestFocus();
            return false;
        }

        return true;
    }

    public void createCourseAppl(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message = jo.getString("message");

            if(success.equals("1")) {
                //successfully created a studentAppl application!
                Toast.makeText(LectCreateApplication.this,message,Toast.LENGTH_LONG).show();
            } else {
                //unable to create an application for this studentAppl!.
                Toast.makeText(LectCreateApplication.this,message,Toast.LENGTH_LONG).show();
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
