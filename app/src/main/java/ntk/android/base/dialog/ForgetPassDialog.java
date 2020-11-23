package ntk.android.base.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.nostra13.universalimageloader.core.ImageLoader;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.R;
import ntk.android.base.config.NtkObserver;
import ntk.android.base.dtomodel.core.AuthUserForgetPasswordModel;
import ntk.android.base.entitymodel.base.CaptchaModel;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;
import ntk.android.base.services.core.CoreAuthService;

public class ForgetPassDialog extends DialogFragment {

    private CaptchaModel captcha;
    int Type=0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_forgetpass, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
//        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ((ToggleSwitch) view.findViewById(R.id.forgetPatternSw)).setOnChangeListener(position -> {
            Type=position;
            if (position == 0)
                view.findViewById(R.id.txtActRegister).setVisibility(View.VISIBLE);
            else
                view.findViewById(R.id.txtActRegister).setVisibility(View.GONE);
        });
        view.findViewById(R.id.txtActRegister).requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        view.findViewById(R.id.cardActRegister).setOnClickListener(v -> ForgetPass());
        view.findViewById(R.id.imgCaptcha).setOnClickListener(v -> callCaptchaApi());
        callCaptchaApi();
    }

    private void ForgetPass() {
       if (Type == 0) {
            AuthUserForgetPasswordModel req = new AuthUserForgetPasswordModel();
            req.Email = ((EditText) getView().findViewById(R.id.txtActRegister)).getText().toString();
            req.CaptchaKey = ((EditText) getView().findViewById(R.id.txtCaptcha)).getText().toString();
            if (captcha != null)
                req.CaptchaKey = captcha.Key;
            new CoreAuthService(getContext()).forgetPassword(req)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()).subscribe(new NtkObserver<ErrorException<TokenInfoModel>>() {
                @Override
                public void onNext(@NonNull ErrorException<TokenInfoModel> tokenInfoModelErrorException) {
                    dismiss();
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Toasty.warning(getContext(), "خطای سامانه", Toasty.LENGTH_LONG, true).show();

                }
            });
        }
        {
            //todo forget bye email
        }
    }

    private void callCaptchaApi() {
        new CoreAuthService(getContext()).getCaptcha().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new NtkObserver<ErrorException<CaptchaModel>>() {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull ErrorException<CaptchaModel> captchaResponce) {
                if (captchaResponce.IsSuccess) {
                    ImageLoader.getInstance().displayImage(captchaResponce.Item.Image, (ImageView) getView().findViewById(R.id.imgCaptcha));
                    captcha = captchaResponce.Item;
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }


        });
    }
}
