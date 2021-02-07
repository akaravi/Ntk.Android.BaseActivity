package ntk.android.base.activity.abstraction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

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
    boolean debugIsVisible = false;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreated();
        startTime = System.currentTimeMillis();
        getTokenDevice();
    }

    //onCreate method of child
    protected abstract void onCreated();

    //show errors
    public void showDebugView(View v) {
        if (debugBtnClickCount++ > 3) {
            Log.i("DEBUG_CLICK", debugBtnClickCount + "");
            showDebug();

        }
    }

    private void showDebug() {
        debugIsVisible = true;
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_debug);
        d.setCancelable(false);
        ((EditText) d.findViewById(R.id.txtUrl)).setText(RetrofitManager.BASE_URL);
        ((EditText) d.findViewById(R.id.txtpackageName)).setText(BaseNtkApplication.get().getApplicationParameter().PACKAGE_NAME());
        d.findViewById(R.id.debugReset).setOnClickListener(v -> {
            debugIsVisible = false;
            ApplicationStaticParameter.URL = "";
            ApplicationStaticParameter.PACKAGE_NAME = "";
            Preferences.with(this).debugInfo().setUrl("");
            Preferences.with(this).debugInfo().setPackageName("");
            d.dismiss();
            getTokenDevice();
        });
        d.findViewById(R.id.debugStart).setOnClickListener(v -> {
            debugIsVisible = false;
            ApplicationStaticParameter.URL = ((EditText) d.findViewById(R.id.txtUrl)).getText().toString();
            ApplicationStaticParameter.PACKAGE_NAME = ((EditText) d.findViewById(R.id.txtpackageName)).getText().toString();
            Preferences.with(this).debugInfo().setCount(20);
            Preferences.with(this).debugInfo().setUrl(ApplicationStaticParameter.URL);
            Preferences.with(this).debugInfo().setPackageName(ApplicationStaticParameter.PACKAGE_NAME);
            d.dismiss();
            getTokenDevice();
        });
        ((EditText) d.findViewById(R.id.txtLinkSiteId)).setText(Preferences.with(this).UserInfo().siteId().toString());
        ((EditText) d.findViewById(R.id.txtlinkUserId)).setText(Preferences.with(this).UserInfo().linkUserId().toString());
        ((EditText) d.findViewById(R.id.txtLinkMemberId)).setText(Preferences.with(this).UserInfo().linkMemberId().toString());
        ((EditText) d.findViewById(R.id.txtDeviceId)).setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        ((EditText) d.findViewById(R.id.txtApplicationId)).setText(NTKApplication.get().getApplicationParameter().APPLICATION_ID());
        d.show();
    }

    /**
     * get token device
     */
    private void getTokenDevice() {
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
                                Toasty.warning(AbstractSplashActivity.this, "َشما به صفحه ی ورود کاربر هدایت می شوید", Toasty.LENGTH_LONG, true).show();
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
                if (Preferences.with(this).appVariableInfo().isRegisterNotInterested())
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
//                startActivity(new Intent(AbstractSplashActivity.this, c));
                    finish();
                }
            }, System.currentTimeMillis() - startTime >= 3000 ? 100 : 3000 - (System.currentTimeMillis() - startTime));
    }
}
