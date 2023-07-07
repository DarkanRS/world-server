package com.rs.game.content.miniquests.huntforsurok;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.miniquest.MiniquestHandler;
import com.rs.engine.miniquest.MiniquestOutline;
import com.rs.engine.quest.Quest;
import com.rs.game.content.miniquests.huntforsurok.npcs.AnnaJones;
import com.rs.game.content.skills.mining.Pickaxe;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@MiniquestHandler(Miniquest.HUNT_FOR_SUROK)
@PluginEventHandler
public class HuntForSurok extends MiniquestOutline {

	@Override
	public int getCompletedStage() {
		return 6;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case 0 -> {
				lines.add("I can start this miniquest by speaking to the Zamorakian mage in the");
				lines.add("wilderness north of Edgeville.");
				lines.add("");
			}
			case 4 -> {
				lines.add("");
				lines.add("");
				lines.add("MINIQUEST COMPLETE!");
			}
			default -> lines.add("Invalid quest stage. Report this to an administrator.");
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Skills.SLAYER, 5000);
		getQuest().sendQuestCompleteInterface(player, 11014, "5,000 Slayer XP", "Ability to slay Bork daily (for 1,500 Slayer XP)", "Summoning charms, big bones and gems) in the", "Chaos Tunnels", "Ability to wear Dagon 'hai robes");
	}

	@Override
	public void updateStage(Player player) {
		if (player.getMiniquestManager().getStage(Miniquest.HUNT_FOR_SUROK) > 1) {
			player.getVars().setVarBit(4311, 1);
			player.getVars().setVarBit(4314, 2);
		}
	}

	public static ObjectClickHandler handleStairsOutOfStatue = new ObjectClickHandler(new Object[] { 23074 }, e -> {
		e.getPlayer().useStairs(Tile.of(3284, 3467, 0));
	});

	public static ObjectClickHandler handleAnnasStatue = new ObjectClickHandler(new Object[] { 23096 }, e -> {
		switch(e.getOption()) {
			case "Excavate" -> {
				if (e.getPlayer().getQuestStage(Quest.WHAT_LIES_BELOW) < 4) {
					e.getPlayer().startConversation(new Dialogue()
							.addNPC(AnnaJones.ID, HeadE.CALM_TALK, "Excuse me. I am working on that statue at the moment. Please don't touch it.")
							.addPlayer(HeadE.AMAZED_MILD, "You are? But you're just sitting there.")
							.addNPC(AnnaJones.ID, HeadE.CALM_TALK, "Yes. I'm on a break.")
							.addPlayer(HeadE.CONFUSED, "Oh, I see. When does your break finish?")
							.addNPC(AnnaJones.ID, HeadE.CALM_TALK, "When I decide to start work again. Right now, I'm enjoying sitting on this bench."));
					return;
				}
				if (!e.getPlayer().getBool("annaTunnelTalk")) {
					e.getPlayer().startConversation(new AnnaJones(e.getPlayer()));
					return;
				}
				Pickaxe pick = Pickaxe.getBest(e.getPlayer());
				if (pick == null) {
					e.getPlayer().simpleDialogue("You need a pickaxe to dig out the statue.");
					return;
				}
				e.getPlayer().repeatAction(pick.getTicks(), num -> {
					e.getPlayer().anim(pick.getAnimId());
					if (Utils.skillSuccess(e.getPlayer().getSkills().getLevel(Skills.MINING), 16, 100)) {
						e.getPlayer().anim(-1);
						e.getPlayer().getVars().saveVarBit(3524, 1);
						if (e.getPlayer().isQuestComplete(Quest.WHAT_LIES_BELOW))
							e.getPlayer().getVars().saveVarBit(4312, 1);
						e.getPlayer().startConversation(new Dialogue()
								.addNPC(AnnaJones.ID, HeadE.CHEERFUL, "You did it! Oh, well done! How exciting!")
								.addPlayer(HeadE.CHEERFUL, "Right, well, I better see what's down there, then."));
						return false;
					}
					return true;
				});
			}
			case "Enter" -> e.getPlayer().useStairs(Tile.of(3179, 5191, 0));
		}
	});
}
