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
package com.rs.game.content.quests.vampireslayer;

import com.rs.cache.loaders.ObjectType;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.engine.pathfinder.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class CountDraynorBoss extends OwnedNPC {
	private static final int COUNT_DRAYNOR_ID = 9356;

	//Vampyre animations
	static final int STUNNED = 1568;
	static final int ASLEEP_IN_COFFIN = 3111;
	static final int AWAKEN = 3322;
	static final int SPAWN = 3328;
	static final int DEATH = 12604;

	//Player animations
	static final int OPEN_COFFIN = 2991;
	static final int MISSING_STAKE_IN_COFFIN = 2992;
	static final int PUSHED_BACK = 3064;
	static final int ON_FLOOR = 16713;
	static final int KILL_W_STAKE = 12606;

	//Coffin ID/animations
	static final int COFFIN_ID = 162;
	static final int COFFIN_OPEN = 3112;

	//Item
	static final int STAKE = 1549;
	static final int STAKE_HAMMER = 15417;
	static final int REGULAR_HAMMER = 2347;
	static final int GARLIC = 1550;

	//Count draynor boss music
	static final int COUNTING_ON_YOU = 717;

	public boolean actuallyDead = false;

	public CountDraynorBoss(Player owner, Tile tile) {
		super(owner, COUNT_DRAYNOR_ID, tile, false);
        setAutoDespawnAtDistance(false);
	}

	@Override
	public void sendDeath(Entity source) {
		removeCombatTarget();
		setAttackedBy(null);
		resetHP();
		setLocked(true);
		faceEntityTile(source);

		WorldTasks.scheduleLooping(new Task() {
			int tick = 0;
			final int finalTick = Ticks.fromSeconds(12);

			@Override
			public void run() {
				if(World.getPlayersInChunkRange(getChunkId(), 2).isEmpty())
					finish();
				if(tick == 1)
					setNextAnimation(new Animation(STUNNED));
				if(tick == finalTick - 1)
					setLocked(false);
				if(tick == finalTick) {
					setCombatTarget(source);
					stop();
				}
				tick++;
			}
		}, 0, 1);
	}


	/**
	 * player in die is seperate from player in the boss.
	 * @param player
	 */
	public void die(Player player) {
        if(getOwner() == null)
            return;
        if(player != getOwner()) {
            player.sendMessage("This is not your vampyre to kill!");
            return;
        }
		WorldTasks.scheduleLooping(new Task() {
			int tick = 0;

			@Override
			public void run() {
				if(tick == 0) {
					player.setNextAnimation(new Animation(KILL_W_STAKE));
					setNextAnimation(new Animation(DEATH));
				}
				if(tick == 5) {
					finish();
					player.getQuestManager().setStage(Quest.VAMPYRE_SLAYER, VampireSlayer.VAMPYRE_KILLED);
					player.startConversation(new Conversation(player) {
						{
							addPlayer(HeadE.CALM_TALK, "I should tell Morgan that I've killed the vampyre!");
						}
					});
					stop();
				}
				tick++;
			}
		}, 0, 1);
	}

	public static ObjectClickHandler handleCoffin = new ObjectClickHandler(new Object[] { 158, COFFIN_ID}, e -> {
		Player p = e.getPlayer();
		if(p.getQuestManager().getStage(Quest.VAMPYRE_SLAYER) != VampireSlayer.STAKE_RECIEVED)
			return;
		if(e.getObject().getId() == COFFIN_ID) {
			p.startConversation(new Conversation(p) {
				{
					addPlayer(HeadE.CALM_TALK, "Count Draynor isn't here. He'll probably be back soon...");
				}
			});
			return;
		}
		if(!p.getInventory().containsItem(STAKE, 1) ||
			(!p.getInventory().containsItem(STAKE_HAMMER, 1) && !p.getInventory().containsItem(REGULAR_HAMMER, 1))) {
			p.startConversation(new Conversation(p) {
				{
					addPlayer(HeadE.CALM_TALK, "I'll need both a stake and stake hammer or hammer, better go get those...");
				}
			});
			return;
		}
		if(e.getObject().getId() == 158) {
			World.removeObject(e.getObject());
			World.spawnObject(new GameObject(COFFIN_ID, e.getObject().getType(), e.getObject().getRotation(), e.getObject().getTile()), true);
		}

		p.save("live_in_scene", true);
		p.lock();
		GameObject coffin = World.getObject(e.getObject().getTile(), ObjectType.forId(10));
		p.getMusicsManager().playSongAndUnlock(COUNTING_ON_YOU);

        CountDraynorBoss countDraynor = new CountDraynorBoss(p, Tile.of(coffin.getX()+1, coffin.getY()+1, coffin.getPlane()));

		countDraynor.setLocked(true);
		countDraynor.faceTile(Tile.of(coffin.getX()+1, coffin.getY() - 5, coffin.getPlane()));
		countDraynor.setHidden(true);

		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				World.removeObject(coffin);
				World.spawnObject(new GameObject(158, e.getObject().getType(), e.getObject().getRotation(), e.getObject().getTile()), true);
				countDraynor.finish();
			}
		}, Ticks.fromMinutes(3));

		WorldTasks.scheduleLooping(new Task() {
			int tick = 0;

			@Override
			public void run() {
				if(tick == 0)
					p.getInterfaceManager().setFadingInterface(115);
				if(tick == 3) {
					p.tele(Tile.of(3079, 9786, 0));
					p.getPackets().sendCameraPos(coffin.getTile().getXInScene(p.getSceneBaseChunkId())-4, coffin.getTile().getYInScene(p.getSceneBaseChunkId())-8, 3000);
					p.getPackets().sendCameraLook(coffin.getTile().getXInScene(p.getSceneBaseChunkId()), coffin.getTile().getYInScene(p.getSceneBaseChunkId()), 300);
				}
				if(tick == 4)
					p.getInterfaceManager().setFadingInterface(170);
				if(tick == 5)
					p.faceObject(coffin);
				if(tick == 6) {
					p.setNextAnimation(new Animation(OPEN_COFFIN));
					p.getPackets().sendObjectAnimation(coffin, new Animation(COFFIN_OPEN));
					countDraynor.setHidden(false);
					countDraynor.setNextAnimation(new Animation(ASLEEP_IN_COFFIN));
				}
				if(tick == 8) {
					p.getInventory().deleteItem(STAKE, 1);
					p.setNextAnimation(new Animation(MISSING_STAKE_IN_COFFIN));
					countDraynor.setNextAnimation(new Animation(AWAKEN));
                    countDraynor.setAutoDespawnAtDistance(true);
				}
				if(tick == 9)
					p.forceMove(Tile.of(p.getX()-1, p.getY(), p.getPlane()), Direction.EAST, PUSHED_BACK, 0, 30);
				if(tick == 10) {
					p.setNextAnimation(new Animation(ON_FLOOR));
					p.getPackets().sendCameraPos(coffin.getTile().getXInScene(p.getSceneBaseChunkId())-4, coffin.getTile().getYInScene(p.getSceneBaseChunkId())-16, 2200, 0, 5);
					p.getPackets().sendCameraLook(coffin.getTile().getXInScene(p.getSceneBaseChunkId()), coffin.getTile().getYInScene(p.getSceneBaseChunkId())-5, 50, 5, 0);
				}
				if(tick == 14)
					countDraynor.faceTile(Tile.of(countDraynor.getX(), countDraynor.getY()+3, countDraynor.getPlane()));
				if(tick == 16) {
					countDraynor.tele(Tile.of(3082, 9776, 0));
					countDraynor.setNextAnimation(new Animation(SPAWN));
				}

				if(tick == 19) {
					countDraynor.setLocked(false);
					countDraynor.setRandomWalk(true);
				}
				if(tick == 20) {
					countDraynor.setCombatTarget(p);
					p.faceEntityTile(countDraynor);
				}
				if(tick == 22) {
					if(p.getInventory().containsItem(GARLIC, 1)) {
						countDraynor.lowerDefense(5, 0.0);
						countDraynor.lowerStrength(5, 0.0);
						p.sendMessage("The garlic has weakened Count Draynor...");
					}
					p.getPackets().sendResetCamera();
					p.unlock();
					p.save("live_in_scene", false);
					stop();
				}

				tick++;
			}
		}, 0, 1);
	});

	public static LoginHandler onLogin = new LoginHandler(e -> {
		if(e.getPlayer().getBool("live_in_scene"))
			e.getPlayer().unlock();
	});

	public static ItemOnNPCHandler hammerOnCountDraynor = new ItemOnNPCHandler(COUNT_DRAYNOR_ID, e -> {
		if (e.getItem().getId() != STAKE_HAMMER && e.getItem().getId() != REGULAR_HAMMER)
			return;
		if ((e.getNPC() instanceof CountDraynorBoss boss) && boss.isLocked()) {
			boss.die(e.getPlayer());
			return;
		}
		e.getPlayer().sendMessage("I must weaken him first");
	});

}
