package com.rs.game.player.cutscenes;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.actions.CutsceneAction;
import com.rs.game.player.cutscenes.actions.DialogueAction;
import com.rs.game.player.cutscenes.actions.LookCameraAction;
import com.rs.game.player.cutscenes.actions.PosCameraAction;

public class CorporealBeastScene extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return true;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		actionsList.add(new LookCameraAction(2993, 4378, 1000, -1));
		actionsList.add(new PosCameraAction(2984, 4383, 5000, -1));
		actionsList.add(new DialogueAction("You peek through the door."));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

}
