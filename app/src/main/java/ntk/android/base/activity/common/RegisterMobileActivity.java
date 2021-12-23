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

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dtomodel.core.AuthUserSignUpModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.core.CoreUserModel;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.base.view.CaptchaView;

public class RegisterMobileActivity extends BaseActivity {

    private static final int REQ_PERMISSION = 100;
    ProgressBar Loading;
    EditText Txt;
    EditText passTxt;
    EditText rePassTxt;

    List<TextView> Lbls;

    private String PhoneNumber = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Boolean Registered = false;//Preferences.with(this).appVariableInfo().isRegistered();
        Boolean islogin = Preferences.with(this).appVariableInfo().isLogin();
        if (Registered && islogin) {
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
        passTxt = findViewById(R.id.txtpass);
        rePassTxt = findViewById(R.id.txtRepass);
        findViewById(R.id.btnActRegister).setOnClickListener(v -> ClickBtn());
        findViewById(R.id.RowNoPhoneActRegister).setOnClickListener(v -> ClickNoPhone());
        findViewById(R.id.rowLogin).setOnClickListener(v -> {
            startActivity(new Intent(RegisterMobileActivity.this, LoginMobileActivity.class));
            finish();
        });
    }

    private void init() {

        ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
        Loading.setVisibility(View.GONE);
//        Loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(ColorUtils.FETCH_Attr_COLOR(this, R.attr.colorAccent)), PorterDuff.Mode.SRC_IN);
        Txt.setTypeface(FontManager.T1_Typeface(this));
        passTxt.setTypeface(FontManager.T1_Typeface(this));
        rePassTxt.setTypeface(FontManager.T1_Typeface(this));
        Lbls.get(0).setTypeface(FontManager.T1_Typeface(this));
        Lbls.get(1).setTypeface(FontManager.T1_Typeface(this));
        Lbls.get(2).setTypeface(FontManager.T1_Typeface(this));
        ((Button) findViewById(R.id.btnActRegister)).setTypeface(FontManager.T1_Typeface(this));
        findViewById(R.id.passToggle).setOnClickListener(v -> Toggle(findViewById(R.id.passToggle), findViewById(R.id.txtpass)));
        findViewById(R.id.repassToggle).setOnClickListener(v -> Toggle(findViewById(R.id.repassToggle), findViewById(R.id.txtRepass)));
    }


    public void ClickBtn() {
        if (Txt.getText().toString().isEmpty())
            Toast.makeText(this, R.string.plz_insert_num, Toast.LENGTH_SHORT).show();
        else if (passTxt.getText().toString().trim().equalsIgnoreCase(""))
            Toast.makeText(this,  R.string.plz_insert_password, Toast.LENGTH_SHORT).show();
        else if (rePassTxt.getText().toString().trim().equalsIgnoreCase(""))
            Toast.makeText(this,  R.string.plz_insert_password, Toast.LENGTH_SHORT).show();
        else if (!passTxt.getText().toString().equalsIgnoreCase(rePassTxt.getText().toString()))
            Toast.makeText(this, R.string.passowrd_not_same, Toast.LENGTH_SHORT).show();
        else if (((CaptchaView) findViewById(R.id.captchaView)).getCaptchaText().isEmpty())
            Toast.makeText(this,  R.string.plz_insert_capcha, Toast.LENGTH_SHORT).show();
        else {
            PhoneNumber = Txt.getText().toString();
            if (AppUtil.isNetworkAvailable(this)) {
                if (CheckPermission()) {
                    Register();
                } else {
                    ActivityCompat.requestPermissions(RegisterMobileActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, REQ_PERMISSION);
                }
            } else {
                Toast.makeText(this, R.string.per_no_net, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void Register() {
        if (AppUtil.isNetworkAvailable(this)) {
            Loading.setVisibility(View.VISIBLE);
            findViewById(R.id.cardActRegister).setVisibility(View.GONE);
            AuthUserSignUpModel request = new AuthUserSignUpModel();
            if (PhoneNumber.length() == 0) {
                PhoneNumber = Txt.getText().toString();
            }
            CaptchaView captchaView = (CaptchaView) findViewById(R.id.captchaView);
            request.Mobile = PhoneNumber;
            request.CaptchaText = captchaView.getCaptchaText();
            request.CaptchaKey = captchaView.getCaptchaKey();
            request.Password = passTxt.getText().toString();
            ServiceExecute.execute( new CoreAuthService(this).signUpUser(request))
                    .subscribe(new NtkObserver<ErrorException<CoreUserModel>>() {
                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull ErrorException<CoreUserModel> response) {
                            Loading.setVisibility(View.GONE);
                            if (!response.IsSuccess) {
                                ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
                                Toasty.error(RegisterMobileActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                return;
                            }
                            startActivity(new Intent(RegisterMobileActivity.this, RegisterMobileConfirmActivity.class));
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
                            Loading.setVisibility(View.GONE);
                            Toasty.warning(RegisterMobileActivity.this, R.string.error_raised, Toasty.LENGTH_LONG, true).show();
                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                            findViewById(R.id.cardPassRegister).setVisibility(View.VISIBLE);
                            findViewById(R.id.cardRePassRegister).setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            Toasty.warning(this, R.string.per_no_net, Toasty.LENGTH_LONG, true).show();
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
            case REQ_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,  R.string.plz_insert_code_login, Toast.LENGTH_SHORT).show();
                    Register();
                } else {
                    Register();
                }
                break;
        }
    }
}
