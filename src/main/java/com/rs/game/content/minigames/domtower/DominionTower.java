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
package com.rs.game.content.minigames.domtower;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.engine.dialogue.Dialogue;
import com.rs.game.World;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public final class DominionTower {

	public static final int CLIMBER = 0, ENDURANCE = 1, MAX_FACTOR = 10000000;

	private transient Player player;
	private transient Instance region;

	private int nextBossIndex;
	private int progress;
	private int dominionFactor;
	private long totalScore;
	private boolean talkedWithFace;
	private int killedBossesCount;
	private int maxFloorEndurance;
	private int maxFloorClimber;

	private static final int[] NORMAL_ARENA = { 456, 768 }, NOMAD_ARENA = { 456, 776 };

	static Item[] commonRewards = { new Item(995, 1000), new Item(386, 10), new Item(380, 10), new Item(374, 10), new Item(398, 10), new Item(441, 10), new Item(560, 10), new Item(565, 10) };
	static Item[] mediumRewards = { new Item(22358), new Item(22366), new Item(22362) };

	public void setPlayer(Player player) {
		this.player = player;
	}

	public DominionTower() {
		nextBossIndex = -1;
	}

	public boolean hasRequiriments() {
		return player.getSkills().getCombatLevelWithSummoning() >= 110;
	}

	public void openSpectate() {
		player.getInterfaceManager().sendInterface(1157);
	}

	public void growFace() {
		player.voiceEffect(7913, false);
		player.simpleDialogue("The face on the wall groans and scowls at you. Perhaps you should", "talk to it first.");
	}

	public void openModes() {
		if (!hasRequiriments()) {
			player.startConversation(new Dialogue().addSimple("You don't have the requirements to play this content, but you can spectate some of the matches" +
					" taking place if you would like.", () -> player.getDominionTower().openSpectate()));
			return;
		}
		if (!talkedWithFace) {
			growFace();
			return;
		}
		if (progress == 256) {
			player.simpleDialogue("You have some dominion factor which you must exchange before", "starting another match.");
			player.sendMessage("You can't go back into the arena, you must go to the next floor on entrance.");
			return;
		}
		player.getInterfaceManager().sendInterface(1164);
		player.getPackets().setIFText(1164, 27, progress == 0 ? "Ready for a new match" : "Floor progress: " + progress);
	}

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(new Object[] { 1163, 1164, 1168, 1170, 1173 }, e -> {
		if (e.getInterfaceId() == 1164) {
			if (e.getComponentId() == 26)
				e.getPlayer().getDominionTower().openClimberMode();
			else if (e.getComponentId() == 28)
				e.getPlayer().getDominionTower().openEnduranceMode();
			else if (e.getComponentId() == 29)
				e.getPlayer().getDominionTower().openSpecialMode();
			else if (e.getComponentId() == 30)
				e.getPlayer().getDominionTower().openFreeStyleMode();
			else if (e.getComponentId() == 31)
				e.getPlayer().getDominionTower().openSpectate();
		} else if (e.getInterfaceId() == 1163) {
			if (e.getComponentId() == 89)
				e.getPlayer().closeInterfaces();
		} else if (e.getInterfaceId() == 1168) {
			if (e.getComponentId() == 254)
				e.getPlayer().closeInterfaces();
		} else if (e.getInterfaceId() == 1170) {
			if (e.getComponentId() == 85)
				e.getPlayer().closeInterfaces();
		} else if (e.getInterfaceId() == 1173)
			if (e.getComponentId() == 58)
				e.getPlayer().closeInterfaces();
			else if (e.getComponentId() == 59)
				e.getPlayer().getDominionTower().startEnduranceMode();
	});

	public static ObjectClickHandler handleDominionTowerInteractions = new ObjectClickHandler(new Object[] { 62674, 62675, 62678, 62679, 62688, 62677, 62680 }, e -> {
		Player player = e.getPlayer();

		switch (e.getObjectId()) {
			case 62674 -> player.useStairs(-1, Tile.of(3744, 6405, 0), 0, 1);
			case 62675 -> player.getCutsceneManager().play(new DTPreview());
			case 62676 -> player.useStairs(-1, Tile.of(3374, 3093, 0), 0, 1);
			case 62678, 62679 -> player.getDominionTower().openModes();
			case 62688 -> {
				if (e.getOption().equals("Claim-rewards")) {
					player.startConversation(new Dialogue()
							.addSimple("You have a Dominion Factor of " + player.getDominionTower().getDominionFactor() + ".")
							.addOptions("If you claim your rewards your progress will be reset.", ops -> {
								ops.add("Claim rewards", () -> player.getDominionTower().openRewardsChest());
								ops.add("Nevermind.");
							}));
				} else if (e.getOption().equals("Check-DF")) {
					e.getPlayer().simpleDialogue("You have a Dominion Factor of " + e.getPlayer().getDominionTower().getDominionFactor() + ".");
				}
			}
			case 62677 -> {
				if (e.getOption().equals("Talk-to"))
					player.getDominionTower().talkToFace();
				else if (e.getOption().equals("Rewards"))
					e.getPlayer().getDominionTower().openRewards();
			}
			case 62680 -> player.getDominionTower().openBankChest();
		}
	});


	private static final int[] MUSICS = { 1015, 1022, 1018, 1016, 1021 };

	public static final class Boss {

		private final String name;
		private final String text;
		private final int[] ids;
		private final boolean forceMulti;
		private final Item item;
		private final int voice;
		private final int[] arena;

		public Boss(String name, String text, int... ids) {
			this(name, text, -1, false, null, NORMAL_ARENA, ids);
		}

		public Boss(String name, String text, int voice, boolean forceMulti, Item item, int[] arena, int... ids) {
			this.name = name;
			this.text = text;
			this.forceMulti = forceMulti;
			this.ids = ids;
			this.item = item;
			this.voice = voice;
			this.arena = arena;
		}

		public boolean isForceMulti() {
			return forceMulti;
		}

		public String getName() {
			return name;
		}
	}

	private static final Boss[] BOSSES = {
			new Boss("Elvarg", "Grrrr", 14548),
			new Boss("Delrith", "Grrrr", -1, false, new Item(2402, 1), NORMAL_ARENA, 14578),
			new Boss("Evil Chicken", "Bwak bwak bwak", 3375),
			new Boss("The Black Knight Titan", "Kill kill kill!", 14436),
			new Boss("Bouncer", "Grrr", 14483),
			// custom bosses
			new Boss("Jad", "Roarrrrrrrrrrrrrrrrrrrrrrrrrr", 2745),
			new Boss("Kalphite Queen", null, 1158),
			new Boss("King Black Dragon", "Grrrr", 50),
			new Boss("Nomad", "You don't stand a chance!", 7985, true, null, NOMAD_ARENA, 8528) };

	private void startEnduranceMode() {
		if (progress == 256) {
			player.simpleDialogue("You have some dominion factor which you must exchange before", "starting another match.");
			player.sendMessage("You can't go back into the arena, you must go to the next floor on entrance.");
			return;
		}
		createArena(ENDURANCE);
	}

	public void createArena(final int mode) {
		player.closeInterfaces();
		player.lock();
		Instance old = region;
		region = Instance.of(Tile.of(3744, 6425, 0), 8, 8);
		region.copyMapAllPlanes(BOSSES[getNextBossIndex()].arena[0], BOSSES[getNextBossIndex()].arena[1]).thenAccept(e -> {
			teleportToArena(mode);
			player.unlock();
			if (old != null)
				old.destroy();
		});
	}

	private void teleportToArena(int mode) {
		player.faceTile(Tile.of(getBaseX() + 11, getBaseY() + 29, 0));
		player.getControllerManager().startController(new DomTowerController(mode));
		player.unlock();
		player.tele(Tile.of(getBaseX() + 10, getBaseY() + 29, 2));
		player.getMusicsManager().playSongAndUnlock(MUSICS[Utils.getRandomInclusive(MUSICS.length - 1)]);
	}

	public String getStartFightText(int message) {
        return switch (message) {
            case 0 -> "Kick my ass!";
            case 1 -> "Please don't hit my face";
            case 2 -> "Argh!";
            default -> "Bring it on!";
        };
	}

	public int getNextBossIndex() {
		if (nextBossIndex < 0 || nextBossIndex >= BOSSES.length)
			selectBoss();
		return nextBossIndex;
	}

	public void startFight(final NPC[] bosses) {
		for (NPC boss : bosses) {
			boss.setCantInteract(true);
			boss.faceTile(Tile.of(boss.getX() - 1, boss.getY(), 0));
		}
		player.lock();
		player.tele(Tile.of(getBaseX() + 25, getBaseY() + 32, 2));
		player.faceTile(Tile.of(getBaseX() + 26, getBaseY() + 32, 0));
		final int index = getNextBossIndex();
		WorldTasks.scheduleTimer(0, 1, (count) -> {
			if (count == 0) {
				player.getInterfaceManager().sendOverlay(1172);
				player.getPackets().setIFHidden(1172, 2, true);
				player.getPackets().setIFHidden(1172, 7, true);
				player.getPackets().setIFText(1172, 4, player.getDisplayName());
				player.getVars().setVar(1241, 1);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 25), player.getSceneY(getBaseY() + 38), 1800);
				player.getPackets().sendCameraLook(player.getSceneX(getBaseX() + 25), player.getSceneY(getBaseY() + 29), 800);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 32), player.getSceneY(getBaseY() + 38), 1800, 6, 6);
			} else if (count == 1)
				player.forceTalk(getStartFightText(Utils.getRandomInclusive(1)));
			else if (count == 3) {
				player.getPackets().setIFHidden(1172, 2, false);
				player.getPackets().setIFHidden(1172, 5, true);
				player.getPackets().setIFText(1172, 6, BOSSES[index].name);
				player.getVars().setVar(1241, 0);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 35), player.getSceneY(getBaseY() + 37), 1800);
				player.getPackets().sendCameraLook(player.getSceneX(getBaseX() + 35), player.getSceneY(getBaseY() + 28), 800);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 42), player.getSceneY(getBaseY() + 37), 1800, 6, 6);
			} else if (count == 4) {
				if (BOSSES[index].text != null)
					bosses[0].forceTalk(BOSSES[index].text);
				if (BOSSES[index].voice != -1)
					player.voiceEffect(BOSSES[index].voice, false);
			} else if (count == 6) {
				player.getControllerManager().sendInterfaces();
				player.getInterfaceManager().sendInterface(1172);
				player.getPackets().setIFHidden(1172, 2, true);
				player.getPackets().setIFHidden(1172, 5, true);
				player.getPackets().setIFText(1172, 8, "Fight!");
				player.getPackets().setIFHidden(1172, 10, true);
				player.getPackets().sendCameraLook(player.getSceneX(getBaseX() + 32), player.getSceneY(getBaseY() + 36), 0);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 32), player.getSceneY(getBaseY() + 16), 5000);
				player.voiceEffect(7882, false);
			} else if (count == 8) {
				if (nextBossIndex != -1 && BOSSES[index].item != null)
					World.addGroundItemNoExpire(BOSSES[index].item, Tile.of(getBaseX() + 26, getBaseY() + 33, 2));
				player.closeInterfaces();
				player.getPackets().sendResetCamera();
				for (NPC boss : bosses) {
					boss.setCantInteract(false);
					boss.setCombatTarget(player);
				}
				player.unlock();
				return false;
			}
			return true;
		});
	}

	public void removeItem() {
		if (nextBossIndex == -1)
			return;
		if (BOSSES[nextBossIndex].item != null) {
			player.getInventory().deleteItem(BOSSES[nextBossIndex].item.getId(), BOSSES[nextBossIndex].item.getAmount());
			player.getEquipment().deleteItem(BOSSES[nextBossIndex].item.getId(), BOSSES[nextBossIndex].item.getAmount());
			player.getAppearance().generateAppearanceData();
		}
	}

	public void loss(final int mode) {
		/*
		 * if(mapBaseCoords == null) { //died on logout
		 * player.tele(Tile.of(3744, 6425, 0));
		 * player.getControllerManager().removeControllerWithoutCheck(); return; }
		 */
		removeItem();
		nextBossIndex = -1;
		player.lock();
		player.tele(Tile.of(getBaseX() + 35, getBaseY() + 31, 2));
		player.faceTile(Tile.of(player.getX() + 1, player.getY(), 0));

		WorldTasks.scheduleTimer(0, 1, (count) -> {
			if (count == 0) {
				player.anim(836);
				player.getInterfaceManager().removeOverlay();
				player.getInterfaceManager().sendInterface(1172);
				player.getPackets().setIFHidden(1172, 2, true);
				player.getPackets().setIFHidden(1172, 5, true);
				player.getPackets().setIFText(1172, 8, "Unlucky, you lost!");
				player.getPackets().setIFText(1172, 10, "You leave with a dominion factor of: " + dominionFactor);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 35), player.getSceneY(getBaseY() + 37), 2500);
				player.getPackets().sendCameraLook(player.getSceneX(getBaseX() + 35), player.getSceneY(getBaseY() + 28), 800);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 42), player.getSceneY(getBaseY() + 37), 2500, 6, 6);
				player.voiceEffect(7874, false);
			} else if (count == 4) {
				player.setForceMultiArea(false);
				player.reset();
				player.anim(-1);
				player.closeInterfaces();
				player.getPackets().sendResetCamera();
				player.unlock();
				destroyArena(false, mode);
				return false;
			}
			return true;
		});
	}

	public void win(int mode) {
		removeItem();
		int factor = getBossesTotalLevel() * (mode == CLIMBER ? 100 : 10);
		progress++;
		if (mode == CLIMBER) {
			if (progress > maxFloorClimber)
				maxFloorClimber = progress;
		} else if (mode == ENDURANCE)
			if (progress > maxFloorEndurance)
				maxFloorEndurance = progress;

		killedBossesCount++;
		dominionFactor += factor;
		totalScore += factor;
		if (dominionFactor > MAX_FACTOR) {
			dominionFactor = MAX_FACTOR;
			player.sendMessage("You've reached the maximum Dominion Factor you can get so you should spend it!");
		}
		nextBossIndex = -1;
		player.lock();
		player.tele(Tile.of(getBaseX() + 35, getBaseY() + 31, 2));
		player.faceTile(Tile.of(getBaseX() + 36, getBaseY() + 31, 0));

		WorldTasks.scheduleTimer(0, 1, (count) -> {
			if (count == 0) {
				player.getInterfaceManager().removeOverlay();
				player.getInterfaceManager().sendInterface(1172);
				player.getPackets().setIFHidden(1172, 2, true);
				player.getPackets().setIFHidden(1172, 5, true);
				player.getPackets().setIFText(1172, 8, "Yeah! You won!");
				player.getPackets().setIFText(1172, 10, "You now have a dominion factor of: " + dominionFactor);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 35), player.getSceneY(getBaseY() + 37), 2500);
				player.getPackets().sendCameraLook(player.getSceneX(getBaseX() + 35), player.getSceneY(getBaseY() + 28), 800);
				player.getPackets().sendCameraPos(player.getSceneX(getBaseX() + 42), player.getSceneY(getBaseY() + 37), 2500, 6, 6);
				player.voiceEffect(7897, false);
			} else if (count == 4) {
				player.reset();
				player.closeInterfaces();
				player.getPackets().sendResetCamera();
				player.unlock();
				return false;
			}
			return true;
		});
	}

	/*
	 * 4928 15936
	 */
	/*
	 * 4960, 15968
	 */

	public void destroyArena(final boolean logout, int mode) {
		Tile tile = Tile.of(3744, 6425, 0);
		if (logout)
			player.setTile(tile);
		else {
			player.getControllerManager().removeControllerWithoutCheck();
			player.lock();
			player.tele(tile);
			if (mode == ENDURANCE)
				progress = 0;
		}
		region.destroy().thenAccept(e -> {
			if (!logout) {
				region = null;
				player.unlock();
			}
		});
	}

	public NPC[] createBosses() {
		NPC[] bosses = new NPC[BOSSES[getNextBossIndex()].ids.length];
		for (int i = 0; i < BOSSES[getNextBossIndex()].ids.length; i++)
			bosses[i] = World.spawnNPC(BOSSES[getNextBossIndex()].ids[i], Tile.of(getBaseX() + 37 + (i * 2), getBaseY() + 31, 2), true, true);
		return bosses;
	}

	public int getBaseX() {
		return region.getBaseX();
	}

	public int getBaseY() {
		return region.getBaseY();
	}

	public void selectBoss() {
		if (nextBossIndex < 0 || nextBossIndex >= BOSSES.length)
			nextBossIndex = Utils.random(BOSSES.length);
	}

	public void openClimberMode() {
		player.sendMessage("Only endurance mode is currently working.");
		// player.getInterfaceManager().sendScreenInterface(96, 1163);
		// selectBoss();
		// player.getPackets().sendIComponentText(1163, 32, "0"); // you leave
		// with
	}

	public void openEnduranceMode() {
		selectBoss();
		player.getInterfaceManager().setFullscreenInterface(96, 1173);
		player.getPackets().setIFText(1173, 25, BOSSES[getNextBossIndex()].name); // current
		player.getPackets().setIFText(1173, 38, String.valueOf(progress + 1)); // current
		player.getPackets().setIFText(1173, 52, "None. Good luck!"); // current
		player.getPackets().setIFText(1173, 29, String.valueOf(dominionFactor)); // current
		player.getPackets().setIFText(1173, 31, dominionFactor == MAX_FACTOR ? "" : String.valueOf(getBossesTotalLevel() * 10)); // on
		// win
		player.getPackets().setIFText(1173, 33, String.valueOf(dominionFactor)); // on
		// death
	}

	public int getBossesTotalLevel() {
		int level = 0;
		for (int id : BOSSES[getNextBossIndex()].ids)
			level = +NPCDefinitions.getDefs(id).combatLevel;
		return level;
	}

	public void openSpecialMode() {
		player.sendMessage("Only endurance mode is currently working.");
		// player.getInterfaceManager().sendScreenInterface(96, 1170);
	}

	public void openFreeStyleMode() {
		player.sendMessage("Only endurance mode is currently working.");
		// player.getInterfaceManager().sendScreenInterface(96, 1168);
	}

	public void talkToFace() {
		talkToFace(false);
	}

	public void talkToFace(boolean fromDialogue) {
		if (!hasRequiriments()) {
			player.simpleDialogue("You need at least level 110 combat to use this tower.");
			return;
		}
		if (!talkedWithFace)
			player.startConversation(new StrangeFace(player));
		else {
			if (!fromDialogue)
				player.voiceEffect(7893, false);
			player.getInterfaceManager().sendInterface(1160);
		}
	}

	public void openRewards() {
		if (!talkedWithFace) {
			talkToFace();
			return;
		}
		player.voiceEffect(7893, false);
		player.getInterfaceManager().sendInterface(1156);
	}

	public void openRewardsChest() {
		if (!talkedWithFace) {
			growFace();
			return;
		}
		if (dominionFactor == 0) {
			player.sendMessage("You have no dominion factor to claim anything with..");
			return;
		}
		progress = 0;
		giveRewards(dominionFactor);
		player.sendMessage("<col=ffffff>You have " + player.getDominionTower().getKilledBossesCount() + " boss kills.");
		player.sendMessage("<col=ffffff>500 are required to obtain dominion gloves (swifts, goliaths, spellcasters).");
		dominionFactor = 0;
	}

	public void giveRewards(int dominionFactor) {
		Item[] rewards;
		int random = Utils.getRandomInclusive(100);
		if (random < 15 && player.getDominionTower().getKilledBossesCount() >= 500)
			rewards = new Item[] { commonRewards[Utils.getRandomInclusive(commonRewards.length - 1)], commonRewards[Utils.getRandomInclusive(commonRewards.length - 1)], commonRewards[Utils.getRandomInclusive(commonRewards.length - 1)],
					mediumRewards[Utils.getRandomInclusive(mediumRewards.length - 1)] };
		else
			rewards = new Item[] { commonRewards[Utils.getRandomInclusive(commonRewards.length - 1)], commonRewards[Utils.getRandomInclusive(commonRewards.length - 1)], commonRewards[Utils.getRandomInclusive(commonRewards.length - 1)] };
		for (Item item : rewards)
            if(player.getInventory().hasFreeSlots())
			    player.getInventory().addItem(item);
            else if(player.getBank().hasBankSpace()) {
                player.getBank().addItem(item, false);
                player.sendMessage("Your reward have been sent to your bank.");
            } else {
                World.addGroundItemNoExpire(item, player.getTile(), player);
                player.sendMessage("You don't have bank space, so your reward has been dropped on the ground.");
            }


		player.getInterfaceManager().sendInterface(1171);
		player.getPackets().sendInterSetItemsOptionsScript(1171, 7, 100, 8, 3, "Take", "Convert", "Discard", "Examine");
		player.getPackets().setIFRightClickOps(1171, 7, 0, 10, 0, 1, 2, 3);
		player.getPackets().sendItems(100, rewards);
	}

	public void openBankChest() {
		if (!talkedWithFace) {
			growFace();
			return;
		}
		player.getBank().open();
	}

	public boolean isTalkedWithFace() {
		return talkedWithFace;
	}

	public void setTalkedWithFace(boolean talkedWithFace) {
		this.talkedWithFace = talkedWithFace;
	}

	public int getProgress() {
		return progress;
	}

	public long getTotalScore() {
		return totalScore;
	}

	public int getDominionFactor() {
		return dominionFactor;
	}

	public Boss getNextBoss() {
		return BOSSES[getNextBossIndex()];
	}

	public int getMaxFloorClimber() {
		return maxFloorClimber;
	}

	public int getMaxFloorEndurance() {
		return maxFloorEndurance;
	}

	public int getKilledBossesCount() {
		return killedBossesCount;
	}

	public void setKilledBossesCount(int count) {
		killedBossesCount = count;
	}

}
