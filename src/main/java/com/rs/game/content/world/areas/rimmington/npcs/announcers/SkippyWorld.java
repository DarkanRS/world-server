package com.rs.game.content.world.areas.rimmington.npcs.announcers;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;


@PluginEventHandler
public class SkippyWorld extends NPC {

	public SkippyWorld(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (Utils.random(50) == 0){
			Shout();
		}
	}

	public void Shout() {
		switch (Utils.random(1, 6)) {
			case 1 -> setNextForceTalk(new ForceTalk("I've got a bottle with your name on it!"));
			case 2 -> setNextForceTalk(new ForceTalk("Mudskippers, thousands of them!"));
			case 3 -> setNextForceTalk(new ForceTalk("They're coming out of the walls!"));
			case 4 -> setNextForceTalk(new ForceTalk("The horror... The horror..."));
			case 5 -> setNextForceTalk(new ForceTalk("I'll get you, I'll get you all!"));
			case 6 -> setNextForceTalk(new ForceTalk("Take this!"));
		}
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 2795 }, (npcId, tile) -> new SkippyWorld(npcId, tile));
}
