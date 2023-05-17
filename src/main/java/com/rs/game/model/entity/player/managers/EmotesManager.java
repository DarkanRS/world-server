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
package com.rs.game.model.entity.player.managers;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Rights;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

import java.util.ArrayList;
import java.util.HashMap;

@PluginEventHandler
public final class EmotesManager {

	private ArrayList<Emote> unlocked;
	private transient Player player;
	private transient long nextEmoteEnd;

	public enum Emote {
		YES(0, 1796, "Yes", new Animation(855)),
		NO(1, 1797, "No", new Animation(856)),
		BOW(2, 1783, "Bow", new Animation(858)),
		ANGRY(3, 1790, "Angry", new Animation(859)),
		THINK(4, 1792, "Think", new Animation(857)),
		WAVE(5, 1793, "Wave", new Animation(863)),
		SHRUG(6, 1829, "Shrug", new Animation(2113)),
		CHEER(7, 1804, "Cheer", new Animation(862)),
		BECKON(8, 1795, "Beckon", new Animation(864)),
		LAUGH(9, 1798, "Laugh", new Animation(861)),
		JUMP_FOR_JOY(10, 1825, "Jump For Joy", new Animation(2109)),
		YAWN(11, 1827, "Yawn", new Animation(2111)),
		DANCE(12, 1794, "Dance", new Animation(866)),
		JIG(13, 1822, "Jig", new Animation(2106)),
		TWIRL(14, 1823, "Twirl", new Animation(2107)),
		HEADBANG(15, 1824, "Headbang", new Animation(2108)),
		CRY(16, 1791, "Cry", new Animation(860)),
		BLOW_KISS(17, 1820, "Blow Kiss", new Animation(1374), new SpotAnim(1702)),
		PANIC(18, 1821, "Panic", new Animation(2105)),
		RASPBERRY(19, 1826, "Raspberry", new Animation(2110)),
		CLAP(20, 1805, "Clap", new Animation(865)),
		SALUTE(21, 1828, "Salute", new Animation(2112)),
		GOBLIN_BOW(22, 1830, "Goblin Bow", 532, 7, new Animation(2127)),
		GOBLIN_SALUTE(23, 1831, "Goblin Salute", 532, 7, new Animation(2128)),
		GLASS_BOX(24, 1817, "Glass Box", 1368, new Animation(1131)),
		CLIMB_ROPE(25, 1818, "Climb Rope", 1369, new Animation(1130)),
		LEAN(26, 1819, "Lean", 1370, new Animation(1129)),
		GLASS_WALL(27, 1806, "Glass Wall", 1367, new Animation(1128)),
		IDEA(28, 1838, "Idea", 2311, new Animation(4276)),
		STOMP(29, 1839, "Stomp", 2312, new Animation(4278)),
		FLAP(30, 1836, "Flap", 2309, new Animation(4280)),
		SLAP_HEAD(31, 1837, "Slap Head", 2310, new Animation(4275)),
		ZOMBIE_WALK(32, 1834, "Zombie Walk", 1921, new Animation(3544)),
		ZOMBIE_DANCE(33, 1833, "Zombie Dance", 1920, new Animation(3543)),
		ZOMBIE_HAND(34, 1835, "Zombie Hand", 4075, 12, new Animation(7272), new SpotAnim(1244)),
		SCARED(35, 1832, "Scared", 1371, new Animation(2836)),
		BUNNY_HOP(36, 1840, "Bunny-hop", 2055, new Animation(6111)),
		CAPE(37, 1855, "Cape", 2787),
		SNOWMAN_DANCE(38, 1841, "Snowman Dance", 4202, new Animation(7531)),
		AIR_GUITAR(39, 1842, "Air Guitar", 4394, new Animation(2414), new SpotAnim(1537)), //music 302
		SAFETY_FIRST(40, 1843, "Safety First", 4476, new Animation(8770), new SpotAnim(1553)),
		EXPLORE(41, 1844, "Explore", 4884, new Animation(9990), new SpotAnim(1734)),
		TRICK(42, 1845, "Trick", 5490, new Animation(10530), new SpotAnim(1864)),
		FREEZE(43, 1846, "Freeze", 5732, new Animation(11044), new SpotAnim(1973)),
		GIVE_THANKS(44, 1847, "Give Thanks", 5641),
		AROUND_THE_WORLD(45, 1848, "Around the World in Eggty Days", 9194, new Animation(11542), new SpotAnim(2037)),
		DRAMATIC_POINT(46, 1849, "Dramatic Point", 6936, new Animation(12658), new SpotAnim(780)),
		FAINT(47, 1850, "Faint", 6095, new Animation(14165)),
		PUPPET_MASTER(48, 1851, "Puppet Master", 8300, 20, new Animation(14869), new SpotAnim(2837)),
		TASKMASTER(49, 1853, "Taskmaster", 8601, 534),
		SEAL_OF_APPROVAL(50, 1852, "Seal of Approval", 8688),
		CAT_FIGHT(51, 2658, "Cat Fight", new Animation(2252)),
		TALK_TO_THE_HAND(52, 2654, "Talk to the Hand", new Animation(2416)),
		SHAKE_HANDS(53, 2651, "Shake Hands", new Animation(2303)),
		HIGH_FIVE(54, 2652, "High Five", new Animation(2312)),
		FACE_PALM(55, 2657, "Face-palm", new Animation(2254)),
		SURRENDER(56, 2661, "Surrender", new Animation(2360)),
		LEVITATE(57, 2653, "Levitate", new Animation(2327)),
		MUSCLE_MAN_POSE(58, 2650, "Muscle-man Pose", new Animation(2566)),
		ROFL(59, 2659, "ROFL", new Animation(2347)),
		BREATHE_FIRE(60, 2660, "Breathe Fire", new Animation(2238), new SpotAnim(358)),
		STORM(61, 2655, "Storm", new Animation(2563), new SpotAnim(365)),
		SNOW(62, 2656, "Snow", new Animation(2417), new SpotAnim(364)),
		INVOKE_SPRING(63, 1854, "Invoke Spring", 9194, new Animation(15357), new SpotAnim(1391)),
		HEAD_IN_THE_SAND(64, 2662, "Head in the Sand", new Animation(12926), new SpotAnim(1761)),
		HULA_HOOP(65, 2663, "Hula Hoop", new Animation(12928)),
		DISAPPEAR(66, 2664, "Disappear", new Animation(12929), new SpotAnim(1760)),
		GHOST(67, 2665, "Ghost", new Animation(12932), new SpotAnim(1762)),
		BRING_IT(68, 2666, "Bring It!", new Animation(12934)),
		PALM_FIST(69, 2667, "Palm-fist", new Animation(12931)),
		KNEEL(70, 2687, "Kneel", new Animation(12449)),
		BEGGING(71, 2688, "Begging", new Animation(12450)),
		STIR_COULDRON(72, 2689, "Stir Cauldron", new Animation(12463)),
		CHEER2(73, 2690, "Cheer", new Animation(12473)),
		TANTRUM(74, 2691, "Tantrum", new Animation(12497)),
		DRAMATIC_DEATH(75, 2692, "Dramatic Death", new Animation(12544)),
		JUMP_YELL(76, 2693, "Jump & Yell", new Animation(12472)),
		POINT(77, 2694, "Point", new Animation(12476)),
		PUNCH(78, 2695, "Punch", new Animation(12477)),
		RAISE_HAND(79, 2696, "Raise Hand", new Animation(12484)),
		MAKE_SPEECH(80, 2697, "Make Speech", new Animation(12489)),
		SWORD_FIGHT(81, 2698, "Sword Fight", new Animation(12496)),
		RAISE_HAND_SIT(82, 2699, "Raise Hand (Sitting)", new Animation(12487)),
		WAVE_SIT(83, 2700, "Wave (Sitting)", new Animation(12488)),
		CHEER_SIT(84, 2701, "Cheer (Sitting)", new Animation(12500)),
		THROW_TOMATO_SIT(85, 2708, "Throw Tomato (Sitting)", new Animation(12468)),
		THROW_FLOWERS(86, 2709, "Throw Flowers (Sitting)", new Animation(12469)),
		AGREE(87, 2702, "Agree (Sitting)", new Animation(12504)),
		POINT_SIT(88, 2703, "Point (Sitting)", new Animation(12505)),
		WHISTLE(89, 2704, "Whistle (Sitting)", new Animation(12509)),
		THUMBSUP_SIT(90, 2706, "Thumbs-Up (Sitting)", new Animation(12687)),
		THUMBSDOWN_SIT(91, 2707, "Thumbs-Down (Sitting)", new Animation(12688)),
		CLAP_SIT(92, 2705, "Clap (Sitting)", new Animation(12691)),
		LIVING_BORROWED_TIME(93, 2749, "Living on Borrowed Time", 9930, 15),
		TROUBADOUR_DANCE(94, 2812, "Troubadour dance", 10138, 100, new Animation(15424)),
		EVIL_LAUGH(95, 2935, "Evil Laugh"),
		GOLF_CLAP(96, 2936, "Golf Clap", new Animation(15520)),
		LOLCANO(97, 2937, "LOLcano"),
		INFERNAL_POWER(98, 2938, "Infernal Power", new Animation(15529), new SpotAnim(2197)),
		DIVINE_POWER(99, 2939, "Divine Power", new Animation(15524), new SpotAnim(2195)),
		YOURE_DEAD(100, 2940, "You're Dead", new Animation(14195)),
		SCREAM(101, 2941, "Scream"),
		TORNADO(102, 2942, "Tornado", new Animation(15530), new SpotAnim(2196)),
		CHAOTIC_COOKERY(103, 2943, "Chaotic Cookery", 10340, new Animation(15604), new SpotAnim(2239)),
		ROFLCOPTER(104, 9780, "ROFLcopter"),
		NATURES_MIGHT(105, 9781, "Nature's Might", new Animation(16376), new SpotAnim(3011)),
		INNER_POWER(106, 9782, "Inner Power", new Animation(16382), new SpotAnim(3014)),
		WEREWOLF_TRANSFORMATION(107, 9783, "Werewolf Transformation"),
		CELEBRATE(108, 10516, "Celebrate", new Animation(16913), new SpotAnim(3175)),
		BREAKDANCE(109, 11187, "Breakdance", new Animation(17079)),
		MAHJARRAT_TRANS(110, 11188, "Mahjarrat Transformation", new Animation(17103), new SpotAnim(3222)),
		BREAK_WIND(111, 11189, "Break Wind", new Animation(17076), new SpotAnim(3226)),
		BACKFLIP(112, 11190, "Backflip", new Animation(17101), new SpotAnim(3221)),
		GRAVEDIGGER(113, 11191, "Gravedigger", new Animation(17077), new SpotAnim(3219)),
		FROG_TRANS(114, 11192, "Frog Transformation", new Animation(17080), new SpotAnim(3220)),
		MEXICAN_WAVE(115, 11387, "Mexican Wave", new Animation(17163)),
		SPORTSMAN(116, 11388, "Sportsman", new Animation(17166));

