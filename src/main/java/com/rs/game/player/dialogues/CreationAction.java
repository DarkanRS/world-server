package com.rs.game.player.dialogues;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.skills.util.ReqItem;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;

public class CreationAction extends Action {

	private Item[] mats;
	private Item product;
	private int nonRemReq;
	private int skill;
	private int cycles, level;
	private double experience;
	private int animationId;
	private int gfx;
	private int delay;
	private boolean consistentAnim = false;

	public CreationAction(ReqItem product, int animationId, int gfx, int delay, int cycles) {
		this.skill = product.getSkill();
		this.mats = product.getMaterials();
		this.level = product.getReq();
		this.experience = product.getXp();
		this.product = product.getProduct();
		this.nonRemReq = product.getTool();
		this.animationId = animationId;
		this.delay = delay;
		this.cycles = cycles;
		this.gfx = gfx;
	}
	
	public CreationAction setConsistentAnimation(boolean consistentAnim) {
		this.consistentAnim = consistentAnim;
		return this;
	}

	public boolean checkAll(Player player) {
		int pLvl = player.getSkills().getLevel(skill);
		if (skill == Constants.SUMMONING)
			pLvl = player.getSkills().getLevelForXp(skill);
		if (pLvl < level) {
			player.sendMessage("You need a " + Constants.SKILL_NAME[skill] + " level of " + level + " to make that.");
			return false;
		}
		return continueNextCycle(player);
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		if (delay == -2) {
			if (animationId > 0) {
				player.setNextAnimation(new Animation(animationId));
			}
			else if (animationId == -1) {
				int anim = -1;
				if (nonRemReq != -1)
					anim = AnimationDefinitions.getAnimationWithItem(nonRemReq);
				if (anim == -1)
					anim = AnimationDefinitions.getAnimationWithItem(product.getId());
				for (Item item : mats) {
					if (item != null) {
						if (anim != -1)
							break;
						anim = AnimationDefinitions.getAnimationWithItem(item.getId());
					}
				}
				if (anim != -1)
					player.setNextAnimation(new Animation(anim));
			}
			if (gfx != -1)
				player.setNextSpotAnim(new SpotAnim(gfx));
			double xp = 0;
			while (cycles > 0) {
				cycles--;
				if (!continueNextCycle(player))
					break;
				for (Item item : mats)
					if (item != null)
						player.getInventory().deleteItem(item);
				xp += experience;
				player.getInventory().addItem(product.getId(), product.getAmount(), true);
			}
			if (xp > 0)
				player.getSkills().addXp(skill, xp);
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return continueNextCycle(player) && cycles > 0;
	}

	private boolean continueNextCycle(Player player) {
		if (!player.getInventory().hasFreeSlots(mats, product))
			return false;
		if (consistentAnim)
			player.setNextAnimation(new Animation(animationId));
		if (mats != null && !player.getInventory().containsItems(mats)) {
			String mat = "";
			for (Item item : mats) {
				if (item != null) {
					mat += item.getAmount() + " ";
					mat += item.getDefinitions().getName() + ", ";
				}
			}
			player.sendMessage("You need " + mat.toLowerCase().trim().substring(0, mat.length() - 2) + " to create a " + product.getName().toLowerCase() + ".");
			return false;
		}
		if (nonRemReq != -1 && !player.getInventory().containsItem(nonRemReq, 1)) {
			player.sendMessage("You require a " + ItemDefinitions.getDefs(nonRemReq).name.toLowerCase() + " to create this.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		cycles--;
		if (animationId > 0)
			player.setNextAnimation(new Animation(animationId));
		else if (animationId == -1) {
			int anim = -1;
			if (nonRemReq != -1)
				anim = AnimationDefinitions.getAnimationWithItem(nonRemReq);
			if (anim == -1)
				anim = AnimationDefinitions.getAnimationWithItem(product.getId());
			for (Item item : mats) {
				if (item != null) {
					if (anim != -1)
						break;
					anim = AnimationDefinitions.getAnimationWithItem(item.getId());
				}
			}
			if (anim != -1)
				player.setNextAnimation(new Animation(anim));
		}
		if (gfx != -1)
			player.setNextSpotAnim(new SpotAnim(gfx));
		for (Item item : mats)
			if (item != null)
				player.getInventory().deleteItem(item);
		player.getSkills().addXp(skill, experience);
		player.getInventory().addItem(product.getId(), product.getAmount());
		return delay;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, delay);
	}
}
