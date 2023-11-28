package com.rs.game.content.skills.thieving;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class PickPocketDummy extends PlayerAction {

	private GameObject object;

	private boolean success = false;

	public PickPocketDummy(GameObject object) {
		this.object = object;
	}
	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			success = successful(player);
			player.faceObject(object);
			WorldTasks.delay(0, () -> {
				player.setNextAnimation(getAnimation());
			});
			setActionDelay(player, 2);
			player.lock();
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		int DarrenLightfinger = 11274;
		int commentaryStage = player.getQuestManager().getAttribs(Quest.BUYERS_AND_CELLARS).getI("DarrenLightfingerCommentary");
		boolean commentaryEnabled = player.getQuestManager().getAttribs(Quest.BUYERS_AND_CELLARS).getB("DarrenLightfingerCommentaryEnable");
		int questStage = player.getQuestStage(Quest.BUYERS_AND_CELLARS);
		String[] DarrenLightfingerCommentary = new String[]{
				"Just keep picking that dummy's enormous canvas pockets and I'll give you the benefit of my wisdom.",
				"Remember: loose fingers, tight wrists! Do it again. Again!",
				"Calm but quick it, that's the ticket. Again!",
				"It's the eye of the kyatt, it's the will of the heist... Again!",
				"Handkerchief on, handkerchief off. Well, no, just handkerchief off, actually. Again!",
				"Oom-pah-pah! Oom... Wait, no, that's the second act.",
				"Once more with feeling!",
		};
		if (!success) {
			player.sendMessage("You failed to pick the dummies' pocket.");
		}
		else {
			if(commentaryEnabled) {
				if(commentaryStage >= 6) {
					player.getQuestManager().getAttribs(Quest.BUYERS_AND_CELLARS).setB("DarrenLightfingerCommentaryEnable", false);
				}
				else {
					player.npcDialogue(DarrenLightfinger, HeadE.HAPPY_TALKING, DarrenLightfingerCommentary[player.getQuestManager().getAttribs(Quest.BUYERS_AND_CELLARS).getI("DarrenLightfingerCommentary")]);
					player.getQuestManager().getAttribs(Quest.BUYERS_AND_CELLARS).setI("DarrenLightfingerCommentary", commentaryStage + 1);
					player.getSkills().addXp(Constants.THIEVING, 4);
					return -1;
				}
			}
			if(questStage == 1) {
				player.unlock();
				player.startConversation(new Dialogue()
						.addNPC(DarrenLightfinger, HeadE.HAPPY_TALKING, "That was nicely done, young sir.")
						.addNPC(DarrenLightfinger, HeadE.HAPPY_TALKING, "I think you're quite ready enough for the big wide world. Unless you'd like some advice?")
						.addOptions(ops -> {
							ops.add("What can you teach me?")
									.addNPC(DarrenLightfinger, HeadE.CALM_TALK, "Just keep picking that dummy's enormous canvas pockets and I'll give you the benefit of my wisdom.")
									.addNext(() -> { player.getQuestManager().getAttribs(Quest.BUYERS_AND_CELLARS).setB("DarrenLightfingerCommentaryEnable", true);
										player.getQuestManager().setStage(Quest.BUYERS_AND_CELLARS, 2);
									});
							ops.add("No, I think I've got the hang of this.", () -> player.getQuestManager().setStage(Quest.BUYERS_AND_CELLARS, 2));
						})
				);
			}
			if(questStage == 2) {
				player.playerDialogue(HeadE.CALM_TALK, "Can we get started? I'm ready.");
				player.npcDialogue(DarrenLightfinger, HeadE.HAPPY_TALKING, "Excellent! Have a word with me again in a second, then, and we'll get you on your way.");
			}
			if(player.getSkills().getLevel(Skills.THIEVING) <= 15)
				player.getSkills().addXp(Constants.THIEVING, 4);
			if(player.getSkills().getLevel(Skills.THIEVING) >= 16)
				player.sendMessage("There's not much more you can learn from a static dummy at this point.");
		}
		stop(player);
		return -1;
	}

	@Override
	public void stop(Player player) {
		player.unlock();
		player.setNextFaceEntity(null);
		setActionDelay(player, 1);
	}

	public boolean rollSuccess(Player player) {
		return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul(), 185, 255);
	}

	private boolean successful(Player player) {
		if (!rollSuccess(player))
			return false;
		return true;
	}

	private boolean checkAll(Player player) {
		if (player.isDead() || player.hasFinished() || player.hasPendingHits())
			return false;
		if (player.getAttackedBy() != null && player.inCombat()) {
			player.sendMessage("You can't do this while you're under combat.");
			return false;
		}
		return true;
	}

	private Animation getAnimation() {
		return new Animation(881);
	}
}
