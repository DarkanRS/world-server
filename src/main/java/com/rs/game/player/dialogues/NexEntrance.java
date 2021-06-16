package com.rs.game.player.dialogues;

import com.rs.game.npc.godwars.zaros.NexArena;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.controllers.NexController;
import com.rs.lib.game.WorldTile;

public class NexEntrance extends Conversation {
		
	public NexEntrance(NexArena arena, Player player) {
		super(player);
		
		addSimple("The room beyond this point is a prison! There is no way out other than death or teleport. Only those who endure dangerous encounters should proceed.");
		addOption("There are currently " + arena.getPlayersCount() + " people fighting.<br>Do you wish to join them?", "Climb down.", "Stay here.");
		addNext(() -> {
			player.setNextWorldTile(new WorldTile(2911, 5204, 0));
			player.getControllerManager().startController(new NexController(arena));
		});
		create();
	}
}
