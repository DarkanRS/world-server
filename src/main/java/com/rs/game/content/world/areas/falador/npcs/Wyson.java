package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Wyson extends Conversation {
    private static final int npcId = 36;

    public static NPCClickHandler Wyson = new NPCClickHandler(new Object[]{ npcId }, e -> {
        switch (e.getOption()) {
            
            case "Talk-to" -> e.getPlayer().startConversation(new Wyson(e.getPlayer()));
        }
    });

    public Wyson(Player player) {
        super(player);
        addNPC(npcId, HeadE.CALM,
                "I'm the head gardener around here. If you're looking for woad leaves, or if you need help with owt, I'm yer man.");
        addOptions(new Options() {
            @Override
            public void create() {
                option("Ok, I will trade my mole parts.",
                        new Dialogue().addPlayer(HeadE.CALM_TALK, "ok, I will trade my mole parts.", () -> {
                            int numNests = player.getInventory().getNumberOf(7416) + player.getInventory().getNumberOf(7418);
                            player.getInventory().deleteItem(7416, Integer.MAX_VALUE);
                            player.getInventory().deleteItem(7418, Integer.MAX_VALUE);
                            for (int i = 0;i < numNests;i++)
                                player.getInventory().addItem(Utils.random(0, 100) <= 7 ? 5075 : 7413, 1);
                        }));
                option("Yes please, I need woad leaves.",
                        new Dialogue()
                                .addNPC(npcId, HeadE.AMAZED, "How much are you willing to pay?")
                                .addOptions(new Options() {
                                    @Override
                                    public void create() {
                                        option("How about 5 coins?", new Dialogue()
                                                .addPlayer(HeadE.CALM, "How about 5 coins?")
                                                .addNPC(npcId, HeadE.FRUSTRATED,
                                                        "No no, that's far too little. Woad leaves are hard to get. I used to have plenty but someone kept"
                                                                + " stealing them off me."));
                                        option("How about 10 coins?", new Dialogue()
                                                .addPlayer(HeadE.CALM, "How about 10 coins?")
                                                .addNPC(npcId, HeadE.FRUSTRATED,
                                                        "No no, that's far too little. Woad leaves are hard to get. I used to have plenty but someone kept "
                                                                + "stealing them off me."));
                                        option("How about 15 coins?",
                                                new Dialogue().addPlayer(HeadE.CALM, "How about 15 coins?")
                                                        .addNPC(npcId, HeadE.FRUSTRATED,
                                                                " Mmmm... okay, that sounds fair.", () -> {
                                                                    if (player.getInventory()
                                                                            .hasCoins(15)) {
                                                                        player.getInventory()
                                                                                .removeCoins(15);
                                                                        player.getInventory().addItem(1793,
                                                                                1);
                                                                    } else
                                                                        player.getPackets().sendGameMessage(
                                                                                "You need 15 coins for this transaction");
                                                                }));
                                        option("How about 20 coins?",
                                                new Dialogue().addPlayer(HeadE.CALM, "How about 20 coins?")
                                                        .addNPC(npcId, HeadE.FRUSTRATED,
                                                                "Okay, that's more than fair", () -> {
                                                                    if (player.getInventory()
                                                                            .hasCoins(20)) {
                                                                        player.getInventory()
                                                                                .removeCoins(20);
                                                                        player.getInventory().addItem(1793,
                                                                                2);
                                                                    } else
                                                                        player.getPackets().sendGameMessage(
                                                                                "You need 20 coins for this transaction");
                                                                }));
                                    }
                                }));
                option("How about ME helping YOU instead?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "How about ME helping YOU instead?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,
                                "That's a nice thing to say. I do need a hand, now you mention it. You see, there's some stupid mole "
                                        + "digging up my lovely garden.")
                        .addPlayer(HeadE.CALM_TALK, "A mole? Surely you've dealt with moles in the past?")
                        .addNPC(npcId, HeadE.AMAZED,
                                "Ah, well this is no ordinary mole! He's a big'un for sure. Ya see... I'm always relied upon to make the"
                                        + " most of this 'ere garden - the faster and bigger I can grow plants the better!")
                        .addNPC(npcId, HeadE.WORRIED,
                                "In my quest for perfection I looked into 'Malignius- Mortifer's-Super-Ultra-Flora-Growth-Potion'. "
                                        + "It worked well on my plants, no doubt about it! But it had the same effect on a nearby mole. Ya can imagine the")
                        .addNPC(npcId, HeadE.FRUSTRATED,
                                "havoc he causes to my patches of sunflowers! Why, if any of the other gardeners knew about this mole,"
                                        + " I'd be looking for a new job in no time!")
                        .addPlayer(HeadE.CALM_TALK, "I see. What do you need me to do?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,
                                "If ya are willing maybe yer wouldn't mind killing it for me? Take a spade and use it to shake up them "
                                        + "mole hills. Be careful though, he really is big!")
                        .addPlayer(HeadE.CALM_TALK, "Is there anything in this for me?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING,
                                "Well, if yer gets any mole skin or mole claws off 'un, I'd trade 'em for bird nests if ye brings 'em here to me.")
                        .addPlayer(HeadE.CALM_TALK, "Right, I'll bear it in mind."));
                option("Sorry, but I'm not interested.",
                        new Dialogue().addPlayer(HeadE.CALM_TALK, "Sorry, but I'm not interested.")
                                .addNPC(npcId, HeadE.CALM, "Fair enough."));
            }
        });
    }
}
