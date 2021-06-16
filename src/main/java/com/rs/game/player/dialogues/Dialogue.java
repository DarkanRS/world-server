package com.rs.game.player.dialogues;

import java.security.InvalidParameterException;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.Player;

public abstract class Dialogue {

	protected Player player;
	protected byte stage = -1;

	public static final int
			REALLY_SAD = 9760, SAD = 9765, DEPRESSED = 9770, WORRIED = 9775, SCARED = 9780, MEAN_FACE = 9785,
			MEAN_HEAD_BANG = 9790, EVIL = 9795, WHAT_THE_CRAP = 9800, CALM = 9805, CALM_TALK = 9810, TOUGH = 9815, SNOBBY = 9820,
			SNOBBY_HEAD_MOVE = 9825, CONFUSED = 9830, DRUNK_HAPPY_TIRED = 9835, TALKING_ALOT = 9845, HAPPY_TALKING = 9850, BAD_ASS = 9855,
			THINKING = 9860, COOL_YES = 9864, LAUGH_EXCITED = 9851, SECRELTY_TALKING = 9838;

	public static final int NORMAL = 9827, QUESTIONS = 9827, MAD = 9789, MOCK = 9878, LAUGHING = 9851;
	
	public Dialogue() {

	}

	public Object[] parameters;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public abstract void start();

	public abstract void run(int interfaceId, int componentId);

	public abstract void finish();

	protected final void end() {
		player.getDialogueManager().finishDialogue();
	}

	protected static final short SEND_1_TEXT_INFO = 210;
	protected static final short SEND_2_TEXT_INFO = 211;
	protected static final short SEND_3_TEXT_INFO = 212;
	protected static final short SEND_4_TEXT_INFO = 213;
	protected static final short SEND_2_ITEM = 131;
	protected static final String SEND_DEFAULT_OPTIONS_TITLE = "Select an Option";
	protected static final short SEND_2_OPTIONS = 236;
	protected static final short SEND_3_OPTIONS = 230;
	protected static final short SEND_4_OPTIONS = 237;
	protected static final short SEND_5_OPTIONS = 238;
	protected static final short SEND_2_LARGE_OPTIONS = 229;
	protected static final short SEND_3_LARGE_OPTIONS = 231;
	protected static final short SEND_1_TEXT_CHAT = 241;
	protected static final short SEND_2_TEXT_CHAT = 242;
	protected static final short SEND_3_TEXT_CHAT = 243;
	protected static final short SEND_4_TEXT_CHAT = 244;
	protected static final short SEND_NO_CONTINUE_1_TEXT_CHAT = 245;
	protected static final short SEND_NO_CONTINUE_2_TEXT_CHAT = 246;
	protected static final short SEND_NO_CONTINUE_3_TEXT_CHAT = 247;
	protected static final short SEND_NO_CONTINUE_4_TEXT_CHAT = 248;
	protected static final short SEND_NO_EMOTE = -1;
	protected static final byte IS_NOTHING = -1;
	public static final byte IS_PLAYER = 0;
	public static final byte IS_NPC = 1;
	public static final byte IS_ITEM = 2;

	private static int[] getIComponentsIds(short interId) {
		int[] childOptions;
		switch (interId) {
		case SEND_1_TEXT_INFO:
			childOptions = new int[1];
			childOptions[0] = 1;
			break;
		case SEND_2_TEXT_INFO:
			childOptions = new int[2];
			childOptions[0] = 1;
			childOptions[1] = 2;
			break;
		case SEND_3_TEXT_INFO:
			childOptions = new int[3];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			break;
		case SEND_4_TEXT_INFO:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_2_LARGE_OPTIONS:
			childOptions = new int[3];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			break;
		case SEND_3_LARGE_OPTIONS:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_2_OPTIONS:
			childOptions = new int[3];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			break;
		case SEND_3_OPTIONS:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_4_OPTIONS:
			childOptions = new int[5];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			childOptions[3] = 3;
			childOptions[4] = 4;
			break;
		case SEND_5_OPTIONS:
			childOptions = new int[6];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			childOptions[3] = 3;
			childOptions[4] = 4;
			childOptions[5] = 5;
			break;
		case SEND_1_TEXT_CHAT:
		case SEND_NO_CONTINUE_1_TEXT_CHAT:
			childOptions = new int[2];
			childOptions[0] = 3;
			childOptions[1] = 4;
			break;
		case SEND_2_TEXT_CHAT:
		case SEND_NO_CONTINUE_2_TEXT_CHAT:
			childOptions = new int[3];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			break;
		case SEND_3_TEXT_CHAT:
		case SEND_NO_CONTINUE_3_TEXT_CHAT:
			childOptions = new int[4];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			break;
		case SEND_4_TEXT_CHAT:
		case SEND_NO_CONTINUE_4_TEXT_CHAT:
			childOptions = new int[5];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			childOptions[4] = 7;
			break;
		default:
			return null;
		}
		return childOptions;
	}

