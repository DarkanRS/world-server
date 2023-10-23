package com.rs.game.content.world.areas.yanille.agility_dungeon;

import com.rs.game.content.bosses.tormenteddemon.TormentedDemon;
import com.rs.game.content.combat.CombatSpell;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

import java.util.List;

@PluginEventHandler
public class SalarinTheTwisted extends NPC {

    public SalarinTheTwisted(int id, Tile tile, boolean permaDeath) {
        super(id, tile, permaDeath);
    }

    @Override
    public void handlePreHit(final Hit hit) {
        super.handlePreHit(hit);
        CombatSpell spell = hit.getData("combatSpell", CombatSpell.class);
        if (hit.getDamage() == 0)
            return;
        if (spell == null) {
            forceTalk("Your pitiful attacks cannot hurt me!");
            hit.setDamage(0);
            return;
        }
        switch(spell) {
            case WIND_STRIKE -> hit.setDamage(90);
            case WATER_STRIKE -> hit.setDamage(100);
            case EARTH_STRIKE -> hit.setDamage(110);
            case FIRE_STRIKE -> hit.setDamage(120);
            default -> {
                forceTalk("Your pitiful attacks cannot hurt me!");
                hit.setDamage(0);
            }
        }
    }

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 205 }, (npcId, tile) -> new SalarinTheTwisted(npcId, tile, false));
}
