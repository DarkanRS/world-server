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
package com.rs.tools.old;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.MapSpriteDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.OverlayDefinitions;
import com.rs.cache.loaders.SpriteDefinitions;
import com.rs.cache.loaders.TextureDefinitions;
import com.rs.cache.loaders.UnderlayDefinitions;
import com.rs.lib.io.InputStream;
import com.rs.lib.util.MapXTEAs;

public class MapDump {

	private static final int SIZE = 200;

	public static void main(String[] args) throws IOException {
		//Cache.init();
		MapXTEAs.loadKeys();
		File outputfile = new File("map.png");
		ImageIO.write(getMap(0), "png", outputfile);
	}

	@SuppressWarnings("unused")
	public static BufferedImage getMap(int z) {
		int[][] rgb = new int[SIZE * 64][SIZE * 64];
		int[] a1 = new int[(64 * SIZE) * (64 * SIZE)];
		int[] a2 = new int[(64 * SIZE) * (64 * SIZE)];
		int[] a3 = new int[(64 * SIZE) * (64 * SIZE)];
		int[] a4 = new int[(64 * SIZE) * (64 * SIZE)];
		int[] a5 = new int[(64 * SIZE) * (64 * SIZE)];
		BufferedImage img = new BufferedImage(rgb.length, rgb[0].length, BufferedImage.TYPE_INT_RGB);
		int[][] overlayIds = new int[64 * SIZE][64 * SIZE];
		int[][] underlayIds = new int[64 * SIZE][64 * SIZE];
		int[][] dataOpcodes = new int[64 * SIZE][64 * SIZE];
		int[][] shapes = new int[64 * SIZE][64 * SIZE];
		int[][] overlayRotations = new int[64 * SIZE][64 * SIZE];
		int[][] heights = new int[64 * SIZE][64 * SIZE];
		int[][] masks = new int[64 * SIZE][64 * SIZE];

		for (int rx = 0; rx < SIZE; rx++)
			for (int ry = 0; ry < SIZE; ry++) {
				int regionId = (rx << 8) | ry;
				int regionX = (regionId >> 8) * 64;
				int regionY = (regionId & 0xff) * 64;
				int mapArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("m" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
				byte[] data = mapArchiveId == -1 ? null : Cache.STORE.getIndex(IndexType.MAPS).getFile(mapArchiveId, 0);
				if (data == null)
					continue;

				if (data != null) {
					InputStream mapStream = new InputStream(data);
					for (int plane = 0; plane < 4; plane++)
						for (int x = 0; x < 64; x++)
							for (int y = 0; y < 64; y++)
								while (true) {
									int value = mapStream.readUnsignedByte();
									if (plane == 0)
										dataOpcodes[rx * 64 + x][ry * 64 + y] = value;
									if (value == 0)
										break;
									if (value == 1) {
										int v = mapStream.readUnsignedByte();
										if (plane == 0)
											heights[rx * 64 + x][ry * 64 + y] = v;
										break;
									}
									if (value <= 49) {
										int v = mapStream.readUnsignedByte();
										if (plane == 0) {
											overlayIds[rx * 64 + x][ry * 64 + y] = v;
											shapes[rx * 64 + x][ry * 64 + y] = (byte) ((value - 2) / 4);
											overlayRotations[rx * 64 + x][ry * 64 + y] = (byte) ((value - 2) & 0x3);
										}
									} else if (value <= 81) {
										if (plane == 0)
											masks[rx * 64 + x][ry * 64 + y] = (byte) (value - 49);
									} else if (plane == 0)
										underlayIds[rx * 64 + x][ry * 64 + y] = (value - 81);
								}
				}
			}

		for (int x = -5; x < rgb.length; x++) {
			for (int y = 0; y < rgb[0].length; y++) {
				int x2 = 5 + x;
				if (x2 < rgb.length) {
					int i_98_ = ((underlayIds[x2][y]) & 0x7fff);
					if (i_98_ > 0) {
						UnderlayDefinitions defs = UnderlayDefinitions.getUnderlayDefinitions(i_98_ - 1);
						a1[y] += defs.r;
						a2[y] += defs.g;
						a3[y] += defs.b;
						a4[y] += defs.a;
						a5[y]++;
					}
				}
				int x3 = x - 5;
				if (x3 >= 0) {
					int i_100_ = ((underlayIds[x3][y]) & 0x7fff);
					if (i_100_ > 0) {
						UnderlayDefinitions defs = UnderlayDefinitions.getUnderlayDefinitions(i_100_ - 1);
						a1[y] -= defs.r;
						a2[y] -= defs.g;
						a3[y] -= defs.b;
						a4[y] -= defs.a;
						a5[y]--;
					}
				}
			}

			if (x >= 0) {
				int c1 = 0;
				int c2 = 0;
				int c3 = 0;
				int r2 = 0;
				int r3 = 0;
				for (int y = -5; y < rgb[0].length; y++) {
					int i_107_ = y + 5;
					if (i_107_ < rgb[0].length) {
						c1 += a1[i_107_];
						c2 += a2[i_107_];
						c3 += a3[i_107_];
						r2 += a4[i_107_];
						r3 += a5[i_107_];
					}
					int i_108_ = y - 5;
					if (i_108_ >= 0) {
						c1 -= a1[i_108_];
						c2 -= a2[i_108_];
						c3 -= a3[i_108_];
						r2 -= a4[i_108_];
						r3 -= a5[i_108_];
					}
					if (y >= 0 && r3 > 0)
						rgb[x][y] = new Color(c1 / r3, c2 / r3, c3 / r3).getRGB();
				}
			}
		}

		for (int x = 0; x < rgb.length; x++)
			for (int y = 0; y < rgb[0].length; y++)
				if (((overlayIds[x][y]) & 0x7fff) > 0) {
					OverlayDefinitions defs = OverlayDefinitions.getOverlayDefinitions(((overlayIds[x][y]) & 0x7fff) - 1);
					int col = defs.primaryRgb == -1 || defs.primaryRgb == 16711935 || defs.primaryRgb == 0 ? defs.secondaryRgb : defs.primaryRgb;
					if (col == 0 || col == -1 || col == 16711935)
						col = 0;
					if (col == 0 && defs.texture != -1)
						col = TextureDefinitions.getDefinitions(defs.texture & 0xFF).color;
					img.setRGB(x, (rgb[0].length - 1) - y, new Color(col).getRGB());
				} else if (((shapes[x][y]) & 0x7fff) > 0) {
					OverlayDefinitions defs = OverlayDefinitions.getOverlayDefinitions(((shapes[x][y]) & 0x7fff) - 1);
					int col = defs.primaryRgb == -1 || defs.primaryRgb == 16711935 || defs.primaryRgb == 0 ? defs.secondaryRgb : defs.primaryRgb;
					if (col == 0 || col == -1 || col == 16711935)
						col = 0;
					img.setRGB(x, (rgb[0].length - 1) - y, new Color(col).getRGB());
				} else {
					//					//RAW rgb setting no blending
					//					UnderlayDefinitions defs = UnderlayDefinitions.getUnderlayDefinitions(((underlayData[(int) (x / scale)][(int) (y / scale)]) & 0x7fff) - 1);
					//					int col = defs.rgb == -1 || defs.rgb == 16711935 || defs.rgb == 0 ? defs.rgb : defs.rgb;
					//					if (col == 0 || col == -1 || col == 16711935) {
					//						col = 0;
					//					}
					//					img.setRGB(x, (rgb[0].length - 1) - y, new Color(defs.r, defs.g, defs.b).getRGB());
					//img.setRGB(x, (rgb[0].length - 1) - y, rgb[x][y]);
				}

		for (int rx = 0; rx < SIZE; rx++)
			for (int ry = 0; ry < SIZE; ry++) {
				int regionId = (rx << 8) | ry;
				int regionX = (regionId >> 8) * 64;
				int regionY = (regionId & 0xff) * 64;
				int landArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
				byte[] data = landArchiveId == -1 ? null : Cache.STORE.getIndex(IndexType.MAPS).getFile(landArchiveId, 0, MapXTEAs.getMapKeys(regionId));
				if (data == null)
					continue;
				InputStream landStream = new InputStream(data);
				int objectId = -1;
				int incr;
				while ((incr = landStream.readSmart2()) != 0) {
					objectId += incr;
					int location = 0;
					int incr2;
					while ((incr2 = landStream.readUnsignedSmart()) != 0) {
						location += incr2 - 1;
						int localX = (location >> 6 & 0x3f);
						int localY = (location & 0x3f);
						int plane = location >> 12;
						int objectData = landStream.readUnsignedByte();
						int type = objectData >> 2;
						int rotation = objectData & 0x3;
						if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64)
							continue;

						if (plane == z) {
							int mapSpriteId = ObjectDefinitions.getDefs(objectId).mapSpriteId;
							if (mapSpriteId == -1)
								continue;
							int spriteId = MapSpriteDefinitions.getMapSpriteDefinitions(mapSpriteId).spriteId;
							if (spriteId == -1)
								continue;
							SpriteDefinitions sprite = new SpriteDefinitions(Cache.STORE, spriteId, 0);
							BufferedImage s = sprite.getImages()[0];
							int width = s.getWidth()/2;
							int height = s.getHeight()/2;
							if (width == 0 || height == 0)
								continue;

							//img.getGraphics().drawImage(s.getScaledInstance(width, height, 0), (int) ((rx * 64 + localX)), (int) (((SIZE * 64 - 1) - (ry * 64 + localY))), null);
						}
					}
				}
			}

		return img;

	}

	public static final int getV(int i, int i_1_, int i_2_) {
		if (i_2_ > 243)
			i_1_ >>= 4;
			else if (i_2_ > 217)
				i_1_ >>= 3;
				else if (i_2_ > 192)
					i_1_ >>= 2;
					else if (i_2_ > 179)
						i_1_ >>= 1;
						return (i_2_ >> 1) + (((i & 0xff) >> 2 << 10) + (i_1_ >> 5 << 7));
	}
}
