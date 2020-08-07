package com.group19.softwareengineeringproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.fragments.Terms_conditions_dialog;
import com.group19.softwareengineeringproject.helpers.RetrofitManager;
import com.group19.softwareengineeringproject.models.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText username;
    EditText password;
    EditText password2;
    Button register;
    String type;
    Spinner spinner;
    CheckBox termsCbox;
    TextView termsView;

    boolean read = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.rUsernameTxt);
        password = findViewById(R.id.rPasswordTxt);
        password2 = findViewById(R.id.rPasswordConfirmTxt);
        termsCbox = findViewById(R.id.rTermsCheckBox);
        termsView = findViewById(R.id.rTermsTxt);
        register = findViewById(R.id.rRegisterBtn);
        spinner = findViewById(R.id.rSpinnerChoices);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.accountTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        termsView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openTermsConditions();
                read = true;
            }
        });

        termsCbox.setOnClickListener(new View.OnClickListener(){
           boolean checked = false;
            @Override
            public void onClick(View v) {
                if (read) {
                    if (checked){
                        termsCbox.setChecked(false);
                        checked = false;
                    }
                    else{
                        termsCbox.setChecked(true);
                        checked = true;
                    }
                }
                else {
                    termsCbox.setChecked(false);
                    Toast.makeText(Register.this,"Please read the terms and conditions first!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int min = 1001;
                int max = 9999;

                Random r = new Random();
                int confirmation_code = r.nextInt(max - min) + min;

                //TODO: Hash password
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String pass2 = password2.getText().toString().trim();

                Pattern participant = Pattern.compile ("[a-z]{2}\\d{5}@surrey.ac.uk");
                Pattern organiser = Pattern.compile ("ussu.[a-z]{2,}@surrey.ac.uk");
                Pattern password = Pattern.compile("^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]{6,}$");
                Matcher p = participant.matcher (user);
                Matcher o = organiser.matcher(user);
                Matcher good_password = password.matcher(pass);

                boolean isParticipant = type.equals("Student");
                boolean isOrganiser = type.equals("Society");
                boolean isMatching = isParticipant && p.matches() || isOrganiser && o.matches();
                boolean isGoodPassword = good_password.matches();
                boolean areTermsAccepted = termsCbox.isChecked();




                if (isMatching & areTermsAccepted){
                    if(pass.equals(pass2) & isGoodPassword) {

                        String encryptedString = pass;
                        try {
                            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                            messageDigest.update(pass.getBytes());
                            encryptedString = new String(messageDigest.digest());
                        } catch (NoSuchAlgorithmException e) {
                            Toast.makeText(Register.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            return;
                        }

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("username", user);
                        map.put("password", encryptedString);
                        map.put("type",type.toLowerCase());
                        Call<LoginResult> loginAttempt = RetrofitManager.getInstance().api.register(map);
                        loginAttempt.enqueue(new Callback<LoginResult>() {
                            @Override
                            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                                if(!response.isSuccessful()) {
                                    Toast.makeText(Register.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    Log.d("omega", "response unsucc");
                                } else {
                                    if(response.body() == null) return;
                                    if(response.body().getResult().equals("register")) {
                                        //Get user info
                                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else if(response.body().getResult().equals("exists")) {
                                        Toast.makeText(Register.this, "Account already exists.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Register.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                        Log.d("omega", "response else");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResult> call, Throwable t) {
                                Toast.makeText(Register.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                Log.d("omega", "unsucccc");
                            }
                        });

                    }
                    else if(!isGoodPassword){
                        Toast.makeText(Register.this,"Passwords need to be at least 6 characters long and contain at least one letter and a number", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(Register.this,"Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (!areTermsAccepted) {
                    Toast.makeText(Register.this,"Terms and conditions must be read and agreed before registering", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Register.this,"Username has to be registered as student or society email", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        type = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void openTermsConditions() {
        Terms_conditions_dialog conditions = new Terms_conditions_dialog();
        conditions.show(getSupportFragmentManager(), "Terms and Conditions");
    }
}