package com.rs.game.content.world.areas.pollnivneach.npcs;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class AliCat {
    public static NPCClickHandler handle = new NPCClickHandler(new Object[]{ 7784 }, new String[] { "Talk-to" }, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        if (ItemDefinitions.getDefs(e.getPlayer().getEquipment().getNeckId()).getName().contains("Catspeak"))
            player.startConversation(new Dialogue()
                    .addNPC(npc,HeadE.CAT_SHOUTING, "Stop, stop. Silly human.")
                    .addPlayer(HeadE.CONFUSED, "Not now puss. I have important work to do.")
                    .addSimple("Ali Cat jumps at your feet four times, twice on each side; you pick up your feet on that side each time he does that.")
                    .addNPC(npc,HeadE.CAT_SHOUTING, "I said stop. Don't you understand?")
                    .addSimple("Ali Cat runs in circles around you twice; you look around as he does so.")
                    .addPlayer(HeadE.CONFUSED, "Don't you have an owner or something? Get away!")
                    .addNPC(npc,HeadE.CAT_SHOUTING, "I have no owner, for I am a free cat. Listen to me or die horribly!")
                    .addSimple("Ali Cat jumps at your feet once, jumps down, then jumps at your head; you struggle for a brief moment, then let him go.")
                    .addPlayer(HeadE.CONFUSED, "Look, fleabag, I need to go down here to rescue a damsel in distress. I know it's dangerous.")
                    .addNPC(npc,HeadE.CAT_SHOUTING, "You think you can fight banshees while surrounded by smoke? More skilled folk than you have died horribly in there.")
                    .addPlayer(HeadE.CONFUSED, "Smoke and banshees? Well, I'm sure I'll prevail.")
                    .addNPC(npc,HeadE.CAT_SHOUTING, "Try wearing your earmuffs and a face mask at the same time, bonehead: it can't be done. You'd be as helpless as a newborn kitten thrown into the Fight Pit. Luckily, I know a solution to this problem.")
                    .addPlayer(HeadE.CONFUSED, "I suppose you'll have me travelling the globe on some epic quest?")
                    .addNPC(npc,HeadE.CAT_SHOUTING, "Oh, no. All you need to do is seek out Catolax, a Slayer master who died ages ago, and have a chat with him. He was buried in the desert, so he may be hard to talk to.")
                    .addPlayer(HeadE.CONFUSED, "The desert's a big place, any better directions?")
                    .addNPC(npc,HeadE.CAT_SHOUTING, "A deity told me to look near the ruins of Ullek. That's some-way east of what you humans call the Agility Pyramid. There are some old tombs in the valley walls there, which are only openable if you know the trick.")
                    .addPlayer(HeadE.SKEPTICAL, "Okay, the tombs east of the Agility Pyramid, talk to dead Slayer guy, learn the 'trick' and I can be off. Oh, and thanks, too.")
                    .addNPC(npc,HeadE.CAT_SHOUTING, "My pleasure. A warning: don't trust any redheads.")
            );
        else
            player.startConversation(new Dialogue()
                    .addNPC(npc,HeadE.CAT_SHOUTING, "Meeeeow.")
                    .addPlayer(HeadE.SKEPTICAL_THINKING, "Ah yes, I can't understand you without my amulet of catspeak.")
            );
    });
}
