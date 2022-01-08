package com.rs.game.player.content.skills;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.XPGainEvent;
import com.rs.plugin.handlers.XPGainHandler;

@PluginEventHandler
public class Prestige {
    public static XPGainHandler handleShenanigans = new XPGainHandler() {
        @Override
        public void handle(XPGainEvent e) {
            Player p = e.getPlayer();
            int skill = e.getSkillId();
            if(p.getSkills().getXp(skill) >= 199_999_900) {
                p.getSkills().setNoPrestige(skill, 120);
                p.getSkills().setXpTo120(skill);
                p.getSavingAttributes().put(Skills.SKILL_NAME[skill], p.getI(Skills.SKILL_NAME[skill]) + 10);
            }
        }
    };
}
