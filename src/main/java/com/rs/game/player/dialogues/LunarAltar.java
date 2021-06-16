package com.rs.game.player.dialogues;

import com.rs.game.player.quests.Quest;

public class LunarAltar extends Dialogue {

	@Override
	public void start() {
		if (!Quest.LUNAR_DIPLOMACY.meetsRequirements(player, "to use the Lunar Spellbook."))
			end();
		else
			sendOptionsDialogue("Change spellbooks?", "Yes, replace my spellbook.", "Never mind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			if (!Quest.LUNAR_DIPLOMACY.meetsRequirements(player, "to use the Lunar Spellbook."))
				return;
			if (player.getCombatDefinitions().getSpellBook() != 430) {
				sendDialogue("Your mind clears and you switch", "back to the lunar spellbook.");
				player.getCombatDefinitions().setSpellBook(2);
			} else {
				sendDialogue("Your mind clears and you switch", "back to the normal spellbook.");
				player.getCombatDefinitions().setSpellBook(0);
			}
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
