package com.rs.game.player.content.skills;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.events.XPGainEvent;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.XPGainHandler;

@PluginEventHandler
public class Prestige {
    public static XPGainHandler handleExperiment1 = new XPGainHandler() {
        @Override
        public void handle(XPGainEvent e) {
            Player p = e.getPlayer();
            int skill = e.getSkillId();
            if(p.getSkills().is120(skill)) {
                p.getSkills().set(skill, 99);
                p.getSkills().setXpTo99(skill);
                p.getSavingAttributes().put(Skills.SKILL_NAME[skill], p.getI(Skills.SKILL_NAME[skill]) + 20);
            }
        }
    };
}
