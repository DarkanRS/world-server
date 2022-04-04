package com.rs.game.content.quests.handlers.fightarena.dialogue;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.handlers.fightarena.FightArena.*;

@PluginEventHandler
public class LazyGuardFightArenaD extends Conversation {
	private static final int NPC = 7550;
	public LazyGuardFightArenaD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.FIGHT_ARENA)) {
			case NOT_STARTED, FREE_JEREMY -> {
				addNPC(NPC, HeadE.CALM_TALK, "Whatchu want?");
				addPlayer(HeadE.HAPPY_TALKING, "Nothing.");
			}
			case GET_JAIL_KEYS -> {
				addPlayer(HeadE.HAPPY_TALKING, "Long live General Khazard!");
				addNPC(NPC, HeadE.CALM_TALK, "Erm, yes... quite right. Have you come to laugh at the fighting slaves? I used to really enjoy it but, after a " +
						"while, they became quite boring. ");
				addNPC(NPC, HeadE.CALM_TALK, "To be honest, now I've locked them up, all I want is a decent drink. Mind you, too much Khali brew and I fall asleep.");
				addPlayer(HeadE.HAPPY_TALKING, "Hard stuff that Khali brew. Tastes great though, doesn't it?");
				addNPC(NPC, HeadE.CALM_TALK, "Well, yes. Between you and me, I find it hard to turn down a nice cold bottle of Khali brew from the bar");
				addNPC(NPC, HeadE.CALM_TALK, "Mmmm, yes. Ahem! Well, yes, enough chit-chat – back to work, guard.");
				addPlayer(HeadE.HAPPY_TALKING, "Yessir!");
			}
			case QUEST_COMPLETE ->  {

			}
		}
	}

    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new LazyGuardFightArenaD(e.getPlayer()).getStart());
        }
    };
}
