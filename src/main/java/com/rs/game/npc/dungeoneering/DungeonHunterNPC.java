package com.rs.game.npc.dungeoneering;

import java.util.List;

import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.skills.DungeoneeringTraps;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class DungeonHunterNPC extends DungeonNPC {

	public DungeonHunterNPC(int id, WorldTile tile, DungeonManager manager) {
		super(id, tile, manager);
	}

	@Override
	public void processNPC() {
		if (isCantInteract() || getId() >= 11096 || getManager() == null)
			return;
		super.processNPC();
		List<MastyxTrap> traps = getManager().getMastyxTraps();
		if (traps.isEmpty())
			return;
		final int tier = DungeoneeringTraps.getNPCTier(getId());
		for (final MastyxTrap trap : traps) {
			if (!withinDistance(trap, 3) || Utils.getRandomInclusive(3) != 0)
				continue;
			trap.setCantInteract(true);
			setCantInteract(true);
			setNextFaceEntity(trap);
			addWalkSteps(trap.getX() + 1, trap.getY() + 1);

			final int trap_tier = trap.getTier();
			double successRatio = getSuccessRatio(tier, trap_tier);
			final boolean failed = successRatio < Math.random();

			setCantInteract(true);
			if (failed) {
				WorldTasksManager.schedule(new WorldTask() {

					int ticks = 0;

					@Override
					public void run() {
						ticks++;
						if (ticks == 5) {
							setNextAnimation(new Animation(13264));
						} else if (ticks == 8) {
							trap.setNextNPCTransformation(1957);
							trap.setNextSpotAnim(new SpotAnim(2561 + trap_tier));
						} else if (ticks == 16) {
							getManager().removeMastyxTrap(trap);
							setCantInteract(false);
							this.stop();
							return;
						}
					}
				}, 0, 0);
			} else {
				WorldTasksManager.schedule(new WorldTask() {

					int ticks = 0;

					@Override
					public void run() {
						ticks++;
						if (ticks == 9) {
							trap.setNextNPCTransformation(1957);
							trap.setNextSpotAnim(new SpotAnim(2551 + trap_tier));
						} else if (ticks == 13) {
							setNextAnimation(new Animation(13260));
						} else if (ticks == 18) {
							setNextNPCTransformation(getId() + 10);
						} else if (ticks == 20) {
							setCantInteract(false);
							getManager().removeMastyxTrap(trap);
							this.stop();
							return;
						}
					}
				}, 0, 0);
			}
		}
	}

	@Override
	public void drop() {
		/*World.addGroundItem(new Item(532), new WorldTile(this)); //big bones
		Drops drops = NPCDrops.getDrops(getId());
		if (drops == null)
			return;
		Drop drop = drops.getDrop(Drops.COMMOM, Double.MAX_VALUE); //to make 100% chance
		if (drop == null) //shouldnt
			return;
		World.addGroundItem(new Item(drop.getItemId()), new WorldTile(this)); //hide*/
	}

	private static double getSuccessRatio(int tier, int trapTier) {
		double successRatio = 0.0;
		int tierProduct = trapTier - tier;
		if (tierProduct == 0)
			successRatio = 0.5;
		else if (tierProduct > 0)
			successRatio = 0.5 + (tierProduct / 10.0);

		if (successRatio > 0.9)
			successRatio = 0.9;
		return successRatio;
	}
}
