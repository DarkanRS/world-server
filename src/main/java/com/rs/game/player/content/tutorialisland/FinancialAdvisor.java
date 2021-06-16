package com.rs.game.player.content.tutorialisland;

import com.rs.Settings;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.statements.NPCStatement;
import com.rs.game.player.content.dialogue.statements.OptionStatement;
import com.rs.game.player.content.dialogue.statements.PlayerStatement;
import com.rs.game.player.controllers.TutorialIslandController;
import com.rs.game.player.controllers.TutorialIslandController.Stage;

public class FinancialAdvisor extends Conversation {

    public FinancialAdvisor(Player player, NPC npc, TutorialIslandController ctrl) {
        super(player);

        if (ctrl.getStage() == Stage.TALK_TO_FINANCIAL_ADVISOR) {
            addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Hello, Who are you?"));
            addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "I'm the Financial Advisor. I'm here to tell people how to", "make money."));
            addNext(new PlayerStatement(HeadE.SKEPTICAL, "Okay. How can I make money then?"));
            addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "How you can make money? Quite."));
        } else {
            addNext(new OptionStatement("Would you like to hear about making money again?", "Yes!", "No thanks."));
            addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Okay, making money. Quite."));
        }

        addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Well, there are three basic ways of making money here:", "combat, quests and trading. I will talk you through each", "of them very quickly."));
        addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Let's start with combat as it is probably still fresh in", "your mind. Many enemies, both human and monster,", "will drop items when they die."));
        addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Now, the next way to earn money quickly is by quests.", "Many people on "+ Settings.getConfig().getServerName()+" have things they need", "doing, which they will reward you for."));
        addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "By getting a high level in skills such as Cooking, Mining,", "Smithing or Fishing, you can create or catch your own", "items and sell them for pure profit."));
        addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Well, that about covers it. Come back if you'd like to go", "over this again."));
        addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.LEAVE_FINANCIAL_ADVISOR_ROOM)));

        create();
    }
}
