package com.rs.game.content.skills.summoning;

import com.rs.game.World;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class ObeliskGlobal {

	public static ObjectClickHandler rechargeSummoning = new ObjectClickHandler(new Object[] { "Obelisk" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getOption()) {
			case "Renew-points" -> {
				if (e.getPlayer().getSkills().getLevel(Constants.SUMMONING) < e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING)) {
					e.getPlayer().sendMessage("You touch the obelisk", true);
					e.getPlayer().setNextAnimation(new Animation(8502));
					World.sendSpotAnim(null, new SpotAnim(1308), e.getObject().getTile());
					WorldTasks.schedule(2, () -> {
						e.getPlayer().getSkills().set(Constants.SUMMONING, e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING));
						e.getPlayer().sendMessage("...and recharge your summoning points.", true);
					});
				}
			}
			}
		}
	};
	
}
