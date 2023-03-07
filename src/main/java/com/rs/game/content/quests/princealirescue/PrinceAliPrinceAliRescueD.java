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
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class PrinceAliPrinceAliRescueD extends Conversation {
	public final static int PRINCE_ALI = 920;
	public final static int PRINCE_ALI2 = 921;

	//items
	public final static int PINK_SKIRT = 1013;
	public final static int BLONDE_WIG = 2419;
	public final static int PASTE = 2424;

	public PrinceAliPrinceAliRescueD(Player player) {
		super(player);
		if(player.isQuestComplete(Quest.PRINCE_ALI_RESCUE))
			addNPC(PRINCE_ALI, HeadE.CALM_TALK, "I owe you my life for that escape. You cannot help me this time, they know who you are. Go in peace, " +
					"friend of Al-Kharid.");
		else if(player.getInventory().containsItem(PASTE, 1) && player.getInventory().containsItem(BLONDE_WIG, 1) &&
				player.getInventory().containsItem(PINK_SKIRT, 1)){
			addPlayer(HeadE.SECRETIVE, "Prince, I come to rescue you.");
			addNPC(PRINCE_ALI, HeadE.HAPPY_TALKING, "That is very very kind of you, how do I get out?");
			addPlayer(HeadE.SECRETIVE, "With a disguise. I have removed the Lady Keli. She is tied up, but will not stay tied up for long.");
			addPlayer(HeadE.SECRETIVE, "Take this disguise, and this key.");
			addSimple("You hand over the disguise and key over to Prince Ali.", ()-> {
				player.getInventory().removeItems(new Item(PASTE, 1), new Item(PINK_SKIRT, 1), new Item(BLONDE_WIG, 1));
				//TODO update this to new logic what on earth even
//				for(NPC npc : World.getNPCsInRegion(JAIL_REGION_ID))
//					if(npc.getId() == PRINCE_ALI)
//						WorldTasks.schedule(new WorldTask() {//deletes all ali2s when region is loaded.
//							int tick;
//							NPC ali2;
//							@Override
//							public void run() {
//								if(tick == 0) {
//									npc.setRespawnTask(50);
//									Tile tile = npc.getTile();
//									npc.finish();
//									ali2 = World.spawnNPC(PRINCE_ALI2, tile, -1, false, true);
//								}
//								if(tick == 10) {
//									ali2.finish();
//									for(NPC npc : World.getNPCsInRegion(JAIL_REGION_ID))
//										if(npc.getId() == PRINCE_ALI2)
//											npc.finish();
//								}
//								if(tick == 11)
//									stop();
//
//								if(World.isRegionLoaded(JAIL_REGION_ID))
//									tick++;
//							}
//						}, 0, 1);
			});
			addNPC(PRINCE_ALI2, HeadE.HAPPY_TALKING, "Thank you my friend, I must leave you now. My father will pay you well for this.");
			addSimple("The prince has escaped, well done! You are now a friend of Al-Kharid and may pass through the Al-Kharid toll gate for free.", ()->{
				player.getQuestManager().completeQuest(Quest.PRINCE_ALI_RESCUE);
			});
		}
		else {
			addNPC(PRINCE_ALI, HeadE.CALM_TALK, "Hello.");
			addPlayer(HeadE.SAD, "Darn I don't have all I need for the disguise");
		}


	}

	public static NPCClickHandler handlePrinceAli = new NPCClickHandler(new Object[] { PRINCE_ALI }, e -> e.getPlayer().startConversation(new PrinceAliPrinceAliRescueD(e.getPlayer()).getStart()));
}

