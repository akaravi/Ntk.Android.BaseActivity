package ntk.android.base.view;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;

import java.util.UUID;

import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.CaptchaModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.services.core.CoreAuthService;

public class CaptchaViewVer2 {
    View view;
    private CaptchaModel captcha;


    public void bind(View inflate) {
        this.view=inflate;
        view.findViewById(R.id.imgCaptcha).setOnClickListener(v -> getNewCaptcha());
    }

    public void getNewCaptcha() {
        ServiceExecute.execute(new CoreAuthService(view.getContext()).getCaptcha())
                .subscribe(new NtkObserver<ErrorException<CaptchaModel>>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ErrorException<CaptchaModel> captchaResponce) {
                        ((EditText) view.findViewById(R.id.txtCaptcha)).setText("");
                        if (captchaResponce.IsSuccess) {
                            captcha = captchaResponce.Item;
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {

                                   Glide.with(NTKApplication.get())
                                            .load(captchaResponce.Item.Image).placeholder(R.drawable.captcha_holder).
                                            apply(new RequestOptions().signature(new ObjectKey(UUID.randomUUID().toString())))
                                           .addListener(new RequestListener<Drawable>() {
                                               @Override
                                               public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                   return false;
                                               }

                                               @Override
                                               public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                   ((ImageView) view.findViewById(R.id.imgCaptcha)).setImageDrawable(resource);
                                                   return false;
                                               }
                                           }).into(((ImageView) view.findViewById(R.id.imgCaptcha)));
//                                }
//                            });

//                            ((ImageView) view.findViewById(R.id.imgCaptcha)).post(new Runnable() {
//                                public void run() {
//                                    Glide.with(view.getContext())
//                                            .load(captcha.Image).
//                                            apply(new RequestOptions().signature(new ObjectKey(System.currentTimeMillis()+"signature string")))
//                                            .into(((ImageView) view.findViewById(R.id.imgCaptcha)));
//                                }
//                            });
                        } else {
                            ((ImageView) view.findViewById(R.id.imgCaptcha)).setImageResource(R.drawable.error_captcha);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        ((ImageView) view.findViewById(R.id.imgCaptcha)).setImageResource(R.drawable.error_captcha);

                    }


                });
    }


    public String getCaptchaText() {
        return ((EditText) view.findViewById(R.id.txtCaptcha)).getText().toString();
    }

    public String getCaptchaKey() {
        if (captcha != null)
            return captcha.Key;
        return "";
    }

}
