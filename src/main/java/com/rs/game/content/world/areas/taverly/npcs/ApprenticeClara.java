package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class ApprenticeClara extends Conversation {
    private static final int npcId = 14906;

    public static NPCInteractionDistanceHandler rcShopsDistance = new NPCInteractionDistanceHandler(new Object[] { 14906, 14872 }, (p, n) -> 2);

    public ApprenticeClara(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "So many runes...");
        addPlayer(HeadE.ROLL_EYES, "Are you alright?");
        addNPC(npcId, HeadE.HAPPY_TALKING,
                "Oh! Don't worry. I'm trying to learn about runes from Mistress Carwen. It's a fascinating subject!");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need Runecrafting supplies", () -> {
                    ShopsHandler.openShop(player, "carwens_rune_shop");
                });
                option("What can you tell me about Runecrafting?", new Dialogue().addNPC(npcId,
                                HeadE.HAPPY_TALKING,
                                "There's so much to talk about! I'm just learning the ropes though. You'd be"
                                        + " better off talking to Mistress Carwen. As part of my duties I do help her by selling runes. Would you like to"
                                        + " take a look?")
                        .addPlayer(HeadE.CALM, "I'll have a look.").addNext(() -> {
                            ShopsHandler.openShop(player, "carwens_rune_shop");
                        }));
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler ApprenticeClaraHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new ApprenticeClara(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "carwens_rune_shop");
    });

}
