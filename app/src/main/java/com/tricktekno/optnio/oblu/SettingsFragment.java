package com.tricktekno.optnio.oblu;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Tobias on 5/6/2015.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences);
    }
}
