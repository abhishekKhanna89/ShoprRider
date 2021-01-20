package com.shopprdriver.Session;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.shopprdriver.R;


public class SessonManager {

    private static SessonManager pref;
    private final SharedPreferences sharedPreference;
    private final SharedPreferences.Editor editor;
    public static final String NAME = "MY_PREFERENCES";
    public static final String Token = "token";
    public static final String NOTIFICATION_TOKEN="notification_token";


    public Dialog mDialog;
    public SessonManager(Context ctx) {
        sharedPreference = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        editor.apply();
    }


    public static SessonManager getInstance(Context ctx) {
        if (pref == null) {
            pref = new SessonManager(ctx);
        }
        return pref;
    }



    public void setToken(String token) {
     //   Log.d("sssss", token);
        editor.putString(Token, token);
        editor.commit();
    }

    public String getToken() {
        return sharedPreference.getString(Token, "");
    }


    public void setNotificationToken(String notificationToken){
        editor.putString(NOTIFICATION_TOKEN,notificationToken);
        editor.apply();
    }
    public String getNotificationToken(){
        return sharedPreference.getString(NOTIFICATION_TOKEN,"");
    }

    public void hideProgress() {
        while (mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();

        }

    }


    public void showProgress(Context mContext) {
        if(mContext!=null){
            mDialog= new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.custom_progress_layout);
            mDialog.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

    }
}
