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
package com.rs.game.content.quests.shieldofarrav;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class StravenShieldOfArravD extends Conversation {
    private final int STRAVEN = 644;

    public StravenShieldOfArravD(Player p) {
        super(p);

        if (p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) == ShieldOfArrav.PROVING_LOYALTY_PHOENIX_STAGE) {
            conversationOptions(p);
            return;
        }
        if (ShieldOfArrav.isPhoenixGang(p))
            addNPC(STRAVEN, HeadE.HAPPY_TALKING, "Greetings, fellow gang member.");
        else {
            addPlayer(HeadE.TALKING_ALOT, "What's through that door?");
            addNPC(STRAVEN, HeadE.FRUSTRATED, "Hey! You can't go in there. Only authorised personnel of the VTAM Corporation are allowed beyond this point.");
        }

        conversationOptions(p);
    }

    public StravenShieldOfArravD(Player p, boolean restartingConversation) {
        super(p);
        conversationOptions(p);
    }

    private void conversationOptions(Player p) {
        if (ShieldOfArrav.isStageInPlayerSave(p, ShieldOfArrav.JOINED_BLACK_ARM_STAGE)) {
            addNPC(STRAVEN, HeadE.SECRETIVE, "You REALLY shouldn't be in here...");
            addPlayer(HeadE.FRUSTRATED, "I guess not.");
        } else if (ShieldOfArrav.isPhoenixGang(p))
            addOptions("Select an option:", new Options() {
                @Override
                public void create() {
                    if (p.getInventory().containsItem(759))
                        option("I've heard you've got some cool treasures in this place.", new Dialogue()
                                .addNPC(STRAVEN, HeadE.HAPPY_TALKING, "Oh yeah, we've all stolen some stuff in our time. Those candlesticks down here, for example," +
                                        " were quite a challenge to get out of the palace.")
                                .addPlayer(HeadE.HAPPY_TALKING, "And the shield of Arrav? I heard you got that!")
                                .addNPC(STRAVEN, HeadE.TALKING_ALOT, "Whoa...that's a blast from the past! We stole that years and years ago! We don't even have all the shield any more.")
                                .addNPC(STRAVEN, HeadE.TALKING_ALOT, "About five years ago we had a massive fight in our gang and the shield got broken in half during that " +
                                        "fight. ")
                                .addNPC(STRAVEN, HeadE.TALKING_ALOT, "Shortly after the fight some gang members decided they didn't want to be part of our gang anymore. " +
                                        "So they split off to form their own gang. The Black Arm Gang.")
                                .addNPC(STRAVEN, HeadE.TALKING_ALOT, "On their way out they looted what treasures they could from us - which included " +
                                        "one of the halves of the shield. We've been rivals with the Black Arms ever since.")
                                .addNext(() -> {
                                    p.startConversation(new StravenShieldOfArravD(p, true).getStart());
                                }));
                    else
                        option("I lost my weapons store key.", new Dialogue()
                                .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "I lost my weapons store key.")
                                .addNPC(STRAVEN, HeadE.SKEPTICAL, "Indeed you have.")
                                .addSimple("He hands you another key", () -> {
                                    p.getInventory().addItem(759, 1, true);
                                }));
                    option("Any suggestions for where I can go thieving?", new Dialogue()
                            .addNPC(STRAVEN, HeadE.HAPPY_TALKING, "You can always try the marketplace in Ardougne. LOTS of opportunity there!")
                            .addNext(() -> {
                                p.startConversation(new StravenShieldOfArravD(p, true).getStart());
                            }));
                    option("Where's the Black Arm Gang hideout?", new Dialogue()
                            .addPlayer(HeadE.TALKING_ALOT, "Where's the Black Arm Gang hideout? I wanna go sabotage 'em!")
                            .addNPC(STRAVEN, HeadE.AMAZED_MILD, "That would be a little tricky; their security is pretty good. Not as good as ours, obviously.")
                            .addNPC(STRAVEN, HeadE.AMAZED_MILD, "But still good. If you really want to go there, it's in the alleyway to the west as you come in the " +
                                    "south gate.")
                            .addNPC(STRAVEN, HeadE.TALKING_ALOT, "One of our operatives is often near the alley - a red haired tramp, goes by the name of Charlie. " +
                                    "He may be able to give you some ideas.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks for the help!")
                            .addNext(() -> {
                                p.startConversation(new StravenShieldOfArravD(p, true).getStart());
                            }));
                    option("Farewell.");
                }
            });
        else if (ShieldOfArrav.isStageInPlayerSave(p, ShieldOfArrav.PROVING_LOYALTY_PHOENIX_STAGE)) {
            addNPC(STRAVEN, HeadE.SECRETIVE, "How's your little mission going?");
            if (p.getInventory().containsItem(761)) {
                addPlayer(HeadE.HAPPY_TALKING, "I have the intelligence report!");
                addNPC(STRAVEN, HeadE.CALM_TALK, "Let's see it then.");
                addSimple("You hand over the report. The man reads the report.");
                addNPC(STRAVEN, HeadE.HAPPY_TALKING, "Yes. Yes, this is very good. Very well, then! You can join the Phoenix Gang! I am Straven, one of the gang leaders.");
                addPlayer(HeadE.HAPPY_TALKING, "Nice to meet you.");
                addNPC(STRAVEN, HeadE.HAPPY_TALKING, "You now have access to the inner sanctum of our subterranean hideout, and our weapons supply depot round the " +
                        "front of this building.", () -> {
                    p.getInventory().deleteItem(761, 1);
                    ShieldOfArrav.setStage(p, ShieldOfArrav.JOINED_PHOENIX_STAGE);
					ShieldOfArrav.setGang(p, "Phoenix");
                    p.getInventory().addItem(759, 1);
                });
            } else if (p.getBank().containsItem(761, 1)) {
                addPlayer(HeadE.HAPPY_TALKING, "I have the intelligence report!");
                addNPC(HeadE.CALM_TALK, "Let's see it then.");
                addPlayer(HeadE.WORRIED, "Oh, its in my bank.");
                addNPC(HeadE.CALM_TALK, "I'll wait here.");
            } else {
                addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "I haven’t managed to find the report yet...");
                addNPC(HeadE.SECRETIVE, "You need to kill Jonny the Beard, who should be in the Blue Moon Inn. ...I would guess. Not being a member of " +
                        "the Phoenix Gang and all.");
            }
        } else if (ShieldOfArrav.isStageInPlayerSave(p, ShieldOfArrav.AFTER_BRIBE_BARAEK_STAGE))
            addOptions("Choose an option:", new Options() {
                @Override
                public void create() {
                    option("I know who you are!", new Dialogue()
                            .addPlayer(HeadE.AMAZED_MILD, "I know who you are!")
                            .addNPC(STRAVEN, HeadE.SKEPTICAL, "Really. Well? Who are we then?")
                            .addPlayer(HeadE.TALKING_ALOT, "This is the headquarters of the Phoenix Gang, the most powerful crime syndicate this city has ever seen!")
                            .addNPC(STRAVEN, HeadE.ANGRY, "No, this is a legitimate business run by legitimate businessmen. Supposing we were this 'Phoenix Gang', " +
                                    "however, what would you want with us?")
                            .addOptions("Select an option", new Options() {
                                @Override
                                public void create() {
                                    option("I'd like to offer you my services.", new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "I'd like to offer you my services.")
                                            .addNPC(STRAVEN, HeadE.FRUSTRATED, "You mean you'd like to join the Phoenix Gang? Well, obviously I can't speak for them, " +
                                                    "but the Phoenix Gang doesn't let people join just like that.")
                                            .addNPC(STRAVEN, HeadE.FRUSTRATED, "You can't be too careful, you understand. Generally someone has to prove their loyalty " +
                                                    "before they can join.")
                                            .addPlayer(HeadE.SKEPTICAL_THINKING, "How would I go about doing that?")
                                            .addNPC(STRAVEN, HeadE.FRUSTRATED, "Obviously, I would have no idea about that. Although having said that, a rival gang of ours, " +
                                                    "er, theirs, called the Black Arm Gang is supposedly meeting a contact from Port Sarim today in the Blue Moon Inn. ")
                                            .addNPC(STRAVEN, HeadE.FRUSTRATED, " The Blue Moon Inn is just by the south entrance to this city, and supposedly the name " +
                                                    "of the contact is Jonny the Beard.")
                                            .addNPC(STRAVEN, HeadE.FRUSTRATED, "OBVIOUSLY I know NOTHING about the dealings of the Phoenix Gang, but I bet if SOMEBODY " +
                                                    "were to kill him and bring back his intelligence report, they would be considered loyal enough to join.")
                                            .addPlayer(HeadE.TALKING_ALOT, "I'll get right on it.")
                                            .addNext(() -> {
                                                ShieldOfArrav.setStage(p, ShieldOfArrav.PROVING_LOYALTY_PHOENIX_STAGE);
                                            }));
                                    option("I want nothing. I was just making sure you were them.", new Dialogue()
                                            .addPlayer(HeadE.SCARED, "I want nothing. I was just making sure you were them.")
                                            .addNPC(STRAVEN, HeadE.ANGRY, "Well, then get lost and stop wasting my time. ...if you know what's good for you."));
                                }
                            }));
                    option("How do I get a job with the VTAM corporation?", new Dialogue()
                            .addPlayer(HeadE.SECRETIVE, "How do I get a job with the VTAM corporation?")
                            .addNPC(STRAVEN, HeadE.CALM_TALK, "Get a copy of the Varrock Herald. If we have any positions right now, they'll be advertised " +
                                    "in there.")
                            .addNext(() -> {
                                p.startConversation(new StravenShieldOfArravD(p, true).getStart());
                            }));
                    option("Why not?", new Dialogue()
                            .addPlayer(HeadE.SECRETIVE, "Why not?")
                            .addNPC(STRAVEN, HeadE.SECRETIVE, " Sorry. That's classified information.")
                            .addNext(() -> {
                                p.startConversation(new StravenShieldOfArravD(p, true).getStart());
                            }));
                    option("Farewell.");
                }
            });
        else
            ;
    }

}
