package com.vanguardghana.atomzpay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.vanguardghana.atomzpay.models.Agent;
import com.vanguardghana.atomzpay.utils.NetworkUtils;
import com.vanguardghana.atomzpay.utils.PreferenceManager;

import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    ProgressDialog progressDialog;
    EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        //hide editText focus
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }


    public void submitDetails(View view) {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        if (!validate(username, password)) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        postSignInData(username, password);


    }


    public boolean validate(String username, String password) {
        boolean valid = true;

        if (username.isEmpty()) {
            usernameEditText.setError("enter a valid ID");
            valid = false;
        } else {
            usernameEditText.setError(null);
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Enter password");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    public void showDialog(String setMessage, String setPositiveButton) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
        builder.setMessage(setMessage)
                .setCancelable(true)
                .setPositiveButton(setPositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });

        AlertDialog createDialog = builder.create();
        createDialog.show();
    }

    // method to send log-in details via ION
    private void postSignInData(String username, String password) {

        if (ConnectionDetector.isConnectingToInternet(this)) {

            Future<JsonObject> loginRequest = NetworkUtils.
                    getLoginRequest(this, username, password);
            loginRequest.setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try {

                        if (e != null) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            showDialog("Oops!! Something went wrong." +
                                    " Server cannot be reached \nTry Again", "Close");
                            System.out.println("-------------------------------------- error");
                        } else {

                            Log.i(TAG, "Login response: " + result.toString() + "");
                            boolean success = result.get("status").getAsString().equals("success");

                            if (success) {

                                JsonObject data = result.get("data").getAsJsonObject();

                                int id = data.get("id").getAsInt();
                                String collectorCode = data.get("collector_code").getAsString();
                                String name = data.get("name").getAsString();
                                String email = getString("email", data);
                                String phone_number = getString("phone_number", data);
                                String token = data.get("api_token").getAsString();


                                JsonObject assignedLocation = data.get("assigned_location")
                                        .getAsJsonObject();

                                int locationId = assignedLocation.get("id").getAsInt();
                                String locationName = assignedLocation.get("name").getAsString();
                                String locationCode = assignedLocation.get("location_code")
                                        .getAsString();

                                //save data to preference
                                PreferenceManager.putAgent(LoginActivity.this,
                                        new Agent(id, collectorCode, name, email, phone_number,
                                                locationId, locationCode, locationName));
                                PreferenceManager.putToken(LoginActivity.this, token);

                                progressDialog.dismiss();

                                Intent intent = new Intent(LoginActivity.this,
                                        PaymentCollectionActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Log.i(TAG, "Result was not successful!!");
                                showDialog("Unable to Sign in: \nServer response error",
                                        "Close");
                                progressDialog.dismiss();
                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();

                        showDialog("Unable to Sign in: \nServer response error",
                                "Close");
                        System.out.println("-------------@@@@@@@@@@@@@@ Server response error ");
                        progressDialog.dismiss();


                    }
                }
            });
        } else {

            showDialog("No internet connection. Check your connection and try again",
                    "Close");
            System.out.println("-----No internet connection." +
                    " Check your connection and try again @##### ");
            progressDialog.dismiss();

        }

    }

    private String getString(String name, JsonObject object) {
        if (object.get(name).isJsonNull())
            return null;
        else return object.get(name).getAsString();
    }

}
