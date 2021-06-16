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