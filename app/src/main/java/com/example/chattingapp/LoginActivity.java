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
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email, password;
    Button btn_login;
    FirebaseAuth auth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set Toolbar title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = email.getText().toString();
                String user_password = password.getText().toString();

                if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password)) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    new BackgroundLogin(LoginActivity.this, user_password, user_email).execute();
                }
            }
        });
    }

    class BackgroundLogin extends AsyncTask<Void, Void, Void> {

        LoginActivity LoginActivity;
        boolean enable = false;
        String user_pass;
        String user_email;

        public BackgroundLogin(LoginActivity LoginActivity,
                               String user_pass,
                               String user_email) {
            this.LoginActivity = LoginActivity;
            this.user_pass = user_pass;
            this.user_email = user_email;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity);
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
            auth.signInWithEmailAndPassword(user_email, user_pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                enable = true;
                                Intent intent = new Intent(LoginActivity.this,
                                        MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

            return null;
        }

    }

}