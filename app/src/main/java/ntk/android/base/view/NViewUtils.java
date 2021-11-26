package ntk.android.base.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;

import java.text.DecimalFormat;

public class NViewUtils {
    private static String[] faNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static int getResIdFromAttribute(Context activity, final int attr) {
        if (attr == 0)
            return 0;
        final TypedValue typedvalueattr = new TypedValue();
        activity.getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }

    public static int getColorWithAlpha(int yourColor, int alpha) {
        int red = Color.red(yourColor);
        int blue = Color.blue(yourColor);
        int green = Color.green(yourColor);
        return Color.argb(alpha, red, green, blue);
    }

    public static String PriceFormat(double price) {
        return convert(new DecimalFormat("###,###,###,###,###,###").format(price));
    }

    private static String convert(String text) {
        if (text.length() == 0) {
            return "";
        }
        String out = "";
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out += faNumbers[number];
            } else if (c == '٫' || c == ',') {
                out += '،';
            } else {
                out += c;
            }
        }
        return out;
    }

    public @ColorInt
    int getColorIdFromAttribute(Context context, @AttrRes int attrId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrId, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }
}

