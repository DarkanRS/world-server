package com.rs.game.content.world.areas.thieves_guild.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.buyersandcellars.npcs.RobinCastle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Robin {
    private static int npcid = 11280;

    public static NPCClickHandler RobinHandler = new NPCClickHandler(new Object[] { 7955, 11268, 11279, 11280 }, new String[] {"Talk-to"}, e -> {
        if(e.getPlayer().isMiniquestStarted(Miniquest.FROM_TINY_ACORNS) && !e.getPlayer().isMiniquestComplete(Miniquest.FROM_TINY_ACORNS)) {
            new com.rs.game.content.miniquests.FromTinyAcorns.Robin(e.getPlayer());
            return;
        }
        int questStage = e.getPlayer().getQuestStage(Quest.BUYERS_AND_CELLARS);
        switch (questStage) {
            case 0:
                RobinCastle.preQuest(e.getPlayer());
                break;
            case 1, 2:
                RobinCastle.someExtraHelp(e.getPlayer());
                break;
            case 3, 4:
                if (e.getNPC().getTile().equals(Tile.of(4762, 5904, 0))){
                    RobinCastle.someExtraHelp(e.getPlayer());
                }
                else {
                    RobinCastle.stage3(e.getPlayer(), e.getNPC());
                }
                break;
            case 5, 6:
                RobinCastle.stage5(e.getPlayer());
                break;
            case 7:
                RobinCastle.stage7(e.getPlayer());
                break;
            case 8:
                if (e.getNPC().getTile().equals(Tile.of(4762, 5904, 0))){
                    RobinCastle.someExtraHelp(e.getPlayer());
                }
                else
                    RobinCastle.stage8(e.getPlayer());
                break;
            default:
                postQuest(e.getPlayer());
        }
    });

    private static void postQuest(Player player) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "Hello there.")
                .addNPC(npcid, HeadE.CALM_TALK, "The Guildmaster wanted me to be on hand in case you needed some more hints on picking pockets. Now, what can I do for you?")
                .addOptions(ops -> {
                    ops.add("I'm always willing to learn.")
                            .addPlayer(HeadE.CALM_TALK, "I'm always willing to learn.")
                            .addNPC(npcid, HeadE.CALM_TALK, "When you're on the prowl for pickpocketing targets, it should be fairly obvious who's not paying enough attention to the world around them.")
                            .addNPC(npcid, HeadE.CALM_TALK, "Just saunter up to them all casual-like, then dip your hand into their wallets as gently and as neatly as you can.")
                            .addNPC(npcid, HeadE.CALM_TALK, "If you succeed, you'll get some of the contents of their pockets; it not, they'll likely punch you in the face, so be warned.")
                            .addNPC(npcid, HeadE.CALM_TALK, "It stings, and you'll need a moment to gather your wits.")
                            .addPlayer(HeadE.CALM_TALK, "Thanks, Robin.")
                            .addNPC(npcid, HeadE.CALM_TALK, "You can use the training dummy if you'd like, but after a while you'll need to switch to real marks if you want to improve.");
                    ops.add("I've got it, thanks.")
                            .addPlayer(HeadE.CALM_TALK, "I’ve got it, thanks.")
                            .addNPC(npcid, HeadE.CALM_TALK, "You can use the training dummy if you’d like, but after a while you’ll need to switch to real marks if you want to improve.");
                    ops.add("Bye for now.")
                            .addPlayer(HeadE.CALM_TALK, "Bye for now.");
                })
        );
    }
}
