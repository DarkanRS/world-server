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