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

import com.rs.cache.loaders.map.ClipFlag;
import com.rs.game.World;
import com.rs.game.map.Chunk;
import com.rs.lib.game.Tile;

public class Route {
	private static final int GRAPH_SIZE = 128;
	private static final int QUEUE_SIZE = (GRAPH_SIZE * GRAPH_SIZE) / 4; // we do /4 because each tile can only be accessed from single direction
	private static final int ALTERNATIVE_ROUTE_MAX_DISTANCE = 100;
	private static final int ALTERNATIVE_ROUTE_RANGE = 10;

	private static final int DIR_NORTH = 0x1;
	private static final int DIR_EAST = 0x2;
	private static final int DIR_SOUTH = 0x4;
	private static final int DIR_WEST = 0x8;

	private final int[][] directions = new int[GRAPH_SIZE][GRAPH_SIZE];
	private final int[][] distances = new int[GRAPH_SIZE][GRAPH_SIZE];
	private final int[][] clip = new int[GRAPH_SIZE][GRAPH_SIZE];
	private final int[] bufferX = new int[QUEUE_SIZE];
	private final int[] bufferY = new int[QUEUE_SIZE];
	private int exitX = -1;
	private int exitY = -1;
	private boolean foundAltRoute = false;
	private int stepCount = 0;

	public long debug_transmittime = 0;

	public Route() {
		
	}

	/**
	 * Find's route using given strategy. Returns amount of steps found. If steps >
	 * 0, route exists. If steps = 0, route exists, but no need to move. If steps <
	 * 0, route does not exist.
	 */
	protected Route find(int srcX, int srcY, int srcZ, int srcSizeXY, RouteStrategy strategy, boolean ignoreTileEventTiles) {
		long start = System.nanoTime();
		for (int x = 0; x < GRAPH_SIZE; x++) {
			for (int y = 0; y < GRAPH_SIZE; y++) {
				directions[x][y] = 0;
				distances[x][y] = 99999999;
			}
		}
		transmitClipData(srcX, srcY, srcZ);

		// we could use performCalculationSX() for every size, but since most common
		// size's are 1 and 2,
		// we will have optimized algorhytm's for them.
		boolean found = false;
		found = switch (srcSizeXY) {
		case 1 -> checkSingleTraversal(srcX, srcY, strategy);
		case 2 -> checkDoubleTraversal(srcX, srcY, strategy);
		default -> checkVariableTraversal(srcX, srcY, srcSizeXY, strategy);
		};

		if (!found && !ignoreTileEventTiles) {
			stepCount = -1;
			debug_transmittime = System.nanoTime() - start;
			return this;
		}

		// when we start searching for path, we position ourselves in the middle of
		// graph
		// so the base(minimum) position is source_pos - HALF_GRAPH_SIZE.
		int graphBaseX = srcX - (GRAPH_SIZE / 2);
		int graphBaseY = srcY - (GRAPH_SIZE / 2);
		int endX = exitX;
		int endY = exitY;

		if (!found && ignoreTileEventTiles) {
			foundAltRoute = true;
			int lowestCost = Integer.MAX_VALUE;
			int lowestDistance = Integer.MAX_VALUE;

			int approxDestX = strategy.getApproxDestinationX();
			int approxDestY = strategy.getApproxDestinationY();

			// what we will do here is search the coordinates range of destination +-
			// ALTERNATIVE_ROUTE_RANGE
			// to see if at least one position in that range is reachable, and reaching it
			// takes no longer than ALTERNATIVE_ROUTE_MAX_DISTANCE steps.
			// if we have multiple positions in our range that fits all the conditions, we
			// will choose the one which takes fewer steps.

			for (int checkX = (approxDestX - ALTERNATIVE_ROUTE_RANGE); checkX <= (approxDestX + ALTERNATIVE_ROUTE_RANGE); checkX++)
				for (int checkY = (approxDestY - ALTERNATIVE_ROUTE_RANGE); checkY <= (approxDestY + ALTERNATIVE_ROUTE_RANGE); checkY++) {
					int graphX = checkX - graphBaseX;
					int graphY = checkY - graphBaseY;
					if (graphX < 0 || graphY < 0 || graphX >= GRAPH_SIZE || graphY >= GRAPH_SIZE || distances[graphX][graphY] >= ALTERNATIVE_ROUTE_MAX_DISTANCE)
						continue; // we are out of graph's bounds or too much steps.
					// calculate the delta's.
					// when calculating, we are also taking the approximated destination size into
					// account to increase precise.
					int deltaX = 0;
					int deltaY = 0;
					if (approxDestX <= checkX)
						deltaX = 1 - approxDestX - (strategy.getApproxDestinationSizeX() - checkX);
					// deltaX = (approxDestX + (strategy.getApproxDestinationSizeX() - 1)) < checkX
					// ? (approxDestX - (checkX - (strategy.getApproxDestinationSizeX() + 1))) : 0;
					else
						deltaX = approxDestX - checkX;
					if (approxDestY <= checkY)
						deltaY = 1 - approxDestY - (strategy.getApproxDestinationSizeY() - checkY);
					// deltaY = (approxDestY + (strategy.getApproxDestinationSizeY() - 1)) < checkY
					// ? (approxDestY - (checkY - (strategy.getApproxDestinationSizeY() + 1))) : 0;
					else
						deltaY = approxDestY - checkY;

					int cost = (deltaX * deltaX) + (deltaY * deltaY);
					if (cost < lowestCost || (cost <= lowestCost && distances[graphX][graphY] < lowestDistance)) {
						// if the cost is lower than the lowest one, or same as the lowest one, but less
						// steps, we accept this position as alternate.
						lowestCost = cost;
						lowestDistance = distances[graphX][graphY];
						endX = checkX;
						endY = checkY;
					}
				}

			if (lowestCost == Integer.MAX_VALUE || lowestDistance == Integer.MAX_VALUE) {
				stepCount = -1;
				debug_transmittime = System.nanoTime() - start;
				return this; // we didin't find any alternative route, sadly.
			}
		}

		if (endX == srcX && endY == srcY) {
			stepCount = 0;
			debug_transmittime = System.nanoTime() - start;
			return this; // path was found, but we didin't move
		}

		// what we will do now is trace the path from the end position
		// for faster performance, we are reusing our queue buffer for another purpose.
		int steps = 0;
		int traceX = endX;
		int traceY = endY;
		int direction = directions[traceX - graphBaseX][traceY - graphBaseY];
		int lastwritten = direction;
		// queue destination position and start tracing from it
		bufferX[steps] = traceX;
		bufferY[steps++] = traceY;
		while (traceX != srcX || traceY != srcY) {
			if (lastwritten != direction) {
				// we changed our direction, write it
				bufferX[steps] = traceX;
				bufferY[steps++] = traceY;
				lastwritten = direction;
			}

			if ((direction & DIR_EAST) != 0)
				traceX++;
			else if ((direction & DIR_WEST) != 0)
				traceX--;

			if ((direction & DIR_NORTH) != 0)
				traceY++;
			else if ((direction & DIR_SOUTH) != 0)
				traceY--;

			direction = directions[traceX - graphBaseX][traceY - graphBaseY];
		}
		stepCount = steps;
		debug_transmittime = System.nanoTime() - start;
		return this;
	}

