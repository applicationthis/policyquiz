package quiz.policyquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText nameEditText, designationEditText, emailEditText, phoneEditText, locationEditText, passwordEditText;
    Button registerButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Bind UI elements
        nameEditText = findViewById(R.id.nameEditText);
        designationEditText = findViewById(R.id.designationEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        locationEditText = findViewById(R.id.locationEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        // Initialize FirebaseAuth and DatabaseReference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Set button click listener
        registerButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString().trim();
            String designation = designationEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate inputs and register user
            if (validateInputs(name, designation, email, phone, location, password)) {
                registerUser(name, designation, email, phone, location, password);
            }
        });
    }

    private boolean validateInputs(String name, String designation, String email, String phone, String location, String password) {
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return false;
        }

        if (designation.isEmpty()) {
            designationEditText.setError("Designation is required");
            designationEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email address");
            emailEditText.requestFocus();
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneEditText.setError("Invalid phone number");
            phoneEditText.requestFocus();
            return false;
        }

        if (location.isEmpty()) {
            locationEditText.setError("Office location is required");
            locationEditText.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser(String name, String designation, String email, String phone, String location, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save user data to Firebase Database
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        User user = new User(name, designation, email, phone, location);
                        databaseReference.child(userId).setValue(user)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        // Registration successful, navigate to LoginActivity
                                        Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish(); // Close MainActivity to prevent returning on back press
                                    } else {
                                        Log.e(TAG, "Failed to save data: ", dbTask.getException());
                                        Toast.makeText(MainActivity.this, "Failed to save data: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e(TAG, "Registration failed: ", task.getException());
                        Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
