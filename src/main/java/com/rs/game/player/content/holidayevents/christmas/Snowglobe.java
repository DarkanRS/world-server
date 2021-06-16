package com.rs.game.player.content.holidayevents.christmas;

import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Snowglobe {
	
	public static ItemClickHandler handle = new ItemClickHandler(11949) {
		@Override
		public void handle(ItemClickEvent e) {
			e.getPlayer().setNextAnimation(new Animation(2926));
			e.getPlayer().startConversation(new Dialogue().addNext(new Dialogue(() -> {
				e.getPlayer().getInterfaceManager().sendInterface(659);
				e.getPlayer().setCloseInterfacesEvent(() -> {
					e.getPlayer().setNextAnimation(new Animation(7538));
				});
			})).addNext(new Dialogue(() -> {
				e.getPlayer().closeInterfaces();
				e.getPlayer().setNextAnimation(new Animation(7528));
				e.getPlayer().setNextSpotAnim(new SpotAnim(1284));
				e.getPlayer().getInventory().addItem(11951, e.getPlayer().getInventory().getFreeSlots());
			})));
		}
	};
}
