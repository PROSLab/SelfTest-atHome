package it.overside.distfinder;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigManager {
    private static final String KEY_PREFERENCE ="sizepreference";

    Context context;
    ConfigManager(Context c){
        this.context = c;
    }


    private SharedPreferences getAppPreference(){
        return this.context.getSharedPreferences(KEY_PREFERENCE,Context.MODE_PRIVATE);
    }

    //#region GESTIONE CONFIGURAZIONI APP

    public void setConfigString(String code,String value){
        SharedPreferences preferences = getAppPreference();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(code,value);
        editor.apply();
    }

    public String getConfig(String code, String def){
        SharedPreferences preferences = getAppPreference();
        return preferences.getString(code,def);
    }


    public boolean getBoolConfig(String code,boolean def){
        SharedPreferences preferences = getAppPreference();
        return preferences.getBoolean(code,def);
    }

    public void setBoolConfig(String code,boolean value){
        SharedPreferences preferences = getAppPreference();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(code,value);
        editor.apply();
    }
    //#endregion
}
