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
package com.rs.game.content.quests.handlers.shieldofarrav;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;

@PluginEventHandler
public class MuseumCuratorArravD extends Conversation {
	final int CURATOR = 646;

	public MuseumCuratorArravD(Player p) {
		super(p);
		addPlayer(HeadE.HAPPY_TALKING, "Hello.");
		addNPC(CURATOR, HeadE.CALM_TALK, "Hi.");
		if(player.isQuestComplete(Quest.SHIELD_OF_ARRAV)) {
			addPlayer(HeadE.HAPPY_TALKING, "Can I have another half of my certificate?");
			addNPC(CURATOR, HeadE.CALM_TALK, "Sure, though I don't see why you want one...");
			addSimple("He scribbles on a piece of paper and tears it in half.");
			addItem(ShieldOfArrav.CERTIFICATE_LEFT, "He hands you the certificate...", ()->{
				if(!ShieldOfArrav.hasGang(p)) {
					p.sendMessage("You should talk to an admin, you don't have a gang!");
					return;
				}
				if(ShieldOfArrav.isBlackArmGang(p))
					p.getInventory().addItem(ShieldOfArrav.CERTIFICATE_LEFT, 1);
				if(ShieldOfArrav.isPhoenixGang(p))
					p.getInventory().addItem(ShieldOfArrav.CERTIFICATE_RIGHT, 1);
			});
			return;
		}
		if(p.getInventory().containsItem("Broken shield") && p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) >= ShieldOfArrav.SPOKE_TO_KING_STAGE)
			tradeShield(p);
		else {
			addNPC(CURATOR, HeadE.CALM_TALK, "Do you have anything for me?");
			addPlayer(HeadE.SAD, "Not currently");
		}
	}

	public MuseumCuratorArravD(Player p, boolean restartConversation) {
		super(p);
		addPlayer(HeadE.HAPPY_TALKING, "Hello.");
		addNPC(CURATOR, HeadE.CALM_TALK, "Hi.");
		if(p.getInventory().containsItem("Broken shield") && p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) >= ShieldOfArrav.SPOKE_TO_KING_STAGE)
			tradeShield(p);
	}

	private void tradeShield(Player p) {
		addPlayer(HeadE.CALM_TALK, "Can you authenticate this item as the Shield Of Arrav?");
		addNPC(CURATOR, HeadE.SKEPTICAL, "Let me have a look at it first.");
		addSimple("The curator peers at the shield");
		addNPC(CURATOR, HeadE.AMAZED, "This is incredible!");
		addNPC(CURATOR, HeadE.AMAZED_MILD, "That shield has been missing for over twenty-five years!");
		addNPC(CURATOR, HeadE.CALM, "Leave the shield here with me and I'll write you out a certificate saying that you have returned the shield, so that you " +
				"can claim your reward from the King.");
		addPlayer(HeadE.HAPPY_TALKING, "Can I have two certificates please?");
		addNPC(CURATOR, HeadE.CALM, "Yes, certainly. Please hand over the shield.");
		addSimple("You hand over the shield half.");
		addSimple("The curator writes out two half-certificates.", () -> {
			if(p.getInventory().containsItem(ShieldOfArrav.SHIELD_LEFT_HALF, 1)) {
				p.getInventory().deleteItem(ShieldOfArrav.SHIELD_LEFT_HALF, 1);
				p.getInventory().addItem(ShieldOfArrav.CERTIFICATE_LEFT, 2);
				ShieldOfArrav.setStage(p, ShieldOfArrav.HAS_CERTIFICATE_STAGE);
			}
			if(p.getInventory().containsItem(ShieldOfArrav.SHIELD_RIGHT_HALF, 1)) {
				p.getInventory().deleteItem(ShieldOfArrav.SHIELD_RIGHT_HALF, 1);
				p.getInventory().addItem(ShieldOfArrav.CERTIFICATE_RIGHT, 2);
				ShieldOfArrav.setStage(p, ShieldOfArrav.HAS_CERTIFICATE_STAGE);
			}
		});
		addNPC(CURATOR, HeadE.CALM_TALK, "Of course, you won't actually be able to claim the reward with only half the reward certificate...");
		addPlayer(HeadE.CONFUSED, "What? I went through a lot of trouble to get that shield piece and now you tell me it was for nothing? That's not very fair!");
		addNPC(CURATOR, HeadE.CALM_TALK, "Well, if you were to get me the other half of the shield, I could give you the other half of the reward certificate.");
		if(ShieldOfArrav.isStageInPlayerSave(p, ShieldOfArrav.JOINED_PHOENIX_STAGE))
			addNPC(CURATOR, HeadE.CALM_TALK, "It's rumoured to be in the possession of the infamous Black Arm Gang, beyond that I can't help you.");
		if(ShieldOfArrav.isStageInPlayerSave(p, ShieldOfArrav.JOINED_BLACK_ARM_STAGE))
			addNPC(CURATOR, HeadE.CALM_TALK, "It's rumoured to be in the possession of the infamous Phoenix Gang, beyond that I can't help you.");
		addPlayer(HeadE.SKEPTICAL, "Okay, I'll see what I can do.");
	}



	public static ItemOnNPCHandler handleItemOnCurator = new ItemOnNPCHandler(646) {
		@Override
		public void handle(ItemOnNPCEvent e) {
			if(e.getItem().getId() == ShieldOfArrav.SHIELD_LEFT_HALF || e.getItem().getId() == ShieldOfArrav.SHIELD_RIGHT_HALF)
				e.getPlayer().startConversation(new MuseumCuratorArravD(e.getPlayer(), true).getStart());


		}
	};
}
