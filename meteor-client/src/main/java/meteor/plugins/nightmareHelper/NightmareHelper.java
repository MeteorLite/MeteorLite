/*
 * Copyright (c) 2019 Hydrox6 <ikada@protonmail.ch>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package meteor.plugins.nightmareHelper;

import javax.inject.Inject;

import dev.hoot.api.widgets.Prayers;
import net.runelite.api.*;
import net.runelite.api.events.*;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "Nightmare Auto Pray",
        description = "Auto prayer for nightmare and Phosani",
        tags = {},
        enabledByDefault = false
)
public class NightmareHelper extends Plugin {
    NPC nm;
    boolean cursed;
    private static final int NIGHTMARE_MELEE_ATTACK = 8594;
    private static final int NIGHTMARE_RANGE_ATTACK = 8596;
    private static final int NIGHTMARE_MAGIC_ATTACK = 8595;
    boolean inFight = false;
    @Inject
    private Client client;
    @Override
    public void startUp() {
        reset();
    }
    @Override
    public void shutDown(){
        reset();
    }
    private void reset()
    {
        inFight = false;
        nm = null;
        cursed = false;
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (!inFight || nm == null || event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }
        if (event.getMessage().toLowerCase().contains("the nightmare has cursed you, shuffling your prayers!"))
        {
            cursed = true;
        }

        if (event.getMessage().toLowerCase().contains("you feel the effects of the nightmare's curse wear off."))
        {
            cursed = false;
        }
    }
    private void activateMage() {
        if(!Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC)) {
            Prayers.toggle(Prayer.PROTECT_FROM_MAGIC);
        }
    }
    private void activateRange() {
        if(!Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES)) {
            Prayers.toggle(Prayer.PROTECT_FROM_MISSILES);
        }
    }
    private void activateMelee() {
        if(!Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE)) {
            Prayers.toggle(Prayer.PROTECT_FROM_MELEE);
        }
    }
    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        GameState gamestate = event.getGameState();

        //if loading happens while inFight, the user has left the area (either via death or teleporting).
        if (gamestate == GameState.LOADING && inFight)
        {
            reset();
        }
    }
    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        Actor actor = event.getActor();
        if (!(actor instanceof NPC))
        {
            return;
        }

        NPC npc = (NPC) actor;

        // this will trigger once when the fight begins
        if (nm == null && npc.getName() != null && (npc.getName().equalsIgnoreCase("The Nightmare") || npc.getName().equalsIgnoreCase("Phosani's Nightmare")))
        {
            //reset everything
            nm = npc;
            inFight = true;
        }

        if (!inFight || !npc.equals(nm))
        {
            return;
        }
        int animID = nm.getAnimation();
        if(animID==NIGHTMARE_MAGIC_ATTACK){
            if(!cursed){
                activateMage();
            }else{
                activateMelee();
            }
        }else if(animID==NIGHTMARE_MELEE_ATTACK){
            if(!cursed){
                activateMelee();
            }else{
                activateRange();
            }
        }else if(animID==NIGHTMARE_RANGE_ATTACK){
            if(!cursed){
                activateRange();
            }else{
                activateMage();
            }
        }
    }
}