		private static HashMap<Integer, Emote> MAP = new HashMap<>();

		static {
			for (Emote emote : Emote.values())
				MAP.put(emote.slotId, emote);
		}

		public static Emote forSlot(int slotId) {
			return MAP.get(slotId);
		}

		private int slotId;
		private int mapId;
		private String name;
		private int varpbit;
		private int value;
		private Animation animation;
		private SpotAnim spotAnim;

		private Emote(int slotId, int mapId, String name) {
			this(slotId, mapId, name, -1, -1, null, null);
		}

		private Emote(int slotId, int mapId, String name, Animation animation) {
			this(slotId, mapId, name, -1, -1, animation, null);
		}

		private Emote(int slotId, int mapId, String name, Animation animation, SpotAnim spotAnim) {
			this(slotId, mapId, name, -1, -1, animation, spotAnim);
		}

		private Emote(int slotId, int mapId, String name, int varpbit) {
			this(slotId, mapId, name, varpbit, 1, null, null);
		}

		private Emote(int slotId, int mapId, String name, int varpbit, Animation animation) {
			this(slotId, mapId, name, varpbit, 1, animation, null);
		}

		private Emote(int slotId, int mapId, String name, int varpbit, Animation animation, SpotAnim spotAnim) {
			this(slotId, mapId, name, varpbit, 1, animation, spotAnim);
		}

