package com.rs.game.content.quests.handlers.templeofikov.dialogues;

import static com.rs.game.content.quests.handlers.templeofikov.TempleOfIkov.HELP_LUCIEN;
import static com.rs.game.content.quests.handlers.templeofikov.TempleOfIkov.NOT_STARTED;
import static com.rs.game.content.quests.handlers.templeofikov.TempleOfIkov.QUEST_COMPLETE;
import static com.rs.game.content.world.doors.Doors.handleDoor;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.handlers.templeofikov.TempleOfIkov;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class LucienVarrockTempleOfIkov extends Conversation {
	private static final int NPC = 8347;
	public LucienVarrockTempleOfIkov(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.TEMPLE_OF_IKOV)) {
			case NOT_STARTED -> {
				addNPC(NPC, HeadE.CALM_TALK, "You shouldn't in here!");
				addPlayer(HeadE.HAPPY_TALKING, "Really?");
				addNPC(NPC, HeadE.CALM_TALK, "Yes...");
			}
			case HELP_LUCIEN -> {
				addNPC(NPC, HeadE.CALM_TALK, "Have you got the Staff of Armadyl yet?");
				if(p.getInventory().containsItem(84)) {
					addPlayer(HeadE.HAPPY_TALKING, "Yes! Here it is.");
					addItem(84, "You show him the Staff of Armadyl");
					addNPC(NPC, HeadE.EVIL_LAUGH, "Muhahahhahahaha! I can feel the power of the staff running through me! I will be more powerful and they " +
							"shall bow down to me! I suppose you want your reward? I shall grant you much power!");
					addNext(()->{
						p.getQuestManager().completeQuest(Quest.TEMPLE_OF_IKOV);
						p.getInventory().removeItems(new Item(84, 1));
						TempleOfIkov.setIkovLucienSide(p, true);
					});
					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "Not yet, but i'm getting it!");
			}
			case QUEST_COMPLETE ->  {
				if(TempleOfIkov.isLucienSide(p)) {
					addNPC(NPC, HeadE.EVIL_LAUGH, "Muhahahhahahaha! I can feel the power of the staff running through me! I will be more powerful and they shall bow down to me!");
					return;
				}
				addNPC(NPC, HeadE.FRUSTRATED, "Gah! I don't have time for this. I'll find someone else to do my bidding.");
			}
		}
	}

	public static ObjectClickHandler handleLuciensHouse = new ObjectClickHandler(new Object[] { 102 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getPlayer().getQuestManager().getStage(Quest.TEMPLE_OF_IKOV) > 0) {
				handleDoor(e.getPlayer(), e.getObject());
				return;
			}
			e.getPlayer().sendMessage("The door is locked...");
		}
	};

    public static NPCClickHandler handleLucianDialogue = new NPCClickHandler(new Object[]{NPC}, new String[]{"Talk-to"}) {
        @Override
        public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new LucienVarrockTempleOfIkov(e.getPlayer()).getStart());
        }
    };
}
