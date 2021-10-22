package com.rs.game.player.cutscenes;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.actions.CutsceneAction;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.lib.game.WorldTile;

public abstract class Cutscene {

	public abstract boolean hiddenMinimap();

	public abstract CutsceneAction[] getActions(Player player);

	private int stage;
	private Object[] cache;
	private CutsceneAction[] actions;
	private int delay;
	private boolean constructingRegion;
	private DynamicRegionReference region;

	public Cutscene() {

	}

	private WorldTile endTile;

	public final void stopCutscene(Player player) {
		if (player.getX() != endTile.getX() || player.getY() != endTile.getY() || player.getPlane() != endTile.getPlane())
			player.setNextWorldTile(endTile);
		if (hiddenMinimap())
			player.getPackets().setBlockMinimapState(0);
		player.getVars().setVar(1241, 3);
		player.getPackets().sendResetCamera();
		player.setLargeSceneView(false);
		player.unlock();
		deleteCache();
		if (region != null)
			region.destroy();
	}

	public final void startCutscene(Player player) {
		if (hiddenMinimap())
			player.getPackets().setBlockMinimapState(2); // minimap
		player.getVars().setVar(1241, 1);
		player.setLargeSceneView(true);
		player.lock();
		player.stopAll(true, false);
	}

	public void constructArea(final Player player, final int baseChunkX, final int baseChunkY, final int widthChunks, final int heightChunks) {
		constructingRegion = true;
		DynamicRegionReference old = region;
		region = new DynamicRegionReference(widthChunks, heightChunks);
		region.copyMapAllPlanes(baseChunkX, baseChunkY, () -> {
			player.setNextWorldTile(new WorldTile(getBaseX() + widthChunks * 4, + getBaseY() + heightChunks * 4, 0));
			constructingRegion = false;
			if (old != null)
				old.destroy();
		});
	}

	public int getLocalX(Player player, int x) {
		if (region == null)
			return x;
		return getX(player, getBaseX() + x);
	}

	public int getLocalY(Player player, int y) {
		if (region == null)
			return y;
		return getY(player, getBaseY() + y);
	}

	public int getBaseX() {
		return region == null ? 0 : region.getBaseX() << 3;
	}

	public int getBaseY() {
		return region == null ? 0 : region.getBaseY() << 3;
	}

	public final void logout(Player player) {
		stopCutscene(player);
	}

	public final boolean process(Player player) {
		if (delay > 0) {
			delay--;
			return true;
		}
		while (true) {
			if (constructingRegion)
				return true;
			if (stage == actions.length) {
				stopCutscene(player);
				return false;
			} else if (stage == 0)
				startCutscene(player);
			CutsceneAction action = actions[stage++];
			action.process(player, cache);
			int delay = action.getActionDelay();
			if (delay == -1)
				continue;
			this.delay = delay;
			return true;
		}
	}

	public void deleteCache() {
		for (Object object : cache)
			destroyCache(object);
	}

	public void destroyCache(Object object) {
		if (object instanceof NPC n)
			n.finish();
	}

	public final void createCache(Player player) {
		actions = getActions(player);
		endTile = new WorldTile(player);
		int lastIndex = 0;
		for (CutsceneAction action : actions) {
			if (action.getCachedObjectIndex() > lastIndex)
				lastIndex = action.getCachedObjectIndex();
		}
		cache = new Object[lastIndex + 1];
		cache[0] = this;
	}

	public static int getX(Player player, int x) {
		return new WorldTile(x, 0, 0).getXInScene(player.getSceneBaseChunkId());
	}

	public static int getY(Player player, int y) {
		return new WorldTile(0, y, 0).getYInScene(player.getSceneBaseChunkId());
	}
}
