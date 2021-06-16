package com.rs.game.player.content.world;

import com.rs.game.ForceTalk;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class SheepShearing {
	
	public static ItemOnNPCHandler handleShearsOnSheep = new ItemOnNPCHandler(5157, 1765, 43, 5160, 5161, 5156) {
		@Override
		public void handle(ItemOnNPCEvent e) {
			final int npcId = e.getNPC().getId();
			if (Utils.getRandomInclusive(2) == 0) {
				e.getNPC().setNextForceTalk(new ForceTalk("Baa!"));
				e.getNPC().playSound(756, 1);
				e.getNPC().addWalkSteps(npcId, npcId, 4, true);
				e.getNPC().setRun(true);
				e.getPlayer().sendMessage("The sheep runs away from you.");
			} else if (e.getPlayer().getInventory().containsItem(1735, 1)) {
				e.getPlayer().playSound(761, 1);
				e.getPlayer().getInventory().addItem(1737, 1);
				e.getPlayer().sendMessage("You shear the sheep of it's fleece.");
				e.getPlayer().setNextAnimation(new Animation(893));
				e.getNPC().transformIntoNPC(5149);
				WorldTasksManager.delay(Ticks.fromSeconds(10), () -> e.getNPC().transformIntoNPC(npcId));
			} else
				e.getPlayer().sendMessage("You need a pair of shears to shear the sheep.");
		}
	};
	
	public static NPCClickHandler handleShearOption = new NPCClickHandler(5157, 1765, 43, 5160, 5161, 5156) {
		@Override
		public void handle(NPCClickEvent e) {
			final int npcId = e.getNPC().getId();
			if (Utils.getRandomInclusive(2) == 0) {
				e.getNPC().setNextForceTalk(new ForceTalk("Baa!"));
				e.getNPC().playSound(756, 1);
				e.getNPC().addWalkSteps(npcId, npcId, 4, true);
				e.getNPC().setRun(true);
				e.getPlayer().sendMessage("The sheep runs away from you.");
			} else if (e.getPlayer().getInventory().containsItem(1735, 1)) {
				e.getPlayer().playSound(761, 1);
				e.getPlayer().getInventory().addItem(1737, 1);
				e.getPlayer().sendMessage("You shear the sheep of it's fleece.");
				e.getPlayer().setNextAnimation(new Animation(893));
				e.getNPC().transformIntoNPC(5149);
				WorldTasksManager.delay(Ticks.fromSeconds(10), () -> e.getNPC().transformIntoNPC(npcId));
			} else
				e.getPlayer().sendMessage("You need a pair of shears to shear the sheep.");
		}
	};

}
