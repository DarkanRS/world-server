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
package com.rs.game.content.skills.util;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.impl.MakeXActionD;
import com.rs.engine.dialogue.impl.MakeXItem;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

public class CreateActionD extends Conversation {

	private int[] anims;
	private Item[][] materials;
	private Item[][] products;
	private int[] reqs;
	private double[] xp;
	private int skill;
	private int delay;

	public CreateActionD(Player player, Item[][] materials, Item[][] products, int delay) {
		super(player);
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		skill = -1;
		anims = null;
		xp = null;
		initialize();
		create();
	}

	public CreateActionD(Player player, Item[][] materials, Item[][] products, double[] xp, int skill, int delay) {
		super(player);
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		anims = null;
		this.xp = xp;
		initialize();
		create();
	}

	public CreateActionD(Player player, Item[][] materials, Item[][] products, double[] xp, int[] anims, int skill, int delay) {
		super(player);
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		this.anims = anims;
		this.xp = xp;
		initialize();
		create();
	}

	public CreateActionD(Player player, Item[][] materials, Item[][] products, double[] xp, int[] anims, int[] reqs, int skill, int delay) {
		super(player);
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		this.anims = anims;
		this.xp = xp;
		this.reqs = reqs;
		initialize();
		create();
	}

	public CreateActionD(Player player, Item[][] materials, Item[][] products, double[] xp, int anim, int skill, int delay) {
		super(player);
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		anims = new int[products.length];
		for (int i = 0;i < products.length;i++)
			anims[i] = anim;
		this.xp = xp;
		initialize();
		create();
	}

	public CreateActionD(Player player, Item[][] materials, Item[][] products, double[] xp, int anim, int[] reqs, int skill, int delay) {
		super(player);
		this.materials = materials;
		this.products = products;
		this.delay = delay;
		this.skill = skill;
		anims = new int[products.length];
		for (int i = 0;i < products.length;i++)
			anims[i] = anim;
		this.xp = xp;
		this.reqs = reqs;
		initialize();
		create();
	}

	public void initialize() {
		MakeXActionD makeX = new MakeXActionD();
		for (int i = 0; i < this.products.length; i++)
			makeX.addOption(new MakeXItem(player, materials[i], products[i], xp == null ? 0.0 : xp[i], anims == null ? -1 : anims[i], reqs == null ? 1 : reqs[i], skill, delay));
		addNext(makeX);
	}
}
