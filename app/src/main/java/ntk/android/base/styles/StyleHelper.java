package ntk.android.base.styles;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StyleHelper {

    private static String EXTRA_STYLE_DB = "STATIC_STYLE";

    /**
     * set style of each module as
     *
     * @param context
     * @param style   String of gson-converted object of each module style
     * @param tag     defult tag of each module
     */
    public static void saveModuleStyle(Context context, String tag, String style) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(EXTRA_STYLE_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(tag, style);
        editor.commit();
    }

    public static <T> T getModuleStyle(Context context, String tag, Class<T> tClass) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(EXTRA_STYLE_DB, Context.MODE_PRIVATE);
        String jsonStr = sharedpreferences.getString(tag, null);
        if (jsonStr == null)
            return null;
        Gson sGson = new GsonBuilder().serializeNulls().create();
        return sGson.fromJson(jsonStr, tClass);
    }

    /**
     * set default fo Module theme base on input object
     * @note if module save before no affect on saved Theme
     * @param context
     * @param TAG
     * @param ob
     * @param cl
     */
    public static void Default(Context context, String TAG, Object ob, Class<?> cl) {
       if (getModuleStyle(context,TAG,cl)==null)
           saveModuleStyle(context,TAG,new GsonBuilder().serializeNulls().create().toJson(ob));
    }
}
