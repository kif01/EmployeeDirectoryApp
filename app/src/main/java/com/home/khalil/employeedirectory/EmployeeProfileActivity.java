package com.home.khalil.employeedirectory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.home.khalil.employeedirectory.SignInActivity.ROLE;

public class EmployeeProfileActivity extends AppCompatActivity {
    private String name;
    private String role;
    private String imageUri;
    private String department;
    private String location;
    private String email;
    private String phoneNumber;
    private String id;
    private String manager;
    private ArrayList<String> directReportEmp;
    public static Activity epa;
    private static final int REQUEST_PERMISSION_CALL = 111;
    private String r;
    double lat;
    double lon;

    private ImageView profileImage;
    private TextView nameText;
    private TextView phoneText;
    private TextView emailText;
    private TextView departmentText;
    private TextView roleText;
    private TextView locationText;
    private CircleImageView circleProfileImage;
    private TextView managerName;
    private TextView managerRole;
    private CircleImageView managerImage;
    private FirebaseFirestore mFirestore;
    private Employee emp;
    private Employee empManager;
    private RecyclerView directEmployeeRCV;
    private DirectEmpRCV mAdapter;
    private TextView managerTitle;
    private TextView directTitle;
    private FloatingActionButton fabMessage;
    private FloatingActionButton fabMap;


    private ArrayList<Employee> directList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        SharedPreferences prefs = getSharedPreferences(ROLE, MODE_PRIVATE);

        r= prefs.getString("r","");

        epa = this;
        directList = new ArrayList<>();
        mAdapter = new DirectEmpRCV(directList);
        directEmployeeRCV = (RecyclerView) findViewById(R.id.direct_recyler_view);
        directEmployeeRCV.setHasFixedSize(true);
        directEmployeeRCV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        directEmployeeRCV.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        role = bundle.getString("role");
        //role = role.substring(0,1).toUpperCase() + role.substring(1).toLowerCase();
        imageUri = bundle.getString("imageUri");
        department = bundle.getString("department");
        location = bundle.getString("location");
        email = bundle.getString("email");
        phoneNumber = bundle.getString("phoneNumber");
        manager = bundle.getString("manager");
        directReportEmp = bundle.getStringArrayList("direct");
        id = bundle.getString("ID");

        Log.d("manager",manager);


        profileImage = (ImageView) findViewById(R.id.profile_image);
        circleProfileImage = (CircleImageView) findViewById(R.id.profile_circle_image);
        nameText = (TextView) findViewById(R.id.profile_name);
        roleText = (TextView) findViewById(R.id.profile_text_role);
        departmentText = (TextView) findViewById(R.id.profile_text_department);
        locationText = (TextView) findViewById(R.id.profile_text_location);
        emailText = (TextView) findViewById(R.id.profile_text_email);
        phoneText = (TextView) findViewById(R.id.profile_text_call);
        managerName = (TextView) findViewById(R.id.text_manager_name);
        managerRole = (TextView) findViewById(R.id.text_manager_role);
        managerImage = (CircleImageView) findViewById(R.id.manager_image);
        managerTitle = (TextView) findViewById(R.id.manager_title);
        directTitle = (TextView) findViewById(R.id.direct_title);
        fabMessage = (FloatingActionButton) findViewById(R.id.fab_message);
        fabMap = (FloatingActionButton) findViewById(R.id.fab_map);

