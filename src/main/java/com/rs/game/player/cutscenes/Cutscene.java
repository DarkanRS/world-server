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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.cutscenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.game.Entity.MoveType;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.cutscenes.actions.*;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public abstract class Cutscene {
	private Player player;
	private int currIndex;
	private Map<String, Object> objects = new HashMap<>();
	private List<CutsceneAction> actions = new ArrayList<>();
	private int delay;
	private boolean hideMap;
	private boolean dialoguePaused;
	private boolean constructingRegion;
	private DynamicRegionReference region;
	private WorldTile endTile;
	
	public abstract void construct(Player player);

	public final void stopCutscene() {
		if (player.getX() != endTile.getX() || player.getY() != endTile.getY() || player.getPlane() != endTile.getPlane())
			player.setNextWorldTile(endTile);
		if (hideMap)
			player.getPackets().setBlockMinimapState(0);
		player.getVars().setVar(1241, 3);
		player.getPackets().sendResetCamera();
		player.setLargeSceneView(false);
		player.unlock();
		deleteObjects();
		if (region != null)
			region.destroy();
		player.delete("cutsceneManagerStartTileX");
		player.delete("cutsceneManagerStartTileY");
		player.delete("cutsceneManagerStartTileZ");
	}

	public final void startCutscene() {
		if (hideMap)
			player.getPackets().setBlockMinimapState(2);
		player.getVars().setVar(1241, 1);
		player.setLargeSceneView(true);
		player.lock();
		player.stopAll(true, false);
		WorldTile tile = new WorldTile(player);
		player.save("cutsceneManagerStartTileX", tile.getX());
		player.save("cutsceneManagerStartTileY", tile.getY());
		player.save("cutsceneManagerStartTileZ", tile.getPlane());
	}

	public void constructArea(final int baseChunkX, final int baseChunkY, final int widthChunks, final int heightChunks) {
		constructingRegion = true;
		DynamicRegionReference old = region;
		region = new DynamicRegionReference(widthChunks, heightChunks);
		region.copyMapAllPlanes(baseChunkX, baseChunkY, () -> {
			player.setNextWorldTile(new WorldTile(getBaseX() + widthChunks * 4, getBaseY() + heightChunks * 4, 0));
			constructingRegion = false;
			if (old != null)
				old.destroy();
		});
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	public int localX(int x) {
		if (region == null)
			return x;
		return xInRegion(getBaseX() + x);
	}

	public int localY(int y) {
		if (region == null)
			return y;
		return yInRegion(getBaseY() + y);
	}

	public int getBaseX() {
		return region == null ? 0 : region.getBaseX();
	}

	public int getBaseY() {
		return region == null ? 0 : region.getBaseY();
	}

	public final void logout() {
		stopCutscene();
	}

	public final boolean process() {
		if (delay > 0) {
			delay--;
			return true;
		}
		if (constructingRegion || dialoguePaused)
			return true;
		while(delay <= 0 && !constructingRegion) {
			if (currIndex == actions.size())
				break;
			if (currIndex == 0)
				startCutscene();
			CutsceneAction action = actions.get(currIndex++);
			action.process(player, objects);
			delay += action.getDelay();
		}
		if (currIndex == actions.size()) {
			stopCutscene();
			return false;
		}
		return true;
	}

	public void deleteObjects() {
		for (Object object : objects.values())
			deleteObject(object);
	}

	public void deleteObject(Object object) {
		if (object instanceof NPC n)
			n.finish();
	}

	public final void createObjectMap() {
		endTile = new WorldTile(player);
		objects.put("cutscene", this);
	}

	public int xInRegion(int x) {
		return new WorldTile(x, 0, 0).getXInScene(player.getSceneBaseChunkId());
	}

	public int yInRegion(int y) {
		return new WorldTile(0, y, 0).getYInScene(player.getSceneBaseChunkId());
	}

	public boolean isHideMap() {
		return hideMap;
	}

	public void hideMinimap() {
		this.hideMap = true;
	}
	
	public void setEndTile(WorldTile tile) {
		this.endTile = tile;
	}
	
	public void camPos(int x, int y, int height, int delay) {
		actions.add(new PosCameraAction(x, y, height, delay));
	}
	
	public void camLook(int x, int y, int height, int delay) {
		actions.add(new LookCameraAction(x, y, height, delay));
	}
	
	public void camPos(int x, int y, int height, int speed1, int speed2, int delay) {
		actions.add(new PosCameraAction(x, y, height, speed1, speed2, delay));
	}
	
	public void camLook(int x, int y, int height, int speed1, int speed2, int delay) {
		actions.add(new LookCameraAction(x, y, height, speed1, speed2, delay));
	}
	
	public void camPos(int x, int y, int height) {
		camPos(x, y, height, 0);
	}
	
	public void camLook(int x, int y, int height) {
		camLook(x, y, height, 0);
	}
	
	public void camPos(int x, int y, int height, int speed1, int speed2) {
		camPos(x, y, height, speed1, speed2, 0);
	}
	
	public void camLook(int x, int y, int height, int speed1, int speed2) {
		camLook(x, y, height, speed1, speed2, 0);
	}
	
	public void dialogue(Dialogue dialogue, int delay) {
		actions.add(new DialogueAction(dialogue, delay));
	}
	
	public void dialogue(Dialogue dialogue) {
		dialogue(dialogue, 0);
	}
	
	public void dialogue(Dialogue dialogue, boolean pause) {
		if (pause) {
			dialoguePaused = true;
			dialogue.addNext(() -> { dialoguePaused = false; });
		}
		actions.add(new DialogueAction(dialogue, 1));
	}
	
	public void constructMap(int baseX, int baseY, int widthChunks, int heightChunks) {
		actions.add(new ConstructMapAction(baseX, baseY, widthChunks, heightChunks));
	}
	
	public void musicEffect(int id, int delay) {
		actions.add(new PlayerMusicEffectAction(id, delay));
	}
	
	public void musicEffect(int id) {
		musicEffect(id, 0);
	}
	
	public void npcCreate(String key, int npcId, int x, int y, int z, int delay) {
		actions.add(new CreateNPCAction(key, npcId, x, y, z, delay));
	}
	
	public void npcCreate(String key, int npcId, int x, int y, int z) {
		npcCreate(key, npcId, x, y, z, 0);
	}
	
	public void npcDestroy(String key, int delay) {
		actions.add(new DestroyCachedObjectAction(key, delay));
	}
	
	public void npcDestroy(String key) {
		npcDestroy(key, 0);
	}
	
	public void npcFaceTile(String key, int x, int y, int delay) {
		actions.add(new NPCFaceTileAction(key, x, y, delay));
	}
	
	public void npcFaceTile(String key, int x, int y) {
		npcFaceTile(key, x, y, 0);
	}
	
	public void npcSpotAnim(String key, SpotAnim anim, int delay) {
		actions.add(new NPCGraphicAction(key, anim, delay));
	}
	
	public void npcSpotAnim(String key, SpotAnim anim) {
		npcSpotAnim(key, anim, 0);
	}
	
	public void npcAnim(String key, Animation anim, int delay) {
		actions.add(new NPCAnimationAction(key, anim, delay));
	}
	
	public void npcAnim(String key, Animation anim) {
		npcAnim(key, anim, 0);
	}
	
	public void npcTalk(String key, String message, int delay) {
		actions.add(new NPCForceTalkAction(key, message, delay));
	}
	
	public void npcTalk(String key, String message) {
		npcTalk(key, message, 0);
	}
	
	public void npcMove(String key, int x, int y, int z, MoveType type, int delay) {
		actions.add(new MoveNPCAction(key, x, y, z, type, delay));
	}
	
	public void npcMove(String key, int x, int y, int z, MoveType type) {
		npcMove(key, x, y, z, type, 0);
	}
	
	public void npcMove(String key, int x, int y, MoveType type, int delay) {
		npcMove(key, x, y, player.getPlane(), type, delay);
	}
	
	public void npcMove(String key, int x, int y, MoveType type) {
		npcMove(key, x, y, player.getPlane(), type, 0);
	}
	
	public void playerMove(int x, int y, int z, MoveType type, int delay) {
		actions.add(new MovePlayerAction(x, y, z, type, delay));
	}
	
	public void playerMove(int x, int y, int z, MoveType type) {
		playerMove(x, y, z, type, 0);
	}
	
	public void playerMove(int x, int y, MoveType type, int delay) {
		playerMove(x, y, player.getPlane(), type, delay);
	}
	
	public void playerMove(int x, int y, MoveType type) {
		playerMove(x, y, player.getPlacedCannon(), type, 0);
	}
	
	public void playerFaceTile(int x, int y, int delay) {
		actions.add(new PlayerFaceTileAction(x, y, delay));
	}
	
	public void playerFaceTile(int x, int y) {
		playerFaceTile(x, y, 0);
	}
	
	public void playerAnim(Animation anim, int delay) {
		actions.add(new PlayerAnimationAction(anim, delay));
	}
	
	public void playerAnim(Animation anim) {
		playerAnim(anim, 0);
	}
	
	public void playerSpotAnim(SpotAnim anim, int delay) {
		actions.add(new PlayerGraphicAction(anim, delay));
	}
	
	public void playerSpotAnim(SpotAnim anim) {
		playerSpotAnim(anim, 0);
	}
	
	public void playerTalk(String message, int delay) {
		actions.add(new PlayerForceTalkAction(message, delay));
	}
	
	public void playerTalk(String message) {
		playerTalk(message, 0);
	}
	
	public void action(int delay, Runnable runnable) {
		actions.add(new CutsceneCodeAction(runnable, delay));
	}
	
	public void action(Runnable runnable) {
		action(0, runnable);
	}
}
