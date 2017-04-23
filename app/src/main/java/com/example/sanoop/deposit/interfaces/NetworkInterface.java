package com.example.sanoop.deposit.interfaces;

import com.example.sanoop.deposit.Constant;
import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.example.sanoop.deposit.interfaces.NetworkInterface.pushDepositData;

/**
 * Created by sanoop on 4/16/2017.
 */

public interface NetworkInterface {

    String getAtmLocations = "banking/icicibank/BranchAtmLocator";
    String getBankSummary = "banking/icicibank/account_summary";
    String getClientToken = "corporate_banking/mybank/authenticate_client";
    String pushDepositData = "random.php";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.BRANCH_ATM_HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    Retrofit depositApi = new Retrofit.Builder()
            .baseUrl(Constant.DEPOSITURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    Retrofit authRetro = new Retrofit.Builder()
            .baseUrl(Constant.AUTHENTICATION_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET(getAtmLocations)
    Call<JsonElement> getAtmLocations(@Query("client_id") String client_id,
                                      @Query("token") String token,
                                      @Query("locate") String locate,
                                      @Query("lat") String lat,
                                      @Query("long") String lon);

    @GET(getBankSummary)
    Call<JsonElement> getBankSummary(@Query("client_id") String client_id,
                                      @Query("token") String token,
                                      @Query("custid") String custId,
                                      @Query("accountno") String accountNo);

    @GET(getClientToken)
    Call<JsonElement> getToken(@Query("client_id") String client_id, @Query("password") String password);

    @POST(pushDepositData)
    Call<JsonElement> pushDepositData(@Body RequestBody body);
}
