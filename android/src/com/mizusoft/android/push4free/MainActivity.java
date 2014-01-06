package com.mizusoft.android.push4free;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 *
 * @author tim
 */
public class MainActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        startService(getApplication());

        Preference infoPreference = (Preference) findPreference("P4FPrefInfo");
        infoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference prfrnc) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/cybertim/push4free"));
                startActivity(intent);
                return true;
            }
        });

        CheckBoxPreference enablePreference = (CheckBoxPreference) findPreference("P4FPrefEnabled");
        enablePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference prfrnc) {
                startService(getApplication());
                return true;
            }
        });

    }

    public static void startService(Context context) {
        Intent service = new Intent(context, MyService.class);
        context.startService(service);
    }

}
