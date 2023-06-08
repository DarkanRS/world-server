package com.rs.game.content.world.areas.falador.npcs.announcers;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;


@PluginEventHandler
public class HeraldOfFaladorWorld extends NPC {

	public HeraldOfFaladorWorld(int id, Tile tile) {
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
			case 1 -> setNextForceTalk(new ForceTalk("Falador, the indestructible city!"));
			case 2 -> setNextForceTalk(new ForceTalk("Falador capes...99% backstab proof!"));
			case 3 -> setNextForceTalk(new ForceTalk("May Falador be your sword on your travels!"));
			case 4 -> setNextForceTalk(new ForceTalk("Welcome to Falador...We'll try not to beat up you too badly."));
			case 5 -> setNextForceTalk(new ForceTalk("I think I am getting splinters."));

		}
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 13939 }, (npcId, tile) -> new HeraldOfFaladorWorld(npcId, tile));
}
