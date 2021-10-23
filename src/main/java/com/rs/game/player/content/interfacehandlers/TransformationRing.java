package com.rs.game.player.content.interfacehandlers;

import com.rs.game.player.Player;
import com.rs.game.player.managers.InterfaceManager.Tab;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class TransformationRing {
	
	public static ItemClickHandler handleItemOption = new ItemClickHandler(new Object[] { "Ring of stone", "Easter ring", "Bone brooch" }, new String[] { "Wear" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (e.getPlayer().inCombat(10000)) {
				e.getPlayer().sendMessage("You wouldn't want to use that right now.");
				return;
			}
			if (e.getItem().getName().equals("Ring of stone"))
				transformInto(e.getPlayer(), 2626);
			else if (e.getItem().getName().equals("Easter ring"))
				transformInto(e.getPlayer(), 3689 + Utils.random(5));
			else if (e.getItem().getName().equals("Bone brooch")) {
				e.getPlayer().stopAll(true, true, true);
				e.getPlayer().lock();
				e.getPlayer().setNextAnimation(new Animation(14870));
				e.getPlayer().setNextSpotAnim(new SpotAnim(2838));
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						transformInto(e.getPlayer(), 12373);
					}
				}, 1);
			}
		}
	};
	
	public static ButtonClickHandler handleDeactivationButton = new ButtonClickHandler(375) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 3)
				deactivateTransformation(e.getPlayer());
		}
	};

	public static void transformInto(Player player, int npcId) {
		player.stopAll(true, true, true);
		player.lock();
		player.getAppearance().transformIntoNPC(npcId);
		player.getInterfaceManager().sendTab(Tab.INVENTORY, 375);
		player.getTempAttribs().setB("TransformationRing", true);
	}

	public static void deactivateTransformation(Player player) {
		player.getTempAttribs().removeB("TransformationRing");
		player.unlock();
		player.setNextAnimation(new Animation(14884));
		player.getAppearance().transformIntoNPC(-1);
		player.getInterfaceManager().sendTab(Tab.INVENTORY);
	}
	
	public static void triggerDeactivation(Player player) {
		if (player.getTempAttribs().getB("TransformationRing"))
			deactivateTransformation(player);
	}

}
