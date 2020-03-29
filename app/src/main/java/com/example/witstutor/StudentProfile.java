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

public class StudentProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentprofile);

        Intent intent = getIntent();
        String student_no = intent.getStringExtra("STUDENT_NO");
        TextView stdno = findViewById(R.id.stuNoTV);
        stdno.setText(student_no);
        //String studNo = stdno.getText().toString().trim();


        ContentValues params = new ContentValues();

        params.put("STUDENT_NO",student_no);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/stuProfile.php",params) {
            @Override
            protected void onPostExecute(String output) {
            processStudent(output);

            }

        };
        asyncHttpPost.execute();

    }

    public void processStudent(String output){

        try {

            JSONArray ja = new JSONArray(output);
            for (int i=0; i<ja.length(); i++){

                JSONObject jo = (JSONObject)ja.get(i);

                TextView f_name = findViewById(R.id.nameTV);
                f_name.setText(jo.getString("STUDENT_FNAME"));

                TextView l_name = findViewById(R.id.surnameTV);
                l_name.setText(jo.getString("STUDENT_LNAME"));

                TextView  studying = findViewById(R.id.studyingTV);
                studying.setText(jo.getString("STUDENT_DEG_ENROLLED"));

                TextView yos = findViewById(R.id.yosTV);
                yos.setText(jo.getString("STUDENT_YOS"));

                TextView school = findViewById(R.id.schoolTV);
                school.setText(jo.getString("SCHOOL_NAME"));

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void onClickStuAppl(View view){

        TextView student_no = findViewById(R.id.stuNoTV);
        String studentNo = student_no.getText().toString();

        Intent intent = new Intent(this,StuViewApplications.class);
        intent.putExtra("STUDENT_NO",studentNo);
        startActivity(intent);

    }

    public void onClickViewStuRecord(View view){

        TextView student_no = findViewById(R.id.stuNoTV);
        String studentNo = student_no.getText().toString();

        Intent intent = new Intent(this,StuViewAttenRecord.class);
        intent.putExtra("STUDENT_NO",studentNo);
        startActivity(intent);

    }

}

