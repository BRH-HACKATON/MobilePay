package com.brh.pronapmobile.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.models.User;

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
        if (!validate()) {
            onRegisterFailed();
            return;
        }

        buttonRegister.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        String name = editTextFullName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String passwordConf = editTextPasswordConf.getText().toString();

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
        // set shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", editTextEmail.getText().toString());
        editor.putString("full_name", editTextFullName.getText().toString());
        editor.commit();

        buttonRegister.setEnabled(true);
        setResult(RESULT_OK);
        finish();
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        buttonRegister.setEnabled(true);
    }

    public boolean validate() {
        if(editTextFullName.getText().toString() == "" || editTextEmail.getText().toString() == ""
                || editTextPassword.getText().toString() == "") {

            Toast.makeText(this, "Vous devez rentrer votre Nom Complet, un Email et un Mot de Passe",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(editTextPassword.getText().toString() != editTextPasswordConf.getText().toString()) {
            Toast.makeText(this, "La confirmation de votre Mot de Passe est incorrecte!",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
