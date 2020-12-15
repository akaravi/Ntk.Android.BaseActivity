package ntk.android.base.view.swicherview;

public class GenericErrors {


    //عدم دسترسی به اینترنت
    public void netError(Switcher switcher, Runnable ss) {
        switcher.showErrorView("لطفا وضعیت شبکه خود را چک کنید", ss);
    }

    public void throwableException(Switcher switcher, Throwable e, Runnable ss) {
        switcher.showErrorView("عملیات با خطا مواجه شد لطفا مجدد تلاش فرمایید", ss);
    }


    public void ntkException(Switcher switcher, String errorMessage, Runnable tryAgainMethod) {
        switcher.showErrorView(errorMessage, tryAgainMethod);
    }
}
