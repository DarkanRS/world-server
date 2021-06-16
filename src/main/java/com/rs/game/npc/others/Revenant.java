package com.rs.game.npc.others;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class Revenant extends NPC {

	public Revenant(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setForceAggroDistance(4);
	}

	@Override
	public void spawn() {
		super.spawn();
		setNextAnimation(new Animation(getSpawnAnimation()));
	}
	
	@Override
	public void drop(Player killer, boolean verifyCombatDefs) {
		try {
			if (!getDefinitions().getName().equals("null"))			
				killer.sendNPCKill(getDefinitions().getName());
			List<Item> drops = genDrop(killer, getDefinitions().getName(), getDefinitions().combatLevel);
			for (Item item : drops) {
				sendDrop(killer, item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	public int getSpawnAnimation() {
		switch (getId()) {
		case 13465:
			return 7410;
		case 13466:
		case 13467:
		case 13468:
		case 13469:
			return 7447;
		case 13470:
		case 13471:
			return 7485;
		case 13472:
			return -1;
		case 13473:
			return 7426;
		case 13474:
			return 7403;
		case 13475:
			return 7457;
		case 13476:
			return 7464;
		case 13477:
			return 7478;
		case 13478:
			return 7416;
		case 13479:
			return 7471;
		case 13480:
			return 7440;
		case 13481:
		default:
			return -1;
		}
	}
	
	public static List<Item> genDrop(Player killer, String name, int combatLevel) {
		List<Item> drops = new ArrayList<>();
		
		double g = Utils.clampD(Math.sqrt(combatLevel), 1.0, 12.0);
		double r = 60000.0 / g;
		
		Utils.add(drops, DropTable.calculateDrops(killer, new DropSet(
				//1/R chance each to obtain an Ancient, Seren, Armadyl, Zamorak, Saradomin or Bandos statuette, or a random brawling glove. The rate for brawling gloves is 2/15 for Smithing and Hunter gloves, and 1/15 for all others
				new DropTable(1, r, new Drop(14876)),
				new DropTable(1, r, new Drop(14877)),
				new DropTable(1, r, new Drop(14878)),
				new DropTable(1, r, new Drop(14879)),
				new DropTable(1, r, new Drop(14880)),
				new DropTable(1, r, new Drop(14881)),
				new DropTable(1, r, new Drop(13845), new Drop(13846), new Drop(13847), new Drop(13848), new Drop(13849), new Drop(13850), new Drop(13851), new Drop(13852), new Drop(13853), new Drop(13854), new Drop(13855), new Drop(13856), new Drop(13857), new Drop(13855), new Drop(13853)),
			
				//2/R chance each to obtain a Ruby chalice, Guthixian brazier, Armadyl totem, Zamorak medallion, Saradomin carving, Bandos scrimshaw or a corrupt dragon item
				new DropTable(2, r, new Drop(14882)),
				new DropTable(2, r, new Drop(14883)),
				new DropTable(2, r, new Drop(14884)),
				new DropTable(2, r, new Drop(14885)),
				new DropTable(2, r, new Drop(14886)),
				new DropTable(2, r, new Drop(14887)),
				new DropTable(2, r, new Drop(13958), new Drop(13961), new Drop(13964), new Drop(13967), new Drop(13970), new Drop(13973), new Drop(13976), new Drop(13979), new Drop(13982), new Drop(13985), new Drop(13988)),
				
				//3/R chance each to obtain a Saradomin amphora, Ancient psaltery bridge, Bronzed dragon claw, Third age carafe or broken statue headdress
				new DropTable(3, r, new Drop(14888)),
				new DropTable(3, r, new Drop(14889)),
				new DropTable(3, r, new Drop(14890)),
				new DropTable(3, r, new Drop(14891)),
				new DropTable(3, r, new Drop(14892)),
				
				//10/R chance each to obtain a piece of Ancient Warriors' equipment or its corrupt version
				new DropTable(10, r, 
						new Drop(13858),
						new Drop(13861),
						new Drop(13864),
						new Drop(13867),
						new Drop(13870),
						new Drop(13873),
						new Drop(13876),
						new Drop(13879, 15, 50),
						new Drop(13883, 15, 50),
						new Drop(13884),
						new Drop(13887),
						new Drop(13890),
						new Drop(13893),
						new Drop(13896),
						new Drop(13899),
						new Drop(13902),
						new Drop(13905)
						),
				new DropTable(10, r, 
						new Drop(13908),
						new Drop(13911),
						new Drop(13914),
						new Drop(13917),
						new Drop(13920),
						new Drop(13923),
						new Drop(13926),
						new Drop(13929),
						new Drop(13932),
						new Drop(13935),
						new Drop(13938),
						new Drop(13941),
						new Drop(13944),
						new Drop(13947),
						new Drop(13950),
						new Drop(13953, 15, 50),
						new Drop(13957, 15, 50)
						)
				)));

		//If no unique is dropped, then a final roll for coins appear, the cap roughly equal to 50 times G.
		if (drops.isEmpty())
			drops.add(new Item(995, (int) (50.0 * g)));
		return drops;
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(13465, 13466, 13467, 13468, 13469, 13470, 13471, 13472, 13473, 13474, 13475, 13476, 13477, 13478, 13479, 13480, 13481) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Revenant(npcId, tile, false);
		}
	};
}
