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
package com.rs.engine.dialogue;

import com.rs.engine.dialogue.statements.*;
import com.rs.engine.quest.Quest;
import com.rs.game.content.world.unorganized_dialogue.StageSelectDialogue;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

import java.util.*;
import java.util.function.Consumer;

public class Conversation {

	public static String DEFAULT_OPTIONS_TITLE = "Choose an option";

	private HashMap<String, Dialogue> markedStages;
	private Dialogue firstDialogue;
	protected Player player;
	protected Dialogue current;
	protected int npcId;
	private boolean created = false;

	public Conversation(Dialogue current) {
		this(null, current);
	}

	public Conversation(Player player) {
		this(player, null);
	}

	public Conversation(Player player, Dialogue current) {
		this.player = player;
		this.current = current;
		this.firstDialogue = current;
		this.markedStages = new HashMap<>();
	}

	public Conversation(Statement statement) {
		this(null, new Dialogue(statement));
	}

	public Dialogue create(String stageName, Dialogue dialogue) {
		markedStages.put(stageName, dialogue);
		return dialogue;
	}

	public Dialogue createStage(String stageName, Statement statement) {
		Dialogue dialogue = new Dialogue(statement);
		markedStages.put(stageName, dialogue);
		return dialogue;
	}

	public void create(String stageName) {
		created = true;
		setFirst(getStage(stageName).getHead());
	}

