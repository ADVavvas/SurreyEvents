package com.group19.softwareengineeringproject.activities;

import java.security.MessageDigest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.helpers.RetrofitManager;
import com.group19.softwareengineeringproject.helpers.UserManager;
import com.group19.softwareengineeringproject.models.LoginResult;
import com.group19.softwareengineeringproject.models.User;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    EditText username;
    EditText password;
    Button login;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText)findViewById(R.id.usernameTxt);
        password = (EditText)findViewById(R.id.passwordTxt);
        login = (Button) findViewById(R.id.loginBtn);
        register = (Button)findViewById(R.id.registerBtn);


        register.setOnClickListener(new View.OnClickListener() {
            Intent registerIntent = new Intent(Login.this,Register.class);
            @Override
            public void onClick(View v) {
                startActivity(registerIntent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString().trim();
                String pwd = password.getText().toString().trim();

                String encryptedString = pwd;
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    messageDigest.update(pwd.getBytes());
                    encryptedString = new String(messageDigest.digest());
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(Login.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("username", user);
                map.put("password",encryptedString);

                Log.d("hashing", encryptedString);

                Call<LoginResult> loginAttempt = RetrofitManager.getInstance().api.login(map);
                loginAttempt.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        if(!response.isSuccessful()) {
                            Toast.makeText(Login.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        } else {
                            if(response.body().getResult().equals("login")) {
                                //Get user info


                                Call<User> userCall = RetrofitManager.getInstance().api.getUser(response.body().getId());
                                userCall.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        if(!response.isSuccessful()) {
                                            Toast.makeText(Login.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            UserManager.getInstance().setUser(response.body());

                                            Intent intent = new Intent(Login.this, Map.class);
                                            startActivity(intent);
                                        }


                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {

                                    }
                                });
                            } else if(response.body().getResult().equals("register")) {
                                Toast.makeText(Login.this, "Account not found.", Toast.LENGTH_LONG).show();
                            } else if(response.body().getResult().equals("wrong")) {
                                Toast.makeText(Login.this, "Wrong password", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Toast.makeText(Login.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

