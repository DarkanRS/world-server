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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.familiar;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.cache.loaders.interfaces.IFTargetParams.UseFlag;
import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.skills.summoning.Summoning;
import com.rs.game.player.content.skills.summoning.Summoning.Pouches;
import com.rs.game.player.dialogues.DismissD;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.WorldUtil;

@PluginEventHandler
public abstract class Familiar extends NPC {

	private transient Player owner;
	private int ticks;
	private int trackTimer;
	private int specialEnergy;
	private transient boolean finished = false;
	private boolean trackDrain;

	private BeastOfBurden bob;
	private Pouches pouch;

	public Familiar(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(Summoning.getNPCId(pouch.getRealPouchId()), tile, false);
		this.owner = owner;
		this.pouch = pouch;
		setIgnoreNPCClipping(true);
		setBlocksOtherNPCs(false);
		resetTickets();
		specialEnergy = 60;
		if (getBOBSize() > 0)
			bob = new BeastOfBurden(getBOBSize());
		call(true);
	}

	public boolean isBeastOfBurden() {
		return bob != null;
	}

	public void store() {
		if (bob == null)
			return;
		bob.open();
	}

	public boolean canStoreEssOnly() {
		return pouch == Pouches.ABYSSAL_LURKER || pouch == Pouches.ABYSSAL_PARASITE || pouch == Pouches.ABYSSAL_TITAN;
	}

	public int getOriginalId() {
		return Summoning.getNPCId(pouch.getRealPouchId());
	}

	public void resetTickets() {
		ticks = (int) (pouch.getPouchTime() / 1000 / 30);
		trackTimer = 0;
	}

