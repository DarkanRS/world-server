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
package com.rs.engine.miniquest;

import com.rs.game.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class MiniquestOutline {

	public final Miniquest getQuest() {
		return getClass().getAnnotation(MiniquestHandler.class).value();
	}

	public abstract int getCompletedStage();
	public abstract List<String> getJournalLines(Player player, int stage);
	public abstract void complete(Player player);
	public abstract void updateStage(Player player);
}
