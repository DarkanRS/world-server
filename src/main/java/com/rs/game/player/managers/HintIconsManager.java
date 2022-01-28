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
package com.rs.game.player.managers;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.lib.game.HintIcon;

public class HintIconsManager {

	private Player player;
	private HintIcon[] loadedIcons;

	public HintIconsManager(Player p) {
		player = p;
		loadedIcons = new HintIcon[7];
	}

	public int addHintIcon(int index, Entity target, int arrowType, int modelId, boolean saveIcon) {
		if (index != -1) {
			HintIcon icon = new HintIcon(target.getIndex(), target instanceof Player ? 10 : 1, arrowType, modelId, index);
			player.getPackets().sendHintIcon(icon);
			if (saveIcon)
				loadedIcons[index] = icon;
		}
		return index;
	}

	public int addHintIcon(Entity target, int arrowType, int modelId, boolean saveIcon) {
		int index = saveIcon ? getFreeIndex() : 7;
		if (index != -1) {
			HintIcon icon = new HintIcon(target.getIndex(), target instanceof Player ? 10 : 1, arrowType, modelId, index);
			player.getPackets().sendHintIcon(icon);
			if (saveIcon)
				loadedIcons[index] = icon;
		}
		return index;
	}

	public int addHintIcon(int coordX, int coordY, int height, int distanceFromFloor, int direction, int arrowType, int modelId, boolean saveIcon) {
		int index = saveIcon ? getFreeIndex() : 7;
		if (index != -1) {
			if (direction < 2 || direction > 6)
				direction = 2;
			HintIcon icon = new HintIcon(coordX, coordY, height, distanceFromFloor, direction, arrowType, modelId, index);
			player.getPackets().sendHintIcon(icon);
			if (saveIcon)
				loadedIcons[index] = icon;
		}
		return index;
	}

	public int addHintIcon(int modelId, boolean saveIcon) {
		int index = saveIcon ? getFreeIndex() : 7;
		if (index != -1) {
			HintIcon icon = new HintIcon(8, modelId, index);
			player.getPackets().sendHintIcon(icon);
			if (saveIcon)
				loadedIcons[index] = icon;
		}
		return index;
	}

	public void removeUnsavedHintIcon() {
		player.getPackets().sendHintIcon(new HintIcon());
	}

	public boolean reloadHintIcon(int index) {
		if ((index >= loadedIcons.length) || (loadedIcons[index] == null))
			return false;
		player.getPackets().sendHintIcon(loadedIcons[index]);
		return true;
	}

	public boolean removeHintIcon(int index) {
		if (index == 7) {
			removeUnsavedHintIcon();
			return true;
		}
		if ((index >= loadedIcons.length) || (loadedIcons[index] == null))
			return false;
		loadedIcons[index].setTargetType(0);
		player.getPackets().sendHintIcon(loadedIcons[index]);
		loadedIcons[index] = null;
		return true;
	}

	public void removeAll() {
		for (int index = 0; index < loadedIcons.length; index++)
			if (loadedIcons[index] != null) {
				loadedIcons[index].setTargetType(0);
				player.getPackets().sendHintIcon(loadedIcons[index]);
				loadedIcons[index] = null;
			}
	}

	public boolean isEmpty() {
		for (HintIcon loadedIcon : loadedIcons)
			if (loadedIcon != null)
				return false;
		return true;
	}

	private int getFreeIndex() {
		for (int index = 0; index < loadedIcons.length; index++)
			if (loadedIcons[index] == null)
				return index;
		return -1;
	}

	public boolean hasHintIcon(int index) {
		if (index >= loadedIcons.length)
			return false;
		return loadedIcons[index] != null;
	}
}