	public boolean sendNPCDialogue(int npcId, int animationId, String... text) {
		return sendEntityDialogue(IS_NPC, npcId, animationId, text);
	}

	public boolean sendItemDialogue(int itemId, String... text) {
		return sendEntityDialogue(IS_ITEM, itemId, -1, text);
	}

	public boolean sendPlayerDialogue(int animationId, String... text) {
		return sendEntityDialogue(IS_PLAYER, -1, animationId, text);
	}
	
	public static boolean sendNPCDialogue(Player player, int npcId, int animationId, String... text) {
		return sendEntityDialogue(player, IS_NPC, npcId, animationId, text);
	}

	public static boolean sendItemDialogue(Player player, int itemId, String... text) {
		return sendEntityDialogue(player, IS_ITEM, itemId, -1, text);
	}

	public static boolean sendPlayerDialogue(Player player, int animationId, String... text) {
		return sendEntityDialogue(player, IS_PLAYER, -1, animationId, text);
	}

	/*
	 * 
	 * auto selects title, new dialogues
	 */
	public boolean sendEntityDialogue(int type, int entityId, int animationId, String... text) {
		String title = "";
		if (type == IS_PLAYER) {
			title = player.getDisplayName();
		} else if (type == IS_NPC) {
			title = NPCDefinitions.getDefs(entityId).getName(player.getVars());
		} else if (type == IS_ITEM)
			title = ItemDefinitions.getDefs(entityId).getName();
		return sendEntityDialogue(type, title, entityId, animationId, text);
	}
	
	public static boolean sendEntityDialogue(Player player, int type, int entityId, int animationId, String... text) {
		String title = "";
		if (type == IS_PLAYER) {
			title = player.getDisplayName();
		} else if (type == IS_NPC) {
			title = NPCDefinitions.getDefs(entityId).getName(player.getVars());
		} else if (type == IS_ITEM)
			title = ItemDefinitions.getDefs(entityId).getName();
		return sendEntityDialogue(player, type, title, entityId, animationId, text);
	}

	/*
	 * idk what it for
	 */
	public int getP() {
		return 1;
	}

	public static final int OPTION_1 = 11, OPTION_2 = 13, OPTION_3 = 14, OPTION_4 = 15, OPTION_5 = 16;

	public static boolean sendOptionsDialogue(Player player, String title, String... options) {
		if (options.length > 5) {
			throw new InvalidParameterException("The max options length is 5.");
		}
		String[] optionArray = new String[5];
		for (int i = 0; i < 5; i++)
			optionArray[i] = "";
		int ptr = 0;
		for (String s : options) {
			if (s != null) {
				optionArray[ptr++] = s;
			}
		}
		player.getInterfaceManager().sendChatBoxInterface(1188);
		player.getPackets().setIFText(1188, 20, title);
		player.getPackets().sendRunScriptReverse(5589, optionArray[4], optionArray[3], optionArray[2], optionArray[1], optionArray[0], options.length);
		return true;
	}

	public boolean sendOptionsDialogue(String title, String... options) {
		if (options.length > 5) {
			throw new InvalidParameterException("The max options length is 5.");
		}
		String[] optionArray = new String[5];
		for (int i = 0; i < 5; i++)
			optionArray[i] = "";
		int ptr = 0;
		for (String s : options) {
			if (s != null) {
				optionArray[ptr++] = s;
			}
		}
		player.getInterfaceManager().sendChatBoxInterface(1188);
		player.getPackets().setIFText(1188, 20, title);
		player.getPackets().sendRunScriptReverse(5589, optionArray[4], optionArray[3], optionArray[2], optionArray[1], optionArray[0], options.length);
		return true;
	}

	public static boolean sendNPCDialogueNoContinue(Player player, int npcId, int animationId, String... text) {
		return sendEntityDialogueNoContinue(player, IS_NPC, npcId, animationId, text);
	}

	public static boolean sendPlayerDialogueNoContinue(Player player, int animationId, String... text) {
		return sendEntityDialogueNoContinue(player, IS_PLAYER, -1, animationId, text);
	}

	/*
	 * 
	 * auto selects title, new dialogues
	 */
	public static boolean sendEntityDialogueNoContinue(Player player, int type, int entityId, int animationId, String... text) {
		String title = "";
		if (type == IS_PLAYER) {
			title = player.getDisplayName();
		} else if (type == IS_NPC) {
			title = NPCDefinitions.getDefs(entityId).getName();
		} else if (type == IS_ITEM)
			title = ItemDefinitions.getDefs(entityId).getName();
		return sendEntityDialogueNoContinue(player, type, title, entityId, animationId, text);
	}

	public static boolean sendEntityDialogueNoContinue(Player player, int type, String title, int entityId, int animationId, String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append(" " + texts[line]);
		String text = builder.toString();
		player.getInterfaceManager().replaceRealChatBoxInterface(1192);
		player.getPackets().setIFText(1192, 16, title);
		player.getPackets().setIFText(1192, 12, text);
		if (type == IS_PLAYER)
			player.getPackets().setIFPlayerHead(1192, 11);
		else
			player.getPackets().setIFNPCHead(1192, 11, entityId);
		if (animationId != -1)
			player.getPackets().setIFAnimation(animationId, 1192, 11);
		return true;
	}

