package ntk.android.base.dialog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import java9.util.function.BiFunction;
import java9.util.stream.StreamSupport;
import ntk.android.base.R;
import ntk.android.base.adapter.MultiChooseAdapter;
import ntk.android.base.utill.FontManager;

public class CheckBoxListDialog extends DialogFragment {
    MultiChooseAdapter adapter;
    BiFunction<List<String>, List<Integer>, Void> function;

    public static CheckBoxListDialog newInstance(BiFunction<List<String>, List<Integer>, Void> func, List<String> list) {
        CheckBoxListDialog dialog = new CheckBoxListDialog();
        dialog.setList(list);
        dialog.setInterface(func);
        return dialog;
    }

    private void setInterface(BiFunction<List<String>, List<Integer>, Void> o) {
        function = o;
    }

    CheckBoxListDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_recyclerview, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.imgToolbarBack).setOnClickListener(v -> dismiss());
        Typeface t1 = FontManager.T1_Typeface(getContext());
        ((TextView) view.findViewById(R.id.txtToolbarTitle)).setTypeface(t1);
        MaterialButton button = (MaterialButton) view.findViewById(R.id.selectBtn);
        button.setTypeface(t1);
        RecyclerView rc = (RecyclerView) view.findViewById(R.id.rc);
        rc.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rc.setAdapter(adapter);
        button.setOnClickListener(view1 -> {
            if (adapter != null) {
                if (adapter.getIndex().size() != 0) {
                    List<Integer> pos = new ArrayList<>();
                    List<String> list = adapter.getIndex();
                    StreamSupport.stream(list).forEach(s -> pos.add(list.indexOf(s)));
                    function.apply(list, pos);
                    dismiss();
                }
            }
        });
    }

    public void setList(List<String> list) {
        adapter = new MultiChooseAdapter(list);
    }

    public boolean isShow() {
        if (this.getDialog() != null && this.getDialog().isShowing() && !this.isRemoving()) {
            //dialog is showing so do something
            return true;
        } else
            return false;
    }


}
