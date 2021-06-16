package com.rs.game.player.dialogues;

import com.rs.game.player.actions.FillAction;
import com.rs.game.player.actions.FillAction.Filler;
import com.rs.game.player.content.SkillsDialogue;

public class FillingD extends Dialogue {

	private Filler filler;

	@Override
	public void start() {
		this.filler = (Filler) parameters[0];
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "Choose how many you wish to fill,<br>then click on the item to begin.", player.getInventory().getItems().getNumberOf(filler.getEmptyItem()), new int[] { filler.getFilledItem()
				.getId() }, null);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(new FillAction(SkillsDialogue.getQuantity(player), filler));
		end();
	}

	@Override
	public void finish() {

	}

}