package ntk.android.base.activity.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import es.dmoral.toasty.Toasty;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.dialog.PrivacyDialog;
import ntk.android.base.dtomodel.core.AuthUserSignInBySmsDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;
import ntk.android.base.view.CaptchaView;

public class AuthWithSmsActivity extends BaseActivity {
    private static final int REQ_PERMISSION = 100;
    ProgressBar Loading;
    EditText Txt;
    private String PhoneNumber = "";
    String privacy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean islogin = Preferences.with(this).appVariableInfo().isLogin();
        if (islogin) {
            Intent intent = new Intent(AuthWithSmsActivity.this, NTKApplication.getApplicationStyle().getMainActivity());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.comon_auth_activity);
        initView();
        init();
    }

    private void initView() {

        Loading = findViewById(R.id.progressActRegister);
        Txt = findViewById(R.id.txtActRegister);
        findViewById(R.id.btnActRegister).setOnClickListener(v -> ClickBtn());
        if (NTKApplication.getApplicationStyle().show_NotInterested_Btn())
            findViewById(R.id.RowNoPhoneActRegister).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.RowNoPhoneActRegister).setVisibility(View.GONE);
        findViewById(R.id.RowNoPhoneActRegister).setOnClickListener(v -> ClickNoPhone());
        privacy = new Preferences.Builder(this).appVariableInfo().aboutUs().AboutUsPrivacyPolicyHtmlBody;
        if (privacy.equalsIgnoreCase(""))
            findViewById(R.id.privacy).setVisibility(View.GONE);
        else {
            findViewById(R.id.privacy).setVisibility(View.VISIBLE);
            findViewById(R.id.privacy).setOnClickListener(view -> PrivacyDialog.showDialog(getSupportFragmentManager(), privacy));
        }
    }

    private void init() {

        ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
        Loading.setVisibility(View.GONE);
//        Loading.getIndeterminateDrawable().setColorFilter(getResources().getColor(ColorUtils.FETCH_Attr_COLOR(this, R.attr.colorAccent)), PorterDuff.Mode.SRC_IN);
        Txt.setTypeface(FontManager.T1_Typeface(this));
        ((TextView) findViewById(R.id.privacy)).setTypeface(FontManager.T1_Typeface(this));
        ((TextView) findViewById(R.id.lblVerificationActRegister)).setTypeface(FontManager.T1_Typeface(this));
        ((Button) findViewById(R.id.btnActRegister)).setTypeface(FontManager.T1_Typeface(this));
    }


    public void ClickBtn() {
        CaptchaView captchaView = (CaptchaView) findViewById(R.id.captchaView);
        if (Txt.getText().toString().isEmpty())
            Toast.makeText(this, R.string.plz_insert_num, Toast.LENGTH_SHORT).show();
        else if (!Txt.getText().toString().startsWith("09") || Txt.getText().toString().length() != 11) {
            Toasty.warning(this, R.string.plz_insert_mobile_correct, Toasty.LENGTH_LONG, true).show();
        } else if (captchaView.getCaptchaText().isEmpty())
            Toast.makeText(this, R.string.plz_insert_capcha, Toast.LENGTH_SHORT).show();
        else {
            PhoneNumber = Txt.getText().toString();
            if (CheckPermission()) {
                Register();
            } else {
                ActivityCompat.requestPermissions(AuthWithSmsActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, REQ_PERMISSION);
            }
        }
    }


    private void Register() {
        if (AppUtil.isNetworkAvailable(this)) {
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

            ServiceExecute.execute(new CoreAuthService(this).signInUserBySMS(request))
                    .subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull ErrorException<TokenInfoModel> response) {
                            Loading.setVisibility(View.GONE);
                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                            if (!response.IsSuccess) {
                                ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
                                Toasty.error(AuthWithSmsActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                return;
                            }
                            Preferences.with(AuthWithSmsActivity.this).UserInfo().setMobile(PhoneNumber);
                            Intent intent = new Intent(AuthWithSmsActivity.this, AuthWithSmsConfirmActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            ((CaptchaView) findViewById(R.id.captchaView)).getNewCaptcha();
                            findViewById(R.id.cardActRegister).setVisibility(View.VISIBLE);
                            Loading.setVisibility(View.GONE);
                            new GenericErrors().throwableException((error, tryAgain) -> Toasty.warning(AuthWithSmsActivity.this, error, Toasty.LENGTH_LONG, true).show()
                                    , e, () -> {
                                    });
                        }
                    });
        } else {
            Loading.setVisibility(View.GONE);
            new GenericErrors().netError((error, tryAgain) -> Toasty.warning(AuthWithSmsActivity.this, error, Toasty.LENGTH_LONG, true).show(), () -> {
            });
        }
    }


    public void ClickNoPhone() {
        Preferences.with(this).appVariableInfo().set_registerNotInterested(true);
        Intent intent = new Intent(this, NTKApplication.getApplicationStyle().getMainActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
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
                    Toast.makeText(this, R.string.plz_insert_code_login, Toast.LENGTH_SHORT).show();
                    Register();
                } else {
                    Register();
                }
                break;
        }
    }

}
