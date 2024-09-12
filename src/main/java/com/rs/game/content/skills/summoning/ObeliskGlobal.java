package com.rs.game.content.skills.summoning;

import com.rs.game.World;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class ObeliskGlobal {

	public static ObjectClickHandler rechargeSummoning = new ObjectClickHandler(new Object[] { "Obelisk" }, e -> {
		switch(e.getOption()) {
		case "Renew-points" -> {
			if (e.getPlayer().getSkills().getLevel(Constants.SUMMONING) < e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING)) {
				e.getPlayer().sendMessage("You touch the obelisk", true);
				e.getPlayer().anim(8502);
				World.sendSpotAnim(e.getObject().getTile(), new SpotAnim(1308));
				WorldTasks.schedule(2, () -> {
					e.getPlayer().getSkills().set(Constants.SUMMONING, e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING));
					e.getPlayer().sendMessage("...and recharge your summoning points.", true);
				});
			}
		}
		}
	});

	public static ObjectClickHandler handleSmallObelisk = new ObjectClickHandler(new Object[] { "Small obelisk" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Renew-points")) {
			int summonLevel = e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING);
			if (e.getPlayer().getSkills().getLevel(Constants.SUMMONING) < summonLevel) {
				e.getPlayer().lock(3);
				e.getPlayer().anim(8502);
				e.getPlayer().getSkills().set(Constants.SUMMONING, summonLevel);
				e.getPlayer().sendMessage("You have recharged your Summoning points.", true);
			} else {
				e.getPlayer().sendMessage("You already have full Summoning points.");
			}
		}
	}
	);

}
