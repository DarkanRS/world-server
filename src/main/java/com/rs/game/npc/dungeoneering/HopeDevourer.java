package com.rs.game.npc.dungeoneering;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.Constants;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class HopeDevourer extends DungeonBoss {

	private int auraTicks;
	private int auraDamage;

	public HopeDevourer(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(12886, 12900), manager.getBossLevel()), tile, manager, reference);
		setHitpoints(getMaxHitpoints());
		setLureDelay(10000);
		setForceFollowClose(true);
		this.auraDamage = (int) Utils.random(getMaxHit() * .1, getMaxHit() * .15);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		auraTicks++;
		if (auraTicks == 20) {
			sendAuraAttack();
			auraTicks = 0;
		}
	}

	@Override
	public boolean canMove(Direction dir) {
		int nextX = dir.getDx() + getX();
		int nextY = dir.getDy() + getY();
		int size = getSize(); //i always do this instead of calling at loop cuz it grabs npcdef from hashmap every call
		for (Player player : getManager().getParty().getTeam()) {
			if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), nextX, nextY, size))
				return false;
		}
		return true;
	}

	private void sendAuraAttack() {
		for (Entity t : super.getPossibleTargets()) {
			t.applyHit(new Hit(this, auraDamage, HitLook.TRUE_DAMAGE, 60));
			if (t instanceof Player) {
				Player player = (Player) t;
				int combatSkill = Utils.random(Constants.MAGIC);
				if (combatSkill == 3)
					combatSkill = 1;
				player.getSkills().set(combatSkill, (int) (player.getSkills().getLevel(combatSkill) * Utils.random(0.94, .99)));
				player.sendMessage("You feel hopeless...");
			}
		}
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> targets = super.getPossibleTargets();
		if (getAttackedBy() == null)
			return targets;
		else {
			ArrayList<Entity> possibleTargets = new ArrayList<Entity>();
			for (Entity t : targets) {
				if (t.inCombat())
					possibleTargets.add(t);
			}
			return possibleTargets;
		}
	}
}
