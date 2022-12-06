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
package com.rs.game.content.minigames.barrows;
import java.util.ArrayList;

import com.rs.game.content.minigames.barrows.BarrowsRoom.RoomType;
import com.rs.game.content.minigames.barrows.Link.RoomStatus;
import com.rs.game.model.entity.player.Player;
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
		ArrayList<Link> links = new ArrayList<>();

		BarrowsRoom start = corners[startIndex];
		ArrayList<Link> paths = start.getLinks();
		paths.get(random.nextInt(4)).setState(RoomStatus.OPEN);
		for (Link l : paths) {
			links.add(l);
			l.setState(RoomStatus.CLOSED);
		}
		for (Link l : paths) {
			BarrowsRoom o = l.getOther(start);
			if (o.getType() == RoomType.EDGE)
				for (Link ll : o.getLinks()) {
					if (links.contains(ll))
						continue;
					links.add(ll);
					if (ll.getOther(o).getType() == RoomType.CORNER)
						ll.setState(RoomStatus.OPEN);
				}
		}
		for (Link l : paths) {
			BarrowsRoom o = l.getOther(start);
			if (o.getType() == RoomType.CORNER) {
				ArrayList<Link> toCheck = new ArrayList<>();
				for (Link ll : o.getLinks()) {
					if (links.contains(ll))
						continue;
					links.add(ll);
					toCheck.add(ll);
				}
				for (Link l2 : toCheck) {
					BarrowsRoom o2 = l2.getOther(o);
					if (o2.getType() == RoomType.EDGE)
						for (Link lll : o2.getLinks()) {
							if (links.contains(lll))
								continue;
							links.add(lll);
							if (lll.getOther(o2).getType() == RoomType.CORNER)
								lll.setState(RoomStatus.OPEN);
						}
				}
				toCheck.get(random.nextInt(2)).setState(RoomStatus.OPEN);
				for (Link s : toCheck)
					s.setState(RoomStatus.CLOSED);
			}
		}

		treasure.getLinks().get(random.nextInt(4)).setState(RoomStatus.OPEN);
		for (Link l : treasure.getLinks())
			l.setState(RoomStatus.CLOSED);

		return links;
	}

	public static void updatePathDoors(Player p, ArrayList<Link> path) {
		for (Link l : path)
			for (Object[] element : DOORS) {
				String roomA = l.getRoomA().getName();
				String roomB = l.getRoomB().getName();
				if ((roomA == element[0] || roomA == element[1]) && (roomB == element[0] || roomB == element[1]))
					if (l.getState() == RoomStatus.OPEN)
						p.getVars().setVarBit((int)element[2], 0);
					else
						p.getVars().setVarBit((int)element[2], 1);
			}
	}

	public static void setSpawn(Player p, int spawn) {
		for (int i=0; i<4; i++)
			p.getVars().setVarBit((465+i),(i == spawn ? 1 : 0));
		switch (spawn) {
		case 0:
			p.setNextWorldTile(WorldTile.of(3535, 9712, 0));
			break;
		case 1:
			p.setNextWorldTile(WorldTile.of(3567, 9712, 0));
			break;
		case 2:
			p.setNextWorldTile(WorldTile.of(3535, 9678, 0));
			break;
		case 3:
			p.setNextWorldTile(WorldTile.of(3569, 9678, 0));
			break;
		}
	}

}