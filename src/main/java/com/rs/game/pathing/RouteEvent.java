package com.rs.game.pathing;

import com.rs.game.Entity;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
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
		if (entity instanceof Player)
			player = (Player) entity;
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
		else if (last != null && match(strategies, last) && !entity.hasWalkSteps()) {
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
		} else {
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

				WorldTile last = new WorldTile(bufferX[0], bufferY[0], entity.getPlane());
				entity.resetWalkSteps();
				if (player != null)
					player.getSession().writeToQueue(new MinimapFlag(last.getXInScene(entity.getSceneBaseChunkId()), last.getYInScene(entity.getSceneBaseChunkId())));
				if (entity.isFrozen())
					return false;
				if (object instanceof Entity && ((Entity) object).hasWalkSteps() && WorldUtil.collides(entity, ((Entity) object)))
					return false;
				for (int step = steps - 1; step >= 0; step--) {
					if (!entity.addWalkSteps(bufferX[step], bufferY[step], 25, true, true))
						break;
				}
				return false;
			}
			if (player != null) {
				player.sendMessage("You can't reach that.");
				player.getSession().writeToQueue(new MinimapFlag());
			}
			return true;
		}
	}

	private boolean simpleCheck(Entity entity) {
		if (object instanceof Entity) {
			return entity.getPlane() == ((Entity) object).getPlane();
		} else if (object instanceof GameObject) {
			return entity.getPlane() == ((GameObject) object).getPlane();
		} else if (object instanceof GroundItem) {
			return entity.getPlane() == ((GroundItem) object).getTile().getPlane();
		} else if (object instanceof WorldTile) {
			return entity.getPlane() == ((WorldTile) object).getPlane();
		} else {
			throw new RuntimeException(object + " is not instanceof any reachable entity.");
		}
	}

	private RouteStrategy[] generateStrategies() {
		if (object instanceof Entity) {
			return new RouteStrategy[] { new EntityStrategy((Entity) object) };
		} else if (object instanceof GameObject) {
			return new RouteStrategy[] { new ObjectStrategy((GameObject) object) };
		} else if (object instanceof WorldTile) {
			return new RouteStrategy[] { new FixedTileStrategy(((WorldTile) object).getX(), ((WorldTile) object).getY()), new FloorItemStrategy(((WorldTile) object), true)};
		} else if (object instanceof GroundItem) {
			GroundItem item = (GroundItem) object;
			return new RouteStrategy[] { new FixedTileStrategy(item.getTile().getX(), item.getTile().getY()), new FloorItemStrategy(item) };
		} else {
			throw new RuntimeException(object + " is not instanceof any reachable entity.");
		}
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
