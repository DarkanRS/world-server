package com.rs.game.player.content.dialogue;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.statements.ItemStatement;
import com.rs.game.player.content.dialogue.statements.NPCStatement;
import com.rs.game.player.content.dialogue.statements.OptionStatement;
import com.rs.game.player.content.dialogue.statements.PlayerStatement;
import com.rs.game.player.content.dialogue.statements.SimpleStatement;
import com.rs.game.player.content.dialogue.statements.Statement;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public class Dialogue {
    private Dialogue prev;
    private ArrayList<Dialogue> next = new ArrayList<>();
    private Runnable event;
    private Statement statement;
    private boolean started = true;

    public Dialogue(Statement statement, Runnable extraFunctionality) {
        this.statement = statement;
        this.event = extraFunctionality;
    }

    public Dialogue(Statement statement) {
        this(statement, () -> { });
    }
    
    public Dialogue(Runnable event) {
    	this(null, event);
    }

    public Dialogue() {
    	this(null, () -> { });
    	this.started = false;
    }
    
    public void clearChildren() {
    	this.started = false;
    	this.event = null;
    	this.statement = null;
    	this.next.clear();
    }

    public Dialogue setFunc(Runnable consumer) {
        this.event = consumer;
        return this;
    }
    
    public Dialogue addNext(Statement statement, Dialogue... options) {
    	Dialogue option = addNext(statement);
    	for (int i = 0;i < options.length;i++)
    		option.addNext(options[i]);
    	return option;
    }
    
    public Dialogue addNext(Statement statement, Runnable... events) {
    	Dialogue option = addNext(statement);
    	for (int i = 0;i < events.length;i++)
    		option.addNext(events[i]);
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
	
	public Dialogue addOptions(Options options) {
		return addOptions(null, options);
	}
	
	public Dialogue addOptions(String title, Options options) {
		if (options.getOptions().size() <= 1) {
			for (String opName : options.getOptions().keySet())
				addNext(options.getOptions().get(opName));
		} else if (options.getOptions().size() <= 5) {
			String[] ops = new String[options.getOptions().keySet().size()];
			options.getOptions().keySet().toArray(ops);
			Dialogue op = new Dialogue(new OptionStatement(title, ops));
			for (String opName : options.getOptions().keySet())
				op.addNext(options.getOptions().get(opName));
			addNext(op);
		} else {
			String[] ops = new String[options.getOptions().keySet().size()];
			options.getOptions().keySet().toArray(ops);
			String[] baseOptions = new String[5];
			for (int i = 0;i < 4;i++) {
				baseOptions[i] = ops[i];
			}
			baseOptions[4] = "More options...";
			Dialogue baseOption = new Dialogue(new OptionStatement(title, baseOptions));
			Dialogue currPage = baseOption;
			for (int i = 0;i < ops.length;i++) {
				currPage.addNext(options.getOptions().get(ops[i]));
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
			currPage.addNext(baseOption);
			addNext(baseOption);
		}
		return this;
	}

    public Dialogue addNext(Statement statement) {
        if (!this.started) {
        	this.statement = statement;
        	this.started = true;
        	return this;
        }
        Dialogue nextD = new Dialogue(statement);
        if (nextD.getPrev() == null)
            nextD.setPrev(this);
        next.add(nextD);
        return nextD;
    }

    public Dialogue addNext(Dialogue dialogue) {
    	if (!this.started) {
    		this.statement = dialogue.statement;
    		this.event = dialogue.event;
    		this.next = dialogue.next;
    		this.started = true;
    		return this;
    	}
        if (dialogue.getPrev() == null)
            dialogue.setPrev(this);
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
        return this.getHead();
    }

    public Dialogue getHead() {
        if (prev == null)
            return this;
        return prev.getHead();
    }

    public Dialogue getNext(int choice) {
        if (next.size() > 0 && choice < next.size())
            return next.get(choice);
        return null;
    }

    public void run(Player player) {
    	if (event != null)
    		event.run();
        if (statement != null) {
            statement.send(player);
        }
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
    	String stmt = statement == null ? "none" : statement.getClass().getSimpleName();
    	String str = "[Dialogue] { stmt: " + stmt + " next: [ ";
    	for (Dialogue d : next) {
    		if (d == null)
    			continue;
    		str += d.getClass().getSimpleName() + ", ";
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
}
