package com.example.witstutor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LectDisplayTutors extends AppCompatActivity {

   private TextView headTutorTV;

    private EditText txtHeadTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lectdisplaytutors);


        //Gets course name from previous activity
        Intent intent = getIntent();
        final String course_name = intent.getStringExtra("COURSE_NAME");


        headTutorTV = findViewById(R.id.headTutorTV);

        //populates listview with tutors
        ContentValues params = new ContentValues();
        params.put("COURSE_NAME",course_name);
        fetchTutors(params);
        //Fetches head tutor from database
        fetchHeadTutor(params);


        //Alert Dialog to enter head tutor student number
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(this);
        mBuilder.setTitle("Enter Head tutor student number");
        txtHeadTutor = new EditText(this);
        txtHeadTutor.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtHeadTutor.setWidth(193);
        mBuilder.setView(txtHeadTutor);

        //OKAY BUTTON
        mBuilder.setCancelable(true);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                headTutorTV.setText(txtHeadTutor.getText().toString());
                assignHeadTutor(headTutorTV.getText().toString(),course_name);

            }

        });
        //CANCEL BUTTON
        mBuilder.setNegativeButton("Cancel",null);

        //Create the Alert Dialog
        final android.app.AlertDialog mDialog = mBuilder.create();

        Button assignHTBtn = findViewById(R.id.assignHTBtn);
        assignHTBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Show alert dialog
                mDialog.show();

            }
        });

    }


    public void assignHeadTutor(String student_no, String course_name){

        ContentValues params = new ContentValues();
        params.put("STUDENT_NO",student_no);
        params.put("COURSE_NAME",course_name);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/updateHT.php",params) {
            @Override
            protected void onPostExecute(String output) {

                processAssignHT(output);
            }

        };
        asyncHttpPost.execute();
    }

    public void processAssignHT(String output){

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message = jo.getString("message");

            if(success.equals("1")) {
                //Update successful!
                Toast.makeText(LectDisplayTutors.this,message,Toast.LENGTH_LONG).show();


            } else if(success.equals("-3")) {
                //Incorrect student number entered on alertdialog
                Toast.makeText(LectDisplayTutors.this,message,Toast.LENGTH_LONG).show();
            } else if(success.equals("-2")) {
                //Head tutor already exists for this course
                Toast.makeText(LectDisplayTutors.this,message,Toast.LENGTH_LONG).show();
            }else{
                //failed to update head tutor
                Toast.makeText(LectDisplayTutors.this,message,Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void fetchTutors(ContentValues params){

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/fetchTutors.php",params) {
            @Override
            protected void onPostExecute(String output) {

                processFetchTutors(output);
            }

        };
        asyncHttpPost.execute();

    }


    public void processFetchTutors(String output){


        ListView  tutorslv = findViewById(R.id.displayTutorsLV);

        try {

            JSONArray ja = new JSONArray(output);

            //take the first value in the returned php JSONArray
            JSONObject test = (JSONObject)ja.get(0);

            //check to see if there are tutors for the selected course
            if(test.getString("STUDENT_NO").equals("0")) {

                List<String> myList = new ArrayList<>();
                JSONObject msg = (JSONObject) ja.get(1);
                String message = msg.getString("message");
                myList.add(message);
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
                tutorslv.setAdapter(mAdapter);

            } else {

                //if there are tutors for that course then display them on the list view

                List<String> myList = new ArrayList<>();
                for(int i = 0 ; i < ja.length();i++){


                    JSONObject jsonObject = (JSONObject)ja.get(i);
                    String student_no = jsonObject.getString("STUDENT_NO");
                    String student_fname =jsonObject.getString("STUDENT_FNAME");
                    String student_lname =jsonObject.getString("STUDENT_LNAME");
                    String details= student_no+" "+student_fname+" "+student_lname;

                    myList.add(details);
                }

                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);

                tutorslv.setAdapter(mAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void fetchHeadTutor(ContentValues params){

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/fetchHT.php",params) {
            @Override
            protected void onPostExecute(String output) {

                processFetchHT(output);
            }

        };
        asyncHttpPost.execute();

    }

    public void processFetchHT(String output){

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("STUDENT_NO");


            if(success.equals("0")) {
                //No head tutor assigned for this course
                String message = jo.getString("message");
                headTutorTV.setText(message);

            }else{
                //Head tutor exists
                String studentno = jo.getString("STUDENT_NO");
                String student_fname = jo.getString("STUDENT_FNAME");
                String student_lname = jo.getString("STUDENT_LNAME");
                String details = studentno+" "+student_fname+" "+student_lname;

                headTutorTV.setText(details);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
