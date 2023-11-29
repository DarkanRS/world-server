package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
//import com.rs.game.content.quests.allfiredup.KingRoaldAllFiredUpD;
import com.rs.game.content.quests.priestinperil.KingRoaldPriestInPerilD;
import com.rs.game.content.quests.shieldofarrav.KingRoaldShieldOfArravD;
import com.rs.game.model.entity.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class KingRoald {
    public static final int ID = 648;

    public static NPCClickHandler handleKingRoald = new NPCClickHandler(new Object[]{ ID }, e -> e.getPlayer().startConversation(new Dialogue()
            .addOptions(ops -> {
                ops.add("Greet the king.")
                        .addNext(() -> {
                            if (!e.getPlayer().isQuestComplete(Quest.PRIEST_IN_PERIL)) {
                                new KingRoaldPriestInPerilD(e.getPlayer());
                                return;
                            }
                            if (e.getPlayer().isQuestComplete(Quest.PRIEST_IN_PERIL) && !e.getPlayer().isQuestStarted(Quest.ALL_FIRED_UP)) {
                                if (e.getPlayer().getSkills().getLevel(Skills.FIREMAKING) >= 43)
                                    e.getPlayer().startConversation(new Dialogue()
                                            .addNPC(ID, HeadE.CHEERFUL, "Well hello there. What do you want? Ah, it's you again. Hello there.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Hello, Your Majesty. I am happy to report that the situation at the Temple of Paterdomus has been sorted. Misthalin's borders should once again be fully protected against the threats from Morytania.")
                                            .addNPC(ID, HeadE.HAPPY_TALKING, "Excellent, excellent. The Kingdom of Misthalin is in your debt.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "In my debt? Does that mean you're going to give me fabulous rewards for my efforts?")
                                            .addNPC(ID, HeadE.LAUGH, "Of course not; however, if it's rewards you're after, it occurs to me that you could be of even more service to the kingdom...and this time, there's payment in it for you.")
                                            .addOptions(ops2 -> {
                                                //ops2.add(("About the Beacon network..."), () -> new KingRoaldAllFiredUpD(e.getPlayer()));
                                                ops2.add("Farewell.");
                                            }));
                                else
                                    e.getPlayer().startConversation(new Dialogue()
                                            .addNPC(ID, HeadE.CHEERFUL, "Well hello there. What do you want? Ah, it's you again. Hello there.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Hello, Your Majesty. I am happy to report that the situation at the Temple of Paterdomus has been sorted. Misthalin's borders should once again be fully protected against the threats from Morytania.")
                                            .addNPC(ID, HeadE.HAPPY_TALKING, "Excellent, excellent. The Kingdom of Misthalin is in your debt.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "In my debt? Does that mean you're going to give me fabulous rewards for my efforts?")
                                            .addNPC(ID, HeadE.LAUGH, "Of course not; however, if it's rewards you're after, it occurs to me that you could be of even more service to the kingdom...and this time, there's payment in it for you.")
                                            .addNPC(ID, HeadE.SHAKING_HEAD, "Although, you might need a little more experience tending fires, first. If you are still interested, come and see me once you've had a little more practice.")
                                    );
                            } else {
                                e.getPlayer().startConversation(new Dialogue()
                                        .addNPC(ID, HeadE.CHEERFUL, "Well hello there. What do you want? Ah, it's you again. Hello there.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Hello, Your Majesty. I am happy to report that the situation at the Temple of Paterdomus has been sorted. Misthalin's borders should once again be fully protected against the threats from Morytania.")
                                        .addNPC(ID, HeadE.HAPPY_TALKING, "Excellent, excellent. The Kingdom of Misthalin is in your debt.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "In my debt? Does that mean you're going to give me fabulous rewards for my efforts?")
                                        .addNPC(ID, HeadE.LAUGH, "Of course not; however, if it's rewards you're after, it occurs to me that you could be of even more service to the kingdom...and this time, there's payment in it for you.")
                                        .addOptions(ops2 -> {
                                            //if (!e.getPlayer().isQuestComplete(Quest.ALL_FIRED_UP))
                                                //ops2.add(("About the Beacon network..."), () -> new KingRoaldAllFiredUpD(e.getPlayer()));
                                            if (!e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
                                                ops2.add("About Shield Of Arrav...", new KingRoaldShieldOfArravD(e.getPlayer()).getStart());
                                            if (e.getPlayer().getInventory().containsItem(11010))
                                                ops2.add("Show him the letter.")
                                                        .addPlayer(HeadE.CALM_TALK, "Your majesty, I think that you should see this letter.")
                                                        .addNPC(ID, HeadE.CALM_TALK, "Letter? Let me see. Where's Postie Pete? He usually deals with the mail around here.")
                                                        .addPlayer(HeadE.CALM_TALK, "This letter was delivered to me by hand, your majesty. I think you may be in some danger.")
                                                        .addNPC(ID, HeadE.CALM_TALK, "Hmmm. I see. I appreciate your concern. However, I assure you, I am quite safe here. My guards are on full alert at all times.")
                                                        .addPlayer(HeadE.CALM_TALK, "I don't think you understand...")
                                                        .addNPC(ID, HeadE.CALM_TALK, "I understand perfectly. Now, for all I know, that letter could be a fake ruse. We get a lot of that sort of thing and it causes a lot of hassle.")
                                                        .addPlayer(HeadE.ANGRY, "I'm trying to save your life!")
                                                        .addNPC(ID, HeadE.ANGRY, "And you are shouting at your king! People who yell at their monarchs often find their heads become...loose!")
                                                        .addPlayer(HeadE.CALM_TALK, "I'm just saying that the letter is real.")
                                                        .addNPC(ID, HeadE.CONFUSED, "Then why do you have it?")
                                                        .addPlayer(HeadE.CONFUSED, "Because I'm supposed to deliv...oh.")
                                                        .addNPC(ID, HeadE.CALM_TALK, "It seems to me that if that letter were genuine, I would be forced to arrest its bearer for treason. You understand.")
                                                        .addNPC(ID, HeadE.CONFUSED, "So what do you say?")
                                                        .addPlayer(HeadE.SAD_MILD, "I...er...reckon it's a fake! ...I guess...")
                                                        .addNPC(ID, HeadE.CHEERFUL, "Just as I thought! Now off you go!");
                                            ops2.add("Farewell.");
                                        })
                                );
                            }
                        });
                ops.add("Nevermind.");
            }))
    );
}
