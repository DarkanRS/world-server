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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.dialogues;

import com.rs.game.player.controllers.FightKilnController;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.utils.Ticks;

public class TokHaarHok extends Dialogue {

	private int npcId;
	private int type;
	private FightKilnController fightKiln;

	// / So...you accept our challenge. Let our sport be glorious. Xill -
	// attack!
	@Override
	public void start() {
		type = (Integer) parameters[0];
		npcId = (Integer) parameters[1];
		fightKiln = (FightKilnController) parameters[2];
		if (type == 0)
			sendNPCDialogue(npcId, 9827, "Let us talk...");
		else if (type == 1)
			sendNPCDialogue(npcId, 9827, "So...you accept our challenge. Let our sport be glorious. Xill - attack!");
		else if (type == 2)
			sendNPCDialogue(npcId, 9827, "Well fought, " + player.getDisplayName() + ". You are ferocious, but you must fight faster... The lava is rising.");
		else if (type == 3)
			sendNPCDialogue(npcId, 9827, "You must be carved from a rock inpervious to magic... You are quite the worthy foe.");
		else if (type == 4)
			sendNPCDialogue(npcId, 9827, "Hurry, " + player.getDisplayName() + "... Kill my brothers before the lava consumes you.");
		else if (type == 7)
			sendNPCDialogue(npcId, 9827, "Amazing! We haven't had such fun in such a long time. But now, the real challenge begins...");
		else if (type == 5)
			sendNPCDialogue(npcId, 9827, "We have thrown many waves at you... You have handled yourself like a true Tokhaar. You have earned our respect.");
		else if (type == 6)
			sendNPCDialogue(npcId, 9827, "You are a Tokhaar... born in a human's body. Truly, we have not seen such skill from anyone out of our kiln.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (type) {
		case 0: // TODO
			switch (stage) {
			case -1:
				stage = 0;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Let's fight.", "I'd like to speak more about you and your kind.");
				break;
			case 0:
				switch (componentId) {
				case OPTION_1:
					stage = 1;
					sendNPCDialogue(npcId, 9827, "Do you have any questions on the rules of our engagement?");
					break;
				case OPTION_2:
				default:

					break;
				}
				break;
			case 1:
				stage = 2;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "No, let's just fight.", "What do I get if I beat you?", "What are the rules?");
				break;
			case 2:
				switch (componentId) {
				case OPTION_1:
					stage = 3;
					sendPlayerDialogue(9827, "No let's just fight.");
					break;
				case OPTION_2:
					break;
				case OPTION_3:
				default:
					break;
				}
				break;
			case 3:
				fightKiln.removeTokHaarTok();
				fightKiln.nextWave();
				end();
				break;
			}
			break;
		case 1:
			end();
			break;
		case 2:
			switch (stage) {
			case -1:
				stage = 0;
				player.getInterfaceManager().closeChatBoxInterface();
				WorldTile lookTo = fightKiln.getWorldTile(37, 50);
				player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 1000);
				WorldTile posTile = fightKiln.getWorldTile(37, 45);
				player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 3000);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						try {
							sendNPCDialogue(npcId, 9827, "Our Mej wish to test you...");
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, Ticks.fromSeconds(3));
				break;
			case 0:
				end();
				break;
			}
			break;
		case 3:
			switch (stage) {
			case -1:
				stage = 0;
				sendNPCDialogue(npcId, 9827, "Ah, the platform is crumbling. Be quick little one - our Ket are comming.");
				break;
			case 0:
				end();
				break;
			}
			break;
		case 4:
			end();
			break;
		case 7:
			switch (stage) {
			case -1:
				stage = 0;
				sendPlayerDialogue(9810, "The real challenge?");
				break;
			case 0:
				stage = 1;
				sendNPCDialogue(npcId, 9827, "Many creatures have entered the kiln over the ages. We remember their shapes.");
				break;
			case 1:
				end();
				break;
			}
			break;
		case 5:
			switch (stage) {
			case -1:
				stage = 0;
				sendNPCDialogue(npcId, 9827, "	Take this cape as a symbol of our -");
				break;
			case 0:
				stage = 1;
				fightKiln.showHarAken();
				player.getInterfaceManager().closeChatBoxInterface();
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						try {
							sendNPCDialogue(npcId, 9827, "Ah - yes, there is one final challenge...");
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, Ticks.fromSeconds(3));
				break;
			case 1:
				end();
				fightKiln.hideHarAken();
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						try {
							fightKiln.removeScene();
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, Ticks.fromSeconds(3));
				break;
			}
			break;

		case 6:
			switch (stage) {
			case -1:
				stage = 0;
				sendNPCDialogue(npcId, 9827, "You have done very well. To mark your triumph, accept a trophy from our home.");
				break;
			case 0:
				stage = 1;
				sendOptionsDialogue("Choose your reward:", "The TokHaar-Kal", "An uncut onyx");
				break;
			case 1:
				if (componentId == OPTION_1) {
					stage = 2;
					sendNPCDialogue(npcId, 9827, "The TokHaar-Kal is a powerful cape that will let others see that you have mastered the Fight Kiln. In addition to this, it provides several stat boosts including 8+ strength.");
				} else {
					stage = 3;
					sendNPCDialogue(npcId, 9827, "Onyx is a precious and rare gem that can be crafted into one of several powerful objects, including the coveted Amulet of Fury.");
				}
				break;
			case 2:
			case 3:
				stage = (byte) (stage == 2 ? 4 : 5);
				sendOptionsDialogue("Accept the " + (stage == 4 ? "TokHaar-Kal" : "uncut onyx") + "?", "Yes.", "No.");
				break;
			case 4:
			case 5:
				if (componentId == OPTION_1) {
					player.getTempAttribs().setI("FightKilnReward", stage == 4 ? 0 : 1);
					stage = 6;
					sendNPCDialogue(npcId, 9827, "Let us test our strength again...soon...");
				} else {
					stage = 1;
					sendOptionsDialogue("Choose your reward:", "The TokHaar-Kal", "An uncut onyx");
				}
				break;
			case 6:
				stage = 7;
				sendNPCDialogue(npcId, 9827, "Now,leave...before the lava consumes you.");
				break;
			case 7:
				end();
				break;
			}
			break;
		}

	}

	@Override
	public void finish() {
		if (type == 5)
			fightKiln.unlockPlayer();
		else if (type != 0)
			fightKiln.removeScene();

	}

}
