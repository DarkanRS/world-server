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
package com.rs.game.content.dialogue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.rs.game.content.dialogue.impl.StageSelectDialogue;
import com.rs.game.content.dialogue.statements.ItemStatement;
import com.rs.game.content.dialogue.statements.NPCStatement;
import com.rs.game.content.dialogue.statements.OptionStatement;
import com.rs.game.content.dialogue.statements.PlayerStatement;
import com.rs.game.content.dialogue.statements.SimpleStatement;
import com.rs.game.content.dialogue.statements.Statement;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public class Dialogue {

	private Dialogue prev;
	private ArrayList<Dialogue> next = new ArrayList<>();
	private Runnable event;
	private Statement statement;
	private int voiceEffectId = -1;
	private boolean started = true;

	public Dialogue(Statement statement, Runnable extraFunctionality) {
		this.statement = statement;
		event = extraFunctionality;
	}

	public Dialogue(Statement statement) {
		this(statement, () -> { });
	}

	public Dialogue(Runnable event) {
		this(null, event);
	}

	public Dialogue() {
		this(null, () -> { });
		started = false;
	}

	public Dialogue(Dialogue dialogue) {
		prev = dialogue.prev;
		next = dialogue.next;
		event = dialogue.event;
		statement = dialogue.statement;
		started = dialogue.started;
	}

	public void clearChildren() {
		started = false;
		event = null;
		statement = null;
		next.clear();
	}

	public Dialogue setFunc(Runnable consumer) {
		event = consumer;
		return this;
	}
	
	public Runnable getFunc() {
		return event;
	}

	public Dialogue addGotoStage(String stageName, Conversation conversation) {
		return addNext(new StageSelectDialogue(stageName, conversation));
	}

	public Dialogue addStatementWithOptions(Statement statement, Dialogue... options) {
		Dialogue option = addNext(statement);
		for (Dialogue option2 : options)
			option.addNext(option2);
		return option;
	}

	public Dialogue addStatementWithActions(Statement statement, Runnable... events) {
		Dialogue option = addNext(statement);
		for (Runnable event2 : events)
			option.addNext(event2);
		return option;
	}

	public Dialogue addPlayer(HeadE expression, String text) {
		return addNext(new PlayerStatement(expression, text));
	}

	public Dialogue addPlayer(HeadE expression, String text, Runnable extraFunctionality) {
		return addNext(new Dialogue(new PlayerStatement(expression, text), extraFunctionality));
	}

	public Dialogue addOption(String title, String... options) {
		return addNext(new OptionStatement(title, options));
	}

	public Dialogue addNPC(int npcId, HeadE expression, String text) {
		return addNext(new NPCStatement(npcId, expression, text));
	}

	public Dialogue addNPC(int npcId, HeadE expression, String text, Runnable extraFunctionality) {
		return addNext(new Dialogue(new NPCStatement(npcId, expression, text), extraFunctionality));
	}

	public Dialogue addItem(int itemId, String text) {
		return addNext(new ItemStatement(itemId, text));
	}

	public Dialogue addItem(int itemId, String text, Runnable extraFunctionality) {
		return addNext(new Dialogue(new ItemStatement(itemId, text), extraFunctionality));
	}

	public Dialogue addSimple(String... text) {
		return addNext(new SimpleStatement(text));
	}

	public Dialogue addSimple(String text, Runnable extraFunctionality) {
		return addNext(new Dialogue(new SimpleStatement(text), extraFunctionality));
	}

	public Dialogue addItemToInv(Player player, Item item, String text) {
		return addNext(new ItemStatement(item.getId(), text)).setFunc(() -> {
			player.getInventory().addItem(item);
		});
	}
	
	public Dialogue addOptions(Consumer<Options> create) {
		Options options = new Options() {
			@Override
			public void create() {
				create.accept(this);
			}
		};
		return addOptions(null, options);
	}

	public Dialogue addOptions(Options options) {
		return addOptions(null, options);
	}
	
	public Dialogue addOptions(String title, Consumer<Options> create) {
		Options options = new Options() {
			@Override
			public void create() {
				create.accept(this);
			}
		};
		return addOptions(title, options);
	}
	
	public Dialogue addOptions(String stageName, Conversation conv, String title, Consumer<Options> create) {
		Options options = new Options(stageName, conv) {
			@Override
			public void create() {
				create.accept(this);
			}
		};
		return addOptions(title, options);
	}

	public Dialogue addOptions(String title, Options options) {
		if (options.getOptions().size() <= 1) {
			for (String opName : options.getOptions().keySet()) {
				Option op = options.getOptions().get(opName);
				if (op.show() && op.getDialogue() != null)
					addNext(op.getDialogue());
			}
			if (options.getConv() != null)
				options.getConv().addStage(options.getStageName(), getNext(0));
		} else if (options.getOptions().size() <= 5) {
			List<String> ops = new ArrayList<>();
			for (String opName : options.getOptions().keySet()) {
				Option o = options.getOptions().get(opName);
				if (o.show())
					ops.add(opName);
			}
			Dialogue op = new Dialogue(new OptionStatement(title, ops.stream().toArray(String[] ::new)));
			for (String opName : options.getOptions().keySet()) {
				Option o = options.getOptions().get(opName);
				if (o.show() && o.getDialogue() != null)
					op.addNext(o.getDialogue());
			}
			addNext(op);
			if (options.getConv() != null)
				options.getConv().addStage(options.getStageName(), op);
		} else {
			String[] ops = new String[options.getOptions().keySet().size()];
			options.getOptions().keySet().toArray(ops);
			String[] baseOptions = new String[5];
			for (int i = 0;i < 4;i++)
				baseOptions[i] = ops[i];
			baseOptions[4] = "More options...";
			Dialogue baseOption = new Dialogue(new OptionStatement(title, baseOptions));
			Dialogue currPage = baseOption;
			for (int i = 0;i < ops.length;i++) {
				Option op = options.getOptions().get(ops[i]);
				if (op.show() && op.getDialogue() != null) {
					currPage.addNext(op.getDialogue());
					if (i >= 3 && ((i+1) % 4) == 0) {
						String[] nextOps = new String[Utils.clampI(ops.length-i, 0, 5)];
						for (int j = 1;j < 5;j++) {
							if (i+j >= ops.length)
								continue;
							nextOps[j-1] = ops[i+j];
						}
						nextOps[nextOps.length-1] = "More options...";
						Dialogue nextPage = new Dialogue(new OptionStatement(title, nextOps));
						currPage.addNext(nextPage);
						currPage = nextPage;
					}
				}
			}
			currPage.addNext(baseOption);
			addNext(baseOption);
			if (options.getConv() != null)
				options.getConv().addStage(options.getStageName(), baseOption);
		}
		return this;
	}

	public Dialogue addNext(Statement statement) {
		if (!started) {
			this.statement = statement;
			started = true;
			return this;
		}
		Dialogue nextD = new Dialogue(statement);
		if (nextD.getPrev() == null)
			nextD.setPrev(this);
		next.add(nextD);
		return nextD;
	}

	public Dialogue addNext(Dialogue dialogue) {
		if (!started) {
			if (dialogue instanceof StageSelectDialogue)
				return dialogue;
			statement = dialogue.statement;
			event = dialogue.event;
			next = dialogue.next;
			started = true;
			return this;
		}
		if (dialogue.getPrev() == null)
			dialogue.setPrev(this);
		else {
			Dialogue copy = new Dialogue(this);
			dialogue.setPrev(copy);
		}
		next.add(dialogue);
		return dialogue;
	}

	public Dialogue addNext(Runnable event) {
		Dialogue dialogue = new Dialogue();
		dialogue.statement = null;
		dialogue.event = event;
		return addNext(dialogue);
	}

	public Dialogue finish() {
		return getHead();
	}

	public Dialogue getHead() {
		if (prev == null)
			return this;
		Set<Dialogue> visited = new HashSet<>();
		Dialogue curr = this;
		while(curr.getPrev() != null) {
			if (visited.contains(curr))
				break;
			visited.add(curr);
			curr = curr.getPrev();
		}
		return curr;
	}

	public Dialogue getNext(int choice) {
		if (next.size() > 0 && choice < next.size())
			return next.get(choice);
		return null;
	}

	public void run(Player player) {
		if (event != null)
			event.run();
		if (statement != null)
			statement.send(player);
		if (voiceEffectId != -1)
			player.playSound(voiceEffectId, 2);
	}

	public Dialogue getPrev() {
		return prev;
	}

	public void setPrev(Dialogue prev) {
		this.prev = prev;
	}

	public Statement getStatement() {
		return statement;
	}

	@Override
	public String toString() {
		String str = "[Dialogue] { stmt: " + statement + " next: [ ";
		for (Dialogue d : next) {
			if (d == null)
				continue;
			str += d.getClass().getSimpleName() + "("+d.getStatement()+")\n\t";
		}
		str += " ] }";
		return str;
	}

	public Dialogue cutPrev() {
		Dialogue isolated = new Dialogue();
		isolated.addNext(this);
		isolated.prev = null;
		return isolated;
	}

	public Dialogue setStage(String stageName, Conversation conversation) {
		Dialogue copy = new Dialogue(this);
		copy.prev = null;
		conversation.addStage(stageName, copy);
		return this;
	}

	public void close(Player player) {
		if (statement != null)
			statement.close(player);
	}

	public int getVoiceEffect() {
		return voiceEffectId;
	}

	public Dialogue voiceEffect(int voiceId) {
		this.voiceEffectId = voiceId;
		return this;
	}
}
