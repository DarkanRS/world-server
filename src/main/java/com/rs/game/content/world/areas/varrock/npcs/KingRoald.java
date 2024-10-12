package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.priestinperil.KingRoaldPriestInPerilD;
import com.rs.game.content.quests.shieldofarrav.KingRoaldShieldOfArravD;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class KingRoald {
    public static final int ID = 648;

    public static NPCClickHandler handleKingRoald = new NPCClickHandler(new Object[]{ ID }, e ->  {
        Dialogue showQuests = new Dialogue().addOptions(questOptions -> {
            if (!e.getPlayer().isQuestComplete(Quest.PRIEST_IN_PERIL))
                questOptions.add("About Priest In Peril...", () -> new KingRoaldPriestInPerilD(e.getPlayer()));
            if (!e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
                questOptions.add("About Shield Of Arrav...", new KingRoaldShieldOfArravD(e.getPlayer()).getStart());
//            if (e.getPlayer().isQuestComplete(Quest.PRIEST_IN_PERIL) && !e.getPlayer().isQuestComplete(Quest.ALL_FIRED_UP) && e.getPlayer().getSkills().getLevel(Skills.FIREMAKING) >= 43)
//                options.add("About All Fired Up...", new KingRoaldAllFiredUpD(e.getPlayer()));
            questOptions.add("Farewell.");
        });

        Dialogue showLetter = new Dialogue()
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

        e.getPlayer().startConversation(new Dialogue()
                .addOptions(outerOptions -> {
                    outerOptions.add("Greet the king.", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Hello, your majesty.")
                            .addNPC(ID, HeadE.CHEERFUL, "Hello citizen, what can I do for you?")
                            .addNext(showQuests));
                    if (e.getPlayer().getInventory().containsItem(11010))
                        outerOptions.add("Show him the letter.", showLetter);
                    outerOptions.add("Nevermind.");
                }));
        }
    );
}

