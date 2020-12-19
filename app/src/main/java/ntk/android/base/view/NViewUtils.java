package ntk.android.base.view;

import android.content.Context;

import java.text.DecimalFormat;

public class NViewUtils {
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static String PriceFormat(float price) {
        return convert(new DecimalFormat("###,###,###,###").format(price));
    }

    private static String[] faNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};

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
}

