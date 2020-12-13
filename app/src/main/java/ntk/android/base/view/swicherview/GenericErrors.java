package ntk.android.base.view.swicherview;

public class GenericErrors {
    Switcher switcher;

    public GenericErrors(Switcher switcher) {
        this.switcher = switcher;
    }
//عدم دسترسی به اینترنت
    public  void netError( Runnable ss) {
        switcher.showErrorView("لطفا وضعیت شبکه خود را چک کنید",ss);
    }
}
