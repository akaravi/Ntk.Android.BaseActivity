package ntk.android.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import java9.util.function.Function;
import ntk.android.base.R;
import ntk.android.base.utill.FontManager;

public class SpinnerAdapter<S> extends ArrayAdapter<String> {

    List<S> items;

    public SpinnerAdapter(Context context, List<String> items) {
        super(context, R.layout.spinner_item, items);



    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spinner_item, parent, false);
        }
        ((TextView) view.findViewById(R.id.tv_spinner)).setText((getItem(position)));
        ((TextView) view.findViewById(R.id.tv_spinner)).setTypeface(FontManager.T1_Typeface(getContext()));

        return view;
    }
//    }  @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        TextView textView = (TextView) super.getView(position, convertView, parent);
//        textView.setTypeface(FontManager.GetTypeface(getContext(), FontManager.IranSans));
//        return textView;
//    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setTypeface(FontManager.GetTypeface(getContext(), FontManager.IranSans));
        return textView;
    }


}
