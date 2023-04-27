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
package com.rs.game.content.world;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

import java.util.Map;

public class RestMusician extends PlayerAction {

	private static int[][] REST_DEFS = { { 5713, 1549, 5748 }, { 11786, 1550, 11788 }, { 5713, 1551, 2921 } // TODO
	// First
	// emote

	};

	private Map<Integer, Integer> musicListing = Map.ofEntries(
			Map.entry(5439, 661),//lute
			Map.entry(8705, 661),//lute
			Map.entry(8698, 657),//violin
			Map.entry(29, 661),//lute
			Map.entry(8709, 660),//double flute
			Map.entry(8715, 664),//drunk
			Map.entry(8723, 659),//elven
			Map.entry(8712, 665),//drum
			Map.entry(8702, 661),//lute
			Map.entry(8706, 657),//violin
			Map.entry(8716, 662),//lyre
			Map.entry(8703, 661), //lute
			Map.entry(8704, 657),//violin
			Map.entry(8717, 662),//lyre
			Map.entry(8718, 662),//lyre
			Map.entry(5442, 661),//lute
			Map.entry(30, 661),//lute
			Map.entry(8699, 657),//violin
			Map.entry(3463, 661),//lute
			Map.entry(8708, 660),//double flute
			Map.entry(8707, 660),//double flute
			Map.entry(8701, 657),//violin
			Map.entry(8700, 657),//violin
			Map.entry(8713, 656)//ghost
			);

	private int musicId;
	private int index;

	public RestMusician(int musicianId) {
		if(musicListing.containsKey(musicianId))
			musicId = musicListing.get(musicianId);
		else
			musicId = -1;
	}

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		index = Utils.random(REST_DEFS.length);
		player.setResting(true);
		player.setNextAnimation(new Animation(REST_DEFS[index][0]));
		player.getAppearance().setBAS(REST_DEFS[index][1]);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if(musicId != -1 && !player.getMusicsManager().isPlaying(musicId))
			player.musicTrack(musicId);
		if (player.getPoison().isPoisoned()) {
			player.sendMessage("You can't rest while you're poisoned.");
			return false;
		}
		if (player.inCombat(10000) || player.hasBeenHit(10000)) {
			player.sendMessage("You can't rest until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(Player player) {
		player.setResting(false);
		player.setNextAnimation(new Animation(REST_DEFS[index][2]));
		player.getEmotesManager().setNextEmoteEnd();
		player.getAppearance().setBAS(-1);
		if(musicId != -1)
			player.getMusicsManager().nextAmbientSong();
	}

}
