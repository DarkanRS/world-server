package com.rs.game.player.content.world.regions.apeatoll;

import com.rs.game.ForceMovement;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class ApeAtollAgility {

	public static void ClimbDownTropicalTree(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock();
		final WorldTile toTile = new WorldTile(2769, 2746, 1);
		player.setNextForceMovement(new ForceMovement(player, 1, toTile, 7, Direction.NORTH));
		player.getAppearance().setBAS(760);
		WorldTasksManager.schedule(new WorldTask() {
			public void run() {
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 55);
				player.getAppearance().setBAS(-1);
				player.unlock();
				stop();
			}
		}, 2);
	}

	public static void ClimbDownVine(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final WorldTile toTile = new WorldTile(player.getX(), player.getY(), 0);
		WorldTasksManager.schedule(new WorldTask() {
			public void run() {
				player.setNextAnimation(new Animation(1381));
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 36);
				stop();
			}
		}, 1);
	}

	public static void ClimbUpSkullSlope(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(4);
		final WorldTile toTile = new WorldTile(2743, 2741, 0);
		player.setNextForceMovement(new ForceMovement(player, 0, toTile, 3, Direction.WEST));
		player.getAppearance().setBAS(739);
		WorldTasksManager.schedule(new WorldTask() {
			public void run() {
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 45);
				player.getAppearance().setBAS(-1);
				stop();
			}
		}, 2);
	}

	public static void ClimbUpTropicalTree(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final WorldTile toTile = new WorldTile(2752, 2742, 2);
		WorldTasksManager.schedule(new WorldTask() {
			public void run() {
				player.setNextAnimation(new Animation(1382));
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 25);
				stop();
			}
		}, 1);
	}

	public static void crossMonkeyBars(final Player player, final GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(4);
		final WorldTile toTile = new WorldTile(2747, 2741, 0);
		final WorldTile toTile2 = new WorldTile(2747, 2741, 2);
		player.setNextForceMovement(new ForceMovement(player, 0, toTile2, 4, Direction.WEST));
		player.getAppearance().setBAS(744);
		WorldTasksManager.schedule(new WorldTask() {
			public void run() {
				player.getAppearance().setBAS(-1);
				player.setNextWorldTile(toTile);
				player.getSkills().addXp(Constants.AGILITY, 35);
				stop();
			}
		}, 3);
	}

	public static void JumpToSteppingStone(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to transform into a ninja monkey to use the ape atoll courses.");
			return;
		}
		player.lock(3);
		final WorldTile toTile = new WorldTile(object.getX(), object.getY(), object.getPlane());
		final WorldTile toTile2 = new WorldTile(player.getX() == 2755 ? 2753 : 2755, 2742, object.getPlane());
		final WorldTile WaterTile = new WorldTile(2756, 2746, object.getPlane());
		final WorldTile Land = new WorldTile(2757, 2746, object.getPlane());
		WorldTasksManager.schedule(new WorldTask() {

			public void run() {
				player.setNextAnimation(new Animation(1381));
				player.setNextWorldTile(toTile);
				WorldTasksManager.schedule(new WorldTask() {

					public void run() {
						if (Utils.random(5) == 0) {
							player.setNextAnimation(new Animation(1381));
							player.applyHit(new Hit(player, Utils.random(200), HitLook.TRUE_DAMAGE));
							player.setNextForceMovement(new ForceMovement(player, 0, WaterTile, 3, Direction.NORTH));
							player.getAppearance().setBAS(741);
							WorldTasksManager.schedule(new WorldTask() {

								public void run() {
									player.getAppearance().setBAS(-1);
									player.setNextWorldTile(Land);
									stop();
								}
							}, 1);
							stop();
						} else {
							player.setNextAnimation(new Animation(1381));
							player.setNextWorldTile(toTile2);
							player.getSkills().addXp(Constants.AGILITY, 15);
							stop();
						}
					}
				}, 1);
				stop();
			}
		}, 1);
	}

	public static void swingRope(final Player player, GameObject object) {
		if (!Agility.hasLevel(player, 48))
			return;
		if (player.getX() == 2756)
			return;
		if (player.getEquipment().getWeaponId() != 4024) {
			player.getPackets().sendGameMessage("You need to be a ninja monkey to be able to do this agility.");
			return;
		}
		player.lock(4);
		player.setNextAnimation(new Animation(1392));
		World.sendObjectAnimation(player, object, new Animation(497));
		final WorldTile toTile = new WorldTile(2756, 2731, object.getPlane());
		player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3, Direction.EAST));
		player.getSkills().addXp(Constants.AGILITY, 22);
		player.getPackets().sendGameMessage("You skilfully swing across.", true);
		WorldTasksManager.schedule(new WorldTask() {

			public void run() {
				player.setNextWorldTile(toTile);
				stop();
			}
		}, 1);
	}
}
