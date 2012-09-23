package com.example.mekurigl;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.*;

public class Setting  extends PreferenceActivity {
	public static final int NOISE_SENSITIVITY_MIN = 1;
	public static final int NOISE_SENSITIVITY_MAX = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		CheckBoxPreference enableToucheffect = (CheckBoxPreference)findPreference("enableTouchEffect");
		enableToucheffect.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String summary;
				if ((Boolean) newValue) {
					summary = getText(R.string.use_toucheffect_summary_on).toString();
				} else {
					summary = getText(R.string.use_toucheffect_summary_off).toString();
				}
				preference.setSummary(summary);
				return true;
			}
		});

		EditTextPreference waitTime = (EditTextPreference)findPreference("noiseSensitivity");
		waitTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String input = newValue.toString();
					int num = Integer.parseInt(input);
					if (input != null && num >= NOISE_SENSITIVITY_MIN && num <= NOISE_SENSITIVITY_MAX){
						preference.setSummary(input + "%");
						return true;
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
						String errMsg = getText(R.string.noise_Sensitivity_range_error).toString();
						builder.setMessage(String.format(errMsg , NOISE_SENSITIVITY_MIN, NOISE_SENSITIVITY_MAX)).show();
						return false;
					}
			}
		});

		CheckBoxPreference showFps = (CheckBoxPreference)findPreference("showFps");
		showFps.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String summary;
				if ((Boolean) newValue) {
					summary = getText(R.string.use_show_fps_summary_on).toString();
				} else {
					summary = getText(R.string.use_show_fps_summary_off).toString();
				}
				preference.setSummary(summary);
				return true;
			}
		});
	}

	public static boolean enableTouchEffect(Context con){
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("enableTouchEffect", false);
	}

	public static int noiseSensitivity(Context con){
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(con).getString("noiseSensitivity", "100"));
	}

	public static boolean showFps(Context con){
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("showFps", false);
	}
}
