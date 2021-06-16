package com.rs.game.player.dialogues;

import com.rs.game.player.content.AncientEffigies;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

/**
 * Ancient effigies dialogue handling.
 * 
 * @author Raghav/Own4g3 <Raghav_ftw@hotmail.com>
 * 
 */
public class AncientEffigiesD extends Dialogue {

	Item item;
	int skill1; // this might needs to be saved
	int skill2;

	@Override
	public void start() {
		item = (Item) parameters[0];
		int type = -1;
		if (item.getMetaData("effigyType") == null) {
			type = Utils.getRandomInclusive(7);
			player.getInventory().replace(item, new Item(item.getId(), item.getAmount()).addMetaData("effigyType", type));
		} else {
			type = item.getMetaDataI("effigyType");
			if (((int) Math.floor(type)) >= AncientEffigies.SKILL_1.length) {
				type = Utils.getRandomInclusive(7);
				player.getInventory().replace(item, new Item(item.getId(), item.getAmount()).addMetaData("effigyType", type));
			}
		}
		skill1 = AncientEffigies.SKILL_1[type];
		skill2 = AncientEffigies.SKILL_2[type];
		sendDialogue(new String[] { "As you inspect the ancient effigy, you begin to feel a", "strange sensation of the relic searching your mind,", "drawing on your knowledge." });
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendDialogue(new String[] { "Images from your experiences of " + AncientEffigies.getMessage(skill1), "fill your mind." });
			stage = 0;
		} else if (stage == 0) {
			player.getTemporaryAttributes().put("skill1", skill1);
			player.getTemporaryAttributes().put("skill2", skill2);
			sendOptionsDialogue("Which images do you wish to focus on?", Constants.SKILL_NAME[skill1], Constants.SKILL_NAME[skill2]);
			stage = 1;
		} else if (stage == 1 && componentId == OPTION_1) {
			if (player.getSkills().getLevel((Integer) player.getTemporaryAttributes().get("skill1")) < AncientEffigies.getRequiredLevel(item.getId())) {
				sendDialogue(new String[] { "The images in your mind fade; the ancient effigy seems", "to desire knowledge of experiences you have not yet", "had." });
				player.sendMessage("You require at least level" + AncientEffigies.getRequiredLevel(item.getId()) + Constants.SKILL_NAME[(Integer) player.getTemporaryAttributes().get("skill1")] + " to investigate the ancient effigy further.");
				player.setNextAnimation(new Animation(4067));
			} else {
				player.getTemporaryAttributes().put("skill", skill1);
				sendDialogue(new String[] { "As you focus on your memories, you can almost hear a", "voice in the back of your mind whispering to you..." });
				stage = 2;
			}
		} else if (stage == 1 && componentId == OPTION_2) {
			if (player.getSkills().getLevel((Integer) player.getTemporaryAttributes().get("skill2")) < AncientEffigies.getRequiredLevel(item.getId())) {
				sendDialogue(new String[] { "The images in your mind fade; the ancient effigy seems", "to desire knowledge of experiences you have not yet", "had." });
				player.sendMessage("You require at least level" + AncientEffigies.getRequiredLevel(item.getId()) + " " + Constants.SKILL_NAME[(Integer) player.getTemporaryAttributes().get("skill1")] + " to investigate the ancient effigy further.");
				player.setNextAnimation(new Animation(4067));
			} else {
				player.getTemporaryAttributes().put("skill", skill2);
				sendDialogue(new String[] { "As you focus on your memories, you can almost hear a", "voice in the back of your mind whispering to you..." });
				stage = 2;
			}
		} else if (stage == 2) {
			player.getSkills().addXpLamp((Integer) player.getTemporaryAttributes().get("skill"), AncientEffigies.getExp(item.getId()));
			player.sendMessage("You have gained " + AncientEffigies.getExp(item.getId()) + " " + Constants.SKILL_NAME[(Integer) player.getTemporaryAttributes().get("skill")] + " experience!");
			AncientEffigies.effigyInvestigation(player, item);
			sendDialogue(new String[] { "The ancient effigy glows briefly; it seems changed", "somehow and no longer responds to the same memories", "as before." });
			stage = 3;
		} else if (stage == 3) {
			sendDialogue(new String[] { "A sudden bolt of inspiration flashes through your mind,", "revealing new insight into your experiences!" });
			stage = -2;
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
