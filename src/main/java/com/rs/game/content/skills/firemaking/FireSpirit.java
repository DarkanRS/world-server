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
package com.rs.game.content.skills.firemaking;

import com.rs.game.model.entity.TimerBar;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class FireSpirit extends OwnedNPC {

	private int life;

	public FireSpirit(WorldTile tile, Player target) {
		super(target, 15451, tile, true);
		life = Ticks.fromMinutes(1);
		getNextHitBars().add(new TimerBar(life * 30));
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (life-- <= 0)
			finish();
	}

	@Override
	public void sendDrop(Player player, Item item) {
		player.getInventory().addItemDrop(item);
	}

	public void giveReward(final Player player) {
		if (player != getOwner() || player.isLocked())
			return;
		player.lock();
		player.setNextAnimation(new Animation(16705));
		WorldTasks.schedule(2, () -> {
			player.unlock();
			player.incrementCount("Fire spirits set free");
			drop(player, false);
			for (int i = 0;i < 5;i++)
				if (Utils.random(100) < 50)
					drop(player, false);
			player.sendMessage("The fire spirit gives you a reward to say thank you for freeing it, before disappearing.");
			finish();
		});
	}
	
	public static NPCClickHandler claim = new NPCClickHandler(new Object[] { 15451 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getNPC() instanceof FireSpirit spirit)
				spirit.giveReward(e.getPlayer());
		}
	};
}
