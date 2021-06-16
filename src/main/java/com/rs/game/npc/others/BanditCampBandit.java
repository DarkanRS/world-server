package com.rs.game.npc.others;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.npc.NPC;
import com.rs.game.npc.godwars.saradomin.SaradominFactionNPC;
import com.rs.game.npc.godwars.zammorak.ZamorakFactionNPC;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class BanditCampBandit extends NPC {

	public BanditCampBandit(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setForceAgressive(true); // to ignore combat lvl
		setIgnoreDocile(true);
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> targets = super.getPossibleTargets();
		ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
		for (Entity t : targets) {
			if (!(t instanceof Player) || (!ZamorakFactionNPC.hasGodItem((Player) t) && !SaradominFactionNPC.hasGodItem((Player) t)))
				continue;
			targetsCleaned.add(t);
		}
		return targetsCleaned;
	}

	@Override
	public void setTarget(Entity entity) {
		if (entity instanceof Player && (ZamorakFactionNPC.hasGodItem((Player) entity) || SaradominFactionNPC.hasGodItem((Player) entity)))
			setNextForceTalk(new ForceTalk(ZamorakFactionNPC.hasGodItem((Player) entity) ? "Prepare to suffer, Zamorakian scum!" : "Time to die, Saradominist filth!"));
		super.setTarget(entity);
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1926, 1931) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new BanditCampBandit(npcId, tile, false);
		}
	};

}
