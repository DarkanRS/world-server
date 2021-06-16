package com.rs.game.player.controllers;

import com.rs.Settings;
import com.rs.game.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class CorporealBeastController extends Controller {

	public static ButtonClickHandler handleEnterWarning = new ButtonClickHandler(650) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 15) {
				e.getPlayer().stopAll();
				e.getPlayer().setNextWorldTile(new WorldTile(2974, 4384, e.getPlayer().getPlane()));
				e.getPlayer().getControllerManager().startController(new CorporealBeastController());
			} else if (e.getComponentId() == 16)
				e.getPlayer().closeInterfaces();
		}
	};

	@Override
	public void start() {

	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 37929 || object.getId() == 38811) {
			removeController();
			player.stopAll();
			player.setNextWorldTile(new WorldTile(2970, 4384, player.getPlane()));
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.sendMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					player.sendItemsOnDeath(null, false);
					player.reset();
					player.setNextWorldTile(new WorldTile(Settings.getConfig().getPlayerRespawnTile()));
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					removeController();
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

}
