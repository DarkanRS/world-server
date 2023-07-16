package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Benny extends Conversation {
	
	public static NPCClickHandler handleArtimeus = new NPCClickHandler(new Object[] { 5925 }, e -> {
		switch(e.getOption()) {
		case "Talk-to" -> e.getPlayer().startConversation(new Benny(e.getPlayer(), e.getNPC()));
		}
	});
	
	public Benny(Player player, NPC npc) {
		super(player);

		addOptions(this, "baseOptions", ops -> {
			ops.add("Can I have a newspaper, please?")
				.addPlayer(HeadE.CONFUSED, "Can I have a newspaper, please?")
				.addNPC(npc.getId(), HeadE.CALM_TALK, "Certainly, " + (player.getAppearance().isMale() ? "Guv" : "Missus") + ". That'll be 50 gold pieces, please.")
				.addOptions(ops2 -> {
				ops2.add("Sure, here you go.")
					.addPlayer(HeadE.CALM_TALK, "Sure, here you go.")
					.addItemToInv(player, new Item(11169, 1), "You buy a newspaper.")
					.addGotoStage("baseOptions", this);
				ops2.add("Uh, no thanks, I've changed my mind.")
					.addPlayer(HeadE.CALM_TALK, "Uh, no thanks, I've changed my mind.")
					.addNPC(npc.getId(), HeadE.CALM_TALK, "Ok, suit yourself. Plenty more fish in the sea.")
					.addGotoStage("baseOptions", this);
			});
			ops.add("How much does a paper cost?")
				.addPlayer(HeadE.CONFUSED, "How much does a paper cost?")
				.addNPC(npc.getId(), HeadE.CALM_TALK, "Just 50 gold pieces! An absolute bargain! Want one?")
				.addOptions(ops2 -> {
				ops2.add("Sure, here you go.")
					.addPlayer(HeadE.CALM_TALK, "Sure, here you go.")
					.addItemToInv(player, new Item(11169, 1), "You buy a newspaper.")
					.addGotoStage("baseOptions", this);
				ops2.add("No, thanks.")
					.addPlayer(HeadE.CALM_TALK, "No, thanks.")
					.addNPC(npc.getId(), HeadE.CALM_TALK, "Ok, suit yourself. Plenty more fish in the sea.")
					.addGotoStage("baseOptions", this);
			});
			ops.add("Varrock Herald? Never heard of it.")
				.addPlayer(HeadE.CONFUSED, "Varrock Herald? Never heard of it.")
				.addNPC(npc.getId(), HeadE.CALM_TALK, "For the illiterate amongst us, I shall elucidate. The Varrock Herald is a new newspaper. It is edited, printed and published by myself, Benny Gutenberg, and each edition promises to enthrall the reader with captivating material! Now, can I interest you in buying one for a mere 50 gold?")
				.addOptions(this, "newspaperOptions", ops4 -> {
					ops4.add("Sure, here you go.")
						.addPlayer(HeadE.CHEERFUL, "Sure, here you go.")
						.addNPC(npc.getId(), HeadE.CHEERFUL, "Thank you, here is your newspaper!")
						.addItemToInv(player, new Item(11169, 1), "You buy a newspaper.")
						.addGotoStage("baseOptions", this);
					ops4.add("No, thanks.")
						.addPlayer(HeadE.CALM_TALK, "No, thanks.")
						.addNPC(npc.getId(), HeadE.CALM_TALK, "Ok, suit yourself. Plenty more fish in the sea.")
						.addGotoStage("baseOptions", this);
				});
			});
	}
}
