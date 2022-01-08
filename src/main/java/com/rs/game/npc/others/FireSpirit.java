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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.others;

import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class FireSpirit extends OwnedNPC {

	private long createTime;

	public FireSpirit(WorldTile tile, Player target) {
		super(target, 15451, tile, true);
		createTime = System.currentTimeMillis();
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (createTime + 60000 < System.currentTimeMillis())
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
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.unlock();
				player.incrementCount("Fire spirits set free");
				drop(player, false);
				for (int i = 0;i < 5;i++)
					if (Utils.random(100) < 50)
						drop(player, false);
				player.sendMessage("The fire spirit gives you a reward to say thank you for freeing it, before disappearing.");
				finish();

			}
		}, 2);
	}
}
