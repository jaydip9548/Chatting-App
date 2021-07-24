package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText username, email, password;
    Button btn;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//Set Toolbar title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String user_name = username.getText().toString();
//                String user_email = email.getText().toString();
//                String user_pass = password.getText().toString();

                String user_name = "jaydip";
                String user_email = "jaydip12@gmail.com";
                String user_pass = "123456";

                if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_pass)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (user_pass.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    new BackgroundTask(RegisterActivity.this, user_name, user_email, user_pass).execute();
                }
            }
        });


    }


    class BackgroundTask extends AsyncTask<Void, Void, Void> {

        RegisterActivity registerActivity;
        boolean enable = false;
        String user_name;
        String user_pass;
        String user_email;

        public BackgroundTask(RegisterActivity registerActivity, String user_name,
                              String user_pass,
                              String user_email) {
            this.registerActivity = registerActivity;
            this.user_name = user_name;
            this.user_pass = user_pass;
            this.user_email = user_email;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(registerActivity);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog);
            progressDialog.getWindow()
                    .setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (enable) {
                progressDialog.dismiss();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {

            auth.createUserWithEmailAndPassword(user_email, user_pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;
                                String userid = firebaseUser.getUid();
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userid);
                                hashMap.put("username", user_name);
//                                hashMap.put("password", user_pass);
                                hashMap.put("imageUrl", "default");

                                reference.setValue(hashMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    enable = true;
                                                    Intent intent = new Intent(registerActivity, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(RegisterActivity.this, "You can't register with this email and password", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

            return null;
        }

    }
}

