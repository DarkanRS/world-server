package com.rs.game.content.miniquests.huntforsurok;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.miniquest.MiniquestHandler;
import com.rs.engine.miniquest.MiniquestOutline;
import com.rs.game.content.miniquests.abyss.ZamorakMage;
import com.rs.game.content.skills.runecrafting.Abyss;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

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
		player.getVars().setVarBit(3524, 1); //mine statue
		player.getVars().setVarBit(4312, 1); //make surok appear outside the statue
		player.getVars().setVarBit(4311, 1); //unlock portal to chaos tunnels from tunnel of chaos
		//4314 turns surok into dagon hai?
	}
}
