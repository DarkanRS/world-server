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
package com.rs.game.content.quests.princealirescue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.princealirescue.PrinceAliRescue.BEER;

@PluginEventHandler
public class JoePrinceAliRescueD extends Conversation {
	private final static int JOE = 916;

	public JoePrinceAliRescueD(Player player) {
		super(player);
		if(player.getQuestManager().getAttribs(Quest.PRINCE_ALI_RESCUE).getB("Joe_guard_is_drunk")) {
			addNPC(JOE, HeadE.DRUNK, "Halt! Who goes there?");
			addPlayer(HeadE.HAPPY_TALKING, "Hello friend, I am just rescuing the prince, is that ok?");
			addNPC(JOE, HeadE.DRUNK, "Thatsh a funny joke. You are lucky I am shober. Go in peace, friend.");
		}
		else
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					if(player.getInventory().containsItem(BEER, 3))
						option("I have some beer here, fancy one?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I have some beer here, fancy one?")
								.addNPC(JOE, HeadE.CALM_TALK, "Ah, that would be lovely, just one now, just to wet my throat.")
								.addPlayer(HeadE.HAPPY_TALKING, "Of course, it must be tough being here without a drink.")
								.addSimple("You hand a beer to the guard, he drinks it in seconds.")
								.addNPC(JOE, HeadE.HAPPY_TALKING, "That was perfect, I can't thank you enough.")
								.addPlayer(HeadE.SECRETIVE, "How are you? Still ok? Not too drunk?")
								.addPlayer(HeadE.SECRETIVE, "Would you care for another, my friend?")
								.addNPC(JOE, HeadE.CALM_TALK, "I better not, I don't want to be drunk on duty.")
								.addPlayer(HeadE.SECRETIVE, "Here, just keep these for later, I hate to see a thirsty guard.")
								.addSimple("You hand two more beers to the guard.")
								.addSimple("He takes a sip of one, and then he drinks them both.")
								.addNPC(JOE, HeadE.DRUNK, "Franksh, that wash just what I need to shtay on guard. No more beersh, I don't want to get drunk.")
								.addSimple("The guard is drunk, and no longer a problem.",
										() -> {
											player.getInventory().deleteItem(BEER, 3);
											player.getQuestManager().getAttribs(Quest.PRINCE_ALI_RESCUE).setB("Joe_guard_is_drunk", true);
										}));
					option("Tell me about the life of a guard.", new Dialogue()
							.addPlayer(HeadE.TALKING_ALOT, "Tell me about the life of a guard.")
							.addNPC(JOE, HeadE.CALM_TALK, "Well, the hours are good.....")
							.addNPC(JOE, HeadE.FRUSTRATED, ".... But most of those hours are a drag. If only I had spent more time in Knight school when I" +
									" was a young boy. Maybe I wouldn't be here now, scared of Keli.")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("Hey, chill out, I won't cause you trouble.", new Dialogue()
											.addPlayer(HeadE.CALM_TALK, "Hey, chill out, I won't cause you trouble.")
											.addPlayer(HeadE.CALM_TALK, "I was just wondering what you do to relax.")
											.addNPC(JOE, HeadE.TALKING_ALOT, "You never relax with these people, but it's a good career for a young man and some " +
													"of the shouting I rather like.")
											.addNPC(JOE, HeadE.AMAZED, "RESISTANCE IS USELESS!", () -> {
												player.getTempAttribs().setB("JoeTheGuardTalksALot", true);})
											.addNext(()->{
												player.startConversation(new JoePrinceAliRescueD(player));}));
									option("What did you want to be when you were a boy?", new Dialogue()
											.addPlayer(HeadE.TALKING_ALOT, "What did you want to be when you were a boy?")
											.addNPC(JOE, HeadE.TALKING_ALOT, "Well, I loved to sit by the lake, with my toes in the water and shoot the fish with my bow and arrow.")
											.addPlayer(HeadE.TALKING_ALOT, "That was a strange hobby for a little boy.")
											.addNPC(JOE, HeadE.TALKING_ALOT, "It kept us from goblin hunting, which was what most boys did. What are you here for?")
											.addNext(()->{
												player.startConversation(new JoePrinceAliRescueD(player));}));
								}
							}));
					if(player.getTempAttribs().getB("JoeTheGuardTalksALot")) {
						option("So what do you buy with these great wages?", new Dialogue()
								.addPlayer(HeadE.TALKING_ALOT, "So what do you buy with these great wages?")
								.addNPC(JOE, HeadE.TALKING_ALOT, "Really, after working here, there's only time for a drink or three. All us guards go to the" +
										" same bar and drink ourselves stupid.")
								.addNPC(JOE, HeadE.TALKING_ALOT, "It's what I enjoy these days, that fade into unconciousness. I can't resist the sight of a " +
										"really cold beer.")
								.addNext(()->{
									player.startConversation(new JoePrinceAliRescueD(player));}));
						option("Would you be interested in making a little more money?", new Dialogue()
								.addPlayer(HeadE.SECRETIVE, "Would you be interested in making a little more money?")
								.addNPC(JOE, HeadE.ANGRY, "WHAT?! Are you trying to bribe me? I may not be a great guard, but I am loyal. How DARE you " +
										"try to bribe me!")
								.addPlayer(HeadE.SHAKING_HEAD, "No, no, you got the wrong idea, totally. I just wondered if you wanted some part-time bodyguard work.")
								.addNPC(JOE, HeadE.CALM_TALK, "Oh. Sorry. No, I don't need money. As long as you were not offering me a bribe.")
								.addNext(()->{
									player.startConversation(new JoePrinceAliRescueD(player));}));
					}
					option("What did you want to be when you were a boy?", new Dialogue()
							.addPlayer(HeadE.TALKING_ALOT, "What did you want to be when you were a boy?")
							.addNPC(JOE, HeadE.TALKING_ALOT, "Well, I loved to sit by the lake, with my toes in the water and shoot the fish with my bow and arrow.")
							.addPlayer(HeadE.TALKING_ALOT, "That was a strange hobby for a little boy.")
							.addNPC(JOE, HeadE.TALKING_ALOT, "It kept us from goblin hunting, which was what most boys did. What are you here for?")
							.addNext(()->{
								player.startConversation(new JoePrinceAliRescueD(player));}));
					option("I had better leave, I don't want trouble.", new Dialogue()
							.addNPC(JOE, HeadE.HAPPY_TALKING, "Thanks, I appreciate that. Talking on duty can be punishable by having your mouth stitched up. These are " +
									"tough people, no mistake."));
				}
			});


	}



	public static NPCClickHandler handleJoe = new NPCClickHandler(new Object[] { JOE }, e -> e.getPlayer().startConversation(new JoePrinceAliRescueD(e.getPlayer()).getStart()));
}

