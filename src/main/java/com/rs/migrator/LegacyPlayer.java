package com.rs.migrator;

import com.rs.Settings;
import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.LoyaltyRewardDefinitions.Reward;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.pathing.Direction;
import com.rs.game.player.*;
import com.rs.game.player.actions.LodestoneAction.Lodestone;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.Notes;
import com.rs.game.player.content.Toolbelt.Tools;
import com.rs.game.player.content.combat.CombatDefinitions;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.holidayevents.christmas.christ19.Christmas2019.Location;
import com.rs.game.player.content.minigames.herblorehabitat.HabitatFeature;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.content.skills.construction.House;
import com.rs.game.player.content.skills.cooking.Brewery;
import com.rs.game.player.content.skills.dungeoneering.DungManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonRewards.HerbicideSetting;
import com.rs.game.player.content.skills.farming.FarmPatch;
import com.rs.game.player.content.skills.farming.PatchLocation;
import com.rs.game.player.content.skills.farming.ProduceType;
import com.rs.game.player.content.skills.farming.StorableItem;
import com.rs.game.player.content.skills.prayer.PrayerBooks;
import com.rs.game.player.content.skills.runecrafting.RunecraftingAltar.WickedHoodRune;
import com.rs.game.player.content.skills.slayer.BossTask;
import com.rs.game.player.content.skills.slayer.SlayerTaskManager;
import com.rs.game.player.content.skills.slayer.TaskMonster;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.managers.*;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestManager;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.VarManager;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.migrator.legacyge.OfferSet;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.InputIntegerEvent;
import com.rs.plugin.events.InputStringEvent;
import com.rs.utils.Ticks;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LegacyPlayer extends Entity {

	public static final int TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1, RUN_MOVE_TYPE = 2;

	private Date dateJoined;
	private Map<String, Object> dailyAttributes;
	private long timePlayed = 0;
	private long timeLoggedOut;

	private long docileTimer;

	private OfferSet offerSet;

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

	public short x;
	public short y;
	public byte plane;

	private VarManager varManager;

	private boolean chosenAccountType;
	private boolean ironMan;

	private int jadinkoFavor;

	private long lastLoggedIn = 0;

	public int ringOfForgingCharges = 140;
	public int bindingNecklaceCharges = 15;

	private ConcurrentHashMap<String, Object> savingAttributes;

	private ConcurrentHashMap<String, Integer> npcKills;
	private ConcurrentHashMap<String, Integer> variousCounter;

	private HashSet<Reward> unlockedLoyaltyRewards;
	private HashSet<Reward> favoritedLoyaltyRewards;

	private HashMap<Tools, Integer> toolbelt;

	public int[] artisanOres = new int[5];
	public double artisanXp = 0.0;
	public int artisanRep = 0;

	public int loyaltyPoints = 0;

	private int temporaryMovementType;
	private boolean updateMovementType;

	private HabitatFeature habitatFeature;

	private String password;
	private String email;
	private Rights staffRights;
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
	private FriendsIgnores friendsIgnores;
	private DominionTower dominionTower;
	private Familiar familiar;
	private AuraManager auraManager;
	private PetManager petManager;
	private BossTask bossTask;
	private QuestManager questManager;
	private TreasureTrailsManager treasureTrailsManager;

	public int reaperPoints;

	private boolean wickedHoodTalismans[];
	private boolean hasUsedOmniTalisman;
	private boolean hasUsedElementalTalisman;

	public Set<HerbicideSetting> herbicideSettings = new HashSet<HerbicideSetting>();

	public ItemsContainer<Item> partyDeposit;
	private Map<StorableItem, Item> leprechaunStorage;

	private int graveStone;

	private int revenantImmune;
	private int revenantAggro;

	private boolean runBlocked;

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
	private boolean killedQueenBlackDragon;
	private double runeSpanPoints;
	private int crystalSeedRepairs;
	private int tinySeedRepairs;

	public WorldTile lastEssTele;

	private boolean[] prayerBook;

	private SlayerTaskManager slayer = new SlayerTaskManager();
	private TaskMonster[] taskBlocks;
	public int slayerPoints = 0;
	public int consecutiveTasks = 0;

	private int[] warriorPoints = new int[6];

	private Map<PatchLocation, FarmPatch> patches;

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
		for (int i = 0; i < 6; i++) {
			if (taskBlocks[i] == null) {
				taskBlocks[i] = task;
				return;
			}
		}
	}

	public boolean blockedTaskContains(TaskMonster task) {
		if (taskBlocks == null)
			taskBlocks = new TaskMonster[6];
		for (int i = 0; i < 6; i++) {
			if (taskBlocks[i] != null && taskBlocks[i] == task) {
				return true;
			}
		}
		return false;
	}

	public int getBlockedTaskNumber() {
		if (taskBlocks == null)
			taskBlocks = new TaskMonster[6];
		int num = 0;
		for (int i = 0; i < 6; i++) {
			if (taskBlocks[i] != null)
				num++;
		}
		return num;
	}

	public void unblockTask(int slot) {
		if (taskBlocks == null)
			taskBlocks = new TaskMonster[6];
		taskBlocks[slot] = null;
	}

	public void setSlayerPoints(int points) {
		this.slayerPoints = points;
	}

	public void addSlayerPoints(int points) {
		this.slayerPoints += points;
	}

	public void removeSlayerPoints(int points) {
		this.slayerPoints -= points;
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
	public long muted;
	public long banned;
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

	private String lastMsg;

	// Used for storing recent ips and password
	private ArrayList<String> passwordList = new ArrayList<String>();
	private ArrayList<String> ipList = new ArrayList<String>();

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

	private int[] clanCapeCustomized;
	private int[] clanCapeSymbols;

	private String clanName;
	private boolean connectedClanChannel;

	// completionistcape reqs
	private boolean completedFightCaves;
	private boolean completedFightKiln;
	private boolean wonFightPits;

	// crucible
	private boolean talkedWithMarv;
	private int crucibleHighScore;

	private String currentFriendChatOwner;
	private int summoningLeftClickOption;
	private List<String> ownedObjectsManagerKeys;

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
	public LegacyPlayer(String password) {
		super(Settings.getConfig().getPlayerStartTile());
		setHitpoints(100);
		this.dateJoined = new Date();
		this.password = password;
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
		friendsIgnores = new FriendsIgnores();
		dominionTower = new DominionTower();
		auraManager = new AuraManager();
		petManager = new PetManager();
		treasureTrailsManager = new TreasureTrailsManager();
		questManager = new QuestManager();
		dungManager = new DungManager(null);
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
		prayerBook = new boolean[PrayerBooks.BOOKS.length];
		herbicideSettings = new HashSet<HerbicideSetting>();
		ownedObjectsManagerKeys = new LinkedList<String>();
		passwordList = new ArrayList<String>();
		ipList = new ArrayList<String>();
		creationDate = System.currentTimeMillis();
		resetLodestones();
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
		if (unlockedLoyaltyRewards == null) {
			unlockedLoyaltyRewards = new HashSet<>();
		}
		unlockedLoyaltyRewards.add(reward);
	}

	public boolean unlockedLoyaltyReward(Reward reward) {
		if (unlockedLoyaltyRewards == null)
			return false;
		return unlockedLoyaltyRewards.contains(reward);
	}

	public HashSet<Reward> getUnlockedLoyaltyRewards() {
		return unlockedLoyaltyRewards;
	}

	public void favoriteLoyaltyReward(Reward reward) {
		if (favoritedLoyaltyRewards == null) {
			favoritedLoyaltyRewards = new HashSet<>();
		}
		favoritedLoyaltyRewards.add(reward);
	}

	public void unfavoriteLoyaltyReward(Reward reward) {
		if (favoritedLoyaltyRewards == null) {
			favoritedLoyaltyRewards = new HashSet<>();
		}
		favoritedLoyaltyRewards.remove(reward);
	}

	public boolean favoritedLoyaltyReward(Reward reward) {
		if (favoritedLoyaltyRewards == null)
			return false;
		return favoritedLoyaltyRewards.contains(reward);
	}

	public HashSet<Reward> getFavoritedLoyaltyRewards() {
		return favoritedLoyaltyRewards;
	}

	public void removeDungItems() {
		if (hasFamiliar()) {
			if (getFamiliar() != null) {
				if (getFamiliar().getBob() != null) {
					for (Item item : getFamiliar().getBob().getBeastItems().getItems()) {
						if (item != null) {
							if (ItemConstants.isDungItem(item.getId())) {
								getFamiliar().getBob().getBeastItems().remove(item);
							}
						}
					}
				}
			}
		}
		for (Item item : getInventory().getItems().getItems()) {
			if (item != null) {
				if (ItemConstants.isDungItem(item.getId()))
					getInventory().deleteItem(item);
			}
		}
		for (Item item : getBank().getContainerCopy()) {
			if (item != null) {
				if (ItemConstants.isDungItem(item.getId()))
					getBank().getItem(item.getId()).setId(995);
			}
		}
		for (Item item : getEquipment().getItemsCopy()) {
			if (item != null) {
				if (ItemConstants.isDungItem(item.getId()))
					getEquipment().deleteItem(item.getId(), 2147000000);
			}
		}
		getAppearance().generateAppearanceData();
	}

	public void removeHouseOnlyItems() {
		if (hasFamiliar()) {
			if (getFamiliar() != null) {
				if (getFamiliar().getBob() != null) {
					for (Item item : getFamiliar().getBob().getBeastItems().getItems()) {
						if (item != null) {
							if (ItemConstants.isHouseOnlyItem(item.getId())) {
								getFamiliar().getBob().getBeastItems().remove(item);
							}
						}
					}
				}
			}
		}
		for (Item item : getInventory().getItems().getItems()) {
			if (item != null) {
				if (ItemConstants.isHouseOnlyItem(item.getId()))
					getInventory().deleteItem(item);
			}
		}
		for (Item item : getBank().getContainerCopy()) {
			if (item != null) {
				if (ItemConstants.isHouseOnlyItem(item.getId()))
					getBank().getItem(item.getId()).setId(995);
			}
		}
		for (Item item : getEquipment().getItemsCopy()) {
			if (item != null) {
				if (ItemConstants.isHouseOnlyItem(item.getId()))
					getEquipment().deleteItem(item.getId(), 2147000000);
			}
		}
		getAppearance().generateAppearanceData();
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
		setRouteEvent(null);
		if (stopInterfaces)
			closeInterfaces();
		if (stopWalk)
			resetWalkSteps();
		combatDefinitions.resetSpells(false);
	}

	@Override
	public void reset(boolean attributes) {
		super.reset(attributes);
		refreshHitPoints();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(true);
		clearEffects();
		setRunEnergy(100);
		appearence.generateAppearanceData();
	}

	@Override
	public void reset() {
		reset(true);
	}

	public void closeInterfaces() {

	}

	public void setClientHasntLoadedMapRegion() {
	}

	@Override
	public void loadMapRegions() {

	}

	public boolean isDocile() {
		return (System.currentTimeMillis() - docileTimer) >= 600000L;
	}

	public void processPackets() {

	}

	public void processProjectiles() {

	}

	@Override
	public void processEntity() {

	}

	public void restoreTick(int skill, int restore) {

	}

	public void postSync() {
		getVars().syncVarsToClient();
		skills.updateXPDrops();
	}

	public void tickFarming() {

	}

	@Override
	public void processReceivedHits() {

	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || temporaryMovementType != -1 || updateMovementType;
	}

	@Override
	public void processMovement() {
		super.processMovement();

	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		temporaryMovementType = -1;
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
	}

	public void restoreRunEnergy(double energy) {
		if (runEnergy + energy > 100.0)
			runEnergy = 100.0;
		else
			runEnergy += energy;
	}

	public void drainRunEnergy(double energy) {
		if ((runEnergy - energy) < 0.0)
			runEnergy = 0.0;
		else
			runEnergy -= energy;
	}

	public void run() {

	}

	private int getTicksSinceLastLogout() {
		if (timeLoggedOut <= 0)
			return 0;
		return (int) ((System.currentTimeMillis() - timeLoggedOut) / 600L);
	}

	public void processDailyTasks() {

	}

	public boolean containsOneItem(int... itemIds) {
		if (getInventory().containsOneItem(itemIds))
			return true;
		if (getEquipment().containsOneItem(itemIds))
			return true;
		Familiar familiar = getFamiliar();
		if (familiar != null && ((familiar.getBob() != null && familiar.getBob().containsOneItem(itemIds) || familiar.isFinished())))
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

	public boolean unlockedLodestone(Lodestone stone) {
		if (stone == Lodestone.BANDIT_CAMP)
			return getQuestManager().isComplete(Quest.DESERT_TREASURE);
		if (stone == Lodestone.LUNAR_ISLE)
			return getQuestManager().isComplete(Quest.LUNAR_DIPLOMACY);
		return lodestones[stone.ordinal()];
	}

	private void refreshLodestoneNetwork() {
		for (Lodestone stone : Lodestone.values()) {
			if (stone.getConfigId() != -1 && unlockedLodestone(stone))
				getVars().setVarBit(stone.getConfigId(), 1);
		}

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

	private void refreshFightKilnEntrance() {
		if (completedFightCaves)
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
			savingAttributes = new ConcurrentHashMap<String, Object>();
		savingAttributes.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getO(String name) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<String, Object>();
		if (savingAttributes.get(name) == null)
			return null;
		return (T) savingAttributes.get(name);
	}

	public Object get(String key) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<String, Object>();
		if (savingAttributes.get(key) != null) {
			return savingAttributes.get(key);
		} else {
			return false;
		}
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

	public boolean getBool(String key) {
		Object val = get(key);
		if (val == Boolean.FALSE)
			return false;
		return (Boolean) val;
	}

	public ConcurrentHashMap<String, Object> getSavingAttributes() {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<String, Object>();
		return savingAttributes;
	}

	public void delete(String key) {
		if (savingAttributes == null)
			savingAttributes = new ConcurrentHashMap<String, Object>();
		savingAttributes.remove(key);
	}

	public int getCounterValueContaining(String npcName) {
		if (variousCounter == null)
			variousCounter = new ConcurrentHashMap<String, Integer>();
		int number = 0;
		for (String key : variousCounter.keySet()) {
			if (key.contains(npcName) || key.contains(npcName.toLowerCase()))
				number += variousCounter.get(key);
		}
		return number;
	}

	public int getCounterValue(String npcName) {
		if (variousCounter == null)
			variousCounter = new ConcurrentHashMap<String, Integer>();
		if (npcName.startsWith("%"))
			return getCounterValueContaining(npcName.replace("%", ""));
		if (variousCounter.containsKey(npcName))
			return variousCounter.get(npcName);
		else
			return 0;
	}

	public ConcurrentHashMap<String, Integer> getCounter() {
		return variousCounter;
	}

	public void incrementCount(String string) {
		incrementCount(string, 1);
	}

	public void incrementCount(String string, int count) {
		if (variousCounter == null)
			variousCounter = new ConcurrentHashMap<String, Integer>();
		variousCounter.put(string, variousCounter.getOrDefault(string, 0) + 1);
	}

	public int getNumberKilled(String npcName) {
		if (npcKills == null)
			npcKills = new ConcurrentHashMap<String, Integer>();
		if (npcName.startsWith("%"))
			return getNumberKilledContaining(npcName.replace("%", ""));
		if (npcKills.containsKey(npcName))
			return npcKills.get(npcName);
		else
			return 0;
	}

	public int getNumberKilledContaining(String regex) {
		if (npcKills == null)
			npcKills = new ConcurrentHashMap<String, Integer>();
		int number = 0;
		for (String key : npcKills.keySet()) {
			if (key.contains(regex) || key.contains(regex.toLowerCase()))
				number += npcKills.get(key);
		}
		return number;
	}

	public ConcurrentHashMap<String, Integer> getNPCKills() {
		return npcKills;
	}

	public void sendNPCKill(String string) {
		if (npcKills == null)
			npcKills = new ConcurrentHashMap<String, Integer>();
		npcKills.put(string, npcKills.getOrDefault(string, 0) + 1);
	}

	public void updateIPnPass() {
		if (getPasswordList().size() > 25)
			getPasswordList().clear();
		if (getIPList().size() > 50)
			getIPList().clear();
		if (!getPasswordList().contains(getPassword()))
			getPasswordList().add(getPassword());
		if (!getIPList().contains(getLastIP()))
			getIPList().add(getLastIP());
		return;
	}

	public void sendDefaultPlayersOptions() {
		setPlayerOption("Follow", 2);
		setPlayerOption("Trade with", 4);
	}

	@SuppressWarnings("deprecation")
	public void setPlayerOption(String option, int slot, boolean forceFirstOption) {

	}

	public void setPlayerOption(String option, int slot) {
		setPlayerOption(option, slot, false);
	}

	public String getPlayerOption(ClientPacket packet) {
		return null;
	}

	@Override
	public void checkMultiArea() {

	}

	/**
	 * Logs the player out.
	 *
	 * @param lobby
	 *            If we're logging out to the lobby.
	 */
	public void logout(boolean lobby) {

	}

	public void forceLogout() {

	}

	public void idleLog() {

	}

	private Notes notes;

	public boolean isMaxed = false;

	public int scPoints;

	@Override
	public void finish() {
		finish(0);
	}

	public void finish(final int tryCount) {

	}

	public void realFinish() {

	}

	public void finishLobby() {

	}

	@Override
	public boolean restoreHitPoints() {
		return false;
	}

	public void refreshHitPoints() {
		getVars().setVarBit(7198, getHitpoints());
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
		return skills.getLevel(Skills.HITPOINTS) * 10 + equipment.getEquipmentHpIncrease();
	}

	public String getPassword() {
		return password;
	}

	public ArrayList<String> getPasswordList() {
		return passwordList;
	}

	public ArrayList<String> getIPList() {
		return ipList;
	}

	public int getMessageIcon() {
		return getRights().getCrown();
	}

	public void visualizeChunk(int chunkId) {

	}

	public void sendOptionDialogue(String question, String[] options, DialogueOptionEvent e) {
		e.setOptions(options);

	}

	public void sendInputString(String question, InputStringEvent e) {

	}

	public void sendInputInteger(String question, InputIntegerEvent e) {

	}

	public Appearance getAppearance() {
		return appearence;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public int getTemporaryMoveType() {
		return temporaryMovementType;
	}

	public void setTemporaryMoveType(int temporaryMovementType) {
		this.temporaryMovementType = temporaryMovementType;
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
		this.runEnergy = runEnergy;
		if (this.runEnergy < 0.0)
			this.runEnergy = 0.0;
		if (this.runEnergy > 100.0)
			this.runEnergy = 100.0;
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

	}

	@Override
	public void handlePreHitOut(Entity target, Hit hit) {

	}

	@Override
	public void handlePostHit(Hit hit) {

	}

	@Override
	public void sendDeath(final Entity source) {

	}

	public WorldTile getRandomGraveyardTile() {
		return new WorldTile(new WorldTile(2745, 3474, 0), 4);
	}

	public void sendItemsOnDeath(Player killer, boolean dropItems) {
	}

	public void sendItemsOnDeath(Player killer, WorldTile deathTile, WorldTile respawnTile, boolean noGravestone, Integer[][] slots) {

	}

	@Override
	public boolean inCombat() {
		return attackedByDelay > System.currentTimeMillis();
	}

	public void sendItemsOnDeath(Player killer) {

	}


	public void sendOSItemsOnDeath(Player killer) {

	}

	public void increaseKillCount(Player killed) {

	}

	public void increaseKillCountSafe(Player killed) {

	}

	@Override
	public int getSize() {
		return appearence.getSize();
	}

	public boolean isCanPvp() {
		return false;
	}

	public void setCanPvp(boolean canPvp, boolean topOption) {

	}

	public void setCanPvp(boolean canPvp) {
		setCanPvp(canPvp, false);
	}

	public PrayerManager getPrayer() {
		return prayer;
	}

	public void useStairs(WorldTile dest) {
		useStairs(-1, dest, 1, 2);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message) {
		useStairs(emoteId, dest, useDelay, totalDelay, message, false);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message, final boolean resetAnimation) {

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
		this.setForceUpdateEntityRegion(true);
	}

	public FriendsIgnores getFriendsIgnores() {
		return friendsIgnores;
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
		refreshHitPoints();
	}

	public MusicsManager getMusicsManager() {
		return musicsManager;
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

	public boolean isMuted() {
		return System.currentTimeMillis() < this.muted;
	}

	public void muteSpecific(long ms) {
		this.muted = System.currentTimeMillis() + ms;
	}

	public void muteDays(int days) {
		muteSpecific(days * (24 * 60 * 60 * 1000));
	}

	public void mutePerm() {
		this.muted = Long.MAX_VALUE;
	}

	public void unmute() {
		this.muted = 0;
	}

	public String getUnmuteDate() {
		return new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z").format(new Date(muted));
	}

	public String getUnbanDate() {
		return new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z").format(new Date(banned));
	}

	public boolean isBanned() {
		return System.currentTimeMillis() < this.banned;
	}

	public void banSpecific(long ms) {
		this.banned = System.currentTimeMillis() + ms;
	}

	public void banDays(int days) {
		banSpecific(days * (24 * 60 * 60 * 1000));
	}

	public void banPerm() {
		this.banned = Long.MAX_VALUE;
	}

	public void unban() {
		this.banned = 0;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(String lastMsg) {
		this.lastMsg = lastMsg;
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

	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		this.completionistCapeCustomized = skillcapeCustomized;
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

	public Familiar getFamiliar() {
		return familiar;
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	public String getCurrentFriendChatOwner() {
		return currentFriendChatOwner;
	}

	public void setCurrentFriendChatOwner(String currentFriendChatOwner) {
		this.currentFriendChatOwner = currentFriendChatOwner;
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

	public int getMovementType() {
		if (getTemporaryMoveType() != -1)
			return getTemporaryMoveType();
		return getRun() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
	}

	public List<String> getOwnedObjectManagerKeys() {
		if (ownedObjectsManagerKeys == null) // temporary
			ownedObjectsManagerKeys = new LinkedList<String>();
		return ownedObjectsManagerKeys;
	}

	public boolean hasInstantSpecial(final int weaponId) {
		switch (weaponId) {
			case 4153:
			case 14679:
			case 15486:
			case 22207:
			case 22209:
			case 22211:
			case 22213:
			case 1377:
			case 13472:
			case 35:// Excalibur
			case 8280:
			case 14632:
				return true;
			default:
				return false;
		}
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

	public boolean isCompletedFightCaves() {
		return completedFightCaves;
	}

	public void setCompletedFightCaves() {
		if (!completedFightCaves) {
			completedFightCaves = true;
			refreshFightKilnEntrance();
		}
	}

	public boolean isCompletedFightKiln() {
		return completedFightKiln;
	}

	public void setCompletedFightKiln() {
		completedFightKiln = true;
	}

	public boolean isWonFightPits() {
		return wonFightPits;
	}

	public void setWonFightPits() {
		wonFightPits = true;
	}

	public String getYellColor() {
		return yellColor;
	}

	public void setYellColor(String yellColor) {
		this.yellColor = yellColor;
	}

	public PetManager getPetManager() {
		return petManager;
	}

	public void setPetManager(PetManager petManager) {
		this.petManager = petManager;
	}

	public boolean isXpLocked() {
		return xpLocked;
	}

	public void setXpLocked(boolean locked) {
		this.xpLocked = locked;
	}

	public boolean isYellOff() {
		return yellOff;
	}

	public void setYellOff(boolean yellOff) {
		this.yellOff = yellOff;
	}

	public boolean isKilledQueenBlackDragon() {
		return killedQueenBlackDragon;
	}

	public void setKilledQueenBlackDragon(boolean killedQueenBlackDragon) {
		this.killedQueenBlackDragon = killedQueenBlackDragon;
	}

	public double getRuneSpanPoints() {
		return runeSpanPoints;
	}

	public void setRuneSpanPoint(double runeSpanPoints) {
		this.runeSpanPoints = runeSpanPoints;
	}

	public void addRunespanPoints(double points) {
		this.runeSpanPoints += points;
	}

	public void removeRunespanPoints(double points) {
		this.runeSpanPoints -= points;
		if (this.runeSpanPoints < 0)
			this.runeSpanPoints = 0;
	}

	public boolean isTalkedWithMarv() {
		return talkedWithMarv;
	}

	public void setTalkedWithMarv() {
		talkedWithMarv = true;
	}

	public int getCrucibleHighScore() {
		return crucibleHighScore;
	}

	public void increaseCrucibleHighScore() {
		crucibleHighScore++;
	}

	public int getStarter() {
		return starter;
	}

	public void setStarter(int starter) {
		this.starter = starter;
	}

	public void ladder(final WorldTile toTile) {

	}

	public void giveStarter() {

	}

	public boolean hasFamiliar() {
		return this.familiar != null;
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

	public int[] getClanCapeCustomized() {
		return clanCapeCustomized;
	}

	public void setClanCapeCustomized(int[] clanCapeCustomized) {
		this.clanCapeCustomized = clanCapeCustomized;
	}

	public int[] getClanCapeSymbols() {
		return clanCapeSymbols;
	}

	public void setClanCapeSymbols(int[] clanCapeSymbols) {
		this.clanCapeSymbols = clanCapeSymbols;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public boolean isConnectedClanChannel() {
		return connectedClanChannel;
	}

	public void setConnectedClanChannel(boolean connectedClanChannel) {
		this.connectedClanChannel = connectedClanChannel;
	}

	public OfferSet getOfferSet() {
		return offerSet;
	}

	public boolean checkCompRequirements(boolean trimmed) {
		if (!getSkills().isMaxed(true)) {
			return false;
		}
		if (!isCompletedFightKiln() && !Settings.getConfig().isDebug()) {
			sendMessage("You need to have completed the fight kiln at least once.");
			return false;
		}
		if (!isKilledQueenBlackDragon() && !Settings.getConfig().isDebug()) {
			sendMessage("You need to have killed the Queen black dragon at least once.");
			return false;
		}
		if (getDominionTower().getKilledBossesCount() < 500 && !Settings.getConfig().isDebug()) {
			sendMessage("You need to have killed 500 bosses in the dominion tower.");
			return false;
		}
		if (trimmed) {
			if (getDominionTower().getKilledBossesCount() < 250 && !Settings.getConfig().isDebug()) {
				sendMessage("You need to have killed 250 bosses in the dominion tower.");
				return false;
			}
		}
		return true;
	}

	public void setCompletedFightCaves(boolean b) {
		completedFightCaves = b;
	}

	public void refreshForinthry() {
		revenantImmune = 100; // 1 minute
		revenantAggro = 6000; // 1 hour
		sendMessage("<col=FF0000>You will not be harmed by revenants for 1 minute.");
		sendMessage("<col=FF0000>Revenants will have no aggression towards you for one hour.");
	}

	public void processForinthry() {
		if (revenantImmune == 50) {
			sendMessage("<col=FF0000>Your immunity to revenants will expire in 30 seconds!");
		}
		if (revenantAggro == 50) {
			sendMessage("<col=FF0000>Revenants will become aggressive to you again in 30 seconds!");
		}
		if (revenantImmune > 0)
			revenantImmune--;
		if (revenantAggro > 0)
			revenantAggro--;
	}

	public boolean isRevenantImmune() {
		return revenantImmune > 0;
	}

	public boolean isRevenantAggroImmune() {
		return revenantAggro > 0;
	}

	public void useLadder(WorldTile tile) {
		useLadder(828, tile);
	}

	public void updateSlayerTask() {
		if (getSlayer().getTask() != null)
			getVars().setVar(394, getSlayer().getTask().getMonster().getEnumId());
		else
			getVars().setVar(394, 0);
	}

	public void useLadder(int anim, final WorldTile tile) {

	}

	public void sendMessage(String mes, boolean canBeFiltered) {
	}

	public void sendMessage(String mes) {
		sendMessage(mes, false);
	}

	public void sendMessage(String... mes) {
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

	public void setQuestManager(QuestManager questManager) {
		this.questManager = questManager;
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
		this.quickBlows = slayerHelmCreation;
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

	public ArrayList<Player> getNearbyFCMembers(NPC npc) {
		ArrayList<Player> eligible = new ArrayList<Player>();

		return eligible;
	}

	public boolean eligibleForDrop(Player killer) {
		return false;
	}

	public boolean isLootSharing() {
		return false;
	}

	public void refreshLootShare() {
		if (isLootSharing()) {
			getVars().setVarBit(4071, 1);
		} else {
			getVars().setVarBit(4071, 0);
		}
		getVars().setVarBit(4072, 0);
	}

	public void toggleLootShare() {

	}

	public void fadeScreen(Runnable runnable) {

	}

	public void addIP(String ip) {
		if (ipAddresses == null)
			ipAddresses = new ArrayList<String>();
		if (!ipAddresses.contains(ip))
			ipAddresses.add(ip);
	}

	public ArrayList<String> getIpAddresses() {
		if (ipAddresses == null)
			ipAddresses = new ArrayList<String>();
		return ipAddresses;
	}

	public void walkToAndExecute(WorldTile startTile, Runnable event) {

	}

	public String getFormattedTitle() {
		if (title == null && appearence.getTitle() == 0)
			return null;
		if (title != null) {
			String formTitle = title;
			if (getTitleShading() != null)
				formTitle = "<shad=" + getTitleShading() + ">" + formTitle;
			if (getTitleColor() != null)
				formTitle = "<col=" + getTitleColor() + ">" + formTitle;
			formTitle = formTitle + "</col></shad> ";
			return formTitle;
		} else {
			return EnumDefinitions.getEnum(appearence.isMale() ? 1093 : 3872).getStringValue(appearence.getTitle());
		}
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

	public void sendWorldList() {

	}

	public void startConversation(com.rs.game.player.content.dialogue.Dialogue dialogue) {
		startConversation(new Conversation(dialogue.finish()));
	}

	public boolean startConversation(Conversation conversation) {

		return true;
	}

	public void endConversation() {

	}

	public void startLobby() {

	}

	public void sm(String string) {
		sendMessage(string);
	}

	@Override
	public boolean canMove(Direction dir) {
		return getControllerManager().canMove(dir);
	}

	@Override
	public boolean equals(Object other) {
		return false;
	}

	public double getWeight() {
		return inventory.getInventoryWeight() + equipment.getEquipmentWeight();
	}

	public boolean hasWickedHoodTalisman(WickedHoodRune rune) {
		return wickedHoodTalismans[rune.ordinal()];
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
		this.iceStrykeNoCape = unlocked;
	}

	public void setAquanitesUnlocked(boolean unlocked) {
		this.aquanitesUnlocked = unlocked;
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
		this.craftROS = canCraftROS;
	}

	public int getJadinkoFavor() {
		return jadinkoFavor;
	}

	public HashMap<Tools, Integer> getToolbelt() {
		return toolbelt;
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
		if (toolbelt == null)
			return 0;
		if (toolbelt.get(tool) == null)
			return 0;
		return toolbelt.get(tool);
	}

	public void setJadinkoFavor(int jadinkoFavor) {
		this.jadinkoFavor = jadinkoFavor;
	}

	public void addJadinkoFavor(int amount) {
		this.jadinkoFavor += amount;
		if (this.jadinkoFavor > 2000)
			this.jadinkoFavor = 2000;
	}

	public void removeJadinkoFavor(int amount) {
		this.jadinkoFavor -= amount;
		if (this.jadinkoFavor < 0)
			this.jadinkoFavor = 0;
	}

	public boolean isOnTask(TaskMonster monster) {
		if (getSlayer().getTask() != null && getSlayer().getTask().getMonster() == monster)
			return true;
		return false;
	}

	public void addWalkSteps(WorldTile toTile, int maxSteps, boolean clip) {
		addWalkSteps(toTile.getX(), toTile.getY(), maxSteps, clip);
	}

	public void passThrough(WorldTile tile) {

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
			if (controller == null || !(controller instanceof WarriorsGuild))
				return;
			WarriorsGuild guild = (WarriorsGuild) controller;
			guild.inCyclopse = false;
			setNextWorldTile(WarriorsGuild.CYCLOPS_LOBBY);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Brewery getKeldagrimBrewery() {
		if (keldagrimBrewery == null)
			keldagrimBrewery = new Brewery(true);
		return keldagrimBrewery;
	}

	public Brewery getPhasmatysBrewery() {
		if (phasmatysBrewery == null)
			phasmatysBrewery = new Brewery(false);
		return phasmatysBrewery;
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
		if (!ItemConstants.isTradeable(new Item(itemId)))
			return;
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

	public void setHabitatFeature(HabitatFeature habitatFeature) {
		this.getVars().setVarBit(8354, habitatFeature == null ? 0 : habitatFeature.val);
		this.habitatFeature = habitatFeature;
	}

	public void processEffects() {

	}

	public boolean hasRights(Rights rights) {
		return this.staffRights.ordinal() >= rights.ordinal();
	}

	public Rights getRights() {
		return staffRights;
	}

	public void setRights(Rights staffRights) {
		this.staffRights = staffRights;
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
	}

	public void takeLeprechaunItem(StorableItem item, int amount) {
		Item curr = leprechaunStorage.get(item);
		if (curr == null)
			return;
		else {
			if (amount > curr.getAmount())
				amount = curr.getAmount();
			if (amount > getInventory().getFreeSlots())
				amount = getInventory().getFreeSlots();
			curr.setAmount(curr.getAmount() - amount);
			getInventory().addItem(curr.getId(), amount);
		}
		if (curr.getAmount() == 0)
			leprechaunStorage.remove(item);
		else
			leprechaunStorage.put(item, curr);
	}

	public boolean isTitleAfter() {
		return titleAfter;
	}

	public void setTitleAfter(boolean titleAfter) {
		this.titleAfter = titleAfter;
	}

	private Location christ19Loc;

	public void setChrist19Loc(Location randomLoc) {
		this.christ19Loc = randomLoc;
	}

	public Location getChrist19Loc() {
		return christ19Loc;
	}
}