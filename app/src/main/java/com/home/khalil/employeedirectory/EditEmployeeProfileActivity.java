package com.home.khalil.employeedirectory;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.guna.libmultispinner.MultiSelectionSpinner;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.home.khalil.employeedirectory.SignInActivity.ROLE;

public class EditEmployeeProfileActivity extends AppCompatActivity  {
    private String name;
    private String role;
    private String imageUri;
    private String department;
    private String location;
    private String email;
    private String phoneNumber;
    private String id;
    private String managerEmployee;
    private ArrayList<String> reports;

    private ArrayList<String> items;


    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    private CircleImageView circleProfileImage;

    MaterialSpinner departmentSpinner;
    MaterialSpinner locationSpinner;
    MaterialSpinner roleSpinner;



    private static int RESULT_LOAD_IMG = 1;
    private Uri imageUriUpdate;
    private FirebaseFirestore mFirestore;
    StorageReference mStorageReference;
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee_profile);




        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        departmentSpinner= (MaterialSpinner) findViewById(R.id.edit_spinner_departments);
        locationSpinner=(MaterialSpinner) findViewById(R.id.edit_spinner_locations);
        roleSpinner=(MaterialSpinner) findViewById(R.id.edit_spinner_roles);


        departmentSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                departmentSpinner.setItems("Department", "Accounting and Finance","Entire Company", "Management", "Research and Development");

            }
        });


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

       /* departmentSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {


            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                department = departmentSpinner.getText().toString();
                //View view = this.getCurrentFocus();


                if(department.equals("Entire Company")){
                    roleSpinner.setText("Entire Company");
                    items=new ArrayList<>();
                    items.add("Chief Executive Officer");
                    roleSpinner.setItems(items);

                }else if(department.equals("Research and Development")){
                    items=new ArrayList<>();
                    items.add("Chief Technology Officer");
                    items.add("Director of Research and Development");
                    items.add("Project Manager");
                    items.add("Team Leader");
                    items.add("Test Analyst");
                    roleSpinner.setItems(items);

                } else if(department.equals("Accounting and Finance")){
                    items=new ArrayList<>();
                    items.add("Chief Financial Officer");
                    items.add("Head of Accounting");
                    items.add("Head of Finance");
                    items.add("Accountant");
                    items.add("Financial Analyst");
                    roleSpinner.setItems(items);

                }else if(department.equals("Management")){

                    items=new ArrayList<>();
                    items.add("Chief Management Officer");
                    items.add("Brand Manager");
                    items.add("Marker Research Analyst");
                    items.add("Sales Manager");
                    roleSpinner.setItems(items);
                }

            }
        });*/

        roleSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                department = departmentSpinner.getText().toString();
                //View view = this.getCurrentFocus();


                if(department.equals("Entire Company")){
                    roleSpinner.setText("Entire Company");
                    items=new ArrayList<>();
                    items.add("Chief Executive Officer");
                    roleSpinner.setItems(items);

                }else if(department.equals("Research and Development")){
                    items=new ArrayList<>();
                    items.add("Chief Technology Officer");
                    items.add("Director of Research and Development");
                    items.add("Project Manager");
                    items.add("Team Leader");
                    items.add("Test Analyst");
                    roleSpinner.setItems(items);

                } else if(department.equals("Accounting and Finance")){
                    items=new ArrayList<>();
                    items.add("Chief Financial Officer");
                    items.add("Head of Accounting");
                    items.add("Head of Finance");
                    items.add("Accountant");
                    items.add("Financial Analyst");
                    roleSpinner.setItems(items);

                }else if(department.equals("Management")){

                    items=new ArrayList<>();
                    items.add("Chief Management Officer");
                    items.add("Brand Manager");
                    items.add("Marker Research Analyst");
                    items.add("Sales Manager");
                    roleSpinner.setItems(items);
                }

            }
        });




        mFirestore=FirebaseFirestore.getInstance();
        mStorageReference= FirebaseStorage.getInstance().getReference();

        mProgressDialog= new ProgressDialog(this);
        mProgressDialog.setMessage("Updating Employee");

        Bundle bundle = getIntent().getExtras();
        name= bundle.getString("name");
        role= bundle.getString("role");
        imageUri= bundle.getString("imageUri");
        department= bundle.getString("department");
        location= bundle.getString("location");
        email= bundle.getString("email");
        phoneNumber= bundle.getString("phoneNumber");
        id=bundle.getString("ID");
        reports=bundle.getStringArrayList("reports");




        circleProfileImage=(CircleImageView) findViewById(R.id.edit_profile_image);
        nameEditText=(EditText) findViewById(R.id.edit_text_name);
       // roleEditText=(EditText) findViewById(R.id.edit_text_role);
        //departmentEditText=(EditText) findViewById(R.id.edit_text_department);
        //locationEditText=(EditText) findViewById(R.id.edit_text_location);
        emailEditText=(EditText) findViewById(R.id.edit_text_email);
        phoneEditText=(EditText) findViewById(R.id.edit_text_phone);



        if(!imageUri.equals("empty")) {
            Picasso.get().load(imageUri).into(circleProfileImage);
        }
        nameEditText.setText(name);
        //roleEditText.setText(role);
        phoneEditText.setText(phoneNumber);
        emailEditText.setText(email);
        roleSpinner.setText(role);
        departmentSpinner.setText(department);
        locationSpinner.setText(location);
        //departmentEditText.setText(department);
        //locationEditText.setText(location);

        circleProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_IMG);
            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (itemId == R.id.action_done) {
            if(validateForm()){

               updateEmployeeData(id);

               super.onBackPressed();
               // Toast.makeText(this,"done",Toast.LENGTH_SHORT).show();
            }
        }

        if(itemId == R.id.action_remove){
            mFirestore.collection("Employees").document(id).delete();

                Intent i = new Intent(EditEmployeeProfileActivity.this, MainActivity.class);
                startActivity(i);
                EmployeeProfileActivity.epa.finish();
                MainActivity.main.finish();
                finish();

           // Toast.makeText(this,"remove",Toast.LENGTH_SHORT).show();
        }

        if(itemId == android.R.id.home){
           super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri uri = data.getData();
                circleProfileImage.setImageURI(uri);
                imageUriUpdate = uri;

            }
        } catch (Exception e) {
            Toast.makeText(this, "Oops! Something went wrong.", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String name2=nameEditText.getText().toString();
        //String role2=roleEditText.getText().toString();

        String email2=emailEditText.getText().toString();
        String phoneNumber2= phoneEditText.getText().toString();
        String role2= roleSpinner.getText().toString();
        String location2= locationSpinner.getText().toString();
        String department2= departmentSpinner.getText().toString();
       // String location2=locationEditText.getText().toString();
        //String department2=departmentEditText.getText().toString();



        if (TextUtils.isEmpty(name2) || !name2.contains(" ")) {
            nameEditText.setError("Required.");
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        if (TextUtils.isEmpty(role2)) {
            roleSpinner.setError("Required.");
            valid = false;
        } else {
            roleSpinner.setError(null);
        }
        if (TextUtils.isEmpty(email2)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }
        if (TextUtils.isEmpty(phoneNumber2)) {
            phoneEditText.setError("Required.");
            valid = false;
        } else {
            phoneEditText.setError(null);
        }
        if (TextUtils.isEmpty(department2) || department2.equals("Department") ) {
            departmentSpinner.setError("Required.");
            valid = false;
        } else {
            departmentSpinner.setError(null);
        }
        if (TextUtils.isEmpty(location2) || location2.equals("Location")  ) {
            locationSpinner.setError("Required.");
            valid = false;
        } else {
            locationSpinner.setError(null);
        }

        return valid;
    }

    private void updateEmployeeData(final String id){
        name=nameEditText.getText().toString();

        phoneNumber=phoneEditText.getText().toString();
        email=emailEditText.getText().toString();
        //department=departmentEditText.getText().toString();
        //role=roleEditText.getText().toString();
        //location=locationEditText.getText().toString();
        department=departmentSpinner.getText().toString();
        location=locationSpinner.getText().toString();
        role=roleSpinner.getText().toString();
        Map<String,Object> emp=new HashMap<>();
        emp.put("name",name);
        emp.put("role",role);
        emp.put("email",email);
        emp.put("phoneNumber",phoneNumber);
        emp.put("department",department);
        emp.put("location",location);



        mFirestore.collection("Employees").document(id).update(emp);
        if(imageUriUpdate!=null) {

            uploadImagetoStorage(id);
        }



    }

    private void uploadImagetoStorage(final String ID){
        Log.d("here","waw");
        StorageReference filePath= mStorageReference.child("EmployeesProfilePics").child(ID);
        filePath.putFile(imageUriUpdate).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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



}

