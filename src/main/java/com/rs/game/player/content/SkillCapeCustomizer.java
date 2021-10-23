package com.rs.game.player.content;

import java.util.Arrays;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.lib.game.Rights;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class SkillCapeCustomizer {

	public static void resetSkillCapes(Player player) {
		player.setMaxedCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(20767).originalModelColors, 4));
		player.setCompletionistCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(20769).originalModelColors, 4));

		player.setClanCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(20708).modifiedModelColors, 4));
		player.setClanCapeSymbols(new int[] { ItemDefinitions.getDefs(20708).modifiedTextureIds[0], ItemDefinitions.getDefs(20708).modifiedTextureIds[1] });
	}

	public static void startCustomizing(Player player, int itemId) {
		player.getTempAttribs().setI("SkillcapeCustomizeId", itemId);
		int[] skillCape = itemId == 20767 ? player.getMaxedCapeCustomized() : player.getCompletionistCapeCustomized();
		player.getInterfaceManager().sendInterface(20);
		for (int i = 0; i < 4; i++)
			player.getVars().setVarBit(9254 + i, skillCape[i]);
		player.getPackets().setIFModel(20, 55, player.getAppearance().isMale() ? ItemDefinitions.getDefs(itemId).getMaleWornModelId1() : ItemDefinitions.getDefs(itemId).getFemaleWornModelId1());
	}

	public static int getCapeId(Player player) {
		return player.getTempAttribs().getI("SkillcapeCustomizeId");
	}

	public static void handleSkillCapeCustomizerColor(Player player, int colorId) {
		if (player.hasRights(Rights.DEVELOPER)) {
			player.sendMessage("Customize color: " + colorId);
		}

		int capeId = getCapeId(player);
		if (capeId == -1)
			return;
		int part = player.getTempAttribs().getI("SkillcapeCustomize");
		if (part == -1)
			return;
		int[] skillCape = capeId == 20767 ? player.getMaxedCapeCustomized() : player.getCompletionistCapeCustomized();
		skillCape[part] = colorId;
		player.getVars().setVarBit(9254 + part, colorId);
		player.getInterfaceManager().sendInterface(20);
	}

	public static ButtonClickHandler handleSkillCapeCustomizer = new ButtonClickHandler(20) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().hasRights(Rights.DEVELOPER)) {
				e.getPlayer().sendMessage("Customize button: " + e.getComponentId());
			}

			int capeId = getCapeId(e.getPlayer());
			if (capeId == -1)
				return;

			int[] skillCape = capeId == 20767 ? e.getPlayer().getMaxedCapeCustomized() : e.getPlayer().getCompletionistCapeCustomized();
			if (e.getComponentId() == 58) { // reset
				if (capeId == 20767)
					e.getPlayer().setMaxedCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(capeId).originalModelColors, 4));
				else
					e.getPlayer().setCompletionistCapeCustomized(Arrays.copyOf(ItemDefinitions.getDefs(capeId).originalModelColors, 4));
				for (int i = 0; i < 4; i++)
					e.getPlayer().getVars().setVarBit(9254 + i, skillCape[i]);
			} else if (e.getComponentId() == 34) { // detail top
				e.getPlayer().getTempAttribs().setI("SkillcapeCustomize", 0);
				e.getPlayer().getInterfaceManager().sendInterface(19);
				e.getPlayer().getVars().setVar(2174, skillCape[0]);
			} else if (e.getComponentId() == 71) { // background top
				e.getPlayer().getTempAttribs().setI("SkillcapeCustomize", 1);
				e.getPlayer().getInterfaceManager().sendInterface(19);
				e.getPlayer().getVars().setVar(2174, skillCape[1]);
			} else if (e.getComponentId() == 83) { // detail button
				e.getPlayer().getTempAttribs().setI("SkillcapeCustomize", 2);
				e.getPlayer().getInterfaceManager().sendInterface(19);
				e.getPlayer().getVars().setVar(2174, skillCape[2]);
			} else if (e.getComponentId() == 95) { // background button
				e.getPlayer().getTempAttribs().setI("SkillcapeCustomize", 3);
				e.getPlayer().getInterfaceManager().sendInterface(19);
				e.getPlayer().getVars().setVar(2174, skillCape[3]);
			} else if (e.getComponentId() == 114 || e.getComponentId() == 142) { // done / close
				e.getPlayer().getAppearance().generateAppearanceData();
				e.getPlayer().closeInterfaces();
			}
		}
	};
}
