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

public class LectViewCourseAtten extends AppCompatActivity {
    List<String> cList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lectviewcourseatten);



        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");

        fetchCourses();

        ListView l = findViewById(R.id.viewRecordLV);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView l = findViewById(R.id.viewRecordLV);
                String selectedFromList =  (String) l.getItemAtPosition(position);

                Intent in = new Intent(LectViewCourseAtten.this,LectViewAttenRecord.class);
                //in.putExtra("LECTURER_NO", personNo);
                in.putExtra("COURSE_NAME", selectedFromList);
                startActivity(in);
            }
        });


    }

    public void fetchCourses () {

        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");

        ContentValues params = new ContentValues();
        params.put("LECTURER_NO", personNo);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/fetchCourse.php", params) {
            @Override
            protected void onPostExecute(String output) {

                processFetchCourses(output);
            }

        };
        asyncHttpPost.execute();

    }

    public void processFetchCourses(String output) {

        ListView l = findViewById(R.id.viewRecordLV);
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
                cList.add(myList.get(0));
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(LectViewCourseAtten.this, android.R.layout.simple_list_item_1, myList);
                l.setAdapter(mAdapter);


            } else {

                //if lecturer has selected courses it will loop through them and display them
                List<String> myList = new ArrayList<>();
                for(int i = 0 ; i < ja.length();i++){

                    JSONObject jsonObject = (JSONObject)ja.get(i);
                    String course_name = jsonObject.getString("COURSE_NAME");
                    myList.add(course_name);
                    cList.add(myList.get(i));
                }

                //Displays courses when clicking MY COURSES button.
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(LectViewCourseAtten.this, android.R.layout.simple_list_item_1, myList);
                l.setAdapter(mAdapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
