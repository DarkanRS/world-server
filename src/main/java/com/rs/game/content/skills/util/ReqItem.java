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
package com.rs.game.content.skills.util;

import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum ReqItem {
	CANNON_COG(Category.ARTISANS, new Item(20475, 1), Constants.SMITHING, 62, 38.0, 20476, new Item[] { new Item(20638, 1) }),
	BROKEN_CANNON_BARREL2(Category.ARTISANS, new Item(20478, 1), Constants.SMITHING, 62, 7.0, -1, new Item[] { new Item(20477, 1) }),
	BROKEN_CANNON_BARREL3(Category.ARTISANS, new Item(20479, 1), Constants.SMITHING, 62, 7.0, -1, new Item[] { new Item(20478, 1) }),
	CANNON_BARREL(Category.ARTISANS, new Item(20480, 1), Constants.SMITHING, 62, 7.0, -1, new Item[] { new Item(20479, 1) }),
	PIPE(Category.ARTISANS, new Item(20482, 1), Constants.SMITHING, 62, 3.0, 20483, new Item[] { new Item(20481, 1) }),
	EMPTY_FUSE_BOX(Category.ARTISANS, new Item(20487, 1), Constants.SMITHING, 62, 15.0, -1, new Item[] { new Item(20488, 1) }),
	FLINT(Category.ARTISANS, new Item(20491, 1), Constants.SMITHING, 62, 15.0, -1, new Item[] { new Item(20490, 1) }),
	CANNONBALL2(Category.ARTISANS, new Item(20492, 4), Constants.SMITHING, 62, 38.0, 20493, new Item[] { new Item(20638, 1) }),
	BRONZE_RAILS(Category.ARTISANS, new Item(20506, 1), Constants.SMITHING, 1, 1.0, -1, new Item[] { new Item(20502, 1) }),
	BRONZE_BASE_PLATE(Category.ARTISANS, new Item(20507, 1), Constants.SMITHING, 2, 1.0, -1, new Item[] { new Item(20502, 1) }),
	BRONZE_SPIKES(Category.ARTISANS, new Item(20508, 1), Constants.SMITHING, 5, 1.0, -1, new Item[] { new Item(20502, 1) }),
	BRONZE_JOINT(Category.ARTISANS, new Item(20509, 1), Constants.SMITHING, 8, 1.0, -1, new Item[] { new Item(20502, 1) }),
	BRONZE_TIE(Category.ARTISANS, new Item(20510, 1), Constants.SMITHING, 11, 1.0, -1, new Item[] { new Item(20502, 1) }),
	BRONZE_TRACK_40(Category.ARTISANS, new Item(20511, 1), Constants.SMITHING, 3, 6.0, -1, new Item[] { new Item(20506, 1), new Item(20507, 1) }),
	BRONZE_TRACK_60(Category.ARTISANS, new Item(20512, 1), Constants.SMITHING, 6, 7.0, -1, new Item[] { new Item(20508, 1), new Item(20511, 1) }),
	BRONZE_TRACK_80(Category.ARTISANS, new Item(20513, 1), Constants.SMITHING, 9, 9.0, -1, new Item[] { new Item(20509, 1), new Item(20512, 1) }),
	BRONZE_TRACK_100(Category.ARTISANS, new Item(20514, 1), Constants.SMITHING, 12, 10.0, -1, new Item[] { new Item(20510, 1), new Item(20513, 1) }),
	IRON_RAILS(Category.ARTISANS, new Item(20515, 1), Constants.SMITHING, 15, 5.0, -1, new Item[] { new Item(20503, 1) }),
	IRON_BASE_PLATE(Category.ARTISANS, new Item(20516, 1), Constants.SMITHING, 19, 5.0, -1, new Item[] { new Item(20503, 1) }),
	IRON_SPIKES(Category.ARTISANS, new Item(20517, 1), Constants.SMITHING, 24, 5.0, -1, new Item[] { new Item(20503, 1) }),
	IRON_JOINT(Category.ARTISANS, new Item(20518, 1), Constants.SMITHING, 29, 5.0, -1, new Item[] { new Item(20503, 1) }),
	IRON_TIE(Category.ARTISANS, new Item(20519, 1), Constants.SMITHING, 34, 5.0, -1, new Item[] { new Item(20503, 1) }),
	STEEL_RAILS(Category.ARTISANS, new Item(20520, 1), Constants.SMITHING, 39, 8.0, -1, new Item[] { new Item(20504, 1) }),
	STEEL_BASE_PLATE(Category.ARTISANS, new Item(20521, 1), Constants.SMITHING, 44, 8.0, -1, new Item[] { new Item(20504, 1) }),
	STEEL_SPIKES(Category.ARTISANS, new Item(20522, 1), Constants.SMITHING, 49, 8.0, -1, new Item[] { new Item(20504, 1) }),
	STEEL_JOINT(Category.ARTISANS, new Item(20523, 1), Constants.SMITHING, 54, 8.0, -1, new Item[] { new Item(20504, 1) }),
	STEEL_TIE(Category.ARTISANS, new Item(20524, 1), Constants.SMITHING, 59, 8.0, -1, new Item[] { new Item(20504, 1) }),
	IRON_TRACK_40(Category.ARTISANS, new Item(20525, 1), Constants.SMITHING, 20, 10.0, -1, new Item[] { new Item(20515, 1), new Item(20516, 1) }),
	IRON_TRACK_60(Category.ARTISANS, new Item(20526, 1), Constants.SMITHING, 25, 11.0, -1, new Item[] { new Item(20517, 1), new Item(20525, 1) }),
	IRON_TRACK_80(Category.ARTISANS, new Item(20527, 1), Constants.SMITHING, 30, 12.0, -1, new Item[] { new Item(20518, 1), new Item(20526, 1) }),
	IRON_TRACK_100(Category.ARTISANS, new Item(20528, 1), Constants.SMITHING, 35, 13.0, -1, new Item[] { new Item(20519, 1), new Item(20527, 1) }),
	STEEL_TRACK_40(Category.ARTISANS, new Item(20529, 1), Constants.SMITHING, 45, 13.0, -1, new Item[] { new Item(20520, 1), new Item(20521, 1) }),
	STEEL_TRACK_60(Category.ARTISANS, new Item(20530, 1), Constants.SMITHING, 50, 16.0, -1, new Item[] { new Item(20522, 1), new Item(20529, 1) }),
	STEEL_TRACK_80(Category.ARTISANS, new Item(20531, 1), Constants.SMITHING, 55, 22.0, -1, new Item[] { new Item(20523, 1), new Item(20530, 1) }),
	STEEL_TRACK_100(Category.ARTISANS, new Item(20532, 1), Constants.SMITHING, 60, 25.0, -1, new Item[] { new Item(20524, 1), new Item(20531, 1) }),
	IRON_INGOT_HEATED(Category.ARTISANS, new Item(20567, 1), Skills.NONE, 70, 0.0, 20565, new Item[] { new Item(20648, 1) }),
	STEEL_INGOT_HEATED(Category.ARTISANS, new Item(20568, 1), Skills.NONE, 75, 0.0, 20565, new Item[] { new Item(20649, 1) }),
	MITHRIL_INGOT_HEATED(Category.ARTISANS, new Item(20569, 1), Skills.NONE, 80, 0.0, 20565, new Item[] { new Item(20650, 1) }),
	ADAMANT_INGOT_HEATED(Category.ARTISANS, new Item(20570, 1), Skills.NONE, 85, 0.0, 20565, new Item[] { new Item(20651, 1) }),
	RUNE_INGOT_HEATED(Category.ARTISANS, new Item(20571, 1), Skills.NONE, 90, 0.0, 20565, new Item[] { new Item(20652, 1) }),
	MINER_HELM_IRON(Category.ARTISANS, new Item(20572, 1), Constants.SMITHING, 30, 101.0, -1, new Item[] { new Item(20632, 1) }),
	MINER_HELM_STEEL(Category.ARTISANS, new Item(20573, 1), Constants.SMITHING, 45, 131.0, -1, new Item[] { new Item(20633, 1) }),
	MINER_HELM_MITHRIL(Category.ARTISANS, new Item(20574, 1), Constants.SMITHING, 60, 136.0, -1, new Item[] { new Item(20634, 1) }),
	MINER_HELM_ADAMANT(Category.ARTISANS, new Item(20575, 1), Constants.SMITHING, 70, 278.0, -1, new Item[] { new Item(20635, 1) }),
	MINER_HELM_RUNE(Category.ARTISANS, new Item(20576, 1), Constants.SMITHING, 90, 505.0, -1, new Item[] { new Item(20636, 1) }),
	MINER_BOOTS_IRON(Category.ARTISANS, new Item(20577, 1), Constants.SMITHING, 30, 101.0, -1, new Item[] { new Item(20632, 1) }),
	MINER_BOOTS_STEEL(Category.ARTISANS, new Item(20578, 1), Constants.SMITHING, 45, 131.0, -1, new Item[] { new Item(20633, 1) }),
	MINER_BOOTS_MITHRIL(Category.ARTISANS, new Item(20579, 1), Constants.SMITHING, 60, 136.0, -1, new Item[] { new Item(20634, 1) }),
	MINER_BOOTS_ADAMANT(Category.ARTISANS, new Item(20580, 1), Constants.SMITHING, 70, 278.0, -1, new Item[] { new Item(20635, 1) }),
	MINER_BOOTS_RUNE(Category.ARTISANS, new Item(20581, 1), Constants.SMITHING, 90, 505.0, -1, new Item[] { new Item(20636, 1) }),
	MINER_CHESTPLATE_IRON(Category.ARTISANS, new Item(20582, 1), Constants.SMITHING, 30, 101.0, -1, new Item[] { new Item(20632, 1) }),
	MINER_CHESTPLATE_STEEL(Category.ARTISANS, new Item(20583, 1), Constants.SMITHING, 45, 131.0, -1, new Item[] { new Item(20633, 1) }),
	MINER_CHESTPLATE_MITHRIL(Category.ARTISANS, new Item(20584, 1), Constants.SMITHING, 60, 136.0, -1, new Item[] { new Item(20634, 1) }),
	MINER_CHESTPLATE_ADAMANT(Category.ARTISANS, new Item(20585, 1), Constants.SMITHING, 70, 278.0, -1, new Item[] { new Item(20635, 1) }),
	MINER_CHESTPLATE_RUNE(Category.ARTISANS, new Item(20586, 1), Constants.SMITHING, 90, 505.0, -1, new Item[] { new Item(20636, 1) }),
	MINER_GAUNTLETS_IRON(Category.ARTISANS, new Item(20587, 1), Constants.SMITHING, 30, 101.0, -1, new Item[] { new Item(20632, 1) }),
	MINER_GAUNTLETS_STEEL(Category.ARTISANS, new Item(20588, 1), Constants.SMITHING, 45, 131.0, -1, new Item[] { new Item(20633, 1) }),
	MINER_GAUNTLETS_MITHRIL(Category.ARTISANS, new Item(20589, 1), Constants.SMITHING, 60, 136.0, -1, new Item[] { new Item(20634, 1) }),
	MINER_GAUNTLETS_ADAMANT(Category.ARTISANS, new Item(20590, 1), Constants.SMITHING, 70, 278.0, -1, new Item[] { new Item(20635, 1) }),
	MINER_GAUNTLETS_RUNE(Category.ARTISANS, new Item(20591, 1), Constants.SMITHING, 90, 505.0, -1, new Item[] { new Item(20636, 1) }),
	WARRIOR_HELM_IRON(Category.ARTISANS, new Item(20592, 1), Constants.SMITHING, 30, 202.0, -1, new Item[] { new Item(20637, 1) }),
	WARRIOR_HELM_STEEL(Category.ARTISANS, new Item(20593, 1), Constants.SMITHING, 45, 253.0, -1, new Item[] { new Item(20638, 1) }),
	WARRIOR_HELM_MITHRIL(Category.ARTISANS, new Item(20594, 1), Constants.SMITHING, 60, 316.0, -1, new Item[] { new Item(20639, 1) }),
	WARRIOR_HELM_ADAMANT(Category.ARTISANS, new Item(20595, 1), Constants.SMITHING, 70, 455.0, -1, new Item[] { new Item(20640, 1) }),
	WARRIOR_HELM_RUNE(Category.ARTISANS, new Item(20596, 1), Constants.SMITHING, 90, 631.0, -1, new Item[] { new Item(20641, 1) }),
	WARRIOR_BOOTS_IRON(Category.ARTISANS, new Item(20597, 1), Constants.SMITHING, 30, 202.0, -1, new Item[] { new Item(20637, 1) }),
	WARRIOR_BOOTS_STEEL(Category.ARTISANS, new Item(20598, 1), Constants.SMITHING, 45, 253.0, -1, new Item[] { new Item(20638, 1) }),
	WARRIOR_BOOTS_MITHRIL(Category.ARTISANS, new Item(20599, 1), Constants.SMITHING, 60, 316.0, -1, new Item[] { new Item(20639, 1) }),
	WARRIOR_BOOTS_ADAMANT(Category.ARTISANS, new Item(20600, 1), Constants.SMITHING, 70, 455.0, -1, new Item[] { new Item(20640, 1) }),
	WARRIOR_BOOTS_RUNE(Category.ARTISANS, new Item(20601, 1), Constants.SMITHING, 90, 631.0, -1, new Item[] { new Item(20641, 1) }),
	WARRIOR_CHESTPLATE_IRON(Category.ARTISANS, new Item(20602, 1), Constants.SMITHING, 30, 202.0, -1, new Item[] { new Item(20637, 1) }),
	WARRIOR_CHESTPLATE_STEEL(Category.ARTISANS, new Item(20603, 1), Constants.SMITHING, 45, 253.0, -1, new Item[] { new Item(20638, 1) }),
	WARRIOR_CHESTPLATE_MITHRIL(Category.ARTISANS, new Item(20604, 1), Constants.SMITHING, 60, 316.0, -1, new Item[] { new Item(20639, 1) }),
	WARRIOR_CHESTPLATE_ADAMANT(Category.ARTISANS, new Item(20605, 1), Constants.SMITHING, 70, 455.0, -1, new Item[] { new Item(20640, 1) }),
	WARRIOR_CHESTPLATE_RUNE(Category.ARTISANS, new Item(20606, 1), Constants.SMITHING, 90, 631.0, -1, new Item[] { new Item(20641, 1) }),
	WARRIOR_GAUNTLETS_IRON(Category.ARTISANS, new Item(20607, 1), Constants.SMITHING, 30, 202.0, -1, new Item[] { new Item(20637, 1) }),
	WARRIOR_GAUNTLETS_STEEL(Category.ARTISANS, new Item(20608, 1), Constants.SMITHING, 45, 253.0, -1, new Item[] { new Item(20638, 1) }),
	WARRIOR_GAUNTLETS_MITHRIL(Category.ARTISANS, new Item(20609, 1), Constants.SMITHING, 60, 316.0, -1, new Item[] { new Item(20639, 1) }),
	WARRIOR_GAUNTLETS_ADAMANT(Category.ARTISANS, new Item(20610, 1), Constants.SMITHING, 70, 455.0, -1, new Item[] { new Item(20640, 1) }),
	WARRIOR_GAUNTLETS_RUNE(Category.ARTISANS, new Item(20611, 1), Constants.SMITHING, 90, 631.0, -1, new Item[] { new Item(20641, 1) }),
	SMITHS_HELM_IRON(Category.ARTISANS, new Item(20612, 1), Constants.SMITHING, 30, 240.0, -1, new Item[] { new Item(20642, 1) }),
	SMITHS_HELM_STEEL(Category.ARTISANS, new Item(20613, 1), Constants.SMITHING, 45, 389.0, -1, new Item[] { new Item(20643, 1) }),
	SMITHS_HELM_MITHRIL(Category.ARTISANS, new Item(20614, 1), Constants.SMITHING, 60, 404.0, -1, new Item[] { new Item(20644, 1) }),
	SMITHS_HELM_ADAMANT(Category.ARTISANS, new Item(20615, 1), Constants.SMITHING, 70, 568.0, -1, new Item[] { new Item(20645, 1) }),
	SMITHS_HELM_RUNE(Category.ARTISANS, new Item(20616, 1), Constants.SMITHING, 90, 758.0, -1, new Item[] { new Item(20646, 1) }),
	SMITHS_BOOTS_IRON(Category.ARTISANS, new Item(20617, 1), Constants.SMITHING, 30, 240.0, -1, new Item[] { new Item(20642, 1) }),
	SMITHS_BOOTS_STEEL(Category.ARTISANS, new Item(20618, 1), Constants.SMITHING, 45, 389.0, -1, new Item[] { new Item(20643, 1) }),
	SMITHS_BOOTS_MITHRIL(Category.ARTISANS, new Item(20619, 1), Constants.SMITHING, 60, 404.0, -1, new Item[] { new Item(20644, 1) }),
	SMITHS_BOOTS_ADAMANT(Category.ARTISANS, new Item(20620, 1), Constants.SMITHING, 70, 568.0, -1, new Item[] { new Item(20645, 1) }),
	SMITHS_BOOTS_RUNE(Category.ARTISANS, new Item(20621, 1), Constants.SMITHING, 90, 758.0, -1, new Item[] { new Item(20646, 1) }),
	SMITHS_CHESTPLATE_IRON(Category.ARTISANS, new Item(20622, 1), Constants.SMITHING, 30, 240.0, -1, new Item[] { new Item(20642, 1) }),
	SMITHS_CHESTPLATE_STEEL(Category.ARTISANS, new Item(20623, 1), Constants.SMITHING, 45, 389.0, -1, new Item[] { new Item(20643, 1) }),
	SMITHS_CHESTPLATE_MITHRIL(Category.ARTISANS, new Item(20624, 1), Constants.SMITHING, 60, 404.0, -1, new Item[] { new Item(20644, 1) }),
	SMITHS_CHESTPLATE_ADAMANT(Category.ARTISANS, new Item(20625, 1), Constants.SMITHING, 70, 568.0, -1, new Item[] { new Item(20645, 1) }),
	SMITHS_CHESTPLATE_RUNE(Category.ARTISANS, new Item(20626, 1), Constants.SMITHING, 90, 758.0, -1, new Item[] { new Item(20646, 1) }),
	SMITHS_GAUNTLETS_IRON(Category.ARTISANS, new Item(20627, 1), Constants.SMITHING, 30, 240.0, -1, new Item[] { new Item(20642, 1) }),
	SMITHS_GAUNTLETS_STEEL(Category.ARTISANS, new Item(20628, 1), Constants.SMITHING, 45, 389.0, -1, new Item[] { new Item(20643, 1) }),
	SMITHS_GAUNTLETS_MITHRIL(Category.ARTISANS, new Item(20629, 1), Constants.SMITHING, 60, 404.0, -1, new Item[] { new Item(20644, 1) }),
	SMITHS_GAUNTLETS_ADAMANT(Category.ARTISANS, new Item(20630, 1), Constants.SMITHING, 70, 568.0, -1, new Item[] { new Item(20645, 1) }),
	SMITHS_GAUNTLETS_RUNE(Category.ARTISANS, new Item(20631, 1), Constants.SMITHING, 90, 758.0, -1, new Item[] { new Item(20646, 1) }),
	IRON_INGOT_I(Category.ARTISANS, new Item(20632, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25629, 1) }),
	STEEL_INGOT_I(Category.ARTISANS, new Item(20633, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25629, 1), new Item(25630, 2) }),
	MITHRIL_INGOT_I(Category.ARTISANS, new Item(20634, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25631, 1), new Item(25630, 4) }),
	ADAMANT_INGOT_I(Category.ARTISANS, new Item(20635, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25632, 1), new Item(25630, 6) }),
	RUNE_INGOT_I(Category.ARTISANS, new Item(20636, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25633, 1), new Item(25630, 8) }),
	IRON_INGOT_II(Category.ARTISANS, new Item(20637, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25629, 9) }),
	STEEL_INGOT_II(Category.ARTISANS, new Item(20638, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25629, 4), new Item(25630, 7) }),
	MITHRIL_INGOT_II(Category.ARTISANS, new Item(20639, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25631, 3), new Item(25630, 12) }),
	ADAMANT_INGOT_II(Category.ARTISANS, new Item(20640, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25632, 3), new Item(25630, 14) }),
	RUNE_INGOT_II(Category.ARTISANS, new Item(20641, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25633, 2), new Item(25630, 16) }),
	IRON_INGOT_III(Category.ARTISANS, new Item(20642, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25629, 12) }),
	STEEL_INGOT_III(Category.ARTISANS, new Item(20643, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25629, 9), new Item(25630, 17) }),
	MITHRIL_INGOT_III(Category.ARTISANS, new Item(20644, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25631, 6), new Item(25630, 24) }),
	ADAMANT_INGOT_III(Category.ARTISANS, new Item(20645, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25632, 4), new Item(25630, 22) }),
	RUNE_INGOT_III(Category.ARTISANS, new Item(20646, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25633, 4), new Item(25630, 30) }),
	IRON_INGOT_IV(Category.ARTISANS, new Item(20648, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25629, 75) }),
	STEEL_INGOT_IV(Category.ARTISANS, new Item(20649, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25629, 40), new Item(25630, 80) }),
	MITHRIL_INGOT_IV(Category.ARTISANS, new Item(20650, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25631, 30), new Item(25630, 120) }),
	ADAMANT_INGOT_IV(Category.ARTISANS, new Item(20651, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25632, 25), new Item(25630, 150) }),
	RUNE_INGOT_IV(Category.ARTISANS, new Item(20652, 1), Skills.NONE, 1, 0.0, -1, new Item[] { new Item(25633, 18), new Item(25630, 144) }),

	SALVE_CLOTH(Category.DUNG_SPINNING, new Item(17468, 1), Constants.CRAFTING, 1, 2.0, -1, new Item[] { new Item(17448, 1) }),
	WILDERCRESS_CLOTH(Category.DUNG_SPINNING, new Item(17470, 1), Constants.CRAFTING, 10, 3.0, -1, new Item[] { new Item(17450, 1) }),
	BLIGHTLEAF_CLOTH(Category.DUNG_SPINNING, new Item(17472, 1), Constants.CRAFTING, 20, 3.0, -1, new Item[] { new Item(17452, 1) }),
	ROSEBLOOD_CLOTH(Category.DUNG_SPINNING, new Item(17474, 1), Constants.CRAFTING, 30, 4.0, -1, new Item[] { new Item(17454, 1) }),
	BRYLL_CLOTH(Category.DUNG_SPINNING, new Item(17476, 1), Constants.CRAFTING, 40, 5.0, -1, new Item[] { new Item(17456, 1) }),
	DUSKWEED_CLOTH(Category.DUNG_SPINNING, new Item(17478, 1), Constants.CRAFTING, 50, 6.0, -1, new Item[] { new Item(17458, 1) }),
	SOULBELL_CLOTH(Category.DUNG_SPINNING, new Item(17480, 1), Constants.CRAFTING, 60, 7.0, -1, new Item[] { new Item(17460, 1) }),
	ECTOCLOTH(Category.DUNG_SPINNING, new Item(17482, 1), Constants.CRAFTING, 70, 9.0, -1, new Item[] { new Item(17462, 1) }),
	RUNIC_CLOTH(Category.DUNG_SPINNING, new Item(17484, 1), Constants.CRAFTING, 80, 10.0, -1, new Item[] { new Item(17464, 1) }),
	SPIRITBLOOM_CLOTH(Category.DUNG_SPINNING, new Item(17486, 1), Constants.CRAFTING, 90, 12.0, -1, new Item[] { new Item(17466, 1) }),

	SALVE_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16735, 1), Constants.CRAFTING, 4, 31.0, 17446, new Item[] { new Item(17468, 2) }),
	WILDERCRESS_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16737, 1), Constants.CRAFTING, 14, 48.0, 17446, new Item[] { new Item(17470, 2) }),
	BLIGHTLEAF_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16739, 1), Constants.CRAFTING, 24, 65.0, 17446, new Item[] { new Item(17472, 2) }),
	ROSEBLOOD_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16741, 1), Constants.CRAFTING, 34, 82.0, 17446, new Item[] { new Item(17474, 2) }),
	BRYLL_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16743, 1), Constants.CRAFTING, 44, 99.0, 17446, new Item[] { new Item(17476, 2) }),
	DUSKWEED_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16745, 1), Constants.CRAFTING, 54, 116.0, 17446, new Item[] { new Item(17478, 2) }),
	SOULBELL_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16747, 1), Constants.CRAFTING, 64, 133.0, 17446, new Item[] { new Item(17480, 2) }),
	ECTOHOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16749, 1), Constants.CRAFTING, 74, 150.0, 17446, new Item[] { new Item(17482, 2) }),
	RUNIC_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16751, 1), Constants.CRAFTING, 84, 167.0, 17446, new Item[] { new Item(17484, 2) }),
	SPIRITBLOOM_HOOD(Category.DUNG_NEEDLE_CRAFTING, new Item(16753, 1), Constants.CRAFTING, 94, 184.0, 17446, new Item[] { new Item(17486, 2) }),
	SALVE_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16845, 1), Constants.CRAFTING, 6, 52.0, 17446, new Item[] { new Item(17468, 3) }),
	WILDERCRESS_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16847, 1), Constants.CRAFTING, 16, 78.0, 17446, new Item[] { new Item(17470, 3) }),
	BLIGHTLEAF_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16849, 1), Constants.CRAFTING, 26, 103.0, 17446, new Item[] { new Item(17472, 3) }),
	ROSEBLOOD_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16851, 1), Constants.CRAFTING, 36, 129.0, 17446, new Item[] { new Item(17474, 3) }),
	BRYLL_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16853, 1), Constants.CRAFTING, 46, 154.0, 17446, new Item[] { new Item(17476, 3) }),
	DUSKWEED_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16855, 1), Constants.CRAFTING, 56, 180.0, 17446, new Item[] { new Item(17478, 3) }),
	SOULBELL_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16857, 1), Constants.CRAFTING, 66, 205.0, 17446, new Item[] { new Item(17480, 3) }),
	ECTOROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16859, 1), Constants.CRAFTING, 76, 231.0, 17446, new Item[] { new Item(17482, 3) }),
	RUNIC_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16861, 1), Constants.CRAFTING, 86, 256.0, 17446, new Item[] { new Item(17484, 3) }),
	SPIRITBLOOM_ROBE_BOTTOM(Category.DUNG_NEEDLE_CRAFTING, new Item(16863, 1), Constants.CRAFTING, 96, 282.0, 17446, new Item[] { new Item(17486, 3) }),
	SALVE_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16911, 1), Constants.CRAFTING, 2, 14.0, 17446, new Item[] { new Item(17468, 1) }),
	WILDERCRESS_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16913, 1), Constants.CRAFTING, 12, 22.0, 17446, new Item[] { new Item(17470, 1) }),
	BLIGHTLEAF_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16915, 1), Constants.CRAFTING, 22, 31.0, 17446, new Item[] { new Item(17472, 1) }),
	ROSEBLOOD_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16917, 1), Constants.CRAFTING, 32, 39.0, 17446, new Item[] { new Item(17474, 1) }),
	BRYLL_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16919, 1), Constants.CRAFTING, 42, 48.0, 17446, new Item[] { new Item(17476, 1) }),
	DUSKWEED_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16921, 1), Constants.CRAFTING, 52, 56.0, 17446, new Item[] { new Item(17478, 1) }),
	SOULBELL_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16923, 1), Constants.CRAFTING, 62, 65.0, 17446, new Item[] { new Item(17480, 1) }),
	ECTOSHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16925, 1), Constants.CRAFTING, 72, 73.0, 17446, new Item[] { new Item(17482, 1) }),
	RUNIC_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16927, 1), Constants.CRAFTING, 82, 82.0, 17446, new Item[] { new Item(17484, 1) }),
	SPIRITBLOOM_SHOES(Category.DUNG_NEEDLE_CRAFTING, new Item(16929, 1), Constants.CRAFTING, 92, 90.0, 17446, new Item[] { new Item(17486, 1) }),
	SALVE_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17151, 1), Constants.CRAFTING, 1, 12.0, 17446, new Item[] { new Item(17468, 1) }),
	WILDERCRESS_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17153, 1), Constants.CRAFTING, 10, 21.0, 17446, new Item[] { new Item(17470, 1) }),
	BLIGHTLEAF_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17155, 1), Constants.CRAFTING, 20, 29.0, 17446, new Item[] { new Item(17472, 1) }),
	ROSEBLOOD_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17157, 1), Constants.CRAFTING, 30, 38.0, 17446, new Item[] { new Item(17474, 1) }),
	BRYLL_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17159, 1), Constants.CRAFTING, 40, 46.0, 17446, new Item[] { new Item(17476, 1) }),
	DUSKWEED_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17161, 1), Constants.CRAFTING, 50, 55.0, 17446, new Item[] { new Item(17478, 1) }),
	SOULBELL_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17163, 1), Constants.CRAFTING, 60, 63.0, 17446, new Item[] { new Item(17480, 1) }),
	ECTOGLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17165, 1), Constants.CRAFTING, 70, 72.0, 17446, new Item[] { new Item(17482, 1) }),
	RUNIC_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17167, 1), Constants.CRAFTING, 80, 80.0, 17446, new Item[] { new Item(17484, 1) }),
	SPIRITBLOOM_GLOVES(Category.DUNG_NEEDLE_CRAFTING, new Item(17169, 1), Constants.CRAFTING, 90, 89.0, 17446, new Item[] { new Item(17486, 1) }),
	SALVE_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17217, 1), Constants.CRAFTING, 8, 96.0, 17446, new Item[] { new Item(17468, 5) }),
	WILDERCRESS_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17219, 1), Constants.CRAFTING, 18, 139.0, 17446, new Item[] { new Item(17470, 5) }),
	BLIGHTLEAF_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17221, 1), Constants.CRAFTING, 28, 181.0, 17446, new Item[] { new Item(17472, 5) }),
	ROSEBLOOD_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17223, 1), Constants.CRAFTING, 38, 224.0, 17446, new Item[] { new Item(17474, 5) }),
	BRYLL_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17225, 1), Constants.CRAFTING, 48, 266.0, 17446, new Item[] { new Item(17476, 5) }),
	DUSKWEED_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17227, 1), Constants.CRAFTING, 58, 309.0, 17446, new Item[] { new Item(17478, 5) }),
	SOULBELL_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17229, 1), Constants.CRAFTING, 68, 351.0, 17446, new Item[] { new Item(17480, 5) }),
	ECTOROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17231, 1), Constants.CRAFTING, 78, 394.0, 17446, new Item[] { new Item(17482, 5) }),
	RUNIC_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17233, 1), Constants.CRAFTING, 88, 436.0, 17446, new Item[] { new Item(17484, 5) }),
	SPIRITBLOOM_ROBE_TOP(Category.DUNG_NEEDLE_CRAFTING, new Item(17235, 1), Constants.CRAFTING, 98, 479.0, 17446, new Item[] { new Item(17486, 5) }),
	PROTOLEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17041, 1), Constants.CRAFTING, 5, 33.0, 17446, new Item[] { new Item(17424, 2) }),
	SUBLEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17043, 1), Constants.CRAFTING, 15, 50.0, 17446, new Item[] { new Item(17426, 2) }),
	PARALEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17045, 1), Constants.CRAFTING, 25, 67.0, 17446, new Item[] { new Item(17428, 2) }),
	ARCHLEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17047, 1), Constants.CRAFTING, 35, 84.0, 17446, new Item[] { new Item(17430, 2) }),
	DROMOLEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17049, 1), Constants.CRAFTING, 45, 101.0, 17446, new Item[] { new Item(17432, 2) }),
	SPINOLEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17051, 1), Constants.CRAFTING, 55, 118.0, 17446, new Item[] { new Item(17434, 2) }),
	GALLILEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17053, 1), Constants.CRAFTING, 65, 135.0, 17446, new Item[] { new Item(17436, 2) }),
	STEGOLEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17055, 1), Constants.CRAFTING, 75, 152.0, 17446, new Item[] { new Item(17438, 2) }),
	MEGALEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17057, 1), Constants.CRAFTING, 85, 169.0, 17446, new Item[] { new Item(17440, 2) }),
	TYRANNOLEATHER_COIF(Category.DUNG_NEEDLE_CRAFTING, new Item(17059, 1), Constants.CRAFTING, 95, 186.0, 17446, new Item[] { new Item(17442, 2) }),
	PROTOLEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17173, 1), Constants.CRAFTING, 9, 99.0, 17446, new Item[] { new Item(17424, 5) }),
	SUBLEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17175, 1), Constants.CRAFTING, 19, 142.0, 17446, new Item[] { new Item(17426, 5) }),
	PARALEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17177, 1), Constants.CRAFTING, 29, 184.0, 17446, new Item[] { new Item(17428, 5) }),
	ARCHLEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17179, 1), Constants.CRAFTING, 39, 269.0, 17446, new Item[] { new Item(17430, 5) }),
	DROMOLEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17181, 1), Constants.CRAFTING, 49, 269.0, 17446, new Item[] { new Item(17432, 5) }),
	SPINOLEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17183, 1), Constants.CRAFTING, 59, 312.0, 17446, new Item[] { new Item(17434, 5) }),
	GALLILEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17185, 1), Constants.CRAFTING, 69, 354.0, 17446, new Item[] { new Item(17436, 5) }),
	STEGOLEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17187, 1), Constants.CRAFTING, 79, 397.0, 17446, new Item[] { new Item(17438, 5) }),
	MEGALEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17189, 1), Constants.CRAFTING, 89, 439.0, 17446, new Item[] { new Item(17440, 5) }),
	TYRANNOLEATHER_BODY(Category.DUNG_NEEDLE_CRAFTING, new Item(17191, 1), Constants.CRAFTING, 99, 482.0, 17446, new Item[] { new Item(17442, 5) }),
	PROTOLEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17195, 1), Constants.CRAFTING, 1, 13.0, 17446, new Item[] { new Item(17424, 1) }),
	SUBLEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17197, 1), Constants.CRAFTING, 11, 21.0, 17446, new Item[] { new Item(17426, 1) }),
	PARALEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17199, 1), Constants.CRAFTING, 21, 30.0, 17446, new Item[] { new Item(17428, 1) }),
	ARCHLEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17201, 1), Constants.CRAFTING, 31, 38.0, 17446, new Item[] { new Item(17430, 1) }),
	DROMOLEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17203, 1), Constants.CRAFTING, 41, 47.0, 17446, new Item[] { new Item(17432, 1) }),
	SPINOLEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17205, 1), Constants.CRAFTING, 51, 55.0, 17446, new Item[] { new Item(17434, 1) }),
	GALLILEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17207, 1), Constants.CRAFTING, 61, 64.0, 17446, new Item[] { new Item(17436, 1) }),
	STEGOLEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17209, 1), Constants.CRAFTING, 71, 72.0, 17446, new Item[] { new Item(17438, 1) }),
	MEGALEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17211, 1), Constants.CRAFTING, 81, 81.0, 17446, new Item[] { new Item(17440, 1) }),
	TYRANNOLEATHER_VAMBRACES(Category.DUNG_NEEDLE_CRAFTING, new Item(17213, 1), Constants.CRAFTING, 91, 89.0, 17446, new Item[] { new Item(17442, 1) }),
	PROTOLEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17297, 1), Constants.CRAFTING, 3, 14.0, 17446, new Item[] { new Item(17424, 1) }),
	SUBLEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17299, 1), Constants.CRAFTING, 13, 23.0, 17446, new Item[] { new Item(17426, 1) }),
	PARALEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17301, 1), Constants.CRAFTING, 23, 31.0, 17446, new Item[] { new Item(17428, 1) }),
	ARCHLEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17303, 1), Constants.CRAFTING, 33, 40.0, 17446, new Item[] { new Item(17430, 1) }),
	DROMOLEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17305, 1), Constants.CRAFTING, 43, 48.0, 17446, new Item[] { new Item(17432, 1) }),
	SPINOLEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17307, 1), Constants.CRAFTING, 53, 57.0, 17446, new Item[] { new Item(17434, 1) }),
	GALLILEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17309, 1), Constants.CRAFTING, 63, 65.0, 17446, new Item[] { new Item(17436, 1) }),
	STEGOLEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17311, 1), Constants.CRAFTING, 73, 74.0, 17446, new Item[] { new Item(17438, 1) }),
	MEGALEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17313, 1), Constants.CRAFTING, 83, 82.0, 17446, new Item[] { new Item(17440, 1) }),
	TYRANNOLEATHER_BOOTS(Category.DUNG_NEEDLE_CRAFTING, new Item(17315, 1), Constants.CRAFTING, 93, 91.0, 17446, new Item[] { new Item(17442, 1) }),
	PROTOLEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17319, 1), Constants.CRAFTING, 7, 54.0, 17446, new Item[] { new Item(17424, 3) }),
	SUBLEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17321, 1), Constants.CRAFTING, 17, 80.0, 17446, new Item[] { new Item(17426, 3) }),
	PARALEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17323, 1), Constants.CRAFTING, 27, 105.0, 17446, new Item[] { new Item(17428, 3) }),
	ARCHLEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17325, 1), Constants.CRAFTING, 37, 131.0, 17446, new Item[] { new Item(17430, 3) }),
	DROMOLEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17327, 1), Constants.CRAFTING, 47, 156.0, 17446, new Item[] { new Item(17432, 3) }),
	SPINOLEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17329, 1), Constants.CRAFTING, 57, 182.0, 17446, new Item[] { new Item(17434, 3) }),
	GALLILEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17331, 1), Constants.CRAFTING, 67, 207.0, 17446, new Item[] { new Item(17436, 3) }),
	STEGOLEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17333, 1), Constants.CRAFTING, 77, 233.0, 17446, new Item[] { new Item(17438, 3) }),
	MEGALEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17335, 1), Constants.CRAFTING, 87, 258.0, 17446, new Item[] { new Item(17440, 3) }),
	TYRANNOLEATHER_CHAPS(Category.DUNG_NEEDLE_CRAFTING, new Item(17337, 1), Constants.CRAFTING, 97, 284.0, 17446, new Item[] { new Item(17442, 3) }),

	HEADLESS_ARROW2(Category.DUNG_ARROW_COMBINING, new Item(17747, 15), Constants.FLETCHING, 1, 0.0, -1, new Item[] { new Item(17742, 15), new Item(17796, 15) }),
	NOVITE_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16427, 15), Constants.FLETCHING, 1, 1.0, -1, new Item[] { new Item(17747, 15), new Item(17885, 15) }),
	BATHUS_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16432, 15), Constants.FLETCHING, 11, 2.0, -1, new Item[] { new Item(17747, 15), new Item(17890, 15) }),
	MARMAROS_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16437, 15), Constants.FLETCHING, 22, 5.0, -1, new Item[] { new Item(17747, 15), new Item(17895, 15) }),
	KRATONITE_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16442, 15), Constants.FLETCHING, 33, 7.0, -1, new Item[] { new Item(17747, 15), new Item(17900, 15) }),
	FRACTITE_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16447, 15), Constants.FLETCHING, 44, 10.0, -1, new Item[] { new Item(17747, 15), new Item(17905, 15) }),
	ZEPHYRIUM_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16452, 15), Constants.FLETCHING, 55, 12.0, -1, new Item[] { new Item(17747, 15), new Item(17910, 15) }),
	ARGONITE_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16457, 15), Constants.FLETCHING, 66, 15.0, -1, new Item[] { new Item(17747, 15), new Item(17915, 15) }),
	KATAGON_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16462, 15), Constants.FLETCHING, 77, 17.0, -1, new Item[] { new Item(17747, 15), new Item(17920, 15) }),
	GORGONITE_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16467, 15), Constants.FLETCHING, 88, 20.0, -1, new Item[] { new Item(17747, 15), new Item(17925, 15) }),
	PROMETHIUM_ARROWS(Category.DUNG_ARROW_COMBINING, new Item(16472, 15), Constants.FLETCHING, 99, 22.0, -1, new Item[] { new Item(17747, 15), new Item(17930, 15) }),

	TANGLE_GUM_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16317, 1), Constants.FLETCHING, 6, 5.0, -1, new Item[] { new Item(17722, 1), new Item(17752, 1) }),
	SEEPING_ELM_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16319, 1), Constants.FLETCHING, 16, 10.0, -1, new Item[] { new Item(17724, 1), new Item(17752, 1) }),
	BLOOD_SPINDLE_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16321, 1), Constants.FLETCHING, 26, 17.0, -1, new Item[] { new Item(17726, 1), new Item(17752, 1) }),
	UTUKU_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16323, 1), Constants.FLETCHING, 36, 26.0, -1, new Item[] { new Item(17728, 1), new Item(17752, 1) }),
	SPINEBEAM_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16325, 1), Constants.FLETCHING, 46, 37.0, -1, new Item[] { new Item(17730, 1), new Item(17752, 1) }),
	BOVISTRANGLER_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16327, 1), Constants.FLETCHING, 56, 51.0, -1, new Item[] { new Item(17732, 1), new Item(17752, 1) }),
	THIGAT_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16329, 1), Constants.FLETCHING, 66, 67.0, -1, new Item[] { new Item(17734, 1), new Item(17752, 1) }),
	CORPSETHORN_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16331, 1), Constants.FLETCHING, 76, 86.0, -1, new Item[] { new Item(17736, 1), new Item(17752, 1) }),
	ENTGALLOW_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16333, 1), Constants.FLETCHING, 86, 106.0, -1, new Item[] { new Item(17738, 1), new Item(17752, 1) }),
	GRAVE_CREEPER_LONGBOW(Category.DUNG_BOWSTRINGING, new Item(16335, 1), Constants.FLETCHING, 96, 129.0, -1, new Item[] { new Item(17740, 1), new Item(17752, 1) }),
	TANGLE_GUM_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16867, 1), Constants.FLETCHING, 1, 5.0, -1, new Item[] { new Item(17702, 1), new Item(17752, 1) }),
	SEEPING_ELM_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16869, 1), Constants.FLETCHING, 11, 9.0, -1, new Item[] { new Item(17704, 1), new Item(17752, 1) }),
	BLOOD_SPINDLE_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16871, 1), Constants.FLETCHING, 21, 15.0, -1, new Item[] { new Item(17706, 1), new Item(17752, 1) }),
	UTUKU_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16873, 1), Constants.FLETCHING, 31, 23.0, -1, new Item[] { new Item(17708, 1), new Item(17752, 1) }),
	SPINEBEAM_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16875, 1), Constants.FLETCHING, 41, 33.0, -1, new Item[] { new Item(17710, 1), new Item(17752, 1) }),
	BOVISTRANGLER_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16877, 1), Constants.FLETCHING, 51, 45.0, -1, new Item[] { new Item(17712, 1), new Item(17752, 1) }),
	THIGAT_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16879, 1), Constants.FLETCHING, 61, 59.0, -1, new Item[] { new Item(17714, 1), new Item(17752, 1) }),
	CORPSETHORN_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16881, 1), Constants.FLETCHING, 71, 75.0, -1, new Item[] { new Item(17716, 1), new Item(17752, 1) }),
	ENTGALLOW_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16883, 1), Constants.FLETCHING, 81, 93.0, -1, new Item[] { new Item(17718, 1), new Item(17752, 1) }),
	GRAVE_CREEPER_SHORTBOW(Category.DUNG_BOWSTRINGING, new Item(16885, 1), Constants.FLETCHING, 91, 113.0, -1, new Item[] { new Item(17720, 1), new Item(17752, 1) }),

	TANGLE_GUM_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16977, 1), Constants.FLETCHING, 8, 9.0, 17754, new Item[] { new Item(17682, 1) }),
	SEEPING_ELM_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16979, 1), Constants.FLETCHING, 18, 16.0, 17754, new Item[] { new Item(17684, 1) }),
	BLOOD_SPINDLE_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16981, 1), Constants.FLETCHING, 28, 27.0, 17754, new Item[] { new Item(17686, 1) }),
	UTUKU_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16983, 1), Constants.FLETCHING, 38, 41.0, 17754, new Item[] { new Item(17688, 1) }),
	SPINEBEAM_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16985, 1), Constants.FLETCHING, 48, 59.0, 17754, new Item[] { new Item(17690, 1) }),
	BOVISTRANGLER_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16987, 1), Constants.FLETCHING, 58, 81.0, 17754, new Item[] { new Item(17692, 1) }),
	THIGAT_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16989, 1), Constants.FLETCHING, 68, 106.0, 17754, new Item[] { new Item(17694, 1) }),
	CORPSETHORN_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16991, 1), Constants.FLETCHING, 78, 135.0, 17754, new Item[] { new Item(17696, 1) }),
	ENTGALLOW_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16993, 1), Constants.FLETCHING, 88, 167.0, 17754, new Item[] { new Item(17698, 1) }),
	GRAVE_CREEPER_STAFF(Category.DUNG_KNIFE_FLETCHING, new Item(16995, 1), Constants.FLETCHING, 98, 203.0, 17754, new Item[] { new Item(17700, 1) }),
	TANGLE_GUM_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17702, 1), Constants.FLETCHING, 1, 5.0, 17754, new Item[] { new Item(17682, 1) }),
	SEEPING_ELM_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17704, 1), Constants.FLETCHING, 11, 9.0, 17754, new Item[] { new Item(17684, 1) }),
	BLOOD_SPINDLE_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17706, 1), Constants.FLETCHING, 21, 15.0, 17754, new Item[] { new Item(17686, 1) }),
	UTUKU_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17708, 1), Constants.FLETCHING, 31, 23.0, 17754, new Item[] { new Item(17688, 1) }),
	SPINEBEAM_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17710, 1), Constants.FLETCHING, 41, 33.0, 17754, new Item[] { new Item(17690, 1) }),
	BOVISTRANGLER_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17712, 1), Constants.FLETCHING, 51, 45.0, 17754, new Item[] { new Item(17692, 1) }),
	THIGAT_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17714, 1), Constants.FLETCHING, 61, 59.0, 17754, new Item[] { new Item(17694, 1) }),
	CORPSETHORN_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17716, 1), Constants.FLETCHING, 71, 75.0, 17754, new Item[] { new Item(17696, 1) }),
	ENTGALLOW_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17718, 1), Constants.FLETCHING, 81, 93.0, 17754, new Item[] { new Item(17698, 1) }),
	GRAVE_CREEPER_SHORTBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17720, 1), Constants.FLETCHING, 91, 113.0, 17754, new Item[] { new Item(17700, 1) }),
	TANGLE_GUM_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17722, 1), Constants.FLETCHING, 6, 5.0, 17754, new Item[] { new Item(17682, 1) }),
	SEEPING_ELM_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17724, 1), Constants.FLETCHING, 16, 10.0, 17754, new Item[] { new Item(17684, 1) }),
	BLOOD_SPINDLE_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17726, 1), Constants.FLETCHING, 26, 17.0, 17754, new Item[] { new Item(17686, 1) }),
	UTUKU_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17728, 1), Constants.FLETCHING, 36, 26.0, 17754, new Item[] { new Item(17688, 1) }),
	SPINEBEAM_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17730, 1), Constants.FLETCHING, 46, 37.0, 17754, new Item[] { new Item(17690, 1) }),
	BOVISTRANGLER_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17732, 1), Constants.FLETCHING, 56, 51.0, 17754, new Item[] { new Item(17692, 1) }),
	THIGAT_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17734, 1), Constants.FLETCHING, 66, 67.0, 17754, new Item[] { new Item(17694, 1) }),
	CORPSETHORN_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17736, 1), Constants.FLETCHING, 76, 86.0, 17754, new Item[] { new Item(17696, 1) }),
	ENTGALLOW_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17738, 1), Constants.FLETCHING, 86, 106.0, 17754, new Item[] { new Item(17698, 1) }),
	GRAVE_CREEPER_LONGBOW_U(Category.DUNG_KNIFE_FLETCHING, new Item(17740, 1), Constants.FLETCHING, 96, 129.0, 17754, new Item[] { new Item(17700, 1) }),
	ARROW_SHAFT2(Category.DUNG_KNIFE_FLETCHING, new Item(17742, 15), Constants.FLETCHING, 1, 0.0, 17754, new Item[] { new Item(17682, 1) }),
	TANGLE_GUM_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17756, 1), Constants.FLETCHING, 3, 12.0, 17754, new Item[] { new Item(17682, 1) }),
	SEEPING_ELM_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17758, 1), Constants.FLETCHING, 13, 21.0, 17754, new Item[] { new Item(17684, 1) }),
	BLOOD_SPINDLE_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17760, 1), Constants.FLETCHING, 23, 36.0, 17754, new Item[] { new Item(17686, 1) }),
	UTUKU_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17762, 1), Constants.FLETCHING, 33, 55.0, 17754, new Item[] { new Item(17688, 1) }),
	SPINEBEAM_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17764, 1), Constants.FLETCHING, 43, 79.0, 17754, new Item[] { new Item(17690, 1) }),
	BOVISTRANGLER_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17766, 1), Constants.FLETCHING, 53, 108.0, 17754, new Item[] { new Item(17692, 1) }),
	THIGAT_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17768, 1), Constants.FLETCHING, 63, 141.0, 17754, new Item[] { new Item(17694, 1) }),
	CORPSETHORN_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17770, 1), Constants.FLETCHING, 73, 180.0, 17754, new Item[] { new Item(17696, 1) }),
	ENTGALLOW_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17772, 1), Constants.FLETCHING, 83, 223.0, 17754, new Item[] { new Item(17698, 1) }),
	GRAVE_CREEPER_TRAP(Category.DUNG_KNIFE_FLETCHING, new Item(17774, 1), Constants.FLETCHING, 93, 271.0, 17754, new Item[] { new Item(17700, 1) }),

	;

	private Item product;
	private int skill;
	private int req;
	private double xp;
	private int tool;
	private Item[] materials;
	private Category category;

	private ReqItem(Category category, Item product, int skill, int req, double xp, int tool, Item[] materials) {
		this.product = product;
		this.skill = skill;
		this.req = req;
		this.xp = xp;
		this.tool = tool;
		this.materials = materials;
		this.category = category;
	}

	private ReqItem(Item product, int skill, int req, double xp, int tool, Item[] materials) {
		this(null, product, skill, req, xp, tool, materials);
	}

	public Item getProduct() {
		return product;
	}

	public int getSkill() {
		return skill;
	}

	public int getReq() {
		return req;
	}

	public double getXp() {
		return xp;
	}

	public int getTool() {
		return tool;
	}

	public Item[] getMaterials() {
		return materials;
	}

	private static Map<Integer, ReqItem> items = new HashMap<>();

	static {
		for (ReqItem item : ReqItem.values())
			items.put(item.getProduct().getId(), item);
	}

	public Item[] getMaterialsFor(int amount) {
		Item[] mats = new Item[materials.length];
		for (int i = 0;i < mats.length;i++)
			mats[i] = new Item(materials[i].getId(), materials[i].getAmount()*amount);
		return mats;
	}

	public static ReqItem getRequirements(int id) {
		return items.get(id);
	}

	public static ReqItem[] getProducts(Category category, int material) {
		ArrayList<ReqItem> products = new ArrayList<>();
		for (ReqItem req : ReqItem.values()) {
			if (req.category != category)
				continue;
			for (Item mat : req.materials)
				if (mat.getId() == material)
					products.add(req);
		}
		return products.toArray(new ReqItem[products.size()]);
	}

	public static ReqItem[] getProducts(Category category) {
		ArrayList<ReqItem> products = new ArrayList<>();
		for (ReqItem req : ReqItem.values()) {
			if (req.category != category)
				continue;
			products.add(req);
		}
		return products.toArray(new ReqItem[products.size()]);
	}

	public Category getCategory() {
		return category;
	}
}
