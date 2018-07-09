package com.vanguardghana.atomzpay;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.vanguardghana.atomzpay.models.Agent;
import com.vanguardghana.atomzpay.models.Vehicle;
import com.vanguardghana.atomzpay.utils.NetworkUtils;
import com.vanguardghana.atomzpay.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

public class PaymentCollectionActivity extends AppCompatActivity {

    private static final int NO_NETWORK = 400;
    private static final int SERVER_ERROR = 404;
    private static final int NO_DATA_IN_DB = 104;
    private static final int SYNC_REST_OF_DATA = 102;


    // Content provider authority
    public static final String AUTHORITY = "com.vanguardghana.atomzpay.provider";
    // Account type
    public static final String ACCOUNT_TYPE = "com.vanguardghana.atomzpay";
    // Account
    public static final String ACCOUNT = "com.vanguardghana.atomzpay";
    private static final String TAG = PaymentCollectionActivity.class.getSimpleName();
    // Instance fields
    Account mAccount;

    //Declaring a new array list for the vehicle strings
    ArrayList<String> Vehicles = new ArrayList<String>();


//    /** URL to query the vehicle types data set for vehicle information */
//    private static final String VEHICLE_REQUEST_URL = "http://192.168.3.41/rsms_tolls/api/login";

    ProgressDialog progressDialog;
    AlertDialog createDialog;
    Timer timer;
    PrintReceipt printReceipt = new PrintReceipt(PaymentCollectionActivity.this);

    SyncDataTask syncDataTask;
    int countDataSent;
    SyncHandler syncHandler = new SyncHandler();
    Message theMessage;

    TextView displayAgentIDTextView, displayAgentNameTextView, displayLocationTextView,
            chooseTypeOfGoods, normalAmountTextView, displayNormalAmt2PayTextView, choosePaymentMethodLabelTextView, chooseNetworkLabel;

    TextInputLayout bulkGoodsTextInputLayout, paymentPhoneNumTextInputLayout, vodaVoucherTextInputLayout;

    EditText bulkGoodsAmountEditText, vodafoneVoucherEditText, paymentPhoneNumEditText;

    Spinner typeOfGoodsSpinner, paymentMethodSpinner, networkTypeSpinner;

    String normalAmount2Pay, amtToPay, paymentMethod, typeOfGoods,
            mobileMoneyNetwork, paymentPhoneNumber, bulkGoodsAmountCharged, vodafoneVoucher;
    private Agent agent;
    private List<Vehicle> vehicleList = new ArrayList<>();
    private ArrayAdapter<String> vehicleAdapter;

    String makePaymentRequestUrl = "http://197.159.134.178:5010/vtpay/api/payments/debit";
    String paymentCollectedDataUrl = "http://41.204.42.117:8080/atomz/api/payment";


//    final String customerID = "20160600010978";

    String receiptInfo;

    String result;

//    Get JSON object from URL


    private DatabaseHelper databaseHelper;
    private List<TollPaymentFactory> paymentList;
    private TollPaymentFactory tollPaymentFactory;

    private boolean syncStatus = false;

    Response<JsonObject> response;
    int resCode;
    String resMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_collection);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        databaseHelper = new DatabaseHelper(this);

//        mAccount = CreateSyncAccount(this);


        // getting default values
        agent = PreferenceManager.getAgent(this);
//        normalAmount2Pay= getLogInData("normalAmount2Pay",this);


        //TextViews
        displayAgentIDTextView = findViewById(R.id.displayAgentID);
        displayAgentNameTextView = findViewById(R.id.displayAgentName);
        displayLocationTextView = findViewById(R.id.displayLocation);
//        displayTollTypeTextView= findViewById(R.id.displayTollType);
        chooseTypeOfGoods = findViewById(R.id.chooseTypeOfGoods);
        normalAmountTextView = findViewById(R.id.normalAmountLabel);
        displayNormalAmt2PayTextView = findViewById(R.id.amountToPay_Normal);
        choosePaymentMethodLabelTextView = findViewById(R.id.choosePaymentMethodLabel);
        chooseNetworkLabel = findViewById(R.id.chooseNetworkLabel);

        //Set TextViews
        displayAgentIDTextView.setText(agent.getCollectorCode());
        displayAgentNameTextView.setText(agent.getName());
        displayLocationTextView.setText(agent.getLocationName());
