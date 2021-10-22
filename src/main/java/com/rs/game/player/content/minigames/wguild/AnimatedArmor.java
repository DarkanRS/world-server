package com.rs.game.player.content.minigames.wguild;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;

public class AnimatedArmor extends NPC {

	private transient Player player;

	public AnimatedArmor(Player player, int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile);
		this.player = player;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!getCombat().hasTarget() && !isDead())
			finish();
	}

	@Override
	public void sendDeath(final Entity source) {
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(new Animation(836));
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop >= 2) {
					if (source instanceof Player player) {
						for (Integer items : getDroppedItems()) {
							if (items == -1)
								continue;
							World.addGroundItem(new Item(items), new WorldTile(getCoordFaceX(getSize()), getCoordFaceY(getSize()), getPlane()), player, true, 60);
						}
						player.setWarriorPoints(3, WarriorsGuild.ARMOR_POINTS[getId() - 4278]);
					}
					finish();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public int[] getDroppedItems() {
		int index = getId() - 4278;
		int[] droppedItems = WarriorsGuild.ARMOUR_SETS[index];
		return droppedItems;
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		super.finish();
		if (player != null) {
			player.getTemporaryAttributes().remove("animator_spawned");
			if (!isDead()) {
				for (int item : getDroppedItems()) {
					if (item == -1)
						continue;
					player.getInventory().addItemDrop(item, 1);
				}
			}
		}
	}
}
