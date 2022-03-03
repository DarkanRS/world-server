package com.rs.game.player.content.combat;

import com.rs.game.Entity;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.others.DoorSupport;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.actions.interactions.StandardEntityInteraction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class AttackNPCHandler {
	public static NPCClickHandler attack = new NPCClickHandler(false, null, new String[]{"Attack"}) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().getInteractionManager().setInteraction(new StandardEntityInteraction(e.getNPC(), PlayerCombat.getAttackRange(e.getPlayer())+1, () -> {
				if (!e.getPlayer().getControllerManager().canAttack(e.getNPC()))
					return;
				if (e.getNPC() instanceof Familiar familiar) {
					if (familiar == e.getPlayer().getFamiliar()) {
						e.getPlayer().sendMessage("You can't attack your own familiar.");
						return;
					}
					if (!familiar.canAttack(e.getPlayer())) {
						e.getPlayer().sendMessage("You can't attack that.");
						return;
					}
				} else if (!e.getNPC().isForceMultiAttacked()) {
					if (!e.getNPC().isAtMultiArea() || !e.getPlayer().isAtMultiArea()) {
						Entity attackedBy = e.getPlayer().getAttackedBy();
						if (attackedBy != e.getNPC() && e.getPlayer().inCombat()) {
							e.getPlayer().sendMessage("You are already in combat.");
							return;
						}
						if (e.getNPC().getAttackedBy() != e.getPlayer() && e.getNPC().inCombat()) {
							e.getPlayer().sendMessage("Someone else is fighting that.");
							return;
						}
					}
				}
				e.getPlayer().setLastNpcInteractedName(e.getNPC().getDefinitions().getName());
				e.getPlayer().stopAll(true);
				e.getPlayer().getActionManager().setAction(new PlayerCombat(e.getNPC()));
			}).keepFacing());
		}
	};

	public static NPCClickHandler dagDoorSupports = new NPCClickHandler(false, new Object[]{2440, 2443, 2446}, new String[]{"Attack"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getNPC() instanceof DoorSupport door) {
				if (!door.canDestroy(e.getPlayer())) {
					e.getPlayer().sendMessage("You cannot see a way to open this door...");
					return;
				}
			}
		}
	};

	public static NPCClickHandler combatDummy = new NPCClickHandler(true, new Object[]{7891}, new String[]{"Attack"}) {
		@Override
		public void handle(NPCClickEvent e) {
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
			return;
		}
	};
}
