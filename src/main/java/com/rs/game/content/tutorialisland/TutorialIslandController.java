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

import java.util.function.Consumer;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.statements.NPCStatement;
import com.rs.game.content.dialogue.statements.OptionStatement;
import com.rs.game.content.skills.cooking.Cooking;
import com.rs.game.content.skills.fishing.Fish;
import com.rs.game.content.skills.fishing.Fishing;
import com.rs.game.content.skills.fishing.FishingSpot;
import com.rs.game.content.skills.mining.Mining;
import com.rs.game.content.skills.mining.RockType;
import com.rs.game.content.skills.smithing.Smelting;
import com.rs.game.content.skills.woodcutting.TreeType;
import com.rs.game.content.skills.woodcutting.Woodcutting;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.Region;
import com.rs.lib.Constants;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;

public final class TutorialIslandController extends Controller {

	private static final int[] TUTORIAL_REGIONS = { 12336, 12592, 12337, 12436 };

	private static final int RUNESCAPE_GUIDE = 945;
	private static final int SURVIVAL_EXPERT = 943;
	private static final int MASTER_CHEF = 942;
	private static final int QUEST_GUIDE = 949;
	private static final int MINING_INSTRUCTOR = 948;
	private static final int COMBAT_INSTRUCTOR = 944;
	private static final int FINANCIAL_ADVISOR = 947;
	private static final int BROTHER_BRACE = 954;
	private static final int MAGIC_INSTRUCTOR = 946;
	private static final int BANKER = 953;
	private static final int FISHING_SPOT = 952;
	private static final int GIANT_RAT = 950;
	private static final int CHICKEN = 951;
	private static final int SKIPPY = 2795;

	private transient String[] prevText = {""};
	private Stage stage;

	public enum Stage {
		TALK_TO_GUIDE(new String[] {
				"Getting started",
				"To start the tutorial use your left mouse button to click on the",
				Settings.getConfig().getServerName() + " Guide in this room. He is indicated by a flashing",
				"yellow arrow above his head. If you can't see him, use your",
				"keyboard's arrow keys to rotate the view."
		}, ctrl -> {
			ctrl.player.getInterfaceManager().removeSubs(Sub.ALL_GAME_TABS);
		}, ctrl -> {
			ctrl.hintNPC(RUNESCAPE_GUIDE);
		}),

