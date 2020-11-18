package ntk.android.base;

import java.util.HashMap;

import ntk.android.base.activity.common.BaseSplashActivity;
import ntk.android.base.activity.common.RegisterMobileActivity;
import ntk.android.base.styles.BaseModuleStyle;
import ntk.android.base.styles.StyleHelper;

/**
 * Created by m.parishani on 29/08/2017.
 */

public abstract class ApplicationStyle extends StyleHelper {
    public ApplicationStyle() {
        modules = new HashMap();
    }

    //need user to sign in in app
    public boolean SIGNING_REQUIRED = false;

    //@deprecated replace with loginStyle
    public Class<? extends BaseSplashActivity> LoginActivity;
    //@deprecated replace with RegisterStyle
    public Class<? extends RegisterMobileActivity> RegisterActivity;
    public HashMap<String, BaseModuleStyle> modules;

    public BaseModuleStyle getModule(String key) {
        BaseModuleStyle baseModuleStyle = modules.get(key);
        if (baseModuleStyle == null)
            throw new RuntimeException("MODULE IS NULL AND NOT SET IN APPLICATION STYLE CLASS");

        return baseModuleStyle;
    }

    public abstract Class<?> getMainActivity();
}

