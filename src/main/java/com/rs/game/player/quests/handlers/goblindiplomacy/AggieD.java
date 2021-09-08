package com.rs.game.player.quests.handlers.goblindiplomacy;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class AggieD extends Conversation {
    public AggieD(Player player, int npcId) {
        super(player);
        addNPC(npcId, HeadE.CALM, "What can I help you with?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("Hey, you are a witch aren't you?", new Dialogue()
                        .addPlayer(HeadE.SKEPTICAL_THINKING, "Hey, you are a witch aren't you?")
                        .addNPC(npcId, HeadE.AMAZED, "My, you are observant!")
                        .addPlayer(HeadE.CALM_TALK, "Cool, do you turn people into frogs?")
                        .addNPC(npcId, HeadE.NO_EXPRESSION, "Oh, not for years. But if you meet a talking chicken, you have probably met the professor in the " +
                                "manor north of here. A few years ago it was a flying fish. That machine is a menace."));
                option("So what is actually in that cauldron?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "So what is actually in that cauldron?")
                        .addNPC(npcId, HeadE.FRUSTRATED, "You don't really expect me to give away trade secrets, do you?"));
                option("What's new in Draynor village?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "What's new in Draynor village?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Hm, a while ago there was a portal that appeared in the woods to the east of Draynor")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "When the portal opened, the god Zamorak stepped through!")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "However, immediately he was confronted by Saradomin and a battle ensued. Both forces fought valiantly for " +
                                "months, unable to gain the upper hand")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "In the end, however, the power of Zamorak was no match for the might of Saradomin. Zamorak was defeated")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Zamorak retreated with the help of his general. But the victory had cost Saradomin dearly, and so he left the " +
                                "battlefield to regroup")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "The battlefield is at peace now")
                        .addPlayer(HeadE.HAPPY_TALKING, "Ok, thanks!"));
                option("What could you make for me?", new Dialogue()
                        .addPlayer(HeadE.SKEPTICAL_THINKING, "What could you make for me?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "I mostly just make what I find pretty. I sometimes make dye for the women's clothes to brighten the place up. " +
                                "I can make red,yellow and blue dyes. If you'd like some, just bring me the appropriate ingredients."));
                option("Can you make dyes for me please?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "Can you make dyes for me please?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "What sort of dye would you like? Red, yellow or blue?")
                        .addOptions(new Options() {
                            @Override
                            public void create() {
                                option("Red dye", () -> {
                                    if(player.getInventory().containsItem(1951, 3) && player.getInventory().containsItem(995, 5)) {
                                        player.getInventory().deleteItem(1951, 3);
                                        player.getInventory().deleteItem(995, 5);
                                        player.getInventory().addItem(1763, 1);
                                    } else
                                        player.getPackets().sendGameMessage("You need 3 red berries and 5 coins");
                                });
                                option("Yellow dye", () -> {
                                    if(player.getInventory().containsItem(1957, 2) && player.getInventory().containsItem(995, 5)) {
                                        player.getInventory().deleteItem(1957, 2);
                                        player.getInventory().deleteItem(995, 5);
                                        player.getInventory().addItem(1765, 1);
                                    } else
                                        player.getPackets().sendGameMessage("You need 2 onions and 5 coins");
                                });
                                option("Blue dye", () -> {
                                    if(player.getInventory().containsItem(1793, 2) && player.getInventory().containsItem(995, 5)) {
                                        player.getInventory().deleteItem(1793, 2);
                                        player.getInventory().deleteItem(995, 5);
                                        player.getInventory().addItem(1767, 1);
                                    } else
                                        player.getPackets().sendGameMessage("You need 2 woad leaves and 5 coins");
                                });
                            }
                        })
                );
            }
        });
        create();
    }

    public static NPCClickHandler handleTalk = new NPCClickHandler(922) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new AggieD(e.getPlayer(), 922));
        }
    };
}
