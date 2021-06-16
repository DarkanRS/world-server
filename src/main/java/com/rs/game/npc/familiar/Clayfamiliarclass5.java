package com.rs.game.npc.familiar;

import com.rs.game.player.Player;
import com.rs.game.player.content.minigames.creations.Score;
import com.rs.game.player.content.skills.summoning.Summoning.Pouches;
import com.rs.game.player.controllers.StealingCreationController;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Clayfamiliarclass5 extends Familiar {

	public Clayfamiliarclass5(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Clay Deposit";
	}

	@Override
	public String getSpecialDescription() {
		return "Deposit all items in the beast of burden's possession in exchange for points.";
	}

	@Override
	public int getBOBSize() {
		return 24;
	}

	@Override
	public int getSpecialAmount() {
		return 30;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		if (getOwner().getControllerManager().getController() == null || !(getOwner().getControllerManager().getController() instanceof StealingCreationController)) {
			dissmissFamiliar(false);
			return false;
		}
		getOwner().setNextSpotAnim(new SpotAnim(1316));
		getOwner().setNextAnimation(new Animation(7660));
		StealingCreationController sc = (StealingCreationController) getOwner().getControllerManager().getController();
		Score score = sc.getGame().getScore(getOwner());
		if (score == null) {
			return false;
		}
		for (Item item : getBob().getBeastItems().getItems()) {
			if (item == null) {
				continue;
			}
			sc.getGame().sendItemToBase(getOwner(), item, sc.getTeam(), true, false);
		}
		return true;
	}
}
