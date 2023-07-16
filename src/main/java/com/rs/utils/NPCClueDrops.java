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
package com.rs.utils;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.utils.drop.ClueDrop;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCClueDrops {

	private static Map<String, ClueDrop> EASY = new HashMap<>();
	private static Map<String, ClueDrop> MEDIUM = new HashMap<>();
	private static Map<String, ClueDrop> HARD = new HashMap<>();
	private static Map<String, ClueDrop> ELITE = new HashMap<>();

	static {
		EASY.put("banshee", new ClueDrop(128));
		EASY.put("barbarian", new ClueDrop(128));
		EASY.put("borrokar", new ClueDrop(128));
		EASY.put("cave slime", new ClueDrop(128));
		EASY.put("dagannoth spawn", new ClueDrop(128));
		EASY.put("farmer", new ClueDrop(128));
		EASY.put("freidir", new ClueDrop(128));
		EASY.put("freygerd", new ClueDrop(128));
		EASY.put("giant cave bug", new ClueDrop(128));
		EASY.put("goblin", new ClueDrop(128));
		EASY.put("icefiend", new ClueDrop(128));
		EASY.put("inga", new ClueDrop(128));
		EASY.put("jennella", new ClueDrop(128));
		EASY.put("lanzig", new ClueDrop(128));
		EASY.put("lensa", new ClueDrop(128));
		EASY.put("molanisk", new ClueDrop(128));
		EASY.put("mugger", new ClueDrop(128));
		EASY.put("ork", new ClueDrop(128));
		EASY.put("pontak", new ClueDrop(128));
		EASY.put("rock crab", new ClueDrop(128));
		EASY.put("sassilik", new ClueDrop(128));
		EASY.put("thug", new ClueDrop(128));
		EASY.put("werewolf", new ClueDrop(128));
		EASY.put("agnar", new ClueDrop(128));
		EASY.put("h.a.m. guard", new ClueDrop(55));
		EASY.put("icefiend", new ClueDrop(128));
		EASY.put("man", new ClueDrop(128));
		EASY.put("minotaur", new ClueDrop(101));
		EASY.put("skeleton", new ClueDrop(128, 21, 22, 25));
		EASY.put("woman", new ClueDrop(128));

		MEDIUM.put("abyssal guardian", new ClueDrop(256));
		MEDIUM.put("abyssal leech", new ClueDrop(256));
		MEDIUM.put("abyssal walker", new ClueDrop(256));
		MEDIUM.put("barbarian skeleton", new ClueDrop(128));
		MEDIUM.put("barbarian spirit", new ClueDrop(128));
		MEDIUM.put("black guard", new ClueDrop(128));
		MEDIUM.put("black guard berserker", new ClueDrop(128));
		MEDIUM.put("black heather", new ClueDrop(128));
		MEDIUM.put("brine rat", new ClueDrop(128));
		MEDIUM.put("catablepon", new ClueDrop(101));
		MEDIUM.put("cockatrice", new ClueDrop(128));
		MEDIUM.put("dagannoth", new ClueDrop(128));
		MEDIUM.put("donny the lad", new ClueDrop(128));
		MEDIUM.put("giant sea snake", new ClueDrop(128));
		MEDIUM.put("giant rock crab", new ClueDrop(128));
		MEDIUM.put("giant skeleton", new ClueDrop(128, 100));
		MEDIUM.put("guard", new ClueDrop(128));
		MEDIUM.put("harpie swarm", new ClueDrop(128));
		MEDIUM.put("ice warrior", new ClueDrop(128));
		MEDIUM.put("jogre", new ClueDrop(129));
		MEDIUM.put("jungle horror", new ClueDrop(128));
		MEDIUM.put("magic axe", new ClueDrop(256));
		MEDIUM.put("magic pickaxe", new ClueDrop(256));
		MEDIUM.put("paladin", new ClueDrop(128));
		MEDIUM.put("pyrefiend", new ClueDrop(128));
		MEDIUM.put("rock lobster", new ClueDrop(128));
		MEDIUM.put("sea snake hatchling", new ClueDrop(128));
		MEDIUM.put("sea snake young", new ClueDrop(128));
		MEDIUM.put("skeleton", new ClueDrop(128, 87, 72, 13, 45, 77));
		MEDIUM.put("skeleton mage", new ClueDrop(128, 94));
		MEDIUM.put("skeleton miner", new ClueDrop(128));
		MEDIUM.put("speedy keith", new ClueDrop(128));
		MEDIUM.put("tribesman", new ClueDrop(138));
		MEDIUM.put("vampyre", new ClueDrop(128));
		MEDIUM.put("wallasalki", new ClueDrop(128));
		MEDIUM.put("werewolf", new ClueDrop(512));
		MEDIUM.put("mummy", new ClueDrop(513));

		HARD.put("aberrant spectre", new ClueDrop(128));
		HARD.put("abyssal demon", new ClueDrop(128));
		HARD.put("ankou", new ClueDrop(512));
		HARD.put("balfrug kreeyath", new ClueDrop(128));
		HARD.put("black demon", new ClueDrop(128));
		HARD.put("black dragon", new ClueDrop(128));
		HARD.put("bloodveld", new ClueDrop(256));
		HARD.put("blue dragon", new ClueDrop(128));
		HARD.put("bree", new ClueDrop(128));
		HARD.put("bronze dragon", new ClueDrop(128));
		HARD.put("brutal green dragon", new ClueDrop(128));
		HARD.put("cave horror", new ClueDrop(128));
		HARD.put("cosmic being", new ClueDrop(128));
		HARD.put("dagannoth rex", new ClueDrop(42));
		HARD.put("dagannoth supreme", new ClueDrop(42));
		HARD.put("dagannoth prime", new ClueDrop(42));
		HARD.put("dark beast", new ClueDrop(128));
		HARD.put("elf warrior", new ClueDrop(128));
		HARD.put("flight kilisa", new ClueDrop(128));
		HARD.put("flockleader geerin", new ClueDrop(128));
		HARD.put("gargoyle", new ClueDrop(128));
		HARD.put("gorak", new ClueDrop(128));
		HARD.put("greater demon", new ClueDrop(128));
		HARD.put("green dragon", new ClueDrop(128));
		HARD.put("growler", new ClueDrop(128));
		HARD.put("hellhound", new ClueDrop(64));
		HARD.put("iron dragon", new ClueDrop(128));
		HARD.put("jelly", new ClueDrop(128));
		HARD.put("kurask", new ClueDrop(128));
		HARD.put("nechryael", new ClueDrop(128));
		HARD.put("red dragon", new ClueDrop(128));
		HARD.put("saradomin knight", new ClueDrop(128));
		HARD.put("saradomin priest", new ClueDrop(128));
		HARD.put("sergeant grimspike", new ClueDrop(128));
		HARD.put("sergeant steelwill", new ClueDrop(128));
		HARD.put("sergeant strongstack", new ClueDrop(128));
		HARD.put("spiritual mage", new ClueDrop(128));
		HARD.put("spiritual ranger", new ClueDrop(128));
		HARD.put("spiritual warrior", new ClueDrop(128));
		HARD.put("starlight", new ClueDrop(128));
		HARD.put("steel dragon", new ClueDrop(64));
		HARD.put("suqah", new ClueDrop(129));
		HARD.put("terror dog", new ClueDrop(128));
		HARD.put("tstanon karlak", new ClueDrop(128));
		HARD.put("vyrewatch", new ClueDrop(128));
		HARD.put("waterfiend", new ClueDrop(128));
		HARD.put("wingman skree", new ClueDrop(128));
		HARD.put("zakl'n gritch", new ClueDrop(128));
		HARD.put("bandit", new ClueDrop(128));
		HARD.put("cyclops", new ClueDrop(512));
		HARD.put("dark warrior", new ClueDrop(128));
		HARD.put("ork", new ClueDrop(128));
		HARD.put("turoth", new ClueDrop(128));
		HARD.put("jungle strykewyrm", new ClueDrop(128));
		HARD.put("desert strykewyrm", new ClueDrop(128));
		HARD.put("ice strykewyrm", new ClueDrop(128));
		HARD.put("glacor", new ClueDrop(128));
		HARD.put("mithril dragon", new ClueDrop(128));
		HARD.put("scabaras ranger", new ClueDrop(128));
		HARD.put("scabaras lancer", new ClueDrop(128));
		HARD.put("locust lancer", new ClueDrop(128));
		HARD.put("locust ranger", new ClueDrop(128));

		ELITE.put("abyssal demon", new ClueDrop(1200));
		ELITE.put("black dragon", new ClueDrop(500));
		ELITE.put("nex", new ClueDrop(128));
		ELITE.put("jungle strykewyrm", new ClueDrop(500));
		ELITE.put("desert strykewyrm", new ClueDrop(500));
		ELITE.put("ice strykewyrm", new ClueDrop(500));
		ELITE.put("glacor", new ClueDrop(500));
		ELITE.put("bronze dragon", new ClueDrop(500));
		ELITE.put("chaos elemental", new ClueDrop(200));
		ELITE.put("commander zilyana", new ClueDrop(250));
		ELITE.put("corporeal beast", new ClueDrop(200));
		ELITE.put("dagannoth prime", new ClueDrop(750));
		ELITE.put("dagannoth rex", new ClueDrop(750));
		ELITE.put("dagannoth supreme", new ClueDrop(750));
		ELITE.put("dark beast", new ClueDrop(1200));
		ELITE.put("general graardor", new ClueDrop(250));
		ELITE.put("giant mole", new ClueDrop(500));
		ELITE.put("k'ril tsutsaroth", new ClueDrop(250));
		ELITE.put("kalphite queen", new ClueDrop(100));
		ELITE.put("king black dragon", new ClueDrop(450));
		ELITE.put("kree'arra", new ClueDrop(250));
		ELITE.put("salarin the twisted", new ClueDrop(500));
		ELITE.put("skeletal wyvern", new ClueDrop(350));
		ELITE.put("steel dragon", new ClueDrop(500));
		ELITE.put("tormented demon", new ClueDrop(1500));
		ELITE.put("ganodermic beast", new ClueDrop(1200));
		ELITE.put("mithril dragon", new ClueDrop(350));
	}

	public static DropSet rollClues(int npcId) {
		List<DropTable> tables = new ArrayList<>();
		NPCDefinitions defs = NPCDefinitions.getDefs(npcId);
		String name = defs.getName().toLowerCase();
		ClueDrop drop;
		drop = EASY.get(name);
		if (drop != null && drop.validCombatLevel(defs.combatLevel))
			tables.add(new DropTable(1.0, drop.getWeight(), new Drop(TreasureTrailsManager.SCROLL_BOXES[0])));
		drop = MEDIUM.get(name);
		if (drop != null && drop.validCombatLevel(defs.combatLevel))
			tables.add(new DropTable(1.0, drop.getWeight(), new Drop(TreasureTrailsManager.SCROLL_BOXES[1])));
		drop = HARD.get(name);
		if (drop != null && drop.validCombatLevel(defs.combatLevel))
			tables.add(new DropTable(1.0, drop.getWeight(), new Drop(TreasureTrailsManager.SCROLL_BOXES[2])));
		drop = ELITE.get(name);
		if (drop != null && drop.validCombatLevel(defs.combatLevel))
			tables.add(new DropTable(1.0, drop.getWeight(), new Drop(TreasureTrailsManager.SCROLL_BOXES[3])));
		return new DropSet(tables);
	}
}
