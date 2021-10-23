package com.rs.game.player.content;

import java.util.Arrays;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.lib.game.Rights;

public final class ClanCapeCustomizer {

	private ClanCapeCustomizer() {

	}

	public static void resetClanCapes(Player player) {
		player.setClanCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(20708).modifiedModelColors, 4));
		int[] clantex = new int[2];
		clantex[0] = ItemDefinitions.getDefs(20708).modifiedTextureIds[0];
		clantex[1] = ItemDefinitions.getDefs(20708).modifiedTextureIds[1];
		player.setClanCapeSymbols(clantex);
	}

	public static void startCustomizing(Player player) {
		int[] skillCape = player.getClanCapeCustomized();
		player.getInterfaceManager().sendInterface(1105);
		for (int i = 0; i < 4; i++)
			player.getVars().setVarBit(9254 + i, skillCape[i]);
		player.getPackets().setIFModel(1105, 55, player.getAppearance().isMale() ? ItemDefinitions.getDefs(20708).getMaleWornModelId1() : ItemDefinitions.getDefs(20708).getFemaleWornModelId1());
	}

	public static void handleSkillCapeCustomizerColor(Player player, int colorId) {
		if (player.hasRights(Rights.DEVELOPER)) {
			player.sendMessage("Customize color: " + colorId);
		}

		Integer part = (Integer) player.getTempAttribs().get("ClanCapeCustomizeID");
		if (part == null)
			return;
		int[] skillCape = player.getClanCapeCustomized();
		skillCape[part] = colorId;
		player.getVars().setVarBit(9254 + part, colorId);
		player.getInterfaceManager().sendInterface(20);
	}

	public static void handleSkillCapeCustomizer(Player player, int buttonId) {
		if (player.hasRights(Rights.DEVELOPER)) {
			player.sendMessage("Customize button: " + buttonId);
		}

		int[] skillCape = player.getClanCapeCustomized();
		if (buttonId == 58) { // reset
			player.setClanCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(20708).modifiedModelColors, 4));
			for (int i = 0; i < 4; i++)
				player.getVars().setVarBit(9254 + i, skillCape[i]);
		} else if (buttonId == 34) { // detail top
			player.getTempAttribs().put("SkillcapeCustomize", 0);
			player.getInterfaceManager().sendInterface(19);
			player.getVars().setVar(2174, skillCape[0]);
		} else if (buttonId == 71) { // background top
			player.getTempAttribs().put("SkillcapeCustomize", 1);
			player.getInterfaceManager().sendInterface(19);
			player.getVars().setVar(2174, skillCape[1]);
		} else if (buttonId == 83) { // detail button
			player.getTempAttribs().put("SkillcapeCustomize", 2);
			player.getInterfaceManager().sendInterface(19);
			player.getVars().setVar(2174, skillCape[2]);
		} else if (buttonId == 95) { // background button
			player.getTempAttribs().put("SkillcapeCustomize", 3);
			player.getInterfaceManager().sendInterface(19);
			player.getVars().setVar(2174, skillCape[3]);
		} else if (buttonId == 114 || buttonId == 142) { // done / close
			player.getAppearance().generateAppearanceData();
			player.closeInterfaces();
		}
	}
}
