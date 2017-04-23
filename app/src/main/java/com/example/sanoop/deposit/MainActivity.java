package com.example.sanoop.deposit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.sanoop.deposit.interfaces.NetworkInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    Button btnLogin;
    EditText edtUsername, edtPassword;
    private NetworkInterface authService = NetworkInterface.authRetro.create(NetworkInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button) findViewById(R.id.btn_login);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<JsonElement> fetchTopProductsResponse= authService.getToken(Constant.CLIENT_ID, Constant.PARTICIPANT_CODE);
                fetchTopProductsResponse.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.code() == 200){
                            try {
                                JSONArray tokenArray = new JSONArray(response.body().toString());
                                JSONObject tokenInfo = (JSONObject) tokenArray.get(0);
                                Constant.TOKEN = tokenInfo.getString("token");
                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                intent.putExtra("username", edtUsername.getText().toString());
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                    }
                });
            }
        });
    }
}
