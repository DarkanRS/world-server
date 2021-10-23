package com.rs.game.player.content.holidayevents.christmas.christ20;

import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.utils.spawns.NPCSpawn;
import com.rs.utils.spawns.NPCSpawns;

@PluginEventHandler
public class Christmas2020 {
	
	public static final String STAGE_KEY = "christ2022";
	
	private static boolean ACTIVE = false;

	@ServerStartupEvent
	public static void load() {
		if (!ACTIVE)
			return;
		NPCSpawns.add(new NPCSpawn(9398, new WorldTile(2655, 5678, 0), "Queen of Snow"));
		NPCSpawns.add(new NPCSpawn(9400, new WorldTile(2654, 5679, 0), "Santa"));
	}
	
	public static LoginHandler login = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if (!ACTIVE)
				return;
			e.getPlayer().getVars().setVarBit(6934, 1);
		}
	};
	
	public static ItemClickHandler handleYoyo = new ItemClickHandler(new Object[] { 4079 }, new String[] { "Play", "Loop", "Walk", "Crazy" }) {

		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Play":
				e.getPlayer().setNextAnimation(new Animation(1457));
				break;
			case "Loop":
				e.getPlayer().setNextAnimation(new Animation(1458));
				break;
			case "Walk":
				e.getPlayer().setNextAnimation(new Animation(1459));
				break;
			case "Crazy":
				e.getPlayer().setNextAnimation(new Animation(1460));
				break;
			}
		}
		
	};
	
}
