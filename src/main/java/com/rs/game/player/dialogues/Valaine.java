package com.rs.game.player.dialogues;

import com.rs.game.npc.NPC;
import com.rs.utils.shop.ShopsHandler;

public class Valaine extends Dialogue {

	private NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendNPCDialogue(npc.getId(), 9827, "Hello there. Want to have a look at what we're selling", "today?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.", "How should I use your shop?", "No, thank you.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, "valaines_shop_of_champions");
				end();
				break;
			case OPTION_2:
				stage = 1;
				sendNPCDialogue(npc.getId(), 9827, "I'm glad you ask! You can buy as many of the items", "stocked as you wish. The price of these items changes", "based on the amount in stock.");
				break;
			case OPTION_3:
			default:
				end();
				break;
			}
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npc.getId(), 9827, "You can also sell most items to the shop and the price given will be based on the amount in stock.");
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
