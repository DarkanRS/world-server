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
package com.rs.engine.dialogue.impl;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.MakeXStatement;

import java.util.ArrayList;
import java.util.List;

public class MakeXActionD extends Dialogue {

	private List<MakeXItem> options = new ArrayList<>();

	public MakeXActionD addOption(MakeXItem option) {
		clearChildren();
		options.add(option);
		MakeXItem[] opArr = new MakeXItem[options.size()];
		options.toArray(opArr);
		addStatementWithOptions(new MakeXStatement(opArr, 28), opArr);
		return this;
	}

	public boolean isEmpty() {
		return options.isEmpty();
	}

}
