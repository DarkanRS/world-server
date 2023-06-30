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
package com.rs.game.model.entity.player;

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.LoyaltyRewardDefinitions.Reward;
import com.rs.cache.loaders.ObjectType;
import com.rs.db.WorldDB;
import com.rs.engine.book.Book;
import com.rs.engine.cutscene.Cutscene;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.dialogue.statements.SimpleStatement;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.miniquest.MiniquestManager;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestManager;
import com.rs.game.World;
import com.rs.game.World.DropMethod;
import com.rs.game.content.*;
import com.rs.game.content.ItemConstants.ItemDegrade;
import com.rs.game.content.Toolbelt.Tools;
import com.rs.game.content.achievements.AchievementInterface;
import com.rs.game.content.bosses.godwars.GodwarsController;
import com.rs.game.content.bosses.godwars.zaros.Nex;
import com.rs.game.content.clans.ClansManager;
import com.rs.game.content.combat.CombatDefinitions;
import com.rs.game.content.death.DeathOfficeController;
import com.rs.game.content.death.GraveStone;
import com.rs.game.content.holidayevents.christmas.christ19.Christmas2019.Location;
import com.rs.game.content.interfacehandlers.TransformationRing;
import com.rs.game.content.minigames.domtower.DominionTower;
import com.rs.game.content.minigames.duel.DuelRules;
import com.rs.game.content.minigames.herblorehabitat.HabitatFeature;
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager;
import com.rs.game.content.minigames.wguild.WarriorsGuild;
import com.rs.game.content.pets.Pet;
import com.rs.game.content.pets.PetManager;
import com.rs.game.content.skills.construction.House;
import com.rs.game.content.skills.cooking.Brewery;
import com.rs.game.content.skills.cooking.Foods;
import com.rs.game.content.skills.dungeoneering.DungManager;
import com.rs.game.content.skills.dungeoneering.DungeonRewards.HerbicideSetting;
import com.rs.game.content.skills.farming.FarmPatch;
import com.rs.game.content.skills.farming.PatchLocation;
import com.rs.game.content.skills.farming.ProduceType;
import com.rs.game.content.skills.farming.StorableItem;
import com.rs.game.content.skills.magic.LodestoneAction.Lodestone;
import com.rs.game.content.skills.prayer.Prayer;
import com.rs.game.content.skills.prayer.PrayerBooks;
import com.rs.game.content.skills.runecrafting.RunecraftingAltar.WickedHoodRune;
import com.rs.game.content.skills.slayer.BossTask;
import com.rs.game.content.skills.slayer.SlayerTaskManager;
import com.rs.game.content.skills.slayer.TaskMonster;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.content.transportation.FadingScreen;
import com.rs.game.content.tutorialisland.GamemodeSelection;
import com.rs.game.content.tutorialisland.TutorialIslandController;
import com.rs.game.content.world.Musician;
import com.rs.game.ge.GE;
import com.rs.game.ge.Offer;
import com.rs.game.map.ChunkManager;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.*;
import com.rs.game.model.entity.player.managers.*;
import com.rs.game.model.entity.player.managers.InterfaceManager.ScreenMode;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.entity.player.social.FCManager;
import com.rs.game.model.item.ItemsContainer;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.*;
import com.rs.lib.model.Account;
import com.rs.lib.model.Social;
import com.rs.lib.model.clan.Clan;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.ServerPacket;
import com.rs.lib.net.Session;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.encoders.MinimapFlag;
import com.rs.lib.net.packets.encoders.ReflectionCheckRequest;
import com.rs.lib.net.packets.encoders.Sound;
import com.rs.lib.net.packets.encoders.Sound.SoundType;
import com.rs.lib.net.packets.encoders.social.MessageGame.MessageType;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.lib.util.Utils;
import com.rs.lib.web.dto.FCData;
import com.rs.net.LobbyCommunicator;
import com.rs.net.decoders.handlers.PacketHandlers;
import com.rs.net.encoders.WorldEncoder;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.*;
import com.rs.utils.AccountLimiter;
import com.rs.utils.MachineInformation;
import com.rs.utils.Ticks;
import com.rs.utils.record.Recorder;
import com.rs.utils.reflect.ReflectionAnalysis;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Player extends Entity {

	private String username;
	private Date dateJoined;

	private Map<String, Object> dailyAttributes;

	public transient int chatType;

	private long timePlayed = 0;
	private long timeLoggedOut;

	private transient HashMap<Integer, ReflectionAnalysis> reflectionAnalyses = new HashMap<>();

	private long docileTimer;

	private Brewery keldagrimBrewery;
	private Brewery phasmatysBrewery;

	private int placedCannon;

	private Set<Integer> diangoReclaim = new HashSet<>();

	private boolean slayerHelmCreation;
	private boolean quickBlows;
	private boolean broadFletching;
	private boolean craftROS; //ring of slay
	private boolean iceStrykeNoCape;
	private boolean aquanitesUnlocked;

	private DungManager dungManager;

	public transient long tolerance = 0;
	public transient long idleTime = 0;
	public transient long dyingTime = 0;
	public transient long spellDelay = 0;
	public transient boolean disconnected = false;
	private transient int pvpCombatLevelThreshhold = -1;
	private transient String[] playerOptions = new String[10];
	private transient Set<Sound> sounds = new HashSet<Sound>();

	private Instance instancedArea;

	private int hw07Stage;
	public transient Runnable onPacketCutsceneFinish;

	public void refreshChargeTimer() {
		addEffect(Effect.CHARGED, 600);
	}

	public void refreshMiasmicTimer(int ticks) {
		if (hasEffect(Effect.MIASMIC_BLOCK))
			return;
		sendMessage("You feel slowed down.");
		addEffect(Effect.MIASMIC_BLOCK, ticks+15);
		addEffect(Effect.MIASMIC_SLOWDOWN, ticks);
	}

	public void addSpellDelay(int ticks) {
		spellDelay = ticks + World.getServerTicks();
	}

	public boolean canCastSpell() {
		return spellDelay < World.getServerTicks();
	}

	public void refreshIdleTime() {
		idleTime = 420000L + System.currentTimeMillis();
	}

	public boolean isIdle() {
		return idleTime < System.currentTimeMillis();
	}

	public void refreshDyingTime() {
		dyingTime = 10000L + System.currentTimeMillis();
	}

	public boolean isDying() {
		return dyingTime > System.currentTimeMillis();
	}

	// transient stuff
	private transient Session session;
	private transient long clientLoadedMapRegion;
	private transient Set<Integer> mapChunksNeedInit;
	private transient ScreenMode screenMode;
	private transient int screenWidth;
	private transient int screenHeight;
	private transient Conversation conversation;
	private transient InterfaceManager interfaceManager;
	private transient HintIconsManager hintIconsManager;
	private transient CutsceneManager cutsceneManager;
	private transient Trade trade;
	private transient DuelRules lastDuelRules;
	private transient Pet pet;
	private VarManager varManager;

	private int lsp;

	private boolean chosenAccountType;
	private boolean ironMan;

	private int jadinkoFavor;

	private long lastLoggedIn = 0;

	public int ringOfForgingCharges = 140;
	public int bindingNecklaceCharges = 15;

	private Map<String, Object> savingAttributes;

	private Map<String, Integer> npcKills;
	private Map<String, Integer> variousCounter;

	private Set<Reward> unlockedLoyaltyRewards;
	private Set<Reward> favoritedLoyaltyRewards;

	private Map<Tools, Integer> toolbelt;

	private transient ArrayList<String> attackedBy = new ArrayList<>();
	private transient Recorder recorder;

	public int[] artisanOres = new int[5];
	public double artisanXp = 0.0;
	public int artisanRep = 0;

	public int loyaltyPoints = 0;

	public ArrayList<String> getAttackedByList() {
		return attackedBy;
	}

	public boolean attackedBy(String name) {
		for (String other : attackedBy)
			if (other.equals(name))
				return true;
		return false;
	}

	public void addToAttackedBy(String name) {
		attackedBy.add(name);
	}

	// used for update
	private transient LocalPlayerUpdate localPlayerUpdate;
	private transient LocalNPCUpdate localNPCUpdate;

	private MoveType tempMoveType;
	private boolean updateMovementType;

	// player stages
	private transient boolean started;
	private transient boolean running;

	private transient boolean resting;
	private transient boolean canPvp;
	private transient boolean cantTrade;
	private transient long foodDelay;
	private transient long potionDelay;
	private transient long boneDelay;
	private transient Runnable closeInterfacesEvent;
	private transient Runnable closeChatboxInterfaceEvent;
	private transient Runnable finishConversationEvent;
	private transient boolean disableEquip;
	private transient MachineInformation machineInformation;
	private transient boolean castedVeng;
	private transient boolean castedMagicImbue;
	private transient boolean invulnerable;
	private transient double hpBoostMultiplier;
	private transient boolean largeSceneView;
	private transient String lastNpcInteractedName = null;
	private transient Account account;

	private transient boolean tileMan;
	private transient int tilesAvailable;
	private transient Set<Integer> tilesUnlocked;

	private HabitatFeature habitatFeature;

	private transient int uuid;
	private String lastIP;
	private ArrayList<String> ipAddresses;
	@SuppressWarnings("unused")
	private long creationDate;
	private Appearance appearence;
	private Inventory inventory;
	private Equipment equipment;
	private Skills skills;
	private CombatDefinitions combatDefinitions;
	private PrayerManager prayer;
	private Bank bank;
	private int starter;
	private ControllerManager controllerManager;
	private MusicsManager musicsManager;
	private EmotesManager emotesManager;
	private DominionTower dominionTower;
	private Familiar summFamiliar;
	private AuraManager auraManager;
	private PetManager petManager;
	private BossTask bossTask;
	private QuestManager questManager;
	private MiniquestManager miniquestManager;
	private TreasureTrailsManager treasureTrailsManager;
	private Map<Integer, Offer> geOffers = new HashMap<>();

	public int reaperPoints;

	private boolean wickedHoodTalismans[];
	private boolean hasUsedOmniTalisman;
	private boolean hasUsedElementalTalisman;

	public Set<HerbicideSetting> herbicideSettings = new HashSet<>();

	public ItemsContainer<Item> partyDeposit;
	private Map<StorableItem, Item> leprechaunStorage;

	private int graveStone;

	private boolean runBlocked;

	private ItemsContainer<Item> trawlerRewards;

	// ectofuntus
	public int boneType;
	public boolean bonesGrinded;
	public int unclaimedEctoTokens;

	public boolean hasScrollOfLife;
	public boolean hasScrollOfCleansing;
	public boolean hasScrollOfEfficiency;
	public boolean hasRigour;
	public boolean hasAugury;
	public boolean hasRenewalPrayer;

	private double runEnergy;
	private boolean allowChatEffects;
	private boolean mouseButtons;
	private int privateChatSetup;
	private int friendChatSetup;
	public int totalDonated;
	private int skullId;
	private boolean forceNextMapLoadRefresh;
	private double runeSpanPoints;
	private transient double bonusXpRate = 0.0;
	private int crystalSeedRepairs;
	private int tinySeedRepairs;

	public Tile lastEssTele;

	private boolean[] prayerBook;

	private SlayerTaskManager slayer = new SlayerTaskManager();
	private TaskMonster[] taskBlocks;
	public int slayerPoints = 0;
	public int consecutiveTasks = 0;

	private int[] warriorPoints = new int[6];

	private transient boolean cantWalk;

	private Map<PatchLocation, FarmPatch> patches;

	public boolean isCantWalk() {
		return cantWalk;
	}

	public void setCantWalk(boolean cantWalk) {
		this.cantWalk = cantWalk;
	}

	public SlayerTaskManager getSlayer() {
		return slayer;
	}

	public void setSlayer(SlayerTaskManager slayer) {
		this.slayer = slayer;
	}

	public TaskMonster[] getBlockedTasks() {
		if (taskBlocks == null)
			taskBlocks = new TaskMonster[6];
		return taskBlocks;
	}

	public boolean hasSlayerTask() {
		return slayer.getTask() != null;
	}

	public void blockTask(TaskMonster task) {
		if (getBlockedTaskNumber() == 6 || blockedTaskContains(task))
			return;
		for (int i = 0; i < 6; i++)
			if (taskBlocks[i] == null) {
				taskBlocks[i] = task;
				return;
			}
	}

	public boolean blockedTaskContains(TaskMonster task) {
		if (taskBlocks == null)
			taskBlocks = new TaskMonster[6];
		for (int i = 0; i < 6; i++)
			if (taskBlocks[i] != null && taskBlocks[i] == task)
				return true;
		return false;
	}

	public int getBlockedTaskNumber() {
		if (taskBlocks == null)
			taskBlocks = new TaskMonster[6];
		int num = 0;
		for (int i = 0; i < 6; i++)
			if (taskBlocks[i] != null)
				num++;
		return num;
	}

	public void unblockTask(int slot) {
		if (taskBlocks == null)
			taskBlocks = new TaskMonster[6];
		taskBlocks[slot] = null;
	}

	public void setSlayerPoints(int points) {
		slayerPoints = points;
	}

	public void addSlayerPoints(int points) {
		slayerPoints += points;
	}

	public void removeSlayerPoints(int points) {
		slayerPoints -= points;
	}

	public int getSlayerPoints() {
		return slayerPoints;
	}

	public int getTinyCrystalSeedRepairs() {
		return tinySeedRepairs;
	}

	public void incrementTinyCrystalSeedRepair() {
		if (tinySeedRepairs <= 0)
			tinySeedRepairs = 1;
		else
			tinySeedRepairs++;
	}

	public int getCrystalSeedRepairs() {
		return crystalSeedRepairs;
	}

	public void incrementCrystalSeedRepair() {
		if (crystalSeedRepairs <= 0)
			crystalSeedRepairs = 1;
		else
			crystalSeedRepairs++;
	}

	private int lastDate = 0;

	private int[] pouches;
	private boolean[] pouchesType;
	private boolean filterGame;
	private boolean xpLocked;
	private boolean yellOff;
	// game bar status
	private int publicStatus;
	private int clanStatus;
	private int tradeStatus;
	private int assistStatus;

	// Recovery ques. & ans.
	private String recovQuestion;
	private String recovAnswer;

	// Used for storing recent ips
	private Set<String> ipList = new HashSet<>();
	private Map<Integer, MachineInformation> machineMap = new HashMap<>();

	// honor
	private int killCount, deathCount;
	// barrows
	private boolean[] killedBarrowBrothers;
	private int hiddenBrother;
	private int barrowsKillCount;
	private int pestPoints;

	// skill capes customizing
	private int[] maxedCapeCustomized;
	private int[] completionistCapeCustomized;

	private int summoningLeftClickOption;

	// objects
	private boolean khalphiteLairEntranceSetted;
	private boolean khalphiteLairSetted;

	// voting
	private int votes;

	private String yellColor = "ff0000";
	private String yellTitle = "";

	private String title = null;
	private String titleColor = null;
	private String titleShading = null;
	private boolean titleAfter = false;

	private House house;

	// creates Player and saved classes
	public Player(Account account) {
		super(Tile.of(Settings.getConfig().getPlayerStartTile()));
		this.account = account;
		username = account.getUsername();
		setHitpoints(100);
		dateJoined = Date.from(Clock.systemUTC().instant());
		house = new House();
		chosenAccountType = false;
		appearence = new Appearance();
		inventory = new Inventory();
		equipment = new Equipment();
		skills = new Skills();
		combatDefinitions = new CombatDefinitions();
		prayer = new PrayerManager();
		bank = new Bank();
		controllerManager = new ControllerManager();
		musicsManager = new MusicsManager();
		emotesManager = new EmotesManager();
		notes = new Notes();
		dominionTower = new DominionTower();
		auraManager = new AuraManager();
		petManager = new PetManager();
		treasureTrailsManager = new TreasureTrailsManager();
		questManager = new QuestManager();
		miniquestManager = new MiniquestManager();
		dungManager = new DungManager(this);
		pouchesType = new boolean[4];
		runEnergy = 100;
		allowChatEffects = true;
		mouseButtons = true;
		pouches = new int[4];
		taskBlocks = new TaskMonster[6];
		for (int i = 0; i < 6; i++)
			taskBlocks[i] = null;
		slayer = new SlayerTaskManager();
		setWickedHoodTalismans(new boolean[WickedHoodRune.values().length]);
		resetBarrows();
		SkillCapeCustomizer.resetSkillCapes(this);
		prayerBook = new boolean[PrayerBooks.BOOKS.length];
		herbicideSettings = new HashSet<>();
		ipList = new HashSet<>();
		machineMap = new HashMap<>();
		creationDate = System.currentTimeMillis();
		resetLodestones();
	}

	public void init(Session session, Account account, int screenMode, int screenWidth, int screenHeight, MachineInformation machineInformation) {
		if (mapChunksNeedInit == null)
			mapChunksNeedInit = IntSets.synchronize(new IntOpenHashSet());
		if (getTile() == null)
			setTile(Tile.of(Settings.getConfig().getPlayerStartTile()));
		this.session = session;
		this.account = account;
		uuid = getUsername().hashCode();
		this.screenMode = ScreenMode.forId(screenMode);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.machineInformation = machineInformation;
		refreshIdleTime();
		addSpellDelay(0);
		if (house == null)
			house = new House();
		if (lodestones == null)
			resetLodestones();
		if (dungManager == null)
			dungManager = new DungManager(this);

		if (herbicideSettings == null)
			herbicideSettings = new HashSet<>();
		if (notes == null)
			notes = new Notes();
		recorder = new Recorder(this);
		reflectionAnalyses = new HashMap<>();
		attackedBy = new ArrayList<>();
		interfaceManager = new InterfaceManager(this);
		hintIconsManager = new HintIconsManager(this);
		localPlayerUpdate = new LocalPlayerUpdate(this);
		localNPCUpdate = new LocalNPCUpdate(this);
		if (varManager == null)
			varManager = new VarManager();
		varManager.setSession(session);
		sounds = new HashSet<>();
		cutsceneManager = new CutsceneManager(this);
		trade = new Trade(this);
		// loads player on saved instances
		appearence.setPlayer(this);
		treasureTrailsManager.setPlayer(this);
		questManager.setPlayer(this);
		if (miniquestManager == null)
			miniquestManager = new MiniquestManager();
		miniquestManager.setPlayer(this);
		inventory.setPlayer(this);
		equipment.setPlayer(this);
		skills.setPlayer(this);
		notes.setPlayer(this);
		house.setPlayer(this);
		combatDefinitions.setPlayer(this);
		prayer.setPlayer(this);
		bank.setPlayer(this);
		controllerManager.setPlayer(this);
		dungManager.setPlayer(this);
		musicsManager.setPlayer(this);
		emotesManager.setPlayer(this);
		dominionTower.setPlayer(this);
		auraManager.setPlayer(this);
		petManager.setPlayer(this);
		setFaceAngle(Utils.getAngleTo(0, -1));
		tempMoveType = null;
		initEntity();
		if (pouchesType == null)
			pouchesType = new boolean[4];
		World.addPlayer(this);
		ChunkManager.updateChunks(this);
		Logger.info(Player.class, "init", "Initiated player: " + account.getUsername());

		// Do not delete >.>, useful for security purpose. this wont waste that
		// much space..
		if (prayerBook == null)
			prayerBook = new boolean[PrayerBooks.BOOKS.length];
		if (ipList == null)
			ipList = new HashSet<>();
		if (machineMap == null)
			machineMap = new HashMap<>();
		updateMachineIPs();
	}

	public void updateMachineIPs() {
		if (ipList.size() > 50)
			ipList.remove(new ArrayList<>(ipList).get(Utils.random(ipList.size())));
		ipList.add(getLastIP());
		if (machineMap.size() > 15)
			machineMap.remove(new ArrayList<>(machineMap.keySet()).get(Utils.random(machineMap.keySet().size())));
		if (machineInformation != null)
			machineMap.put(machineInformation.hashCode(), machineInformation);
		return;
	}

	public boolean[] getPrayerBook() {
		return prayerBook;
	}

	public TreasureTrailsManager getTreasureTrailsManager() {
		return treasureTrailsManager;
	}

	public void setWildernessSkull() {
		addEffect(Effect.SKULL, Ticks.fromMinutes(30));
		skullId = 0;
		appearence.generateAppearanceData();
	}

	public void setFightPitsSkull() {
		addEffect(Effect.SKULL, Integer.MAX_VALUE);
		skullId = 1;
		appearence.generateAppearanceData();
	}

	public void setSkullInfiniteDelay(int skullId) {
		addEffect(Effect.SKULL, Integer.MAX_VALUE);
		this.skullId = skullId;
		appearence.generateAppearanceData();
	}

	public void removeSkull() {
		removeEffect(Effect.SKULL);
		appearence.generateAppearanceData();
	}

	public boolean hasSkull() {
		return hasEffect(Effect.SKULL);
	}

	public void unlockLoyaltyReward(Reward reward) {
		if (unlockedLoyaltyRewards == null)
			unlockedLoyaltyRewards = new HashSet<>();
		unlockedLoyaltyRewards.add(reward);
	}

	public boolean unlockedLoyaltyReward(Reward reward) {
		if (unlockedLoyaltyRewards == null)
			return false;
		return unlockedLoyaltyRewards.contains(reward);
	}

	public Set<Reward> getUnlockedLoyaltyRewards() {
		return unlockedLoyaltyRewards;
	}

	public void favoriteLoyaltyReward(Reward reward) {
		if (favoritedLoyaltyRewards == null)
			favoritedLoyaltyRewards = new HashSet<>();
		favoritedLoyaltyRewards.add(reward);
	}

	public void unfavoriteLoyaltyReward(Reward reward) {
		if (favoritedLoyaltyRewards == null)
			favoritedLoyaltyRewards = new HashSet<>();
		favoritedLoyaltyRewards.remove(reward);
	}

	public boolean favoritedLoyaltyReward(Reward reward) {
		if (favoritedLoyaltyRewards == null)
			return false;
		return favoritedLoyaltyRewards.contains(reward);
	}

	public Set<Reward> getFavoritedLoyaltyRewards() {
		return favoritedLoyaltyRewards;
	}

	public void removeDungItems() {
		if (hasFamiliar())
			if (getFamiliar() != null)
				if (getFamiliar().getInventory() != null)
					for (Item item : getFamiliar().getInventory().array())
						if (item != null)
							if (ItemConstants.isDungItem(item.getId()))
								getFamiliar().getInventory().remove(item);
		for (Item item : getInventory().getItems().array())
			if (item != null)
				if (ItemConstants.isDungItem(item.getId()))
					getInventory().deleteItem(item);
		for (Item item : getBank().getContainerCopy())
			if (item != null)
				if (ItemConstants.isDungItem(item.getId()))
					getBank().getItem(item.getId()).setId(995);
		for (Item item : getEquipment().getItemsCopy())
			if (item != null)
				if (ItemConstants.isDungItem(item.getId()))
					getEquipment().deleteItem(item.getId(), 2147000000);
		getAppearance().generateAppearanceData();
	}

	public void removeHouseOnlyItems() {
		if (hasFamiliar())
			if (getFamiliar() != null)
				if (getFamiliar().getInventory() != null)
					for (Item item : getFamiliar().getInventory().array())
						if (item != null)
							if (ItemConstants.isHouseOnlyItem(item.getId()))
								getFamiliar().getInventory().remove(item);
		for (Item item : getInventory().getItems().array())
			if (item != null)
				if (ItemConstants.isHouseOnlyItem(item.getId()))
					getInventory().deleteItem(item);
		for (Item item : getBank().getContainerCopy())
			if (item != null)
				if (ItemConstants.isHouseOnlyItem(item.getId()))
					getBank().getItem(item.getId()).setId(995);
		for (Item item : getEquipment().getItemsCopy())
			if (item != null)
				if (ItemConstants.isHouseOnlyItem(item.getId()))
					getEquipment().deleteItem(item.getId(), 2147000000);
		getAppearance().generateAppearanceData();
	}

	public void initNewChunks() {
		for (int chunkId : getMapChunksNeedInit()) {
			ChunkManager.getChunk(chunkId).init(this);
		}
	}

	// now that we inited we can start showing game
	public void start() {
		loadMapRegions();
		getMapChunksNeedInit().addAll(getMapChunkIds());
		started = true;
		Logger.info(Player.class, "start", "Started player: " + account.getUsername());
		run();
		if (isDead())
			sendDeath(null);
	}

	public void stopAll() {
		stopAll(true);
	}

	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
	}

	public void stopAll(boolean stopWalk, boolean stopInterface) {
		stopAll(stopWalk, stopInterface, true);
	}

	// as walk done clientsided
	public void stopAll(boolean stopWalk, boolean stopInterfaces, boolean stopActions) {
		TransformationRing.triggerDeactivation(this);
		setRouteEvent(null);
		if (stopInterfaces)
			closeInterfaces();
		if (stopWalk)
			resetWalkSteps();
		if (stopActions) {
			getActionManager().forceStop();
			getInteractionManager().forceStop();
		}
		combatDefinitions.resetSpells(false);
	}

	@Override
	public void reset(boolean attributes) {
		super.reset(attributes);
		refreshHitPoints();
		hintIconsManager.removeAll();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(true);
		resting = false;
		foodDelay = 0;
		potionDelay = 0;
		castedVeng = false;
		castedMagicImbue = false;
		setRunEnergy(100);
		appearence.generateAppearanceData();
	}

	@Override
	public void reset() {
		reset(true);
	}

	public void closeInterfaces() {
		if (interfaceManager.containsScreenInter())
			interfaceManager.removeCentralInterface();
		if (interfaceManager.containsInventoryInter())
			interfaceManager.removeInventoryInterface();
		endConversation();
		getSession().writeToQueue(ServerPacket.TRIGGER_ONDIALOGABORT);
		if (closeInterfacesEvent != null) {
			Runnable event = closeInterfacesEvent;
			closeInterfacesEvent = null;
			event.run();
		}
		if (closeChatboxInterfaceEvent != null) {
			Runnable event = closeChatboxInterfaceEvent;
			closeChatboxInterfaceEvent = null;
			event.run();
		}
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = World.getServerTicks() + 10;
	}

	@Override
	public void loadMapRegions() {
		if (started)
			docileTimer = System.currentTimeMillis();
		else
			docileTimer = System.currentTimeMillis()-(lastLoggedIn-docileTimer);
		boolean wasAtDynamicRegion = isHasNearbyInstancedChunks();
		super.loadMapRegions();
		setClientHasntLoadedMapRegion();
		if (isHasNearbyInstancedChunks()) {
			getPackets().sendDynamicMapRegion(!started);
			if (!wasAtDynamicRegion)
				localNPCUpdate.reset();
		} else {
			getPackets().sendMapRegion(!started);
			if (wasAtDynamicRegion)
				localNPCUpdate.reset();
			setInstancedArea(null);
		}
		forceNextMapLoadRefresh = false;
	}

	public boolean isDocile() {
		return (System.currentTimeMillis() - docileTimer) >= 600000L;
	}
	@Override
	public void processEntity() {
		try {
			if (getSession().isClosed())
				finish(0);
			processPackets();
			cutsceneManager.process();
			super.processEntity();
			if (hasStarted() && isIdle() && !hasRights(Rights.ADMIN) && !getNSV().getB("idleLogImmune")) {
				if (getInteractionManager().getInteraction() instanceof PlayerCombatInteraction combat) {
					if (!(combat.getAction().getTarget() instanceof Player))
						idleLog();
				} else
					logout(true);
			}
			if (disconnected && !finishing)
				finish(0);

			timePlayed++;
			timeLoggedOut = System.currentTimeMillis();

			if (getTickCounter() % FarmPatch.FARMING_TICK == 0)
				tickFarming();

			if (getTickCounter() % FarmPatch.FARMING_TICK == 0) {
				getKeldagrimBrewery().process();
				getPhasmatysBrewery().process();
			}

			processTimePlayedTasks();
			processTimedRestorations();
			processMusic();
			processItemDegrades();
			auraManager.process();
			prayer.processPrayer();
			controllerManager.process();
		} catch (Throwable e) {
			Logger.handle(Player.class, "processEntity:Player", e);
		}
	}

	private void processTimePlayedTasks() {
		if (timePlayed % 500 == 0) {
			if (getDailyI("loyaltyTicks") < 12) {
				loyaltyPoints += 175;
				incDailyI("loyaltyTicks");
			} else if (!getDailyB("loyaltyNotifiedCap")) {
				sendMessage("<col=FF0000>You've reached your loyalty point cap for the day. You now have " + Utils.formatNumber(loyaltyPoints) + ".");
				setDailyB("loyaltyNotifiedCap", true);
			}
		}
	}

	private void processTimedRestorations() {
		if (getNextRunDirection() == null) {
			double energy = (8.0 + Math.floor(getSkills().getLevel(Constants.AGILITY) / 6.0)) / 100.0;
			if (isResting()) {
				energy = 1.68;
				if (Musician.isNearby(this)) //TODO optimize this with its own resting variable
					energy = 2.28;
			}
			restoreRunEnergy(energy);
		}
		if (!isDead()) {
			if (getTickCounter() % 50 == 0)
				getCombatDefinitions().restoreSpecialAttack();

			//Restore skilling stats
			if (getTickCounter() % 100 == 0) {
				final int amount = (getPrayer().active(Prayer.RAPID_RESTORE) ? 2 : 1) + (isResting() ? 1 : 0);
				Arrays.stream(Skills.SKILLING).forEach(skill -> restoreTick(skill, amount));
			}

			//Restore combat stats
			if (getTickCounter() % (getPrayer().active(Prayer.BERSERKER) ? 115 : 100) == 0) {
				final int amount = (getPrayer().active(Prayer.RAPID_RESTORE) ? 2 : 1) + (isResting() ? 1 : 0);
				Arrays.stream(Skills.COMBAT).forEach(skill -> restoreTick(skill, amount));
			}
		}
	}

	private void processItemDegrades() {
		if (inCombat() || isAttacking()) {
			for (int i = 0; i < Equipment.SIZE; i++) {
				Item item = getEquipment().getItem(i);
				if (item == null)
					continue;
				for (ItemDegrade d : ItemDegrade.values())
					if ((d.getItemId() == item.getId() || d.getDegradedId() == item.getId()) && item.getMetaData() == null) {
						getEquipment().setSlot(i, new Item(d.getDegradedId() != -1 ? d.getDegradedId() : d.getItemId(), item.getAmount()).addMetaData("combatCharges", d.getDefaultCharges()));
						sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(item.getId()).getName() + " has slightly degraded!");
						break;
					}
				if (item.getMetaData("combatCharges") != null) {
					item.addMetaData("combatCharges", item.getMetaDataI("combatCharges") - 1);
					if (item.getMetaDataI("combatCharges") <= 0) {
						ItemDegrade deg = null;
						for (ItemDegrade d : ItemDegrade.values())
							if (d.getItemId() == item.getId() || d.getDegradedId() == item.getId() && d.getBrokenId() != -1) {
								deg = d;
								break;
							}
						if (deg != null) {
							if (deg.getBrokenId() == 4207) {
								getEquipment().deleteSlot(i);
								getAppearance().generateAppearanceData();
								if (getInventory().hasFreeSlots()) {
									getInventory().addItem(4207, 1);
									sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(deg.getItemId()).getName() + " has reverted to a crystal seed!");
								} else {
									World.addGroundItem(new Item(4207), Tile.of(getX(), getY(), getPlane()));
									sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(deg.getItemId()).getName() + " has reverted to a crystal seed and fallen to the floor!");
								}
								break;
							}
							getEquipment().setSlot(i, new Item(deg.getBrokenId(), item.getAmount()));
							getAppearance().generateAppearanceData();
							sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(item.getId()).getName() + " has fully degraded!");
						} else {
							getEquipment().deleteSlot(i);
							getAppearance().generateAppearanceData();
							sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(item.getId()).getName() + " has degraded to dust!");
						}
					}
				}
			}
		}
	}

	private void processMusic() {
		if (!getTempAttribs().getB("MUSIC_BREAK") && musicsManager.musicEnded()) {
			getTempAttribs().setB("MUSIC_BREAK", true);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					musicsManager.nextAmbientSong();
					getTempAttribs().setB("MUSIC_BREAK", false);
				}
			}, Utils.randomInclusive(10, 30));
		}
	}

	public void restoreTick(int skill, int restore) {
		int currentLevel = getSkills().getLevel(skill);
		int normalLevel = getSkills().getLevelForXp(skill);
		if (currentLevel > normalLevel)
			getSkills().set(skill, currentLevel - 1);
		else if (currentLevel < normalLevel)
			getSkills().set(skill, Utils.clampI(currentLevel + restore, 0, normalLevel));
	}

	public void postSync() {
		getInventory().processRefresh();
		getVars().syncVarsToClient();
		skills.updateXPDrops();

		for (Sound sound : sounds)
			if (sound != null)
				getPackets().sendSound(sound);
		sounds.clear();
	}

	public void tickFarming() {
		List<PatchLocation> toRemove = new ArrayList<>();
		for (FarmPatch patch : getPatches().values()) {
			if (patch == null)
				continue;
			patch.tick(this);
			patch.updateVars(this);
			if (patch.needsRemove())
				toRemove.add(patch.location);
		}
		for (PatchLocation p : toRemove)
			getPatches().remove(p);
	}

	@Override
	public void processReceivedHits() {
		super.processReceivedHits();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || tempMoveType != null || updateMovementType;
	}

	@Override
	public void processMovement() {
		super.processMovement();
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		tempMoveType = null;
		updateMovementType = false;
	}

	public void toggleRun(boolean update) {
		super.setRun(!getRun());
		updateMovementType = true;
		if (update)
			sendRunButtonConfig();
	}

	public void setRunHidden(boolean run) {
		super.setRun(run);
		updateMovementType = true;
	}

	@Override
	public void setRun(boolean run) {
		if (run != getRun()) {
			super.setRun(run);
			updateMovementType = true;
		}
		sendRunButtonConfig();
	}

	public VarManager getVars() {
		return varManager;
	}

	public void sendRunButtonConfig() {
		getVars().setVar(173, resting ? 3 : getRun() ? 1 : 0);
	}

	public void restoreRunEnergy(double energy) {
		if (runEnergy + energy > 100.0)
			runEnergy = 100.0;
		else
			runEnergy += energy;
		getPackets().sendRunEnergy(runEnergy);
	}

	public void drainRunEnergy(double energy) {
		if (getNSV().getB("infRun"))
			return;
		if ((runEnergy - energy) < 0.0)
			runEnergy = 0.0;
		else
			runEnergy -= energy;
		getPackets().sendRunEnergy(runEnergy);
	}

	public void run() {
		if (getAccount().getRights() == null) {
			setRights(Rights.PLAYER);
			LobbyCommunicator.updateRights(this);
		}
		LobbyCommunicator.addWorldPlayer(account, response -> {
			if (response == null || !response)
				forceLogout();
		});
		getClan(clan -> appearence.generateAppearanceData());
		getGuestClan();
		int updateTimer = (int) World.getTicksTillUpdate();
		if (updateTimer != -1)
			getPackets().sendSystemUpdate(updateTimer);
		addIP(getSession().getIP());
		lastIP = getSession().getIP();
		interfaceManager.sendInterfaces();
		getPackets().sendRunEnergy(runEnergy);
		refreshAllowChatEffects();
		refreshMouseButtons();
		refreshPrivateChatSetup();
		refreshOtherChatsSetup();
		sendRunButtonConfig();
		if (!hasRights(Rights.ADMIN)) {
			removeDungItems();
			removeHouseOnlyItems();
		}
		sendDefaultPlayersOptions();
		checkMultiArea();
		inventory.init();
		equipment.init();
		skills.init();
		combatDefinitions.init();
		prayer.init();
		house.init();
		refreshHitPoints();
		prayer.refreshPoints();
		getPoison().refresh();
		getVars().setVar(281, 1000); // unlock can't do this on tutorial
		getVars().setVar(1160, -1); // unlock summoning orb
		getVars().setVar(1159, 1);
		getVars().setVarBit(4221, 0); //unlock incubator
		getVars().setVarBit(1766, 1); //unlock killerwatt portal
		getVars().setVarBit(6471, 45); //chaos dwarf area
		getVars().setVarBit(532, 4); //lumbridge underground
		getVars().setVar(678, 3); //rfd chest

		updateSlayerTask();

		Toolbelt.refreshToolbelt(this);
		questManager.unlockQuestTabOptions();
		questManager.updateAllQuestStages();
		miniquestManager.updateAllStages();
		getPackets().sendGameBarStages();
		musicsManager.init();
		emotesManager.refreshListConfigs();
		sendUnlockedObjectConfigs();
		questManager.sendQuestPoints();
		AchievementInterface.init(this);

		for (Item item : equipment.getItemsCopy())
			if (item != null)
				PluginManager.handle(new ItemEquipEvent(this, item, true));

		GE.updateOffers(username);

		if (summFamiliar != null)
			summFamiliar.respawn(this);
		else
			petManager.init();
		running = true;
		updateMovementType = true;
		pvpCombatLevelThreshhold = -1;
		appearence.generateAppearanceData();
		checkWasInDynamicRegion();
		controllerManager.login(); // checks what to do on login after welcome
		//unlock robust glass
		getVars().setVarBit(4322, 1);
		//unlock ability to use elemental and catalytic runes
		getVars().setVarBit(5493, 1);
		// screen
		if (machineInformation != null)
			machineInformation.sendSuggestions(this);
		notes.init();

		double farmingTicksMissed = Math.floor(getTicksSinceLastLogout() / FarmPatch.FARMING_TICK);
		if (farmingTicksMissed > 768.0)
			farmingTicksMissed = 768.0;
		if (farmingTicksMissed < 1.0)
			farmingTicksMissed = 0.0;
		for (int i = 0;i < farmingTicksMissed;i++)
			tickFarming();

		for (FarmPatch p : getPatches().values())
			if (p != null)
				p.updateVars(this);

		if (getStarter() > 0) {
			sendMessage("Welcome to " + Settings.getConfig().getServerName() + ".");
			if (!Settings.getConfig().getLoginMessage().isEmpty())
				sendMessage(Settings.getConfig().getLoginMessage());
			processDailyTasks();
		}

		if (!isChosenAccountType()) {
			if (!Settings.getConfig().isDebug())
				getControllerManager().startController(new TutorialIslandController());
			else
				setStarter(1);
			if (!getUsername().startsWith("cli_bot")) {
				PlayerLook.openCharacterCustomizing(this);
				startConversation(new GamemodeSelection(this));
			} else
				setChosenAccountType(true);
		}
		//getPackets().write(new UpdateRichPresence("state", "Logged in as " + getDisplayName()));
		PluginManager.handle(new LoginEvent(this));
		PluginManager.handle(new EnterChunkEvent(this, getChunkId()));
	}

	private double getTicksSinceLastLogout() {
		if (timeLoggedOut <= 0)
			return 0;
		return (double) ((System.currentTimeMillis() - timeLoggedOut) / 600L);
	}

	public void processDailyTasks() {
		if (Utils.getTodayDate() != lastDate) {
			incrementCount("Unique days logged in");
			sendMessage("<col=FF0000>Your dailies have been reset.</col>");
			dailyAttributes = new ConcurrentHashMap<>();
			lastDate = Utils.getTodayDate();
		}
	}

	public boolean containsOneItem(int... itemIds) {
		if (getInventory().containsOneItem(itemIds) || getEquipment().containsOneItem(itemIds))
			return true;
		Familiar familiar = getFamiliar();
		if (familiar != null && ((familiar.getInventory() != null && familiar.containsOneItem(itemIds) || familiar.isFinished())))
			return true;
		return false;
	}

	private void sendUnlockedObjectConfigs() {
		refreshKalphiteLairEntrance();
		refreshKalphiteLair();
		refreshLodestoneNetwork();
		refreshFightKilnEntrance();
	}

	private boolean[] lodestones = new boolean[Lodestone.values().length];

	public void resetLodestones() {
		lodestones = new boolean[Lodestone.values().length];
		lodestones[0] = true;
		lodestones[1] = true;
	}

	public void unlockLodestone(Lodestone stone, GameObject object) {
		if (lodestones[stone.ordinal()])
			return;
		if (stone == Lodestone.BANDIT_CAMP || stone == Lodestone.LUNAR_ISLE) {
			sendMessage("This lodestone doesn't respond.");
			return;
		}
		final Tile tile = object.getTile();
		if (object != null) {
			playCutscene(cs -> {
				cs.camPos(object.getX()+1, object.getY()+6, 5000);
				cs.camLook(object.getX(), object.getY(), 0);
				cs.delay(2);
				cs.action(() -> {
					lodestones[stone.ordinal()] = true;
					refreshLodestoneNetwork();
					getPackets().sendSpotAnim(new SpotAnim(3019), tile);
					if (stone.getAchievement() != null)
						getInterfaceManager().sendAchievementComplete(stone.getAchievement());
				});
				cs.delay(10);
				cs.camPosResetSoft();
			});
		}
	}

	public boolean unlockedLodestone(Lodestone stone) {
		if (stone == Lodestone.BANDIT_CAMP)
			return isQuestComplete(Quest.DESERT_TREASURE);
		if (stone == Lodestone.LUNAR_ISLE)
			return isQuestComplete(Quest.LUNAR_DIPLOMACY);
		return lodestones[stone.ordinal()];
	}

	private void refreshLodestoneNetwork() {
		for (Lodestone stone : Lodestone.values())
			if (stone.getConfigId() != -1 && unlockedLodestone(stone))
				getVars().setVarBit(stone.getConfigId(), 1);

		//		// unlocks bandit camp lodestone
		//		getPackets().sendConfigByFile(358, 15);
		//		// unlocks lunar isle lodestone
		//		getPackets().sendConfigByFile(2448, 190);
	}

	private void refreshKalphiteLair() {
		if (khalphiteLairSetted)
			getVars().setVarBit(7263, 1);
	}

	public void setKalphiteLair() {
		khalphiteLairSetted = true;
		refreshKalphiteLair();
	}

	public void refreshFightKilnEntrance() {
		if (getCounterValue("Fight Caves clears") > 0)
			getVars().setVarBit(10838, 1);
	}

	private void refreshKalphiteLairEntrance() {
		if (khalphiteLairEntranceSetted)
			getVars().setVarBit(7262, 1);
	}

	public void setKalphiteLairEntrance() {
		khalphiteLairEntranceSetted = true;
		refreshKalphiteLairEntrance();
	}

	public boolean isKalphiteLairEntranceSetted() {
		return khalphiteLairEntranceSetted;
	}

	public boolean isKalphiteLairSetted() {
		return khalphiteLairSetted;
	}

	public void save(String key, Object value) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		savingAttributes.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getO(String name) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		if (savingAttributes.get(name) == null)
			return null;
		return (T) savingAttributes.get(name);
	}

	public Object get(String key) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		if (savingAttributes.get(key) != null)
			return savingAttributes.get(key);
		return false;
	}

	public int getI(String key) {
		return getI(key, -1);
	}

	public int getI(String key, int def) {
		Object val = get(key);
		if (val == Boolean.FALSE)
			return def;
		return (int) (val instanceof Integer ? (int) val : (double) val);
	}

	public long getL(String key) {
		return getL(key, -1L);
	}

	public long getL(String key, long def) {
		Object val = get(key);
		if (val == Boolean.FALSE)
			return def;
		return (val instanceof Long ? (long) val : ((Double) val).longValue());
	}

	public boolean getBool(String key) {
		Object val = get(key);
		if (val == Boolean.FALSE)
			return false;
		return (Boolean) val;
	}

	public Map<String, Object> getSavingAttributes() {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		return savingAttributes;
	}

	public void delete(String key) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<>();
		savingAttributes.remove(key);
	}

	public int getCounterValueContaining(String npcName) {
		if (variousCounter == null)
			variousCounter = new ConcurrentHashMap<>();
		int number = 0;
		for (String key : variousCounter.keySet())
			if (key.contains(npcName) || key.contains(npcName.toLowerCase()))
				number += variousCounter.get(key);
		return number;
	}

	public int getCounterValue(String npcName) {
		if (variousCounter == null)
			variousCounter = new ConcurrentHashMap<>();
		if (npcName.startsWith("%"))
			return getCounterValueContaining(npcName.replace("%", ""));
		if (variousCounter.containsKey(npcName))
			return variousCounter.get(npcName);
		return 0;
	}

	public void incrementCount(String string) {
		incrementCount(string, 1);
	}

	public void incrementCount(String string, int count) {
		if (variousCounter == null)
			variousCounter = new ConcurrentHashMap<>();
		variousCounter.put(string, variousCounter.getOrDefault(string, 0) + count);
	}

	public int getNumberKilled(String npcName) {
		if (npcKills == null)
			npcKills = new ConcurrentHashMap<>();
		if (npcName.startsWith("%"))
			return getNumberKilledContaining(npcName.replace("%", ""));
		if (npcKills.containsKey(npcName))
			return npcKills.get(npcName);
		return 0;
	}

	public int getNumberKilledContaining(String regex) {
		if (npcKills == null)
			npcKills = new ConcurrentHashMap<>();
		int number = 0;
		for (String key : npcKills.keySet())
			if (key.contains(regex) || key.contains(regex.toLowerCase()))
				number += npcKills.get(key);
		return number;
	}

	public Map<String, Integer> getNPCKills() {
		return npcKills;
	}

	public void sendNPCKill(String string) {
		if (npcKills == null)
			npcKills = new ConcurrentHashMap<>();
		npcKills.put(string, npcKills.getOrDefault(string, 0) + 1);
	}

	public void sendDefaultPlayersOptions() {
		setPlayerOption("Follow", 2);
		setPlayerOption("Trade with", 4);
	}

	@SuppressWarnings("deprecation")
	public void setPlayerOption(String option, int slot, boolean forceFirstOption) {
		if (playerOptions == null)
			playerOptions = new String[10];
		playerOptions[slot-1] = option.equals("null") ? null : option;
		getPackets().sendPlayerOption(option, slot, forceFirstOption);
	}

	public void setPlayerOption(String option, int slot) {
		setPlayerOption(option, slot, false);
	}

	public String getPlayerOption(ClientPacket packet) {
		switch(packet) {
		case PLAYER_OP1:
			return playerOptions[0];
		case PLAYER_OP2:
			return playerOptions[1];
		case PLAYER_OP3:
			return playerOptions[2];
		case PLAYER_OP4:
			return playerOptions[3];
		case PLAYER_OP5:
			return playerOptions[4];
		case PLAYER_OP6:
			return playerOptions[5];
		case PLAYER_OP7:
			return playerOptions[6];
		case PLAYER_OP8:
			return playerOptions[7];
		case PLAYER_OP9:
			return playerOptions[8];
		case PLAYER_OP10:
			return playerOptions[9];
		default:
			return "null";
		}
	}

	@Override
	public void checkMultiArea() {
		if (!started)
			return;
		boolean isAtMultiArea = isForceMultiArea() ? true : World.isMultiArea(getTile());
		if (isAtMultiArea && !isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendVarc(616, 1);
		} else if (!isAtMultiArea && isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendVarc(616, 0);
		}
	}

	/**
	 * Logs the player out.
	 *
	 * @param lobby: If we're logging out to the lobby.
	 */
	public void logout(boolean lobby) {
		if (!running)
			return;
		if (inCombat(10000) || hasBeenHit(10000)) {
			sendMessage("You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (getEmotesManager().isAnimating()) {
			sendMessage("You can't log out while performing an emote.");
			return;
		}
		if (isLocked()) {
			sendMessage("You can't log out while performing an action.");
			return;
		}
		if (isDead() || isDying())
			return;
		getPackets().sendLogout(lobby);
		finish();
		running = false;
	}

	public void forceLogout() {
		getPackets().sendLogout(false);
		running = false;
		realFinish();
	}

	public void idleLog() {
		incrementCount("Idle logouts");
		getPackets().sendLogout(true);
		finish();
	}

	private transient boolean finishing;

	private Notes notes;

	public boolean isMaxed = false;

	public int scPoints;

	@Override
	public void finish() {
		finish(0);
	}

	public void finish(final int tryCount) {
		disconnected = true;
		if (hasFinished())
			return;
		stopAll(false, true, !(getInteractionManager().getInteraction() instanceof PlayerCombatInteraction));
		if ((inCombat(10000) || hasBeenHit(10000) || getEmotesManager().isAnimating() || isLocked()) && tryCount < 6) {
			WorldTasks.schedule(Ticks.fromSeconds(10), () -> {
				try {
					finishing = false;
					if (isDead() || isDying())
						finish(tryCount);
					else
						finish(tryCount + 1);
				} catch (Throwable e) {
					Logger.handle(Player.class, "finish", e);
				}
			});
			return;
		}
		realFinish();
	}

	public void realFinish() {
		getTempAttribs().setB("realFinished", true);
		if (isDead() || isDying())
			return;
		stopAll();
		cutsceneManager.logout();
		controllerManager.logout(); // checks what to do on before logout for
		house.finish();
		dungManager.finish();
		running = false;
		if (summFamiliar != null && !summFamiliar.isFinished())
			summFamiliar.finish();
		else if (pet != null)
			pet.finish();
		lastLoggedIn = System.currentTimeMillis();
		setFinished(true);
		AccountLimiter.remove(getSession().getIP());
		session.setDecoder(null);
		WorldDB.getPlayers().save(this, () -> {
			LobbyCommunicator.removeWorldPlayer(this);
			World.removePlayer(this);
			ChunkManager.updateChunks(this);
			WorldDB.getHighscores().save(this);
			Logger.info(Player.class, "realFinish", "Finished Player: " + getUsername());
		});
		ChunkManager.updateChunks(this);
	}

	public long getLastLoggedIn() {
		return lastLoggedIn;
	}

	@Override
	public boolean restoreHitPoints() {
		boolean update = super.restoreHitPoints();
		int toRegen = 0;
		if (isResting())
			toRegen += 1;
		if (getPrayer().active(Prayer.RAPID_HEAL))
			toRegen += 1;
		if (getPrayer().active(Prayer.RAPID_RENEWAL))
			toRegen += 4;
		if (getEquipment().getGlovesId() == 11133)
			toRegen *= 2;
		if ((getHitpoints() + toRegen) > getMaxHitpoints())
			toRegen = getMaxHitpoints() - getHitpoints();
		if (getHitpoints() < getMaxHitpoints())
			setHitpoints(getHitpoints() + toRegen);
		if (update || toRegen > 0)
			refreshHitPoints();
		return update;
	}

	public void refreshHitPoints() {
		getVars().setVarBit(7198, Utils.clampI(getHitpoints(), 0, Short.MAX_VALUE));
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		refreshHitPoints();
	}

	public DungManager getDungManager() {
		return dungManager;
	}

	public void setDungManager(DungManager dungManager) {
		this.dungManager = dungManager;
	}

	@Override
	public int getMaxHitpoints() {
		return skills.getLevel(Constants.HITPOINTS) * 10 + equipment.getEquipmentHpIncrease();
	}

	public String getUsername() {
		return username;
	}

	public Set<String> getIPList() {
		return ipList;
	}

	public int getMessageIcon() {
		return getRights().getCrown();
	}

	public WorldEncoder getPackets() {
		try {
			return session.getEncoder(WorldEncoder.class);
		} catch(Throwable e) {
			System.err.println("Error casting player's encoder to world encoder.");
			return null;
		}
	}

	public void visualizeChunk(int chunkId) {
		int[] oldChunk = MapUtils.decode(Structure.CHUNK, chunkId);
		for (int i = 0;i < 8;i++)
			getPackets().sendGroundItem(new GroundItem(new Item(14486, 1), Tile.of((oldChunk[0] << 3) + i, (oldChunk[1] << 3), getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().sendGroundItem(new GroundItem(new Item(14486, 1), Tile.of((oldChunk[0] << 3), (oldChunk[1] << 3) + i, getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().sendGroundItem(new GroundItem(new Item(14486, 1), Tile.of((oldChunk[0] << 3) + i, (oldChunk[1] << 3) + 7, getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().sendGroundItem(new GroundItem(new Item(14486, 1), Tile.of((oldChunk[0] << 3) + 7, (oldChunk[1] << 3) + i, getPlane())));
	}

	public void devisualizeChunk(int chunkId) {
		int[] oldChunk = MapUtils.decode(Structure.CHUNK, chunkId);
		for (int i = 0;i < 8;i++)
			getPackets().removeGroundItem(new GroundItem(new Item(14486, 1), Tile.of((oldChunk[0] << 3) + i, (oldChunk[1] << 3), getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().removeGroundItem(new GroundItem(new Item(14486, 1), Tile.of((oldChunk[0] << 3), (oldChunk[1] << 3) + i, getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().removeGroundItem(new GroundItem(new Item(14486, 1), Tile.of((oldChunk[0] << 3) + i, (oldChunk[1] << 3) + 7, getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().removeGroundItem(new GroundItem(new Item(14486, 1), Tile.of((oldChunk[0] << 3) + 7, (oldChunk[1] << 3) + i, getPlane())));
	}

	public void sendOptionDialogue(Consumer<Options> options) {
		startConversation(new Dialogue().addOptions(options));
	}

	public void sendOptionDialogue(String question, Consumer<Options> options) {
		startConversation(new Dialogue().addOptions(question, options));
	}

	public void sendInputName(String question, InputStringEvent e) {
		sendInputName(question, null, e);
	}

	public void sendInputName(String question, String description, InputStringEvent e) {
		getTempAttribs().setO("pluginEnterName", e);
		getPackets().sendInputNameScript(question);
		if (description != null)
			getPackets().setIFText(1110, 70, description);
		setCloseChatboxInterfaceEvent(() -> getTempAttribs().removeO("pluginEnterName"));
	}

	public void sendInputLongText(String question, InputStringEvent e) {
		getTempAttribs().setO("pluginEnterLongText", e);
		getPackets().sendInputLongTextScript(question);
		setCloseChatboxInterfaceEvent(() -> getTempAttribs().removeO("pluginEnterLongText"));
	}

	public void sendInputInteger(String question, InputIntegerEvent e) {
		getTempAttribs().setO("pluginInteger", e);
		getPackets().sendInputIntegerScript(question);
		setCloseChatboxInterfaceEvent(() -> {
			if(getTempAttribs().getB("viewingDepositBox") && !getInterfaceManager().topOpen(11)) {
				getInterfaceManager().sendSubDefaults(Sub.TAB_INVENTORY, Sub.TAB_EQUIPMENT);
				getTempAttribs().setB("viewingDepositBox", false);
			}
			getTempAttribs().removeO("pluginInteger");
		});
	}

	public void sendInputHSL(InputHSLEvent e) {
		getTempAttribs().setO("pluginHSL", e);
		setCloseChatboxInterfaceEvent(() -> getTempAttribs().removeO("pluginHSL"));
	}

	public void sendInputForumQFC(InputStringEvent e) {
		getTempAttribs().setO("pluginQFCD", e);
		setCloseChatboxInterfaceEvent(() -> getTempAttribs().removeO("pluginQFCD"));
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean isRunning() {
		return running;
	}

	public String getDisplayName() {
		return account.getDisplayName();
	}

	public Appearance getAppearance() {
		return appearence;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public MoveType getTemporaryMoveType() {
		return tempMoveType;
	}

	public void setTemporaryMoveType(MoveType temporaryMovementType) {
		this.tempMoveType = temporaryMovementType;
	}

	public LocalPlayerUpdate getLocalPlayerUpdate() {
		return localPlayerUpdate;
	}

	public LocalNPCUpdate getLocalNPCUpdate() {
		return localNPCUpdate;
	}

	public ScreenMode getScreenMode() {
		return screenMode;
	}

	public void setScreenMode(ScreenMode mode) {
		this.screenMode = mode;
	}

	public boolean resizeable() {
		return screenMode.resizeable();
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public Session getSession() {
		return session;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public boolean clientHasLoadedMapRegion() {
		return clientLoadedMapRegion < World.getServerTicks();
	}

	public void setClientHasLoadedMapRegion() {
		clientLoadedMapRegion = -1;
	}

	public boolean clientHasLoadedMapRegionFinished() {
		return clientLoadedMapRegion == -1;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Skills getSkills() {
		return skills;
	}

	public double getRunEnergy() {
		return runEnergy;
	}

	public void setRunEnergy(double runEnergy) {
		if (getNSV().getB("infRun")) {
			this.runEnergy = 100.0;
			getPackets().sendRunEnergy(this.runEnergy);
			return;
		}
		this.runEnergy = runEnergy;
		if (this.runEnergy < 0.0)
			this.runEnergy = 0.0;
		if (this.runEnergy > 100.0)
			this.runEnergy = 100.0;
		getPackets().sendRunEnergy(this.runEnergy);
	}

	public boolean isResting() {
		return resting;
	}

	public void setResting(boolean resting) {
		this.resting = resting;
		sendRunButtonConfig();
	}

	public CombatDefinitions getCombatDefinitions() {
		return combatDefinitions;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void handlePreHit(Hit hit) {
		TransformationRing.triggerDeactivation(this);

		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if (invulnerable) {
			hit.setDamage(0);
			return;
		}

		Entity source = hit.getSource();
		if (source == null)
			return;

		if (hasEffect(Effect.STAFF_OF_LIGHT_SPEC) && hit.getLook() == HitLook.MELEE_DAMAGE) {
			hit.setDamage((int) (hit.getDamage() * 0.5));
			spotAnim(2320);
		}
		if (prayer.hasPrayersOn() && hit.getDamage() != 0)
			if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				if (prayer.active(Prayer.PROTECT_MAGIC))
					hit.setDamage((int) (hit.getDamage() * source.getMagePrayerMultiplier()));
				else if (prayer.active(Prayer.DEFLECT_MAGIC)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getMagePrayerMultiplier()));
					if (deflectedDamage > 0 && Math.random() < 0.6) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextSpotAnim(new SpotAnim(2228));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				if (prayer.active(Prayer.PROTECT_RANGE))
					hit.setDamage((int) (hit.getDamage() * source.getRangePrayerMultiplier()));
				else if (prayer.active(Prayer.DEFLECT_RANGE)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getRangePrayerMultiplier()));
					if (deflectedDamage > 0 && Math.random() < 0.6) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextSpotAnim(new SpotAnim(2229));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.MELEE_DAMAGE)
				if (prayer.active(Prayer.PROTECT_MELEE))
					hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
				else if (prayer.active(Prayer.DEFLECT_MELEE)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
					if (deflectedDamage > 0 && Math.random() < 0.6) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextSpotAnim(new SpotAnim(2230));
						setNextAnimation(new Animation(12573));
					}
				}
		if (hit.getDamage() >= 200)
			if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				int reducedDamage = hit.getDamage() * combatDefinitions.getBonus(Bonus.ABSORB_MELEE) / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				int reducedDamage = hit.getDamage() * combatDefinitions.getBonus(Bonus.ABSORB_RANGE) / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				int reducedDamage = hit.getDamage() * combatDefinitions.getBonus(Bonus.ABSORB_MAGIC) / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			}
		int shieldId = equipment.getShieldId();
		if (shieldId == 13742) {
			if (Utils.getRandomInclusive(100) <= 70)
				hit.setDamage((int) (hit.getDamage() * 0.75));
		} else if (shieldId == 13740) { // divine
			int drain = (int) (Math.ceil(hit.getDamage() * 0.3) / 2);
			if (prayer.getPoints() >= drain) {
				hit.setDamage((int) (hit.getDamage() * 0.70));
				prayer.drainPrayer(drain);
			}
		}
		if (castedVeng && hit.getDamage() >= 4) {
			castedVeng = false;
			setNextForceTalk(new ForceTalk("Taste vengeance!"));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					source.applyHit(new Hit(Player.this, (int) (hit.getDamage() * 0.75), HitLook.TRUE_DAMAGE));
				}
			});
		}
		if (source instanceof Player p2)
			if (p2.prayer.hasPrayersOn())
				if (p2.prayer.active(Prayer.SMITE)) {
					int drain = hit.getDamage() / 4;
					if (drain > 0)
						prayer.drainPrayer(drain);
				} else if (hit.getDamage() == 0)
					return;
		getControllerManager().processIncomingHit(hit);
		getAuraManager().onIncomingHit(hit);
	}

	@Override
	public void handlePreHitOut(Entity target, Hit hit) {
		if (getEquipment().fullGuthansEquipped())
			if (Utils.random(4) == 0) {
				int heal = hit.getDamage();
				if (heal > 0)
					if (getHitpoints() < getMaxHitpoints()) {
						heal(heal);
						target.setNextSpotAnim(new SpotAnim(398));
					}
			}
		if (getPrayer().hasPrayersOn())
			if (getPrayer().active(Prayer.SOUL_SPLIT)) {
				if (hit.getDamage() == 0)
					return;
				switch(hit.getLook()) {
				case MELEE_DAMAGE:
				case RANGE_DAMAGE:
				case MAGIC_DAMAGE:
					target.sendSoulSplit(hit, this);
					break;
				default:
					break;
				}
			}
		getAuraManager().onOutgoingHit(hit);
		getControllerManager().processOutgoingHit(hit, target);
	}

	@Override
	public void handlePostHit(Hit hit) {

	}

	public void safeDeath(Tile respawnTile) {
		safeDeath(null, respawnTile, "Oh dear, you are dead!", null);
	}

	public void safeDeath(Tile respawnTile, String message, Consumer<Player> onFall) {
		safeDeath(null, respawnTile, message, onFall);
	}

	public void safeDeath(Tile respawnTile, Consumer<Player> onFall) {
		safeDeath(null, respawnTile, "Oh dear, you are dead!", onFall);
	}

	public void safeDeath(Entity source, Tile respawnTile, String message, Consumer<Player> onFall) {
		lock();
		stopAll();
		if (prayer.active(Prayer.RETRIBUTION))
			retribution(source);
		if (prayer.active(Prayer.WRATH))
			wrath(source);
		WorldTasks.scheduleTimer(0, 1, tick -> {
			switch(tick) {
				case 0 -> setNextAnimation(new Animation(836));
				case 1 -> sendMessage(message);
				case 3 -> {
					reset();
					setNextTile(respawnTile);
					setNextAnimation(new Animation(-1));
					if (onFall != null)
						onFall.accept(this);
				}
				case 4 ->  {
					jingle(90);
					unlock();
					return false;
				}
			}
			return true;
		});
	}
	@Override
	public void sendDeath(final Entity source) {
		incrementCount("Deaths");

		if (prayer.hasPrayersOn() && !getTempAttribs().getB("startedDuel")) {
			if (prayer.active(Prayer.RETRIBUTION))
				retribution(source);
			if (prayer.active(Prayer.WRATH))
				wrath(source);
		}

		refreshDyingTime();
		setNextAnimation(new Animation(-1));
		if (!controllerManager.sendDeath())
			return;
		lock(7);
		stopAll();
		if (summFamiliar != null)
			summFamiliar.sendDeath(this);
		Tile lastTile = Tile.of(getTile());
		if (isHasNearbyInstancedChunks())
			lastTile = getRandomGraveyardTile();
		final Tile deathTile = lastTile;
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(836));
				else if (loop == 1)
					sendMessage("Oh dear, you are dead!");
				else if (loop == 2) {
					reset();
					if (source instanceof Player opp && opp.hasRights(Rights.ADMIN))
						setNextTile(Settings.getConfig().getPlayerRespawnTile());
					else
						controllerManager.startController(new DeathOfficeController(deathTile, hasSkull()));
				} else if (loop == 3) {
					setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					jingle(90);
					unlock();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void retribution(Entity source) {
		setNextSpotAnim(new SpotAnim(437));
		for (Direction dir : Direction.values())
			World.sendSpotAnim(Tile.of(getX() - dir.getDx(), getY() - dir.getDy(), getPlane()), new SpotAnim(438, 20, 10, dir.getId()));
		if (isAtMultiArea()) {
			for (Player player : queryNearbyPlayersByTileRange(1, player -> !player.isDead() && player.isCanPvp() && player.withinDistance(getTile(), 1) || getControllerManager().canHit(player)))
				player.applyHit(new Hit(this, Utils.getRandomInclusive((int) (skills.getLevelForXp(Constants.PRAYER) * 2.5)), HitLook.TRUE_DAMAGE));
			for (NPC npc : queryNearbyNPCsByTileRange(1, npc -> !npc.isDead() && npc.withinDistance(this, 1) && npc.getDefinitions().hasAttackOption() && getControllerManager().canHit(npc)))
				npc.applyHit(new Hit(this, Utils.getRandomInclusive((int) (skills.getLevelForXp(Constants.PRAYER) * 2.5)), HitLook.TRUE_DAMAGE));
		} else if (source != null && source != this && !source.isDead() && !source.hasFinished() && source.withinDistance(getTile(), 1))
			source.applyHit(new Hit(this, Utils.getRandomInclusive((int) (skills.getLevelForXp(Constants.PRAYER) * 2.5)), HitLook.TRUE_DAMAGE));
	}

	public void wrath(Entity source) {
		for (Direction dir : Direction.values())
			World.sendProjectile(this, Tile.of(getX() + (dir.getDx()*2), getY() + (dir.getDy()*2), getPlane()), 2261, 0, 0, 15, 0.4, 35, 15,
				proj -> World.sendSpotAnim(proj.getToTile(), new SpotAnim(2260)));
		setNextSpotAnim(new SpotAnim(2259));
		WorldTasks.schedule(() -> {
			if (isAtMultiArea()) {
				for (Player player : queryNearbyPlayersByTileRange(1, player -> !player.isDead() && player.isCanPvp() && player.withinDistance(getTile(), 2) || getControllerManager().canHit(player)))
					player.applyHit(new Hit(this, Utils.getRandomInclusive((skills.getLevelForXp(Constants.PRAYER) * 3)), HitLook.TRUE_DAMAGE));
				for (NPC npc : queryNearbyNPCsByTileRange(1, npc -> !npc.isDead() && npc.withinDistance(this, 2) && npc.getDefinitions().hasAttackOption() && getControllerManager().canHit(npc)))
					npc.applyHit(new Hit(this, Utils.getRandomInclusive((skills.getLevelForXp(Constants.PRAYER) * 3)), HitLook.TRUE_DAMAGE));
			} else if (source != null && source != this && !source.isDead() && !source.hasFinished() && source.withinDistance(getTile(), 2))
				source.applyHit(new Hit(this, Utils.getRandomInclusive((skills.getLevelForXp(Constants.PRAYER) * 3)), HitLook.TRUE_DAMAGE));
		});
	}

	public Tile getRandomGraveyardTile() {
		return Tile.of(Tile.of(2745, 3474, 0), 4);
	}

	public void sendPVEItemsOnDeath(Player killer, boolean dropItems) {
		Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(this, true, dropItems, prayer.isProtectingItem());
		sendPVEItemsOnDeath(killer, Tile.of(getTile()), Tile.of(getTile()), true, slots);
	}

	public void sendPVEItemsOnDeath(Player killer, Tile deathTile, Tile respawnTile, boolean noGravestone, Integer[][] slots) {
		if (hasRights(Rights.ADMIN) || Settings.getConfig().isDebug())
			return;
		auraManager.removeAura();
		Item[][] items = GraveStone.getItemsKeptOnDeath(this, slots);
		inventory.reset();
		equipment.reset();
		appearence.generateAppearanceData();
		WorldDB.getPlayers().save(this);
		for (Item item : items[0])
			inventory.addItem(item);
		for (Item item : items[1]) {
			if (item == null)
				continue;
			ItemDegrade deg = null;
			for(ItemDegrade d : ItemDegrade.values())
				if (d.getDegradedId() == item.getId() || d.getItemId() == item.getId()) {
					deg = d;
					break;
				}
			if (deg != null)
				if (deg.getBrokenId() != -1) {
					item.setId(deg.getBrokenId());
					item.deleteMetaData();
				} else {
					item.setAmount(ItemDefinitions.getDefs(item.getId()).getValue());
					item.setId(995);
					item.deleteMetaData();
				}
		}
		if (items[1].length != 0)
			if (noGravestone)
				for (Item item : items[1])
					World.addGroundItem(item, deathTile, killer == null ? this : killer, true, 210, (killer == null || killer == this) ? DropMethod.NORMAL : DropMethod.TURN_UNTRADEABLES_TO_COINS);
			else
				new GraveStone(this, deathTile, items[1]);
	}

	public void sendPVPItemsOnDeath(Player killer) {
//		if (hasRights(Rights.ADMIN) || Settings.getConfig().isDebug())
//			return;
		if (killer != null && !killer.getUsername().equals(getUsername()) && killer.isIronMan())
			killer = null;
		auraManager.removeAura();
		CopyOnWriteArrayList<Item> containedItems = new CopyOnWriteArrayList<>();
		for (int i = 0; i < 14; i++)
			if (equipment.getItem(i) != null && equipment.getItem(i).getId() != -1 && equipment.getItem(i).getAmount() != -1)
				containedItems.add(new Item(equipment.getItem(i).getId(), equipment.getItem(i).getAmount(), equipment.getItem(i).getMetaData()));
		for (int i = 0; i < 28; i++)
			if (inventory.getItem(i) != null && inventory.getItem(i).getId() != -1 && inventory.getItem(i).getAmount() != -1)
				containedItems.add(new Item(getInventory().getItem(i).getId(), getInventory().getItem(i).getAmount(), getInventory().getItem(i).getMetaData()));
		if (containedItems.isEmpty())
			return;
		int keptAmount = hasSkull() ? 0 : 3;
		if (prayer.isProtectingItem())
			keptAmount++;
		CopyOnWriteArrayList<Item> keptItems = new CopyOnWriteArrayList<>();
		Item lastItem = new Item(1, 1);
		for (int i = 0; i < keptAmount; i++) {
			for (Item item : containedItems) {
				int price = item.getDefinitions().getValue();
				if (price >= lastItem.getDefinitions().getValue())
					lastItem = item;
			}
			keptItems.add(lastItem);
			containedItems.remove(lastItem);
			lastItem = new Item(1, 1);
		}
		inventory.reset();
		equipment.reset();
		WorldDB.getPlayers().save(this);
		for (Item item : keptItems)
			if (item.getId() != 1)
				getInventory().addItem(item);
		List<Item> droppedItems = new ArrayList<>();
		for (Item item : containedItems) {
			if (ItemConstants.isTradeable(item) || item.getId() == 24444)
				droppedItems.add(item);
				//World.addGroundItem(item, getLastTile(), killer == null ? this : killer, true, 60);
			else {
				ItemDegrade deg = null;
				for (ItemDegrade d : ItemDegrade.values()) {
					if (d.getDegradedId() == item.getId() || d.getItemId() == item.getId()) {
						deg = d;
						break;
					}
				}
				if (deg != null && deg.getBrokenId() != -1) {
					Item broken = new Item(deg.getBrokenId(), item.getAmount());
					droppedItems.add((!ItemConstants.isTradeable(broken) && (killer != null && killer != this)) ? new Item(995, item.getDefinitions().getValue()) : broken);
				} else
					droppedItems.add(new Item(995, item.getDefinitions().getValue()));
			}
		}
		if (killer == null) {
			for (Item item : droppedItems)
				World.addGroundItem(item, getLastTile(), this, true, 60);
		} else {
			List<Item> foodItems = new ArrayList<>();
			List<Item> trophyItems = new ArrayList<>();
			for (Item item : droppedItems) {
				if (Foods.isConsumable(item) || Potions.Potion.POTS.keySet().contains(item.getId()) || item.getId() == 24444)
					foodItems.add(item);
				else
					trophyItems.add(item);
			}
			if (!trophyItems.isEmpty())
				World.addGroundItem(new Item(24444, 1).addMetaData("trophyBoneOriginator", getDisplayName()).addMetaData("trophyBoneItems", trophyItems), getLastTile(), killer, true, 60);
			for (Item item : foodItems)
				World.addGroundItem(item, getLastTile(), killer, true, 60);
		}
		getAppearance().generateAppearanceData();
	}

	public void increaseKillCount(Player killed) {
		killed.deathCount++;
		if (killed.getSession().getIP().equals(getSession().getIP()))
			return;
		killCount++;
		sendMessage("<col=ff0000>You have killed " + killed.getDisplayName() + ", you have now " + killCount + " kills.");
	}

	public void increaseKillCountSafe(Player killed) {
		if (killed.getSession().getIP().equals(getSession().getIP()))
			return;
		killCount++;
		sendMessage("<col=ff0000>You have killed " + killed.getDisplayName() + ", you have now " + killCount + " kills.");
	}

	@Override
	public int getSize() {
		return appearence.getSize();
	}

	public boolean isCanPvp() {
		return canPvp;
	}

	public void setCanPvp(boolean canPvp, boolean topOption) {
		this.canPvp = canPvp;
		appearence.generateAppearanceData();
		setPlayerOption(canPvp ? "Attack" : "null", 1, topOption);
		getPackets().sendDrawOrder(canPvp && !topOption);
	}

	public void setCanPvp(boolean canPvp) {
		setCanPvp(canPvp, true);
	}

	public PrayerManager getPrayer() {
		return prayer;
	}

	public void useStairs(Tile dest) {
		useStairs(-1, dest, 1, 2);
	}

	public void useStairs(int animId, Tile dest) {
		useStairs(animId, dest, 1, 2, null);
	}

	public void useStairs(int animId, final Tile dest, int useDelay, int totalDelay) {
		useStairs(animId, dest, useDelay, totalDelay, null);
	}

	public void promptUpDown(int emoteId, String up, Tile upTile, String down, Tile downTile) {
		startConversation(new Dialogue().addOptions(ops -> {
			ops.add(up, () -> useStairs(emoteId, upTile, 2, 3));
			ops.add(down, () -> useStairs(emoteId, downTile, 2, 3));
		}));
	}

	public void promptUpDown(String up, Tile upTile, String down, Tile downTile) {
		promptUpDown(-1, up, upTile, down, downTile);
	}

	public void handleOneWayDoor(GameObject object, int xOff, int yOff) {
		handleOneWayDoor(object, new GameObject(object.getId(), object.getType(), object.getRotation() + 1, object.getX(), object.getY(), object.getPlane()), xOff, yOff);
	}

	public void handleOneWayDoor(GameObject object, int replaceId) {
		handleOneWayDoor(object, new GameObject(replaceId, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane()));
	}

	public void handleOneWayDoor(GameObject object) {
		handleOneWayDoor(object, new GameObject(object.getId(), object.getType(), object.getRotation() + 1, object.getX(), object.getY(), object.getPlane()));
	}

	public void handleOneWayDoor(GameObject object, GameObject opened, int rotation1, int rotation2) { //TODO so broken
		World.spawnObjectTemporary(opened, 2);
		lock(2);
		stopAll();
		if (object.getRotation() == rotation1 || object.getRotation() == rotation2)
			addWalkSteps(object.getX(), getY() == object.getY() ? object.getY()+1 : object.getY(), -1, false);
		else
			addWalkSteps(getX() == object.getX() ? object.getX()+1 : object.getX(), object.getY(), -1, false);
	}

	public void handleOneWayDoor(GameObject object, GameObject opened) {
		World.spawnObjectTemporary(opened, 2);
		lock(2);
		stopAll();
		if (object.getRotation() % 2 != 0)
			addWalkSteps(object.getX(), getY() >= object.getY() ? object.getY() - 1 : object.getY(), -1, false);
		else
			addWalkSteps(getX() >= object.getX() ? object.getX() - 1 : object.getX(), object.getY(), -1, false);
	}

	public void useStairs(int emoteId, final Tile dest, int useDelay, int totalDelay, final String message) {
		useStairs(emoteId, dest, useDelay, totalDelay, message, false);
	}

	public void useStairs(int emoteId, final Tile dest, int useDelay, int totalDelay, final String message, final boolean resetAnimation) {
		stopAll();
		lock(totalDelay);
		if (emoteId != -1)
			setNextAnimation(new Animation(emoteId));
		if (useDelay == 0)
			setNextTile(dest);
		else {
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isDead())
						return;
					if (resetAnimation)
						setNextAnimation(new Animation(-1));
					setNextTile(dest);
					if (message != null)
						sendMessage(message);
				}
			}, useDelay - 1);
		}
	}

	public Bank getBank() {
		return bank;
	}

	public ControllerManager getControllerManager() {
		return controllerManager;
	}

	public void switchMouseButtons() {
		mouseButtons = !mouseButtons;
		refreshMouseButtons();
	}

	public void switchAllowChatEffects() {
		allowChatEffects = !allowChatEffects;
		refreshAllowChatEffects();
	}

	public void refreshAllowChatEffects() {
		getVars().setVar(171, allowChatEffects ? 0 : 1);
	}

	public void refreshMouseButtons() {
		getVars().setVar(170, mouseButtons ? 0 : 1);
	}

	public void refreshPrivateChatSetup() {
		getVars().setVar(287, privateChatSetup);
	}

	public void refreshOtherChatsSetup() {
		int value = friendChatSetup << 6;
		getVars().setVar(1438, value);
	}

	public void setPrivateChatSetup(int privateChatSetup) {
		this.privateChatSetup = privateChatSetup;
	}

	public void setFriendChatSetup(int friendChatSetup) {
		this.friendChatSetup = friendChatSetup;
	}

	public void setClanChatSetup(int clanChatSetup) {
	}

	public void setGuestChatSetup(int guestChatSetup) {
	}

	public int getPrivateChatSetup() {
		return privateChatSetup;
	}

	public boolean isForceNextMapLoadRefresh() {
		return forceNextMapLoadRefresh;
	}

	public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
		this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
		setForceUpdateEntityRegion(true);
	}

	public void addPotionDelay(int ticks) {
		potionDelay = ticks + World.getServerTicks();
	}

	public boolean canPot() {
		return World.getServerTicks() >= potionDelay;
	}

	public void addFoodDelay(int ticks) {
		foodDelay = ticks + World.getServerTicks();
	}

	public boolean canEat() {
		return World.getServerTicks() >= foodDelay;
	}

	public boolean canBury() {
		return World.getServerTicks() >= boneDelay;
	}

	public void addBoneDelay(int ticks) {
		boneDelay = ticks + World.getServerTicks();
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
		refreshHitPoints();
	}

	public MusicsManager getMusicsManager() {
		return musicsManager;
	}

	public HintIconsManager getHintIconsManager() {
		return hintIconsManager;
	}

	public boolean isCastVeng() {
		return castedVeng;
	}

	public void setCastVeng(boolean castVeng) {
		castedVeng = castVeng;
	}

	public boolean isCastMagicImbue() {
		return castedMagicImbue;
	}

	public void setCastMagicImbue(boolean castMagicImbue) {
		castedMagicImbue = castMagicImbue;
	}

	public int getKillCount() {
		return killCount;
	}

	public int getBarrowsKillCount() {
		return barrowsKillCount;
	}

	public int setBarrowsKillCount(int barrowsKillCount) {
		return this.barrowsKillCount = barrowsKillCount;
	}

	public int setKillCount(int killCount) {
		return this.killCount = killCount;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public int setDeathCount(int deathCount) {
		return this.deathCount = deathCount;
	}

	public void setCloseChatboxInterfaceEvent(Runnable closeInterfacesEvent) {
		this.closeChatboxInterfaceEvent = closeInterfacesEvent;
	}

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
	}

	public void setFinishConversationEvent(Runnable finishConversationEvent) {
		this.finishConversationEvent = finishConversationEvent;
	}

	public boolean[] getKilledBarrowBrothers() {
		return killedBarrowBrothers;
	}

	public int getKilledBarrowBrothersCount() {
		int count = 0;
		for (boolean b : killedBarrowBrothers)
			if (b)
				count++;
		return count;
	}

	public void setHiddenBrother(int hiddenBrother) {
		this.hiddenBrother = hiddenBrother;
	}

	public int getHiddenBrother() {
		return hiddenBrother;
	}

	public void resetBarrows() {
		hiddenBrother = -1;
		killedBarrowBrothers = new boolean[7];
		barrowsKillCount = 0;
	}

	public ItemsContainer<Item> getTrawlerRewards() {
		return trawlerRewards;
	}

	public void setTrawlerRewards(ItemsContainer<Item> trawlerRewards) {
		this.trawlerRewards = trawlerRewards;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public String getRecovQuestion() {
		return recovQuestion;
	}

	public void setRecovQuestion(String recovQuestion) {
		this.recovQuestion = recovQuestion;
	}

	public String getRecovAnswer() {
		return recovAnswer;
	}

	public void setRecovAnswer(String recovAnswer) {
		this.recovAnswer = recovAnswer;
	}

	public boolean[] getPouchesType() {
		return pouchesType;
	}

	public int[] getPouches() {
		return pouches;
	}

	public EmotesManager getEmotesManager() {
		return emotesManager;
	}

	public String getLastIP() {
		return lastIP;
	}

	public String getLastHostname() {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(getLastIP());
			String hostname = addr.getHostName();
			return hostname;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setPestPoints(int pestPoints) {
		this.pestPoints = pestPoints;
	}

	public int getPestPoints() {
		return pestPoints;
	}

	public boolean isUpdateMovementType() {
		return updateMovementType;
	}

	public CutsceneManager getCutsceneManager() {
		return cutsceneManager;
	}

	public void sendPublicChatMessage(PublicChatMessage message) {
		for (Player p : queryNearbyPlayersByTileRange(16, p -> p.getLocalPlayerUpdate().getLocalPlayers()[getIndex()] != null))
			p.getPackets().sendPublicMessage(this, message);
	}

	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		completionistCapeCustomized = skillcapeCustomized;
	}

	public int[] getMaxedCapeCustomized() {
		return maxedCapeCustomized;
	}

	public void setMaxedCapeCustomized(int[] maxedCapeCustomized) {
		this.maxedCapeCustomized = maxedCapeCustomized;
	}

	public void setSkullId(int skullId) {
		this.skullId = skullId;
	}

	public int getSkullId() {
		return skullId;
	}

	public boolean isFilterGame() {
		return filterGame;
	}

	public void setFilterGame(boolean filterGame) {
		this.filterGame = filterGame;
	}

	public DominionTower getDominionTower() {
		return dominionTower;
	}

	public Trade getTrade() {
		return trade;
	}

	public void setProtectionPrayBlock(int ticks) {
		getTempAttribs().setL("protPrayBlock", World.getServerTicks() + ticks);
		if (ticks > 0)
			prayer.closePrayers(Prayer.PROTECT_MAGIC, Prayer.PROTECT_MELEE, Prayer.PROTECT_RANGE, Prayer.PROTECT_SUMMONING, Prayer.DEFLECT_MAGIC, Prayer.DEFLECT_MELEE, Prayer.DEFLECT_RANGE, Prayer.DEFLECT_SUMMONING);
	}

	public boolean isProtectionPrayBlocked() {
		return World.getServerTicks() < getTempAttribs().getL("protPrayBlock");
	}

	public Familiar getFamiliar() {
		return summFamiliar;
	}

	public Pouch getFamiliarPouch() {
		if (summFamiliar == null)
			return null;
		return summFamiliar.getPouch();
	}

	public void setFamiliar(Familiar familiar) {
		this.summFamiliar = familiar;
	}

	public int getSummoningLeftClickOption() {
		return summoningLeftClickOption;
	}

	public void setSummoningLeftClickOption(int summoningLeftClickOption) {
		this.summoningLeftClickOption = summoningLeftClickOption;
	}

	public AuraManager getAuraManager() {
		return auraManager;
	}

	public String getPronoun(String male, String female) {
		return (getAppearance().isMale() ? male : female);
	}

	public MoveType getMovementType() {
		if (getTemporaryMoveType() != null)
			return getTemporaryMoveType();
		return getRun() ? MoveType.RUN : MoveType.WALK;
	}

	public void setDisableEquip(boolean equip) {
		disableEquip = equip;
	}

	public boolean isEquipDisabled() {
		return disableEquip;
	}

	public int getPublicStatus() {
		return publicStatus;
	}

	public void setPublicStatus(int publicStatus) {
		this.publicStatus = publicStatus;
	}

	public int getClanStatus() {
		return clanStatus;
	}

	public void setClanStatus(int clanStatus) {
		this.clanStatus = clanStatus;
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public int getAssistStatus() {
		return assistStatus;
	}

	public void setAssistStatus(int assistStatus) {
		this.assistStatus = assistStatus;
	}

	public Notes getNotes() {
		return notes;
	}

	public boolean isCantTrade() {
		return cantTrade;
	}

	public void setCantTrade(boolean canTrade) {
		cantTrade = canTrade;
	}

	public String getYellColor() {
		return yellColor;
	}

	public void setYellColor(String yellColor) {
		this.yellColor = yellColor;
	}

	/**
	 * Gets the pet.
	 *
	 * @return The pet.
	 */
	public Pet getPet() {
		return pet;
	}

	/**
	 * Sets the pet.
	 *
	 * @param pet
	 *            The pet to set.
	 */
	public void setPet(Pet pet) {
		this.pet = pet;
	}

	/**
	 * Gets the petManager.
	 *
	 * @return The petManager.
	 */
	public PetManager getPetManager() {
		return petManager;
	}

	/**
	 * Sets the petManager.
	 *
	 * @param petManager
	 *            The petManager to set.
	 */
	public void setPetManager(PetManager petManager) {
		this.petManager = petManager;
	}

	public boolean isXpLocked() {
		return xpLocked;
	}

	public void setXpLocked(boolean locked) {
		xpLocked = locked;
	}

	public boolean isYellOff() {
		return yellOff;
	}

	public void setYellOff(boolean yellOff) {
		this.yellOff = yellOff;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public double getHpBoostMultiplier() {
		return hpBoostMultiplier;
	}

	public void setHpBoostMultiplier(double hpBoostMultiplier) {
		this.hpBoostMultiplier = hpBoostMultiplier;
	}

	public boolean hasLargeSceneView() {
		return largeSceneView;
	}

	public void setLargeSceneView(boolean largeSceneView) {
		this.largeSceneView = largeSceneView;
	}

	public double getRuneSpanPoints() {
		return runeSpanPoints;
	}

	public void setRuneSpanPoint(double runeSpanPoints) {
		this.runeSpanPoints = runeSpanPoints;
	}

	public void addRunespanPoints(double points) {
		runeSpanPoints += points;
	}

	public void removeRunespanPoints(double points) {
		runeSpanPoints -= points;
		if (runeSpanPoints < 0)
			runeSpanPoints = 0;
	}

	public DuelRules getLastDuelRules() {
		return lastDuelRules;
	}

	public void setLastDuelRules(DuelRules duelRules) {
		lastDuelRules = duelRules;
	}

	public int getStarter() {
		return starter;
	}

	public void setStarter(int starter) {
		this.starter = starter;
	}

	public void ladder(final Tile toTile) {
		setNextAnimation(new Animation(828));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextTile(toTile);
			}
		}, 1);
	}

	public void giveStarter() {
		getBank().clear();
		getEquipment().reset();
		getInventory().reset();
		setStarter(1);
		getBank().addItem(new Item(995, 25), false);
		for (Item item : Settings.getConfig().getStartItems())
			getInventory().addItem(item);
		sendMessage("Welcome to " + Settings.getConfig().getServerName() + ".");
		if (!Settings.getConfig().getLoginMessage().isEmpty())
			sendMessage(Settings.getConfig().getLoginMessage());
		getAppearance().generateAppearanceData();
	}

	public boolean hasFamiliar() {
		return summFamiliar != null;
	}

	public double getBonusXpRate() {
		return bonusXpRate;
	}

	public void setBonusXpRate(double bonusXpRate) {
		this.bonusXpRate = bonusXpRate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void clearTitle() {
		setTitleAfter(false);
		setTitle(null);
		setTitleColor(null);
		setTitleShading(null);
		getAppearance().setTitle(0);
		getAppearance().generateAppearanceData();
	}

	public void clearCustomTitle() {
		setTitleAfter(false);
		setTitle(null);
		setTitleColor(null);
		setTitleShading(null);
	}

	public String getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(String titleColor) {
		this.titleColor = titleColor;
	}

	public String getTitleShading() {
		return titleShading;
	}

	public void setTitleShading(String titleShading) {
		this.titleShading = titleShading;
	}

	public boolean isTitleAfter() {
		return titleAfter;
	}

	public void setTitleAfter(boolean titleAfter) {
		this.titleAfter = titleAfter;
	}

	public String getLastNpcInteractedName() {
		return lastNpcInteractedName;
	}

	public void setLastNpcInteractedName(String lastNpcInteractedName) {
		this.lastNpcInteractedName = lastNpcInteractedName;
	}

	public boolean checkCompRequirements(boolean trimmed) {
		if (!getSkills().isMaxed(true))
			return false;
		if (getCounterValue("Fight Kiln clears") <= 0 && !Settings.getConfig().isDebug()) {
			sendMessage("You need to have completed the fight kiln at least once.");
			return false;
		}
		if (getNumberKilled("Queen Black Dragon") > 0 && !Settings.getConfig().isDebug()) {
			sendMessage("You need to have killed the Queen black dragon at least once.");
			return false;
		}
		if (getDominionTower().getKilledBossesCount() < 500 && !Settings.getConfig().isDebug()) {
			sendMessage("You need to have killed 500 bosses in the dominion tower.");
			return false;
		}
		if (trimmed) {

		}
		return true;
	}

	public void refreshForinthry() {
		addEffect(Effect.REV_IMMUNE, 100);
		addEffect(Effect.REV_AGGRO_IMMUNE, 6000);
		sendMessage("<col=FF0000>You will not be harmed by revenants for 1 minute.");
		sendMessage("<col=FF0000>Revenants will have no aggression towards you for one hour.");
	}

	public long getTicksSinceLastAction() {
		return recorder.getTicksSinceLastAction();
	}

	public void sendGodwarsKill(NPC npc) {
		boolean dropKey = false;
		if (Utils.getRandomInclusive(500) <= 10 && npc.getDefinitions().combatLevel <= 134)
			dropKey = true;
		if (npc.getId() >= 6247 && npc.getId() <= 6259) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.SARADOMIN);
			if (dropKey)
				World.addGroundItem(new Item(20124, 1), Tile.of(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), this, false, 60);
			return;
		}
		if (npc.getId() >= 6260 && npc.getId() <= 6283) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.BANDOS);
			if (dropKey)
				World.addGroundItem(new Item(20122, 1), Tile.of(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), this, false, 60);
			return;
		}
		if (npc.getId() >= 6222 && npc.getId() <= 6246) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.ARMADYL);
			if (dropKey)
				World.addGroundItem(new Item(20121, 1), Tile.of(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), this, false, 60);
			return;
		}
		if (npc.getId() >= 6203 && npc.getId() <= 6221) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.ZAMORAK);
			if (dropKey)
				World.addGroundItem(new Item(20123, 1), Tile.of(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), this, false, 60);
			return;
		}
		if (npc.getId() >= 13447 && npc.getId() <= 13459) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.ZAROS);
			return;
		}
	}

	public void openBook(Book book) {
		book.open(this);
	}

	public void useLadder(Tile tile) {
		useLadder(828, tile);
	}

	public void updateSlayerTask() {
		if (getSlayer().getTask() != null)
			getVars().setVar(394, getSlayer().getTask().getMonster().getEnumId());
		else
			getVars().setVar(394, 0);
	}

	public void useLadder(int anim, final Tile tile) {
		lock();
		setNextAnimation(new Animation(anim));
		WorldTasks.scheduleTimer(tick -> {
			if (tick == 1)
				setNextTile(tile);
			if (tick == 2) {
				unlock();
				return false;
			}
			return true;
		});
	}

	public void sendMessage(String mes, boolean canBeFiltered) {
		getPackets().sendGameMessage(mes, canBeFiltered);
	}

	public void sendMessage(String mes) {
		sendMessage(mes, false);
	}

	public void sendMessage(MessageType type, String message) {
		getPackets().sendMessage(type, message, null);
	}

	public void sendMessage(String... mes) {
		String text = "";
		for (String str : mes)
			text += str + "<br>";
		getPackets().sendGameMessage(text);
	}

	public Date getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	public QuestManager getQuestManager() {
		return questManager;
	}

	public MiniquestManager getMiniquestManager() {
		return miniquestManager;
	}

	public boolean isSlayerHelmCreation() {
		return slayerHelmCreation;
	}

	public void setSlayerHelmCreation(boolean slayerHelmCreation) {
		this.slayerHelmCreation = slayerHelmCreation;
	}

	public boolean hasLearnedQuickBlows() {
		return quickBlows;
	}

	public void setHasLearnedQuickBlows(boolean slayerHelmCreation) {
		quickBlows = slayerHelmCreation;
	}

	public boolean hasYellTitle() {
		if (getYellTitle() == null || getYellTitle().isEmpty())
			return false;
		return true;
	}

	public String getYellTitle() {
		return yellTitle;
	}

	public void setYellTitle(String yellTitle) {
		this.yellTitle = yellTitle;
	}

	public int getGraveStone() {
		return graveStone;
	}

	public void setGraveStone(int graveStone) {
		this.graveStone = graveStone;
	}

	public boolean isIronMan() {
		return ironMan;
	}

	public Map<String, Object> getDailyAttributes() {
		if (dailyAttributes == null)
			dailyAttributes = new ConcurrentHashMap<>();
		return dailyAttributes;
	}

	public void setDailyI(String name, int value) {
		getDailyAttributes().put("dailyI"+name, value);
	}

	public void setDailyB(String name, boolean value) {
		getDailyAttributes().put("dailyB"+name, value);
	}

	public void setDailyL(String name, long value) {
		getDailyAttributes().put("dailyL"+name, value);
	}

	public boolean getDailyB(String name) {
		if (getDailyAttributes().get("dailyB"+name) == null)
			return false;
		return (Boolean) getDailyAttributes().get("dailyB"+name);
	}

	public int getDailySubI(String name, int sub) {
		return sub - getDailyI(name);
	}

	public int getDailyI(String name, int def) {
		if (getDailyAttributes().get("dailyI"+name) == null)
			return def;
		if (getDailyAttributes().get("dailyI"+name) != null && getDailyAttributes().get("dailyI"+name) instanceof Integer)
			return (int) getDailyAttributes().get("dailyI"+name);
		return (int) Math.floor(((double) getDailyAttributes().get("dailyI"+name)));
	}

	public int getDailyI(String name) {
		return getDailyI(name, 0);
	}

	public long getDailyL(String name) {
		if (getDailyAttributes().get("dailyL"+name) == null)
			return 0;
		if (getDailyAttributes().get("dailyI"+name) != null && getDailyAttributes().get("dailyI"+name) instanceof Long)
			return (long) getDailyAttributes().get("dailyI"+name);
		return (long) Math.floor(((double) getDailyAttributes().get("dailyI"+name)));
	}

	public void incDailyI(String name) {
		int newVal = getDailyI(name) + 1;
		setDailyI(name, newVal);
	}

	public void setIronMan(boolean ironMan) {
		this.ironMan = ironMan;
		if (ironMan) {
			setTitle("Ironman");
			setTitleColor("FF0000");
			setTitleShading("000000");
		}
	}

	public void applyAccountTitle() {
		if (ironMan) {
			setTitle("Ironman");
			setTitleColor("FF0000");
			setTitleShading("000000");
		}
		getAppearance().generateAppearanceData();
	}

	public boolean isChosenAccountType() {
		return chosenAccountType;
	}

	public void setChosenAccountType(boolean chosenAccountType) {
		this.chosenAccountType = chosenAccountType;
	}

	public boolean getUsedOmniTalisman() {
		return hasUsedOmniTalisman;
	}

	public boolean getUsedElementalTalisman() {
		return hasUsedElementalTalisman;
	}

	public void setUsedOmniTalisman(boolean bool) {
		hasUsedOmniTalisman = bool;
	}

	public void setUsedElementalTalisman(boolean bool) {
		hasUsedElementalTalisman = bool;
	}

	public boolean[] getWickedHoodTalismans() {
		return wickedHoodTalismans;
	}

	public void setWickedHoodTalismans(boolean wickedHoodTalismans[]) {
		this.wickedHoodTalismans = wickedHoodTalismans;
	}

	public int getLastDate() {
		return lastDate;
	}

	public void setLastDate(int lastDate) {
		this.lastDate = lastDate;
	}

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

	public boolean hasBossTask() {
		return bossTask != null;
	}

	public BossTask getBossTask() {
		return bossTask;
	}

	public void setBossTask(BossTask bossTask) {
		this.bossTask = bossTask;
	}

	public void simpleDialogue(String... message) {
		startConversation(new Dialogue().addSimple(message));
	}

	public void npcDialogue(int npcId, HeadE emote, String message) {
		startConversation(new Dialogue().addNPC(npcId, emote, message));
	}

	public void npcDialogue(NPC npc, HeadE emote, String message) {
		startConversation(new Dialogue().addNPC(npc, emote, message));
	}

	public void itemDialogue(int itemId, String message) {
		startConversation(new Dialogue().addItem(itemId, message));
	}

	public void playerDialogue(HeadE emote, String message) {
		startConversation(new Dialogue().addPlayer(emote, message));
	}

	public ArrayList<Player> getNearbyFCMembers(NPC npc) {
		ArrayList<Player> eligible = new ArrayList<>();
		FCData fc = FCManager.getFCData(getSocial().getCurrentFriendsChat());
		if (fc == null) {
			sendMessage("Error loading active friend's chat. Could not lootshare.");
			eligible.add(this);
			return eligible;
		}
		for (Player player : World.getPlayersInChunkRange(npc.getChunkId(), 4)) {
			if (!player.isRunning() || !player.isLootSharing() || !fc.getUsernames().contains(player.getUsername()))
				continue;
			if (fc.getRank(player.getAccount()).ordinal() >= fc.getSettings().getRankToLS().ordinal()) //TODO friend rank may need to be coded differently?
				eligible.add(player);
		}
		return eligible;
	}

	public boolean isLootSharing() {
		return getNSV().getB("lootShare");
	}

	public void refreshLootShare() {
		if (isLootSharing())
			getVars().setVarBit(4071, 1);
		else
			getVars().setVarBit(4071, 0);
		getVars().setVarBit(4072, 0);
	}

	public void toggleLootShare() {
		if (getAccount().getSocial().getCurrentFriendsChat() != null) {
			if (isLootSharing())
				getNSV().removeB("lootShare");
			else
				getNSV().setB("lootShare", true);
			if (isLootSharing())
				sendMessage("You are now lootsharing.");
			else
				sendMessage("You are no longer lootsharing.");
			refreshLootShare();
		} else {
			sendMessage("You aren't currently in a friends chat.");
			getVars().setVarBit(4072, 0);
		}
	}

	public void fadeScreen(Runnable runnable) {
		FadingScreen.fade(this, runnable);
	}

	public void addIP(String ip) {
		if (ipAddresses == null)
			ipAddresses = new ArrayList<>();
		if (!ipAddresses.contains(ip))
			ipAddresses.add(ip);
	}

	public ArrayList<String> getIpAddresses() {
		if (ipAddresses == null)
			ipAddresses = new ArrayList<>();
		return ipAddresses;
	}

	public void walkToAndExecute(Tile startTile, Runnable event) {
		Route route = RouteFinder.find(getX(), getY(), getPlane(), getSize(), new FixedTileStrategy(startTile.getX(), startTile.getY()), true);
		int last = -1;
		if (route.getStepCount() == -1)
			return;
		for (int i = route.getStepCount() - 1; i >= 0; i--)
			if (!addWalkSteps(route.getBufferX()[i], route.getBufferY()[i], 25, true, true))
				break;
		if (last != -1) {
			Tile tile = Tile.of(route.getBufferX()[last], route.getBufferY()[last], getPlane());
			getSession().writeToQueue(new MinimapFlag(tile.getXInScene(getSceneBaseChunkId()), tile.getYInScene(getSceneBaseChunkId())));
		} else
			getSession().writeToQueue(new MinimapFlag());
		setRouteEvent(new RouteEvent(startTile, event));
	}

	public String getFormattedTitle() {
		if (title == null && appearence.getTitle() == 0)
			return null;
		if (title == null)
			return EnumDefinitions.getEnum(appearence.isMale() ? 1093 : 3872).getStringValue(appearence.getTitle());
		String formTitle = title;
		if (getTitleShading() != null)
			formTitle = "<shad=" + getTitleShading() + ">" + formTitle;
		if (getTitleColor() != null)
			formTitle = "<col=" + getTitleColor() + ">" + formTitle;
		formTitle = formTitle + "</col></shad> ";
		return formTitle;
	}

	public boolean containsItem(int id) {
		return getInventory().containsItem(id, 1) || getEquipment().containsOneItem(id) || getBank().containsItem(id, 1);
	}

	public boolean containsItems(int... ids) {
		for (int id : ids)
			if (containsItem(id))
				return true;
		return false;
	}

	public void startConversation(com.rs.engine.dialogue.Dialogue dialogue) {
		startConversation(new Conversation(dialogue.finish()));
	}

	public boolean startConversation(Conversation conversation) {
		if (conversation.getCurrent() == null)
			return false;
		conversation.setPlayer(this);
		if (!conversation.isCreated())
			conversation.create();
		this.conversation = conversation;
		conversation.start();
		return true;
	}

	public void endConversation() {
		conversation = null;
		if (getInterfaceManager().containsChatBoxInter())
			getInterfaceManager().closeChatBoxInterface();
		if (finishConversationEvent != null) {
			Runnable event = finishConversationEvent;
			finishConversationEvent = null;
			event.run();
		}
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void sm(String string) {
		sendMessage(string);
	}

	@Override
	public boolean canMove(Direction dir) {
		if (!getControllerManager().canMove(dir))
			return false;
		if (tileMan) {
			if (tilesUnlocked == null) {
				tilesUnlocked = new HashSet<>();
				tilesAvailable = 50;
			}
			int tileHash = getTile().transform(dir.getDx(), dir.getDy()).getTileHash();
			if (!tilesUnlocked.contains(tileHash)) {
				if (tilesAvailable <= 0)
					return false;
				tilesAvailable--;
				tilesUnlocked.add(tileHash);
				markTile(Tile.of(tileHash));
			}
		}
		return true;
	}

	public void markTile(Tile tile) {
		getPackets().sendAddObject(new GameObject(21777, ObjectType.GROUND_DECORATION, 0, tile));
		//model 4162 = orange square
		//model 2636 = white dot?

	}

	public void updateTilemanTiles() {
		for (int i : tilesUnlocked) {
			Tile tile = Tile.of(i);
			if (Utils.getDistance(getTile(), tile) < 64)
				markTile(tile);
		}
	}

	@Override
	public int hashCode() {
		return uuid;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Player p)
			return p.hashCode() == hashCode();
		return false;
	}

	public double getWeight() {
		return inventory.getInventoryWeight() + equipment.getEquipmentWeight();
	}

	public boolean hasWickedHoodTalisman(WickedHoodRune rune) {
		return wickedHoodTalismans[rune.ordinal()];
	}

	public ReflectionAnalysis getReflectionAnalysis(int id) {
		return reflectionAnalyses.get(id);
	}

	public void queueReflectionAnalysis(ReflectionAnalysis reflectionCheck) {
		if (!reflectionCheck.isBuilt())
			throw new RuntimeException("Cannot queue an unbuilt reflection analysis.");
		reflectionAnalyses.put(reflectionCheck.getId(), reflectionCheck);
		getSession().writeToQueue(new ReflectionCheckRequest(reflectionCheck.getChecks()));
	}

	public void unlockWickedHoodRune(WickedHoodRune rune) {
		wickedHoodTalismans[rune.ordinal()] = true;
	}

	public boolean iceStrykeNoCape() {
		return iceStrykeNoCape;
	}

	public boolean aquanitesUnlocked() {
		return aquanitesUnlocked;
	}

	public void setIceStrykeNoCape(boolean unlocked) {
		iceStrykeNoCape = unlocked;
	}

	public void setAquanitesUnlocked(boolean unlocked) {
		aquanitesUnlocked = unlocked;
	}

	public boolean isBroadFletching() {
		return broadFletching;
	}

	public boolean hasCraftROS() {
		return craftROS;
	}

	public void setBroadFletching(boolean broadFletching) {
		this.broadFletching = broadFletching;
	}

	public void setCraftROS(boolean canCraftROS) {
		craftROS = canCraftROS;
	}

	public int getJadinkoFavor() {
		return jadinkoFavor;
	}

	public Map<Tools, Integer> getToolbelt() {
		return toolbelt;
	}

	public void processPackets() {
		Packet packet;
		while ((packet = session.getPacketQueue().poll()) != null) {
			//Logger.trace(Player.class, "processPackets", "Packet processed: " + packet.getOpcode());
			if (hasStarted() && packet.getOpcode() != ClientPacket.IF_CONTINUE && packet.getOpcode() != ClientPacket.IF_OP1 && !isChosenAccountType())
				continue;
			PacketHandler<Player, Packet> handler = PacketHandlers.getHandler(packet.getOpcode());
			if (handler != null)
				handler.handle(this, packet);
		}
	}

	public Item getItemWithPlayer(int id) {
		Item item = getItemFromInv(id);
		if (item == null)
			item = getItemFromEquip(id);
		return item;
	}

	public Item getItemFromEquip(int id) {
		return getEquipment().getItemById(id);
	}

	public Item getItemFromInv(int id) {
		return getInventory().getItemById(id);
	}

	public void clearToolbelt() {
		toolbelt.clear();
	}

	public boolean addToolbelt(int itemId) {
		if (toolbelt == null)
			toolbelt = new HashMap<>();
		Tools tool = Tools.forId(itemId);
		if (tool == null)
			return false;
		if (toolbelt.get(tool) != null && toolbelt.get(tool) <= tool.getValue(itemId)) {
			sendMessage("You already have this tool on your belt.");
			return false;
		}
		toolbelt.put(tool, tool.getValue(itemId));
		sendMessage("You add the " + ItemDefinitions.getDefs(itemId).name + " to your toolbelt.");
		return true;
	}

	public boolean containsTool(int itemId) {
		if (toolbelt == null)
			return false;
		Tools tool = Tools.forId(itemId);
		if (tool == null)
			return false;
		if (tool.contains(getToolValue(tool), itemId))
			return true;
		return false;
	}

	public int getToolValue(Tools tool) {
		if ((toolbelt == null) || (toolbelt.get(tool) == null))
			return 0;
		return toolbelt.get(tool);
	}

	public void setJadinkoFavor(int jadinkoFavor) {
		this.jadinkoFavor = jadinkoFavor;
	}

	public void addJadinkoFavor(int amount) {
		jadinkoFavor += amount;
		if (jadinkoFavor > 2000)
			jadinkoFavor = 2000;
	}

	public void removeJadinkoFavor(int amount) {
		jadinkoFavor -= amount;
		if (jadinkoFavor < 0)
			jadinkoFavor = 0;
	}

	public boolean isOnTask(TaskMonster monster) {
		if (getSlayer().getTask() != null && getSlayer().getTask().getMonster() == monster)
			return true;
		return false;
	}

	public void addWalkSteps(Tile toTile, int maxSteps, boolean clip) {
		addWalkSteps(toTile.getX(), toTile.getY(), maxSteps, clip);
	}

	public void passThrough(Tile tile) {
		final boolean running = getRun();
		setRunHidden(false);
		lock(5);
		addWalkSteps(tile.getX(), tile.getY(), 3, false);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				setRunHidden(running);
				unlock();
				stop();
			}
		}, 3);
	}

	public int[] getWarriorPoints() {
		if (warriorPoints == null || warriorPoints.length != 6)
			warriorPoints = new int[6];
		return warriorPoints;
	}

	public void setWarriorPoints(int index, int pointsDifference) {
		if (warriorPoints == null || warriorPoints.length != 6)
			warriorPoints = new int[6];

		warriorPoints[index] += pointsDifference;
		if (warriorPoints[index] < 0) {
			Controller controller = getControllerManager().getController();
			if (controller == null || !(controller instanceof WarriorsGuild guild))
				return;
			guild.inCyclopse = false;
			setNextTile(WarriorsGuild.CYCLOPS_LOBBY);
			warriorPoints[index] = 0;
		} else if (warriorPoints[index] > 65535)
			warriorPoints[index] = 65535;
		refreshWarriorPoints(index);
	}

	public void refreshWarriorPoints(int index) {
		if (warriorPoints == null || warriorPoints.length != 6)
			warriorPoints = new int[6];

		getVars().setVarBit(index + 8662, warriorPoints[index]);
	}

	public Brewery getKeldagrimBrewery() {
		if (keldagrimBrewery == null)
			keldagrimBrewery = new Brewery(true);
		keldagrimBrewery.setPlayer(this);
		return keldagrimBrewery;
	}

	public Brewery getPhasmatysBrewery() {
		if (phasmatysBrewery == null)
			phasmatysBrewery = new Brewery(false);
		phasmatysBrewery.setPlayer(this);
		return phasmatysBrewery;
	}

	private int easter20Stage = 0;

	public int getEaster20Stage() {
		return easter20Stage;
	}

	public void setEaster20Stage(int easter20Stage) {
		this.easter20Stage = easter20Stage;
	}

	private int christ19Stage = 0;
	private Location christ19Loc = null;

	public Location getChrist19Loc() {
		return christ19Loc;
	}

	public void setChrist19Loc(Location christ19Loc) {
		this.christ19Loc = christ19Loc;
	}

	public int getChrist19Stage() {
		return christ19Stage;
	}

	public void setChrist19Stage(int christ19Stage) {
		this.christ19Stage = christ19Stage;
	}

	public int getHw07Stage() {
		return hw07Stage;
	}

	public void setHw07Stage(int hw07Stage) {
		this.hw07Stage = hw07Stage;
	}

	public boolean isRunBlocked() {
		return runBlocked;
	}

	public void blockRun() {
		runBlocked = true;
	}

	public void unblockRun() {
		runBlocked = false;
	}

	public Set<Integer> getDiangoReclaim() {
		if (diangoReclaim == null)
			diangoReclaim = new HashSet<>();
		return diangoReclaim;
	}

	public void addDiangoReclaimItem(int itemId) {
		if (diangoReclaim == null)
			diangoReclaim = new HashSet<>();
		diangoReclaim.add(itemId);
	}

	public int getPlacedCannon() {
		return placedCannon;
	}

	public void setPlacedCannon(int placedCannon) {
		this.placedCannon = placedCannon;
	}

	public HabitatFeature getHabitatFeature() {
		return habitatFeature;
	}

	public Clan getClan() {
		return ClansManager.getClan(getAccount().getSocial().getClanName());
	}

	public void getClan(Consumer<Clan> cb) {
		ClansManager.getClan(getAccount().getSocial().getClanName(), cb);
	}

	public Clan getGuestClan() {
		return ClansManager.getClan(getAccount().getSocial().getGuestedClanChat());
	}

	public void getGuestClan(Consumer<Clan> cb) {
		ClansManager.getClan(getAccount().getSocial().getGuestedClanChat(), cb);
	}

	public void setHabitatFeature(HabitatFeature habitatFeature) {
		getVars().setVarBit(8354, habitatFeature == null ? 0 : habitatFeature.val);
		this.habitatFeature = habitatFeature;
	}

	public boolean hasRights(Rights rights) {
		return getAccount().hasRights(rights);
	}

	public Rights getRights() {
		return getAccount().getRights();
	}

	public void setRights(Rights staffRights) {
		getAccount().setRights(staffRights);
		LobbyCommunicator.updateRights(this);
	}

	public Map<PatchLocation, FarmPatch> getPatches() {
		if (patches == null)
			patches = new HashMap<>();
		return patches;
	}

	public boolean isGrowing(PatchLocation loc, ProduceType produce) {
		FarmPatch patch = getPatch(loc);
		if (patch == null)
			return produce == null;
		if (produce != null)
			return patch.seed != null && patch.seed == produce;
		return patch.seed == null;
	}

	public FarmPatch getPatch(PatchLocation loc) {
		return getPatches().get(loc);
	}

	public void putPatch(FarmPatch patch) {
		getPatches().put(patch.location, patch);
	}

	public void clearPatch(PatchLocation loc) {
		getPatches().remove(loc);
	}

	public long getTimePlayed() {
		return timePlayed;
	}

	public Map<StorableItem, Item> getLeprechaunStorage() {
		if (leprechaunStorage == null)
			leprechaunStorage = new HashMap<>();
		return leprechaunStorage;
	}

	public int getNumInLeprechaun(StorableItem item) {
		return leprechaunStorage.get(item) == null ? 0 : leprechaunStorage.get(item).getAmount();
	}

	public void storeLeprechaunItem(StorableItem item, int itemId, int amount) {
		Item curr = leprechaunStorage.get(item);
		if (curr == null) {
			if (amount > item.maxAmount)
				amount = item.maxAmount;
			if (amount > getInventory().getNumberOf(itemId))
				amount = getInventory().getNumberOf(itemId);
			if (amount <= 0)
				return;
			curr = new Item(itemId, amount);
			getInventory().deleteItem(itemId, amount);
		} else {
			if ((curr.getAmount()+amount) > item.maxAmount)
				amount = item.maxAmount - curr.getAmount();
			if (amount > getInventory().getNumberOf(itemId))
				amount = getInventory().getNumberOf(itemId);
			if (amount <= 0)
				return;
			curr.setAmount(curr.getAmount() + amount);
			getInventory().deleteItem(itemId, amount);
		}
		leprechaunStorage.put(item, curr);
		item.updateVars(this);
	}

	public void takeLeprechaunItem(StorableItem item, int amount) {
		Item curr = leprechaunStorage.get(item);
		if (curr == null)
			return;
		if (amount > curr.getAmount())
			amount = curr.getAmount();
		if (amount > getInventory().getFreeSlots())
			amount = getInventory().getFreeSlots();
		curr.setAmount(curr.getAmount() - amount);
		getInventory().addItem(curr.getId(), amount);
		if (curr.getAmount() == 0)
			leprechaunStorage.remove(item);
		else
			leprechaunStorage.put(item, curr);
		item.updateVars(this);
	}

	public int getUuid() {
		return getUsername().hashCode();
	}

	public Social getSocial() {
		return getAccount().getSocial();
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Map<Integer, Offer> getGEOffers() {
		if (geOffers == null)
			geOffers = new HashMap<>();
		return geOffers;
	}

	public void setGEOffers(List<Offer> offers) {
		for (Offer offer : offers)
			geOffers.put(offer.getBox(), offer);
	}

	public int getLsp() {
		return lsp;
	}

	public void setLsp(int lsp) {
		this.lsp = lsp;
	}

	public void promptSetYellColor() {
		sendInputName("Enter yell color in HEX: (ex: 00FF00 for green)", color -> {
			if (color.length() != 6)
				startConversation(new Dialogue().addSimple("The HEX yell color you wanted to pick cannot be longer and shorter then 6."));
			else if (Utils.containsInvalidCharacter(color.toLowerCase()) || color.contains("_"))
				startConversation(new Dialogue().addSimple("The requested yell color can only contain numeric and regular characters."));
			else {
				setYellColor(color);
				startConversation(new Dialogue().addSimple("Your yell color has been changed to <col=" + getYellColor() + ">" + getYellColor() + "</col>."));
			}
		});
	}

	public void promptSetYellTitle() {
		sendInputName("Enter yell title:", title -> {
			if (title.length() > 20)
				startConversation(new Dialogue().addSimple("Your yell title cannot be longer than 20 characters."));
			else if (Utils.containsBadCharacter(title))
				startConversation(new Dialogue().addSimple("Keep your title as only letters and numbers.."));
			else {
				setYellTitle(title);
				getAppearance().generateAppearanceData();
				startConversation(new Dialogue().addSimple("Your yell title has been changed to " + getYellTitle() + "."));
			}
		});
	}

	public void promptSetTitle() {
		sendInputName("Enter title:", title -> {
			if (title.length() > 20)
				startConversation(new Dialogue().addSimple("Your title cannot be longer than 20 characters."));
			else if (Utils.containsBadCharacter(title))
				startConversation(new Dialogue().addSimple("Keep your title as only letters and numbers.."));
			else if (title.toLowerCase().contains("ironman") || title.toLowerCase().contains("hard"))
				startConversation(new Dialogue().addSimple("That title is reserved for special account types."));
			else {
				setTitle(title);
				getAppearance().generateAppearanceData();
				startConversation(new Dialogue().addSimple("Your title has been changed to " + getTitle() + "."));
			}
		});
	}

	public void promptSetTitleColor() {
		sendInputName("Enter title color in HEX: (ex: 00FF00 for green)", color -> {
			if (color.length() != 6)
				startConversation(new Dialogue().addSimple("HEX colors are 6 characters long bud."));
			else if (Utils.containsInvalidCharacter(color.toLowerCase()) || color.contains("_"))
				startConversation(new Dialogue().addSimple("HEX colors just contain letters and numbers bud."));
			else {
				setTitleColor(color);
				getAppearance().generateAppearanceData();
				startConversation(new Dialogue().addSimple("Your title has been changed to " + getTitle() + "."));
			}
		});
	}

	public void promptSetTitleShade() {
		sendInputName("Enter title shade color in HEX: (ex: 00FF00 for green)", color -> {
			if (color.length() != 6)
				startConversation(new Dialogue().addSimple("HEX colors are 6 characters long bud."));
			else if (Utils.containsInvalidCharacter(color.toLowerCase()) || color.contains("_"))
				startConversation(new Dialogue().addSimple("HEX colors just contain letters and numbers bud."));
			else {
				setTitleShading(color);
				getAppearance().generateAppearanceData();
				startConversation(new Dialogue().addSimple("Your title has been changed to " + getTitle() + "."));
			}
		});
	}

	public void simpleDialogue(String message) {
		startConversation(new com.rs.engine.dialogue.Dialogue(new SimpleStatement(message)));
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getInvisibleSkillBoost(int skill) {
		int boost = 0;

		if (Arrays.stream(Skills.SKILLING).anyMatch(check -> check == skill) && hasEffect(Effect.DUNG_HS_SCROLL_BOOST))
			boost += getTempAttribs().getI("hsDungScrollTier", 0);

		switch(skill) {
		case Skills.WOODCUTTING:
			if (getFamiliarPouch() == Pouch.BEAVER)
				boost += 2;
			break;
		case Skills.MINING:
			if (getFamiliarPouch() == Pouch.DESERT_WYRM)
				boost += 1;
			else if (getFamiliarPouch() == Pouch.VOID_RAVAGER)
				boost += 1;
			else if (getFamiliarPouch() == Pouch.OBSIDIAN_GOLEM)
				boost += 7;
			else if (getFamiliarPouch() == Pouch.LAVA_TITAN)
				boost += 10;
			break;
		case Skills.FISHING:
			if (getFamiliarPouch() == Pouch.GRANITE_CRAB)
				boost += 1;
			else if (getFamiliarPouch() == Pouch.IBIS)
				boost += 3;
			else if (getFamiliarPouch() == Pouch.GRANITE_LOBSTER)
				boost += 4;
			break;
		case Skills.FIREMAKING:
			if (getFamiliarPouch() == Pouch.PYRELORD)
				boost += 3;
			else if (getFamiliarPouch() == Pouch.LAVA_TITAN)
				boost += 10;
			else if (getFamiliarPouch() == Pouch.PHOENIX)
				boost += 12;
			break;
		case Skills.HUNTER:
			if (getFamiliarPouch() == Pouch.SPIRIT_GRAAHK || getFamiliarPouch() == Pouch.SPIRIT_LARUPIA || getFamiliarPouch() == Pouch.SPIRIT_KYATT)
				boost += 5;
			else if (getFamiliarPouch() == Pouch.WOLPERTINGER)
				boost += 5;
			else if (getFamiliarPouch() == Pouch.ARCTIC_BEAR)
				boost += 7;
			break;
		}

		return boost;
	}

	public boolean isTileMan() {
		return tileMan;
	}

	public void setTileMan(boolean tileMan) {
		this.tileMan = tileMan;
	}

	public int getPvpCombatLevelThreshhold() {
		return pvpCombatLevelThreshhold;
	}

	public void setPvpCombatLevelThreshhold(int pvpCombatLevelThreshhold) {
		this.pvpCombatLevelThreshhold = pvpCombatLevelThreshhold;
		getAppearance().generateAppearanceData();
	}

	public Sound playSound(Sound sound) {
		if (sound.getId() == -1)
			return null;
		sounds.add(sound);
		return sound;
	}

	private Sound playSound(int soundId, int delay, SoundType type) {
		return playSound(new Sound(soundId, delay, type));
	}

	public void jingle(int jingleId, int delay) {
		playSound(jingleId, delay, SoundType.JINGLE);
	}

	public void jingle(int jingleId) {
		playSound(jingleId, 0, SoundType.JINGLE);
	}

	public void musicTrack(int trackId, int delay, int volume) {
		playSound(trackId, delay, SoundType.MUSIC).volume(volume);
	}

	public void musicTrack(int trackId, int delay) {
		playSound(trackId, delay, SoundType.MUSIC);
	}

	public void musicTrack(int trackId) {
		musicTrack(trackId, 100);
	}

	public void soundEffect(int soundId, int delay) {
		playSound(soundId, delay, SoundType.EFFECT);
	}

	public void soundEffect(int soundId) {
		soundEffect(soundId, 0);
	}

	public void voiceEffect(int voiceId, int delay) {
		playSound(voiceId, delay, SoundType.VOICE);
	}

	public void voiceEffect(int voiceId) {
		voiceEffect(voiceId, 0);
	}

	public Map<Integer, MachineInformation> getMachineMap() {
		return machineMap;
	}

	public MachineInformation getMachineInfo() {
		return machineInformation;
	}

	private void checkWasInDynamicRegion() {
		if (instancedArea != null) {
			Instance prevInstance = Instance.get(instancedArea.getId());
			if (prevInstance != null && prevInstance.isPersistent()) {
				prevInstance.teleportTo(this);
				setForceNextMapLoadRefresh(true);
				return;
			}
			setNextTile(instancedArea.getReturnTo());
			instancedArea = null;
		}
	}

	public Recorder getRecorder() {
		return recorder;
	}

	public boolean isQuestComplete(Quest quest, String actionString) {
		return getQuestManager().isComplete(quest, actionString);
	}

	public boolean isQuestComplete(Quest quest) {
		return isQuestComplete(quest, null);
	}

	public boolean isMiniquestComplete(Miniquest quest, String actionString) {
		return getMiniquestManager().isComplete(quest, actionString);
	}

	public boolean isMiniquestComplete(Miniquest quest) {
		return isMiniquestComplete(quest, null);
	}

	public void delayLock(int ticks, Runnable task) {
		lock();
		WorldTasks.delay(ticks, task);
		WorldTasks.delay(ticks+1, () -> unlock());
	}

	public void playPacketCutscene(int id, Runnable onFinish) {
		getPackets().sendCutscene(id);
		onPacketCutsceneFinish = onFinish;
	}
	public void playCutscene(Consumer<Cutscene> constructor) {
		getCutsceneManager().play(new Cutscene() {
			@Override
			public void construct(Player player) {
				constructor.accept(this);
			}
		});
	}
    public void playCutscene(Cutscene scene) {
		getCutsceneManager().play(scene);
    }

	@Override
	public void setBasNoReset(int bas) {
		super.setBasNoReset(bas);
		getAppearance().generateAppearanceData();
	}

	public void setInstancedArea(Instance instancedArea) {
		this.instancedArea = instancedArea;
	}

	public Instance getInstancedArea() {
		return instancedArea;
	}

	public Set<Integer> getMapChunksNeedInit() {
		return mapChunksNeedInit;
	}
}
