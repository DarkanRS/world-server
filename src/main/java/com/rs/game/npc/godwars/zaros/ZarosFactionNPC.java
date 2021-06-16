package com.rs.game.npc.godwars.zaros;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.Bonus;
import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ZarosFactionNPC extends NPC {

	private static final int CAP_BONUS = 200;

	private static final Bonus[][] BONUSES = { { Bonus.STAB_DEF, Bonus.SLASH_DEF, Bonus.CRUSH_DEF }, { Bonus.RANGE_DEF }, {}, { Bonus.MAGIC_DEF } };

	public ZarosFactionNPC(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> targets = getPossibleTargets(true);
		ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
		for (Entity t : targets) {
			if (t instanceof ZarosFactionNPC || t instanceof Familiar || hasSuperiourBonuses(t))
				continue;
			targetsCleaned.add(t);
		}
		return targetsCleaned;
	}

	private boolean hasSuperiourBonuses(Entity t) {
		if (!(t instanceof Player))
			return false;
		Player player = (Player) t;
		for (Bonus bonus : BONUSES[getId() - 13456]) {
			if (player.getCombatDefinitions().getBonus(bonus) >= (bonus == Bonus.RANGE_DEF ? 100 : CAP_BONUS))
				return true;
		}
		return false;
	}

	public static boolean isNexArmour(String name) {
		return name.contains("pernix") || name.contains("torva") || name.contains("virtus") || name.contains("zaryte");
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(13456, 13457, 13458, 13459) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new ZarosFactionNPC(npcId, tile);
		}
	};
}