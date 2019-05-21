package com.example.tiketsayaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SigninAct extends AppCompatActivity {

    TextView btn_new_account;
    Button btn_sign_in;
    EditText xusername, xpassword;

    DatabaseReference reference;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // load element
        btn_new_account = findViewById(R.id.btn_new_account);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        xusername = findViewById(R.id.xusername);
        xpassword = findViewById(R.id.xpassword);

        btn_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoregisterone = new Intent(SigninAct.this,RegisterOneAct.class);
                startActivity(gotoregisterone);

            }
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // mengubah state menjadi loading
                btn_sign_in.setEnabled(false);
                btn_sign_in.setText("Loading ...");

                final String username = xusername.getText().toString();
                final String password = xpassword.getText().toString();

                if(username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "username kosong!", Toast.LENGTH_SHORT).show();
                    // mengubah state menjadi sign in
                    btn_sign_in.setEnabled(true);
                    btn_sign_in.setText("SING IN");
                }
                else{
                    if(password.isEmpty()){
                        Toast.makeText(getApplicationContext(), "password kosong!", Toast.LENGTH_SHORT).show();
                        // mengubah state menjadi sign in
                        btn_sign_in.setEnabled(true);
                        btn_sign_in.setText("SING IN");
                    }
                    else {
                        reference = FirebaseDatabase.getInstance()
                                .getReference().child("Users").child(username);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    // ambil data password dari firebase
                                    String passwordFromFirebase = dataSnapshot.child("password").getValue().toString();

                                    //validasi password dengan password firebase
                                    if(password.equals(passwordFromFirebase)){

                                        //simpan username (key) kepada local
                                        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(username_key, xusername.getText().toString());
                                        editor.apply();

                                        // berpindah activity
                                        Intent gotohome = new Intent(SigninAct.this, HomeAct.class);
                                        startActivity(gotohome);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "password salah!", Toast.LENGTH_SHORT).show();
                                        // mengubah state menjadi sign in
                                        btn_sign_in.setEnabled(true);
                                        btn_sign_in.setText("SING IN");
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Username tidak ada!", Toast.LENGTH_SHORT).show();
                                    // mengubah state menjadi sign in
                                    btn_sign_in.setEnabled(true);
                                    btn_sign_in.setText("SING IN");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "Database Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
}
