// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.quests.demonslayer;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.InstancedController;
import com.rs.lib.game.Tile;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

import java.util.stream.Stream;

public class PlayerVSDelrithController extends InstancedController {
	private static final Tile LOCATION_ON_DEATH = Tile.of(3211, 3382, 0);
	static final int DELRITH = 879;
	static final int DARK_WIZARD7 = 8872;
	static final int DARK_WIZARD20 = 8873;
	static final int DENATH = 4663;

	// Wizard spell animations
	static final int SPELL1 = 707;
	static final int SPELL2 = 718;
	static final int SPELL3 = 717;
	static final int SPELL4 = 711;

	// Delrith animation
	static final int RESURRECT = 4623;

    boolean ambientMusicOn = false;

	public PlayerVSDelrithController() {
		super(Instance.of(LOCATION_ON_DEATH, 4, 4).setEntranceOffset(new int[] { 19, 17, 0 }));
	}

	@Override
    public Genre getGenre() {
        return Music.getGenreByName("Other Dungeons");
    }

    @Override
    public boolean playAmbientOnControllerRegionEnter() {
        return false;
    }

    @Override
    public boolean playAmbientMusic() {
        return ambientMusicOn;
    }

	@Override
	public void onBuildInstance() {
		int spawnX = 19, spawnY = 17;
		int endX = 15, endY = 20;
		player.lock();
		getInstance().copyMapAllPlanes(401, 419).thenAccept(b -> player.playCutscene(cs -> {
			cs.fadeIn(5);
			cs.action(() -> {
				getInstance().teleportLocal(player, spawnX, spawnY, 0);
				player.musicTrack(195);
				player.getPackets().setBlockMinimapState(2);
				player.getAppearance().transformIntoNPC(266);
				cs.setEndTile(Tile.of(cs.getX(endX), cs.getY(endY), 0));
			});
			cs.delay(1);
			cs.camPos(spawnX-4, spawnY+6, 2000);
			cs.camLook(spawnX, spawnY, 50);
			cs.delay(1);
			cs.camPos(spawnX, spawnY+6, 2000, 0, 5);
			cs.npcCreate("w1", DARK_WIZARD7, spawnX-1, spawnY+2, 0);
			cs.npcCreate("w2", DARK_WIZARD20, spawnX+2, spawnY+2, 0);
			cs.npcCreate("w3", DARK_WIZARD20, spawnX-1, spawnY-1, 0);
			cs.npcCreate("denath", DENATH, spawnX+2, spawnY-1, 0);
			cs.action(() -> {
				Stream.of("w1", "w2", "w3", "denath").forEach(label -> {
					if (!label.equals("denath"))
						cs.getNPC(label).persistBeyondCutscene();
					cs.getNPC(label).setRandomWalk(false);
					cs.getNPC(label).faceTile(Tile.of(cs.getX(spawnX), cs.getY(spawnY), 0));
				});
			});
			cs.fadeOut(5);
			cs.delay(1);
			cs.dialogue(new Dialogue().addNPC(DENATH, HeadE.EVIL_LAUGH, "Arise, O mighty Delrith! Bring destruction to this soft weak city!"));
			cs.action(1, () -> Stream.of("w1", "w2", "w3", "denath").forEach(label -> cs.getNPC(label).anim(SPELL1)));
			cs.action(1, () -> Stream.of("w1", "w2", "w3", "denath").forEach(label -> cs.getNPC(label).anim(SPELL2)));
			cs.dialogue(new Dialogue().addNPC(DARK_WIZARD7, HeadE.EVIL_LAUGH, "Arise Delrith!"));
			cs.action(1, () -> Stream.of("w1", "w2", "w3", "denath").forEach(label -> {
				cs.getNPC(label).anim(SPELL1);
				cs.getNPC(label).forceTalk("Arise Delrith!");
			}));
			cs.action(1, () -> Stream.of("w1", "w2", "w3", "denath").forEach(label -> cs.getNPC(label).anim(SPELL3)));
			cs.dialogue(new Dialogue().addSimple("The wizards cast an evil spell..."), 1);
			cs.camPos(spawnX, spawnY-4, 1500);
			cs.camLook(spawnX, spawnY+1, 50);
			cs.camShake(3, 100, 1, 30, 1);
			cs.npcCreate("delrith", DELRITH, Tile.of(cs.getX(spawnX), cs.getY(spawnY), 0));
			cs.npcFaceDir("delrith", Direction.SOUTHEAST);
			cs.npcAnim("delrith", RESURRECT);
			cs.action(() -> {
				cs.getNPC("delrith").persistBeyondCutscene();
				cs.getNPC("delrith").setRandomWalk(false);
			});
			cs.delay(1);
			cs.npcTalk("delrith", "RaawRRgh!");
			Stream.of("w1", "w2", "w3", "denath").forEach(label -> {
				cs.npcSpotAnim(label, 108);
				cs.npcAnim(label, SPELL4);
			});
			cs.delay(1);
			cs.action(() -> player.getVars().setVarBit(2569, 1));
			cs.camShake(3, 0, 0, 0, 0);
			cs.camLook(spawnX, spawnY+10, 250, 0, 1);
			cs.delay(1);
			cs.npcWalk("delrith", spawnX, spawnY-2);
			cs.delay(1);
			cs.camPos(spawnX-4, spawnY+6, 2000);
			cs.camLook(spawnX, spawnY-1, 50);
			cs.delay(1);
			Stream.of("w1", "w2", "w3", "denath").forEach(label -> cs.npcFaceNPC(label, "delrith"));
			cs.dialogue(new Dialogue()
					.addNPC(DENATH, HeadE.EVIL_LAUGH, "Ha ha ha! At last you are free, my demonic brother! Rest now and then have your revenge on this pitiful city!")
					.addNPC(DENATH, HeadE.EVIL_LAUGH, "We will destroy-"), true);
			cs.npcFaceDir("delrith", Direction.NORTHWEST);
			Stream.of("w1", "w2", "w3", "denath").forEach(label -> cs.npcFaceDir(label, Direction.NORTHWEST));
			cs.dialogue(new Dialogue()
					.addNPC(DENATH, HeadE.SCARED, "Noo! Not Silverlight! Delrith is not ready yet!")
					.addNPC(DENATH, HeadE.SCARED, "I've got to get out of here."), true);
			cs.npcFaceNPC("denath", null);
			cs.npcWalk("denath", spawnX+13, spawnY);
			cs.delay(3);
			cs.playerMove(endX, endY, Entity.MoveType.TELE);
			cs.camPosReset();
			cs.action(() -> player.getAppearance().transformIntoNPC(-1));
			cs.delay(2);
			cs.action(() -> {
				ambientMusicOn = true;
				player.unlock();
				player.setForceMultiArea(true);
				Stream.of("w1", "w2", "w3").forEach(label -> {
					cs.getNPC(label).setRandomWalk(true);
					cs.getNPC(label).setForceMultiArea(true);
					cs.getNPC(label).setForceAggroDistance(20);
					cs.getNPC(label).setTarget(player);
				});
				cs.getNPC("delrith").setRandomWalk(true);
				cs.getNPC("delrith").setForceMultiArea(true);
				cs.getNPC("delrith").setForceAggroDistance(20);
				cs.getNPC("delrith").setTarget(player);
			});
		}));
	}

	@Override
	public void onDestroyInstance() {
		player.getPackets().setBlockMinimapState(0);
		player.setForceMultiArea(false);
		player.getTempAttribs().setB("FinalDemonSlayerCutscene", false);
		player.unlock();
	}

	@Override
	public boolean sendDeath() {
		player.stopAll();
		player.reset();
		player.sendMessage("You have been defeated!");
		Magic.sendNormalTeleportSpell(player, LOCATION_ON_DEATH);
		player.getVars().setVarBit(2569, 0);
		return false;
	}
}
