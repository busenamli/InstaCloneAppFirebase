package com.busenamli.navigationexample.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.busenamli.navigationexample.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText forgetPasswordEmail;
    private FirebaseAuth firebaseAuth;
    Button forgetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        forgetPasswordEmail = findViewById(R.id.forgetPasswordEmail);
        forgetPasswordButton = findViewById(R.id.forgetPasswordButton);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void forgetPasswordClicked(View view){

        String email = forgetPasswordEmail.getText().toString();

        if (email.matches("")) {
            forgetPasswordEmail.setError("Şifre boş bırakılamaz");
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ForgetPasswordActivity.this, "Sıfırlama e-postası gönderildi. Mail adresinizi kontrol edin!",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgetPasswordActivity.this, "Sıfırlama e-postası gönderilemedi! Lütfen e-mail adresinizi kontrol ediniz!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}