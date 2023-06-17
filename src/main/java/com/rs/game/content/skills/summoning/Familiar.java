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
package com.rs.game.content.skills.summoning;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.cache.loaders.interfaces.IFEvents.UseFlag;
import com.rs.engine.dialogue.Dialogue;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.skills.summoning.EnchantedHeadwear.Headwear;
import com.rs.game.content.skills.summoning.Summoning.ScrollTarget;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.actions.EntityFollow;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.item.ItemsContainer;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.GenericAttribMap;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public final class Familiar extends NPC {

	private static final int ITEMS_KEY = 530;
	
	public static final int CANCEL_SPECIAL = -2;
	public static final int DEFAULT_ATTACK_SPEED = -1;
	
	private transient Player owner;
	private transient boolean finished = false;
	private transient int forageTicks = 0;
	
	private int ticks;
	private int specialEnergy;
	private boolean specOn = false;
	private boolean trackDrain;
	private ItemsContainer<Item> inv;
	private Pouch pouch;
	private GenericAttribMap attribs = new GenericAttribMap();

	public Familiar(Player owner, Pouch pouch, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(pouch.getBaseNpc(), tile, false);
		this.owner = owner;
		this.pouch = pouch;
		setIgnoreNPCClipping(true);
		setRun(true);
		resetTickets();
		specialEnergy = 60;
		if (pouch.getBobSize() > 0)
			inv = new ItemsContainer<>(pouch.getBobSize(), false);
		anim(pouch.getSpawnAnim());
		call(true);
	}
	
	@Override
	public boolean blocksOtherNpcs() {
		return false;
	}

	public boolean hasInventory() {
		return inv != null;
	}
	
	public ItemsContainer<Item> getInventory() {
		return inv;
	}
	
	public static ButtonClickHandler handleInvInter = new ButtonClickHandler(665, e -> {
		if (e.getPlayer().getFamiliar() == null || e.getPlayer().getFamiliar().inv == null || e.getPlayer().getFamiliar().pouch.isForager())
			return;
		if (e.getComponentId() == 0)
			if (e.getPacket() == ClientPacket.IF_OP1)
				e.getPlayer().getFamiliar().addItem(e.getSlotId(), 1);
			else if (e.getPacket() == ClientPacket.IF_OP2)
				e.getPlayer().getFamiliar().addItem(e.getSlotId(), 5);
			else if (e.getPacket() == ClientPacket.IF_OP3)
				e.getPlayer().getFamiliar().addItem(e.getSlotId(), 10);
			else if (e.getPacket() == ClientPacket.IF_OP4)
				e.getPlayer().getFamiliar().addItem(e.getSlotId(), Integer.MAX_VALUE);
			else if (e.getPacket() == ClientPacket.IF_OP5)
				e.getPlayer().sendInputInteger("Enter Amount:", num -> e.getPlayer().getFamiliar().addItem(e.getSlotId(), num));
			else if (e.getPacket() == ClientPacket.IF_OP6)
				e.getPlayer().getInventory().sendExamine(e.getSlotId());
	});

	public static ButtonClickHandler handleInter = new ButtonClickHandler(671, e -> {
		if (e.getPlayer().getFamiliar() == null || e.getPlayer().getFamiliar().inv == null)
			return;
		if (e.getComponentId() == 27) {
			if (e.getPacket() == ClientPacket.IF_OP1)
				e.getPlayer().getFamiliar().removeItem(e.getSlotId(), 1);
			else if (e.getPacket() == ClientPacket.IF_OP2)
				e.getPlayer().getFamiliar().removeItem(e.getSlotId(), 5);
			else if (e.getPacket() == ClientPacket.IF_OP3)
				e.getPlayer().getFamiliar().removeItem(e.getSlotId(), 10);
			else if (e.getPacket() == ClientPacket.IF_OP4)
				e.getPlayer().getFamiliar().removeItem(e.getSlotId(), Integer.MAX_VALUE);
			else if (e.getPacket() == ClientPacket.IF_OP5)
				e.getPlayer().sendInputInteger("Enter Amount:", num -> e.getPlayer().getFamiliar().removeItem(e.getSlotId(), num));
		} else if (e.getComponentId() == 29)
			e.getPlayer().getFamiliar().takeInventory();
	});
	
	public static NPCClickHandler handleStore = new NPCClickHandler(Pouch.getAllNPCKeysWithInventory(), new String[] { "Store", "Take", "Withdraw" }, e -> {
		if (!(e.getNPC() instanceof Familiar familiar))
			return;
		if (familiar.getOwner() != e.getPlayer()) {
			e.getPlayer().sendMessage("This isn't your familiar.");
			return;
		}
		familiar.openInventory();
	});
	
	public void openInventory() {
		if (inv == null)
			return;
		owner.getInterfaceManager().sendInterface(671);
		owner.getPackets().sendItems(ITEMS_KEY, inv);
		owner.getPackets().setIFRightClickOps(671, 27, 0, ITEMS_KEY, 0, 1, 2, 3, 4, 5);
		owner.getPackets().sendInterSetItemsOptionsScript(671, 27, ITEMS_KEY, 6, 5, "Withdraw", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X", "Examine");
		if (!pouch.isForager()) {
			owner.getInterfaceManager().sendInventoryInterface(665);
			owner.getPackets().sendItems(93, owner.getInventory().getItems());
			owner.getPackets().setIFRightClickOps(665, 0, 0, 27, 0, 1, 2, 3, 4, 5);
			owner.getPackets().sendInterSetItemsOptionsScript(665, 0, 93, 4, 7, "Store", "Store-5", "Store-10", "Store-All", "Store-X", "Examine");
		}
	}
	
	public void dropInventory() {
		if (inv == null)
			return;
		Tile tile = Tile.of(getCoordFaceX(getSize()), getCoordFaceY(getSize()), getPlane());
		for (int i = 0; i < inv.getSize(); i++) {
			Item item = inv.get(i);
			if (item != null)
				World.addGroundItem(item, tile, owner, false, -1);
		}
		inv.reset();
	}

	public void takeInventory() {
		if (inv == null)
			return;
		Item[] itemsBefore = inv.getItemsCopy();
		for (int i = 0; i < inv.getSize(); i++) {
			Item item = inv.get(i);
			if (item != null) {
				if (!owner.getInventory().addItem(item))
					break;
				inv.remove(i, item);
			}
		}
		inv.shift();
		refreshItems(itemsBefore);
	}
	
	public void removeItem(Item item) {
		if (inv == null)
			return;
		Item[] itemsBefore = inv.getItemsCopy();
		inv.remove(item);
		refreshItems(itemsBefore);
	}
	
	public void removeItem(int slot, int amount) {
		if (inv == null)
			return;
		Item item = inv.get(slot);
		if (item == null)
			return;
		Item[] itemsBefore = inv.getItemsCopy();
		int maxAmount = inv.getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		int freeSpace = owner.getInventory().getFreeSlots();
		if (!item.getDefinitions().isStackable()) {
			if (freeSpace == 0) {
				owner.sendMessage("Not enough space in your inventory.");
				return;
			}
			if (freeSpace < item.getAmount()) {
				item.setAmount(freeSpace);
				owner.sendMessage("Not enough space in your inventory.");
			}
		} else if (freeSpace == 0 && !owner.getInventory().containsItem(item.getId(), 1)) {
			owner.sendMessage("Not enough space in your inventory.");
			return;
		}
		inv.remove(slot, item);
		inv.shift();
		owner.getInventory().addItem(item);
		refreshItems(itemsBefore);
	}
	
	@Override
	public void processEntity() {
		super.processEntity();
		if (isDead() || isCantInteract())
			return;
		if (forageTicks++ >= 50) {
			rollForage();
			forageTicks = 0;
		}
		pouch.tick(owner, this);
	}
	
	public void rollForage() {
		if (inv == null || inv.freeSlots() <= 0)
			return;
		pouch.rollForage(owner, inv);
	}

	public void addItem(int slot, int amount) {
		if (inv == null)
			return;
		Item item = owner.getInventory().getItem(slot);
		if (item == null)
			return;
		if (canStoreEssOnly() && item.getId() != 1436 && item.getId() != 7936) {
			owner.sendMessage("An abyssal familiar can only carry blank rune essence.");
			return;
		}
		if (!canStoreEssOnly() && (item.getId() == 1436 || item.getId() == 7936)) {
			owner.sendMessage("Only an abyssal familiar can carry blank rune essence.");
			return;
		}
		if (!ItemConstants.isTradeable(item) || item.getId() == 4049 || item.getDefinitions().getValue() > 50000) {
			owner.sendMessage("You cannot store this item.");
			return;
		}
		Item[] itemsBefore = inv.getItemsCopy();
		int maxAmount = owner.getInventory().getItems().getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		int freeSpace = inv.getFreeSlots();
		if (!item.getDefinitions().isStackable()) {
			if (freeSpace == 0) {
				owner.sendMessage("Not enough space in your familiar's inventory.");
				return;
			}

			if (freeSpace < item.getAmount()) {
				item.setAmount(freeSpace);
				owner.sendMessage("Not enough space in your familiar's inventory.");
			}
		} else if (freeSpace == 0 && !inv.containsOne(item)) {
			owner.sendMessage("Not enough space in your familiar's inventory.");
			return;
		}
		inv.add(item);
		inv.shift();
		owner.getInventory().deleteItem(slot, item);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		if (inv == null)
			return;
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = inv.array()[index];
			if (itemsBefore[index] != item)
				changedSlots[count++] = index;

		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refreshItems(finalChangedSlots);
	}

	public void refreshItems(int... slots) {
		if (inv == null)
			return;
		owner.getPackets().sendUpdateItems(ITEMS_KEY, inv, slots);
	}

	public boolean containsOneItem(int... itemIds) {
		if (inv == null)
			return false;
		for (int itemId : itemIds)
			if (inv.containsOne(new Item(itemId, 1)))
				return true;
		return false;
	}
	
	@Override
	public void setTarget(Entity target) {
		if (isPassive())
			return;
		super.setTarget(target);
	}
	
	public boolean isPassive() {
		return pouch.isPassive();
	}

	public boolean canStoreEssOnly() {
		return pouch == Pouch.ABYSSAL_LURKER || pouch == Pouch.ABYSSAL_PARASITE || pouch == Pouch.ABYSSAL_TITAN;
	}

	public int getOriginalId() {
		return pouch.getBaseNpc();
	}

	public void resetTickets() {
		ticks = pouch.getPouchTime();
	}

	public static ButtonClickHandler handleFamiliarOptionSettings = new ButtonClickHandler(880, e -> {
		if (e.getComponentId() >= 7 && e.getComponentId() <= 19)
			setLeftclickOption(e.getPlayer(), (e.getComponentId() - 7) / 2);
		else if (e.getComponentId() == 21)
			confirmLeftOption(e.getPlayer());
		else if (e.getComponentId() == 25)
			setLeftclickOption(e.getPlayer(), 7);
	});

	public static ButtonClickHandler summoningOrb = new ButtonClickHandler(747, e -> {
		if (e.getComponentId() == 8)
			selectLeftOption(e.getPlayer());
		else if (e.getPlayer().getPet() != null) {
			if (e.getComponentId() == 11 || e.getComponentId() == 20)
				e.getPlayer().getPet().call();
			else if (e.getComponentId() == 12 || e.getComponentId() == 21)
				e.getPlayer().startConversation(new DismissD(e.getPlayer()).getStart());
			else if (e.getComponentId() == 10 || e.getComponentId() == 19)
				e.getPlayer().getPet().sendFollowerDetails();
		} else if (e.getPlayer().getFamiliar() != null)
			if (e.getComponentId() == 11 || e.getComponentId() == 20)
				e.getPlayer().getFamiliar().call();
			else if (e.getComponentId() == 12 || e.getComponentId() == 21)
				e.getPlayer().startConversation(new DismissD(e.getPlayer()).getStart());
			else if (e.getComponentId() == 13 || e.getComponentId() == 22)
				e.getPlayer().getFamiliar().takeInventory();
			else if (e.getComponentId() == 14 || e.getComponentId() == 23)
				e.getPlayer().getFamiliar().renewFamiliar();
			else if (e.getComponentId() == 16)
				e.getPlayer().getFamiliar().interact();
			else if (e.getComponentId() == 19 || e.getComponentId() == 10)
				e.getPlayer().getFamiliar().sendFollowerDetails();
			else if (e.getComponentId() == 18) {
				if (e.getPlayer().getFamiliar().getPouch().getScroll().getTarget() == ScrollTarget.CLICK)
					e.getPlayer().getFamiliar().castSpecial(null);
				else if (e.getPlayer().getFamiliar().getPouch().getScroll().getTarget() == ScrollTarget.COMBAT)
					e.getPlayer().getFamiliar().setSpecActive(true);
			}
	});

	public static ButtonClickHandler followerInterface = new ButtonClickHandler(662, e -> {
		if (e.getPlayer().getFamiliar() == null) {
			if (e.getPlayer().getPet() == null)
				return;
			if (e.getComponentId() == 49)
				e.getPlayer().getPet().call();
			else if (e.getComponentId() == 51)
				e.getPlayer().startConversation(new DismissD(e.getPlayer()).getStart());
			return;
		}
		if (e.getComponentId() == 49)
			e.getPlayer().getFamiliar().call();
		else if (e.getComponentId() == 51)
			e.getPlayer().startConversation(new DismissD(e.getPlayer()).getStart());
		else if (e.getComponentId() == 67)
			e.getPlayer().getFamiliar().takeInventory();
		else if (e.getComponentId() == 69)
			e.getPlayer().getFamiliar().renewFamiliar();
		else if (e.getComponentId() == 74) {
			if (e.getPlayer().getFamiliar().getPouch().getScroll().getTarget() == ScrollTarget.CLICK)
				e.getPlayer().getFamiliar().castSpecial(null);
			else if (e.getPlayer().getFamiliar().getPouch().getScroll().getTarget() == ScrollTarget.COMBAT)
				e.getPlayer().getFamiliar().setSpecActive(true);
		}
	});
	
	public Pouch getPouch() {
		return pouch;
	}

	public void castSpecial(Object target) {
		if (!hasScroll()) {
			owner.sendMessage("You don't have any scrolls left.");
			return;
		}
		if (!hasEnergy() || owner.getTempAttribs().getL("familiarSpecTimer") > World.getServerTicks())
			return;
		if (executeSpecial(target)) {
			owner.getTempAttribs().setL("familiarSpecTimer", World.getServerTicks() + 4);
			owner.setNextAnimation(new Animation(7660));
			owner.setNextSpotAnim(new SpotAnim(1316));
			drainSpec();
			decrementScroll();
		}
	}
	
	public boolean executeSpecial(Object target) {
		switch(pouch.getScroll().getTarget()) {
		case CLICK:
			return pouch.getScroll().use(owner, this);
		case COMBAT:
			if (getTarget() != null && pouch.getScroll().onCombatActivation(owner, this, getTarget()))
				setSpecActive(true);
			return true;
		case ITEM:
			if (target instanceof Item i)
				return pouch.getScroll().item(owner, this, i);
			return false;
		case OBJECT:
			if (target instanceof GameObject o)
				return pouch.getScroll().object(owner, this, o);
			return false;
		case ENTITY:
			if (target instanceof Entity o)
				return pouch.getScroll().entity(owner, this, o);
			break;
		default:
			break;
		}
		return false;
	}
	
	public int getSpecCost() {
		if (owner.getEquipment().getCapeId() == 19893)
			return (int) ((double) pouch.getScroll().getPointCost() * 0.80);
		return pouch.getScroll().getPointCost();
	}
	
	public int castCombatSpecial(Entity target) {
		if (!isSpecOn())
			return CANCEL_SPECIAL;
		if (!hasScroll()) {
			owner.sendMessage("You don't have any scrolls left.");
			return CANCEL_SPECIAL;
		}
		if (!hasEnergy())
			return CANCEL_SPECIAL;
		int spec = executeCombatSpecial(target);
		if (spec != CANCEL_SPECIAL) {
			drainSpec();
			decrementScroll();
			setSpecActive(false);
			return spec == DEFAULT_ATTACK_SPEED ? getAttackSpeed() : spec;
		}
		return CANCEL_SPECIAL;
	}
	
	public int executeCombatSpecial(Entity target) {
		if (pouch.getScroll().getTarget() != ScrollTarget.COMBAT)
			return CANCEL_SPECIAL;
		return pouch.getScroll().attack(owner, this, target);
	}
	
	public boolean hasScroll() {
		if (owner.getNSV().getB("infSpecialAttack"))
			return true;
		if (owner.getInventory().containsItem(pouch.getScroll().getId()))
			return true;
		Item headwear = owner.getEquipment().get(Equipment.HEAD);
		if (headwear != null && headwear.getMetaDataI("summScrollId") == pouch.getScroll().getId())
			return headwear.getMetaDataI("summScrollsStored") > 0;
		return false;
	}
	
	public void decrementScroll() {
		Item item = owner.getEquipment().get(Equipment.HEAD);
		if (item != null && item.getMetaDataI("summScrollId") == pouch.getScroll().getId()) {
			item.decMetaDataI("summScrollsStored");
			if (item.getMetaDataI("summScrollsStored") <= 0) {
				item.deleteMetaData();
				Headwear headwear = Headwear.forId(item.getId());
				if (headwear != null) {
					item.setId(headwear.enchantedId);
					owner.getEquipment().refresh(Equipment.HEAD);
					owner.getAppearance().generateAppearanceData();
				}
			}
			return;
		}
		owner.getInventory().deleteItem(pouch.getScroll().getId(), 1);
	}

	@Override
	public void processNPC() {
		if (isDead() || isCantInteract())
			return;
		Familiar.sendLeftClickOption(owner);
		ticks--;
		if (ticks % 50 == 0) {
			if (trackDrain)
				owner.getSkills().drainSummoning(1);
			trackDrain = !trackDrain;
			if (ticks == 100)
				owner.sendMessage("You have 1 minute before your familiar vanishes.");
			else if (ticks == 50)
				owner.sendMessage("You have 30 seconds before your familiar vanishes.");
			sendTimeRemaining();
		}
		if (ticks == 0) {
			dismiss();
			return;
		}
		if (pouch.getPVPNpc() != -1 && owner.isCanPvp() && getId() != pouch.getPVPNpc()) {
			transformIntoNPC(pouch.getPVPNpc());
			call(false);
			return;
		}
		if (!owner.isCanPvp() && getId() != pouch.getBaseNpc()) {
			transformIntoNPC(pouch.getBaseNpc());
			call(false);
			return;
		}
		if (!withinDistance(owner, 12)) {
			call(false);
			return;
		}
		if (!getCombat().process())
			if (!getCombat().hasTarget() && isAgressive() && owner.getAttackedBy() != null && owner.inCombat() && canAttack(owner.getAttackedBy()) && Utils.getRandomInclusive(5) == 0)
				getCombat().setTarget(owner.getAttackedBy());
			else if (routeEvent == null && !isLocked() && !getActionManager().hasSkillWorking())
				getActionManager().setAction(new EntityFollow(owner));
	}

	public boolean canAttack(Entity target) {
		if (target instanceof Player player)
			if (!owner.isCanPvp() || !player.isCanPvp() || (owner == target))
				return false;
		return !target.isDead() && (target instanceof NPC n && n.isForceMultiAttacked()) || ((owner.isAtMultiArea() && isAtMultiArea() && target.isAtMultiArea()) || (owner.isForceMultiArea() && target.isForceMultiArea())) && owner.getControllerManager().canAttack(target);
	}

	public boolean renewFamiliar() {
		if (ticks > 200) {
			owner.sendMessage("You need to have at least two minutes remaining before you can renew your familiar.", true);
			return false;
		}
		if (!owner.getInventory().getItems().contains(new Item(pouch.getId(), 1))) {
			owner.sendMessage("You need a " + ItemDefinitions.getDefs(pouch.getId()).getName().toLowerCase() + " to renew your familiar's timer.");
			return false;
		}
		resetTickets();
		owner.getInventory().deleteItem(pouch.getId(), 1);
		call(true);
		owner.sendMessage("You use your remaining pouch to renew your familiar.");
		return true;
	}

	public void sendTimeRemaining() {
		owner.getVars().setVarBit(4534, ticks / 100);
		owner.getVars().setVarBit(4290, (ticks % 100) == 0 ? 0 : 1);
	}

	/**
	 * Var 448 sets to itemId of pouch then gets npc chathead from enum 1279 and stores it in var 1174
	 * Var 1174 set to 0 will refresh the head as 448 will recalculate from the enum
	 * 
	 * 
	 */
	public void sendMainConfigs() {
		owner.getVars().setVar(448, pouch.getId());// configures familiar type
		owner.getVars().setVar(1174, 0, true); //refresh familiar head
		owner.getVars().setVarBit(4282, pouch.getHeadAnimIndex()); //refresh familiar emote
		refreshSpecialEnergy();
		sendTimeRemaining();
		owner.getVars().setVarBit(4288, pouch.getScroll().getPointCost());// check
		owner.getPackets().sendVarcString(204, pouch.getScroll().getName());
		owner.getPackets().sendVarcString(205, pouch.getScroll().getDescription());
		owner.getPackets().sendVarc(1436, pouch.getScroll().getTarget() == ScrollTarget.CLICK || pouch.getScroll().getTarget() == ScrollTarget.COMBAT ? 1 : 0);
		owner.getPackets().sendRunScript(751);
		sendLeftClickOption(owner);
		sendOrbTargetParams();
	}

	public void sendFollowerDetails() {
		owner.getInterfaceManager().sendSub(Sub.TAB_FOLLOWER, 662);
		owner.getInterfaceManager().openTab(Sub.TAB_FOLLOWER);
	}

	public static void selectLeftOption(Player player) {
		player.getInterfaceManager().sendSub(Sub.TAB_FOLLOWER, 880);
		player.getInterfaceManager().openTab(Sub.TAB_FOLLOWER);
		sendLeftClickOption(player);
	}

	public static void confirmLeftOption(Player player) {
		player.getInterfaceManager().openTab(Sub.TAB_INVENTORY);
		player.getInterfaceManager().removeSub(Sub.TAB_FOLLOWER);
		sendLeftClickOption(player);
	}

	public static void setLeftclickOption(Player player, int summoningLeftClickOption) {
		if (summoningLeftClickOption == player.getSummoningLeftClickOption())
			return;
		player.setSummoningLeftClickOption(summoningLeftClickOption);
		sendLeftClickOption(player);
	}

	public static void sendLeftClickOption(Player player) {
		if (player.getFamiliar() == null)
			return;
		player.getVars().setVar(1493, player.getSummoningLeftClickOption());
		player.getVars().setVar(1494, player.getSummoningLeftClickOption());
		player.getPackets().setIFHidden(747, 9, false);
	}

	public void sendOrbTargetParams() {
		switch (pouch.getScroll().getTarget()) {
		case CLICK:
		case COMBAT:
			owner.getPackets().setIFEvents(new IFEvents(747, 18, 0, 0).enableRightClickOptions(0));
			owner.getPackets().setIFEvents(new IFEvents(662, 74, 0, 0).enableRightClickOptions(0));
			break;
		case OBJECT:
			owner.getPackets().setIFEvents(new IFEvents(747, 18, 0, 0).enableUseOptions(UseFlag.WORLD_OBJECT));
			owner.getPackets().setIFEvents(new IFEvents(662, 74, 0, 0).enableUseOptions(UseFlag.WORLD_OBJECT));
			break;
		case ENTITY:
			owner.getPackets().setIFEvents(new IFEvents(747, 18, 0, 0).enableUseOptions(UseFlag.NPC, UseFlag.PLAYER));
			owner.getPackets().setIFEvents(new IFEvents(662, 74, 0, 0).enableUseOptions(UseFlag.NPC, UseFlag.PLAYER));
			break;
		case ITEM:
			owner.getPackets().setIFEvents(new IFEvents(747, 18, 0, 0).enableUseOptions(UseFlag.ICOMPONENT));
			owner.getPackets().setIFEvents(new IFEvents(662, 74, 0, 0).enableUseOptions(UseFlag.ICOMPONENT));
			break;
		default:
			break;
		}
	}

	private transient boolean sentRequestMoveMessage;

	public void call() {
		if (isDead() || isCantInteract())
			return;
		call(false);
	}

	public void call(boolean login) {
		if (isDead() || isCantInteract())
			return;
		if (login)
			sendMainConfigs();
		else
			removeTarget();
		Tile teleTile = null;
		teleTile = owner.getNearestTeleTile(getSize());
		if (teleTile == null) {
			if (!sentRequestMoveMessage) {
				owner.sendMessage("Theres not enough space for your familiar to appear.");
				sentRequestMoveMessage = true;
			}
			return;
		}
		sentRequestMoveMessage = false;
		spotAnim(getSize() > 1 ? 1315 : 1314);
		setNextTile(teleTile);
		getActionManager().forceStop();
	}

	public void dismiss() {
		anim(pouch.getDespawnAnim());
		kill();
		WorldTasks.schedule(3, () -> finish());
	}
	
	public void kill() {
		resetWalkSteps();
		setCantInteract(true);
		getCombat().removeTarget();
		owner.setFamiliar(null);
		owner.getPackets().sendRunScript(2471);
		owner.getInterfaceManager().removeSub(Sub.TAB_FOLLOWER);
		dropInventory();
	}

	private transient boolean dead;

	@Override
	public void sendDeath(Entity source) {
		if (dead)
			return;
		dead = true;
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		setCantInteract(true);
		getCombat().removeTarget();
		anim(-1);
		kill();
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0) {
				anim(defs.getDeathEmote());
			} else if (loop >= defs.getDeathDelay()) {
				finish();
				return false;
			}
			return true;
		});
	}

	public void respawn(Player owner) {
		this.owner = owner;
		initEntity();
		deserialize();
		call(true);
	}

	public boolean isAgressive() {
		return pouch.getScroll().getTarget() == ScrollTarget.COMBAT;
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

	public boolean hasEnergy() {
		if (specialEnergy < getSpecCost()) {
			owner.sendMessage("Your familiar doesn't have enough special energy.");
			return false;
		}
		return true;
	}
	
	public void drainSpec(int specialReduction) {
		if (owner.getNSV().getB("infSpecialAttack"))
			return;
		specialEnergy -= specialReduction;
		if (specialEnergy < 0)
			specialEnergy = 0;
		refreshSpecialEnergy();
	}

	public void drainSpec() {
		drainSpec(getSpecCost());
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

	public GenericAttribMap getAttribs() {
		return attribs;
	}

	public boolean isSpecOn() {
		return specOn;
	}

	public void setSpecActive(boolean specOn) {
		this.specOn = specOn;
	}

	public void interact() {
		owner.startConversation(new Dialogue().addOptions("What would you like to do?", ops -> {
			if (inv != null)
				ops.add("Open Familiar Inventory", () -> openInventory());
			ops.add("Talk-to", Interactions.getTalkToDialogue(owner, this));
			Interactions.addExtraOps(owner, ops, this);
		}));
	}

	public boolean commandAttack(Entity target) {
		if (target instanceof Player player) {
			if (!owner.isCanPvp() || !player.isCanPvp()) {
				owner.sendMessage("You can only attack players in a player-vs-player area.");
				return false;
			}
			if (!owner.getFamiliar().canAttack(player)) {
				owner.sendMessage("You can only use your familiar in a multi-zone area.");
				return false;
			}
		} else if (target instanceof NPC npc) {
			if (!npc.getDefinitions().hasAttackOption()) {
				owner.sendMessage("You can't attack them.");
				return false;
			}
			if (target instanceof Familiar familiar) {
				if (familiar == this) {
					owner.sendMessage("You can't attack your own familiar.");
					return false;
				}
				if (!owner.getFamiliar().canAttack(familiar.getOwner())) {
					owner.sendMessage("You can only attack players in a player-vs-player area.");
					return false;
				}
			}
			if (!owner.getFamiliar().canAttack(target)) {
				owner.sendMessage("You can only use your familiar in a multi-zone area.");
				return false;
			}
		}
		owner.getFamiliar().setTarget(target);
		return true;
	}
}
