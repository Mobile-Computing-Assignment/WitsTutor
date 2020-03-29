package com.example.witstutor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LectSubmittedAppl extends AppCompatActivity {

    ArrayList<String> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lectsubmittedappl);

        ListView l = findViewById(R.id.lectSubmittedApplLV);
        l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView)view).getText().toString();
                String student_no = selectedItem.substring(0,selectedItem.indexOf(" "));

                if(selectedItems.contains(student_no)){
                    selectedItems.remove(student_no);

                }else{
                    selectedItems.add(student_no);
                }
                //System.out.print(selectedItems);
            }

        });



        Button addtutorsbtn = findViewById(R.id.addTutorsBtn);
        addtutorsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTutors(selectedItems);
                finish();

            }
        });


        Intent intent = getIntent();
        final String course_name = intent.getStringExtra("COURSE_NAME");

        ContentValues params = new ContentValues();

        params.put("COURSE_NAME",course_name);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/fetchStuAppl.php",params) {
            @Override
            protected void onPostExecute(String output) {
                processSubmittedAppl(output);
            }

        };
        asyncHttpPost.execute();

    }

    public void insertTutors(ArrayList<String> selectedItems){

        String studentno="";
        for(int i = 0;i<selectedItems.size();i++){
            studentno=studentno+selectedItems.get(i);

            if(i!=selectedItems.size()-1){
                studentno=studentno+",";
            }
        }

        Intent intent = getIntent();
        final String course_name = intent.getStringExtra("COURSE_NAME");

        ContentValues params = new ContentValues();

        params.put("STUDENT_NO",studentno);
        params.put("COURSE_NAME",course_name);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/insertTutors.php",params) {
            @Override
            protected void onPostExecute(String output) {
                processInsertTutor(output);
            }

        };
        asyncHttpPost.execute();



    }

    public void processInsertTutor(String output){

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message = jo.getString("message");

            if(success.equals("1")) {
                //Successfully added tutors to the course
                Toast.makeText(LectSubmittedAppl.this,message,Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(LectSubmittedAppl.this,message,Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void processSubmittedAppl(String output) {

        ListView l = findViewById(R.id.lectSubmittedApplLV);
        l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        try {

            JSONArray ja = new JSONArray(output);

            //take the first value in the returned php JSONArray
            JSONObject test = (JSONObject)ja.get(0);

            //check to see if there are submitted applications for that studentAppl
            if(test.getString("STUDENT_NO").equals("0")) {

                List<String> myList = new ArrayList<>();
                JSONObject msg = (JSONObject) ja.get(1);
                String message = msg.getString("message");
                myList.add(message);
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, R.layout.rowlayout, R.id.checkedTextView, myList);
                l.setAdapter(mAdapter);

            } else {

                //if there are submitted applications for that studentAppl then display them on the list view

                List<String> myList = new ArrayList<>();
                for(int i = 0 ; i < ja.length();i++){

                    List<String> tempList = new ArrayList<>();

                    JSONObject jsonObject = (JSONObject)ja.get(i);
                    String student_no = jsonObject.getString("STUDENT_NO");
                    String student_fname =jsonObject.getString("STUDENT_FNAME");
                    String student_lname =jsonObject.getString("STUDENT_LNAME");
                    String details= student_no+" "+student_fname+" "+student_lname;

                    myList.add(details);
                }

                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, R.layout.rowlayout, R.id.checkedTextView, myList);

                l.setAdapter(mAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
