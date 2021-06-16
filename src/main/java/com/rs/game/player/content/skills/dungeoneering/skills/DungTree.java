package com.rs.game.player.content.skills.dungeoneering.skills;

import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;

public enum DungTree {
	TANGLE_GUM_VINE(1, 35, 17682, 150, 255),
	SEEPING_ELM_TREE(10, 60, 17684, 150, 240),
	BLOOD_SPINDLE_TREE(20, 85, 17686, 150, 230),
	UTUKU_TREE(30, 115, 17688, 150, 220),
	SPINEBEAM_TREE(40, 145, 17690, 150, 210),
	BOVISTRANGLER_TREE(50, 175, 17692, 150, 200),
	THIGAT_TREE(60, 210, 17694, 150, 190),
	CORPESTHORN_TREE(70, 245, 17696, 150, 180),
	ENTGALLOW_TREE(80, 285, 17698, 150, 170),
	GRAVE_CREEPER_TREE(90, 330, 17700, 150, 170);
	
	private int level;
	private double xp;
	private int logsId;
	private int rate1;
	private int rate99;

	private DungTree(int level, double xp, int logsId, int rate1, int rate99) {
		this.level = level;
		this.xp = xp;
		this.logsId = logsId;
		this.rate1 = rate1;
		this.rate99 = rate99;
	}

	public int getLevel() {
		return level;
	}

	public double getXp() {
		return xp;
	}

	public int getLogsId() {
		return logsId;
	}
	
	public boolean rollSuccess(int level, DungHatchet hatchet) {
		return Utils.skillSuccess(level, hatchet.getToolMod(), rate1, rate99);
	}
	
	public void giveLog(Player player) {
		player.getInventory().addItem(logsId, 1);
		player.getSkills().addXp(Constants.WOODCUTTING, xp);
	}
}
