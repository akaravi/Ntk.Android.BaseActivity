package ntk.android.base.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ntk.android.base.R;
import ntk.android.base.view.CaptchaView;

public class CaptchaDialog extends DialogFragment {

    private Runnable runnable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_captcha, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.ignoreTxt).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnActRegister).setOnClickListener(v -> runnable.run());
        ((CaptchaView) view.findViewById(R.id.captchaView)).getNewCaptcha();
    }

    public CaptchaView getCaptcha() {
        return getView().findViewById(R.id.captchaView);
    }

    public boolean isShow() {
        if (this.getDialog() != null && this.getDialog().isShowing() && !this.isRemoving()) {
            //dialog is showing so do something
            return true;
        } else
            return false;
    }

    public void setOnclickListener(Runnable runnable) {
        this.runnable = runnable;
    }

    public void lockButton(boolean lockButton) {

        getView().findViewById(R.id.btnActRegister).setAlpha(lockButton ? (float) 0.5 : 1);
        getView().findViewById(R.id.btnActRegister).setEnabled(!lockButton);
    }
}
