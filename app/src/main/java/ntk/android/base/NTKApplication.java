package ntk.android.base;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import es.dmoral.toasty.Toasty;
import ntk.android.base.utill.FontManager;


/**
 *
 */
public abstract class NTKApplication extends BaseNtkApplication implements ApplicationParamProvider {
    public static boolean Inbox = false;
    //@Notify please note that not change this value to True

    static ApplicationStyle applicationStyle;



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
        instance=this;
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

    @Override
    public void bindFireBase() {
        FirebaseMessaging.getInstance().subscribeToTopic(getApplicationParameter().PACKAGE_NAME());
    }
//   public abstract ApplicationParameter getApplicationParameter();
}
