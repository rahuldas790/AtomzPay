//package com.vanguardghana.atomzpay;
//
//import android.accounts.Account;
//import android.accounts.AccountManager;
//import android.content.AbstractThreadedSyncAdapter;
//import android.content.ContentProviderClient;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.SyncResult;
//import android.os.Bundle;
//import android.util.Log;
//
//public class SyncAdapter extends AbstractThreadedSyncAdapter {
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
////            paymentCollectionActivity.syncAllPayments();
//
////            Log.e("CCCCheckkkkkkkkkkkkkkk", "onPerformSync: "+ paymentCollectionActivity.syncAllPayments()  );
//
//
//        }
//    }
