package com.example.witstutor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LectViewAttenRecord extends AppCompatActivity {

    List<String> cList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lectviewattenrecord);

        fetchCourses();

    }

    public void fetchCourses () {

        Intent intent = getIntent();
        final String course_name = intent.getStringExtra("COURSE_NAME");

        ContentValues params = new ContentValues();
        params.put("COURSE_NAME", course_name);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/fetchLectTutorAtten.php", params) {
            @Override
            protected void onPostExecute(String output) {

                processFetchTutorAtten(output);
            }

        };
        asyncHttpPost.execute();

    }

    public void processFetchTutorAtten(String output) {

        ListView l = findViewById(R.id.viewAttenRecLV);
        try {

            JSONArray ja = new JSONArray(output);

            //take the first value in the returned php JSONArray
            JSONObject test = (JSONObject)ja.get(0);

            //check to see if the lecturer has courses
            if(test.getString("TUTOR_ATTENDANCE_DATE").equals("0")) {

                List<String> myList = new ArrayList<>();
                JSONObject msg = (JSONObject) ja.get(1);
                String message = msg.getString("message");
                myList.add(message);
                cList.add(myList.get(0));
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(LectViewAttenRecord.this, android.R.layout.simple_list_item_1, myList);
                l.setAdapter(mAdapter);


            } else {

                //if lecturer has selected courses it will loop through them and display them
                List<String> myList = new ArrayList<>();
                for(int i = 0 ; i < ja.length();i++){

                    JSONObject jsonObject = (JSONObject)ja.get(i);
                    String attendance_date = jsonObject.getString("TUTOR_ATTENDANCE_DATE");
                    String attendance_no = jsonObject.getString("ATTENDANCE_NO");
                    String details = attendance_date+"\t\t\t\t\t\t\t\t\t\t"+attendance_no;
                    myList.add(details);
                    cList.add(myList.get(i));
                }

                //Displays courses when clicking MY COURSES button.
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(LectViewAttenRecord.this, android.R.layout.simple_list_item_1, myList);
                l.setAdapter(mAdapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
