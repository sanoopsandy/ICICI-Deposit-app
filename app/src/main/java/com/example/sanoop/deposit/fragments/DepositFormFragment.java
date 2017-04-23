package com.example.sanoop.deposit.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sanoop.deposit.Constant;
import com.example.sanoop.deposit.DashboardActivity;
import com.example.sanoop.deposit.R;
import com.example.sanoop.deposit.interfaces.NetworkInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sanoop on 4/17/2017.
 */

public class DepositFormFragment extends Fragment implements FragmentManager.OnBackStackChangedListener{

    TextView txtTotalAmount, txtTwoThousandTotal, txtFiveHundredTotal, txtHundredTotal, txtFiftyTotal, txtTwentlyTotal, txtTenTotal, txtCoinsTotal;
    EditText edtTwoThousandQty, edtFiveHundredQty, edtHundredQty, edtFiftyQty, edtTwentyQty, edtTenQty, edtCoinsQty, edtAccountNo;
    double total = 0.0;
    Button btnSubmit;
    private NetworkInterface depositApi = NetworkInterface.depositApi.create(NetworkInterface.class);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.deposit_form_fragment, container, false);
        txtTotalAmount = (TextView) rootView.findViewById(R.id.txtTotalAmount);
        txtTwoThousandTotal = (TextView) rootView.findViewById(R.id.txtTTTotal);
        txtFiveHundredTotal = (TextView) rootView.findViewById(R.id.txtFHTotal);
        txtHundredTotal = (TextView) rootView.findViewById(R.id.txtHTotal);
        txtFiftyTotal = (TextView) rootView.findViewById(R.id.txtFTotal);
        txtTwentlyTotal = (TextView) rootView.findViewById(R.id.txtTWTotal);
        txtTenTotal = (TextView) rootView.findViewById(R.id.txtTETotal);
        txtCoinsTotal = (TextView) rootView.findViewById(R.id.txtCTotal);
        edtAccountNo = (EditText) rootView.findViewById(R.id.edtAccountNo);
        edtTwoThousandQty = (EditText) rootView.findViewById(R.id.edtTwoThousand);
        edtFiveHundredQty = (EditText) rootView.findViewById(R.id.edtFiveHundred);
        edtHundredQty = (EditText) rootView.findViewById(R.id.edtHundred);
        edtFiftyQty= (EditText) rootView.findViewById(R.id.edtFifty);
        edtTwentyQty= (EditText) rootView.findViewById(R.id.edtTwenty);
        edtTenQty= (EditText) rootView.findViewById(R.id.edtTen);
        edtCoinsQty= (EditText) rootView.findViewById(R.id.edtCoins);
        btnSubmit= (Button) rootView.findViewById(R.id.btnSubmit);
        edtTwoThousandQty.setText(String.valueOf(0));
        edtFiveHundredQty.setText(String.valueOf(0));
        edtHundredQty.setText(String.valueOf(0));
        edtFiftyQty.setText(String.valueOf(0));
        edtTwentyQty.setText(String.valueOf(0));
        edtTenQty.setText(String.valueOf(0));
        edtCoinsQty.setText(String.valueOf(0));
        edtTwoThousandQty.addTextChangedListener(getTextWatcher(txtTwoThousandTotal, 2000));
        edtFiveHundredQty.addTextChangedListener(getTextWatcher(txtFiveHundredTotal, 500));
        edtHundredQty.addTextChangedListener(getTextWatcher(txtHundredTotal, 100));
        edtFiftyQty.addTextChangedListener(getTextWatcher(txtFiftyTotal, 50));
        edtTwentyQty.addTextChangedListener(getTextWatcher(txtTwentlyTotal, 20));
        edtTenQty.addTextChangedListener(getTextWatcher(txtTenTotal, 10));
        edtCoinsQty.addTextChangedListener(getTextWatcher(txtCoinsTotal, 1));
        edtAccountNo.setText(Constant.ACCOUNT_NUMBER);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray depostData = new JSONArray();
                JSONObject depositForm = new JSONObject();
                try {
                    depositForm.put("accountNo", edtAccountNo.getText().toString());
                    depositForm.put("total", String.valueOf(total));
                    depositForm.put("2000", edtTwoThousandQty.getText().toString());
                    depositForm.put("500", edtFiveHundredQty.getText().toString());
                    depositForm.put("100", edtHundredQty.getText().toString());
                    depositForm.put("50", edtFiftyQty.getText().toString());
                    depositForm.put("20", edtTwentyQty.getText().toString());
                    depositForm.put("10", edtTenQty.getText().toString());
                    depositForm.put("coins", edtCoinsQty.getText().toString());
                    depostData.put(depositForm);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Random r = new Random();
                final int otp = r.nextInt(999999 - 100000) + 100000;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());
                alertDialogBuilder.setTitle(String.valueOf(otp));
                // set dialog message
                alertDialogBuilder
                        .setMessage("Click yes to save Deposit Code and No to delete Deposit Code")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                Constant.DEPOSITCODE = String.valueOf(otp);
                                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                Constant.DEPOSITCODE = "";
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                /*RequestBody body = new FormBody.Builder()
                .add("deposit",depostData.toString())
                .build();
                Call<JsonElement> generateDepositCode= depositApi.pushDepositData(body);
                generateDepositCode.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.code() == 200){
                            try {
                                final JSONObject depositCode = new JSONObject(response.body().toString());
                                final String depositOtp = depositCode.getString("otp");
                                if (!depositOtp.isEmpty()){
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            getActivity());
                                    alertDialogBuilder.setTitle("Deposit Code");
                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage("Click yes to save OTP and No to delete OTP")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    // if this button is clicked, close
                                                    // current activity
                                                    Random r = new Random();
                                                    int otp = r.nextInt(999999 - 100000) + 100000;
                                                    Constant.DEPOSITCODE = String.valueOf(otp);
                                                    Intent intent = new Intent(getActivity(), DashboardActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    // if this button is clicked, just close
                                                    // the dialog box and do nothing
                                                    Constant.DEPOSITCODE = "";
                                                    dialog.cancel();
                                                }
                                            });

                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // show it
                                    alertDialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i("DepositForm", "onResponse: " + response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        t.printStackTrace();
                    }
                });*/
            }
        });
        return rootView;
    }

    private TextWatcher getTextWatcher(final TextView textView, final int amount) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do what you want with your EditText
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int quantity;
                if (editable.toString().equals("")){
                  quantity = 0;
                }else {
                    quantity = Integer.valueOf(editable.toString());
                }
                double total = quantity * amount;
                textView.setText(String.valueOf(total));
                calculateTotal();
            }
        };
    }

    private void calculateTotal() {
        total = Double.parseDouble(txtTwoThousandTotal.getText().toString())
                + Double.parseDouble(txtFiveHundredTotal.getText().toString())
                + Double.parseDouble(txtHundredTotal.getText().toString())
                + Double.parseDouble(txtFiftyTotal.getText().toString())
                + Double.parseDouble(txtTwentlyTotal.getText().toString())
                + Double.parseDouble(txtTenTotal.getText().toString())
                + Double.parseDouble(txtCoinsTotal.getText().toString());
        txtTotalAmount.setText("Total Deposit Amount: \n" + String.valueOf(total));
    }

    @Override
    public void onBackStackChanged() {
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        startActivity(intent);
    }
}
