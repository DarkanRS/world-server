// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.dialogues_matrix;

import com.rs.game.content.SkillsDialogue;
import com.rs.lib.game.Item;

public class CreateActionD extends MatrixDialogue {

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
		skill = -1;
		anims = null;
		xp = null;
	}

	public CreateActionD(Item[][] materials, Item[][] products, double[] xp, int skill, int delay) {
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		anims = null;
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
		anims = new int[products.length];
		for (int i = 0;i < products.length;i++)
			anims[i] = anim;
		this.xp = xp;
	}

	public CreateActionD(Item[][] materials, Item[][] products, double[] xp, int anim, int[] reqs, int skill, int delay) {
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		anims = new int[products.length];
		for (int i = 0;i < products.length;i++)
			anims[i] = anim;
		this.xp = xp;
		this.reqs = reqs;
	}

	@Override
	public void start() {
		Item[] products = new Item[this.products.length];
		for (int i = 0; i < this.products.length; i++)
			products[i] = this.products[i][0];
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
