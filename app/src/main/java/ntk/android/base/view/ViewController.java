package ntk.android.base.view;

import androidx.annotation.LayoutRes;

public abstract class ViewController {
    @LayoutRes
    protected int error_view;
    @LayoutRes
    protected int loading_view;
    @LayoutRes
    protected int empty_view;

    public int getEmpty_view() {
        return empty_view;
    }

    public int getError_view() {
        return error_view;
    }

    public int getLoading_view() {
        return loading_view;
    }

    public ViewController setError_view(int error_view) {
        this.error_view = error_view;
        return this;
    }

    public ViewController setLoading_view(int loading_view) {
        this.loading_view = loading_view;
        return this;
    }

    public ViewController setEmpty_view(int empty_view) {
        this.empty_view = empty_view;
        return this;
    }
}
