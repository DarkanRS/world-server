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
package com.rs.game.player.content.skills.dungeoneering;

public enum KinshipPerk {
	TANK(), //DONE
	TACTICIAN(), //DONE
	BERSERKER(), //DONE
	SNIPER(), //TODO
	KEEN_EYE(), //DONE
	DESPERADO(), //DONE
	BLAZER(), //DONE
	BLASTER(), //TODO
	BLITZER(), //DONE
	MEDIC(), //DONE
	GATHERER(), //TODO
	ARTISAN(); //TODO
	
	public int getVarbit() {
		return 8053 + ordinal();
	}
	
	public int getItemId() {
		return 18817 + ordinal();
	}
}
