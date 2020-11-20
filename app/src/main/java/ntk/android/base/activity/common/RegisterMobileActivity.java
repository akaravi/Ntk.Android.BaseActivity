package ntk.android.base.activity.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.dtomodel.core.AuthUserSignUpModel;
import ntk.android.base.entitymodel.base.CaptchaModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.core.CoreUserModel;
import ntk.android.base.event.MessageEvent;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public class RegisterMobileActivity extends AppCompatActivity {

    ProgressBar Loading;
    EditText Txt;
    EditText CaptchaTxt;
    EditText passTxt;
    EditText rePassTxt;

    List<TextView> Lbls;

    private CountDownTimer Timer;
    private String PhoneNumber = "";
    CaptchaModel captcha;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Boolean Registered = Preferences.with(this).appVariableInfo().isRegistered();
        Boolean islogin = Preferences.with(this).appVariableInfo().isLogin();
        if (Registered&&islogin) {
            startActivity(new Intent(RegisterMobileActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comon_register_activity);
        initView();
        init();
    }

    private void initView() {

        Lbls = new ArrayList() {
            {
                add(findViewById(R.id.lblVerificationActRegister));
                add(findViewById(R.id.lblNoPhoneActRegister));
                add(findViewById(R.id.txtCaptcha));
            }
        };

        Loading = findViewById(R.id.progressActRegister);
        Txt = findViewById(R.id.txtActRegister);
        CaptchaTxt = findViewById(R.id.txtCaptcha);
        passTxt = findViewById(R.id.txtpass);
        rePassTxt = findViewById(R.id.txtRepass);
        findViewById(R.id.btnActRegister).setOnClickListener(v -> ClickBtn());
        findViewById(R.id.RowNoPhoneActRegister).setOnClickListener(v -> ClickNoPhone());
        findViewById(R.id.imgCaptcha).setOnClickListener(v -> callCaptchaApi());
        findViewById(R.id.rowLogin).setOnClickListener(v -> {
            startActivity(new Intent(RegisterMobileActivity.this, LoginMobileActivity.class));
            finish();
        });
    }

    private void init() {

        callCaptchaApi();
        Loading.setVisibility(View.GONE);
//        Loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(ColorUtils.FETCH_Attr_COLOR(this, R.attr.colorAccent)), PorterDuff.Mode.SRC_IN);
        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        CaptchaTxt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        passTxt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        rePassTxt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(0).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(1).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbls.get(2).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        ((Button) findViewById(R.id.btnActRegister)).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        findViewById(R.id.passToggle).setOnClickListener(v -> Toggle(findViewById(R.id.passToggle), findViewById(R.id.txtpass)));
        findViewById(R.id.repassToggle).setOnClickListener(v -> Toggle(findViewById(R.id.repassToggle), findViewById(R.id.txtRepass)));
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

    public void ClickBtn() {
        if (Txt.getText().toString().isEmpty())
            Toast.makeText(this, "شماره موبایل خود را وارد نمایید", Toast.LENGTH_SHORT).show();
        else if (passTxt.getText().toString().trim().equalsIgnoreCase(""))
            Toast.makeText(this, "کلمه عبور را وارد نمایید", Toast.LENGTH_SHORT).show();
        else if (rePassTxt.getText().toString().trim().equalsIgnoreCase(""))
            Toast.makeText(this, "تکرار کلمه عبور را وارد نمایید", Toast.LENGTH_SHORT).show();
        else if (!passTxt.getText().toString().equalsIgnoreCase(rePassTxt.getText().toString()))
            Toast.makeText(this, "مقادیر ورودی کلمه عبور برابر نیستند", Toast.LENGTH_SHORT).show();
        else if (CaptchaTxt.getText().toString().isEmpty())
            Toast.makeText(this, "متن کپچا را وارد نمایید", Toast.LENGTH_SHORT).show();
        else {
            PhoneNumber = Txt.getText().toString();
            if (AppUtill.isNetworkAvailable(this)) {
                if (CheckPermission()) {
                    Register();
                } else {
                    ActivityCompat.requestPermissions(RegisterMobileActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 100);
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
            AuthUserSignUpModel request = new AuthUserSignUpModel();
            if (PhoneNumber.length() == 0) {
                PhoneNumber = Txt.getText().toString();
            }
            request.Mobile = PhoneNumber;
            request.CaptchaText = CaptchaTxt.getText().toString();
            request.Password = passTxt.getText().toString();
            if (captcha != null && captcha.Key != null)
                request.CaptchaKey = captcha.Key;

            new CoreAuthService(this).signUpUser(request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<ErrorException<CoreUserModel>>() {
                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull ErrorException<CoreUserModel> response) {
                            Loading.setVisibility(View.GONE);
                            if (!response.IsSuccess) {
                                Toasty.error(RegisterMobileActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                return;
                            }
                            startActivity(new Intent(RegisterMobileActivity.this, ConfirmMobileActivity.class));
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            callCaptchaApi();
                            Loading.setVisibility(View.GONE);
                            Toasty.warning(RegisterMobileActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();
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
