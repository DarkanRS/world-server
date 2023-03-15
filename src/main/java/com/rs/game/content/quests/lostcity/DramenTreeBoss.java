package com.rs.game.content.quests.lostcity;

import static com.rs.game.content.quests.lostcity.LostCity.TREE_SPIRIT;

import com.rs.game.World;
import com.rs.game.content.skills.woodcutting.TreeType;
import com.rs.game.content.skills.woodcutting.Woodcutting;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DramenTreeBoss {

	public static ObjectClickHandler handleDramenTree = new ObjectClickHandler(new Object[] { "Dramen tree" }, e -> {
		GameObject obj = e.getObject();
		if(e.getPlayer().getQuestManager().getStage(Quest.LOST_CITY) == LostCity.CHOP_DRAMEN_TREE) {
			if(!e.getPlayer().inCombat()) {
				for (NPC npc : World.getNPCsInChunkRange(e.getPlayer().getChunkId(), 4))
					if (npc.getId() == TREE_SPIRIT) {
						npc.forceTalk("You must defeat me before touching the tree!");
						return;
					}
				NPC spirit = World.spawnNPC(TREE_SPIRIT, Tile.of(obj.getX(), obj.getY() + 2, obj.getPlane()), -1, false, true);
				spirit.setTarget(e.getPlayer());
				spirit.forceTalk("You must defeat me before touching the tree!");
			}
		}else if(e.getPlayer().isQuestComplete(Quest.LOST_CITY) || e.getPlayer().getQuestManager().getStage(Quest.LOST_CITY) >= LostCity.FIND_ZANARIS) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.DRAMEN) {
					@Override
					public void fellTree() {
					}
				});
		} else
			e.getPlayer().sendMessage("The tree seems to have a ominous aura to it. You do not feel like chopping it down.");
	});

	public static NPCDeathHandler handleTreeSpiritDeath = new NPCDeathHandler(TREE_SPIRIT, e -> {
		if(e.killedByPlayer() && ((Player)e.getKiller()).getQuestManager().getStage(Quest.LOST_CITY) == LostCity.CHOP_DRAMEN_TREE)
			((Player)e.getKiller()).getQuestManager().setStage(Quest.LOST_CITY, LostCity.FIND_ZANARIS);
	});


}
