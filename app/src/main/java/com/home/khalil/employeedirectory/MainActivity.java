package com.home.khalil.employeedirectory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.claudiodegio.msv.MaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.home.khalil.employeedirectory.SignInActivity.ROLE;

public class MainActivity extends AppCompatActivity implements OnSearchViewListener {

    private RecyclerView employeeRCV;
    private FirebaseFirestore mFirestore;
    private ArrayList<Employee> employeeList;
    private EmployeeRecycleViewAdapter mAdapter;
    private MaterialSearchView mSearchView;
    private FirebaseAuth mAuth;
    private boolean isClosed=true;
    private ProgressBar spinner;
    private String role;

    public static Activity main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSearchView = (MaterialSearchView) findViewById(R.id.sv);
        mSearchView.setOnSearchViewListener(this);
        main=this;

        SharedPreferences prefs = getSharedPreferences(ROLE, MODE_PRIVATE);

        role= prefs.getString("r","");

        Log.d("shpx","role: "+role);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i= new Intent(MainActivity.this, AddEmployeeActivity.class);
               startActivity(i);
            }
        });

        if(!role.equals("system administrator")){
            fab.hide();
        }else {
            fab.show();
        }

        employeeList= new ArrayList<>();
        mFirestore=FirebaseFirestore.getInstance();
        mAdapter= new EmployeeRecycleViewAdapter(employeeList);
        employeeRCV= (RecyclerView) findViewById(R.id.main_recycler_view);
        employeeRCV.setHasFixedSize(true);
        employeeRCV.setLayoutManager(new LinearLayoutManager(this));
        employeeRCV.setAdapter(mAdapter);

       // mAuth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);



        mFirestore.collection("Employees").orderBy("name", Query.Direction.ASCENDING).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!= null){

                    Log.d("Error", "Error:"+ e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String id=doc.getDocument().getId();
                        Employee emp= doc.getDocument().toObject(Employee.class).withID(id);
                        Log.d("blablax1",emp.getDirectReportEmp().toString());

                       //Log.d("herex",emp.getId());
                        employeeList.add(emp);
                        mAdapter.notifyDataSetChanged();

                    }
                }

                spinner.setVisibility(View.GONE);
            }
        });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent i= new Intent(MainActivity.this,SignInActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSearchViewShown() {
        isClosed=false;

    }

    @Override
    public void onSearchViewClosed() {
      // employeeRCV= (RecyclerView) findViewById(R.id.main_recycler_view);
        employeeRCV.setHasFixedSize(true);
        employeeRCV.setLayoutManager(new LinearLayoutManager(this));
        employeeRCV.setAdapter(mAdapter);
        isClosed=true;

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public void onQueryTextChange(String s) {
        if(s!=null && !s.isEmpty()){
            ArrayList<Employee> list= new ArrayList<>();
            for(Employee emp: employeeList){
                String name=emp.getName().toLowerCase();
                Log.d("ice1",name.indexOf(" ")+"");
                if(name.startsWith(s.toLowerCase(),name.
                        indexOf(" ")+1) || name.startsWith(s.toLowerCase())){
                    list.add(emp);
                }
            }
            EmployeeRecycleViewAdapter adapter= new EmployeeRecycleViewAdapter(list);
            employeeRCV.setHasFixedSize(true);
            employeeRCV.setLayoutManager(new LinearLayoutManager(this));
            employeeRCV.setAdapter(adapter);

        }else{
            employeeRCV.setHasFixedSize(true);
            employeeRCV.setLayoutManager(new LinearLayoutManager(this));
            employeeRCV.setAdapter(mAdapter);
        }

    }

    @Override
    public void onBackPressed() {
        if(isClosed==false) {
            Log.d("CDA", "onBackPressed Called");
            mSearchView.closeSearch();
        }else{
            finish();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        final ArrayList<Employee> employeeList2= new ArrayList<>();
        mFirestore.collection("Employees").orderBy("name", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!= null){

                    Log.d("Error", "Error:"+ e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String id=doc.getDocument().getId();
                        Employee emp= doc.getDocument().toObject(Employee.class).withID(id);
                        employeeList2.add(emp);
                        mAdapter.notifyDataSetChanged();
                        Log.d("blablax1",emp.getDirectReportEmp().toString());

                    }
                }
            }
        });



        mAdapter= new EmployeeRecycleViewAdapter(employeeList2);
       // employeeRCV= (RecyclerView) findViewById(R.id.main_recycler_view);
        employeeRCV.setHasFixedSize(true);
        employeeRCV.setLayoutManager(new LinearLayoutManager(this));
        employeeRCV.setAdapter(mAdapter);

    }
}
