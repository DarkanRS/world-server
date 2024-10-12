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
package com.rs.game.content.commands.admin;

import com.rs.Launcher;
import com.rs.Settings;
import com.rs.cache.ArchiveType;
import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.cache.loaders.map.ClipFlag;
import com.rs.engine.command.Commands;
import com.rs.engine.cutscene.ExampleCutscene;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.pathfinder.*;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.achievements.Achievement;
import com.rs.game.content.combat.CombatDefinitions.Spellbook;
import com.rs.game.content.combat.PlayerCombatKt;
import com.rs.game.content.dnds.eviltree.EvilTreesKt;
import com.rs.game.content.dnds.shootingstar.ShootingStars;
import com.rs.game.content.minigames.barrows.BarrowsController;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.game.content.pets.Pet;
import com.rs.game.content.randomevents.RandomEvents;
import com.rs.game.content.skills.runecrafting.runespan.RunespanController;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.content.tutorialisland.TutorialIslandController;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.map.instance.Instance;
import com.rs.game.map.instance.InstancedChunk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.ModelRotator;
import com.rs.game.model.entity.Rotation;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.InstancedController;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.managers.InterfaceManager;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.*;
import com.rs.lib.net.ServerPacket;
import com.rs.lib.net.packets.decoders.ReflectionCheckResponse.ResponseCode;
import com.rs.lib.net.packets.encoders.HintTrail;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.RSColor;
import com.rs.lib.util.Utils;
import com.rs.lib.util.reflect.ReflectionCheck;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.kts.PluginScriptHost;
import com.rs.tools.MapSearcher;
import com.rs.tools.NPCDropDumper;
import com.rs.utils.DropSets;
import com.rs.utils.ObjAnimList;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;
import com.rs.utils.music.Song;
import com.rs.utils.music.Voices;
import com.rs.utils.reflect.ReflectionAnalysis;
import com.rs.utils.reflect.ReflectionTest;
import com.rs.utils.shop.ShopsHandler;
import com.rs.utils.spawns.ItemSpawns;
import com.rs.utils.spawns.NPCSpawn;
import com.rs.utils.spawns.NPCSpawns;
import kotlin.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PluginEventHandler
public class MiscTest {

	private static final int[] UNIDENTIFIED_ANIMS = { 2057, 12549, 15461, 3024, 2202, 2205, 2200, 2212, 2207, 2211, 2208, 2197, 2195, 15719, 3145, 4122, 3874, 587, 10707, 1107, 2938, 2435, 2438, 2434, 2552, 2449, 2447, 14165, 2976, 2932, 3320, 3911, 5964, 3777, 3727, 2825, 2826, 16254, 16355, 16366, 16363, 16382, 16393, 10371, 1599, 3853, 3452, 3471, 3423, 3348, 2048, 3982, 2149, 6083, 2148, 2788, 11620, 2614, 15138, 12033, 3071, 13691, 2764, 12919, 1409, 6703, 15624, 2450, 1354, 15622, 2378, 9104, 16445, 654, 3032, 3670, 15147, 14175, 9021, 12913, 4471, 1852, 11146, 3266, 1633, 15128, 2343, 15203, 2010,
			4223, 9899, 2903, 2326, 1307, 2891, 15139, 1196, 1895, 1485, 2697, 1267, 4611, 1914, 1606, 2094, 2813, 2793, 3182, 12806, 11604, 1317, 2684, 3562, 3308, 1155, 2138, 1855, 1867, 1255, 3093, 9723, 1893, 786, 3458, 3454, 3456, 3455, 3453, 2008, 2007, 2002, 3166, 2767, 11224, 5767, 3092, 1356, 1361, 1006, 948, 3546, 9681, 1492, 646, 1136, 345, 6, 1898, 2159, 120, 1418, 9946, 166, 179, 14844, 3437, 232, 16322, 296, 9502, 1017, 3575, 3574, 3681, 3601, 3588, 3612, 3650, 14289, 3120, 3122, 2998, 3576, 10243, 1870, 3098, 1621, 1620, 3123, 1922, 3037, 1452, 1456, 3474, 3344, 1998, 2000,
			2030, 1761, 1410, 2330, 1095, 3189, 2897, 1232, 1213, 1219, 1498, 1279, 1635, 2808, 3987, 3978, 1876, 2571, 1262, 2103, 455, 2711, 3220, 466, 443, 1247, 450, 730, 3229, 463, 458, 456, 2166, 1546, 1562, 3164, 1815, 5013, 4019, 4016, 4029, 4135, 4138, 15080, 4156, 4164, 15113, 15129, 4222, 6689, 4262, 4253, 4281, 4258, 4283, 4285, 4316, 4333, 9164, 4337, 4347, 4352, 4404, 4359, 4364, 4361, 4434, 4576, 4573, 4834, 4553, 7139, 4574, 4592, 7033, 12609, 4600, 4752, 4749, 4822, 4818, 4803, 4796, 4909, 4912, 4892, 14242, 4904, 4908, 4903, 4878, 4907, 4863, 8525, 5015, 5054, 15137, 5421,
			5076, 5077, 5053, 5139, 5157, 5154, 5266, 5299, 5307, 14302, 5360, 5354, 5062, 8849, 5420, 2757, 9405, 5599, 5588, 6104, 10355, 9577, 5736, 5733, 6364, 5796, 5812, 5813, 6147, 5861, 5905, 5902, 5904, 5908, 6073, 4219, 6125, 11411, 6121, 6213, 6179, 6169, 4852, 6459, 11608, 6273, 15011, 452, 12217, 6482, 11739, 6427, 6452, 6409, 6530, 6554, 6611, 6599, 6665, 6700, 6636, 6644, 6641, 6640, 14723, 11540, 6851, 12446, 6860, 14174, 6899, 6921, 6920, 9387, 6941, 7024, 16420, 1016, 16421, 16419, 15238, 6981, 7113, 7130, 7136, 7137, 7141, 7135, 7119, 7138, 7114, 7116, 7145, 35, 7233, 7240,
			5076, 5077, 5053, 5139, 5157, 5154, 5266, 5299, 5307, 14302, 5360, 5354, 5062, 8849, 5420, 2757, 9405, 5599, 5588, 6104, 10355, 9577, 5736, 5733, 6364, 5796, 5812, 5813, 6147, 5861, 5905, 5902, 5904, 5908, 6073, 4219, 6125, 11411, 6121, 6213, 6179, 6169, 4852, 6459, 11608, 6273, 15011, 452, 12217, 6482, 11739, 6427, 6452, 6409, 6530, 6554, 6611, 6599, 6665, 6700, 6636, 6644, 6641, 6640, 14723, 11540, 6851, 12446, 6860, 14174, 6899, 6921, 6920, 9387, 6941, 7024, 16420, 1016, 16421, 16419, 15238, 6981, 7113, 7130, 7136, 7137, 7141, 7135, 7119, 7138, 7114, 7116, 7145, 35, 7233, 7240,
			7253, 7281, 7293, 7278, 7290, 7277, 7296, 7299, 7321, 7348, 7352, 7359, 8806, 8859, 7388, 7489, 9610, 9633, 7538, 8450, 7545, 7551, 8535, 7634, 7652, 8402, 8474, 8466, 8418, 8486, 8434, 8379, 8414, 8430, 8422, 10554, 8426, 8494, 8482, 8490, 8446, 8462, 8470, 8137, 8509, 8662, 8590, 8645, 8625, 486, 11451, 8699, 1221, 1220, 1228, 8711, 8748, 8746, 8747, 8744, 8768, 8803, 8826, 8895, 8884, 10475, 10992, 11568, 11552, 8991, 9018, 10449, 9861, 9056, 9043, 9034, 10121, 15135, 9148, 9147, 9142, 9149, 4294, 12397, 9224, 9319, 9245, 9232, 9238, 9251, 9257, 9269, 15142, 9327, 9328, 9351,
			12187, 9384, 9427, 9391, 9389, 9397, 9513, 9574, 9543, 9582, 9982, 9909, 15416, 4856, 12373, 3329, 9854, 13656, 16301, 6830, 10024, 9971, 9972, 9966, 9990, 10079, 12319, 10052, 10047, 10094, 10095, 10220, 10191, 10128, 10127, 10221, 10229, 10331, 10334, 10316, 12191, 10350, 10353, 10373, 10414, 10489, 10445, 10443, 10448, 10430, 14223, 15726, 10559, 10746, 10649, 10702, 10880, 10893, 10827, 10821, 10754, 10894, 10712, 10786, 10790, 10788, 10805, 10809, 10807, 10780, 10784, 10782, 10792, 10811, 10803, 10778, 14633, 10989, 10935, 10932, 10930, 10995, 14713, 11011, 11032, 11056,
			11220, 1132, 1715, 1708, 11286, 11284, 11336, 11340, 11350, 11316, 11314, 11305, 11312, 11376, 1742, 11372, 11321, 11298, 11547, 16934, 11561, 11566, 11576, 11590, 11622, 11625, 11644, 11655, 11688, 11716, 11765, 11770, 11766, 884, 11794, 11819, 7527, 15825, 11860, 11862, 11882, 11821, 11933, 13945, 3273, 411, 11959, 3327, 12059, 12115, 12113, 12117, 12137, 12130, 12111, 12167, 12112, 16438, 12225, 12219, 12224, 12924, 12241, 12296, 12600, 12357, 12401, 12400, 12417, 12432, 12421, 12433, 12442, 12503, 12495, 12529, 12554, 12589, 12536, 12550, 1513, 12630, 1548, 12606, 4609, 12636,
			4615, 12655, 12733, 9514, 5083, 12781, 12775, 12787, 12814, 12832, 6607, 12836, 12835, 12888, 12887, 16931, 12923, 12265, 13593, 13663, 14808, 13315, 13782, 13643, 13730, 13572, 13563, 13581, 13594, 13559, 13557, 13554, 13566, 13574, 13582, 13721, 13007, 13500, 13353, 14610, 13758, 13763, 13636, 13324, 14798, 13766, 14152, 13817, 13814, 13831, 13839, 13967, 13959, 14083, 14058, 13973, 14086, 15127, 14078, 14007, 14147, 1038, 14116, 14101, 2129, 4874, 14163, 14177, 14206, 14208, 14231, 14273, 14275, 14317, 14355, 14345, 14335, 14349, 14562, 14561, 14556, 14514, 14569, 14414, 14533,
			14962, 14401, 14673, 14639, 14645, 14722, 14733, 14748, 14780, 14787, 14941, 14841, 14845, 14886, 14929, 14982, 3302, 3300, 3288, 3290, 15239, 15034, 15063, 15097, 7573, 15103, 15105, 15217, 15187, 15176, 15181, 6292, 6998, 7493, 15261, 7502, 7437, 7436, 7434, 15906, 15317, 15305, 15308, 15275, 15276, 2916, 15304, 16077, 107, 379, 307, 10825, 1478, 4207, 10860, 10824, 10116, 7456, 9573, 12465, 5848, 15353, 10964, 1174, 1291, 12437, 9674, 16875, 1202, 1730, 1786, 2120, 2566, 2231, 15740, 2605, 3413, 3536, 3497, 3052, 3929, 7055, 8644, 7157, 6787, 5684, 8498, 6066, 6575, 6366, 5784,
			6786, 4405, 9856, 4729, 9818, 9211, 9700, 9699, 9694, 9503, 9953, 11553, 16501, 12361, 11843, 10731, 10734, 11957, 11235, 16325, 10978, 12666, 12546, 12760, 12901, 12934, 15644, 13686, 13510, 13597, 13956, 13700, 14528, 14306, 16921, 14878, 14955, 15313, 15376, 15330, 15402, 15382, 15429, 15433, 15536, 15560, 15573, 15566, 15576, 15900, 15584, 15592, 15602, 15733, 15702, 15637, 15639, 15658, 15645, 15717, 15716, 16212, 15746, 15754, 15940, 16404, 15994, 15898, 15757, 16062, 15802, 15808, 15937, 16072, 8527, 15943, 8545, 16245, 16410, 16439, 16429, 16370, 16532, 16548, 16533, 16632,
			16686, 16647, 16617, 16623, 16620, 16629, 16603, 16671, 16940, 16929, 16870, 16826, 16835, 16855, 16825, 16770, 16767, 16760, 16850, 16864, 16881, 16917, 16894, 16935, 16938, 16973, 16964, 16958, 16978, 16987, 17064, 17010, 17132, 17118, 17159, 17184, 17169, 17155, 17149, 17158, 17168 };

