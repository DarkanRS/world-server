package com.rs.game.player.content.skills.thieving;

import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.drop.DropTable;

public class PickPocketAction extends Action {

	private NPC npc;
	private PickPocketableNPC npcData;
	private static final Animation STUN_ANIMATION = new Animation(422),
	PICKPOCKETING_ANIMATION = new Animation(881),
	DOUBLE_LOOT_ANIMATION = new Animation(5074),
	TRIPLE_LOOT_ANIMATION = new Animation(5075),
	QUADRUPLE_LOOT_ANIMATION = new Animation(5078);

	private static final SpotAnim DOUBLE_LOOT_GFX = new SpotAnim(873),
	TRIPLE_LOOT_GFX = new SpotAnim(874),
	QUADRUPLE_LOOT_GFX = new SpotAnim(875);

	private int index;
	private boolean success = false;

	public PickPocketAction(NPC npc, PickPocketableNPC npcData) {
		this.npc = npc;
		this.npcData = npcData;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			success = successful(player);
			player.faceEntity(npc);
			if (npcData.equals(PickPocketableNPC.DESERT_PHOENIX))
				player.sendMessage("You attempt to grab the phoenix's tail-feather.");
			else
				player.sendMessage("You attempt to pick the " + npc.getDefinitions().getName().toLowerCase() + "'s pocket...");
			WorldTasksManager.delay(0, () -> {
				player.setNextAnimation(getAnimation());
				player.setNextSpotAnim(getGraphics());
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
			if (npcData.equals(PickPocketableNPC.DESERT_PHOENIX))
				player.sendMessage("You fail to grab the feather.");
			else
				player.sendMessage("You fail to pick the " + npc.getDefinitions().getName().toLowerCase() + "'s pocket.");
			npc.setNextAnimation(npcData.getStunAnimation() != null ? npcData.getStunAnimation() : STUN_ANIMATION);
			npc.faceEntity(player);
			player.setNextAnimation(new Animation(424));
			player.setNextSpotAnim(new SpotAnim(80, 5, 60));
			player.sendMessage("You've been stunned.");
			player.applyHit(new Hit(player, npcData.getStunDamage(), HitLook.TRUE_DAMAGE));
			if (npcData.equals(PickPocketableNPC.MASTER_FARMER) || npcData.equals(PickPocketableNPC.FARMER))
				npc.setNextForceTalk(new ForceTalk("Cor blimey mate, what are ye doing in me pockets?"));
			else if (npcData.equals(PickPocketableNPC.DESERT_PHOENIX))
				npc.setNextForceTalk(new ForceTalk("Squawk!"));
			else
				npc.setNextForceTalk(new ForceTalk("What do you think you're doing?"));
			stop(player);
		} else {
			if (npcData == PickPocketableNPC.DWARF_TRADER)
				Thieving.checkGuards(player);
			player.sendMessage("" + getMessage(player), true);
			double totalXp = npcData.getExperience();
			if (hasTheivingSuit(player))
				totalXp *= 1.025;
			player.incrementCount(npc.getDefinitions().getName()+" pickpocketed");
			player.getSkills().addXp(Constants.THIEVING, totalXp);
			for (int i = 0; i <= index; i++) {
				Item[] items = DropTable.calculateDrops(player, npcData.getLoot());
				for (int d = 0;d < items.length;d++)
					player.getInventory().addItem(items[d].getId(), items[d].getAmount());
			}
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
		if (!success) {
			player.lock(npcData.getStunTime());
		}
	}

	private boolean hasTheivingSuit(Player player) {
		if (player.getEquipment().getHatId() == 21482 && player.getEquipment().getChestId() == 21480 && player.getEquipment().getLegsId() == 21481 && player.getEquipment().getBootsId() == 21483)
			return true;
		return false;
	}

	private boolean successful(Player player) {
		if (!npcData.rollSuccess(player))
			return false;
		if (Utils.getRandomInclusive(50) < 5) {
			for (int i = 0; i < 4; i++) {
				if (npcData.getThievingLevels()[i] <= player.getSkills().getLevel(Constants.THIEVING) && npcData.getAgilityLevels()[i] <= player.getSkills().getLevel(Constants.AGILITY))
					index = i;
			}
		} else {
			index = 0;
		}
		return true;
	}

	private String getMessage(Player player) {
		if (npcData.equals(PickPocketableNPC.DESERT_PHOENIX))
			return "You grab a tail-feather.";
		else {
			switch (index) {
			case 0:
				return "You succesfully pick the " + npc.getDefinitions().getName().toLowerCase() + "'s pocket.";
			case 1:
				return "Your lighting-fast reactions allow you to steal double loot.";
			case 2:
				return "Your lighting-fast reactions allow you to steal triple loot.";
			case 3:
				return "Your lighting-fast reactions allow you to steal quadruple loot.";
			}
		}
		return null;
		
	}

	private boolean checkAll(Player player) {
		if (player.isDead() || player.hasFinished() || npc.isDead() || npc.hasFinished() || player.hasPendingHits())
			return false;
		if (player.getSkills().getLevel(Constants.THIEVING) < npcData.getThievingLevels()[0]) {
			player.getDialogueManager().execute(new SimpleMessage(), "You need a thieving level of " + npcData.getThievingLevels()[0] + " to steal from this npc.");
			return false;
		}
		if (player.getInventory().getFreeSlots() < 1) {
			player.sendMessage("You don't have enough space in your inventory.");
			return false;
		}
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

	private Animation getAnimation() {
		switch (index) {
		case 0:
			return PICKPOCKETING_ANIMATION;
		case 1:
			return DOUBLE_LOOT_ANIMATION;
		case 2:
			return TRIPLE_LOOT_ANIMATION;
		case 3:
			return QUADRUPLE_LOOT_ANIMATION;
		}
		return null;
	}

	private SpotAnim getGraphics() {
		switch (index) {
		case 0:
			return null;
		case 1:
			return DOUBLE_LOOT_GFX;
		case 2:
			return TRIPLE_LOOT_GFX;
		case 3:
			return QUADRUPLE_LOOT_GFX;
		}
		return null;
	}

}
