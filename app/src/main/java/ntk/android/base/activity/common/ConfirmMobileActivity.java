package ntk.android.base.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.dtomodel.core.AuthEmailConfirmDtoModel;
import ntk.android.base.dtomodel.core.AuthMobileConfirmDtoModel;
import ntk.android.base.entitymodel.base.CaptchaModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.entitymodel.core.CoreUserModel;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.prefrense.Preferences;

public class ConfirmMobileActivity extends BaseActivity {
    ProgressBar Loading;
    EditText Txt;
    EditText CaptchaTxt;

    List<TextView> Lbls;

    private CountDownTimer Timer;

    CaptchaModel captcha;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_mobile_confirm_activity);
        initView();
        init();

    }

    private void initView() {

        Lbls = new ArrayList() {
            {
                add(findViewById(R.id.lblVerificationActRegister));

                add(findViewById(R.id.lblCounterActRegister));
                add(findViewById(R.id.txtCaptcha));
            }
        };

        Loading = findViewById(R.id.progressActRegister);
        Txt = findViewById(R.id.txtActRegister);
        CaptchaTxt = findViewById(R.id.txtCaptcha);
        findViewById(R.id.btnActRegister).setOnClickListener(v -> ClickBtn());
        findViewById(R.id.lblCounterActRegister).setOnClickListener(v -> ClickCounter());
        findViewById(R.id.imgCaptcha).setOnClickListener(v -> callCaptchaApi());
    }

    private void init() {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        Txt.setFilters(filterArray);
        Txt.setText("");
        Txt.setHint("کد اعتبار سنجی");
        ((Button) findViewById(R.id.btnActRegister)).setText("ادامــه");
        Txt.setInputType(InputType.TYPE_CLASS_NUMBER);
        Timer = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000) % 60;
                int minutes = (int) ((l / (1000 * 60)) % 60);
                Lbls.get(1).setClickable(false);
                Lbls.get(1).setText(" لطفا منتظر دریافت کد اعتبار سنجی بمانید " + String.format("%d:%d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                Lbls.get(1).setText("ارسال مجدد کد اعتبار سنجی ");
                Lbls.get(1).setClickable(true);
                Timer.cancel();
            }
        }.start();
    }

    private void ClickBtn() {
        if (Txt.getText().toString().isEmpty()) {
            Toast.makeText(this, "کد اعتبار سنجی را وارد نمایید", Toast.LENGTH_SHORT).show();
        } else {
            if (AppUtill.isNetworkAvailable(this)) {
                Verify();
            } else {
                Toast.makeText(this, "عدم دسترسی به اینترنت", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Verify() {
        if (AppUtill.isNetworkAvailable(this)) {
            Loading.setVisibility(View.VISIBLE);


            AuthMobileConfirmDtoModel request = new AuthMobileConfirmDtoModel();

            if (captcha != null && captcha.Key != null)
                request.CaptchaKey = captcha.Key;
            request.Mobile = Preferences.with(this).UserInfo().mobile();
            request.LinkUserId = Preferences.with(this).UserInfo().linkUserId();
//                    request.Password = Txt.getText().toString();
            new CoreAuthService(this).confirmMobile(request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {
                        @Override
                        public void onNext(@NonNull ErrorExceptionBase response) {
                            Loading.setVisibility(View.GONE);
                            if (!response.IsSuccess) {
                                Toasty.warning(ConfirmMobileActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                                return;
                            }
//                            EasyPreference.with(RegisterActivity.this).addLong("UserId", response.Item.UserId);
//                            EasyPreference.with(RegisterActivity.this).addLong("MemberUserId", response.Item.MemberId);
//                            EasyPreference.with(RegisterActivity.this).addLong("SiteId", response.Item.SiteId);
//                           Preferences.with(this).UserInfo().setRegisterd(true);

                            startActivity(new Intent(ConfirmMobileActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
                            finish();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
//                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
//                            findViewById(R.id.cardPassRegister).setVisibility(View.VISIBLE);
//                            findViewById(R.id.cardRePassRegister).setVisibility(View.VISIBLE);
                            Loading.setVisibility(View.GONE);
                            Toasty.warning(ConfirmMobileActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();
//
                        }
                    });
//            request.Code = Txt.getText().toString(); todo

//            Observable<CoreUserResponse> observable = iCore.RegisterWithMobile(headers, request);
//            observable.observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new Observer<CoreUserResponse>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onNext(CoreUserResponse response) {
//                            Loading.setVisibility(View.GONE);
//
//
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
//                            Loading.setVisibility(View.GONE);
//                            Toasty.warning(ntk.android.base.activity.common.RegisterActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    });
        } else {
            Loading.setVisibility(View.GONE);
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }

    }

    public void ClickCounter() {
        Verify();
    }

    private void callCaptchaApi() {
        new CoreAuthService(this).getCaptcha().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new NtkObserver<ErrorException<CaptchaModel>>() {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull ErrorException<CaptchaModel> captchaResponce) {
                if (captchaResponce.IsSuccess) {
                    ImageLoader.getInstance().displayImage(captchaResponce.Item.Image, (ImageView) findViewById(R.id.imgCaptcha));
                    captcha = captchaResponce.Item;
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }


        });
    }
}