	@ServerStartupEvent
	public static void loadCommands() {
		//		Commands.add(Rights.ADMIN, "command [args]", "Desc", (p, args) -> {
		//
		//		});
		Commands.add(Rights.DEVELOPER, "reloadplugins", "legit test meme", (p, args) -> {
			try {
				PluginScriptHost.Companion.loadAndExecuteScripts();
				p.sendMessage("Reloaded plugins successfully.");
			} catch(Throwable e) {
				p.sendMessage("Error compiling plugins.");
			}
		});

		Commands.add(Rights.ADMIN, "testcoordclue [deg1, min1, dir1, deg2, min2, dir2]", "teleports you to the coordinates for clues", (p, args) -> {
			int deg1 = Integer.parseInt(args[0]);
			int min1 = Integer.parseInt(args[1]);
			int dir1 = switch(args[2].toLowerCase()) {
				case "north" -> 0;
				case "south" -> 1;
				case "west" -> 2;
				case "east" -> 3;
				default -> throw new IllegalArgumentException("Must be a direction as a string.");
			};
			int deg2 = Integer.parseInt(args[3]);
			int min2 = Integer.parseInt(args[4]);
			int dir2 = switch(args[5].toLowerCase()) {
				case "north" -> 0;
				case "south" -> 1;
				case "west" -> 2;
				case "east" -> 3;
				default -> throw new IllegalArgumentException("Must be a direction as a string.");
			};
			p.tele(TreasureTrailsManager.getTile(deg1, min1, dir1, deg2, min2, dir2));
		});

		Commands.add(Rights.ADMIN, "shootingstar", "spawn a shooting star", (p, args) -> ShootingStars.spawnStar());

		Commands.add(Rights.ADMIN, "eviltree", "spawn an evil tree", (p, args) -> EvilTreesKt.spawnTree());

		Commands.add(Rights.DEVELOPER, "dumpdrops [npcId]", "exports a drop dump file for the specified NPC", (p, args) -> NPCDropDumper.dumpNPC(args[0]));

		Commands.add(Rights.DEVELOPER, "createinstance [chunkX, chunkY, width, height]", "create a test instance for getting coordinates and setting up cutscenes", (p, args) -> p.getControllerManager().startController(new InstancedController(Instance.of(p.getTile(), Integer.parseInt(args[2]), Integer.parseInt(args[3]), false)) {
            @Override
            public void onBuildInstance() {
                getInstance().copyMapAllPlanes(Integer.parseInt(args[0]), Integer.parseInt(args[1]))
                        .thenAccept(b -> player.playCutscene(cs -> getInstance().teleportLocal(player, (Integer.parseInt(args[2]) * 8) / 2, (Integer.parseInt(args[3]) * 8) / 2, 0)));
            }

            @Override
            public void onDestroyInstance() { }
        }));

		Commands.add(Rights.DEVELOPER, "togglejfr", "Toggles JFR for the staff webhook tick profiler", (p, args) -> {
			Settings.getConfig().setJFR(!Settings.getConfig().isEnableJFR());
			p.sendMessage("JFR is now " + (Settings.getConfig().isEnableJFR() ? "enabled." : "disabled."));
		});

		Commands.add(Rights.DEVELOPER, "clanify", "Toggles the ability to clanify objects and npcs by examining them.", (p, args) -> {
			p.getNSV().setB("clanifyStuff", !p.getNSV().getB("clanifyStuff"));
			p.sendMessage("CLANIFY: " + p.getNSV().getB("clanifyStuff"));
		});

		Commands.add(Rights.DEVELOPER, "allstopfaceme", "Stops all body model rotators.", (p, args) -> {
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				player.setBodyModelRotator(null);
			}
			for (NPC npc : World.getNPCs()) {
				if (npc == null || npc.hasFinished())
					continue;
				npc.setBodyModelRotator(null);
			}
		});

		Commands.add(Rights.DEVELOPER, "allfaceme", "Sets body model rotators for all entities in the server.", (p, args) -> {
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				player.setBodyModelRotator(new ModelRotator().addRotator(new Rotation(p).enableAll()));
			}
			for (NPC npc : World.getNPCs()) {
				if (npc == null || npc.hasFinished())
					continue;
				npc.setBodyModelRotator(new ModelRotator().addRotator(new Rotation(p).enableAll()));
			}
		});

		Commands.add(Rights.DEVELOPER, "spawnmax", "Spawns another max into the world on top of the player.", (p, args) -> World.spawnNPC(3373, Tile.of(p.getTile()), true, true));

		Commands.add(Rights.DEVELOPER, "playcs", "Plays a cutscene using new cutscene system", (p, args) -> p.getCutsceneManager().play(new ExampleCutscene()));

