package com.rs.game.player.dialogues;

import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.skills.util.Category;
import com.rs.game.player.content.skills.util.ReqItem;

public class CreationActionD extends Dialogue {

	private int animation;
	private int product;
	private int delay;
	private int material;
	private int gfx = -1;
	private boolean consistentAnim = false;
	private CreationAction customAction;
	private ReqItem[] options;
	private Category category;
	
	public CreationActionD(Category category, int material, int animation, int delay) {
		this.product = -1;
		this.animation = animation;
		this.delay = delay;
		this.material = material;
		this.category = category;
	}

	public CreationActionD(Category category, int material, int animation, int delay, boolean skip) {
		this.product = -1;
		this.animation = animation;
		this.delay = delay;
		this.material = material;
		this.category = category;
	}

	public CreationActionD(int product, int animation, int delay) {
		this.product = product;
		this.animation = animation;
		this.delay = delay;
		this.material = -1;
	}
	
	public CreationActionD(Category category, ReqItem[] options, int animation, int delay) {
		this.category = category;
		this.animation = animation;
		this.options = options;
		this.delay = delay;
		this.material = -1;
		this.product = -1;
	}

	public CreationActionD attachCustomAction(CreationAction action) {
		this.customAction = action;
		return this;
	}
	
	public CreationActionD setConsistentAnimation() {
		this.consistentAnim = true;
		return this;
	}

	public CreationActionD setGraphics(int gfx) {
		this.gfx = gfx;
		return this;
	}

	@Override
	public void start() {
		if (product != -1) {
			SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "What would you like to make?", 28, new ReqItem[] { ReqItem.getRequirements(product) }, null);
		} else if (options != null) {
			SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "What would you like to make?", 28, options, null);
		} else {
			options = ReqItem.getProducts(category, material);
			SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.SELECT, "What would you like to make?", 28, options, null);
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int option = SkillsDialogue.getItemSlot(componentId);
		int quantity = SkillsDialogue.getQuantity(player);
		ReqItem produce = product == -1 ? options[option] : ReqItem.getRequirements(product);
		player.getActionManager().setAction(customAction != null ? customAction : new CreationAction(produce, animation, gfx, delay, quantity).setConsistentAnimation(consistentAnim));
		end();
	}

	@Override
	public void finish() {

	}

}
