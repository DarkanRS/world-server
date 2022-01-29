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
		x = object.getX();
		y = object.getY();
		routeType = getType(object);
		type = object.getType();
		rotation = object.getRotation();
		sizeX = rotation == 0 || rotation == 2 ? object.getDefinitions().getSizeX() : object.getDefinitions().getSizeY();
		sizeY = rotation == 0 || rotation == 2 ? object.getDefinitions().getSizeY() : object.getDefinitions().getSizeX();
		accessBlockFlag = object.getDefinitions().getAccessBlockFlag();
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
		if (!(other instanceof ObjectStrategy strategy))
			return false;
		return x == strategy.x && y == strategy.y && routeType == strategy.routeType && type == strategy.type && rotation == strategy.rotation && sizeX == strategy.sizeX && sizeY == strategy.sizeY && accessBlockFlag == strategy.accessBlockFlag;
	}

}
