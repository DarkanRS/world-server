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
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class WizardTraibornDemonSlayerD extends Conversation {
	Player p;
	final int WIZARD_TRAIBORN = 881;
	final int ABOUT_SIR_PRYSIN = 0;
	final int KEYS_DIALOGUE = 1;
	final int KEYS_OPTIONS = 2;
	final int HAS_BONES = 3;
	final int DOES_NOT_HAVE_BONES = 4;
	final int RETRIEVE_KEY_AGAIN = 5;

	public WizardTraibornDemonSlayerD(Player p) {
		super(p);
		this.p = p;

		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Ello young thingummywut.");
		if(!p.isQuestComplete(Quest.DEMON_SLAYER) && p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).getB(DemonSlayer.WIZARD_RITUAL_KNOWN_ATTR))
			if(p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).getB(DemonSlayer.WIZARD_KEY_PREVIOUSLY_RETRIEVED_ATTR)) {
				if(!p.getInventory().containsItem(DemonSlayer.WIZARD_KEY)) {
					addNext(()->{p.startConversation(new WizardTraibornDemonSlayerD(p, RETRIEVE_KEY_AGAIN).getStart());});
					return;
				}
			} else if(p.getInventory().containsItem(526, 25)) {
				addNext(()->{p.startConversation(new WizardTraibornDemonSlayerD(p, HAS_BONES).getStart());});
				return;
			}
			else {
				addNext(()->{p.startConversation(new WizardTraibornDemonSlayerD(p, DOES_NOT_HAVE_BONES).getStart());});
				return;
			}
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("What's a thingummywut?", new Dialogue()
						.addPlayer(HeadE.SKEPTICAL_THINKING, "What's a thingummywut?")
						.addNPC(WIZARD_TRAIBORN, HeadE.WORRIED, "A thingummywut? Where? Where?")
						.addNPC(WIZARD_TRAIBORN, HeadE.WORRIED, "Those pesky thingummywuts. They get everywhere. They leave a terrible mess too.")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("Err you just called me a thingummywut.", new Dialogue()
										.addPlayer(HeadE.FRUSTRATED, "Err you just called me thingummywut.")
										.addNPC(WIZARD_TRAIBORN, HeadE.AMAZED_MILD, "You're a thingummywut? I've never seen one up close before. They said I was mad!")
										.addNPC(WIZARD_TRAIBORN, HeadE.AMAZED_MILD, "Now you are my proof! There ARE thingummywuts in this tower. Now where can I find a " +
												"cage big enough to keep you?")
										.addOptions("Choose an option:", new Options() {
											@Override
											public void create() {
												option("Err I'd better be off really.", new Dialogue()
														.addPlayer(HeadE.WORRIED, "Err I'd better be off really.")
														.addNPC(WIZARD_TRAIBORN, HeadE.CALM_TALK, "Oh ok, have a good time, and watch out for sheep! They're more cunning than they look."));
												option("They're right, you are mad.", new Dialogue()
														.addPlayer(HeadE.FRUSTRATED, "They're right, you are mad.")
														.addNPC(WIZARD_TRAIBORN, HeadE.SAD_MILD, "That's a pity. I thought maybe they were winding me up."));
											}
										}));
								option("Tell me what they look like and I'll mash 'em.", new Dialogue()
										.addPlayer(HeadE.ANGRY, "Tell me what they look like and I'll mash 'em.")
										.addNPC(WIZARD_TRAIBORN, HeadE.LAUGH, "Don't be ridiculous. No-one has ever seen one.")
										.addNPC(WIZARD_TRAIBORN, HeadE.LAUGH, "They're invisible, or a myth, or a figment of my imagination. Can't remember which right now."));
							}
						}));
				option("Teach me to be a mighty and powerful wizard.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Teach me to be a mighty and powerful wizard.")
						.addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Wizard eh? You don't want any truck with that sort. They're not to be trusted. That's what" +
								" I've heard anyways.")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("So aren't you a wizard?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "So aren't you a wizard?")
										.addNPC(WIZARD_TRAIBORN, HeadE.ANGRY, "How dare you? Of course I'm a wizard. Now don't be so cheeky or I'll turn you into a frog."));
								option("Oh I'd better stop talking to you then.", new Dialogue()
										.addPlayer(HeadE.AMAZED_MILD, "Oh I'd better stop talking to you then.")
										.addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Cheerio then. It was nice chatting to you."));
							}
						}));
				if(!p.isQuestComplete(Quest.DEMON_SLAYER)
						&& p.getQuestManager().getStage(Quest.DEMON_SLAYER) >= DemonSlayer.AFTER_SIR_PRYSIN_INTRO_STAGE
						&& !p.getInventory().containsItem(DemonSlayer.WIZARD_KEY))
					option("I need to get a key given to you by Sir Prysin.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I need to get a key given to you by Sir Prysin.")
							.addNPC(WIZARD_TRAIBORN, HeadE.SKEPTICAL_THINKING, "Sir Prysin? Who's that? What would I want his key for?")
							.addNext(() -> {
								p.startConversation(new WizardTraibornDemonSlayerD(p, ABOUT_SIR_PRYSIN).getStart());
							}));
			}
		});
	}

	public WizardTraibornDemonSlayerD(Player p, int convoID) {
		super(p);
		this.p = p;

		switch(convoID) {
		case ABOUT_SIR_PRYSIN:
			aboutSirPrysin(p);
			break;
		case KEYS_DIALOGUE:
			keyDialogue(p);
			break;
		case KEYS_OPTIONS:
			keysOption(p);
			break;
		case HAS_BONES:
			hasBones(p);
			break;
		case DOES_NOT_HAVE_BONES:
			doesNotHaveBones(p);
			break;
		case RETRIEVE_KEY_AGAIN:
			retrieveKeyAgain(p);
			break;
		}

	}

	private void aboutSirPrysin(Player p) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("He told me you were looking after it for him.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "He told me you were looking after it for him.")
						.addNPC(WIZARD_TRAIBORN, HeadE.CALM_TALK, "Oh that's great, if it wouldn't be too much trouble.")
						.addNext(()->{p.startConversation(new WizardTraibornDemonSlayerD(p, KEYS_OPTIONS).getStart());}));
				option("He's one of the King's knights.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "He's one of the King's knights.")
						.addNPC(WIZARD_TRAIBORN, HeadE.CALM_TALK, "Say, I remember one of the King's knights. He had nice shoes...")
						.addNPC(WIZARD_TRAIBORN, HeadE.CALM_TALK, "...and didn't like my homemade spinach rolls. Would you like a spinach roll?")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("Yes please.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Yes please.")
										.addSimple("Traiborn digs around in the pockets of his robes. After a few moments he triumphantly presents you with " +
												"a spinach roll.", ()-> {p.getInventory().addItem(new Item(1969, 1), true);})
										.addPlayer(HeadE.HAPPY_TALKING, "Thank you very much.")
										.addNext(()->{p.startConversation(new WizardTraibornDemonSlayerD(p, ABOUT_SIR_PRYSIN).getStart());}));
								option("No thanks.", new Dialogue());
							}
						}));
				option("Well, have you got any keys knocking around?", new Dialogue()
						.addNext(()->{p.startConversation(new WizardTraibornDemonSlayerD(p, KEYS_DIALOGUE).getStart());}));
			}
		});
	}

	private void keyDialogue(Player p) {
		addPlayer(HeadE.HAPPY_TALKING, "Well, have you got any keys knocking around?");
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Now you come to mention it, yes I do have a key. It's in my special closet of" +
				" valuable stuff. Now how do I get into that?");
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "I sealed it using one of my magic rituals. So it would make sense that " +
				"another ritual would open it again.");
		addPlayer(HeadE.SKEPTICAL_THINKING, "So do you know what ritual to use?");
		addNPC(WIZARD_TRAIBORN, HeadE.SKEPTICAL_THINKING, "Let me think a second.");
		addNPC(WIZARD_TRAIBORN, HeadE.SKEPTICAL_THINKING, "Yes a simple drazier style ritual should suffice. Hmm, main problem with that is I'll need 25 sets " +
				"of bones. Now where am I going to get hold of something like that?");
		addOptions("", new Options() {
			@Override
			public void create() {
				option("Hmm, that's too bad. I really need that key.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Hmm, that's too bad. I really need that key.")
						.addNPC(WIZARD_TRAIBORN, HeadE.CALM_TALK, "Ah well, sorry I couldn't be any more help."));
				option("I'll get the bones for you.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I'll help get the bones for you.")
						.addNPC(WIZARD_TRAIBORN, HeadE.CALM_TALK, "Ooh that would be very good of you.")
						.addPlayer(HeadE.HAPPY_TALKING, "Okay, I'll speak to you when I've got some bones.", ()-> {
							p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).setB(DemonSlayer.WIZARD_RITUAL_KNOWN_ATTR, true);
						}));
			}
		});
	}

	private void keysOption(Player p) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Err I'd better be off really.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Err I'd better be off really.")
						.addNext(()->{p.startConversation(new WizardTraibornDemonSlayerD(p, ABOUT_SIR_PRYSIN).getStart());}));
				option("Well, have you got any keys knocking around?", new Dialogue()
						.addNext(()->{p.startConversation(new WizardTraibornDemonSlayerD(p, KEYS_DIALOGUE).getStart());}));
			}
		});
	}

	private void hasBones(Player p) {
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "How are you doing finding bones?");
		addPlayer(HeadE.HAPPY_TALKING, "I have them");
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Give 'em here then.");
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Hurrah! That's all 25 sets of bones.");
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Wings of dark and colour too, Spreading in the morning dew; Locked away I have a key; Return it now, " +
				"please, unto me.", () -> {
					p.getInventory().deleteItem(526, 25);
					p.getInventory().addItem(new Item(DemonSlayer.WIZARD_KEY), false);
					p.getQuestManager().getAttribs(Quest.DEMON_SLAYER).setB(DemonSlayer.WIZARD_KEY_PREVIOUSLY_RETRIEVED_ATTR, true);
				});
		addPlayer(HeadE.HAPPY_TALKING, "Thank you very much.");
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Not a problem for a friend of Sir What's-his-face.");
	}

	private void doesNotHaveBones(Player p) {
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "How are you doing finding bones?");
		addPlayer(HeadE.SAD, "I haven't gotten all of them at the moment.");
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Keep working on it");
	}

	private void retrieveKeyAgain(Player p) {
		addPlayer(HeadE.SAD, "I've lost the key you gave to me.");
		addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Yes I know, it was returned to me. If you want it back you're going to have to collect another 25 sets of " +
				"bones.");
		if(p.getInventory().containsItem(526, 25)) {
			addPlayer(HeadE.HAPPY_TALKING, "Oh perfect, I have the bones right here");
			addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Give 'em here then.");
			addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Hurrah! That's all 25 sets of bones.");
			addNPC(WIZARD_TRAIBORN, HeadE.HAPPY_TALKING, "Wings of dark and colour too, Spreading in the morning dew; Locked away I have a key; Return it now, " +
					"please, unto me.", () -> {
						for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
							if(npc.getId() == 881) {
								npc.setNextAnimation(new Animation(716));
								npc.setNextSpotAnim(new SpotAnim(102));
							}
						p.getInventory().deleteItem(526, 25);
						p.getInventory().addItem(new Item(DemonSlayer.WIZARD_KEY), false);
					});
			addPlayer(HeadE.HAPPY_TALKING, "Thank you very much.");
		}


	}




	public static NPCClickHandler handleWizardTraiborn = new NPCClickHandler(new Object[] { 881 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new WizardTraibornDemonSlayerD(e.getPlayer()).getStart());
		}
	};
}
