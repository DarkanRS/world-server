package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.slayer.Slayer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class HoleInTheWall extends NPC {

	private transient boolean hasGrabbed;

	public HoleInTheWall(int id, WorldTile tile) {
		super(id, tile);
		setCantFollowUnderCombat(true);
		setCantInteract(true);
		setForceAgressive(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (getId() == 2058) {
			if (!hasGrabbed) {
				for (Entity entity : getPossibleTargets()) {
					if (entity == null || entity.isDead() || !withinDistance(entity, 1))
						continue;
					if (entity instanceof Player) {
						final Player player = (Player) entity;
						player.resetWalkSteps();
						hasGrabbed = true;
						if (Slayer.hasSpinyHelmet(player)) {
							setNextNPCTransformation(7823);
							setNextAnimation(new Animation(1805));
							setCantInteract(false);
							player.sendMessage("The spines on your helmet repel the beast's hand.");
							return;
						}
						setNextAnimation(new Animation(1802));
						player.lock(4);
						player.setNextAnimation(new Animation(425));
						player.sendMessage("A giant hand appears and grabs your head.");
						WorldTasksManager.schedule(new WorldTask() {

							@Override
							public void run() {
								player.applyHit(new Hit(player, Utils.getRandomInclusive(44), HitLook.TRUE_DAMAGE));
								setNextAnimation(new Animation(-1));
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										hasGrabbed = false;
									}
								}, 20);
							}
						}, 5);
					}
				}
			}
		} else {
			if (!getCombat().process()) {
				setCantInteract(true);
				setNextNPCTransformation(2058);
			}
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					setNPC(2058);
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							hasGrabbed = false;
						}
					}, 8);
					spawn();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(2058) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new HoleInTheWall(npcId, tile);
		}
	};
}
