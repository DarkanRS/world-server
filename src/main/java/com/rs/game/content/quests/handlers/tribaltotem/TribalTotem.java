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
package com.rs.game.content.quests.handlers.tribaltotem;

import static com.rs.game.content.world.doors.Doors.handleDoor;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@QuestHandler(Quest.TRIBAL_TOTEM)
@PluginEventHandler
public class TribalTotem extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int TALK_TO_WIZARD = 1;
	public final static int REDIRECT_TELE_STONE = 2;
	public final static int GET_TOTEM = 3;
	public final static int QUEST_COMPLETE = 4;


	protected final static String LOCK_PASS_ATTR = "LOCK_PASS";
	protected final static String DISARMED_STAIRS_ATTR = "DISARMED_STAIRS";
	protected final static String CHANGED_CRATE_ATTR = "CHANGED_CRATE";


	public static final int TOTEM = 1857;

	@Override
	public int getCompletedStage() {
		return 4;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("Lord Handelmort of Ardougne is a collector of exotic ");
			lines.add("artefacts. A recent addition to his private collection is");
			lines.add("a strange looking totem from Karamja. The Rantuki Tribe ");
			lines.add("are not happy about the recent disappearance of their totem.");
			lines.add("");
			lines.add("I can start this quest by speaking to Kangai Mau at The");
			lines.add("Shrimp And Parrot Inn.");
			lines.add("");
			lines.add("~~Requirements~~");
			lines.add("21 Thieving");
			lines.add("");
			break;
		case TALK_TO_WIZARD:
			lines.add("Lord Handlemort has a mansion in Ardougne, we can poke around");
			lines.add("there. Though I hear Wizard Comperty has a way to teleport");
			lines.add("anywhere based on a stone. I ought to talk to him. He has his");
			lines.add("own building in eastern Ardougne by the marketplace.");
			lines.add("");
			break;
		case REDIRECT_TELE_STONE:
			lines.add("The rumor was true, Wizard Comperty has a way to teleport");
			lines.add("anywhere. But I somehow need to get the stone inside his");
			lines.add("mansion. I should check the delivery company RPDT and poke");
			lines.add("around. Maybe I can switch crates or something...");
			lines.add("");
			break;
		case GET_TOTEM:
			lines.add("Okay, the stone is in the mansion. Now I just need to tele");
			lines.add("in, get past the theft protection system and take the totem.");
			lines.add("I have deduced from my rumors that Handlemort's middle name is");
			lines.add("the password for the lock.");
			lines.add("");
            lines.add("Maybe I can get his middle name from the real estate agent or");
            lines.add("his paper guides about ardougne, south of the mansion.");
            lines.add("");
			break;
		case QUEST_COMPLETE:
			lines.add("");
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
		player.getSkills().addXpQuest(Constants.THIEVING, 1775);
		player.getInventory().addItem(3144, 5, true);//Karambwan
		getQuest().sendQuestCompleteInterface(player, TOTEM, "1,775 Thieving XP", "5 Karambwan");
	}

	public static ObjectClickHandler handleFrontDoorMansion = new ObjectClickHandler(new Object[] { 2706 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			p.sendMessage("It is securely locked...");
		}
	};

	public static ObjectClickHandler handleLockDoorInMansion = new ObjectClickHandler(new Object[] { 2705 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if (p.getX() < obj.getX() || p.isQuestComplete(Quest.TRIBAL_TOTEM) || (p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).getO(LOCK_PASS_ATTR) != null
					&& ((String) p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).getO(LOCK_PASS_ATTR)).equalsIgnoreCase("KURT"))) {
				handleDoor(p, obj);
				return;
			}
			p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).setO(LOCK_PASS_ATTR, "AAAA");
			p.getInterfaceManager().sendInterface(285);//Door lock tribal totem
		}
	};

	public static ItemClickHandler handleClickOnGuideBook = new ItemClickHandler(new Object[] { 1856 }, new String[] { "Read" }) { //Guide book for middle name
		@Override
		public void handle(ItemClickEvent e) {
			e.getPlayer().openBook(new RealEstateGuideBook());
		}
	};

	public static ObjectClickHandler handleTrapStairs = new ObjectClickHandler(new Object[] { 2711 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.getOption().equalsIgnoreCase("climb-up"))
				if(p.isQuestComplete(Quest.TRIBAL_TOTEM)
						|| p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).getB(DISARMED_STAIRS_ATTR))
					p.useStairs(-1, WorldTile.of(p.getX()-3, obj.getY(), p.getPlane() + 1), 0, 1);
				else {
					p.applyHit(new Hit(25, Hit.HitLook.TRUE_DAMAGE));
					p.sendMessage("You activate the trap stairs!");
					p.setNextWorldTile(WorldTile.of(2638, 9721, 0));
				}
			if(e.getOption().equalsIgnoreCase("investigate"))
				if(p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).getB(DISARMED_STAIRS_ATTR))
					p.sendMessage("It is already disarmed");
				else if(p.getSkills().getLevel(Constants.THIEVING) < 21)
					p.startConversation(new Conversation(e.getPlayer()) {
						{
							addPlayer(HeadE.SAD, "I can't seem to disarm the trap stairs...");
							addSimple("You need 21 thieving...");
							create();
						}
					});
				else {
					p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).setB(DISARMED_STAIRS_ATTR, true);
					p.sendMessage("You disarm the trap...");
				}
		}
	};

	public static ButtonClickHandler handleLockInterface = new ButtonClickHandler(285) {
		final int FIRST_LETTER_COMP = 6;
		final int SECOND_LETTER_COMP = 7;
		final int THIRD_LETTER_COMP = 8;
		final int FOURTH_LETTER_COMP = 9;
		final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int indexFirst = 0;
		int indexSecond = 0;
		int indexThird = 0;
		int indexFourth = 0;
		@Override
		public void handle(ButtonClickEvent e) {
			Player p = e.getPlayer();
			switch(e.getComponentId()) {
			case 18 -> {//Enter pass
				if(((String)p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).getO(LOCK_PASS_ATTR)).equalsIgnoreCase("KURT")) {
					p.sendMessage("Correct password.");
					p.closeInterfaces();
				} else
					p.sendMessage("Incorrect password.");
			}

			//LEFT
			case 10 -> {
				if(indexFirst - 1 >= 0)
					indexFirst--;
				p.getPackets().setIFText(e.getInterfaceId(), FIRST_LETTER_COMP, ""+LETTERS.charAt(indexFirst));
			}
			case 12 -> {
				if(indexSecond - 1 >= 0)
					indexSecond--;
				p.getPackets().setIFText(e.getInterfaceId(), SECOND_LETTER_COMP, ""+LETTERS.charAt(indexSecond));
			}
			case 14 -> {
				if(indexThird - 1 >= 0)
					indexThird--;
				p.getPackets().setIFText(e.getInterfaceId(), THIRD_LETTER_COMP, ""+LETTERS.charAt(indexThird));
			}
			case 16 -> {
				if(indexFourth - 1 >= 0)
					indexFourth--;
				p.getPackets().setIFText(e.getInterfaceId(), FOURTH_LETTER_COMP, ""+LETTERS.charAt(indexFourth));
			}

			//RIGHT
			case 11 -> {
				if(indexFirst + 1 < LETTERS.length())
					indexFirst++;
				p.getPackets().setIFText(e.getInterfaceId(), FIRST_LETTER_COMP, ""+LETTERS.charAt(indexFirst));
			}
			case 13 -> {
				if(indexSecond + 1 < LETTERS.length())
					indexSecond++;
				p.getPackets().setIFText(e.getInterfaceId(), SECOND_LETTER_COMP, ""+LETTERS.charAt(indexSecond));
			}
			case 15 -> {
				if(indexThird + 1 < LETTERS.length())
					indexThird++;
				p.getPackets().setIFText(e.getInterfaceId(), THIRD_LETTER_COMP, ""+LETTERS.charAt(indexThird));
			}
			case 17 -> {
				if(indexFourth + 1 < LETTERS.length())
					indexFourth++;
				p.getPackets().setIFText(e.getInterfaceId(), FOURTH_LETTER_COMP, ""+LETTERS.charAt(indexFourth));
			}
			}
			String newPass = ""+LETTERS.charAt(indexFirst) + LETTERS.charAt(indexSecond) + LETTERS.charAt(indexThird) + LETTERS.charAt(indexFourth);
			p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).setO(LOCK_PASS_ATTR, newPass);
		}
	};

	public static ObjectClickHandler handleMansionTotemChest = new ObjectClickHandler(new Object[] { 2709 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.getOption().equalsIgnoreCase("open")) {
				p.setNextAnimation(new Animation(536));
				p.lock(2);
				GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
				p.faceObject(openedChest);
				World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
				if(!p.getInventory().containsItem(TOTEM, 1) && !p.isQuestComplete(Quest.TRIBAL_TOTEM)) {
					p.startConversation(new Dialogue().addPlayer(HeadE.SECRETIVE, "This looks like the Totem. Now back to Brimhaven, I gotta" +
							" get this to Kangai Mau..."));
					p.getInventory().addItem(new Item(TOTEM, 1));
				}
				else
					p.sendMessage("The chest is empty...");
			}
		}
	};

	public static ObjectClickHandler handleRPDTCrate = new ObjectClickHandler(new Object[] { 2707 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(p.getQuestManager().getStage(Quest.TRIBAL_TOTEM) == REDIRECT_TELE_STONE)
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addSimple("The address label says this crate is headed to Wizard's Tower in Draynor.");
						create();
					}
				});
		}
	};

	public static ObjectClickHandler handleRPDTCrateMansion = new ObjectClickHandler(new Object[] { 2708 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(obj.getTile().matches(WorldTile.of(2650, 3272, 0)))
				if(p.getQuestManager().getStage(Quest.TRIBAL_TOTEM) == REDIRECT_TELE_STONE)
					p.startConversation(new Conversation(e.getPlayer()) {
						{
							addSimple("The address label says this crate is headed to Handlemort's mansion.");
							if(p.getInventory().hasFreeSlots())
								addSimple("You take off the label.", ()->{
									p.getInventory().addItem(1858, 1);
								});
							else
								addSimple("You need to make room for the label...");
							create();
						}
					});
		}
	};

	public static ItemOnObjectHandler itemOnMansionCrate = new ItemOnObjectHandler(true, new Object[] { 2707 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			Player p = e.getPlayer();
			if(e.getItem().getId() == 1858) { //address label
				p.getInventory().removeItems(new Item(1858, 1));
				p.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).setB(CHANGED_CRATE_ATTR, true);
				p.sendMessage("You switch the address labels... The stone now goes to the mansion.");
			}

		}
	};
}
