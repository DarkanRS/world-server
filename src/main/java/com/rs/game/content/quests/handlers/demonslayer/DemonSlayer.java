// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.quests.handlers.demonslayer;

import java.util.ArrayList;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Areas;

@QuestHandler(Quest.DEMON_SLAYER)
@PluginEventHandler
public class DemonSlayer extends QuestOutline {
	//stages
	final static int NOT_STARTED_STAGE = 0;
	final static int AFTER_GYPSY_ARIS_INTRO_STAGE = 1;
	final static int AFTER_SIR_PRYSIN_INTRO_STAGE = 2;
	final static int KEY1_DRAIN_LOCATION_KNOWN_STAGE = 3;
	final static int KEY2_WIZARD_LOCATION_KNOWN_STAGE = 4;
	final static int KEY3_ROVIN_LOCATION_KNOWN_STAGE = 5;
	final static int WIZARD_RITUAL_KNOWN_STAGE = 6;
	final static int WIZARD_KEY_PREVIOUSLY_RETRIEVED_STAGE = 7;
	final static int SILVERLIGHT_OBTAINED_STAGE = 8;
	final static int QUEST_COMPLETE_STAGE = 9;

	//attributes
	final static String AFTER_SIR_PRYSIN_INTRO_ATTR = "AFTER_PRYSIN_INTRO";
	final static String KEY1_DRAIN_LOCATION_KNOWN_ATTR = "KEY1_DRAIN_LOC_KNOWN";
	final static String KEY2_WIZARD_LOCATION_KNOWN_ATTR = "KEY2_WIZARD_LOC_KNOWN";
	final static String KEY3_ROVIN_LOCATION_KNOWN_ATTR = "KEY3_ROVIN_LOC_KNOWN";
	final static String WIZARD_RITUAL_KNOWN_ATTR = "WIZARD_RITUAL_KNOWN";
	final static String WIZARD_KEY_PREVIOUSLY_RETRIEVED_ATTR = "WIZARD_KEY_PREVIOUSLY_RETRIEVED";

