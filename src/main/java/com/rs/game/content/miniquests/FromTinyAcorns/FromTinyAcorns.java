package com.rs.game.content.miniquests.FromTinyAcorns;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.miniquest.MiniquestHandler;
import com.rs.engine.miniquest.MiniquestOutline;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@MiniquestHandler(
		miniquest = Miniquest.FROM_TINY_ACORNS,
		startText = "Speak to Darren Lightfinger in his cellar accessed through a trapdoor north of Lumbridge furnace.",
		itemsText = "None",
		combatText = "None",
		rewardsText = "1,000 Thieving XP<br>Access to the advanced pickpocketing trainer and coshing volunteers in the Thieves' Guild",
		completedStage = 5
)
@PluginEventHandler
public class FromTinyAcorns extends MiniquestOutline {

	public static LoginHandler login = new LoginHandler(e -> {
		boolean hasDragon = e.getPlayer().getBank().containsItem(18651,1) || e.getPlayer().getInventory().containsItem(18651);
		if (!e.getPlayer().getMiniquestManager().isComplete(Miniquest.FROM_TINY_ACORNS) && e.getPlayer().getMiniquestManager().getStage(Miniquest.FROM_TINY_ACORNS) >= 2 && !hasDragon) {
			e.getPlayer().getMiniquestManager().setStage(Miniquest.FROM_TINY_ACORNS, 1);
			e.getPlayer().getVars().setVarBit(7821, 1);
		}
	});

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case 0 -> {
				lines.add("I can start this miniquest by speaking to Darren Lightfinger in");
				lines.add("the Lumbridge Thieves' Guild.");
				lines.add("");
			}
			case 1 -> {
				lines.add("I need to pay Urist Loric a visit and steal the dragon.");
				lines.add("Maybe Robin will be able to help.");
				lines.add("");
			}
			case 2 -> {
				lines.add("I've stolen the dragon.");
				lines.add("Now I just need to get Dareen's investment back.");
				lines.add("");
			}
			case 3 -> {
				lines.add("Urist agreed to give Darren his money back after loosing the dragon.");
				lines.add("I should take the Banker's Note back to Darren.");
				lines.add("");
			}
			case 4 -> lines.add("MINIQUEST COMPLETE!");

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
		if(player.getMiniquestManager().getStage(Miniquest.FROM_TINY_ACORNS) == 1)
			player.getVars().setVarBit(7821, 1);
		else
			player.getVars().setVarBit(7821, 0);
	}

	public static ItemClickHandler talismanHandler = new ItemClickHandler(new Object[] { 18649 }, new String[] { "Put-down" }, e -> {
		if (!e.getPlayer().getTile().withinArea(3220, 3427, 3228, 3432)) {
			e.getPlayer().sendMessage("I should find a suitable spot to put this. Maybe just north of him...");
			return;
		}
		World.addGroundItem(e.getItem(), e.getPlayer().getTile(), e.getPlayer());
		e.getPlayer().getInventory().deleteItem(e.getItem());
		e.getPlayer().getInventory().refresh();
	});

	public static ObjectClickHandler handleStall = new ObjectClickHandler(new Object[] { 51656 }, e -> {
		if(e.getOption().equalsIgnoreCase("Steal-from")) {
			boolean UristDistracted = e.getPlayer().getMiniquestManager().getAttribs(Miniquest.FROM_TINY_ACORNS).getB("UristDistracted");
			boolean GuardDistracted = e.getPlayer().getMiniquestManager().getAttribs(Miniquest.FROM_TINY_ACORNS).getB("GuardDistracted");
			int UristID = 11270;
			int GuardID = 11269;
			if (e.getPlayer().getInventory().containsItem(18651)) {
				e.getPlayer().sendMessage("You've stolen the Toy Baby Dragon already.");
				return;
			}
			if (UristDistracted && GuardDistracted) {
				e.getPlayer().getActionManager().setAction(new stealFromStall(e.getObject()));
			} else {
				if (!UristDistracted) {
					e.getPlayer().startConversation(new Dialogue()
							.addNPC(UristID, HeadE.CALM_TALK, "Sorry, " + e.getPlayer().getPronoun("lad", "miss") + ", I can't let you pick it up just yet. Still needs its oil and polish before I can call it a finished work, see.")
							.addPlayer(HeadE.SKEPTICAL, "It looks finished to me.")
							.addNPC(UristID, HeadE.SHAKING_HEAD, "And it'd look finished until the works gummed up or the oil clouded the rubies. Can't let a piece this pricey be a rush job, can I?")
					);
					return;
				}
				e.getPlayer().npcDialogue(GuardID, HeadE.ANGRY, "Oi! Put that back, thief!");
			}
		}
	});
}
