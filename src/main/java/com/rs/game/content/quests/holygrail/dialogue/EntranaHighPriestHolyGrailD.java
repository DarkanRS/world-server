package com.rs.game.content.quests.holygrail.dialogue;

import static com.rs.game.content.quests.holygrail.HolyGrail.GO_TO_ENTRANA;
import static com.rs.game.content.quests.holygrail.HolyGrail.GO_TO_MCGRUBOR;

import com.rs.game.World;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class EntranaHighPriestHolyGrailD extends Conversation {
	private static final int NPC = 216;
	static class CroneDialogue extends Conversation {
		public CroneDialogue(Player player) {
			super(player);
			Dialogue croneQuestions = new Dialogue();
			croneQuestions.addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("What are the six heads?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "What are the six heads?")
							.addNPC(217, HeadE.CALM_TALK, "The six stone heads have appeared just recently in the world. They all face the point of the realm crossing. ")
							.addNPC(217, HeadE.CALM_TALK, "Find where two of the heads face, and you should be able to pinpoint where it is.")
							.addNext(croneQuestions)
					);
					option("What's a Fisher King?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "What's a Fisher King?")
							.addNPC(217, HeadE.CALM_TALK, "The Fisher King is the owner and slave of the Grail.")
							.addNext(croneQuestions)
					);
					option("What do you mean by the whistle?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "What do you mean by the whistle?")
							.addNPC(217, HeadE.CALM_TALK, "You don't know about the whistles yet? The whistles are easy. You will need one to get to and from the Fisher King's realm. ")
							.addNPC(217, HeadE.CALM_TALK, "They reside in a haunted manor house in Misthalin, though you may not perceive them unless you carry something from the realm of the Fisher King...")
							.addNext(croneQuestions)
					);
					option("Ok, I will go searching.", new Dialogue());
				}
			});
			addNext(croneQuestions);
		}
	}

	public EntranaHighPriestHolyGrailD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case GO_TO_ENTRANA, GO_TO_MCGRUBOR -> {
				addNPC(NPC, HeadE.CALM_TALK, "Many greetings. Welcome to our fair island.");
				addPlayer(HeadE.HAPPY_TALKING, "Hello. I am in search of the Holy Grail.");
				addNPC(NPC, HeadE.CALM_TALK, "The object of which you speak did once pass through holy Entrana. I know not where it is now however. " +
						"Nor do I really care.", ()->{
					for(NPC npc : World.getNPCsInChunkRange(p.getChunkId(), 1))
						if(npc.getId() == 217 && npc instanceof OwnedNPC crone && crone.getOwner() == p)
							return;
					new OwnedNPC(p, 217, p.getTile(), true);
				});
				addNPC(217, HeadE.CALM_TALK, "Did you say the Grail? You are a Grail knight, yes? Well you'd better hurry. A Fisher King is in pain.");
				addPlayer(HeadE.HAPPY_TALKING, "Well I would, but I don't know where I am going!");
				addNPC(217, HeadE.CALM_TALK, " Go to where the six heads face, blow the whistle and away you go!", ()-> {
					p.getQuestManager().setStage(Quest.HOLY_GRAIL, GO_TO_MCGRUBOR);
				});
				addNext(()->{p.startConversation(new CroneDialogue(p).getStart());});
			}
			default -> {
				addNPC(NPC, HeadE.CALM_TALK, "Bless you " + p.getDisplayName() + "!");
			}
		}
	}


    public static NPCClickHandler handlePriest = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new EntranaHighPriestHolyGrailD(e.getPlayer()).getStart()));

	public static NPCClickHandler handleCrone = new NPCClickHandler(new Object[]{217}, e -> e.getPlayer().startConversation(new CroneDialogue(e.getPlayer()).getStart()));
}
