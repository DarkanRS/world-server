package com.rs.game.npc.dungeoneering;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants.GuardianMonster;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class ForgottenWarrior extends Guardian {

	public ForgottenWarrior(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
	}

	@Override
	public void drop() {
		super.drop();
		GuardianMonster m = GuardianMonster.forId(getId());
		if (m == null)
			return;
		int size = getSize();
		ArrayList<Item> drops = new ArrayList<Item>();
		int tier = getDefinitions().combatLevel / 11;
		if (tier > 10)
			tier = 10;
		else if (tier < 1)
			tier = 1;
		if (m.name().contains("WARRIOR"))
			drops.add(new Item(DungeonUtils.getRandomMeleeGear(Utils.random(tier) + 1)));
		else if (m.name().contains("MAGE"))
			drops.add(new Item(DungeonUtils.getRandomMagicGear(Utils.random(tier) + 1)));
		else
			drops.add(new Item(DungeonUtils.getRandomRangeGear(Utils.random(tier) + 1)));
		for (Item item : drops)
			World.addGroundItem(item, new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane()));
	}

}
