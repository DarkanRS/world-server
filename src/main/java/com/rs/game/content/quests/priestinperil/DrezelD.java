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
package com.rs.game.content.quests.priestinperil;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class DrezelD extends Conversation {
	static final int Drezel = 1047;
	@ServerStartupEvent
	public static void addLoSOverride() {
		Entity.addLOSOverride( Drezel );
	}

	public static NPCClickHandler HandleDrezel = new NPCClickHandler(new Object[] { Drezel }, new String[] { "Talk-to" }, e -> {
			e.getPlayer().startConversation(new DrezelD(e.getPlayer()));
	});

	public DrezelD(Player player) {
		super(player);
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 4) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Hello.")
					.addNPC(Drezel, HeadE.CALM_TALK, "Oh! You do not appear to be one of those Zamorakians who imprisoned me! Who are you and why are you here?")
					.addPlayer(HeadE.CALM_TALK, "My name's " + player.getDisplayName() + ". King Roald sent me to find out what was going on at the temple. I take it you are Drezel?")
					.addNPC(Drezel, HeadE.CALM_TALK, "That's right! Oh, praise be to Saradomin! All is not yet lost!")
					.addNPC(Drezel, HeadE.CALM_TALK, "I feared that when those Zamorakians attacked this place and imprisoned me up here, Misthalin would be doomed!")
					.addNPC(Drezel, HeadE.CALM_TALK, "If they should manage to desecrate the holy River Salve, we will be defenseless against the vampyres of Morytania!")
					.addOptions(ops -> {
						ops.add("Why is the river such a good defence?")
								.addPlayer(HeadE.CALM_TALK, "Why is the river such a good defence?")
								.addNPC(Drezel, HeadE.CALM_TALK, "I'm not sure if this is the best time for a history lesson. I suppose I can give you a brief overview though. It might help you understand our next steps.")
								.addNPC(Drezel, HeadE.CALM_TALK, "It is said that many years ago, the land that is now Morytania was a peaceful Saradominist kingdom. Alas, all that ended when the vampyres invaded, claiming the land as their own.")
								.addNPC(Drezel, HeadE.CALM_TALK, "Morytania wasn't enough for them though. Soon, they set their sights on these lands as well.")
								.addNPC(Drezel, HeadE.CALM_TALK, "Misthalin would have quickly fallen to the vampyres, were it not for the bravery of the Seven Priestly Warriors.")
								.addNPC(Drezel, HeadE.CALM_TALK, "They led the armies of Misthalin against the vampyres in a great battle that took place right where this temple stands.")
								.addNPC(Drezel, HeadE.CALM_TALK, "For ten days and nights they fought, never sleeping, never eating, fueled by their desire to make the world a better place for humans to live.")
								.addNPC(Drezel, HeadE.CALM_TALK, "On the eleventh day they were to be joined by reinforcements. However, when those reinforcements arrived, they found the Seven Priestly Warriors and their forces had been slain by the vampyres.")
								.addNPC(Drezel, HeadE.CALM_TALK, "Their sacrifice was not in vain though. Before they were defeated, the Seven Priestly Warriors managed to bless the Salve with the holy power of Saradomin.")
								.addNPC(Drezel, HeadE.CALM_TALK, "Their blessings remain to this day, ensuring no vampyre can cross the river.")
								.addNPC(Drezel, HeadE.CALM_TALK, "The temple here was built soon after. It guards the source of the River Salve, and acts as the only passage between Misthalin and Morytania.")
								.addNPC(Drezel, HeadE.CALM_TALK, "Once the temple was complete, the bodies of the Seven Priestly Warriors were laid to rest in tombs of honour in the mausoleum below. On the top of each tomb, a golden gift was placed as a mark of respect.")
								.addNPC(Drezel, HeadE.CALM_TALK, "A great statue was also built over the river, so that all who might try and cross into Misthalin would know that these lands are protected by Saradomin himself.")
								.addPlayer(HeadE.CALM_TALK, "Okay, I can see how the river protects the border. Where do these Zamorakians fit in though?")
								.addNPC(Drezel, HeadE.CALM_TALK, "Well, as much as it saddens me to say so adventurer, Lord Saradomin's presence has not been felt on the land for many years now.")
								.addNPC(Drezel, HeadE.CALM_TALK, "Of course, while we know that he still watches over us, his power here is not as strong as it once was.")
								.addNPC(Drezel, HeadE.CALM_TALK, "I fear this group of Zamorakians intends to take advantage of that. If they can somehow pollute the Salve, the blessings on the river might fail.")
								.addNPC(Drezel, HeadE.CALM_TALK, "If that happens, there won't be anything to stop an invasion from Morytania.")
								.addNPC(Drezel, HeadE.CALM_TALK, "So what do you say adventurer? Will you aid me and all of Misthalin in foiling this Zamorakian plot?")
								.addOptions(ops2 -> {
									ops2.add("Yes, of course.")
											.addPlayer(HeadE.CALM_TALK, "Yes, of course.")
											.addNPC(Drezel, HeadE.CALM_TALK, "Thank you, adventurer. There is a well in the mausoleum beneath the temple that provides direct access to the source of the Salve. That will be where the Zamorakians will attempt their sabotage.")
											.addNPC(Drezel, HeadE.CALM_TALK, "We need to get to that well. However, the immediate problem is that I'm trapped in this cell.")
											.addNPC(Drezel, HeadE.CALM_TALK, "The key must be somewhere nearby. One of the Zamorakians might have it, or they might have hidden it somewhere. That's not the only problem though.")
											.addPlayer(HeadE.CALM_TALK, "What else is there?")
											.addNPC(Drezel, HeadE.CALM_TALK, "When the Salve was blessed, a small number of vampyres were left trapped on this side of it. They were heavily weakened, but still posed a dangerous threat.")
											.addNPC(Drezel, HeadE.CALM_TALK, "This coffin here contains one of those vampyres. The Zamorakians somehow found it and brought it here with them.")
											.addPlayer(HeadE.CALM_TALK, "I see. It doesn't seem to be posing a threat right now though.")
											.addNPC(Drezel, HeadE.CALM_TALK, "No, but as those fiendish Zamorakians pointed out to me with delight, as I am the descendant of one of the Seven Priestly Warriors who blessed the river, it will likely recognise the smell of my blood.")
											.addNPC(Drezel, HeadE.CALM_TALK, "If I get too close, it will probably wake up and kill me, very slowly and painfully.")
											.addPlayer(HeadE.CALM_TALK, "Maybe I could kill it somehow?")
											.addNPC(Drezel, HeadE.CALM_TALK, "No adventurer, you should not risk waking it. Even now, it could pose a huge threat.")
											.addNPC(Drezel, HeadE.CALM_TALK, "The priests of old would incapacitate trapped vampyres with the blessings of Saradomin. I suggest you do the same.")
											.addPlayer(HeadE.CALM_TALK, "Right. So, I need to find the key to your cell and do something about the vampyre. Got it.")
											.addNPC(Drezel, HeadE.CALM_TALK, "Good luck.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 5));
									ops2.add("No.")
											.addPlayer(HeadE.CALM_TALK, "No.")
											.addNPC(Drezel, HeadE.CALM_TALK, "Oooooh... I knew it was too good to be true... Then leave me to my fate villain, there's no need to taunt me as well as keeping me imprisoned.");
								});
						ops.add("So, what now?")
								.addPlayer(HeadE.CALM_TALK, "So, what now?")
								.addNPC(Drezel, HeadE.CALM_TALK, "Well, let's just say if we cannot undo whatever damage has been done here, the entire land is in grave peril!")
								.addNPC(Drezel, HeadE.CALM_TALK, "So what do you say adventurer? Will you aid me and all of Misthalin in foiling this Zamorakian plot?")
								.addOptions(ops2 -> {
									ops2.add("Yes, of course.")
											.addPlayer(HeadE.CALM_TALK, "Yes, of course.")
											.addNPC(Drezel, HeadE.CALM_TALK, "Thank you, adventurer. There is a well in the mausoleum beneath the temple that provides direct access to the source of the Salve. That will be where the Zamorakians will attempt their sabotage.")
											.addNPC(Drezel, HeadE.CALM_TALK, "We need to get to that well. However, the immediate problem is that I'm trapped in this cell.")
											.addNPC(Drezel, HeadE.CALM_TALK, "The key must be somewhere nearby. One of the Zamorakians might have it, or they might have hidden it somewhere. That's not the only problem though.")
											.addPlayer(HeadE.CALM_TALK, "What else is there?")
											.addNPC(Drezel, HeadE.CALM_TALK, "When the Salve was blessed, a small number of vampyres were left trapped on this side of it. They were heavily weakened, but still posed a dangerous threat.")
											.addNPC(Drezel, HeadE.CALM_TALK, "This coffin here contains one of those vampyres. The Zamorakians somehow found it and brought it here with them.")
											.addPlayer(HeadE.CALM_TALK, "I see. It doesn't seem to be posing a threat right now though.")
											.addNPC(Drezel, HeadE.CALM_TALK, "No, but as those fiendish Zamorakians pointed out to me with delight, as I am the descendant of one of the Seven Priestly Warriors who blessed the river, it will likely recognise the smell of my blood.")
											.addNPC(Drezel, HeadE.CALM_TALK, "If I get too close, it will probably wake up and kill me, very slowly and painfully.")
											.addPlayer(HeadE.CALM_TALK, "Maybe I could kill it somehow?")
											.addNPC(Drezel, HeadE.CALM_TALK, "No adventurer, you should not risk waking it. Even now, it could pose a huge threat.")
											.addNPC(Drezel, HeadE.CALM_TALK, "The priests of old would incapacitate trapped vampyres with the blessings of Saradomin. I suggest you do the same.")
											.addPlayer(HeadE.CALM_TALK, "Right. So, I need to find the key to your cell and do something about the vampyre. Got it.")
											.addNPC(Drezel, HeadE.CALM_TALK, "Good luck.", () -> player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 5));
									ops2.add("No.")
											.addPlayer(HeadE.CALM_TALK, "No.")
											.addNPC(Drezel, HeadE.CALM_TALK, "Oooooh... I knew it was too good to be true...  Then leave me to my fate villain, there's no need to taunt me as well as keeping me imprisoned.");
								});

					})
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 5) {
			if(player.getInventory().containsItem(2944)){
				player.startConversation(new Dialogue()
						.addNPC(Drezel, HeadE.CALM_TALK, "How goes it adventurer? Any luck in finding the key to the cell or a way of stopping the vampyre yet?")
						.addPlayer(HeadE.CALM_TALK, "I have this key I took from one of those Zamorakian monks!")
						.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work adventurer! Quickly, try it on the door, and see if it will free me!")
				);
				return;
			}
			if(player.getInventory().containsItem(2945)){
				player.startConversation(new Dialogue()
						.addNPC(Drezel, HeadE.CALM_TALK, "How goes it adventurer? Any luck in finding the key to the cell or a way of stopping the vampyre yet?")
						.addPlayer(HeadE.CALM_TALK, "I have this key I took from one of the monuments underground.")
						.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work adventurer! Quickly, try it on the door, and see if it will free me!")
				);
				return;
			}
			else{
				player.startConversation(new Dialogue()
						.addNPC(Drezel, HeadE.CALM_TALK, "How goes it adventurer? Any luck in finding the key to the cell or a way of stopping the vampyre yet?")
						.addPlayer(HeadE.CALM_TALK, "No, not yet...")
						.addNPC(Drezel, HeadE.CALM_TALK, "Well don't give up adventurer! That key must be around here somewhere! I know none of those Zamorakians ever got very far from this building!")
						.addPlayer(HeadE.CALM_TALK, "How do you know that?")
						.addNPC(Drezel, HeadE.CALM_TALK, "I could hear them laughing about some gullible fool that they tricked into killing the guard dog at the monument.")
						.addPlayer(HeadE.CALM_TALK, "Oh.")
						.addNPC(Drezel, HeadE.CALM_TALK, "Honestly, what kind of idiot would go around killing things just because a stranger told them to? What kind of oafish, numb-skulled, dim-witted...")
						.addPlayer(HeadE.CALM_TALK, "Okay, okay, I get the picture!")
						.addOptions(ops -> {
							ops.add("Could you tell me more about this temple?")
									.addPlayer(HeadE.CALM_TALK, "Could you tell me more about this temple?")
									.addNPC(Drezel, HeadE.CALM_TALK, "It is said that many years ago, the land that is now Morytania was a peaceful Saradominist kingdom. Alas, all that ended when the vampyres invaded, claiming the land as their own.")
									.addNPC(Drezel, HeadE.CALM_TALK, "Morytania wasn't enough for them though. Soon, they set their sights on these lands as well.")
									.addNPC(Drezel, HeadE.CALM_TALK, "Misthalin would have quickly fallen to the vampyres, were it not for the bravery of the Seven Priestly Warriors.")
									.addNPC(Drezel, HeadE.CALM_TALK, "They led the armies of Misthalin against the vampyres in a great battle that took place right where this temple stands.")
									.addNPC(Drezel, HeadE.CALM_TALK, "For ten days and nights they fought, never sleeping, never eating, fuelled by their desire to make the world a better place for humans to live.")
									.addNPC(Drezel, HeadE.CALM_TALK, "On the eleventh day they were to be joined by reinforcements. However, when those reinforcements arrived, they found the Seven Priestly Warriors and their forces had been slain by the vampyres.")
									.addNPC(Drezel, HeadE.CALM_TALK, "Their sacrifice was not in vain though. Before they were defeated, the Seven Priestly Warriors managed to bless the Salve with the holy power of Saradomin.")
									.addNPC(Drezel, HeadE.CALM_TALK, "Their blessings remain to this day, ensuring no vampyre can cross the river.")
									.addNPC(Drezel, HeadE.CALM_TALK, "The temple here was built soon after. It guards the source of the River Salve, and acts as the only passage between Misthalin and Morytania.")
									.addNPC(Drezel, HeadE.CALM_TALK, "Once the temple was complete, the bodies of the Seven Priestly Warriors were laid to rest in tombs of honour in the mausoleum below. On the top of each tomb, a golden gift was placed as a mark of respect.")
									.addNPC(Drezel, HeadE.CALM_TALK, "A great statue was also built over the river, so that all who might try and cross into Misthalin would know that these lands are protected by Saradomin himself.")
									.addPlayer(HeadE.CALM_TALK, "Thank you for the information. I'll get going.");
							ops.add("I'll get going.")
									.addPlayer(HeadE.CALM_TALK, "I'll get going.");
						})
				);
			}
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 6) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "The key fit the lock! You're free to leave now!")
					.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work, adventurer! Unfortunately, as you know, I cannot risk waking the vampyre in that coffin.")
					.addPlayer(HeadE.CALM_TALK, "Do you have any ideas about dealing with the vampyre?")
					.addNPC(Drezel, HeadE.CALM_TALK, "Well, the water of the Salve should still have enough power to work against the vampyre, even if the Zamorakians have desecrated it.")
					.addNPC(Drezel, HeadE.CALM_TALK, "Maybe you should try and get hold of some?")
			);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 7) {
			if(player.getInventory().containsItem(2953)){
				player.startConversation(new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "The key fit the lock! You're free to leave now!")
						.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work, adventurer! Unfortunately, as you know, I cannot risk waking the vampyre in that coffin.")
						.addPlayer(HeadE.CALM_TALK, "I have some water from the Salve. It seems to have been desecrated though. Do you think you could bless it for me?")
						.addNPC(Drezel, HeadE.CALM_TALK, "Yes, good thinking adventurer! Give it to me, I will bless it!", () -> {
							player.getInventory().replace(2953, 2954);
							player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 8);
						}));
				return;
			}
			if(player.getInventory().containsItem(2954)) {
				player.startConversation(new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "The key fit the lock! You're free to leave now!")
						.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work, adventurer! Unfortunately, as you know, I cannot risk waking the vampyre in that coffin.")
						.addPlayer(HeadE.CALM_TALK, "I have some blessed water from the Salve in this bucket. Do you think it will help against that vampyre?")
						.addNPC(Drezel, HeadE.CALM_TALK, "Yes! Great idea! If his coffin is doused in the blessed water he will be unable to leave it! Use it on his coffin, quickly!")
				);
				return;
			}
			else
				player.startConversation(new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "The key fit the lock! You're free to leave now!")
						.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work, adventurer! Unfortunately, as you know, I cannot risk waking the vampyre in that coffin.")
						.addPlayer(HeadE.CALM_TALK, "Do you have any ideas about dealing with the vampyre?")
						.addNPC(Drezel, HeadE.CALM_TALK, "Well, the water of the Salve should still have enough power to work against the vampyre, even if the Zamorakians have desecrated it.")
						.addNPC(Drezel, HeadE.CALM_TALK, "Maybe you should try and get hold of some?")
				);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 8) {
			if(player.getInventory().containsItem(2953)){
				player.startConversation(new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "The key fit the lock! You're free to leave now!")
						.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work, adventurer! Unfortunately, as you know, I cannot risk waking the vampyre in that coffin.")
						.addPlayer(HeadE.CALM_TALK, "I have some water from the Salve. It seems to have been desecrated though. Do you think you could bless it for me?")
						.addNPC(Drezel, HeadE.CALM_TALK, "Yes, good thinking adventurer! Give it to me, I will bless it!", () -> {
							player.getInventory().replace(2953, 2954);
							player.getQuestManager().setStage(Quest.PRIEST_IN_PERIL, 8);
						}));
				return;
			}
			if(player.getInventory().containsItem(2954)) {
				player.startConversation(new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "The key fit the lock! You're free to leave now!")
						.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work, adventurer! Unfortunately, as you know, I cannot risk waking the vampyre in that coffin.")
						.addPlayer(HeadE.CALM_TALK, "I have some blessed water from the Salve in this bucket. Do you think it will help against that vampyre?")
						.addNPC(Drezel, HeadE.CALM_TALK, "Yes! Great idea! If his coffin is doused in the blessed water he will be unable to leave it! Use it on his coffin, quickly!")
				);
				return;
			}
			else
				player.startConversation(new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "The key fit the lock! You're free to leave now!")
						.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work, adventurer! Unfortunately, as you know, I cannot risk waking the vampyre in that coffin.")
						.addPlayer(HeadE.CALM_TALK, "Do you have any ideas about dealing with the vampyre?")
						.addNPC(Drezel, HeadE.CALM_TALK, "Well, the water of the Salve should still have enough power to work against the vampyre, even if the Zamorakians have desecrated it.")
						.addNPC(Drezel, HeadE.CALM_TALK, "Maybe you should try and get hold of some?")
				);
		}
		if (player.getQuestManager().getStage(Quest.PRIEST_IN_PERIL) == 9) {
			player.startConversation(new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "I poured the blessed water over the coffin. I think that should trap the vampyre in there long enough for you to escape.")
					.addNPC(Drezel, HeadE.CALM_TALK, "Excellent work adventurer! I am free at last! Let me ensure that evil vampyre is trapped for good. I will then meet you down in the mausoleum.")
					.addPlayer(HeadE.CALM_TALK, "Okay.")
			);
		}
	}
}

