package com.rs.game.content.quests.handlers.lostcity;

import static com.rs.game.content.quests.handlers.lostcity.LostCity.TREE_SPIRIT;

import com.rs.game.World;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.woodcutting.TreeType;
import com.rs.game.content.skills.woodcutting.Woodcutting;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DramenTreeBoss {


	public static ObjectClickHandler handleDramenTree = new ObjectClickHandler(new Object[] { "Dramen tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(p.getQuestManager().getStage(Quest.LOST_CITY) == LostCity.CHOP_DRAMEN_TREE) {
				if(!p.inCombat()) {
					for (NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId()))
						if (npc.getId() == TREE_SPIRIT) {
							npc.forceTalk("You must defeat me before touching the tree!");
							return;
						}
					NPC spirit = World.spawnNPC(TREE_SPIRIT, WorldTile.of(obj.getX(), obj.getY() + 2, obj.getPlane()), -1, false, true);
					spirit.setTarget(p);
					spirit.forceTalk("You must defeat me before touching the tree!");
				}
			}else if(p.isQuestComplete(Quest.LOST_CITY) || p.getQuestManager().getStage(Quest.LOST_CITY) >= LostCity.FIND_ZANARIS) {
				if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
					e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.DRAMEN) {
						@Override
						public void fellTree() {
						}
					});
			} else
				p.sendMessage("The tree seems to have a ominous aura to it. You do not feel like chopping it down.");
		}
	};

	public static NPCDeathHandler handleTreeSpiritDeath = new NPCDeathHandler(TREE_SPIRIT) {
		@Override
		public void handle(NPCDeathEvent e) {
			if(e.killedByPlayer() && ((Player)e.getKiller()).getQuestManager().getStage(Quest.LOST_CITY) == LostCity.CHOP_DRAMEN_TREE)
				((Player)e.getKiller()).getQuestManager().setStage(Quest.LOST_CITY, LostCity.FIND_ZANARIS);
		}
	};


}
