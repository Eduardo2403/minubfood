package mx.com.minubfood.minubfood_clientes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEdit;
    private View mProgressView;
    private View splashScreen;
    private View loginScreen;
    private View signupScreen;
    private LinearLayout splashLogo;
    private TextView userRegister;
    private Button registerUser;
    private Button userLogin;

    private EditText userEmail;
    private EditText userPassword;
    private EditText userEmailReg;
    private EditText userPasswordReg;
    private EditText userPhoneReg;

    private Handler handlerSplashScreen;
    private Runnable runnableSplashScreen = new Runnable() {
        @Override
        public void run() {
            if (sharedPreferences.getBoolean("user_active", false)) {
                splashScreen.animate()
                        .alpha(0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                finish();
                                startActivity(new Intent(MainActivity.this, FoodActivity.class));
                            }
                        });

            } else {
                splashScreen.animate()
                        .alpha(0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                splashScreen.setVisibility(View.GONE);
                                loginScreen.setVisibility(View.VISIBLE);
                            }
                        });
            }

            handlerSplashScreen.removeCallbacks(this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("minubfood_customer", MODE_PRIVATE);
        sharedPreferencesEdit = sharedPreferences.edit();

        mProgressView = findViewById(R.id.login_progress);
        splashScreen = findViewById(R.id.splash_screen);
        splashLogo = findViewById(R.id.splash_logo);
        loginScreen = findViewById(R.id.login_screen);
        signupScreen = findViewById(R.id.signup_screen);
        userRegister = findViewById(R.id.user_register);
        registerUser = findViewById(R.id.register_user);
        userLogin = findViewById(R.id.user_login);

        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        userEmailReg = findViewById(R.id.user_email_r);
        userPasswordReg = findViewById(R.id.user_password_r);
        userPhoneReg = findViewById(R.id.user_phone_r);

        userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginScreen.setVisibility(View.GONE);
                signupScreen.setVisibility(View.VISIBLE);
            }
        });
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (registerNewUser(userEmailReg.getText().toString(), userPasswordReg.getText().toString(), userPhoneReg.getText().toString())) {
                            hideProgress();
                            userEmailReg.setText("");
                            userPasswordReg.setText("");
                            userPhoneReg.setText("");
                            finish();
                            startActivity(new Intent(MainActivity.this, FoodActivity.class));

                        } else {
                            hideProgress();
                            Toast.makeText(MainActivity.this, "Esta usuario ya existe", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000);
            }
        });
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (checkIfUserExist(userEmail.getText().toString(), userPassword.getText().toString())) {
                            hideProgress();
                            userEmail.setText("");
                            userPassword.setText("");
                            finish();
                            startActivity(new Intent(MainActivity.this, FoodActivity.class));
                        } else {
                            hideProgress();
                            Toast.makeText(MainActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000);
            }
        });
        splashLogo.animate()
                .alpha(1f)
                .setDuration(2000)
                .setListener(null);

        initProcess();
    }

    private boolean registerNewUser(String email, String password, String phone) {
        try {
            JSONArray users = new JSONArray(sharedPreferences.getString("USERS", "[]"));
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("email").equals(email)) {
                    return false;
                }
            }
            JSONObject user = new JSONObject();
            user.put("email", email);
            user.put("password", password);
            user.put("cellphone", phone);
            users.put(user);
            sharedPreferencesEdit.putString("USERS", users.toString());
            sharedPreferencesEdit.putBoolean("user_active", true);
            sharedPreferencesEdit.putString("user", user.toString());
            sharedPreferencesEdit.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkIfUserExist(String email, String password) {
        try {
            JSONArray users = new JSONArray(sharedPreferences.getString("USERS", "[]"));
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("email").equals(email) && user.getString("password").equals(password)) {
                    sharedPreferencesEdit.putBoolean("user_active", true);
                    sharedPreferencesEdit.putString("user", user.toString());
                    sharedPreferencesEdit.apply();
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void showProgress() {
        findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        findViewById(R.id.login_progress).setVisibility(View.GONE);
    }

    private void initProcess() {
        if (null == handlerSplashScreen) {
            handlerSplashScreen = new Handler();
            handlerSplashScreen.postDelayed(runnableSplashScreen, 5000);
        }
    }


}

