package com.example.witstutor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StuViewApplications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stuviewapplications);

        Intent in = getIntent();
        final String studentNo = in.getStringExtra("STUDENT_NO");

        ListView l = findViewById(R.id.courseApplLV);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView l = findViewById(R.id.courseApplLV);
                String selectedFromList =  (String) l.getItemAtPosition(position);

                Intent in = new Intent(StuViewApplications.this,StudentApply.class);
                in.putExtra("STUDENT_NO", studentNo);
                in.putExtra("COURSE_NAME", selectedFromList);
                startActivity(in);
            }
        });

        Intent intent = getIntent();
        String person_no = intent.getStringExtra("LECTURER_NO");

        ContentValues params = new ContentValues();
        params.put("LECTURER_NO", person_no);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/stuCourseAppl.php", params) {
            @Override
            protected void onPostExecute(String output) {
                processAppl(output);
            }

        };
        asyncHttpPost.execute();

    }

    public void processAppl(String output) {

        ListView l = findViewById(R.id.courseApplLV);
        try {

            JSONArray ja = new JSONArray(output);

            //take the first value in the returned php JSONArray
            JSONObject test = (JSONObject)ja.get(0);

            //check to see if the lecturer has courses
            if(test.getString("COURSE_NAME").equals("0")) {

                List<String> myList = new ArrayList<>();
                JSONObject msg = (JSONObject) ja.get(1);
                String message = msg.getString("message");
                myList.add(message);
                //applicationList.add(myList.get(0));
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(StuViewApplications.this, android.R.layout.simple_list_item_1, myList);
                l.setAdapter(mAdapter);

            } else {

                //if lecturer has selected courses it will loop through them and display them
                List<String> myList = new ArrayList<>();
                for(int i = 0 ; i < ja.length();i++){

                    JSONObject jsonObject = (JSONObject)ja.get(i);
                    String course_name = jsonObject.getString("COURSE_NAME");
                    myList.add(course_name);
                    //applicationList.add(myList.get(i));
                }

                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(StuViewApplications.this, android.R.layout.simple_list_item_1,myList);
                l.setAdapter(mAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }





}
