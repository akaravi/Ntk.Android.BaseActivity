package ntk.android.base.view;

import android.widget.Toast;

import ntk.android.base.NTKApplication;

public enum ThemeNameEnum {
    DEFAULT("default"),
    THEME1("1"),
    THEME2("2"),
    THEME3("3");
    private final String name;

    ThemeNameEnum(String name) {
        this.name = name;
    }

    public static ThemeNameEnum  get(String typeId) {
        ThemeNameEnum theme;
        if (typeId.equals(ThemeNameEnum.THEME1.code()))
            theme = ThemeNameEnum.THEME1;
        else if (typeId.equals(ThemeNameEnum.THEME2.code()))
            theme = ThemeNameEnum.THEME2;
        else if(typeId.equals(ThemeNameEnum.THEME3.code()))
            theme=ThemeNameEnum.THEME3;
        else {
            Toast.makeText(NTKApplication.get(), "تم حالت پیش فرض", Toast.LENGTH_SHORT).show();
            theme = ThemeNameEnum.DEFAULT;
        }
        return theme;
    }

    public String code() {
        return name;
    }
}
