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
package com.rs.tools.old;

import com.rs.cache.loaders.NPCDefinitions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

public class RuneWikiNPCDefinitionsDumper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Cache.init();
		for (int i = 6793; i < 6891; i++)
			if (!dumpNPC(i))
				System.out.println("Failed dumping npc: " + i + ", " + NPCDefinitions.getDefs(i).getName());
	}

	private static final String COMBAT_LEVEL_LINE = "<th nowrap=\"nowrap\"><a href=\"/wiki/Combat_level\" title=\"Combat level\">Combat level</a>";
	private static final String HP_LINE = "<th> <a href=\"/wiki/Constitution\" title=\"Constitution\">Life points</a>";
	private static final String AGGRESSIVE_LINE = "<th> <a href=\"/wiki/Aggressiveness\" title=\"Aggressiveness\">Aggressive</a>?";
	private static final String COMBAT_STYLE_LINE = "<th nowrap=\"nowrap\"><a href=\"/wiki/Combat_style\" title=\"Combat style\" class=\"mw-redirect\">Attack style</a>";
	private static final String MAX_HIT_LINE = "<th nowrap=\"nowrap\"><a href=\"/wiki/Monster_maximum_hit\" title=\"Monster maximum hit\">Max hit</a>";
	private static final String ATTACK_SPEED_LINE = "<th nowrap=\"nowrap\"><a href=\"/wiki/Attack_speed\" title=\"Attack speed\">Attack speed</a>";

	public static boolean dumpNPC(int npcId) {
		NPCDefinitions defs = NPCDefinitions.getDefs(npcId);
		if (!defs.hasAttackOption())
			return true;
		String pageName = defs.getName();
		if (pageName == null || pageName.equals("null"))
			return true;
		pageName = pageName.replaceAll(" ", "_");
		try {
			WebPage page = new WebPage("http://runescape.wikia.com/wiki/" + pageName);
			try {
				page.load();
			} catch (SocketTimeoutException e) {
				return dumpNPC(npcId);
			} catch (Exception e) {
				System.out.println("Invalid page: " + npcId + ", " + pageName);
				return false;
			}
			int hitpoints = -1;
			boolean aggressive = false;
			int attackStyle = -1;
			int maxhit = -1;
			int attackSpeed = -1;
			for (int linesCount = 0; linesCount < page.getLines().size(); linesCount++) {
				String line = page.getLines().get(linesCount);
				if (line.equalsIgnoreCase(COMBAT_LEVEL_LINE)) {
					linesCount++;
					String combatLevelsLine = getFormatedString(page.getLines().get(linesCount));
					String[] levels = combatLevelsLine.split(",");
					linesCount += 3;
					String hpCheckLine = page.getLines().get(linesCount);
					if (!hpCheckLine.equalsIgnoreCase(HP_LINE))
						throw new RuntimeException("invalid hp line.");
					linesCount++;
					String lifePointsLine = getFormatedString(page.getLines().get(linesCount));
					String[] lifePoints = lifePointsLine.split(",");
					for (int i = 0; i < levels.length; i++) {
						int level = Integer.valueOf(levels[i]);
						if (level == defs.combatLevel) {
							hitpoints = Integer.valueOf(lifePoints[i >= lifePoints.length ? lifePoints.length - 1 : i]);
							break;
						}
					}
					if (hitpoints < 0)
						return false;
				} else if (line.equalsIgnoreCase(AGGRESSIVE_LINE)) {
					linesCount++;
					String isAggresive = getFormatedString(page.getLines().get(linesCount));
					aggressive = !isAggresive.equalsIgnoreCase("No");
				} else if (line.equalsIgnoreCase(COMBAT_STYLE_LINE)) {
					// </th><td> <a href="/wiki/Melee" title="Melee">Melee</a>
					linesCount++;
					String attackStyleLine = page.getLines().get(linesCount).toLowerCase();
					if (attackStyleLine.contains("melee"))
						attackStyle = 0;
					else if (attackStyleLine.contains("ranged"))
						attackStyle = 1;
					else if (attackStyleLine.contains("magic"))
						attackStyle = 2;
				} else if (line.equalsIgnoreCase(MAX_HIT_LINE)) {
					linesCount++;
					String maxhitLine = page.getLines().get(linesCount);
					maxhitLine = maxhitLine.replace("</th><td> ", "");
					maxhitLine = maxhitLine.replace("</th><td>", "");
					maxhitLine = maxhitLine.replace("/", " ");
					maxhitLine = maxhitLine.split(" ")[0];
					if (maxhitLine.equals("")) {
						linesCount++;
						maxhitLine = page.getLines().get(linesCount);
						maxhitLine = maxhitLine.replace("<ul><li>", "");
						maxhitLine = maxhitLine.replace("/", " ");
						maxhitLine = maxhitLine.split(" ")[0];
					}
					maxhitLine = maxhitLine.replace(",", "");
					maxhitLine = maxhitLine.replace("<a", "0");
					maxhitLine = maxhitLine.replace("Varies", "0");
					maxhit = Integer.valueOf(maxhitLine);
				} else if (line.equalsIgnoreCase(ATTACK_SPEED_LINE)) {
					linesCount++;
					String attackSpeedLine = page.getLines().get(linesCount);
					if (attackSpeedLine.contains("Unknown"))
						attackSpeed = 4;
					else {
						String speed = attackSpeedLine.substring(attackSpeedLine.indexOf("Speed") + 5, attackSpeedLine.indexOf(".gif"));
						attackSpeed = Integer.valueOf(speed) + 1;
					}
				}
			}
			if (maxhit == -1 || hitpoints == -1 || attackStyle == -1 || attackSpeed == -1)
				return false;
			System.out.println("id: " + npcId + ", " + defs.getName());
			System.out.println("maxhit: " + maxhit);
			System.out.println("hp: " + hitpoints);
			System.out.println("aggressive: " + aggressive);
			System.out.println("attackStyle: " + attackStyle);
			System.out.println("attackSpeed: " + attackSpeed);
			// //npcId - hitpoints attackAnim defenceAnim deathAnim attackDelay
			// deathDelay respawnDelay maxHit attackStyle attackGfx
			// attackProjectile agressivenessType
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("testsrer.txt", true));
				String t = attackStyle == 0 ? "MELEE" : attackStyle == 1 ? "RANGE" : "MAGE";
				String a = aggressive ? "AGRESSIVE" : "PASSIVE";
				writer.write("//" + defs.getName() + " - " + defs.combatLevel);
				writer.newLine();
				writer.write(npcId + " - " + hitpoints + " -1 -1 -1 " + attackSpeed + " 1 60 " + maxhit + " " + t + " -1 -1 " + a);
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getFormatedString(String s) {
		return s.replace("</th><td>", "").replace("to", ",").replace("-", ",").replace("/", ",").replace("Varies", "0").replaceAll(" ", "").replace("<br", "").replace("<br/>(0in<ahref=\"/wiki/Daemonheim\"title=\"Daemonheim\">Daemonheim</a>)", "")
				.replace("(13in<i><ahref=\"/wiki/The_Restless_Ghost\"title=\"TheRestlessGhost\">TheRestlessGhost</a></i>)", "13,").replace("(variesinDaemonheim:...", "");

	}
}
