package ntk.android.base.styles;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import ntk.android.base.activity.BaseActivity;


/**
 * Created by m.parishani on 08/11/2017.
 */

public class BaseModuleStyle implements Serializable {

    public final String TAG;

    @SerializedName("Type")
    protected int Type;
    @SerializedName("InnerType")
    protected int InnerType;

    public BaseModuleStyle(String tag, int theme, int subtheme) {
        TAG = tag;
        Type = theme;
        InnerType = subtheme;
    }

    public int getModuleType() {
        return Type;
    }

    public String getTAG() {
        return TAG;
    }
//    public BaseDialog styleBaseDialog(String placeTag, int styleType) {
//        return null;
//    }

    protected BaseActivity styleActivity(String placeTag, int styleType) {
        return null;
    }

//    protected BaseFragment styleFragment(String placeTag, int StyleType) {
//        return null;
//    }


}
