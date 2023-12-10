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
package com.rs.game.content.holidayevents.christmas.christ19;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ResourceImpD extends Conversation {

	private static final int IMP_HEAD = 9364;

	public static NPCClickHandler handleSnowImpTalk = new NPCClickHandler(new Object[] { 9372, 9373, 9374, 9375 }, e -> {
		String noun = "";
		int stage = 0;
		switch(e.getNPC().getId()) {
		case 9372:
			noun = "wine";
			stage = 5;
			break;
		case 9373:
			noun = "yule logs";
			stage = 9;
			break;
		case 9374:
			noun = "turkeys";
			stage = 7;
			break;
		case 9375:
			noun = "taters";
			stage = 3;
			break;
		}
		e.getPlayer().startConversation(new ResourceImpD(e.getPlayer(), noun, stage));
	});

	public ResourceImpD(Player player, String noun, int stage) {
		super(player);

		switch(player.getI(Christmas2019.STAGE_KEY, 0)) {
		case 2:
		case 4:
		case 6:
		case 8:
			addPlayer(HeadE.ANGRY, "Hey! Give those "+noun+" back!");
			addNPC(IMP_HEAD, HeadE.EVIL_LAUGH, "Tee hee hee! Why should I listen to you?");
			addNPC(IMP_HEAD, HeadE.ANGRY, "Because I'm wif him. Give him da "+noun+" back, Snowie's orders!");
			addNPC(IMP_HEAD, HeadE.SCARED, "Oh.. Rasmus.. Sorry, guv. I'll bring da "+noun+" back to da feast den.");
			addNPC(IMP_HEAD, HeadE.ANGRY, "You betta! Get moving dis instant!");
			addPlayer(HeadE.CHEERFUL, "Alright, we'll see you there.", () -> {
				player.save(Christmas2019.STAGE_KEY, stage);
				player.setChrist19Loc(null);
				PluginManager.handle(new EnterChunkEvent(player, player.getChunkId()));
			});
			break;
		default:
			addSimple("You've harassed the imp enough. Talk to Rasmus for the next location.");
			break;
		}

		create();
	}

}
