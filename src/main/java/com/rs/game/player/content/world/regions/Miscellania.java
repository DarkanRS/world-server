package com.rs.game.player.content.world.regions;

import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Miscellania {
	
	public static NPCClickHandler handleAdvisorGhrim = new NPCClickHandler(1375) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.FREMENNIK_BOOTS).getStart());
						}
					});
				}
			});
		}
	};
	
	public static ObjectClickHandler handleUndergroundEntrance = new ObjectClickHandler(new Object[] { 15115, 15116 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(e.getObjectId() == 15115 ? new WorldTile(2509, 3847, 0) : new WorldTile(2509, 10245, 0));
		}
	};
	
	public static ObjectClickHandler handleUndergroundCrevices = new ObjectClickHandler(new Object[] { 15186, 15187 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getObjectId() == 15186 ? new WorldTile(2505, 10283, 0) : new WorldTile(2505, 10280, 0));
		}
	};
	
	public static ObjectClickHandler handleTrees = new ObjectClickHandler(new Object[] { 46274, 46275, 46277 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("You'd better leave that to the serfs.");
		}
	};
	
	public static ObjectClickHandler handleMiscTeak = new ObjectClickHandler(new Object[] { 15062 }, new WorldTile(2594, 3890, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("You'd better leave that to the serfs.");
		}
	};

}
