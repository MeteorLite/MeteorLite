package meteor.ui.themes;

import com.formdev.flatlaf.IntelliJTheme;
import kotlin.jvm.internal.DefaultConstructorMarker;
import lombok.SneakyThrows;


import java.util.Objects;


public final class MeteorliteTheme extends IntelliJTheme.ThemeLaf {



    private MeteorliteTheme(IntelliJTheme theme) {
        super(theme);
    }


    public MeteorliteTheme(IntelliJTheme theme, DefaultConstructorMarker $constructor_marker) {
        this(theme);
    }


        @SneakyThrows
        public static void install() {
            MeteorliteTheme theme = new MeteorliteTheme(new IntelliJTheme(Objects.requireNonNull(MeteorliteTheme.class.getResourceAsStream("/themes/meteorlite.theme.json"))), null);
            IntelliJTheme.ThemeLaf.setup(theme);
        }

}
