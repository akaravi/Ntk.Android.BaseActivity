package ntk.android.base.activity.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.dtomodel.core.AuthUserSignInBySmsDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.event.MessageEvent;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.base.view.CaptchaView;

public class AuthWithSmsActivity extends BaseActivity {
    ProgressBar Loading;
    EditText Txt;
    private String PhoneNumber = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Boolean Registered = Preferences.with(this).appVariableInfo().isRegistered();
        Boolean islogin = Preferences.with(this).appVariableInfo().isLogin();
        if (Registered && islogin) {
            startActivity(new Intent(AuthWithSmsActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comon_auth_activity);
        initView();
        init();
    }

    private void initView() {


        Loading = findViewById(R.id.progressActRegister);
        Txt = findViewById(R.id.txtActRegister);
        findViewById(R.id.btnActRegister).setOnClickListener(v -> ClickBtn());
        findViewById(R.id.RowNoPhoneActRegister).setOnClickListener(v -> ClickNoPhone());
    }

    private void init() {

        ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
        Loading.setVisibility(View.GONE);
//        Loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(ColorUtils.FETCH_Attr_COLOR(this, R.attr.colorAccent)), PorterDuff.Mode.SRC_IN);
        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((TextView) findViewById(R.id.lblVerificationActRegister)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((Button) findViewById(R.id.btnActRegister)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
    }


    public void ClickBtn() {
        CaptchaView captchaView = (CaptchaView) findViewById(R.id.captchaView);
        if (Txt.getText().toString().isEmpty())
            Toast.makeText(this, "شماره موبایل خود را وارد نمایید", Toast.LENGTH_SHORT).show();
        else if (captchaView.getCaptchaText().isEmpty())
            Toast.makeText(this, "متن کپچا را وارد نمایید", Toast.LENGTH_SHORT).show();
        else {
            PhoneNumber = Txt.getText().toString();
            if (AppUtill.isNetworkAvailable(this)) {
                if (CheckPermission()) {
                    Register();
                } else {
                    ActivityCompat.requestPermissions(AuthWithSmsActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 100);
                }
            } else {
                Toast.makeText(this, "عدم دسترسی به اینترنت", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void Register() {
        if (AppUtill.isNetworkAvailable(this)) {
            Loading.setVisibility(View.VISIBLE);
            findViewById(R.id.cardActRegister).setVisibility(View.GONE);
            AuthUserSignInBySmsDtoModel request = new AuthUserSignInBySmsDtoModel();
            if (PhoneNumber.length() == 0) {
                PhoneNumber = Txt.getText().toString();
            }
            CaptchaView captchaView = findViewById(R.id.captchaView);
            request.Mobile = PhoneNumber;
            request.CaptchaText = captchaView.getCaptchaText();
            request.CaptchaKey = captchaView.getCaptchaKey();

            new CoreAuthService(this).signInUserBySMS(request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull ErrorException<TokenInfoModel> response) {
                            Loading.setVisibility(View.GONE);
                            if (!response.IsSuccess) {
                                ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
                                Toasty.error(AuthWithSmsActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                return;
                            }
                            Preferences.with(AuthWithSmsActivity.this).UserInfo().setMobile(PhoneNumber);
                            startActivity(new Intent(AuthWithSmsActivity.this, ConfirmAuthMobileActivity.class));
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
                            Loading.setVisibility(View.GONE);
                            Toasty.warning(AuthWithSmsActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();
                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                            findViewById(R.id.cardPassRegister).setVisibility(View.VISIBLE);
                            findViewById(R.id.cardRePassRegister).setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            Toasty.warning(this, "عدم دسترسی به اینترنت", Toasty.LENGTH_LONG, true).show();
        }
    }


    public void ClickNoPhone() {
        Preferences.with(this).appVariableInfo().set_registerNotInterested(true);
        startActivity(new Intent(this, NTKApplication.getApplicationStyle().getMainActivity()));
        finish();
    }

    private void Toggle(ImageView toogle, EditText et_input_pass) {
        if (et_input_pass.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")) {
            et_input_pass.setTransformationMethod(new SingleLineTransformationMethod());
            toogle.setImageResource(R.drawable.toggle_hide);
        } else {
            et_input_pass.setTransformationMethod(new PasswordTransformationMethod());
            toogle.setImageResource(R.drawable.toggle_show);
        }

        et_input_pass.setSelection(et_input_pass.getText().length());
    }

    private boolean CheckPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            return checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "عدم اجازه برای گرفتن پیامک کد اعتبار سنجی", Toast.LENGTH_SHORT).show();
                } else {
                    Register();
                }
                break;
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
