package ntk.android.base.utill;

import android.content.Context;
import android.graphics.Typeface;

public class FontManager {

    public static final String IranSans = "fonts/1_req.ttf";
    public static final String IranSansBold = "fonts/1_bold.ttf";
    public static final String Harlow = "fonts/HARLOWSI.TTF";
    public static final String DastNevis = "fonts/2_req.otf";

    public static Typeface GetTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static Typeface T1_Typeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), IranSans);
    }

    public static Typeface T1_BOLD_Typeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), IranSansBold);
    }

    public static Typeface T2_Typeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), DastNevis);
    }

    public static Typeface T2_BOLD_Typeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), Harlow);
    }

}
