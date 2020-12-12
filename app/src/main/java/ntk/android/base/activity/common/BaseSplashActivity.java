package ntk.android.base.activity.common;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.ApplicationStaticParameter;
import ntk.android.base.BaseNtkApplication;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.appclass.UpdateClass;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.entitymodel.application.ApplicationAppModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.services.application.ApplicationAppService;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.FontManager;
import ntk.android.base.utill.prefrense.Preferences;

public abstract class BaseSplashActivity extends BaseActivity {

    TextView Lbl;
    long startTime;
    private int debugBtnClickCount = 0;
    private boolean inDebug;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_splash_activity);
        initView();
        init();
        getData();
        startTime = System.currentTimeMillis();

    }

    private void initView() {
        Lbl = findViewById(R.id.lblVersionActSplash);
        findViewById(R.id.btnTryAgain).setOnClickListener(v -> ClickRefresh());
        findViewById(R.id.debugModeView).setOnClickListener(this::onClick);
        findViewById(R.id.activity_BaseError).findViewById(R.id.debugModeView).setOnClickListener(this::onClick);
    }

    private void onClick(View v) {
        if (debugBtnClickCount++ > 3)
            showDebug();
    }

    private void showDebug() {
        inDebug = true;
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_debug);
        ((EditText) d.findViewById(R.id.txtUrl)).setText(RetrofitManager.BASE_URL);
        ((EditText) d.findViewById(R.id.txtpackageName)).setText(BaseNtkApplication.get().getApplicationParameter().PACKAGE_NAME());
        d.findViewById(R.id.debugReset).setOnClickListener(v -> {
            ApplicationStaticParameter.URL = "";
            ApplicationStaticParameter.PACKAGE_NAME = "";
        });
        d.findViewById(R.id.debugStart).setOnClickListener(v -> {

            ApplicationStaticParameter.URL = ((EditText) d.findViewById(R.id.txtUrl)).getText().toString();
            ApplicationStaticParameter.PACKAGE_NAME = ((EditText) d.findViewById(R.id.txtpackageName)).getText().toString();
            Preferences.with(this).debugInfo().setCount(20);
            Preferences.with(this).debugInfo().setUrl(ApplicationStaticParameter.URL);
            Preferences.with(this).debugInfo().setPackageName(ApplicationStaticParameter.PACKAGE_NAME);
            d.dismiss();
            getData();
        });
        ((EditText) d.findViewById(R.id.txtLinkSiteId)).setText(Preferences.with(this).UserInfo().siteId().toString());
        ((EditText) d.findViewById(R.id.txtlinkUserId)).setText(Preferences.with(this).UserInfo().linkUserId().toString());
        ((EditText) d.findViewById(R.id.txtLinkMemberId)).setText(Preferences.with(this).UserInfo().linkMemberId().toString());
        ((EditText) d.findViewById(R.id.txtDeviceId)).setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        ((EditText) d.findViewById(R.id.txtApplicationId)).setText(NTKApplication.get().getApplicationParameter().APPLICATION_ID());
        d.show();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        Lbl.setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        Lbl.setText("نسخه  " + (int) Float.parseFloat(BaseNtkApplication.get().getApplicationParameter().VERSION_NAME())
                + "." + BaseNtkApplication.get().getApplicationParameter().VERSION_CODE());

    }


    /**
     * get all needed data
     */
    private void getData() {
        if (AppUtill.isNetworkAvailable(this)) {
            getTokenDevice();

        } else {
            switcher.showErrorView();
        }
    }

    private void getTokenDevice() {
        new CoreAuthService(this).getTokenDevice()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<TokenInfoModel> tokenInfoModelErrorException) {
                        if (tokenInfoModelErrorException.IsSuccess)
//                            getThemeData();
                            getCurrentApp();
                        else
                            switcher.showErrorView();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        switcher.showErrorView();
                    }
                });
    }

    private void getCurrentApp() {
        new ApplicationAppService(this).currentApp()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new NtkObserver<ErrorException<ApplicationAppModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<ApplicationAppModel> response) {
                        if (!response.IsSuccess) {
                            switcher.showErrorView();
                            //replace with layout
                            Toasty.warning(BaseSplashActivity.this, response.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                            return;
                        }

                        Preferences.with(BaseSplashActivity.this).appVariableInfo().setUpdateInfo(new UpdateClass(response.Item));
                        Preferences.with(BaseSplashActivity.this).UserInfo().seTheme(new Gson().toJson(response.Item.ThemeConfigJsonValues));
                        HandelDataAction(response.Item);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    /**
     * @param model get from response
     */
    private void HandelDataAction(ApplicationAppModel model) {

        long userId = Preferences.with(this).UserInfo().userId();
        Preferences.with(this).appVariableInfo().setConfigapp(new Gson().toJson(model));
        //user has token
        if (userId > 0) {
            new CoreAuthService(this).correctTokenInfo().observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new NtkObserver<Boolean>() {
                        @Override
                        public void onNext(@NonNull Boolean aBoolean) {
                            if (aBoolean)//user sign in and have valid token
                                startnewActivity(NTKApplication.getApplicationStyle().getMainActivity());
                            else//user token in invalid then go to register
                            {
                                Preferences.with(BaseSplashActivity.this).appVariableInfo().setIsLogin(false);
                                Toasty.warning(BaseSplashActivity.this, "َشما به صفحه ی ورود کاربر هدایت می شوید", Toasty.LENGTH_LONG, true).show();
                                startnewActivity(AuthWithSmsActivity.class);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            switcher.showErrorView();
                        }
                    });
        } else {//if user seen intro
            if (Preferences.with(this).appVariableInfo().IntroSeen()) {
                //if user not interested to login
                if (Preferences.with(this).appVariableInfo().isRegisterNotInterested())
                    startnewActivity(NTKApplication.getApplicationStyle().getMainActivity());
                    //user maybe interest to login
                else if (Preferences.with(this).appVariableInfo().isLogin())
                    startnewActivity(NTKApplication.getApplicationStyle().getMainActivity());
                else
                    startnewActivity(AuthWithSmsActivity.class);
            } else
                startnewActivity(IntroActivity.class);
        }
    }

    /**
     * handle click of try again
     */

    public void ClickRefresh() {
        switcher.showProgressView();
        getData();
    }

    public void startnewActivity(Class c) {
        new Handler().postDelayed(() -> {
            if (!inDebug) {
                startActivity(new Intent(BaseSplashActivity.this, c));
                finish();
            }
        }, System.currentTimeMillis() - startTime >= 5000 ? 100 : 5000 - System.currentTimeMillis() - startTime);

    }

}
