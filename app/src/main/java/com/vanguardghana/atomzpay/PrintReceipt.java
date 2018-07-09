package com.vanguardghana.atomzpay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.UsbThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

import static com.telpo.tps550.api.demo.printer.UsbPrinterActivity.printContent;

public class PrintReceipt extends Activity{


    Activity mActivity;

    public Activity getActivity() {
        return mActivity;
    }

    Handler mHandler;

    private String printVersion;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;

    String receiptContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;

    private boolean LowBattery = false;

    private final static int MAX_LEFT_DISTANCE = 255;

    private String Result;
    private Boolean nopaper = false;

    UsbThermalPrinter mUsbThermalPrinter;

    ProgressDialog progressDialog;

    //constructor
    public PrintReceipt(Activity activity){
        mActivity = activity;
        mHandler = new MyHandler();

        mUsbThermalPrinter = new UsbThermalPrinter(mActivity);

    }




//    @Override
//    public void run() {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    mUsbThermalPrinter.start(0);
//                    mUsbThermalPrinter.reset();
//                    printVersion = mUsbThermalPrinter.getVersion();
//                } catch (TelpoException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (printVersion != null) {
//                        Message message = new Message();
//                        message.what = PRINTVERSION;
//                        message.obj = "1";
//                        mHandler.sendMessage(message);
//                    } else {
//                        Message message = new Message();
//                        message.what = PRINTVERSION;
//                        message.obj = "0";
//                        mHandler.sendMessage(message);
//                    }
//                }
//            }
//        }).start();
//    }



    public void checkAndPrint(String receiptInfo){
        printContent = receiptInfo;
        leftDistance = 1;
        lineDistance = 1;

        wordFont = 2;
        printGray = 1;

        if (printContent == null || printContent.length() == 0) {
            Toast.makeText(getActivity(), mActivity.getString(R.string.empty), Toast.LENGTH_LONG).show();
            return;
        }
        if (LowBattery == true) {
            mHandler.sendMessage(mHandler.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(getActivity(), mActivity.getString(R.string.bl_dy), mActivity.getString(R.string.printing_wait));
                mHandler.sendMessage(mHandler.obtainMessage(PRINTCONTENT, 1, 0, null));
            } else {
                Toast.makeText(getActivity(), mActivity.getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
            }
        }
    }

    private final BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                //TPS390 can not print,while in low battery,whether is charging or not charging
                if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()){
                    if (level * 5 <= scale) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                }else {
                    if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                        if (level * 5 <= scale) {
                            LowBattery = true;
                        } else {
                            LowBattery = false;
                        }
                    } else {
                        LowBattery = false;
                    }
                }
            }
//            //Only use for TPS550MTK devices
//            else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
//                int status = intent.getIntExtra("action", 0);
//                int level = intent.getIntExtra("level", 0);
//                if(status == 0){
//                    if(level < 1){
//                        LowBattery = true;
//                    }else {
//                        LowBattery = false;
//                    }
//                }else {
//                    LowBattery = false;
//                }
//            }
        }
    };

    private void noPaperDlg() {
        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(getActivity());
        dlg.setTitle(mActivity.getString(R.string.noPaper));
        dlg.setMessage(mActivity.getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dlg.show();
    }




    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(mActivity.getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(mActivity.getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                    break;

                case PRINTCONTENT:
                    new contentPrintThread().start();
                    break;

                case CANCELPROMPT:
                    if (progressDialog != null && !getActivity().isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(getActivity());
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(mActivity.getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(mActivity.getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(getActivity(), "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private class contentPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                mUsbThermalPrinter.setLeftIndent(leftDistance);
                mUsbThermalPrinter.setLineSpace(lineDistance);
                mUsbThermalPrinter.setFontSize(2);
                mUsbThermalPrinter.setGray(printGray);
                mUsbThermalPrinter.addString(printContent);
                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(10);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    mHandler.sendMessage(mHandler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                mHandler.sendMessage(mHandler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    mHandler.sendMessage(mHandler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && !getActivity().isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        unregisterReceiver(printReceive);
        mUsbThermalPrinter.stop();
        super.onDestroy();
    }

    public Boolean checkPaper() {
        Boolean noPaper = false ;
        try {
            mUsbThermalPrinter.reset();
            mUsbThermalPrinter.setGray(printGray);
            mUsbThermalPrinter.addString("");
            mUsbThermalPrinter.printString();
            mUsbThermalPrinter.walkPaper(1);
            noPaper = true;

        } catch (Exception e) {
            e.printStackTrace();
            Result = e.toString();
            if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {

            }
            else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                mHandler.sendMessage(mHandler.obtainMessage(OVERHEAT, 1, 0, null));
            } else {
                mHandler.sendMessage(mHandler.obtainMessage(PRINTERR, 1, 0, null));
            }
        }

        return noPaper;
    }




}
