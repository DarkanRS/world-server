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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.util.Utils;

public enum Miniquest {
	ENTER_THE_ABYSS("Enter the Abyss", new Quest[] { Quest.RUNE_MYSTERIES }, null, null),
	KNIGHTS_WAVE_TRAINING_GROUNDS("Knights Waves Training Grounds", new Quest[] { Quest.KINGS_RANSOM }, null, null),
	;

	static {
		initializeHandlers();
	}

	public static void initializeHandlers() {
		try {
			List<Class<?>> classes = Utils.getClassesWithAnnotation("com.rs", MiniquestHandler.class);
			for (Class<?> clazz : classes) {
				MiniquestHandler handler = clazz.getAnnotation(MiniquestHandler.class);
				if (handler == null || clazz.getSuperclass() != MiniquestOutline.class)
					continue;
				handler.value().handler = (MiniquestOutline) clazz.getConstructor().newInstance();
			}
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	private String name;
	private Quest[] preReqs;
	private Map<Integer, Integer> skillReqs;
	@SuppressWarnings("unused")
	private Function<Player, Boolean> canStart;
	private MiniquestOutline handler;

	Miniquest(String name, Quest[] preReqs, Map<Integer, Integer> skillReqs, Function<Player, Boolean> canStart) {
		this.name = name;
		this.preReqs = preReqs;
		this.skillReqs = skillReqs;
		this.canStart = canStart;
	}

	public boolean isImplemented() {
		return handler != null;
	}

	public MiniquestOutline getHandler() {
		return handler;
	}

	public boolean meetsReqs(Player player) {
		return meetsReqs(player, null);
	}

	public boolean meetsReqs(Player player, String actionStr) {
		boolean meetsRequirements = true;
		for (Quest preReq : preReqs) {
			if (!player.isQuestComplete(preReq, actionStr)) {
				player.sendMessage("You need to complete " + preReq.getDefs().name + " first.");
				meetsRequirements = false;
			}
		}
		if (skillReqs != null) {
			for (int skillId : skillReqs.keySet()) {
				if (player.getSkills().getLevelForXp(skillId) < skillReqs.get(skillId)) {
					if (actionStr != null)
						player.sendMessage("You need a " + Skills.SKILL_NAME[skillId] + " level of " + skillReqs.get(skillId) + ".");
					meetsRequirements = false;
				}
			}
		}
		if (!meetsRequirements && actionStr != null)
			player.sendMessage("You must meet the requirements for the miniquest: " + getName() + " " + actionStr);
		return meetsRequirements;
	}

	public void sendQuestCompleteInterface(Player player, int itemId, String... lines) {
		String line = "";
		for (String l : lines)
			line += l + "<br>";

		//random quest jingle
		int jingleNum = Utils.random(0, 4);
		if(jingleNum == 3)
			jingleNum = 318;
		else
			jingleNum+=152;
		player.jingle(jingleNum);

		player.getInterfaceManager().sendInterface(1244);
		player.getPackets().setIFItem(1244, 24, itemId, 1);
		player.getPackets().setIFText(1244, 25, "You have completed "+getName()+"!");
		player.getPackets().setIFText(1244, 26, line);
	}

	public String getName() {
		return name;
	}
}