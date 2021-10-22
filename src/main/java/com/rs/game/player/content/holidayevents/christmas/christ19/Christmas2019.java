package com.rs.game.player.content.holidayevents.christmas.christ19;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.ForceTalk;
import com.rs.game.player.Player;
import com.rs.game.player.content.pet.Pets;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.utils.spawns.NPCSpawn;
import com.rs.utils.spawns.NPCSpawns;

@PluginEventHandler
public class Christmas2019 {
	
	public static final String STAGE_KEY = "christ2021";

	private static boolean ACTIVE = false;

	public enum Imp {
		WINE(9372, 6928, Location.VARROCK_CASTLE, Location.CAMELOT_CASTLE, Location.ARDOUGNE_CASTLE), 
		YULE_LOG(9373, 6929, Location.LUMBRIDGE_COOK, Location.YANILLE_COOK, Location.COOKING_GUILD), 
		TURKEY(9374, 6930, Location.LUMBRIDGE_CHICKEN, Location.FALADOR_CHICKEN, Location.PHASMATYS_CHICKEN), 
		POTATOES(9375, 6931, Location.LUMBRIDGE_POTATO, Location.DRAYNOR_POTATO, Location.ARDOUGNE_POTATO);

		private int npcId;
		private int varBit;
		private Location[] locs;

		private static Map<Integer, Imp> CHUNK_MAP = new HashMap<>();

		static {
			for (Imp i : Imp.values()) {
				for (Location l : i.locs) {
					CHUNK_MAP.put(l.chunkId, i);
				}
			}
		}

		public static Imp forChunk(int chunkId) {
			return CHUNK_MAP.get(chunkId);
		}

		private Imp(int npcId, int varBit, Location... locs) {
			this.npcId = npcId;
			this.varBit = varBit;
			this.locs = locs;
		}

		public Location randomLoc() {
			return locs[Utils.random(locs.length)];
		}
	}

	public enum Location {
		VARROCK_CASTLE(new WorldTile(3202, 3491, 0), "Him seems to be somewhere in Varrock.."), CAMELOT_CASTLE(new WorldTile(2765, 3506, 0), "Him seems to be somewhere near Seer's Village.."), ARDOUGNE_CASTLE(new WorldTile(2571, 3292, 0), "Him seems to be somewhere in Ardougne.."),

		LUMBRIDGE_COOK(new WorldTile(3211, 3211, 0), "Him seems to be somewhere near Lumbridge.."), YANILLE_COOK(new WorldTile(2564, 3099, 0), "Him seems to be somewhere near Yanille.."), COOKING_GUILD(new WorldTile(3140, 3444, 0), "Him seems to be somewhere near Varrock.."),

		LUMBRIDGE_CHICKEN(new WorldTile(3236, 3300, 0), "Him seems to be somewhere near Lumbridge.."), FALADOR_CHICKEN(new WorldTile(3020, 3291, 0), "Him seems to be somewhere near Falador.."), PHASMATYS_CHICKEN(new WorldTile(3620, 3522, 0), "Him seems to be somewhere near Canifis.."),

		LUMBRIDGE_POTATO(new WorldTile(3260, 3307, 0), "Him seems to be somewhere near Lumbridge.."), DRAYNOR_POTATO(new WorldTile(3148, 3283, 0), "Him seems to be somewhere near Draynor.."), ARDOUGNE_POTATO(new WorldTile(2628, 3364, 0), "Him seems to be somewhere near Ardougne..");

		private WorldTile loc;
		private String hint;
		private int chunkId;

		private Location(WorldTile loc, String hint) {
			this.loc = loc;
			this.hint = hint;
			this.chunkId = loc.getChunkId();
		}

		public String getHint() {
			return hint;
		}

		public Imp getImp() {
			return Imp.forChunk(chunkId);
		}
	}

