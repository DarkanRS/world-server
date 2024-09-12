package com.rs.game.content.world.areas.catherby.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Beekeeper extends Conversation {

    private static final int npcId = 8649;

    public static NPCClickHandler Beekeeper = new NPCClickHandler(new Object[]{npcId}, e -> {
        switch (e.getOption()) {

        case "Talk-To" -> e.getPlayer().startConversation(new Beekeeper(e.getPlayer()));
        }
    });

    public Beekeeper(Player player) {
        super(player);

        player.startConversation(new Conversation(new Dialogue()
                .addNPC(npcId, HeadE.CHEERFUL, "Hello! What do you think of my apiary? Nice, isn't it?")
                .addPlayer(HeadE.SKEPTICAL, "You mean all these beehives?")
                .addNPC(npcId, HeadE.CHEERFUL, "Yup! They're filled with bees. Also wax, and delicious honey too!")
                .addNPC(npcId, HeadE.CHEERFUL, "You're welcome to help yourself to as much wax and honey as you like.")
                .addNPC(npcId, HeadE.SKEPTICAL, "Oh, but you'll need some insect repellant - here.")
                .addItemToInv(player, new Item(28, 1), "The beekeeper hands you some insect repellant.")
                .addPlayer(HeadE.CHEERFUL, "Thank you!")
                .addNPC(npcId, HeadE.ANGRY, "Leave the bees, though. The bees are mine!")
                .addNPC(npcId, HeadE.CHEERFUL_EXPOSITION, "I love bees!")
                .finish()));
    }

    public static ObjectClickHandler handleBeehives = new ObjectClickHandler(new Object[] { 68 }, e -> {
        if (e.getOpNum() == ClientPacket.OBJECT_OP2) {
            Player player = e.getPlayer();
            if (player.getInventory().hasFreeSlots()) {
                player.anim(833);
                player.lock(1);
                if (player.getInventory().containsItem(28)) {
                    player.getInventory().addItem(12156, 1);
                } else {
                    player.forceTalk("Ouch!");
                    player.applyHit(new Hit(10, Hit.HitLook.TRUE_DAMAGE));
                    player.sendMessage("The bees sting your hands as you reach inside!");
                }
            }
        }
    });
}

