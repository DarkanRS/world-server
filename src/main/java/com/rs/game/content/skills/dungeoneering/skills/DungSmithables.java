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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

import java.util.ArrayList;
import java.util.HashMap;

public enum DungSmithables {
	NOVITE_BATTLEAXE(new Item(15753, 1), 5, 25.0, new Item[] { new Item(17650, 2) }),
	BATHUS_BATTLEAXE(new Item(15755, 1), 15, 43.0, new Item[] { new Item(17652, 2) }),
	MARMAROS_BATTLEAXE(new Item(15757, 1), 25, 61.0, new Item[] { new Item(17654, 2) }),
	KRATONITE_BATTLEAXE(new Item(15759, 1), 35, 79.0, new Item[] { new Item(17656, 2) }),
	FRACTITE_BATTLEAXE(new Item(15761, 1), 45, 97.0, new Item[] { new Item(17658, 2) }),
	ZEPHYRIUM_BATTLEAXE(new Item(15763, 1), 55, 115.0, new Item[] { new Item(17660, 2) }),
	ARGONITE_BATTLEAXE(new Item(15765, 1), 65, 133.0, new Item[] { new Item(17662, 2) }),
	KATAGON_BATTLEAXE(new Item(15767, 1), 75, 151.0, new Item[] { new Item(17664, 2) }),
	GORGONITE_BATTLEAXE(new Item(15769, 1), 85, 169.0, new Item[] { new Item(17666, 2) }),
	PROMETHIUM_BATTLEAXE(new Item(15771, 1), 95, 187.0, new Item[] { new Item(17668, 2) }),
	NOVITE_GAUNTLETS(new Item(16273, 1), 1, 10.0, new Item[] { new Item(17650, 1) }),
	BATHUS_GAUNTLETS(new Item(16275, 1), 11, 19.0, new Item[] { new Item(17652, 1) }),
	MARMAROS_GAUNTLETS(new Item(16277, 1), 21, 28.0, new Item[] { new Item(17654, 1) }),
	KRATONITE_GAUNTLETS(new Item(16279, 1), 31, 37.0, new Item[] { new Item(17656, 1) }),
	FRACTITE_GAUNTLETS(new Item(16281, 1), 41, 46.0, new Item[] { new Item(17658, 1) }),
	ZEPHYRIUM_GAUNTLETS(new Item(16283, 1), 51, 55.0, new Item[] { new Item(17660, 1) }),
	ARGONITE_GAUNTLETS(new Item(16285, 1), 61, 64.0, new Item[] { new Item(17662, 1) }),
	KATAGON_GAUNTLETS(new Item(16287, 1), 71, 73.0, new Item[] { new Item(17664, 1) }),
	GORGONITE_GAUNTLETS(new Item(16289, 1), 81, 82.0, new Item[] { new Item(17666, 1) }),
	PROMETHIUM_GAUNTLETS(new Item(16291, 1), 91, 91.0, new Item[] { new Item(17668, 1) }),
	NOVITE_PICKAXE(new Item(16295, 1), 2, 11.0, new Item[] { new Item(17650, 1) }),
	BATHUS_PICKAXE(new Item(16297, 1), 12, 20.0, new Item[] { new Item(17652, 1) }),
	MARMAROS_PICKAXE(new Item(16299, 1), 22, 29.0, new Item[] { new Item(17654, 1) }),
	KRATONITE_PICKAXE(new Item(16301, 1), 32, 38.0, new Item[] { new Item(17656, 1) }),
	FRACTITE_PICKAXE(new Item(16303, 1), 42, 47.0, new Item[] { new Item(17658, 1) }),
	ZEPHYRIUM_PICKAXE(new Item(16305, 1), 52, 56.0, new Item[] { new Item(17660, 1) }),
	ARGONITE_PICKAXE(new Item(16307, 1), 62, 65.0, new Item[] { new Item(17662, 1) }),
	KATAGON_PICKAXE(new Item(16309, 1), 72, 74.0, new Item[] { new Item(17664, 1) }),
	GORGONITE_PICKAXE(new Item(16311, 1), 82, 83.0, new Item[] { new Item(17666, 1) }),
	PROMETHIUM_PICKAXE(new Item(16313, 1), 92, 92.0, new Item[] { new Item(17668, 1) }),
	NOVITE_BOOTS(new Item(16339, 1), 1, 10.0, new Item[] { new Item(17650, 1) }),
	BATHUS_BOOTS(new Item(16341, 1), 11, 19.0, new Item[] { new Item(17652, 1) }),
	MARMAROS_BOOTS(new Item(16343, 1), 21, 28.0, new Item[] { new Item(17654, 1) }),
	KRATONITE_BOOTS(new Item(16345, 1), 31, 37.0, new Item[] { new Item(17656, 1) }),
	FRACTITE_BOOTS(new Item(16347, 1), 41, 46.0, new Item[] { new Item(17658, 1) }),
	ZEPHYRIUM_BOOTS(new Item(16349, 1), 51, 55.0, new Item[] { new Item(17660, 1) }),
	ARGONITE_BOOTS(new Item(16351, 1), 61, 64.0, new Item[] { new Item(17662, 1) }),
	KATAGON_BOOTS(new Item(16353, 1), 71, 73.0, new Item[] { new Item(17664, 1) }),
	GORGONITE_BOOTS(new Item(16355, 1), 81, 82.0, new Item[] { new Item(17666, 1) }),
	PROMETHIUM_BOOTS(new Item(16357, 1), 91, 91.0, new Item[] { new Item(17668, 1) }),
	NOVITE_HATCHET(new Item(16361, 1), 2, 11.0, new Item[] { new Item(17650, 1) }),
	BATHUS_HATCHET(new Item(16363, 1), 12, 20.0, new Item[] { new Item(17652, 1) }),
	MARMAROS_HATCHET(new Item(16365, 1), 22, 29.0, new Item[] { new Item(17654, 1) }),
	KRATONITE_HATCHET(new Item(16367, 1), 32, 38.0, new Item[] { new Item(17656, 1) }),
	FRACTITE_HATCHET(new Item(16369, 1), 42, 47.0, new Item[] { new Item(17658, 1) }),
	ZEPHYRIUM_HATCHET(new Item(16371, 1), 52, 56.0, new Item[] { new Item(17660, 1) }),
	ARGONITE_HATCHET(new Item(16373, 1), 62, 65.0, new Item[] { new Item(17662, 1) }),
	KATAGON_HATCHET(new Item(16375, 1), 72, 74.0, new Item[] { new Item(17664, 1) }),
	GORGONITE_HATCHET(new Item(16377, 1), 82, 83.0, new Item[] { new Item(17666, 1) }),
	PROMETHIUM_HATCHET(new Item(16379, 1), 92, 92.0, new Item[] { new Item(17668, 1) }),
	NOVITE_LONGSWORD(new Item(16383, 1), 4, 24.0, new Item[] { new Item(17650, 2) }),
	BATHUS_LONGSWORD(new Item(16385, 1), 14, 42.0, new Item[] { new Item(17652, 2) }),
	MARMAROS_LONGSWORD(new Item(16387, 1), 24, 60.0, new Item[] { new Item(17654, 2) }),
	KRATONITE_LONGSWORD(new Item(16389, 1), 34, 78.0, new Item[] { new Item(17656, 2) }),
	FRACTITE_LONGSWORD(new Item(16391, 1), 44, 96.0, new Item[] { new Item(17658, 2) }),
	ZEPHYRIUM_LONGSWORD(new Item(16393, 1), 54, 114.0, new Item[] { new Item(17660, 2) }),
	ARGONITE_LONGSWORD(new Item(16395, 1), 64, 132.0, new Item[] { new Item(17662, 2) }),
	KATAGON_LONGSWORD(new Item(16397, 1), 74, 150.0, new Item[] { new Item(17664, 2) }),
	GORGONITE_LONGSWORD(new Item(16399, 1), 84, 168.0, new Item[] { new Item(17666, 2) }),
	PROMETHIUM_LONGSWORD(new Item(16401, 1), 94, 186.0, new Item[] { new Item(17668, 2) }),
	NOVITE_MAUL(new Item(16405, 1), 8, 56.0, new Item[] { new Item(17650, 4) }),
	BATHUS_MAUL(new Item(16407, 1), 18, 92.0, new Item[] { new Item(17652, 4) }),
	MARMAROS_MAUL(new Item(16409, 1), 28, 128.0, new Item[] { new Item(17654, 4) }),
	KRATONITE_MAUL(new Item(16411, 1), 38, 164.0, new Item[] { new Item(17656, 4) }),
	FRACTITE_MAUL(new Item(16413, 1), 48, 200.0, new Item[] { new Item(17658, 4) }),
	ZEPHYRIUM_MAUL(new Item(16415, 1), 58, 236.0, new Item[] { new Item(17660, 4) }),
	ARGONITE_MAUL(new Item(16417, 1), 68, 272.0, new Item[] { new Item(17662, 4) }),
	KATAGON_MAUL(new Item(16419, 1), 78, 308.0, new Item[] { new Item(17664, 4) }),
	GORGONITE_MAUL(new Item(16421, 1), 88, 344.0, new Item[] { new Item(17666, 4) }),
	PROMETHIUM_MAUL(new Item(16423, 1), 98, 380.0, new Item[] { new Item(17668, 4) }),
	NOVITE_PLATESKIRT(new Item(16647, 1), 7, 40.0, new Item[] { new Item(17650, 3) }),
	BATHUS_PLATESKIRT(new Item(16649, 1), 17, 67.0, new Item[] { new Item(17652, 3) }),
	MARMAROS_PLATESKIRT(new Item(16651, 1), 27, 94.0, new Item[] { new Item(17654, 3) }),
	KRATONITE_PLATESKIRT(new Item(16653, 1), 37, 121.0, new Item[] { new Item(17656, 3) }),
	FRACTITE_PLATESKIRT(new Item(16655, 1), 47, 148.0, new Item[] { new Item(17658, 3) }),
	ZEPHYRIUM_PLATESKIRT(new Item(16657, 1), 57, 175.0, new Item[] { new Item(17660, 3) }),
	ARGONITE_PLATESKIRT(new Item(16659, 1), 67, 202.0, new Item[] { new Item(17662, 3) }),
	KATAGON_PLATESKIRT(new Item(16661, 1), 77, 229.0, new Item[] { new Item(17664, 3) }),
	GORGONITE_PLATESKIRT(new Item(16663, 1), 87, 256.0, new Item[] { new Item(17666, 3) }),
	PROMETHIUM_PLATESKIRT(new Item(16665, 1), 97, 283.0, new Item[] { new Item(17668, 3) }),
	NOVITE_PLATELEGS(new Item(16669, 1), 7, 40.0, new Item[] { new Item(17650, 3) }),
	BATHUS_PLATELEGS(new Item(16671, 1), 17, 67.0, new Item[] { new Item(17652, 3) }),
	MARMAROS_PLATELEGS(new Item(16673, 1), 27, 94.0, new Item[] { new Item(17654, 3) }),
	KRATONITE_PLATELEGS(new Item(16675, 1), 37, 121.0, new Item[] { new Item(17656, 3) }),
	FRACTITE_PLATELEGS(new Item(16677, 1), 47, 148.0, new Item[] { new Item(17658, 3) }),
	ZEPHYRIUM_PLATELEGS(new Item(16679, 1), 57, 175.0, new Item[] { new Item(17660, 3) }),
	ARGONITE_PLATELEGS(new Item(16681, 1), 67, 202.0, new Item[] { new Item(17662, 3) }),
	KATAGON_PLATELEGS(new Item(16683, 1), 77, 229.0, new Item[] { new Item(17664, 3) }),
	GORGONITE_PLATELEGS(new Item(16685, 1), 87, 256.0, new Item[] { new Item(17666, 3) }),
	PROMETHIUM_PLATELEGS(new Item(16687, 1), 97, 283.0, new Item[] { new Item(17668, 3) }),
	NOVITE_FULL_HELM(new Item(16691, 1), 5, 25.0, new Item[] { new Item(17650, 2) }),
	BATHUS_FULL_HELM(new Item(16693, 1), 15, 43.0, new Item[] { new Item(17652, 2) }),
	MARMAROS_FULL_HELM(new Item(16695, 1), 25, 61.0, new Item[] { new Item(17654, 2) }),
	KRATONITE_FULL_HELM(new Item(16697, 1), 35, 79.0, new Item[] { new Item(17656, 2) }),
	FRACTITE_FULL_HELM(new Item(16699, 1), 45, 97.0, new Item[] { new Item(17658, 2) }),
	ZEPHYRIUM_FULL_HELM(new Item(16701, 1), 55, 115.0, new Item[] { new Item(17660, 2) }),
	ARGONITE_FULL_HELM(new Item(16703, 1), 65, 133.0, new Item[] { new Item(17662, 2) }),
	KATAGON_FULL_HELM(new Item(16705, 1), 75, 151.0, new Item[] { new Item(17664, 2) }),
	GORGONITE_FULL_HELM(new Item(16707, 1), 85, 169.0, new Item[] { new Item(17666, 2) }),
	PROMETHIUM_FULL_HELM(new Item(16709, 1), 95, 187.0, new Item[] { new Item(17668, 2) }),
	NOVITE_CHAINBODY(new Item(16713, 1), 6, 39.0, new Item[] { new Item(17650, 3) }),
	BATHUS_CHAINBODY(new Item(16715, 1), 16, 66.0, new Item[] { new Item(17652, 3) }),
	MARMAROS_CHAINBODY(new Item(16717, 1), 26, 93.0, new Item[] { new Item(17654, 3) }),
	KRATONITE_CHAINBODY(new Item(16719, 1), 36, 120.0, new Item[] { new Item(17656, 3) }),
	FRACTITE_CHAINBODY(new Item(16721, 1), 46, 147.0, new Item[] { new Item(17658, 3) }),
	ZEPHYRIUM_CHAINBODY(new Item(16723, 1), 56, 174.0, new Item[] { new Item(17660, 3) }),
	ARGONITE_CHAINBODY(new Item(16725, 1), 66, 201.0, new Item[] { new Item(17662, 3) }),
	KATAGON_CHAINBODY(new Item(16727, 1), 76, 228.0, new Item[] { new Item(17664, 3) }),
	GORGONITE_CHAINBODY(new Item(16729, 1), 86, 255.0, new Item[] { new Item(17666, 3) }),
	PROMETHIUM_CHAINBODY(new Item(16731, 1), 96, 282.0, new Item[] { new Item(17668, 3) }),
	NOVITE_DAGGER(new Item(16757, 1), 1, 10.0, new Item[] { new Item(17650, 1) }),
	BATHUS_DAGGER(new Item(16765, 1), 10, 19.0, new Item[] { new Item(17652, 1) }),
	MARMAROS_DAGGER(new Item(16773, 1), 20, 28.0, new Item[] { new Item(17654, 1) }),
	KRATONITE_DAGGER(new Item(16781, 1), 30, 37.0, new Item[] { new Item(17656, 1) }),
	FRACTITE_DAGGER(new Item(16789, 1), 40, 46.0, new Item[] { new Item(17658, 1) }),
	ZEPHYRIUM_DAGGER(new Item(16797, 1), 50, 55.0, new Item[] { new Item(17660, 1) }),
	ARGONITE_DAGGER(new Item(16805, 1), 60, 64.0, new Item[] { new Item(17662, 1) }),
	KATAGON_DAGGER(new Item(16813, 1), 70, 73.0, new Item[] { new Item(17664, 1) }),
	GORGONITE_DAGGER(new Item(16821, 1), 80, 82.0, new Item[] { new Item(17666, 1) }),
	PROMETHIUM_DAGGER(new Item(16829, 1), 90, 91.0, new Item[] { new Item(17668, 1) }),
	NOVITE_2H_SWORD(new Item(16889, 1), 8, 56.0, new Item[] { new Item(17650, 4) }),
	BATHUS_2H_SWORD(new Item(16891, 1), 18, 92.0, new Item[] { new Item(17652, 4) }),
	MARMAROS_2H_SWORD(new Item(16893, 1), 28, 128.0, new Item[] { new Item(17654, 4) }),
	KRATONITE_2H_SWORD(new Item(16895, 1), 38, 164.0, new Item[] { new Item(17656, 4) }),
	FRACTITE_2H_SWORD(new Item(16897, 1), 48, 200.0, new Item[] { new Item(17658, 4) }),
	ZEPHYRIUM_2H_SWORD(new Item(16899, 1), 58, 236.0, new Item[] { new Item(17660, 4) }),
	ARGONITE_2H_SWORD(new Item(16901, 1), 68, 272.0, new Item[] { new Item(17662, 4) }),
	KATAGON_2H_SWORD(new Item(16903, 1), 78, 308.0, new Item[] { new Item(17664, 4) }),
	GORGONITE_2H_SWORD(new Item(16905, 1), 88, 344.0, new Item[] { new Item(17666, 4) }),
	PROMETHIUM_2H_SWORD(new Item(16907, 1), 98, 380.0, new Item[] { new Item(17668, 4) }),
	NOVITE_RAPIER(new Item(16935, 1), 3, 23.0, new Item[] { new Item(17650, 2) }),
	BATHUS_RAPIER(new Item(16937, 1), 13, 41.0, new Item[] { new Item(17652, 2) }),
	MARMAROS_RAPIER(new Item(16939, 1), 23, 59.0, new Item[] { new Item(17654, 2) }),
	KRATONITE_RAPIER(new Item(16941, 1), 33, 77.0, new Item[] { new Item(17656, 2) }),
	FRACTITE_RAPIER(new Item(16943, 1), 43, 95.0, new Item[] { new Item(17658, 2) }),
	ZEPHYRIUM_RAPIER(new Item(16945, 1), 53, 113.0, new Item[] { new Item(17660, 2) }),
	ARGONITE_RAPIER(new Item(16947, 1), 63, 131.0, new Item[] { new Item(17662, 2) }),
	KATAGON_RAPIER(new Item(16949, 1), 73, 149.0, new Item[] { new Item(17664, 2) }),
	GORGONITE_RAPIER(new Item(16951, 1), 83, 167.0, new Item[] { new Item(17666, 2) }),
	PROMETHIUM_RAPIER(new Item(16953, 1), 93, 185.0, new Item[] { new Item(17668, 2) }),
	NOVITE_WARHAMMER(new Item(17019, 1), 3, 23.0, new Item[] { new Item(17650, 2) }),
	BATHUS_WARHAMMER(new Item(17021, 1), 13, 41.0, new Item[] { new Item(17652, 2) }),
	MARMAROS_WARHAMMER(new Item(17023, 1), 23, 59.0, new Item[] { new Item(17654, 2) }),
	KRATONITE_WARHAMMER(new Item(17025, 1), 33, 77.0, new Item[] { new Item(17656, 2) }),
	FRACTITE_WARHAMMER(new Item(17027, 1), 43, 95.0, new Item[] { new Item(17658, 2) }),
	ZEPHYRIUM_WARHAMMER(new Item(17029, 1), 53, 113.0, new Item[] { new Item(17660, 2) }),
	ARGONITE_WARHAMMER(new Item(17031, 1), 63, 131.0, new Item[] { new Item(17662, 2) }),
	KATAGON_WARHAMMER(new Item(17033, 1), 73, 149.0, new Item[] { new Item(17664, 2) }),
	GORGONITE_WARHAMMER(new Item(17035, 1), 83, 167.0, new Item[] { new Item(17666, 2) }),
	PROMETHIUM_WARHAMMER(new Item(17037, 1), 93, 185.0, new Item[] { new Item(17668, 2) }),
	NOVITE_SPEAR(new Item(17063, 1), 7, 54.0, new Item[] { new Item(17650, 4) }),
	BATHUS_SPEAR(new Item(17071, 1), 17, 90.0, new Item[] { new Item(17652, 4) }),
	MARMAROS_SPEAR(new Item(17079, 1), 27, 126.0, new Item[] { new Item(17654, 4) }),
	KRATONITE_SPEAR(new Item(17087, 1), 37, 162.0, new Item[] { new Item(17656, 4) }),
	FRACTITE_SPEAR(new Item(17095, 1), 47, 198.0, new Item[] { new Item(17658, 4) }),
	ZEPHYRIUM_SPEAR(new Item(17103, 1), 57, 234.0, new Item[] { new Item(17660, 4) }),
	ARGONITE_SPEAR(new Item(17111, 1), 67, 270.0, new Item[] { new Item(17662, 4) }),
	KATAGON_SPEAR(new Item(17119, 1), 77, 306.0, new Item[] { new Item(17664, 4) }),
	GORGONITE_SPEAR(new Item(17127, 1), 87, 342.0, new Item[] { new Item(17666, 4) }),
	PROMETHIUM_SPEAR(new Item(17135, 1), 97, 378.0, new Item[] { new Item(17668, 4) }),
	NOVITE_PLATEBODY(new Item(17239, 1), 9, 72.0, new Item[] { new Item(17650, 5) }),
	BATHUS_PLATEBODY(new Item(17241, 1), 19, 117.0, new Item[] { new Item(17652, 5) }),
	MARMAROS_PLATEBODY(new Item(17243, 1), 29, 162.0, new Item[] { new Item(17654, 5) }),
	KRATONITE_PLATEBODY(new Item(17245, 1), 39, 207.0, new Item[] { new Item(17656, 5) }),
	FRACTITE_PLATEBODY(new Item(17247, 1), 49, 252.0, new Item[] { new Item(17658, 5) }),
	ZEPHYRIUM_PLATEBODY(new Item(17249, 1), 59, 297.0, new Item[] { new Item(17660, 5) }),
	ARGONITE_PLATEBODY(new Item(17251, 1), 69, 342.0, new Item[] { new Item(17662, 5) }),
	KATAGON_PLATEBODY(new Item(17253, 1), 79, 387.0, new Item[] { new Item(17664, 5) }),
	GORGONITE_PLATEBODY(new Item(17255, 1), 89, 432.0, new Item[] { new Item(17666, 5) }),
	PROMETHIUM_PLATEBODY(new Item(17257, 1), 99, 477.0, new Item[] { new Item(17668, 5) }),
	NOVITE_KITESHIELD(new Item(17341, 1), 6, 39.0, new Item[] { new Item(17650, 3) }),
	BATHUS_KITESHIELD(new Item(17343, 1), 16, 66.0, new Item[] { new Item(17652, 3) }),
	MARMAROS_KITESHIELD(new Item(17345, 1), 26, 93.0, new Item[] { new Item(17654, 3) }),
	KRATONITE_KITESHIELD(new Item(17347, 1), 36, 120.0, new Item[] { new Item(17656, 3) }),
	FRACTITE_KITESHIELD(new Item(17349, 1), 46, 147.0, new Item[] { new Item(17658, 3) }),
	ZEPHYRIUM_KITESHIELD(new Item(17351, 1), 56, 174.0, new Item[] { new Item(17660, 3) }),
	ARGONITE_KITESHIELD(new Item(17353, 1), 66, 201.0, new Item[] { new Item(17662, 3) }),
	KATAGON_KITESHIELD(new Item(17355, 1), 76, 228.0, new Item[] { new Item(17664, 3) }),
	GORGONITE_KITESHIELD(new Item(17357, 1), 86, 255.0, new Item[] { new Item(17666, 3) }),
	PROMETHIUM_KITESHIELD(new Item(17359, 1), 96, 282.0, new Item[] { new Item(17668, 3) }),
	NOVITE_ARROWHEADS(new Item(17885, 20), 1, 10.0, new Item[] { new Item(17650, 1) }),
	BATHUS_ARROWHEADS(new Item(17890, 20), 10, 19.0, new Item[] { new Item(17652, 1) }),
	MARMAROS_ARROWHEADS(new Item(17895, 20), 20, 28.0, new Item[] { new Item(17654, 1) }),
	KRATONITE_ARROWHEADS(new Item(17900, 20), 30, 37.0, new Item[] { new Item(17656, 1) }),
	FRACTITE_ARROWHEADS(new Item(17905, 20), 40, 46.0, new Item[] { new Item(17658, 1) }),
	ZEPHYRIUM_ARROWHEADS(new Item(17910, 20), 50, 55.0, new Item[] { new Item(17660, 1) }),
	ARGONITE_ARROWHEADS(new Item(17915, 20), 60, 64.0, new Item[] { new Item(17662, 1) }),
	KATAGON_ARROWHEADS(new Item(17920, 20), 70, 73.0, new Item[] { new Item(17664, 1) }),
	GORGONITE_ARROWHEADS(new Item(17925, 20), 80, 82.0, new Item[] { new Item(17666, 1) }),
	PROMETHIUM_ARROWHEADS(new Item(17930, 20), 90, 91.0, new Item[] { new Item(17668, 1) });

