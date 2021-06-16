package com.rs.game.player.content.world.regions;

import com.rs.game.player.Player;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Yanille {
	
	public static ObjectClickHandler handleUnderwallTunnelShortcut = new ObjectClickHandler(new Object[] { 9301, 9302 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 14))
				return;
			AgilityShortcuts.crawlUnder(e.getPlayer(), e.getPlayer().transform(0, e.getObjectId() == 9302 ? -5 : 5, 0));
		}
	};
	
	public static ObjectClickHandler handleMagicGuildStairs = new ObjectClickHandler(new Object[] { 1722, 1723 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getPlayer().transform(0, e.getObjectId() == 1722 ? 4 : -4, e.getObjectId() == 1722 ? 1 : -1));
		}
	};
	
	public static ObjectClickHandler handleTreeGnomeVillageGateSqueeze = new ObjectClickHandler(new Object[] { 2186 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			AgilityShortcuts.sidestep(e.getPlayer(), e.getPlayer().getY() < e.getObject().getY() ? e.getPlayer().transform(0, 1, 0) : e.getPlayer().transform(0, -1, 0));
		}
	};
	
	public static NPCClickHandler handleMagicGuildArmorShop = new NPCClickHandler(1658) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 3) {
				ShopsHandler.openShop(e.getPlayer(), "magic_guild_store_robes");
			}
		}
	};
	
	public static NPCClickHandler handleElkoy = new NPCClickHandler(473, 474) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 3) {
				e.getPlayer().fadeScreen(() -> {
					e.getPlayer().sendMessage("Elkoy leads you through the maze...");
					e.getPlayer().setNextWorldTile(e.getNPC().getId() == 473 ? new WorldTile(2515, 3160, 0) : new WorldTile(2502, 3193, 0));
				});
			}
		}
	};
	
	public static ObjectClickHandler handleMagicGuildPortals = new ObjectClickHandler(new Object[] { 2518, 2156, 2157 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getObjectId()) {
			case 2518:
				e.getPlayer().sendOptionDialogue("Teleport to Thormac's Tower?", new String[] { "Yes, teleport me to Thormac's Tower.", "Not right now." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(new WorldTile(2702, 3403, 0));
					}
				});
				break;
			case 2156:
				e.getPlayer().sendOptionDialogue("Teleport to the Wizard's Tower?", new String[] { "Yes, teleport me to the Wizard's Tower.", "Not right now." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(new WorldTile(3109, 3164, 0));
					}
				});
				break;
			case 2157:
				e.getPlayer().sendOptionDialogue("Teleport to the Dark Wizard's Tower?", new String[] { "Yes, teleport me to the Dark Wizard's Tower.", "Not right now." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(new WorldTile(2906, 3334, 0));
					}
				});
				break;
			}
		}
	};
	
	public static ObjectClickHandler handleMagicGuildEnter = new ObjectClickHandler(new Object[] { "Magic guild door" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSkills().getLevel(Constants.MAGIC) < 66) {
				e.getPlayer().sendMessage("You require 66 magic to enter the Magic Guild.");
				return;
			}
			Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
		}
	};
	
	public static ObjectClickHandler handleGrottoBridge = new ObjectClickHandler(new Object[] { 2830, 2831 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			WorldTile endLoc;
			if (e.getObjectId() == 2830)
				endLoc = new WorldTile(2530, 3029, 0);
			else
				endLoc = new WorldTile(2531, 3026, 0);

			e.getPlayer().lock();
			e.getPlayer().setNextFaceWorldTile(endLoc);
			e.getPlayer().setNextAnimation(new Animation(769));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().unlockNextTick();
					e.getPlayer().setNextWorldTile(endLoc);
					e.getPlayer().setNextAnimation(new Animation(-1));
				}
			}, 0);
		}
	};
}