//        displayTollTypeTextView.setText(tollType);


        //TextInputLayout
//        bulkGoodsTextInputLayout= findViewById(R.id.bulkGoodsTextInputLayout);
        paymentPhoneNumTextInputLayout = findViewById(R.id.paymentPhoneNumTextInputLayout);
        vodaVoucherTextInputLayout = findViewById(R.id.vodaVoucherTextInputLayout);

        //EditTexts
//        bulkGoodsAmountEditText = findViewById(R.id.amountToPay_Bulk);
        vodafoneVoucherEditText = findViewById(R.id.vodafoneVoucher);
        paymentPhoneNumEditText = findViewById(R.id.paymentPhoneNum);

        //Spinners
        typeOfGoodsSpinner = findViewById(R.id.typeOfGoodsSpinner);
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);
        networkTypeSpinner = findViewById(R.id.paymentNetworkSpinner);

//        if(tollType.equals("MARKET")) {
//            chooseTypeOfGoods.setVisibility(View.VISIBLE);
//            typeOfGoodsSpinner.setVisibility(View.VISIBLE);
//
//        }else {
//            chooseTypeOfGoods.setVisibility(View.GONE);
//            typeOfGoodsSpinner.setVisibility(View.GONE);
//            typeOfGoods = "Normal";
//
//        }

