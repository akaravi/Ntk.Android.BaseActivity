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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dtomodel.core.AuthMobileConfirmDtoModel;
import ntk.android.base.entitymodel.base.ErrorExceptionBase;
import ntk.android.base.event.MessageEvent;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.base.view.CaptchaView;

public class RegisterMobileConfirmActivity extends BaseActivity {
    ProgressBar Loading;
    EditText Txt;
    List<TextView> Lbls;

    private CountDownTimer Timer;
    private final long timeSmsTryAgain = 1000 * 60 * 3;

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

        findViewById(R.id.btnActRegister).setOnClickListener(v -> ClickBtn());
        findViewById(R.id.lblCounterActRegister).setOnClickListener(v -> ClickCounter());
    }

    private void init() {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        Txt.setFilters(filterArray);
        Txt.setText("");
        Txt.setHint(R.string.login_code);
        ((Button) findViewById(R.id.btnActRegister)).setText(R.string.Continue_string);
        Txt.setInputType(InputType.TYPE_CLASS_NUMBER);
        Timer = new CountDownTimer(timeSmsTryAgain, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000) % 60;
                int minutes = (int) ((l / (1000 * 60)) % 60);
                Lbls.get(1).setClickable(false);
                Lbls.get(1).setText(getString(R.string.plz_wait_recieve_code) + String.format("%d:%d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                Lbls.get(1).setText(R.string.send_login_code_again);
                Lbls.get(1).setClickable(true);
                Timer.cancel();
            }
        }.start();
    }

    private void ClickBtn() {
        if (Txt.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.plz_insert_code, Toast.LENGTH_SHORT).show();
        } else {
            Verify();

        }
    }

    private void Verify() {
        if (AppUtil.isNetworkAvailable(this)) {
            Loading.setVisibility(View.VISIBLE);

            CaptchaView captchaView = (CaptchaView) findViewById(R.id.captchaView);
            AuthMobileConfirmDtoModel request = new AuthMobileConfirmDtoModel();

            request.CaptchaText = captchaView.getCaptchaText();
            request.CaptchaKey = captchaView.getCaptchaKey();
            request.Mobile = Preferences.with(this).UserInfo().mobile();
            request.Code= Txt.getText().toString();
            ServiceExecute.execute(new CoreAuthService(this).confirmMobile(request))
                    .subscribe(new NtkObserver<ErrorExceptionBase>() {
                        @Override
                        public void onNext(@NonNull ErrorExceptionBase response) {
                            Loading.setVisibility(View.GONE);
                            if (!response.IsSuccess) {
                                captchaView.getNewCaptcha();
                                Toasty.warning(RegisterMobileConfirmActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                                Loading.setVisibility(View.GONE);  return;
                            }
//                            EasyPreference.with(RegisterActivity.this).addLong("UserId", response.Item.UserId);
//                            EasyPreference.with(RegisterActivity.this).addLong("MemberUserId", response.Item.MemberId);
//                            EasyPreference.with(RegisterActivity.this).addLong("SiteId", response.Item.SiteId);
//                           Preferences.with(this).UserInfo().setRegisterd(true);

                            startActivity(new Intent(RegisterMobileConfirmActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
                            finish();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            captchaView.getNewCaptcha();
                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                            Loading.setVisibility(View.GONE);
                            Toasty.warning(RegisterMobileConfirmActivity.this, R.string.error_raised, Toasty.LENGTH_LONG, true).show();
//
                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            Toasty.warning(this, R.string.per_no_net, Toasty.LENGTH_LONG, true).show();
        }

    }

    public void ClickCounter() {
        Verify();
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
