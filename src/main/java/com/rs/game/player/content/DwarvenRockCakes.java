package com.rs.game.player.content;

import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class DwarvenRockCakes {
	
	public static ItemClickHandler rockCakeClick = new ItemClickHandler(7509, 7510) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!e.getOption().contains("Eat"))
				return;

			if (e.getItem().getId() == 7509 && e.getPlayer().getHitpoints() > 20) {
				Hit h = new Hit(20, HitLook.TRUE_DAMAGE);
				e.getPlayer().removeHitpoints(h);
				e.getPlayer().fakeHit(h);
				e.getPlayer().setNextForceTalk(new ForceTalk("Ow! Ow! That's hot!"));
			} else if (e.getItem().getId() == 7510 && e.getPlayer().getHitpoints() > 100) {
				Hit h = new Hit(100, HitLook.TRUE_DAMAGE);
				e.getPlayer().removeHitpoints(h);
				e.getPlayer().fakeHit(h);
				e.getPlayer().setNextForceTalk(new ForceTalk("Ow! I nearly broke a tooth!"));
			}
		}
	};
	
	public static NPCClickHandler clickRohak = new NPCClickHandler("Rohak") {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Dialogue().addOptions(new Options() {
				@Override
				public void create() {
					option("Hot Dwarven Rock Cake", () -> e.getPlayer().getInventory().addItemDrop(7509, 1));
					option("Cool Dwarven Rock Cake", () -> e.getPlayer().getInventory().addItemDrop(7510, 1));
				}
			}));
		}
	};
}
