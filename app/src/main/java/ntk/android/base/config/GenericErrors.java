package ntk.android.base.config;

public class GenericErrors {


    //عدم دسترسی به اینترنت
    public void netError(ErrorViewInterface switcher, Runnable ss) {
        switcher.showError("لطفا وضعیت شبکه خود را چک کنید", ss);
    }

    public void throwableException(ErrorViewInterface switcher, Throwable e, Runnable ss) {
        switcher.showError("عملیات با خطا مواجه شد لطفا مجدد تلاش فرمایید", ss);
    }


    public void ntkException(ErrorViewInterface switcher, String errorMessage, Runnable tryAgainMethod) {
        switcher.showError(errorMessage, tryAgainMethod);
    }

}