//        Apply the adapters to the spinner
        typeOfGoodsSpinner.setAdapter(setSpinnerListItems(R.array.typeOfGoodsSpinner_array));
        paymentMethodSpinner.setAdapter(setSpinnerListItems(R.array.paymentMethodSpinner_array));
        networkTypeSpinner.setAdapter(setSpinnerListItems(R.array.paymentNetworkSpinner_array));

        loadInfo();

        typeOfGoodsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    displayNormalAmt2PayTextView.setText(vehicleList.get(position).getAmount());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:

                        paymentMethod = "cash";
                        chooseNetworkLabel.setVisibility(View.GONE);
                        networkTypeSpinner.setVisibility(View.GONE);
                        paymentPhoneNumTextInputLayout.setVisibility(View.GONE);
                        paymentPhoneNumEditText.getText().clear();
                        networkTypeSpinner.setSelection(0);
                        break;

                    case 1:

                        paymentMethod = "mobile money";
                        chooseNetworkLabel.setVisibility(View.VISIBLE);
                        networkTypeSpinner.setVisibility(View.VISIBLE);
                        paymentPhoneNumTextInputLayout.setVisibility(View.VISIBLE);

                }

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        networkTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        mobileMoneyNetwork = null;
                        vodaVoucherTextInputLayout.setVisibility(View.GONE);
                        vodafoneVoucherEditText.getText().clear();
                        break;

                    case 1:

                        mobileMoneyNetwork = "MTN";
                        vodaVoucherTextInputLayout.setVisibility(View.GONE);
                        vodafoneVoucherEditText.getText().clear();
                        chooseNetworkLabel.setError(null);
                        break;

                    case 2:

                        mobileMoneyNetwork = "VODAFONE";
                        vodaVoucherTextInputLayout.setVisibility(View.VISIBLE);
                        chooseNetworkLabel.setError(null);
                        break;

                    case 3:

                        mobileMoneyNetwork = "TIGO";
                        vodaVoucherTextInputLayout.setVisibility(View.GONE);
                        vodafoneVoucherEditText.getText().clear();
                        chooseNetworkLabel.setError(null);
                        break;

                    case 4:

                        mobileMoneyNetwork = "AIRTEL";
                        vodaVoucherTextInputLayout.setVisibility(View.GONE);
                        vodafoneVoucherEditText.getText().clear();
                        chooseNetworkLabel.setError(null);
                }

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Progress Bar
        progressDialog = new ProgressDialog(PaymentCollectionActivity.this, R.style.Theme_AppCompat_Dialog);


        //end of onCreate.
    }

    private void loadInfo() {

        final String accessToken = PreferenceManager.getToken(this);
        Toast.makeText(this, "" + accessToken, Toast.LENGTH_SHORT).show();

        if (ConnectionDetector.isConnectingToInternet(this)) {

            final Future<JsonObject> vehicleRequest = NetworkUtils.getVehicleRequest(this, accessToken);
            vehicleRequest.setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    try {
                        if (e != null) {
                            e.printStackTrace();
                            showDialog("Ooops!! Something went wrong.  \nThere was an error connecting to the Server." +
                                    "\nUnable to load the regions", "Close");
                            System.out.println("---------------------------------------------------------------- error");
                        } else {

                            Log.i(TAG, "Login response: " + result.toString() + "");
                            //Extract response Status code
                            boolean success = result.get("status").getAsString().equals("success");

                            if (success) {

                                JsonArray jsonArray = result.get("data").getAsJsonArray();

                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JsonObject item = jsonArray.get(i).getAsJsonObject();

                                    int id = item.get("id").getAsInt();
                                    String name = item.get("name").getAsString();
                                    String type = item.get("vehicle_type_code").getAsString();
                                    String amount = item.get("amount").getAsString();
                                    Vehicle vehicle = new Vehicle(id, name, type, amount);
                                    vehicleList.add(vehicle);
                                    Vehicles.add(name);
                                }

                                Log.i("sfdjafj", "list size " + vehicleList.size());
                                if (vehicleList.size() > 0) {
                                    setAdapters();
                                    vehicleAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("--------------------------------@@@@@@@@@@@@@@@@@@ Server response error ");

                    }

                }
            });

        } else {

            showDialog("No internet connection. Check your connection and try again", "Close");
            System.out.println("-----No internet connection. Check your connection and try again @##### ");

        }


    }

    private void setAdapters() {
        vehicleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView title = view.findViewById(android.R.id.text1);
                title.setText(vehicleList.get(position).getName());
                return view;
            }

            @Override
            public int getCount() {
                return vehicleList.size();
            }

            @Nullable
            @Override
            public String getItem(int position) {
                if (vehicleList.size() == 0)
                    return "";
                return vehicleList.get(position).getName();
            }
        };
        typeOfGoodsSpinner.setAdapter(vehicleAdapter);
    }


    // this inflates menu items.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_payment_collector, menu);

        return true;
    }

    // this handles onclick of menu items.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.sync: // on click to sync data
                syncDataTask = new SyncDataTask();
                syncDataTask.execute();

                return true;

            case R.id.logout: // on click on log out.
                PreferenceManager.clearPreferences(this);
                startLogInActivity();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // This method starts log in page when logout is clicked.
    public void startLogInActivity() {
        Intent showLoginActivity = new Intent(this, LoginActivity.class);
        startActivity(showLoginActivity);
    }


   /* public static String getLogInData(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "Dude, There's Nothing");
    }*/

    //    this method is used to initialise an adapter for the various spinners.
    ArrayAdapter setSpinnerListItems(int arrayListFromResources) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayListFromResources, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void showDialog(int stringResId, String setPositiveButton) {
        String message = getString(stringResId);
        showDialog(message, setPositiveButton);
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

        createDialog = builder.create();
        createDialog.show();
    }


    public void makePayment(View view) {


        paymentPhoneNumber = paymentPhoneNumEditText.getText().toString().trim();
        bulkGoodsAmountCharged = bulkGoodsAmountEditText.getText().toString().trim();
        vodafoneVoucher = vodafoneVoucherEditText.getText().toString().trim();

        System.out.println("clicked");

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Payment failed", Toast.LENGTH_SHORT).show();
            return;
        }


        AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
        builder1.setMessage("Please Confirm Payment")
                .setCancelable(true)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        System.out.println("@@@@@@@@@@@@@@@$$$$$$$$$$!!!!!!!!" + printReceipt.checkPaper());
                        if (!printReceipt.checkPaper()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PaymentCollectionActivity.this, R.style.Theme_AppCompat_Dialog);
                            builder.setMessage("There's no paper in the device. Payment Will Not Be Processed")
                                    .setCancelable(true)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User clicked OK button

                                        }

                                    });

                            AlertDialog createDialog = builder.create();
                            createDialog.show();
                        } else {

                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Proccessing Payment...");
                            progressDialog.show();

                            if (paymentMethod.equals("cash")) {
                                addDataToDB();
                                setInfoAndPrintReceipt();
                                progressDialog.dismiss();

                            } else if (paymentMethod.equals("mobile money")) {

                                makePaymentRequest(paymentPhoneNumber, mobileMoneyNetwork, amtToPay, vodafoneVoucher);

                            }
                        }

                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog createDialog1 = builder1.create();
        createDialog1.show();


