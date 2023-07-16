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
package com.rs.tools;

import com.rs.cache.Cache;
import com.rs.cache.loaders.*;
import com.rs.cache.loaders.map.Region;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.MapXTEAs;
import com.rs.utils.BigBufferedImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapImageDumper {

	private static int[][] TILE_SHAPES = {
			{
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1
			},
			{
				1, 0, 0, 0,
				1, 1, 0, 0,
				1, 1, 1, 0,
				1, 1, 1, 1
			},

			{
				1, 1, 0, 0,
				1, 1, 0, 0,
				1, 0, 0, 0,
				1, 0, 0, 0
			},

			{
				0, 0, 1, 1,
				0, 0, 1, 1,
				0, 0, 0, 1,
				0, 0, 0, 1,
			},
			{
				0, 1, 1, 1,
				0, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1
			},
			{
				1, 1, 1, 0,
				1, 1, 1, 0,
				1, 1, 1, 1,
				1, 1, 1, 1
			},
			{
				1, 1, 0, 0,
				1, 1, 0, 0,
				1, 1, 0, 0,
				1, 1, 0, 0
			},
			{
				0, 0, 0, 0,
				0, 0, 0, 0,
				1, 0, 0, 0,
				1, 1, 0, 0
			},
			{
				1, 1, 1, 1,
				1, 1, 1, 1,
				0, 1, 1, 1,
				0, 0, 1, 1
			},
			{
				1, 1, 1, 1,
				1, 1, 0, 0,
				1, 0, 0, 0,
				1, 0, 0, 0
			},

			{
				0, 0, 0, 0,
				0, 0, 1, 1,
				0, 1, 1, 1,
				0, 1, 1, 1
			},

			{
				0, 0, 0, 0,
				0, 0, 0, 0,
				0, 1, 1, 0,
				1, 1, 1, 1
			}
	};

	private static int[][] TILE_ROTATIONS = {
			{
				0, 1, 2, 3,
				4, 5, 6, 7,
				8, 9, 10, 11,
				12, 13, 14, 15
			},

			{
				12, 8, 4, 0,
				13, 9, 5, 1,
				14, 10, 6, 2,
				15, 11, 7, 3
			},

			{
				15, 14, 13, 12,
				11, 10, 9, 8,
				7, 6, 5, 4,
				3, 2, 1, 0
			},

			{
				3, 7, 11, 15,
				2, 6, 10, 14,
				1, 5, 9, 13,
				0, 4, 8, 12
			}
	};

	private final List<Region> regions = new ArrayList<>();
	private final List<Integer> flags = new ArrayList<>();

	private Region lowestX;
	private Region lowestY;
	private Region highestX;
	private Region highestY;

	private static final int MAX_REGION = Short.MAX_VALUE-2;
	private static final int PIXELS_PER_TILE = 4;

	private static final boolean DRAW_UNDERLAY = true;
	private static final boolean DRAW_OVERLAY = true;
	private static final boolean DRAW_OBJECTS = true;
	private static final boolean DRAW_WALLS = true;
	private static final boolean DRAW_ICONS = true;

	private static final boolean DRAW_REGIONS = false;
	private static final boolean LABEL = true;
	private static final boolean OUTLINE = true;
	private static final boolean FILL = true;

	private static final int Z_MIN = 0, Z_MAX = 3;

	private void initialize() throws IOException {
		for (int i = 0; i < MAX_REGION; i++) {
			final Region region = new Region(i);
			region.loadRegionMap(true);
			if (region.isMissingXtea())
				flags.add(i);
			if (region.hasData() || i == 0) {
				regions.add(region);
				if (lowestX == null || region.getBaseX() < lowestX.getBaseX())
					lowestX = region;

				if (highestX == null || region.getBaseX() > highestX.getBaseX())
					highestX = region;

				if (lowestY == null || region.getBaseY() < lowestY.getBaseY())
					lowestY = region;

				if (highestY == null || region.getBaseY() > highestY.getBaseY())
					highestY = region;
			}
		}
	}

	private void draw() throws IOException {
		int minX = lowestX.getBaseX();
		int minY = lowestY.getBaseY();

		int maxX = highestX.getBaseX() + Region.WIDTH;
		int maxY = highestY.getBaseY() + Region.HEIGHT;

		int dimX = maxX - minX;
		int dimY = maxY - minY;

		int boundX = dimX - 1;
		int boundY = dimY - 1;

		dimX *= PIXELS_PER_TILE;
		dimY *= PIXELS_PER_TILE;

		for (int z = Z_MIN; z <= Z_MAX; z++) {
			if (z != 0)
				continue;
			System.out.println("Generating map images for z = " + z);

			System.out.println("dimx: " + dimX + ", " + dimY);

			BufferedImage baseImage = BigBufferedImage.create(dimX, dimY, BufferedImage.TYPE_INT_RGB);
			BufferedImage fullImage = BigBufferedImage.create(dimX, dimY, BufferedImage.TYPE_INT_RGB);
			//BufferedImage baseImage = new BufferedImage(dimX, dimY, BufferedImage.TYPE_INT_RGB);
			//BufferedImage fullImage = new BufferedImage(dimX, dimY, BufferedImage.TYPE_INT_RGB);

			Graphics2D graphics = fullImage.createGraphics();

			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

			if (DRAW_UNDERLAY) {
				System.out.println("Drawing underlay");
				drawUnderlay(z, baseImage);
			}

			if (DRAW_UNDERLAY) {
				System.out.println("Blending underlay");
				blendUnderlay(z, baseImage, fullImage, boundX, boundY);
			}

			if (DRAW_OVERLAY) {
				System.out.println("Drawing overlay");
				drawOverlay(z, fullImage);
			}

			if (DRAW_OBJECTS) {
				System.out.println("Drawing locations");
				drawLocations(z, graphics);
			}

			if (DRAW_WALLS) {
				System.out.println("Drawing walls");
				drawWalls(z, graphics);
			}

			if (DRAW_ICONS) {
				System.out.println("Drawing icons");
				drawIcons(z, graphics);
			}

			if (DRAW_REGIONS) {
				System.out.println("Drawing regions");
				drawRegions(z, graphics);
			}

			graphics.dispose();

			System.out.println("Writing to files");
			ImageIO.write(fullImage, "png", new File("WORLD_MAP_" + z + ".png"));
		}
	}

	private void drawUnderlay(int z, BufferedImage image) {
		for (Region region : regions) {
			int drawBaseX = region.getBaseX() - lowestX.getBaseX();
			int drawBaseY = highestY.getBaseY() - region.getBaseY();

			for (int x = 0; x < Region.WIDTH; ++x) {
				int drawX = drawBaseX + x;

				for (int y = 0; y < Region.HEIGHT; ++y) {
					int drawY = drawBaseY + ((Region.HEIGHT - 1) - y);

					int underlayId = region.getUnderlayId(z, x, y) - 1;

					int rgb = Color.CYAN.getRGB();

					if (underlayId > -1) {
						UnderlayDefinitions underlay = UnderlayDefinitions.getUnderlayDefinitions(underlayId);
						rgb = underlay.rgb;
					}

					drawMapSquare(image, drawX, drawY, rgb, -1, -1);
				}
			}
		}
	}

	private void blendUnderlay(int z, BufferedImage baseImage, BufferedImage fullImage, int boundX, int boundY) {
		for (Region region : regions) {
			int drawBaseX = region.getBaseX() - lowestX.getBaseX();
			int drawBaseY = highestY.getBaseY() - region.getBaseY();

			for (int x = 0; x < Region.WIDTH; ++x) {
				int drawX = drawBaseX + x;

				for (int y = 0; y < Region.HEIGHT; ++y) {
					int drawY = drawBaseY + ((Region.HEIGHT - 1) - y);

					Color c = getMapSquare(baseImage, drawX, drawY);

					if (c.equals(Color.CYAN))
						continue;

					int tRed = 0, tGreen = 0, tBlue = 0;
					int count = 0;

					int maxDY = Math.min(boundY, drawY + 3);
					int maxDX = Math.min(boundX, drawX + 3);
					int minDY = Math.max(0, drawY - 3);
					int minDX = Math.max(0, drawX - 3);


					for (int dy = minDY; dy < maxDY; dy++)
						for (int dx = minDX; dx < maxDX; dx++) {
							c = getMapSquare(baseImage, dx, dy);

							if (c.equals(Color.CYAN))
								continue;

							tRed += c.getRed();
							tGreen += c.getGreen();
							tBlue += c.getBlue();
							count++;
						}

					if (count > 0) {
						c = new Color(tRed / count, tGreen / count, tBlue / count);
						drawMapSquare(fullImage, drawX, drawY, c.getRGB(), -1, -1);
					}
				}
			}
		}
	}

	private void drawOverlay(int z, BufferedImage image) {
		for (Region region : regions) {
			int drawBaseX = region.getBaseX() - lowestX.getBaseX();
			int drawBaseY = highestY.getBaseY() - region.getBaseY();

			for (int x = 0; x < Region.WIDTH; ++x) {
				int drawX = drawBaseX + x;

				for (int y = 0; y < Region.HEIGHT; ++y) {
					int drawY = drawBaseY + ((Region.HEIGHT - 1) - y);
					int overlayId = region.getOverlayId(z, x, y) - 1;
					int shapeId = region.getOverlayPathShape(z, x, y) - 1;
					if (overlayId > -1) {
						int rgb = OverlayDefinitions.getOverlayDefinitions(overlayId).getOverlayRGB();
						drawMapSquare(image, drawX, drawY, rgb, region.getOverlayPathShape(z, x, y), region.getOverlayRotation(z, x, y));
					} else if (shapeId > -1) {
						int rgb = OverlayDefinitions.getOverlayDefinitions(shapeId).getShapeRGB();
						drawMapSquare(image, drawX, drawY, rgb, region.getOverlayPathShape(z, x, y), region.getOverlayRotation(z, x, y));
					}
				}
			}
		}
	}

	private void drawLocations(int z, Graphics2D graphics) {
		for (Region region : regions) {
			int drawBaseX = region.getBaseX() - lowestX.getBaseX();
			int drawBaseY = highestY.getBaseY() - region.getBaseY();
			if (region.getObjects() == null)
				continue;
			for (WorldObject location : region.getObjects()) {
				int localX = location.getX() - region.getBaseX();
				int localY = location.getY() - region.getBaseY();

				if (!canDrawLocation(region, location, z, localX, localY))
					continue;

				ObjectDefinitions objType = ObjectDefinitions.getDefs(location.getId());

				int drawX = drawBaseX + localX;
				int drawY = drawBaseY + ((Region.HEIGHT - 1) - localY);

				if (objType.mapSpriteId != -1) {
					int spriteId = MapSpriteDefinitions.getMapSpriteDefinitions(objType.mapSpriteId).spriteId;
					if (spriteId == -1)
						continue;
					Image spriteImage = new SpriteDefinitions(Cache.STORE, spriteId, 0).getImages()[0];
					graphics.drawImage(spriteImage, drawX * PIXELS_PER_TILE, drawY * PIXELS_PER_TILE, null);
				}
			}
		}
	}

	private void drawWalls(int z, Graphics2D graphics) {
		for (Region region : regions) {
			int drawBaseX = region.getBaseX() - lowestX.getBaseX();
			int drawBaseY = highestY.getBaseY() - region.getBaseY();
			if (region.getObjects() == null)
				continue;
			for (WorldObject location : region.getObjects()) {
				graphics.setColor(Color.WHITE);

				int localX = location.getX() - region.getBaseX();
				int localY = location.getY() - region.getBaseY();

				if (!canDrawLocation(region, location, z, localX, localY))
					continue;

				ObjectDefinitions objType = ObjectDefinitions.getDefs(location.getId());

				// Don't draw walls on water
				if (objType.mapSpriteId == 22)
					continue;

				String objName = objType.getName().toLowerCase();

				if (objName.contains("door") || objName.contains("gate"))
					graphics.setColor(Color.RED);

				int drawX = drawBaseX + localX;
				int drawY = drawBaseY + ((Region.HEIGHT - 1) - localY);

				drawX *= PIXELS_PER_TILE;
				drawY *= PIXELS_PER_TILE;

				if (location.getType() == ObjectType.WALL_STRAIGHT) { // Straight walls
					if (location.getRotation() == 0)
						graphics.drawLine(drawX, drawY, drawX, drawY + PIXELS_PER_TILE);
					else if (location.getRotation() == 1)
						graphics.drawLine(drawX, drawY, drawX + PIXELS_PER_TILE, drawY);
					else if (location.getRotation() == 2)
						graphics.drawLine(drawX + PIXELS_PER_TILE, drawY, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
					else if (location.getRotation() == 3)
						graphics.drawLine(drawX, drawY + PIXELS_PER_TILE, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
				} else if (location.getType() == ObjectType.WALL_WHOLE_CORNER) { // Corner walls
					if (location.getRotation() == 0) { // West & South
						graphics.drawLine(drawX, drawY, drawX, drawY + PIXELS_PER_TILE);
						graphics.drawLine(drawX, drawY, drawX + PIXELS_PER_TILE, drawY);
					} else if (location.getRotation() == 1) { // South & East
						graphics.drawLine(drawX, drawY, drawX + PIXELS_PER_TILE, drawY);
						graphics.drawLine(drawX + PIXELS_PER_TILE, drawY, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
					} else if (location.getRotation() == 2) { // East & North
						graphics.drawLine(drawX + PIXELS_PER_TILE, drawY, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
						graphics.drawLine(drawX, drawY + PIXELS_PER_TILE, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
					} else if (location.getRotation() == 3) { // North & West
						graphics.drawLine(drawX, drawY + PIXELS_PER_TILE, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
						graphics.drawLine(drawX, drawY, drawX, drawY + PIXELS_PER_TILE);
					}
				} else if (location.getType() == ObjectType.WALL_STRAIGHT_CORNER) { // Single points
					if (location.getRotation() == 0)
						graphics.drawLine(drawX, drawY + 1, drawX, drawY + 1);
					else if (location.getRotation() == 1)
						graphics.drawLine(drawX + 3, drawY + 1, drawX + 3, drawY + 1);
					else if (location.getRotation() == 2)
						graphics.drawLine(drawX + 3, drawY + 4, drawX + 3, drawY + 4);
					else if (location.getRotation() == 3)
						graphics.drawLine(drawX, drawY + 3, drawX, drawY + 3);
				} else if (location.getType() == ObjectType.WALL_INTERACT)
					if (location.getRotation() == 0 || location.getRotation() == 2)
						graphics.drawLine(drawX, drawY + PIXELS_PER_TILE, drawX + PIXELS_PER_TILE, drawY);
					else if (location.getRotation() == 1 || location.getRotation() == 3)
						graphics.drawLine(drawX, drawY, drawX + PIXELS_PER_TILE, drawY + PIXELS_PER_TILE);
			}
		}
	}

	private void drawIcons(int z, Graphics2D graphics) {
		for (Region region : regions) {
			int drawBaseX = region.getBaseX() - lowestX.getBaseX();
			int drawBaseY = highestY.getBaseY() - region.getBaseY();
			if (region.getObjects() == null)
				continue;
			for (WorldObject location : region.getObjects()) {
				int localX = location.getX() - region.getBaseX();
				int localY = location.getY() - region.getBaseY();

				if (!canDrawLocation(region, location, z, localX, localY))
					continue;

				ObjectDefinitions objType = ObjectDefinitions.getDefs(location.getId());

				int drawX = drawBaseX + localX;
				int drawY = drawBaseY + (63 - localY);

				if (objType.mapIcon != -1) {
					AreaDefinitions areaType = AreaDefinitions.getDefinitions(objType.mapIcon);
					if (areaType.areaName != null)
						System.out.println("{ \"name\": \""+areaType.areaName+"\", \"coords\": ["+location.getX()+", "+location.getY()+", 0] },");
					if (areaType.spriteId != -1) {
						Image spriteImage = new SpriteDefinitions(Cache.STORE, areaType.spriteId, 0).getImages()[0];
						graphics.drawImage(spriteImage, (drawX - 1) * PIXELS_PER_TILE, (drawY - 1) * PIXELS_PER_TILE, null);
					}
				}
			}
		}
	}

	private boolean canDrawLocation(Region region, WorldObject location, int z, int x, int y) {
		if (region.isLinkedBelow(z, x, y) || region.isVisibleBelow(z, x, y))
			return false;

		if (location.getPlane() == z + 1 && (region.isLinkedBelow(z + 1, x, y) || region.isVisibleBelow(z + 1, x, y)))
			return true;

		return z == location.getPlane();
	}

	private void drawRegions(int z, Graphics2D graphics) {
		for (Region region : regions) {
			int baseX = region.getBaseX();
			int baseY = region.getBaseY();
			int drawBaseX = baseX - lowestX.getBaseX();
			int drawBaseY = highestY.getBaseY() - baseY;

			if (LABEL) {
				graphics.setColor(Color.RED);
				graphics.drawString(String.valueOf(region.getRegionId()), drawBaseX * PIXELS_PER_TILE, drawBaseY * PIXELS_PER_TILE + graphics.getFontMetrics().getHeight());
			}

			if (OUTLINE) {
				graphics.setColor(Color.RED);
				graphics.drawRect(drawBaseX * PIXELS_PER_TILE, drawBaseY * PIXELS_PER_TILE, 64 * PIXELS_PER_TILE, 64 * PIXELS_PER_TILE);
			}

			if (FILL)
				if (flags.contains(region.getRegionId())) {
					graphics.setColor(new Color(255, 0, 0, 80));
					graphics.fillRect(drawBaseX * PIXELS_PER_TILE, drawBaseY * PIXELS_PER_TILE, 64 * PIXELS_PER_TILE, 64 * PIXELS_PER_TILE);
				}
		}
	}

	private void drawMapSquare(BufferedImage image, int x, int y, int overlayRGB, int shape, int rotation) {
		if (shape > -1) {
			int[] shapeMatrix = TILE_SHAPES[shape];
			int[] rotationMatrix = TILE_ROTATIONS[rotation & 0x3];
			int shapeIndex = 0;
			for (int tilePixelY = 0; tilePixelY < PIXELS_PER_TILE; tilePixelY++)
				for (int tilePixelX = 0; tilePixelX < PIXELS_PER_TILE; tilePixelX++) {
					int drawx = x * PIXELS_PER_TILE + tilePixelX;
					int drawy = y * PIXELS_PER_TILE + tilePixelY;

					if (shapeMatrix[rotationMatrix[shapeIndex++]] != 0)
						image.setRGB(drawx, drawy, new Color(overlayRGB).getRGB());
				}
		} else
			for (int tilePixelY = 0; tilePixelY < PIXELS_PER_TILE; tilePixelY++)
				for (int tilePixelX = 0; tilePixelX < PIXELS_PER_TILE; tilePixelX++) {
					int drawx = x * PIXELS_PER_TILE + tilePixelX;
					int drawy = y * PIXELS_PER_TILE + tilePixelY;
					image.setRGB(drawx, drawy, new Color(overlayRGB).getRGB());
				}
	}

	public Color getMapSquare(BufferedImage image, int x, int y) {
		x *= PIXELS_PER_TILE;
		y *= PIXELS_PER_TILE;

		return new Color(image.getRGB(x, y));
	}

	public static void main(String[] args) throws IOException {
		//Cache.init();
		MapXTEAs.loadKeys();
		long ms = System.currentTimeMillis();
		MapImageDumper dumper = new MapImageDumper();
		dumper.initialize();
		dumper.draw();
		System.out.println("Time taken: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - ms) + "s");
	}

}