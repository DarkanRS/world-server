package com.rs.game.content.skills.hunter.falconry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.content.DropCleaners;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Kebbit extends NPC {
	
	public enum KebbitType {
		SPOTTED(5098, 5094, 43, 42, 26, 310, 10125),
		DARK(5099, 5095, 57, 99, 0, 253, 10115),
		DASHING(5100, 5096, 69, 156, 0, 205, 10127);

		public final int kebbitId, caughtId, level, xp, rate1, rate99, furId;

		static Map<Integer, KebbitType> CATCH_MAP = new HashMap<>();
		static Map<Integer, KebbitType> CAUGHT_MAP = new HashMap<>();

		static {
			for (KebbitType k : KebbitType.values()) {
				CATCH_MAP.put(k.kebbitId, k);
				CAUGHT_MAP.put(k.caughtId, k);
			}
		}

		static KebbitType forKebbit(int npcId) {
			return CATCH_MAP.get(npcId);
		}

		static KebbitType forCaught(int npcId) {
			return CAUGHT_MAP.get(npcId);
		}

		KebbitType(int kebbitId, int caughtId, int level, int xp, int rate1, int rate99, int furId) {
			this.kebbitId = kebbitId;
			this.caughtId = caughtId;
			this.level = level;
			this.xp = xp;
			this.rate1 = rate1;
			this.rate99 = rate99;
			this.furId = furId;
		}
	}
	
	private KebbitType type;
	private int catchTimer = -1;
	private Player caughtBy;
	private int hintIcon = -1;

	public Kebbit(int id, Tile tile) {
		super(id, tile);
		this.type = KebbitType.forKebbit(id);
		if (this.type == null)
			throw new RuntimeException("Invalid falconry kebbit spawned somehow.");
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
		if (catchTimer-- == 0)
			expire();
	}
	
	@Override
	public void finish() {
		super.finish();
		resetValues();
	}

	private void expire() {
		returnBirdToCatcher();
		resetValues();
	}
	
	private void resetValues() {
		if (caughtBy != null && !caughtBy.hasFinished() && caughtBy.hasStarted() && caughtBy.getControllerManager().isIn(FalconryController.class)) {
			caughtBy.getEquipment().setNoPluginTrigger(Equipment.WEAPON, new Item(FalconryController.BIRD_GLOVE));
			caughtBy.getAppearance().generateAppearanceData();
			caughtBy.unlock();
			if (hintIcon != -1)
				caughtBy.getHintIconsManager().removeHintIcon(hintIcon);
		}
		transformIntoNPC(type.kebbitId);
		catchTimer = -1;
		hintIcon = -1;
		caughtBy = null;
	}

	public KebbitType getType() {
		return type;
	}
	
	public void returnBirdToCatcher() {
		if (caughtBy != null && !caughtBy.hasFinished() && caughtBy.hasStarted() && caughtBy.getControllerManager().isIn(FalconryController.class)) {
			final Player returnPlayer = caughtBy;
			if (hintIcon != -1)
				returnPlayer.getHintIconsManager().removeHintIcon(hintIcon);
			returnPlayer.lock();
			returnPlayer.soundEffect(2633);
			World.sendProjectile(this, caughtBy, 922, 24, 8, 0, 1.0, 15, 15, backProj -> {
				unlock();
				returnPlayer.unlock();
				returnPlayer.getEquipment().setNoPluginTrigger(Equipment.WEAPON, new Item(FalconryController.BIRD_GLOVE));
				returnPlayer.getAppearance().generateAppearanceData();
			});
		}
		hintIcon = -1;
		caughtBy = null;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(Arrays.stream(KebbitType.values()).map(k -> k.kebbitId).toArray(), (npcId, tile) -> new Kebbit(npcId, tile));

	public void sendFalcon(Player player) {
		if (player.getSkills().getLevel(Skills.HUNTER) < type.level) {
			player.sendMessage("You need a hunter level of " + type.level + " to catch those.");
			return;
		}
		if (player.getEquipment().getWeaponId() != FalconryController.BIRD_GLOVE) {
			player.sendMessage("Your bird is waiting on a catch already.");
			return;
		}
		if (isLocked() || catchTimer >= 0) {
			player.sendMessage("This kebbit is already being targeted.");
			return;
		}
		resetWalkSteps();
		lock();
		player.lock();
		player.faceEntity(this);
		player.getEquipment().setNoPluginTrigger(Equipment.WEAPON, new Item(FalconryController.EMPTY_GLOVE));
		player.getAppearance().generateAppearanceData();
		player.soundEffect(2634);
		World.sendProjectile(player, this, 922, 24, 8, 0, 1.0, 15, 15, toProj -> {
			if (Utils.skillSuccess(player.getSkills().getLevel(Skills.HUNTER), type.rate1, type.rate99)) {
				hintIcon = player.getHintIconsManager().addHintIcon(this, 0, -1, false);
				catchTimer = Ticks.fromSeconds(60);
				caughtBy = player;
				transformIntoNPC(type.caughtId);
				unlock();
				player.unlock();
				return;
			}
			player.soundEffect(2633);
			World.sendProjectile(this, player, 922, 24, 8, 0, 1.0, 15, 15, backProj -> {
				unlock();
				player.unlock();
				player.getEquipment().setNoPluginTrigger(Equipment.WEAPON, new Item(FalconryController.BIRD_GLOVE));
				player.getAppearance().generateAppearanceData();
			});
		});
	}

	public void loot(Player player) {
		if (caughtBy != player) {
			player.sendMessage("This isn't your catch to loot.");
			return;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("You don't have enough space in your inventory.");
			return;
		}
		player.incrementCount(NPCDefinitions.getDefs(type.kebbitId).getName() + " hunted at falconry");
		player.faceEntity(this);
		player.lock();
		player.anim(827);
		player.getSkills().addXp(Skills.HUNTER, type.xp);
		player.getInventory().addItemDrop(type.furId, 1);
		if (!DropCleaners.Companion.bonecrush(player, new Item(526, 1)))
			player.getInventory().addItemDrop(526, 1);
		sendDeath(player);
	}
}
