package com.rs.game.content.quests.demonslayer;

import com.rs.engine.cutscene.Cutscene;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;

//TODO: Check Fight Arena, Dragon Slayer, Vampire Slayer, wally vs delrith cutscene, and the rest of the cutscenes
public class WallyVSDelrithCutscene extends Cutscene {
	@Override
	public void construct(Player player) {
		music(-1);
		fadeIn(0);
		hideMinimap();
		delay(4);
		dynamicRegion(player.getTile(), 401, 419, 4, 4);
		action(()-> player.getAppearance().transformIntoNPC(266));
		playerMove(19, 17, 0, Entity.MoveType.TELE);
		camShake(1, 0, 10, 5, 10);
		camPos(19, 17, 1300);
		camLook(19 + 4, 17 - 4, 50);
		delay(1);
		fadeOut(0);
		music(196);
		npcCreate("wally", 4664, 19 - 1, 17 - 5, player.getPlane());
		delay(1);
		dialogue(new Dialogue()
				.addNPC(882, HeadE.TALKING_ALOT, "Wally managed to arrive at the stone circle just as Delrith was summoned by a cult of chaos druids...")
			, true);
		npcMove("wally", Tile.of(19, 17 - 2, player.getPlane()), Entity.MoveType.RUN);
		dialogue(new Dialogue().addNPC(4664, HeadE.TALKING_ALOT, "Die foul demon!"));
		camLook(19, 17 - 4, 0, 4, 4);
		delay(1);
		npcAnim("wally", new Animation(12311));
		delay(1);
		npcAnim("wally", new Animation(2394));
		delay(1);
		npcAnim("wally", new Animation(16290));
		camLook(19, 17 - 3, 0, 1, 1);
		camPos(19, 17, 250, 0, 40);
		delay(2);
		dialogue(new Dialogue().addNPC(4664, HeadE.TALKING_ALOT, "Aber, Gabindo, Purchai, Camerinthum, and Carlem"), true);
		delay(3);
		fadeIn(0);
		delay(4);
		camPos(19 - 2, 17 - 5, 1450);
		camLook(19, 17 - 2, 600);
		delay(1);
		fadeOutQuickly(0);
		delay(1);
		npcFaceTile("wally", 19 - 1, 17 - 3, 0);
		npcAnim("wally", new Animation(12625));
		dialogue(new Dialogue().addNPC(4664, HeadE.TALKING_ALOT, "I am the greatest demon slayer EVER!"), true);
		dialogue(new Dialogue()
				.addNPC(4664, HeadE.TALKING_ALOT, "By reciting the correct magical incantation, and thrusting Silverlight into Delrith while he was newly " +
						"summoned, Wally was able to imprison Delrith in the stone block in the centre of the circle.")
				, true);
		fadeIn(0);
		delay(4);
		action(()->{player.getAppearance().transformIntoNPC(-1);});
		action(()->{player.setNextTile(getEndTile());});
		camShakeReset();
		delay(1);
		fadeOut(0);
		music(125);
		action(()->{player.getTempAttribs().setB("DemonSlayerCutscenePlayed", true);});
		dialogue(new GypsyArisDemonSlayerD(player, 1).getStart(), false);
	}
}
