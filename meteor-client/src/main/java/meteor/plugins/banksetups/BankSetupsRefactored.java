package meteor.plugins.banksetups;

import com.google.gson.reflect.TypeToken;
import dev.hoot.api.commons.FileUtil;
import dev.hoot.api.game.GameThread;
import dev.hoot.api.items.Bank;
import dev.hoot.api.items.Equipment;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.packets.DialogPackets;
import dev.hoot.api.packets.ItemPackets;
import dev.hoot.api.packets.MousePackets;
import dev.hoot.api.packets.WidgetPackets;
import dev.hoot.api.widgets.Dialog;
import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.game.chatbox.ChatboxPanelManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.overlay.OverlayManager;
import net.runelite.api.Item;
import net.runelite.api.KeyCode;
import net.runelite.api.Point;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.queries.BankItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@PluginDescriptor(name = "Bank Setups", description = "Allows saving and loading bank setups. Hold shift to remove setups. Not yet working with noted items :(")
public class BankSetupsRefactored extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BankSetupsOverlay overlay;
    @Inject
    ItemManager itemManager;
    int dialogCloseTickCounter;
    List<BankSetupObject> bankSetups = new ArrayList<>();

    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    private ExecutorService executor;

    @Override
    public void startup() {
        executor = Executors.newSingleThreadExecutor();
        overlayManager.add(overlay);

        if (!FileUtil.exists(this, "bankSetups.json")) {
            FileUtil.writeJson(this, "bankSetups.json", bankSetups);
        } else {
            bankSetups = FileUtil.readJson(this, "bankSetups.json", new TypeToken<>() {});
        }
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if (e.getMenuOption().equals("save-current")) {
            e.consume();
            List<int[]> invent = getInvent();
            nameInput(invent, null);
        }

        if (e.getMenuOption().equals("save-current+equipment")) {
            e.consume();
            List<int[]> invent = getInvent();
            List<int[]> equipment = getEquipment();
            nameInput(invent, equipment);
        }

        if (e.getMenuOption().contains("Gear: ")) {
            e.consume();
            int numActions = 0;
            BankSetupObject setup = bankSetups.get(e.getId());
            if (setup.equipment != null) {
                int firstFree = getFirstEmptySlot();
                for (int[] equipment : setup.equipment) {
                    Widget item = getBankItemWidget(equipment[0]);
                    if (item == null) {
                        int y = itemManager.canonicalize(equipment[0]);
                        item = getBankItemWidget(y);
                        if (item == null) {
                            continue;
                        }
                    }

                    MousePackets.queueClickPacket(0, 0);
                    if (equipment[1] != 1) {
                        WidgetPackets.widgetAction(item, "Withdraw-X");
                        DialogPackets.sendNumberInput(equipment[1]);
                        MousePackets.queueClickPacket(0, 0);
                        ItemPackets.queueBankItemActionPacket(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), item.getItemId(), firstFree);
                        numActions += 3;
                        continue;
                    }

                    WidgetPackets.widgetAction(item, "Withdraw-1");
                    MousePackets.queueClickPacket(0, 0);
                    ItemPackets.queueBankItemActionPacket(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), item.getItemId(), firstFree);
                    numActions += 2;
                }
            }

            for (int[] ints : setup.inventory) {
                Widget item = getBankItemWidget(ints[0]);
                if (item == null) {
                    continue;
                }

                MousePackets.queueClickPacket(0, 0);
                if (ints[1] != 1) {
                    WidgetPackets.widgetAction(item, "Withdraw-X");
                    DialogPackets.sendNumberInput(ints[1]);
                    numActions += 2;
                    continue;
                }

                WidgetPackets.widgetAction(item, "Withdraw-1");
                numActions += 1;
            }

            int numTicks = ((int) Math.ceil(numActions / 10.0)) + 1;
            dialogCloseTickCounter = numTicks;
        }

        if (e.getMenuOption().contains("Remove: ")) {
            e.consume();
            bankSetups.remove(e.getId());
            FileUtil.writeJson(this, "bankSetups.json", bankSetups);
        }
    }

    private void nameInput(List<int[]> invent, List<int[]> equipment) {
        chatboxPanelManager.openTextInput("Setup Name")
                .value("")
                .onDone((input) ->
                {
                    input = input.strip();
                    bankSetups.add(new BankSetupObject(input, invent, equipment));
                    FileUtil.writeJson(this, "bankSetups.json", bankSetups);
                })
                .build();
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (dialogCloseTickCounter > 0) {
            dialogCloseTickCounter--;
        }
    }

    @Subscribe
    public void onClientTick(ClientTick e) {
        if (dialogCloseTickCounter > 0) {
            if (Dialog.isEnterInputOpen()) {
                GameThread.invoke(() -> client.runScript(138));
            }
        }

        Widget incinerator = client.getWidget(WidgetInfo.BANK_INCINERATOR);
        if (incinerator != null) {
            if (!incinerator.isHidden()) {
                Point p = incinerator.getCanvasLocation();
                int x = p.getX();
                int y = p.getY();
                overlay.setPreferredLocation(new java.awt.Point(x, y - (incinerator.getHeight() + 10)));
            }
        }

        Point mousePoint = client.getMouseCanvasPosition();
        if (overlay.getBounds().contains(mousePoint.getX(), mousePoint.getY())) {
            client.insertMenuItem("save-current", "", 10000000, 10000, 0, 0, false);
            client.insertMenuItem("save-current+equipment", "", 10000000, 10000, 0, 0, false);
            for (int i = 0; i < bankSetups.size(); i++) {
                if (client.isKeyPressed(KeyCode.KC_SHIFT)) {
                    client.insertMenuItem("Remove: " + bankSetups.get(i).name, "", 10000000, i, 0, 0, false);
                    continue;
                }
                client.insertMenuItem("Gear: " + bankSetups.get(i).name, "", 10000000, i, 0, 0, false);
            }
        }
    }

    public List<int[]> getInvent() {
        List<Item> items = Inventory.getAll();
        return getItems(items);
    }

    public List<int[]> getEquipment() {
        List<Item> items = Equipment.getAll();
        return getItems(items);
    }

    public List<int[]> getItems(List<Item> items) {
        List<int[]> retItems = new ArrayList<int[]>();
        int currItemID = -1;
        int currQuantity = -1;
        for (Item item : items) {
            if (item.getId() != currItemID) {
                if (currItemID != -1) {
                    retItems.add(new int[]{currItemID, currQuantity});
                }

                currItemID = item.getId();
                currQuantity = item.getQuantity();
                if (items.size() - 1 == items.indexOf(item)) {
                    retItems.add(new int[]{currItemID, currQuantity});
                }

                continue;
            }

            currQuantity++;
            if (items.size() - 1 == items.indexOf(item)) {
                retItems.add(new int[]{currItemID, currQuantity});
            }
        }
        return retItems;
    }

    public Widget getBankItemWidget(int id) {
        WidgetItem bankItem = new BankItemQuery().idEquals(id).result(client).first();
        if (bankItem != null) {
            return bankItem.getWidget();
        } else {
            return null;
        }
    }

    private int getFirstEmptySlot() {
        List<Item> items = Bank.getInventory(x -> true);
        int lastIndex = 0;
        if (items.size() == 0) {
            return lastIndex;
        }

        for (Item item : items) {
            if ((item.getSlot() - 1) > lastIndex) {
                return lastIndex + 1;
            }
            lastIndex = item.getSlot();
        }

        if (lastIndex != 27) {
            return lastIndex + 1;
        }

        return -1;
    }
}
