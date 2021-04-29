package ntk.android.base;

import java.util.HashMap;

import ntk.android.base.activity.BaseActivity;
import ntk.android.base.activity.common.BaseSplashActivity;
import ntk.android.base.styles.BaseModuleStyle;
import ntk.android.base.styles.StyleHelper;
import ntk.android.base.utill.prefrense.EasyPreference;
import ntk.android.base.view.ViewController;

/**
 * Created by m.parishani on 29/08/2017.
 */

public abstract class ApplicationStyle extends StyleHelper {
    String APP_LANGUAGE;

    public void setAppLanguage(String appLanguage) {
        if (appLanguage == null)
            appLanguage = "fa";
        EasyPreference.with(NTKApplication.instance.getApplicationContext()).addString("DEFUALT_APP_LANG", appLanguage);
        APP_LANGUAGE = appLanguage.toLowerCase().trim();
    }

    public String getAppLanguage() {
        if (APP_LANGUAGE == null)
            APP_LANGUAGE = EasyPreference.with(NTKApplication.instance.getApplicationContext()).getString("DEFUALT_APP_LANG", "en");
        return APP_LANGUAGE;
    }

    public ApplicationStyle() {
        modules = new HashMap();
        Init();
    }

    //need user to sign in in app
    public boolean SIGNING_REQUIRED = false;

    public boolean show_NotInterested_Btn() {
        return show_notInterestedBtn;
    }

    protected boolean show_notInterestedBtn = true;
    //@deprecated replace with loginStyle
    public Class<? extends BaseSplashActivity> LoginActivity;
    //@deprecated replace with RegisterStyle
    public Class<? extends BaseActivity> RegisterActivity;
    public HashMap<String, BaseModuleStyle> modules;

    public BaseModuleStyle getModule(String key) {
        BaseModuleStyle baseModuleStyle = modules.get(key);
        if (baseModuleStyle == null)
            throw new RuntimeException("MODULE IS NULL AND NOT SET IN APPLICATION STYLE CLASS");

        return baseModuleStyle;
    }

    public void Init() {

    }

    public abstract ViewController getViewController();

    public abstract Class<?> getMainActivity();

}

