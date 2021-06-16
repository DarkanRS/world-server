package com.rs.game.player.dialogues;

import com.rs.game.player.content.SkillsDialogue;
import com.rs.lib.game.Item;

public class CreateActionD extends Dialogue {

	private int[] anims;
	private Item[][] materials;
	private Item[][] products;
	private int[] reqs;
	private double[] xp;
	private int skill;
	private int delay;

	public CreateActionD(Item[][] materials, Item[][] products, int delay) {
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = -1;
		this.anims = null;
		this.xp = null;
	}

	public CreateActionD(Item[][] materials, Item[][] products, double[] xp, int skill, int delay) {
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		this.anims = null;
		this.xp = xp;
	}

	public CreateActionD(Item[][] materials, Item[][] products, double[] xp, int[] anims, int skill, int delay) {
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		this.anims = anims;
		this.xp = xp;
	}

	public CreateActionD(Item[][] materials, Item[][] products, double[] xp, int[] anims, int[] reqs, int skill, int delay) {
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		this.anims = anims;
		this.xp = xp;
		this.reqs = reqs;
	}
	
	public CreateActionD(Item[][] materials, Item[][] products, double[] xp, int anim, int skill, int delay) {
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		this.anims = new int[products.length];
		for (int i = 0;i < products.length;i++)
			this.anims[i] = anim;
		this.xp = xp;
	}

	public CreateActionD(Item[][] materials, Item[][] products, double[] xp, int anim, int[] reqs, int skill, int delay) {
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		this.anims = new int[products.length];
		for (int i = 0;i < products.length;i++)
			this.anims[i] = anim;
		this.xp = xp;
		this.reqs = reqs;
	}

	@Override
	public void start() {
		Item[] products = new Item[this.products.length];
		for (int i = 0; i < this.products.length; i++) {
			products[i] = this.products[i][0];
		}
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE_INTERVAL, "Which item would you like to make?", 0, products, null);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int type = SkillsDialogue.getItemSlot(componentId);
		player.getActionManager().setAction(new CreateAction(materials, products, xp, anims, reqs, skill, delay, type));
		end();
	}

	@Override
	public void finish() {

	}
}
