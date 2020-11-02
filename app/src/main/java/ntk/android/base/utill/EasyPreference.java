package ntk.android.base.utill;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import ntk.android.base.BaseNtkApplication;

//import ntk.android.base.BuildConfig;

public class EasyPreference {

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static Builder with(Context context, String prefName) {
        return new Builder(context, prefName);
    }

    public static class Builder {
        SharedPreferences preferences;
        SharedPreferences.Editor editor;

        public Builder(Context context) {
            preferences = context.getSharedPreferences(BaseNtkApplication.get().getApplicationParameter().PACKAGE_NAME(), Context.MODE_PRIVATE);
            editor = preferences.edit();
        }

        public Builder(Context context, String prefName) {
            preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }

        public Builder addBoolean(String key, boolean value) {
            key=key.toLowerCase();
            editor.putBoolean(key, value);
            save();
            return this;
        }

        public Builder addString(String key, String value) {
            key=key.toLowerCase();
            editor.putString(key, value);
            save();
            return this;
        }

        public Builder addInt(String key, int value) {
            key=key.toLowerCase();
            editor.putInt(key, value);
            save();
            return this;
        }

        public Builder addFloat(String key, float value) {
            key=key.toLowerCase();
            editor.putFloat(key, value);
            return this;
        }

        public Builder addLong(String key, long value) {
            key=key.toLowerCase();
            editor.putLong(key, value);
            return this;
        }

        public Builder addStringSet(String key, Set<String> value) {
            key=key.toLowerCase();
            editor.putStringSet(key, value);
            return this;
        }

        public Builder save() {
            editor.apply();
            return this;
        }

        public boolean getBoolean(String key, boolean defalutValue) {
            key=key.toLowerCase();
            return preferences.getBoolean(key, defalutValue);
        }

        public String getString(String key, String defalutValue) {
            key=key.toLowerCase();
            return preferences.getString(key, defalutValue);
        }

        public int getInt(String key, int defalutValue) {
            key=key.toLowerCase();
            return preferences.getInt(key, defalutValue);
        }

        public float getFloat(String key, float defalutValue) {
            key=key.toLowerCase();
            return preferences.getFloat(key, defalutValue);
        }

        public long getLong(String key, long defalutValue) {
            key=key.toLowerCase();
            return preferences.getLong(key, defalutValue);
        }

        public Set<String> getStringSet(String key, Set<String> defalutValue) {
            key=key.toLowerCase();
            return preferences.getStringSet(key, defalutValue);
        }

        public Builder remove(String key) {
            key=key.toLowerCase();
            editor.remove(key);
            return this;
        }

        public Builder clearAll() {
            editor.clear();
            return this;
        }
    }
}
