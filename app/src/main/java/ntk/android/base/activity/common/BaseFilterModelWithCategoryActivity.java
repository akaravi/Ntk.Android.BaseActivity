package ntk.android.base.activity.common;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import io.reactivex.Observable;
import java9.util.function.Function;
import ntk.android.base.R;
import ntk.android.base.config.ErrorExceptionObserver;
import ntk.android.base.config.ServiceExecute;
import ntk.android.base.entitymodel.base.ErrorException;
import ntk.android.base.entitymodel.base.FilterModel;
import ntk.android.base.utill.FontManager;

public abstract class BaseFilterModelWithCategoryActivity<TENTITY, TCATEGORY> extends BaseFilterModelListActivity<TENTITY> {
    @Override
    public void showCategoryListDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.setContentView(R.layout.dialog_category);
        TextView Lbl = dialog.findViewById(R.id.lblTitleCategoryDialog);
        Lbl.setTypeface(FontManager.T1_Typeface(this));

        Button Btn = dialog.findViewById(R.id.btnSubmitDialogCategory);
        Btn.setTypeface(FontManager.T1_Typeface(this));
        Btn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        FilterModel filterModel = new FilterModel().setRowPerPage(1000);
        ServiceExecute.execute(getCatService().apply(filterModel))
                .subscribe(new ErrorExceptionObserver<TCATEGORY>(switcher::showErrorView) {

                    @Override
                    protected void SuccessResponse(ErrorException<TCATEGORY> response) {
                        if (dialog.isShowing()) {
                            dialog.findViewById(R.id.dialog_loading).setVisibility(View.GONE);
                            onCategoryResponse(response, dialog);
                            dialog.findViewById(R.id.btnSubmitDialogCategory).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    protected Runnable tryAgainMethod() {
                        return () -> {
                        };
                    }
                });
    }

    protected abstract void onCategoryResponse(ErrorException<TCATEGORY> response, Dialog dialog);

    @Override
    protected boolean showCategory() {
        return true;
    }

    public abstract Function<FilterModel, Observable<ErrorException<TCATEGORY>>> getCatService();

}
