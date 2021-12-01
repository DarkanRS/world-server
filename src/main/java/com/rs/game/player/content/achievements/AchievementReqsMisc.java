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
package com.rs.game.player.content.achievements;

import com.rs.lib.game.VarManager;

public class AchievementReqsMisc {
	
	public static String script_3223(VarManager player, int achivementId, int reqIndex) {
//		int int2;
		String str0;
		str0 = "";
//		int2 = 0;
//		switch (achivementId) {
//			case 147:
//			case 23:
//			case 294:
//			case 167:
//			case 249:
//				if (reqIndex == 1) {
//					str0 = "You must have access to the fairy ring network to complete this Task.";
//					if (player.getVars().getVarBit(2328) == 1) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 49:
//				if (reqIndex == 1) {
//					str0 = "You must unlock 500 music tracks in order to perform the Air Guitar emote.";
//					if (player.getVars().getVarBit(4394) == 1) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 59:
//				if (reqIndex == 2) {
//					str0 = "You must also have completed the Abyss miniquest.";
//					if (player.getVars().getVar(492) >= 4) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 107:
//				if (reqIndex == 1) {
//					str0 = "You must have progressed to a certain point in the Dragon Slayer quest.";
//					if (player.getVars().getVar(176) >= 2 && player.getVars().getVarBit(3746) == 0 || player.getVars().getVar(176) >= 10) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 178:
//				if (reqIndex == 1) {
//					str0 = "You must begin the relevant section of Otto Godblessed's barbarian training.";
//					if (player.getVars().getVarBit(3757) > 0) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 180:
//				if (reqIndex == 1) {
//					str0 = "You must begin the relevant section of Otto Godblessed's barbarian training.";
//					if (player.getVars().getVarBit(3764) > 0) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 177:
//				if (reqIndex == 1) {
//					str0 = "You must begin the relevant section of Otto Godblessed's barbarian training.";
//					if (player.getVars().getVarBit(3764) > 0) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 316:
//				if (reqIndex == 1) {
//					str0 = "You must begin the relevant section of Otto Godblessed's barbarian training.";
//					if (player.getVars().getVarBit(3757) > 0) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 321:
//				if (reqIndex == 1) {
//					str0 = "You must begin the relevant section of Otto Godblessed's barbarian training.";
//					if (player.getVars().getVarBit(3764) > 0) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 322:
//				if (reqIndex == 1) {
//					str0 = "You must begin the relevant section of Otto Godblessed's barbarian training.";
//					if (player.getVars().getVarBit(3763) > 0) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 323:
//				if (reqIndex == 1) {
//					str0 = "You must begin the relevant section of Otto Godblessed's barbarian training.";
//					if (player.getVars().getVarBit(3761) > 0) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 175:
//				if (reqIndex == 1) {
//					str0 = "You must complete the Bar Crawl miniquest.";
//					if (player.getVars().getVar(76) >= 6 || player.getVars().getVarBit(3378) == 1) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 331:
//			case 219:
//				if (reqIndex == 2) {
//					str0 = "You must have a total combat level of at least 100 to accept an assignment in Shilo Village.";
//					if (script_1432() >= 100) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 248:
//				if (reqIndex == 1) {
//					str0 = "You must have completed the Knight Waves in Camelot.";
//					if (player.getVars().getVarBit(3909) == 8) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3011:
//			case 276:
//				if (reqIndex == 1) {
//					str0 = "You require 33 Quest Points to enter the Champions' Guild.";
//					if (player.getVars().getVar(101) >= 33) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 281:
//				if (reqIndex == 1) {
//					str0 = "You must unlock all four emotes by completing levels of the Stronghold of Security.";
//					if (player.getVars().getVarBit(2309) == 1 && player.getVars().getVarBit(2310) == 1 && player.getVars().getVarBit(2311) == 1 && player.getVars().getVarBit(2312) == 1) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 285:
//				if (reqIndex == 1) {
//					str0 = "You must learn the secret of the Senntisten necklace.";
//					if (player.getVars().getVarBit(3639) == 1) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 289:
//				if (reqIndex == 1) {
//					str0 = "You must have a total combat level of at least 40 to accept an assignment from Vannaka.";
//					if (script_1432() >= 40) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 300:
//				if (reqIndex == 1) {
//					str0 = "Completing quests will increase your access to Kudos with the Varrock Museum.";
//					if (script_4035() >= 153) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3000:
//				if (reqIndex == 2) {
//					if (getMinute() >= player.getVars().getVar(451)) {
//						int2 = 1;
//					}
//					str0 = "You may gather the Tears of Guthix once every week.";
//				} else if (reqIndex == 3) {
//					if (player.getVars().getVar(101) >= player.getVars().getVarBit(456) || script_4218() <= 0) {
//						int2 = 1;
//					}
//					str0 = "You must have gained a Quest Point or 100,000 total experience to enter Juna's cavern.";
//				}
//				break;
//			case 3013:
//			case 3001:
//				if (reqIndex == 1) {
//					str0 = "You must have a total combat level of at least 40 to fight for the Void Knights.";
//					if (getMyCombat() >= 40) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3002:
//				if (reqIndex == 1) {
//					str0 = "You must have Larry or Chuck explain the purpose of penguin spying.";
//					if (player.getVars().getVarBit(5277) == 1) {
//						int2 = 1;
//					}
//				} else if (reqIndex == 2) {
//					str0 = "You must have spied on fewer than ten penguins already this week.";
//					if (player.getVars().getVarBit(5276) < 10) {
//						int2 = 1;
//					}
//				} else if (reqIndex == 3) {
//					str0 = "You may spy on penguins if your total Penguin Points are less than the maximum of fifty.";
//					if (player.getVars().getVarBit(5275) < 50) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 12:
//				if (reqIndex == 1) {
//					str0 = "You must have Larry or Chuck explain the purpose of Penguin Hide and Seek.";
//					if (player.getVars().getVarBit(5277) == 1) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3003:
//				if (reqIndex == 1) {
//					str0 = "You may not chop down more than two evil trees per day.";
//					if (player.getVars().getVarBit(1545) < 2) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3007:
//				if (reqIndex == 1) {
//					str0 = "You may attempt the Agility, Magic and Ranged performances after a week has passed since your last show.";
//					if (player.getVars().getVarBit(5251) == 0 || player.getVars().getVarBit(5252) == 0 || player.getVars().getVarBit(5253) == 0) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3008:
//				if (reqIndex == 2) {
//					str0 = "You must wait at least a day since you last faced Bork.";
//					if (player.getVars().getVar(1199) != getCurrentDaysSinceLaunch()) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3010:
//				if (reqIndex == 2) {
//					str0 = "At least a week must pass since you last faced the Skeletal Horror.";
//					if (getMinute() > player.getVars().getVarBit(6305)) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3012:
//				if (reqIndex == 1) {
//					str0 = "You require 50 Runecrafting to enter the Runecrafters' Guild.";
//					if (getSkillActualLvl(20) >= 50) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3015:
//				if (reqIndex == 2) {
//					str0 = "You must have at least 65 Attack or Defence in order to take on a case.";
//					if (getSkillActualLvl(0) >= 65 || getSkillActualLvl(1) >= 65) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3031:
//				if (reqIndex == 1) {
//					str0 = "You must have a total combat level of at least 48 to fight in the Clan Wars.";
//					if (script_1432() >= 48) {
//						int2 = 1;
//					}
//				}
//				break;
//			case 3034:
//				if (reqIndex == 1) {
//					str0 = "To enter the Warriors' Guild your Attack or Strength level must be 99, or your combined Attack and Strength levels must total 130 or more.";
//					if (getSkillActualLvl(2) + getSkillActualLvl(0) >= 130 || getSkillActualLvl(0) >= 99 || getSkillActualLvl(2) >= 99) {
//						int2 = 1;
//					}
//				}
//				break;
//			default:
//				str0 = "";
//				int2 = 0;
//				break;
//		}
//		if (int2 == 1) {
//			str0 = concat("<str>", str0);
//		}
		return str0;
	}
}
