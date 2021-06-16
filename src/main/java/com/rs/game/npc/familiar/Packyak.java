package com.rs.game.npc.familiar;

import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.skills.summoning.Summoning.Pouches;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Packyak extends Familiar {

	public Packyak(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, false);
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public String getSpecialName() {
		return "Winter Storage";
	}

	@Override
	public String getSpecialDescription() {
		return "Use special move on an item in your inventory to send it to your bank.";
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ITEM;
	}

	@Override
	public int getBOBSize() {
		return 30;
	}

	@Override
	public boolean isAgressive() {
		return false;
	}

	@Override
	public boolean submitSpecial(Object object) {
		int slotId = (Integer) object;
		if (ItemConstants.isDungItem(getOwner().getInventory().getItem(slotId).getId())) {
			getOwner().sendMessage("I don't know how you managed to get a yak in here, but do you think");
			getOwner().sendMessage("the owner is retarded enough to let you bank a dung item with this scroll?");
			return false;
		}
		if (getSpecialEnergy() > getSpecialAmount()) {
			if (getOwner().getBank().hasBankSpace()) {
				getOwner().incrementCount("Items banked with yak");
				getOwner().getInventory().deleteItem(12435, 1);
				getOwner().getBank().depositItem(slotId, 1, true);
				getOwner().sendMessage("Your pack yak has sent an item to your bank.");
				getOwner().setNextSpotAnim(new SpotAnim(1316));
				getOwner().setNextAnimation(new Animation(7660));
				drainSpecial(getSpecialAmount());
			}
		} else {
			getOwner().sendMessage("Your familiar does not have enough special energy.");
			return false;
		}
		return true;
	}
}
