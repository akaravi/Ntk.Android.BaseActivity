package ntk.android.base.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Map;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.BaseNtkApplication;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.config.ConfigRestHeader;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.EasyPreference;
import ntk.android.base.utill.FontManager;
import ntk.android.base.api.core.entity.CoreMain;
import ntk.android.base.api.core.entity.CoreTheme;
import ntk.android.base.api.core.interfase.ICore;
import ntk.android.base.api.core.model.MainCoreResponse;
import ntk.android.base.config.RetrofitManager;

public abstract class BaseSplashActivity extends BaseActivity {

    TextView Lbl;

    long startTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        initView();
        init();
        getData();
        startTime = System.currentTimeMillis();
    }

    private void initView() {
        Lbl = findViewById(R.id.lblVersionActSplash);
        findViewById(R.id.btnTryAgain).setOnClickListener(v -> ClickRefresh());
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

        ICore iCore = new RetrofitManager(this).getCachedRetrofit().create(ICore.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        Observable<CoreTheme> call = iCore.GetThemeCore(headers);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CoreTheme>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CoreTheme theme) {
                        //todo check successfully on coreTheme
                        EasyPreference.with(BaseSplashActivity.this).addString("Theme", new Gson().toJson(theme.Item.ThemeConfigJson));
                        //now can get main response
                        requestMainData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        switcher.showErrorView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * req main data
     */
    private void requestMainData() {
        ICore iCore = new RetrofitManager(this).getCachedRetrofit().create(ICore.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        Observable<MainCoreResponse> observable = iCore.GetResponseMain(headers);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MainCoreResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MainCoreResponse mainCoreResponse) {
                        if (!mainCoreResponse.IsSuccess) {
                            switcher.showErrorView();
                            //replace with layout
                            Toasty.warning(BaseSplashActivity.this, mainCoreResponse.ErrorMessage, Toasty.LENGTH_LONG, true).show();
                            return;

                        }
                        HandelDataAction(mainCoreResponse.Item);

                    }


                    @Override
                    public void onError(Throwable e) {
                        //replace with layout
                        switcher.showErrorView();
                        Toasty.warning(BaseSplashActivity.this, "خطای سامانه مجددا تلاش کنید", Toasty.LENGTH_LONG, true).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * @param model get from response
     */
    private void HandelDataAction(CoreMain model) {

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
//                Loading.setVisibility(View.GONE);
                startActivity(new Intent(BaseSplashActivity.this, IntroActivity.class));
                finish();
            }, System.currentTimeMillis() - startTime >= 3000 ? 100 : 3000 - System.currentTimeMillis() - startTime);
            return;
        }
        if (!EasyPreference.with(BaseSplashActivity.this).getBoolean("Registered", false)) {
            new Handler().postDelayed(() -> {
//                Loading.setVisibility(View.GONE);
                boolean register_not_interested = EasyPreference.with(this).getBoolean("register_not_interested", false);
                if (register_not_interested)
                    startActivity(new Intent(BaseSplashActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
                else
                    startActivity(new Intent(BaseSplashActivity.this, RegisterActivity.class));
                finish();
            }, System.currentTimeMillis() - startTime >= 3000 ? 100 : 3000 - System.currentTimeMillis() - startTime);
            return;
        }
        new Handler().postDelayed(() -> {
//            Loading.setVisibility(View.GONE);
            startActivity(new Intent(BaseSplashActivity.this, NTKApplication.getApplicationStyle().getMainActivity()));
            finish();
        }, System.currentTimeMillis() - startTime >= 3000 ? 100 : 3000 - System.currentTimeMillis() - startTime);
    }

    /**
     * handle click of try again
     */

    public void ClickRefresh() {
        switcher.showProgressView();
        getData();
    }
}
