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
package com.rs.game.content.skills.farming;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;
@PluginEventHandler
public class Pineapples extends GameObject {

    public Pineapples(GameObject object, Player player) {
        super(object);
        int x = object.getX();
        int y = object.getY();
        handlePineapple(player, x, y);
    }

    public void handlePineapple(final Player player, int x, int y) {
        String selectedPineapple = String.valueOf(x + y);
        if(player.getTempAttribs().getI(selectedPineapple) >= 6){
            player.sendMessage("There are no pineapples left on this plant");
            return;
        }

        if(player.getTempAttribs().getI(selectedPineapple) == 0);
        player.getTempAttribs().setI(selectedPineapple,Utils.random(1, 5));

        if(player.getTempAttribs().getI(selectedPineapple) == 1){
            if(!player.getInventory().hasFreeSlots()){
                player.simpleDialogue("You do not have enough space.");
                return;
            }
            player.anim(2280);
            player.sendMessage("You pick a Pineapple from the tree");
            player.getInventory().addItem(2114);
            player.getTempAttribs().setI(selectedPineapple,6);
            WorldTasks.schedule(Ticks.fromMinutes(5), () -> {
                try {
                    player.getTempAttribs().setI(selectedPineapple,0);
                } catch (Throwable e) {
                    player.getTempAttribs().setI(selectedPineapple,0);
                }
            });
        }

        else{
            if(!player.getInventory().hasFreeSlots()){
                player.simpleDialogue("You do not have enough space.");
                return;
            }
            player.anim(2280);
            player.sendMessage("You pick a Pineapple from the tree");
            player.getInventory().addItem(2114);
            player.getTempAttribs().setI(selectedPineapple,player.getTempAttribs().getI(selectedPineapple) - 1);
        }
    }

    public static ObjectClickHandler pickPineapple = new ObjectClickHandler(new Object[] { 1408 }, e -> new Pineapples(e.getObject(), e.getPlayer()));

}
