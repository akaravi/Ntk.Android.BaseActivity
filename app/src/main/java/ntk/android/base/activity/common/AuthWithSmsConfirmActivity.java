package ntk.android.base.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.Extras;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dialog.CaptchaDialog;
import ntk.android.base.dtomodel.core.AuthUserSignInBySmsDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.event.MessageEvent;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public class AuthWithSmsConfirmActivity extends BaseAuthActivity {
    final int timeSmsTryAgain = 1000 * 60;
    TextInputEditText authEdittext;

    private CountDownTimer Timer;
    private int getNewCodeCount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_smsconfirm_activity);
        //initilize view
        initView();
        //set fonts
        setFont();

    }

    private void initView() {
        //call captcha
        if (getIntent().getExtras() != null && !getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG).equals("")) {
            setSavedCaptcha(getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG), getIntent().getExtras().getString(Extras.EXTRA_FIRST_ARG), getIntent().getExtras().getString(Extras.Extra_THIRD_ARG));
        } else getNewCaptcha();
        Loading = findViewById(R.id.progressOnBtn);
        //hide load on start
        Loading.setVisibility(View.GONE);
        authEdittext = findViewById(R.id.CodeEt);
        //login click listener
        findViewById(R.id.submitBtn).setOnClickListener(v -> ClickBtn());
        //go back click listener
        findViewById(R.id.changeNumberBtn).setOnClickListener(v -> changNumber());
        //countdown click listener
        findViewById(R.id.countDownView).setOnClickListener(v -> ClickCounter());

        //trick to get click
        ((TextInputEditText) findViewById(R.id.nonFocusable)).setMovementMethod(null);
        ((TextInputEditText) findViewById(R.id.nonFocusable)).setKeyListener(null);
        findViewById(R.id.nonFocusable).setOnClickListener(v -> getNewCaptcha());
        //set max length
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        authEdittext.setFilters(filterArray);
        authEdittext.setText("");
        //init timer
        Timer = new CountDownTimer(timeSmsTryAgain, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000) % 60;
                int minutes = (int) ((l / (1000 * 60)) % 60);
                TextView countDownTv = findViewById(R.id.countDownTv);
                countDownTv.setClickable(false);
                countDownTv.setText(String.format("%d:%d", minutes, seconds) + "  " + getString(R.string.plz_wait_recieve_code));
            }

            @Override
            public void onFinish() {
                TextView countDownTv = findViewById(R.id.countDownTv);
                countDownTv.setText(R.string.send_login_code_again);
                countDownTv.setClickable(true);
                Timer.cancel();
            }
        }.start();
    }

    private void changNumber() {
        Intent intent = new Intent(this, AuthWithSmsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }


    private void setFont() {
        //title
        ((TextView) findViewById(R.id.labelRegister)).setTypeface(FontManager.T1_Typeface(this));

        //mobile et
        ((TextInputLayout) findViewById(R.id.textInputLayout)).setTypeface(FontManager.T1_Typeface(this));
        authEdittext.setTypeface(FontManager.T1_Typeface(this));
        //captcha et
        ((TextInputLayout) findViewById(R.id.captchaTextInputLayout)).setTypeface(FontManager.T1_Typeface(this));
        ((TextInputEditText) findViewById(R.id.txtCaptcha)).setTypeface(FontManager.T1_Typeface(this));
        //buttons
        ((MaterialButton) findViewById(R.id.submitBtn)).setTypeface(FontManager.T1_Typeface(this));
        ((MaterialButton) findViewById(R.id.changeNumberBtn)).setTypeface(FontManager.T1_Typeface(this));
    }

    private void ClickBtn() {
        if (authEdittext.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.plz_insert_code, Toast.LENGTH_SHORT).show();
        } else {
            Verify();
        }
    }

    private void Verify() {
        if (AppUtil.isNetworkAvailable(this)) {
            ////show loading
            Loading.setVisibility(View.VISIBLE);
            //disable click
            findViewById(R.id.submitBtn).setEnabled(false);
            AuthUserSignInBySmsDtoModel request = new AuthUserSignInBySmsDtoModel();
            request.CaptchaKey = getCaptchaKey();
            request.CaptchaText = getCaptchaText();
            request.Code = authEdittext.getText().toString();
            request.Mobile = Preferences.with(this).UserInfo().mobile();
            request.SiteId = Preferences.with(this).UserInfo().siteId();
            //call api
            ServiceExecute.execute(new CoreAuthService(this).signInUserBySMS(request)).subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                @Override
                public void onNext(@NonNull ErrorException<TokenInfoModel> response) {
                    //show button
                    Loading.setVisibility(View.GONE);
                    findViewById(R.id.submitBtn).setEnabled(true);
                    if (!response.IsSuccess) {
                        getNewCaptcha();
                        Toasty.warning(AuthWithSmsConfirmActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                        return;
                    }
                    Preferences.with(AuthWithSmsConfirmActivity.this).appVariableInfo().setIsLogin(true);
                    Preferences.with(AuthWithSmsConfirmActivity.this).UserInfo().setUserId(response.Item.UserId);
                    Intent intent = new Intent(AuthWithSmsConfirmActivity.this, NTKApplication.getApplicationStyle().getMainActivity());
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    getNewCaptcha();
                    Loading.setVisibility(View.GONE);
                    findViewById(R.id.submitBtn).setEnabled(true);
                    new GenericErrors().throwableException((error, tryAgain) -> Toasty.warning(AuthWithSmsConfirmActivity.this, error, Toasty.LENGTH_LONG, true).show(), e, () -> {
                    });
                }
            });
        } else {
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
                ServiceExecute.execute(new CoreAuthService(this).signInUserBySMS(request)).subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
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
                        Toasty.success(AuthWithSmsConfirmActivity.this, R.string.code_sent_again).show();
                        Timer.start();
                        if (dialog.isShow()) dialog.dismiss();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (dialog.isShow()) {
                            dialog.lockButton(false);
                            dialog.getCaptcha().getNewCaptcha();
                        }
                        Toasty.warning(AuthWithSmsConfirmActivity.this, R.string.error_raised, Toasty.LENGTH_LONG, true).show();
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
        authEdittext.setText(event.GetMessage());
    }

}