//        int howMuchInfo = databaseHelper.getPaymentsCount();
////
//        databaseHelper.deleteAllPayment();
////
//        System.out.println("These are the quantity of info in db ----> " + howMuchInfo);

//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
//            builder.setMessage("Do You Want to Confirm Payment? ")
//                    .setCancelable(true)
//                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // User clicked OK button
//                            progressDialog = new ProgressDialog(PaymentCollectionActivity.this, R.style.Theme_AppCompat_Dialog);
//                            progressDialog.setIndeterminate(true);
//                            progressDialog.setMessage("Proccessing Payment...");
//                            progressDialog.show();
//
//                            if(paymentMethod.equals("cash")){
//                                paymentCollectedData(agentID, locationID, tollTypeID, paymentMethod, paymentPhoneNumber,typeOfGoods,
//                                        amtToPay, customerPhoneNum);
//
//                            }else if(paymentMethod.equals("mobile money")) {
//
//                                makePaymentRequest( paymentPhoneNumber, mobileMoneyNetwork,amtToPay,vodafoneVoucher);
//
//                            }
//                        }
//
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });


////        databaseHelper.insertPaymentAppData(dePaymentData);
//
//        System.out.println("i am the id of just inserted data=====>"+databaseHelper.getPaymentAppData(id).getId());
//        System.out.println("i am the json of just inserted data=====>"+databaseHelper.getPaymentAppData(id).getJsonData());
//        System.out.println("i am the timestamp of just inserted data=====>"+databaseHelper.getPaymentAppData(id).getTimestamp());


//        TollPaymentFactory tollPaymentFactory = databaseHelper.getPaymentAppData(id);

//        paymentList = databaseHelper.getAllPayments();
//
//        for(int j=0;j<paymentList.size();j++) {
//            System.out.println(paymentList);
//
//            int id = paymentList.get(j).getId();
//            System.out.println(id + "#########################");
//
//            System.out.println("i am the id of just inserted data=====>" + databaseHelper.getPaymentAppData(id).getId());
//            System.out.println("i am the json of just inserted data=====>" + databaseHelper.getPaymentAppData(id).getJsonData());
//            System.out.println("i am the timestamp of just inserted data=====>" + databaseHelper.getPaymentAppData(id).getTimestamp());
//        }


    }

    public boolean validate() {
        boolean valid = true;


        if (typeOfGoods.equals("Normal")) {
            amtToPay = normalAmount2Pay;

        } else if (typeOfGoods.equals("Bulk")) {
            amtToPay = bulkGoodsAmountCharged;
            if (Double.parseDouble(amtToPay) < Double.parseDouble(normalAmount2Pay)) {
                bulkGoodsAmountEditText.setError("Amount Cannot be less than " + normalAmount2Pay);
                valid = false;
            }

        } else {
            amtToPay = normalAmount2Pay;
        }


        return valid;
    }

    public void addDataToDB() {

        JsonObject paymentData = new JsonObject();
        paymentData.addProperty("collector", agent.getCollectorCode());
        paymentData.addProperty("location", agent.getLocationName());
        paymentData.addProperty("toll_type", "");
        paymentData.addProperty("goods_type", typeOfGoods);
        paymentData.addProperty("payment_mode", paymentMethod);
        paymentData.addProperty("amount", amtToPay);
        paymentData.addProperty("mobile_money_number", paymentPhoneNumber);

        String dePaymentData = String.valueOf(paymentData);
        System.out.println(dePaymentData);


        databaseHelper.insertPaymentAppData(dePaymentData);

    }

    public void setInfoAndPrintReceipt() {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        //Receipt info.
        receiptInfo = "\n---------------------------\n" +
                "     ATOMZ PAY TOLL RECEIPT.     \n" +
                "AGENT CODE: " + agent.getCollectorCode() + " \n" +
                "LOCATION: " + agent.getLocationName() + " \n" +
                "TOLL TYPE: " + "" + " \n" +
                "AMOUNT CHARGED: \u20B5" + amtToPay + " \n" +
                "PAYMENT MODE: " + paymentMethod + " \n" +
                "DATE: " + formattedDate + " \n" +
                "Please Retain As Receipt\n \n" +
                "---------------------------\n" +
                "             Copyright \u00a9 \n" +
                "     Vanguard Technology Ltd.";


        printReceipt.checkAndPrint(receiptInfo);
    }


