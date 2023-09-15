package com.msq.Parser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;

import com.msq.R;
import com.msq.UtilFiles.ConnectivityReceiver;
import com.msq.UtilFiles.Parameters;
import com.msq.UtilFiles.SavePref;
import com.msq.UtilFiles.util;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cqlsys on 21/4/17.
 */

public class GetMethod {

    String url = "";
    RequestBody requestBody;
    Context context;
    WeakReference<Context> contextWeakReference;
    AlertDialog.Builder builder;
    private SavePref savePref;
    ProgressDialog ProgressDialog;
    String jsonData = "";

    public GetMethod(Context context, ProgressDialog ProgressDialog, String url, RequestBody requestBody) {
        this.url = url;
        this.requestBody = requestBody;
        this.ProgressDialog = ProgressDialog;
        contextWeakReference = new WeakReference<Context>(context);
        this.context = contextWeakReference.get();
        savePref = new SavePref(context);
    }


    public void hitApi() {
        if (ConnectivityReceiver.isConnected()) {
        jsonData = "";
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .addHeader(Parameters.AUTHORIZATION_KEY,"Bearer "+savePref.getAuthorization_key())
                .addHeader(Parameters.SECURITYKEY,util.SECURITYKEY)
                .get().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDialog();
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                jsonData = response.body().string();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new Message(jsonData));
//                        if(context instanceof ForgotPasswordActivity){
//                            ((ForgotPasswordActivity) context).onReceiveResponse(jsonData);
//                        }
                    }
                });

                /*if (!response.isSuccessful()) {
                    showDialog();
                    throw new IOException("Unexpected code " + response);
                } else {
                    jsonData = response.body().string();
                }*/
            }
        });
        } else {
            ProgressDialog.dismiss();
            util.IOSDialog(context, util.internet_Connection_Error);
        }
    }


    private void showDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Warning!");
        builder.setMessage(util.internet_Connection_Error);
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog alert11 = builder.create();
                alert11.show();
            }
        });

    }


}