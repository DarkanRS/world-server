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
package com.rs.game.player.dialogues;

import java.util.function.Consumer;

import com.rs.game.player.Player;
import com.rs.lib.game.Item;

public class DestroyItemConsumer extends Dialogue {

	private Item item;
	private Consumer<Player> onDestroy;

	public DestroyItemConsumer(Item item, Consumer<Player> onDestroy) {
		this.item = item;
		this.onDestroy = onDestroy;
	}

	@Override
	public void start() {
		player.getInterfaceManager().sendChatBoxInterface(1183);
		player.getPackets().setIFText(1183, 7, item.getName());
		player.getPackets().setIFItem(1183, 13, item.getId(), 1);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == 1183 && componentId == 9) {
			onDestroy.accept(player);
			player.getInventory().deleteItem(item);
			player.getPackets().sendSound(4500, 0, 1);
		}
		end();
	}

	@Override
	public void finish() {

	}

}