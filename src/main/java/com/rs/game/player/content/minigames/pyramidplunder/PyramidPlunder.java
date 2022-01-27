package com.rs.game.player.content.minigames.pyramidplunder;

import java.util.Arrays;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.ForceMovement;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.others.OwnedNPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.Ticks;


/**
 * 2366, 2375, 2376, 2377 varbits
 *
 */
@PluginEventHandler
public class PyramidPlunder {//All objects within the minigame

	static int[] exitDoors = {
			Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3),
			Utils.randomInclusive(0,3), Utils.randomInclusive(0,3)
	};

	@ServerStartupEvent
	public static void init() {
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				exitDoors = new int[] {
						Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3),
						Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3)
				};
			}
		}, 0, Ticks.fromMinutes(4));
	}



	public static ObjectClickHandler handlePyramidExits = new ObjectClickHandler(new Object[] { 16458 }) {
		@Override
		public void handle(ObjectClickEvent e) {;
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addOptions("Would you like to exit?", new Options() {
					@Override
					public void create() {
						option("Yes", new Dialogue()
								.addNext(()-> {
									teleAndResetRoom(e.getPlayer(), new WorldTile(3288, 2801, 0));
									e.getPlayer().getControllerManager().forceStop();
								}));
						option("No", new Dialogue());
					}
				});
				create();
			}
		});
		}
	};

    private static void giveCheckUrnXP(Player p) {
        if (isIn21Room(p))
            p.getSkills().addXp(Constants.THIEVING, 20);
        else if (isIn31Room(p))
            p.getSkills().addXp(Constants.THIEVING, 30);
        else if (isIn41Room(p))
            p.getSkills().addXp(Constants.THIEVING, 50);
        else if (isIn51Room(p))
            p.getSkills().addXp(Constants.THIEVING, 70);
        else if (isIn61Room(p))
            p.getSkills().addXp(Constants.THIEVING, 100);
        else if (isIn71Room(p))
            p.getSkills().addXp(Constants.THIEVING, 150);
        else if (isIn81Room(p))
            p.getSkills().addXp(Constants.THIEVING, 225);
        else if (isIn91Room(p))
            p.getSkills().addXp(Constants.THIEVING, 275);
    }

    private static void giveSearchCheckedUrnXP(Player p) {
        if (isIn21Room(p))
            p.getSkills().addXp(Constants.THIEVING, 20);
        else if (isIn31Room(p))
            p.getSkills().addXp(Constants.THIEVING, 30);
        else if (isIn41Room(p))
            p.getSkills().addXp(Constants.THIEVING, 50);
        else if (isIn51Room(p))
            p.getSkills().addXp(Constants.THIEVING, 70);
        else if (isIn61Room(p))
            p.getSkills().addXp(Constants.THIEVING, 100);
        else if (isIn71Room(p))
            p.getSkills().addXp(Constants.THIEVING, 150);
        else if (isIn81Room(p))
            p.getSkills().addXp(Constants.THIEVING, 225);
        else if (isIn91Room(p))
            p.getSkills().addXp(Constants.THIEVING, 275);
    }

    private static void giveSearchBlindUrnXP(Player p) {
        if (isIn21Room(p))
            p.getSkills().addXp(Constants.THIEVING, 60);
        else if (isIn31Room(p))
            p.getSkills().addXp(Constants.THIEVING, 90);
        else if (isIn41Room(p))
            p.getSkills().addXp(Constants.THIEVING, 150);
        else if (isIn51Room(p))
            p.getSkills().addXp(Constants.THIEVING, 215);
        else if (isIn61Room(p))
            p.getSkills().addXp(Constants.THIEVING, 300);
        else if (isIn71Room(p))
            p.getSkills().addXp(Constants.THIEVING, 450);
        else if (isIn81Room(p))
            p.getSkills().addXp(Constants.THIEVING, 675);
        else if (isIn91Room(p))
            p.getSkills().addXp(Constants.THIEVING, 825);
    }

    private static void giveSearchGoldChestXP(Player p) {
        if (isIn21Room(p))
            p.getSkills().addXp(Constants.THIEVING, 40);
        else if (isIn31Room(p))
            p.getSkills().addXp(Constants.THIEVING, 60);
        else if (isIn41Room(p))
            p.getSkills().addXp(Constants.THIEVING, 100);
        else if (isIn51Room(p))
            p.getSkills().addXp(Constants.THIEVING, 140);
        else if (isIn61Room(p))
            p.getSkills().addXp(Constants.THIEVING, 200);
        else if (isIn71Room(p))
            p.getSkills().addXp(Constants.THIEVING, 300);
        else if (isIn81Room(p))
            p.getSkills().addXp(Constants.THIEVING, 450);
        else if (isIn91Room(p))
            p.getSkills().addXp(Constants.THIEVING, 550);
    }

    private static void giveDoorXP(Player p) {
        if (isIn21Room(p))
            p.getSkills().addXp(Constants.THIEVING, 0);
        else if (isIn31Room(p))
            p.getSkills().addXp(Constants.THIEVING, 0);
        else if (isIn41Room(p))
            p.getSkills().addXp(Constants.THIEVING, 0);
        else if (isIn51Room(p))
            p.getSkills().addXp(Constants.THIEVING, 0);
        else if (isIn61Room(p))
            p.getSkills().addXp(Constants.THIEVING, 0);
        else if (isIn71Room(p))
            p.getSkills().addXp(Constants.THIEVING, 0);
        else if (isIn81Room(p))
            p.getSkills().addXp(Constants.THIEVING, 0);
        else if (isIn91Room(p))
            p.getSkills().addXp(Constants.THIEVING, 0);
    }

    private static void giveTrapXP(Player p) {
        p.getSkills().addXp(Constants.THIEVING, 10);
    }

	public static ObjectClickHandler handlePlunderUrns = new ObjectClickHandler(new Object[] { 16518, 16519, 16520, 16521, 16522, 16523, 16524,
			16525, 16526, 16527, 16528, 16529, 16530, 16531, 16532 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.isAtObject()) {
                e.getPlayer().lock(1);
                WorldTasks.schedule(new WorldTask() {//I had to do this to actually make the player face the urn more often. He only faces the urn 1/2 the time without this
                    int tick;
                    boolean checked = false;
                    @Override
                    public void run() {
                        if (tick == 0)
                            e.getPlayer().faceObject(e.getObject());
                        if (!checked && tick >= 0
                                && (Direction.getDirectionTo(e.getPlayer(), e.getObject()).getId() == e.getPlayer().getDirection().getId())) {//make sure you face the urn...
                            checkUrn(e);
                            checked = true;
                        }
                        if (tick == 3)
                            stop();
                        tick++;
                    }
                }, 0, 1);
            }
		}
	};

    private static void checkUrn(ObjectClickEvent e) {
        Player p = e.getPlayer();
        GameObject obj = e.getObject();
        int varNum = obj.getDefinitions().varpBit;
        if (p.getVars().getVarBit(varNum) == 0) { //Untouched urn
            if (e.getOption().equalsIgnoreCase("check for snakes")) {
                p.lock(1);
                p.setNextAnimation(new Animation(4340));
                p.getVars().setVarBit(varNum, 2);
                giveCheckUrnXP(p);
            } else { //search option
                p.lock(3);
                WorldTasks.scheduleTimer(tick -> {
                    if (tick == 0)
                        p.setNextAnimation(new Animation(4340));//check urn anim
                    if (tick == 1)
                        if (urnSnakeBiteChance(p)) {
                            p.setNextAnimation(new Animation(4341));//snake bite anim
                            p.applyHit(new Hit(p.getSkills().getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE));
                            p.forceTalk("Ow!");
                            p.getVars().setVarBit(varNum, 2);
                        } else {
                            p.setNextAnimation(new Animation(4342));//loot urn anim
                            urnLoot(p);
                            giveSearchBlindUrnXP(p);
                            p.getVars().setVarBit(varNum, 1);
                        }
                    if (tick == 3)
                        return false;
                    return true;
                });
            }
        } else if (p.getVars().getVarBit(varNum) == 1)//empty urn
            return;
        else if (p.getVars().getVarBit(varNum) == 2) {//Snake active
            if (e.getOption().equalsIgnoreCase("charm snake")) {
                if (p.getInventory().containsItem(4605, 1))
                    p.getVars().setVarBit(varNum, 3);
                else
                    p.sendMessage("You need a snake charm flute for that!");
            } else if (urnSnakeBiteChance(p)) {
                p.applyHit(new Hit(p.getSkills().getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE));
                p.forceTalk("Ow!");
            } else {
                urnLoot(p);
                giveSearchCheckedUrnXP(p);
                p.getVars().setVarBit(varNum, 1);
            }
        } else if (p.getVars().getVarBit(varNum) == 3)//snake charmed
            if (urnSnakeBiteChance(p, true)) {
                p.applyHit(new Hit(p.getSkills().getLevel(Constants.HITPOINTS) / 5, Hit.HitLook.TRUE_DAMAGE));
                p.forceTalk("Ow!");
            } else {
                urnLoot(p);
                giveSearchCheckedUrnXP(p);
                p.getVars().setVarBit(varNum, 1);
            }
    }

	private static void urnLoot(Player p) {
		if (isIn21Room(p))
			p.getInventory().addItem(rollItem(0), true);//Gold chest has higher chance of rare items + scepter
		else if (isIn31Room(p))
			p.getInventory().addItem(rollItem(1), true);
		else if (isIn41Room(p))
			p.getInventory().addItem(rollItem(2), true);
		else if (isIn51Room(p))
			p.getInventory().addItem(rollItem(3), true);
		else if (isIn61Room(p))
			p.getInventory().addItem(rollItem(4), true);
		else if (isIn71Room(p))
			p.getInventory().addItem(rollItem(5), true);
		else if (isIn81Room(p))
			p.getInventory().addItem(rollItem(6), true);
        else if (isIn91Room(p))
            p.getInventory().addItem(rollItem(7), true);
	}

	private static boolean urnSnakeBiteChance(Player p) {
		return urnSnakeBiteChance(p, false);
	}

	private static boolean urnSnakeBiteChance(Player p, boolean hasCharm) {
		double ratio = (p.getSkills().getLevel(Constants.THIEVING))/100.0*(hasCharm ? 6.0 : 3.0); //level 1 is ~0, level 99 is ~3
		if(ratio < 1.0)
			ratio = 1.0;
		if (isIn21Room(p))
			return Utils.random(0.0, 1.0) < (0.25/ratio);
        else if (isIn31Room(p))
            return Utils.random(0.0, 1.0) < (0.3/ratio);
        else if (isIn41Room(p))
            return Utils.random(0.0, 1.0) < (0.4/ratio);
        else if (isIn51Room(p))
            return Utils.random(0.0, 1.0) < (0.5/ratio);
        else if (isIn61Room(p))
            return Utils.random(0.0, 1.0) < (0.6/ratio);
        else if (isIn71Room(p))
            return Utils.random(0.0, 1.0) < (0.7/ratio);
        else if (isIn81Room(p))
            return Utils.random(0.0, 1.0) < (0.8/ratio);
        else if (isIn91Room(p))
            return Utils.random(0.0, 1.0) < (0.9/ratio);
        else
            return true;
	}

	public static ObjectClickHandler handleGrandChest = new ObjectClickHandler(new Object[] { 16537 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.isAtObject()) {
				p.getVars().setVarBit(2363, 1);
				if(Utils.randomInclusive(0, 4) == 1) {
					OwnedNPC swarm = new OwnedNPC(p, 2001, p, false);
					swarm.setTarget(p);
				}
				if (isIn21Room(p))
					p.getInventory().addItem(rollItem(0, 1.1, true), true);//Gold chest has higher chance of rare items + scepter
                else if (isIn31Room(p))
                    p.getInventory().addItem(rollItem(1, 1.1, true), true);
                else if (isIn41Room(p))
                    p.getInventory().addItem(rollItem(2, 1.1, true), true);
                else if (isIn51Room(p))
                    p.getInventory().addItem(rollItem(3, 1.1, true), true);
                else if (isIn61Room(p))
                    p.getInventory().addItem(rollItem(4, 1.1, true), true);
                else if (isIn71Room(p))
                    p.getInventory().addItem(rollItem(5, 1.1, true), true);
                else if (isIn81Room(p))
                    p.getInventory().addItem(rollItem(6, 1.1, true), true);
                else if (isIn91Room(p))
                    p.getInventory().addItem(rollItem(7, 1.1, true), true);
                giveSearchGoldChestXP(p);
			}
		}
	};

	public static ObjectClickHandler handleSarcophagus = new ObjectClickHandler(new Object[] { 16547 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(e.isAtObject())
				if(p.getVars().getVarBit(2362) == 0) {
					if (isIn21Room(p)) {
						if(p.getSkills().getLevel(Constants.STRENGTH) < 21) {
							p.sendMessage("You need 21 strength...");
							return;
						}
					} else if (isIn31Room(p)) {
						if(p.getSkills().getLevel(Constants.STRENGTH) < 31) {
							p.sendMessage("You need 31 strength...");
							return;
						}
					} else if (isIn41Room(p)) {
						if(p.getSkills().getLevel(Constants.STRENGTH) < 41) {
							p.sendMessage("You need 41 strength...");
							return;
						}
					} else if (isIn51Room(p)) {
						if(p.getSkills().getLevel(Constants.STRENGTH) < 51) {
							p.sendMessage("You need 51 strength...");
							return;
						}
					} else if (isIn61Room(p)) {
						if(p.getSkills().getLevel(Constants.STRENGTH) < 61) {
							p.sendMessage("You need 61 strength...");
							return;
						}
					} else if (isIn71Room(p)) {
						if(p.getSkills().getLevel(Constants.STRENGTH) < 71) {
							p.sendMessage("You need 71 strength...");
							return;
						}
					} else if (isIn81Room(p)) {
                        if (p.getSkills().getLevel(Constants.STRENGTH) < 81) {
                            p.sendMessage("You need 81 strength...");
                            return;
                        }
                    } else if (isIn91Room(p)) {
                        if (p.getSkills().getLevel(Constants.STRENGTH) < 91) {
                            p.sendMessage("You need 91 strength...");
                            return;
                        }
                    }
					p.getVars().setVarBit(2362, 1);
					if (Utils.randomInclusive(0, 4) == 1) {
                        OwnedNPC mummy = new OwnedNPC(p, 2015, p, false);
						mummy.setTarget(p);
					}
				} else if(p.getVars().getVarBit(2362) == 1) {
					p.getVars().setVarBit(2362, 2);
					if (isIn21Room(p))
						p.getInventory().addItem(rollItem(0, 1.1, true), true);//Gold chest has higher chance of rare items + scepter
					else if (isIn31Room(p))
						p.getInventory().addItem(rollItem(1, 1.1, true), true);
					else if (isIn41Room(p))
						p.getInventory().addItem(rollItem(2, 1.1, true), true);
					else if (isIn51Room(p))
						p.getInventory().addItem(rollItem(3, 1.1, true), true);
					else if (isIn61Room(p))
						p.getInventory().addItem(rollItem(4, 1.1, true), true);
					else if (isIn71Room(p))
						p.getInventory().addItem(rollItem(5, 1.1, true), true);
					else if (isIn81Room(p))
						p.getInventory().addItem(rollItem(6, 1.1, true), true);
                    else if (isIn91Room(p))
                        p.getInventory().addItem(rollItem(7, 1.1, true), true);
				}
		}
	};

    public static ObjectClickHandler handleEngravedSarcophagus = new ObjectClickHandler(new Object[] { 59795 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            if(e.isAtObject())
                if(p.getVars().getVarBit(3422) == 0) {
                        if (p.getSkills().getLevel(Constants.STRENGTH) < 91) {
                            p.sendMessage("You need 91 strength...");
                            return;
                        }
                    p.getVars().setVarBit(3422, 1);
                    if (Utils.randomInclusive(0, 4) == 1) {
                        OwnedNPC mummy = new OwnedNPC(p, 2015, p, false);
                        mummy.setTarget(p);
                    }
                } else if(p.getVars().getVarBit(3422) == 1) {
                    p.getVars().setVarBit(3422, 2);
                    p.getInventory().addItem(rollItem(7, 1.2, true), true);
                }
        }
    };

	private static Item rollItem(int roomNum, double modifier) {
		return rollItem(roomNum, modifier, false);
	}

	private static Item rollItem(int roomNum, boolean scepter) {
		return rollItem(roomNum, 1, scepter);
	}

	private static Item rollItem(int roomNum) {
		return rollItem(roomNum, 1, false);
	}

	private static final int IVORY_COMB = 9026;//only first 2, urns only
	private static final int POTTERY_STATUETTE = 9036;



	private static final int POTTERY_SCARAB = 9032;
	private static final int STONE_SEAL = 9042;
	private static final int STONE_SCARAB = 9030;
	private static final int STONE_STATUETTE = 9038;
	private static final int GOLDEN_SEAL = 9040;
	private static final int GOLDEN_SCARAB = 9028;
	private static final int GOLDEN_STATUETTE = 9034;
	private static final int JEWELED_GOLDEN_STATUETTE = 20661;
	private static final int PHARAOH_SCEPTRE = 9044; //Only golden chest and sarcophagus incremental chanves

	static final double ivory_base_chance = 1/5.0;
	static final double pottery_stat_base_chance = 1/9.0;
	static final double pottery_scarab_base_chance = 1/9.0;
	static final double stone_seal_base_chance = 1/10.0;
	static final double stone_scarab_base_chance = 1/12.0;
	static final double stone_stat_base_chance = 1/15.0;
	static final double gold_seal_base_chance = 1/28.0;
	static final double gold_scarab_base_chance = 1/32.0;
	static final double gold_stat_base_chance = 1/35.0;
	static final double jeweled_gold_stat_base_chance = 1/130.0;

	static final double PHARAOH_SCEPTRE_base_chance = 1/3500.0; //Proof: https://oldschool.runescape.wiki/w/Grand_Gold_Chest

	static final List<Double> rolls = Arrays.asList(PHARAOH_SCEPTRE_base_chance, jeweled_gold_stat_base_chance, gold_stat_base_chance, gold_scarab_base_chance,
			gold_seal_base_chance, stone_stat_base_chance, stone_scarab_base_chance, stone_seal_base_chance, pottery_scarab_base_chance,
			pottery_stat_base_chance, ivory_base_chance);

	static final List<Integer> rewards = Arrays.asList(PHARAOH_SCEPTRE, JEWELED_GOLDEN_STATUETTE, GOLDEN_STATUETTE, GOLDEN_SCARAB, GOLDEN_SEAL,
			STONE_STATUETTE, STONE_SCARAB, STONE_SEAL, POTTERY_SCARAB, POTTERY_STATUETTE, IVORY_COMB);

	private static Item rollItem(int roomNum, double modifier, boolean scepter) {
		if(roomNum < 0)
			roomNum = 0;
		if(roomNum > 7)
			roomNum = 7;
		modifier = modifier + (roomNum+1.0)*0.02;
		return roll(modifier, scepter, roomNum <= 1);
	}

	private static Item roll(double modifier, boolean scepter, boolean ivoryComb) {
		for (int i = scepter ? 0 : 1; i < rolls.size() - (ivoryComb ? 0 : 1); i++)
			if (Utils.random(0.0, 1.0) <= rolls.get(i) * modifier)
				return new Item(rewards.get(i));
		return roll(modifier, scepter, ivoryComb);
	}

	public static ObjectClickHandler handlePyramidTombDoors = new ObjectClickHandler(new Object[] { 16539, 16540, 16541, 16542 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.isAtObject()) {
				if (isIn21Room(p)) {
					if (16542 - obj.getId() == exitDoors[0]) {
                        teleAndResetRoom(p, new WorldTile(1977, 4471, 0));
						return;
					}
				} else if (isIn31Room(p)) {
                    if (16542 - obj.getId() == exitDoors[1]) {
                        teleAndResetRoom(p, new WorldTile(1954, 4477, 0));
                        return;
                    }
                } else if (isIn41Room(p)) {
                    if (16542 - obj.getId() == exitDoors[1]) {
                        teleAndResetRoom(p, new WorldTile(1927, 4453, 0));
                        return;
                    }
                } else if (isIn51Room(p)) {
					if (16542 - obj.getId() == exitDoors[2]) {
						teleAndResetRoom(p, new WorldTile(1965, 4444, 0));
						return;
					}
				} else if (isIn61Room(p)) {
					if (16542 - obj.getId() == exitDoors[3]) {
						teleAndResetRoom(p, new WorldTile(1927, 4424, 0));
						return;
					}
				} else if (isIn71Room(p)) {
					if (16542 - obj.getId() == exitDoors[4]) {
						teleAndResetRoom(p, new WorldTile(1943, 4421, 0));
						return;
					}
				} else if (isIn81Room(p)) {
					if (16542 - obj.getId() == exitDoors[5]) {
						teleAndResetRoom(p, new WorldTile(1974, 4420, 0));
						return;
					}
				} else if (isIn91Room(p))
					e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
						{
							addSimple("Opening this door will cause you to leave the pyramid.");
							addOptions("Would you like to exit?", new Options() {
								@Override
								public void create() {
									option("Yes", new Dialogue()
											.addNext(() -> {
												teleAndResetRoom(p, new WorldTile(3288, 2801, 0));
												e.getPlayer().getControllerManager().forceStop();
											}));
									option("No", new Dialogue());
								}
							});
							create();
						}
					});

				p.sendMessage("This door is not the exit...");
			}
		}
	};

	private static void teleAndResetRoom(Player p, WorldTile tile) {
		p.setNextWorldTile(tile);
		for(int i = 2346; i <= 2363; i++)
			p.getVars().setVarBit(i, 0);
	}

	public static ObjectClickHandler handleSpearTrap = new ObjectClickHandler(new Object[] { 16517 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.isAtObject()) {
				if (isIn21Room(p)) {
					if (p.getSkills().getLevel(Constants.THIEVING) < 21) {
						p.sendMessage("You need a thieving level of 21 or higher...");
						return;
					}
				} else if (isIn31Room(p)) {
					if (p.getSkills().getLevel(Constants.THIEVING) < 31) {
						p.sendMessage("You need a thieving level of 31 or higher...");
						return;
					}
				} else if (isIn41Room(p)) {
					if (p.getSkills().getLevel(Constants.THIEVING) < 41) {
						p.sendMessage("You need a thieving level of 41 or higher...");
						return;
					}
				} else if (isIn51Room(p)) {
					if (p.getSkills().getLevel(Constants.THIEVING) < 51) {
						p.sendMessage("You need a thieving level of 51 or higher...");
						return;
					}
				} else if (isIn61Room(p)) {
					if (p.getSkills().getLevel(Constants.THIEVING) < 61) {
						p.sendMessage("You need a thieving level of 61 or higher...");
						return;
					}
				} else if (isIn71Room(p)) {
                    if (p.getSkills().getLevel(Constants.THIEVING) < 71) {
                        p.sendMessage("You need a thieving level of 71 or higher...");
                        return;
                    }
                } else if (isIn81Room(p)) {
                    if (p.getSkills().getLevel(Constants.THIEVING) < 81) {
                        p.sendMessage("You need a thieving level of 81 or higher...");
                        return;
                    }
                } else if (isIn91Room(p))
					if (p.getSkills().getLevel(Constants.THIEVING) < 91) {
						p.sendMessage("You need a thieving level of 91 or higher...");
						return;
					}
				passTrap(e);
			}
		}
	};

    /**
     * Use nearby tiles surrounding the trap object to choose a direction for the far tiles.
     * Both near and far point in the same directions per index N, E, S, W
     * If a nearby tile is a trap that is the direction we must go as a player...
     * @param e
     */
	private static void passTrap(ObjectClickEvent e) {
		Player p = e.getPlayer();
		WorldTile tile = e.getObject();
		WorldTile[] nearbyTiles = {
				new WorldTile(tile.getX(), tile.getY()+1, tile.getPlane()), new WorldTile(tile.getX()+1, tile.getY(), tile.getPlane()),
				new WorldTile(tile.getX(), tile.getY()-1, tile.getPlane()), new WorldTile(tile.getX()-1, tile.getY(), tile.getPlane())
		};
		WorldTile[] farTiles = {
				new WorldTile(p.getX(), p.getY()+3, p.getPlane()), new WorldTile(p.getX()+3, p.getY(), p.getPlane()),
				new WorldTile(p.getX(), p.getY()-3, p.getPlane()), new WorldTile(p.getX()-3, p.getY(), p.getPlane())
		};
		int i = 0;
		for(WorldTile nearbyTile : nearbyTiles) {
			GameObject obj2 = World.getObject(nearbyTile, ObjectType.SCENERY_INTERACT);
			if (obj2 != null && obj2.getId() == 16517) {
				p.lock(3);
				boolean hasRun = p.getRun();
				p.setRun(false);
				p.addWalkSteps(farTiles[i], 4, false);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						p.setRun(hasRun);
                        giveTrapXP(p);
					}
				}, 1);
				return;
			}
			i++;
		}
	}

	public static boolean isIn21Room(Player p) {//they are little boxes...
		if((p.getX()>=1921 && p.getX()<=1933) && (p.getY()>=4463 && p.getY()<=4480))
			return true;
		return false;
	}

    public static boolean isIn31Room(Player p) {
        if((p.getX()>=1968 && p.getX()<=1984) && (p.getY()>=4451 && p.getY()<=4475))
            return true;
        return false;
    }

	public static boolean isIn41Room(Player p) {
		if((p.getX()>=1946 && p.getX()<=1961) && (p.getY()>=4462 && p.getY()<=4479))
			return true;
		return false;
	}

	public static boolean isIn51Room(Player p) {
		if((p.getX()>=1922 && p.getX()<=1943) && (p.getY()>=4447 && p.getY()<=4460))
			return true;
		return false;
	}

	public static boolean isIn61Room(Player p) {
		if((p.getX()>=1949 && p.getX()<=1967) && (p.getY()>=4443 && p.getY()<=4456))
			return true;
		return false;
	}

	public static boolean isIn71Room(Player p) {
		if((p.getX()>=1921 && p.getX()<=1933) && (p.getY()>=4422 && p.getY()<=4441))
			return true;
		return false;
	}

	public static boolean isIn81Room(Player p) {
		if((p.getX()>=1938 && p.getX()<=1957) && (p.getY()>=4419 && p.getY()<=4436))
			return true;
		return false;
	}

	public static boolean isIn91Room(Player p) {
		if((p.getX()>=1965 && p.getX()<=1979) && (p.getY()>=4417 && p.getY()<=4438))
			return true;
		return false;
	}

	final static WorldTile[] rightHandSpearTraps = {
			new WorldTile(1927, 4473, 0), new WorldTile(1928, 4473, 0),
			new WorldTile(1930, 4452, 0), new WorldTile(1930, 4453, 0),
			new WorldTile(1955, 4474, 0), new WorldTile(1954, 4474, 0),
			new WorldTile(1961, 4444, 0), new WorldTile(1961, 4445, 0),
			new WorldTile(1927, 4428, 0), new WorldTile(1926, 4428, 0),
			new WorldTile(1944, 4425, 0), new WorldTile(1945, 4425, 0),
			new WorldTile(1974, 4424, 0), new WorldTile(1975, 4424, 0)
	};
	final static WorldTile[] leftHandSpearTraps = {
			new WorldTile(1927, 4472, 0), new WorldTile(1928, 4472, 0),
			new WorldTile(1931, 4452, 0), new WorldTile(1931, 4453, 0),
			new WorldTile(1955, 4473, 0), new WorldTile(1954, 4473, 0),
			new WorldTile(1962, 4444, 0), new WorldTile(1962, 4445, 0),
			new WorldTile(1927, 4427, 0), new WorldTile(1926, 4427, 0),
			new WorldTile(1944, 4424, 0), new WorldTile(1945, 4424, 0),
			new WorldTile(1974, 4423, 0), new WorldTile(1975, 4423, 0)
	};

	public static PlayerStepHandler handleRightHandSpearTraps = new PlayerStepHandler(rightHandSpearTraps) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getPlayer().isLocked())
				return;
			Direction rightHandTrap = Direction.rotateClockwise(e.getStep().getDir(), 2);//90 degree turn
			activateTrap(e, rightHandTrap);
			hitPlayer(e);
		}
	};

	public static PlayerStepHandler handleLeftHandSpearTraps = new PlayerStepHandler(leftHandSpearTraps) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getPlayer().isLocked())
				return;
			Direction leftHandTrap = Direction.rotateClockwise(e.getStep().getDir(), 6);//270 degree turn
			activateTrap(e, leftHandTrap);
			hitPlayer(e);
		}
	};

	private static void activateTrap(PlayerStepEvent e, Direction trapDir) {
		WorldTile trapTile = e.getTile();
		for(GameObject obj : World.getRegion(trapTile.getRegionId()).getObjects())
			if(obj.getId() == 16517) {
				if(trapTile.matches(obj) || (obj.getX() - trapDir.getDx() == trapTile.getX() && obj.getY() - trapDir.getDy() == trapTile.getY())) {
					obj.animate(new Animation(463));
					break;
				}
			}
	}

	private static void hitPlayer(PlayerStepEvent e) {
		Player p = e.getPlayer();
		p.applyHit(new Hit(30, Hit.HitLook.POISON_DAMAGE));
		Direction oppositeDir = Direction.rotateClockwise(e.getStep().getDir(), 4);//180 degree turn
		int dX = oppositeDir.getDx();
		int dY = oppositeDir.getDy();
		WorldTile prevTile = new WorldTile(e.getTile().getX() + dX, e.getTile().getY() + dY, e.getTile().getPlane());
		p.lock(3);
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;
			@Override
			public void run() {
				if(ticks == 0) {
					p.setNextAnimation(new Animation(1832));
					p.setNextForceMovement(new ForceMovement(prevTile, 1, e.getStep().getDir()));
				}
				else if (ticks == 1) {
					p.setNextWorldTile(prevTile);
					p.forceTalk("Ouch!");
				}
				else if (ticks == 2)
					stop();
				ticks++;
			}
		}, 0, 1);
	}

}
