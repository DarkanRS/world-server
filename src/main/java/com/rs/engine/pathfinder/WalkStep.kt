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
package com.rs.engine.pathfinder

class WalkStep(@JvmField val dir: Direction, @JvmField val x: Int, @JvmField val y: Int, private var clip: Boolean) {
    fun checkClip(): Boolean {
        return clip
    }

    fun setCheckClip(clip: Boolean) {
        this.clip = clip
    }

    override fun toString(): String {
        return "[$x, $y, $dir, $clip]"
    }
}
