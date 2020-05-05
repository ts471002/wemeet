package com.example.wemeet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wemeet.pojo.user.User;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.NetworkUtil;
import com.example.wemeet.util.ReturnVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String LOGGED_IN = "loggedIn";
    public static final String USER_EMAIL = "userEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 监听输入框
        EditText emailInput = findViewById(R.id.input_email);
        EditText passwordInput = findViewById(R.id.input_password);

        EditTextChange editTextChange = new EditTextChange();
        emailInput.addTextChangedListener(editTextChange);
        passwordInput.addTextChangedListener(editTextChange);
    }

    // 响应loginButton
    public void login(View view) {
        EditText emailInput = findViewById(R.id.input_email);
        EditText passwordInput = findViewById(R.id.input_password);
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        User tempUser = new User();
        tempUser.setEmail(email).setPassword(password);

        UserInterface userInterface = NetworkUtil.getRetrofit().create(UserInterface.class);
        userInterface.login(tempUser).enqueue(new Callback<ReturnVO>() {
            @Override
            public void onResponse(Call<ReturnVO> call, Response<ReturnVO> response) {
                ReturnVO result = response.body();
                assert result != null;
                Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                if (result.getCode() == 200) {
                    // 保存登录状态信息
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(LOGGED_IN, true);
                    editor.putString(USER_EMAIL, email);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    // 为了防止用户使用后退按钮返回到登录活动，您必须在启动一个新活动之后finish()该活动
                    LoginActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<ReturnVO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // EditText监听器
    class EditTextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditText emailInput = findViewById(R.id.input_email);
            EditText passwordInput = findViewById(R.id.input_password);
            Button loginButton = findViewById(R.id.button_login);
            boolean b = emailInput.getText().length() > 0;
            boolean b1 = passwordInput.getText().length() > 0;
            if (b && b1) {
                loginButton.setEnabled(true);
            } else {
                loginButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }
}
