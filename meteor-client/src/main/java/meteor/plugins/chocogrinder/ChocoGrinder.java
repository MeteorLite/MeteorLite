package meteor.plugins.chocogrinder;

import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.items.Bank;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.packets.ItemPackets;
import dev.hoot.api.packets.MousePackets;
import dev.hoot.api.packets.NPCPackets;
import dev.hoot.api.packets.WidgetPackets;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;

@PluginDescriptor(name = "Chocolate grinder", enabledByDefault = false)
public class ChocoGrinder extends Plugin {
    int gametick =0;
    Item knife;
    int failed =0;
    int chocoSlot =-1;
    boolean doNothing =false;
    @Override
    public void startup(){
        if(client.getGameState()== GameState.LOGGED_IN){
            knife = Inventory.getFirst(	946);
        }
        chocoSlot =-1;
        failed =0;
        gametick =0;
        doNothing=false;
    }
    public void clickPacket(){
        client.setMouseLastPressedMillis(System.currentTimeMillis());
        int mousePressedTime = (int)((client.getMouseLastPressedMillis()) - (client.getClientMouseLastPressedMillis()));
        if (mousePressedTime < 0) {
            return;
        }
        if (mousePressedTime > 32767) {
            mousePressedTime = 32767;
        }
        client.setClientMouseLastPressedMillis(client.getMouseLastPressedMillis());
        int mouseInfo = (mousePressedTime << 1) + (1);
        MousePackets.queueClickPacket(mouseInfo,0,0);
    }
    public void itemOnItemPacket(int id1,int slot1,int id2, int slot2){
        ItemPackets.queueItemOnItemPacket(id1,slot1,id2,slot2);
    }
    @Subscribe
    public void onGameTick(GameTick event) throws InterruptedException {
        if(client.getGameState()!= GameState.LOGGED_IN){
            return;
        }
        if(chocoSlot==-1){
            Item bars=Bank.getFirst("Chocolate bar");
            if(bars==null){
                this.toggle();
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "ChocoGrinder", "Either bank was not open or you did not having chocolate bars in bank", null);
                return;
            }
            chocoSlot=bars.getSlot();
        }
        if(knife==null){
            knife = Inventory.getFirst(	946);
            return;
        }
        if(gametick==0&&!doNothing){
            if(!Inventory.getAll(1975).isEmpty()) {
                clickPacket();
                WidgetPackets.queueWidgetAction2Packet(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(),1975,0);
            }
            clickPacket();
            WidgetPackets.queueWidgetActionPacket(WidgetInfo.BANK_ITEM_CONTAINER.getPackedId(),1973,chocoSlot);
            for (int i = 0; i < 27; i++) {
                clickPacket();
                itemOnItemPacket(1973,26,946,27);
            }
        }
        if(gametick==1){
            if(Inventory.getAll("Chocolate dust").size()==27&&!doNothing){
                doNothing=true;
                gametick=0;
                return;
            }
        }
        if(gametick==2){
            NPC bank = NPCs.getNearest("Banker");
            if (bank != null) {
                if(Inventory.getFreeSlots()>0){
                    failed++;
                    if(failed>2){
                        this.toggle();
                        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "ChocoGrinder", "Out of chocolate", null);
                        return;
                    }
                    clickPacket();
                    NPCPackets.queueNPCAction3Packet(bank.getIndex(),0);
                    return;
                }
                failed=0;
                clickPacket();
                NPCPackets.queueNPCAction3Packet(bank.getIndex(),0);
                doNothing=false;
            }
        }
        gametick++;
        if(gametick>=3){
            gametick=0;
        }
    }
}
