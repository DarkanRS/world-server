package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.shieldofarrav.KingRoaldShieldOfArravD;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class KingRoald {
    public static final int ID = 648;

    public static NPCClickHandler handleKingRoald = new NPCClickHandler(new Object[] { 648 }, e -> {
        e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
            {
                addOptions(ops -> {
                    if (!e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
                        ops.add("About Shield Of Arrav...", new KingRoaldShieldOfArravD(player).getStart());
                    if (e.getPlayer().getInventory().containsItem(11010))
                        ops.add("Show him the letter.")
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

                    if (!e.getPlayer().isQuestStarted(Quest.PRIEST_IN_PERIL))
                        ops.add("Greet the king.")
                                .addPlayer(HeadE.CHEERFUL, "Greetings, your majesty.")
                                .addNPC(ID, HeadE.CALM_TALK, "Well hello there. What do you want?")
                                .addPlayer(HeadE.CALM_TALK, "I am looking for a quest!")
                                .addNPC(ID, HeadE.CALM_TALK, "A quest you say? Hmm, what an odd request to make of the king. It's funny you should mention it though, as there is something you can do for me.")
                                .addNPC(ID, HeadE.CALM_TALK, "Are you aware of the temple east of here? It stands on the river Salve and guards the entrance to the lands of Morytania?")
                                .addPlayer(HeadE.CONFUSED, "No, I don't think I know it...")
                                .addNPC(ID, HeadE.CALM_TALK, "Hmm, how strange that you don't. Well anyway, it has been some days since I last heard from Drezel, the priest who lives there.")
                                .addNPC(ID, HeadE.CALM_TALK, "Be a sport and go make sure that nothing untoward has happened to the silly old codger for me, would you?")
                                .addQuestStart(Quest.PRIEST_IN_PERIL);

                    ops.add("Nevermind.");
                });
                create();
            }
        });
    });
}
