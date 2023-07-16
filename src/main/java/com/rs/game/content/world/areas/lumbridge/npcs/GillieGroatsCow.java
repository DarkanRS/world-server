package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
@QuestHandler(Quest.COOKS_ASSISTANT)
public class GillieGroatsCow extends Conversation {
    //Identify Cow by ID
    private static int objId = 47721;
    //Identify Gillie by ID
    private static final int npcId = 3807;

    public static ObjectClickHandler handleOptions = new ObjectClickHandler(new Object[] {objId}, e -> {
    	 if (e.getOption().equals("Milk")) {
             e.getPlayer().startConversation(new GillieGroatsCow(e.getPlayer()));
         }
    });

    public GillieGroatsCow(Player player) {
        super(player);
        //Identify NPC by ID
        if(player.getQuestManager().getStage(Quest.COOKS_ASSISTANT) != 1) {
            addNPC(npcId, HeadE.ANGRY, "Hands off, That milk is for the Duke!");
            create();
        }
        else
            player.getInventory().addItem(new Item(1927, 1));
    }
}

//TODO handle steal cowbell

