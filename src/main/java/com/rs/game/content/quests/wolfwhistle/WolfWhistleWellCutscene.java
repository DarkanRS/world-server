package com.rs.game.content.quests.wolfwhistle;

import java.util.ArrayList;
import java.util.Random;

import com.rs.game.World;
import com.rs.game.engine.cutscene.Cutscene;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class WolfWhistleWellCutscene extends Cutscene {

	final static int WOLF_MEAT = 15058;
	final static int WOLF_BONES = 15059;
	final static int WOLF_MEAT_SCARED = 15060;
	final static int WOLF_BONES_SCARED = 15061;
	final static int TROLL_FLINTER = 15062;
	final static int TROLL_BONE = 15063;
	//final static int TROLL_CLUB_SHOULDERS = 15064;
	//final static int TROLL_SHIELD_BONE_SHOULDERS = 15065;
	final static int TROLL_CLUB = 15066;
	final static int TROLL_BONE_SHIELD = 15067;
	final static int TROLL_BONE_SCARED = 15068;
	final static int TROLL_CLUB_SCARED = 15069;
	//final static int TROLL_SHIELD_BONE_SHOULDERS_SCARED = 15070;
	// final static int TROLL_CLUB_SCARED_1 = 15071;
	final static int TROLL_SHIELD_BONE_SCARED = 15072;
	final static int BOWLOFTRIX_CAULDRON = 67500;

	final static int SPIRIT_WOLF = 15074;
	final static int GIANT_WOLPERTINGER = 6990;

	final static int SCALECTRIX = 15055;
	final static int BOWLOFTRIX = 15057;

	// spot anims
	final static int SUMMONING_SPAWN = 1315;
	final static int ONE = 2860;
	final static int TWO = 2826;
	final static int WOLPERTINGER_FEAR_FLASH = 2820;

	// animations
	final static int WOLPERTINGER_FEAR_CAST = 15929;
	final static int WOLPERTINGER_DESPAWN = 15930;
	final static int WOLPERTINGER_SPAWN = 15932;
	final static int TROLL_COWER = 15921;
	// 1522

	final String[] TROLL_KEYS = new String[] { "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10", "t11", "t12" };
	final ArrayList<NPC> whiners = new ArrayList<>();

	boolean playing = false;

	@Override
	public void construct(Player player) {
		playing = true;
		fadeInBG(4);
		hideMinimap();
		dynamicRegion(178, 554, 4, 4);

		spawnObj(BOWLOFTRIX_CAULDRON, 0, 14, 23, 0);
		npcCreate("giantwolpertinger", GIANT_WOLPERTINGER, 14, 0, 0);
		npcCreate("scalectrix", SCALECTRIX, 13, 8, 0);
		npcCreate("wolfmeat", WOLF_MEAT, 15, 21, 0);
		npcCreate("wolfbones", WOLF_BONES, 13, 21, 0);
		npcCreate("trollflinter", TROLL_FLINTER, 11, 23, 0);
		npcCreate("t0", TROLL_BONE, 16, 18, 0); // 3 with just bone frontline runner
		npcCreate("t1", TROLL_BONE, 10, 27, 0);
		npcCreate("t2", TROLL_BONE, 15, 25, 0);
		npcCreate("t3", TROLL_BONE_SHIELD, 11, 17, 0); // 2 with bone + shield frontline runner
		npcCreate("t4", TROLL_BONE_SHIELD, 11, 21, 0); // runner
		npcCreate("t5", TROLL_CLUB, 16, 17, 0); // 8 with club frontline runner
		npcCreate("t6", TROLL_CLUB, 10, 23, 0);
		npcCreate("t7", TROLL_CLUB, 11, 25, 0);
		npcCreate("t8", TROLL_CLUB, 13, 25, 0);
		npcCreate("t9", TROLL_CLUB, 18, 22, 0);
		npcCreate("t10", TROLL_CLUB, 15, 28, 0);
		npcCreate("t11", TROLL_CLUB, 13, 28, 0);
		npcCreate("t12", TROLL_CLUB, 9, 19, 0);
		delay(1);
		playerFaceDir(Direction.NORTH);
		playerMove(14, 8, 0, Entity.MoveType.TELE);
		npcFaceDir("scalectrix", Direction.NORTH);
		npcFaceDir("trollflinter", Direction.EAST);
		npcFaceNPC("giantwolpertinger", "wolfbones");

		camPosReset();
		camPos(14, 18, 2000);
		camLook(14, 8, 0);
		delay(1);
		fadeOutBG(4);

		playerMove(14, 14, Entity.MoveType.WALK);
		npcMove("scalectrix", 14, 8, Entity.MoveType.WALK);
		npcMove("scalectrix", 14, 13, Entity.MoveType.WALK);
		npcMove("scalectrix", 13, 13, Entity.MoveType.WALK);
		npcMove("scalectrix", 13, 14, Entity.MoveType.WALK);
		camPos(14, 30, 2000, 0, 20, 2);
		delay(5);
		dialogue(new Dialogue()
						.addNPC(SCALECTRIX, HeadE.CALM_TALK, "This is it, get ready!")
				, true);

		camPos(18, 13, 2000);
		camLook(14, 23, 0);
		dialogue(new Dialogue()
						.addNPC(WOLF_MEAT, HeadE.T_HAPPY_TALK, "It's da girly dat had da woof! We gots more druid meat meat 'fore da big fight, lads!")
				, true);

		camPos(16, 21, 2000);
		camLook(14, 14, 0);
		dialogue(new Dialogue()
						.addPlayer(HeadE.ANGRY, "You want to eat something? Chew on this, you filthy animals!")
						.addNPC(SCALECTRIX, HeadE.ANGRY, "Yes! Feel the true power of the Druids!")
				, true);

		camPos(16, 25, 2000);
		camLook(13, 14, 0);
		delay(1);
		playerSpotAnim(new SpotAnim(1300));
		playerAnim(new Animation(7660));
		npcSpotAnim("scalectrix", new SpotAnim(1300));
		npcAnim("scalectrix", new Animation(7660));
		npcMove("giantwolpertinger", 13, 16, Entity.MoveType.TELE);
		npcSpotAnim("giantwolpertinger", new SpotAnim(SUMMONING_SPAWN));
		npcAnim("giantwolpertinger", new Animation(WOLPERTINGER_SPAWN));
		delay(5);

		camPos(18, 13, 2000);
		camLook(14, 23, 0);
		dialogue(new Dialogue()
						.addNPC(WOLF_BONES, HeadE.T_LAUGH, "Har har har! A big bunny! Dat's not scary at all!")
				, true);

		camPos(16, 25, 2000);
		camLook(13, 14, 0);
		delay(1);
		npcAnim("giantwolpertinger", new Animation(WOLPERTINGER_FEAR_CAST));
		npcSpotAnim("giantwolpertinger", new SpotAnim(WOLPERTINGER_FEAR_FLASH));

		action(() -> {
			NPC t0 = getNPC("t0");
			NPC t3 = getNPC("t3");
			NPC t5 = getNPC("t5");
			NPC wolfbones = getNPC("wolfbones");
			NPC wolfmeat = getNPC("wolfmeat");
			t0.forceTalk("Gotta run!");
			t3.forceTalk("Me no like!");
			t5.forceTalk("It too huge!");
			WorldTasks.scheduleTimer(tick -> {
				World.sendSpotAnim(player, new SpotAnim(ONE), WorldTile.of(wolfbones.getX(), wolfbones.getY(), wolfbones.getPlane()));
				World.sendSpotAnim(player, new SpotAnim(ONE), WorldTile.of(wolfmeat.getX(), wolfmeat.getY(), wolfmeat.getPlane()));
				World.sendSpotAnim(player, new SpotAnim(ONE), WorldTile.of(t0.getX(), t0.getY(), t0.getPlane()));
				World.sendSpotAnim(player, new SpotAnim(ONE), WorldTile.of(t3.getX(), t3.getY(), t3.getPlane()));
				World.sendSpotAnim(player, new SpotAnim(ONE), WorldTile.of(t5.getX(), t5.getY(), t5.getPlane()));
				return playing;
			});
		});
		delay(2);
		npcMove("t4", 8, 20, Entity.MoveType.WALK); // maybe run
		{
			Animation cower = new Animation(TROLL_COWER);
			for (String key : TROLL_KEYS)
			{
				npcAnim(key, cower);
			}
		}
		delay(1);
		npcTransform("t0", TROLL_BONE_SCARED);
		npcTransform("t1", TROLL_BONE_SCARED);
		npcTransform("t2", TROLL_BONE_SCARED);
		npcTransform("t3", TROLL_SHIELD_BONE_SCARED);
		npcTransform("t4", TROLL_SHIELD_BONE_SCARED);
		npcTransform("t5", TROLL_CLUB_SCARED);
		npcTransform("t6", TROLL_CLUB_SCARED);
		npcTransform("t7", TROLL_CLUB_SCARED);
		npcTransform("t8", TROLL_CLUB_SCARED);
		npcTransform("t9", TROLL_CLUB_SCARED);
		npcTransform("t10", TROLL_CLUB_SCARED);
		npcTransform("t11", TROLL_CLUB_SCARED);
		npcTransform("t12", TROLL_CLUB_SCARED);

		action(() -> {
			whiners.add(getNPC("t1"));
			whiners.add(getNPC("t2"));
			whiners.add(getNPC("t6"));
			whiners.add(getNPC("t7"));
			whiners.add(getNPC("t8"));
			whiners.add(getNPC("t9"));
			whiners.add(getNPC("t10"));
			whiners.add(getNPC("t11"));
			whiners.add(getNPC("t12"));
			Random random = new Random();
			WorldTasks.scheduleTimer(0, 4, tick -> {
				int range[] = new int[] { -1, -1, -1 };
				int iter = 0;
				while (iter != 3) {
					int rand = random.nextInt(0, whiners.size());
					if (rand == range[0] || rand == range[1] || rand == range[2])
						continue;
					range[iter++] = rand;
				}
				whiners.get(range[0]).forceTalk(generateRandomWhine());
				whiners.get(range[1]).forceTalk(generateRandomWhine());
				whiners.get(range[2]).forceTalk(generateRandomWhine());
				return playing;
			});
		});

		camPos(18, 13, 2000);
		camLook(14, 23, 0);
		dialogue(new Dialogue()
						.addNPC(WOLF_MEAT, HeadE.T_SCARED, "It scaring me! Why it scaring me?")
						.addNPC(WOLF_BONES, HeadE.T_SCARED, "Leg it! It going to eat us all!")
						.addNPC(WOLF_MEAT, HeadE.T_SCARED, "It messing with Wolf Meat's brains! It in my head! Make it stop!")
						.addNPC(WOLF_BONES, HeadE.T_SCARED, "Wolf Bones need his mummy!")
				, true);

		camPos(14, 18, 2000);
		camLook(14, 8, 0);

		camPos(16, 25, 2000);
		camLook(13, 14, 0);
		npcTransform("wolfbones", WOLF_BONES_SCARED);
		npcTransform("wolfmeat", WOLF_MEAT_SCARED);
		npcTalk("wolfbones", "I gettin' out of 'ere!");
		npcTalk("wolfmeat", "Run! Run fer it!");
		dialogue(new Dialogue()
				.addPlayer(HeadE.HAPPY_TALKING, "Quick! Let's get Bowloftrix, before they regroup.")
				, true);
		npcMove("wolfbones", 12, 21, Entity.MoveType.WALK);
		npcMove("wolfmeat", 13, 21, Entity.MoveType.WALK);
		dialogue(new Dialogue()
				.addNPC(SCALECTRIX, HeadE.HAPPY_TALKING, "Yes, the wolpertinger won't last long.")
				.addNPC(SCALECTRIX, HeadE.AMAZED_MILD, "In fact, there it goes...")
				, true);

		npcAnim("giantwolpertinger", new Animation(WOLPERTINGER_DESPAWN));
		delay(8);
		npcDestroy("giantwolpertinger");

		dialogue(new Dialogue()
				.addPlayer(HeadE.CALM_TALK, "Well let's get Bowloftrix and get out.")
				.addNPC(SCALECTRIX, HeadE.CALM, "Oh dear...I hope he's all right.")
				, true);
		playerMove(14, 19, Entity.MoveType.RUN);
		npcMove("scalectrix", 13, 19, Entity.MoveType.RUN);
		fadeInBG(0);
		delay(4);
		fadeOutBG(0);
		//start new cutscene/new dynamic region
		//spawn scalectrix
		//spawn bowloftrix
		//player face bowloftrix
		//bowloftrix face player
		//scalectrix face player
		//fade to clear
//		dialogue(new Dialogue()
//				.addNPC(BOWLOFTRIX, HeadE.HAPPY_TALKING, "Thank you both! I was a gonner until you showed up!")
//				, true);
//		//scalectrix face bowloftrix
//		dialogue(new Dialogue()
//				.addNPC(SCALECTRIX, HeadE.HAPPY_TALKING, "Well he is the one you should really be thanking. Without him we'd never have been able to call the wolpertinger.")
//				.addNPC(BOWLOFTRIX, HeadE.HAPPY_TALKING, "Yes, thank you very much, hero!")
//				.addPlayer(HeadE.HAPPY_TALKING, "No problem, I'm just glad we got you out of there before you were badly injured.")
//				, true);
//		//bowloftrix faces scalectrix
//		dialogue(new Dialogue()
//				.addNPC(SCALECTRIX, HeadE.HAPPY_TALKING, "Especially as that means you'll be able to come back and help with Pikkupstix's next project.")
//				.addNPC(BOWLOFTRIX, HeadE.SHAKING_HEAD, "Uh..well.yes...")
//				.addNPC(BOWLOFTRIX, HeadE.SECRETIVE, "Oh! Ow! Ow! I think the trolls cracked one of my ribs! I should get that seen to...right away!")
//				.addNPC(SCALECTRIX, HeadE.AMAZED_MILD, "Oh, you poor man! Go see the healers before you faint!")
//				, true);
		//player face east or south east or something
		//scalectrix runs away out of scene
		//delay
		//scalectrix faces player
		//player faces scalectrix
//		dialogue(new Dialogue()
//				.addPlayer(HeadE.CONFUSED, "He semmed to run quite quickly for a man with a cracked rib.")
//				.addNPC(SCALECTRIX, HeadE.CONFUSED, "Maybe he was in such pain he wanted to get there quickly.")
//				.addNPC(SCALECTRIX, HeadE.HAPPY_TALKING, "Anyway, he's alive and well, all thanks to you!")
//				, true);
		//fade to black
		//restore scene
		action(() -> {
			player.getQuestManager().completeQuest(Quest.WOLF_WHISTLE);
			playing = false;
		});
	}

	private String generateRandomWhine() {
		Random random = new Random();
		return switch (random.nextInt(0, 10)) {
			case 0 -> "It makin' faces at me!";
			case 1 -> "It bigger den me!";
			case 2 -> "I didn't want to come 'ere anyway!";
			case 3 -> "Get me out of 'ere!";
			case 4 -> "I is scared!";
			case 5 -> "It makin' me brains hurt!";
			case 6 -> "I dun't like it!";
			case 7 -> "Boss! Make it stop!";
			case 8 -> "I dun't wanna!";
			case 9 -> "Bunnies! Big bunnies!";
			default -> "";
		};
	}

}

