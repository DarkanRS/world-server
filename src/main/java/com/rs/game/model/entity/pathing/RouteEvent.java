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
package com.rs.game.model.entity.pathing;

import com.rs.game.content.Effect;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.packets.encoders.MinimapFlag;
import com.rs.utils.WorldUtil;

public class RouteEvent {

	private Object object;
	private Runnable event;
	private boolean alternative;
	private RouteStrategy[] last;

	public RouteEvent(Object object, Runnable event) {
		this(object, event, false);
	}

	public RouteEvent(Object object, Runnable event, boolean alternative) {
		this.object = object;
		this.event = event;
		this.alternative = alternative;
	}

	public boolean processEvent(Entity entity) {
		Player player = null;
		if (entity instanceof Player p)
			player = p;
		if (!simpleCheck(entity)) {
			if (player != null) {
				player.sendMessage("You can't reach that.");
				player.getSession().writeToQueue(new MinimapFlag());
			}
			return true;
		}
		RouteStrategy[] strategies = generateStrategies();
		if (last != null && match(strategies, last) && entity.hasWalkSteps())
			return false;
		if (last != null && match(strategies, last) && !entity.hasWalkSteps()) {
			for (int i = 0; i < strategies.length; i++) {
				RouteStrategy strategy = strategies[i];
				int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, entity.getX(), entity.getY(), entity.getPlane(), entity.getSize(), strategy, i == (strategies.length - 1));
				if (steps == -1)
					continue;
				if ((!RouteFinder.lastIsAlternative() && steps <= 0) || alternative) {
					if (alternative && player != null)
						player.getSession().writeToQueue(new MinimapFlag());
					event.run();
					return true;
				}
			}
			if (player != null) {
				player.sendMessage("You can't reach that.");
				player.getSession().writeToQueue(new MinimapFlag());
			}
			return true;
		}
		last = strategies;

		for (int i = 0; i < strategies.length; i++) {
			RouteStrategy strategy = strategies[i];
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, entity.getX(), entity.getY(), entity.getPlane(), entity.getSize(), strategy, i == (strategies.length - 1));
			if (steps == -1)
				continue;
			if ((!RouteFinder.lastIsAlternative() && steps <= 0)) {
				if (alternative && player != null)
					player.getSession().writeToQueue(new MinimapFlag());
				event.run();
				return true;
			}
			int[] bufferX = RouteFinder.getLastPathBufferX();
			int[] bufferY = RouteFinder.getLastPathBufferY();

			WorldTile last = WorldTile.of(bufferX[0], bufferY[0], entity.getPlane());
			entity.resetWalkSteps();
			if (player != null)
				player.getSession().writeToQueue(new MinimapFlag(last.getXInScene(entity.getSceneBaseChunkId()), last.getYInScene(entity.getSceneBaseChunkId())));
			if (entity.hasEffect(Effect.FREEZE) || (object instanceof Entity e && e.hasWalkSteps() && WorldUtil.collides(entity, e)))
				return false;
			for (int step = steps - 1; step >= 0; step--)
				if (!entity.addWalkSteps(bufferX[step], bufferY[step], 25, true, true))
					break;
			return false;
		}
		if (player != null) {
			player.sendMessage("You can't reach that.");
			player.getSession().writeToQueue(new MinimapFlag());
		}
		return true;
	}

	private boolean simpleCheck(Entity entity) {
		if (object instanceof Entity e)
			return entity.getPlane() == e.getPlane();
		if (object instanceof GameObject e)
			return entity.getPlane() == e.getPlane();
		else if (object instanceof GroundItem e)
			return entity.getPlane() == e.getTile().getPlane();
		else if (object instanceof WorldTile e)
			return entity.getPlane() == e.getPlane();
		else
			throw new RuntimeException(object + " is not instanceof any reachable entity.");
	}

	private RouteStrategy[] generateStrategies() {
		if (object instanceof Entity e)
			return new RouteStrategy[] { new EntityStrategy(e) };
		if (object instanceof GameObject go)
			return new RouteStrategy[] { new ObjectStrategy(go) };
		if (object instanceof WorldTile wt)
			return new RouteStrategy[] { new FixedTileStrategy(wt.getX(), wt.getY()), new FloorItemStrategy(wt, true)};
		else if (object instanceof GroundItem gi)
			return new RouteStrategy[] { new FixedTileStrategy(gi.getTile().getX(), gi.getTile().getY()), new FloorItemStrategy(gi) };
		else
			throw new RuntimeException(object + " is not instanceof any reachable entity.");
	}

	private boolean match(RouteStrategy[] a1, RouteStrategy[] a2) {
		if (a1.length != a2.length)
			return false;
		for (int i = 0; i < a1.length; i++)
			if (!a1[i].equals(a2[i]))
				return false;
		return true;
	}

}
