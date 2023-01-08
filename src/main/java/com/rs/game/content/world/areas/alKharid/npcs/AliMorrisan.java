package com.rs.game.content.world.areas.alKharid.npcs;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

//TODO Rouge Trader miniquest and add shops

@PluginEventHandler
public class AliMorrisan extends Conversation {

    //Identify NPC by ID
    private static int npcId = 1862;

    public static NPCClickHandler AliMorrisan = new NPCClickHandler(new Object[]{npcId}) {
        @Override
        //Handle Right-Click
        public void handle(NPCClickEvent e) {
            switch (e.getOption()) {
                //Start Conversation
                case "Talk-to" -> e.getPlayer().startConversation(new AliMorrisan(e.getPlayer()));
            }
        }
    };

    public AliMorrisan(Player player) {
        super(player);
        addNPC(npcId,HeadE.SAD_CRYING, "My friend!");
        addNPC(npcId, HeadE.SAD_CRYING, "Can you help me?");
        addOptions(new Options() {
            @Override
            public void create() {

                option("Sure, how can I help?", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Sure, how can I help?")
                        .addNPC(npcId, HeadE.SAD_CRYING, "I have no more stock left!")
                        .addNPC(npcId, HeadE.SAD_CRYING, "If you can find me a supplier of Blackjacks, Clothes, and Runes I will give you a discount!")
                        .addPlayer(HeadE.HAPPY_TALKING, "I'll see what I can do.")
                );
                option("Not right now.", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "Not right now.")
                );

            }
        });
    }
}