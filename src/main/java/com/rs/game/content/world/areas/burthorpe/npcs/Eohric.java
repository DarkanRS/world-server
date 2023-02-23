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
package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Eohric extends Conversation {
    private static final int npcId = 1080;


    public static NPCClickHandler Eohric = new NPCClickHandler(new Object[]{npcId}, e -> {
        switch (e.getOption()) {
            //Start Conversation
            case "Talk-to" -> e.getPlayer().startConversation(new Eohric(e.getPlayer()));
        }
    });

    public Eohric(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Hello. Can I help?");

        addOptions(new Options() {
            @Override
            public void create() {

                option("What is this Place?", new Dialogue()
                        .addPlayer(HeadE.CONFUSED, "What is this Place?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "This is Burthorpe Castle, home to His Royal Highness Prince Anlaf, heir to the throne of Asgarnia.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "No doubt you're impressed.")
                        .addOptions(new Options() {
                            @Override
                            public void create() {
                                option("Where is the prince?", new Dialogue()
                                        .addNPC(npcId, HeadE.SHAKING_HEAD, ": I cannot disclose the prince's exact whereabouts for fear of compromising his personal safety.")
                                        .addNPC(npcId, HeadE.HAPPY_TALKING, ": But rest assured that he is working tirelessly to maintain the safety and wellbeing of Burthorpe's people.")
                                );

                                option("Goodbye.", new Dialogue()
                                        .addPlayer(HeadE.HAPPY_TALKING, "Goodbye")
                                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Goodbye.")
                                );
                            }
                        })
                );

                option("That's quite an outfit.", new Dialogue()
                        .addPlayer(HeadE.CONFUSED, "That's quite an outfit.")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Make them I did, with magics.")
                        .addPlayer(HeadE.CONFUSED, "Magic, in the Warrior's Guild?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "A skilled warrior also am I. Harallak mistakes does not make. Potential in my invention he sees and opprotunity grasps.")
                        .addPlayer(HeadE.CHEERFUL, "I see, so you made the magical machines and Harrallak saw how they could be used in the guild to train warrior's combat... interesting. Harrallak certainly is an intellegent guy.")
                );

                option("Goodbye.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Goodbye")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Goodbye.")
                );

            }


        });
    }
}
