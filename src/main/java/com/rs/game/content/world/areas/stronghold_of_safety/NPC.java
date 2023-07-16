package com.rs.game.content.world.areas.stronghold_of_safety;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.managers.EmotesManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class NPC {
    private static final int JAILGUARD = 7142;
    private static final int PROFESSOR_HENRY = 7143;

    @ServerStartupEvent
    public static void addLoSOverrides() {
        Entity.addLOSOverrides( 7151, 7152, 7153, 7154, 7155, 7156, 7157 );
    }

    public static NPCClickHandler HandleSoPSGuard = new NPCClickHandler(new Object[]{JAILGUARD}, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(JAILGUARD, HeadE.HAPPY_TALKING, "Can I help you?")
                    .addPlayer(HeadE.CONFUSED, "I hope so. What is this place?")
                    .addNPC(JAILGUARD, HeadE.HAPPY_TALKING, "Above us is the Misthalin Training Centre of Excellence, where young adventurers are taught things that will help keep themselves safe.")
                    .addNPC(JAILGUARD, HeadE.HAPPY_TALKING, "They say that hidden away somewhere here is the entrance to the old jail, which no doubt has fabulous treasures for those willing to search for them.")
                    .addNPC(JAILGUARD, HeadE.HAPPY_TALKING, "Together they’re called the Stronghold of Player Safety, for historical reasons.")
                    .addPlayer(HeadE.CONFUSED, "So what do you do?")
                    .addNPC(JAILGUARD, HeadE.HAPPY_TALKING, "I guard this stairway to make sure that prospective students are ready.")
                    .addNPC(JAILGUARD, HeadE.HAPPY_TALKING, "That’s interesting. Goodbye."));
        }
    });

    public static NPCClickHandler HandleProfessorHenry = new NPCClickHandler(new Object[]{PROFESSOR_HENRY}, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                if (e.getPlayer().getEmotesManager().unlockedEmote(EmotesManager.Emote.SAFETY_FIRST))
                    e.getPlayer().startConversation(new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Hello, Professor.")
                            .addNPC(PROFESSOR_HENRY, HeadE.HAPPY_TALKING, "Ah, hello, " + e.getPlayer().getDisplayName() + ". How goes the adventuring? I trust you are enjoying your adventures?")
                            .addPlayer(HeadE.HAPPY_TALKING, "I am, thanks. I was wondering where the jail block might be.")
                            .addNPC(PROFESSOR_HENRY, HeadE.HAPPY_TALKING, "Ah, yes. Search the cells downstairs and I’m sure you’ll find it easily enough.")
                            .addPlayer(HeadE.CONFUSED, "Okay, thanks.")
                    );
                else
                    e.getPlayer().startConversation(new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Hello!")
                            .addNPC(PROFESSOR_HENRY, HeadE.HAPPY_TALKING, "Good day! Did you want to know about the old jail block? We always need adventurers to keep the cockroaches in check.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Do I need to do your exam?")
                            .addNPC(PROFESSOR_HENRY, HeadE.HAPPY_TALKING, "No, the exam isn’t really for adventurers like you. Are you looking for the jail block?")
                            .addPlayer(HeadE.CONFUSED, "Sure, tell me about the jail.")
                            .addNPC(PROFESSOR_HENRY, HeadE.HAPPY_TALKING, "In the cells downstairs there’s a secret passage into the old prison. I hear that fame and fortune awaits a suitably skilled adventurer.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks, I’ll look into that.")
                    );
            }
        }
    });

    public static NPCClickHandler HandleStudents = new NPCClickHandler(new Object[]{ 7151, 7152, 7153, 7154, 7155, 7156, 7157 }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                e.getNPC().resetDirection();
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(PROFESSOR_HENRY, HeadE.VERY_FRUSTRATED, "No talking during the exam!")
                        .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Sorry, Professor!")
                );
            }
        }
    });
}
