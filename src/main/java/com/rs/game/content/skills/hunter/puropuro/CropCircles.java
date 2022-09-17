package com.rs.game.content.skills.hunter.puropuro;

import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class CropCircles {
	
	public enum CropCircle {
		ARDOUGNE(new WorldTile(2647, 3347, 0)),
		BRIMHAVEN(new WorldTile(2808, 3200, 0)),
		CATHERBY(new WorldTile(2818, 3470, 0)),
		DRAYNOR_VILLAGE(new WorldTile(3115, 3272, 0)),
		HARMONY_ISLAND(new WorldTile(3810, 2852, 0)),
		LUMBRIDGE(new WorldTile(3160, 3298, 0)),
		MISCELLANIA(new WorldTile(2538, 3845, 0)),
		MOS_LE_HARMLESS(new WorldTile(3697, 3025, 0)),
		RIMMINGTON(new WorldTile(2979, 3216, 0)),
		DORICS_HOUSE(new WorldTile(2953, 3444, 0)),
		COOKS_GUILD(new WorldTile(3141, 3461, 0)),
		CHAMPIONS_GUILD(new WorldTile(3212, 3345, 0)),
		TAVERLEY(new WorldTile(2893, 3398, 0)),
		TREE_GNOME_STRONGHOLD(new WorldTile(2435, 3472, 0)),
		YANILLE(new WorldTile(2582, 3104, 0));

		private final WorldTile entranceTile;

		CropCircle(WorldTile tile) {
			this.entranceTile = tile;
		}

		public WorldTile getEntranceTile() {
			return entranceTile;
		}

		public int getX() {
			return entranceTile.getX();
		}

		public int getY() {
			return entranceTile.getY();
		}

		public int getPlane() {
			return entranceTile.getPlane();
		}
	}

	public static ObjectClickHandler teleportToPuroPuro = new ObjectClickHandler(new Object[] { 24988, 24991 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!e.isAtObject())
				return;
			WorldTasks.schedule(10, new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().getControllerManager().startController(new PuroPuroController(e.getObject()));
					if (e.getObjectId() == 24988) {
						e.getPlayer().addEffect(Effect.FARMERS_AFFINITY, 3000);
						e.getPlayer().sendMessage("You feel the magic of the crop circle grant you a Farmer's affinity.");
					}
				}
			});
			Magic.sendTeleportSpell(e.getPlayer(), 6601, -1, 1118, -1, 0, 0, new WorldTile(2590 + Utils.randomInclusive(0, 3), 4318 + Utils.randomInclusive(0, 3), 0), 9, false, Magic.OBJECT_TELEPORT, null);
		}
	};

	private static CropCircle locationOne = null;
	private static CropCircle locationTwo = null;

	@ServerStartupEvent
	public static void initCropCircles() {
		WorldTasks.schedule(0, 3000, () -> {
			int random = Utils.random(0, CropCircle.values().length);
			int random2 = Utils.random(0, CropCircle.values().length);
			
			while (random2 == random)
				random2 = Utils.random(0, CropCircle.values().length);
			
			locationOne = CropCircle.values()[random];
			locationTwo = CropCircle.values()[random2];

			NPC impOne = new NPC(1531, new WorldTile(locationOne.getX() - 1, locationOne.getY() - 1, locationOne.getPlane()));
			NPC impTwo = new NPC(1531, new WorldTile(locationTwo.getX() - 1, locationTwo.getY() - 1, locationTwo.getPlane()));
			
			WorldTile[] impPathOne = setImpPath(locationOne);
			WorldTile[] impPathTwo = setImpPath(locationTwo);

			impOne.setRandomWalk(false);
			impOne.finishAfterTicks(42);
			
			impTwo.setRandomWalk(false);
			impTwo.finishAfterTicks(42);

			WorldTasks.scheduleTimer(ticks -> {
				if (ticks % 5 == 0 && ticks <= 35) {
					impOne.setForceWalk(impPathOne[ticks / 5]);
					impTwo.setForceWalk(impPathTwo[ticks / 5]);
				}

				if (ticks == 41) {
					impOne.setNextSpotAnim(new SpotAnim(1119)); // 931?
					impTwo.setNextSpotAnim(new SpotAnim(1119)); // 931?
				}

				if (ticks == 45) {
					spawnCropCircle(locationOne);
					spawnCropCircle(locationTwo);
					return false;
				}
				return true;
			});
		});
	}
	
	private static WorldTile[] setImpPath(CropCircle location) {
		WorldTile[] impPath = new WorldTile[] {
				new WorldTile(location.getX() + 1, location.getY() - 1, location.getPlane()),
				new WorldTile(location.getX() + 1, location.getY() + 1, location.getPlane()),
				new WorldTile(location.getX() - 1, location.getY() + 1, location.getPlane()),
				new WorldTile(location.getX() - 1, location.getY() - 1, location.getPlane()),
				new WorldTile(location.getX() + 1, location.getY() - 1, location.getPlane()),
				new WorldTile(location.getX() + 1, location.getY() + 1, location.getPlane()),
				new WorldTile(location.getX() - 1, location.getY() + 1, location.getPlane()),
				new WorldTile(location.getX() - 1, location.getY() - 1, location.getPlane()) 
		};
		return impPath;
	}

	private static void spawnCropCircle(CropCircle location) {
		World.spawnObjectTemporary(new GameObject(24986, 2, location.getX() - 1, location.getY() + 1, location.getPlane()), Ticks.fromMinutes(30));
		World.spawnObjectTemporary(new GameObject(24985, 2, location.getX(), location.getY() + 1, location.getPlane()), Ticks.fromMinutes(30));
		World.spawnObjectTemporary(new GameObject(24984, 2, location.getX() + 1, location.getY() + 1, location.getPlane()), Ticks.fromMinutes(30));
		World.spawnObjectTemporary(new GameObject(24987, 2, location.getX() - 1, location.getY(), location.getPlane()), Ticks.fromMinutes(30));
		World.spawnObjectTemporary(new GameObject(24988, 0, location.getX(), location.getY(), location.getPlane()), Ticks.fromMinutes(30));
		World.spawnObjectTemporary(new GameObject(24987, 0, location.getX() + 1, location.getY(), location.getPlane()), Ticks.fromMinutes(30));
		World.spawnObjectTemporary(new GameObject(24984, 0, location.getX() - 1, location.getY() - 1, location.getPlane()), Ticks.fromMinutes(30));
		World.spawnObjectTemporary(new GameObject(24985, 0, location.getX(), location.getY() - 1, location.getPlane()), Ticks.fromMinutes(30));
		World.spawnObjectTemporary(new GameObject(24986, 0, location.getX() + 1, location.getY() - 1, location.getPlane()), Ticks.fromMinutes(30));
	}
}
