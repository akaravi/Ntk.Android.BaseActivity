package ntk.android.base.activity.common;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.dtomodel.application.AppThemeDtoModel;
import ntk.android.base.dtomodel.application.MainResponseDtoModel;
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
        d.findViewById(R.id.btn).setOnClickListener(v -> {

            ApplicationStaticParameter.URL = ((EditText) d.findViewById(R.id.txtUrl)).getText().toString();
            ApplicationStaticParameter.PACKAGE_NAME = ((EditText) d.findViewById(R.id.txtpackageName)).getText().toString();
            Preferences.with(this).debugInfo().setCount(20);
            Preferences.with(this).debugInfo().setUrl(ApplicationStaticParameter.URL);
            Preferences.with(this).debugInfo().setPackageName(ApplicationStaticParameter.PACKAGE_NAME);
            d.dismiss();
            getData();
        });
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
                            getThemeData();
                        else
                            switcher.showErrorView();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        switcher.showErrorView();
                    }
                });
    }

    /**
     * get theme from server
     */
    private void getThemeData() {
        new ApplicationAppService(this).getAppTheme().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NtkObserver<ErrorException<AppThemeDtoModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<AppThemeDtoModel> theme) {
                        //todo check successfully on coreTheme
                        Preferences.with(BaseSplashActivity.this).UserInfo().seTheme( new Gson().toJson(theme.Item.ThemeConfigJson));
                        //now can get main response
                        if (theme.IsSuccess)
                            requestMainData();
                        else
                            switcher.showErrorView();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        switcher.showErrorView();
                    }
                });
    }

    /**
     * req main data
     */
    private void requestMainData() {
        new ApplicationAppService(this).getResponseMain().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new NtkObserver<ErrorException<MainResponseDtoModel>>() {
                    @Override
                    public void onNext(@NonNull ErrorException<MainResponseDtoModel> mainCoreResponse) {
                        if (!mainCoreResponse.IsSuccess) {
                            switcher.showErrorView();
                            //replace with layout
                            Toasty.warning(BaseSplashActivity.this, mainCoreResponse.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                            return;

                        }
                        HandelDataAction(mainCoreResponse.Item);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //replace with layout
                        switcher.showErrorView();
                        Toasty.warning(BaseSplashActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();

                    }
                });
    }


    /**
     * @param model get from response
     */
    private void HandelDataAction(MainResponseDtoModel model) {

        Preferences.with(this).UserInfo().setMemberUserId(model.MemberUserId);
        Preferences.with(this).UserInfo().setUserId(model.UserId);
        Preferences.with(this).UserInfo().setSiteId(model.SiteId);
        Preferences.with(this).appVariableInfo().setConfigapp(new Gson().toJson(model));
        if (model.UserId <= 0)
            Preferences.with(this).appVariableInfo().setRegistered(false);

        if (!Preferences.with(this).appVariableInfo().IntroSeen()) {
            new Handler().postDelayed(() -> {
                if (!inDebug) {
                    startActivity(new Intent(BaseSplashActivity.this, IntroActivity.class));
                    finish();
                }
            }, System.currentTimeMillis() - startTime >= 5000 ? 100 : 5000 - System.currentTimeMillis() - startTime);
            return;
        }
        if (!Preferences.with(this).appVariableInfo().isRegistered()) {
            new Handler().postDelayed(() -> {
//                Loading.setVisibility(View.GONE);
                if (!inDebug) {
                    boolean register_not_interested = Preferences.with(this).appVariableInfo().isRegistered();
                    ;
                    if (register_not_interested)
                        startActivity(new Intent(BaseSplashActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
                    else
                        startActivity(new Intent(BaseSplashActivity.this, AuthWithSmsActivity.class));
                    finish();
                }
            }, System.currentTimeMillis() - startTime >= 5000 ? 100 : 5000 - System.currentTimeMillis() - startTime);
            return;
        }
        new Handler().postDelayed(() -> {
//            Loading.setVisibility(View.GONE);
            if (!inDebug) {
                startActivity(new Intent(BaseSplashActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
                finish();
            }
        }, System.currentTimeMillis() - startTime >= 5000 ? 100 : 5000 - System.currentTimeMillis() - startTime);
    }

    /**
     * handle click of try again
     */

    public void ClickRefresh() {
        switcher.showProgressView();
        getData();
    }


}
