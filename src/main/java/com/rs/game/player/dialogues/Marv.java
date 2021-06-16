package com.rs.game.player.dialogues;

public class Marv extends Dialogue {

	int npcId;

	@Override
	public void start() {
		if (!player.isTalkedWithMarv())
			sendEntityDialogue(IS_NPC, "Marv", 13986, 9827, "Ah, a new face eager to take on the Crucible no doubt. You wealthy?");
		else if ((boolean) parameters[0]) {
			player.getInterfaceManager().sendInterface(1297);
			end();
		} else
			// TODO
			end();
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendPlayerDialogue(9827, "What? I guess... Wait - who are you, and why are you hiding?");
			break;
		case 0:
			stage = 1;
			sendEntityDialogue(IS_NPC, "Marv", 13986, 9827, "We don't hide, my friend. We are the guardians of the Crucible, and this has been a dangerous place in the past. We're just protecting our best interests.");
			break;
		case 1:
			stage = 2;
			sendPlayerDialogue(9827, "Okay, no offence meant. So what is this 'Crucible'?");
			break;
		case 2:
			stage = 3;
			sendEntityDialogue(IS_NPC, "Marv", 13986, 9827, "A better question. You're in for a treat, my friend. Beyod that doorway you'll find an arena fit for slaying others just like yourself for fame and riches.");
			break;
		case 3:
			stage = 4;
			sendEntityDialogue(IS_NPC, "Marv", 13986, 9827, "And if you prove yourself worthy, we'll bestow the power of the Crucible upon you.");
			break;
		case 4:
			stage = 5;
			sendPlayerDialogue(9827, "Right. Where do I get started?");
			break;
		case 5:
			stage = 6;
			sendEntityDialogue(IS_NPC, "Marv", 13986, 9827, "Hold on, my friend. You're wealthy, right? Got plenty of expensive armour and stacks of money?");
			break;
		case 6:
			stage = 7;
			sendPlayerDialogue(9827, "Why's that so important?");
			break;
		case 7:
			stage = 8;
			sendEntityDialogue(IS_NPC, "Marv", 13986, 9827, "Oh, it's not, really. I just like to make sure you're well prepared.");
			break;
		case 8:
			stage = 9;
			sendPlayerDialogue(9827, "Hmm... I have a fair amount.");
			break;
		case 9:
			stage = 10;
			sendEntityDialogue(IS_NPC, "Marv", 13986, 9827, "Excellent. We have a selection of lessons you can go throught, although you can go straight into the arena if you wish.");
			break;
		case 10:
			stage = 11;
			player.setTalkedWithMarv();
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Best I look at what I'm getting into.", "I'll learn as I go.");
			break;
		case 11:
			switch (componentId) {
			case OPTION_1:
				stage = 12;
				sendEntityDialogue(IS_NPC, "Marv", 13986, 9827, "Wise my friend.");
				break;
			case OPTION_2:
			default:
				end();
				break;
			}
			break;
		case 12:
			player.getInterfaceManager().sendInterface(1295);
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
