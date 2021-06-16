package com.rs.game.player.content.dialogue.impl.skilling;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.statements.MakeXStatement;

public class MakeXActionD extends Dialogue {
	
	private List<MakeXItem> options = new ArrayList<MakeXItem>();
	
	public MakeXActionD addOption(MakeXItem option) {
		clearChildren();
		options.add(option);
		MakeXItem[] opArr = new MakeXItem[options.size()];
		options.toArray(opArr);
		addNext(new MakeXStatement(opArr, 28), opArr);
		return this;
	}
	
	public boolean isEmpty() {
		return options.isEmpty();
	}

}
