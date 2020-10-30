package ntk.android.base;

import android.content.Context;


import androidx.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import es.dmoral.toasty.Toasty;
import ntk.android.base.utill.FontManager;


/**
 *
 */
public abstract class NTKBASEApplication extends MultiDexApplication implements ApplicationParamProvider {
    public static boolean Inbox = false;
    private static NTKBASEApplication instance;
     static ApplicationStyle applicationStyle;

    public static NTKBASEApplication get() {
        return instance;
    }

    public static ApplicationStyle getApplicationStyle() {
        return applicationStyle;
    }

    /**
     * when attach process to system , init Bug reporting
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    public static Context getAppContext() {
        return instance.getApplicationContext();
    }



    public String generateAppName() {
        String appname = getApplicationParameter().PACKAGE_NAME();
        return appname.substring(appname.lastIndexOf(".") + 1, appname.length());
    }
    @Override
    public void onCreate() {
        instance=this;
        super.onCreate();
        if (!new File(getCacheDir(), "image").exists()) {
            new File(getCacheDir(), "image").mkdirs();
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new UnlimitedDiskCache(new File(getCacheDir(), "image")))
                .diskCacheFileNameGenerator(imageUri -> {
                    String[] Url = imageUri.split("/");
                    return Url[Url.length];
                })
                .build();
        ImageLoader.getInstance().init(config);

        Toasty.Config.getInstance()
                .setToastTypeface(FontManager.GetTypeface(getApplicationContext(), FontManager.IranSans))
                .setTextSize(14).apply();
    }

//   public abstract ApplicationParameter getApplicationParameter();
}
