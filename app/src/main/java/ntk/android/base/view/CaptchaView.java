package ntk.android.base.view;

import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ntk.android.base.R;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.CaptchaModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.services.core.CoreAuthService;

public class CaptchaView extends FrameLayout {

    private CaptchaModel captcha;

    public CaptchaView(Context context) {
        this(context, null);
    }

    public CaptchaView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.inflate(R.layout.sub_base_captcha, this);
        inflate.findViewById(R.id.imgCaptcha).setOnClickListener(v -> getNewCaptcha());

    }

    public void getNewCaptcha() {
        ServiceExecute.execute(new CoreAuthService(getContext()).getCaptcha())
                .subscribe(new NtkObserver<ErrorException<CaptchaModel>>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ErrorException<CaptchaModel> captchaResponce) {
                        ((EditText) findViewById(R.id.txtCaptcha)).setText("");
                        ((ImageView) findViewById(R.id.imgCaptcha)).setImageDrawable(null);
                        if (captchaResponce.IsSuccess) {
                            captcha = captchaResponce.Item;
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Glide.with(getContext())
//                                            .load(captcha.Image)
//                                            .into( ((ImageView) findViewById(R.id.imgCaptcha)));
//                                }
//                            });
                            if (Looper.getMainLooper().getThread() == Thread.currentThread())
                                Log.d("YES", "YESS00");
                            ((ImageView) findViewById(R.id.imgCaptcha)).post(new Runnable() {
                                public void run() {
                                    Glide.with(getContext())
                                            .load(captcha.Image)
                                            .into(((ImageView) findViewById(R.id.imgCaptcha)));
                                }
                            });
                        } else {
                            ((ImageView) findViewById(R.id.imgCaptcha)).setImageResource(R.drawable.error_captcha);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        ((ImageView) findViewById(R.id.imgCaptcha)).setImageResource(R.drawable.error_captcha);

                    }


                });
    }


    public String getCaptchaText() {
        return ((EditText) findViewById(R.id.txtCaptcha)).getText().toString();
    }

    public String getCaptchaKey() {
        if (captcha != null)
            return captcha.Key;
        return "";
    }

}