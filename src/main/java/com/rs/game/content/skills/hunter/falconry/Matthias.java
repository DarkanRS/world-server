package com.rs.game.content.skills.hunter.falconry;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.Region;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Matthias extends NPC {
	
	private static List<GameObject> POST_TILES = new ArrayList<>();
	private static final int BIRD_FREQUENCY = Ticks.fromSeconds(15);

	public Matthias(WorldTile tile) {
		super(5092, tile);
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
		if (getTickCounter() % BIRD_FREQUENCY == 0)
			toggleBird();
	}

	private void toggleBird() {
		boolean hasBird = getId() == 5092;
		List<GameObject> posts = POST_TILES.stream()
				.filter(obj -> obj.getId() == (hasBird ? 19220 : 19221))
				.toList();
		GameObject post = posts.get(Utils.random(posts.size()));
		if (post == null)
			return;
		resetWalkSteps();
		faceObject(post);
		freeze();
		if (hasBird) {
			if (Utils.random(10) == 0)
				forceTalk("Gouge 'em, Valor!");
			transformIntoNPC(5093);
			setBas(1);
			World.sendProjectile(this, post, 922, 41, 16, 31, 0.8, 16, 0, proj -> {
				post.setId(19221);
				unfreeze();
			});
		} else {
			if (Utils.random(10) == 0)
				forceTalk("Valor, to me!");
			post.setId(19220);
			World.sendProjectile(post, this, 922, 41, 16, 31, 0.8, 16, 0, proj -> {
				transformIntoNPC(5092);
				setBas(-1);
				unfreeze();
			});
		}
	}
	
	@ServerStartupEvent
	public static void init() {
		Region region = World.getRegion(9528, true);
		POST_TILES = region.getAllObjects()
			.stream()
			.filter(obj -> obj != null && (obj.getId() == 19220 || obj.getId() == 19221))
			.toList();
	}

	public static NPCClickHandler handleMatthias = new NPCClickHandler(new Object[] { 5092, 5093 }) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Talk-to" -> {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CHEERFUL, "Hello there.");
						addNPC(e.getNPCId(), HeadE.CONFUSED, "Greetings. Can I help you at all?");
						addOptions(ops -> {
							ops.add("Do you have any quests I could do?")
								.addNPC(e.getNPCId(), HeadE.CONFUSED, "A quest? What a strange notion. Do you normally go around asking complete strangers for quests?")
								.addPlayer(HeadE.SKEPTICAL, "Er, yes, now that you come to mention it.")
								.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Oh, ok then. Well, no, I don't. Sorry.");
							
							ops.add("What is this place?")
								.addNPC(e.getNPCId(), HeadE.CHEERFUL, "A good question; straight and to the point.")
								.addNPC(e.getNPCId(), HeadE.CHEERFUL, "My name is Matthias, I am a falconer, and this is where I train my birds.")
								.addOptions(watOp -> {
									watOp.add("That sounds like fun; could I have a go?", () -> FalconryController.beginFalconry(e.getPlayer()));
									
									watOp.add("That doesn't sound like my sort of thing.")
										.addNPC(e.getNPCId(), HeadE.CALM_TALK, "Fair enough; it does require a great deal of patience and skill, so I can understand if you might feel intimidated.");
									
									watOp.add("What's this falconry thing all about then?")
										.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Well, some people see it as a sport, although such a term does not really convey the amount of patience and dedication to be profiecient at the task.")
										.addNPC(e.getNPCId(), HeadE.CHEERFUL, "Putting it simply, it is the training and use of birds of prey in hunting quarry.");
								});
						});
						create();
					}
				});
			}
			case "Falconry" -> FalconryController.beginFalconry(e.getPlayer());
			}
		}
	};
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(5092, 5093) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Matthias(tile);
		}
	};
}
