package com.solvworth.crest;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sonesis 04/01/2017.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences("CREST", PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDbState(boolean db_exists) {
        editor.putBoolean("state", db_exists);
        editor.commit();
    }

    public boolean getDbState() {
        return pref.getBoolean("state", false);
    }

    public void setSoundState(boolean db_exists) {
        editor.putBoolean("sound", db_exists);
        editor.commit();
    }

    public boolean getSoundState() {
        return pref.getBoolean("sound", true);
    }
}
