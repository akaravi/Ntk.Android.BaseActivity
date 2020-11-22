package ntk.android.base.utill;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ViewMyUtils {
    public static DisplayMetrics getDisplayMetric(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

}
