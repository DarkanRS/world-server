package com.rs.game.content.miniquests.troll_warzone.npcs;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;

public class TrollGeneralCombat extends CombatScript {
    @Override
    public Object[] getKeys() {
        return new Object[] { 14991, 14992 };
    }

    @Override
    public int attack(NPC npc, Entity target) {
        if (npc.inMeleeRange(target)) {
            npc.anim(1932);
            delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 2, NPCCombatDefinitions.AttackStyle.MELEE, target)));
        } else {
            npc.sync(1933, 262);
            delayHit(npc, World.sendProjectile(npc, target, 295, 34, 16, 60, 2.0, 16, 0).getTaskDelay(), target, getRangeHit(npc, getMaxHit(npc, 2, NPCCombatDefinitions.AttackStyle.RANGE, target)));
        }
        return npc.getAttackSpeed();
    }
}
