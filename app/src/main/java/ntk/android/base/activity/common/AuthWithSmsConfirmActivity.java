package ntk.android.base.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dialog.CaptchaDialog;
import ntk.android.base.dtomodel.core.AuthUserSignInBySmsDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.event.MessageEvent;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.base.view.CaptchaView;

public class AuthWithSmsConfirmActivity extends BaseActivity {
    final int timeSmsTryAgain = 1000 * 60;
    ProgressBar Loading;
    EditText Txt;
    List<TextView> Lbls;

    private CountDownTimer Timer;
    private int getNewCodeCount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_auth_confirm_activity);
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
        findViewById(R.id.btnActRegister).setOnClickListener(v -> ClickBtn());
        findViewById(R.id.lblCounterActRegister).setOnClickListener(v -> ClickCounter());
    }

    private void init() {
        ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        Txt.setFilters(filterArray);
        Txt.setText("");
        Txt.setHint("کد اعتبار سنجی");
        ((Button) findViewById(R.id.btnActRegister)).setText("ادامــه");
        Txt.setInputType(InputType.TYPE_CLASS_NUMBER);
        Timer = new CountDownTimer(timeSmsTryAgain, 1000) {
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
            Verify();
        }
    }

    private void Verify() {
        if (AppUtill.isNetworkAvailable(this)) {
            Loading.setVisibility(View.VISIBLE);

            CaptchaView captchaView = (CaptchaView) findViewById(R.id.captchaView);
            AuthUserSignInBySmsDtoModel request = new AuthUserSignInBySmsDtoModel();
            request.CaptchaKey = captchaView.getCaptchaKey();
            request.CaptchaText = captchaView.getCaptchaText();
            request.Code = Txt.getText().toString();
            request.Mobile = Preferences.with(this).UserInfo().mobile();
            request.SiteId = Preferences.with(this).UserInfo().siteId();
//                    request.Password = Txt.getText().toString();
            ServiceExecute.execute(new CoreAuthService(this).signInUserBySMS(request))
                    .subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<TokenInfoModel> response) {
                            Loading.setVisibility(View.GONE);
                            if (!response.IsSuccess) {
                                captchaView.getNewCaptcha();
                                Toasty.warning(AuthWithSmsConfirmActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                                return;
                            }
                            Preferences.with(AuthWithSmsConfirmActivity.this).appVariableInfo().setIsLogin(true);
                            Preferences.with(AuthWithSmsConfirmActivity.this).UserInfo().setUserId(response.Item.UserId);
                            startActivity(new Intent(AuthWithSmsConfirmActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
                            finish();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            captchaView.getNewCaptcha();
                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                            Loading.setVisibility(View.GONE);
                            new GenericErrors().throwableException((error, tryAgain) -> Toasty.warning(AuthWithSmsConfirmActivity.this, error, Toasty.LENGTH_LONG, true).show()
                                    , e, () -> {
                                    });
                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            new GenericErrors().netError((error, tryAgain) -> Toasty.warning(AuthWithSmsConfirmActivity.this, error, Toasty.LENGTH_LONG, true).show(), () -> {
            });
        }

    }

    public void ClickCounter() {
        if (++getNewCodeCount > 2) {
            startActivity(new Intent(this, RegisterMobileActivity.class));
            finish();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            CaptchaDialog dialog = new CaptchaDialog();
            dialog.setCancelable(false);
            dialog.show(fm, "fragment_captcha");
            dialog.setOnclickListener(() -> {
                AuthUserSignInBySmsDtoModel request = new AuthUserSignInBySmsDtoModel();
                request.Mobile = Preferences.with(this).UserInfo().mobile();
                request.CaptchaText = dialog.getCaptcha().getCaptchaText();
                request.CaptchaKey = dialog.getCaptcha().getCaptchaKey();
                dialog.lockButton(true);
                ServiceExecute.execute(new CoreAuthService(this).signInUserBySMS(request))
                        .subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull ErrorException<TokenInfoModel> response) {
                                Loading.setVisibility(View.GONE);
                                if (!response.IsSuccess) {
                                    if (dialog.isShow()) {
                                        dialog.getCaptcha().getNewCaptcha();
                                        dialog.lockButton(false);
                                    }
                                    Toasty.error(AuthWithSmsConfirmActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                    return;
                                }
                                Toasty.success(AuthWithSmsConfirmActivity.this, "کد اعتبار سنجی مجددا ارسال شد").show();
                                Timer.start();
                                if (dialog.isShow())
                                    dialog.dismiss();
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                if (dialog.isShow()) {
                                    dialog.lockButton(false);
                                    dialog.getCaptcha().getNewCaptcha();
                                }
                                Toasty.warning(AuthWithSmsConfirmActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();
                            }
                        });
            });


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void SetMessage(MessageEvent event) {
        Txt.setText(event.GetMessage());
    }

}
