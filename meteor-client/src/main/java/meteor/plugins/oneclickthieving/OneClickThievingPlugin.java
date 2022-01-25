package meteor.plugins.oneclickthieving;

import com.google.inject.Provides;
import dev.hoot.api.entities.NPCs;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import meteor.chat.ChatColorType;
import meteor.chat.ChatMessageBuilder;
import meteor.chat.ChatMessageManager;
import meteor.chat.QueuedMessage;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@PluginDescriptor(
      name = "One Click Pickpocket",
      description = "QOL for pickpocketing",
      enabledByDefault = false
)

public class OneClickThievingPlugin extends Plugin
{
   @Inject
   private Client client;

   @Inject
   private ItemManager itemManager;

   @Inject
   private OneClickThievingConfig config;

   @Inject
   private ChatMessageManager chatMessageManager;

   @Inject
   private OverlayManager overlayManager;

   @Provides
   public OneClickThievingConfig getConfig(ConfigManager configManager)
   {
      return configManager.getConfig(OneClickThievingConfig.class);
   }

   @Override
   public void startup()
   {

   }

   @Override
   public void shutdown()
   {

   }

   Set<String> foodMenuOption = Set.of("Drink","Eat");
   Set<Integer> prayerPotionIDs = Set.of(139,141,143,2434,3024,3026,3028,3030);
   Set<Integer> foodBlacklist = Set.of(139,141,143,2434,3024,3026,3028,3030,24774);
   Set<Integer> coinPouches = Set.of(22521,22522,22523,22524,22525,22526,22527,22528,22529,22530,22531,22532,22533,22534,22535,22536,22537,22538,24703);
   private boolean shouldHeal = false;
   private int prayerTimeOut = 0;

   @Subscribe
   public void onChatMessage(ChatMessage event)
   {
      if(event.getMessage().contains("You have run out of prayer points"))
      {
         prayerTimeOut = 0;
      }

   }

   @Subscribe
   public void onStatChanged(StatChanged event)
   {
      if(event.getSkill() == Skill.PRAYER && event.getBoostedLevel() == 0 && prayerTimeOut == 0)
      {
         prayerTimeOut = 10;
      }
   }

   @Subscribe
   public void onMenuOptionClicked(MenuOptionClicked event)
   {
      method2(event);
   }

