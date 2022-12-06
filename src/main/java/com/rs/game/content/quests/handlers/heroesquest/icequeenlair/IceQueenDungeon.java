package com.rs.game.content.quests.handlers.heroesquest.icequeenlair;

import com.rs.game.content.skills.mining.Pickaxe;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class IceQueenDungeon {
	public static ObjectClickHandler handleRockSlide = new ObjectClickHandler(new Object[]{2634}) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if (!p.containsTool(1265) && Pickaxe.getBest(p) == null) {
				p.sendMessage("You do not have a pickaxe...");
				return;
			}
			if (p.getSkills().getLevel(Constants.MINING) < 50)
				return;
			GameObject obj = e.getObject();
			int id = e.getObjectId();
			boolean hasRun = e.getPlayer().getRun();
			if (e.isAtObject()) {
				if (e.getOption().equalsIgnoreCase("Investigate"))
					p.sendMessage("It appears to need 50 mining to clear...");
				else //Clear rocks
					WorldTasks.scheduleTimer(i -> {
						if (i == 0)
							p.lock();
						if (i == 1)
							p.faceObject(obj);
						if (i == 2)
							p.setNextAnimation(new Animation(625));
						if (i == 5)
							obj.setId(472);
						if (i == 9)
							obj.setId(473);
						if (i == 12)
							obj.setId(474);
						if (i == 15) {
							p.setRun(false);
							if (p.getX() < obj.getX())
								p.addWalkSteps(WorldTile.of(2840, p.getY() - 1, 0), 4, false);
							else
								p.addWalkSteps(WorldTile.of(2837, p.getY() + 1, 0), 4, false);
						}
						if (i == 19) {
							obj.setId(473);
							p.setRun(hasRun);
							p.unlock();
						}
						if (i == 20)
							obj.setId(472);
						if (i == 21) {
							obj.setId(id);
							return false;
						}
						return true;
					});
			}
		}
	};

	public static ObjectClickHandler handleIceQueenLairLadders = new ObjectClickHandler(new Object[]{20987, 33184, 100}) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch (e.getObjectId()) {
				case 20987 -> {
					e.getPlayer().useLadder(WorldTile.of(e.getPlayer().getX(), e.getPlayer().getY() + 6400, 0));
				}
				case 33184 -> {
					e.getPlayer().useLadder(WorldTile.of(e.getPlayer().getX(), e.getPlayer().getY() - 6400, 0));
				}
			}
		}
	};

	public static NPCInstanceHandler aggroWarriors = new NPCInstanceHandler(145) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			NPC warrior = new NPC(npcId, tile);
			warrior.setForceAgressive(true);
			return warrior;
		}
	};
}
