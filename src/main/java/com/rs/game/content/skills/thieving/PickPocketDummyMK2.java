package com.rs.game.content.skills.thieving;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class PickPocketDummyMK2 extends PlayerAction {

	private GameObject object;

	private boolean success = false;

	public PickPocketDummyMK2(GameObject object) {
		this.object = object;
	}
	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			success = successful(player);
			player.faceObject(object);
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

	@Override
	public int processWithDelay(Player player) {
		if (!success) {
			player.sendMessage("You failed to pick the dummies' pocket.");
		}
		else {
			if(player.getSkills().getLevel(Skills.THIEVING) <= 15)
				player.getSkills().addXp(Constants.THIEVING, 4);
			if(player.getSkills().getLevel(Skills.THIEVING) >= 16)
				player.sendMessage("There's not much more you can learn from a static dummy at this point.");
		}
		stop(player);
		return -1;
	}

	@Override
	public void stop(Player player) {
		player.unlock();
		player.setNextFaceEntity(null);
		setActionDelay(player, 1);
	}

	public boolean rollSuccess(Player player) {
		return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul() + (hasArdyCloak(player) ? 0.1 : 0.0), 185, 255);
	}

	private boolean successful(Player player) {
		if (!rollSuccess(player))
			return false;
		return true;
	}

	private boolean checkAll(Player player) {
		if (player.isDead() || player.hasFinished() || player.hasPendingHits())
			return false;
		if (player.getAttackedBy() != null && player.inCombat()) {
			player.sendMessage("You can't do this while you're under combat.");
			return false;
		}
		return true;
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
}
