package com.rs.game.content.quests.handlers.lostcity;

import static com.rs.game.content.quests.handlers.lostcity.LostCity.CHOP_DRAMEN_TREE;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.FIND_ZANARIS;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.LEPRACAUN;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.LEPRACAUN_TREE;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.NOT_STARTED;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.QUEST_COMPLETE;
import static com.rs.game.content.quests.handlers.lostcity.LostCity.TALK_TO_LEPRAUCAN;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class LeprecaunLostCityD extends Conversation {
	private final int FORGETTINGTOSAY = 0;
	public LeprecaunLostCityD(Player p) {
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
							.addNext(()->{p.startConversation(new LeprecaunLostCityD(p, FORGETTINGTOSAY).getStart());})
							);
					option("I've been in that shed and I didn't see a city.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I've been in that shed and I didn't see a city.")
							.addNext(()->{p.startConversation(new LeprecaunLostCityD(p, FORGETTINGTOSAY).getStart());})
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
								for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
									if(npc.getId() == LEPRACAUN) {
										npc.resetWalkSteps();
										npc.faceEntity(p);
										npc.setNextAnimation(new Animation(5488));
										npc.forceTalk("Avach Sarimporto!");
										Magic.sendObjectTeleportSpell(p, false, WorldTile.of(3047, 3236, 0));
										break;
									}
							})
							);
					option("No thanks, I'll get there on my own", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "No thanks, I'll get there on my own")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Fine, have it yer way. I'm off!")
							.addNext(()-> {
								for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
									if(npc.getId() == LEPRACAUN)
										npc.finish();
							})
							);
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
							.addNext(()->{
								for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
									if(npc.getId() == LEPRACAUN)
										npc.finish();
								p.sendMessage("The leprechaun magically disappears.");
							})
							);
					option("How do I get to Zanaris again?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "How do I get to Zanaris again?")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Yer stupid elephant! I'll tell yer again! Yer need to be entering the shed in the " +
									"middle of the swamp while holding a dramen staff.")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Yer can make the dramen staff from a dramen tree branch, and there's a dramen tree on Entrana. " +
									"now leave me alone, yer great elephant.")
							.addNext(()->{
								for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
									if(npc.getId() == LEPRACAUN)
										npc.finish();
								p.sendMessage("The leprechaun magically disappears.");
							})
							);
				}
			});

		}
		}
	}

	public LeprecaunLostCityD(Player p, int id) {
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
								for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
									if(npc.getId() == LEPRACAUN) {
										npc.resetWalkSteps();
										npc.faceEntity(p);
										npc.setNextAnimation(new Animation(5488));
										npc.forceTalk("Avach Sarimporto!");
										Magic.sendObjectTeleportSpell(p, false, WorldTile.of(3047, 3236, 0));
										break;
									}
							})
							);
					option("No thanks, I'll get there on my own", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "No thanks, I'll get there on my own")
							.addNPC(LEPRACAUN, HeadE.CALM_TALK, "Fine, have it yer way. I'm off!")
							.addNext(()-> {
								p.getQuestManager().setStage(Quest.LOST_CITY, CHOP_DRAMEN_TREE);
								for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
									if(npc.getId() == LEPRACAUN)
										npc.finish();
							})
							);
				}
			});
		}
		}
	}

	public static ObjectClickHandler handleTreeLep = new ObjectClickHandler(true, new Object[] { LEPRACAUN_TREE }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			for(NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId()))
				if(npc.getId() == LEPRACAUN)
					return;
			p.startConversation(new Conversation(p) {
				{
					addNPC(LEPRACAUN, HeadE.FRUSTRATED, "Hey! Yer big elephant! Don't go choppin' down me house, now!");
					addNPC(LEPRACAUN, HeadE.AMAZED_MILD, "Woah, woah!");
					addNPC(LEPRACAUN, HeadE.AMAZED, "AAAAAAAAAAAAAAHHHH!!!!");
					addSimple("The leprechaun falls down", () -> {
						WorldTasks.schedule(new WorldTask() {
							int tick;
							NPC lepracaun;
							@Override
							public void run() {
								if(tick == 0 )
									lepracaun = World.spawnNPC(LEPRACAUN, WorldTile.of(obj.getX(), obj.getY()-1, obj.getPlane()), -1, false, true);
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
									lepracaun.walkToAndExecute(WorldTile.of(obj.getX(), obj.getY()-1, obj.getPlane()), ()->{
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
		}
	};

	public static NPCClickHandler handleLeprecaunDialogue = new NPCClickHandler(new Object[] { LEPRACAUN }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new LeprecaunLostCityD(e.getPlayer()).getStart());
		}
	};
}
