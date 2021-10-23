package com.rs.game.player.cutscenes;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.actions.CutsceneAction;

public class Example2Cutscene extends Cutscene {
    static final int WALLY = 4664;
    static final int GYPSY_ARIS = 882;

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	@Override
	public CutsceneAction[] getActions(Player p) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

}
