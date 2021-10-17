package com.rs.game.npc.dungeoneering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants.GuardianMonster;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

//status: done

public final class DivineSkinweaver extends DungeonBoss {

	private static final int[][] HOLES =
	{
	{ 0, 10 },
	{ 5, 15 },
	{ 11, 15 },
	{ 15, 10 },
	{ 15, 5 } };

	private static final String[] CLOSE_HOLE_MESSAGES =
	{ "Ride the wind and smite the tunnel.", "We have little time, tear down the tunnel.", "Churra! Bring down the tunnel while you can." };

	private final boolean[] holeClosed;
	private int count;
	private boolean requestedClose;
	private int healDelay;
	private int respawnDelay;
	private final List<DungeonSkeletonBoss> skeletons;
	private int killedCount;

	public DivineSkinweaver(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
		holeClosed = new boolean[5];
		skeletons = Collections.synchronizedList(new ArrayList<DungeonSkeletonBoss>());
		setIgnoreDocile(true);
		setForceAgressive(true);
	}

	public void removeSkeleton(DungeonSkeletonBoss skeleton) {
		skeletons.remove(skeleton);
		if (!requestedClose && count < holeClosed.length) {
			killedCount++;
			if (killedCount == 3) {
				requestedClose = true;
				killedCount = 0;
				this.setNextForceTalk(new ForceTalk(CLOSE_HOLE_MESSAGES[Utils.random(CLOSE_HOLE_MESSAGES.length)]));
				for (Player p2 : getManager().getParty().getTeam()) {
					if (!getManager().isAtBossRoom(p2))
						continue;
					p2.sendMessage("Divine skinweaver: <col=99CC66>" + getNextForceTalk().getText());
				}
			}
		}
	}

	private int[] getOpenHole() {
		List<int[]> holes = new ArrayList<int[]>();
		for (int[] hole : HOLES) {
			GameObject object = getManager().getObjectWithType(getReference(), 49289, ObjectType.WALL_STRAIGHT, hole[0], hole[1]);
			if (object != null && object.getId() != 49289)
				holes.add(new int[]
				{ object.getX() + Utils.ROTATION_DIR_X[object.getRotation()], object.getY() + Utils.ROTATION_DIR_Y[object.getRotation()] });
		}
		if (holes.size() == 0)
			return null;
		return holes.get(Utils.random(holes.size()));
	}

	@Override
	public void processNPC() {
		List<Entity> targets = getPossibleTargets();
		if (respawnDelay > 0) {
			respawnDelay--;
		} else if (count < holeClosed.length && targets.size() != 0 && skeletons.size() < 8) { //blablala spawn skeletons
			int[] coords = getOpenHole();
			if (coords != null) {
				int skeleType = Utils.random(3);
                int cbLevel = getManager().getCombatLevelMonster();
                cbLevel = (int) (cbLevel - Math.ceil(cbLevel*0.35));
				if (skeleType == 0)
					skeletons.add((DungeonSkeletonBoss) getManager().spawnNPC(DungeonUtils.getClosestToCombatLevel(GuardianMonster.SKELETON_MAGIC.getNPCIds(), getManager().getCombatLevelMonster()), 0, new WorldTile(coords[0], coords[1], 0), getReference(), DungeonConstants.BOSS_NPC));
				else if (skeleType == 1)
					skeletons.add((DungeonSkeletonBoss) getManager().spawnNPC(DungeonUtils.getClosestToCombatLevel(GuardianMonster.SKELETON_MELEE.getNPCIds(), getManager().getCombatLevelMonster()), 0, new WorldTile(coords[0], coords[1], 0), getReference(), DungeonConstants.BOSS_NPC));
				else if (skeleType == 2)
					skeletons.add((DungeonSkeletonBoss) getManager().spawnNPC(DungeonUtils.getClosestToCombatLevel(GuardianMonster.SKELETON_RANGED.getNPCIds(), getManager().getCombatLevelMonster()), 0, new WorldTile(coords[0], coords[1], 0), getReference(), DungeonConstants.BOSS_NPC));
				respawnDelay = 25;
			}
		}
		if (healDelay > 0) {
			healDelay--;
			return;
		}
		Entity healTarget = null;
		for (Entity target : targets) {
			if (target.getHitpoints() >= target.getMaxHitpoints())
				continue;
			if (healTarget == null || Utils.getDistance(this, healTarget) > Utils.getDistance(this, target))
				healTarget = target;
		}
		if (healTarget == null)
			return;
		int distance = (int) (4 - Utils.getDistance(this, healTarget));
		if (distance == 4 || distance < 0)
			return;
		int maxHeal = (int) (healTarget.getMaxHitpoints() * 0.35);
		
		healTarget.heal((distance + 1) * maxHeal / 4, 60);
		setNextAnimation(new Animation(13678));
		setNextSpotAnim(new SpotAnim(2445));
		healTarget.setNextSpotAnim(new SpotAnim(2443, 60, 0));
		faceEntity(healTarget);
		healDelay = 4;
	}

	public void talkTo(Player player) {
		if (count < holeClosed.length || skeletons.size() > 0) {
			player.getDialogueManager().execute(new SimpleNPCMessage(), getId(), "Chat later and kill the skeletons now, brah.");
			return;
		}
		if (killedCount == Integer.MAX_VALUE)
			return;
		setNextForceTalk(new ForceTalk("I see little danger in this room so move on to the next with my thanks."));
		for (Player p2 : getManager().getParty().getTeam()) {
			if (!getManager().isAtBossRoom(p2))
				continue;
			p2.sendMessage("Divine skinweaver: <col=99CC66>" + getNextForceTalk().getText());
		}
		getManager().openStairs(getReference());
		drop();
		killedCount = Integer.MAX_VALUE;
	}

	public void blockHole(Player player, GameObject object) {
		if (count >= holeClosed.length)
			return;
		player.setNextAnimation(new Animation(833));
		if (!requestedClose) {
			player.sendMessage("The portal is fully powered and shocks you with a large burst of energy.");
			player.applyHit(new Hit(player, (int) (player.getMaxHitpoints() * 0.2), HitLook.TRUE_DAMAGE));
			return;
		}
		holeClosed[count++] = true;
		requestedClose = false;
		GameObject closedHole = new GameObject(object);
		closedHole.setId(49289);
		World.spawnObject(closedHole);

	}
	
	@Override
	public List<Entity> getPossibleTargets() {
		ArrayList<Entity> targets = new ArrayList<>();
		for (Player player : getManager().getParty().getTeam()) {
			if (player == null || !getManager().isAtBossRoom(player))
				continue;
			targets.add(player);
		}
		return targets;
	}
}
