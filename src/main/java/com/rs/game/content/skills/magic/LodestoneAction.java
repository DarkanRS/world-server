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
package com.rs.game.content.skills.magic;

import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.Achievement;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class LodestoneAction extends PlayerAction {

	private final int HOME_ANIMATION = 16385, HOME_GRAPHIC = 3017;

	public static enum Lodestone {
		LUMBRIDGE(69836, null, 47, 10907, Tile.of(3233, 3222, 0)),
		BURTHORPE(69831, null, 42, 10902, Tile.of(2899, 3545, 0)),
		LUNAR_ISLE(69828, null, 39, -1, Tile.of(2085, 3915, 0)),
		BANDIT_CAMP(69827, null, 7, -1, Tile.of(3214, 2955, 0)),
		TAVERLY(69839, Achievement.RITE_OF_PASSAGE_1005, 50, 10910, Tile.of(2878, 3443, 0)),
		ALKARID(69829, Achievement.OPEN_SESAME_995, 40, 10900, Tile.of(3297, 3185, 0)),
		VARROCK(69840, Achievement.WELCOME_TO_BARTERTOWN_1006, 51, 10911, Tile.of(3214, 3377, 0)),
		EDGEVILLE(69834, Achievement.COMING_LIKE_A_GHOST_TOWN_1000, 45, 10905, Tile.of(3067, 3506, 0)),
		FALADOR(69835, Achievement.FOLLOW_THAT_STAR_1001, 46, 10906, Tile.of(2967, 3404, 0)),
		PORT_SARIM(69837, Achievement.SETTING_SAIL_1003, 48, 10908, Tile.of(3011, 3216, 0)),
		DRAYNOR_VILLAGE(69833, Achievement.AT_A_CROSSROADS_999, 44, 10904, Tile.of(3105, 3299, 0)),
		ARDOUGNE(69830, Achievement.OPEN_MARKET_996, 41, 10901, Tile.of(2634, 3349, 0)),
		CATHERBAY(69832, Achievement.BEACHHEAD_998, 43, 10903, Tile.of(2831, 3452, 0)),
		YANILLE(69841, Achievement.MAGICAL_MYSTERY_TOUR_1007, 52, 10912, Tile.of(2529, 3095, 0)),
		SEERS_VILLAGE(69838, Achievement.FIND_ENLIGHTENMENT_1004, 49, 10909, Tile.of(2689, 3483, 0));

		private int objectId;
		private Achievement achievement;
		private int component;
		private int configId;
		private Tile tile;

		public static Lodestone forComponent(int component) {
			for (Lodestone stone : Lodestone.values())
				if (stone.component == component)
					return stone;
			return null;
		}

		public static Lodestone forObject(int objectId) {
			for (Lodestone stone : Lodestone.values())
				if (stone.objectId == objectId)
					return stone;
			return null;
		}

		private Lodestone(int objectId, Achievement achievement, int component, int configId, Tile tile) {
			this.objectId = objectId;
			this.achievement = achievement;
			this.component = component;
			this.configId = configId;
			this.tile = tile;
		}

		public int getObjectId() {
			return objectId;
		}

		public Achievement getAchievement() {
			return achievement;
		}

		public int getConfigId() {
			return configId;
		}

		public Tile getTile() {
			return tile;
		}
	}

	public static ObjectClickHandler handleUnlock = new ObjectClickHandler(new Object[] { 69827, 69828, 69829, 69830, 69831, 69832, 69833, 69834, 69835, 69836, 69837, 69838, 69839, 69840, 69841 }, e -> {
		if (e.getOpNum() != ClientPacket.OBJECT_OP1)
			return;
		Lodestone stone = Lodestone.forObject(e.getObject().getId());
		if (stone != null)
			e.getPlayer().unlockLodestone(stone, e.getObject());
	});

	public static ButtonClickHandler handleLodestoneButtons = new ButtonClickHandler(1092, e -> {
		e.getPlayer().stopAll();
		Lodestone stone = Lodestone.forComponent(e.getComponentId());
		if (stone == null || (stone == Lodestone.BANDIT_CAMP && !e.getPlayer().isQuestComplete(Quest.DESERT_TREASURE, "to use this lodestone.")) || (stone == Lodestone.LUNAR_ISLE && !e.getPlayer().isQuestComplete(Quest.LUNAR_DIPLOMACY, "to use this lodestone.")))
			return;
		if (e.getPlayer().unlockedLodestone(stone))
			e.getPlayer().getActionManager().setAction(new LodestoneAction(stone.getTile()));
		else
			e.getPlayer().sendMessage("You have not unlocked this lodestone yet. Go find it and activate it!");
	});

	private int currentTime;
	private Tile tile;

	public LodestoneAction(Tile tile) {
		this.tile = tile;
	}

	@Override
	public boolean start(final Player player) {
		if (!player.getControllerManager().processMagicTeleport(tile))
			return false;
		return process(player);
	}

	@Override
	public int processWithDelay(Player player) {
		if (currentTime++ == 0) {
			player.setNextAnimation(new Animation(HOME_ANIMATION));
			player.setNextSpotAnim(new SpotAnim(HOME_GRAPHIC));
		} else if (currentTime == 18) {
			player.lock();
			player.getControllerManager().magicTeleported(Magic.MAGIC_TELEPORT);
			if (player.getControllerManager().getController() == null)
				Magic.teleControllersCheck(player, tile);
			player.setNextTile(tile);
			player.setNextFaceTile(tile.transform(0, -1, 0));
			WorldTasks.schedule(new WorldTask() {
				int stage = 0;

				@Override
				public void run() {
					if (stage == 0) {
						player.setNextAnimation(new Animation(HOME_ANIMATION+1));
						player.setNextSpotAnim(new SpotAnim(HOME_GRAPHIC+1));
					} else if (stage == 5)
						player.setNextAnimation(new Animation(16393));
					else if (stage == 7) {
						player.setNextTile(tile.transform(0, -1, 0));
						player.setNextAnimation(new Animation(-1));
						player.setNextSpotAnim(new SpotAnim(-1));
						player.unlock();
						stop();
					}
					stage++;
				}
			}, 0, 0);
		} else if (currentTime == 19)
			return -1;
		return 0;
	}

	@Override
	public boolean process(Player player) {
		if (player.inCombat(10000) || player.hasBeenHit(10000)) {
			player.sendMessage("You can't home teleport until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public void stop(Player player) {
		if (currentTime < 18) {
			player.setNextAnimation(new Animation(-1));
			player.setNextSpotAnim(new SpotAnim(-1));
		}
	}

}
