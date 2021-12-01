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
package com.rs.game.player.content.world.regions;

import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.controllers.WildernessController;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Wilderness {
	
	/* Magic axe hut if I decide to add it back
	 * Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 32, 14, 7.5, new DropTable(Rarity.COMMON, 995, 200), new DropTable(Rarity.COMMON, 2297, 1), new DropTable(Rarity.COMMON, 1365, 1), new DropTable(Rarity.COMMON, 1353, 1), new DropTable(Rarity.UNCOMMON, 991, 1), new DropTable(Rarity.UNCOMMON, 1369, 1), new DropTable(Rarity.UNCOMMON, 1355, 1), new DropTable(Rarity.RARE, 1371, 1), new DropTable(Rarity.RARE, 1357, 1), new DropTable(Rarity.RARE, 1373, 1), new DropTable(Rarity.RARE, 1359, 1));
	 */
	
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
            if(obj.getId() == 26327) {
                AgilityShortcuts.forceMovement(p, new WorldTile(2943, 3767, 0), 2049, 1, 1);
            }
            if(obj.getId() == 26328) {
                p.setNextWorldTile(new WorldTile(2943, 3767, 0));
                AgilityShortcuts.forceMovementInstant(p, new WorldTile(2950, 3767, 0), 2050, 1, 1, Direction.WEST);
            }

            //Outside GWD
            if(obj.getId() == 26324) {
                AgilityShortcuts.forceMovementInstant(p, new WorldTile(2928, 3757, 0), 2049, 1, 1, Direction.NORTH);
            }
            if(obj.getId() == 26323) {
                AgilityShortcuts.forceMovementInstant(p, new WorldTile(2927, 3761, 0), 2050, 1, 1, Direction.NORTH);
            }

        }
    };
}
