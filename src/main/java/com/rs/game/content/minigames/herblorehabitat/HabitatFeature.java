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
package com.rs.game.content.minigames.herblorehabitat;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.World;
import com.rs.game.content.skills.magic.Magic;
import com.rs.lib.Constants;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public enum HabitatFeature {
	Boneyard(7, 56, 75),
	Abandoned_house(3, 57, 71),
	Thermal_vent(4, 59, 72),
	Tall_grass(2, 62, 70),
	Pond(1, 65, 68),
	Standing_stones(5, 70, 73),
	Dark_pit(6, 80, 74);

	private static Map<Integer, HabitatFeature> BUTTON_MAP = new HashMap<>();

	static {
		for (HabitatFeature h : HabitatFeature.values())
			BUTTON_MAP.put(h.button, h);
	}

	public static HabitatFeature forButton(int buttonId) {
		return BUTTON_MAP.get(buttonId);
	}

	public final int val;
	public final int level;
	public final int button;

	private HabitatFeature(int val, int level, int button) {
		this.val = val;
		this.level = level;
		this.button = button;
	}

	public static LoginHandler updateFeature = new LoginHandler(e -> e.getPlayer().setHabitatFeature(e.getPlayer().getHabitatFeature()));

	public static ObjectClickHandler handleFeatureBuild = new ObjectClickHandler(new Object[] { 56803 }, e -> e.getPlayer().getInterfaceManager().sendInterface(459));

	public static ButtonClickHandler handleFeature = new ButtonClickHandler(459, e -> {
		if (e.getComponentId() >= 68 && e.getComponentId() <= 75) {
			HabitatFeature toBuild = HabitatFeature.forButton(e.getComponentId());
			if (toBuild == null)
				e.getPlayer().setHabitatFeature(null);
			else {
				if (e.getPlayer().getSkills().getLevel(Constants.CONSTRUCTION) < toBuild.level) {
					e.getPlayer().sendMessage("You need a Construction level of " + toBuild.level + " to build a " + toBuild.name() + ".");
					return;
				}
				e.getPlayer().setHabitatFeature(toBuild);
			}
			World.sendSpotAnim(Tile.of(2952, 2908, 0), new SpotAnim(1605));
			e.getPlayer().closeInterfaces();
		}
	});

	public static ItemClickHandler handleWitchdoctorTele = new ItemClickHandler(new Object[] { 20046 }, new String[] { "Teleport" }, e -> Magic.sendTeleportSpell(e.getPlayer(), 7082, 7084, 1229, 1229, 1, 0, Tile.of(2952, 2933, 0), 4, true, Magic.ITEM_TELEPORT, null));
}
