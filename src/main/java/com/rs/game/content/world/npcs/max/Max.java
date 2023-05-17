package com.rs.game.content.world.npcs.max;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.db.WorldDB;
import com.rs.game.content.world.npcs.max.tasks.*;
import com.rs.game.model.entity.actions.EntityFollow;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.NPCBodyMeshModifier;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PluginEventHandler
public class Max extends NPC {
	
	public static final int NORM = 3373, PESTLE = 3374, FLETCH = 3380, SMITH = 3399, ADZE = 3705;
	private static int RANK_DISPLAY = 0;
	
	private Task task;
	private int sleepTicks;
	private int right = -1, left = -1;
	
	private int rank;
	private String displayName;
	private int cbLevel;

	public Max(int id, Tile tile) {
		super(id, tile);
		setRun(true);
		setIgnoreNPCClipping(true);
		transformIntoNPC(PESTLE);
		nextTask();
		rank = RANK_DISPLAY;
		RANK_DISPLAY++;
		updateName();
		setLoadsUpdateZones();
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
		if (task != null && sleepTicks-- <= 0)
			sleepTicks = task.tick(this);
		if (displayName != null)
			setPermName(displayName);
		if (cbLevel > 0)
			setCombatLevel(cbLevel);
		if (getTickCounter() % 300 == 0)
			updateName();
	}
	
	public String getRankColor() {
		return switch(rank) {
		case 0 -> "<img=7><col=FFD700>";
		case 1 -> "<img=6><col=C0C0C0>";
		case 2 -> "<img=5><col=CD7F30>";
		default -> "<col=FFFFF0>";
		};
	}
	
	private void updateName() {
		try {
			WorldDB.getHighscores().getPlayerAtPosition(rank, hs -> {
				try {
					displayName = getRankColor() + hs.getDisplayName() + "</col><col=FFFFFF> (total: " + hs.getTotalLevel() + ")</col>";
					cbLevel = getCombatLevel(
							Skills.getLevelForXp(Skills.ATTACK, hs.getXp()[Skills.ATTACK]),
							Skills.getLevelForXp(Skills.STRENGTH, hs.getXp()[Skills.STRENGTH]),
							Skills.getLevelForXp(Skills.DEFENSE, hs.getXp()[Skills.DEFENSE]),
							Skills.getLevelForXp(Skills.RANGE, hs.getXp()[Skills.RANGE]),
							Skills.getLevelForXp(Skills.MAGIC, hs.getXp()[Skills.MAGIC]),
							Skills.getLevelForXp(Skills.HITPOINTS, hs.getXp()[Skills.HITPOINTS]),
							Skills.getLevelForXp(Skills.PRAYER, hs.getXp()[Skills.PRAYER]),
							Skills.getLevelForXp(Skills.SUMMONING, hs.getXp()[Skills.SUMMONING]));
				} catch(Throwable e) {
					Logger.warn(Max.class, "updateName", "Error updating Max name for rank " + rank + " player.");
				}
			});
		} catch(Throwable e) {
			Logger.warn(Max.class, "updateName", "Error updating Max name for rank " + rank + " player.");
		}
	}

	public static int getCombatLevel(int atk, int str, int def, int range, int magic, int hp, int pray, int summ) {
		int meleeBased = atk + str;
		int rangeBased = (int) (range * 1.5);
		int magicBased = (int) (magic * 1.5);
		int realBase = meleeBased;
		if (rangeBased > realBase)
			realBase = rangeBased;
		if (magicBased > realBase)
			realBase = magicBased;
		realBase *= 1.3;
		realBase = ((realBase + def + hp + (pray / 2)) + (summ / 2)) / 4;
		return realBase;
	}
	
	public void wearItems(int weapon, int shield) {
		this.right = weapon;
		this.left = shield;
		setBodyMeshModifier(new NPCBodyMeshModifier(getDefinitions())
						 //head   body   legs   boots  gloves
				.addModels(65291, 62746, 62743, 53327, 2301, weapon == -1 ? -1 : ItemDefinitions.getDefs(weapon).maleEquip1, 65300, shield == -1 ? -1 : ItemDefinitions.getDefs(shield).maleEquip1, 252));
	}

	public String getCurrName() {
		return getCustomName() == null ? "Max" : getCustomName().substring(getCustomName().indexOf("0>")+2, getCustomName().indexOf("</"));
	}

	public void itemAnim(Animation anim, int delay) {
		int prevRight = right;
		int prevLeft = left;
		wearItems(anim.getDefs().rightHandItem, anim.getDefs().leftHandItem);
		setNextAnimation(anim);
		WorldTasks.delay(delay, () -> wearItems(prevRight, prevLeft));
	}

	public void nextTask() {
		getActionManager().forceStop();
		task = getNextPossibleTasks().get(0);
	}

	private List<Task> getNextPossibleTasks() {
		List<Task> nextTasks = new ArrayList<>();
		nextTasks.add(new MaxTaskWC());
		nextTasks.add(new MaxTaskFarm());
		nextTasks.add(new MaxTaskFM());
		nextTasks.add(new MaxTaskAlch());
		nextTasks.add(new MaxTaskSmith());
		nextTasks.add(new MaxTaskCook());
		Collections.shuffle(nextTasks);
		if (task == null)
			return nextTasks;
		return nextTasks.stream().filter(pred -> !pred.getClass().isAssignableFrom(task.getClass())).toList();
	}
	
	public static NPCClickHandler clickClose = new NPCClickHandler(new Object[] { NORM, PESTLE, FLETCH, SMITH, ADZE }, new String[] { "Talk-to", "Trade" }, e -> {
		if (!(e.getNPC() instanceof Max max))
			return;
		switch(e.getOption()) {
			case "Talk-to" -> e.getPlayer().startConversation(new MaxD(e.getPlayer(), max));
			case "Trade" -> e.getPlayer().sendMessage("Sending trade request...");
		}
	});

	public static NPCClickHandler clickDistance = new NPCClickHandler(false, new Object[] { NORM, PESTLE, FLETCH, SMITH, ADZE }, new String[] { "Follow" }, e -> {
		e.getPlayer().getActionManager().setAction(new EntityFollow(e.getNPC()));
	});
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { NORM, PESTLE, FLETCH, SMITH, ADZE }, (npcId, tile) -> new Max(npcId, tile));
}
