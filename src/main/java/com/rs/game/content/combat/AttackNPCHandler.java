package com.rs.game.content.combat;

import com.rs.game.content.world.npcs.DoorSupport;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.interactions.StandardEntityInteraction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class AttackNPCHandler {
	public static NPCClickHandler attack = new NPCClickHandler(false, null, new String[]{"Attack"}, e -> {
		e.getPlayer().stopAll(true);
		e.getPlayer().getInteractionManager().setInteraction(new PlayerCombatInteraction(e.getPlayer(), e.getNPC()));
	});

	public static NPCClickHandler dagDoorSupports = new NPCClickHandler(false, new Object[]{2440, 2443, 2446}, new String[]{"Destroy"}, e -> {
		if (e.getNPC() instanceof DoorSupport door) {
			if (!door.canDestroy(e.getPlayer())) {
				e.getPlayer().sendMessage("You cannot see a way to open this door...");
				return;
			}
		}
		e.getPlayer().stopAll(true);
		e.getPlayer().getInteractionManager().setInteraction(new PlayerCombatInteraction(e.getPlayer(), e.getNPC()));
	});

	public static NPCClickHandler combatDummy = new NPCClickHandler(true, new Object[]{7891}, new String[]{"Attack"}, e -> {
		e.getPlayer().getInteractionManager().setInteraction(new StandardEntityInteraction(e.getNPC(), 0, () -> {
			if (!e.getPlayer().getControllerManager().canAttack(e.getNPC()))
				return;
			e.getNPC().resetWalkSteps();
			e.getPlayer().faceEntity(e.getNPC());
			if (e.getPlayer().getSkills().getLevelForXp(Constants.ATTACK) < 5) {
				if (e.getPlayer().getActionManager().getActionDelay() < 1) {
					e.getPlayer().getActionManager().setActionDelay(4);
					e.getPlayer().setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(e.getPlayer().getEquipment().getWeaponId(), e.getPlayer().getCombatDefinitions().getAttackStyle())));
					e.getPlayer().getSkills().addXp(Constants.ATTACK, 15);
				}
			} else
				e.getPlayer().sendMessage("You have nothing more you can learn from this.");
		}));
	});
}
