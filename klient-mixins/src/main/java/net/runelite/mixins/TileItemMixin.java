package net.runelite.mixins;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import meteor.eventbus.events.ItemQuantityChanged;
import net.runelite.api.mixins.FieldHook;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Shadow;
import net.runelite.api.util.Text;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSTileItem;

@Mixin(RSTileItem.class)
public abstract class TileItemMixin implements RSTileItem {

  @Shadow("client")
  private static RSClient client;

  @Inject
  private int rl$sceneX = -1;

  @Inject
  private int rl$sceneY = -1;

  @Inject
  TileItemMixin() {
  }

  @Inject
  @Override
  public Tile getTile() {
    int x = rl$sceneX;
    int y = rl$sceneY;

    if (x == -1 || y == -1) {
      return null;
    }

    Tile[][][] tiles = client.getScene().getTiles();
    return tiles[client.getPlane()][x][y];
  }

  @Inject
  @Override
  public void onUnlink() {
    if (rl$sceneX != -1) {
      // on despawn, the first item unlinked is the item despawning. However on spawn
      // items can be delinked in order to sort them, so we can't assume the item here is despawning
      if (client.getLastItemDespawn() == null) {
        client.setLastItemDespawn(this);
      }
    }
  }

  @Inject
  @FieldHook(value = "quantity", before = true)
  public void quantityChanged(int quantity) {
    if (rl$sceneX != -1) {
      ItemQuantityChanged itemQuantityChanged = new ItemQuantityChanged(this, getTile(),
              getQuantity(), quantity);
      client.getCallbacks().post(ItemQuantityChanged.class, itemQuantityChanged);
    }
  }

  @Inject
  @Override
  public int getX() {
    return rl$sceneX;
  }

  @Inject
  @Override
  public void setX(int x) {
    rl$sceneX = x;
  }

  @Inject
  @Override
  public int getY() {
    return rl$sceneY;
  }

  @Inject
  @Override
  public void setY(int y) {
    rl$sceneY = y;
  }

  @Inject
  @Override
  public int getDistanceFromLocalPlayer() {
    //Mancrappen :tm:
    int distanceX;
    int distanceY;
    Tile localTile = client.getScene().getTiles()[client.getPlane()][rl$sceneX][rl$sceneY];
    LocalPoint tileLocalLocation = localTile.getLocalLocation();
    LocalPoint localPlayerPosition = client.getLocalPlayer().getLocalLocation();

    if (tileLocalLocation.getX() > localPlayerPosition.getX())
      distanceX = tileLocalLocation.getX() - localPlayerPosition.getX();
    else
      distanceX = localPlayerPosition.getX() - tileLocalLocation.getX();

    if (tileLocalLocation.getY() > localPlayerPosition.getY())
      distanceY = tileLocalLocation.getY() - localPlayerPosition.getY();
    else
      distanceY = localPlayerPosition.getY() - tileLocalLocation.getY();

    return (distanceX + distanceY) / 2;
  }

  @Override
  @Inject
  public int getActionId(int action) {
    switch (action) {
      case 0:
        return MenuAction.GROUND_ITEM_FIRST_OPTION.getId();
      case 1:
        return MenuAction.GROUND_ITEM_SECOND_OPTION.getId();
      case 2:
        return MenuAction.GROUND_ITEM_THIRD_OPTION.getId();
      case 3:
        return MenuAction.GROUND_ITEM_FOURTH_OPTION.getId();
      case 4:
        return MenuAction.GROUND_ITEM_FIFTH_OPTION.getId();
      default:
        throw new IllegalArgumentException("action = " + action);
    }
  }

  @Override
  @Inject
  public void interact(int action) {
    interact(getId(), getActionId(action));
  }

  @Override
  @Inject
  public void interact(String action) {
    interact(getActions().indexOf(action));
  }

  @Inject
  @Override
  public String[] getRawActions() {
    return getComposition().getGroundActions();
  }

  @Inject
  @Override
  public void interact(int identifier, int opcode, int param0, int param1) {
    Point screenCoords = getScreenCoords();
    int x = screenCoords != null ? screenCoords.getX() : -1;
    int y = screenCoords != null ? screenCoords.getY() : -1;

    client.interact(identifier, opcode, param0, param1, x, y);
  }

  @Inject
  @Override
  public void interact(int index, int menuAction) {
    interact(getId(), menuAction, getTile().getSceneLocation().getX(),
            getTile().getSceneLocation().getY());
  }

  @Override
  @Inject
  public void pickup() {
    interact(getId(), MenuAction.GROUND_ITEM_THIRD_OPTION.getId());
  }

  @Inject
  @Override
  public String getName() {
    return Text.removeTags(Text.sanitize(getComposition().getName()));
  }

  @Inject
  private ItemComposition getComposition() {
    return client.getItemComposition(getId());
  }

  @Inject
  @Override
  public boolean isTradable() {
    return getComposition().isTradeable();
  }

  @Inject
  @Override
  public boolean isStackable() {
    return getComposition().isStackable();
  }

  @Inject
  @Override
  public boolean isMembers() {
    return getComposition().isMembers();
  }

  @Inject
  @Override
  public int getNotedId() {
    return getComposition().getLinkedNoteId();
  }

  @Inject
  @Override
  public boolean isNoted() {
    return getComposition().getNote() > -1;
  }

  @Inject
  @Override
  public int getStorePrice() {
    return getComposition().getPrice();
  }

  @Inject
  @Override
  public String[] getInventoryActions() {
    return getComposition().getInventoryActions();
  }

  @Inject
  private Point getScreenCoords() {
    return Perspective.localToCanvas(client, getLocalLocation(), client.getPlane());
  }
}
