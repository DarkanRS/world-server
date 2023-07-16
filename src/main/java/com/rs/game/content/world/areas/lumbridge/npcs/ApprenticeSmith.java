package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ApprenticeSmith extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 4904;

    public static NPCClickHandler ApprenticeSmith = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        
        case "Talk-to" -> e.getPlayer().startConversation(new ApprenticeSmith(e.getPlayer()));
    	}
    });


    public ApprenticeSmith(Player player) {
        super(player);
        addNPC(npcId, HeadE.CALM_TALK, "Would you like to learn about the basics of smithing?");
        addOptions(new Options() {
            @Override
            public void create() {
                if (!player.getInventory().containsItems(new Item(438, 1), new Item(436, 1))) {
                    option("Yes", new Dialogue()
                            .addNPC(npcId, HeadE.WORRIED, "You'll need to have mined some tin and copper ore to smelt first. Go see the mining tutor to the south if you're not sure how to do this.")
                    );
                } else {
                    option("Yes", new Dialogue()
                            .addNPC(npcId, HeadE.HAPPY_TALKING, "I see you have some ore with you to smelt, so let's get started.")
                            .addNPC(npcId, HeadE.CALM_TALK, "Click on the furnace to bring up a menu of metal bars you can try to make from your ore.")
                            .addNPC(npcId, HeadE.CALM_TALK, "When you have a full inventory, take it to the bank, you can find it on the roof of the castle in Lumbridge.")
                            .addNPC(npcId, HeadE.CALM_TALK, "If you have a hammer with you, you can smith the bronze bars into equipment on the anvil.")
                            .addNPC(npcId, HeadE.HAPPY_TALKING, "Alternatively you can run up to Varrock. Look for my Master, the Smithing Tutor, in the west of the city, he can help you smith better gear.")
                    );
                }
                option("Not today.", new Dialogue()
                        .addPlayer(HeadE.SKEPTICAL, "Not today thanks.")
                        .addNPC(npcId, HeadE.LAUGH, "Okay, be sure to come ask me if you need some help.")
                );
            }
        });
    }
}