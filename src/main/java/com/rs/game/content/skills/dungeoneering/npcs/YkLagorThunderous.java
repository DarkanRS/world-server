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
package com.rs.game.content.skills.dungeoneering.npcs;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.content.skills.dungeoneering.npcs.combat.YkLagorThunderousCombat;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.List;

public class YkLagorThunderous extends DungeonBoss {

	private static int[][] BROKEN_FLOORS_TILES =
		{
				{ 5, 11 },
				{ 2, 13 },
				{ 10, 11 },
				{ 13, 12 },
				{ 11, 8 },
				{ 11, 6 },
				{ 13, 4 },
				{ 9, 5 },
				{ 10, 3 },
				{ 3, 3 },
				{ 4, 9 },
				{ 7, 4 },
				{ 4, 6 } };
	private static int[][] PILLAR_SAFEZONE =
		{
				{ 1, 6 },
				{ 1, 9 },
				{ 14, 9 },
				{ 14, 6 } };
	private static final SpotAnim EARTH_QUAKE_GRAPHICS = new SpotAnim(1551, 10, 20);

	private YkLagorMage[] mysteriousMages;
	private int startCountdown;
	private int nextAttack;

	private boolean loaded;

	public YkLagorThunderous(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(11872, 11886), manager.getBossLevel()), tile, manager, reference);
		setCantInteract(true);
		setCantFollowUnderCombat(true);
		setMages();
		loaded = true;
	}

	private void setMages() {
		int index = 0;
		mysteriousMages = new YkLagorMage[8];
		mysteriousMages[index++] = new YkLagorMage(this, 11887, getManager().getTile(getReference(), 7, 3), getManager(), 1);
		mysteriousMages[index++] = new YkLagorMage(this, 11887, getManager().getTile(getReference(), 4, 5), getManager(), 1);
		mysteriousMages[index++] = new YkLagorMage(this, 11887, getManager().getTile(getReference(), 3, 8), getManager(), 1);
		mysteriousMages[index++] = new YkLagorMage(this, 11887, getManager().getTile(getReference(), 4, 11), getManager(), 1);
		mysteriousMages[index++] = new YkLagorMage(this, 11887, getManager().getTile(getReference(), 7, 12), getManager(), 1);
		mysteriousMages[index++] = new YkLagorMage(this, 11887, getManager().getTile(getReference(), 10, 11), getManager(), 1);
		mysteriousMages[index++] = new YkLagorMage(this, 11887, getManager().getTile(getReference(), 11, 8), getManager(), 1);
		mysteriousMages[index++] = new YkLagorMage(this, 11887, getManager().getTile(getReference(), 10, 5), getManager(), 1);
	}

	private final static String[] QUOTES =
		{ "We will break you!", "You've outlived your use!", "You do not belong here.", "Your soul belongs to us!", "Mah zodas'bakh me'ah." };

	//private final static int[] QUOTES_SOUNDS =
	//{ 1899, 1903, 1902, 1901, 1900 };

	public YkLagorMage[] getMages() {
		return mysteriousMages;
	}

	@Override
	public void processNPC() {
		if (!loaded)
			return;
		if (isCantInteract()) {
			if (startCountdown > 0) {
				startCountdown--;
				if (startCountdown == 0) {
					YkLagorThunderousCombat.sendMagicalAttack(this, true);
					getCombat().setCombatDelay(5);
					setCantInteract(false);
					mysteriousMages = null;
				}
				return;
			}
			int distractedMages = 0;
			for (YkLagorMage mage : mysteriousMages)
				if (mage.isUnderCombat() || mage.isDead() || mage.hasFinished())
					distractedMages++;
			if (distractedMages >= 4 && Utils.random(5) == 0) {
				setNextAnimation(new Animation(14443));
				setNextSpotAnim(new SpotAnim(166));
				setNextForceTalk(new ForceTalk("MY TURN!"));
				//playSound(1935, 1);
				playMusic();
				startCountdown = 10;
				return;
			}
			setNextAnimation(new Animation(14442));
			setNextSpotAnim(new SpotAnim(165));
			if (Utils.random(33) == 0 && !getPossibleTargets().isEmpty()) {
				YkLagorMage mage = mysteriousMages[Utils.random(mysteriousMages.length)];
				if (mage.isUnderCombat() || mage.isDead() || mage.hasFinished())
					return;
				int index = Utils.random(QUOTES.length);
				mage.setNextForceTalk(new ForceTalk(QUOTES[index]));
				//mage.playSound(QUOTES_SOUNDS[index], 1);
			}

			return;
		}
		super.processNPC();
	}

	public void playMusic() {
		for (Player player : getManager().getParty().getTeam()) {
			if (player.isDead() || !getManager().isAtBossRoom(player.getTile()))
				continue;
			player.getMusicsManager().forcePlayMusic(863);
		}
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	public List<Entity> getPossibleTargets(boolean special, boolean npc) {
		List<Entity> possibleTargets = super.getPossibleTargets(npc);
		if (special)
			for (int[] element : PILLAR_SAFEZONE) {
				Tile tile = getManager().getTile(getReference(), element[0], element[1]);
				for (Entity t : possibleTargets)
					if (t.getX() == tile.getX() && t.getY() == tile.getY())
						possibleTargets.remove(t);
			}
		return possibleTargets;
	}

	public void sendBrokenFloor() {
		for (int[] element : BROKEN_FLOORS_TILES) {
			Tile tile = getManager().getTile(getReference(), element[0], element[1]);
			World.sendSpotAnim(tile, EARTH_QUAKE_GRAPHICS);
		}
	}

	public int getNextAttack() {
		return nextAttack;
	}

	public void increaseNextAttack(int nextAttack) {
		this.nextAttack += nextAttack;
	}

	public static boolean isBehindPillar(Player player, DungeonManager manager, RoomReference rRef) {
		for (int[] element : PILLAR_SAFEZONE) {
			Tile tile = manager.getTile(rRef, element[0], element[1]);
			if (player.getX() == tile.getX() && player.getY() == tile.getY())
				return true;
		}
		return false;
	}
}
