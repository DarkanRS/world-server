package com.rs.game.content.quests.heroesquest.icequeenlair;

import com.rs.game.content.skills.mining.Pickaxe;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class IceQueenDungeon {
	public static ObjectClickHandler handleRockSlide = new ObjectClickHandler(new Object[]{2634}, e -> {
		if (!e.getPlayer().containsTool(1265) && Pickaxe.getBest(e.getPlayer()) == null) {
			e.getPlayer().sendMessage("You do not have a pickaxe...");
			return;
		}
		if (e.getPlayer().getSkills().getLevel(Constants.MINING) < 50)
			return;
		GameObject obj = e.getObject();
		int id = e.getObjectId();
		boolean hasRun = e.getPlayer().getRun();
		if (e.isAtObject()) {
			if (e.getOption().equalsIgnoreCase("Investigate"))
				e.getPlayer().sendMessage("It appears to need 50 mining to clear...");
			else //Clear rocks
				WorldTasks.scheduleTimer(i -> {
					if (i == 0)
						e.getPlayer().lock();
					if (i == 1)
						e.getPlayer().faceObject(obj);
					if (i == 2)
						e.getPlayer().setNextAnimation(new Animation(625));
					if (i == 5)
						obj.setId(472);
					if (i == 9)
						obj.setId(473);
					if (i == 12)
						obj.setId(474);
					if (i == 15) {
						e.getPlayer().setRun(false);
						if (e.getPlayer().getX() < obj.getX())
							e.getPlayer().addWalkSteps(Tile.of(2840, e.getPlayer().getY() - 1, 0), 4, false);
						else
							e.getPlayer().addWalkSteps(Tile.of(2837, e.getPlayer().getY() + 1, 0), 4, false);
					}
					if (i == 19) {
						obj.setId(473);
						e.getPlayer().setRun(hasRun);
						e.getPlayer().unlock();
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
	});

	public static ObjectClickHandler handleIceQueenLairLadders = new ObjectClickHandler(new Object[]{20987, 33184, 100}, e -> {
		switch (e.getObjectId()) {
		case 20987 -> {
			e.getPlayer().useLadder(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() + 6400, 0));
		}
		case 33184 -> {
			e.getPlayer().useLadder(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() - 6400, 0));
		}
	}
	});

	public static NPCInstanceHandler aggroWarriors = new NPCInstanceHandler(145, (npcId, tile) -> {
		NPC warrior = new NPC(npcId, tile);
		warrior.setForceAgressive(true);
		return warrior;
	});
}
