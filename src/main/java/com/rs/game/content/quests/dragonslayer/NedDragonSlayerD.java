package com.rs.game.content.quests.dragonslayer;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.CRANDOR_MAP;
import static com.rs.game.content.quests.dragonslayer.DragonSlayer.IS_BOAT_FIXED_ATTR;
import static com.rs.game.content.quests.dragonslayer.DragonSlayer.NED;
import static com.rs.game.content.quests.dragonslayer.DragonSlayer.NED_BOAT_VISIBILITY_VAR;
import static com.rs.game.content.quests.dragonslayer.DragonSlayer.NED_IS_CAPTAIN_ATTR;
import static com.rs.game.content.quests.dragonslayer.DragonSlayer.OWNS_BOAT_ATTR;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class NedDragonSlayerD extends Conversation {
	/**
	 * Only called in PREPARE_FOR_CRANDOR stage.
	 */
	public NedDragonSlayerD(Player p) {
		super(p);
		GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER);

		if(attr.getB(NED_IS_CAPTAIN_ATTR)) {
			addPlayer(HeadE.HAPPY_TALKING, "Will you take me to Crandor now?");
			addNPC(NED, HeadE.CALM_TALK, "Sure, meet me at Port Sarim.");
			return;
		}

		addPlayer(HeadE.HAPPY_TALKING, "You're a sailor? Could you take me to the island of Crandor?");
		addNPC(NED, HeadE.CALM_TALK, "Well, I was a sailor. I've not been able to get work at sea these days though. They say I'm too old. Sorry, where was it " +
				"you said you wanted to go?");
		addPlayer(HeadE.HAPPY_TALKING, "To the island of Crandor.");
		addNPC(NED, HeadE.CALM_TALK, "Crandor? But ... It would be a chance to sail a ship once more. I'd sail anywhere if it was a chance to sail again. ");
		addNPC(NED, HeadE.CALM_TALK, "Then again, no captain in his right mind would sail to that island. Ah, you only live once! I'll do it! So, where's your ship?");
		if(attr.getB(OWNS_BOAT_ATTR)) {
			addPlayer(HeadE.HAPPY_TALKING, "It's the Lady Lumbridge, in Port Sarim");
			addNPC(NED, HeadE.CALM_TALK, "That old pile of junk? Last I head, she wasn't seaworthy");
			if(attr.getB(IS_BOAT_FIXED_ATTR)) {
				addPlayer(HeadE.HAPPY_TALKING, "I fixed her up.");
				addNPC(NED, HeadE.CALM_TALK, "You did? Excellent! Just show me the map and we can get ready to go!");
				if(p.getInventory().containsItem(new Item(CRANDOR_MAP))) {
					addPlayer(HeadE.HAPPY_TALKING, "Here you go.");
					addSimple("You hand the map to Ned.", () -> {
						attr.setB(NED_IS_CAPTAIN_ATTR, true);
						p.getInventory().removeItems(new Item(CRANDOR_MAP, 1));
						p.getVars().setVar(NED_BOAT_VISIBILITY_VAR, 7);
					});
					addNPC(NED, HeadE.CALM_TALK, "Excellent! I'll meet you at the ship, then.");
				} else if(p.getBank().containsItem(CRANDOR_MAP, 1)) {
					addPlayer(HeadE.HAPPY_TALKING, "The map's in the bank. I'll have to go and get it.");
					addNPC(NED, HeadE.CALM_TALK, "You do that then.");
				} else {
					addPlayer(HeadE.HAPPY_TALKING, "Uh...yeah...about that. I don't actually have a map on me.");
					addNPC(NED, HeadE.CALM_TALK, "You'd better go find it, then. I'm not going to go sailing off without a map to show me where to head!");
				}
			} else {
				addPlayer(HeadE.CALM_TALK, "Yea I still need to fix her up");
				addNPC(NED, HeadE.CALM_TALK, "You do that then.");
				addPlayer(HeadE.SAD, "I will....");

			}
		}
		else {
			addPlayer(HeadE.HAPPY_TALKING, "I don't have a boat");
			addNPC(NED, HeadE.CALM_TALK, "Well, that's too bad isn't it?");
			addPlayer(HeadE.SAD, "Yes it is...");
		}

	}
}
