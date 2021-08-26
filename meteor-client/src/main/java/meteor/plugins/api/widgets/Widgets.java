package meteor.plugins.api.widgets;

import meteor.plugins.api.game.Game;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class Widgets {

    public static Widget get(WidgetInfo widgetInfo) {
        return Game.getClient().getWidget(widgetInfo);
    }

    public static Widget get(int group, int id) {
        return Game.getClient().getWidget(group, id);
    }

    public static Widget get(int group, int id, int child) {
        return get(group, id) == null ? null : get(group, id).getChild(child);
    }

    public static List<Widget> get(int group) {
        Widget[] widgets = Game.getClient().getWidgets()[group];
        if (widgets == null) {
            return null;
        }

        return Arrays.asList(widgets);
    }

    public static Widget fromId(int packedId) {
        return Game.getClient().getWidget(packedId);
    }
}
