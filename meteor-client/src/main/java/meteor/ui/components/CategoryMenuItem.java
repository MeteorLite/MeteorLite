package meteor.ui.components;

import javafx.scene.control.MenuItem;
import meteor.plugins.Plugin;

public class CategoryMenuItem extends MenuItem {
  
  public Category category;
  public Plugin plugin;

  public CategoryMenuItem(Category category, Plugin plugin, String s) {
    super(s);
    this.category = category;
  }
}
