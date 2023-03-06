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

import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.map.ClipFlag;

/**
 * @author Mangis
 *
 */
public abstract class RouteStrategy {

	public abstract boolean canExit(int currentX, int currentY, int sizeXY, int[][] clip, int clipBaseX, int clipBaseY);

	public abstract int getApproxDestinationX();

	public abstract int getApproxDestinationY();

	public abstract int getApproxDestinationSizeX();

	public abstract int getApproxDestinationSizeY();

	@Override
	public abstract boolean equals(Object other);

	protected static boolean checkWallDecorationInteract(int[][] clip, int currentX, int currentY, int sizeXY, int targetX, int targetY, ObjectType targetType, int targetRotation) {
		if (currentX == targetX && currentY == targetY)
			return true;
		if (targetType == ObjectType.DIAGONAL_OUTSIDE_WALL_DEC || targetType == ObjectType.DIAGONAL_INSIDE_WALL_DEC) {
			if (targetType == ObjectType.DIAGONAL_INSIDE_WALL_DEC)
				targetRotation = targetRotation + 2 & 0x3;
			if (targetRotation == 0) {
				if (currentX == (targetX + 1) && currentY == targetY && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_W))
					return true;
				if (currentX == targetX && currentY == (targetY - 1) && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_N))
					return true;
			} else if (targetRotation == 1) {
				if (currentX == (targetX - 1) && currentY == targetY && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_E))
					return true;
				if (currentX == targetX && currentY == (targetY - 1) && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_N))
					return true;
			} else if (targetRotation == 2) {
				if (currentX == (targetX - 1) && currentY == targetY && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_E))
					return true;
				if (currentX == targetX && currentY == (targetY + 1) && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_S))
					return true;
			} else if (targetRotation == 3) {
				if (currentX == (targetX + 1) && currentY == targetY && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_W))
					return true;
				if (currentX == targetX && currentY == (targetY + 1) && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_S))
					return true;
			}
		} else if (targetType == ObjectType.DIAGONAL_INWALL_DEC) {
			if (currentX == targetX && currentY == (targetY + 1) && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_S))
				return true;
			if (currentX == targetX && currentY == (targetY - 1) && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_N))
				return true;
			if (currentX == (targetX - 1) && currentY == targetY && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_E))
				return true;
			if (currentX == (targetX + 1) && currentY == targetY && !ClipFlag.flagged(clip[currentX][currentY], ClipFlag.BW_W))
				return true;
		}
		return false;
	}

	public static boolean checkWallInteract(int[][] clipMap, int x, int y, int size, int destX, int destY, ObjectType objectType, int rotation) {
		if (size == 1) {
			if (destX == x && destY == y)
				return true;
		} else if (destX >= x && destX <= size + x - 1 && destY >= destY && destY <= size + destY - 1)
			return true;
		if (size == 1) {
			if (objectType == ObjectType.WALL_STRAIGHT)
				if (rotation == 0) {
					if (destX - 1 == x && destY == y)
						return true;
					if (destX == x && destY + 1 == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX == x && y == destY - 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 1) {
					if (destX == x && destY + 1 == y)
						return true;
					if (destX - 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX + 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 2) {
					if (destX + 1 == x && destY == y)
						return true;
					if (destX == x && y == destY + 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX == x && y == destY - 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 3) {
					if (destX == x && y == destY - 1)
						return true;
					if (destX - 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX + 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				}
			if (objectType == ObjectType.WALL_WHOLE_CORNER)
				if (rotation == 0) {
					if ((destX - 1 == x && destY == y) || (destX == x && destY + 1 == y))
						return true;
					if (destX + 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX == x && y == destY - 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 1) {
					if (destX - 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if ((destX == x && destY + 1 == y) || (destX + 1 == x && destY == y))
						return true;
					if (destX == x && y == destY - 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 2) {
					if (destX - 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX == x && y == destY + 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if ((destX + 1 == x && destY == y) || (destX == x && y == destY - 1))
						return true;
				} else if (rotation == 3) {
					if (destX - 1 == x && destY == y)
						return true;
					if (destX == x && y == destY + 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX + 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX == x && y == destY - 1)
						return true;
				}
			if (objectType == ObjectType.WALL_INTERACT) {
				if (destX == x && y == destY + 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_S))
					return true;
				if (destX == x && y == destY - 1 && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_N))
					return true;
				if (destX - 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_E))
					return true;
				return destX + 1 == x && destY == y && !ClipFlag.flagged(clipMap[x][y], ClipFlag.BW_W);
			}
		} else {
			int width = size + x - 1;
			int height = size + y - 1;
			if (objectType == ObjectType.WALL_STRAIGHT)
				if (rotation == 0) {
					if (destX - size == x && destY >= y && destY <= height)
						return true;
					if (destX >= x && destX <= width && y == destY + 1 && !ClipFlag.flagged(clipMap[destX][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX >= x && destX <= width && destY - size == y && !ClipFlag.flagged(clipMap[destX][height], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 1) {
					if (destX >= x && destX <= width && destY + 1 == y)
						return true;
					if (destX - size == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[width][destY], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX + 1 == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[x][destY], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 2) {
					if (destX + 1 == x && destY >= y && destY <= height)
						return true;
					if (destX >= x && destX <= width && destY + 1 == y && !ClipFlag.flagged(clipMap[destX][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX >= x && destX <= width && destY - size == y && !ClipFlag.flagged(clipMap[destX][height], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 3) {
					if (destX >= x && destX <= width && destY - size == y)
						return true;
					if (destX - size == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[width][destY], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX + 1 == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[x][destY], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				}
			if (objectType == ObjectType.WALL_WHOLE_CORNER)
				if (rotation == 0) {
					if (destX - size == x && destY >= y && destY <= height)
						return true;
					if (destX >= x && destX <= width && destY + 1 == y)
						return true;
					if (destX + 1 == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[x][destY], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX >= x && destX <= width && destY - size == y && !ClipFlag.flagged(clipMap[destX][height], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 1) {
					if (destX - size == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[width][destY], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX >= x && destX <= width && destY + 1 == y)
						return true;
					if (destX + 1 == x && destY >= y && destY <= height)
						return true;
					if (destX >= x && destX <= width && destY - size == y && !ClipFlag.flagged(clipMap[destX][height], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
				} else if (rotation == 2) {
					if (destX - size == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[width][destY], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX >= x && destX <= width && destY + 1 == y && !ClipFlag.flagged(clipMap[destX][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX + 1 == x && destY >= y && destY <= height)
						return true;
					if (destX >= x && destX <= width && destY - size == y)
						return true;
				} else if (rotation == 3) {
					if (destX - size == x && destY >= y && destY <= height)
						return true;
					if (destX >= x && destX <= width && y == destY + 1 && !ClipFlag.flagged(clipMap[destX][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX + 1 == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[x][destY], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
						return true;
					if (destX >= x && destX <= width && destY - size == y)
						return true;
				}
			if (objectType == ObjectType.WALL_INTERACT) {
				if (destX >= x && destX <= width && destY + 1 == y && !ClipFlag.flagged(clipMap[destX][y], ClipFlag.BW_S, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
					return true;
				if (destX >= x && destX <= width && destY - size == y && !ClipFlag.flagged(clipMap[destX][height], ClipFlag.BW_N, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
					return true;
				if (destX - size == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[width][destY], ClipFlag.BW_E, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR))
					return true;
				return destX + 1 == x && destY >= y && destY <= height && !ClipFlag.flagged(clipMap[x][destY], ClipFlag.BW_W, ClipFlag.BW_FULL, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PFBW_FLOOR);
			}
		}
		return false;
	}
	public static final int BLOCK_FLAG_NORTH = 0x1;
	public static final int BLOCK_FLAG_EAST = 0x2;
	public static final int BLOCK_FLAG_SOUTH = 0x4;
	public static final int BLOCK_FLAG_WEST = 0x8;

	protected static boolean checkFilledRectangularInteract(int[][] clip, int currentX, int currentY, int sizeX, int sizeY, int targetX, int targetY, int targetSizeX, int targetSizeY, int accessBlockFlag) {
		int srcEndX = currentX + sizeX;
		int srcEndY = currentY + sizeY;
		int destEndX = targetX + targetSizeX;
		int destEndY = targetY + targetSizeY;
		if (destEndX == currentX && (accessBlockFlag & BLOCK_FLAG_EAST) == 0) {
			int maxY = currentY > targetY ? currentY : targetY;
			for (int y = srcEndY < destEndY ? srcEndY : destEndY; maxY < y; maxY++)
				if (((clip[destEndX - 1][maxY]) & 0x8) == 0)
					return true;
		} else if (srcEndX == targetX && (accessBlockFlag & BLOCK_FLAG_WEST) == 0) {
			int minY = currentY > targetY ? currentY : targetY;
			for (int y = srcEndY < destEndY ? srcEndY : destEndY; minY < y; minY++)
				if (((clip[targetX][minY]) & 0x80) == 0)
					return true;
		} else if (currentY == destEndY && (accessBlockFlag & BLOCK_FLAG_NORTH) == 0) {
			int maxX = currentX > targetX ? currentX : targetX;
			for (int x = srcEndX < destEndX ? srcEndX : destEndX; maxX < x; maxX++)
				if (((clip[maxX][destEndY - 1]) & 0x2) == 0)
					return true;
		} else if (targetY == srcEndY && (accessBlockFlag & BLOCK_FLAG_SOUTH) == 0) {
			int minX = currentX > targetX ? currentX : targetX;
			for (int x = srcEndX < destEndX ? srcEndX : destEndX; minX < x; minX++)
				if (((clip[minX][targetY]) & 0x20) == 0)
					return true;
		}
		return false;
	}

}
