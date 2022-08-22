package ntk.android.base;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.yariksoffice.lingver.Lingver;

import java.io.File;

import es.dmoral.toasty.Toasty;
import ntk.android.base.utill.FontManager;


/**
 *
 */
public abstract class NTKApplication extends BaseNtkApplication implements ApplicationParamProvider {
    public static boolean Inbox = false;
    //@Notify please note that not change this value to True

    protected static ApplicationStyle applicationStyle;

    private LocalizationApplicationDelegate localizationDelegate = new LocalizationApplicationDelegate();
    @Override
    protected void attachBaseContext(Context base) {
        instance = this;

        MultiDex.install(base);
        super.attachBaseContext(base);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localizationDelegate.onConfigurationChanged(this);
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }

    @Override
    public Resources getResources() {
        return localizationDelegate.getResources(this, super.getResources());
    }

    public static ApplicationStyle getApplicationStyle() {
        return applicationStyle;
    }


    public String generateAppName() {
        String appname = getApplicationParameter().PACKAGE_NAME();
        return appname.substring(appname.lastIndexOf(".") + 1, appname.length());
    }

    @Override
    public void onCreate() {
        super.onCreate();
//               Lingver.init(this, getLanguage());
        instance = this;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(new File(getCacheDir(), "image")))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);

        Toasty.Config.getInstance()
                .setToastTypeface(FontManager.T1_Typeface(getApplicationContext()))
                .setTextSize(14).apply();
    }



    @Override
    public void bindFireBase() {
        FirebaseMessaging.getInstance().subscribeToTopic(getApplicationParameter().PACKAGE_NAME());
    }

    @Override
    public String getLanguage() {
        return applicationStyle.getAppLanguage();
    }
}
