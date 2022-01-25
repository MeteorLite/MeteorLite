package meteor.plugins.gearhelper;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.items.Bank;
import dev.hoot.api.packets.ItemPackets;
import dev.hoot.api.packets.MousePackets;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import com.google.inject.Inject;
import net.runelite.api.widgets.WidgetInfo;
import java.util.Arrays;
import java.util.List;

@PluginDescriptor(
        name = "Gear Helper",
        enabledByDefault = false
)
public class GearHelperPlugin extends Plugin {
    @Inject
    Client client;
    int index = -1;
    @Inject
    private GearHelperConfig config;

    @Provides
    public GearHelperConfig getConfig(ConfigManager manager)
    {
        return manager.getConfig(GearHelperConfig.class);
    }
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        System.out.println(index);
        if(config==null||this.config.fastEquip()==null){
            return;
        }
        if(index==-1){
            return;
        }
        List<String> itemIDs = Arrays.asList(config.fastEquip().trim().split(","));
        if (e.getParam1() == WidgetInfo.BANK_ITEM_CONTAINER.getPackedId()) {
            Item item = Bank.getFirst(x -> x.getSlot() == e.getParam0());
            if(itemIDs.contains(Integer.toString(item.getId()))){
                MousePackets.queueClickPacket(0,0);
                Bank.withdraw(item.getId(),1, Bank.WithdrawMode.ITEM);
                MousePackets.queueClickPacket(0,0);
                ItemPackets.queueBankItemActionPacket(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(),item.getId(), index);
                e.consume();
            }
        }
    }
    @Subscribe
    public void onInventChanged(ItemContainerChanged e){
        if(e.getContainerId()==InventoryID.INVENTORY.getId()){
            if(e.getItemContainer()!=null){
                for (int i = 0; i < e.getItemContainer().getItems().length; i++) {
                    if(e.getItemContainer().getItem(i)==null){
                        index = i;
                        return;
                    }else{
                        if(e.getItemContainer().getItem(i).getId()==-1){
                            index = i;
                            return;
                        }
                    }
                }
            }
        }
    }
}
