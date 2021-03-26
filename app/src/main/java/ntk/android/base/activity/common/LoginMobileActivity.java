package ntk.android.base.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.nostra13.universalimageloader.core.ImageLoader;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dialog.ForgetPassDialog;
import ntk.android.base.dtomodel.core.AuthUserSignInModel;
import ntk.android.base.entitymodel.base.CaptchaModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public class LoginMobileActivity extends BaseActivity {

    ProgressBar Loading;
    EditText Txt;
    EditText CaptchaTxt;
    EditText passTxt;

    CaptchaModel captcha;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Boolean login = Preferences.with(this).appVariableInfo().isLogin();
        if (login) {
            startActivity(new Intent(LoginMobileActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_login_with_mobile);
        initView();
        init();

    }

    private void init() {
        callCaptchaApi();
        Loading.setVisibility(View.GONE);
        Txt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        CaptchaTxt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        passTxt.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));

    }

    private void initView() {
        Loading = findViewById(R.id.progressActRegister);
        Txt = findViewById(R.id.txtActRegister);
        CaptchaTxt = findViewById(R.id.txtCaptcha);
        passTxt = findViewById(R.id.txtpass);
        findViewById(R.id.btnActRegister).setOnClickListener(v -> signIn());
        findViewById(R.id.imgCaptcha).setOnClickListener(v -> callCaptchaApi());
        findViewById(R.id.passToggle).setOnClickListener(v -> Toggle(findViewById(R.id.passToggle), findViewById(R.id.txtpass)));
        findViewById(R.id.RowForgotPasswordActLogin).setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            ForgetPassDialog editNameDialogFragment = new ForgetPassDialog();
            editNameDialogFragment.show(fm, "fragment_edit_name");
        });
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

    private void callCaptchaApi() {
        ServiceExecute.execute(new CoreAuthService(this).getCaptcha())
                .subscribe(new NtkObserver<ErrorException<CaptchaModel>>() {
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

    private void signIn() {
        if (Txt.getText().toString().isEmpty())
            Toast.makeText(this, R.string.plz_insert_num, Toast.LENGTH_SHORT).show();
        else if (passTxt.getText().toString().trim().equalsIgnoreCase(""))
            Toast.makeText(this, R.string.plz_insert_password, Toast.LENGTH_SHORT).show();
        else if (CaptchaTxt.getText().toString().isEmpty())
            Toast.makeText(this,  R.string.plz_insert_capcha, Toast.LENGTH_SHORT).show();
        else {
            AuthUserSignInModel req = new AuthUserSignInModel();
            req.Mobile = Txt.getText().toString();
            req.Password = passTxt.getText().toString();
            req.captchaText = CaptchaTxt.getText().toString();
            req.SiteId = Preferences.with(LoginMobileActivity.this).UserInfo().siteId();
            if (captcha != null)
                req.captchaKey = captcha.Key;
            ServiceExecute.execute(new CoreAuthService(this).signInUser(req))
                    .subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<TokenInfoModel> response) {
                            Loading.setVisibility(View.GONE);
                            if (!response.IsSuccess) {
                                Toasty.error(LoginMobileActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                return;
                            }
                            Preferences.with(LoginMobileActivity.this).appVariableInfo().setIsLogin(true);
                            startActivity(new Intent(LoginMobileActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            callCaptchaApi();
                            Loading.setVisibility(View.GONE);
                            Toasty.warning(LoginMobileActivity.this, e.getMessage(), Toasty.LENGTH_LONG, true).show();

                        }
                    });
        }
    }
}
