package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Sanfew extends Conversation {

    final static int ENCHANTED_RAW_BEAR_MEAT = 524;
    final static int ENCHANTED_RAW_RAT_MEAT = 523;
    final static int ENCHANTED_RAW_CHICKEN = 525;
    final static int ENCHANTED_RAW_BEEF = 522;

    private final static int SANFEW = 454;
    static boolean hasEnchantedItems(Player player) {
        boolean hasItems = false;
        if(player.getInventory().containsItem(ENCHANTED_RAW_BEAR_MEAT) && player.getInventory().containsItem(ENCHANTED_RAW_RAT_MEAT) &&
                player.getInventory().containsItem(ENCHANTED_RAW_CHICKEN) && player.getInventory().containsItem(ENCHANTED_RAW_BEEF))
            hasItems = true;
        return hasItems;
    }

    public Sanfew(Player player) {
        super(player);

        switch(player.getQuestManager().getStage(Quest.DRUIDIC_RITUAL)) {
            case 1:
                addPlayer(HeadE.CALM_TALK, "Hello there.");
                addNPC(SANFEW, HeadE.CALM_TALK, "What can I do for you young 'un?", () -> {
                    player.voiceEffect(77263);
                });
                addPlayer(HeadE.CALM_TALK, "I've been sent to assist you with the ritual to purify the Varrockian stone circle.", ()->{
                    player.getPackets().resetSounds();
                });
                addNPC(SANFEW, HeadE.CALM_TALK, "Well, what I'm struggling with right now is the meats needed for the potion to honour Guthix. I need the raw" +
                        " meats of four different animals for it, ");
                addNPC(SANFEW, HeadE.CALM_TALK, "but not just any old meats will do. Each meat has to be dipped individually into the Cauldron of Thunder " +
                        "for it to work correctly.");
                addNPC(SANFEW, HeadE.CALM_TALK, "I will need 4 raw meats put into the cauldron. They are rat, bear, beef and chicken");
                addOptions(new Options() {
                    @Override
                    public void create() {
                        option("Where can I find this cauldron?", new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "Where can I find this cauldron?")
                                .addNPC(SANFEW, HeadE.CALM_TALK, "It is located somewhere in the mysterious underground halls which are located somewhere" +
                                        " in the woods just South of here. They are too dangerous for me to go myself however.")
                                .addPlayer(HeadE.CALM_TALK, "Ok, I'll go do that then.", () -> {
                                    player.getQuestManager().setStage(Quest.DRUIDIC_RITUAL, 2, true);
                                }));
                        option("Ok, I'll go do that then.",  () -> {
                            player.getQuestManager().setStage(Quest.DRUIDIC_RITUAL, 2, true);
                        });
                    }
                });
                break;
            case 2:
                addNPC(SANFEW, HeadE.CALM_TALK, "Did you bring me the required ingredients for the potion?");
                if (hasEnchantedItems(player)) {
                    addPlayer(HeadE.CALM_TALK, "Yes, I have all four now!");
                    addNPC(SANFEW, HeadE.CALM_TALK, "Well hand 'em over then lad! Thank you so much adventurer! These meats will allow our potion to honour " +
                            "Guthix to be completed, and bring one step closer to reclaiming our stone circle! ");
                    addNPC(SANFEW, HeadE.CALM_TALK, "Now go and talk to SANFEW and he will introduce you to the wonderful world of herblore and " +
                            "potion making!",  () -> {
                        player.getInventory().deleteItem(ENCHANTED_RAW_BEAR_MEAT, 1);
                        player.getInventory().deleteItem(ENCHANTED_RAW_CHICKEN, 1);
                        player.getInventory().deleteItem(ENCHANTED_RAW_RAT_MEAT, 1);
                        player.getInventory().deleteItem(ENCHANTED_RAW_BEEF, 1);
                        player.getQuestManager().setStage(Quest.DRUIDIC_RITUAL, 3, true);
                    });

                } else {
                    addPlayer(HeadE.CALM_TALK, "No, not yet...");
                    addNPC(SANFEW, HeadE.CALM_TALK, "Well, let me know when you do young 'un.");
                    addPlayer(HeadE.CALM_TALK, "I'll get on with it.");
                }
                break;
            default:
                addNPC(SANFEW, HeadE.CALM_TALK, "What can I do for you young 'un?", ()->{
                    player.voiceEffect(77263);
                });
                addPlayer(HeadE.CALM_TALK, "Nothing at the moment.", ()->{

                });
                break;
        }
        create();
    }

    public static NPCClickHandler sanfewHandler = new NPCClickHandler(new Object[] { SANFEW }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new Sanfew(e.getPlayer()));
    });


}
