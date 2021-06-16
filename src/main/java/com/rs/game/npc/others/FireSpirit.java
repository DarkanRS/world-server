package com.rs.game.npc.others;

import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
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
		WorldTasksManager.schedule(new WorldTask() {
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
