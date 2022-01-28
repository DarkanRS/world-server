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
package com.rs.game.player.content.dialogue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Options {

	private Map<String, Option> options = new LinkedHashMap<>(); //LinkedHashMap O(1) but allows ordered keys
	private String stageName;
	private Conversation conv;

	public Options() {
		create();
	}

	public Options(String stageName, Conversation conv) {
		this.conv = conv;
		this.stageName = stageName;
		create();
	}

	public abstract void create();

	public void option(String name, Dialogue dialogue) {
		options.put(name, new Option(dialogue.getHead()));
	}

	public void option(String name, Runnable consumer) {
		options.put(name, new Option(new Dialogue(null, consumer)));
	}

	public void option(Supplier<Boolean> constraint, String name, Dialogue dialogue) {
		options.put(name, new Option(constraint, dialogue.getHead()));
	}

	public void option(String name) {
		option(name, () -> {});
	}

	public Map<String, Option> getOptions() {
		return options;
	}

	public String getStageName() {
		return stageName;
	}

	public Conversation getConv() {
		return conv;
	}
}
