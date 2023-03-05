package com.rs.game.content.world.areas.burthorpe.npcs.announcers;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;


@PluginEventHandler
public class WoundedSoldierWorld extends NPC {

	public WoundedSoldierWorld(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (Utils.random(50) == 0){
			CallForHelp();
		}
	}

	public void CallForHelp() {
		switch (Utils.random(1, 7)) {
			case 1 -> setNextForceTalk(new ForceTalk("My duodenum!"));
			case 2 -> setNextForceTalk(new ForceTalk("My spleen!"));
			case 4 -> setNextForceTalk(new ForceTalk("Medic!"));
			case 5 -> setNextForceTalk(new ForceTalk("Gods, all my organs hurt!"));
			case 6 -> setNextForceTalk(new ForceTalk("So much pain!"));
			case 7 -> setNextForceTalk(new ForceTalk("Urgh... my pancreas!"));
		}
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 15019,15021,15022,15023,15024,15025,15026,15027,15028,15029,15030,15031,15033 }, (npcId, tile) -> new WoundedSoldierWorld(npcId, tile));
}
