package com.rs.game.player.dialogues;

import com.rs.game.object.GameObject;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.skills.cooking.Cooking;
import com.rs.game.player.content.skills.cooking.Cooking.Cookables;

public class CookingD extends Dialogue {

	private Cookables cooking;
	private GameObject object;

	@Override
	public void start() {
		this.cooking = (Cookables) parameters[0];
		this.object = (GameObject) parameters[1];

		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.COOK, "Choose how many you wish to cook,<br>then click on the item to begin.", player.getInventory().getItems().getNumberOf(cooking.getRawItem()), new int[] { cooking.getProduct()
				.getId() }, null);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
		player.getActionManager().setAction(new Cooking(object, cooking.getRawItem(), SkillsDialogue.getQuantity(player)));
	}

	@Override
	public void finish() {

	}

}
