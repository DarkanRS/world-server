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
package com.rs.game.content.world.areas.uncategorizeddumpfix;


import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Uncategorizeddumpfixes {
    //Temple of Light
    public static ObjectClickHandler handletempleoflightstairswide = new ObjectClickHandler(new Object[]{10015, 10016}, e -> {
        if (e.getObjectId() == 10015)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 4 : e.getObject().getRotation() == 3 ? -4 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 2 ? -4 : 0, 1));
        else if (e.getObjectId() == 10016)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -4 : e.getObject().getRotation() == 3 ? 4 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 2 ? 4 : 0, -1));
    });

    public static ObjectClickHandler handletempleoflightstairsthin = new ObjectClickHandler(new Object[]{10018, 10017}, e -> {
        if (e.getObjectId() == 10018)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -4 : e.getObject().getRotation() == 3 ? 4 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 2 ? 4 : 0, -1));
        else if (e.getObjectId() == 10017)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 4 : e.getObject().getRotation() == 3 ? -4 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 2 ? -4 : 0, 1));
    });
    //Citharede
    public static ObjectClickHandler handlewinchladderup = new ObjectClickHandler(new Object[]{63591}, e -> {
        e.getPlayer().useLadder(Tile.of(3415, 3159, 1));
    });

    public static ObjectClickHandler handlewinchladderdown = new ObjectClickHandler(new Object[]{63592}, e -> {
        e.getPlayer().setNextTile(Tile.of(3415, 3161, 0));
    });

    public static ObjectClickHandler handlespiraltowerup = new ObjectClickHandler(new Object[]{63583}, e -> {
        e.getPlayer().setNextTile(Tile.of(3449, 3174, 1));
    });

    public static ObjectClickHandler handlespiraltowerdown = new ObjectClickHandler(new Object[]{63584}, e -> {
        e.getPlayer().setNextTile(Tile.of(3446, 3177, 0));
    });

    public static ObjectClickHandler handlespiraltowerup2 = new ObjectClickHandler(new Object[]{63585}, e -> {
        e.getPlayer().setNextTile(Tile.of(3447, 3179, 2));
    });

    public static ObjectClickHandler handlespiraltowerdown2 = new ObjectClickHandler(new Object[]{63586}, e -> {
        e.getPlayer().setNextTile(Tile.of(3451, 3176, 1));
    });

    public static ObjectClickHandler handlespiraltowerup3 = new ObjectClickHandler(new Object[]{63587}, e -> {
        e.getPlayer().setNextTile(Tile.of(3447, 3179, 3));
    });

    public static ObjectClickHandler handlespiraltowerdown3 = new ObjectClickHandler(new Object[]{63588}, e -> {
        e.getPlayer().setNextTile(Tile.of(3450, 3175, 2));
    });


    public static ObjectClickHandler handlepirateshipladders = new ObjectClickHandler(new Object[]{16945, 16946, 16947}, e -> {
        if (e.getObjectId() == 16945)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, e.getObject().getRotation() == 3 ? 3 : e.getObject().getRotation() == 1 ? -3 : 0, 1));
        else if (e.getObjectId() == 16947)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, e.getObject().getRotation() == 3 ? -3 : e.getObject().getRotation() == 1 ? 3 : 0, -1));
        else if (e.getObjectId() == 16946)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? 3 : 0, e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, 1));

    });
    //Observatory goblin kitchen dungeon-Note the stairs(25429) at (2335, 9350, 0) south of the ruin one is supposed to be a cutscene leading to the end of the quest but shares an ID
    public static ObjectClickHandler handlegoblinladderup = new ObjectClickHandler(new Object[]{25429}, e -> {
        e.getPlayer().setNextTile(Tile.of(2458, 3185, 0));
    });

    public static ObjectClickHandler handlegoblinladderdown = new ObjectClickHandler(new Object[]{25432}, e -> {
        e.getPlayer().setNextTile(Tile.of(2335, 9394, 0));
    });

    public static ObjectClickHandler handleObservatoryladderup = new ObjectClickHandler(new Object[]{25431}, e -> {
        e.getPlayer().setNextTile(Tile.of(2443, 3158, 1));
    });

    public static ObjectClickHandler handleObservatoryladderdown = new ObjectClickHandler(new Object[]{25437}, e -> {
        e.getPlayer().setNextTile(Tile.of(2444, 3162, 0));
    });
    //West Ardougne
    public static ObjectClickHandler handlewestardougnespiralstairs = new ObjectClickHandler(new Object[]{34388, 34390}, e -> {
        if (e.getObjectId() == 34388)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, e.getObject().getRotation() == 3 ? 3 : e.getObject().getRotation() == 1 ? -3 : 0, 1));
        else if (e.getObjectId() == 34390)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, e.getObject().getRotation() == 3 ? -3 : e.getObject().getRotation() == 1 ? 3 : 0, -1));
    });

    public static ObjectClickHandler handlewestardougnestairs = new ObjectClickHandler(new Object[]{34397, 34398}, e -> {
        if (e.getObjectId() == 34397)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 4 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, 1));
        else if (e.getObjectId() == 34398)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 4 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, -1));
    });

    public static ObjectClickHandler handlehadleyspiralstairs = new ObjectClickHandler(new Object[]{1738, 1740}, e -> {
        if (e.getObjectId() == 1738)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? 0 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 4 ? 0 : e.getObject().getRotation() == 1 ? -0 : 0, 1));
        else if (e.getObjectId() == 1740)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? -0 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 4 ? -0 : e.getObject().getRotation() == 1 ? -0 : 0, -1));
    });

    public static ObjectClickHandler handlehadleystairsup = new ObjectClickHandler(new Object[]{2536}, e -> {
        e.getPlayer().setNextTile(Tile.of(2517, 3426, 1));
    });

    public static ObjectClickHandler handlehadleystairsdown = new ObjectClickHandler(new Object[]{2535}, e -> {
        e.getPlayer().setNextTile(Tile.of(2516, 3423, 0));
    });

    //Carnillean
    public static ObjectClickHandler handlecarnilleanstairsup = new ObjectClickHandler(new Object[]{73418}, e -> {
        e.getPlayer().setNextTile(Tile.of(2569, 3267, 1));
    });

    public static ObjectClickHandler handlecarnilleanstairsdown = new ObjectClickHandler(new Object[]{73419}, e -> {
        e.getPlayer().setNextTile(Tile.of(2568, 3269, 0));
    });

    public static ObjectClickHandler handlecarnilleankitchenstairsup = new ObjectClickHandler(new Object[]{34829}, e -> {
        e.getPlayer().setNextTile(Tile.of(2569, 3268, 0));
    });

    public static ObjectClickHandler handlecarnilleankitchenstairsdown = new ObjectClickHandler(new Object[]{36703}, e -> {
        e.getPlayer().setNextTile(Tile.of(2569, 9668, 0));
    });
    //Fight Arena
    public static ObjectClickHandler handlefightarenastairs = new ObjectClickHandler(new Object[]{41121, 41122}, e -> {
        if (e.getObjectId() == 41121)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 4 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, 1));
        else if (e.getObjectId() == 41122)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 4 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, -1));
    });
    //Clocktower
    public static ObjectClickHandler handleclocktowerspiralstairs = new ObjectClickHandler(new Object[]{21871, 21872}, e -> {
        if (e.getObjectId() == 21871)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -2 : e.getObject().getRotation() == 1 ? -2 : 0, e.getObject().getRotation() == 0 ? -2 : e.getObject().getRotation() == 1 ? 2 : 0, 1));
        else if (e.getObjectId() == 21872)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 2 : e.getObject().getRotation() == 1 ? 2 : 0, e.getObject().getRotation() == 0 ? 2 : e.getObject().getRotation() == 1 ? -2 : 0, -1));
    });

    public static ObjectClickHandler handleclocktowerandMeiyerditchstairs = new ObjectClickHandler(new Object[]{17974, 17975}, e -> {
        if (e.getObjectId() == 17974)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 2 : e.getObject().getRotation() == 3 ? -2 : 0, e.getObject().getRotation() == 2 ? -2 : e.getObject().getRotation() == 0 ? 2 : 0, 1));
        else if (e.getObjectId() == 17975)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -2 : e.getObject().getRotation() == 3 ? 2 : 0, e.getObject().getRotation() == 2 ? 2 : e.getObject().getRotation() == 0 ? -2 : 0, -1));
    });

    //Dorgeshuun
    public static ObjectClickHandler handleDorgeshuunboilerstairs = new ObjectClickHandler(new Object[]{22651, 22650}, e -> {
        if (e.getObjectId() == 22651)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 3 ? -0 : 0, -1));
        else if (e.getObjectId() == 22650)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 3 ? 3 : 0, 1));
    });
    public static ObjectClickHandler handleDorgeshuunboilerstairs2 = new ObjectClickHandler(new Object[]{22608, 22609}, e -> {
        if (e.getObjectId() == 22608)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -0 : e.getObject().getRotation() == 0 ? -3 : 0, e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 0 ? -0 : 0, 1));
        else if (e.getObjectId() == 22609)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 1 ? 0 : e.getObject().getRotation() == 0 ? 3 : 0, -1));
    });

    //Legend's Guild
    public static ObjectClickHandler handleLegendsGuildstairs = new ObjectClickHandler(new Object[]{41435, 41436}, e -> {
        if (e.getObjectId() == 41435)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 3 ? -0 : 0, 1));
        else if (e.getObjectId() == 41436)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 3 ? 3 : 0, -1));
    });
    //Ape Atoll
    public static ObjectClickHandler handlefallingdowncratecave = new ObjectClickHandler(new Object[]{4714}, e -> {
        e.getPlayer().setNextTile(Tile.of(2803, 9170, 0));
    });
    public static ObjectClickHandler handlebambooladderbridge = new ObjectClickHandler(new Object[]{4743}, e -> {
        e.getPlayer().setNextTile(Tile.of(2803, 2725, 0));
    });
    public static ObjectClickHandler handlebambooladder = new ObjectClickHandler(new Object[]{4773, 4779}, e -> {
        if (e.getObjectId() == 4773)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -2 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 3 ? -2 : 0, 1));
        else if (e.getObjectId() == 4779)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 2 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 3 ? 2 : 0, -1));
    });
    public static ObjectClickHandler handleclimbingropeup = new ObjectClickHandler(new Object[]{4728}, e -> {
        e.getPlayer().setNextTile(Tile.of(2765, 2768, 0));
    });

    public static ObjectClickHandler handleclimbingropeup2 = new ObjectClickHandler(new Object[]{4889}, e -> {
        e.getPlayer().setNextTile(Tile.of(2748, 2767, 0));
    });

    //brimhaven
    public static ObjectClickHandler handlebrimhavenstairs = new ObjectClickHandler(new Object[]{45, 46}, e -> {
        if (e.getObjectId() == 45)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? -0 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 2 ? -4 : e.getObject().getRotation() == 0 ? 4 : 0, 1));
        else if (e.getObjectId() == 46)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? 0 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 2 ? 4 : e.getObject().getRotation() == 0 ? -4 : 0, -1));
    });
    //Fisher Realm
    public static ObjectClickHandler handlefisherkingstairs = new ObjectClickHandler(new Object[]{1730, 1731}, e -> {
        if (e.getObjectId() == 1730)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 3 ? -0 : 0, 1));
        else if (e.getObjectId() == 1731)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 3 ? 3 : 0, -1));

    });
    //keldagrim
    public static ObjectClickHandler handlekeldagrimstairs = new ObjectClickHandler(new Object[]{6085, 6086}, e -> {
        if (e.getObjectId() == 6085)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? 3 : 0, 1));
        else if (e.getObjectId() == 6086)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 2 ? 3 : e.getObject().getRotation() == 0 ? -3 : 0, -1));
    });
    public static ObjectClickHandler handlekeldagrimpalacestairs = new ObjectClickHandler(new Object[]{6089, 6090}, e -> {
        if (e.getObjectId() == 6089)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? -3 : e.getObject().getRotation() == 1 ? 3 : 0, e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 1 ? -0 : 0, 1));
        else if (e.getObjectId() == 6090)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? 3 : e.getObject().getRotation() == 1 ? -3 : 0, e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 1 ? -0 : 0, -1));
    });
    public static ObjectClickHandler handlekeldagrimwidestairs = new ObjectClickHandler(new Object[]{6087, 6088}, e -> {
        if (e.getObjectId() == 6087)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? 0 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? -0 : 0, 1));
        else if (e.getObjectId() == 6088)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? -0 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 2 ? 3 : e.getObject().getRotation() == 0 ? -0 : 0, -1));
    });
    public static ObjectClickHandler handleboatmanladderup = new ObjectClickHandler(new Object[]{45005}, e -> {
        e.getPlayer().setNextTile(Tile.of(2871, 10176, 1));
    });

    public static ObjectClickHandler handleboatmanladderdown = new ObjectClickHandler(new Object[]{45006}, e -> {
        e.getPlayer().setNextTile(Tile.of(2871, 10173, 0));
    });

    public static ObjectClickHandler handleboatmanladderup2 = new ObjectClickHandler(new Object[]{45007}, e -> {
        e.getPlayer().setNextTile(Tile.of(2873, 10173, 2));
    });
    //Zemouregal's fort
    public static ObjectClickHandler handleZemouregalstairs = new ObjectClickHandler(new Object[]{44253, 44255}, e -> {
        if (e.getObjectId() == 44253)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? -3 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 0 ? 3 : 0, 1));
        else if (e.getObjectId() == 44255)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? 3 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 0 ? -3 : 0, -1));
    });
    public static ObjectClickHandler handleZemouregalrightstairsuponly = new ObjectClickHandler(new Object[]{44254}, e -> {
        e.getPlayer().setNextTile(Tile.of(2836, 3868, 1));
    });
    //Nora T. Hagg House
    public static ObjectClickHandler handlenorathaggstairs = new ObjectClickHandler(new Object[]{24672, 24673}, e -> {
        if (e.getObjectId() == 24672)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 1 ? -0 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 1 ? -0 : 0, 1));
        else if (e.getObjectId() == 24673)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 1 ? -0 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 1 ? -0 : 0, -1));
    });

    //Frozen Waste
    public static ObjectClickHandler handlefrozenwastestairs = new ObjectClickHandler(new Object[]{47142, 47144}, e -> {
        if (e.getObjectId() == 47142)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 0 ? -1 : 0, e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 0 ? -4 : 0, 1));
        else if (e.getObjectId() == 47144)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 0 ? 1 : 0, e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 0 ? 4 : 0, -1));
    });
    public static ObjectClickHandler handlefrozenwasterightstairsuponly = new ObjectClickHandler(new Object[]{47143}, e -> {
        e.getPlayer().setNextTile(Tile.of(2921, 3931, 1));
    });
    //falador
    public static ObjectClickHandler handlefaladorstairs = new ObjectClickHandler(new Object[]{11736, 11737}, e -> {
        if (e.getObjectId() == 11736)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 0 ? 4 : 0, 1));
        else if (e.getObjectId() == 11737)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 0 ? -4 : 0, -1));
    });
    //Mage Training Arena
    public static ObjectClickHandler handlemagetrainladderup = new ObjectClickHandler(new Object[]{10775}, e -> {
        e.getPlayer().setNextTile(Tile.of(3357, 3307, 1));
    });

    public static ObjectClickHandler handlemagetrainladderdown = new ObjectClickHandler(new Object[]{10776}, e -> {
        e.getPlayer().setNextTile(Tile.of(3360, 3306, 0));
    });

    public static ObjectClickHandler handlemagetrainladderup2 = new ObjectClickHandler(new Object[]{10771}, e -> {
        e.getPlayer().setNextTile(Tile.of(3369, 3307, 1));
    });

    public static ObjectClickHandler handlemagetrainladderdown2 = new ObjectClickHandler(new Object[]{10773}, e -> {
        e.getPlayer().setNextTile(Tile.of(3367, 3306, 0));
    });

    public static ObjectClickHandler handlerunemechanicup = new ObjectClickHandler(new Object[]{528}, e -> {
        e.getPlayer().setNextTile(Tile.of(3358, 3305, 0));
    });

    public static ObjectClickHandler handlerunemechanicdown = new ObjectClickHandler(new Object[]{527}, e -> {
        e.getPlayer().setNextTile(Tile.of(3619, 4814, 0));
    });
    //Templebarriermort
    public static ObjectClickHandler handleTemplespiralstairsup = new ObjectClickHandler(new Object[]{30722}, e -> {
        e.getPlayer().setNextTile(Tile.of(3415, 3485, 1));
    });

    public static ObjectClickHandler handleTemplespiralstairsdown = new ObjectClickHandler(new Object[]{30723}, e -> {
        e.getPlayer().setNextTile(Tile.of(3414, 3486, 0));
    });

    public static ObjectClickHandler handleTemplespiralstairsup2 = new ObjectClickHandler(new Object[]{30724}, e -> {
        e.getPlayer().setNextTile(Tile.of(3415, 3492, 1));
    });

    public static ObjectClickHandler handleTemplespiralstairsdown2 = new ObjectClickHandler(new Object[]{30725}, e -> {
        e.getPlayer().setNextTile(Tile.of(3414, 3491, 0));
    });
    //Fenkenstraincastle
    public static ObjectClickHandler handleFenkenstraincastlestairs = new ObjectClickHandler(new Object[]{5206, 5207}, e -> {
        if (e.getObjectId() == 5206)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 1 ? -0 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 1 ? -0 : 0, 1));
        else if (e.getObjectId() == 5207)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 1 ? -0 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 1 ? -0 : 0, -1));
    });
    public static ObjectClickHandler experimentcavegraveentrance = new ObjectClickHandler(new Object[]{5167}, e -> {
        e.getPlayer().setNextTile(Tile.of(3577, 9927, 0));
    });
    public static ObjectClickHandler experimentcavegraveexit = new ObjectClickHandler(new Object[]{1757}, e -> {
        e.getPlayer().setNextTile(Tile.of(3578, 3527, 0));
    });

    //Meiyerditch
    public static ObjectClickHandler handleMeiyerditchstairs = new ObjectClickHandler(new Object[]{17976, 17978}, e -> {
        if (e.getObjectId() == 17976)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? 3 : 0, 1));
        else if (e.getObjectId() == 17978)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 2 ? 3 : e.getObject().getRotation() == 0 ? -3 : 0, -1));
    });
    //Harmony
    public static ObjectClickHandler handleharmonystairs = new ObjectClickHandler(new Object[]{22247, 22253}, e -> {
        if (e.getObjectId() == 22247)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? 3 : 0, 1));
        else if (e.getObjectId() == 22253)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 2 ? 3 : e.getObject().getRotation() == 0 ? -3 : 0, -1));
    });
    public static ObjectClickHandler handleharmonystairs2 = new ObjectClickHandler(new Object[]{22248, 22254}, e -> {
        if (e.getObjectId() == 22248)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? 3 : 0, 1));
        else if (e.getObjectId() == 22254)
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 2 ? 3 : e.getObject().getRotation() == 0 ? -3 : 0, -1));
    });
}
