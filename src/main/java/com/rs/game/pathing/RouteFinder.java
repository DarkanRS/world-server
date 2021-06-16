package com.rs.game.pathing;

/**
 * @author Mangis
 * 
 */
public class RouteFinder {

	public static final int WALK_ROUTEFINDER = 0;
	private static int lastUsed;

	public static int findRoute(int type, int srcX, int srcY, int srcZ, int srcSizeXY, RouteStrategy strategy, boolean findAlternative) {
		switch (lastUsed = type) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.findRoute(srcX, srcY, srcZ, srcSizeXY, strategy, findAlternative);
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static int[] getLastPathBufferX() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.getLastPathBufferX();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static int[] getLastPathBufferY() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.getLastPathBufferY();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static boolean lastIsAlternative() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.lastIsAlternative();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}
}
