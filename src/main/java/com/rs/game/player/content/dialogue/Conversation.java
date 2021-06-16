package com.rs.game.player.content.dialogue;

import java.util.HashMap;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.statements.ItemStatement;
import com.rs.game.player.content.dialogue.statements.NPCStatement;
import com.rs.game.player.content.dialogue.statements.OptionStatement;
import com.rs.game.player.content.dialogue.statements.PlayerStatement;
import com.rs.game.player.content.dialogue.statements.SimpleStatement;
import com.rs.game.player.content.dialogue.statements.Statement;
import com.rs.lib.util.Utils;

public class Conversation {

    public static String DEFAULT_OPTIONS_TITLE = "Choose an option";
    
    private HashMap<String, Dialogue> markedStages;
    protected Player player;
    protected Dialogue current;
    private int npcId;
    
    public Conversation(Dialogue current) {
    	this(null, current);
    }

    public Conversation(Player player) {
        this(player, null);
    }

    public Conversation(Player player, Dialogue current) {
        this.player = player;
        this.current = current;
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
        setFirst(getStage(stageName).getHead());
    }

    public boolean create() {
    	if (current != null) {
    		setFirst(current.getHead());
    		return true;
    	} else
    		return false;
    }
    
    public Dialogue getCurrent() {
    	return current;
    }
    
    public Dialogue getStart() {
    	return current.getHead();
    }

    public void setFirst(Dialogue dialogue) {
        this.current = dialogue;
    }

    public Dialogue getStage(String stageName) {
        return markedStages.get(stageName);
    }

    public Dialogue addNext(String stageName, Dialogue dialogue) {
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
        Dialogue dialogue = new Dialogue(statement);
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
    
    public void addOptions(Options options) {
    	addOptions(null, options);
    }
    
    public Dialogue addNext(Statement statement, Dialogue... options) {
    	return addNext(null, statement, options);
    }
    
    public Dialogue addNext(String name, Statement statement, Dialogue... options) {
    	Dialogue option = addNext(name, statement);
    	for (int i = 0;i < options.length;i++)
    		option.addNext(options[i]);
    	return option;
    }
    
    public Dialogue addNext(Statement statement, Runnable... events) {
    	return addNext(null, statement, events);
    }
    
    public Dialogue addNext(String name, Statement statement, Runnable... events) {
    	Dialogue option = addNext(name, statement);
    	for (int i = 0;i < events.length;i++)
    		option.addNext(events[i]);
    	return option;
    }
    
    public Dialogue addNext(String markedStage) {
    	return addNext(markedStages.get(markedStage));
    }
    
	public Dialogue addOptions(String title, Options options) {
		if (options.getOptions().size() <= 1) {
			for (String opName : options.getOptions().keySet())
				return addNext(options.getOptions().get(opName));
		} else if (options.getOptions().size() <= 5) {
			String[] ops = new String[options.getOptions().keySet().size()];
			options.getOptions().keySet().toArray(ops);
			Dialogue op = new Dialogue(new OptionStatement(title, ops));
			for (String opName : options.getOptions().keySet())
				op.addNext(options.getOptions().get(opName));
			return addNext(op);
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
			return addNext(baseOption);
		}
		return null;
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
        current = current.getNext(current.getStatement() != null ? current.getStatement().getOptionId(componentId) : 0);
        if (current == null) {
            player.endConversation();
            return;
        }
        if (player.getInterfaceManager().containsChatBoxInter())
            player.getInterfaceManager().closeChatBoxInterface();
        current.run(player);
    }

	public void setPlayer(Player player) {
		this.player = player;
	}
}