	public static void closeNoContinueDialogue(Player player) {
		player.getInterfaceManager().closeReplacedRealChatBoxInterface();
	}

	public boolean send1ItemDialogue(int itemId1, String title, String... texts) {
		player.getInterfaceManager().sendChatBoxInterface(519);
		String text = "";
		text += title+"<br>";
		for (String s : texts) {
			text += s + "<br>";
		}
		player.getPackets().setIFText(519, 1, text);
		player.getPackets().setIFItem(519, 0, itemId1, 1);
		return true;
	}

	public boolean send2ItemDialogue(int itemId1, int itemId2, String title, String... texts) {
		player.getInterfaceManager().sendChatBoxInterface(131);
		String text = "";
		text += title+"<br>";
		for (String s : texts) {
			text += s + "<br>";
		}
		player.getPackets().setIFText(131, 1, text);
		player.getPackets().setIFItem(131, 0, itemId1, 1);
		player.getPackets().setIFItem(131, 2, itemId2, 1);
		return true;
	}

	/*
	 * new dialogues
	 */
	public boolean sendEntityDialogue(int type, String title, int entityId, int animationId, String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append(" " + texts[line]);
		String text = builder.toString();
		if (type == IS_NPC) {
			player.getInterfaceManager().sendChatBoxInterface(1184);
			player.getPackets().setIFText(1184, 17, title);
			player.getPackets().setIFText(1184, 13, text);
			player.getPackets().setIFNPCHead(1184, 11, entityId);
			if (animationId != -1)
				player.getPackets().setIFAnimation(animationId, 1184, 11);
		} else if (type == IS_PLAYER) {
			player.getInterfaceManager().sendChatBoxInterface(1191);
			player.getPackets().setIFText(1191, 8, title);
			player.getPackets().setIFText(1191, 17, text);
			player.getPackets().setIFPlayerHead(1191, 15);
			if (animationId != -1)
				player.getPackets().setIFAnimation(animationId, 1191, 15);
		}
		return true;
	}
	
	public static boolean sendEntityDialogue(Player player, int type, String title, int entityId, int animationId, String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append(" " + texts[line]);
		String text = builder.toString();
		if (type == IS_NPC) {
			player.getInterfaceManager().sendChatBoxInterface(1184);
			player.getPackets().setIFText(1184, 17, title);
			player.getPackets().setIFText(1184, 13, text);
			player.getPackets().setIFNPCHead(1184, 11, entityId);
			if (animationId != -1)
				player.getPackets().setIFAnimation(animationId, 1184, 11);
		} else if (type == IS_PLAYER) {
			player.getInterfaceManager().sendChatBoxInterface(1191);
			player.getPackets().setIFText(1191, 8, title);
			player.getPackets().setIFText(1191, 17, text);
			player.getPackets().setIFPlayerHead(1191, 15);
			if (animationId != -1)
				player.getPackets().setIFAnimation(animationId, 1191, 15);
		}
		player.getTemporaryAttributes().put("staticDialogue", true);
		return true;
	}

	public boolean sendDialogue(String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append((line == 0 ? "<p=" + getP() + ">" : "<br>") + texts[line]);
		String text = builder.toString();
		player.getInterfaceManager().sendChatBoxInterface(1186);
		player.getPackets().setIFText(1186, 1, text);
		return true;
	}

	public boolean sendEntityDialogue(short interId, String[] talkDefinitons, byte type, int entityId, int animationId) {
		if (type == IS_PLAYER || type == IS_NPC) {
			String[] texts = new String[talkDefinitons.length - 1];
			for (int i = 0; i < texts.length; i++)
				texts[i] = talkDefinitons[i + 1];
			sendEntityDialogue(type, talkDefinitons[0], entityId, animationId, texts);
			return true;
		}
		int[] componentOptions = getIComponentsIds(interId);
		if (componentOptions == null)
			return false;
		player.getInterfaceManager().sendChatBoxInterface(interId);
		if (talkDefinitons.length != componentOptions.length)
			return false;
		for (int childOptionId = 0; childOptionId < componentOptions.length; childOptionId++)
			player.getPackets().setIFText(interId, componentOptions[childOptionId], talkDefinitons[childOptionId]);
		if (type == IS_PLAYER || type == IS_NPC) {
			if (type == IS_PLAYER)
				player.getPackets().setIFPlayerHead(interId, 2);
			else
				player.getPackets().setIFNPCHead(interId, 2, entityId);
			if (animationId != -1)
				player.getPackets().setIFAnimation(animationId, interId, 2);
		} else if (type == IS_ITEM)
			player.getPackets().setIFItem(interId, 2, entityId, animationId);
		return true;
	}

}
