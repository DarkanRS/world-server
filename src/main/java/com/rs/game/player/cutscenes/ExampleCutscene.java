package com.rs.game.player.cutscenes;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.actions.ConstructMapAction;
import com.rs.game.player.cutscenes.actions.CreateNPCAction;
import com.rs.game.player.cutscenes.actions.CutsceneAction;
import com.rs.game.player.cutscenes.actions.DestroyCachedObjectAction;
import com.rs.game.player.cutscenes.actions.LookCameraAction;
import com.rs.game.player.cutscenes.actions.MoveNPCAction;
import com.rs.game.player.cutscenes.actions.MovePlayerAction;
import com.rs.game.player.cutscenes.actions.NPCAnimationAction;
import com.rs.game.player.cutscenes.actions.NPCFaceTileAction;
import com.rs.game.player.cutscenes.actions.NPCForceTalkAction;
import com.rs.game.player.cutscenes.actions.NPCGraphicAction;
import com.rs.game.player.cutscenes.actions.PlayerAnimationAction;
import com.rs.game.player.cutscenes.actions.PlayerFaceTileAction;
import com.rs.game.player.cutscenes.actions.PlayerForceTalkAction;
import com.rs.game.player.cutscenes.actions.PlayerGraphicAction;
import com.rs.game.player.cutscenes.actions.PlayerMusicEffectAction;
import com.rs.game.player.cutscenes.actions.PosCameraAction;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class ExampleCutscene extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	private static int GUTHIX = 1, GUARD1 = 2, GUARD2 = 3;

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		// first part
		actionsList.add(new ConstructMapAction(360, 482, 3, 3));
		actionsList.add(new PlayerMusicEffectAction(215, -1));
		actionsList.add(new MovePlayerAction(10, 0, 0, Player.TELE_MOVE_TYPE, 0)); // out
		actionsList.add(new LookCameraAction(10, 8, 1000, -1));
		actionsList.add(new PosCameraAction(10, 0, 2000, 3));
		actionsList.add(new CreateNPCAction(GUTHIX, 8008, 10, 6, 0, -1));
		actionsList.add(new NPCFaceTileAction(GUTHIX, 10, 5, -1));
		actionsList.add(new NPCGraphicAction(GUTHIX, new SpotAnim(184), 2));

		actionsList.add(new NPCForceTalkAction(GUTHIX, "....", 3));

		actionsList.add(new NPCForceTalkAction(GUTHIX, "GuthiXx!@!@!@!", -1));
		actionsList.add(new NPCAnimationAction(GUTHIX, new Animation(2108), 3)); // headbang

		actionsList.add(new NPCFaceTileAction(GUTHIX, 9, 6, -1));
		actionsList.add(new MovePlayerAction(9, 6, 0, Player.TELE_MOVE_TYPE, -1));
		actionsList.add(new PlayerFaceTileAction(9, 5, -1));
		actionsList.add(new PlayerAnimationAction(new Animation(2111), -1));
		actionsList.add(new PlayerGraphicAction(new SpotAnim(184), 1));

		actionsList.add(new DestroyCachedObjectAction(GUTHIX, 0));

		actionsList.add(new PlayerFaceTileAction(9, 7, 1));

		actionsList.add(new PlayerFaceTileAction(8, 6, 1));

		actionsList.add(new PlayerFaceTileAction(10, 6, 1));

		actionsList.add(new PlayerForceTalkAction("Huh?", 1));

		actionsList.add(new PlayerAnimationAction(new Animation(857), -1));
		actionsList.add(new PlayerForceTalkAction("Where am I?", 3));

		actionsList.add(new CreateNPCAction(GUARD1, 296, 3, 7, 0, -1)); // Todo
		actionsList.add(new CreateNPCAction(GUARD2, 298, 3, 5, 0, -1)); // Todo
		actionsList.add(new MoveNPCAction(GUARD1, 8, 7, false, 0));

		actionsList.add(new MoveNPCAction(GUARD2, 8, 5, false, 2));
		actionsList.add(new NPCForceTalkAction(GUARD1, "You! What are you doing here?", -1));

		actionsList.add(new PlayerFaceTileAction(8, 6, 3));

		actionsList.add(new PlayerForceTalkAction("Idk... Walking??", 2));

		actionsList.add(new NPCForceTalkAction(GUARD1, "You must have slipped", 1));

		actionsList.add(new NPCForceTalkAction(GUARD1, "and hit your head on the ice.", 1));

		actionsList.add(new NPCForceTalkAction(GUARD2, "Does it matter?", 1));

		actionsList.add(new NPCForceTalkAction(GUARD1, "Lets just take him to Falador...", 2));

		actionsList.add(new MoveNPCAction(GUARD1, 15, 7, false, -1));
		actionsList.add(new MovePlayerAction(15, 6, false, -1));
		actionsList.add(new MoveNPCAction(GUARD2, 15, 5, false, 0));

		actionsList.add(new PlayerForceTalkAction("What's Falador?", 0));

		actionsList.add(new NPCForceTalkAction(GUARD2, "Dammit...", 5));

		// second part
		actionsList.add(new ConstructMapAction(369, 421, 4, 6));
		actionsList.add(new PlayerMusicEffectAction(214, -1));
		actionsList.add(new CreateNPCAction(GUARD1, 296, 13, 39, 0, -1));
		actionsList.add(new CreateNPCAction(GUARD2, 298, 15, 39, 0, -1));
		actionsList.add(new MovePlayerAction(14, 38, 0, Player.TELE_MOVE_TYPE, 0));
		actionsList.add(new PosCameraAction(14, 5, 5000, -1));
		actionsList.add(new LookCameraAction(14, 20, 3000, -1));
		actionsList.add(new MovePlayerAction(14, 25, false, -1));
		actionsList.add(new MoveNPCAction(GUARD1, 13, 25, false, -1));
		actionsList.add(new MoveNPCAction(GUARD2, 15, 25, false, -1));
		actionsList.add(new PosCameraAction(14, 16, 4000, 6, 6, 10));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

}
