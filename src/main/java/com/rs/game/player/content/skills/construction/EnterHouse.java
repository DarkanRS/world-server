package com.rs.game.player.content.skills.construction;

import com.rs.game.player.dialogues.Dialogue;

/**
 * 
 * @author Jonathan
 * @since January 22th, 2014
 */
public class EnterHouse extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("What would you like to do?", "Go to your house.", "Go to your house (building mode).", "Go to a friend's house.", "Never mind.");
	}

	@Override
	public void run(int interfaceId, int option) {
		switch (stage) {
		case -1:
			switch (option) {
			case OPTION_1:
				player.getHouse().setBuildMode(false);
				player.getHouse().enterMyHouse();
				end();
				break;
			case OPTION_2:
				player.getHouse().kickGuests();
				player.getHouse().setBuildMode(true);
				player.getHouse().enterMyHouse();
				end();
				break;
			case OPTION_3:
				if (player.isIronMan()) {
					player.sendMessage("You cannot enter another player's house as an ironman.");
					end();
					break;
				}
				player.sendInputString("Enter name of the person who's house you'd like to join:", (name) -> House.enterHouse(player, name));
				end();
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {
	}

}