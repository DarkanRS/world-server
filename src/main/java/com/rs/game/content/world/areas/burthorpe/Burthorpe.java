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
package com.rs.game.content.world.areas.burthorpe;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import static com.rs.game.content.world.doors.Doors.handleDoubleDoor;

@PluginEventHandler
public class Burthorpe {

    //Cutscenes
    //9 - morningstar jumping off the mountain into a house
    //10 - player ambushes a group of trolls with a group of jewish archers
    //11 - player gets given a weapon and rushes into the cave with ozan
    //12 - player fires cannon that breaks the walls down to cover the cave entrance

    public static ItemClickHandler handleWalkWithBabyTroll = new ItemClickHandler(new Object[] { 23030 }, new String[] { "Walk with" }, e -> e.getPlayer().getPetManager().spawnPet(e.getItem().getId(), true));

    private static Dialogue[] trollDialogues = {
            new Dialogue()
                    .addNPC(14846, HeadE.T_CONFUSED, "Dadda?")
                    .addPlayer(HeadE.CONFUSED, "No, I'm not your dad.")
                    .addNPC(14846, HeadE.T_CONFUSED, "Momma?")
                    .addPlayer(HeadE.FRUSTRATED, "No, I'm not your mother either!")
                    .addNPC(14846, HeadE.T_CONFUSED, "Food?")
                    .addPlayer(HeadE.ANGRY, "I am not food!")
                    .addNPC(14846, HeadE.T_SAD, "I hungry.")
                    .addPlayer(HeadE.LAUGH, "Aren't you always?"),
            new Dialogue()
                    .addNPC(14846, HeadE.T_CONFUSED, "We fight?")
                    .addPlayer(HeadE.LAUGH, "You're too young to fight!")
                    .addNPC(14846, HeadE.T_ANGRY, "I big and scary!")
                    .addPlayer(HeadE.CALM_TALK, "You have to eat a lot more vegetables first.")
                    .addNPC(14846, HeadE.T_SAD, "I no want to be named vegetable."),
            new Dialogue()
                    .addNPC(14846, HeadE.T_CONFUSED, "Sleepy?")
                    .addPlayer(HeadE.LAUGH, "Aww, little baby is sleepy?")
                    .addNPC(14846, HeadE.T_ANGRY, "*Grrr*")
                    .addPlayer(HeadE.LAUGH, "Uuu, I'm sooo scared.")
                    .addNPC(14846, HeadE.T_ANGRY, "I eat you!"),
            new Dialogue()
                    .addNPC(14846, HeadE.T_CONFUSED, "Food?")
                    .addPlayer(HeadE.CONFUSED, "Is that all you think about? Food?")
                    .addNPC(14846, HeadE.T_ANGRY, "Food!")
                    .addPlayer(HeadE.FRUSTRATED, "I'll see what I can find..")
    };

    public static NPCClickHandler talkToBabyTroll = new NPCClickHandler(new Object[] { 14846 }, new String[] { "Talk-to" }, e -> {
        e.getPlayer().startConversation(trollDialogues[Utils.random(trollDialogues.length)]);
    });

    public static ObjectClickHandler handleCaveEntrance = new ObjectClickHandler(new Object[]{66876}, e -> {
        e.getPlayer().setNextTile(Tile.of(2292, 4516, 0));
    });

    public static ObjectClickHandler handleCaveExit = new ObjectClickHandler(new Object[]{67002}, e -> {
        e.getPlayer().setNextTile(Tile.of(2876, 3502, 0));
    });

    public static ObjectClickHandler handleCastleLockedDoor = new ObjectClickHandler(new Object[]{66967}, e -> {
        e.getPlayer().sendMessage("This door is securely locked");
    });

    public static ObjectClickHandler handleCastleLadders = new ObjectClickHandler(new Object[]{66986, 66988}, e -> {
        switch (e.getObjectId()) {
            case 66986 -> {
                e.getPlayer().ladder(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() + 2, 2));
            }
            case 66988 -> {
                e.getPlayer().ladder(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() - 2, 0));
            }
        }
    });

    public static ObjectClickHandler handleCastleStairs = new ObjectClickHandler(new Object[]{66971, 66970, 66972, 66969}, e -> {
        switch (e.getObjectId()) {
            case 66970 -> {
                e.getPlayer().useStairs(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() - 4, 2));
            }
            case 66971, 66969 -> {
                e.getPlayer().useStairs(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() + 4, 1));
            }
            case 66972 -> {
                e.getPlayer().useStairs(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() - 4, 0));
            }
        }
    });

    public static ObjectClickHandler handleHeroesGuildDoors = new ObjectClickHandler(new Object[]{2624, 2625}, e -> {
        if (e.getPlayer().isQuestComplete(Quest.HEROES_QUEST) || e.getPlayer().getX() < e.getObject().getX()) {
            handleDoubleDoor(e.getPlayer(), e.getObject());
            e.getPlayer().getMusicsManager().playSpecificAmbientSong(77, true);
        } else
            e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                int NPC = 796;

                {
                    addNPC(NPC, HeadE.FRUSTRATED, "Hey! Only heroes are allowed in there.");
                    addPlayer(HeadE.SECRETIVE, "Umm, how do I know if I am a hero?");
                    addNPC(NPC, HeadE.HAPPY_TALKING, "By completing the Heroes' Quest of course");
                    addPlayer(HeadE.SAD, "Oh..");
                    create();
                }
            });
    });
}