	private boolean checkSingleTraversal(int srcX, int srcY, RouteStrategy strategy) {
		// first, we will cache our static fields to local variables, this is done for
		// performance, because
		// modern jit compiler's usually takes advantage of things like this
		int[][] _directions = directions;
		int[][] _distances = distances;
		int[][] _clip = clip;
		int[] _bufferX = bufferX;
		int[] _bufferY = bufferY;

		// when we start searching for path, we position ourselves in the middle of
		// graph
		// so the base(minimum) position is source_pos - HALF_GRAPH_SIZE.
		int graphBaseX = srcX - (GRAPH_SIZE / 2);
		int graphBaseY = srcY - (GRAPH_SIZE / 2);
		int currentX = srcX;
		int currentY = srcY;
		int currentGraphX = srcX - graphBaseX;
		int currentGraphY = srcY - graphBaseY;

		// setup information about source tile.
		_distances[currentGraphX][currentGraphY] = 0;
		_directions[currentGraphX][currentGraphY] = 99;

		// queue variables
		int read = 0, write = 0;
		// insert our current position as first queued position.
		_bufferX[write] = currentX;
		_bufferY[write++] = currentY;

		while (read != write) {
			currentX = _bufferX[read];
			currentY = _bufferY[read];
			read = (read + 1) & (QUEUE_SIZE - 1);

			currentGraphX = currentX - graphBaseX;
			currentGraphY = currentY - graphBaseY;

			if (strategy.canExit(currentX, currentY, 1, _clip, graphBaseX, graphBaseY)) {
				exitX = currentX;
				exitY = currentY;
				return true;
			}

			// if we can't exit at current tile, check where we can go from this tile
			int nextDistance = _distances[currentGraphX][currentGraphY] + 1;
			if (currentGraphX > 0 && _directions[currentGraphX - 1][currentGraphY] == 0 && !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E)) {
				_bufferX[write] = currentX - 1;
				_bufferY[write] = currentY;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX - 1][currentGraphY] = DIR_EAST;
				_distances[currentGraphX - 1][currentGraphY] = nextDistance;
			}
			if (currentGraphX < (GRAPH_SIZE - 1) && _directions[currentGraphX + 1][currentGraphY] == 0 && !ClipFlag.flagged(_clip[currentGraphX + 1][currentGraphY], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_W)) {
				_bufferX[write] = currentX + 1;
				_bufferY[write] = currentY;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX + 1][currentGraphY] = DIR_WEST;
				_distances[currentGraphX + 1][currentGraphY] = nextDistance;
			}
			if (currentGraphY > 0 && _directions[currentGraphX][currentGraphY - 1] == 0 && !ClipFlag.flagged(_clip[currentGraphX][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N)) {
				_bufferX[write] = currentX;
				_bufferY[write] = currentY - 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX][currentGraphY - 1] = DIR_NORTH;
				_distances[currentGraphX][currentGraphY - 1] = nextDistance;
			}
			if (currentGraphY < (GRAPH_SIZE - 1) && _directions[currentGraphX][currentGraphY + 1] == 0 && !ClipFlag.flagged(_clip[currentGraphX][currentGraphY + 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_S)) {
				_bufferX[write] = currentX;
				_bufferY[write] = currentY + 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX][currentGraphY + 1] = DIR_SOUTH;
				_distances[currentGraphX][currentGraphY + 1] = nextDistance;
			}
			if (currentGraphX > 0 && currentGraphY > 0 && _directions[currentGraphX - 1][currentGraphY - 1] == 0 && !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_NE)
					&& !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E)
					&& !ClipFlag.flagged(clip[currentGraphX][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N)) {
				_bufferX[write] = currentX - 1;
				_bufferY[write] = currentY - 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX - 1][currentGraphY - 1] = DIR_NORTH | DIR_EAST;
				_distances[currentGraphX - 1][currentGraphY - 1] = nextDistance;
			}
			if (currentGraphX < (GRAPH_SIZE - 1) && currentGraphY > 0 && _directions[currentGraphX + 1][currentGraphY - 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX + 1][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_W, ClipFlag.PF_NW)
					&& !ClipFlag.flagged(_clip[currentGraphX + 1][currentGraphY], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_W)
					&& !ClipFlag.flagged(_clip[currentGraphX][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N)) {
				_bufferX[write] = currentX + 1;
				_bufferY[write] = currentY - 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX + 1][currentGraphY - 1] = DIR_NORTH | DIR_WEST;
				_distances[currentGraphX + 1][currentGraphY - 1] = nextDistance;
			}
			if (currentGraphX > 0 && currentGraphY < (GRAPH_SIZE - 1) && _directions[currentGraphX - 1][currentGraphY + 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY + 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_SE)
					&& !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E)
					&& !ClipFlag.flagged(_clip[currentGraphX][currentGraphY + 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_S)) {
				_bufferX[write] = currentX - 1;
				_bufferY[write] = currentY + 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX - 1][currentGraphY + 1] = DIR_SOUTH | DIR_EAST;
				_distances[currentGraphX - 1][currentGraphY + 1] = nextDistance;
			}
			if (currentGraphX < (GRAPH_SIZE - 1) && currentGraphY < (GRAPH_SIZE - 1) && _directions[currentGraphX + 1][currentGraphY + 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX + 1][currentGraphY + 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_SW)
					&& !ClipFlag.flagged(_clip[currentGraphX + 1][currentGraphY], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_W)
					&& !ClipFlag.flagged(_clip[currentGraphX][currentGraphY + 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_S)) {
				_bufferX[write] = currentX + 1;
				_bufferY[write] = currentY + 1;
				write = (write + 1) & (QUEUE_SIZE - 1);

				_directions[currentGraphX + 1][currentGraphY + 1] = DIR_SOUTH | DIR_WEST;
				_distances[currentGraphX + 1][currentGraphY + 1] = nextDistance;
			}

		}

		exitX = currentX;
		exitY = currentY;
		return false;
	}

	private boolean checkDoubleTraversal(int srcX, int srcY, RouteStrategy strategy) {
		return checkVariableTraversal(srcX, srcY, 2, strategy);
	}

	private boolean checkVariableTraversal(int srcX, int srcY, int size, RouteStrategy strategy) {
		// first, we will cache our static fields to local variables, this is done for
		// performance, because
		// modern jit compiler's usually takes advantage of things like this
		int[][] _directions = directions;
		int[][] _distances = distances;
		int[][] _clip = clip;
		int[] _bufferX = bufferX;
		int[] _bufferY = bufferY;

		// when we start searching for path, we position ourselves in the middle of
		// graph
		// so the base(minimum) position is source_pos - HALF_GRAPH_SIZE.
		int graphBaseX = srcX - (GRAPH_SIZE / 2);
		int graphBaseY = srcY - (GRAPH_SIZE / 2);
		int currentX = srcX;
		int currentY = srcY;
		int currentGraphX = srcX - graphBaseX;
		int currentGraphY = srcY - graphBaseY;

		// setup information about source tile.
		_distances[currentGraphX][currentGraphY] = 0;
		_directions[currentGraphX][currentGraphY] = 99;

		// queue variables
		int read = 0, write = 0;
		// insert our current position as first queued position.
		_bufferX[write] = currentX;
		_bufferY[write++] = currentY;

		while (read != write) {
			currentX = _bufferX[read];
			currentY = _bufferY[read];
			read = (read + 1) & (QUEUE_SIZE - 1);

			currentGraphX = currentX - graphBaseX;
			currentGraphY = currentY - graphBaseY;

			if (strategy.canExit(currentX, currentY, size, _clip, graphBaseX, graphBaseY)) {
				exitX = currentX;
				exitY = currentY;
				return true;
			}

			int nextDistance = _distances[currentGraphX][currentGraphY] + 1;
			if (currentGraphX > 0 && _directions[currentGraphX - 1][currentGraphY] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_NE)
					&& !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY + (size - 1)], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_SE))
				exit: do {
					for (int y = 1; y < (size - 1); y++)
						if (ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY + y], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_NE, ClipFlag.PF_SE))
							break exit;
					_bufferX[write] = currentX - 1;
					_bufferY[write] = currentY;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX - 1][currentGraphY] = DIR_EAST;
					_distances[currentGraphX - 1][currentGraphY] = nextDistance;
				} while (false);
			if (currentGraphX < (GRAPH_SIZE - size) && _directions[currentGraphX + 1][currentGraphY] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX + size][currentGraphY], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_W, ClipFlag.PF_NW)
					&& !ClipFlag.flagged(_clip[currentGraphX + size][currentGraphY + (size - 1)], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_SW))
				exit: do {
					for (int y = 1; y < (size - 1); y++)
						if (ClipFlag.flagged(_clip[currentGraphX + size][currentGraphY + y], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_NW, ClipFlag.PF_SW))
							break exit;
					_bufferX[write] = currentX + 1;
					_bufferY[write] = currentY;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX + 1][currentGraphY] = DIR_WEST;
					_distances[currentGraphX + 1][currentGraphY] = nextDistance;
				} while (false);
			if (currentGraphY > 0 && _directions[currentGraphX][currentGraphY - 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_NE)
					&& !ClipFlag.flagged(_clip[currentGraphX + (size - 1)][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_W, ClipFlag.PF_NW))
				exit: do {
					for (int y = 1; y < (size - 1); y++)
						if (ClipFlag.flagged(_clip[currentGraphX + y][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_W, ClipFlag.PF_NW, ClipFlag.PF_NE))
							break exit;
					_bufferX[write] = currentX;
					_bufferY[write] = currentY - 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX][currentGraphY - 1] = DIR_NORTH;
					_distances[currentGraphX][currentGraphY - 1] = nextDistance;
				} while (false);
			if (currentGraphY < (GRAPH_SIZE - size) && _directions[currentGraphX][currentGraphY + 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX][currentGraphY + size], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_SE)
					&& !ClipFlag.flagged(_clip[currentGraphX + (size - 1)][currentGraphY + size], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_SW))
				exit: do {
					for (int y = 1; y < (size - 1); y++)
						if (ClipFlag.flagged(_clip[currentGraphX + y][currentGraphY + size], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_SE, ClipFlag.PF_SW))
							break exit;
					_bufferX[write] = currentX;
					_bufferY[write] = currentY + 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX][currentGraphY + 1] = DIR_SOUTH;
					_distances[currentGraphX][currentGraphY + 1] = nextDistance;
				} while (false);
			if (currentGraphX > 0 && currentGraphY > 0 && _directions[currentGraphX - 1][currentGraphY - 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_NE))
				exit: do {
					for (int y = 1; y < size; y++)
						if (ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY + (y - 1)], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_NE, ClipFlag.PF_SE)
								|| ClipFlag.flagged(_clip[currentGraphX + (y - 1)][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_W, ClipFlag.PF_NW, ClipFlag.PF_NE))
							break exit;
					_bufferX[write] = currentX - 1;
					_bufferY[write] = currentY - 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX - 1][currentGraphY - 1] = DIR_NORTH | DIR_EAST;
					_distances[currentGraphX - 1][currentGraphY - 1] = nextDistance;
				} while (false);
			if (currentGraphX < (GRAPH_SIZE - size) && currentGraphY > 0 && _directions[currentGraphX + 1][currentGraphY - 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX + size][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_W, ClipFlag.PF_NW))
				exit: do {
					for (int y = 1; y < size; y++)
						if (ClipFlag.flagged(_clip[currentGraphX + size][currentGraphY + (y - 1)], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_NW, ClipFlag.PF_SW)
								|| ClipFlag.flagged(_clip[currentGraphX + y][currentGraphY - 1], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_W, ClipFlag.PF_NW, ClipFlag.PF_NE))
							break exit;
					_bufferX[write] = currentX + 1;
					_bufferY[write] = currentY - 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX + 1][currentGraphY - 1] = DIR_NORTH | DIR_WEST;
					_distances[currentGraphX + 1][currentGraphY - 1] = nextDistance;
				} while (false);
			if (currentGraphX > 0 && currentGraphY < (GRAPH_SIZE - size) && _directions[currentGraphX - 1][currentGraphY + 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY + size], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_SE))
				exit: do {
					for (int y = 1; y < size; y++)
						if (ClipFlag.flagged(_clip[currentGraphX - 1][currentGraphY + y], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_NE, ClipFlag.PF_SE)
								|| ClipFlag.flagged(_clip[currentGraphX + (y - 1)][currentGraphY + size], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_SE, ClipFlag.PF_SW))
							break exit;
					_bufferX[write] = currentX - 1;
					_bufferY[write] = currentY + 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX - 1][currentGraphY + 1] = DIR_SOUTH | DIR_EAST;
					_distances[currentGraphX - 1][currentGraphY + 1] = nextDistance;
				} while (false);
			if (currentGraphX < (GRAPH_SIZE - size) && currentGraphY < (GRAPH_SIZE - size) && _directions[currentGraphX + 1][currentGraphY + 1] == 0
					&& !ClipFlag.flagged(_clip[currentGraphX + size][currentGraphY + size], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_SW))
				exit: do {
					for (int y = 1; y < size; y++)
						if (ClipFlag.flagged(_clip[currentGraphX + y][currentGraphY + size], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_E, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_SE, ClipFlag.PF_SW)
								|| ClipFlag.flagged(_clip[currentGraphX + size][currentGraphY + y], ClipFlag.PFBW_FLOOR, ClipFlag.PFBW_GROUND_DECO, ClipFlag.PF_FULL, ClipFlag.PF_N, ClipFlag.PF_S, ClipFlag.PF_W, ClipFlag.PF_NW, ClipFlag.PF_SW))
							break exit;
					_bufferX[write] = currentX + 1;
					_bufferY[write] = currentY + 1;
					write = (write + 1) & (QUEUE_SIZE - 1);

					_directions[currentGraphX + 1][currentGraphY + 1] = DIR_SOUTH | DIR_WEST;
					_distances[currentGraphX + 1][currentGraphY + 1] = nextDistance;
				} while (false);

		}

		exitX = currentX;
		exitY = currentY;
		return false;
	}

	private void transmitClipData(int x, int y, int z) {
		int graphBaseX = x - (GRAPH_SIZE / 2);
		int graphBaseY = y - (GRAPH_SIZE / 2);

		for (int transmitRegionX = graphBaseX >> 6; transmitRegionX <= (graphBaseX + (GRAPH_SIZE - 1)) >> 6; transmitRegionX++) {
			for (int transmitRegionY = graphBaseY >> 6; transmitRegionY <= (graphBaseY + (GRAPH_SIZE - 1)) >> 6; transmitRegionY++) {
				int startX = Math.max(graphBaseX, transmitRegionX << 6), startY = Math.max(graphBaseY, transmitRegionY << 6);
				int endX = Math.min(graphBaseX + GRAPH_SIZE, (transmitRegionX << 6) + 64), endY = Math.min(graphBaseY + GRAPH_SIZE, (transmitRegionY << 6) + 64);
				for (int fillX = startX; fillX < endX; fillX++)
					for (int fillY = startY; fillY < endY; fillY++)
						clip[fillX - graphBaseX][fillY - graphBaseY] = WorldCollision.getFlags(Tile.of(fillX, fillY, z));
			}
		}
	}

	public int[] getBufferX() {
		return bufferX;
	}

	public int[] getBufferY() {
		return bufferY;
	}

	public boolean foundAltRoute() {
		return foundAltRoute;
	}

	public int getStepCount() {
		return stepCount;
	}
}
