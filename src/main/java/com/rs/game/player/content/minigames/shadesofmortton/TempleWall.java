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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.minigames.shadesofmortton;

import com.rs.game.object.GameObject;
import com.rs.lib.util.Utils;

public class TempleWall extends GameObject {
	
	private int baseId;
	private int buildProgress;
	
	public TempleWall(GameObject object) {
		super(object);
		this.baseId = id;
		this.buildProgress = 5;
		ShadesOfMortton.addWall(this);
	}

	public void increaseProgress() {
		buildProgress += 4;
		update();
	}
	
	public void decreaseProgress() {
		buildProgress--;
		if (buildProgress <= 0) {
			destroy();
			return;
		}
		update();
	}
	
	public void destroy() {
		ShadesOfMortton.deleteWall(this);
	}
	
	public void update() {
		this.setId(baseId + Utils.clampI(buildProgress / 10, 0, 10));
	}
	
	public int getRepairPerc() {
		return Utils.clampI(buildProgress, 0, 100);
	}

}
