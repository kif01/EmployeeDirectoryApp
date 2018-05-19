package com.home.khalil.employeedirectory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guna.libmultispinner.MultiSelectionSpinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddEmployeeActivity extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener{
    private EditText mNameText;
    private EditText mRoleText;
    private EditText mEmailText;
    private EditText mPhoneNumberText;
    private EditText mDepartmentText;
    private EditText mLocationText;
    private CircleImageView mProfilePic;
    MaterialSpinner departmentSpinner;
    MaterialSpinner locationSpinner;
    MaterialSpinner roleSpinner;
    private Spinner mgrSpinner;
    private HashMap<String,Employee> employees;
    //private Spinner spinnerManager;
    private ArrayList<String> names;
    private ArrayList<String> names2;
    private String name;
    private String role;
    private String email;
    private String phoneNumber;
    private String department;
    private String location;
    private Uri imageUri;
    private String userID;
   // String manager;
    MultiSelectionSpinner multiSelectionSpinner;
    private ArrayList<String> report;

    private FirebaseFirestore mFirestore;
    private FirebaseFirestore mFirestoreRead;
    StorageReference mStorageReference;
    private ProgressDialog mProgressDialog;

    private SpinnerAdapter adapter;
    private ArrayAdapter spinnerArrayAdapter;

    private String manager= "No Manager";
    private ArrayList<String> selectedDr;

    private FirebaseAuth mAuth;


    private static int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        final String[] departments={"Accounting and Finance","Entire Company", "Management", "Research and Development"};
        String[] locations={"London","Manchester","Southampton"};
        employees= new HashMap<>();
        names= new ArrayList();



        names2=new ArrayList<>();
        names2.add("No Manager");
        getSupportActionBar().setTitle("Add Employee");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // spinnerManager= findViewById(R.id.spinner_manager);
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner);
        mgrSpinner=(Spinner) findViewById(R.id.spinner_manager);

        mAuth = FirebaseAuth.getInstance();
       // mAuth.signOut();
        report=new ArrayList<>();
        report.add(null);

        mFirestore=FirebaseFirestore.getInstance();
        mFirestoreRead=FirebaseFirestore.getInstance();
        mStorageReference= FirebaseStorage.getInstance().getReference();
        mProgressDialog= new ProgressDialog(this);
        mProgressDialog.setMessage("Adding Employee");

        FloatingActionButton submit = (FloatingActionButton) findViewById(R.id.fab_add_submit);
        mNameText= (EditText) findViewById(R.id.add_name);
       // mRoleText= (EditText) findViewById(R.id.add_role);
        mEmailText=(EditText) findViewById(R.id.add_email);
        mPhoneNumberText= (EditText) findViewById(R.id.add_phone_number);
        //mDepartmentText= (EditText) findViewById(R.id.add_department);
        //mLocationText= (EditText) findViewById(R.id.add_location);
        mProfilePic=(CircleImageView) findViewById(R.id.add_profile_image);

        adapter =new SpinnerAdapter(AddEmployeeActivity.this, android.R.layout.simple_list_item_1);

        selectedDr= new ArrayList<>();
        selectedDr.add(null);
        departmentSpinner= (MaterialSpinner) findViewById(R.id.spinner_departments);
        departmentSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                departmentSpinner.setItems("Department", "Accounting and Finance","Entire Company", "Management", "Research and Development");

            }
        });

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

                                employees.put(emp.getName(),emp);

                            }
                        }

                        for(String name : employees.keySet()){
                            names.add(name);
                            names2.add(name);
                        }
                        multiSelectionSpinner.setItems(names);

                        adapter.addAll(names2);
                        adapter.add("No Manager");
                        mgrSpinner.setAdapter(adapter);
                        mgrSpinner.setSelection(adapter.getCount());


                    }
                });




      mgrSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i ==0){
                    manager="No Manager";

                }else{


                    try {
                        manager = employees.get(mgrSpinner.getSelectedItem().toString()).getEmail();

                    }catch (NullPointerException e){

                    }
                    //Log.d("qwerty", mgrSpinner.getSelectedItem().toString());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        //spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_row);
       // mgrSpinner.setAdapter(spinnerArrayAdapter);

        /*mgrSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
               // if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (AddEmployeeActivity.this, "Selected : " + selectedItemText, Toast.LENGTH_LONG)
                            .show();

                    Log.d("here",selectedItemText);
               // }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




      //  spinnerManager.setAdapter(spinnerArrayAdapter);
        //spinnerManager.onIt*/

        multiSelectionSpinner.setListener(this);


        locationSpinner=(MaterialSpinner) findViewById(R.id.spinner_locations);
        locationSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                locationSpinner.setItems("Location","London","Manchester","Southampton");
            }
        });


        roleSpinner=(MaterialSpinner) findViewById(R.id.spinner_roles);


        /*departmentSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });*/


        departmentSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {


            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                department = departmentSpinner.getText().toString();
                //View view = this.getCurrentFocus();


                if(department.equals("Entire Company")){
                    roleSpinner.setText("Entire Company");
                    roleSpinner.setItems("Chif Executive Officer");

                }else if(department.equals("Research and Development")){
                    roleSpinner.setItems("Chief Technology Officer","Director of Research and Development","Project Manager","Team Leader","Developer","Test Analyst");

                } else if(department.equals("Accounting and Finance")){
                    roleSpinner.setItems("Chief Financial Officer","Head of Accounting", "Head of Finance","Accountant","Financial Analyst");

                }else if(department.equals("Management")){
                    roleSpinner.setItems("Chief Management Officer","Brand Manager","Market Research Analyst","Sales Manager");
                }

            }
        });





        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_IMG);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Log.d("pizza", manager+ selectedDr.toString());

                name = mNameText.getText().toString();
                role = roleSpinner.getText().toString().toLowerCase();
                email = mEmailText.getText().toString();
                phoneNumber = mPhoneNumberText.getText().toString();
                location = locationSpinner.getText().toString();


                if (validateForm()) {
                    mProgressDialog.show();
                    if(imageUri == null){
                        storeEmployeeData(name,role,email,phoneNumber,department,"empty",location,manager,selectedDr);
                    }else{
                        storeEmployeeData(name,role,email,phoneNumber,department,imageUri.toString(),location,manager,selectedDr);


                        //uploadImagetoStorage(userID);
                    }
                    //createAccount("ab@flycoast.co.uk","sysadmin");

                    updateUI();



                }

            }
        });



    }

    private boolean validateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(name) || !name.contains(" ")) {
            mNameText.setError("Required.");
            valid = false;
        } else {
            mNameText.setError(null);
        }
        if (TextUtils.isEmpty(role)) {
            roleSpinner.setError("Required.");
            valid = false;
        } else {
            roleSpinner.setError(null);
        }
        if (TextUtils.isEmpty(email)) {
            mEmailText.setError("Required.");
            valid = false;
        } else {
            mEmailText.setError(null);
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberText.setError("Required.");
            valid = false;
        } else {
            mPhoneNumberText.setError(null);
        }
        if (TextUtils.isEmpty(department) ||
                department.equals("Department") ) {
            departmentSpinner.setError("Required.");
            valid = false;
        } else {
            departmentSpinner.setError(null);
        }
        if (TextUtils.isEmpty(location) ||
                location.equals("Location")  ) {
            locationSpinner.setError("Required.");
            valid = false;
        } else {
            locationSpinner.setError(null);
        }
        return valid;
    }

    private void updateUI(){
        mProfilePic.setImageResource(R.drawable.default_profile_pic);
        mNameText.setText(null);
        roleSpinner.setText(null);
        mEmailText.setText(null);
        mPhoneNumberText.setText(null);
        departmentSpinner.setText(null);
        locationSpinner.setText(null);
    }

    private void uploadImagetoStorage(final String ID){
        Log.d("here","waw");
        StorageReference filePath= mStorageReference.child("EmployeesProfilePics").child(userID);
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("test1","Image successfully uploaded");
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mFirestore.collection("Employees").document(ID).update("imageUri",downloadUrl.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("test2","Error");
            }
        });
    }

    private void storeEmployeeData(String name, String role, final String email, String phoneNumber, String department,
                                   String imagePath, String location,String manager, ArrayList<String>direct){
        Employee e= new Employee(department,direct,email,imagePath,location, manager,name,phoneNumber,role);

        createAccount(email,role);
        mFirestore.collection("Employees").add(e).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
               // Toast.makeText(AddEmployeeActivity.this,
                        //"Employee successfully added", Toast.LENGTH_SHORT).show();
               // mProgressDialog.hide();
                //mProgressDialog.dismiss();
                if(imageUri!=null) {

                    mFirestoreRead.collection("Employees").whereEqualTo("email", email).
                            addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(AddEmployeeActivity.this,
                                        "Oops! An error has occurred.", Toast.LENGTH_SHORT).show();
                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    userID = doc.getDocument().getId();
                                    Log.d("hey", "id: " + userID + "\n");
                                    uploadImagetoStorage(userID);
                                    break;
                                }
                            }
                            Log.d("what2","here2");


                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddEmployeeActivity.this, "Ooops! An error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri imgUri = data.getData();
                mProfilePic.setImageURI(imgUri);
                imageUri = imgUri;

            }
        } catch (Exception e) {
            Toast.makeText(this, "Oops! Something went wrong.", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        if(itemId == android.R.id.home){
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void selectedIndices(List<Integer> indices) {
        Toast.makeText(this, indices.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void selectedStrings(List<String> strings) {
        selectedDr= new ArrayList<>();
        Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();
       // Log.d("icy", strings.size()+"");
        if(strings.size()==0){
            selectedDr.add(null);
        }

        for(int i=0; i < strings.size(); i++){
            Employee e= employees.get(strings.get(i));
            selectedDr.add(e.getEmail());
        }


    }

    private void createAccount(String email, String password) {
        //Log.d(TAG, "createAccount:" + email);
      //  mAuth = FirebaseAuth.getInstance();

        // showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            Toast.makeText(AddEmployeeActivity.this, "Succes.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("jalamoca", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AddEmployeeActivity.this, "Oops! An error occured. Try Again.",
                                    Toast.LENGTH_LONG).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        mProgressDialog.hide();
                        mProgressDialog.dismiss();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }


}
