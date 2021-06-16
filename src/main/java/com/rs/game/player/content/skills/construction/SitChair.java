package com.rs.game.player.content.skills.construction;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;

public class SitChair extends Action {

	private WorldTile originalTile;
	private WorldTile chairTile;
	private boolean tped;
	private int animation;

	public SitChair(Player player, GameObject object) {
		this.animation = HouseConstants.getSitAnimation(object.getId());
		this.originalTile = new WorldTile(player);
		chairTile = object;
		WorldTile face = new WorldTile(player);
		if (object.getType() == ObjectType.SCENERY_INTERACT) {
			if (object.getRotation() == 0)
				face.moveLocation(0, -1, 0);
			else if (object.getRotation() == 2)
				face.moveLocation(0, 1, 0);
			else if (object.getRotation() == 3)
				face.moveLocation(1, 0, 0);
			else if (object.getRotation() == 1)
				face.moveLocation(-1, 0, 0);
		} else if (object.getType() == ObjectType.GROUND_INTERACT) {
			if (object.getRotation() == 1)
				face.moveLocation(-1, 1, 0);
			else if (object.getRotation() == 2)
				face.moveLocation(1, 1, 0);
			else if (object.getRotation() == 0)
				face.moveLocation(-1, -1, 0);
			else if (object.getRotation() == 3)
				face.moveLocation(1, -1, 0);
		}
		player.setNextFaceWorldTile(face);
	}

	@Override
	public boolean start(Player player) {
		setActionDelay(player, 1);
		return true;
	}

	@Override
	public boolean process(Player player) {
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (!tped) {
			player.setNextWorldTile(chairTile);
			tped = true;
		}
		player.setNextAnimation(new Animation(animation));
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.lock(1);
		player.setNextWorldTile(originalTile);
		player.setNextAnimation(new Animation(-1));
	}
}