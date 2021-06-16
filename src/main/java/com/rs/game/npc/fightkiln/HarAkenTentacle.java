package com.rs.game.npc.fightkiln;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;

public class HarAkenTentacle extends NPC {

	private HarAken aken;

	public HarAkenTentacle(int id, WorldTile tile, HarAken aken) {
		super(id, tile, true);
		setForceMultiArea(true);
		setCantFollowUnderCombat(true);
		setForceAgressive(true);
		setNextAnimation(new Animation(id == 15209 ? 16238 : 16241));
		freeze(10000000);
		this.aken = aken;
	}

	@Override
	public void sendDeath(Entity source) {
		aken.removeTentacle(this);
		super.sendDeath(source);
	}
	
	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public List<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		Set<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isRunning())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.1;
	}
}