        final Bundle bundle2 = new Bundle();

        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle2.putString("location",location);
                Intent i = new Intent(EmployeeProfileActivity.this,MapsActivity.class);
                i.putExtras(bundle2);
                startActivity(i);
            }


        });

        mFirestore = FirebaseFirestore.getInstance();

        if (imageUri.equals("empty")) {
            profileImage.setVisibility(View.INVISIBLE);
            circleProfileImage.setVisibility(View.VISIBLE);

        } else {
            profileImage.setVisibility(View.VISIBLE);
            circleProfileImage.setVisibility(View.INVISIBLE);
            Picasso.get().load(imageUri).into(profileImage);
        }
        nameText.setText(name);
        roleText.setText(role.substring(0,1).toUpperCase() + role.substring(1).toLowerCase());
        phoneText.setText(phoneNumber);
        emailText.setText(email);
        departmentText.setText(department);
        locationText.setText(location);

        emailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent emailIntent = new
                        Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                Log.d("email?","email: "+ email);
                emailIntent.setType("text/plain");
                emailIntent.putExtra
                        (Intent.EXTRA_EMAIL, new String[] { email });
                startActivity(emailIntent);
            }
        });

        phoneText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("clickedx", "clicked");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                if (ContextCompat.checkSelfPermission(view.getContext(),
                        android.Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions
                            (EmployeeProfileActivity.this,
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            REQUEST_PERMISSION_CALL);
                } else {
                    startActivity(callIntent);
                }
            }
        });

        fabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent msgIntent = new Intent(Intent.ACTION_VIEW);
                msgIntent.setData(Uri.parse("sms:" + phoneNumber));
                startActivity(msgIntent);
            }
        });

        FloatingActionButton edit_fab = (FloatingActionButton) findViewById(R.id.fab_edit);

        if(!r.equals("system administrator")){
            edit_fab.hide();
        }else {
            edit_fab.show();
        }

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("role", role);
                bundle.putString("imageUri", imageUri);
                bundle.putString("department", department);
                bundle.putString("location", location);
                bundle.putString("email", email);
                bundle.putString("phoneNumber", phoneNumber);
                bundle.putString("ID", id);

                Intent i = new Intent(EmployeeProfileActivity.this, EditEmployeeProfileActivity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        if (!manager.equals("No Manager")) {
            Log.d("opx",manager);
            mFirestore.collection("Employees").whereEqualTo
                    ("email", manager).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(EmployeeProfileActivity.this,
                                "Oops! An error has occurred.", Toast.LENGTH_SHORT).show();
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            empManager = doc.getDocument().toObject(Employee.class);


                            managerName.setText(empManager.getName());
                            managerRole.setText(empManager.getRole());
                            Picasso.get().load(empManager.getImageUri()).
                                    placeholder(R.drawable.progress_animation).into(managerImage);
                            break;

                            // Log.d("hey", "id: " + emp.print() + "\n");
                            //Log.d("zxz", "id: " + directList.toString()+ "\n");


                        }
                    }
                    Log.d("zxz", "id: " + directList.toString() + "\n");

                }
            });

        } else {
          Log.d("here",manager);
            managerName.setVisibility(View.GONE);
            managerRole.setVisibility(View.GONE);
            managerImage.setVisibility(View.GONE);
            managerTitle.setVisibility(View.GONE);
            directTitle.setPadding(0, 100, 0, 0);

        }


        Log.d("profile2", name + ", " + role + ", " + imageUri + ", " + department + ", " + location + ", " + email + ", " + phoneNumber + ", " + id);
        Log.d("lolz", directReportEmp.size() + "");
        if (directReportEmp.get(0) != null) {
            for (int i = 0; i < directReportEmp.size(); i++) {
                mFirestore.collection("Employees").
                        whereEqualTo("email", directReportEmp.get(i))
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(EmployeeProfileActivity.this,
                                    "Oops! An error has occurred.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                Employee emp = doc.getDocument().toObject(Employee.class);
                                directList.add(emp);
                                mAdapter.notifyDataSetChanged();
                                // Log.d("hey", "id: " + emp.print() + "\n");
                                //Log.d("zxz", "id: " + directList.toString()+ "\n");

                                break;
                            }
                        }
                        Log.d("zxz", "id: " + directList.toString() + "\n");

                    }
                });

            }
        } else {
            Log.d("lolz", "here:");
            managerTitle.setPadding(0, 50, 0, 0);
            directTitle.setVisibility(View.GONE);
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        mFirestore.collection("Employees").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d("Error", "Error:" + e.getMessage());
                }
                try {
                    emp = documentSnapshot.toObject(Employee.class);
                    nameText.setText(emp.getName());
                    roleText.setText(emp.getRole());
                    phoneText.setText(emp.getPhoneNumber());
                    emailText.setText(emp.getEmail());
                    departmentText.setText(emp.getDepartment());
                    locationText.setText(emp.getLocation());
                    String img = emp.getImageUri();
                    if (!img.equals("empty")) {
                        Log.d("eyx", "hereeyex");
                        Picasso.get().load(img).into(profileImage);
                    }

                    Log.d("trap", emp.getDepartment()+", "+emp.getLocation()+", "+emp.getRole());

                } catch (NullPointerException n) {

                }


            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CALL) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
        }
    }
}
