package com.rs.game.content.tutorialisland;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.SimpleStatement;
import com.rs.game.model.entity.player.Player;

public class GamemodeSelection extends Conversation {

	public GamemodeSelection(Player player) {
		super(player);
		
		addNext("start", new SimpleStatement("Welcome to Darkan. We will start by setting up your gamemode options."));
		addOptions("Would you like this account to be an ironman?", ops -> {
			ops.add("Yes (No trading, picking up items that aren't yours, etc.)", new Dialogue().addOptions("Is an ironman account alright with you?", confirm -> {
				confirm.add("Yes", () -> {
					player.setIronMan(true);
					player.setChosenAccountType(true);
					player.getAppearance().generateAppearanceData();
				});
				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
			}));
			ops.add("No", new Dialogue().addOptions("Is a normal account alright with you?", confirm -> {
				confirm.add("Yes", () -> {
					player.setIronMan(false);
					player.setChosenAccountType(true);
					player.getAppearance().generateAppearanceData();
				});
				confirm.add("No, let me choose again.", new Dialogue().addGotoStage("start", this));
			}));
		});

		create();
	}

}
