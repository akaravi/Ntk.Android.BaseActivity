package ntk.android.base.utill;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import androidx.annotation.AttrRes;

public class ColorUtils {

    public static int FETCH_Attr_COLOR(Context mContext, @AttrRes int id) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] {id });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }
}
