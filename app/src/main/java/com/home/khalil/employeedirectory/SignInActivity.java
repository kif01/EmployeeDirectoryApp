package com.home.khalil.employeedirectory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailID;
    private EditText role;
    private ProgressDialog mProgressDialog;
    private Button logIn;
    private String emailIdText;
    private String roleText;
    public static final String ROLE= "role";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailID=(EditText) findViewById(R.id.sign_in_email);
        role= (EditText) findViewById(R.id.sign_in_role);
        logIn = (Button) findViewById(R.id.sign_in_button);


        mProgressDialog= new ProgressDialog(this);
        mProgressDialog.setMessage("Logging in...");
        mAuth = FirebaseAuth.getInstance();

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailIdText= emailID.getText().toString();
                roleText= role.getText().toString().toLowerCase();
                SharedPreferences.Editor editor = getSharedPreferences(ROLE, MODE_PRIVATE).edit();

                if(validateForm()) {
                    editor.putString("r",roleText);
                    editor.commit();
                    mProgressDialog.show();
                    signIn(emailIdText, roleText);
                }
            }
        });
    }

    private void signIn(String email, final String role){
        mAuth.signInWithEmailAndPassword(email, role)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            mProgressDialog.hide();
                            mProgressDialog.dismiss();



                            Log.d("signIn", "signInWithEmail:success");
                            Intent i = new Intent(SignInActivity.this,MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                            ;
                        } else {
                            mProgressDialog.hide();
                            mProgressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w("signIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication Failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

   private boolean validateForm(){

        boolean valid = true;

        if (TextUtils.isEmpty(emailIdText)) {
            emailID.setError("Required.");
            valid = false;
        } else {
            emailID.setError(null);
        }

        if (TextUtils.isEmpty(roleText)) {
            role.setError("Required.");
            valid = false;
        } else {
            role.setError(null);
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent i = new Intent(SignInActivity.this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

}
