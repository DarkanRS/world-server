package com.rs.game.content.world.npcs.max;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.db.WorldDB;
import com.rs.game.content.skills.farming.FarmPatch;
import com.rs.game.content.world.npcs.max.tasks.Farming;
import com.rs.game.content.world.npcs.max.tasks.Task;
import com.rs.game.model.entity.actions.EntityFollow;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.NPCBodyMeshModifier;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Max extends NPC {
	
	public static final int NORM = 3373, PESTLE = 3374, FLETCH = 3380, SMITH = 3399, ADZE = 3705;
	
	private Task task;
	private int sleepTicks;
	private int right = -1, left = -1;
	
	private volatile String displayName;
	private volatile int cbLevel;

	public Max(int id, WorldTile tile) {
		super(id, tile);
		setRun(true);
		setIgnoreNPCClipping(true);
		task = new Farming();
		transformIntoNPC(PESTLE);
		checkTopPlayer();
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
		if (getTickCounter() % 15 == 0)
			checkTopPlayer();
	}
	
	private void checkTopPlayer() {
		try {
			WorldDB.getHighscores().getTopPlayer(hs -> {
				displayName = hs.getDisplayName() + "<col=FFFFFF> (total: " + hs.getTotalLevel() + ")</col>";
				cbLevel = getCombatLevel(
						Skills.getLevelForXp(Skills.ATTACK, hs.getXp()[Skills.ATTACK]),
						Skills.getLevelForXp(Skills.STRENGTH, hs.getXp()[Skills.STRENGTH]),
						Skills.getLevelForXp(Skills.DEFENSE, hs.getXp()[Skills.DEFENSE]),
						Skills.getLevelForXp(Skills.RANGE, hs.getXp()[Skills.RANGE]),
						Skills.getLevelForXp(Skills.MAGIC, hs.getXp()[Skills.MAGIC]),
						Skills.getLevelForXp(Skills.HITPOINTS, hs.getXp()[Skills.HITPOINTS]),
						Skills.getLevelForXp(Skills.PRAYER, hs.getXp()[Skills.PRAYER]),
						Skills.getLevelForXp(Skills.SUMMONING, hs.getXp()[Skills.SUMMONING]));
			});
		} catch(Throwable e) {
			e.printStackTrace();
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
	
	public static NPCClickHandler clickClose = new NPCClickHandler(new Object[] { NORM, PESTLE, FLETCH, SMITH, ADZE }, new String[] { "Talk-to", "Trade" }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (!(e.getNPC() instanceof Max max))
				return;
			switch(e.getOption()) {
				case "Talk-to" -> e.getPlayer().startConversation(new MaxD(e.getPlayer(), max));
				case "Trade" -> e.getPlayer().sendMessage("Sending trade request...");
			}
		}
	};

	public static NPCClickHandler clickDistance = new NPCClickHandler(false, new Object[] { NORM, PESTLE, FLETCH, SMITH, ADZE }, new String[] { "Follow" }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().getActionManager().setAction(new EntityFollow(e.getNPC()));
		}
	};
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(NORM, PESTLE, FLETCH, SMITH, ADZE) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Max(npcId, tile);
		}
	};
	
	public void wearItems(int weapon, int shield) {
		this.right = weapon;
		this.left = shield;
		setBodyMeshModifier(new NPCBodyMeshModifier(getDefinitions())
				.addModels(65291, 62746, 62743, 27738, 13307, weapon == -1 ? -1 : ItemDefinitions.getDefs(weapon).maleEquip1, 65300, shield == -1 ? -1 : ItemDefinitions.getDefs(shield).maleEquip1, 252));
	}

	public String getCurrName() {
		return getCustomName() == null ? "Max" : getCustomName().substring(0, getCustomName().indexOf("<"));
	}

	public void itemAnim(Animation anim, int delay) {
		int prevRight = right;
		int prevLeft = left;
		wearItems(anim.getDefs().rightHandItem, anim.getDefs().leftHandItem);
		setNextAnimation(anim);
		WorldTasks.delay(delay, () -> wearItems(prevRight, prevLeft));
	}
}
