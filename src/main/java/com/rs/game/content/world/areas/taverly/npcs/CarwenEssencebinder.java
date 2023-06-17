package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.skills.runecrafting.RunecraftingAltar;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CarwenEssencebinder extends Conversation {
    private static final int npcId = 14872;

    public CarwenEssencebinder(Player player, NPC npc) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Welcome to my shop, friend. Are you here to practice runecrafting?");
        addPlayer(HeadE.ROLL_EYES, "Are you alright?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("Can you teleport me to the Essence Mine?", () -> {
                    RunecraftingAltar.handleEssTele(player, npc);
                });
                option("What can you tell me about Runecrafting?", new Dialogue()

                        .addNPC(npcId, HeadE.HAPPY_TALKING,"When you cast a spell, you tap into the latent power of runes.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"Naturally occuring runes are incredibly rare, but the recent rediscovered art of runecrafting allows us to create them ourselves.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,"By acquiring essence stones and infusing them at an elemental altar, you make it possible for mages to work their magic.")
                        .addPlayer(HeadE.CALM, "I'll have a look.")
                );
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler CarwenEssencebinderHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new CarwenEssencebinder(e.getPlayer(), e.getNPC()));
        if (e.getOption().equalsIgnoreCase("teleport"))
            RunecraftingAltar.handleEssTele(e.getPlayer(), e.getNPC());

    });

}
