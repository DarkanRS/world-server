package com.rs.game.content.quests.handlers.holygrail;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.*;

@PluginEventHandler
public class KingPercivalHolyGrailD extends Conversation {
	private static final int NPC = 212;
	public KingPercivalHolyGrailD(Player p) {
		super(p);
		if(p.getQuestManager().getStage(Quest.HOLY_GRAIL) >= GIVE_AURTHUR_HOLY_GRAIL) {
			addNPC(NPC, HeadE.TALKING_ALOT, "You missed all the excitement! I got here and agreed to take over duties as king here, then before my eyes the" +
					" most miraculous changes occurred here... grass and trees were growing outside before our very eyes!");
			addNPC(NPC, HeadE.HAPPY_TALKING, "Thank you very much for showing me the way home.");
			return;
		}
		addNPC(NPC, HeadE.CALM_TALK, "Hi");
		addPlayer(HeadE.HAPPY_TALKING, "Hello");
		addNPC(NPC, HeadE.CALM_TALK, "...");
		addPlayer(HeadE.HAPPY_TALKING, "...");
		addNPC(NPC, HeadE.CALM_TALK, "See you later then?");
		addPlayer(HeadE.HAPPY_TALKING, "Yes...");
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new KingPercivalHolyGrailD(e.getPlayer()).getStart());
        }
    };
}
