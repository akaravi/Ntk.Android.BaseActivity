package ntk.android.base.activity.abstraction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.yariksoffice.lingver.Lingver;

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
import ntk.android.base.config.TokenDeviceExp;
import ntk.android.base.dtomodel.core.TokenDeviceModel;
import ntk.android.base.entitymodel.application.ApplicationAppModel;
import ntk.android.base.entitymodel.application.ApplicationThemeConfigModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.services.application.ApplicationAppService;
import ntk.android.base.services.application.ApplicationThemeService;
import ntk.android.base.services.core.CoreAuthService;
import ntk.android.base.utill.AppUtil;
import ntk.android.base.utill.prefrense.Preferences;
/**
 * Splash screen of all app
 * important :
 * switcher should not be null
 * splashIndicator & lblWorkActSplash is that is for progress view if be null not user don't see any progress
 */
public abstract class AbstractSplashActivity extends BaseActivity {
    //time for starting activity
    long startTime;
    //count of debug view clicking
    protected int debugBtnClickCount = 0;
    //for debug view visibility
    protected volatile boolean debugIsVisible = false;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreated();
        startTime = System.currentTimeMillis();
        //get token of device
        getTokenDevice();
    }

    //onCreate method of child
    protected abstract void onCreated();

    //debug view click listener
    public void showDebugView(View v) {
        if (debugBtnClickCount++ > 3) {
            Log.i("DEBUG_CLICK", debugBtnClickCount + "");
            if (!debugIsVisible) showDebug();

        }
    }

    /**
     * create debug dialog contain Api url ,packageName ,...
     */
    protected void showDebug() {
        //debug view is visible ,prevent going next page
        debugIsVisible = true;
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_debug);
        d.setCancelable(false);
        //get last debug info
        String debugUrl = Preferences.with(this).debugInfo().url();
        String debugPackageName = Preferences.with(this).debugInfo().packageName();
        //if static url not exist then use default url
        String staticPackageName = ApplicationStaticParameter.PACKAGE_NAME;
        ((EditText) d.findViewById(R.id.txtUrl)).setText(!debugUrl.equalsIgnoreCase("") ? debugUrl :
                !staticPackageName.equalsIgnoreCase("") ? staticPackageName : RetrofitManager.BASE_URL);
        //if static packageName not exist then use default packageName
        ((EditText) d.findViewById(R.id.txtpackageName)).setText(debugPackageName.equalsIgnoreCase("") ?
                BaseNtkApplication.get().getApplicationParameter().PACKAGE_NAME() : debugPackageName);
        //set saved site id
        ((EditText) d.findViewById(R.id.txtLinkSiteId)).setText(Preferences.with(this).UserInfo().siteId().toString());
        //set saved site id
        ((EditText) d.findViewById(R.id.txtlinkUserId)).setText(Preferences.with(this).UserInfo().linkUserId().toString());
        //set saved site link member id
        ((EditText) d.findViewById(R.id.txtLinkMemberId)).setText(Preferences.with(this).UserInfo().linkMemberId().toString());
        //set saved site unique device id
        ((EditText) d.findViewById(R.id.txtDeviceId)).setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        //set saved site APPLICATION_ID
        ((EditText) d.findViewById(R.id.txtApplicationId)).setText(NTKApplication.get().getApplicationParameter().APPLICATION_ID());
        //reset button click listener
        d.findViewById(R.id.debugReset).setOnClickListener(v -> {
            //reset all debug variables
            ApplicationStaticParameter.URL = "";
            ApplicationStaticParameter.PACKAGE_NAME = "";
            Preferences.with(this).debugInfo().setUrl("");
            Preferences.with(this).debugInfo().setPackageName("");
            d.dismiss();
            debugIsVisible = false;
            getTokenDevice();
        });
        //setter debug variable listener
        d.findViewById(R.id.debugStart).setOnClickListener(v -> {
            ApplicationStaticParameter.URL = ((EditText) d.findViewById(R.id.txtUrl)).getText().toString();
            ApplicationStaticParameter.PACKAGE_NAME = ((EditText) d.findViewById(R.id.txtpackageName)).getText().toString();
            //set max count for debug variable
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
     * api for get token device
     */
    protected void getTokenDevice() {
        //check connectivity
        if (AppUtil.isNetworkAvailable(this)) {
            switcher.showContentView();
            //show progress title
            TextView Lb2 = findViewById(R.id.lblWorkActSplash);
            if (Lb2 != null)
                Lb2.setText(R.string.splashGetTokenStep);
            //show linear progress
            LinearProgressIndicator indicator = findViewById(R.id.splashIndicator);
            if (indicator != null) {
                indicator.setProgress(10);
                indicator.show();
            }
            ServiceExecute.execute(new CoreAuthService(this).getTokenDevice())
                    .subscribe(new NtkObserver<ErrorException<TokenDeviceModel>>() {
                        @Override
                        public void onNext(ErrorException<TokenDeviceModel> tokenDeviceModelErrorException) {
                            if (tokenDeviceModelErrorException.IsSuccess)
                                currentToken();
                            else
                                switcher.showErrorView(tokenDeviceModelErrorException.ErrorMessage,() -> getTokenDevice());
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e instanceof TokenDeviceExp) {
                                    //got to frist step
                                    getTokenDevice();
                                    return;
                            }
                            switcher.showErrorView(e.getMessage(), () -> getTokenDevice());
                        }
                    });
        } else {
            //show generic net error
            new GenericErrors().netError(switcher::showErrorView, this::getTokenDevice);
        }

    }

    private void currentToken() {
        if (AppUtil.isNetworkAvailable(this)) {
            switcher.showContentView();
            //show progress
            TextView Lb2 = findViewById(R.id.lblWorkActSplash);
            if (Lb2 != null)
                Lb2.setText(R.string.splashGetTheme);
            LinearProgressIndicator indicator = findViewById(R.id.splashIndicator);
            if (indicator != null) {
                indicator.setProgress(30);
            }
            //call api
            ServiceExecute.execute(new CoreAuthService(this).correctTokenInfo())
                    .subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                        @Override
                        public void onNext(@NonNull ErrorException<TokenInfoModel> tokenInfoModelErrorException) {
                            if (tokenInfoModelErrorException.IsSuccess)
                                themeApi();
                            else
                                switcher.showErrorView(tokenInfoModelErrorException.ErrorMessage, () -> currentToken());
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            if (e instanceof TokenDeviceExp) {
                                //got to frist step
                                getTokenDevice();
                                return;
                            }
                            switcher.showErrorView(e.getMessage(), () -> currentToken());
                        }
                    });
        } else {
            //show generic net error
            new GenericErrors().netError(switcher::showErrorView, this::currentToken);
        }
    }

    /**
     * get theme of application
     */
    private void themeApi() {
        //check connectivity
        if (AppUtil.isNetworkAvailable(this)) {
            switcher.showContentView();
            //show progress
            TextView Lb2 = findViewById(R.id.lblWorkActSplash);
            if (Lb2 != null)
                Lb2.setText(R.string.splashGetTheme);
            LinearProgressIndicator indicator = findViewById(R.id.splashIndicator);
            if (indicator != null) {
                indicator.setProgress(30);
            }
            //call api
            ServiceExecute.execute(new ApplicationThemeService(this).getAppTheme())
                    .subscribe(new ErrorExceptionObserver<ApplicationThemeConfigModel>(switcher::showErrorView) {
                        @Override
                        protected void SuccessResponse(ErrorException<ApplicationThemeConfigModel> response) {
                            //set thmeme of app
                            NTKApplication.getApplicationStyle().setTheme(response.Item);
                            //next api
                            getCurrentApp();
                        }

                        @Override
                        protected Runnable tryAgainMethod() {
                            return AbstractSplashActivity.this::themeApi;
                        }
                    });
        } else {
            //show generic net error
            new GenericErrors().netError(switcher::showErrorView, this::themeApi);
        }

    }

    /**
     * getting application info
     */
    private void getCurrentApp() {

        if (AppUtil.isNetworkAvailable(this)) {
            switcher.showContentView();
            //show progress
            TextView Lb2 = findViewById(R.id.lblWorkActSplash);
            if (Lb2 != null)
                Lb2.setText(R.string.splashGetCurrentAppStep);
            LinearProgressIndicator indicator = findViewById(R.id.splashIndicator);
            if (indicator != null) {
                indicator.setProgress(50);
            }
            ServiceExecute.execute(new ApplicationAppService(this).currentApp())
                    .subscribe(new ErrorExceptionObserver<ApplicationAppModel>(switcher::showErrorView) {
                        @Override
                        protected void SuccessResponse(ErrorException<ApplicationAppModel> response) {
                            //set locale
                            NTKApplication.getApplicationStyle().setAppLanguage(response.Item.Lang);
                            Lingver.getInstance().setLocale(AbstractSplashActivity.this, (NTKApplication.getApplicationStyle().getAppLanguage()));
                            //add update response
                            Preferences.with(AbstractSplashActivity.this).appVariableInfo().setUpdateInfo(new UpdateClass(response.Item));
                            //set qr code
                            //Preferences.with(AbstractSplashActivity.this).appVariableInfo().setQRCode(response.Item.DownloadLinkSrcQRCodeBase64);
                            //set about us view
                            Preferences.with(AbstractSplashActivity.this).appVariableInfo().setAboutUs(new AboutUsClass(response.Item));
                            //set app id
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
        //user has token and is login
//        if (userId > 0) {
//            TextView Lb2 = findViewById(R.id.lblWorkActSplash);
//            if (Lb2 != null)
//                Lb2.setText(R.string.splashGetUserInformationStep);
//            LinearProgressIndicator indicator = findViewById(R.id.splashIndicator);
//            if (indicator != null) {
//                indicator.setProgress(70);
//            }
//            //check user token is correct
//            ServiceExecute.execute(new CoreAuthService(this).correctTokenInfo())
//                    .subscribe(new NtkObserver<Boolean>() {
//                        @Override
//                        public void onNext(@NonNull Boolean aBoolean) {
//                            if (aBoolean)//user sign in and have valid token
//                                startNewActivity(NTKApplication.getApplicationStyle().getMainActivity());
//                            else//user token in invalid then go to register
//                            {
//                                //go to login page
//                                Preferences.with(AbstractSplashActivity.this).UserInfo().setUserId(0);
//                                Preferences.with(AbstractSplashActivity.this).appVariableInfo().setIsLogin(false);
//                                Toasty.warning(AbstractSplashActivity.this, R.string.per_navigate_login, Toasty.LENGTH_LONG, true).show();
//                                startNewActivity(AuthWithSmsActivity.class);
//                            }
//                        }
//
//                        @Override
//                        public void onError(@NonNull Throwable e) {
//                            switcher.showErrorView();
//                        }
//                    });
//        } else {//if user seen intro
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
//        }
    }

    /**
     * starting new activity at least 3 sec after seeing splash Screen
     *
     * @param c
     */
    public void startNewActivity(Class c) {

        new Handler().postDelayed(() -> {
            //if debug view is hide start new activity
            if (!debugIsVisible) {
                Intent intent = new Intent(AbstractSplashActivity.this, c);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        }, System.currentTimeMillis() - startTime >= 3000 ? 100 : 3000 - (System.currentTimeMillis() - startTime));
    }
}
