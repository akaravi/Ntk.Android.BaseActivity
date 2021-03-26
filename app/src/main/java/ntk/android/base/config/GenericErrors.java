package ntk.android.base.config;

import ntk.android.base.BaseNtkApplication;
import ntk.android.base.R;

public class GenericErrors {


    //عدم دسترسی به اینترنت
    public void netError(ErrorViewInterface switcher, Runnable ss) {
        switcher.showError(BaseNtkApplication.get().getString(R.string.plz_check_network), ss);
    }

    public void throwableException(ErrorViewInterface switcher, Throwable e, Runnable ss) {
        switcher.showError(BaseNtkApplication.get().getString(R.string.error_raised) + "\n" + e.getMessage(), ss);
    }


    public void ntkException(ErrorViewInterface switcher, String errorMessage, Runnable tryAgainMethod) {
        switcher.showError(errorMessage, tryAgainMethod);
    }

}
