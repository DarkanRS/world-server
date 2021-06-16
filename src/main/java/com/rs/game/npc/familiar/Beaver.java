package com.rs.game.npc.familiar;

import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.summoning.Summoning.Pouches;
import com.rs.game.player.content.skills.woodcutting.TreeType;
import com.rs.game.player.content.skills.woodcutting.Woodcutting;
import com.rs.lib.game.WorldTile;

public class Beaver extends Familiar {

	public Beaver(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Multichop";
	}

	@Override
	public String getSpecialDescription() {
		return "Chops a tree, giving the owner its logs. There is also a chance that random logs may be produced.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 3;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.OBJECT;
	}

	@Override
	public boolean submitSpecial(Object context) {
		GameObject object = (GameObject) context;
		getOwner().getActionManager().setAction(new Woodcutting(object, TreeType.NORMAL));
		return true;
	}
}
