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
package com.rs.game.content.tutorialisland;

import com.rs.Settings;
import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.LegacyItemStatement;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.OptionStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;

public class CombatInstructor extends Conversation {

	public CombatInstructor(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);

		if (ctrl.inSection(Stage.TALK_TO_COMBAT_INSTRUCTOR, Stage.TALK_TO_COMBAT_INSTRUCTOR_2)) {
			addNext(new PlayerStatement(HeadE.CHEERFUL, "Hi! My name's " + player.getDisplayName() + "."));
			addNext(new NPCStatement(npc.getId(), HeadE.FRUSTRATED, "Do I look like I care? To me you're just another", "newcomer who thinks they're ready to fight."));
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "I am Vannaka, the greatest swordsman alive, and I'm", "here to teach you the basics of combat. Let's get started", "by teaching you to wield a weapon."));
			addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.OPEN_EQUIPMENT_TAB)));
		} else if (ctrl.inSection(Stage.TALK_TO_COMBAT_INSTRUCTOR_2, Stage.TALK_TO_COMBAT_INSTRUCTOR_3))
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Very good, but that little butter knife isn't going to", "protect you much. Here, take these."));
		else if (ctrl.inSection(Stage.TALK_TO_COMBAT_INSTRUCTOR_3, Stage.LEAVE_COMBAT_AREA)) {
			addNext(new PlayerStatement(HeadE.CHEERFUL, "I did it! I killed a giant rat!"));
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "I saw. You seem better at this than I thought. Now", "that you have grasped basic swordplay, let's move on."));
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION,"Let's try some ranged attacking, with this you can kill", "foes from a distance. Also, foes unable to reach you are", "as good as dead. You'll be able to attack the rats", "without entering the pit."));
		} else
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "What now?"));
		if (!player.containsOneItem(1277, 1171) && ctrl.pastStage(Stage.TALK_TO_COMBAT_INSTRUCTOR_2))
			addNext(new Dialogue(new LegacyItemStatement(1277, 1171, "", "The Combat Guide gives you a <col=0000FF>bronze sword</col> and a <col=0000FF>wooden shield</col>!"), () -> {
				player.getInventory().addItem(1171, 1);
				player.getInventory().addItem(1277, 1);
				ctrl.nextStage(Stage.EQUIP_SWORD_AND_SHIELD);
			}));
		if (player.getInventory().missingItems(841, 882) && ctrl.pastStage(Stage.TALK_TO_COMBAT_INSTRUCTOR_3))
			addNext(new Dialogue(new LegacyItemStatement(841, 897, "", "The Combat Guide gives you a <col=0000FF>shortbow</col> and some <col=0000FF>arrows</col>!"), () -> {
				player.getInventory().addItem(841, 1);
				player.getInventory().addItem(882, 50);
				ctrl.nextStage(Stage.KILL_RAT_RANGE);
			}));
		if (ctrl.getStage().ordinal() >= Stage.LEAVE_COMBAT_AREA.ordinal()) {
			addNext("Recap", new OptionStatement("What would you like to hear more about?", "Tell me about combat stats.", "Tell me about melee combat again.", "Tell me about ranged combat again.", "Tell me about the Wilderness.", "Nope, I'm ready to move on!"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about combat stats again."))
			.addNext(new Dialogue(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Certainly. You have three main combat stats: Strength", "Defence and Attack."), () -> player.getInterfaceManager().openTab(Sub.TAB_SKILLS)))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Strength determines the maximum hit you will be able", "to deal with your blows, Defence determines the amount", "of damage you will be able to defend and Attack", "determines the accuracy of your blows."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Other stats are used in combat such as Prayer,", "Hitpoints, Magic and Ranged. All of these stats can go", "towards determining your combat level, which is shown", "near the top of your combat interface screen."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "You will find out on the mainland that certain items can", "also affect your stats. There are potions that can be", "drunk that can alter your stats temporarily, such as", "raising Strength."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "You will also raise your Defence and Attack values by", "using different weapons and armours."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Before going into combat with an opponent it is wise to", "put the mouse over them and see what combat level", "they are."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Green coloured writing usually means it will be an easy", "fight for you, red means you will probably lose, yellow", "means they are around your level and orange means", "they are slightly stronger."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Sometimes things will go your way, sometimes they", "won't. There is no such thing as a guaranteed win, but", "if the odds are on your side, you stand the best chance", "of walking way victorious."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Now, was there something else you wanted to hear", "about again?"))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about melee combat again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Ah, my speciality. Close combat fighting, which is also", "known as melee fighting, covers all combat done at close", "range to your opponent."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Melee fighting depends entirely upon your three basic", "combat stats: Attack, Defence, and Strength."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Also, of course, it depends on the quality of your", "armour and weaponry. A high-levelled fighter with good", "armour and weapons will be deadly up close."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "If this is the path you wish to take, remember your", "success depends on getting as close to your enemy as", "quickly as possible."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Personally, I consider the fine art of melee combat to", "be the ONLY combat method worth using."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Now, did you want to hear anything else?"))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about ranged combat again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Thinking of being a ranger, eh? Well, okay. I don't", "enjoy it myself, but I can see the appeal."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Ranging employs a lot of different weapons as a skill,", "not just the shortbow you have there. Spears, throwing", "knives, and crossbows are all used best at a distance", "from your enemy."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Now, those rats were easy pickings, but on the", "mainland you will be very lucky if you can find a spot", "where you can shoot at your enemies without them", "being able to retaliate."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "At close range, rangers often do badly in combat. Your", "best tactic as a ranger is to hit and run, keeping your", "foe at a distance."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Your effectiveness as a ranger is almost entirely", "dependent on your Ranged stat. As with all skills, the", "more you train it, the more powerful it will become."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Anything else you need to know?"))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about the Wilderness again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Ah yes, the Wilderness. It is a place of evil, mark my", "words. Many is the colleague I have lost in that foul", "place."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "It is also a place of both adventure and wealth, so if", "you are brave enough and strong enough to survive it,", "you will make a killing, Literally!"))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "It is also the only place in the land of " + Settings.getConfig().getServerName(), "where players are able to attack each other at will, and", "as such is the haunt of many Player Killers, or PKers", "if you will."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "There are a few things different in the Wilderness in", "comparison to the rest of the lands of " + Settings.getConfig().getServerName() + ".", "Firstly, as I just mentioned, you can and will be", "attacked by other players."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "For this reason, you will be given a warning when you", "approach the Wilderness, as it is not a place you would", "wish to enter by accident."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Secondly, there are a number of 'levels' to it. The", "further into it you travel, the greater the range of", "people you can attack."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "In level 1 wilderness you will only be able to attack, or", "be attacked by, those players within one combat level of", "yourself."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "In level 50, any player within fifty levels of you will be", "able to attack, or be attacked by you. Always keep an", "eye on what level of the Wilderness you are currently", "in."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Your current Wildnerness level is shown at the bottom", "right of the screen. The final thing you should know", "about the Wilderness is being 'skulled'."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "If you attack another player without them having", "attacked first, you will gain a skull above your", "character's head."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "What this means is that if you die while skulled, you will", "lose EVERYTHING that your character was carrying."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "When skulled, you should try to avoid dying for the", "twenty minutes or so it will take for the skull to wear", "off."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "If you wish to find the Wilderness, head north from", "where you start on the mainland. It is rather large and", "hard to miss."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "If you don't wish to end up there, take notice of the", "warning you will receive when getting near to it."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Now, was there anything more?"))
			.addNext(getStage("Recap"));
		}

		create();
	}
}
