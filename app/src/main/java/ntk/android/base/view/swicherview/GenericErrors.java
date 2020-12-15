package ntk.android.base.view.swicherview;

import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.TokenInfoModel;

public class GenericErrors {
    Switcher switcher;

    public GenericErrors(Switcher switcher) {
        this.switcher = switcher;
    }
//عدم دسترسی به اینترنت
    public  void netError( Runnable ss) {
        switcher.showErrorView("لطفا وضعیت شبکه خود را چک کنید",ss);
    }  public  void throwableException(Throwable e, Runnable ss) {
        switcher.showErrorView("لطفا وضعیت شبکه خود را چک کنید",ss);
    }


}