//		Commands.add(Rights.DEVELOPER, "modeldebug", "Spawns a ton of models of a certain color around you.", (p, args) -> {
//			List<RSModel> meshes = new ArrayList<>();
//			int baseCol = RSColor.RGB_to_HSL(238, 213, 54);
//			for (int i = 0;i < Cache.STORE.getIndex(IndexType.MODELS).getLastArchiveId();i++) {
//				RSModel model = RSModel.getMesh(i);
//				if (model.getAvgColor() == 0)
//					continue;
//				if (model != null && model.faceCount < 64)
//					meshes.add(model);
//			}
//			meshes.sort((m1, m2) -> {
//				//if ((m1.vertexCount + m1.faceCount) == (m2.vertexCount + m2.faceCount))
//				return Math.abs(m1.getAvgColor()-baseCol) - Math.abs(m2.getAvgColor()-baseCol);
//				//return (m1.vertexCount + m1.faceCount + Math.abs(m1.getAvgColor()-baseCol)) / 2 - (m2.vertexCount + m2.faceCount + Math.abs(m2.getAvgColor()-baseCol)) / 2;
//			});
//			int count = 0;
//			int x = 0, y = 0;
//			for (RSModel model : meshes) {
//				if (count++ > 100)
//					break;
//				if (count % 10 == 0) {
//					y++;
//					x = 0;
//				}
//				GameObject obj = new GameObject(1, ObjectType.SCENERY_INTERACT, 0, p.transform(x++, y));
//				World.spawnObject(obj);
//				WorldTasks.schedule(15, () -> {
//					obj.modifyMesh().addModels(model.id, model.id, model.id);
//					obj.refresh();
//				});
//				System.out.println(model.id + " - v" + model.vertexCount + " - f" + model.faceCount + " - " + Math.abs(model.getAvgColor()-baseCol));
//			}
//		});

		Commands.add(Rights.DEVELOPER, "bonusxp [amount]", "Sets your bonus XP rate.", (p, args) -> {
			p.setBonusXpRate(Double.valueOf(args[0]));
			p.sendMessage("Your bonus XP rates is now: " + p.getBonusXpRate());
		});

		Commands.add(Rights.DEVELOPER, "forcememclean", "Forces a mem clean op", (p, args) -> Launcher.cleanMemory(true));

		Commands.add(Rights.DEVELOPER, "tilefree", "Checks if tile is free", (p, args) -> {
			for (int x = -10;x < 10;x++)
				for (int y = -10;y < 10;y++)
					if (World.floorAndWallsFree(Tile.of(p.getX() + x, p.getY() + y, p.getPlane()), 1))
						World.sendSpotAnim(Tile.of(p.getX() + x, p.getY() + y, p.getPlane()), new SpotAnim(2000, 0, 96));
		});

		Commands.add(Rights.DEVELOPER, "tutisland", "Start tutorial island", (p, args) -> p.getControllerManager().startController(new TutorialIslandController()));

		/**
		 * 31 orange glow
		 * 40 fire cape
		 * 54 old hitsplats
		 * 61 sick fire glow
		 * 62 smokey pulsating
		 * 89 colored glow
		 * 361 bright white with bright colored glow
		 * 451 running watery color
		 * 637 ghostlyish mostly transparent
		 * 647 another bright glow with less bloom
		 * 648 breathing texture almost
		 * 649 recolorable ghostly
		 * 654, 656, 658 storm with thunder
		 * 655 subtle dripping recolor
		 * 657 fast pulsating ecto recolor
		 * 676, 677 some colorful wheel
		 * 679 recolorable ghillie suit
		 * 691 another bright bloom glow
		 * 707 transparent with black stars pulsating
		 * 718 explosion bloom particles
		 * 722 sick glow particle swirl recolorable
		 * 811 eye blinding bloom
		 * 824 bright looking lava
		 * 825 recolorable bloom lava
		 * 826 magical psychadelic moving
		 * 831 recolorable upward flowing liquid
		 * 847 sick bright yellow magical
		 * 856 recolorable fast flowing liquid
		 * 870 very bloomy recolorable
		 * 875 bright but low bloom recolorable
		 * 876-878 bright cool bloom
		 * 880 alternate fire cape lava
		 * 906 recolorable dragonhide lookin
		 * 916 eye rape bloom
		 *
		 */
		Commands.add(Rights.DEVELOPER, "drtor [texId]", "Set equipment texture override", (p, args) -> {
			if (p.getEquipment().get(Equipment.CHEST) != null)
				p.getEquipment().get(Equipment.CHEST).addMetaData("drTOr", Integer.parseInt(args[0]));
			if (p.getEquipment().get(Equipment.LEGS) != null)
				p.getEquipment().get(Equipment.LEGS).addMetaData("drTOr", Integer.parseInt(args[0]));
			if (p.getEquipment().get(Equipment.SHIELD) != null)
				p.getEquipment().get(Equipment.SHIELD).addMetaData("drTOr", Integer.parseInt(args[0]));
			if (p.getEquipment().get(Equipment.HEAD) != null)
				p.getEquipment().get(Equipment.HEAD).addMetaData("drTOr", Integer.parseInt(args[0]));
			p.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.DEVELOPER, "drcor [r, g, b]", "Set equipment color override", (p, args) -> {
			if (p.getEquipment().get(Equipment.CHEST) != null)
				p.getEquipment().get(Equipment.CHEST).addMetaData("drCOr", RSColor.RGB_to_HSL(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])));
			if (p.getEquipment().get(Equipment.LEGS) != null)
				p.getEquipment().get(Equipment.LEGS).addMetaData("drCOr", RSColor.RGB_to_HSL(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])));
			if (p.getEquipment().get(Equipment.SHIELD) != null)
				p.getEquipment().get(Equipment.SHIELD).addMetaData("drCOr", RSColor.RGB_to_HSL(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])));
			if (p.getEquipment().get(Equipment.HEAD) != null)
				p.getEquipment().get(Equipment.HEAD).addMetaData("drCOr", RSColor.RGB_to_HSL(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])));
			p.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.DEVELOPER, "tileman", "Set to tileman mode", (p, args) -> p.setTileMan(true));

		Commands.add(Rights.DEVELOPER, "players", "Lists online players", (p, args) -> {
			p.getPackets().setIFText(275, 1, "Online Players");
			int componentId = 10;

			for (Player player : World.getPlayers()) {
				p.getPackets().setIFText(275, componentId++, "<shad=000000><col="+(switch(player.getAccount().getSocial().getStatus()) {
					case 0 -> "00FF00";
					case 1 -> "FFFF00";
                    case 2 -> "FF0000";
					default -> "FFFFFF";
				})+">" + player.getDisplayName() + "</col></shad> logged in for " + Utils.ticksToTime((double) player.getTimePlayedThisSession()) + "");
			}

			p.getPackets().sendRunScript(1207, componentId - 10);
			p.getInterfaceManager().sendInterface(275);
		});

		Commands.add(Rights.DEVELOPER, "names", "Sets NPCs names to something.", (p, args) -> {
			String name = Utils.concat(args);
			if (name.equals("null"))
				name = null;
			for (NPC n : World.getNPCs())
				if (n != null)
					n.setPermName(name);
		});

		Commands.add(Rights.DEVELOPER, "barrowcheat", "Loots a full barrows chest.", (p, args) -> {
			if (p.getControllerManager().isIn(BarrowsController.class))
				p.getControllerManager().getController(BarrowsController.class).cheat();
		});

		Commands.add(Rights.DEVELOPER, "random", "Forces a random event.", (p, args) -> RandomEvents.attemptSpawnRandom(p, true));

		Commands.add(Rights.DEVELOPER, "freezeme [ticks]", "Freezes you for specific timeframe.", (p, args) -> p.freeze(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "farm", "Force a farming tick.", (p, args) -> p.tickFarming());

		Commands.add(Rights.DEVELOPER, "runespan", "Teleports to runespan.", (p, args) -> {
			p.tele(Tile.of(3995, 6103, 1));
			p.getControllerManager().startController(new RunespanController());
		});

		Commands.add(Rights.DEVELOPER, "proj [id]", "Sends a projectile over the player.", (p, args) -> {
			p.getTempAttribs().setI("tempProjCheck", Integer.parseInt(args[0]));
			World.sendProjectile(Tile.of(p.getX() + 5, p.getY(), p.getPlane()), Tile.of(p.getX() - 5, p.getY(), p.getPlane()), Integer.parseInt(args[0]), new Pair<>(40, 40), 0, 30, 0);
		});

		Commands.add(Rights.DEVELOPER, "projrot [id next/prev]", "Sends a projectile over the player.", (p, args) -> {
			int projId = p.getTempAttribs().getI("tempProjCheck", 0);
			World.sendProjectile(Tile.of(p.getX() + 5, p.getY(), p.getPlane()), Tile.of(p.getX() - 5, p.getY(), p.getPlane()), projId, new Pair<>(40, 40), 0, 30, 0);
			p.getPackets().sendDevConsoleMessage("Projectile: " + projId);
			if (args[0].equals("next"))
				p.getTempAttribs().setI("tempProjCheck", Utils.clampI(projId+1, 0, 5000));
			else
				p.getTempAttribs().setI("tempProjCheck", Utils.clampI(projId-1, 0, 5000));
		});

		Commands.add(Rights.DEVELOPER, "object [id (type) (rotation)]", "Spawns an object south of the player's tile.", (p, args) -> {
			int rotation = args.length > 2 ? Integer.parseInt(args[2]) : 0;
			ObjectType type = args.length > 1 ? ObjectType.forId(Integer.parseInt(args[1])) : ObjectDefinitions.getDefs(Integer.parseInt(args[0])).types[0];
			if (type == null)
				type = ObjectDefinitions.getDefs(Integer.parseInt(args[0])).types[0];
			World.spawnObject(new GameObject(Integer.parseInt(args[0]), type, rotation, p.getX(), p.getY()-1, p.getPlane()));
		});

		Commands.add(Rights.DEVELOPER, "objecttmp [id (type) (rotation)]", "Spawns an object south of the player's tile for 1 tick.", (p, args) -> {
			int rotation = args.length > 2 ? Integer.parseInt(args[2]) : 0;
			ObjectType type = args.length > 1 ? ObjectType.forId(Integer.parseInt(args[1])) : ObjectDefinitions.getDefs(Integer.parseInt(args[0])).types[0];
			if (type == null)
				type = ObjectDefinitions.getDefs(Integer.parseInt(args[0])).types[0];
			GameObject before = World.getSpawnedObject(p.transform(0, -1, 0));
			if (before != null)
				WorldTasks.schedule(3, () -> World.spawnObject(before));
			World.spawnObjectTemporary(new GameObject(Integer.parseInt(args[0]), type, rotation, p.getX(), p.getY()-1, p.getPlane()), 1);
		});

		Commands.add(Rights.DEVELOPER, "cutscene [id]", "Plays a predefined cutscene", (p, args) -> p.getPackets().sendCutscene(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "pin", "Opens bank pin interface.", (p, args) -> p.getBank().openPin());

		Commands.add(Rights.DEVELOPER, "areaobj [(radius)]", "Lists out nearby objects.", (p, args) -> {
			int radius = args.length > 0 ? Integer.parseInt(args[0]) : 0;
			List<GameObject> objects = new ArrayList<>();
			if (radius == 0) {
				GameObject[] objs = World.getBaseObjects(p.getTile());
				if (objs != null)
					for (GameObject obj : objs)
						if (obj != null)
							objects.add(obj);
			} else
				for (int z = -3;z < 3;z++)
					for (int x = -radius;x < radius;x++)
						for (int y = -radius;y < radius;y++) {
							Tile t = p.transform(x, y, z);
							if (t.getPlane() >= 4 || t.getPlane() < 0)
								continue;
							GameObject[] objs = World.getBaseObjects(t);
							if (objs != null)
								for (GameObject obj : objs)
									if (obj != null)
										objects.add(obj);
						}
			for (GameObject o : objects) {
				Logger.debug(MiscTest.class, "areaobj", o);
				Logger.debug(MiscTest.class, "areaobj", "vb: " + o.getDefinitions().varpBit);
			}
		});

		Commands.add(Rights.DEVELOPER, "npcwalkdir", "Spawn npc walking dir.", (player, args) -> {
			Direction dir = Arrays.stream(Direction.values()).filter(n -> n.name().equalsIgnoreCase(args[0])).findFirst().get();
			if (dir == null)
				return;
			NPC npc = World.spawnNPC(1, player.getTile(), true, false, null);
			npc.addWalkSteps(player.getTile().getX() + (dir.dx * 20), player.getTile().getY() + (dir.dy * 20));
		});

		Commands.add(Rights.DEVELOPER, "headicon", "Set custom headicon.", (player, args) -> {
			player.getTempAttribs().setI("customHeadIcon", Integer.parseInt(args[0]));
			player.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.DEVELOPER, "skull", "Set custom skull.", (player, args) -> {
			player.setSkullInfiniteDelay(Integer.parseInt(args[0]));
			player.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.DEVELOPER, "sd", "Search for a door pair.", (p, args) -> Doors.searchDoors(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "getapp", "Prints out appearance data", (p, args) -> p.getAppearance().printDebug());

		Commands.add(Rights.DEVELOPER, "vischunks", "Toggles the visualization of chunks.", (p, args) -> {
			p.getNSV().setB("visChunks", !p.getNSV().getB("visChunks"));
			p.sendMessage("Visualizing chunks: " + p.getNSV().getB("visChunks"));
		});

		Commands.add(Rights.DEVELOPER, "spawntestnpc", "Spawns a combat test NPC.", (p, args) -> {
			NPC n = World.spawnNPC(14256, Tile.of(p.getTile()), true, true);
			n.setLoadsUpdateZones();
			n.setPermName("Losercien (punching bag)");
			n.setHitpoints(Integer.MAX_VALUE / 2);
			n.getCombatDefinitions().setHitpoints(Integer.MAX_VALUE / 2);
			n.setForceMultiArea(true);
			n.setForceMultiAttacked(true);
			n.anim(10993);
		});

		Commands.add(Rights.ADMIN, "clearbank,emptybank", "Empties the players bank entirely.", (p, args) -> p.sendOptionDialogue("Clear bank?", ops -> {
            ops.add("Yes", () -> p.getBank().clear());
            ops.add("No");
        }));

		if (!Settings.getConfig().isDebug()) {
			Commands.add(Rights.DEVELOPER, "exec [command to execute]", "Executes a command-line command on the remote server.", (p, args) -> Launcher.executeCommand(p, Utils.concat(args)));
		}

		Commands.add(Rights.DEVELOPER, "shop [name]", "Opens a shop container of specified id.", (p, args) -> ShopsHandler.openShop(p, args[0]));

		Commands.add(Rights.DEVELOPER, "dial [npcId animId]", "Dialogue box", (p, args) -> {
			p.getInterfaceManager().sendChatBoxInterface(1184);
			p.getPackets().setIFText(1184, 17, NPCDefinitions.getDefs(Integer.parseInt(args[0])).getName());
			p.getPackets().setIFText(1184, 13, "How dare you!");
			p.getPackets().setIFNPCHead(1184, 11, Integer.parseInt(args[0]));
			//p.getPackets().setIFAngle(1184, 11, 100, 1900, 1000);
			p.getPackets().setIFAnimation(Integer.parseInt(args[1]), 1184, 11);
		});

		Commands.add(Rights.DEVELOPER, "dialrot [npcId next/prev/start_num]", "Dialogue box", (p, args) -> {
			int idx = p.getTempAttribs().getI("tempDialCheck", 0);
			if(args[1].matches("[0-9]+"))
				idx = Integer.parseInt(args[1])+1;
			int anim = UNIDENTIFIED_ANIMS[idx];
			p.getInterfaceManager().sendChatBoxInterface(1184);
			p.getPackets().setIFText(1184, 17, NPCDefinitions.getDefs(Integer.parseInt(args[0])).getName());
			p.getPackets().setIFText(1184, 13, "DICK!");
			p.getPackets().setIFNPCHead(1184, 11, Integer.parseInt(args[0]));
			//p.getPackets().setIFAngle(1184, 11, 100, 1900, 1000);
			p.getPackets().setIFAnimation(anim, 1184, 11);
			p.sendMessage("Anim: " + anim);
			if (args[1].equals("next"))
				p.getTempAttribs().setI("tempDialCheck", Utils.clampI(idx+1, 0, UNIDENTIFIED_ANIMS.length-1));
			else
				p.getTempAttribs().setI("tempDialCheck", Utils.clampI(idx-1, 0, UNIDENTIFIED_ANIMS.length-1));
			p.getPackets().sendDevConsoleMessage(idx + "/" + UNIDENTIFIED_ANIMS.length);
		});

		Commands.add(Rights.DEVELOPER, "icompanim [interfaceId componentId animId]", "Plays animation id on interface component.", (p, args) -> {
			p.getInterfaceManager().sendInterface(Integer.parseInt(args[0]));
			p.getPackets().setIFAnimation(Integer.parseInt(args[2]), Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		});

		Commands.add(Rights.DEVELOPER, "icompanimrot [interfaceId componentId next/prev]", "Rotates animations on an interface component.", (p, args) -> {
			int idx = p.getTempAttribs().getI("tempDialCheck", 0);
			int anim = UNIDENTIFIED_ANIMS[idx];
			p.getInterfaceManager().sendInterface(Integer.parseInt(args[0]));
			p.getPackets().setIFAnimation(anim, Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			p.sendMessage("Anim: " + anim);
			if (args[2].equals("next"))
				p.getTempAttribs().setI("tempDialCheck", Utils.clampI(idx+1, 0, UNIDENTIFIED_ANIMS.length-1));
			else
				p.getTempAttribs().setI("tempDialCheck", Utils.clampI(idx-1, 0, UNIDENTIFIED_ANIMS.length-1));
			p.getPackets().sendDevConsoleMessage(idx + "/" + UNIDENTIFIED_ANIMS.length);
		});

		Commands.add(Rights.DEVELOPER, "setlook [slot id]", "Appearance setting", (p, args) -> {
			p.getAppearance().setLook(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			p.getAppearance().generateAppearanceData();
		});

		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.DEVELOPER, "sound [id]", "Plays a sound effect.", (p, args) ->
				p.soundEffect(Integer.parseInt(args[0]), true));

		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.DEVELOPER, "tilesound [id]", "Plays a tile sound effect.", (p, args) ->
				World.soundEffect(p.getTile(), Integer.parseInt(args[0])));


		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.DEVELOPER, "music [id (volume)]", "Plays a music track.", (p, args) -> p.getMusicsManager().playSongWithoutUnlocking(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "unusedmusic", "Shows unused music.", (p, args) -> {
			int count = 0;
			for(int i = 0; i < 1099; i++)
				if(Music.getSongGenres(i).length == 0) {
					Song song = Music.getSong(i);
					count++;
					if(song == null)
						Logger.error(MiscTest.class, "unusedmusic", "Error @" + i);
					else
						Logger.debug(MiscTest.class, "unusedmusic", i + " " + song.getName() + ": " + song.getHint());
				}
			Logger.debug(MiscTest.class, "unusedmusic", "Total unused: " + count);
			Logger.debug(MiscTest.class, "unusedmusic", "Unused is " + Math.ceil(count/1099.0*100) + "%");
		});

		Commands.add(Rights.DEVELOPER, "nextm", "Plays a music track.", (p, args) -> p.getMusicsManager().nextAmbientSong());

		Commands.add(Rights.DEVELOPER, "genre", "Shows genre", (p, args) -> {
            Genre genre = p.getMusicsManager().getPlayingGenre();
            if(genre == null)
                p.sendMessage("No genre, remember this updates after an ambient song is played...");
            else
				p.sendMessage(genre.getGenreName());
		});


		Commands.add(Rights.DEVELOPER, "script", "Runs a clientscript with no arguments.", (p, args) -> p.getPackets().sendRunScriptBlank(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "scriptargs", "Runs a clientscript with no arguments.", (p, args) -> {
			Object[] scriptArgs = new Object[args.length-1];
			for (int i = 1;i < args.length;i++) {
				try {
					scriptArgs[i - 1] = Integer.parseInt(args[i]);
				} catch(Throwable e) {
					scriptArgs[i - 1] = args[i];
				}
			}
			p.getPackets().sendRunScript(Integer.parseInt(args[0]), scriptArgs);
		});

		Commands.add(Rights.DEVELOPER, "frogland", "Plays frogland to everyone on the server.", (p, args) -> World.allPlayers(target -> {
            target.getPackets().sendRunScript(1764, 12451857, 12451853, 20, 0); //0 music volume, 1 sound effect volume, 2 ambient sound volume
            target.musicTrack(409);
        }));

		Commands.add(Rights.DEVELOPER, "musicall [id]", "Plays music to everyone on the server.", (p, args) -> World.allPlayers(target -> {
            target.getPackets().sendRunScript(1764, 12451857, 12451853, 20, 0); //0 music volume, 1 sound effect volume, 2 ambient sound volume
            target.musicTrack(Integer.parseInt(args[0]));
        }));

		Commands.add(Rights.DEVELOPER, "jingle [id]", "plays jingles", (p, args) -> p.jingle(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "tileflags", "Get the tile flags for the tile you're standing on.", (p, args) -> p.sendMessage("" + ClipFlag.getFlags(WorldCollision.getFlags(p.getTile()))));

		Commands.add(Rights.DEVELOPER, "cheev [id]", "Sends achievement complete interface.", (p, args) -> p.getInterfaceManager().sendAchievementComplete(Achievement.forId(Integer.parseInt(args[0]))));

		Commands.add(Rights.ADMIN, "update,restart [ticks]", "Restarts the server after specified number of ticks.", (p, args) -> World.safeShutdown(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "npc [npcId]", "Spawns an NPC with specified ID.", (p, args) -> World.spawnNPC(Integer.parseInt(args[0]), Tile.of(p.getTile()), true, false));
		Commands.add(Rights.DEVELOPER, "npcwithfunc [npcId]", "Spawns an NPC with specified ID with its custom functionality.", (p, args) -> World.spawnNPC(Integer.parseInt(args[0]), Tile.of(p.getTile()), true, true));

		Commands.add(Rights.DEVELOPER, "addnpc [npcId]", "Spawns an NPC permanently with specified ID.", (p, args) -> {
			if (!Settings.getConfig().isDebug())
				return;
			if (NPCSpawns.addSpawn(p.getUsername(), Integer.parseInt(args[0]), Tile.of(p.getTile())))
				p.sendMessage("Added spawn.");
		});

		Commands.add(Rights.DEVELOPER, "dropitem", "Spawns an item on the floor until it is picked up.", (p, args) -> World.addGroundItem(new Item(Integer.parseInt(args[0]), 1), Tile.of(p.getX(), p.getY(), p.getPlane())));

		Commands.add(Rights.DEVELOPER, "addgrounditem,addgitem [itemId respawnTicks]", "Spawns a ground item permanently with specified ID.", (p, args) -> {
			if (!Settings.getConfig().isDebug())
				return;
			if (ItemSpawns.addSpawn(p.getUsername(), Integer.parseInt(args[0]), 1, Integer.parseInt(args[1]), Tile.of(p.getTile())))
				p.sendMessage("Added spawn.");
		});

		Commands.add(Rights.ADMIN, "god", "Toggles god mode for the player.", (p, args) -> {
			p.getNSV().setB("godMode", !p.getNSV().getB("godMode"));
			p.sendMessage("GODMODE: " + p.getNSV().getB("godMode"));
		});

		Commands.add(Rights.ADMIN, "unnullall", "Forces the player out of a controller and unlocks them hopefully freeing any stuck-ness.", (p, args) -> {
			for (Player player : World.getPlayers()) {
				if (player == null)
					continue;
				player.unlock();
				player.getControllerManager().forceStop();
				if (player.getMoveTile() == null)
					player.tele(Settings.getConfig().getPlayerRespawnTile());
			}
		});

		Commands.add(Rights.ADMIN, "infspec", "Toggles infinite special attack for the player.", (p, args) -> {
			p.getNSV().setB("infSpecialAttack", !p.getNSV().getB("infSpecialAttack"));
			p.sendMessage("INFINITE SPECIAL ATTACK: " + p.getNSV().getB("infSpecialAttack"));
		});

		Commands.add(Rights.ADMIN, "infpray", "Toggles infinite prayer for the player.", (p, args) -> {
			p.getNSV().setB("infPrayer", !p.getNSV().getB("infPrayer"));
			p.sendMessage("INFINITE PRAYER: " + p.getNSV().getB("infPrayer"));
		});

		Commands.add(Rights.ADMIN, "infrun", "Toggles infinite run for the player.", (p, args) -> {
			p.getNSV().setB("infRun", !p.getNSV().getB("infRun"));
			p.sendMessage("INFINITE RUN: " + p.getNSV().getB("infRun"));
		});

		Commands.add(Rights.ADMIN, "infrunes", "Toggles infinite runes for the player.", (p, args) -> {
			p.getNSV().setB("infRunes", !p.getNSV().getB("infRunes"));
			p.sendMessage("INFINITE RUNES: " + p.getNSV().getB("infRunes"));
		});

		Commands.add(Rights.ADMIN, "maxbank", "Sets all the item counts in the player's bank to 10m.", (p, args) -> {
			for (Item i : p.getBank().getContainerCopy())
				if (i != null)
					i.setAmount(10500000);
		});

		Commands.add(Rights.ADMIN, "spellbook [modern/lunar/ancient]", "Switches to modern, lunar, or ancient spellbooks.", (p, args) -> {
			switch(args[0].toLowerCase()) {
				case "modern":
				case "normal":
					p.getCombatDefinitions().setSpellbook(Spellbook.MODERN);
					break;
				case "ancient":
				case "ancients":
					p.getCombatDefinitions().setSpellbook(Spellbook.ANCIENT);
					break;
				case "lunar":
				case "lunars":
					p.getCombatDefinitions().setSpellbook(Spellbook.LUNAR);
					break;
				case "dung":
					p.getCombatDefinitions().setSpellbook(Spellbook.DUNGEONEERING);
					break;
				default:
					p.sendMessage("Invalid spellbook. Spellbooks are modern, lunar, ancient, and dung");
					break;
			}
		});

		Commands.add(Rights.ADMIN, "prayers [normal/curses]", "Switches to curses, or normal prayers.", (p, args) -> {
			switch(args[0].toLowerCase()) {
				case "normal":
				case "normals":
					p.getPrayer().setPrayerBook(false);
					break;
				case "curses":
				case "ancients":
					p.getPrayer().setPrayerBook(true);
					break;
				default:
					p.sendMessage("Invalid prayer book. Prayer books are normal and curses.");
					break;
			}
		});

		Commands.add(Rights.DEVELOPER, "reloadlocalmap", "Forces your local map to reload", (p, args) -> {
			for (GameObject obj : ChunkManager.getChunk(p.getChunkId()).getAllBaseObjects(true)) {
				p.sendMessage(obj.toString());
			}
			if (ChunkManager.getChunk(p.getChunkId()) instanceof InstancedChunk c)
				p.sendMessage(c.getOriginalBaseX() + ", " + c.getOriginalBaseY() + " - " + c.getRotation());

			p.setForceNextMapLoadRefresh(true);
			p.loadMapRegions();
		});

		Commands.add(Rights.DEVELOPER, "reloadshops", "Reloads the shop data file.", (p, args) -> ShopsHandler.reloadShops());

		Commands.add(Rights.DEVELOPER, "reloadcombat", "Reloads the NPC combat definitions files.", (p, args) -> {
			NPCCombatDefinitions.reload();
			for (NPC npc : World.getNPCs())
				if (npc != null)
					npc.resetLevels();
		});

		Commands.add(Rights.DEVELOPER, "reloaddrops", "Reloads the drop table files.", (p, args) -> DropSets.reloadDrops());

		Commands.add(Rights.DEVELOPER, "loginmessage,loginmes [announcement]", "Sets the server login announcement.", (p, args) -> {
			Settings.getConfig().setLoginMessage("<shad=000000><col=ff0000>" + Utils.concat(args));
            try {
                Settings.saveConfig();
            } catch (IOException e) {
               	p.sendMessage("Failed to save config");
            }
            p.sendMessage(Settings.getConfig().getLoginMessage());
		});

		Commands.add(Rights.DEVELOPER, "coords,getpos,mypos,pos,loc", "Gets the coordinates for the tile.", (p, args) -> {
			p.sendMessage("Coords: " + p.getX() + "," + p.getY() + "," + p.getPlane() + ", regionId: " + p.getRegionId() + ", chunkX: " + p.getChunkX() + ", chunkY: " + p.getChunkY() + ", hash: " + p.getTileHash());
			p.sendMessage("ChunkId: " + p.getChunkId());
			if (ChunkManager.getChunk(p.getChunkId()) instanceof InstancedChunk instance)
				p.sendMessage("In instanced chunk copied from: " + instance.getOriginalBaseX() + ", " + instance.getOriginalBaseY() + " rotation: " + instance.getRotation());
			p.sendMessage("JagCoords: " + p.getPlane() + ","+p.getRegionX()+","+p.getRegionY()+","+p.getXInRegion()+","+p.getYInRegion());
			p.sendMessage("Local coords: " + p.getXInRegion() + " , " + p.getYInRegion());
			p.sendMessage("16x16: " +(p.getXInScene(p.getSceneBaseChunkId()) % 16) +", "+(p.getYInScene(p.getSceneBaseChunkId()) % 16));
		});

		Commands.add(Rights.ADMIN, "search,si,itemid [item name]", "Searches for items containing the words searched.", (p, args) -> {
			p.getPackets().sendDevConsoleMessage("Searching for items containing: " + Arrays.toString(args));
			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
				boolean contains = true;
				for (String arg : args)
					if (!ItemDefinitions.getDefs(i).getName().toLowerCase().contains(arg.toLowerCase()) || ItemDefinitions.getDefs(i).isLended()) {
						contains = false;
						continue;
					}
				if (contains)
					p.getPackets().sendDevConsoleMessage("Result found: " + i + " - " + ItemDefinitions.getDefs(i).getName() + " " + (ItemDefinitions.getDefs(i).isNoted() ? "(noted)" : "") + "" + (ItemDefinitions.getDefs(i).isLended() ? "(lent)" : ""));
			}
		});

		Commands.add(Rights.ADMIN, "item,spawn [itemId (amount)]", "Spawns an item with specified id and amount.", (p, args) -> {
			p.getInventory().addItem(Integer.parseInt(args[0]), args.length >= 2 ? Integer.parseInt(args[1]) : 1);
			p.stopAll();
		});

		Commands.add(Rights.ADMIN, "master,max", "Maxes all stats out.", (p, args) -> {
			for (int skill = 0; skill < 25; skill++)
				p.getSkills().setXp(skill, 105000000);
			p.reset();
			p.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.ADMIN, "ironman [true/false]", "Changes ironman status of the player.", (p, args) -> p.setIronMan(Boolean.valueOf(args[0])));

		Commands.add(Rights.DEVELOPER, "killnpcs", "Kills all npcs around the player.", (p, args) -> {
			for (NPC npc : World.getNPCsInChunkRange(p.getChunkId(), 3)) {
				if (npc instanceof Familiar || npc instanceof Pet)
					continue;
				if (Utils.getDistance(npc.getTile(), p.getTile()) < 9 && npc.getPlane() == p.getPlane()) {
					for (int i = 0; i < 100; ++i)
						npc.applyHit(new Hit(p, 10000, HitLook.TRUE_DAMAGE));
				}
			}
		});

		Commands.add(Rights.ADMIN, "boostlevel [skillId level]", "Sets a skill to a specified level.", (p, args) -> {
			int skill = Integer.parseInt(args[0]);
			int level = Integer.parseInt(args[1]);

			p.sendMessage("Boosting " + Skills.SKILL_NAME[skill] + " by " + level);
			p.getSkills().set(skill, level);
		});

		Commands.add(Rights.DEVELOPER, "deathnpcs", "Kills all npcs around the player.", (p, args) -> {
			for (NPC npc : World.getNPCs()) {
				if (npc instanceof Familiar || npc instanceof Pet)
					continue;
				if (Utils.getDistance(npc.getTile(), p.getTile()) < 9 && npc.getPlane() == p.getPlane())
					npc.sendDeath(p);
			}
		});

		Commands.add(Rights.DEVELOPER, "resetquest [questName]", "Resets the specified quest.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.name().toLowerCase().contains(args[0]) && quest.isImplemented()) {
					p.getQuestManager().resetQuest(quest);
					p.sendMessage("Reset quest: " + quest.name());
					return;
				}
			for (Miniquest quest : Miniquest.values())
				if (quest.name().toLowerCase().contains(args[0]) && quest.isImplemented()) {
					p.getMiniquestManager().reset(quest);
					p.sendMessage("Reset miniquest: " + quest.name());
					return;
				}
		});

		Commands.add(Rights.DEVELOPER, "completequest [questName]", "Resets the specified quest.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.name().toLowerCase().contains(args[0])) {
					p.getQuestManager().completeQuest(quest);
					p.sendMessage("Completed quest: " + quest.name());
					return;
				}
			for (Miniquest quest : Miniquest.values())
				if (quest.name().toLowerCase().contains(args[0])) {
					p.getMiniquestManager().complete(quest);
					p.sendMessage("Completed miniquest: " + quest.name());
					return;
				}
		});

		Commands.add(Rights.DEVELOPER, "completeallquests", "Completes all quests.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.isImplemented()) {
					p.getQuestManager().completeQuest(quest);
					p.sendMessage("Completed quest: " + quest.name());
				}
			for (Miniquest quest : Miniquest.values())
				if (quest.isImplemented()) {
					p.getMiniquestManager().complete(quest);
					p.sendMessage("Completed miniquest: " + quest.name());
				}
		});

		Commands.add(Rights.DEVELOPER, "resetallquests", "Resets all quests.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.isImplemented()) {
					p.getQuestManager().resetQuest(quest);
					p.sendMessage("Reset quest: " + quest.name());
				}
			for (Miniquest quest : Miniquest.values())
				if (quest.isImplemented()) {
					p.getMiniquestManager().reset(quest);
					p.sendMessage("Reset miniquest: " + quest.name());
				}
		});

		Commands.add(Rights.DEVELOPER, "hinttrail [x y modelId]", "Sets a hint trail from the player to the specified location.", (p, args) -> {
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			int modelId = Integer.parseInt(args[2]);
			//47868
			Route route = RouteFinderKt.routeEntityToTile(p, Tile.of(x, y, p.getPlane()), 25);
			int[] bufferX = new int[route.size()];
			int[] bufferY = new int[route.size()];
			int i = 0;
			for (RouteCoordinates tile : route.getCoords()) {
				bufferX[i] = tile.getPacked() & 0xFFFF;
				bufferY[i++] = (tile.getPacked() >> 16) & 0xFFFF;
			}
			p.getSession().writeToQueue(new HintTrail(Tile.of(p.getTile()), modelId, bufferX, bufferY, i));
		});

		Commands.add(Rights.DEVELOPER, "searchobj,so [objectId index]", "Searches the entire gameworld for an object matching the ID and teleports you to it.", (p, args) -> {
			List<GameObject> objs = MapSearcher.getObjectsById(Integer.parseInt(args[0]));
			if (objs.isEmpty()) {
				p.sendMessage("Nothing found for " + args[0]);
				return;
			}
			int i = 0;
			for (GameObject obj : objs)
				p.getPackets().sendDevConsoleMessage(i++ + ": " + obj.toString());
			if(args.length == 1) {
				p.tele(objs.getFirst().getTile());
				return;
			}
			p.tele(objs.get(Integer.parseInt(args[1])).getTile());
		});

		Commands.add(Rights.DEVELOPER, "searchnpc,sn [npcId index]", "Searches the entire (loaded) gameworld for an NPC matching the ID and teleports you to it.", (p, args) -> {
			int i = 0;
			List<NPCSpawn> npcs = NPCSpawns.getAllSpawns().stream().filter(n -> n.getNPCId() == Integer.parseInt(args[0])).toList();
			for(NPCSpawn npc : npcs)
				p.getPackets().sendDevConsoleMessage(i++ + ": " + npc.toString());
			if (args.length == 1) {
				p.tele(Tile.of(npcs.getFirst().getTile()));
				return;
			}
			p.tele(npcs.get(Integer.parseInt(args[1])).getTile());
		});

		Commands.add(Rights.ADMIN, "hide", "Hides the player from other players.", (p, args) -> {
			p.setTrulyHidden(!p.isTrulyHidden());
			p.sendMessage("Hidden: " + p.isTrulyHidden());
		});

		Commands.add(Rights.ADMIN, "setlevel [skillId level]", "Sets a skill to a specified level.", (p, args) -> {
			int skill = Integer.parseInt(args[0]);
			int level = Integer.parseInt(args[1]);
			if (level < 0 || level > (skill == Constants.DUNGEONEERING ? 120 : 99)) {
				p.sendMessage("Please choose a valid level.");
				return;
			}
			if (skill < 0 || skill >= Constants.SKILL_NAME.length) {
				p.sendMessage("Please choose a valid skill.");
				return;
			}
			p.getSkills().set(skill, level);
			p.getSkills().setXp(skill, Skills.getXPForLevel(level));
			p.getAppearance().generateAppearanceData();
			p.reset();
			p.sendMessage("Successfully set " + Constants.SKILL_NAME[skill] + " to " + level + ".");
		});

		Commands.add(Rights.ADMIN, "unlockdgfloors", "Unlocks all dungeoneering floors and complexities.", (p, args) -> {
			p.getDungManager().setMaxFloor(60);
			p.getDungManager().setMaxComplexity(6);
		});

		Commands.add(Rights.ADMIN, "reset", "Resets all stats to 1.", (p, args) -> {
			for (int skill = 0; skill < 25; skill++)
				p.getSkills().setXp(skill, 0);
			p.getSkills().init();
		});

		Commands.add(Rights.DEVELOPER, "voice, v [id]", "Plays voices.", (p, args) -> {
			p.getSession().write(ServerPacket.RESET_SOUNDS);
			p.voiceEffect(Integer.parseInt(args[0]), true);
		});

		Commands.add(Rights.DEVELOPER, "familiarhead", "Tests familiar/pet chathead icons", (p, args) -> {
			Pouch pouch = Pouch.valueOf(args[0]);
			if (pouch == null) {
				p.sendMessage("Invalid pouch name.");
				return;
			}
			p.getVars().setVar(448, pouch.getId());// configures familiar type
			p.getVars().setVar(1174, 0, true); //refresh familiar head
			p.getVars().setVarBit(4282, Integer.parseInt(args[1])); //refresh familiar emote
		});

		Commands.add(Rights.DEVELOPER, "playthroughvoices [start finish tick_delay]", "Gets player rights", (p, args) -> {
			//		Voice[] voices = new Voice[3];
			//		voices[0] = new Voice("Test1", new int[]{1, 2, 3});
			//		voices[1] = new Voice("Test3", new int[]{4, 5, 6});
			//		voices[2] = new Voice("Test4", new int[]{7, 8, 9});
			//		try {
			//			JsonFileManager.saveJsonFile(voices, new File("developer-information/voice.json"));
			//		} catch(Exception e) {
			//			Logger.debug(e.getStackTrace());
			//		}

			int tickDelay = Integer.parseInt(args[2]);

			WorldTasks.scheduleLooping(new Task() {
				int tick;
				int voiceID = 0;

				@Override
				public void run() {
					if(tick == 0)
						voiceID = Integer.parseInt(args[0]);

					if(Voices.voicesMarked.contains(voiceID))
						for(int i = voiceID; i < 100_000; i++) {
							if(!Voices.voicesMarked.contains(voiceID))
								break;
							voiceID++;
						}

					if(!Voices.voicesMarked.contains(voiceID)) {
						p.sendMessage("Playing voice " + voiceID);
						p.voiceEffect(voiceID++, true);
					}

					if(voiceID > Integer.parseInt(args[1]))
						stop();
					tick++;
				}
			}, 0, tickDelay);
		});

		Commands.add(Rights.ADMIN, "tele,tp [x y (z)] or [tileHash] or [z,regionX,regionY,localX,localY]", "Teleports the player to a coordinate.", (p, args) -> {
			if (args[0].contains(",")) {
				args = args[0].split(",");
				int plane = Integer.parseInt(args[0]);
				int x = Integer.parseInt(args[1]) << 6 | Integer.parseInt(args[3]);
				int y = Integer.parseInt(args[2]) << 6 | Integer.parseInt(args[4]);
				p.resetWalkSteps();
				p.tele(Tile.of(x, y, plane));
			} else if (args.length == 1) {
				p.resetWalkSteps();
				p.tele(Tile.of(Integer.parseInt(args[0])));
			} else {
				p.resetWalkSteps();
				p.tele(Tile.of(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args.length >= 3 ? Integer.parseInt(args[2]) : p.getPlane()));
			}
		});

		Commands.add(Rights.ADMIN, "teler,tpr [regionId]", "Teleports the player to a region id.", (p, args) -> {
			int regionX = (Integer.parseInt(args[0]) >> 8) * 64 + 32;
			int regionY = (Integer.parseInt(args[0]) & 0xff) * 64 + 32;
			p.resetWalkSteps();
			p.tele(Tile.of(regionX, regionY, 0));
		});

		Commands.add(Rights.ADMIN, "telec,tpc [chunkId]", "Teleports the player to chunk coordinates.", (p, args) -> {
			int[] coords = MapUtils.decode(MapUtils.Structure.CHUNK, Integer.parseInt(args[0]));
			p.resetWalkSteps();
			p.tele(Tile.of(coords[0] * 8 + 4, coords[1] * 8 + 4, coords[2]));
		});

		Commands.add(Rights.ADMIN, "settitle [new title]", "Sets player title.", (p, args) -> {
			StringBuilder title = new StringBuilder();
			for (int i = 0; i < args.length; i++)
				title.append(args[i]).append((i == args.length - 1) ? "" : " ");
			p.setTitle(title.toString());
			p.setTitleColor(null);
			p.setTitleShading(null);
			p.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.ADMIN, "cleartitle,deltitle,removetitle", "Removes player title.", (p, args) -> {
			p.setTitle(null);
			p.setTitleColor(null);
			p.setTitleShading(null);
			p.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.DEVELOPER, "dropstobank,bankdrops", "Will send all drops recieved from monsters directly to the bank.", (p, args) -> p.getNSV().setB("sendingDropsToBank", true));

		Commands.add(Rights.DEVELOPER, "spotanim,gfx [id height]", "Creates a spot animation on top of the player.", (p, args) -> p.setNextSpotAnim(new SpotAnim(Integer.parseInt(args[0]), 0, args.length == 1 ? 0 : Integer.parseInt(args[1]))));

		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.DEVELOPER, "anim,emote [id]", "Animates the player with specified ID.", (p, args) -> {
			if (Integer.parseInt(args[0]) > Utils.getAnimationDefinitionsSize())
				return;
			p.setNextAnimation(new Animation(Integer.parseInt(args[0])));
		});

		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.DEVELOPER, "sync,animgfx [id]", "Animates the player with specified ID and plays a SpotAnim at the same time.", (p, args) -> {
			if ((Integer.parseInt(args[0]) > Utils.getAnimationDefinitionsSize()) || (Integer.parseInt(args[1]) > Utils.getSpotAnimDefinitionsSize()))
				return;
			p.setNextAnimation(new Animation(Integer.parseInt(args[0])));
			p.setNextSpotAnim(new SpotAnim(Integer.parseInt(args[1])));
		});

		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.DEVELOPER, "bas,render [id]", "Sets the BAS of the player to specified ID.", (p, args) -> p.getAppearance().setBAS(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "camlook [localX localY z (speed1 speed2)]", "Points the camera at the specified tile.", (p, args) -> {
			Chunk chunk = ChunkManager.getChunk(p.getSceneBaseChunkId());
			Tile tile = Tile.of(chunk.getBaseX() + Integer.parseInt(args[0]), chunk.getBaseY() + Integer.parseInt(args[1]), p.getPlane());
			if (p.getInstancedArea() != null)
				tile = Tile.of(p.getInstancedArea().getLocalTile(Integer.parseInt(args[0]), Integer.parseInt(args[1]), p.getPlane()));
			if (args.length == 3)
				p.getPackets().sendCameraLook(tile, Integer.parseInt(args[2]));
			else if (args.length == 5)
				p.getPackets().sendCameraLook(tile, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		});

		Commands.add(Rights.DEVELOPER, "campos [localX localY z (speed1 speed2)]", "Locks the camera to a specified tile.", (p, args) -> {
			Chunk chunk = ChunkManager.getChunk(p.getSceneBaseChunkId());
			Tile tile = Tile.of(chunk.getBaseX() + Integer.parseInt(args[0]), chunk.getBaseY() + Integer.parseInt(args[1]), p.getPlane());
			if (p.getInstancedArea() != null)
				tile = Tile.of(p.getInstancedArea().getLocalTile(Integer.parseInt(args[0]), Integer.parseInt(args[1]), p.getPlane()));
			if (args.length == 3)
				p.getPackets().sendCameraPos(tile, Integer.parseInt(args[2]));
			else if (args.length == 5)
				p.getPackets().sendCameraPos(tile, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		});

		Commands.add(Rights.DEVELOPER, "resetcam", "Resets the camera back on the player.", (p, _) -> p.getPackets().sendResetCamera());

		Commands.add(Rights.ADMIN, "spec", "Restores special attack energy to full.", (p, args) -> p.getCombatDefinitions().resetSpecialAttack());

		Commands.add(Rights.ADMIN, "bank", "Opens the bank.", (p, args) -> p.getBank().open());

		Commands.add(Rights.ADMIN, "empty", "Empties the player's inventory.", (p, args) -> {
			p.stopAll();
			p.getInventory().reset();
		});

		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.ADMIN, "tonpc,pnpc,npcme [npcId]", "Transforms the player into an NPC.", (p, args) -> {
			if (Integer.parseInt(args[0]) > Utils.getNPCDefinitionsSize())
				return;
			p.getAppearance().transformIntoNPC(Integer.parseInt(args[0]));
		});

		Commands.add(Rights.ADMIN, "camshake [slot, v1, v2, v3, v4]", "Transforms the player into an NPC.", (p, args) -> p.getPackets().sendCameraShake(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])));

		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.ADMIN, "inter [interfaceId]", "Opens an interface with specific ID.", (p, args) -> p.getInterfaceManager().sendInterface(Integer.parseInt(args[0])));

		Commands.add(Rights.DEVELOPER, "winter [interfaceId componentId]", "Sends an interface to the window specified component.", (p, args) -> {
			if (Integer.parseInt(args[1]) > Utils.getInterfaceDefinitionsComponentsSize(p.resizeable() ? InterfaceManager.RESIZEABLE_TOP : InterfaceManager.FIXED_TOP))
				return;
			if (p.getNSV().getI("prevWinterCmd", -1) != -1)
				p.getInterfaceManager().removeGameWindowSub(p.getNSV().getI("prevWinterCmd", -1), p.getNSV().getI("prevWinterCmd", -1));
			//inter 115 to test well
			p.getNSV().setI("prevWinterCmd", Integer.parseInt(args[1]));
			p.getInterfaceManager().sendGameWindowSub(Integer.parseInt(args[1]), Integer.parseInt(args[1]), Integer.parseInt(args[0]), true);
		});

		Commands.add(Rights.ADMIN, "istrings [interfaceId]", "Debugs an interface's text components.", (p, args) -> {
			int interId = Integer.parseInt(args[0]);
			p.getInterfaceManager().sendInterface(interId);
			for (int componentId = 0; componentId < Utils.getInterfaceDefinitionsComponentsSize(interId); componentId++)
				p.getPackets().setIFText(interId, componentId, ""+componentId);
		});

		Commands.add(Rights.DEVELOPER, "iftext [interfaceId componentId text]", "Sets the text of an interface.", (p, args) -> {
			int interId = Integer.parseInt(args[0]);
			int compId = Integer.parseInt(args[1]);
			String val = args[2];
			p.getInterfaceManager().sendInterface(interId);
			p.getPackets().setIFText(interId, compId, val);
		});

		Commands.add(Rights.DEVELOPER, "ifgraphic [interfaceId componentId graphicId]", "Sets the graphic of an interface.", (p, args) -> {
			int interId = Integer.parseInt(args[0]);
			int compId = Integer.parseInt(args[1]);
			int graphicId = Integer.parseInt(args[2]);
			p.getPackets().setIFGraphic(interId, compId, graphicId);
		});

		Commands.add(Rights.DEVELOPER, "imodels [interfaceId]", "Debugs an interface's models.", (p, args) -> {
			int interId = Integer.parseInt(args[0]);
			p.getInterfaceManager().sendInterface(interId);
			for (int componentId = 0; componentId < Utils.getInterfaceDefinitionsComponentsSize(interId); componentId++)
				p.getPackets().setIFModel(interId, componentId, 66);
		});

		Commands.add(Rights.DEVELOPER, "imodel [interfaceId componentId modelId]", "Sends a model to an interface", (p, args) -> {
			int interId = Integer.parseInt(args[0]);
			int compId = Integer.parseInt(args[1]);
			if (compId > Utils.getInterfaceDefinitionsComponentsSize(interId)) {
				p.sendMessage("There are " + Utils.getInterfaceDefinitionsComponentsSize(interId) + " components. Too high.");
				return;
			}
			p.getInterfaceManager().sendInterface(interId);
			p.getPackets().setIFModel(interId, compId, Integer.parseInt(args[2]));
		});

		Commands.add(Settings.getConfig().isDebug() ? Rights.PLAYER : Rights.DEVELOPER, "companim [npcId]", "Prints out animations compatible with the npc id.", (p, args) -> {
			if (Integer.parseInt(args[0]) > Utils.getNPCDefinitionsSize())
				return;
			NPCDefinitions defs = NPCDefinitions.getDefs(Integer.parseInt(args[0]));
			if (defs == null)
				return;
			p.getPackets().sendDevConsoleMessage(Integer.parseInt(args[0]) + ": " + defs.getCompatibleAnimations().toString());
			p.sendMessage(Integer.parseInt(args[0]) + ": " + defs.getCompatibleAnimations().toString());
			Logger.debug(MiscTest.class, "companim", defs.getCompatibleAnimations().toString());
		});

		Commands.add(Rights.DEVELOPER, "varcstr [id value]", "Sets a varc string value.", (p, args) -> p.getPackets().sendVarcString(Integer.parseInt(args[0]), Utils.concat(args, 1)));

		Commands.add(Rights.DEVELOPER, "varcstrloop [startId endId]", "Sets a varc string value.", (p, args) -> {
			for (int i = Integer.parseInt(args[0]); i < Integer.parseInt(args[1]); i++) {
				if (i >= Cache.STORE.getIndex(IndexType.CONFIG).getValidFilesCount(ArchiveType.VARC_STRING.getId()))
					break;
				p.getPackets().sendVarcString(i, "str"+i);
			}
		});

		Commands.add(Rights.DEVELOPER, "varc [id value]", "Sets a varc value.", (p, args) -> p.getPackets().sendVarc(Integer.parseInt(args[0]), Integer.parseInt(args[1])));

		Commands.add(Rights.DEVELOPER, "varcloop [startId endId value]", "Sets varc value for all varcs between 2 ids.", (p, args) -> {
			for (int i = Integer.parseInt(args[0]); i < Integer.parseInt(args[1]); i++) {
				if (i >= Cache.STORE.getIndex(IndexType.CONFIG).getValidFilesCount(ArchiveType.VARC.getId()))
					break;
				p.getPackets().sendVarc(i, Integer.parseInt(args[2]));
			}
		});

		Commands.add(Rights.DEVELOPER, "var [id value]", "Sets a var value.", (p, args) -> p.getVars().setVar(Integer.parseInt(args[0]), Integer.parseInt(args[1])));

		Commands.add(Rights.DEVELOPER, "getvar [id]", "Gets a var value.", (p, args) -> p.getPackets().sendDevConsoleMessage("Var: " + Integer.parseInt(args[0]) + " -> " + p.getVars().getVar(Integer.parseInt(args[0]))));

		Commands.add(Rights.DEVELOPER, "varloop [startId endId value]", "Sets var value for all vars between 2 ids.", (p, args) -> {
			for (int i = Integer.parseInt(args[0]); i < Integer.parseInt(args[1]); i++) {
				if (i >= Cache.STORE.getIndex(IndexType.CONFIG).getValidFilesCount(ArchiveType.VARS.getId()))
					break;
				p.getVars().setVar(i, Integer.parseInt(args[2]));
			}
		});

		Commands.add(Rights.DEVELOPER, "getvarbit [id]", "Gets a varbit value.", (p, args) -> p.getPackets().sendDevConsoleMessage("Varbit: " + Integer.parseInt(args[0]) + " -> " + p.getVars().getVarBit(Integer.parseInt(args[0]))));

		Commands.add(Rights.DEVELOPER, "varbit [id value]", "Sets a varbit value.", (p, args) -> p.getVars().setVarBit(Integer.parseInt(args[0]), Integer.parseInt(args[1])));

		Commands.add(Rights.DEVELOPER, "varbitloop [startId endId value]", "Sets varbit value for all varbits between 2 ids.", (p, args) -> {
			for (int i = Integer.parseInt(args[0]); i < Integer.parseInt(args[1]); i++) {
				if (i >= Utils.getVarbitDefinitionsSize())
					break;
				p.getVars().setVarBit(i, Integer.parseInt(args[2]));
			}
		});

		Commands.add(Rights.ADMIN, "resettask", "Resets current slayer task.", (p, args) -> {
			p.getSlayer().removeTask();
			p.updateSlayerTask();
		});

		Commands.add(Rights.DEVELOPER, "objectanim,oanim [x y (objectType)]", "Makes an object play an animation.", (p, args) -> {
			GameObject object = args.length == 3 ? World.getObject(Tile.of(Integer.parseInt(args[0]), Integer.parseInt(args[1]), p.getPlane())) : World.getObject(Tile.of(Integer.parseInt(args[0]), Integer.parseInt(args[1]), p.getPlane()), ObjectType.forId(Integer.parseInt(args[2])));
			if (object == null) {
				p.getPackets().sendDevConsoleMessage("No object was found.");
				return;
			}
			p.getPackets().sendObjectAnimation(object, new Animation(Integer.parseInt(args[args.length == 3 ? 2 : 3])));
		});

		Commands.add(Rights.DEVELOPER, "objectanimloop,oanimloop [x y (startId) (endId)]", "Loops through object animations.", (p, args) -> {
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			GameObject o = ChunkManager.getChunk(Tile.of(x, y, p.getPlane()).getChunkId()).getSpawnedObject(Tile.of(x, y, p.getPlane()));
			if (o == null) {
				p.getPackets().sendDevConsoleMessage("Could not find object at [x=" + x + ", y=" + y + ", z=" + p.getPlane() + "].");
				return;
			}
			if (!ObjAnimList.inited())
				ObjAnimList.init();
			final int start = args.length > 2 ? Integer.parseInt(args[2]) : 10;
			final int end = args.length > 3 ? Integer.parseInt(args[3]) : 20000;
			p.getTempAttribs().setI("loopAnim", start);
			WorldTasks.scheduleLooping(new Task() {
				int anim = p.getTempAttribs().getI("loopAnim");

				@Override
				public void run() {
					anim = p.getTempAttribs().getI("loopAnim");
					if (anim >= end || p == null || !p.isRunning() || p.hasFinished())
						stop();
					else {
						World.sendObjectAnimation(o, new Animation(anim));
						p.sendMessage("Current animation: " + anim);
					}
					for (int i = anim + 1; i < end; i++) {
						if (ObjAnimList.isUsed(i))
							continue;
						anim = i;
						p.getTempAttribs().setI("loopAnim", anim);
						break;
					}
					return;
				}
			}, 0, 2);
		});

		Commands.add(Rights.ADMIN, "killme", "Kills yourself.", (p, args) -> p.applyHit(new Hit(null, p.getHitpoints(), HitLook.TRUE_DAMAGE)));

		Commands.add(Rights.DEVELOPER, "killallstaff", "Kills all staff members.", (p, args) -> World.allPlayers(other -> {
            if (other.hasRights(Rights.ADMIN))
                other.applyHit(new Hit(null, p.getHitpoints(), HitLook.TRUE_DAMAGE));
        }));

		Commands.add(Rights.DEVELOPER, "hidec [interfaceId componentId hidden]", "show hide comp.", (p, args) -> p.getPackets().setIFHidden(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Boolean.valueOf(args[2])));

		Commands.add(Rights.DEVELOPER, "ifgraphic [interfaceId componentId graphicId]", "interface set graphic.", (p, args) -> p.getPackets().setIFGraphic(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])));

		Commands.add(Rights.ADMIN, "checkclient [player name]", "Verifies the user's client.", (p, args) -> {
			Player target = World.getPlayerByDisplay(args[0]);
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else
				target.queueReflectionAnalysis(new ReflectionAnalysis()
						.addTest(new ReflectionTest("Client", "validates client class", new ReflectionCheck("com.Loader", "MAJOR_BUILD"), check -> check.getResponse().getCode() == ResponseCode.SUCCESS && check.getResponse().getStringData().equals("public static final")))
						.addTest(new ReflectionTest("Loader", "validates launcher class", new ReflectionCheck("com.darkan.Loader", "DOWNLOAD_URL"), check -> check.getResponse().getCode() == ResponseCode.SUCCESS && check.getResponse().getStringData().equals("public static")))
						.addTest(new ReflectionTest("Player rights", "validates player rights client sided", new ReflectionCheck("com.jagex.client", "PLAYER_RIGHTS", true), check -> check.getResponse().getCode() == ResponseCode.SUCCESS && check.getResponse().getData() == target.getRights().getCrown()))
						.addTest(new ReflectionTest("Lobby port method", "validates getPort", new ReflectionCheck("com.Loader", "I", "getPort", new Object[] { Integer.valueOf(1115) }), check -> check.getResponse().getCode() == ResponseCode.NUMBER && check.getResponse().getData() == 43594))
						.addTest(new ReflectionTest("Local checksum method", "validates getLocalChecksum", new ReflectionCheck("com.darkan.Download", "java.lang.String", "getLocalChecksum", new Object[] { }), check -> check.getResponse().getCode() == ResponseCode.STRING && check.getResponse().getStringData().equals("e4d95327297ffca1698dff85eda6622d")))
						.build());
		});

		Commands.add(Rights.DEVELOPER, "getip [player name]", "Verifies the user's client.", (p, args) -> World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
            if (target == null)
                p.sendMessage("Couldn't find player.");
            else {
                p.sendMessage("<col=ff0000>IP addresses for " + Utils.concat(args));
                for (String ip : target.getIpAddresses())
                    p.sendMessage("<col=ff0000>" + ip);
            }
        }));

		Commands.add(Rights.ADMIN, "copy [player name]", "Copies the other player's levels, equipment, and inventory.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null) {
				p.sendMessage("Couldn't find player " + Utils.concat(args) + ".");
				return;
			}
			Item[] equip = target.getEquipment().getItemsCopy();
			for (int i = 0; i < equip.length; i++) {
				if (equip[i] == null)
					continue;
				p.getEquipment().setSlot(i, new Item(equip[i]));
			}
			Item[] inv = target.getInventory().getItems().getItemsCopy();
			for (int i = 0; i < inv.length; i++) {
				if (inv[i] == null)
					continue;

				p.getInventory().getItems().set(i, new Item(inv[i]));
				p.getInventory().refresh(i);
			}
			for (int i = 0; i < p.getSkills().getLevels().length; i++) {
				p.getSkills().set(i, target.getSkills().getLevelForXp(i));
				p.getSkills().setXp(i, Skills.getXPForLevel(target.getSkills().getLevelForXp(i)));
			}
			p.getAppearance().generateAppearanceData();
		});
	}

}
