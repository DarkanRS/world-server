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

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SirPrysinDemonSlayerD extends Conversation {
	Player p;
	final int SIR_PRYSIN = 883;
	final int SIR_PRYSIN_W_SWORD = 4657;
	final int ASK_ABOUT_SILVERLIGHT = 0;
	final int ASK_ABOUT_KEYS_OPTIONS = 1;
	final int GIVE_KEYS_DIALOGUE = 2;
	final int KEY_OPTIONS = 3;
	final int KEY_LOCATIONS_OPTIONS = 4;
	final int SILVERLIGHT_CUTSCENE = 5;

	public SirPrysinDemonSlayerD(Player p) {
		super(p);
		this.p = p;

		if(p.getQuestManager().getStage(Quest.DEMON_SLAYER) >= DemonSlayer.SILVERLIGHT_OBTAINED_STAGE) {
			addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Hello again!");
			addNext(() -> {
				p.startConversation(new SirPrysinDemonSlayerD(p, SILVERLIGHT_CUTSCENE).getStart());
			});
			return;
		}

		if(p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).getB(DemonSlayer.AFTER_SIR_PRYSIN_INTRO_ATTR)) {
			if(hasAllKeys(p)) {
				addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Hello again!");
				addNext(() -> {
					p.startConversation(new SirPrysinDemonSlayerD(p, SILVERLIGHT_CUTSCENE).getStart());
				});
			} else {
				addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Hello again.");
				addNext(() -> {
					p.startConversation(new SirPrysinDemonSlayerD(p, KEY_LOCATIONS_OPTIONS).getStart());
				});
			}
		} else {
			addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Hello, who are you?");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("I am a mighty adventurer. Who are you?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I am a mighty adventurer. Who are you?")
							.addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "I am Sir Prysin. A bold and famous knight of the realm."));
					option("I'm not sure, I was hoping you could tell me.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure, I was hoping you could tell me.")
							.addNPC(SIR_PRYSIN, HeadE.SKEPTICAL, "Well I've never met you before."));
					if(p.getQuestManager().getStage(Quest.DEMON_SLAYER) == DemonSlayer.AFTER_GYPSY_ARIS_INTRO_STAGE)
						option("Gypsy Aris said I should come and talk to you.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Gypsy Aris said I should come and talk to you.")
								.addNPC(SIR_PRYSIN, HeadE.AMAZED_MILD, "Gypsy Aris? Is she still alive? I remember her from when I was pretty young. Well what do you " +
										"need to talk to me about?")
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("I need to find Silverlight.", new Dialogue()
												.addNext(() -> {p.startConversation(new SirPrysinDemonSlayerD(p, ASK_ABOUT_SILVERLIGHT).getStart());}));
										option("Yes, she is still alive.", new Dialogue()
												.addPlayer(HeadE.CALM_TALK, "Yes she is still alive. She lives right outside the castle!")
												.addNPC(SIR_PRYSIN, HeadE.AMAZED_MILD, "Oh, is that the same gypsy? I would have thought she would have died by now. She was " +
														"pretty old when I was a lad.")
												.addNPC(SIR_PRYSIN, HeadE.CALM_TALK, "Anyway, what can I do for you?")
												.addNext(() -> {p.startConversation(new SirPrysinDemonSlayerD(p, ASK_ABOUT_SILVERLIGHT).getStart());}));
									}
								}));

				}
			});
		}
	}

	public SirPrysinDemonSlayerD(Player p, int convoID) {
		super(p);
		this.p = p;

		switch(convoID) {
		case ASK_ABOUT_SILVERLIGHT:
			askAboutSilverlight(p);
			break;
		case ASK_ABOUT_KEYS_OPTIONS:
			askingAboutGivingKeyOptions(p);
			break;
		case GIVE_KEYS_DIALOGUE:
			soGiveKeysDialogue(p);
			break;
		case KEY_OPTIONS:
			askingAboutGivingKeyOptions(p);
			break;
		case KEY_LOCATIONS_OPTIONS:
			keyLocationsOptions(p);
			break;
		case SILVERLIGHT_CUTSCENE:
			silverLightCutscene(p);
			break;
		}

	}

	private void askAboutSilverlight(Player p) {
		addPlayer(HeadE.TALKING_ALOT, "I need to find Silverlight.");
		addNPC(SIR_PRYSIN, HeadE.SKEPTICAL, "What do you need to find that for?");
		addPlayer(HeadE.TALKING_ALOT, "I need it to fight Delrith.");
		addNPC(SIR_PRYSIN, HeadE.SKEPTICAL_THINKING, "Delrith? I thought the world was rid of him, thanks to my great-grandfather.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Well, the gypsy's crystal ball seems to think otherwise.", new Dialogue()
						.addPlayer(HeadE.TALKING_ALOT, "Well, the gypsy's crystal ball seems to think otherwise.")
						.addNPC(SIR_PRYSIN, HeadE.AMAZED_MILD, "Well if the ball says so, I'd better help you.")
						.addNPC(SIR_PRYSIN, HeadE.SKEPTICAL_THINKING, "The problem is getting Silverlight.")
						.addPlayer(HeadE.AMAZED_MILD, "You mean you don't have it?")
						.addNPC(SIR_PRYSIN, HeadE.NERVOUS, "Oh I do have it, but it is so powerful that the king made me put it in a special box which " +
								"needs three different keys to open it. That way it won't fall into the wrong hands.")
						.addNext(() -> {p.startConversation(new SirPrysinDemonSlayerD(p, ASK_ABOUT_KEYS_OPTIONS).getStart());}));
				option("He's back and unfortunately I've got to deal with him.", new Dialogue()
						.addPlayer(HeadE.AMAZED_MILD, "He's back and unfortunately I've got to deal with him.")
						.addNPC(SIR_PRYSIN, HeadE.CALM_TALK, "You don't look up to much. I suppose Silverlight may be good enough to carry you through though.")
						.addNPC(SIR_PRYSIN, HeadE.CALM_TALK, "The problem is getting Silverlight.")
						.addPlayer(HeadE.WORRIED, "You mean you don't have it?")
						.addNPC(SIR_PRYSIN, HeadE.NERVOUS, "Oh I do have it, but it is so powerful that the king made me put it in a special box which " +
								"needs three different keys to open it. That way it won't fall into the wrong hands.")
						.addNext(() -> {p.startConversation(new SirPrysinDemonSlayerD(p, ASK_ABOUT_KEYS_OPTIONS).getStart());}));
			}
		});
	}

	private void soGiveKeysDialogue(Player p) {
		addNPC(SIR_PRYSIN, HeadE.NERVOUS, "Um, well, it's not so easy");
		addNPC(SIR_PRYSIN, HeadE.NERVOUS, "I kept one of the keys. I gave the other two to other people for safe keeping.");
		addNPC(SIR_PRYSIN, HeadE.NERVOUS, "One I gave to Rovin, the captain of the palace guard.");
		addNPC(SIR_PRYSIN, HeadE.NERVOUS, "I gave the other to the wizard Traiborn.");
		addNext(() -> {
			p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).setB(DemonSlayer.AFTER_SIR_PRYSIN_INTRO_ATTR, true);
			p.getQuestManager().setStage(Quest.DEMON_SLAYER, DemonSlayer.AFTER_SIR_PRYSIN_INTRO_STAGE);
			p.startConversation(new SirPrysinDemonSlayerD(p, KEY_LOCATIONS_OPTIONS).getStart());
		});
	}

	private void askingAboutGivingKeyOptions(Player p) {
		addOptions("Choose an option", new Options() {
			@Override
			public void create() {
				option("So give me the keys!", new Dialogue()
						.addPlayer(HeadE.AMAZED_MILD, "So give me the keys!")
						.addNext(() -> {p.startConversation(new SirPrysinDemonSlayerD(p, GIVE_KEYS_DIALOGUE).getStart());}));
				option("And why is this a problem?", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "And why is this a problem?")
						.addNext(() -> {p.startConversation(new SirPrysinDemonSlayerD(p, GIVE_KEYS_DIALOGUE).getStart());}));
			}
		});
	}

	private void keyLocationsOptions(Player p) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Can you give me your key?", new Dialogue()
						.addPlayer(HeadE.SECRETIVE, "Can you give me your key?")
						.addNPC(SIR_PRYSIN, HeadE.WORRIED, "Um.... ah....")
						.addNPC(SIR_PRYSIN, HeadE.SAD_MILD_LOOK_DOWN, "Well there's a problem there as well.")
						.addNPC(SIR_PRYSIN, HeadE.SAD_MILD_LOOK_DOWN, "I managed to drop the key in the drain just outside the palace kitchen. " +
								"It is just inside and I can't reach it.", ()->{
									p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).setB(DemonSlayer.KEY1_DRAIN_LOCATION_KNOWN_ATTR, true);
								})
						.addNext(()->{p.startConversation(new SirPrysinDemonSlayerD(p, KEY_LOCATIONS_OPTIONS).getStart());}));
				option("Where can I find Captain Rovin?", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Where can I find Captain Rovin?")
						.addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Captain Rovin lives at the top of the guards' quarters in the north-west wing of this" +
								" palace.", ()->{
									p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).setB(DemonSlayer.KEY3_ROVIN_LOCATION_KNOWN_ATTR, true);
								})
						.addNext(()->{p.startConversation(new SirPrysinDemonSlayerD(p, KEY_LOCATIONS_OPTIONS).getStart());}));
				option("Where does the wizard live?", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Where does the wizard live?")
						.addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Wizard Traiborn?")
						.addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "He is one of the wizards who lives in the tower on the little island just off the south coast. " +
								"I believe his quarters are on the first floor of the tower.", ()->{
									p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).setB(DemonSlayer.KEY2_WIZARD_LOCATION_KNOWN_ATTR, true);
								})
						.addNext(()->{p.startConversation(new SirPrysinDemonSlayerD(p, KEY_LOCATIONS_OPTIONS).getStart());}));
				if(p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).getB(DemonSlayer.KEY1_DRAIN_LOCATION_KNOWN_ATTR))
					option("So what does the drain lead to?", new Dialogue()
							.addPlayer(HeadE.SKEPTICAL_THINKING, "So what does the drain connect to?")
							.addNPC(SIR_PRYSIN, HeadE.CALM_TALK, "It is the drain for the drainpipe running from the sink in the kitchen down to the palace sewers.")
							.addNext(()->{p.startConversation(new SirPrysinDemonSlayerD(p, KEY_LOCATIONS_OPTIONS).getStart());}));
				option("Farewell.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Well I'd better go key hunting.")
						.addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Ok, goodbye."));
			}
		});
	}

	private boolean hasAllKeys(Player p) {
		Inventory inv = p.getInventory();
		return inv.containsItem(DemonSlayer.ROVIN_KEY) && inv.containsItem(DemonSlayer.WIZARD_KEY) && inv.containsItem(DemonSlayer.PRYSIN_KEY);
	}

	private void silverLightCutscene(Player p) {
		if(p.getInventory().containsItem(DemonSlayer.SILVERLIGHT)) {
			addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Silverlight looks good on you...");
			addPlayer(HeadE.HAPPY_TALKING, "I guess it does");
			return;
		}
		if(p.getQuestManager().getStage(Quest.DEMON_SLAYER) >= DemonSlayer.SILVERLIGHT_OBTAINED_STAGE) {
			addPlayer(HeadE.SAD_MILD, "I lost Silverlight");
			if(!p.getInventory().hasFreeSlots()) {
				addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Good thing I found Silverlight again.");
				addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "You will need space in your inventory though.");
				addPlayer(HeadE.FRUSTRATED, "Oh, good thing you found it. I will make room.");
				return;
			}
			addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Good thing I found Silverlight again.", ()-> {cutscene(p);});
			addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "That sword belonged to my great-grandfather. Make sure you treat it with respect!");
			return;
		}

		//first time reaching this function without silverlightin inventory
		addPlayer(HeadE.HAPPY_TALKING, "I've got all three keys!");
		addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Excellent! Now I can give you Silverlight.", ()-> {cutscene(p);});
		addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "That sword belonged to my great-grandfather. Make sure you treat it with respect!");
		addNPC(SIR_PRYSIN, HeadE.HAPPY_TALKING, "Now go kill that demon!");
	}

	private void cutscene(Player p) {
		for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
			if(npc.getId() == SIR_PRYSIN) {
				npc.transformIntoNPC(266);
				NPC dummy = World.spawnNPC(SIR_PRYSIN, WorldTile.of(3204, 3470, 0), -1, false, true);
				dummy.setRandomWalk(false);
				dummy.faceTile(WorldTile.of(3204, 3469, 0));
				WorldTasks.schedule(new WorldTask() {
					int tick;
					WorldTile playerTile;
					@Override
					public void run() {
						if (tick == 0) {
							p.lock();
							playerTile = p.getTile();
							p.setNextWorldTile(WorldTile.of(3204, 3471, 0));
							p.faceEntity(dummy);
						} else if (tick == 1) {
							dummy.setNextAnimation(new Animation(2579));
							p.getVars().setVarBit(6922, 1);
						} else if(tick == 2) {
							dummy.setNextAnimation(new Animation(4611));
							p.getVars().setVarBit(6922, 2);
						} else if(tick == 3) {
							dummy.setNextAnimation(new Animation(2579));
							p.getVars().setVarBit(6922, 0);
							dummy.transformIntoNPC(SIR_PRYSIN_W_SWORD);
						} else if(tick == 4)
							dummy.faceEntity(p);
						else if(tick == 5)
							dummy.setNextAnimation(new Animation(15953));
						else if(tick == 6) {
							dummy.transformIntoNPC(SIR_PRYSIN);
							p.setNextAnimation(new Animation(15952));
							p.getInventory().addItem(DemonSlayer.SILVERLIGHT, 1);
							p.getQuestManager().setStage(Quest.DEMON_SLAYER, DemonSlayer.SILVERLIGHT_OBTAINED_STAGE);
							p.getInventory().deleteItem(DemonSlayer.PRYSIN_KEY, 1);
							p.getInventory().deleteItem(DemonSlayer.WIZARD_KEY, 1);
							p.getInventory().deleteItem(DemonSlayer.ROVIN_KEY, 1);
						} else if(tick == 9) {
							p.unlock();
							p.setNextWorldTile(playerTile);
							dummy.finish();
							npc.transformIntoNPC(SIR_PRYSIN);
							stop();
						}
						tick++;
					}
				}, 0, 1);
			}
	}

	public static NPCClickHandler handleSirPrysin = new NPCClickHandler(new Object[] { 883 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new SirPrysinDemonSlayerD(e.getPlayer()).getStart());
		}
	};
}

