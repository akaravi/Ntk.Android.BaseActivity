package ntk.android.base.activity.abstraction;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;
import ntk.android.base.ApplicationStaticParameter;
import ntk.android.base.BaseNtkApplication;
import ntk.android.base.NTKApplication;
import ntk.android.base.R;
import ntk.android.base.activity.BaseActivity;
import ntk.android.base.activity.common.AuthWithSmsActivity;
import ntk.android.base.activity.common.IntroActivity;
import ntk.android.base.appclass.AboutUsClass;
import ntk.android.base.appclass.UpdateClass;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.GenericErrors;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.config.RetrofitManager;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.application.ApplicationAppModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.services.application.ApplicationAppService;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtill;
import ntk.android.base.utill.prefrense.Preferences;

public abstract class AbstractSplashActivity extends BaseActivity {
    long startTime;
    protected int debugBtnClickCount = 0;
    protected volatile boolean debugIsVisible = false;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreated();
        setLocale();
        startTime = System.currentTimeMillis();
        getTokenDevice();
    }

    private void setLocale() {
        Resources resources = NTKApplication.get().getResources();
        Configuration overrideConfiguration = resources.getConfiguration();
        Locale overrideLocale = new Locale(NTKApplication.get().getLanguage());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            overrideConfiguration.setLocale(overrideLocale);
        } else {
            overrideConfiguration.locale = overrideLocale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NTKApplication.get().createConfigurationContext(overrideConfiguration);
        } else {
            resources.updateConfiguration(overrideConfiguration, null);
        }
    }

    //onCreate method of child
    protected abstract void onCreated();

    //show errors
    public void showDebugView(View v) {
        if (debugBtnClickCount++ > 3) {
            Log.i("DEBUG_CLICK", debugBtnClickCount + "");
            if (!debugIsVisible) showDebug();

        }
    }

    protected void showDebug() {
        debugIsVisible = true;
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_debug);
        d.setCancelable(false);
        String debugUrl = Preferences.with(this).debugInfo().url();
        String debugPackageName = Preferences.with(this).debugInfo().packageName();
        String staticPackageName = ApplicationStaticParameter.PACKAGE_NAME;
        ((EditText) d.findViewById(R.id.txtUrl)).setText(!debugUrl.equalsIgnoreCase("") ? debugUrl :
                !staticPackageName.equalsIgnoreCase("") ? staticPackageName : RetrofitManager.BASE_URL);
        ((EditText) d.findViewById(R.id.txtpackageName)).setText(debugPackageName.equalsIgnoreCase("") ?
                BaseNtkApplication.get().getApplicationParameter().PACKAGE_NAME() : debugPackageName);
        ((EditText) d.findViewById(R.id.txtLinkSiteId)).setText(Preferences.with(this).UserInfo().siteId().toString());
        ((EditText) d.findViewById(R.id.txtlinkUserId)).setText(Preferences.with(this).UserInfo().linkUserId().toString());
        ((EditText) d.findViewById(R.id.txtLinkMemberId)).setText(Preferences.with(this).UserInfo().linkMemberId().toString());
        ((EditText) d.findViewById(R.id.txtDeviceId)).setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        ((EditText) d.findViewById(R.id.txtApplicationId)).setText(NTKApplication.get().getApplicationParameter().APPLICATION_ID());
        d.findViewById(R.id.debugReset).setOnClickListener(v -> {

            ApplicationStaticParameter.URL = "";
            ApplicationStaticParameter.PACKAGE_NAME = "";
            Preferences.with(this).debugInfo().setUrl("");
            Preferences.with(this).debugInfo().setPackageName("");
            d.dismiss();
            debugIsVisible = false;
            getTokenDevice();
        });
        d.findViewById(R.id.debugStart).setOnClickListener(v -> {
            ApplicationStaticParameter.URL = ((EditText) d.findViewById(R.id.txtUrl)).getText().toString();
            ApplicationStaticParameter.PACKAGE_NAME = ((EditText) d.findViewById(R.id.txtpackageName)).getText().toString();
            Preferences.with(this).debugInfo().setCount(20);
            Preferences.with(this).debugInfo().setUrl(ApplicationStaticParameter.URL);
            Preferences.with(this).debugInfo().setPackageName(ApplicationStaticParameter.PACKAGE_NAME);
            d.dismiss();
            debugIsVisible = false;
            getTokenDevice();
        });

        d.show();
    }

    /**
     * get token device
     */
    protected void getTokenDevice() {
        //check connectivity
        if (AppUtill.isNetworkAvailable(this)) {
            switcher.showContentView();
            ServiceExecute.execute(new CoreAuthService(this).getTokenDevice())
                    .subscribe(new ErrorExceptionObserver<TokenInfoModel>(switcher::showErrorView) {
                        @Override
                        protected void SuccessResponse(ErrorException<TokenInfoModel> tokenInfoModelErrorException) {
                            getCurrentApp();
                        }

                        @Override
                        protected Runnable tryAgainMethod() {
                            return AbstractSplashActivity.this::getTokenDevice;
                        }


                    });
        } else {
            //show generic net error
            new GenericErrors().netError(switcher::showErrorView, this::getTokenDevice);
        }

    }


    private void getCurrentApp() {

        if (AppUtill.isNetworkAvailable(this)) {
            switcher.showContentView();
            ServiceExecute.execute(new ApplicationAppService(this).currentApp())
                    .subscribe(new ErrorExceptionObserver<ApplicationAppModel>(switcher::showErrorView) {
                        @Override
                        protected void SuccessResponse(ErrorException<ApplicationAppModel> response) {
//                            NTKApplication.getApplicationStyle().setAppLanguage(response.Item.);
                            Preferences.with(AbstractSplashActivity.this).appVariableInfo().setUpdateInfo(new UpdateClass(response.Item));
                            Preferences.with(AbstractSplashActivity.this).appVariableInfo().setQRCode(response.Item.DownloadLinkSrcByDomainQRCodeBase64);
                            Preferences.with(AbstractSplashActivity.this).appVariableInfo().setAboutUs(new AboutUsClass(response.Item));
                            Preferences.with(AbstractSplashActivity.this).appVariableInfo().setAppId(response.Item.Id);
                            HandelDataAction(response.Item);
                        }

                        @Override
                        protected Runnable tryAgainMethod() {
                            return AbstractSplashActivity.this::getCurrentApp;
                        }
                    });
        } else {
            //show generic net error
            new GenericErrors().netError(switcher::showErrorView, this::getTokenDevice);
        }
    }

    /**
     * @param model get from response
     */
    private void HandelDataAction(ApplicationAppModel model) {

        long userId = Preferences.with(this).UserInfo().userId();
        Preferences.with(this).appVariableInfo().setApplicationAppModel(new Gson().toJson(model));
        //user has token
        if (userId > 0) {
            ServiceExecute.execute(new CoreAuthService(this).correctTokenInfo())
                    .subscribe(new NtkObserver<Boolean>() {
                        @Override
                        public void onNext(@NonNull Boolean aBoolean) {
                            if (aBoolean)//user sign in and have valid token
                                startNewActivity(NTKApplication.getApplicationStyle().getMainActivity());
                            else//user token in invalid then go to register
                            {
                                Preferences.with(AbstractSplashActivity.this).UserInfo().setUserId(0);
                                Preferences.with(AbstractSplashActivity.this).appVariableInfo().setIsLogin(false);
                                Toasty.warning(AbstractSplashActivity.this, R.string.per_navigate_login, Toasty.LENGTH_LONG, true).show();
                                startNewActivity(AuthWithSmsActivity.class);
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
                if (NTKApplication.getApplicationStyle().show_NotInterested_Btn() & Preferences.with(this).appVariableInfo().isRegisterNotInterested())
                    startNewActivity(NTKApplication.getApplicationStyle().getMainActivity());
                    //user maybe interest to login
                else if (Preferences.with(this).appVariableInfo().isLogin())
                    startNewActivity(NTKApplication.getApplicationStyle().getMainActivity());
                else
                    startNewActivity(AuthWithSmsActivity.class);
            } else
                startNewActivity(IntroActivity.class);
        }
    }

    public void startNewActivity(Class c) {

        new Handler().postDelayed(() -> {
            if (!debugIsVisible) {
                Intent intent = new Intent(AbstractSplashActivity.this, c);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        }, System.currentTimeMillis() - startTime >= 3000 ? 100 : 3000 - (System.currentTimeMillis() - startTime));
    }
}
