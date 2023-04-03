package ntk.android.base.activity.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import es.dmoral.toasty.Toasty;
import ntk.android.base.Extras;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
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

public class AuthWithSmsActivity extends BaseAuthActivity {
    private static final int REQ_PERMISSION = 100;

    TextInputEditText mobileEditText;
    private String PhoneNumber = "";
    String privacy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check if login
        boolean isLogin = Preferences.with(this).appVariableInfo().isLogin();
        if (isLogin) {
            Intent intent = new Intent(AuthWithSmsActivity.this, NTKApplication.getApplicationStyle().getMainActivity());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.auth_sms_activity);
        //initilize view
        initView();
        //set fonts
        setFont();
    }

    private void initView() {
        //call captcha
        getNewCaptcha();
        Loading = findViewById(R.id.progressOnBtn);
        //hide load on start
        Loading.setVisibility(View.GONE);
        mobileEditText = findViewById(R.id.mobileEt);
        //login click listener
        findViewById(R.id.submitBtn).setOnClickListener(v -> ClickBtn());
        //not interested btn
        findViewById(R.id.notInterestedBtn).setOnClickListener(v -> ClickNoPhone());
        // check privacy
        privacy = new Preferences.Builder(this).appVariableInfo().aboutUs().AboutUsPrivacyPolicyHtmlBody;
        if (privacy.equalsIgnoreCase(""))
            findViewById(R.id.privacyView).setVisibility(View.GONE);
        else {
            findViewById(R.id.privacyView).setVisibility(View.VISIBLE);
            findViewById(R.id.privacyView).setOnClickListener(view -> PrivacyDialog.showDialog(getSupportFragmentManager(), privacy));
        }

        //trick to get click
        ((TextInputEditText) findViewById(R.id.nonFocusable)).setMovementMethod(null);
        ((TextInputEditText) findViewById(R.id.nonFocusable)).setKeyListener(null);
        findViewById(R.id.nonFocusable).setOnClickListener(v -> getNewCaptcha());

    }

    private void setFont() {
        //title
        ((TextView) findViewById(R.id.labelRegister)).setTypeface(FontManager.T1_Typeface(this));
        //mobile et
        ((TextInputLayout) findViewById(R.id.textInputLayout)).setTypeface(FontManager.T1_Typeface(this));
        mobileEditText.setTypeface(FontManager.T1_Typeface(this));
        //captcha et
        ((TextInputLayout) findViewById(R.id.captchaTextInputLayout)).setTypeface(FontManager.T1_Typeface(this));
        ((TextInputEditText) findViewById(R.id.txtCaptcha)).setTypeface(FontManager.T1_Typeface(this));
        //buttons
        ((MaterialButton) findViewById(R.id.submitBtn)).setTypeface(FontManager.T1_Typeface(this));
        ((MaterialButton) findViewById(R.id.notInterestedBtn)).setTypeface(FontManager.T1_Typeface(this));
        //privacy
        ((TextView) findViewById(R.id.privacyTxt)).setTypeface(FontManager.T1_Typeface(this));
    }


    public void ClickBtn() {
        if (mobileEditText.getText().toString().isEmpty())
            Toast.makeText(this, R.string.plz_insert_num, Toast.LENGTH_SHORT).show();
        else if (!mobileEditText.getText().toString().startsWith("09") || mobileEditText.getText().toString().length() != 11) {
            Toasty.warning(this, R.string.plz_insert_mobile_correct, Toasty.LENGTH_LONG, true).show();
        } else if (getCaptchaText().isEmpty())
            Toast.makeText(this, R.string.plz_insert_capcha, Toast.LENGTH_SHORT).show();
        else {
            PhoneNumber = mobileEditText.getText().toString();
            //check sms permission
            if (CheckPermission()) {
                RegisterApi();
            } else {
                //call to get permission
                ActivityCompat.requestPermissions(AuthWithSmsActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, REQ_PERMISSION);
            }
        }
    }


    private void RegisterApi() {
        if (AppUtil.isNetworkAvailable(this)) {
            Loading.setVisibility(View.VISIBLE);
            findViewById(R.id.submitBtn).setEnabled(false);
            AuthUserSignInBySmsDtoModel request = new AuthUserSignInBySmsDtoModel();
            if (PhoneNumber.length() == 0) {
                PhoneNumber = mobileEditText.getText().toString();
            }
            request.Mobile = PhoneNumber;
            request.CaptchaText = getCaptchaText();
            request.CaptchaKey = getCaptchaKey();

            ServiceExecute.execute(new CoreAuthService(this).signInUserBySMS(request))
                    .subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                        @Override
                        public void onNext(@io.reactivex.annotations.NonNull ErrorException<TokenInfoModel> response) {
                            Loading.setVisibility(View.GONE);
                            findViewById(R.id.submitBtn).setEnabled(true);
                            if (!response.IsSuccess) {
                                getNewCaptcha();
                                Toasty.error(AuthWithSmsActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                                return;
                            }
                            Preferences.with(AuthWithSmsActivity.this).UserInfo().setMobile(PhoneNumber);
                            Intent intent = new Intent(AuthWithSmsActivity.this, AuthWithSmsConfirmActivity.class);
                            intent.putExtra(Extras.EXTRA_FIRST_ARG, getCaptchaKey());
                            intent.putExtra(Extras.EXTRA_SECOND_ARG, getCaptchaText());
                            intent.putExtra(Extras.Extra_THIRD_ARG, getCaptchaImageUrl());
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Loading.setVisibility(View.GONE);
                            findViewById(R.id.submitBtn).setEnabled(true);
                            getNewCaptcha();
                            new GenericErrors().throwableException((error, tryAgain) -> Toasty.warning(AuthWithSmsActivity.this, error, Toasty.LENGTH_LONG, true).show()
                                    , e, () -> {
                                    });
                        }
                    });
        } else {

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
                }
                RegisterApi();
                break;
        }
    }

}
