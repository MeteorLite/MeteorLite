package net.runelite.mixins;

import meteor.eventbus.events.PostItemComposition;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSItemComposition;
import net.runelite.rs.api.RSModel;

@Mixin(RSItemComposition.class)
public abstract class ItemCompositionMixin implements RSItemComposition {

  private static final int DEFAULT_CUSTOM_SHIFT_CLICK_INDEX = -2;

  @Shadow("client")
  private static RSClient client;

  @Inject
  private int shiftClickActionIndex = DEFAULT_CUSTOM_SHIFT_CLICK_INDEX;

  @Inject
  private int modelOverride = -1;

  @Inject
  ItemCompositionMixin() {
  }

  @Override
  @Inject
  public void setModelOverride(int id) {
    modelOverride = id;
  }

  @Inject
  @Override
  public boolean isStackable() {
    return getIsStackable() != 0;
  }

  @Inject
  @Override
  public void setShiftClickActionIndex(int shiftClickActionIndex) {
    this.shiftClickActionIndex = shiftClickActionIndex;
  }

  @Copy("getShiftClickIndex")
  @Replace("getShiftClickIndex")
  public int copy$getShiftClickActionIndex() {
    return shiftClickActionIndex == DEFAULT_CUSTOM_SHIFT_CLICK_INDEX
        ? copy$getShiftClickActionIndex() : shiftClickActionIndex;
  }

  @Inject
  @Override
  public void resetShiftClickActionIndex() {
    shiftClickActionIndex = DEFAULT_CUSTOM_SHIFT_CLICK_INDEX;
  }

  @Copy("post")
  public void rs$post() {

  }

  @Replace("post")
  public void rl$post() {
    rs$post();
    final PostItemComposition event = new PostItemComposition(this);
    client.getCallbacks().post(PostItemComposition.class, event);
  }

  @Copy("getModel")
  @Replace("getModel")
  public RSModel copy$getModel(int quantity) {
    if (modelOverride == -1) {
      return copy$getModel(quantity);
    }

    return client.getRSItemDefinition(modelOverride).getModel$api(quantity);
  }

  @Inject
  @Override
  public int getHaPrice() {
    int price = getPrice();
    return (int) ((float) price * 0.6f);
  }
}
