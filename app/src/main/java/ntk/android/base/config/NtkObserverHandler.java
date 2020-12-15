package ntk.android.base.config;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import java9.util.function.Consumer;
import java9.util.function.Function;
import ntk.android.base.view.swicherview.Switcher;

public abstract class NtkObserverHandler<T> implements Observer<T> {
    Function<String, Void> s;
    Switcher switcher;

    Consumer<Throwable> error;

    NtkObserverHandler(Switcher switcher) {
        this.switcher = switcher;
    }


    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onComplete() {

    }
}
