package com.rs.game.pathing;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.object.GameObject;
import com.rs.game.object.GameObject.RouteType;

public class ObjectStrategy extends RouteStrategy {

	private int x;
	private int y;
	private int routeType;
	private ObjectType type;
	private int rotation;
	private int sizeX;
	private int sizeY;
	private int accessBlockFlag;

	public ObjectStrategy(GameObject object) {
		this.x = object.getX();
		this.y = object.getY();
		this.routeType = getType(object);
		this.type = object.getType();
		this.rotation = object.getRotation();
		this.sizeX = rotation == 0 || rotation == 2 ? object.getDefinitions().getSizeX() : object.getDefinitions().getSizeY();
		this.sizeY = rotation == 0 || rotation == 2 ? object.getDefinitions().getSizeY() : object.getDefinitions().getSizeX();
		this.accessBlockFlag = object.getDefinitions().getAccessBlockFlag();
		if (rotation != 0)
			accessBlockFlag = ((accessBlockFlag << rotation) & 0xF) + (accessBlockFlag >> (4 - rotation));
	}

	@Override
	public boolean canExit(int currentX, int currentY, int sizeXY, int[][] clip, int clipBaseX, int clipBaseY) {
		switch (routeType) {
		case 0:
			return RouteStrategy.checkWallInteract(clip, currentX - clipBaseX, currentY - clipBaseY, sizeXY, x - clipBaseX, y - clipBaseY, type, rotation);
		case 1:
			return RouteStrategy.checkWallDecorationInteract(clip, currentX - clipBaseX, currentY - clipBaseY, sizeXY, x - clipBaseX, y - clipBaseY, type, rotation);
		case 2:
			return RouteStrategy.checkFilledRectangularInteract(clip, currentX - clipBaseX, currentY - clipBaseY, sizeXY, sizeXY, x - clipBaseX, y - clipBaseY, sizeX, sizeY, accessBlockFlag);
		case 3:
			return currentX == x && currentY == y;
		}
		return false;
	}

	@Override
	public int getApproxDestinationX() {
		return x;
	}

	@Override
	public int getApproxDestinationY() {
		return y;
	}

	@Override
	public int getApproxDestinationSizeX() {
		return sizeX;
	}

	@Override
	public int getApproxDestinationSizeY() {
		return sizeY;
	}

	private int getType(GameObject object) {
		if (object.getRouteType() == RouteType.WALK_ONTO)
			return 3;
		switch(object.getType()) {
		case WALL_STRAIGHT:
		case WALL_DIAGONAL_CORNER:
		case WALL_WHOLE_CORNER:
		case WALL_STRAIGHT_CORNER:
		case WALL_INTERACT:
			return 0;
		case STRAIGHT_INSIDE_WALL_DEC:
		case STRAIGHT_OUSIDE_WALL_DEC:
		case DIAGONAL_OUTSIDE_WALL_DEC:
		case DIAGONAL_INSIDE_WALL_DEC:
		case DIAGONAL_INWALL_DEC:
			return 1;
		case SCENERY_INTERACT:
		case GROUND_INTERACT:
		case GROUND_DECORATION:
			return 2;
		default:
			return 3;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ObjectStrategy))
			return false;
		ObjectStrategy strategy = (ObjectStrategy) other;
		return x == strategy.x && y == strategy.y && routeType == strategy.routeType && type == strategy.type && rotation == strategy.rotation && sizeX == strategy.sizeX && sizeY == strategy.sizeY && accessBlockFlag == strategy.accessBlockFlag;
	}

}