//    private void paymentCollectedData(JsonObject json) {
//
//
//
//        ConnectionDetector cd = new ConnectionDetector();
//        if (cd.isConnectingToInternet(this)) {
//
//            final String apiToken= getLogInData("apiToken", this);
//
//            Ion.with(this)
//                    .load("POST", paymentCollectedDataUrl )
//                    .setHeader("Accept", "application/json")
//                    .setHeader("Authorization",  "Bearer " + apiToken)
//                    .setJsonObjectBody(json)
//                    .asJsonObject()
//                    .withResponse()
//                    .setCallback(new FutureCallback<Response<JSONObject>>() {
//                        @Override
//                        public void onCompleted(Exception e, Response<JSONObject> response) {
//
//
//                            try {
//
//                                if (e != null) {
//                                    e.printStackTrace();
//                                    progressDialog.dismiss();
////                                    showDialog("Oops!! Something went wrong \nTry Again", "Close");
//
//                                    System.out.println("---------------------------------------------------------------- error");
//                                }else {
//
//                                    //Extract response Status code
//                                    int responseCode = response.getHeaders().code();
//
//                                    //Extract response Results
//                                    String result = String.valueOf(response.getResult());
//
//                                    if (!result.contentEquals("null")) {
//                                        System.out.println("------------------------------------------------------@@@@@@@@@@@@@@@@@@ Data is " + result);
//
//                                        JSONObject resultJson = new JSONObject(result);
//
//                                        String status = resultJson.getString("status"));
//
//                                        if (status.equals("success") & responseCode == 200) {
//
//                                            progressDialog.dismiss();
//                                            showDialog("Payment Data Submitted Successfully.", "Close");
//                                            System.out.println("--------------------------@@@@@@@@@@- payment data submission Success");
//
//
////                                            printReceipt.checkAndPrint();
//
//                                            typeOfGoodsSpinner.setSelection(0);
//                                            paymentMethodSpinner.setSelection(0);
//
//
//                                        }
//
//                                        if (responseCode == 422) {
//                                            showDialog("Unable to Proccess Payment: \n Check form Fields", "Close");
//                                            System.out.println("--------------------------@@@@@@@@@@- Check form data");
//
//                                        }
//
//
//                                    }
////
//                                }
//
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//
//                                int responseCode = response.getHeaders().code();
//
//                                showDialog("Unable to Proccess Payment Now: \nServer response error", "Close");
//                                System.out.println("--------------------------------@@@@@@@@@@@@@@@@@@ Server response error @@" );
//                                System.out.println(responseCode);
//
//                                progressDialog.dismiss();
//                            }
//                        }
//                    }) ;
//
//
//        } else {
//
//            showDialog("No internet connection. Check your connection and try again", "Close");
//
//        }
//
//
//    }

    private Boolean sendCollectedPaymentData() {

        syncStatus = false;
        final String apiToken = PreferenceManager.getToken(this);


        ConnectionDetector cd = new ConnectionDetector();
        if (cd.isConnectingToInternet(this)) {

            paymentList = databaseHelper.getAllPayments();


            if (paymentList.size() != 0) {

                System.out.println(paymentList);

                while (!paymentList.isEmpty()) {
                    int i = 0;

                    String jsonData = paymentList.get(i).getJsonData(); //raw json data to send
                    TollPaymentFactory tollPaymentFactory = paymentList.get(i); //tollPaymentFactory Object

                    JsonObject sendJson = new JsonParser().parse(jsonData).getAsJsonObject(); //ready jsonData to send.

                    System.out.println("INdex --------------========  " + i);

                    try {
                        response = Ion.with(this)
                                .load("POST", paymentCollectedDataUrl)
                                .setHeader("Accept", "application/json")
                                .setHeader("Authorization", "Bearer " + apiToken)
                                .setJsonObjectBody(sendJson)
                                .asJsonObject()
                                .withResponse()
                                .get();
                        resCode = response.getHeaders().code();
                        resMsg = String.valueOf(response.getResult());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        if (e != null) {
                            e.printStackTrace();
                            paymentList.clear();
                            syncHandler.sendMessage(syncHandler.obtainMessage(SERVER_ERROR));
//                            showDialog("Server Connection Interrupted. Kindly Resync To Ensure All Data Is Synched To Server.Thanks.","Close");
                            resCode = 401;
                            System.out.println("---------------------------------------------------------------- error trying");
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        if (e != null) {
                            e.printStackTrace();
                            paymentList.clear();
                            syncHandler.sendMessage(syncHandler.obtainMessage(SERVER_ERROR));
//                            showDialog("Server not reachable. Kindly Resync To Ensure All Data Is Synched To Server.Thanks.","Close");
                            resCode = 401;
                            System.out.println("---------------------------------------------------------------- error trying " + resCode);
                        }
                    }

                    if (resCode == 200) {

                        databaseHelper.deletePayment(tollPaymentFactory);
                        System.out.println("INdex to delete --------------%%%%%%%%%  " + i);
                        paymentList.remove(i);
                        syncStatus = true;

                        countDataSent++;


                    } else {
//                        showDialog("Server not reachable.Kindly Resync To Ensure All Data Is Synched To Server.Thanks.","Close");
                        paymentList.clear();
                        syncHandler.sendMessage(syncHandler.obtainMessage(SERVER_ERROR));

                        System.out.println("---------------------------------------------------------------- errorIN response  " + resCode);
                        System.out.println("---------------------------------------------------------------- errorIN response  " + resMsg);
                    }

                    if (countDataSent == 50) {
                        try {
                            System.out.println("Thread is Sleeping.----------------");
                            Thread.sleep(30000);

                            countDataSent = 0;

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            paymentList.clear();
                            syncHandler.sendMessage(syncHandler.obtainMessage(SERVER_ERROR));
                        }
                    }

                }


            } else {
                syncHandler.sendMessage(syncHandler.obtainMessage(NO_DATA_IN_DB));

                System.out.println("!!!!!!!!!!!!!!!!!!!--------->>>>>>>>>>DB is Emptyyyyy");
                return syncStatus;

            }


        } else {

            syncHandler.sendMessage(syncHandler.obtainMessage(NO_NETWORK));

            System.out.println("--------------------------------@@@@@@@@@@@@@@@@@@ No internet connection. ");

        }

        return syncStatus;
    }

    private void makePaymentRequest(String dePaymentPhoneNumber, String mobileMoneyNetwork, String amountToPay,
                                    String vodafoneVoucher) {

        ConnectionDetector cd = new ConnectionDetector();
        if (cd.isConnectingToInternet(this)) {
//            leadership-conf/public/api/attendant

            Ion.with(this)
                    .load("POST", makePaymentRequestUrl)
                    .setHeader("Accept", "application/json")
                    .setHeader("Authorization", "Bearer ajTc6q5IB8niY09aklW2yPDuxFpYuJ")
                    .setBodyParameter("category", "the category")
                    .setBodyParameter("revenueItem", "the item")
                    .setBodyParameter("customerId", dePaymentPhoneNumber)
                    .setBodyParameter("amountToPay", amountToPay)
                    .setBodyParameter("mobileNo", dePaymentPhoneNumber)
                    .setBodyParameter("mno", mobileMoneyNetwork)
                    .setBodyParameter("source", "ATOMZ")
                    .setBodyParameter("voucherCode", vodafoneVoucher)
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> response) {


                            try {

                                if (e != null) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                    showDialog("Oops!! Something went wrong \nTry Again", "Close");

                                    System.out.println("---------------------------------------------------------------- error");
                                } else {

                                    //Extract response Status code
                                    int responseCode = response.getHeaders().code();

                                    //Extract response Results
                                    String result = response.getResult();

                                    if (!result.contentEquals("null")) {
                                        System.out.println("------------------------------------------------------@@@@@@@@@@@@@@@@@@ Data is " + result);

                                        JSONObject json = new JSONObject(result);
                                        String msg = json.getString("msg");
//
                                        if (msg.equals("Transaction in progress") & responseCode == 200) {

                                            progressDialog.dismiss();
                                            showDialog("Transaction in Progress...\nAlert User to Confirm Payment ", "Close");
                                            System.out.println("--------------------------@@@@@@@@@@- payment transaction Success");

                                            addDataToDB();
                                            setInfoAndPrintReceipt();


                                        }


                                        if (!msg.equals("Transaction in progress") & responseCode != 200) {
                                            showDialog("Unable to Proccess Payment: \nServer response error", "Close");
                                            System.out.println("--------------------------@@@@@@@@@@- server application response error.");

                                        }


                                    }
//
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();

                                int responseCode = response.getHeaders().code();

                                showDialog("Unable to Proccess Payment Now: \nServer response error", "Close");
                                System.out.println("--------------------------------@@@@@@@@@@@@@@@@@@ Server response error @@");
                                System.out.println(responseCode);

                                progressDialog.dismiss();
                            }
                        }
                    });


        } else {

            showDialog("No internet connection. Check your connection and try again", "Close");

        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        timer = new Timer();
        AppClass.LogOutTimerTask logoutTimeTask = new AppClass.LogOutTimerTask(this,
                PaymentCollectionActivity.this);
        timer.schedule(logoutTimeTask, 900000); //auto logout in 15 minutes. i.e(15*60,000)

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }
    }


    public void syncDataToServer(View view) {

        databaseHelper.deleteAllPayment();


//        // Pass the settings flags by inserting them in a bundle
//        Bundle settingsBundle = new Bundle();
//        settingsBundle.putBoolean(
//                ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        settingsBundle.putBoolean(
//                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        /*
//         * Request the sync for the default account, authority, and
//         * manual sync settings
//         */
//        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

    }

    class SyncDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {

            progressDialog.dismiss();

            return sendCollectedPaymentData();
            //return null;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Syncing To Server......");
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.dismiss();

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            int howMuchInfo = databaseHelper.getPaymentsCount();

            if (howMuchInfo <= 0) {
                showDialog("All Data Synced To Server Successfully", "Close");
//                new Thread(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        }
//                ).run();
            } else {
                syncHandler.sendMessage(syncHandler.obtainMessage(SYNC_REST_OF_DATA));
            }

        }

    }


    private class SyncHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NO_NETWORK:
                    showDialog(R.string.no_internet_message, "OK");

                    break;
                case SERVER_ERROR:
                    syncDataTask.cancel(true);
                    showDialog(R.string.server_not_reachable, "Ok");
                    break;

                case NO_DATA_IN_DB:
                    showDialog(R.string.no_payment_data, "Close");

                    break;

                case SYNC_REST_OF_DATA:
                    showDialog(R.string.no_sync_data, "OK");

                default:
                    break;
            }
        }
    }


