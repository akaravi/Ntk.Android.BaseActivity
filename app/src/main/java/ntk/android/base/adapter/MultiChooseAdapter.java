package ntk.android.base.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

class MultiChooseAdapter<S> extends ArrayAdapter<String> {
    public MultiChooseAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
