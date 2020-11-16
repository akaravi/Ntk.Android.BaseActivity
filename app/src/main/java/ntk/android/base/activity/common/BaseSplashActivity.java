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

import java.util.Map;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.ApplicationStaticParameter;
import ntk.android.base.BaseNtkApplication;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.api.core.interfase.ICore;
import ntk.android.base.api.core.model.MainCoreResponse;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.dtomodel.application.AppThemeDtoModel;
import ntk.android.base.dtomodel.application.MainResponseDtoModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.services.application.ApplicationAppService;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EasyPreference;
import ntk.android.base.utill.FontManager;

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
            EasyPreference.with(this).addInt("NTK_TEST_COUNT", 20);
            ApplicationStaticParameter.URL = ((EditText) d.findViewById(R.id.txtUrl)).getText().toString();
            ApplicationStaticParameter.PACKAGE_NAME = ((EditText) d.findViewById(R.id.txtpackageName)).getText().toString();
            EasyPreference.with(this).addString("NTK_TEST_URL", ApplicationStaticParameter.URL);
            EasyPreference.with(this).addString("NTK_TEST_PACKAGENAME", ApplicationStaticParameter.PACKAGE_NAME);
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
            getThemeData();

        } else {
            switcher.showErrorView();
        }
    }

    private void getTokenDevice() {
        new CoreAuthService(this).getTokenDevice();
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
                        EasyPreference.with(BaseSplashActivity.this).addString("Theme", new Gson().toJson(theme.Item.ThemeConfigJson));
                        //now can get main response
                        requestMainData();
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

        EasyPreference.with(BaseSplashActivity.this).addLong("MemberUserId", model.MemberUserId);
        EasyPreference.with(BaseSplashActivity.this).addLong("UserId", model.UserId);
        EasyPreference.with(BaseSplashActivity.this).addLong("SiteId", model.SiteId);
        EasyPreference.with(BaseSplashActivity.this).addString("configapp", new Gson().toJson(model));
        if (model.UserId <= 0)
            EasyPreference.with(BaseSplashActivity.this).addBoolean("Registered", false);

//        Loading.cancelAnimation();
//        Loading.setVisibility(View.GONE);

        if (!EasyPreference.with(BaseSplashActivity.this).getBoolean("Intro", false)) {
            new Handler().postDelayed(() -> {
                if (!inDebug) {
                    startActivity(new Intent(BaseSplashActivity.this, IntroActivity.class));
                    finish();
                }
            }, System.currentTimeMillis() - startTime >= 5000 ? 100 : 5000 - System.currentTimeMillis() - startTime);
            return;
        }
        if (!EasyPreference.with(BaseSplashActivity.this).getBoolean("Registered", false)) {
            new Handler().postDelayed(() -> {
//                Loading.setVisibility(View.GONE);
                if (!inDebug) {
                    boolean register_not_interested = EasyPreference.with(this).getBoolean("register_not_interested", false);
                    if (register_not_interested)
                        startActivity(new Intent(BaseSplashActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
                    else
                        startActivity(new Intent(BaseSplashActivity.this, RegisterActivity.class));
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
