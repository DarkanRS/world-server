package com.rs.game.content.quests.templeofikov;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class LucienVarrockMob extends NPC {
	private static int NPC = 8347;
	public LucienVarrockMob(int id, Tile tile) {
		super(id, tile, false);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if(source instanceof Player p && p.getQuestManager().getStage(Quest.TEMPLE_OF_IKOV) == TempleOfIkov.HELP_LUCIEN) {
			p.startConversation(new Dialogue().addNPC(NPC, HeadE.FRUSTRATED, "You have defeated me for now! I shall reappear in the North!", ()->{
				TempleOfIkov.setIkovLucienSide(p, false);
				WorldTasks.delay(3, () -> {
					p.getQuestManager().completeQuest(Quest.TEMPLE_OF_IKOV);
				});
			}));
		}
	}

	@Override
	public boolean canBeAttackedBy(Player player) {
		if(player.getEquipment().getAmuletId() == 87)//Armadyl pendant
			return true;
		this.faceEntity(player);
		player.faceEntity(this);
		WorldTasks.delay(1, () -> {
			this.setNextAnimation(new Animation(805));
		});
		player.startConversation(new Dialogue()
				.addNPC(NPC, HeadE.CALM_TALK, "You don't want to attack me. I am your friend.")
				.addSimple("You decide to not attack Lucien. He is your friend.")
		);
		return false;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(8347, (npcId, tile) -> new LucienVarrockMob(npcId, tile));


}
