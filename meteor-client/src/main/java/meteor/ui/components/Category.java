package meteor.ui.components;

import java.util.ArrayList;

public class Category {
    public String name;
    public boolean open;
    public ArrayList<String> plugins = new ArrayList<>();

  public Category(String name) {
    this.name = name;
  }
}
