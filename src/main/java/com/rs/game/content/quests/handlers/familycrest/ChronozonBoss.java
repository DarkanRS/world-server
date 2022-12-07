package com.rs.game.content.quests.handlers.familycrest;

import static com.rs.game.content.quests.handlers.familycrest.FamilyCrest.JOHNATHAN_CREST;
import static com.rs.game.content.quests.handlers.familycrest.FamilyCrest.KILL_CHRONOZON;

import com.rs.game.World;
import com.rs.game.content.combat.CombatSpell;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ChronozonBoss extends NPC {
	boolean hitWater = false;
	boolean hitEarth = false;
	boolean hitFire = false;
	boolean hitAir = false;

	public ChronozonBoss(int id, WorldTile tile) {
		super(id, tile, false);
	}

	@Override
	public void sendDeath(Entity source) {
		if(canDie()) {
            hitWater = false;
            hitEarth = false;
            hitFire = false;
            hitAir = false;
            super.sendDeath(source);
        }
		else {
			forceTalk("Hahaha, you cannot defeat me!");
			setHitpoints(getMaxHitpoints());
		}
	}

	@Override
	public void handlePreHit(final Hit hit) {
		if(hit.getSource() instanceof Player) {
			Player p = (Player)hit.getSource();
			if(!hit.missed() && hit.getData("combatSpell") != null)
				if(!hitFire && hit.getData("combatSpell", CombatSpell.class).isFireBlast()) {
					p.sendMessage("Chronozon weakens...");
					hitFire = true;
				}
				else if(!hitWater && hit.getData("combatSpell", CombatSpell.class).isWaterBlast()) {
					p.sendMessage("Chronozon weakens...");
					hitWater = true;
				}
				else if(!hitAir && hit.getData("combatSpell", CombatSpell.class).isAirBlast()) {
					p.sendMessage("Chronozon weakens...");
					hitAir = true;
				}
				else if(!hitEarth && hit.getData("combatSpell", CombatSpell.class).isEarthBlast()) {
					p.sendMessage("Chronozon weakens...");
					hitEarth = true;
				}
			super.handlePreHit(hit);
		}
	}

	private boolean canDie() {
		return (hitAir && hitEarth && hitFire && hitWater);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(667) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new ChronozonBoss(npcId, tile);
		}
	};

	public static NPCDeathHandler handleChronozonDeath = new NPCDeathHandler(667) {
		@Override
		public void handle(NPCDeathEvent e) {
			if(e.getKiller() instanceof Player) {
				Player p = (Player)e.getKiller();
				if(p.getQuestManager().getStage(Quest.FAMILY_CREST) == KILL_CHRONOZON && !p.getInventory().containsItem(JOHNATHAN_CREST))
					World.addGroundItem(new Item(JOHNATHAN_CREST, 1), WorldTile.of(e.getNPC().getTile()), p);
			}
		}
	};


}
