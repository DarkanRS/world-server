package com.rs.game.player.quests.handlers.piratestreasure;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.transportation.TravelMethods;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.player.dialogues.BoatingDialogue.getBoatForShip;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.*;

@PluginEventHandler
public class CustomsOfficerPiratesTreasureD extends Conversation {



    public CustomsOfficerPiratesTreasureD(Player p) {
        super(p);
        addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "Halt! I am going to need to inspect your items.");
        addPlayer(HeadE.WORRIED, "Can I journey on this ship?");
        addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "You need to be searched before you can board.");
        addOptions("Choose an option:", new Options() {
            @Override
            public void create() {
                if(p.getInventory().containsItem(RUM))
                    option("Search away, I have nothing to hide.", new Dialogue()
                            .addPlayer(HeadE.WORRIED, "Search away, I have nothing to hide.")
                            .addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "Aha, trying to smuggle rum are we?")
                            .addPlayer(HeadE.HAPPY_TALKING, "Umm... it's for personal use?")
                            .addSimple("The customs officer confiscates your rum.", () -> {
                                while(p.getInventory().containsItem(RUM))
                                    p.getInventory().removeItems(new Item(RUM, 1));
                            })
                            .addSimple("You will need to find some way to smuggle it off the island.")
                    );
                else
                    option("Search away, I have nothing to hide.", new Dialogue()
                            .addPlayer(HeadE.WORRIED, "Search away, I have nothing to hide.")
                            .addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "Well, you've got some odd stuff, but it's all legal. You can board...")
                            .addNext(()->{
                                Object[] attributes = getBoatForShip(player, CUSTOMS_OFFICER);
                                TravelMethods.Carrier ship = (TravelMethods.Carrier) attributes[0];
                                boolean returning = (Boolean) attributes[1];
                                TravelMethods.sendCarrier(player, ship, returning);
                            })
                    );

                option("You're not putting your hands on my things!", new Dialogue()
                    .addPlayer(HeadE.ANGRY, "You're not putting your hands on my things!")
                    .addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "You're not getting on this ship then."));
            }
        });


    }
}
