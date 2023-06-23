package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.PlayerLook;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Hairdresser extends Conversation {
    private static final int npcId = 598;

    public static NPCClickHandler Hairdresser = new NPCClickHandler(new Object[]{ npcId }, e -> {
        switch (e.getOption()) {
            
            case "Talk-to" -> e.getPlayer().startConversation(new Hairdresser(e.getPlayer()));
            case "Hair-cut" -> PlayerLook.openHairdresserSalon(e.getPlayer());
        }
    });

    public Hairdresser(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Good afternoon, " + player.getPronoun("sir", "madam") + ". Are you interested in a new haircut?");
        addOptions(new Options() {
            @Override
            public void create() {

                option("Yes, please.", new Dialogue()
                        .addNext(() -> {
                            PlayerLook.openHairdresserSalon(player);
                        }));
                option("No, thank you.", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "No, thank you.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Very well. Come back if you change your mind.")
                );

            }


        });
    }


}
