package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Werewolf extends NPC {

	private int realId;

	public Werewolf(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		realId = id;
	}

	public boolean hasWolfbane(Entity target) {
		if (target instanceof NPC)
			return false;
		return ((Player) target).getEquipment().getWeaponId() == 2952;
	}

	@Override
	public void processNPC() {
		if (isDead() || isCantInteract())
			return;
		if (isUnderCombat() && getId() == realId && Utils.random(5) == 0) {
			final Entity target = getCombat().getTarget();
			if (!hasWolfbane(target)) {
				setNextAnimation(new Animation(6554));
				setCantInteract(true);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						setNextNPCTransformation(realId - 20);
						setNextAnimation(new Animation(-1));
						setCantInteract(false);
						setTarget(target);
					}
				}, 1);
				return;
			}
		}
		super.processNPC();
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6026, 6027, 6028, 6029, 6030, 6031, 6032, 6033, 6034, 6035, 6036, 6037, 6038, 6039, 6040, 6041, 6042, 6043, 6044, 6045) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Werewolf(npcId, tile, false);
		}
	};
}
