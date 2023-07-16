package com.rs.game.content.clans.clanCamp.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CaptainOfTheGuard extends Conversation {

    private static final int npcId = 13633;

    private boolean hasClan;

    private String title = player.getAppearance().isMale() ? "sir" : "ma'am";

    public static NPCClickHandler CaptainOfTheGuard = new NPCClickHandler(new Object[] { npcId }, e -> {
        String t = e.getPlayer().getAppearance().isMale() ? "sir" : "ma'am";
        if (e.getOption().equalsIgnoreCase("talk-to")) {
            e.getPlayer().startConversation(new CaptainOfTheGuard(e.getPlayer()));
        }
        if (e.getOption().equalsIgnoreCase("get-cloak")) {
            if(e.getPlayer().getClan() == null){
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "You must be in a clan to have a clan cloak, " + t + ".")
                );
                return;
            }
            if (!e.getPlayer().getInventory().hasFreeSlots()){
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Unfortunately you do not have enough space for that, " + t + ".")
                );
                return;
            }
            if(e.getPlayer().getInventory().hasFreeSlots() && e.getPlayer().getInventory().containsItem(20708)) {
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "You can only have one clan cloak, " + t + ". Those are the rules")
                );
                return;
            }
            else {
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Certainly. Here you are..")
                );
                e.getPlayer().getInventory().addItem(20708);
                e.getPlayer().sendMessage("The captain gives you a cloak.");
            }
        }
    });


    public CaptainOfTheGuard(Player player) {
        super(player);
        if(player.getClan() != null)
            hasClan = true;
        if(!hasClan)
            player.startConversation(new Dialogue()
                    .addNPC(npcId, HeadE.HAPPY_TALKING, "Alf A Numerius at your service, " + title +". It's my job to give out clan cloaks. If you'd like to know more about clans, talk to the clan scribe.")
            );
        else
            player.startConversation(new Dialogue()
                    .addNPC(npcId, HeadE.HAPPY_TALKING, "Need something, " + title + "?")
                    .addOptions(ops -> {

                        ops.add("I'd like a clan cloak.")
                                .addNext(() -> {
                                    if (!player.getInventory().hasFreeSlots()){
                                        player.startConversation(new Dialogue()
                                                .addNPC(npcId, HeadE.HAPPY_TALKING, "Unfortunately you do not have enough space for that, " + title)
                                        );
                                        return;
                                    }
                                    if(player.getInventory().hasFreeSlots() && player.getInventory().containsItem(20708))
                                        player.startConversation(new Dialogue()
                                                .addNPC(npcId, HeadE.HAPPY_TALKING, "You can only have one clan cloak, " + title + ". Those are the rules")
                                        );
                                    else {
                                        player.startConversation(new Dialogue()
                                                .addNPC(npcId, HeadE.HAPPY_TALKING, "Certainly. Here you are " + title + ".")
                                        );
                                        player.getInventory().addItem(20708);
                                        player.sendMessage("The captain gives you a cloak.");
                                    }

                                });

                        ops.add("Goodbye")
                                .addNPC(npcId, HeadE.HAPPY_TALKING, "Goodbye, " + title + ".");
                    })
            );
    }

}
