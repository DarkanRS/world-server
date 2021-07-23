package com.rs.game.player.quests.handlers.goblindiplomacy;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

import java.util.Random;

@PluginEventHandler
public class WysonTheGardener extends Conversation {
    static final int MOLECLAW = 7416;
    static final int MOLESKIN = 7418;
    static final int RED_EGG_NEST = 5070;
    static final int GREEN_EGG_NEST = 5071;
    static final int BLUE_EGG_NEST = 5072;
    static final int TREE_SEED_NEST = 5073;
    static final int RING_NEST = 5074;
    static final int EMPTY_BIRD_NEST = 5075;
    static final int ALLOTMENTS_SEED_NEST = 7413;
    private static int determineNestMoleParts() {//https://runescape.fandom.com/wiki/Mole_skin?oldid=5371847
        Random random = new Random();
        int roll = Utils.random(0, 100);//0-99
        if(roll < 20)//1/5
            return EMPTY_BIRD_NEST;
        else {//4/5
            int roll2 = Utils.random(0, 3);//0-2
            if(roll2 == 0)
                return RING_NEST;
            if(roll2 == 1)
                return ALLOTMENTS_SEED_NEST;
            if(roll2 == 2)
                return TREE_SEED_NEST;
        }
        return EMPTY_BIRD_NEST;
    }

    public WysonTheGardener(Player player, int npcId) {
        super(player);
        addNPC(npcId, HeadE.CALM, "I'm the head gardener around here. If you're looking for woad leaves, or if you need help with owt, I'm yer man.");
        addOptions(new Options() {
            @Override
            public void create() {
                option("Ok, I will trade my mole parts.", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "ok, I will trade my mole parts.", () -> {
                            while(player.getInventory().containsItem(MOLECLAW)) {
                                player.getInventory().deleteItem(MOLECLAW, 1);
                                player.getInventory().addItem(determineNestMoleParts(), 1);
                            }
                            while(player.getInventory().containsItem(MOLESKIN)) {
                                player.getInventory().deleteItem(MOLESKIN, 1);
                                player.getInventory().addItem(determineNestMoleParts(), 1);
                            }
                        }));
                option("Yes please, I need woad leaves.", new Dialogue()
                        .addNPC(npcId, HeadE.AMAZED, "How much are you willing to pay?")
                        .addOptions(new Options() {
                            @Override
                            public void create() {
                                option("How about 5 coins?", new Dialogue()
                                        .addPlayer(HeadE.CALM, "How about 5 coins?")
                                        .addNPC(npcId, HeadE.FRUSTRATED, "No no, that's far too little. Woad leaves are hard to get. I used to have plenty but someone kept" +
                                                " stealing them off me."));
                                option("How about 10 coins?", new Dialogue()
                                        .addPlayer(HeadE.CALM, "How about 10 coins?")
                                        .addNPC(npcId, HeadE.FRUSTRATED, "No no, that's far too little. Woad leaves are hard to get. I used to have plenty but someone kept " +
                                                "stealing them off me."));
                                option("How about 15 coins?", new Dialogue()
                                        .addPlayer(HeadE.CALM, "How about 15 coins?")
                                        .addNPC(npcId, HeadE.FRUSTRATED, " Mmmm... okay, that sounds fair.", () -> {
                                            if (player.getInventory().containsItem(995, 15)) {
                                                player.getInventory().deleteItem(995, 15);
                                                player.getInventory().addItem(1793, 1);
                                            } else {
                                                player.getPackets().sendGameMessage("You need 15 coins for this transaction");
                                            }
                                        }));
                                option("How about 20 coins?", new Dialogue()
                                        .addPlayer(HeadE.CALM, "How about 20 coins?")
                                        .addNPC(npcId, HeadE.FRUSTRATED, "Okay, that's more than fair", () -> {
                                            if (player.getInventory().containsItem(995, 20)) {
                                                player.getInventory().deleteItem(995, 20);
                                                player.getInventory().addItem(1793, 2);
                                            } else {
                                                player.getPackets().sendGameMessage("You need 20 coins for this transaction");
                                            }
                                        }));
                            }
                        }));
                option("How about ME helping YOU instead?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "How about ME helping YOU instead?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "That's a nice thing to say. I do need a hand, now you mention it. You see, there's some stupid mole " +
                                "digging up my lovely garden.")
                        .addPlayer(HeadE.CALM_TALK, "A mole? Surely you've dealt with moles in the past?")
                        .addNPC(npcId, HeadE.AMAZED, "Ah, well this is no ordinary mole! He's a big'un for sure. Ya see... I'm always relied upon to make the" +
                                " most of this 'ere garden - the faster and bigger I can grow plants the better!")
                        .addNPC(npcId, HeadE.WORRIED, "In my quest for perfection I looked into 'Malignius- Mortifer's-Super-Ultra-Flora-Growth-Potion'. " +
                                "It worked well on my plants, no doubt about it! But it had the same effect on a nearby mole. Ya can imagine the")
                        .addNPC(npcId, HeadE.FRUSTRATED, "havoc he causes to my patches of sunflowers! Why, if any of the other gardeners knew about this mole," +
                                " I'd be looking for a new job in no time!")
                        .addPlayer(HeadE.CALM_TALK, "I see. What do you need me to do?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "If ya are willing maybe yer wouldn't mind killing it for me? Take a spade and use it to shake up them " +
                                "mole hills. Be careful though, he really is big!")
                        .addPlayer(HeadE.CALM_TALK, "Is there anything in this for me?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Well, if yer gets any mole skin or mole claws off 'un, I'd trade 'em for bird nests if ye brings 'em here to me.")
                        .addPlayer(HeadE.CALM_TALK, "Right, I'll bear it in mind.")
                );
                option("Sorry, but I'm not interested.", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "Sorry, but I'm not interested.")
                        .addNPC(npcId, HeadE.CALM, "Fair enough."));
            }
        });
        create();
    }

    public static NPCClickHandler handleTalk = new NPCClickHandler(36) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new WysonTheGardener(e.getPlayer(), 36));
        }
    };
}
