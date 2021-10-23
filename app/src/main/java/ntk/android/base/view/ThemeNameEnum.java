package ntk.android.base.view;

import android.widget.Toast;

import ntk.android.base.NTKApplication;
import ntk.android.base.styles.BaseModuleStyle;

public enum ThemeNameEnum {
    DEFAULT("default"),
    THEME1("1"),
    THEME2("2"),
    THEME3("3"),
    THEME4("4");
    private final String name;

    ThemeNameEnum(String name) {
        this.name = name;
    }

    public static ThemeNameEnum get(String typeId) {
        ThemeNameEnum theme;
        if (typeId.equals(THEME1.code()))
            theme = THEME1;
        else if (typeId.equals(THEME2.code()))
            theme = THEME2;
        else if (typeId.equals(THEME3.code()))
            theme = THEME3;
        else if (typeId.equals(THEME4.code()))
            theme = THEME4;
        else {
            Toast.makeText(NTKApplication.get(), "تم حالت پیش فرض", Toast.LENGTH_SHORT).show();
            theme = ThemeNameEnum.DEFAULT;
        }
        return theme;
    }

    public static ThemeNameEnum get(BaseModuleStyle style){
        return get(style.getTAG());
    }
    public String code() {
        return name;
    }
}
