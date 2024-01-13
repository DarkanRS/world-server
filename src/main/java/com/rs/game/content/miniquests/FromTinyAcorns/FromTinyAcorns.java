package com.rs.game.content.miniquests.FromTinyAcorns;

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

@MiniquestHandler(
		miniquest = Miniquest.FROM_TINY_ACORNS,
		startText = "Speak to Darren Lightfinger in his cellar accessed through a trapdoor north of Lumbridge furnace.",
		itemsText = "None",
		combatText = "None",
		rewardsText = "1,000 Thieving XP<br>Small pouch<br>Access to the advanced pickpocketing trainer and coshing volunteers in the Thieves' Guild",
		completedStage = 4
)
@PluginEventHandler
public class FromTinyAcorns extends MiniquestOutline {
	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case 0 -> {
				lines.add("I can start this miniquest by speaking to the Zamorakian mage in the");
				lines.add("wilderness north of Edgeville.");
				lines.add("");
			}
			case 1 -> {
				lines.add("I spoke to the Zamorakian mage in the wilderness north of Edgeville.");
				lines.add("He told me I should meet him in Varrock.");
				lines.add("");
			}
			case 3 -> {
				lines.add("I gave the mage the scrying orb and he told me to speak to him after");
				lines.add("he's finished gathering the data from it. I should speak to him again.");
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
		player.getSkills().addXpQuest(Skills.THIEVING, 1000);
		sendQuestCompleteInterface(player, 18651);
	}

	@Override
	public void updateStage(Player player) {
		if(player.getMiniquestManager().getStage(Miniquest.FROM_TINY_ACORNS) <=3)
			player.getVars().setVar(7821, 0);
		else
			player.getVars().setVar(7821, 1);
	}

}
