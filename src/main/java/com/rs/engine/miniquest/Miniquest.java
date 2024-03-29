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

import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.util.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum Miniquest {
	ENTER_THE_ABYSS("Enter the Abyss", new Quest[]{Quest.RUNE_MYSTERIES}, null, null, null),
	KNIGHTS_WAVE_TRAINING_GROUNDS("Knights Waves Training Grounds", new Quest[]{Quest.KINGS_RANSOM}, null, null, null),
	TROLL_WARZONE("Troll Warzone Tutorial", null, null, null, null),
	WITCHES_POTION("Witch's Potion", null, null, null, null),
	HUNT_FOR_SUROK("The Hunt for Surok", new Quest[]{Quest.WHAT_LIES_BELOW}, null, Map.of(Skills.MINING, 42, Skills.PRAYER, 43), null),
	FROM_TINY_ACORNS("From Tiny Acorns", new Quest[] { Quest.BUYERS_AND_CELLARS }, null, Map.of(Skills.THIEVING, 24), null),
	LOST_HER_MARBLES("Lost Her Marbles", null, new Miniquest[] { Miniquest.FROM_TINY_ACORNS }, Map.of(Skills.THIEVING, 41), null),
	A_GUILD_OF_OUR_OWN("A Guild of Our Own", null, new Miniquest[] { Miniquest.LOST_HER_MARBLES }, Map.of(Skills.THIEVING, 62, Skills.HERBLORE, 46, Skills.AGILITY, 40), null),
	BAR_CRAWL("Alfred Grimhand's Barcrawl", null, null, null, null)
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
				handler.miniquest().handler = (MiniquestOutline) clazz.getConstructor().newInstance();
			}
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	private final String name;
	private final Quest[] questPreReqs;
	private final Miniquest[] miniquestPreReqs;
	private final Map<Integer, Integer> skillReqs;
	private MiniquestOutline handler;

	Miniquest(String name, Quest[] questPreReqs, Miniquest[] miniquestPreReqs, Map<Integer, Integer> skillReqs, Function<Player, Boolean> canStart) {
		this.name = name;
		this.questPreReqs = questPreReqs;
		this.miniquestPreReqs = miniquestPreReqs;
		this.skillReqs = skillReqs;
	}

	public boolean isImplemented() {
		return handler != null;
	}

	public MiniquestOutline getHandler() {
		return handler;
	}

	public boolean meetsReqs(Player player) {
		return meetsReqs(player, null, false);
	}

	public boolean meetsReqs(Player player, String actionStr,  boolean outputReqs) {
		boolean meetsRequirements = true;
		if (questPreReqs != null) {
			for (Quest preReq : questPreReqs) {
				if (!player.isQuestComplete(preReq, actionStr)) {
					if (outputReqs)
					player.sendMessage("You need to complete " + preReq.getDefs().name + " first.");
					meetsRequirements = false;
				}
			}
		}
		if (miniquestPreReqs != null) {
			for (Miniquest preReq : miniquestPreReqs) {
				if (!player.isMiniquestComplete(preReq, actionStr, outputReqs)) {
					if (outputReqs)
					player.sendMessage("You need to complete " + preReq.getName() + " first.");
					meetsRequirements = false;
				}
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
			if (outputReqs)
			player.sendMessage("You must meet the requirements for the miniquest: " + getName() + " " + actionStr);
		return meetsRequirements;
	}

	public String getName() {
		return name;
	}
}
