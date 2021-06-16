package com.rs.game.player.content.minigames.barrows;
import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.content.minigames.barrows.BarrowsRoom.RoomType;
import com.rs.game.player.content.minigames.barrows.Link.RoomStatus;
import com.rs.lib.game.WorldTile;

public class BarrowsPath {

	private static BarrowsRoom[] corners;
	private static BarrowsRoom treasure;
	public final static int 
		NORTHWEST_ROPE = 465,
		NORTHEAST_ROPE = 466,
		SOUTHWEST_ROPE = 467,
		SOUTHEAST_ROPE = 468;
	
		public static Object[][] DOORS = {
		{"NORTHWEST", "NORTHEAST", 469},
		{"NORTHWEST", "SOUTHWEST", 470},
		{"NORTHWEST", "WESTEDGE", 471},
		{"NORTHWEST", "NORTHEDGE", 472},
		{"NORTHEDGE", "CHEST", 473},
		{"NORTHEAST", "NORTHEDGE", 474},
		{"NORTHEAST", "EASTEEDGE", 475},
		{"NORTHEAST", "SOUTHEAST", 476},
		{"WESTEDGE", "CHEST", 477},
		{"EASTEDGE", "CHEST", 478},
		{"SOUTHWEST", "WESTEDGE", 479},
		{"SOUTHEDGE", "CHEST", 480},
		{"SOUTHEAST", "EASTEDGE", 481},
		{"SOUTHWEST", "SOUTHEDGE", 482},
		{"SOUTHEAST", "SOUTHEDGE", 483},
		{"SOUTHWEST", "SOUTHEAST", 484}};

	private static java.util.Random random = new java.util.Random(System.currentTimeMillis());

	public static void init() {
		BarrowsRoom topleft = new BarrowsRoom(RoomType.CORNER, "NORTHWEST");
		BarrowsRoom topright = new BarrowsRoom(RoomType.CORNER, "NORTHEAST");
		BarrowsRoom botleft = new BarrowsRoom(RoomType.CORNER, "SOUTHWEST");
		BarrowsRoom botright = new BarrowsRoom(RoomType.CORNER, "SOUTHEAST");

		BarrowsRoom topedge = new BarrowsRoom(RoomType.EDGE, "NORTHEDGE");
		BarrowsRoom leftedge = new BarrowsRoom(RoomType.EDGE, "WESTEDGE");
		BarrowsRoom rightedge = new BarrowsRoom(RoomType.EDGE, "EASTEDGE");
		BarrowsRoom botedge = new BarrowsRoom(RoomType.EDGE, "SOUTHEDGE");

		BarrowsRoom treasure = new BarrowsRoom(RoomType.TREASURE, "CHEST");

		topleft.addLink(topright);
		topleft.addLink(botleft);
		topleft.addLink(topedge);
		topleft.addLink(leftedge);

		topright.addLink(botright);
		topright.addLink(topedge);
		topright.addLink(rightedge);

		botright.addLink(botleft);
		botright.addLink(rightedge);
		botright.addLink(botedge);

		botleft.addLink(leftedge);
		botleft.addLink(botedge);

		treasure.addLink(topedge);
		treasure.addLink(rightedge);
		treasure.addLink(leftedge);
		treasure.addLink(botedge);

		corners = new BarrowsRoom[] { topleft, topright, botleft, botright };
		BarrowsPath.treasure = treasure;
	}

	public static ArrayList<Link> generateBarrowsPath(int startIndex) {
		init();
		ArrayList<Link> links = new ArrayList<Link>();

		BarrowsRoom start = corners[startIndex];
		ArrayList<Link> paths = start.getLinks();
		paths.get(random.nextInt(4)).setState(RoomStatus.OPEN);
		for (Link l : paths) {
			links.add(l);
			l.setState(RoomStatus.CLOSED);
		}
		for (Link l : paths) {
			BarrowsRoom o = l.getOther(start);
			if (o.getType() == RoomType.EDGE) {
				for (Link ll : o.getLinks()) {
					if (!links.contains(ll)) {
						links.add(ll);
					} else {
						continue;
					}
					if (ll.getOther(o).getType() == RoomType.CORNER) {
						ll.setState(RoomStatus.OPEN);
					}
				}
			}
		}
		for (Link l : paths) {
			BarrowsRoom o = l.getOther(start);
			if (o.getType() == RoomType.CORNER) {
				ArrayList<Link> toCheck = new ArrayList<Link>();
				for (Link ll : o.getLinks()) {
					if (!links.contains(ll)) {
						links.add(ll);
						toCheck.add(ll);
					} else {
						continue;
					}
				}
				for (Link l2 : toCheck) {
					BarrowsRoom o2 = l2.getOther(o);
					if (o2.getType() == RoomType.EDGE) {
						for (Link lll : o2.getLinks()) {
							if (!links.contains(lll)) {
								links.add(lll);
							} else {
								continue;
							}
							if (lll.getOther(o2).getType() == RoomType.CORNER) {
								lll.setState(RoomStatus.OPEN);
							}
						}
					}
				}
				toCheck.get(random.nextInt(2)).setState(RoomStatus.OPEN);
				for (Link s : toCheck) {
					s.setState(RoomStatus.CLOSED);
				}
			}
		}

		treasure.getLinks().get(random.nextInt(4)).setState(RoomStatus.OPEN);
		for (Link l : treasure.getLinks()) {
			l.setState(RoomStatus.CLOSED);
		}

		return links;
	}
	
	public static void updatePathDoors(Player p, ArrayList<Link> path) {
		for (Link l : path) {
			for (int i=0; i<DOORS.length; i++) {
				String roomA = l.getRoomA().getName();
				String roomB = l.getRoomB().getName();
				if ((roomA == DOORS[i][0] || roomA == DOORS[i][1]) && (roomB == DOORS[i][0] || roomB == DOORS[i][1])) {
					if (l.getState() == RoomStatus.OPEN)
						p.getVars().setVarBit((int)DOORS[i][2], 0);
					else
						p.getVars().setVarBit((int)DOORS[i][2], 1);
				}	
			}
		}
	}
	
	public static void setSpawn(Player p, int spawn) {
		for (int i=0; i<4; i++) {	
			p.getVars().setVarBit((465+i),(i == spawn ? 1 : 0));
		}
		switch (spawn) {
			case 0:
				p.setNextWorldTile(new WorldTile(3535, 9712, 0));
				break;
			case 1:
				p.setNextWorldTile(new WorldTile(3567, 9712, 0));
				break;
			case 2:
				p.setNextWorldTile(new WorldTile(3535, 9678, 0));
				break;
			case 3:
				p.setNextWorldTile(new WorldTile(3569, 9678, 0));
				break;
		}
	}

}