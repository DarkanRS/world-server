// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.world.areas.varrock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.InventoryDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.achievements.AchievementDef;
import com.rs.game.content.achievements.AchievementDef.Area;
import com.rs.game.content.achievements.AchievementDef.Difficulty;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.content.combat.XPType;
import com.rs.game.content.quests.dragonslayer.GuildMasterDragonSlayerD;
import com.rs.game.content.quests.heroesquest.dialogues.KatrineHeroesQuestD;
import com.rs.game.content.quests.heroesquest.dialogues.StravenHeroesQuestD;
import com.rs.game.content.quests.knightssword.KnightsSword;
import com.rs.game.content.quests.knightssword.ReldoKnightsSwordD;
import com.rs.game.content.quests.scorpioncatcher.ScorpionCatcher;
import com.rs.game.content.quests.shieldofarrav.BaraekShieldOfArravD;
import com.rs.game.content.quests.shieldofarrav.CharlieTheTrampArravD;
import com.rs.game.content.quests.shieldofarrav.KatrineShieldOfArravD;
import com.rs.game.content.quests.shieldofarrav.KingRoaldShieldOfArravD;
import com.rs.game.content.quests.shieldofarrav.MuseumCuratorArravD;
import com.rs.game.content.quests.shieldofarrav.ReldoShieldOfArravD;
import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.game.content.quests.shieldofarrav.StravenShieldOfArravD;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.content.world.doors.Doors;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Varrock {
	public static PlayerStepHandler musicBlueMoonInn = new PlayerStepHandler(new Tile[] { Tile.of(3215, 3395, 0), Tile.of(3216, 3395, 0), Tile.of(3233, 3396, 0) }, e -> {
		if(e.getTile().getX() <= 3216 && e.getStep().getDir() == Direction.WEST)
			if(e.getPlayer().getMusicsManager().isPlaying(716))
				e.getPlayer().getMusicsManager().nextAmbientSong();
		if(e.getTile().getX() == 3216 && e.getStep().getDir() == Direction.EAST)
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(716, true);

		if(e.getTile().getX() == 3233 && e.getStep().getDir() == Direction.WEST)
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(716, true);
		if(e.getTile().getX() == 3233 && e.getStep().getDir() == Direction.EAST) {
			if(e.getPlayer().getMusicsManager().isPlaying(716))
				e.getPlayer().getMusicsManager().nextAmbientSong();
		}
	});

	public static PlayerStepHandler musicDancingDonkeyInn = new PlayerStepHandler(new Tile[] { Tile.of(3274, 3389, 0), Tile.of(3275, 3389, 0) }, e -> {
		if (e.getTile().getX() <= 3275 && e.getStep().getDir() == Direction.EAST)
			if (e.getPlayer().getMusicsManager().isPlaying(721))
				e.getPlayer().getMusicsManager().nextAmbientSong();
		if (e.getTile().getX() == 3274 && e.getStep().getDir() == Direction.WEST)
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(721, true);
	});

	public static PlayerStepHandler musicBoarsHeadInn = new PlayerStepHandler(new Tile[] { Tile.of(3281, 3506, 0), Tile.of(3280, 3506, 0) }, e -> {
		if (e.getStep().getDir() == Direction.NORTH)
			if (e.getPlayer().getMusicsManager().isPlaying(720))
				e.getPlayer().getMusicsManager().nextAmbientSong();
		if (e.getStep().getDir() == Direction.SOUTH)
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(720, true);
	});

	public static NPCClickHandler handlePeskaBarbarianVillage = new NPCClickHandler(new Object[] { 538 }, e -> {
		int NPC= e.getNPCId();
		if(e.getOption().equalsIgnoreCase("talk-to")) {
			e.getPlayer().startConversation(new Dialogue()
					.addNPC(NPC, HeadE.CALM_TALK, "Are you interested in buying or selling a helmet?")
					.addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("I could be, yes.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I could be, yes.")
									.addNPC(NPC, HeadE.CALM_TALK, "Let me show you my inventory then...")
									.addNext(()->{ShopsHandler.openShop(e.getPlayer(), "helmet_shop");})
							);
							option("No, I'll pass on that.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "No, I'll pass on that.")
									.addNPC(NPC, HeadE.CALM_TALK, "Well, alright.")
							);
							if(e.getPlayer().getQuestManager().getStage(Quest.SCORPION_CATCHER) == ScorpionCatcher.LOOK_FOR_SCORPIONS
								&& e.getPlayer().getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp2LocKnown"))
								option("I've heard you have a small scorpion in your possession.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I've heard you have a small scorpion in your possession.")
										.addNPC(NPC, HeadE.CALM_TALK, "Now how could you know about that, I wonder? Mind you, I don't have it anymore.")
										.addNPC(NPC, HeadE.CALM_TALK, "I gave it as a present to my brother Ivor when I visited our outpost northwest of Camelot.")
										.addNPC(NPC, HeadE.CALM_TALK, "Well, actually I hid it in his bed so it would nip him. It was a bit of a surprise gift.")
										.addPlayer(HeadE.HAPPY_TALKING, "Okay ill look at the barbarian outpost, perhaps you mean the barbarian agility area?")
										.addNPC(NPC, HeadE.SECRETIVE, "Perhaps...")
								);
						}
					})
			);


		}
		if(e.getOption().equalsIgnoreCase("trade"))
			ShopsHandler.openShop(e.getPlayer(), "helmet_shop");
	});

	public static ObjectClickHandler varrockCenterStairs = new ObjectClickHandler(new Object[] { 24367 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(e.getObject().getX(), 3476, 1), 1, 2);
	});

	public static ObjectClickHandler blueMoonStairs = new ObjectClickHandler(new Object[] { 37117 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(e.getObject().getX()-2, e.getPlayer().getY(), 0), 1, 2);
	});

	public static ObjectClickHandler handleVariousStaircases = new ObjectClickHandler(new Object[] { 24356 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(obj.getRotation() == 0)
			p.useStairs(-1, Tile.of(p.getX(), obj.getY()+3, p.getPlane() + 1), 0, 1);
		else if (obj.getRotation() == 1)
			p.useStairs(-1, Tile.of(p.getX()+4, p.getY(), p.getPlane() + 1), 0, 1);
		return;
	});

	public static ObjectClickHandler handleChaosAltar = new ObjectClickHandler(new Object[] { 61 }, e -> {
		Player p = e.getPlayer();
		if(e.getOption().equalsIgnoreCase("Pray-at")) {
			final int maxPrayer = p.getSkills().getLevelForXp(Constants.PRAYER) * 10;
			if (p.getPrayer().getPoints() < maxPrayer) {
				p.lock(5);
				p.sendMessage("You pray to the gods...", true);
				p.setNextAnimation(new Animation(645));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						p.getPrayer().restorePrayer(maxPrayer);
						p.sendMessage("...and recharged your prayer.", true);
					}
				}, 2);
			} else
				p.sendMessage("You already have full prayer.");
		} else if(e.getOption().equalsIgnoreCase("Check"))
			p.startConversation(new Conversation(p) {
				{
					addSimple("You find a small inscription at the bottom of the altar. It reads: 'Snarthon Candtrick Termanto'.");
					create();
				}
			});
	});

	public static ObjectClickHandler handleDummies = new ObjectClickHandler(new Object[] { 23921 }, e -> {
		if (e.getPlayer().getSkills().getLevelForXp(Constants.ATTACK) >= 8) {
			e.getPlayer().sendMessage("There is nothing more you can learn from hitting a dummy.");
			return;
		}
		XPType type = e.getPlayer().getCombatDefinitions().getAttackStyle().getXpType();
		if (type != XPType.ACCURATE && type != XPType.AGGRESSIVE && type != XPType.CONTROLLED && type != XPType.DEFENSIVE) {
			e.getPlayer().sendMessage("You can't hit a dummy with that attack style.");
			return;
		}
		e.getPlayer().setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(e.getPlayer().getEquipment().getWeaponId(), e.getPlayer().getCombatDefinitions().getAttackStyle())));
		e.getPlayer().lock(3);
		World.sendObjectAnimation(e.getObject(), new Animation(6482));
		e.getPlayer().getSkills().addXp(Constants.ATTACK, 5);
	});

	public static ObjectClickHandler handleVarrockSewerEntrance = new ObjectClickHandler(new Object[] { "Manhole" }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(e.getOption().equalsIgnoreCase("Climb-Down"))
			if(obj.getTile().matches(Tile.of(3237, 3458, 0)))
				p.useStairs(833, Tile.of(3237, 9858, 0), 1, 2);
	});

	public static ObjectClickHandler handleKeldagrimTrapdoor = new ObjectClickHandler(new Object[] { 28094 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2911, 10176, 0));
	});

	public static ObjectClickHandler handleRiverLumSteppingStones = new ObjectClickHandler(new Object[] { 9315 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 31))
			return;
		AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getObject().getRotation() == 1 ? -5 : 5, 0, 0), 4);
	});

	public static ObjectClickHandler handleGrandExchangeShortcut = new ObjectClickHandler(new Object[] { 9311, 9312 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 21))
			return;
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				boolean withinGE = e.getObjectId() == 9312;
				Tile tile = withinGE ? Tile.of(3139, 3516, 0) : Tile.of(3143, 3514, 0);
				e.getPlayer().lock();
				ticks++;
				if (ticks == 1) {
					e.getPlayer().setNextAnimation(new Animation(2589));
					e.getPlayer().setNextForceMovement(new ForceMovement(e.getObject().getTile(), 1, withinGE ? Direction.WEST : Direction.EAST));
				} else if (ticks == 3) {
					e.getPlayer().setNextTile(Tile.of(3141, 3515, 0));
					e.getPlayer().setNextAnimation(new Animation(2590));
				} else if (ticks == 5) {
					e.getPlayer().setNextAnimation(new Animation(2591));
					e.getPlayer().setNextTile(tile);
				} else if (ticks == 6) {
					e.getPlayer().setNextTile(Tile.of(tile.getX() + (withinGE ? -1 : 1), tile.getY(), tile.getPlane()));
					e.getPlayer().unlock();
					stop();
				}
			}
		}, 0, 0);
	});
	
	public static ObjectClickHandler handleFenceShortcut = new ObjectClickHandler(new Object[] { 9300 }, e -> {
		if (!e.isAtObject())
			return;
		switch (e.getObject().getRotation()) {
		case 0 -> AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() >= e.getObject().getX() ? -1 : 1, 0, 0), 839);
		case 1 -> AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() >= e.getObject().getY() ? -1 : 1, 0), 839);
		case 2 -> AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() >= e.getObject().getX() ? -1 : 1, 0, 0), 839);
		case 3 -> AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() >= e.getObject().getY() ? -1 : 1, 0), 839);
		}
	});

	public static ObjectClickHandler handleStileShortcuts = new ObjectClickHandler(new Object[] { 45205, 34776, 48208 }, e -> {
		if (!e.isAtObject())
			return;
		switch (e.getObject().getRotation()) {
		case 0 -> AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() >= e.getObject().getY() ? -2 : 2, 0), 839);
		case 1 -> AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() >= e.getObject().getX() ? -2 : 2, 0, 0), 839);
		case 2 -> AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() >= e.getObject().getY() ? -2 : 2, 0), 839);
		case 3 -> AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() >= e.getObject().getX() ? -2 : 2, 0, 0), 839);
		}
	});

	public static ObjectClickHandler handlePhoenixGangHideoutLadder = new ObjectClickHandler(new Object[] { 24363 }, e -> {
		if (e.getObject().getTile().matches(Tile.of(3244, 3383, 0)) && e.getOption().equalsIgnoreCase("climb-down"))
			e.getPlayer().ladder(Tile.of(3245, 9783, 0));
	});

	public static ObjectClickHandler handlePhoenixGangVarrockLadder = new ObjectClickHandler(new Object[] { 2405 }, e -> {
		if (e.getObject().getTile().matches(Tile.of(3244, 9783, 0)) && e.getOption().equalsIgnoreCase("climb-up"))
			e.getPlayer().ladder(Tile.of(3243, 3383, 0));
	});

	public static ObjectClickHandler handleChampionsGuildFrontDoor = new ObjectClickHandler(new Object[] { 1805 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if (p.getY() >= obj.getY()) {
			if (p.getQuestManager().getQuestPoints() <= 31) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("You need 32 quest points to enter the champions guild.");
						create();
					}
				});
				return;
			}
			Doors.handleDoor(p, obj);
			p.npcDialogue(198, HeadE.CHEERFUL, "Greetings bold adventurer. Welcome to the guild of Champions.");
		} else
			Doors.handleDoor(p, obj);
	});

}