   @Subscribe
   public void onGameTick(GameTick event)
   {
      if(prayerTimeOut>0)
      {
         prayerTimeOut--;
      }

      if (client.getBoostedSkillLevel(Skill.HITPOINTS) >= Math.min(client.getRealSkillLevel(Skill.HITPOINTS),config.HPTopThreshold()))
      {
         shouldHeal = false;
      }
      else if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= Math.max(5,config.HPBottomThreshold()))
      {
         shouldHeal = true;
      }
   }

   private void method2(MenuOptionClicked event)
   {
      if(config.clickOverride()) {
         if (NPCs.getNearest(config.npcID()) != null) {
            NPC npc = NPCs.getNearest(config.npcID());
            event.setMenuEntry(new MenuEntry("Pickpocket","<col=ffff00>"+npc.getName()+"<col=ff00>  (level-"+npc.getCombatLevel()+")",npc.getIndex(),MenuAction.NPC_THIRD_OPTION.getId(),0,0,false));
            switch (npc.getActions().indexOf("Pickpocket")) {
               case 0:
                  event.setMenuAction(MenuAction.NPC_FIRST_OPTION);
                  break;
               case 1:
                  event.setMenuAction(MenuAction.NPC_SECOND_OPTION);
                  break;
               case 2:
                  event.setMenuAction(MenuAction.NPC_THIRD_OPTION);
                  break;
               case 3:
                  event.setMenuAction(MenuAction.NPC_FOURTH_OPTION);
                  break;
               case 4:
                  event.setMenuAction(MenuAction.NPC_FIFTH_OPTION);
                  break;
               default:
                  client.addChatMessage(ChatMessageType.GAMEMESSAGE, "oneClickThieving", "did not find pickpocket option on npc check configs and enable plugin again", null);
                  event.consume();
                  this.toggle();
                  return;
            }
         }else{
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "oneClickThieving", "npc not found please change the id and enable plugin again", null);
            event.consume();
            this.toggle();
            return;
         }
      }
      if (config.disableWalk() && event.getMenuOption().equals("Walk here"))
      {
         event.consume();
         return;
      }

      if (!event.getMenuOption().equals("Pickpocket"))
      {
         return;
      }

      //dodgy necklace
      if(config.enableNecklace() && getWidgetItem(21143) != null && !isItemEquipped(List.of(21143)))
      {
         event.setMenuEntry(new MenuEntry("Wear", "Wear", 21143, MenuAction.ITEM_SECOND_OPTION.getId(), getWidgetItem(21143).getSlot(), 9764864, false));
         return;
      }

      WidgetItem coinpouch = getWidgetItem(coinPouches);
      if (config.enableCoinPouch() && coinpouch != null && coinpouch.getQuantity() == 28)
      {
         event.setMenuEntry(new MenuEntry("Open-all", "Open-all", coinpouch.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), coinpouch.getSlot(), 9764864, false));
         return;
      }

      if(config.enableHeal() && shouldHeal)
      {
         WidgetItem food = getItemMenu(foodMenuOption,foodBlacklist);
         if (config.haltOnLowFood() && food == null)
         {
            event.consume();
            sendGameMessage("You are out of food");
            return;
         }
         else if (food != null)
         {
            String[] foodMenuOptions = itemManager.getItemComposition(food.getId()).getInventoryActions();
            event.setMenuEntry(new MenuEntry(foodMenuOptions[0], foodMenuOptions[0], food.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), food.getSlot(), 9764864, false));
            return;
         }
      }

      if(config.enableSpell())
      {
         //check spellbook
         if(client.getVarbitValue(4070) != 3)
         {
            event.consume();
            sendGameMessage("You are on the wrong spellbook");
            return;
         }

         if(client.getVarbitValue(12414) == 0)
         {
            event.setMenuEntry(new MenuEntry("Cast", "<col=00ff00>Shadow Veil</col>", 1, MenuAction.CC_OP.getId(), -1, 14287024, false));
            return;
         }
      }

      if(config.enablePray())
      {
         if (client.getBoostedSkillLevel(Skill.PRAYER) == 0 && prayerTimeOut == 0)
         {
            WidgetItem prayerPotion = getWidgetItem(prayerPotionIDs);
            if (prayerPotion != null)
            {
               event.setMenuEntry(new MenuEntry("Drink", "Drink", prayerPotion.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), prayerPotion.getSlot(), 9764864, false));
               return;
            }
         }

         //if redemption is off
         if(client.getVarbitValue(4120) == 0 && client.getBoostedSkillLevel(Skill.PRAYER) > 0 )
         {
            if(config.prayMethod() == PrayMethod.LAZY_PRAY)
            {
               event.setMenuEntry(new MenuEntry("Activate", "<col=ff9040>Redemption</col>", 1, MenuAction.CC_OP.getId(), -1, 35454997, false));
               return;
            }
            else if(config.prayMethod() == PrayMethod.REACTIVE_PRAY && shouldPray())
            {
               event.setMenuEntry(new MenuEntry("Activate", "<col=ff9040>Redemption</col>", 1, MenuAction.CC_OP.getId(), -1, 35454997, false));
               return;
            }
         }
      }
   }

   private boolean shouldPray()
   {
      return (client.getBoostedSkillLevel(Skill.HITPOINTS) < 11);
   }

   public boolean isItemEquipped(Collection<Integer> itemIds) {
      assert client.isClientThread();

      ItemContainer equipmentContainer = client.getItemContainer(InventoryID.EQUIPMENT);
      if (equipmentContainer != null) {
         Item[] items = equipmentContainer.getItems();
         for (Item item : items) {
            if (itemIds.contains(item.getId())) {
               return true;
            }
         }
      }
      return false;
   }

   private int getRandomIntBetweenRange(int min, int max) {
      //return (int) ((Math.random() * ((max - min) + 1)) + min); //This does not allow return of negative values
      return ThreadLocalRandom.current().nextInt(min, max + 1);
   }

   public WidgetItem getWidgetItem(Collection<Integer> ids) {
      Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
      if (inventoryWidget != null) {
         Collection<WidgetItem> items = inventoryWidget.getWidgetItems();
         for (WidgetItem item : items) {
            if (ids.contains(item.getId())) {
               return item;
            }
         }
      }
      return null;
   }

   private WidgetItem getWidgetItem(int id) {
      Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
      if (inventoryWidget != null) {
         Collection<WidgetItem> items = inventoryWidget.getWidgetItems();
         for (WidgetItem item : items) {
            if (item.getId() == id) {
               return item;
            }
         }
      }
      return null;
   }

   public WidgetItem getItemMenu(Collection<String>menuOptions, Collection<Integer> ignoreIDs) {
      Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
      if (inventoryWidget != null) {
         Collection<WidgetItem> items = inventoryWidget.getWidgetItems();
         for (WidgetItem item : items) {
            if (ignoreIDs.contains(item.getId())) {
               continue;
            }
            String[] menuActions = itemManager.getItemComposition(item.getId()).getInventoryActions();
            for (String action : menuActions) {
               if (action != null && menuOptions.contains(action)) {
                  return item;
               }
            }
         }
      }
      return null;
   }

   private WidgetItem getItemMenu(Collection<String> menuOptions) {
      Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
      if (inventoryWidget != null) {
         Collection<WidgetItem> items = inventoryWidget.getWidgetItems();
         for (WidgetItem item : items) {
            String[] menuActions = itemManager.getItemComposition(item.getId()).getInventoryActions();
            for (String action : menuActions) {
               if (action != null && menuOptions.contains(action)) {
                  return item;
               }
            }
         }
      }
      return null;
   }

   public void sendGameMessage(String message) {
      String chatMessage = new ChatMessageBuilder()
              .append(ChatColorType.HIGHLIGHT)
              .append(message)
              .build();

      chatMessageManager
              .queue(QueuedMessage.builder()
                      .type(ChatMessageType.CONSOLE)
                      .runeLiteFormattedMessage(chatMessage)
                      .build());
   }

}
