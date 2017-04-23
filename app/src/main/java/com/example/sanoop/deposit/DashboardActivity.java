package com.example.sanoop.deposit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanoop.deposit.fragments.DepositFormFragment;
import com.example.sanoop.deposit.fragments.LocateAtmFragment;
import com.example.sanoop.deposit.interfaces.NetworkInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sanoop on 4/11/2017.
 */

public class DashboardActivity extends AppCompatActivity{

    TextView txtAccountType, txtAccountNo, txtAccountBalance, txtPhone, txtRewardPoints, depositCode;
    private Toolbar toolbar;
    public static DrawerLayout drawerLayout;
    private NetworkInterface networkCallService = NetworkInterface.retrofit.create(NetworkInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashboard_activity);
        txtAccountType = (TextView) findViewById(R.id.txtAccountType);
        txtAccountNo = (TextView) findViewById(R.id.txtAccountNo);
        txtAccountBalance = (TextView) findViewById(R.id.txtAccountBalance);
        depositCode= (TextView) findViewById(R.id.depositCode);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtRewardPoints = (TextView) findViewById(R.id.txtRewardPoints);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        initialize();
        if (!Constant.DEPOSITCODE.isEmpty()){
            depositCode.setVisibility(View.VISIBLE);
            depositCode.setText("Deposit Code : " + Constant.DEPOSITCODE);
        }else {
            depositCode.setVisibility(View.GONE);
        }
    }

    private void initialize() {
        Call<JsonElement> fetchBankDetails= networkCallService.getBankSummary(Constant.CLIENT_ID, Constant.TOKEN, Constant.CUSTOMER_ID, Constant.ACCOUNT_NUMBER);
        fetchBankDetails.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                try {
                    JSONArray bankSummaryArray = new JSONArray(response.body().toString());
                    for (int i = 1; i <bankSummaryArray.length();i++){
                        JSONObject bankSummary = bankSummaryArray.getJSONObject(i);
                        txtAccountType.setText(bankSummary.getString("accounttype"));
                        txtAccountNo.setText(bankSummary.getString("accountno"));
                        txtAccountBalance.setText(bankSummary.getString("balance"));
                        txtPhone.setText(bankSummary.getString("mobileno"));
                        txtRewardPoints.setText(bankSummary.getString("reward_point"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }

    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                String tag = null;
                switch (id){
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.locator:
                        fragment = new LocateAtmFragment();
                        tag = "map";
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.deposit:
                        fragment = new DepositFormFragment();
                        tag = "deposit";
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        finish();

                }
                if (fragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.container, fragment, tag);
                    transaction.commit();

                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText("Sandy");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
