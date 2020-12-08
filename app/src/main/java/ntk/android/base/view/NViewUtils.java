package ntk.android.base.view;

import android.content.Context;

public class NViewUtils {
    public static int dpToPx( Context context,int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}

