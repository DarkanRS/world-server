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
package com.rs.game.content.world.areas.falador;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.knightssword.SquireKnightsSwordD;
import com.rs.game.content.quests.piratestreasure.RedbeardFrankPiratesTreasureD;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Falador {

	public static PlayerStepHandler musicArtisansWorkshop = new PlayerStepHandler(new Tile[] { Tile.of(3035, 3339, 0), Tile.of(3035, 3338, 0), Tile.of(3034, 3339, 0), Tile.of(3034, 3338, 0) }, e -> {
		if(e.getTile().getX() == 3035 && e.getStep().getDir() == Direction.EAST) {
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(582, true);
			return;
		}
		if(e.getTile().getX() == 3034 && e.getPlayer().getMusicsManager().isPlaying(582))
			e.getPlayer().getMusicsManager().nextAmbientSong();
	});

	public static PlayerStepHandler musicRisingSunInn = new PlayerStepHandler(new Tile[] { Tile.of(2956, 3378, 0), Tile.of(2956, 3379, 0), Tile.of(2961, 3372, 0), Tile.of(2962, 3372, 0) }, e -> {
		if(e.getTile().getY() == 3378 && e.getStep().getDir() == Direction.SOUTH) {
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(718, true);
			return;
		}
		if(e.getTile().getX() == 2961 && e.getStep().getDir() == Direction.WEST) {
			e.getPlayer().getMusicsManager().playSpecificAmbientSong(718, true);
			return;
		}
		if((e.getTile().getY() == 3379 || e.getTile().getX() == 2961) && e.getPlayer().getMusicsManager().isPlaying(718))
			e.getPlayer().getMusicsManager().nextAmbientSong();
	});

	@ServerStartupEvent
	public static void addLoSOverrides() {
		Entity.addLOSOverrides(2290);
	}


	public static ObjectClickHandler handleUnderwallTunnelShortcut = new ObjectClickHandler(new Object[] { 9309, 9310 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 26))
			return;
		AgilityShortcuts.crawlUnder(e.getPlayer(), e.getPlayer().transform(0, e.getObjectId() == 9310 ? -4 : 4, 0));
	});

	public static ObjectClickHandler handleFistOfGuthixEntrance = new ObjectClickHandler(new Object[] { 20608, 30203 }, e -> {
		if(e.getObjectId() == 20608)
			e.getPlayer().useStairs(-1, Tile.of(1677, 5598, 0), 1, 1);
		if(e.getObjectId() == 30203)
			e.getPlayer().useStairs(-1, Tile.of(2969, 9672, 0), 1, 1);
	});

	public static ObjectClickHandler handleCrumblingWallShortcut = new ObjectClickHandler(new Object[] { 11844 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 5))
			return;
		AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 2 : -2, 0, 0));
	});

	public static ObjectClickHandler handleCabbagePatchStile = new ObjectClickHandler(new Object[] { 7527 }, e -> {
		Player p = e.getPlayer();
		WorldObject obj = e.getObject();
		if(!obj.getTile().matches(Tile.of(3063, 3282, 0)))
			return;
		if(p.getY() > obj.getY())
			AgilityShortcuts.climbOver(p, Tile.of(obj.getX(), obj.getY()-1, obj.getPlane()));
		if(p.getY() < obj.getY())
			AgilityShortcuts.climbOver(p, Tile.of(obj.getX(), obj.getY()+1, obj.getPlane()));
	});

	public static ObjectClickHandler handleCowFieldStile = new ObjectClickHandler(new Object[] { 7527 }, e -> {
		Player p = e.getPlayer();
		WorldObject obj = e.getObject();
		if(!obj.getTile().matches(Tile.of(3043, 3305, 0)))
			return;
		if(p.getX() > obj.getX())
			AgilityShortcuts.climbOver(p, Tile.of(obj.getX()-1, obj.getY(), obj.getPlane()));
		if(p.getX() < obj.getX())
			AgilityShortcuts.climbOver(p, Tile.of(obj.getX()+1, obj.getY(), obj.getPlane()));
	});
}