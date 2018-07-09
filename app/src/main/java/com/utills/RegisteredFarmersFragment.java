//package com.utills;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.andexert.library.RippleView;
//
//import com.androidnetworking.AndroidNetworking;
//import com.androidnetworking.common.ANRequest;
//import com.androidnetworking.common.ANResponse;
//import com.androidnetworking.common.Priority;
//import com.androidnetworking.error.ANError;
//import com.androidnetworking.interfaces.JSONObjectRequestListener;
//import com.degreatinusah.datacollectiontool.R;
//import com.degreatinusah.datacollectiontool.activities.MainActivity;
//import com.degreatinusah.datacollectiontool.adapters.FarmersAdapter;
//import com.degreatinusah.datacollectiontool.adapters.MyDividerItemDecoration;
//import com.degreatinusah.datacollectiontool.adapters.RecyclerTouchListener;
//import com.degreatinusah.datacollectiontool.database.DatabaseHelper;
//import com.degreatinusah.datacollectiontool.models.Farmer;
//import com.degreatinusah.datacollectiontool.utils.AppConstants;
//import com.degreatinusah.datacollectiontool.utils.AppStatus;
//import com.degreatinusah.datacollectiontool.utils.AppUtils;
//import com.degreatinusah.datacollectiontool.utils.EndPoints;
//import com.vanguardghana.atomzpay.R;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Response;
//
//
//public class RegisteredFarmersFragment extends Fragment {
//
//
//    private OnFragmentInteractionListener mListener;
//
//    private List<Farmer> farmerList = new ArrayList<>();
//    private RecyclerView recyclerView;
//    private FarmersAdapter mAdapter;
//    private FloatingActionButton fabSync;
//    private boolean syncStatus = false;
//    LinearLayout llTransaction;
//    RelativeLayout rlReload;
//    RippleView rpReload;
//    private SwipeRefreshLayout swipeContainer;
//    ProgressDialog pDialog;
//    boolean isRunning = false;
//    private DatabaseHelper dbHelper;
//    private AppUtils appUtils;
//
//    public RegisteredFarmersFragment() {
//        // Required empty public constructor
//    }
//
//    public static RegisteredFarmersFragment newInstance(String param1, String param2) {
//        RegisteredFarmersFragment fragment = new RegisteredFarmersFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        AndroidNetworking.initialize(getActivity().getApplicationContext());
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_registered_farmers, container, false);
//        llTransaction = (LinearLayout) view.findViewById(R.id.llTransaction);
//        rlReload = (RelativeLayout) view.findViewById(R.id.rlReload);
//        rpReload = (RippleView) view.findViewById(R.id.rpReload);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        fabSync = (FloatingActionButton) view.findViewById(R.id.fabSynFarmers);
//        dbHelper = new DatabaseHelper(getActivity());
//        appUtils = new AppUtils(getActivity());
//
//        // Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
//        showDefaultUI();
//        mAdapter = new FarmersAdapter(getActivity().getApplicationContext(), farmerList);
//        prepareFarmerData();
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
//        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
//
//        recyclerView.setAdapter(mAdapter);
//
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                Farmer farmer = farmerList.get(position);
//                Toast.makeText(getActivity().getApplicationContext(), farmer.getFirstName() + " " + farmer.getFarmerId() + " is selected!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
//        fabSync.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
////                List<com.degreatinusah.datacollectiontool.datatabase.models.Farmer> dbFarmers = dbHelper.getAllFarmers();
////                for (int i = 0; i < dbFarmers.size(); i++) {
////                    com.degreatinusah.datacollectiontool.datatabase.models.Farmer dbFarmer = dbFarmers.get(i);
////                    if (dbHelper.deleteFarmerById(String.valueOf(dbFarmer.getId()))) {
////                        Toast.makeText(getActivity().getApplicationContext(), dbFarmer.getId() + " is Deleted!", Toast.LENGTH_SHORT).show();
////                    }
////
////                }
//                if (AppStatus.getInstance(getActivity()).isOnline()) {
//                    new SyncDataTask().execute();
//                } else {
//                    new AppUtils(getActivity()).showAlertMsg("No Internet Connection.");
//                }
//            }
//        });
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (!isRunning) {
//                    llTransaction.setVisibility(View.GONE);
//                    postRefresh();
//                    //getDriversOrderHistory(accountNo);
//                }
//
//            }
//        });
//
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//
//        rlReload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                llTransaction.setVisibility(View.GONE);
//                postRefresh();
//            }
//        });
//        rpReload.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
//            @Override
//            public void onComplete(RippleView rippleView) {
//                llTransaction.setVisibility(View.GONE);
//                postRefresh();
//            }
//        });
//        rpReload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                llTransaction.setVisibility(View.GONE);
//                postRefresh();
//            }
//        });
//        pDialog = new ProgressDialog(getActivity());
//        return view;
//    }
//
//    private void showDefaultUI() {
//        farmerList.clear();
//        fabSync.setVisibility(View.GONE);
//        llTransaction.setVisibility(View.VISIBLE);
//    }
//
//    private void prepareFarmerData() {
//
//        isRunning = true;
//        farmerList = appUtils.getAllFarmersList();
////        if (dbHelper.getFarmerCount() == 0) {
////            farmerList.clear();
////        }
//        if (farmerList.isEmpty()) {
//            fabSync.setVisibility(View.GONE);
//            llTransaction.setVisibility(View.VISIBLE);
//        } else {
//            isRunning = false;
//            fabSync.setVisibility(View.VISIBLE);
//            llTransaction.setVisibility(View.GONE);
//            swipeContainer.setRefreshing(false);
//        }
//        mAdapter = new FarmersAdapter(getActivity().getApplicationContext(), farmerList);
//        // recyclerView.setAdapter(mAdapter);
//        recyclerView.swapAdapter(mAdapter, true);
//        // recyclerView.scrollBy(0, 0);
//        //recyclerView.invalidate();
//        mAdapter.notifyDataSetChanged();
//
//    }
//
//    private void postRefresh() {
//        prepareFarmerData();
//        swipeContainer.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeContainer.setRefreshing(true);
//            }
//        });
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        dbHelper = new DatabaseHelper(context);
//        appUtils = new AppUtils(context);
//        farmerList = new ArrayList<>();
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//        //farmerList = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//
//    private void showStatus(String message) {
//        isRunning = false;
//        // appUtils = new AppUtils(getActivity());
//        appUtils.showAlertMsg(message);
//        swipeContainer.setRefreshing(false);
//        if (farmerList.size() == 0) {
//            llTransaction.setVisibility(View.VISIBLE);
//        }
//    }
//
//    class SyncDataTask extends AsyncTask<Void, Void,Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... voids) {
//            return syncAllFarmers();
//            //return null;
//        }
//
//        /**
//         * Before starting background thread Show Progress Dialog
//         */
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog.setMessage("Syncing To Server......");
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//            //pDialog.hide();
//            if (pDialog.isShowing()) {
//                pDialog.dismiss();
//            }
//            swipeContainer.setRefreshing(false);
//            //  refreshFragment();
//            if(result && farmerList.size()<=0) {
//                showStatus("All Data Synced To Server Successfully");
//            }else{
//                showStatus("Not All Data Were Synced. Kindly Resync To Ensure All Data Is Synched To Server.Thanks.");
//            }
//            updateUI();
//           // prepareFarmerData();
//            //refreshFragment();
//        }
//
//    }
//
//
//    private boolean syncAllFarmers() {
//
//
//        try {
//            for (int i = 0; i < farmerList.size(); i++) {
//                boolean isSynched = sendFarmerToServer(farmerList.get(i), i);
//                if (!isSynched) {
//                    return false;
//                }
//            }
//            return true;
//        } catch (Exception ex) {
//        }
//        return false;
//
//    }
//
//    private void updateUI() {
//        if (farmerList.size() <= 0) {
//          //  showStatus("All Farmers Synced To Server Successfully");
//            fabSync.setVisibility(View.GONE);
//            llTransaction.setVisibility(View.VISIBLE);
//            swipeContainer.setRefreshing(false);
//
//        } else {
//            isRunning = false;
//            fabSync.setVisibility(View.VISIBLE);
//            llTransaction.setVisibility(View.GONE);
//            swipeContainer.setRefreshing(false);
//        }
//        mAdapter = new FarmersAdapter(getActivity().getApplicationContext(), farmerList);
//        // recyclerView.setAdapter(mAdapter);
//        recyclerView.swapAdapter(mAdapter, true);
//        // recyclerView.scrollBy(0, 0);
//        //recyclerView.invalidate();
//        mAdapter.notifyDataSetChanged();
//    }
//
//
//    private boolean sendFarmerToServer(Farmer farmer, int index) {
//        syncStatus = false;
//        AppUtils appUtils = new AppUtils(getActivity());
//        JSONObject params = new JSONObject();
//        try {
//            params.put("firstName", farmer.getFirstName());
//            params.put("lastName", farmer.getLastName());
//            params.put("middleName", farmer.getMiddleName());
//            params.put("age", String.valueOf(farmer.getAge()));
//            params.put("contact", farmer.getContact());
//            params.put("genderId", String.valueOf(farmer.getGenderId()));
//            params.put("birthdate", farmer.getBirthdate());
//            params.put("maritalStatus", farmer.getMaritalStatus());
//            params.put("haveChildren", farmer.isHaveChildren());
//            params.put("childrenCount", farmer.getChildrenCount());
//            params.put("householdSize", farmer.getHouseholdSize());
//            params.put("startFarmingYear", farmer.getStartFarmingYear());
//            params.put("yearsOfExperience", farmer.getYearsOfExperience());
//            params.put("districtId", farmer.getDistrictId());
//            params.put("community", farmer.getCommunity());
//            params.put("extensionAgentId", appUtils.getPref(AppConstants.AGENT_ID, "0"));
//            params.put("onMobileMoney", farmer.isOnMobileMoney());
//            params.put("operatorId", farmer.getOperatorId());
//            params.put("regMoMoNumber", farmer.getRegMoMoNumber());
//            params.put("copFarmUndAgregation", farmer.getCopFarmUndAgregation());
//            params.put("leadFarmer", farmer.isLeadFarmer());
//            params.put("farmerNumber", farmer.getFarmerNumber());
//            params.put("cooperativeNum", farmer.getCooperativeNum());
//            params.put("contractWithOffTaker", farmer.isContractWithOffTaker());
//            params.put("inputsFromOffTaker", farmer.isInputsFromOffTaker());
//            params.put("offTakerName", farmer.getOffTakerName());
//            params.put("farmAcquiTypeId", farmer.getFarmAcquiTypeId());
//            params.put("farmBusinessStatusId", farmer.getFarmBusinessStatusId());
//            params.put("buyerCategoryId", farmer.getBuyerCategoryId());
//            params.put("agreementwithBuyers", farmer.isAgreementwithBuyers());
//            params.put("inAnyOrganOrProject", farmer.isInAnyOrganOrProject());
//            params.put("organOrProjName", farmer.getOrganOrProjName());
//            params.put("inAnyFarmerUnionOrCoop", farmer.isInAnyFarmerUnionOrCoop());
//            params.put("farmerUnionOrCooperativeName", farmer.getFarmerUnionOrCooperativeName());
//            params.put("receiveAnyInfo", farmer.isReceiveAnyInfo());
//            params.put("farmerInfoReceivedId", new JSONArray(farmer.getFarmerInfoReceivedId()));
//            params.put("farmerCropsId", new JSONArray(farmer.getFarmerCompanyIds()));
//            params.put("farmerCompanyIds", new JSONArray(farmer.getFarmerCompanyIds()));
//            params.put("inputsReceivedByLeadFarmer", new JSONArray(farmer.getInputsReceivedByLeadFarmer()));
//            params.put("farmsizebyfarmer", farmer.getFarmsizebyfarmer());
//
//            params.put("photo", farmer.getPhoto());
//            ANRequest request = AndroidNetworking.post(EndPoints.SAVE_FARMER)
//                    .addJSONObjectBody(params) // posting any type of file
//                    .addHeaders("Authorization", "Bearer " + appUtils.getPref(AppConstants.API_TOKEN, ""))
//                    .setTag("SynchRequest")
//                    .setPriority(Priority.MEDIUM)
//                    .build();
//            ANResponse<JSONObject> response = request.executeForJSONObject();
//
//            if (response.isSuccess()) {
//                JSONObject jsonObject = response.getResult();
//                System.out.println(response.toString());
//                // Log.d(TAG, response.toString());
//
//                try {
//                    String message = jsonObject.getString("message");
//                    if (jsonObject.getInt("code") == AppConstants.POST_STATUS_CODE) {
//                        //  appUtils.showAlertMsg(message);
//                        try {
//                            if (dbHelper.deleteFarmerById(farmer.getFarmerId())) {
//                                farmerList.remove(index);
//                                syncStatus = true;
//                                return syncStatus;
//                            }
//
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                            return false;
//                        }
//
//
//                    } else {
//                        showStatus(message);
//                        syncStatus = false;
//                        //appUtils.showAlertMsg("An Error Occurred Whiles Saving The Farmer Information");
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    syncStatus = false;
//                    showStatus("An Error Occurred Whiles Syncing The Farmer Information");
//
//                }
//            } else {
//                ANError error = response.getError();
//                showStatus(error.getErrorBody());
//                syncStatus = false;
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            syncStatus = false;
//        }
//        return syncStatus;
//    }
//}
