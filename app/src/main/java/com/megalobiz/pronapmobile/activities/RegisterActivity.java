package com.megalobiz.pronapmobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.megalobiz.pronapmobile.R;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_REGISTER = 0;

    private AppCompatButton buttonRegister;

    private TextInputEditText editTextFullName;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextPasswordConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = findViewById(R.id.btnRegister);
        editTextFullName = findViewById(R.id.fullName);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPasswordConf = findViewById(R.id.passwordConfirmation);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register() {
        Log.d(TAG, "Register");

        // TODO : Validate inputs
        /*if (!validate()) {
            onRegistrationFailed();
            return;
        }*/

        buttonRegister.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onRegisterSuccess();
                        // onRegisterFailed();
                        progressDialog.dismiss();
                    }
                }, 2000);
    }

    public void onRegisterSuccess() {
        buttonRegister.setEnabled(true);
        setResult(RESULT_OK);
        finish();
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        buttonRegister.setEnabled(true);
    }
}
