package com.rs.game.content.dialogues_matrix;
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
//package com.rs.game.player.dialogues;
//
//import com.rs.cache.loaders.NPCDefinitions;
//import com.rs.game.npc.NPC;
//import com.rs.game.player.content.skills.herblore.BobBarter;
//
///**
// *
// * @author Tyler
// *
// */
//public class BobBarterD extends Dialogue {
//	NPC npc;
//
//	@Override
//	public void start() {
//		npc = (NPC) parameters[0];
//		sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npc.getId()).getName(), "Good day, How can I help you?" }, IS_NPC, npc.getId(), 9827);
//
//	}
//
//	@Override
//	public void run(int interfaceId, int componentId) {
//		if (stage == -1) {
//			stage = 0;
//			sendOptionsDialogue("What would you like to say?", "Decant my potions please.", "Nevermind.");
//		} else if (stage == 0) {
//			if (componentId == OPTION_1) {
//				BobBarter decanting = new BobBarter(player);
//				decanting.decant();
//			} else {
//				end();
//			}
//
//		}
//
//	}
//
//	@Override
//	public void finish() {
//
//	}
//
//}