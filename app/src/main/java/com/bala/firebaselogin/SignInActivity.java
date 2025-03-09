package com.bala.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private FirebaseAuth mAuth;

    private EditText emailTextInput;
    private EditText passwordTextInput;
    private Button signInButton;
    private Button forgotPasswordButton;
    private Button sendVerifyMailAgainButton;
    private TextView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize UI components
        emailTextInput = findViewById(R.id.signInEmailTextInput);
        passwordTextInput = findViewById(R.id.signInPasswordTextInput);
        signInButton = findViewById(R.id.signInButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        sendVerifyMailAgainButton = findViewById(R.id.sendVerifyMailAgainButton); // FIXED: Properly initialized
        errorView = findViewById(R.id.errorTextView); // FIXED: Properly initialized

        sendVerifyMailAgainButton.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTextInput.getText().toString().trim();
                String password = passwordTextInput.getText().toString().trim();

                if (email.isEmpty()) {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText("Email can't be empty");
                    return;
                }

                if (password.isEmpty()) {
                    errorView.setVisibility(View.VISIBLE);
                    errorView.setText("Password can't be empty");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        if (user.isEmailVerified()) {
                                            Intent homeActivity = new Intent(SignInActivity.this, MainActivity.class);
                                            startActivity(homeActivity);
                                            finish();
                                        } else {
                                            sendVerifyMailAgainButton.setVisibility(View.VISIBLE);
                                            errorView.setVisibility(View.VISIBLE);
                                            errorView.setText("Please verify your email and sign in again.");
                                        }
                                    }
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    if (task.getException() != null) {
                                        errorView.setVisibility(View.VISIBLE);
                                        errorView.setText(task.getException().getMessage());
                                    }
                                }
                            }
                        });
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPasswordActivity = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordActivity);
                finish();
            }
        });

        sendVerifyMailAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignInActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
