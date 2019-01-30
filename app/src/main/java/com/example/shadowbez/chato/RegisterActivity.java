package com.example.shadowbez.chato;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    public static String TAG = RegisterActivity.class.getName();

    private ProgressBar progressBar;
    private EditText email;
    private EditText password;
    private EditText passwordRepeat;
    private LinearLayout linearLayout;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
//            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        initGUI();
    }


    public void registerUserOnClick(View view) {
        if (validPassword()) {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(RegisterActivity.this, email.getText().toString(),
                                        Toast.LENGTH_LONG).show();
                                //ADD TO DATABASE
                                FirebaseUser user = mAuth.getCurrentUser();
                                addUserToDatabase(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Auth not successful.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void initGUI() {
        progressBar = findViewById(R.id.register_progress);
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.password_register);
        passwordRepeat = findViewById(R.id.password_register_repeat);
        linearLayout = findViewById(R.id.email_register_form);
    }

    private boolean validPassword() {
        return !password.getText().toString().isEmpty() && password.getText().toString().equals(passwordRepeat.getText().toString());
    }

    private void addUserToDatabase(FirebaseUser user) {
//        String id = user.getUid();
//        String email = user.getEmail();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference newUserChild = databaseReference.child(DatabaseConstants.USERS).child(id);
//        //TODO make better if
//        if (newUserChild != null) {
//            newUserChild.setValue(new User(id, email));
//            //DatabaseReference newChatRoomIdChild = newUserChild.child(DatabaseConstants.USER_CHAT_ROOM_IDS).push();
//            //newChatRoomIdChild.setValue(1);
//            return true;
//        } else {
//            return false;
//        }

        String id = user.getUid();
        String email = user.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DatabaseConstants.USERS).document(id)
                .set(new User(id, email)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("wololo", "SUCCESS - user registered to database");
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("wololo", "ERROR - user cant be registered to database");
            }
        });
    }
}