		OPEN_SETTINGS(new String[] {
				"Player controls",
				"Please click on the flashing spanner icon found at the bottom",
				"right of your screen. This will display your player controls."
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_SETTINGS);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_SETTINGS);
		}),

		TALK_TO_GUIDE_2(new String[] {
				"Player controls",
				"On the side panel you can now see a variety of options from",
				"changing the brightness of the screen and of the volume of",
				"music, to selecting whether your player should accept help",
				"from other players. Don't worry about these too much for now,",
				"they will become clearer as you explore the game. Talk to the",
				Settings.getConfig().getServerName()+" Guide to continue."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
			ctrl.hintNPC(RUNESCAPE_GUIDE);
		}),

		LEAVE_GUIDE_ROOM(new String[] {
				"Interacting with scenery",
				"You can interact with many items of scenery by simply clicking",
				"on them. Right clicking will also give more options. Feel free to",
				"try it with the things in this room, then click the door",
				"indicated with the yellow arrow to go through to the next",
				"instructor."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3097, 3107, 125);
		}),

		TALK_TO_SURVIVAL_EXPERT(new String[] {
				"Moving around",
				"Follow the path to find the next instructor. Clicking on the",
				"ground will walk you to that point. Talk to the Survival Expert by",
				"the pond to continue the tutorial. Remember that you can rotate",
				"the view by pressing the arrow keys."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.hintNPC(SURVIVAL_EXPERT);
		}),

		OPEN_INVENTORY(new String[] {
				"Viewing the items that you were given.",
				"Click on the flashing backpack icon to the right hand side of",
				"the main window to view your inventory. Your inventory is a list",
				"of everything you have in your backpack."
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_INVENTORY);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_INVENTORY);
		}),

		CHOP_TREE(new String[] {
				"Cut down a tree",
				"You can click on the backpack icon at any time to view the",
				"items that you currently have in your inventory. You will see",
				"that you now have an axe in your inventory. Use this to get",
				"some logs by clicking on one of the trees in the area."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
			ctrl.removeHint();
			ctrl.hintLocation(3099, 3095, 150);
		}),

		MAKE_A_FIRE(new String[] {
				"Making a fire",
				"Well done! You managed to cut some logs from the tree! Next,",
				"use the tinderbox in your inventory to light the logs.",
				"First click on the tinderbox to 'use' it.",
				"Then click on the logs in your inventory to light them."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
		}),

		OPEN_SKILLS(new String[] {
				"",
				"You gained some experience.",
				"Click on the flashing bar graph icon near the inventory button",
				"to see your skill stats."
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_SKILLS);
		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_SKILLS);
		}),

		TALK_TO_SURVIVAL_EXPERT_2(new String[] {
				"Your skill stats.",
				"Here you will see how good your skills are. As you move your",
				"mouse over any of the icons in this panel, the small yellow",
				"popup box will show you the exact amount of experience you",
				"have and how much is needed to get to the next level. Speak to",
				"the Survival Expert to continue."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
			ctrl.hintNPC(SURVIVAL_EXPERT);
		}),

		CATCH_SHRIMP(new String[] {
				"Catch some Shrimp",
				"Click on the sparkling fishing spot, indicated by the flashing",
				"arrow. Remember, you can check your inventory by clicking the",
				"backpack icon."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(FISHING_SPOT);
		}),

		BURN_SHRIMP(new String[] {
				"Cooking your shrimp.",
				"Now you have caught some shrimp, let's cook it. First light a",
				"fire: chop down a tree and then use the tinderbox on the logs.",
				"If you've lost your axe or tinderbox Brynna will give you",
				"another."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
		}),

		COOK_SHRIMP(new String[] {
				"Burning your shrimp.",
				"You have just burnt your first shrimp. This is normal. As you",
				"get more experience in Cooking, you will burn stuff less often.",
				"Let's try cooking without burning it this time. First catch some",
				"more shrimp, then use them on a fire."
		}, ctrl -> {

		}, ctrl -> {

		}),

		LEAVE_SURVIVAL_EXPERT(new String[] {
				"Well done, you've just cooked your first "+Settings.getConfig().getServerName()+" meal.",
				"If you'd like a recap on anything you've learnt so far, speak to",
				"the Survival Expert. You can now move on to the next instructor.",
				"Click on the gate shown and follow the path.",
				"Remember, you can move the camera with the arrow keys."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.hintLocation(3089, 3092, 120);
		}),

		ENTER_CHEF_HOUSE(new String[] {
				"Find your next instructor.",
				"Follow the path until you get to the door with the yellow arrow",
				"above it. Click on the door to open it. Notice the mini-map in",
				"the top right; this shows a top down view of the area around",
				"you. This can also be used for navigation."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3078, 3084, 150);
		}),

		TALK_TO_CHEF(new String[] {
				"Find your next instructor.",
				"Talk to the chef indicated. He will teach you the more advanced",
				"aspects of Cooking such as combining ingredients. He will also",
				"teach you about your music player menu as well."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(MASTER_CHEF);
		}),

		MAKE_DOUGH(new String[] {
				"Making dough.",
				"This is the base for many of the meals. To make dough we must",
				"mix flour and water. First, right click the bucket of water and",
				"select use, then left click on the pot of flour."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
		}),

		COOK_DOUGH(new String[] {
				"Cooking dough.",
				"Now you have made dough, you can cook it. To cook the dough,",
				"use it with the range shown by the arrow. If you lose your",
				"dough, talk to Lev - he will give you more ingredients."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.hintLocation(3075, 3081, 125);
		}),

		OPEN_MUSIC(new String[] {
				"Cooking dough",
				"Well done! Your first loaf of bread. As you gain experience in",
				"Cooking, you will be able to make other things like pies, cakes",
				"and even kebabs. Now you've got the hang of cooking, let's",
				"move on. Click on the flashing icon in the bottom right to see",
				"the jukebox."
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_MUSIC);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_MUSIC);
		}),

		LEAVE_CHEF_HOUSE(new String[] {
				"The music player.",
				"From this interface you can control the music that is played.",
				"As you explore the world, more of the tunes will become",
				"unlocked. Once you've examined this menu use the next door",
				"to continue. If you need a recap on anything covered here,",
				"talk to Lev."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.hintLocation(3072, 3090, 125);
			ctrl.player.getInterfaceManager().flashTabOff();
		}),

		OPEN_EMOTES(new String[] {
				"Emotes.",
				"",
				"Now, how about showing some feelings? You will see a flashing",
				"icon in the shape of a person. Click on that to access your",
				"emotes."
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_EMOTES);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_EMOTES);
		}),

		USE_EMOTE(new String[] {
				"Emotes.",
				"For those situations where words don't quest describe how you",
				"feel, try an emote. Go ahead, try one out! You might notice",
				"that some of the emotes are grey and cannot be used now.",
				"Don't worry! As you progress further into the game you'll gain",
				"access to all sorts of things, including more fun emotes like",
				"these"
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
		}),

		RUN(new String[] {
				"Running.",
				"It's only a short distance to the next guide.",
				"Why not try running there? You can run by clicking",
				"on the boot icon next to your minimap or by holding",
				"down your control key while clicking your destination."
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.ORB_RUN);
		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTab(Sub.ORB_RUN);
			ctrl.getPlayer().setRun(false);
		}),

		ENTER_QUEST_GUIDE_HOUSE(new String[] {
				"Run to the next guide.",
				"Now that you have the run button turned on, follow the path",
				"until you come to the end. You may notice that the number on",
				"the button goes down. This is your run energy. If your run",
				"energy reaches zero, you'll stop running. Click on the door",
				"to pass through it."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
			ctrl.hintLocation(3086, 3126, 125);
		}),

		TALK_TO_QUEST_GUIDE(new String[] {
				"",
				"Talk with the Quest Guide.",
				"",
				"He will tell you all about quests.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(QUEST_GUIDE);
		}),

		OPEN_QUEST_TAB(new String[] {
				"",
				"Open the Quest Journal.",
				"",
				"Click on the flashing icon next to your inventory.",
				""
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_QUEST);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_QUEST);
		}),

		TALK_TO_QUEST_GUIDE_2(new String[] {
				"Your Quest Journal",
				"",
				"This is your Quest Journal, a list of all the quests in the game.",
				"Talk to the Quest Guide again for an explanation.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
			ctrl.hintNPC(QUEST_GUIDE);
		}),

		LEAVE_QUEST_GUIDE_HOUSE(new String[] {
				"",
				"Moving on.",
				"It's time to enter some caves. Click on the ladder to go down to",
				"the next area.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3088, 3119, 50);
		}),

		TALK_TO_MINING_GUIDE(new String[] {
				"Mining and Smithing.",
				"Next let's get you a weapon, or more to the point, you can",
				"make your first weapon yourself. Don't panic, the Mining",
				"Instructor will help you. Talk to him and he'll tell you all about it.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(MINING_INSTRUCTOR);
		}),

		PROSPECTING_TIN(new String[] {
				"Prospecting.",
				"To prospect a mineable rock, just right click it and select the",
				"'prospect rock' option. This will tell you the type of ore you can",
				"mine from it. Try it now on one of the rocks indicated.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3076, 9504, 45);
		}),

		PROSPECTING_COPPER(new String[] {
				"It's tin.",
				"",
				"So now you know there's tin in the grey rocks, try prospecting",
				"the brown ones next.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3086, 9501, 45);
		}),

		TALK_TO_MINING_GUIDE_2(new String[] {
				"It's copper.",
				"Talk to the Mining Instructor to find out about these types of",
				"ore and how you can mind them. He'll even give you the",
				"required tools.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(MINING_INSTRUCTOR);
		}),

		MINING_TIN(new String[] {
				"Mining.",
				"It's quite simple really. All you need to do is right click on the",
				"rock and select 'mine'. You can only mine when you have a",
				"pickaxe. So give it a try: first mine one tin ore.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3076, 9504, 45);
		}),

		MINING_COPPER(new String[] {
				"Mining.",
				"Now you have some tin ore you just need some copper ore,",
				"then you'll have all you need to create a bronze bar. As you",
				"did before right click on the copper rock and select 'mine'.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3086, 9501, 45);
		}),

		SMELTING(new String[] {
				"Smelting.",
				"You should now have both some copper and tin ore. So let's",
				"smelt them to make a bronze bar. To do this, right click on",
				"either tin or copper ore and select use then left click on the",
				"furnace. Try it now."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3079, 9496, 125);
		}),

		TALK_TO_MINING_GUIDE_3(new String[] {
				"You've made a bronze bar!",
				"",
				"Speak to the Mining Instructor and he'll show you how to make",
				"it into a weapon.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(MINING_INSTRUCTOR);
		}),

		CLICK_ANVIL(new String[] {
				"Smithing a dagger.",
				"To smith you'll need a hammer and enough metal bars to make",
				"the desired item, as well as a handy anvil. To start the",
				"process, click on the anvil, or alternatively use the bar on it.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3083, 9499, 35);
		}),

		SMITH_DAGGER(new String[] {
				"Smithing a dagger.",
				"Now you have the Smithing menu open, you will see a list of all",
				"the things you can make. Only the dagger can be made at your",
				"skill level; this is shown by the white text under it. You'll need",
				"to select the dagger to continue."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
		}),

		LEAVE_MINING_AREA(new String[] {
				"You've finished in this area.",
				"So let's move on. Go through the gates shown by the arrow.",
				"Remember, you may need to move the camera to see your",
				"surroundings. Speak to the guide for a recap at any time.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.hintLocation(3094, 9502, 125);
		}),

		TALK_TO_COMBAT_INSTRUCTOR(new String[] {
				"Combat.",
				"",
				"In this area you will find out about combat with swords and",
				"bows. Speak to the guide and he will tell you all about it.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(COMBAT_INSTRUCTOR);
		}),

		OPEN_EQUIPMENT_TAB(new String[] {
				"Wielding weapons.",
				"",
				"You now have access to a new interface. Click on the flashing",
				"icon of a man, the one to the right of your backpack icon.",
				""
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_EQUIPMENT);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_EQUIPMENT);
		}),

		OPEN_EQUIPMENT_SCREEN(new String[] {
				"This is your worn inventory.",
				"From here you can see what items you have equipped. You will",
				"notice the button 'View equipment stats'. Click on this now to",
				"display the details of what you have equipped.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
		}),

		WIELD_DAGGER(new String[] {
				"Worn interface",
				"You can see what items you are wearing in the worn inventory",
				"to the left of the screen, with their combined statistics on the",
				"right. Let's add something. Left click your dagger to 'wield' it.",
				""
		}, ctrl -> {

		}, ctrl -> {

		}),

		TALK_TO_COMBAT_INSTRUCTOR_2(new String[] {
				"You're now holding your dagger.",
				"Clothes, armour, weapons and many other items are equipped",
				"like this. You can unequip items by clicking on the item in the",
				"worn inventory. You can close this window by clicking on the",
				"small 'x' in the top right hand corner. Speak to the Combat",
				"Instructor to continue."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.hintNPC(COMBAT_INSTRUCTOR);
		}),

		EQUIP_SWORD_AND_SHIELD(new String[] {
				"Unequipping items.",
				"In your worn inventory panel, right click on the dagger and",
				"select the remove option from the drop down list. After you've",
				"unequipped the dagger, wield the sword and shield. As you",
				"pass the mouse over an item, you will see its name appear at",
				"the top left of the screen."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
		}),

		OPEN_COMBAT_TAB(new String[] {
				"Combat interface.",
				"",
				"Click on the flashing crossed swords icon to see the combat",
				"interface.",
				""
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_COMBAT);
		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_COMBAT);
		}),

		ENTER_RAT_CAGE(new String[] {
				"This is your combat interface.",
				"From this interface you can select the type of attack your",
				"character will use. Different monsters have different",
				"weaknesses. If you hover your mouse over the buttons, you",
				"will see the type of XP you will receive when using each type of",
				"attack. Now you have the tools needed for battle why not slay",
				"some rats. Click on the gates indicated to continue."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
			ctrl.hintLocation(3111, 9518, 125);
		}),

		ATTACK_RAT_MELEE(new String[] {
				"Attacking.",
				"",
				"To attack the rat, right click it and select the attack option. You",
				"will then walk over to it and start hitting it.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(GIANT_RAT);
		}),

		KILL_RAT_MELEE(new String[] {
				"Sit back and watch.",
				"While you are fighting you will see a bar over your head. The",
				"bar shows how much health you have left. Your opponent will",
				"have one too. You will continue to attack the rat until it's dead",
				"or you do something else."
		}, ctrl -> {

		}, ctrl -> {

		}),

		TALK_TO_COMBAT_INSTRUCTOR_3(new String[] {
				"Well done, you've made your first kill!",
				"",
				"Pass through the gate and talk to the Combat Instructor; he",
				"will give you your next task.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(COMBAT_INSTRUCTOR);
		}),

		KILL_RAT_RANGE(new String[] {
				"Rat ranging.",
				"Now you have a bow and some arrows. Before you can use",
				"them you'll need to equip them. Once equipped with the",
				"ranging gear, try killing another rat. Remember: to attack, right",
				"click on the monster and select attack."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(GIANT_RAT);
		}),

		LEAVE_COMBAT_AREA(new String[] {
				"Moving on.",
				"You have completed the tasks here. To move on, click on the",
				"ladder shown. If you need to go over any of what you learnt",
				"here, just talk to the Combat Instructor and he'll tell you what",
				"he can."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3111, 9526, 125);
		}),

		OPEN_BANK(new String[] {
				"Banking.",
				"Follow the path and you will come to the front of the building.",
				"This is the Bank of " + Settings.getConfig().getServerName() + ", where you can store all you",
				"most valued items. To open your bank box just right click on an",
				"open booth indicated and select 'use'."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3122, 3124, 125);
		}),

		ENTER_FINANCIAL_ROOM(new String[] {
				"This is your bank box.",
				"You can store stuff here for safekeeping. If you die, anything",
				"in your bank will be saved. To deposit something, right click it",
				"and select 'store'. Once you've had a good look, close the",
				"window and move through the door indicated."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3124, 3124, 125);
		}),

		TALK_TO_FINANCIAL_ADVISOR(new String[] {
				"Financial advice.",
				"",
				"The guide here will tell you all about making cash. Just click on",
				"him to hear what he's got to say.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(FINANCIAL_ADVISOR);
		}),

		LEAVE_FINANCIAL_ADVISOR_ROOM(new String[] {
				"",
				"",
				"Continue through the next door.",
				"",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3129, 3124, 125);
		}),

		TALK_TO_BROTHER_BRACE(new String[] {
				"Prayer.",
				"Follow the path to the chapel and enter it.",
				"Once inside talk to the monk. He'll tell you all about the Prayer",
				"skill.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(BROTHER_BRACE);
		}),

		OPEN_PRAYER_TAB(new String[] {
				"Your Prayer menu.",
				"",
				"Click on the flashing icon to open the Prayer menu.",
				"",
				""
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_PRAYER);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_PRAYER);
		}),

		TALK_TO_BROTHER_BRACE_2(new String[] {
				"",
				"Your Prayer Menu.",
				"",
				"Talk with Brother Brace and he'll tell you about prayers.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
			ctrl.hintNPC(BROTHER_BRACE);
		}),

		OPEN_FRIENDS_TAB(new String[] {
				"",
				"Friends list.",
				"You should now see another new icon. Click on the flashing",
				"smiling face to open your friends list.",
				""
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_FRIENDS);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_FRIENDS);
		}),

		OPEN_IGNORE_LIST(new String[] {
				"This is your friends list.",
				"",
				"This will be explained by Brother Brace shortly, but first click",
				"on the red dot button on the bottom right of the friends list.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.player.getInterfaceManager().flashTabOff();
		}),

		TALK_TO_BROTHER_BRACE_3(new String[] {
				"This is your ignore list.",
				"The two lists - friends and ignore - can be very helpful for",
				"keeping track of when your friends are online or for blocking",
				"messages from people you simply don't like. Speak with",
				"Brother Brace and he will tell you more."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.hintNPC(BROTHER_BRACE);
		}),

		LEAVE_CHURCH_AREA(new String[] {
				"",
				"Your final instructor!",
				"You're almost finished on tutorial island. Pass through the",
				"door to find the path leading to your final instructor.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintLocation(3122, 3102, 125);
		}),

		TALK_TO_MAGIC_INSTRUCTOR(new String[] {
				"Your final instructor!",
				"Just follow the path to the Wizard's house, where you will be",
				"shown how to cast spells. Just talk with the mage indicated to",
				"find out more.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(MAGIC_INSTRUCTOR);
		}),

		OPEN_MAGIC_TAB(new String[] {
				"Open up your final menu.",
				"",
				"Open up the Magic menu by clicking on the flashing icon next",
				"to the Prayer button you just learned about.",
				""
		}, ctrl -> {
			ctrl.player.getInterfaceManager().sendSubDefault(Sub.TAB_MAGIC);
		}, ctrl -> {
			ctrl.removeHint();
			ctrl.player.getInterfaceManager().flashTab(Sub.TAB_MAGIC);
		}),

		TALK_TO_MAGIC_INSTRUCTOR_2(new String[] {
				"",
				"This is your spells list.",
				"",
				"Ask the mage about it.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.hintNPC(MAGIC_INSTRUCTOR);
			ctrl.player.getInterfaceManager().flashTabOff();
			ctrl.player.startConversation(new MagicInstructor(ctrl.player, ctrl.getNPC(MAGIC_INSTRUCTOR), ctrl));
		}),

		CAST_WIND_STRIKE(new String[] {
				"Cast Wind Strike at a chicken.",
				"Now you have runes you should see the Wind Strike icon at the",
				"top left corner of the Magic interface - third in from the",
				"left. Walk over to the caged chickens, click the Wind Strike icon",
				"and then select one of the chickens to cast it on. It may take",
				"several tries. If you need more runes ask Terrova."
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(CHICKEN);
		}),

		TALK_TO_MAGIC_INSTRUCTOR_3(new String[] {
				"You have almost completed the tutorial!",
				"",
				"All you need to do now is move on to the mainland. Just speak",
				"with Terrova and he'll teleport you to Lumbridge Castle.",
				""
		}, ctrl -> {

		}, ctrl -> {
			ctrl.removeHint();
			ctrl.hintNPC(MAGIC_INSTRUCTOR);
		});

		private String[] textBody;
		private Consumer<TutorialIslandController> setupInterfaces;
		private Consumer<TutorialIslandController> onStart;

		Stage(String[] textBody, Consumer<TutorialIslandController> setupInterfaces, Consumer<TutorialIslandController> onStart) {
			this.textBody = textBody;
			this.setupInterfaces = setupInterfaces;
			this.onStart = onStart;
		}

		public void start(TutorialIslandController ctr) {
			setupInterfaces.accept(ctr);
			onStart.accept(ctr);
			ctr.sendText(textBody);
			ctr.sendProgress();
		}

		public void loadAllUpTo(TutorialIslandController ctr) {
			for (Stage s : Stage.values()) {
				if (s == this) {
					s.start(ctr);
					return;
				}
				s.setupInterfaces.accept(ctr);
				s.onStart.accept(ctr);
			}
		}
	}

	public TutorialIslandController() {
		stage = Stage.TALK_TO_GUIDE;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (interfaceId == Inventory.INVENTORY_INTERFACE && packet == ClientPacket.IF_OP2) {
			Item item = player.getInventory().getItem(slotId);
			if (item != null && item.getId() == 1511)
				sendText(true, "Please wait.", "", "Your character is now attempting to light the fire.", "This should only take a few seconds.");
		}
		if (getStage() == Stage.OPEN_SETTINGS && componentId == 87)
			nextStage(Stage.TALK_TO_GUIDE_2);
		else if (getStage() == Stage.OPEN_INVENTORY && (componentId == 79 || componentId == 116))
			nextStage(Stage.CHOP_TREE);
		else if (getStage() == Stage.OPEN_SKILLS && (componentId == 77 || componentId == 114))
			nextStage(Stage.TALK_TO_SURVIVAL_EXPERT_2);
		else if (getStage() == Stage.OPEN_MUSIC && componentId == 89)
			nextStage(Stage.LEAVE_CHEF_HOUSE);
		else if (getStage() == Stage.OPEN_EMOTES && componentId == 88)
			nextStage(Stage.USE_EMOTE);
		else if (getStage() == Stage.USE_EMOTE && interfaceId == 590 && componentId == 8)
			nextStage(Stage.RUN);
		else if (getStage() == Stage.RUN && interfaceId == 750 && componentId == 4)
			nextStage(Stage.ENTER_QUEST_GUIDE_HOUSE);
		else if (getStage() == Stage.OPEN_QUEST_TAB && (componentId == 78 || componentId == 115))
			nextStage(Stage.TALK_TO_QUEST_GUIDE_2);
		else if (getStage() == Stage.OPEN_EQUIPMENT_TAB && (componentId == 80 || componentId == 117))
			nextStage(Stage.OPEN_EQUIPMENT_SCREEN);
		else if (getStage() == Stage.OPEN_EQUIPMENT_SCREEN && interfaceId == 387 && componentId == 38)
			nextStage(Stage.WIELD_DAGGER);
		else if (getStage() == Stage.OPEN_COMBAT_TAB && (componentId == 75 || componentId == 112))
			nextStage(Stage.ENTER_RAT_CAGE);
		else if (getStage() == Stage.OPEN_PRAYER_TAB && (componentId == 81 || componentId == 118))
			nextStage(Stage.TALK_TO_BROTHER_BRACE_2);
		else if (getStage() == Stage.OPEN_FRIENDS_TAB && componentId == 84)
			nextStage(Stage.OPEN_IGNORE_LIST);
		else if (getStage() == Stage.OPEN_IGNORE_LIST && interfaceId == 550 && componentId == 48)
			nextStage(Stage.TALK_TO_BROTHER_BRACE_3);
		else if (getStage() == Stage.OPEN_MAGIC_TAB && (componentId == 82 || componentId == 119))
			nextStage(Stage.TALK_TO_MAGIC_INSTRUCTOR_2);
		return true;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == SKIPPY) {
			player.startConversation(new Skippy(player, npc, this));
			return false;
		}
		if (npc.getId() == RUNESCAPE_GUIDE && pastStage(Stage.TALK_TO_GUIDE)) {
			player.startConversation(new RuneScapeGuide(player, npc, this));
			return false;
		}
		if (npc.getId() == SURVIVAL_EXPERT && pastStage(Stage.TALK_TO_SURVIVAL_EXPERT)) {
			player.startConversation(new SurvivalExpert(player, npc, this));
			return false;
		} else if (npc.getId() == FISHING_SPOT && pastStage(Stage.CATCH_SHRIMP)) {
			player.getActionManager().setAction(new Fishing(FishingSpot.SHRIMP, npc));
			sendText(true, "Please wait.", "This should only take a few seconds.", "As you gain Fishing experience you'll find that there are many", "types of fish and many ways to catch them.");
			return false;
		} else if (npc.getId() == MASTER_CHEF && pastStage(Stage.TALK_TO_CHEF)) {
			player.startConversation(new MasterChef(player, npc, this));
			return false;
		} else if (npc.getId() == QUEST_GUIDE && pastStage(Stage.TALK_TO_QUEST_GUIDE)) {
			player.startConversation(new QuestGuide(player, npc, this));
			return false;
		} else if (npc.getId() == MINING_INSTRUCTOR && pastStage(Stage.TALK_TO_MINING_GUIDE)) {
			player.startConversation(new MiningInstructor(player, npc, this));
			return false;
		} else if (npc.getId() == COMBAT_INSTRUCTOR && pastStage(Stage.TALK_TO_COMBAT_INSTRUCTOR)) {
			player.startConversation(new CombatInstructor(player, npc, this));
			return false;
		} else if (npc.getId() == BANKER && pastStage(Stage.OPEN_BANK)) {
			player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(BANKER, HeadE.CHEERFUL, "Good day, would you like to access your bank account?"))
					.addNext(new OptionStatement("Select an Option", "Yes.", "No thanks."))
					.addNext(new Dialogue().setFunc(() -> {
						player.getBank().clear();
						player.getBank().addItem(new Item(995, 25), false);
						nextStage(Stage.ENTER_FINANCIAL_ROOM);
						player.getBank().open();
					}
							)).finish()));
			return false;
		} else if (npc.getId() == FINANCIAL_ADVISOR && pastStage(Stage.TALK_TO_FINANCIAL_ADVISOR)) {
			player.startConversation(new FinancialAdvisor(player, npc, this));
			return false;
		} else if (npc.getId() == BROTHER_BRACE && pastStage(Stage.TALK_TO_BROTHER_BRACE)) {
			player.startConversation(new BrotherBrace(player, npc, this));
			return false;
		} else if (npc.getId() == MAGIC_INSTRUCTOR && pastStage(Stage.TALK_TO_MAGIC_INSTRUCTOR)) {
			player.startConversation(new MagicInstructor(player, npc, this));
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 3014 && pastStage(Stage.LEAVE_GUIDE_ROOM)) {
			nextStage(Stage.TALK_TO_SURVIVAL_EXPERT);
			player.handleOneWayDoor(object);
			return false;
		}
		if (object.getId() == 3033 && pastStage(Stage.CHOP_TREE)) {
			player.getActionManager().setAction(new Woodcutting(object, TreeType.NORMAL));
			sendText(true, "Please wait.", "", "Your character is now attempting to cut down the tree. Sit back", "for a moment while he does all the hard work.");
		} else if (object.getDefinitions().getName().equals("Oak"))
			sendText(true, "", "", "You wouldn't know where to start with this tree.");
		else if ((object.getId() == 3015 || object.getId() == 3016) && pastStage(Stage.LEAVE_SURVIVAL_EXPERT)) {
			nextStage(Stage.ENTER_CHEF_HOUSE);
			player.handleOneWayDoor(object, 0, 1);
		} else if (object.getId() == 3017 && pastStage(Stage.ENTER_CHEF_HOUSE)) {
			nextStage(Stage.TALK_TO_CHEF);
			player.handleOneWayDoor(object);
		} else if (object.getId() == 3018 && pastStage(Stage.LEAVE_CHEF_HOUSE)) {
			nextStage(Stage.OPEN_EMOTES);
			player.handleOneWayDoor(object, 0, 1);
		} else if (object.getId() == 3019 && pastStage(Stage.ENTER_QUEST_GUIDE_HOUSE)) {
			nextStage(Stage.TALK_TO_QUEST_GUIDE);
			player.handleOneWayDoor(object);
		} else if (object.getId() == 3029 && pastStage(Stage.LEAVE_QUEST_GUIDE_HOUSE)) {
			nextStage(Stage.TALK_TO_MINING_GUIDE);
			player.useLadder(new WorldTile(3088, 9520, 0));
		} else if (object.getId() == 3028 && pastStage(Stage.LEAVE_QUEST_GUIDE_HOUSE))
			player.useLadder(new WorldTile(3088, 3120, 0));
		else if (object.getId() == 3043)
			player.getActionManager().setAction(new Mining(RockType.TIN, object));
		else if (object.getId() == 3042)
			player.getActionManager().setAction(new Mining(RockType.COPPER, object));
		else if (object.getId() == 2783 && pastStage(Stage.CLICK_ANVIL)) {
			nextStage(Stage.SMITH_DAGGER);
			return true;
		} else if ((object.getId() == 3020 || object.getId() == 3021) && pastStage(Stage.ENTER_CHEF_HOUSE)) {
			player.handleOneWayDoor(object, 0, 1);
			nextStage(Stage.TALK_TO_COMBAT_INSTRUCTOR);
		} else if (object.getId() == 3022 || object.getId() == 3023 && pastStage(Stage.ENTER_RAT_CAGE)) {
			if (getStage() == Stage.KILL_RAT_RANGE) {
				player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(COMBAT_INSTRUCTOR, HeadE.FRUSTRATED, "No, don't enter the pit. Range the rats from outside", "the cage."))));
				return false;
			}
			player.handleOneWayDoor(object, 0, 1);
			nextStage(Stage.ATTACK_RAT_MELEE);
		} else if (object.getId() == 3030 && pastStage(Stage.LEAVE_COMBAT_AREA)) {
			nextStage(Stage.OPEN_BANK);
			player.useLadder(player.transform(0, -6400, 0));
		} else if (object.getId() == 3031 && pastStage(Stage.LEAVE_COMBAT_AREA))
			player.useLadder(player.transform(0, 6400, 0));
		else if (object.getId() == 3045 && pastStage(Stage.OPEN_BANK))
			player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(BANKER, HeadE.CHEERFUL, "Good day, would you like to access your bank account?"))
					.addNext(new OptionStatement("Select an Option", "Yes.", "No thanks."))
					.addNext(new Dialogue().setFunc(() -> {
						nextStage(Stage.ENTER_FINANCIAL_ROOM);
						player.getBank().open();
					}
							)).finish()));
		else if (object.getId() == 3024 && pastStage(Stage.ENTER_FINANCIAL_ROOM)) {
			nextStage(Stage.TALK_TO_FINANCIAL_ADVISOR);
			player.handleOneWayDoor(object);
		} else if (object.getId() == 3025 && pastStage(Stage.LEAVE_FINANCIAL_ADVISOR_ROOM)) {
			nextStage(Stage.TALK_TO_BROTHER_BRACE);
			player.handleOneWayDoor(object);
		} else if (object.getId() == 36999 || object.getId() == 37002)
			Doors.handleDoubleDoor(player, object);
		else if (object.getId() == 3026 && pastStage(Stage.LEAVE_CHURCH_AREA)) {
			nextStage(Stage.TALK_TO_MAGIC_INSTRUCTOR);
			player.handleOneWayDoor(object, 0, 1);
		}
		return false;
	}

	@Override
	public boolean processObjectClick2(GameObject object) {
		if (object.getId() == 3043) {
			nextStage(Stage.PROSPECTING_COPPER);
			player.sendMessage("The rock contains tin!");
		} else if (object.getId() == 3042) {
			nextStage(Stage.TALK_TO_MINING_GUIDE_2);
			player.sendMessage("The rock contains copper!");
		}
		return false;
	}

	@Override
	public boolean processItemOnObject(GameObject object, Item item) {
		if ((item.getId() == 438 || item.getId() == 436) && object.getId() == 3044)
			player.getActionManager().setAction(new Smelting(Smelting.SmeltingBar.BRONZE, object, 1));
		else if (item.getId() == Fish.SHRIMP.getId() || item.getId() == 2307)
			return true;
		return false;
	}

	@Override
	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if (usedWith(itemUsed, usedWith, 590, 1511))
			sendText(true, "Please wait.", "", "Your character is now attempting to light the fire.", "This should only take a few seconds.");
		else if (usedWith(itemUsed, usedWith, 1929, 1933)) {
			player.getInventory().deleteItem(1929, 1);
			player.getInventory().deleteItem(1933, 1);
			player.getInventory().addItem(1925, 1);
			player.getInventory().addItem(1931, 1);
			player.getInventory().addItem(2307, 1);
			nextStage(Stage.COOK_DOUGH);
			return false;
		}
		return true;
	}

	public boolean usedWith(Item itemUsed, Item usedWith, int item1, int item2) {
		return (itemUsed.getId() == item1 && usedWith.getId() == item2) || (usedWith.getId() == item1 && itemUsed.getId() == item2);
	}

	@Override
	public boolean gainXP(int skillId, double exp) {
		double currXp = player.getSkills().getXp(skillId);
		int levelPost = Skills.getLevelForXp(skillId, (long) (currXp + exp));
		if (levelPost > 3) {
			player.getSkills().set(skillId, 3);
			return false;
		}
		if (player.getSkills().getLevelForXp(skillId) >= 3)
			return false;
		return true;
	}

	@Override
	public void trackXP(int skillId, int addedXp) {
		player.closeInterfaces();
		if (getStage() == Stage.MAKE_A_FIRE && skillId == Constants.FIREMAKING)
			nextStage(Stage.OPEN_SKILLS);
		if (getStage() == Stage.ATTACK_RAT_MELEE && (skillId == Constants.ATTACK || skillId == Constants.STRENGTH || skillId == Constants.DEFENSE))
			nextStage(Stage.KILL_RAT_MELEE);
		if (getStage() == Stage.CAST_WIND_STRIKE && skillId == Constants.MAGIC)
			nextStage(Stage.TALK_TO_MAGIC_INSTRUCTOR_3);
	}

	@Override
	public boolean canAddInventoryItem(int itemId, int amount) {
		if (getStage() == Stage.CHOP_TREE && itemId == TreeType.NORMAL.getLogsId()[0]) {
			nextStage(Stage.MAKE_A_FIRE);
			player.startConversation(new Dialogue().addItem(itemId, "You get some logs."));
		} else if (itemId == Fish.SHRIMP.getId()) {
			if (getStage() == Stage.CATCH_SHRIMP)
				nextStage(Stage.BURN_SHRIMP);
		} else if (itemId == Cooking.Cookables.RAW_SHRIMP.getBurntId().getId()) {
			if (getStage() == Stage.COOK_SHRIMP) {
				player.getInventory().addItem(Cooking.Cookables.RAW_SHRIMP.getProduct());
				return false;
			}
			nextStage(Stage.COOK_SHRIMP);
		} else if (itemId == 2309 && getStage() == Stage.COOK_DOUGH)
			nextStage(Stage.OPEN_MUSIC);
		else if (itemId == Cooking.Cookables.RAW_SHRIMP.getProduct().getId()) {
			if (getStage() == Stage.BURN_SHRIMP) {
				player.getInventory().addItem(Cooking.Cookables.RAW_SHRIMP.getBurntId());
				return false;
			}
			nextStage(Stage.LEAVE_SURVIVAL_EXPERT);
		} else if (itemId == 438)
			nextStage(Stage.MINING_COPPER);
		else if (itemId == 436)
			nextStage(Stage.SMELTING);
		else if (itemId == 2349)
			nextStage(Stage.TALK_TO_MINING_GUIDE_3);
		else if (itemId == 1205)
			nextStage(Stage.LEAVE_MINING_AREA);
		return true;
	}

	@Override
	public void processIncomingHit(Hit hit) {
		if (player.getHitpoints() <= hit.getDamage())
			hit.setDamage(0);
	}

	@Override
	public void processNPCDeath(NPC npc) {
		if (npc.getId() == GIANT_RAT) {
			nextStage(Stage.TALK_TO_COMBAT_INSTRUCTOR_3);
			nextStage(Stage.LEAVE_COMBAT_AREA);
		}
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (pastStage(Stage.WIELD_DAGGER)) {
			if (itemId == 1205)
				nextStage(Stage.TALK_TO_COMBAT_INSTRUCTOR_2);
			else if ((itemId == 1171 && player.getEquipment().getWeaponId() == 1277) || (itemId == 1277 && player.getEquipment().getShieldId() == 1171))
				nextStage(Stage.OPEN_COMBAT_TAB);
			return true;
		}
		player.sendMessage("You'll be told how to equip items later.");
		return false;
	}

	@Override
	public void start() {
		if (getStage() == Stage.TALK_TO_GUIDE)
			player.setNextWorldTile(new WorldTile(3094, 3107, 0));
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendSub(Sub.ABOVE_CHATBOX, 371);
		getStage().loadAllUpTo(this);
	}

	@Override
	public boolean canTakeItem(GroundItem item) {
		if (item != null && item.isPrivate() && item.getVisibleToId() == player.getUuid())
			return true;
		return false;
	}

	@Override
	public boolean canTrade() {
		return  false;
	}

	@Override
	public boolean login() {
		start();
		sendInterfaces();
		return false;
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeSub(Sub.ABOVE_CHATBOX);
		player.getInterfaceManager().closeReplacedRealChatBoxInterface();
	}

	@Override
	public boolean logout() {
		return false;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void nextStage(Stage next) {
		Stage previous = Stage.values()[next.ordinal()-1];
		if (getStage() == previous) {
			setStage(next);
			next.start(this);
		}
	}

	public void sendProgress() {
		float percent = (float) getStage().ordinal() / (float) Stage.values().length;
		player.getVars().setVar(406, Math.round(percent * 21.0f));
	}

	public NPC getNPC(int id) {
		for (int regionId : TUTORIAL_REGIONS) {
			Region r = World.getRegion(regionId, true);
			if (r == null || r.getNPCsIndexes() == null)
				continue;
			for (int npcIdx : r.getNPCsIndexes()) {
				NPC npc = World.getNPCs().get(npcIdx);
				if (npc == null || npc.getId() != id)
					continue;
				return npc;
			}
		}
		return null;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return false;
	}

	public void removeHint() {
		player.getHintIconsManager().removeUnsavedHintIcon();
	}

	public void hintLocation(int x, int y, int height) {
		player.getHintIconsManager().addHintIcon(x, y, 0, height, 10, 0, -1, false);
	}

	public void hintNPC(int npcId) {
		NPC npc = getNPC(npcId);
		if (npc != null && !npc.isDead())
			player.getHintIconsManager().addHintIcon(npc, 0, -1, false);
	}

	public boolean inSection(Stage min, Stage max) {
		return getStage().ordinal() >= min.ordinal() && getStage().ordinal() < max.ordinal();
	}

	public boolean pastStage(Stage stage) {
		return getStage().ordinal() >= stage.ordinal();
	}

	public void sendText(String... text) {
		sendText(false, text);
	}

	public void sendText(boolean temp, String... text) {
		player.getInterfaceManager().replaceRealChatBoxInterface(372);
		player.getPackets().setIFHidden(371, 4, true);
		for (int i = 0; i < 7; i++)
			if (i < text.length)
				player.getPackets().setIFText(372, i, text[i]);
			else
				player.getPackets().setIFText(372, i, "");
		if (!temp)
			prevText = text;
		else
			player.setCloseInterfacesEvent(() -> {
				sendText(prevText);
			});
	}

}
