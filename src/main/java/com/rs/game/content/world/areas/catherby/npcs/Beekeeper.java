package com.rs.game.content.world.areas.catherby.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Beekeeper extends Conversation {
    
        //Identify NPC by ID
        private static final int npcId = 8649;

        public static NPCClickHandler Beekeeper = new NPCClickHandler(new Object[]{npcId}, e -> {
        	switch (e.getOption()) {
            //Start Conversation
            case "Talk-To" -> e.getPlayer().startConversation(new Beekeeper(e.getPlayer()));
        	}
        });

        public Beekeeper(Player player) {
            super(player);

            player.startConversation(new Conversation(new Dialogue()
                    .addNPC(npcId, HeadE.CHEERFUL, "Hello! What do you think of my apiary? Nice, isn't it?")
                    .addPlayer(HeadE.SKEPTICAL, "You mean all these beehives?")
                    .addNPC(npcId, HeadE.CHEERFUL, "Yup! They're filled with bees. Also wax, and delicious honey too!")
                    .addNPC(npcId, HeadE.CHEERFUL, "You're welcome to help yourself to as much wax and honey as you like.")
                    .addNPC(npcId, HeadE.SKEPTICAL, "Oh, but you'll need some insect repellant - here.")
                    .addItemToInv(player, new Item(28, 1), "The beekeeper hands you some insect repellant.")
                    .addPlayer(HeadE.CHEERFUL, "Thank you!")
                    .addNPC(npcId, HeadE.ANGRY, "Leave the bees, though. The bees are mine!")
                    .addNPC(npcId, HeadE.CHEERFUL_EXPOSITION, "I love bees!")
                    .finish()));
        }
    }

