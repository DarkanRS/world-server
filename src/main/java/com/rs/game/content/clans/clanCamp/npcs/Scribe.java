package com.rs.game.content.clans.clanCamp.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.clans.ClansManager.promptName;

@PluginEventHandler
public class Scribe extends Conversation {

    private static final int npcId = 5915;

    private boolean hasClan;

    private int Vexilum = 20709;

    private String title = player.getAppearance().isMale() ? "sir" : "ma'am";

    public static NPCClickHandler Scribe = new NPCClickHandler(new Object[] { npcId }, e -> {
        String t = e.getPlayer().getAppearance().isMale() ? "sir" : "ma'am";
        if (e.getOption().equalsIgnoreCase("talk-to")) {
            e.getPlayer().startConversation(new Scribe(e.getPlayer()));
        }
        if (e.getOption().equalsIgnoreCase("get-vexillum")) {
            if(e.getPlayer().getClan() == null){
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "You must be in a clan to have a vexillum, " + t + ".")
                );
                return;
            }
            if (!e.getPlayer().getInventory().hasFreeSlots()){
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Unfortunately you do not have enough space for that, " + t + ".")
                );
                return;
            }
            if(e.getPlayer().getInventory().hasFreeSlots() && e.getPlayer().getInventory().containsItem(20709)) {
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "You can only have one clan vexillum, " + t + ". Those are the rules")
                );
                return;
            }
            else {
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Certainly, " + t + ". Here you are..")
                );
                e.getPlayer().getInventory().addItem(20709);
                e.getPlayer().sendMessage("The scribe gives you a vexillum.");
            }
        }
    });




    public Scribe(Player player) {
        super(player);
        if(player.getClan() != null)
            hasClan = true;
        if(!hasClan)
            player.startConversation(new Dialogue()
                    .addNPC(npcId, HeadE.HAPPY_TALKING, "Clan scribe Amos Twinly at your service, " + title +". I keep a record of all clans, and distribute clan charters to those who wish to start a clan. ")
                    .addNPC(npcId, HeadE.HAPPY_TALKING,  "If you are a member of a clan, I can also provide you with a vexillum - a banner displaying your clan's motif.")

                    .addOptions(clanOps -> {
                        clanOps.add("I'd like to charter a new clan")
                                .addNPC(npcId, HeadE.HAPPY_TALKING, "Splendid news " + title + ". What would you like your clan to be called?")
                                .addNext(() -> promptName(player));

                        clanOps.add("Goodbye")
                                .addNPC(npcId, HeadE.HAPPY_TALKING, "Goodbye");
                    }));
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
                                        player.sendMessage("The scribe gives you a clan cloak.");
                                    }

                                });

                        ops.add("Goodbye")
                                .addNPC(npcId, HeadE.HAPPY_TALKING, "Goodbye, " + title + ".");
                    })
            );
    }

}
