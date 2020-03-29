package com.example.witstutor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class LecturerProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecprofile);

        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");

        ContentValues params = new ContentValues();
        params.put("LECTURER_NO",personNo);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/lecProfile.php",params) {
            @Override
            protected void onPostExecute(String output) {
                processLecturer(output);
                TextView person_no = findViewById(R.id.perNoTV);
                person_no.setText(personNo);
            }

        };
        asyncHttpPost.execute();
    }

    public void processLecturer(String output){

        try {

            JSONArray ja = new JSONArray(output);
            for (int i=0; i<ja.length(); i++){

                JSONObject jo = (JSONObject)ja.get(i);

                TextView f_name = findViewById(R.id.nameTV);
                f_name.setText(jo.getString("LECTURER_FNAME"));

                TextView l_name = findViewById(R.id.surnameTV);
                l_name.setText(jo.getString("LECTURER_LNAME"));

                TextView school = findViewById(R.id.schoolTV);
                school.setText(jo.getString("SCHOOL_NAME"));

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onclickmyCourse(View v) {

        TextView person_no = findViewById(R.id.perNoTV);
        String personNo = person_no.getText().toString();

        Intent intent = new Intent(this, LectCourse.class);
        intent.putExtra("LECTURER_NO", personNo);
        startActivity(intent);
    }

    public void onClickApplications(View view) {

        TextView person_no = findViewById(R.id.perNoTV);
        String personNo = person_no.getText().toString();

        Intent intent = new Intent(this,LectApplication.class);
        intent.putExtra("LECTURER_NO", personNo);
        startActivity(intent);
    }

    public void onClickAttenCourse(View view){

        TextView person_no = findViewById(R.id.perNoTV);
        String personNo = person_no.getText().toString();

        Intent intent = new Intent(this,LectAttenCourse.class);
        intent.putExtra("LECTURER_NO", personNo);
        startActivity(intent);

    }
    public void onClickAttenRecord(View view){

        TextView person_no = findViewById(R.id.perNoTV);
        String personNo = person_no.getText().toString();

        Intent intent = new Intent(this,LectViewCourseAtten.class);
        intent.putExtra("LECTURER_NO", personNo);
        startActivity(intent);

    }


}
