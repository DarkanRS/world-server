package com.rs.game.player.content.world.regions.dungeons;

import com.rs.game.player.content.skills.slayer.TaskMonster;
import com.rs.game.player.controllers.KuradalDungeonController;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class KuradalsDungeon {
	
	public static ObjectClickHandler handleBarriers = new ObjectClickHandler(new Object[] { 47236 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getRotation() == 2) {
				e.getPlayer().walkOneStep(e.getPlayer().getX() > e.getObject().getX() ? -1 : 1, 0, false);
			} else {
				e.getPlayer().walkOneStep(0, e.getPlayer().getY() == e.getObject().getY() ? -1 : 1, false);
			}
		}
	};
	
	public static ObjectClickHandler handleEntrances = new ObjectClickHandler(new Object[] { 47232 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSlayer().getTask() != null) {
				TaskMonster currentTask = e.getPlayer().getSlayer().getTask().getMonster();
				switch (currentTask) {
					case HELLHOUNDS:
					case GREATER_DEMONS:
					case BLUE_DRAGONS:
					case GARGOYLES:
					case ABYSSAL_DEMONS:
					case DARK_BEASTS:
					case IRON_DRAGONS:
					case STEEL_DRAGONS:
						e.getPlayer().getControllerManager().startController(new KuradalDungeonController());
						return;
					default:
				}
			}
			e.getPlayer().sendMessage("Sorry, this dungeon is exclusive only to those who need to go in there.");
		}
	};
}
