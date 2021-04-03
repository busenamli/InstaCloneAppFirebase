package com.busenamli.navigationexample.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.busenamli.navigationexample.home.HomeActivity;
import com.busenamli.navigationexample.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText signupEmail, signupPassword;
    Button signupButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupButton = findViewById(R.id.signupButton);

        initFBAuthentication();
    }

    private void initFBAuthentication() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void signupButtonClicked(View view){

        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();

        if (email.matches("") || password.matches("")) {
            if (email.matches("")) {
                signupEmail.setError("E-mail boş bırakılamaz");
                return;
            } else {
                signupPassword.setError("Şifre boş bırakılamaz");
                return;
            }
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    writeFireStore(email);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void writeFireStore(String email) {

        HashMap<String, Object> postData = new HashMap<>();
        postData.put("user_id", firebaseAuth.getCurrentUser().getUid());
        postData.put("user_email", email);
        postData.put("user_name", "");
        postData.put("user_description", "");
        postData.put("user_city", "");
        postData.put("profile_photo_url", "");
        postData.put("signup_date", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SignUpActivity.this, "Kayıt başarılı", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}