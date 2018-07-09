//package com.vanguardghana.atomzpay;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//
//public class SyncService extends Service {
//    // Storage for an instance of the sync adapter
//    private static PaymentCollectionActivity.SyncAdapter sSyncAdapter = null;
//
//    public SyncService() {
//        super();
//    }
//
//    @Override
//    public void onCreate() {
//        if (sSyncAdapter == null)
//            sSyncAdapter = new PaymentCollectionActivity.SyncAdapter(getApplicationContext(), true);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return sSyncAdapter.getSyncAdapterBinder();
//    }
//}
//