		private Emote(int slotId, int mapId, String name, int varpbit, int value) {
			this(slotId, mapId, name, varpbit, value, null, null);
		}

		private Emote(int slotId, int mapId, String name, int varpbit, int value, Animation animation) {
			this(slotId, mapId, name, varpbit, value, animation, null);
		}

		private Emote(int slotId, int mapId, String name, int varpbit, int value, Animation animation, SpotAnim spotAnim) {
			this.slotId = slotId;
			this.mapId = mapId;
			this.name = name;
			this.varpbit = varpbit;
			this.value = value;
			this.animation = animation;
			this.spotAnim = spotAnim;
		}

		public int getMapId() {
			return mapId;
		}

		public Animation getAnim() {
			return animation;
		}
	}

	public EmotesManager() {
		unlocked = new ArrayList<>();
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void unlockEmote(Emote emote) {
		if (unlocked.contains(emote))
			return;
		if (unlocked.add(emote))
			refreshListConfigs();
	}
	
	public void lockEmote(Emote emote) {
		if (!unlocked.contains(emote))
			return;
		if (unlocked.remove(emote))
			refreshListConfigs();
	}

	public boolean unlockedEmote(Emote emote) {
		if (player.hasRights(Rights.ADMIN) || emote.ordinal() <= Emote.SALUTE.ordinal() || emote == Emote.CELEBRATE || emote == Emote.CAPE)
			return true;
		return unlocked.contains(emote);
	}

	public static ButtonClickHandler handleEmoteBook = new ButtonClickHandler(new Object[] { 590, 464 }, e -> {
		if ((e.getInterfaceId() == 590 && e.getComponentId() != 8))
			return;
		Emote emote = Emote.forSlot(e.getSlotId());
		if (emote != null)
			e.getPlayer().getEmotesManager().useBookEmote(emote);
	});

	public void useBookEmote(Emote emote) {
		if (player.inCombat(10000) || player.hasBeenHit(10000)) {
			player.sendMessage("You can't do this while you're under combat.");
			return;
		}
		player.stopAll(false);
		player.getTreasureTrailsManager().useEmote(emote);
		if (!unlockedEmote(emote)) {
			if (emote == Emote.AIR_GUITAR)
				player.simpleDialogue("This emote can be acessed by unlocking " + Settings.AIR_GUITAR_MUSICS_COUNT + " pieces of music.");
			else
				player.simpleDialogue("You haven't unlocked "+emote.name+" yet.");
		} else {
			if (World.getServerTicks() < nextEmoteEnd) {
				player.sendMessage("You're already doing an emote!");
				return;
			}
			if (emote.animation != null) {
				player.setNextAnimation(emote.animation);
				if (emote.spotAnim != null)
					player.setNextSpotAnim(emote.spotAnim);
			} else if (emote == Emote.TASKMASTER) {
				player.setNextAnimation(new Animation(player.getAppearance().isMale() ? 15033 : 15034));
				player.setNextSpotAnim(new SpotAnim(2930));
			} else if (emote == Emote.LOLCANO) {
				player.setNextAnimation(new Animation(player.getAppearance().isMale() ? 15532 : 15533));
				player.setNextSpotAnim(new SpotAnim(2191));
			} else if (emote == Emote.SCREAM)
				player.setNextAnimation(new Animation(player.getAppearance().isMale() ? 15526 : 15527));
			else if (emote == Emote.ROFLCOPTER) {
				player.setNextAnimation(new Animation(player.getAppearance().isMale() ? 16373 : 16374));
				player.setNextSpotAnim(new SpotAnim(3010));
			} else if (emote == Emote.WEREWOLF_TRANSFORMATION) {
				player.setNextAnimation(new Animation(16380));
				player.setNextSpotAnim(new SpotAnim(3013));
				player.setNextSpotAnim(new SpotAnim(3016));
			} else if (emote == Emote.EVIL_LAUGH) {
				player.setNextAnimation(new Animation(player.getAppearance().isMale() ? 15535 : 15536));
				player.setNextSpotAnim(new SpotAnim(2191));
			} else if (emote == Emote.LIVING_BORROWED_TIME) {
				final NPC grim = new NPC(14388, Tile.of(player.getX(), player.getY() + 1, player.getPlane()));
				World.addNPC(grim);
				player.lock();
				grim.setNextFaceEntity(player);
				player.setNextFaceEntity(grim);
				WorldTasks.scheduleTimer(1, tick -> {
					if (tick >= 10 || player.hasFinished())
						return false;
					if (tick == 0) {
						grim.setNextAnimation(new Animation(13964));
						player.setNextSpotAnim(new SpotAnim(1766));
						player.setNextAnimation(new Animation(13965));
					} else if (tick == 8) {
						grim.setFinished(true);
						World.removeNPC(grim);
						grim.setNextFaceEntity(null);
					} else if (tick == 9) {
						player.setNextForceTalk(new ForceTalk("Phew! Close call."));
						player.setNextFaceEntity(null);
						player.unlock();
					}
					return true;
				});
			} else if (emote == Emote.CAPE) {
				final int capeId = player.getEquipment().getCapeId();
				switch (capeId) {
				case 9747:
				case 9748:
				case 25324:
					player.setNextAnimation(new Animation(4959));
					player.setNextSpotAnim(new SpotAnim(823));

					break;
				case 9753:
				case 9754:
				case 25326:
					player.setNextAnimation(new Animation(4961));
					player.setNextSpotAnim(new SpotAnim(824));
					break;
				case 9750:
				case 9751:
				case 25325:
					player.setNextAnimation(new Animation(4981));
					player.setNextSpotAnim(new SpotAnim(828));
					break;
				case 9768:
				case 9769:
				case 25332:
					player.setNextAnimation(new Animation(14242));
					player.setNextSpotAnim(new SpotAnim(2745));
					break;
				case 9756:
				case 9757:
				case 25327:
					player.setNextAnimation(new Animation(4973));
					player.setNextSpotAnim(new SpotAnim(832));
					break;
				case 9762:
				case 9763:
				case 25329:
					player.setNextAnimation(new Animation(4939));
					player.setNextSpotAnim(new SpotAnim(813));
					break;
				case 9759:
				case 9760:
				case 25328:
					player.setNextAnimation(new Animation(4979));
					player.setNextSpotAnim(new SpotAnim(829));
					break;
				case 9801:
				case 9802:
				case 25344:
					player.setNextAnimation(new Animation(4955));
					player.setNextSpotAnim(new SpotAnim(821));
					break;
				case 9807:
				case 9808:
				case 25346:
					player.setNextAnimation(new Animation(4957));
					player.setNextSpotAnim(new SpotAnim(822));
					break;
				case 9783:
				case 9784:
				case 25337:
					player.setNextAnimation(new Animation(4937));
					player.setNextSpotAnim(new SpotAnim(812));
					break;
				case 9798:
				case 9799:
				case 25343:
					player.setNextAnimation(new Animation(4951));
					player.setNextSpotAnim(new SpotAnim(819));
					break;
				case 9804:
				case 9805:
				case 25345:
					player.setNextAnimation(new Animation(4975));
					player.setNextSpotAnim(new SpotAnim(831));
					break;
				case 9780:
				case 9781:
				case 25336:
					player.setNextAnimation(new Animation(4949));
					player.setNextSpotAnim(new SpotAnim(818));
					break;
				case 9795:
				case 9796:
				case 25342:
					player.setNextAnimation(new Animation(4943));
					player.setNextSpotAnim(new SpotAnim(815));
					break;
				case 9792:
				case 9793:
				case 25341:
					player.setNextAnimation(new Animation(4941));
					player.setNextSpotAnim(new SpotAnim(814));
					break;
				case 9774:
				case 9775:
				case 25334:
					player.setNextAnimation(new Animation(4969));
					player.setNextSpotAnim(new SpotAnim(835));
					break;
				case 9771:
				case 9772:
				case 25333:
					player.setNextAnimation(new Animation(4977));
					player.setNextSpotAnim(new SpotAnim(830));
					break;
				case 9777:
				case 9778:
				case 25335:
					player.setNextAnimation(new Animation(4965));
					player.setNextSpotAnim(new SpotAnim(826));
					break;
				case 9786:
				case 9787:
				case 25338:
					player.setNextAnimation(new Animation(4967));
					player.setNextSpotAnim(new SpotAnim(1656));
					break;
				case 9810:
				case 9811:
				case 25347:
					player.setNextAnimation(new Animation(4963));
					player.setNextSpotAnim(new SpotAnim(825));
					break;
				case 9765:
				case 9766:
				case 25330:
					player.setNextAnimation(new Animation(4947));
					player.setNextSpotAnim(new SpotAnim(817));
					break;
				case 9789:
				case 9790:
				case 25331:
					player.setNextAnimation(new Animation(4953));
					player.setNextSpotAnim(new SpotAnim(820));
					break;
				case 12169:
				case 12170:
				case 25348:
					player.setNextAnimation(new Animation(8525));
					player.setNextSpotAnim(new SpotAnim(1515));
					break;
				case 9948:
				case 9949:
				case 25339:
					player.setNextAnimation(new Animation(5158));
					player.setNextSpotAnim(new SpotAnim(907));
					break;
				case 9813:
					player.setNextAnimation(new Animation(4945));
					player.setNextSpotAnim(new SpotAnim(816));
					break;
				case 18508:
				case 18509: // Dungeoneering cape
					if (player.isLocked())
						break;
					final int rand = Utils.random(0, 3);
					player.setNextAnimation(new Animation(13190));
					player.setNextSpotAnim(new SpotAnim(2442));
					player.lock();
					WorldTasks.schedule(new WorldTask() {
						int step;

						@Override
						public void run() {
							if (step == 1) {
								if (rand == 0)
									player.getAppearance().transformIntoNPC(11227);
								else if (rand == 1)
									player.getAppearance().transformIntoNPC(11228);
								else if (rand == 2)
									player.getAppearance().transformIntoNPC(11229);

								if (rand == 0)
									player.setNextAnimation(new Animation(13192));
								else if (rand == 1)
									player.setNextAnimation(new Animation(13193));
								else if (rand == 2)
									player.setNextAnimation(new Animation(13194));
							}
							if (step == 6)
								player.getAppearance().transformIntoNPC(-1);
							if (step == 8) {
								player.unlock();
								stop();
							}
							step++;
						}
					}, 0, 0);
					break;
				case 19709:
				case 19710: // Master dungeoneering cape
					if (player.isLocked())
						break;
					player.setNextFaceTile(Tile.of(player.getX(), player.getY() - 1, player.getPlane()));
					player.lock();
					WorldTasks.schedule(new WorldTask() {
						int step;

						@Override
						public void run() {
							if (step == 1) {
								player.getAppearance().transformIntoNPC(11229);
								player.setNextAnimation(new Animation(14608));
								World.sendProjectile(player, Tile.of(player.getX(), player.getY() - 1, player.getPlane()), 2781, 30, 30, 6, 20, 1, 0);
								World.sendSpotAnim(Tile.of(player.getX(), player.getY() - 1, player.getPlane()), new SpotAnim(2777));
							}
							if (step == 3) {
								player.getAppearance().transformIntoNPC(11228);
								player.setNextAnimation(new Animation(14609));
								player.setNextSpotAnim(new SpotAnim(2782));
								World.sendSpotAnim(Tile.of(player.getX() + 1, player.getY() - 1, player.getPlane()), new SpotAnim(2778));
							}
							if (step == 5) {
								player.getAppearance().transformIntoNPC(11227);
								player.setNextAnimation(new Animation(14610, 15));
								World.sendSpotAnim(Tile.of(player.getX(), player.getY() - 1, player.getPlane()), new SpotAnim(2779));
								World.sendSpotAnim(Tile.of(player.getX(), player.getY() + 1, player.getPlane()), new SpotAnim(2780));
							}
							if (step == 9)
								player.setNextSpotAnim(new SpotAnim(2442));
							if (step == 10) {
								player.setNextSpotAnim(new SpotAnim(-1));
								player.getAppearance().transformIntoNPC(-1);
								player.unlock();
								stop();
							}
							step++;
						}
					}, 0, 0);
					break;
				case 20763: // Veteran cape
					if (player.getControllerManager().getController() != null) {
						player.sendMessage("You cannot do this here!");
						return;
					}
					player.setNextAnimation(new Animation(352));
					player.setNextSpotAnim(new SpotAnim(1446));
					break;
				case 20765: // Classic cape
					if (player.getControllerManager().getController() != null) {
						player.sendMessage("You cannot do this here!");
						return;
					}
					int random = Utils.getRandomInclusive(2);
					player.setNextAnimation(new Animation(122));
					player.setNextSpotAnim(new SpotAnim(random == 0 ? 1471 : 1466));
					break;
				case 20767: // Max cape
					if (player.getControllerManager().getController() != null) {
						player.sendMessage("Dont annoy other players!");
						return;
					}
					int size = NPCDefinitions.getDefs(1224).size;
					Tile spawnTile = Tile.of(Tile.of(player.getX() + 1, player.getY(), player.getPlane()));
					if (!World.floorAndWallsFree(spawnTile, size))
						spawnTile = player.getNearestTeleTile(size);
					if (spawnTile == null) {
						player.sendMessage("Need more space to perform this skillcape emote.");
						return;
					}
					nextEmoteEnd = World.getServerTicks() + 25;
					final Tile npcTile = spawnTile;
					WorldTasks.schedule(new WorldTask() {
						private int step;
						private NPC npc;

						@Override
						public void run() {
							if (step == 0) {
								npc = new NPC(1224, npcTile);
								npc.setNextAnimation(new Animation(1434));
								npc.setNextSpotAnim(new SpotAnim(1482));
								player.setNextAnimation(new Animation(1179));
								npc.setNextFaceEntity(player);
								player.setNextFaceEntity(npc);
							} else if (step == 2) {
								npc.setNextAnimation(new Animation(1436));
								npc.setNextSpotAnim(new SpotAnim(1486));
								player.setNextAnimation(new Animation(1180));
							} else if (step == 3) {
								npc.setNextSpotAnim(new SpotAnim(1498));
								player.setNextAnimation(new Animation(1181));
							} else if (step == 4)
								player.setNextAnimation(new Animation(1182));
							else if (step == 5) {
								npc.setNextAnimation(new Animation(1448));
								player.setNextAnimation(new Animation(1250));
							} else if (step == 6) {
								player.setNextAnimation(new Animation(1251));
								player.setNextSpotAnim(new SpotAnim(1499));
								npc.setNextAnimation(new Animation(1454));
								npc.setNextSpotAnim(new SpotAnim(1504));
							} else if (step == 11) {
								player.setNextAnimation(new Animation(1291));
								player.setNextSpotAnim(new SpotAnim(1686));
								player.setNextSpotAnim(new SpotAnim(1598));
								npc.setNextAnimation(new Animation(1440));
							} else if (step == 16) {
								player.setNextFaceEntity(null);
								npc.finish();
								stop();
							}
							step++;
						}

					}, 0, 1);
					break;
				case 20769:
				case 20771: // Compl cape
					if (!World.floorAndWallsFree(player.getTile(), 3)) {
						player.sendMessage("Need more space to perform this skillcape emote.");
						return;
					}
					if (player.getControllerManager().getController() != null) {
						player.sendMessage("Dont annoy other players!");
						return;
					}
					nextEmoteEnd = World.getServerTicks() + 20;
					WorldTasks.schedule(new WorldTask() {
						private int step;
						@Override
						public void run() {
							if (step == 0) {
								player.setNextAnimation(new Animation(356));
								player.setNextSpotAnim(new SpotAnim(307));
							} else if (step == 2) {
								player.getAppearance().transformIntoNPC(capeId == 20769 ? 1830 : 3372);
								player.setNextAnimation(new Animation(1174));
								player.setNextSpotAnim(new SpotAnim(1443));
							} else if (step == 4)
								player.getPackets().sendCameraShake(3, 25, 50, 25, 50);
							else if (step == 5)
								player.getPackets().sendStopCameraShake();
							else if (step == 8) {
								player.getAppearance().transformIntoNPC(-1);
								player.setNextAnimation(new Animation(1175));
								stop();
							}
							step++;
						}
					}, 0, 1);
					break;
				default:
					player.sendMessage("You need to be wearing a skillcape in order to perform this emote.");
					break;
				}
				return;
			} else if (emote == Emote.GIVE_THANKS)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						if (step == 0) {
							player.setNextAnimation(new Animation(10994));
							player.setNextSpotAnim(new SpotAnim(86));
						} else if (step == 1) {
							player.setNextAnimation(new Animation(10996));
							player.getAppearance().transformIntoNPC(8499);
						} else if (step == 6) {
							player.setNextAnimation(new Animation(10995));
							player.setNextSpotAnim(new SpotAnim(86));
							player.getAppearance().transformIntoNPC(-1);
							stop();
						}
						step++;
					}
					private int step;
				}, 0, 1);
			else if (emote == Emote.SEAL_OF_APPROVAL)
				WorldTasks.schedule(new WorldTask() {
					int random = (int) (Math.random() * (2 + 1));
					@Override
					public void run() {
						if (step == 0) {
							player.setNextAnimation(new Animation(15104));
							player.setNextSpotAnim(new SpotAnim(1287));
						} else if (step == 1) {
							player.setNextAnimation(new Animation(15106));
							player.getAppearance().transformIntoNPC(random == 0 ? 13255 : (random == 1 ? 13256 : 13257));
						} else if (step == 2)
							player.setNextAnimation(new Animation(15108));
						else if (step == 3) {
							player.setNextAnimation(new Animation(15105));
							player.setNextSpotAnim(new SpotAnim(1287));
							player.getAppearance().transformIntoNPC(-1);
							stop();
						}
						step++;
					}
					private int step;
				}, 0, 1);
			setNextEmoteEnd();
		}
	}

	public void setNextEmoteEnd() {
		nextEmoteEnd = player.getLastAnimationEnd() - 1;
	}

	public void setNextEmoteEnd(int ticks) {
		nextEmoteEnd = World.getServerTicks() + ticks;
	}

	public void refreshListConfigs() {
		for (Emote emote : unlocked)
			if (emote.varpbit != -1)
				player.getVars().setVarBit(emote.varpbit, emote.value);
	}

	public boolean isAnimating() {
		return World.getServerTicks() < nextEmoteEnd;
	}

	public void unlockEmotesBook() {
		player.getPackets().setIFRightClickOps(590, 8, 0, Emote.values().length, 0, 1);
	}
}
