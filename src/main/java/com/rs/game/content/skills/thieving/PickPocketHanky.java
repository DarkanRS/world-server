package com.rs.game.content.skills.thieving;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class PickPocketHanky extends PlayerAction {
	private NPC npc;
	private boolean success = false;
	public PickPocketHanky(NPC npc) {
		this.npc = npc;
	}
	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			success = successful(player);
			player.faceEntity(npc);
			player.sendMessage("You attempt to pick the " + npc.getDefinitions().getName().toLowerCase() + "'s pocket...");
			WorldTasks.delay(0, () -> {
				player.setNextAnimation(new Animation(881));
			});
			setActionDelay(player, 2);
			player.lock();
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	public static double calculateExperience(Player player) {
		int playerLevel = player.getSkills().getLevel(Skills.THIEVING);
		double experience = 0.5 * playerLevel + 26;
		return experience;
	}

	@Override
	public int processWithDelay(Player player) {
		String[] response = new String[]{
				"No, no, not like that.",
				"Spotted you!"
		};
		if (!success) {
			player.sendMessage("You fail to pick the " + npc.getDefinitions().getName().toLowerCase() + "'s pocket.");
			npc.setNextAnimation(new Animation(422));
			npc.faceEntity(player);
			player.setNextAnimation(new Animation(424));
			player.setNextSpotAnim(new SpotAnim(80, 5, 60));
			player.sendMessage("You've been stunned.");
			player.applyHit(new Hit(player, 1, Hit.HitLook.TRUE_DAMAGE));
			npc.setNextForceTalk(new ForceTalk(response[Utils.random(2)]));
			stop(player);
		} else {
			double totalXp = calculateExperience(player);
			player.incrementCount(npc.getDefinitions().getName() + " pickpocketed");
			player.getSkills().addXp(Constants.THIEVING, totalXp);
			if (player.getWeeklyI("HankyPoints") < HankyPoints.maxPoints(player)) {
				player.simpleDialogue("You gain 1 Hanky Point.");
				player.incWeeklyI("HankyPoints");
			}
			else
				player.sendMessage("You have earned the maximum number of Hanky Points this week.");
			stop(player);
		}
		return -1;
	}

	@Override
	public void stop(Player player) {
		player.unlock();
		npc.setNextFaceEntity(null);
		player.setNextFaceEntity(null);
		setActionDelay(player, 1);
		if (!success)
			player.lock(4);
	}

	public boolean rollSuccess(Player player) {
		if(player.getSkills().getLevel(Skills.THIEVING) >= 95)
			return true;
		else
			return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul() + (hasArdyCloak(player) ? 0.1 : 0.0), 100, 255);
	}

	public static boolean hasArdyCloak(Player player) {
		switch(player.getEquipment().getCapeId()) {
			case 15349:
			case 19748:
			case 9777:
			case 9778:
				return true;
			default:
				return false;
		}
	}

	private boolean successful(Player player) {
		if (!rollSuccess(player))
			return false;
		return true;
	}

	private boolean checkAll(Player player) {
		if (player.isDead() || player.hasFinished() || npc.isDead() || npc.hasFinished() || player.hasPendingHits())
			return false;
		if (player.getAttackedBy() != null && player.inCombat()) {
			player.sendMessage("You can't do this while you're under combat.");
			return false;
		}
		if (npc.getAttackedBy() != null && npc.inCombat()) {
			player.sendMessage("The npc is under combat.");
			return false;
		}
		if (npc.isDead()) {
			player.sendMessage("Too late, the npc is dead.");
			return false;
		}
		return true;
	}

}

