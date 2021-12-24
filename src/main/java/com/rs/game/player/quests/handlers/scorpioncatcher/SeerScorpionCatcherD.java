package com.rs.game.player.quests.handlers.scorpioncatcher;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher.*;

@PluginEventHandler
public class SeerScorpionCatcherD extends Conversation {
    private final static int NPC = 388;
    //Only during Look for scorpion stage...
    public SeerScorpionCatcherD(Player p) {
        super(p);
        if(ScorpionCatcher.hasLostCage(p)) {
            addNPC(NPC, HeadE.CALM_TALK, "Thormac has your cage.");
            addPlayer(HeadE.FRUSTRATED, "Oh...");
        }

        GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER);
        if(!attr.getB(HAS_SEER1_PROHPECY_ATTR)) {
            addNPC(NPC, HeadE.CALM_TALK, "Many greetings");
            addPlayer(HeadE.HAPPY_TALKING, "Your friend Thormac sent me to speak to you.");
            addNPC(NPC, HeadE.CALM_TALK, "What does the old fellow want?");
            addPlayer(HeadE.HAPPY_TALKING, "He's lost his valuable lesser Kharid scorpions.");
            addNPC(NPC, HeadE.CALM_TALK, "Well you have come to the right place. I am a master of animal detection. Let me look into my looking glass.");
            addSimple("The seer produces a small mirror");
            addSimple("The seer gazes into the mirror");
            addSimple("The seer smoothes his hair with his hand.");
            addNPC(NPC, HeadE.CALM_TALK, "I can see a scorpion that you seek. It resides in a dark place, between a lake and a holy island. There by the " +
                    "entrance shall you find it. So close and yet so far.", ()->{
                p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setB(HAS_SEER1_PROHPECY_ATTR, true);
            });
            addPlayer(HeadE.HAPPY_TALKING, "That was cryptic. Can't you just tell me where it is?");
            addNPC(NPC, HeadE.CALM_TALK, "Where would be the fun in that?");
        } else if(attr.getI(SCORP_COUNT_ATTR) == 0) {
            addNPC(NPC, HeadE.CALM_TALK, "Got your first scorpion yet?");
            addPlayer(HeadE.HAPPY_TALKING, "Not yet...");
        } else if(!attr.getB(HAS_SEER2_PROHPECY_ATTR)) {
            addPlayer(HeadE.HAPPY_TALKING, "Hi, I've retrieved the first scorpion.");
            addNPC(NPC, HeadE.CALM_TALK, "Well, I've checked my looking glass. The second Kharid Scorpion has been spirited away by a brute of a man." +
                    " He runs a shop in a village two canoe trips from Lumbridge.");
            addNPC(NPC, HeadE.CALM_TALK, "That's all I can tell you about that scorpion.");
            addPlayer(HeadE.HAPPY_TALKING, "Any more scorpions?");
            addNPC(NPC, HeadE.CALM_TALK, "It's good that you should ask. I have information on the last scorpion for you. It seems to be in some sort of " +
                    "upstairs room. There's some brown clothing lying on a table nearby.");
            addPlayer(HeadE.HAPPY_TALKING, "Oh come on now! Brown clothing? That's not a clue!");
            addNPC(NPC, HeadE.CALM_TALK, "Alright alright! The clothing is adorned with a golden four-pointed star. Happy now?");
            addPlayer(HeadE.HAPPY_TALKING, "Not really.");
            addNPC(NPC, HeadE.CALM_TALK, "Fine! You should go to where monks reside.");
            addPlayer(HeadE.HAPPY_TALKING, "Up a tree?");
            addNPC(NPC, HeadE.FRUSTRATED, "MONKS! NOT MONKEYS!!!!", ()->{
                p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).setB(HAS_SEER2_PROHPECY_ATTR, true);
            });
        } else if(attr.getI(SCORP_COUNT_ATTR) == 1) {
            addNPC(NPC, HeadE.CALM_TALK, "Got your second scorpion yet?");
            addPlayer(HeadE.HAPPY_TALKING, "Not yet...");
        } else if(attr.getI(SCORP_COUNT_ATTR) == 2) {
            addNPC(NPC, HeadE.CALM_TALK, "Got your third scorpion yet?");
            addPlayer(HeadE.HAPPY_TALKING, "Not yet...");
        } else {
            addPlayer(HeadE.HAPPY_TALKING, "I got all the scorpions!");
            addNPC(NPC, HeadE.CALM_TALK, "Great!");
        }
    }
}
