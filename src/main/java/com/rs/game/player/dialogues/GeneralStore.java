package com.rs.game.player.dialogues;

import com.rs.utils.shop.ShopsHandler;

public class GeneralStore extends Dialogue {

	private int npcId;
	private String shopId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		shopId = (String) parameters[1];
		sendNPCDialogue(npcId, 9827, "Can I help you at all?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please. What are you selling?", "How should I use your shop?", "No, thanks.");
			break;
		case 0:
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, shopId);
				end();
			} else if (componentId == OPTION_2) {
				stage = 1;
				sendNPCDialogue(npcId, 9827, "I'm glad you ask! You can buy as many of the items", "stocked as you wish. The price of these items changes", "based on the amount in stock.");
			} else if (componentId == OPTION_3)
				end();
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npcId, 9827, "You can also sell most items to the shop and the price", "given will be based on the amount in stock.");
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
