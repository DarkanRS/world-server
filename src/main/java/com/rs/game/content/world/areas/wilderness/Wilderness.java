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
package com.rs.game.content.world.areas.wilderness;

import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.thieving.Thieving;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.DropSets;

@PluginEventHandler
public class Wilderness {

	public static ObjectClickHandler handleMagicAxeHutChests = new ObjectClickHandler(new Object[] { 2566 }, new WorldTile(3188, 3962, 0), new WorldTile(3189, 3962, 0), new WorldTile(3193, 3962, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getOpNum()) {
				case OBJECT_OP1 -> {
					e.getPlayer().sendMessage("You attempt to open the chest without disarming the traps.");
					e.getPlayer().applyHit(new Hit((int) (e.getPlayer().getSkills().getLevel(Skills.HITPOINTS) + 20), Hit.HitLook.TRUE_DAMAGE));
				}
				case OBJECT_OP2 -> {
					Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 32, 14, 7.5, DropSets.getDropSet("magic_axe_hut_chest"));
				}
				default -> e.getPlayer();
			}
		};
	};

	public static ObjectClickHandler handleKBDEnterLadder = new ObjectClickHandler(new Object[] { 1765 }, new WorldTile(3017, 3849, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(828, new WorldTile(3069, 10255, 0), 1, 2);
		}
	};

	public static ObjectClickHandler handleKBDExitLadder = new ObjectClickHandler(new Object[] { 32015 }, new WorldTile(3069, 10256, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(828, new WorldTile(3017, 3848, 0), 1, 2);
		}
	};

	public static ObjectClickHandler handleKBDEnterLever = new ObjectClickHandler(new Object[] { 1816 }, new WorldTile(3067, 10252, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().stopAll();
			Magic.pushLeverTeleport(e.getPlayer(), new WorldTile(2273, 4681, 0));
			e.getPlayer().getControllerManager().forceStop();
		}
	};

	public static ObjectClickHandler handleBorkExit = new ObjectClickHandler(new Object[] { 29537 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(-1, new WorldTile(3142, 5545, 0));
		}
	};

	public static ObjectClickHandler handleBorkEntrance = new ObjectClickHandler(new Object[] { 28779 },
			new WorldTile(3142, 5545, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("Not implemented...");
//			e.getPlayer().getControllerManager().startController(new BorkController(0, null));
		}
	};

	public static ObjectClickHandler handleChaosTunnelsPortals = new ObjectClickHandler(new Object[] { 28779, 77745 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player player = e.getPlayer();
			int x = e.getObject().getX();
			int y = e.getObject().getY();
			if(x == 3254 && y == 5451)
				player.setNextWorldTile(new WorldTile(3250, 5448, 0));
			if(x == 3250 && y == 5448)
				player.setNextWorldTile(new WorldTile(3254, 5451, 0));
			if(x == 3241 && y == 5445)
				player.setNextWorldTile(new WorldTile(3233, 5445, 0));
			if(x == 3233 && y == 5445)
				player.setNextWorldTile(new WorldTile(3241, 5445, 0));
			if(x == 3259 && y == 5446)
				player.setNextWorldTile(new WorldTile(3265, 5491, 0));
			if(x == 3265 && y == 5491)
				player.setNextWorldTile(new WorldTile(3259, 5446, 0));
			if(x == 3260 && y == 5491)
				player.setNextWorldTile(new WorldTile(3266, 5446, 0));
			if(x == 3266 && y == 5446)
				player.setNextWorldTile(new WorldTile(3260, 5491, 0));
			if(x == 3241 && y == 5469)
				player.setNextWorldTile(new WorldTile(3233, 5470, 0));
			if(x == 3233 && y == 5470)
				player.setNextWorldTile(new WorldTile(3241, 5469, 0));
			if(x == 3235 && y == 5457)
				player.setNextWorldTile(new WorldTile(3229, 5454, 0));
			if(x == 3229 && y == 5454)
				player.setNextWorldTile(new WorldTile(3235, 5457, 0));
			if(x == 3280 && y == 5460)
				player.setNextWorldTile(new WorldTile(3273, 5460, 0));
			if(x == 3273 && y == 5460)
				player.setNextWorldTile(new WorldTile(3280, 5460, 0));
			if(x == 3283 && y == 5448)
				player.setNextWorldTile(new WorldTile(3287, 5448, 0));
			if(x == 3287 && y == 5448)
				player.setNextWorldTile(new WorldTile(3283, 5448, 0));
			if(x == 3244 && y == 5495)
				player.setNextWorldTile(new WorldTile(3239, 5498, 0));
			if(x == 3239 && y == 5498)
				player.setNextWorldTile(new WorldTile(3244, 5495, 0));
			if(x == 3232 && y == 5501)
				player.setNextWorldTile(new WorldTile(3238, 5507, 0));
			if(x == 3238 && y == 5507)
				player.setNextWorldTile(new WorldTile(3232, 5501, 0));
			if(x == 3218 && y == 5497)
				player.setNextWorldTile(new WorldTile(3222, 5488, 0));
			if(x == 3222 && y == 5488)
				player.setNextWorldTile(new WorldTile(3218, 5497, 0));
			if(x == 3218 && y == 5478)
				player.setNextWorldTile(new WorldTile(3215, 5475, 0));
			if(x == 3215 && y == 5475)
				player.setNextWorldTile(new WorldTile(3218, 5478, 0));
			if(x == 3224 && y == 5479)
				player.setNextWorldTile(new WorldTile(3222, 5474, 0));
			if(x == 3222 && y == 5474)
				player.setNextWorldTile(new WorldTile(3224, 5479, 0));
			if(x == 3208 && y == 5471)
				player.setNextWorldTile(new WorldTile(3210, 5477, 0));
			if(x == 3210 && y == 5477)
				player.setNextWorldTile(new WorldTile(3208, 5471, 0));
			if(x == 3214 && y == 5456)
				player.setNextWorldTile(new WorldTile(3212, 5452, 0));
			if(x == 3212 && y == 5452)
				player.setNextWorldTile(new WorldTile(3214, 5456, 0));
			if(x == 3204 && y == 5445)
				player.setNextWorldTile(new WorldTile(3197, 5448, 0));
			if(x == 3197 && y == 5448)
				player.setNextWorldTile(new WorldTile(3204, 5445, 0));
			if(x == 3189 && y == 5444)
				player.setNextWorldTile(new WorldTile(3187, 5460, 0));
			if(x == 3187 && y == 5460)
				player.setNextWorldTile(new WorldTile(3189, 5444, 0));
			if(x == 3192 && y == 5472)
				player.setNextWorldTile(new WorldTile(3186, 5472, 0));
			if(x == 3186 && y == 5472)
				player.setNextWorldTile(new WorldTile(3192, 5472, 0));
			if(x == 3185 && y == 5478)
				player.setNextWorldTile(new WorldTile(3191, 5482, 0));
			if(x == 3191 && y == 5482)
				player.setNextWorldTile(new WorldTile(3185, 5478, 0));
			if(x == 3171 && y == 5473)
				player.setNextWorldTile(new WorldTile(3167, 5471, 0));
			if(x == 3167 && y == 5471)
				player.setNextWorldTile(new WorldTile(3171, 5473, 0));
			if(x == 3171 && y == 5478)
				player.setNextWorldTile(new WorldTile(3167, 5478, 0));
			if(x == 3167 && y == 5478)
				player.setNextWorldTile(new WorldTile(3171, 5478, 0));
			if(x == 3168 && y == 5456)
				player.setNextWorldTile(new WorldTile(3178, 5460, 0));
			if(x == 3178 && y == 5460)
				player.setNextWorldTile(new WorldTile(3168, 5456, 0));
			if(x == 3191 && y == 5495)
				player.setNextWorldTile(new WorldTile(3194, 5490, 0));
			if(x == 3194 && y == 5490)
				player.setNextWorldTile(new WorldTile(3191, 5495, 0));
			if(x == 3141 && y == 5480)
				player.setNextWorldTile(new WorldTile(3142, 5489, 0));
			if(x == 3142 && y == 5489)
				player.setNextWorldTile(new WorldTile(3141, 5480, 0));
			if(x == 3142 && y == 5462)
				player.setNextWorldTile(new WorldTile(3154, 5462, 0));
			if(x == 3154 && y == 5462)
				player.setNextWorldTile(new WorldTile(3142, 5462, 0));
			if(x == 3143 && y == 5443)
				player.setNextWorldTile(new WorldTile(3155, 5449, 0));
			if(x == 3155 && y == 5449)
				player.setNextWorldTile(new WorldTile(3143, 5443, 0));
			if(x == 3307 && y == 5496)
				player.setNextWorldTile(new WorldTile(3317, 5496, 0));
			if(x == 3317 && y == 5496)
				player.setNextWorldTile(new WorldTile(3307, 5496, 0));
			if(x == 3318 && y == 5481)
				player.setNextWorldTile(new WorldTile(3322, 5480, 0));
			if(x == 3322 && y == 5480)
				player.setNextWorldTile(new WorldTile(3318, 5481, 0));
			if(x == 3299 && y == 5484)
				player.setNextWorldTile(new WorldTile(3303, 5477, 0));
			if(x == 3303 && y == 5477)
				player.setNextWorldTile(new WorldTile(3299, 5484, 0));
			if(x == 3286 && y == 5470)
				player.setNextWorldTile(new WorldTile(3285, 5474, 0));
			if(x == 3285 && y == 5474)
				player.setNextWorldTile(new WorldTile(3286, 5470, 0));
			if(x == 3290 && y == 5463)
				player.setNextWorldTile(new WorldTile(3302, 5469, 0));
			if(x == 3302 && y == 5469)
				player.setNextWorldTile(new WorldTile(3290, 5463, 0));
			if(x == 3296 && y == 5455)
				player.setNextWorldTile(new WorldTile(3299, 5450, 0));
			if(x == 3299 && y == 5450)
				player.setNextWorldTile(new WorldTile(3296, 5455, 0));
			if(x == 3280 && y == 5501)
				player.setNextWorldTile(new WorldTile(3285, 5508, 0));
			if(x == 3285 && y == 5508)
				player.setNextWorldTile(new WorldTile(3280, 5501, 0));
			if(x == 3300 && y == 5514)
				player.setNextWorldTile(new WorldTile(3297, 5510, 0));
			if(x == 3297 && y == 5510)
				player.setNextWorldTile(new WorldTile(3300, 5514, 0));
			if(x == 3289 && y == 5533)
				player.setNextWorldTile(new WorldTile(3288, 5536, 0));
			if(x == 3288 && y == 5536)
				player.setNextWorldTile(new WorldTile(3289, 5533, 0));
			if(x == 3285 && y == 5527)
				player.setNextWorldTile(new WorldTile(3282, 5531, 0));
			if(x == 3282 && y == 5531)
				player.setNextWorldTile(new WorldTile(3285, 5527, 0));
			if(x == 3325 && y == 5518)
				player.setNextWorldTile(new WorldTile(3323, 5531, 0));
			if(x == 3323 && y == 5531)
				player.setNextWorldTile(new WorldTile(3325, 5518, 0));
			if(x == 3299 && y == 5533)
				player.setNextWorldTile(new WorldTile(3297, 5536, 0));
			if(x == 3297 && y == 5538)
				player.setNextWorldTile(new WorldTile(3299, 5533, 0));
			if(x == 3321 && y == 5554)
				player.setNextWorldTile(new WorldTile(3315, 5552, 0));
			if(x == 3315 && y == 5552)
				player.setNextWorldTile(new WorldTile(3321, 5554, 0));
			if(x == 3291 && y == 5555)
				player.setNextWorldTile(new WorldTile(3285, 5556, 0));
			if(x == 3285 && y == 5556)
				player.setNextWorldTile(new WorldTile(3291, 5555, 0));
			if(x == 3266 && y == 5552)
				player.setNextWorldTile(new WorldTile(3262, 5552, 0));
			if(x == 3262 && y == 5552)
				player.setNextWorldTile(new WorldTile(3266, 5552, 0));
			if(x == 3256 && y == 5561)
				player.setNextWorldTile(new WorldTile(3253, 5561, 0));
			if(x == 3253 && y == 5561)
				player.setNextWorldTile(new WorldTile(3256, 5561, 0));
			if(x == 3249 && y == 5546)
				player.setNextWorldTile(new WorldTile(3252, 5543, 0));
			if(x == 3252 && y == 5543)
				player.setNextWorldTile(new WorldTile(3249, 5546, 0));
			if(x == 3261 && y == 5536)
				player.setNextWorldTile(new WorldTile(3268, 5534, 0));
			if(x == 3268 && y == 5534)
				player.setNextWorldTile(new WorldTile(3261, 5536, 0));
			if(x == 3243 && y == 5526)
				player.setNextWorldTile(new WorldTile(3241, 5529, 0));
			if(x == 3241 && y == 5529)
				player.setNextWorldTile(new WorldTile(3243, 5526, 0));
			if(x == 3230 && y == 5547)
				player.setNextWorldTile(new WorldTile(3226, 5553, 0));
			if(x == 3226 && y == 5553)
				player.setNextWorldTile(new WorldTile(3230, 5547, 0));
			if(x == 3206 && y == 5553)
				player.setNextWorldTile(new WorldTile(3204, 5546, 0));
			if(x == 3204 && y == 5546)
				player.setNextWorldTile(new WorldTile(3206, 5553, 0));
			if(x == 3211 && y == 5533)
				player.setNextWorldTile(new WorldTile(3214, 5533, 0));
			if(x == 3214 && y == 5533)
				player.setNextWorldTile(new WorldTile(3211, 5533, 0));
			if(x == 3208 && y == 5527)
				player.setNextWorldTile(new WorldTile(3211, 5523, 0));
			if(x == 3211 && y == 5523)
				player.setNextWorldTile(new WorldTile(3208, 5527, 0));
			if(x == 3201 && y == 5531)
				player.setNextWorldTile(new WorldTile(3197, 5529, 0));
			if(x == 3197 && y == 5529)
				player.setNextWorldTile(new WorldTile(3201, 5531, 0));
			if(x == 3202 && y == 5515)
				player.setNextWorldTile(new WorldTile(3196, 5512, 0));
			if(x == 3196 && y == 5512)
				player.setNextWorldTile(new WorldTile(3202, 5515, 0));
			if(x == 3190 && y == 5515)
				player.setNextWorldTile(new WorldTile(3190, 5519, 0));
			if(x == 3190 && y == 5519)
				player.setNextWorldTile(new WorldTile(3190, 5515, 0));
			if(x == 3185 && y == 5518)
				player.setNextWorldTile(new WorldTile(3181, 5517, 0));
			if(x == 3181 && y == 5517)
				player.setNextWorldTile(new WorldTile(3185, 5518, 0));
			if(x == 3187 && y == 5531)
				player.setNextWorldTile(new WorldTile(3182, 5530, 0));
			if(x == 3182 && y == 5530)
				player.setNextWorldTile(new WorldTile(3187, 5531, 0));
			if(x == 3169 && y == 5510)
				player.setNextWorldTile(new WorldTile(3159, 5501, 0));
			if(x == 3159 && y == 5501)
				player.setNextWorldTile(new WorldTile(3169, 5510, 0));
			if(x == 3165 && y == 5515)
				player.setNextWorldTile(new WorldTile(3173, 5530, 0));
			if(x == 3173 && y == 5530)
				player.setNextWorldTile(new WorldTile(3165, 5515, 0));
			if(x == 3156 && y == 5523)
				player.setNextWorldTile(new WorldTile(3152, 5520, 0));
			if(x == 3152 && y == 5520)
				player.setNextWorldTile(new WorldTile(3156, 5523, 0));
			if(x == 3148 && y == 5533)
				player.setNextWorldTile(new WorldTile(3153, 5537, 0));
			if(x == 3153 && y == 5537)
				player.setNextWorldTile(new WorldTile(3148, 5533, 0));
			if(x == 3143 && y == 5535)
				player.setNextWorldTile(new WorldTile(3147, 5541, 0));
			if(x == 3147 && y == 5541)
				player.setNextWorldTile(new WorldTile(3143, 5535, 0));
			if(x == 3168 && y == 5541)
				player.setNextWorldTile(new WorldTile(3171, 5542, 0));
			if(x == 3171 && y == 5542)
				player.setNextWorldTile(new WorldTile(3168, 5541, 0));
			if(x == 3190 && y == 5549)
				player.setNextWorldTile(new WorldTile(3190, 5554, 0));
			if(x == 3190 && y == 5554)
				player.setNextWorldTile(new WorldTile(3190, 5549, 0));
			if(x == 3180 && y == 5557)
				player.setNextWorldTile(new WorldTile(3174, 5558, 0));
			if(x == 3174 && y == 5558)
				player.setNextWorldTile(new WorldTile(3180, 5557, 0));
			if(x == 3162 && y == 5557)
				player.setNextWorldTile(new WorldTile(3158, 5561, 0));
			if(x == 3158 && y == 5561)
				player.setNextWorldTile(new WorldTile(3162, 5557, 0));
			if(x == 3166 && y == 5553)
				player.setNextWorldTile(new WorldTile(3162, 5545, 0));
			if(x == 3162 && y == 5545)
				player.setNextWorldTile(new WorldTile(3166, 5553, 0));
			if(x == 3115 && y == 5528)
				player.setNextWorldTile(new WorldTile(3142, 5545, 0));
		}
	};




	public static ObjectClickHandler handleKBDExitLever = new ObjectClickHandler(new Object[] { 1817 }, new WorldTile(2273, 4680, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			Magic.pushLeverTeleport(e.getPlayer(), new WorldTile(3067, 10254, 0));
			e.getPlayer().getControllerManager().startController(new WildernessController());
		}
	};

	public static ObjectClickHandler handleFireGiantDungeonExit = new ObjectClickHandler(new Object[] { 32048 }, new WorldTile(3043, 10328, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getPlayer().transform(3, -6400, 0));
			e.getPlayer().getControllerManager().startController(new WildernessController());
		}
	};

	public static ObjectClickHandler handleRedDragIsleShortcut = new ObjectClickHandler(new Object[] { 73657 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 54)) {
				e.getPlayer().getPackets().sendGameMessage("You need level 54 agility to use this shortcut.");
				return;
			}
			AgilityShortcuts.forceMovement(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getY() > 3800 ? 1 : -1, e.getPlayer().getY() > 3800 ? -2 : 2), 4721, 1, 1);
		}
	};

	public static ObjectClickHandler handleGWDShortcut = new ObjectClickHandler(new Object[] { 26323, 26324, 26328, 26327 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			WorldObject obj = e.getObject();
			if (!Agility.hasLevel(p, 60)) {
				p.getPackets().sendGameMessage("You need level 60 agility to use this shortcut.");
				return;
			}

			//Wildy
			if(obj.getId() == 26327)
				AgilityShortcuts.forceMovement(p, new WorldTile(2943, 3767, 0), 2049, 1, 1);
			if(obj.getId() == 26328) {
				p.setNextWorldTile(new WorldTile(2943, 3767, 0));
				AgilityShortcuts.forceMovementInstant(p, new WorldTile(2950, 3767, 0), 2050, 1, 1, Direction.WEST);
			}

			//Outside GWD
			if(obj.getId() == 26324)
				AgilityShortcuts.forceMovementInstant(p, new WorldTile(2928, 3757, 0), 2049, 1, 1, Direction.NORTH);
			if(obj.getId() == 26323)
				AgilityShortcuts.forceMovementInstant(p, new WorldTile(2927, 3761, 0), 2050, 1, 1, Direction.NORTH);

		}
	};
}
