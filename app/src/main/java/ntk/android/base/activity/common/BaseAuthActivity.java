package ntk.android.base.activity.common;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.UUID;

import io.reactivex.annotations.NonNull;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.CaptchaModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.services.core.CoreAuthService;

public class BaseAuthActivity extends BaseActivity {
    protected View Loading;
    private CaptchaModel captcha;

    public String getCaptchaText() {
        return ((EditText) findViewById(R.id.txtCaptcha)).getText().toString();
    }

    public String getCaptchaKey() {
        if (captcha != null) return captcha.Key;
        return "";
    }
    public String getCaptchaImageUrl() {
        if (captcha != null) return captcha.Image;
        return "";
    }

    public void getNewCaptcha() {
        ServiceExecute.execute(new CoreAuthService(this).getCaptcha()).subscribe(new NtkObserver<ErrorException<CaptchaModel>>() {
            @Override
            public void onNext(@NonNull ErrorException<CaptchaModel> captchaResponce) {
                ((EditText) findViewById(R.id.txtCaptcha)).setText("");
                if (captchaResponce.IsSuccess) {
                    captcha = captchaResponce.Item;
                    Glide.with(NTKApplication.get()).load(captchaResponce.Item.Image).placeholder(R.drawable.captcha_holder).apply(new RequestOptions().signature(new ObjectKey(UUID.randomUUID().toString()))).fitCenter().into(((ImageView) findViewById(R.id.imgCaptcha)));
                } else {
                    ((ImageView) findViewById(R.id.imgCaptcha)).setImageResource(R.drawable.error_captcha);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                ((ImageView) findViewById(R.id.imgCaptcha)).setImageResource(R.drawable.error_captcha);

            }


        });
    }

    public void setSavedCaptcha(String key, String text, String image) {
        captcha = new CaptchaModel();
        captcha.Key = key;
        captcha.Image = image;
        ((EditText) findViewById(R.id.txtCaptcha)).setText(text);
        Glide.with(NTKApplication.get()).load(captcha.Image).placeholder(R.drawable.captcha_holder).apply(new RequestOptions().signature(new ObjectKey(UUID.randomUUID().toString()))).fitCenter().into(((ImageView) findViewById(R.id.imgCaptcha)));

    }

}
