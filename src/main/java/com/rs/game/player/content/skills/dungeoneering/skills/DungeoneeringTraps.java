package com.rs.game.player.content.skills.dungeoneering.skills;

import com.rs.game.npc.NPC;
import com.rs.game.npc.dungeoneering.MastyxTrap;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public class DungeoneeringTraps {

	public static final int[] ITEM_TRAPS =
	{ 17756, 17758, 17760, 17762, 17764, 17766, 17768, 17770, 17772, 17774 };
	private static final int[] MASTRYX_HIDES =
	{ 17424, 17426, 17428, 17430, 17432, 17434, 17436, 17438, 17440, 17442 };
	private static final int[] HUNTER_LEVELS =
	{ 1, 10, 20, 30, 40, 50, 60, 70, 80, 90 };

	public static void placeTrap(final Player player, final DungeonManager manager, final int index) {
		int levelRequired = HUNTER_LEVELS[index];
		if (manager.getMastyxTraps().size() > 5) {
			player.sendMessage("Your party has already placed the maximum amount of traps allowed.");
			return;
		} else if (player.getSkills().getLevel(Constants.HUNTER) < levelRequired) {
			player.sendMessage("You need a Hunter level of " + levelRequired + " in order to place this trap.");
			return;
		}
		player.lock(2);
		player.setNextAnimation(new Animation(827));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				manager.addMastyxTrap(new MastyxTrap(player.getDisplayName(), 11076 + index, player, -1, false));
				player.getInventory().deleteItem(new Item(ITEM_TRAPS[index], 1));
				player.sendMessage("You lay the trap onto the floor.");
			}
		}, 2);
	}

	public static void removeTrap(Player player, MastyxTrap trap, DungeonManager manager) {
		if (player.getDisplayName().equals(trap.getPlayerName())) {
			player.setNextAnimation(new Animation(827));
			player.sendMessage("You dismantle the trap.");
			player.getInventory().addItem(ITEM_TRAPS[trap.getTier()], 1);
			manager.removeMastyxTrap(trap);
		} else
			player.sendMessage("This trap is not yours to remove!");
	}

	public static void skinMastyx(Player player, NPC npc) {
		player.setNextAnimation(new Animation(827));
		player.getInventory().addItemDrop(MASTRYX_HIDES[getNPCTier(npc.getId() - 10)], Utils.random(2, 5));
		npc.finish();
	}

	public static int getNPCTier(int id) {
		return id - 11086;
	}
}
