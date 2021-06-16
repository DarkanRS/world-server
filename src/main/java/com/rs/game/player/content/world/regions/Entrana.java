package com.rs.game.player.content.world.regions;

import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.skills.woodcutting.TreeType;
import com.rs.game.player.content.skills.woodcutting.Woodcutting;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Entrana {
	
	public static ObjectClickHandler handleMagicDoor = new ObjectClickHandler(new Object[] { 2407 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleDoor(e.getPlayer(), e.getObject());
			Magic.sendNormalTeleportSpell(e.getPlayer(), 0, 0, new WorldTile(3093, 3222, 0));
		}
	};
	
	public static ObjectClickHandler handleDramenTree = new ObjectClickHandler(new Object[] { "Dramen tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.DRAMEN) {
					@Override
					public void fellTree() { }
				});
		}
	};
	
	public static ObjectClickHandler handleEntranaDungeonLadders = new ObjectClickHandler(new Object[] { 2408 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer())
					.addNPC(656, HeadE.CONFUSED, "Be careful going in there! You are unarmed, and there is much evilness lurking down there! The evilness seems to block off our contact with our gods,")
					.addNPC(656, HeadE.CONFUSED, "so our prayers seem to have less effect down there. Oh, also, you won't be able to come back this way - This ladder only goes one way!")
					.addNPC(656, HeadE.CONFUSED, "The only exit from the caves below is a portal which is guarded by greater demons!")
					.addOption("Select an Option", "Well, that is a risk I will have to take.", "I don't think I'm strong enough to enter then.")
					.addPlayer(HeadE.CALM_TALK, "Well, that's a risk I will have to take.")
					.addNext(() -> {
						e.getPlayer().useLadder(new WorldTile(2822, 9774, 0));
					}));
		}
	};
}
