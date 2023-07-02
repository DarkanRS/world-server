package com.rs.game.content.quests.whatliesbelow.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RatBurgiss extends Conversation {
    private static final int ID = 5833;

    public static NPCClickHandler handleRatBurgiss = new NPCClickHandler(new Object[] { ID }, e -> e.getPlayer().startConversation(new RatBurgiss(e.getPlayer())));

    public RatBurgiss(Player player) {
        super(player);
        addOptions(ops -> {
            ops.add("About the Achievement System...", new AchievementSystemDialogue(player, ID, SetReward.VARROCK_ARMOR).getStart());
            if (!player.isQuestStarted(Quest.WHAT_LIES_BELOW)) {

            }
        });
    }
}
