package ntk.android.base.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.yariksoffice.lingver.Lingver;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;

import ntk.android.base.NTKApplication;


class BaseLocaleActivity extends LocalizationActivity {

//    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Lingver.getInstance().setLocale(this, NTKApplication.getApplicationStyle().getAppLanguage());
    }
}
