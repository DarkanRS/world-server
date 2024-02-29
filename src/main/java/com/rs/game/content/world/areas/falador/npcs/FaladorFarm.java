package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class FaladorFarm {

    public static NPCClickHandler HandleMilkSeller = new NPCClickHandler(new Object[]{ 11547 }, new String[] { "Talk-to" }, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();

        player.startConversation(new Dialogue()
                .addNPC(npc, HeadE.HAPPY_TALKING, "Would you like to buy some milk?")
                .addOptions((ops) -> {
                    ops.add("Sure.", () -> ShopsHandler.openShop(player, "milk_shop"));
                    ops.add("No, thanks.")
                            .addPlayer(HeadE.CALM_TALK, "No, thanks.")
                            .addNPC(npc, HeadE.HAPPY_TALKING, "If you change your mind, you know where we are.");
                }));
    });

    public static NPCClickHandler HandleSarah = new NPCClickHandler(new Object[]{ 2304 }, new String[] { "Talk-to" }, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();

        player.startConversation(new Dialogue()
                .addNPC(npc, HeadE.HAPPY_TALKING, "Would you like to buy some farming supplies?")
                .addOptions(ops -> {
                    ops.add("Let's see what you've got then.", () -> ShopsHandler.openShop(player, "sarahs_farming_shop"));
                    ops.add("No, thanks.")
                            .addPlayer(HeadE.CALM_TALK, "No, thanks.")
                            .addNPC(npc, HeadE.HAPPY_TALKING, "Okay. Fare well on your travels.");
                }));
    });

}