//    public static Account CreateSyncAccount(Context context) {
//        // Create the account type and default account
//        Account newAccount = new Account(
//                ACCOUNT, ACCOUNT_TYPE);
//        // Get an instance of the Android account manager
//        AccountManager accountManager =
//                (AccountManager) context.getSystemService(
//                        ACCOUNT_SERVICE);
//        /*
//         * Add the account and account type, no password or user data
//         * If successful, return the Account object, otherwise report an error.
//         */
//        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
//            /*
//             * If you don't set android:syncable="true" in
//             * in your <provider> element in the manifest,
//             * then call context.setIsSyncable(account, AUTHORITY, 1)
//             * here.
//             */
//        } else {
//            /*
//             * The account exists or some other error occurred. Log this, report it,
//             * or handle it internally.
//             */
//        }
//
//        return newAccount;
//    }
//
//
//
//    public static class SyncAdapter extends AbstractThreadedSyncAdapter {
//        private AccountManager mAccountManager;
//
//
//        ContentResolver mContentResolver;
//
//        PaymentCollectionActivity paymentCollectionActivity = new PaymentCollectionActivity();
//
//        public SyncAdapter(Context context, boolean autoInitialize) {
//            super(context, autoInitialize);
//
//            mContentResolver = context.getContentResolver();
//
//        }
//
//        public SyncAdapter(
//                Context context,
//                boolean autoInitialize,
//                boolean allowParallelSyncs) {
//            super(context, autoInitialize, allowParallelSyncs);
//
//            mContentResolver = context.getContentResolver();
//
//
//        }
//
//        @Override
//        public void onPerformSync(Account account,
//                                  Bundle extras,
//                                  String authority,
//                                  ContentProviderClient provider,
//                                  SyncResult syncResult) {
//
//            Log.e("IvaAPP -&gt;", "onPerformSync for account[" + account.name + "]");
//
//            paymentCollectionActivity.syncAllPayments();
//
//            Log.e("CCCCheckkkkkkkkkkkkkkk", "onPerformSync: "+ paymentCollectionActivity.syncAllPayments()  );
//
//
//        }
//    }


}
