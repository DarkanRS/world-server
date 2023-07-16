package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.knightssword.SquireKnightsSwordD;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SquireAsrol extends Conversation {
    private static final int npcId = 606;

    public static NPCClickHandler SquireAsrol = new NPCClickHandler(new Object[]{ npcId }, e -> {
        switch (e.getOption()) {
            
            case "Talk-to" -> e.getPlayer().startConversation(new SquireAsrol(e.getPlayer()));
        }
    });

    public SquireAsrol(Player player) {
        super(player);
        addNPC(npcId, HeadE.CHEERFUL, "Hello, what are you after?");
        addOptions("What would you like to say?", new Options() {
            @Override
            public void create() {
                if(!player.isQuestComplete(Quest.KNIGHTS_SWORD))
                    option("About Knight's Sword.", new Dialogue()
                            .addNext(()->{
                                player.startConversation(new SquireKnightsSwordD(player));
                            })
                    );
                option("About the Achievement System...",
                        new AchievementSystemDialogue(player, npcId, SetReward.FALADOR_SHIELD)
                                .getStart());
            }
        });
    }
}
