package com.brh.pronapmobile.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.models.User;
import com.brh.pronapmobile.utils.HashGenerator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_REGISTER = 0;

    private AppCompatButton buttonLogin;
    private TextView tvForget;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilPasswordConf;
    private String hashedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = findViewById(R.id.btnLogin);
        tilEmail = findViewById(R.id.email);
        tilPassword = findViewById(R.id.password);
        tilPasswordConf = findViewById(R.id.passwordConfirmation);
        tvForget = findViewById(R.id.tvForgetPassword);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // only 1 User record will be in database (user_id = 1)
        // If User is already in database, goto MainActivity
        if(User.isLoggedIn()) {
            // Login directly
            onLoginSuccess();
        }

        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Un Code de réinitialisation a été envoyé sur votre email",
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    public void login() {
        Log.d(TAG, "Login");

        // TODO : Validate inputs
        if (!validate()) {
            onLoginFailed();
            return;
        }

        buttonLogin.setEnabled(false);

        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        String hashedPassword = HashGenerator.sha1(password);
        if(hashedPassword != null) {
            User user = new User();
            user.setId(1);
            user.setEmail(email);
            user.setPassword(hashedPassword);
            user.setStatus(1);
            user.save();

            onLoginSuccess();
        } else {
            Toast.makeText(LoginActivity.this, "Impossible de se connecter, Problème de sécurité",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void onLoginSuccess() {
        buttonLogin.setEnabled(true);
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();

        buttonLogin.setEnabled(true);
    }

    public void register() {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(i, REQUEST_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_REGISTER) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                onLoginSuccess();
                this.finish();
            }
        }
    }

    public boolean validate() {
        if(tilEmail.getEditText().getText().toString().equals("") || tilPassword.getEditText().getText().toString().equals("")
                || tilPasswordConf.getEditText().getText().toString().equals("")) {
            Toast.makeText(this, "Vous devez rentrer un Email et un Mot de Passe",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(!tilPassword.getEditText().getText().toString().equals(tilPasswordConf.getEditText().getText().toString())) {
            Toast.makeText(this, "Les 2 mots de passe entrés sont différents!",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
