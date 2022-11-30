package ntk.android.base;

import android.content.Context;

import java.util.HashMap;

import ntk.android.base.activity.BaseActivity;
import ntk.android.base.activity.common.BaseSplashActivity;
import ntk.android.base.entitymodel.application.ApplicationThemeConfigModel;
import ntk.android.base.styles.BaseModuleStyle;
import ntk.android.base.styles.StyleHelper;
import ntk.android.base.styles.UnitStyleEnum;
import ntk.android.base.utill.prefrense.EasyPreference;
import ntk.android.base.view.ThemeNameEnum;
import ntk.android.base.view.ViewController;

/**
 * Created by m.parishani on 29/08/2017.
 */

public abstract class ApplicationStyle extends StyleHelper {
    String APP_LANGUAGE;
    protected ThemeNameEnum theme;

    public void setAppLanguage(int enumLang) {
        String appLanguage = language(enumLang);
        EasyPreference.with(NTKApplication.instance.getApplicationContext()).addString("DEFUALT_APP_LANG", appLanguage);
        APP_LANGUAGE = appLanguage.toLowerCase().trim();
    }

    public String language(int enumLang) {
        String appLanguage = "fa";
        if (enumLang == 0)
            appLanguage = "fa";
        else if (enumLang == 1)
            appLanguage = "fa";
        else if (enumLang == 2)
            appLanguage = "en";
        else if (enumLang == 3)
            appLanguage = "de";
        else if (enumLang == 4)
            appLanguage = "fr";
        else if (enumLang == 5)
            appLanguage = "ch";
        else if (enumLang == 6)
            appLanguage = "jp";
        else if (enumLang == 7)
            appLanguage = "es";
        return appLanguage;
    }

    public void setTheme(ApplicationThemeConfigModel appTheme) {
        if (appTheme == null || appTheme.TypeId == null) {
            theme = ThemeNameEnum.DEFAULT;
            return;
        }
        String typeId = appTheme.TypeId;
        theme = ThemeNameEnum.get(typeId);
        EasyPreference.with(NTKApplication.instance.getApplicationContext()).addString("DEFAULT_APP_THEME", theme.code());
    }

    public ThemeNameEnum getTheme() {
        if (theme == null) {
            String themeName = EasyPreference.with(NTKApplication.instance.getApplicationContext()).getString("DEFAULT_APP_THEME", ThemeNameEnum.DEFAULT.code());
            theme = ThemeNameEnum.get(themeName);
        }
        return theme;
    }

    public String getAppLanguage() {
        return getAppLanguage(NTKApplication.instance.getApplicationContext());
    }

    public static String GET_DEFAULT(Context context) {
        return EasyPreference.with(context).getString("DEFUALT_APP_LANG", "fa");
    }

    public String getAppLanguage(Context context) {
        if (APP_LANGUAGE == null)
            APP_LANGUAGE = EasyPreference.with(context).getString("DEFUALT_APP_LANG", "fa");
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
    public HashMap<UnitStyleEnum, BaseModuleStyle> modules;

    public BaseModuleStyle getModule(UnitStyleEnum key) {
        BaseModuleStyle baseModuleStyle = modules.get(key);
        //todo for next release
//        if (baseModuleStyle == null)
//            throw new RuntimeException("MODULE IS NULL AND NOT SET IN APPLICATION STYLE CLASS");

        return baseModuleStyle;
    }

    public void Init() {

    }

    public abstract ViewController getViewController();

    public abstract Class<?> getMainActivity();

}