	//Items
	final static int WIZARD_KEY = 2399;
	final static int ROVIN_KEY = 2400;
	final static int PRYSIN_KEY = 2401;
	final static int SILVERLIGHT = 2402;


	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE_STAGE;
	}
	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
		case NOT_STARTED_STAGE:
			lines.add("A mighty demon is being summoned to destroy the city");
			lines.add("of Varrock. You are the one destined to stop him.");
			lines.add("");
			lines.add("I can start this quest by speaking to Gypsy Aris in");
			lines.add("her tent at Varrock square.");
			lines.add("");
			lines.add("I will have to assure her I will try my best to stop");
			lines.add("the demon and complete this quest.");
			lines.add("");
			break;
		case AFTER_GYPSY_ARIS_INTRO_STAGE:
			lines.add("Gypsy Aris says Sir Prysin knows where Silverlight");
			lines.add("would be. I can find him in the first floor of Varrock");
			lines.add("castle to the south west.");
			lines.add("");
			break;
		case AFTER_SIR_PRYSIN_INTRO_STAGE:
		case KEY1_DRAIN_LOCATION_KNOWN_STAGE:
		case KEY2_WIZARD_LOCATION_KNOWN_STAGE:
		case KEY3_ROVIN_LOCATION_KNOWN_STAGE:
		case WIZARD_RITUAL_KNOWN_STAGE:
		case WIZARD_KEY_PREVIOUSLY_RETRIEVED_STAGE:
			lines.add("Sir Prysin says Silverlight was placed in a special");
			lines.add("box. It can only be opened with special keys...");
			lines.add("");

			//---Sir Prysin---
			if(player.getQuestManager().getAttribs(Quest.DEMON_SLAYER).getB(KEY1_DRAIN_LOCATION_KNOWN_ATTR)) {
				lines.add("Sir Prysin's key is stuck in a drain North West");
				lines.add("of Varrock castle. I can use a bucket of water");
				lines.add("to push it into the sewers.");
				lines.add("");
			}
			if(player.getVars().getVarBit(2568) == 1) {
				lines.add("The key was pushed into the varrock sewers,");
				lines.add("I just have to find it...");
				lines.add("");
			}
			if(player.getInventory().containsItem(PRYSIN_KEY)) {
				lines.add("I have gotten Sir Prysin's key");
				lines.add("");
			}
			//------

			if(player.getQuestManager().getAttribs(Quest.DEMON_SLAYER).getB(KEY2_WIZARD_LOCATION_KNOWN_ATTR)) {
				lines.add("I can speak to Wizard Traiborn in the wizards");
				lines.add("tower to ask about the 2nd key.");
				lines.add("");
			}
			if(player.getQuestManager().getAttribs(Quest.DEMON_SLAYER).getB(WIZARD_RITUAL_KNOWN_ATTR)) {
				lines.add("Wizard Traiborn needs 25 unnoted regular bones for");
				lines.add("a ritual to get a silverlight key.");
				lines.add("");
			}
			if(player.getInventory().containsItem(WIZARD_KEY)) {
				lines.add("I have gotten Wizard Traiborn's key");
				lines.add("");
			}

			if(player.getQuestManager().getAttribs(Quest.DEMON_SLAYER).getB(KEY3_ROVIN_LOCATION_KNOWN_ATTR)) {
				lines.add("I can speak to Roving in the North West tower");
				lines.add("on the 3rd floor for his key.");
				lines.add("");
			}

			if(player.getInventory().containsItem(ROVIN_KEY)) {
				lines.add("I have gotten Captain Rovin's key");
				lines.add("");
			}
			break;
		case SILVERLIGHT_OBTAINED_STAGE:
			lines.add("I can now fight Delrith at the Dark Wizards altar");
			lines.add("I must bring Silverlight for the scene to occur.");
			lines.add("If I forgot the incantation I can ask Gypsy Aris");
			lines.add("");
			break;
		case QUEST_COMPLETE_STAGE:
			lines.add("");
			lines.add("QUEST COMPLETE!");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		getQuest().sendQuestCompleteInterface(player, SILVERLIGHT, "Silverlight");
	}

	public static ItemOnObjectHandler handleBucketOfWaterOnDrain = new ItemOnObjectHandler(new Object[] { 31759 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			Player p = e.getPlayer();
			if(p.getVars().getVarBit(2568) == 0 && e.getItem().getId() == 1929) {
				p.getInventory().replace(e.getItem(), new Item(1925, 1));
				e.getPlayer().getVars().setVarBit(2568, 1);
				p.sendMessage("You pour the liquid down the drain.");
				p.startConversation(new Conversation(p) {
					{
						addPlayer(HeadE.WORRIED, "OK, I think I've washed the key down into the sewer. I'd better go down and get it!");
						create();
					}
				});
			}

		}
	};

	public static ObjectClickHandler handleDrainSearch = new ObjectClickHandler(new Object[] { 31759 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			p.sendMessage("You peer into the drain.");
            if(!p.getQuestManager().isComplete(Quest.DEMON_SLAYER) && !p.getInventory().containsItem(PRYSIN_KEY) && p.getVars().getVarBit(2568) != 1)
                p.getVars().setVarBit(2568, 0);
			p.startConversation(new Conversation(p) {
				{
					if(p.getVars().getVarBit(2568) == 0)
						addPlayer(HeadE.WORRIED, "It looks like I will need to wash the key down with a bucket of water.");
					if(p.getVars().getVarBit(2568) == 1)
						addPlayer(HeadE.HAPPY_TALKING, "Okay, time to go in the sewers and get that key...");
					if(p.getVars().getVarBit(2568) == 2)
						addPlayer(HeadE.SKEPTICAL_THINKING, "Filthy in there...");
					create();
				}
			});
		}
	};

	public static ItemClickHandler handleDarklightQuestRequirement = new ItemClickHandler(new Object[]{6746}, new String[]{"Wield"}) {
		@Override
		public void handle(ItemClickEvent e) {
			if (e.getPlayer().isEquipDisabled())
				return;
			if (!e.getPlayer().getQuestManager().isComplete(Quest.DEMON_SLAYER)) {
				e.getPlayer().sendMessage("You must complete the Demon Slayer quest to use this item...");
				return;
			}
			Equipment.sendWear(e.getPlayer(), e.getSlotId(), e.getItem().getId());
		}
	};

	public static ObjectClickHandler handleRustyKey = new ObjectClickHandler(new Object[] { 17431 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			p.getInventory().addItem(PRYSIN_KEY);
			p.startConversation(new Conversation(p) {
				{
					addItem(PRYSIN_KEY, "You pick up an old rusty key.");
					create();
				}
			});
			e.getPlayer().getVars().setVarBit(2568, 2);
		}
	};

	public static EnterChunkHandler handleFinalCutsceneChunk = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			if (e.getEntity() instanceof Player p && p.hasStarted() && Areas.withinArea("dark_wizard_altar", e.getChunkId())) {
				if(p.getQuestManager().getStage(Quest.DEMON_SLAYER) != SILVERLIGHT_OBTAINED_STAGE || p.getTempAttribs().getB("FinalDemonSlayerCutscene") || (!p.getInventory().containsItem(SILVERLIGHT) && !p.getEquipment().getWeaponName().equalsIgnoreCase("Silverlight")))
					return;
				p.getTempAttribs().setB("FinalDemonSlayerCutscene", true);
				p.getControllerManager().startController(new DemonSlayer_PlayerVSDelrith());
			}
		}
	};

	public static LoginHandler onLogin = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if(e.getPlayer().getQuestManager().isComplete(Quest.DEMON_SLAYER))
				e.getPlayer().getVars().setVarBit(2568, 2);
			else
				e.getPlayer().getVars().setVarBit(2568, 0);
		}
	};
}
