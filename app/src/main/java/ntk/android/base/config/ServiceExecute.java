package ntk.android.base.config;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ntk.android.base.entitymodel.base.ErrorException;

public class ServiceExecute<R> {
    Observable<R> o;

    public ServiceExecute(Observable<R> we) {
        o=we;
    }

    public static <T> ServiceExecute<T> execute(Observable<T> we) {
        return new ServiceExecute<>(we);
    }

    public void subscribe(Observer<R> t) {
        o.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t);
    }
}
