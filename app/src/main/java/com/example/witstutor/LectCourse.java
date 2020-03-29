package com.example.witstutor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LectCourse extends AppCompatActivity {

    boolean[] checkedItems;
    String[] course ;
    List<String> cList = new ArrayList<>();
    ArrayList<Integer> selectedCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lectcourse);

        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");

        fetchCourses();

        ListView l = findViewById(R.id.coursesLV);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView l = findViewById(R.id.coursesLV);
                String selectedFromList =  (String) l.getItemAtPosition(position);

                Intent in = new Intent(LectCourse.this,LectDisplayTutors.class);
                in.putExtra("LECTURER_NO", personNo);
                in.putExtra("COURSE_NAME", selectedFromList);
                startActivity(in);
            }
        });

        Button addCourseBtn = findViewById(R.id.addCourseBtn);
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues params = new ContentValues();
                params.put("LECTURER_NO",personNo);

                @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://witstutor.herokuapp.com/selectCourses.php",params) {
                    @Override
                    protected void onPostExecute(String output) {

                        processCourses(output);

                        createDialog();
                    }

                };
                asyncHttpPost.execute();
            }

        });

    }

    //fetches courses based on lecturer's school id from database and stores it in studentAppl(array)
    public void processCourses(String output) {

        try {

            JSONArray ja = new JSONArray(output);
            course = new String[ja.length()];
            checkedItems = new boolean[course.length];

            for (int i = 0; i < ja.length(); i++){

                JSONObject jo = (JSONObject)ja.get(i);
                course[i] = jo.getString("COURSE_NAME");
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    //Displays an alert dialog with studentAppl names from studentAppl(array) that can be selected/checked
    public void createDialog() {

        for(int i = 0 ; i < cList.size();i++) {

            for(int j = 0; j < course.length;j++) {

                if(cList.get(i).equals(course[j])) {
                    selectedCourses.add(j);
                    checkedItems[j] = true;
                }
            }
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LectCourse.this);
        mBuilder.setTitle("Select your courses");

        //OKAY BUTTON
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String item = "";

                //item = item + studentAppl[selectedTutors.indexOf(pos)];
                for(int i = 0;i < selectedCourses.size();i++) {

                    item = item + course[selectedCourses.get(i)];

                    if(i != selectedCourses.size()-1) {
                        item += ",";
                    }
                }

                //Display courses from the database if the lecturer has selected a studentAppl(s)
                List<String> myList = new ArrayList<>(Arrays.asList(item.split(",")));

                //Pass selected courses to database
                setList(myList);
                fetchCourses();

            }

        });


        mBuilder.setMultiChoiceItems(course, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int pos, boolean isChecked) {

                String item1 = "";

                if (isChecked && !selectedCourses.contains(pos)) {

                    //if (!selectedTutors.contains(pos)) {
                    //If it is not part of the list of the selected courses, then add it to the list
                    selectedCourses.add(pos);
                    //}

                } else {

                    if(selectedCourses.contains(pos)) {

                        //Remove the unchecked courses from the lecturer's selected studentAppl list in the database
                        item1 = item1 + course[pos];// item contains courses that have been checked.
                        selectedCourses.remove(selectedCourses.indexOf(pos));
                        deleteCourse(item1);
                    }

                }
            }

        });

        //CANCEL BUTTON
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //CLEAR ALL BUTTON
        mBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i = 0; i < checkedItems.length;i++){
                    checkedItems[i] = false;
                    selectedCourses.clear();
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //Inserts courses that where selected by the lecturer from the alert dialog into the database
    public void setList(List<String> clist) {

        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");

        String courses = "";

        //loops through array containing courses(clist) and adds to a courses(string)
        for (int i = 0; i < clist.size(); i++) {
            courses = courses + clist.get(i);

            if(i != clist.size() - 1){
                courses = courses + ",";
            }

        }

        ContentValues params = new ContentValues();
        params.put("LECTURER_NO", personNo);
        params.put("COURSE_NAME", courses);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                    "https://witstutor.herokuapp.com/insertCourse.php", params) {
            @Override
            protected void onPostExecute(String output) {
                Toast.makeText(LectCourse.this,"successfully added your courses",Toast.LENGTH_SHORT).show();
            }

        };
        asyncHttpPost.execute();
    }

    //fetches courses taught by the lecturer from the database as selected from the alert dialog
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

    //takes fetched courses from the LECTURER_COURSE table and displays them on a list view,
    //if the lecturer has already selected the courses or displays a message to indicate that lecturer currently has no courses.
    public void processFetchCourses(String output) {

        ListView l = findViewById(R.id.coursesLV);
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
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(LectCourse.this, android.R.layout.simple_list_item_1, myList);
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
                ArrayAdapter<String> mAdapter = new ArrayAdapter<>(LectCourse.this, android.R.layout.simple_list_item_1,myList);
                l.setAdapter(mAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void deleteCourse(String item) {

        Intent intent = getIntent();
        final String personNo = intent.getStringExtra("LECTURER_NO");

        ContentValues params = new ContentValues();
        params.put("LECTURER_NO", personNo);
        params.put("COURSE_NAME", item);

        @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                "https://witstutor.herokuapp.com/deleteCourses.php", params) {
            @Override
            protected void onPostExecute(String output) {

                processDeleteCourse(output);
            }

        };
        asyncHttpPost.execute();

    }

    public void processDeleteCourse(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message = jo.getString("message");

            if(success.equals("1")) {
                //successfully removed the studentAppl!
                Toast.makeText(LectCourse.this,message,Toast.LENGTH_LONG).show();
            } else {
                //unable to remove the selected courses.
                Toast.makeText(LectCourse.this,message,Toast.LENGTH_LONG).show();
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
