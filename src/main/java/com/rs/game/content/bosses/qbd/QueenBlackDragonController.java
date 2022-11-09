// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.bosses.qbd;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.bosses.qbd.npcs.QueenBlackDragon;
import com.rs.game.content.death.DeathOfficeController;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public final class QueenBlackDragonController extends Controller {

	public static final WorldTile OUTSIDE = Settings.getConfig().getPlayerRespawnTile();

	private static final int[][][] PLATFORM_STEPS = {
			{ { 88, 86 }, { 88, 87 }, { 88, 88 }, { 88, 89 }, { 88, 90 }, { 88, 91 }, { 89, 91 }, { 89, 90 }, { 89, 89 }, { 89, 88 }, { 89, 87 }, { 89, 86 }, { 90, 86 }, { 90, 87 }, { 90, 88 }, { 90, 89 }, { 90, 90 }, { 90, 91 }, { 91, 91 },
				{ 91, 90 }, { 91, 89 }, { 91, 88 }, { 91, 87 }, { 92, 87 }, { 92, 88 }, { 92, 89 }, { 92, 90 }, { 92, 91 }, { 93, 91 }, { 93, 90 }, { 93, 89 }, { 93, 88 }, { 94, 88 }, { 94, 89 }, { 94, 90 }, { 94, 91 }, { 95, 91 }, { 95, 90 },
				{ 95, 89 }, { 96, 89 }, { 96, 90 }, { 96, 91 }, { 97, 91 }, { 97, 90 }, { 98, 90 }, { 98, 91 }, { 99, 91 } },
			{ { 106, 91 }, { 106, 90 }, { 106, 89 }, { 106, 88 }, { 106, 87 }, { 106, 86 }, { 105, 86 }, { 105, 87 }, { 105, 88 }, { 105, 89 }, { 105, 90 }, { 105, 91 }, { 104, 91 }, { 104, 90 }, { 104, 89 }, { 104, 88 }, { 104, 87 }, { 104, 86 },
					{ 103, 87 }, { 103, 88 }, { 103, 89 }, { 103, 90 }, { 103, 91 }, { 102, 91 }, { 102, 90 }, { 102, 89 }, { 102, 88 }, { 102, 87 }, { 101, 88 }, { 101, 89 }, { 101, 90 }, { 101, 91 }, { 100, 91 }, { 100, 90 }, { 100, 89 },
					{ 100, 88 }, { 99, 88 }, { 99, 89 }, { 99, 90 }, { 98, 89 } },
			{ { 99, 90 }, { 100, 90 }, { 100, 89 }, { 99, 89 }, { 98, 89 }, { 97, 89 }, { 95, 88 }, { 96, 88 }, { 97, 88 }, { 98, 88 }, { 99, 88 }, { 99, 87 }, { 98, 87 }, { 97, 87 }, { 96, 87 }, { 96, 86 }, { 97, 86 }, { 98, 86 } } };


	private int platformStand;
	private transient QueenBlackDragon npc;
	private DynamicRegionReference bossRegion;
	private WorldTile bossBase;
	private DynamicRegionReference rewardRegion;
	private WorldTile rewardBase;
	
	public static ObjectClickHandler entrance = new ObjectClickHandler(new Object[] { 70812 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOption().equals("Investigate")) {
				e.getPlayer().startConversation(new Dialogue()
						.addSimple("You will be sent to the heart of this cave complex - alone. There is no way out other than victory, teleportation, or death. Only those who can endure dangerous counters (level 110 or more) should proceed.")
						.addOptions(ops -> {
							ops.add("Proceed.", () -> enterPortal(e.getPlayer()));
							ops.add("Step away from the portal.");
						}));
			} else if (e.getOption().equals("Pass through"))
				enterPortal(e.getPlayer());
		}
	};
	
	private static void enterPortal(Player player) {
		if (player.getSkills().getLevelForXp(Constants.SUMMONING) < 60) {
			player.sendMessage("You need a Summoning level of 60 to go through this portal.");
			return;
		}
		player.lock();
		player.getControllerManager().startController(new QueenBlackDragonController());
		player.setNextAnimation(new Animation(16752));
	}

	@Override
	public void start() {
		player.lock();
		bossRegion = new DynamicRegionReference(8, 8);
		bossRegion.copyMapAllPlanes(176, 792, () -> {
			bossBase = bossRegion.getBase().transform(0, 0, 1);
			player.fadeScreen(() -> {
				player.resetReceivedHits();
				npc = new QueenBlackDragon(player, bossBase.transform(31, 37, 0), bossBase);
				player.setNextWorldTile(bossBase.transform(33, 28, 0));
				player.setLargeSceneView(true);
				player.setForceMultiArea(true);
				player.unlock();
				player.getPackets().sendVarc(184, 150);
				player.getPackets().sendVarc(1924, 0);
				player.getPackets().sendVarc(1925, 0);
				player.getInterfaceManager().sendSub(Sub.FULL_GAMESPACE_BG, 1285);
				player.getMusicsManager().playSongAndUnlock(1119); // AWOKEN
			});
		});
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (npc == null)
			return true;
		if (object.getId() == 70790) {
			if (npc.getPhase() < 5)
				return true;
			player.lock();
			player.fadeScreen(() -> {
				player.resetReceivedHits();
				player.sendMessage("You descend the stairs that appeared when you defeated the Queen Black Dragon.");
				player.getPackets().sendVarc(184, -1);
				npc.finish();
				rewardRegion = new DynamicRegionReference(8, 8);
				rewardRegion.copyMapAllPlanes(160, 760, () -> {
					player.resetReceivedHits();
					rewardBase = rewardRegion.getBase().transform(0, 0, 0);
					player.setNextWorldTile(rewardBase.transform(31, 36, 0));
					player.setForceNextMapLoadRefresh(true);
					player.loadMapRegions();
					player.getInterfaceManager().removeSub(Sub.FULL_GAMESPACE_BG);
					player.unlock();
				});
			});
			return false;
		}
		if (object.getId() == 70813) {
			Magic.sendObjectTeleportSpell(player, true, new WorldTile(2994, 3233, 0));
			return false;
		}
		if (object.getId() == 70814) {
			player.sendMessage("The gate is locked.");
			return false;
		}
		if (object.getId() == 70815) {
			player.startConversation(new Dialogue()
					.addSimple("This strange device is covered in indecipherable script. It opens for you, displaying only a small sample of the objects it contains.")
					.addNext(() -> npc.openRewardChest(true)));
			return false;
		}
		if (object.getId() == 70817) {
			npc.openRewardChest(false);
			return false;
		}
		if (object.getId() == npc.getActiveArtifact().getId()) {
			player.getMusicsManager().playSongAndUnlock(1118); // QUEEN BLACK DRAGON
			npc.setSpawningWorms(false);
			npc.setNextAttack(20);
			npc.setActiveArtifact(new GameObject(object.getId() + 1, ObjectType.SCENERY_INTERACT, 0, object));
			npc.setHitpoints(npc.getMaxHitpoints());
			npc.setCantInteract(false);
			npc.setPhase(npc.getPhase() + 1);
			World.spawnObject(npc.getActiveArtifact());
			switch (object.getId()) {
			case 70777:
				player.getPackets().sendVarc(1924, 2);
				World.spawnObject(new GameObject(70843, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(24, 21, -1)));
				break;
			case 70780:
				player.getPackets().sendVarc(1924, 4);
				World.spawnObject(new GameObject(70845, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(24, 21, -1)));
				break;
			case 70783:
				player.getPackets().sendVarc(1924, 6);
				World.spawnObject(new GameObject(70847, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(24, 21, -1)));
				break;
			case 70786:
				player.getPackets().sendVarc(1924, 8);
				player.getPackets().sendRemoveObject(new GameObject(70849, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(24, 21, -1)));
				World.removeObject(new GameObject(70778, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(33, 31, 0)));
				World.removeObject(new GameObject(70776, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(33, 31, 0)));
				World.spawnObject(new GameObject(70790, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(31, 29, 0)));
				World.spawnObject(new GameObject(70775, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(31, 29, -1)));
				World.spawnObject(new GameObject(70849, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(24, 21, -1)));
				World.spawnObject(new GameObject(70837, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(22, 24, -1)));
				World.spawnObject(new GameObject(70840, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(34, 24, -1)));
				World.spawnObject(new GameObject(70822, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(21, 35, -1)));
				World.spawnObject(new GameObject(70818, ObjectType.SCENERY_INTERACT, 0, bossBase.transform(39, 35, -1)));
				break;
			}
			return false;
		}
		return true;
	}

	@Override
	public void process() {
		if (npc == null)
			return;
		if (player.getY() < bossBase.getY() + 28) {
			if (npc.hasFinished())
				return;
			if (platformStand++ == 6) {
				player.sendMessage("You are damaged for standing too long on the raw magical platforms.");
				player.applyHit(new Hit(npc, 200, HitLook.TRUE_DAMAGE));
				platformStand = 0;
			}
		} else
			platformStand = 0;
	}

	@Override
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (npc != null && nextY < bossBase.getY() + 28) {
			if (npc.getPhase() > 1) {
				for (int[] step : PLATFORM_STEPS[0])
					if (bossBase.getX() + (step[0] - 64) == nextX && bossBase.getY() + (step[1] - 64) == nextY)
						return true;
				if (npc.getPhase() > 2) {
					for (int[] step : PLATFORM_STEPS[1])
						if (bossBase.getX() + (step[0] - 64) == nextX && bossBase.getY() + (step[1] - 64) == nextY)
							return true;
					if (npc.getPhase() > 3)
						for (int[] step : PLATFORM_STEPS[2])
							if (bossBase.getX() + (step[0] - 64) == nextX && bossBase.getY() + (step[1] - 64) == nextY)
								return true;
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (npc == null)
			return true;
		switch (interfaceId) {
		case 1284:
			switch (componentId) {
			case 8:
				player.getBank().addItems(npc.getRewards().toArray(), true);
				npc.getRewards().clear();
				player.sendMessage("All the items were moved to your bank.");
				break;
			case 9:
				npc.getRewards().clear();
				player.sendMessage("All the items were removed from the chest.");
				break;
			case 10:
				for (int slot = 0; slot < npc.getRewards().toArray().length; slot++) {
					Item item = npc.getRewards().get(slot);
					if (item == null)
						continue;
					boolean added = true;
					if (item.getDefinitions().isStackable() || item.getAmount() < 2) {
						added = player.getInventory().addItem(item);
						if (added)
							npc.getRewards().toArray()[slot] = null;
					} else
						for (int i = 0; i < item.getAmount(); i++) {
							Item single = new Item(item.getId());
							if (!player.getInventory().addItem(single)) {
								added = false;
								break;
							}
							npc.getRewards().remove(single);
						}
					if (!added) {
						player.sendMessage("You only had enough space in your inventory to accept some of the items.");
						break;
					}
				}
				break;
			case 7:
				Item item = npc.getRewards().get(slotId);
				if (item == null)
					return true;
				break;
			default:
				return true;
			}
			npc.openRewardChest(false);
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		end(0);
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("Oh dear, you have died.");
				else if (loop == 3) {
					end(0);
					player.getControllerManager().startController(new DeathOfficeController(OUTSIDE, player.hasSkull()));
				} else if (loop == 4) {
					player.jingle(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean logout() {
		end(1);
		return false;
	}

	@Override
	public void forceClose() {
		end(0);
	}

	private void end(int type) {
		player.setForceMultiArea(false);
		player.setLargeSceneView(false);
		if (type == 0) {
			player.getInterfaceManager().removeSub(Sub.FULL_GAMESPACE_BG);
			player.getPackets().sendVarc(184, -1);
		} else
			player.getTile().setLocation(OUTSIDE);
		removeController();
		if (npc != null)
			player.getBank().addItems(npc.getRewards().toArray(), false);
		bossRegion.destroy();
		if (rewardRegion != null)
			rewardRegion.destroy();
	}

	public WorldTile getBase() {
		return bossBase;
	}

	public QueenBlackDragon getNpc() {
		return npc;
	}

}