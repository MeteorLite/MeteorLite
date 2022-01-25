package meteor.plugins.blackjack;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.movement.Reachable;
import net.runelite.api.*;
import net.runelite.api.events.*;

@PluginDescriptor(
        name = "Blackjack",
        enabledByDefault = false,
        description = "makes all of your clicks do the correct blackjack action on the nearest blackjackable npc. supports bandits and menaphite thugs. turn the plugin on after trapping the npc"
)
public class BlackjackPlugin extends Plugin {

    private State state = State.KNOCKOUT;
    private int nextKnockOutTick;
    NPC npc;
    @Override
    public void startup(){
        npc = null;
    }
    @Override
    public void shutdown(){
        npc = null;
    }
    @Subscribe(priority = 1000)
    public void onMenuOptionClicked(MenuOptionClicked event){
        if(npc==null){
            npc= NPCs.getNearest("Menaphite Thug","Bandit");
        }
        if(npc==null){
            return;
        }
        if(npc.getWorldLocation()==null){
            npc=null;
            return;
        }
        if(!Reachable.isInteractable(npc)){
            npc=null;
            return;
        }
        if(state == State.KNOCKOUT){
            event.setMenuEntry(new MenuEntry("Knock-Out","<col=ffff00>"+npc.getName()+"<col=ff00>  (level-"+npc.getCombatLevel()+")",npc.getIndex(), MenuAction.NPC_FIFTH_OPTION.getId(),0,0,false));
        }else{
            event.setMenuEntry(new MenuEntry("Pickpocket","<col=ffff00>"+npc.getName()+"<col=ff00>  (level-"+npc.getCombatLevel()+")",npc.getIndex(), MenuAction.NPC_THIRD_OPTION.getId(),0,0,false));
        }
    }
    @Subscribe
    private void onClientTick(ClientTick event)
    {
        if (client.getTickCount() >= nextKnockOutTick)
        {
            state = State.KNOCKOUT;
        }
    }
    public enum State
    {
        KNOCKOUT,
        PICKPOCKET
    }
    @Provides
    public BlackjackConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(BlackjackConfig.class);
    }
    @Subscribe
    private void onChatMessage(ChatMessage event){
        final String msg = event.getMessage();
        if (event.getType() == ChatMessageType.SPAM && (msg.equals("You smack the bandit over the head and render them unconscious."))){
            state = State.PICKPOCKET;
            nextKnockOutTick = client.getTickCount() + 4;
        }
    }
}


