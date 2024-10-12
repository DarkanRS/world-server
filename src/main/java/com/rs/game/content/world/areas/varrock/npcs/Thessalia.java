package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.PlayerLook;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Thessalia extends Conversation {
    public static final int ID = 548;
    public static NPCClickHandler ThessaliasMakeOver = new NPCClickHandler(new Object[] { 548 }, e -> {
        switch(e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Thessalia(e.getPlayer()));
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "thessalias_clothing");
            case "Change-clothes" -> PlayerLook.openThessaliasMakeOver(e.getPlayer());
        }
    });

    public Thessalia(Player player) {
        super(player);
        addNPC(ID, HeadE.CHEERFUL, "Would you like to buy any fine clothes?");
        addNPC(ID, HeadE.CHEERFUL, "Or if you're more after fancy dress costumes or commemorative capes, talk to granny Iffie.");
        addOptions(ops -> {
            ops.add("What do you have?")
                .addPlayer(HeadE.CONFUSED, "What do you have?")
                    .addNPC(ID, HeadE.CHEERFUL, "Well, I have a number of fine pieces of clothing on sale or, if you prefer, I can offer you an exclusive, total clothing makeover?")
                    .addOptions(ops2 -> {
                        ops2.add("Tell me more about this makeover.")
                                .addPlayer(HeadE.CONFUSED, "Tell me more about this makeover?")
                                .addNPC(ID, HeadE.CHEERFUL, "Certainly!")
                                .addNPC(ID, HeadE.CHEERFUL, "Here at Thessalia's Fine Clothing Boutique we offer a unique service, where we will totally revamp your outfit to your choosing. Tired of always wearing the same old outfit, day-in, day-out? Then this is the service for you!")
                                .addNPC(ID, HeadE.CONFUSED, "So, what do you say? Interested?")
                                        .addOptions(ops3 -> {
                                        ops3.add("I'd like to change my outfit, please.")
                                                .addPlayer(HeadE.CHEERFUL, "I'd like to change my outfit, please.")
                                                .addNPC(ID, HeadE.CHEERFUL, "Wonderful. Feel free to try on some items and see if there's anything you would like.")
                                                .addPlayer(HeadE.CHEERFUL, "Okay, thanks.");
                                        ops3.add("I'd just like to buy some clothes.", () -> ShopsHandler.openShop(player, "thessalias_clothing"));
                                        ops3.add("No, thank you.");
                                        });

            ops.add("No, thank you.");
            });
        });

    }
}
