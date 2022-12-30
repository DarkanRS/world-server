package com.rs.game.content.dialogue.npc.lumbridge;

// Basic dialogue handler for linear text conversations with no choices.

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;


@PluginEventHandler
@QuestHandler(Quest.COOKS_ASSISTANT)

public class GillieGroatsCow extends Conversation {
    //Identify Cow by ID
    private static int objId = 47721;
    //Identify Gillie by ID
    private static int npcId = 3807;

    public static ObjectClickHandler handleOptions = new ObjectClickHandler(new Object[] {objId}) {
        @Override
        public void handle(ObjectClickEvent e) {
            if (e.getOption().equals("Milk")) {
                e.getPlayer().startConversation(new GillieGroatsCow(e.getPlayer()));
            }
        }
    };

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
