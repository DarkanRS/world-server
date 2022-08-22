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
package com.rs.game.content.quests.handlers.princealirescue;

import static com.rs.game.content.quests.handlers.princealirescue.PrinceAliRescue.JAIL_REGION_ID;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class PrinceAliPrinceAliRescueD extends Conversation {
	Player p;
	public final static int PRINCE_ALI = 920;
	public final static int PRINCE_ALI2 = 921;

	//items
	public final static int BEER = 1917;
	public final static int PINK_SKIRT = 1013;

	//key stuff
	public final static int SOFT_CLAY = 1761;
	public final static int BRONZE_KEY = 2418;
	public final static int KEY_PRINT = 2423;
	public final static int BRONZE_BAR = 2349;

	//wig items
	public final static int BALL_WOOL = 1759;
	public final static int WIG = 2421;
	public final static int BLONDE_WIG = 2419;
	public final static int YELLOW_DYE = 1765;

	//Paste items
	public final static int ASHES = 592;
	public final static int REDBERRY = 1951;
	public final static int POT_OF_FLOUR = 1933;
	public final static int WATER_BUCKET = 1929;
	public final static int PASTE = 2424;

	public final static int ROPE = 954;

	public PrinceAliPrinceAliRescueD(Player p) {
		super(p);
		this.p = p;

		if(p.isQuestComplete(Quest.PRINCE_ALI_RESCUE))
			addNPC(PRINCE_ALI, HeadE.CALM_TALK, "I owe you my life for that escape. You cannot help me this time, they know who you are. Go in peace, " +
					"friend of Al-Kharid.");
		else if(p.getInventory().containsItem(PASTE, 1) && p.getInventory().containsItem(BLONDE_WIG, 1) &&
				p.getInventory().containsItem(PINK_SKIRT, 1)){
			addPlayer(HeadE.SECRETIVE, "Prince, I come to rescue you.");
			addNPC(PRINCE_ALI, HeadE.HAPPY_TALKING, "That is very very kind of you, how do I get out?");
			addPlayer(HeadE.SECRETIVE, "With a disguise. I have removed the Lady Keli. She is tied up, but will not stay tied up for long.");
			addPlayer(HeadE.SECRETIVE, "Take this disguise, and this key.");
			addSimple("You hand over the disguise and key over to Prince Ali.", ()-> {
				p.getInventory().removeItems(new Item(PASTE, 1), new Item(PINK_SKIRT, 1), new Item(BLONDE_WIG, 1));
				for(NPC npc : World.getNPCsInRegion(JAIL_REGION_ID))
					if(npc.getId() == PRINCE_ALI)
						WorldTasks.schedule(new WorldTask() {//deletes all ali2s when region is loaded.
							int tick;
							NPC ali2;
							@Override
							public void run() {
								if(tick == 0) {
									npc.setRespawnTask(50);
									WorldTile tile = npc.getTile();
									npc.finish();
									ali2 = World.spawnNPC(PRINCE_ALI2, tile, -1, false, true);
								}
								if(tick == 10) {
									ali2.finish();
									for(NPC npc : World.getNPCsInRegion(JAIL_REGION_ID))
										if(npc.getId() == PRINCE_ALI2)
											npc.finish();
								}
								if(tick == 11)
									stop();

								if(World.isRegionLoaded(JAIL_REGION_ID))
									tick++;
							}
						}, 0, 1);
			});
			addNPC(PRINCE_ALI2, HeadE.HAPPY_TALKING, "Thank you my friend, I must leave you now. My father will pay you well for this.");
			addSimple("The prince has escaped, well done! You are now a friend of Al-Kharid and may pass through the Al-Kharid toll gate for free.", ()->{
				p.getQuestManager().completeQuest(Quest.PRINCE_ALI_RESCUE);
			});
		}
		else {
			addNPC(PRINCE_ALI, HeadE.CALM_TALK, "Hello.");
			addPlayer(HeadE.SAD, "Darn I don't have all I need for the disguise");
		}


	}

	public static NPCClickHandler handlePrinceAli = new NPCClickHandler(new Object[] { PRINCE_ALI }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new PrinceAliPrinceAliRescueD(e.getPlayer()).getStart());
		}
	};
}

