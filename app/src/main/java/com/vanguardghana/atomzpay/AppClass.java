package com.vanguardghana.atomzpay;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.util.TimerTask;

public class AppClass extends Application {


    static Context mContext;
    static Activity mActivity;




    @Override
    public void onCreate() {
        super.onCreate();
    }


    public static class LogOutTimerTask extends TimerTask {

        public LogOutTimerTask(Context context, Activity activity ){
            mContext = context;
            mActivity = activity;

        }


        public void run() {

            //redirect user to login screen
            Intent i = new Intent(mContext, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(i);
            mActivity.finish();

        }
    }

}
