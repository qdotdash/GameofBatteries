package com.qdotdash.gameofbatteries;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private Context context;
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context)
    {
        this.context = context;
        getSharedPreference();
    }

    private void getSharedPreference(){
        sharedPreferences = context.getSharedPreferences("Check",Context.MODE_PRIVATE);
    }

    public void writePreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("value","INIT_OK");
        editor.commit();
    }
    public boolean checkpreference(){
        boolean status = false;

        if(sharedPreferences.getString("value","null").equals("null")){
            status = false;
        }
        else{
            status = true;
        }
        return  status;
    }
    public  void clearpreference(){
        sharedPreferences.edit().clear().commit();
    }
}
