package com.rs.game.content.quests.lostcity;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import static com.rs.game.content.quests.lostcity.LostCity.*;

@PluginEventHandler
public class LeprecaunLostCityD extends Conversation {
	private final int FORGETTINGTOSAY = 0;
	public LeprecaunLostCityD(Player p, NPC leprechaun) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.LOST_CITY)) {
		case NOT_STARTED -> {
			addNPC(LEPRACAUN, HeadE.VERY_FRUSTRATED, "Ah, yer big elephant! Yer've caught me! What would an elephant like yer be wanting wid ol' Shamus, then?");
			addPlayer(HeadE.HAPPY_TALKING, "I'm not sure.");
			addNPC(LEPRACAUN, HeadE.FRUSTRATED, "Well that's just great, you nuisance!");
		}
		case TALK_TO_LEPRAUCAN -> {
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "Ah, yer big elephant! Yer've caught me! What would an elephant like yer be wanting wid ol' Shamus, then?");
			addPlayer(HeadE.HAPPY_TALKING, "I want to find Zanaris.");
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "Zanaris, is it now? Well, well, well... You'll be needing that funny little shed out there in the swamp, so you will.");
			addPlayer(HeadE.HAPPY_TALKING, "Shed? I thought Zanaris was a city.");
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "Aye, that it is!");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("How does it fit in a shed, then?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "How does it fit in a shed, then?")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Ah, yer stupid elephant! The city isn't IN the shed! The doorway to the shed is a portal to " +
									"Zanaris, so it is.")
							.addPlayer(HeadE.HAPPY_TALKING, "So, I just walk into the shed and end up in Zanaris?")
							.addNext(()->{p.startConversation(new LeprecaunLostCityD(p, leprechaun, FORGETTINGTOSAY).getStart());})
							);
					option("I've been in that shed and I didn't see a city.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I've been in that shed and I didn't see a city.")
							.addNext(()->{p.startConversation(new LeprecaunLostCityD(p, leprechaun, FORGETTINGTOSAY).getStart());})
							);
				}
			});

		}
		case CHOP_DRAMEN_TREE ->  {
			addPlayer(HeadE.HAPPY_TALKING, "I am still looking for Zanaris.");
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "Did yer need a teleport over to Port Sarim?");
			addOptions("Teleport to Port Sarim?", new Options() {
				@Override
				public void create() {
					option("Yes, please, a teleport would be useful.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Yes, please, a teleport would be useful.")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Right yer are. Hold on!")
							.addNext(()-> {
								leprechaun.resetWalkSteps();
								leprechaun.faceEntity(p);
								leprechaun.setNextAnimation(new Animation(5488));
								leprechaun.forceTalk("Avach Sarimporto!");
								Magic.sendObjectTeleportSpell(p, false, Tile.of(3047, 3236, 0));
							}));
					option("No thanks, I'll get there on my own", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "No thanks, I'll get there on my own")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Fine, have it yer way. I'm off!")
							.addNext(()-> leprechaun.finish()));
				}
			});
		}
		case FIND_ZANARIS, QUEST_COMPLETE ->  {
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "Ah, yer big elephant! Yer've caught me! What would an elephant like yer be wanting wid ol' Shamus, then?");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("I'm not sure", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm not sure")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Ha! Look at yer! Look at the stupid elephant who tries to go catching a leprechaun " +
									"when he don't even be knowing what he wants!")
							.addNext(() -> {
								leprechaun.finish();
								p.sendMessage("The leprechaun magically disappears.");
							}));
					option("How do I get to Zanaris again?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "How do I get to Zanaris again?")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Yer stupid elephant! I'll tell yer again! Yer need to be entering the shed in the " +
									"middle of the swamp while holding a dramen staff.")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Yer can make the dramen staff from a dramen tree branch, and there's a dramen tree on Entrana. " +
									"now leave me alone, yer great elephant.")
						.addNext(() -> {
							leprechaun.finish();
							p.sendMessage("The leprechaun magically disappears.");
						}));
				}
			});

		}
		}
	}

	public LeprecaunLostCityD(Player p, NPC leprechaun, int id) {
		super(p);
		switch(id) {
		case FORGETTINGTOSAY -> {
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "Oh, was I forgetting to say? Ya need to be carrying a dramen staff to be getting there! Otherwise, yer'll " +
					"just be ending up in the shed.");
			addPlayer(HeadE.HAPPY_TALKING, "Where could I get such a staff?");
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "Dramen staves are crafted from branches of the dramen tree, so they are. I hear there's a dramen tree in a " +
					"cave over on the island of Entrana.");
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "There would probably be a good place for an elephant like yer to look, I reckon. The monks are running a ship " +
					"from Port Sarim to Entrana, so I hear.");
			addNPC(LEPRACAUN, HeadE.CALM_TALK, "Did yer need a teleport over to Port Sarim?");
			addOptions("Teleport to Port Sarim?", new Options() {
				@Override
				public void create() {
					option("Yes, please, a teleport would be useful.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Yes, please, a teleport would be useful.")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Right yer are. Hold on!")
							.addNext(()-> {
								p.getQuestManager().setStage(Quest.LOST_CITY, CHOP_DRAMEN_TREE);
								leprechaun.resetWalkSteps();
								leprechaun.faceEntity(p);
								leprechaun.setNextAnimation(new Animation(5488));
								leprechaun.forceTalk("Avach Sarimporto!");
								Magic.sendObjectTeleportSpell(p, false, Tile.of(3047, 3236, 0));
							})
							);
					option("No thanks, I'll get there on my own", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "No thanks, I'll get there on my own")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Fine, have it yer way. I'm off!")
							.addNext(()-> {
								p.getQuestManager().setStage(Quest.LOST_CITY, CHOP_DRAMEN_TREE);
								leprechaun.finish();
							})
							);
				}
			});
		}
		}
	}

	public static ObjectClickHandler handleTreeLep = new ObjectClickHandler(true, new Object[] { LEPRACAUN_TREE }, e -> {
		GameObject obj = e.getObject();
		for(NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 1))
			if(npc.getId() == LEPRACAUN)
				return;
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(LEPRACAUN, HeadE.FRUSTRATED, "Hey! Yer big elephant! Don't go choppin' down me house, now!");
				addNPC(LEPRACAUN, HeadE.AMAZED_MILD, "Woah, woah!");
				addNPC(LEPRACAUN, HeadE.AMAZED, "AAAAAAAAAAAAAAHHHH!!!!");
				addSimple("The leprechaun falls down", () -> {
					NPC lepracaun = World.spawnNPC(LEPRACAUN, Tile.of(obj.getX(), obj.getY()-1, obj.getPlane()), -1, false, true);
					WorldTasks.schedule(new WorldTask() {
						private int tick;
						@Override
						public void run() {
							if(tick == 1)
								lepracaun.forceTalk("Ouch!!");
							if(tick == 10)
								lepracaun.forceTalk("My aching back...");
							if(tick == 30)
								lepracaun.forceTalk("Might need to walk this off...");
							if(tick == 60)
								lepracaun.forceTalk("Oww, that's sore...");
							if(tick == 85) {
								lepracaun.forceTalk("Welp, better head home...");
								lepracaun.walkToAndExecute(Tile.of(obj.getX(), obj.getY()-1, obj.getPlane()), ()->{
									lepracaun.forceTalk("Back up the tree...");
									if(!lepracaun.hasFinished())
										lepracaun.finish();
								});
							}
							if(tick == 90) {
								if(!lepracaun.hasFinished())
									lepracaun.finish();
								stop();
							}
							tick++;
						}
					}, 0, 1);
				});
				create();
			}
		});
	});

	public static NPCClickHandler handleLeprecaunDialogue = new NPCClickHandler(new Object[] { LEPRACAUN }, e -> e.getPlayer().startConversation(new LeprecaunLostCityD(e.getPlayer(), e.getNPC()).getStart()));
}
