package ntk.android.base.utill;

import android.content.Context;
import android.graphics.Typeface;

public class FontManager {

    public static final String IranSans = "fonts/Iran_Sans.ttf";
    public static final String IranSansBold = "fonts/iranbold.ttf";
    public static final String Harlow = "fonts/HARLOWSI.TTF";
    public static final String DastNevis = "fonts/Far_DastNevis.otf";

    public static Typeface GetTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
