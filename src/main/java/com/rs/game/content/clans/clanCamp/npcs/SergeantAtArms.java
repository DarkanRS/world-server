package com.rs.game.content.clans.clanCamp.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SergeantAtArms extends Conversation {

    private static final int npcId = 5914;

    private boolean hasClan;

    public static NPCClickHandler SergeantAtArms = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to")) {
            e.getPlayer().startConversation(new SergeantAtArms(e.getPlayer()));
        }
        if (e.getOption().equalsIgnoreCase("badge")) {
            if(e.getPlayer().getClan() != null)
            e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING,"I need a Rated Clan Wars badge.")
                    .addNext(() -> {
                        if (e.getPlayer().getClan() != null) {
                            e.getPlayer().startConversation(new Dialogue()
                                    .addNPC(npcId, HeadE.SHAKING_HEAD, "You need to be in a clan to get a badge. I can't just give them out to just anyone. Talk to the clan scribe over there. He can help you set up a clan."));
                            return;
                        }
                        if (e.getPlayer().getClan() != null && e.getPlayer().getInventory().containsItem(20710)) {
                            e.getPlayer().startConversation(new Dialogue()
                                    .addNPC(npcId, HeadE.SHAKING_HEAD, "You've already got a badge. Don't waste my time."));
                            return;
                        }
                        if (e.getPlayer().getClan() != null && !e.getPlayer().getInventory().containsItem(20710)) {
                            if (e.getPlayer().getInventory().hasFreeSlots()) {
                                e.getPlayer().startConversation(new Dialogue()
                                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Sure, here you go."));
                                e.getPlayer().getInventory().addItem(20710, 1);
                                e.getPlayer().sendMessage("The sergeant hands you an RCW badge.");
                            }
                            else
                                e.getPlayer().startConversation(new Dialogue()
                                        .addNPC(npcId, HeadE.SHAKING_HEAD, "You are carrying enough already."));
                        }
                    })
            );

        }
    });


    public SergeantAtArms(Player player) {
        super(player);
        if(player.getClan() != null)
            hasClan = true;
        player.startConversation(new Dialogue()
                .addNPC(npcId, HeadE.HAPPY_TALKING, "Want something?")
                .addOptions(ops -> {

                    ops.add("Who are you?")
                            .addNPC(npcId, HeadE.CALM, "I'm the Sergeant-at-Arms. I organise Rated Clan Wars matches.")
                            .addPlayer(HeadE.CALM_TALK, "You look like you've seen combat.")
                            .addNPC(npcId, HeadE.UPSET_SNIFFLE, "I was here at the Siege of Falador. A lot of people died. I didn't.");

                    ops.add("I'd like to know more about Rated Clan Wars.")
                            .addNPC(npcId, HeadE.HAPPY_TALKING, "Rated Clan Wars is a pitched battle between two clans. You need to be in a clan to take part, and you need a Rated Clan Wars badge.")
                            .addNext(() -> {
                                if (player.getInventory().containsItem(20712)) {
                                    return;
                                } else {
                                    player.startConversation(new Dialogue()
                                            .addNPC(npcId, HeadE.CALM_TALK, "There's a rulebook if you want to know more.")
                                            .addOptions(ops1 -> {
                                                ops1.add("Sure, I'd like a copy")
                                                        .addNext(() -> {
                                                            if (player.getInventory().hasFreeSlots()) {
                                                                player.getInventory().addItem(20712, 1);
                                                                player.startConversation(new Dialogue()
                                                                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Sure, here you go."));
                                                            }
                                                            else
                                                                addNPC(npcId, HeadE.SHAKING_HEAD, "You are carrying enough already.");
                                                        });
                                                ops1.add("No Thanks");
                                            }));
                                }
                            });

                    ops.add("I need a Rated Clan Wars badge.")
                            .addNext(() -> {
                                if (!hasClan) {
                                    player.startConversation(new Dialogue()
                                            .addNPC(npcId, HeadE.SHAKING_HEAD, "You need to be in a clan to get a badge. I can't just give them out to just anyone. Talk to the clan scribe over there. He can help you set up a clan."));
                                    return;
                                }
                                if (hasClan && player.getInventory().containsItem(20710)) {
                                    player.startConversation(new Dialogue()
                                            .addNPC(npcId, HeadE.SHAKING_HEAD, "You've already got a badge. Don't waste my time."));
                                    return;
                                }
                                if (hasClan && !player.getInventory().containsItem(20710)) {
                                    if (player.getInventory().hasFreeSlots()) {
                                        player.getInventory().addItem(20710, 1);
                                        player.sendMessage("The sergeant hands you an RCW badge.");
                                        player.startConversation(new Dialogue()
                                                .addNPC(npcId, HeadE.HAPPY_TALKING, "Sure, here you go."));
                                    }
                                    else{
                                        player.startConversation(new Dialogue()
                                                .addNPC(npcId, HeadE.SHAKING_HEAD, "You are carrying enough already."));
                                    }
                                }
                            });


                    ops.add("No thanks.")
                            .addPlayer(HeadE.CONFUSED, "No thanks.");
                }));
    }
}