	public static ButtonClickHandler handleFamiliarOptionSettings = new ButtonClickHandler(880) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 7 && e.getComponentId() <= 19)
				setLeftclickOption(e.getPlayer(), (e.getComponentId() - 7) / 2);
			else if (e.getComponentId() == 21)
				confirmLeftOption(e.getPlayer());
			else if (e.getComponentId() == 25)
				setLeftclickOption(e.getPlayer(), 7);
		}
	};

	public static ButtonClickHandler handleFamiliarOption = new ButtonClickHandler(747) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 8)
				selectLeftOption(e.getPlayer());
			else if (e.getPlayer().getPet() != null) {
				if (e.getComponentId() == 11 || e.getComponentId() == 20)
					e.getPlayer().getPet().call();
				else if (e.getComponentId() == 12 || e.getComponentId() == 21)
					e.getPlayer().getDialogueManager().execute(new DismissD());
				else if (e.getComponentId() == 10 || e.getComponentId() == 19)
					e.getPlayer().getPet().sendFollowerDetails();
			} else if (e.getPlayer().getFamiliar() != null)
				if (e.getComponentId() == 11 || e.getComponentId() == 20)
					e.getPlayer().getFamiliar().call();
				else if (e.getComponentId() == 12 || e.getComponentId() == 21)
					e.getPlayer().getDialogueManager().execute(new DismissD());
				else if (e.getComponentId() == 13 || e.getComponentId() == 22)
					e.getPlayer().getFamiliar().takeBob();
				else if (e.getComponentId() == 14 || e.getComponentId() == 23)
					e.getPlayer().getFamiliar().renewFamiliar();
				else if (e.getComponentId() == 19 || e.getComponentId() == 10)
					e.getPlayer().getFamiliar().sendFollowerDetails();
				else if (e.getComponentId() == 18) {
					if (e.getPlayer().getFamiliar().getSpecialAttack() == SpecialAttack.CLICK)
						e.getPlayer().getFamiliar().setSpecial(true);
					if (e.getPlayer().getFamiliar().hasSpecialOn())
						e.getPlayer().getFamiliar().submitSpecial(e.getPlayer());
				}
		}
	};

	public static ButtonClickHandler handleFamiliarOrbOption = new ButtonClickHandler(662) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getFamiliar() == null) {
				if (e.getPlayer().getPet() == null)
					return;
				if (e.getComponentId() == 49)
					e.getPlayer().getPet().call();
				else if (e.getComponentId() == 51)
					e.getPlayer().getDialogueManager().execute(new DismissD());
				return;
			}
			if (e.getComponentId() == 49)
				e.getPlayer().getFamiliar().call();
			else if (e.getComponentId() == 51)
				e.getPlayer().getDialogueManager().execute(new DismissD());
			else if (e.getComponentId() == 67)
				e.getPlayer().getFamiliar().takeBob();
			else if (e.getComponentId() == 69)
				e.getPlayer().getFamiliar().renewFamiliar();
			else if (e.getComponentId() == 74) {
				if (e.getPlayer().getFamiliar().getSpecialAttack() == SpecialAttack.CLICK)
					e.getPlayer().getFamiliar().setSpecial(true);
				if (e.getPlayer().getFamiliar().hasSpecialOn())
					e.getPlayer().getFamiliar().submitSpecial(e.getPlayer());
			}
		}
	};

	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
		if (hasEffect(Effect.FREEZE))
			return;
		int size = getSize();
		int targetSize = owner.getSize();
		if (WorldUtil.collides(getX(), getY(), size, owner.getX(), owner.getY(), targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size))
							return;
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!lineOfSightTo(owner, true) || !WorldUtil.isInRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0))
			calcFollow(owner, 2, true, false);
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		unlockOrb();
		trackTimer++;
		if (trackTimer == 50) {
			trackTimer = 0;
			ticks--;
			if (trackDrain)
				owner.getSkills().drainSummoning(1);
			trackDrain = !trackDrain;
			if (ticks == 2)
				owner.sendMessage("You have 1 minute before your familiar vanishes.");
			else if (ticks == 1)
				owner.sendMessage("You have 30 seconds before your familiar vanishes.");
			else if (ticks == 0) {
				removeFamiliar();
				dissmissFamiliar(false);
				return;
			}
			sendTimeRemaining();
		}
		int originalId = getOriginalId() + 1;
		if (owner.isCanPvp() && getId() == getOriginalId()) {
			transformIntoNPC(originalId);
			call(false);
			return;
		}
		if (!owner.isCanPvp() && getId() == originalId && pouch != Pouches.MAGPIE && pouch != Pouches.IBIS && pouch != Pouches.BEAVER && pouch != Pouches.MACAW && pouch != Pouches.FRUIT_BAT) {
			transformIntoNPC(originalId - 1);
			call(false);
			return;
		} else if (!withinDistance(owner, 12)) {
			call(false);
			return;
		}
		if (!getCombat().process())
			if (isAgressive() && owner.getAttackedBy() != null && owner.inCombat() && canAttack(owner.getAttackedBy()) && Utils.getRandomInclusive(25) == 0)
				getCombat().setTarget(owner.getAttackedBy());
			else
				sendFollow();
	}

	public boolean canAttack(Entity target) {
		if (target instanceof Player player) {
			if (!owner.isCanPvp() || !player.isCanPvp() || (owner == target))
				return false;
		}
		return !target.isDead() && ((owner.isAtMultiArea() && isAtMultiArea() && target.isAtMultiArea()) || (owner.isForceMultiArea() && target.isForceMultiArea())) && owner.getControllerManager().canAttack(target);
	}

	public boolean renewFamiliar() {
		if (ticks > 5) {
			owner.sendMessage("You need to have at least two minutes and fifty seconds remaining before you can renew your familiar.", true);
			return false;
		}
		if (!owner.getInventory().getItems().contains(new Item(pouch.getRealPouchId(), 1))) {
			owner.sendMessage("You need a " + ItemDefinitions.getDefs(pouch.getRealPouchId()).getName().toLowerCase() + " to renew your familiar's timer.");
			return false;
		}
		resetTickets();
		owner.getInventory().deleteItem(pouch.getRealPouchId(), 1);
		call(true);
		owner.sendMessage("You use your remaining pouch to renew your familiar.");
		return true;
	}

	public void takeBob() {
		if (bob == null)
			return;
		bob.takeBob();
	}

	public void sendTimeRemaining() {
		owner.getVars().setVar(1176, ticks * 65);
	}

	public void sendMainConfigs() {
		switchOrb(true);
		owner.getVars().setVar(448, pouch.getRealPouchId());// configures
		// familiar type
		// based on
		// pouch?
		owner.getVars().setVar(1160, 243269632); // sets npc emote
		refreshSpecialEnergy();
		sendTimeRemaining();
		owner.getVars().setVar(1175, getSpecialAmount() << 23);// check
		owner.getPackets().sendVarcString(204, getSpecialName());
		owner.getPackets().sendVarcString(205, getSpecialDescription());
		owner.getPackets().sendVarc(1436, getSpecialAttack() == SpecialAttack.CLICK ? 1 : 0);
		unlockOrb(); // temporary
	}

	public void sendFollowerDetails() {
		boolean res = owner.getInterfaceManager().hasRezizableScreen();
		owner.getInterfaceManager().setInterface(true, res ? 746 : 548, res ? 120 : 184, 662);
		owner.getPackets().setIFHidden(662, 44, true);
		owner.getPackets().setIFHidden(662, 45, true);
		owner.getPackets().setIFHidden(662, 46, true);
		owner.getPackets().setIFHidden(662, 47, true);
		owner.getPackets().setIFHidden(662, 48, true);
		owner.getPackets().setIFHidden(662, 71, false);
		owner.getPackets().setIFHidden(662, 72, false);
		unlock();
		owner.getPackets().sendVarc(168, 8);// tab id
	}

	public void switchOrb(boolean on) {
		owner.getVars().setVar(1174, on ? -1 : 0);
		if (on)
			unlock();
		else
			lockOrb();
	}

	public void unlockOrb() {
		owner.getPackets().setIFHidden(747, 9, false);
		sendLeftClickOption(owner);
	}

	public static void selectLeftOption(Player player) {
		boolean res = player.getInterfaceManager().hasRezizableScreen();
		player.getInterfaceManager().setInterface(true, res ? 746 : 548, res ? 120 : 184, 880);
		sendLeftClickOption(player);
		player.getPackets().sendVarc(168, 8);// tab id
	}

	public static void confirmLeftOption(Player player) {
		player.getPackets().sendVarc(168, 4);// inv tab id
		player.getInterfaceManager().removeWindowInterface(120, 184);
	}

	public static void setLeftclickOption(Player player, int summoningLeftClickOption) {
		if (summoningLeftClickOption == player.getSummoningLeftClickOption())
			return;
		player.setSummoningLeftClickOption(summoningLeftClickOption);
		sendLeftClickOption(player);
	}

	public static void sendLeftClickOption(Player player) {
		player.getVars().setVar(1493, player.getSummoningLeftClickOption());
		player.getVars().setVar(1494, player.getSummoningLeftClickOption());
	}

	public void unlock() {
		switch (getSpecialAttack()) {
		case CLICK:
			owner.getPackets().setIFTargetParams(new IFTargetParams(747, 18, 0, 0).enableRightClickOptions(0));
			owner.getPackets().setIFTargetParams(new IFTargetParams(662, 74, 0, 0).enableRightClickOptions(0));
			break;
		case ENTITY:
			owner.getPackets().setIFTargetParams(new IFTargetParams(747, 18, 0, 0).enableUseOptions(UseFlag.NPC,UseFlag.PLAYER));
			owner.getPackets().setIFTargetParams(new IFTargetParams(662, 74, 0, 0).enableUseOptions(UseFlag.NPC,UseFlag.PLAYER));
			break;
		case OBJECT:
		case ITEM:
			owner.getPackets().setIFTargetParams(new IFTargetParams(747, 18, 0, 0).enableUseOptions(UseFlag.ICOMPONENT));
			owner.getPackets().setIFTargetParams(new IFTargetParams(662, 74, 0, 0).enableUseOptions(UseFlag.ICOMPONENT));
			break;
		}
		owner.getPackets().setIFHidden(747, 9, false);
	}

	public void lockOrb() {
		owner.getPackets().setIFHidden(747, 9, true);
	}

	private transient boolean sentRequestMoveMessage;

	public void call() {
		if (getAttackedBy() != null && inCombat()) {
			owner.sendMessage("You cant call your familiar while it is under combat.");
			return;
		}
		call(false);
	}

	public void call(boolean login) {
		if (login) {
			if (bob != null)
				bob.setEntitys(owner, this);
			sendMainConfigs();
		} else
			removeTarget();
		WorldTile teleTile = null;
		teleTile = owner.getNearestTeleTile(this.getSize());
		if (login || teleTile != null)
			WorldTasks.schedule(() -> setNextSpotAnim(new SpotAnim(getDefinitions().size > 1 ? 1315 : 1314)));
		if (teleTile == null) {
			if (!sentRequestMoveMessage) {
				owner.sendMessage("Theres not enough space for your familiar appear.");
				sentRequestMoveMessage = true;
			}
			return;
		}
		sentRequestMoveMessage = false;
		setNextWorldTile(teleTile);
	}

	public void removeFamiliar() {
		owner.setFamiliar(null);
	}

	public void dissmissFamiliar(boolean logged) {
		finish();
		if (!logged && !isFinished()) {
			setFinished(true);
			switchOrb(false);
			owner.getInterfaceManager().removeWindowInterface(98, 212);
			owner.getPackets().setIFTargetParamsDefault(747, 18, 0, 0);
			if (bob != null)
				bob.dropBob();
		}
	}

	private transient boolean dead;

	@Override
	public void sendDeath(Entity source) {
		if (dead)
			return;
		dead = true;
		removeFamiliar();
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		setCantInteract(true);
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					owner.sendMessage("Your familiar slowly begins to fade away..");
					dissmissFamiliar(false);
				} else if (loop >= defs.getDeathDelay())
					stop();
				loop++;
			}
		}, 0, 1);
	}

	public void respawnFamiliar(Player owner) {
		this.owner = owner;
		initEntity();
		deserialize();
		call(true);
	}

	public abstract String getSpecialName();

	public abstract String getSpecialDescription();

	public abstract int getBOBSize();

	public abstract int getSpecialAmount();

	public abstract SpecialAttack getSpecialAttack();

	public abstract boolean submitSpecial(Object object);

	public boolean isAgressive() {
		return true;
	}

	public static enum SpecialAttack {
		ITEM, ENTITY, CLICK, OBJECT
	}

	public BeastOfBurden getBob() {
		return bob;
	}

	public void refreshSpecialEnergy() {
		owner.getVars().setVar(1177, specialEnergy);
	}

	public void restoreSpecialAttack(int energy) {
		if (specialEnergy >= 60)
			return;
		specialEnergy = energy + specialEnergy >= 60 ? 60 : specialEnergy + energy;
		refreshSpecialEnergy();
	}

	public void setSpecial(boolean on) {
		if (!on)
			owner.getTempAttribs().removeB("FamiliarSpec");
		else {
			if (specialEnergy < getSpecialAmount()) {
				owner.sendMessage("You familiar doesn't have enough special energy.");
				return;
			}
			owner.getTempAttribs().setB("FamiliarSpec", true);
		}
	}

	public void drainSpecial(int specialReduction) {
		specialEnergy -= specialReduction;
		if (specialEnergy < 0)
			specialEnergy = 0;
		refreshSpecialEnergy();
	}

	public void drainSpecial() {
		specialEnergy -= getSpecialAmount();
		refreshSpecialEnergy();
	}

	public boolean hasSpecialOn() {
		if (owner.getTempAttribs().removeB("FamiliarSpec")) {
			if (!owner.getInventory().containsItem(Summoning.getScrollId(pouch.getRealPouchId()), 1)) {
				owner.sendMessage("You don't have the scrolls to use this move.");
				return false;
			}
			owner.getInventory().deleteItem(Summoning.getScrollId(pouch.getRealPouchId()), 1);
			drainSpecial();
			return true;
		}
		return false;
	}

	public int getSpecialEnergy() {
		return specialEnergy;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean isFinished() {
		return finished;
	}
}