	public boolean create() {
		try {
			created = true;
			if (current != null) {
				setFirst(firstDialogue);
				return true;
			}
			return false;
		} catch(Exception e) {
			throw new RuntimeException("Error creating dialogue: " + getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

	public Dialogue getCurrent() {
		return current;
	}

	public Dialogue getStart() {
		try {
			return current.getHead();
		} catch(Exception e) {
			throw new RuntimeException("Error creating dialogue: " + getClass().getSimpleName() + " - " + e.getMessage());
		}
	}

	public void setFirst(Dialogue dialogue)  {
		current = dialogue;
	}

	public Dialogue getStage(String stageName) {
		return markedStages.get(stageName);
	}

	public Dialogue addNext(String stageName, Dialogue dialogue) {
		if (firstDialogue == null)
			firstDialogue = dialogue;
		if (markedStages == null)
			throw new RuntimeException("Do not call builder functions outside the constructor.");
		if (stageName != null)
			markedStages.put(stageName, dialogue);
		if (current == null)
			current = dialogue;
		else
			current = current.addNext(dialogue);
		return current;
	}

	public Dialogue addNext(Dialogue dialogue) {
		return addNext(null, dialogue);
	}

	public Dialogue addNext(Runnable event) {
		return addNext(new Dialogue(event));
	}

	public Dialogue addNext(String stageName, Statement statement) {
		if (markedStages == null)
			throw new RuntimeException("Do not call builder functions outside the constructor.");
		Dialogue dialogue = new Dialogue(statement);
		if (firstDialogue == null)
			firstDialogue = dialogue;
		if (stageName != null)
			markedStages.put(stageName, dialogue);
		if (current == null)
			current = dialogue;
		else
			current = current.addNext(dialogue);
		return current;
	}

	public Dialogue addNext(Statement statement) {
		return addNext(null, statement);
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

	public Dialogue addOptions(Options options) {
		return addOptions(null, options);
	}

	public Dialogue addNext(Statement statement, Dialogue... options) {
		return addNext(null, statement, options);
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

	public Dialogue addOptions(String title, Consumer<Options> create) {
		Options options = new Options() {
			@Override
			public void create() {
				create.accept(this);
			}
		};
		return addOptions(title, options);
	}
	
	public Dialogue addOptions(Conversation conversation, String stageName, Consumer<Options> create) {
		Options options = new Options(stageName, conversation) {
			@Override
			public void create() {
				create.accept(this);
			}
		};
		return addOptions(DEFAULT_OPTIONS_TITLE, options);
	}
	
	public Dialogue addOptions(String stageName, String title, Consumer<Options> create) {
		Options options = new Options(stageName, this) {
			@Override
			public void create() {
				create.accept(this);
			}
		};
		return addOptions(title, options);
	}
	
	public Dialogue addNext(String name, Statement statement, Dialogue... options) {
		Dialogue option = addNext(name, statement);
		for (Dialogue option2 : options)
			option.addNext(option2);
		return option;
	}

	public Dialogue addNext(Statement statement, Runnable... events) {
		return addNext(null, statement, events);
	}

	public Dialogue addNext(String name, Statement statement, Runnable... events) {
		Dialogue option = addNext(name, statement);
		for (Runnable event : events)
			option.addNext(event);
		return option;
	}

	public Dialogue addNext(String markedStage) {
		return addNext(markedStages.get(markedStage));
	}

	public Dialogue addOptions(String title, Options options) {
		if (options.getOptions().size() <= 1) {
			for (String opName : options.getOptions().keySet()) {
				Option op = options.getOptions().get(opName);
				if (op.show() && op.getDialogue() != null) {
					Dialogue next = addNext(op.getDialogue());
					if (options.getConv() != null)
						options.getConv().addStage(options.getStageName(), next);
					return next;
				}
			}
			return null;
		}
		if (options.getOptions().size() <= 5) {
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
			if (options.getConv() != null)
				options.getConv().addStage(options.getStageName(), op);
			return addNext(op);
		}
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
		if (options.getConv() != null)
			options.getConv().addStage(options.getStageName(), baseOption);
		return addNext(baseOption);
	}

	public Dialogue addItem(int itemId, String text) {
		return addNext(new ItemStatement(itemId, text));
	}

	public Dialogue addItem(int itemId, String text, Runnable extraFunctionality) {
		return addNext(new Dialogue(new ItemStatement(itemId, text), extraFunctionality));
	}

	public Dialogue addNPC(int npcId, HeadE expression, String text) {
		this.npcId = npcId;
		return addNext(new NPCStatement(npcId, expression, text));
	}

	public Dialogue addNPC(int npcId, HeadE expression, String text, Runnable extraFunctionality) {
		this.npcId = npcId;
		return addNext(new Dialogue(new NPCStatement(npcId, expression, text), extraFunctionality));
	}

	public Dialogue addNPC(HeadE expression, String text) {
		return addNext(new NPCStatement(npcId, expression, text));
	}

	public Dialogue addNPC(HeadE expression, String text, Runnable extraFunctionality) {
		return addNext(new Dialogue(new NPCStatement(npcId, expression, text), extraFunctionality));
	}
	
	public Dialogue addNPC(NPC npc, HeadE expression, String text) {
		return addNext(new NPCStatement(npc.getCustomName(), npc.getId(), expression, text));
	}

	public Dialogue addNPC(NPC npc, HeadE expression, String text, Runnable extraFunctionality) {
		return addNext(new Dialogue(new NPCStatement(npc.getCustomName(), npc.getId(), expression, text), extraFunctionality));
	}

	public Dialogue addQuestStart(Quest quest) {
		return addNext(new QuestStartStatement(quest));
	}

	public Dialogue addSimple(String... text) {
		return addNext(new SimpleStatement(text));
	}

	public Dialogue addSimple(String text, Runnable extraFunctionality) {
		return addNext(new Dialogue(new SimpleStatement(text), extraFunctionality));
	}

	public void start() {
		if (current == null)
			return;
		current.run(player);
	}

	public void process(int interfaceId, int componentId) {
		process(current.getStatement() != null ? current.getStatement().getOptionId(componentId) : 0);
	}
	
	public void process(int opIndex) {
		if (current != null) {
			current.close(player);
			if (current.getVoiceEffect() > -1)
				player.getPackets().resetSounds();
		}
		current = current.getNext(opIndex);
		if (current == null) {
			player.endConversation();
			return;
		}
		if (current instanceof StageSelectDialogue d) {
			if (d.getFunc() != null)
				d.getFunc().run();
			current = d.getConversation().getStage(d.getStageName());
		}
		if (player.getInterfaceManager().containsChatBoxInter())
			player.getInterfaceManager().closeChatBoxInterface();
		current.run(player);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void addStage(String stageName, Dialogue dialogue) {
		markedStages.put(stageName, dialogue);
	}

	@Override
	public String toString() {
		Set<Dialogue> visited = new HashSet<>();
		StringBuilder str = new StringBuilder();
		Dialogue head = current;
		Dialogue curr = current;
		while(curr != null) {
			visited.add(curr);
			str.append(curr + "\n");
			curr = curr.getNext(0);
			if (visited.contains(curr))
				break;
		}
		curr = head;
		str.append(created);
		return str.toString();
	}

	public boolean isCreated() {
		return created;
	}
}