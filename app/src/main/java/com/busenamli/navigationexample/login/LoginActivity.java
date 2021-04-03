package com.busenamli.navigationexample.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.busenamli.navigationexample.home.HomeActivity;
import com.busenamli.navigationexample.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText email, password;
    TextView forgetPasswordText;
    Button loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        forgetPasswordText = findViewById(R.id.forgetPasswordText);

        firebaseAuth = FirebaseAuth.getInstance(); //Initilaze edildi.

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); //Aktif kullanıcı var mı?

        if(firebaseUser != null){

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        forgetPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    public void loginClicked(View view){

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(userEmail.matches("") || userPassword.matches("")){
            if(userEmail.matches("")) {
                email.setError("E-mail boş bırakılamaz");
                return;
            }else{
                password.setError("Şifre boş bırakılamaz");
                return;
            }
        }else{
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String message;
                            if (task.isSuccessful()) {
                                message = "Giriş başarılı";

                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                message = "Giriş başarısız";
                            }
                            Toast.makeText(LoginActivity.this, message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void signupClicked(View view){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}