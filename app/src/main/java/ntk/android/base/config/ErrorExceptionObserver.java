package ntk.android.base.config;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import ntk.android.base.entitymodel.base.ErrorException;

public abstract class ErrorExceptionObserver<T> implements Observer<ErrorException<T>> {
    ErrorViewInterface switcher;

    public ErrorExceptionObserver(ErrorViewInterface sw) {
        switcher = sw;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull ErrorException<T> tErrorException) {
        if (tErrorException.IsSuccess)
            SuccessResponse(tErrorException);
        else
        failResponse(tErrorException);
    }

    private void failResponse(ErrorException<T> tErrorException) {
        if (tErrorException.ErrorMessage == null)
            tErrorException.ErrorMessage = "خطای Ntk رخ داد";
        new GenericErrors().ntkException(switcher, tErrorException.ErrorMessage, tryAgainMethod());
    }

    /**
     * sure to be success response
     *
     * @param tErrorException
     */
    protected abstract void SuccessResponse(ErrorException<T> tErrorException);

    /**
     * @return method that call when user want to try again on error
     */
    protected abstract Runnable tryAgainMethod();

    @Override
    public void onError(@NonNull Throwable e) {
        new GenericErrors().throwableException(switcher, e, tryAgainMethod());
    }


    @Override
    public void onComplete() {

    }
}
