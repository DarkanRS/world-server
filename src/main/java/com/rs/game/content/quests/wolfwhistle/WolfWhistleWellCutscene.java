package com.rs.game.content.quests.wolfwhistle;

import com.rs.engine.cutscene.Cutscene;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;

import java.util.ArrayList;
import java.util.Random;

public class WolfWhistleWellCutscene extends Cutscene {

    private final static int WOLF_MEAT = 15058;
	private final static int WOLF_BONES = 15059;
	private final static int WOLF_MEAT_SCARED = 15060;
	private final static int WOLF_BONES_SCARED = 15061;
	private final static int TROLL_FLINTER = 15062;
	private final static int TROLL_BONE = 15063;
	private final static int TROLL_CLUB = 15066;
	private final static int TROLL_BONE_SHIELD = 15067;
	private final static int TROLL_BONE_SCARED = 15068;
	private final static int TROLL_SHIELD_BONE_SCARED = 15072;
	private final static int BOWLOFTRIX_CAULDRON = 67500;

	private final static int GIANT_WOLPERTINGER = 6990;

	private final static int SCALECTRIX = 15055;

    // spot anims
	private final static int SUMMONING_SPAWN = 1315;
	private final static int ONE = 2860;
	private final static int WOLPERTINGER_FEAR_FLASH = 2820;

    // animations
	private final static int WOLPERTINGER_FEAR_CAST = 15929;
	private final static int WOLPERTINGER_DESPAWN = 15930;
	private final static int WOLPERTINGER_SPAWN = 15932;
	private final static int TROLL_COWER = 15921;
    // 1522

	private final String[] TROLL_KEYS = new String[]{"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10", "t11", "t12"};
	private final ArrayList<NPC> whiners = new ArrayList<>();

	private boolean playing = false;

    @Override
    public void construct(Player player) {
        playing = true;
        fadeInBG(4);
        hideMinimap();
        dynamicRegion(player.getTile(), 178, 554, 4, 4);

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
                World.sendSpotAnim(Tile.of(wolfbones.getX(), wolfbones.getY(), wolfbones.getPlane()), new SpotAnim(ONE));
                World.sendSpotAnim(Tile.of(wolfmeat.getX(), wolfmeat.getY(), wolfmeat.getPlane()), new SpotAnim(ONE));
                World.sendSpotAnim(Tile.of(t0.getX(), t0.getY(), t0.getPlane()), new SpotAnim(ONE));
                World.sendSpotAnim(Tile.of(t3.getX(), t3.getY(), t3.getPlane()), new SpotAnim(ONE));
                World.sendSpotAnim(Tile.of(t5.getX(), t5.getY(), t5.getPlane()), new SpotAnim(ONE));
                return playing;
            });
        });
        delay(2);
        npcMove("t4", 8, 20, Entity.MoveType.WALK); // maybe run
        {
            Animation cower = new Animation(TROLL_COWER);
            for (String key : TROLL_KEYS) {
                npcAnim(key, cower);
            }
        }
        delay(1);
        for (int i = 1;i < 13;i++)
            npcTransform("t"+i, i == 3 || i == 4 ? TROLL_SHIELD_BONE_SCARED : TROLL_BONE_SCARED);

        action(() -> {
            for (int i = 1;i < 13;i++)
                whiners.add(getNPC("t"+i));
            Random random = new Random();
            WorldTasks.scheduleTimer(0, 4, tick -> {
                int range[] = new int[]{-1, -1, -1};
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

