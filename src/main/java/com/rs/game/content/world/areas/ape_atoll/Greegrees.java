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
package com.rs.game.content.world.areas.ape_atoll;

import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class Greegrees {

//    /**
//     * Edge cases
//     * 1. Taking entity damage ungreegree
//     * 2. Attacking ungree
//     * 3. Magic unavailable upon greegree
//     * 4. Upon teleport and full inventory drops to ground
//     */
//
//    private static final int[] greegrees = new int[]{4024, 4025, 4026, 4027, 4028, 4029, 4030, 4031};
//    public static ItemClickHandler handleGreeGreeEquip = new ItemClickHandler(greegrees) {
//        @Override
//        public void handle(ItemClickEvent e) {
//            switch(e.getOption()) {
//                case "Remove":
//                    Equipment.sendRemove(e.getPlayer(), Equipment.getItemSlot(e.getItem().getId()));
//                    e.getPlayer().getAppearance().transformIntoNPC(-1);
//                    break;
//                case "Hold":
//                    if (e.getPlayer().isEquipDisabled())
//                        return;
//                    if(!Areas.withinArea("greegreeable", e.getPlayer().getChunkId()))
//                        e.getPlayer().sendMessage("You attempt to use the monkey greegree but nothing happens");
//                    Equipment.sendWear(e.getPlayer(), e.getSlotId(), e.getItem().getId());
//                    break;
//            }
//        }
//    };
//
//    public static ItemEquipHandler handleNinjaGreeGree = new ItemEquipHandler(greegrees) {
//        @Override
//        public void handle(ItemEquipEvent e) {
//            int idx = e.getItem().getId()-4024;
//            if(Areas.withinArea("greegreeable", e.getPlayer().getChunkId()))
//                e.getPlayer().getAppearance().transformIntoNPC(1480+idx);//ninja monkey
//        }
//    };
//
//    public static EnterChunkHandler handleRemoveNPCAppearence = new EnterChunkHandler() {
//        @Override
//        public void handle(EnterChunkEvent e) {
//            if (e.getEntity() instanceof Player p && p.hasStarted())
//                switch(p.getAppearance().getTransformedNPC()) {
//                    case 1480://Small ninja
//                    case 1481://Medium ninja
//                    case 1482://Gorilla
//                    case 1483:
//                    case 1484:
//                    case 1485://small zombie
//                    case 1486://medium zombie
//                    case 1487://karamja monkey
//                        if (!Areas.withinArea("greegreeable", e.getPlayer().getChunkId())) {
//                            p.getAppearance().transformIntoNPC(-1);
//                            if(p.getInventory().hasFreeSlots())
//                                p.sendMessage("The monkey greegree wrenches itself from your hand as its power begins to fade.");
//                            else
//                                p.sendMessage("The monkey greegree wrenches itself from your hand and drops to the ground as its power begins to fade.");
//                        }
//                    default:
//                        return;
//                }
//        }
//    };

}
