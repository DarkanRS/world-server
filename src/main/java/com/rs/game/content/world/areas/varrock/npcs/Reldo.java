package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.knightssword.KnightsSword;
import com.rs.game.content.quests.knightssword.ReldoKnightsSwordD;
import com.rs.game.content.quests.shieldofarrav.ReldoShieldOfArravD;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Reldo {
    public static NPCClickHandler handleReldo = new NPCClickHandler(new Object[] { 647 }, e -> {
        e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
            {
                addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
                addOptions("What would you like to say?", new Options() {
                    @Override
                    public void create() {
                        if(!e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
                            option("About Shield Of Arrav...", new ReldoShieldOfArravD(player).getStart());
                        if(e.getPlayer().getQuestManager().getStage(Quest.KNIGHTS_SWORD) >= KnightsSword.TALK_TO_RELDO
                                && !e.getPlayer().isQuestComplete(Quest.KNIGHTS_SWORD))
                            option("About Knight's Sword...", new ReldoKnightsSwordD(player).getStart());
                        option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.VARROCK_ARMOR).getStart());
                    }
                });
                create();
            }
        });
    });
}