	public final Item product;
	public final int req;
	public final double xp;
	public final Item[] materials;

	public static final HashMap<Integer, DungSmithables[]> SMITHABLES = new HashMap<>();

	static {
		for (int i = 17650;i <= 17668;i += 2)
			SMITHABLES.put(i, DungSmithables.genSortedArray(i));
	}

	DungSmithables(Item product, int req, double xp, Item[] materials) {
		this.product = product;
		this.req = req;
		this.xp = xp;
		this.materials = materials;
	}

	public static DungSmithables[] forBar(int barId) {
		return SMITHABLES.get(barId);
	}

	private static DungSmithables[] genSortedArray(int barId) {
		ArrayList<DungSmithables> prods = new ArrayList<>();
		for (DungSmithables d : DungSmithables.values())
			if (d.materials[0].getId() == barId)
				prods.add(d);
		// TODO: IntelliJ says to replace 247 with prods.sort(Comparator.comparingInt(ds -> ds.req));
		prods.sort((ds1, ds2) -> ds1.req < ds2.req ? -1 : (ds1.req > ds2.req) ? 1 : 0);
		DungSmithables[] arr = new DungSmithables[prods.size()];
		for (int i = 0;i < arr.length;i++)
			arr[i] = prods.get(i);
		return arr;
	}

	public boolean canMake(Player player) {
		return player.getInventory().containsItems(materials) && player.getSkills().getLevel(Constants.SMITHING) >= req;
	}
}