	@ServerStartupEvent
	public static void loadSnowyCupboard() {
		if (!ACTIVE)
			return;
		for (Imp i : Imp.values()) {
			for (Location l : i.locs) {
				NPCSpawns.add(new NPCSpawn(i.npcId, l.loc, "Imp for Christmas event."));
			}
		}

		NPCSpawns.add(new NPCSpawn(8540, new WorldTile(2655, 5678, 0), "Queen of Snow"));
		NPCSpawns.add(new NPCSpawn(8539, new WorldTile(2654, 5679, 0), "Santa"));

		NPCSpawns.add(new NPCSpawn(9386, new WorldTile(2652, 5663, 0), "Partygoer"));
		NPCSpawns.add(new NPCSpawn(9389, new WorldTile(2658, 5663, 0), "Partygoer"));
		NPCSpawns.add(new NPCSpawn(9392, new WorldTile(2655, 5659, 0), "Partygoer"));
		NPCSpawns.add(new NPCSpawn(9386, new WorldTile(2662, 5654, 0), "Partygoer"));
		NPCSpawns.add(new NPCSpawn(9389, new WorldTile(2649, 5653, 0), "Partygoer"));
		NPCSpawns.add(new NPCSpawn(9392, new WorldTile(2655, 5668, 0), "Partygoer"));
	}

	public static EnterChunkHandler handleChunkEvents = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			if (!ACTIVE)
				return;
			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				if (p.getChrist19Loc() == null) {
					p.getVars().setVarBit(6928, 1);
					p.getVars().setVarBit(6929, 1);
					p.getVars().setVarBit(6930, 1);
					p.getVars().setVarBit(6931, 1);
					return;
				}
				if (p.getPetManager().getNpcId() == Pets.SNOW_IMP.getBabyNpcId()) {
					Location loc = p.getChrist19Loc();
					if (e.getChunkId() == loc.chunkId) {
						if (p.getPet() != null) {
							p.getPet().setNextForceTalk(new ForceTalk("Der he is!"));
						}
						p.getVars().setVarBit(loc.getImp().varBit, 0);
					} else {
						if (p.getPet() != null) {
							int prevDist = p.getTempI("christ19LocDist");
							int currDist = (int) Utils.getDistance(p, loc.loc);
							if (prevDist != 0) {
								if (currDist > prevDist)
									p.getPet().setNextForceTalk(new ForceTalk("Yer headin the wrong way, guv!"));
								else
									p.getPet().setNextForceTalk(new ForceTalk("Yer gettin closer, guv!"));
							}
							p.setTempI("christ19LocDist", currDist);
						}
						p.getVars().setVarBit(6928, 1);
						p.getVars().setVarBit(6929, 1);
						p.getVars().setVarBit(6930, 1);
						p.getVars().setVarBit(6931, 1);
					}
				}
			}
		}
	};

	public static LoginHandler login = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if (!ACTIVE)
				return;
			e.getPlayer().getVars().setVarBit(6928, 1);
			e.getPlayer().getVars().setVarBit(6929, 1);
			e.getPlayer().getVars().setVarBit(6930, 1);
			e.getPlayer().getVars().setVarBit(6931, 1);
			if (e.getPlayer().getI(Christmas2019.STAGE_KEY) == 10)
				e.getPlayer().getVars().setVarBit(6934, 1);
		}
	};

	public static ItemClickHandler handle = new ItemClickHandler(new Object[] { 14599 }, new String[] { "Summon Imp" }) {
		@Override
		public void handle(ItemClickEvent e) {
			e.getPlayer().getPetManager().spawnPet(50001, false);
		}
	};

	public static ItemEquipHandler handleUnequipIceAmulet = new ItemEquipHandler(14599) {
		@Override
		public void handle(ItemEquipEvent e) {
			if (e.dequip() && e.getPlayer().getPetManager().getNpcId() == Pets.SNOW_IMP.getBabyNpcId()) {
				e.getPlayer().getPet().pickup();
			}
		}
	};
}
