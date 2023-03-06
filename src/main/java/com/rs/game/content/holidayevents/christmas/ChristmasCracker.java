package com.rs.game.content.holidayevents.christmas;

import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnPlayerHandler;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;
import com.rs.utils.drop.WeightedSet;
import com.rs.utils.drop.WeightedTable;

@PluginEventHandler
public class ChristmasCracker {
	private static DropSet PARTY_HATS = new WeightedSet(
			new WeightedTable(32, new Drop(1038)),
			new WeightedTable(28, new Drop(1040)),
			new WeightedTable(23, new Drop(1048)),
			new WeightedTable(20, new Drop(1044)),
			new WeightedTable(15, new Drop(1042)),
			new WeightedTable(10, new Drop(1046))
			);

	private static DropSet CRACKER_SECONDARIES = new WeightedSet(
			new WeightedTable(11, new Drop(1718)),
			new WeightedTable(11, new Drop(950)),
			new WeightedTable(9, new Drop(1635)),
			new WeightedTable(16, new Drop(1969)),
			new WeightedTable(15, new Drop(1897)),
			new WeightedTable(24, new Drop(1973)),
			new WeightedTable(17, new Drop(2355)),
			new WeightedTable(15, new Drop(441, 5)),
			new WeightedTable(5, new Drop(563)),
			new WeightedTable(5, new Drop(1217))
			);

	public static ItemOnPlayerHandler popCracker = new ItemOnPlayerHandler(new Object[] { 962 }, e -> {
		if (e.getTarget().isIronMan()) {
			e.getPlayer().sendMessage("They stand alone!");
			return;
		}
		if (e.getTarget().getInventory().getFreeSlots() <= 2) {
			e.getPlayer().sendMessage("The other player does not have enough inventory space to recieve a cracker if they win.");
			return;
		}

		if (e.getPlayer().getInventory().getFreeSlots() <= 2) {
			e.getPlayer().sendMessage("You don't have enough inventory space to do this.");
			return;
		}

		int random = Utils.random(1000);
		e.getTarget().setNextFaceTile(e.getPlayer().getTile());
		e.getPlayer().setNextAnimation(new Animation(15152));
		e.getTarget().setNextAnimation(new Animation(15153));
		e.getPlayer().setNextSpotAnim(new SpotAnim(2952));
		e.getPlayer().sendMessage("You use the cracker on " + e.getTarget().getDisplayName() + "..");
		e.getTarget().sendMessage(e.getPlayer().getDisplayName() + " has used a christmas cracker on you..");
		e.getPlayer().getInventory().deleteItem(962, 1);
		if (random < 500 || e.getPlayer().isIronMan()) {
			for (Item rew : DropTable.calculateDrops(PARTY_HATS))
				e.getPlayer().getInventory().addItemDrop(rew);
			for (Item rew : DropTable.calculateDrops(CRACKER_SECONDARIES))
				e.getTarget().getInventory().addItemDrop(rew);
			e.getPlayer().sendMessage("and you got the reward!" + (e.getPlayer().isIronMan() ? " Because you stand alone." : ""));
			e.getTarget().sendMessage("but you didn't get the reward." + (e.getPlayer().isIronMan() ? " Because they stand alone." : ""));
		} else {
			for (Item rew : DropTable.calculateDrops(PARTY_HATS))
				e.getTarget().getInventory().addItemDrop(rew);
			for (Item rew : DropTable.calculateDrops(CRACKER_SECONDARIES))
				e.getPlayer().getInventory().addItemDrop(rew);
			e.getTarget().sendMessage("and you got the reward!");
			e.getPlayer().sendMessage("but you didn't get the reward.");
		}
	});
}
