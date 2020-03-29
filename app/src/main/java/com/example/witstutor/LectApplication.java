package com.example.witstutor;

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

public class LectApplication extends AppCompatActivity {

    //List<String> applicationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lectapplication);


        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");

        ListView l = findViewById(R.id.displayCoursesLV);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView l = findViewById(R.id.displayCoursesLV);
                String courseselectedFromList =  (String) l.getItemAtPosition(position);
                Intent in = new Intent(LectApplication.this,LectSubmittedAppl.class);
                in.putExtra("COURSE_NAME", courseselectedFromList);
                startActivity(in);
            }
        });

        ContentValues params = new ContentValues();
        params.put("LECTURER_NO",personNo);

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/applications.php",params) {
            @Override
            protected void onPostExecute(String output) {
                processCourseAppl(output);
            }

        };
        asyncHttpPost.execute();

    }


    public void processCourseAppl(String output) {

        ListView l = findViewById(R.id.displayCoursesLV);
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
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(LectApplication.this, android.R.layout.simple_list_item_1, myList);
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

                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(LectApplication.this, android.R.layout.simple_list_item_1,myList);
                l.setAdapter(mAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void createApplication(View view){

        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");

        Intent in = new Intent(LectApplication.this,LectCourseList.class);
        in.putExtra("LECTURER_NO", personNo);
        startActivity(in);
    }

}
