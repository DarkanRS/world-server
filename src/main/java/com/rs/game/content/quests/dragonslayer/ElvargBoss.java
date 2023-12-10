package com.rs.game.content.quests.dragonslayer;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.*;

@PluginEventHandler
public class ElvargBoss extends NPC {
	private static final int ELVARG_ID = 742;
	private static final int ELVARG_OBJ = 25202;
	private static final int ELVARG_HEADLESS_OBJ = 25203;
	private static final int ELVARG_DEATH_ANIM = 14260;
	private static final int ELVARG_REMOVE_HEAD_ANIM = 6654;
	private static final int ELVARG_SHOW_OFF_HEAD_ANIM = 6655;

	public ElvargBoss(Tile tile) {
		super(ELVARG_ID, tile);
	}

	@Override
	public boolean canBeAttackedBy(Player player) {
		if(player.getQuestManager().getStage(Quest.DRAGON_SLAYER) != PREPARE_FOR_CRANDOR)
			return false;
		return true;
	}

	@Override
	public boolean canAggroPlayer(Player player) {
		if(player.getQuestManager().getStage(Quest.DRAGON_SLAYER) != PREPARE_FOR_CRANDOR)
			return false;
		return true;
	}

	@Override
	public void sendDeath(Entity source) {
		if(source instanceof Player p) {
			p.lock();
			ElvargBoss elvarg = this;
			removeTarget();
			elvarg.setCantInteract(true);
			WorldTasks.schedule(new Task() {
				int tick = 0;
				int WALK_TO_TILE_TICK = 7;
				Tile animTile;
				GameObject elvargObj = null;

				@Override
				public void run() {
					if(tick == 0)
						elvarg.walkToAndExecute(Tile.of(2854, 9638, 0), ()->{
							animTile = Tile.of(elvarg.getX()-1, elvarg.getY()+1, elvarg.getPlane());
							elvarg.setNextFaceTile(Tile.of(getX()-1, getY()+1, getPlane()));
							tick++;
						});

					if (tick == 2)
						setNextAnimation(new Animation(ELVARG_DEATH_ANIM));

					if(tick == 5) {
						elvarg.setCantInteract(false);
						elvarg.setRespawnTask(200);

						elvargObj = new GameObject(ELVARG_OBJ, ObjectDefinitions.getDefs(ELVARG_HEADLESS_OBJ).types[0], Direction.rotateClockwise(Direction.WEST, 4).getId()/2, 2854, 9638, 0);
						World.spawnObject(elvargObj);
					}

					if(tick == WALK_TO_TILE_TICK)
						p.walkToAndExecute(animTile, () -> {
							p.faceObject(elvargObj);
							tick++;
						});

					if(tick == 9)
						p.setNextAnimation(new Animation(ELVARG_REMOVE_HEAD_ANIM));
					if(tick == 10) {
					}

					if(tick == 11) {
						World.removeObject(elvargObj);
						elvargObj = new GameObject(ELVARG_HEADLESS_OBJ, ObjectDefinitions.getDefs(ELVARG_HEADLESS_OBJ).types[0], Direction.rotateClockwise(Direction.WEST, 4).getId()/2, 2854, 9638, 0);
						World.spawnObjectTemporary(elvargObj, 150);
						if(p.getQuestManager().getStage(Quest.DRAGON_SLAYER) == PREPARE_FOR_CRANDOR) {
							p.getInventory().addItem(new Item(ELVARG_HEAD, 1), true);
							p.getQuestManager().setStage(Quest.DRAGON_SLAYER, REPORT_TO_OZIACH);
						}
						p.setNextAnimation(new Animation(ELVARG_SHOW_OFF_HEAD_ANIM));
					}

					if(tick == 13) {
						p.unlock();
						stop();
					}

					if(tick != WALK_TO_TILE_TICK && tick != 0)
						tick++;

				}
			}, 0, 1);
		}
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(ELVARG_ID, (npcId, tile) -> new ElvargBoss(tile));
}
