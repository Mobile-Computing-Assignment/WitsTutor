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

public class StuViewAttenRecord extends AppCompatActivity {

    List<String> cList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stuviewattenrecord);

        fetchStuAttenRecord();

        Intent intent = getIntent();
        final String student_no = intent.getStringExtra("STUDENT_NO");

        ListView l = findViewById(R.id.stuViewRecordLV);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView l = findViewById(R.id.stuViewRecordLV);
                String selectedFromList =  (String) l.getItemAtPosition(position);
                String storeSelection = selectedFromList.substring(0,selectedFromList.indexOf("\t"));

                Intent in = new Intent(StuViewAttenRecord.this,StuViewCourseAtten.class);
                in.putExtra("STUDENT_NO", student_no);
                in.putExtra("COURSE_NAME", storeSelection);
                startActivity(in);
            }
        });


    }

    public void fetchStuAttenRecord() {

        Intent intent = getIntent();
        final String student_no = intent.getStringExtra("STUDENT_NO");

        ContentValues params = new ContentValues();
        params.put("STUDENT_NO", student_no);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/fetchStuAttenRecord.php", params) {
            @Override
            protected void onPostExecute(String output) {

                processFetchStuAttenRecord(output);
            }

        };
        asyncHttpPost.execute();

    }

    public void processFetchStuAttenRecord(String output) {

        ListView l = findViewById(R.id.stuViewRecordLV);
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
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(StuViewAttenRecord.this, android.R.layout.simple_list_item_1, myList);
                l.setAdapter(mAdapter);


            } else {

                //if lecturer has selected courses it will loop through them and display them
                List<String> myList = new ArrayList<>();
                for(int i = 0 ; i < ja.length();i++){

                    JSONObject jsonObject = (JSONObject)ja.get(i);
                    String course_name = jsonObject.getString("COURSE_NAME");
                    String attendance_no = jsonObject.getString("ATTENDANCE_NO");
                    String details = course_name+"\t\t\t\t\t\t\t\t\t\t"+attendance_no;
                    myList.add(details);
                    cList.add(myList.get(i));
                }

                //Displays courses when clicking MY COURSES button.
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(StuViewAttenRecord.this, android.R.layout.simple_list_item_1, myList);
                l.setAdapter(mAdapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
