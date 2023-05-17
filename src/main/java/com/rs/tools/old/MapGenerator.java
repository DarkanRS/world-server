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

import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.*;
import com.rs.lib.io.InputStream;
import com.rs.lib.util.MapXTEAs;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MapGenerator extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = -6054276817868497393L;

	public static void main(String[] args) throws IOException {
		//Cache.init();
		MapXTEAs.loadKeys();
		new MapGenerator();
	}

	public MapGenerator() {
		xPos = 1500;
		yPos = 1500;
		scale = 1;
		ratio = 20;
		setTitle("Map Viewer");
		setSize(640, 640);
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		checkLoad();

	}

	private int xPos, yPos, planePos, maxRX, maxRY, rxPos, ryPos, ratio;
	private double scale;

	private BufferedImage map;
	private BufferedImage screen;
	private boolean loading;
	private boolean forceNoRefresh;

	public void checkLoad() {
		if (!loading) {
			final int newRX = xPos / 64;
			final int newRY = yPos / 64;
			final int newMaxRX = (int) ((getWidth() / 64) / scale) + (int) (1 * scale) + ratio * 2;
			final int newMaxRY = (int) ((getHeight() / 64) / scale) + (int) (1 * scale) + ratio * 2;
			if (Math.abs(newRX - rxPos) >= ratio / 2 || Math.abs(newRY - ryPos) >= ratio / 2 || newMaxRX != maxRX || newMaxRY != maxRY || forceNoRefresh) {
				loading = true;
				repaint();
				Thread t = new Thread() {

					@Override
					public void run() {
						map = getMap(newRX, newRY, newMaxRX, newMaxRY);
						rxPos = newRX;
						ryPos = newRY;
						maxRX = newMaxRX;
						maxRY = newMaxRY;
						loading = false;
						forceNoRefresh = false;
						repaint();
					}

				};
				t.setPriority(Thread.MIN_PRIORITY);
				t.setDaemon(true);
				t.start();
			}
		}
	}

	@Override
	public void paint(Graphics g) {

		//		for (int x = 0; x < 10; x++) {
		//			for (int y = 0; y < 10; y++) {
		//				int regionId = ((x + xPos) << 8) | (y + yPos);
		//				BufferedImage[] data = getMap(regionId);
		//				if (data == null)
		//					continue;
		//				g.drawImage(data[planePos], x * 64, 640 - y * 64, null);
		//
		//			}
		//		}

		screen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g2 = screen.getGraphics();
		int localX = xPos & 63;
		int localY = yPos & 63;

		if (map != null && !forceNoRefresh)
			g2.drawImage(map, (int) (((-localX) + (rxPos - xPos / 64) * 64 - ratio * 64) * scale), (int) ((-((64 + (ryPos - yPos / 64) * 64 + ratio * 64) - localY)) * scale), this);
		g2.setColor(Color.PINK);
		if (loading)
			g2.drawString("Loading", 50, 50);
		if (mouseLocation2 != null) {
			int[] pos = getPos();
			g2.drawString("Position: " + pos[0] + ", " + pos[1], 50, 60);
		}
		g2.dispose();
		g.drawImage(screen, 0, 0, this);
	}

	public int[] getPos() {
		return new int[] { (int) (xPos + ratio * 64 + mouseLocation2.getX() / scale), (int) (yPos + ratio * 64 + (getHeight() - mouseLocation2.getY() / scale)) };
	}

	@SuppressWarnings("unused")
	public BufferedImage getMap(int rxPos, int ryPos, int maxRX, int maxRY) {
		int[][] rgb = new int[(int) (maxRX * 64 * scale)][(int) (maxRY * 64 * scale)];
		int[] a1 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		int[] a2 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		int[] a3 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		int[] a4 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		int[] a5 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		BufferedImage img = new BufferedImage(rgb.length, rgb[0].length, BufferedImage.TYPE_INT_RGB);
		int[][] overlayIds = new int[64 * maxRX][64 * maxRY];
		int[][] underlayIds = new int[64 * maxRX][64 * maxRY];
		int[][] dataOpcodes = new int[64 * maxRX][64 * maxRY];
		int[][] shapes = new int[64 * maxRX][64 * maxRY];
		int[][] overlayRotations = new int[64 * maxRX][64 * maxRY];
		int[][] heights = new int[64 * maxRX][64 * maxRY];
		int[][] masks = new int[64 * maxRX][64 * maxRY];

		for (int rx = 0; rx < maxRX; rx++)
			for (int ry = 0; ry < maxRY; ry++) {
				int regionId = ((rx + rxPos) << 8) | (ry + ryPos);
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
									if (plane == planePos)
										dataOpcodes[rx * 64 + x][ry * 64 + y] = value;
									if (value == 0)
										break;
									if (value == 1) {
										int v = mapStream.readUnsignedByte();
										if (plane == planePos)
											heights[rx * 64 + x][ry * 64 + y] = v;
										break;
									}
									if (value <= 49) {
										int v = mapStream.readUnsignedByte();
										if (plane == planePos) {
											overlayIds[rx * 64 + x][ry * 64 + y] = v;
											shapes[rx * 64 + x][ry * 64 + y] = (byte) ((value - 2) / 4);
											overlayRotations[rx * 64 + x][ry * 64 + y] = (byte) ((value - 2) & 0x3);
										}
									} else if (value <= 81) {
										if (plane == planePos)
											masks[rx * 64 + x][ry * 64 + y] = (byte) (value - 49);
									} else if (plane == planePos)
										underlayIds[rx * 64 + x][ry * 64 + y] = (value - 81);
								}
				}
			}

		for (int x = -5; x < rgb.length; x++) {
			for (int y = 0; y < rgb[0].length; y++) {
				int x2 = 5 + x;
				if (x2 < rgb.length) {
					int i_98_ = ((underlayIds[(int) (x2 / scale)][(int) (y / scale)]) & 0x7fff);
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
					int i_100_ = ((underlayIds[(int) (x3 / scale)][(int) (y / scale)]) & 0x7fff);
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
					// sets map
					if (y >= 0 && r3 > 0)
						rgb[x][y] = new Color(c1 / r3, c2 / r3, c3 / r3).getRGB();
				}
			}
		}

		for (int x = 0; x < rgb.length; x++)
			for (int y = 0; y < rgb[0].length; y++)
				if (((overlayIds[(int) (x / scale)][(int) (y / scale)]) & 0x7fff) > 0) {
					OverlayDefinitions defs = OverlayDefinitions.getOverlayDefinitions(((overlayIds[(int) (x / scale)][(int) (y / scale)]) & 0x7fff) - 1);
					int col = defs.primaryRgb == -1 || defs.primaryRgb == 16711935 || defs.primaryRgb == 0 ? defs.secondaryRgb : defs.primaryRgb;
					if (col == 0 || col == -1 || col == 16711935)
						col = 0;
					if (col == 0 && defs.texture != -1)
						col = TextureDefinitions.getDefinitions(defs.texture & 0xFF).color;
					img.setRGB(x, (rgb[0].length - 1) - y, new Color(col).getRGB());
				} else if (((shapes[(int) (x / scale)][(int) (y / scale)]) & 0x7fff) > 0) {
					OverlayDefinitions defs = OverlayDefinitions.getOverlayDefinitions(((shapes[(int) (x / scale)][(int) (y / scale)]) & 0x7fff) - 1);
					int col = defs.primaryRgb == -1 || defs.primaryRgb == 16711935 || defs.primaryRgb == 0 ? defs.secondaryRgb : defs.primaryRgb;
					if (col == 0 || col == -1 || col == 16711935)
						col = 0;
					img.setRGB(x, (rgb[0].length - 1) - y, new Color(col).getRGB());
				} else
					//					//RAW rgb setting no blending
					//					UnderlayDefinitions defs = UnderlayDefinitions.getUnderlayDefinitions(((underlayData[(int) (x / scale)][(int) (y / scale)]) & 0x7fff) - 1);
					//					int col = defs.rgb == -1 || defs.rgb == 16711935 || defs.rgb == 0 ? defs.rgb : defs.rgb;
					//					if (col == 0 || col == -1 || col == 16711935) {
					//						col = 0;
					//					}
					//					img.setRGB(x, (rgb[0].length - 1) - y, new Color(defs.r, defs.g, defs.b).getRGB());
					img.setRGB(x, (rgb[0].length - 1) - y, rgb[x][y]);

		for (int rx = 0; rx < maxRX; rx++)
			for (int ry = 0; ry < maxRY; ry++) {
				int regionId = ((rx + rxPos) << 8) | (ry + ryPos);
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
						/*
						 * int objectPlane = plane; if (mapSettings != null && (mapSettings[1][localX][localY] & 2)
						 * == 2) objectPlane--; if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4)
						 * continue;
						 */

						// System.out.println(spriteId);
						if (plane == planePos) {
							int mapSpriteId = ObjectDefinitions.getDefs(objectId).mapSpriteId;
							if (mapSpriteId == -1)
								continue;
							int spriteId = MapSpriteDefinitions.getMapSpriteDefinitions(mapSpriteId).spriteId;
							if (spriteId == -1)
								continue;
							SpriteDefinitions sprite = new SpriteDefinitions(Cache.STORE, spriteId, 0);
							BufferedImage s = sprite.getImages()[0];
							int width = (int) (s.getWidth() / 2 * scale);
							int height = (int) (s.getHeight() / 2 * scale);
							if (width == 0 || height == 0)
								continue;
							/*
							 * if(width > s.getWidth() || height > s.getHeight()) { width = s.getWidth(); height =
							 * s.getHeight();
							 *
							 * }
							 */

							img.getGraphics().drawImage(s.getScaledInstance(width, height, 0), (int) ((rx * 64 + localX) * scale), (int) (((maxRY * 64 - 1) - (ry * 64 + localY)) * scale), null);
						}

						// spawnObject(new WorldObject(objectId, type, rotation,
						// localX + regionX*64, localY + regionY*64,
						// objectPlane), objectPlane, localX, localY, true);
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

	@Override
	public void mouseClicked(MouseEvent e) {
		mouseLocation = e.getPoint();
		int[] pos = getPos();
		StringSelection selection = new StringSelection(pos[0] + " " + pos[1] + " " + planePos);

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
		e.consume();
	}

	private Point mouseLocation;
	private Point mouseLocation2;

	@Override
	public void mousePressed(MouseEvent e) {
		mouseLocation = e.getPoint();
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		drag(e.getPoint());
		e.consume();
	}

	public void drag(Point newLoc) {
		int diffX = (int) ((mouseLocation.x - newLoc.x) / scale);
		int diffY = (int) ((mouseLocation.y - newLoc.y) / scale);
		if (diffX != 0 || diffY != 0) {
			xPos += diffX;
			yPos -= diffY;
			mouseLocation = newLoc;
			checkLoad();
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		drag(e.getPoint());
		e.consume();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseLocation2 = e.getPoint();
		this.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!loading) {
			if (e.getUnitsToScroll() > 0) {
				if (e.isControlDown())
					planePos = (planePos - 1) & 0x3;
				else
					scale /= 1.5;

			} else if (e.isControlDown())
				planePos = (planePos + 1) & 0x3;
			else
				scale *= 1.5;
			forceNoRefresh = true;
			checkLoad();
		}

	}

}
