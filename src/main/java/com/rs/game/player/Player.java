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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.LoyaltyRewardDefinitions.Reward;
import com.rs.cores.CoresManager;
import com.rs.db.WorldDB;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.World.DropMethod;
import com.rs.game.WorldProjectile;
import com.rs.game.ge.GE;
import com.rs.game.ge.Offer;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.others.GraveStone;
import com.rs.game.npc.pet.Pet;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.pathing.FixedTileStrategy;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.pathing.RouteFinder;
import com.rs.game.player.actions.LodestoneAction.Lodestone;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.ItemConstants.ItemDegrade;
import com.rs.game.player.content.Notes;
import com.rs.game.player.content.PlayerLook;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.game.player.content.Toolbelt;
import com.rs.game.player.content.Toolbelt.Tools;
import com.rs.game.player.content.achievements.AchievementInterface;
import com.rs.game.player.content.books.Book;
import com.rs.game.player.content.combat.CombatDefinitions;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.statements.SimpleStatement;
import com.rs.game.player.content.holidayevents.christmas.christ19.Christmas2019.Location;
import com.rs.game.player.content.interfacehandlers.TransformationRing;
import com.rs.game.player.content.minigames.duel.DuelRules;
import com.rs.game.player.content.minigames.herblorehabitat.HabitatFeature;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.content.skills.construction.House;
import com.rs.game.player.content.skills.cooking.Brewery;
import com.rs.game.player.content.skills.dungeoneering.DungManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonRewards.HerbicideSetting;
import com.rs.game.player.content.skills.farming.FarmPatch;
import com.rs.game.player.content.skills.farming.PatchLocation;
import com.rs.game.player.content.skills.farming.ProduceType;
import com.rs.game.player.content.skills.farming.StorableItem;
import com.rs.game.player.content.skills.prayer.Prayer;
import com.rs.game.player.content.skills.prayer.PrayerBooks;
import com.rs.game.player.content.skills.runecrafting.RunecraftingAltar.WickedHoodRune;
import com.rs.game.player.content.skills.slayer.BossTask;
import com.rs.game.player.content.skills.slayer.SlayerTaskManager;
import com.rs.game.player.content.skills.slayer.TaskMonster;
import com.rs.game.player.content.transportation.FadingScreen;
import com.rs.game.player.content.world.Musician;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.DeathOfficeController;
import com.rs.game.player.controllers.GodwarsController;
import com.rs.game.player.controllers.TutorialIslandController;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.player.dialogues.StartDialogue;
import com.rs.game.player.managers.ActionManager;
import com.rs.game.player.managers.AuraManager;
import com.rs.game.player.managers.ControllerManager;
import com.rs.game.player.managers.CutscenesManager;
import com.rs.game.player.managers.DialogueManager;
import com.rs.game.player.managers.EmotesManager;
import com.rs.game.player.managers.HintIconsManager;
import com.rs.game.player.managers.InteractionManager;
import com.rs.game.player.managers.InterfaceManager;
import com.rs.game.player.managers.MusicsManager;
import com.rs.game.player.managers.PrayerManager;
import com.rs.game.player.managers.TreasureTrailsManager;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestManager;
import com.rs.game.player.social.FCManager;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.PublicChatMessage;
import com.rs.lib.game.Rights;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.VarManager;
import com.rs.lib.game.WorldTile;
import com.rs.lib.model.Account;
import com.rs.lib.model.Clan;
import com.rs.lib.model.Social;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.Session;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.encoders.MinimapFlag;
import com.rs.lib.net.packets.encoders.ReflectionCheckRequest;
import com.rs.lib.net.packets.encoders.social.MessageGame.MessageType;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.lib.util.ReflectionCheck;
import com.rs.lib.util.Utils;
import com.rs.lib.web.dto.FCData;
import com.rs.net.LobbyCommunicator;
import com.rs.net.decoders.handlers.PacketHandlers;
import com.rs.net.encoders.WorldEncoder;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.InputIntegerEvent;
import com.rs.plugin.events.InputStringEvent;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.utils.Click;
import com.rs.utils.MachineInformation;
import com.rs.utils.Ticks;

public class Player extends Entity {

	public static final int TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1, RUN_MOVE_TYPE = 2;

	private String username;
	private Date dateJoined;

	private Map<String, Object> dailyAttributes;

	public transient int chatType;

	private long timePlayed = 0;
	private long timeLoggedOut;

	private transient HashMap<Integer, ReflectionCheck> reflectionChecks = new HashMap<>();

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
	private transient String[] playerOptions = new String[10];

	private int hw07Stage;

	public void refreshChargeTimer() {
		getTempAttribs().setL("chargeTimer", World.getServerTicks()+600);
	}

	public boolean isCharged() {
		return World.getServerTicks() < getTempAttribs().getL("chargeTimer");
	}

	public void refreshMiasmicTimer(int ticks) {
		if (World.getServerTicks() < getTempAttribs().getL("miasmicImmune"))
			return;
		sendMessage("You feel slowed down.");
		getTempAttribs().setL("miasmicImmune", World.getServerTicks()+ticks+15);
		getTempAttribs().setL("miasmicEffect", World.getServerTicks()+ticks);
	}

	public boolean isMiasmicEffectActive() {
		return World.getServerTicks() < getTempAttribs().getL("miasmicEffect");
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
	private transient int displayMode;
	private transient int screenWidth;
	private transient int screenHeight;
	private transient Conversation conversation;
	private transient InterfaceManager interfaceManager;
	private transient DialogueManager dialogueManager;
	private transient HintIconsManager hintIconsManager;
	private transient ActionManager actionManager;
	private transient InteractionManager interactionManager;
	private transient CutscenesManager cutscenesManager;
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

	private ConcurrentHashMap<String, Object> savingAttributes;

	private ConcurrentHashMap<String, Integer> npcKills;
	private ConcurrentHashMap<String, Integer> variousCounter;

	private HashSet<Reward> unlockedLoyaltyRewards;
	private HashSet<Reward> favoritedLoyaltyRewards;

	private HashMap<Tools, Integer> toolbelt;

	private transient ArrayList<String> attackedBy = new ArrayList<>();

	public transient Queue<Click> clickQueue = new LinkedList<>();
	public transient Click lastClick;

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

	private int temporaryMovementType;
	private boolean updateMovementType;

	// player stages
	private transient boolean started;
	private transient boolean running;

	private transient boolean resting;
	private transient boolean canPvp;
	private transient boolean cantTrade;
	private transient long lockDelay; // used for doors and stuff like that
	private transient long foodDelay;
	private transient long potionDelay;
	private transient long boneDelay;
	private transient Runnable closeInterfacesEvent;
	private transient boolean disableEquip;
	private transient MachineInformation machineInformation;
	private transient boolean castedVeng;
	private transient boolean castedMagicImbue;
	private transient boolean invulnerable;
	private transient double hpBoostMultiplier;
	private transient boolean largeSceneView;
	private transient String lastNpcInteractedName = null;
	private transient Account account;

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
	private Familiar familiar;
	private AuraManager auraManager;
	private PetManager petManager;
	private BossTask bossTask;
	private QuestManager questManager;
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
	private transient double bonusXpRate = 0.0;
	private int crystalSeedRepairs;
	private int tinySeedRepairs;

	public WorldTile lastEssTele;

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
	private ArrayList<String> ipList = new ArrayList<>();

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

	private transient boolean nextTickUnlock;

	public void unlockNextTick() {
		nextTickUnlock = true;
	}

	// creates Player and saved classes
	public Player(Account account) {
		super(Settings.getConfig().getPlayerStartTile());
		this.account = account;
		username = account.getUsername();
		setHitpoints(100);
		dateJoined = new Date();
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
		ipList = new ArrayList<>();
		creationDate = System.currentTimeMillis();
		resetLodestones();
	}

	public void init(Session session, Account account, int displayMode, int screenWidth, int screenHeight, MachineInformation machineInformation) {
		this.session = session;
		this.account = account;
		uuid = getUsername().hashCode();
		this.displayMode = displayMode;
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
		reflectionChecks = new HashMap<>();
		attackedBy = new ArrayList<>();
		interfaceManager = new InterfaceManager(this);
		dialogueManager = new DialogueManager(this);
		hintIconsManager = new HintIconsManager(this);
		localPlayerUpdate = new LocalPlayerUpdate(this);
		localNPCUpdate = new LocalNPCUpdate(this);
		actionManager = new ActionManager(this);
		interactionManager = new InteractionManager(this);
		if (varManager == null)
			varManager = new VarManager();
		varManager.setSession(session);
		cutscenesManager = new CutscenesManager(this);
		trade = new Trade(this);
		// loads player on saved instances
		appearence.setPlayer(this);
		treasureTrailsManager.setPlayer(this);
		questManager.setPlayer(this);
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
		temporaryMovementType = -1;
		initEntity();
		if (clickQueue == null)
			clickQueue = new LinkedList<>();
		if (pouchesType == null)
			pouchesType = new boolean[4];
		World.addPlayer(this);
		World.updateEntityRegion(this);
		if (Settings.getConfig().isDebug())
			Logger.log(this, "Initiated player: " + account.getUsername());

		// Do not delete >.>, useful for security purpose. this wont waste that
		// much space..
		if (prayerBook == null)
			prayerBook = new boolean[PrayerBooks.BOOKS.length];
		if (ipList == null)
			ipList = new ArrayList<>();
		updateIPnPass();
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

	public HashSet<Reward> getUnlockedLoyaltyRewards() {
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

	public HashSet<Reward> getFavoritedLoyaltyRewards() {
		return favoritedLoyaltyRewards;
	}

	public void removeDungItems() {
		if (hasFamiliar())
			if (getFamiliar() != null)
				if (getFamiliar().getBob() != null)
					for (Item item : getFamiliar().getBob().getBeastItems().getItems())
						if (item != null)
							if (ItemConstants.isDungItem(item.getId()))
								getFamiliar().getBob().getBeastItems().remove(item);
		for (Item item : getInventory().getItems().getItems())
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
				if (getFamiliar().getBob() != null)
					for (Item item : getFamiliar().getBob().getBeastItems().getItems())
						if (item != null)
							if (ItemConstants.isHouseOnlyItem(item.getId()))
								getFamiliar().getBob().getBeastItems().remove(item);
		for (Item item : getInventory().getItems().getItems())
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

	public void refreshRegionItems() {
		for (int regionId : getMapRegionsIds()) {
			List<GroundItem> floorItems = World.getRegion(regionId).getAllGroundItems();
			if (floorItems == null)
				continue;
			for (GroundItem item : floorItems)
				getPackets().removeGroundItem(item);
			for (GroundItem item : floorItems) {
				if (item.isPrivate() && item.getVisibleToId() != getUuid())
					continue;
				getPackets().sendGroundItem(item);
			}
		}
	}

	public void refreshSpawnedObjects() {
		for (int regionId : getMapRegionsIds()) {
			List<GameObject> removedObjects = new ArrayList<>(World.getRegion(regionId).getRemovedObjects().values());
			for (GameObject object : removedObjects)
				getPackets().sendRemoveObject(object);
			List<GameObject> spawnedObjects = World.getRegion(regionId).getSpawnedObjects();
			for (GameObject object : spawnedObjects)
				getPackets().sendAddObject(object);
		}
	}

	// now that we inited we can start showing game
	public void start() {
		loadMapRegions();
		started = true;
		run();

		//tele to demonheim if your physically in a dungeon but have no loaded dungeon/party
		if(getBool("isLoggedOutInDungeon"))
			if(getDungManager().getParty() == null) {
				setNextWorldTile(new WorldTile(DungeonConstants.OUTSIDE, 2));
				this.reset();
				getEquipment().reset();
				getInventory().reset();
				getInventory().addItem(15707, 1);
			}
		save("isLoggedOutInDungeon", false);

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
		interactionManager.forceStop();
		if (stopInterfaces)
			closeInterfaces();
		if (stopWalk)
			resetWalkSteps();
		if (stopActions)
			actionManager.forceStop();
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
		clearEffects();
		setRunEnergy(100);
		appearence.generateAppearanceData();
	}

	@Override
	public void reset() {
		reset(true);
	}

	public void closeInterfaces() {
		if (interfaceManager.containsScreenInter())
			interfaceManager.removeScreenInterface();
		if (interfaceManager.containsInventoryInter())
			interfaceManager.removeInventoryInterface();
		endConversation();
		dialogueManager.finishDialogue();
		getPackets().closeGESearch();
		if (closeInterfacesEvent != null) {
			closeInterfacesEvent.run();
			closeInterfacesEvent = null;
		}
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = World.getServerTicks() + 30;
	}

	@Override
	public void loadMapRegions() {
		if (started)
			docileTimer = System.currentTimeMillis();
		else
			docileTimer = System.currentTimeMillis()-(lastLoggedIn-docileTimer);
		boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		setClientHasntLoadedMapRegion();
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicMapRegion(!started);
			if (!wasAtDynamicRegion)
				localNPCUpdate.reset();
		} else {
			getPackets().sendMapRegion(!started);
			if (wasAtDynamicRegion)
				localNPCUpdate.reset();
		}
		forceNextMapLoadRefresh = false;
	}

	public boolean isDocile() {
		return (System.currentTimeMillis() - docileTimer) >= 600000L;
	}

	public void processProjectiles() {
		for (int regionId : getMapRegionsIds()) {
			Region region = World.getRegion(regionId);
			for (WorldProjectile projectile : region.getProjectiles())
				getPackets().sendProjectile(projectile);
		}
	}

	@Override
	public void processEntity() {
		if (getSession().isClosed())
			finish(0);
		processPackets();
		processForinthry();
		cutscenesManager.process();
		super.processEntity();
		if (hasStarted() && isIdle())
			if (!hasRights(Rights.ADMIN))
				if (getActionManager().getAction() instanceof PlayerCombat combat) {
					if (!(combat.getTarget() instanceof Player))
						idleLog();
				} else
					logout(true);
		if (disconnected && !finishing)
			finish(0);
		timePlayed = getTimePlayed() + 1;
		timeLoggedOut = System.currentTimeMillis();
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
		if (getNextRunDirection() == null) {
			double energy = (8.0 + Math.floor(getSkills().getLevel(Constants.AGILITY) / 6.0)) / 100.0;
			if (isResting()) {
				energy = 1.68;
				if (Musician.isNearby(this))
					energy = 2.28;
			}
			restoreRunEnergy(energy);
		}

		if (getTickCounter() % FarmPatch.FARMING_TICK == 0)
			tickFarming();

		processMusic();

		if (inCombat() || isAttacking())
			for (int i = 0;i < Equipment.SIZE;i++) {
				Item item = getEquipment().getItem(i);
				if (item == null)
					continue;
				for (ItemDegrade d : ItemDegrade.values())
					if ((d.getItemId() == item.getId() || d.getDegradedId() == item.getId()) && item.getMetaData() == null) {
						getEquipment().set(i, new Item(d.getDegradedId() != -1 ? d.getDegradedId() : d.getItemId(), item.getAmount()).addMetaData("combatCharges", d.getDefaultCharges()));
						getEquipment().refresh(i);
						sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(item.getId()).getName() + " has slightly degraded!");
						break;
					}
				if (item.getMetaData("combatCharges") != null) {
					item.addMetaData("combatCharges", item.getMetaDataI("combatCharges")-1);
					if (item.getMetaDataI("combatCharges") <= 0) {
						ItemDegrade deg = null;
						for (ItemDegrade d : ItemDegrade.values())
							if (d.getItemId() == item.getId() || d.getDegradedId() == item.getId() && d.getBrokenId() != -1) {
								deg = d;
								break;
							}
						if (deg != null) {
							if (deg.getBrokenId() == 4207) {

								getEquipment().set(i, null);
								getEquipment().refresh(i);
								getAppearance().generateAppearanceData();
								if (getInventory().hasFreeSlots()) {
									getInventory().addItem(4207, 1);
									sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(deg.getItemId()).getName() + " has reverted to a crystal seed!");
								} else {
									World.addGroundItem(new Item(4207), new WorldTile(getX(), getY(), getPlane()));
									sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(deg.getItemId()).getName() + " has reverted to a crystal seed and fallen to the floor!");
								}
								break;
							}
							getEquipment().set(i, new Item(deg.getBrokenId(), item.getAmount()));
							getEquipment().refresh(i);
							getAppearance().generateAppearanceData();
							sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(item.getId()).getName() + " has fully degraded!");
						} else {
							getEquipment().set(i, null);
							getEquipment().refresh(i);
							getAppearance().generateAppearanceData();
							sendMessage("<col=FF0000>Your " + ItemDefinitions.getDefs(item.getId()).getName() + " has degraded to dust!");
						}
					}
				}
			}
		auraManager.process();
		interactionManager.process();
		actionManager.process();
		prayer.processPrayer();
		controllerManager.process();
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
		getVars().syncVarsToClient();
		skills.updateXPDrops();
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
		if (lockDelay > World.getServerTicks())
			return;
		super.processReceivedHits();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || temporaryMovementType != -1 || updateMovementType;
	}

	@Override
	public void processMovement() {
		super.processMovement();
		if (nextTickUnlock) {
			unlock();
			nextTickUnlock = false;
		}
		//Magic.teleControllersCheck(this, this);
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
		if ((runEnergy - energy) < 0.0)
			runEnergy = 0.0;
		else
			runEnergy -= energy;
		getPackets().sendRunEnergy(runEnergy);
	}

	public void run() {
		LobbyCommunicator.addWorldPlayer(this, response -> {
			if (!Settings.getConfig().isDebug() && !response) {
				forceLogout();
				return;
			}
		});
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
		getPackets().sendGameBarStages(this);
		musicsManager.init();
		emotesManager.refreshListConfigs();
		sendUnlockedObjectConfigs();
		questManager.sendQuestPoints();
		AchievementInterface.init(this);

		for (Item item : equipment.getItemsCopy())
			if (item != null)
				PluginManager.handle(new ItemEquipEvent(this, item, true));

		GE.updateOffers(username);

		if (familiar != null)
			familiar.respawnFamiliar(this);
		else
			petManager.init();
		running = true;
		updateMovementType = true;
		appearence.generateAppearanceData();
		controllerManager.login(); // checks what to do on login after welcome
		//unlock robust glass
		getVars().setVarBit(4322, 1);
		// screen
		if (machineInformation != null)
			machineInformation.sendSuggestions(this);
		notes.init();

		int farmingTicksMissed = getTicksSinceLastLogout() / FarmPatch.FARMING_TICK;
		if (farmingTicksMissed > 768)
			farmingTicksMissed = 768;
		if (farmingTicksMissed <= 0)
			farmingTicksMissed = 0;
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
			PlayerLook.openCharacterCustomizing(this);
			getDialogueManager().execute(new StartDialogue());
		}
		//getPackets().write(new UpdateRichPresence("state", "Logged in as " + getDisplayName()));
		PluginManager.handle(new LoginEvent(this));
		PluginManager.handle(new EnterChunkEvent(this, getChunkId()));
	}

	private int getTicksSinceLastLogout() {
		if (timeLoggedOut <= 0)
			return 0;
		return (int) ((System.currentTimeMillis() - timeLoggedOut) / 600L);
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

	public void unlockLodestone(Lodestone stone, GameObject object) {
		if (lodestones[stone.ordinal()])
			return;
		if (stone == Lodestone.BANDIT_CAMP || stone == Lodestone.LUNAR_ISLE) {
			sendMessage("This lodestone doesn't respond.");
			return;
		}
		lodestones[stone.ordinal()] = true;
		refreshLodestoneNetwork();

		if (object != null) {
			getPackets().sendSpotAnim(new SpotAnim(3019), object);
			if (stone.getAchievement() != null)
				getInterfaceManager().sendAchievementComplete(stone.getAchievement());
		}
	}

	public boolean unlockedLodestone(Lodestone stone) {
		if (stone == Lodestone.BANDIT_CAMP)
			return getQuestManager().isComplete(Quest.DESERT_TREASURE);
		if (stone == Lodestone.LUNAR_ISLE)
			return getQuestManager().isComplete(Quest.LUNAR_DIPLOMACY);
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

	public boolean getBool(String key) {
		Object val = get(key);
		if (val == Boolean.FALSE)
			return false;
		return (Boolean) val;
	}

	public ConcurrentHashMap<String, Object> getSavingAttributes() {
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

	public ConcurrentHashMap<String, Integer> getCounter() {
		return variousCounter;
	}

	public void incrementCount(String string) {
		incrementCount(string, 1);
	}

	public void incrementCount(String string, int count) {
		if (variousCounter == null)
			variousCounter = new ConcurrentHashMap<>();
		variousCounter.put(string, variousCounter.getOrDefault(string, 0) + 1);
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

	public ConcurrentHashMap<String, Integer> getNPCKills() {
		return npcKills;
	}

	public void sendNPCKill(String string) {
		if (npcKills == null)
			npcKills = new ConcurrentHashMap<>();
		npcKills.put(string, npcKills.getOrDefault(string, 0) + 1);
	}

	public void updateIPnPass() {
		if (getIPList().size() > 50)
			getIPList().clear();
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
		boolean isAtMultiArea = isForceMultiArea() ? true : World.isMultiArea(this);
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
	 * @param lobby
	 *            If we're logging out to the lobby.
	 */
	public void logout(boolean lobby) {
		if (!running)
			return;
		long currentTime = System.currentTimeMillis();
		if (inCombat(10000)) {
			sendMessage("You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (getEmotesManager().isAnimating()) {
			sendMessage("You can't log out while performing an emote.");
			return;
		}
		if (lockDelay >= currentTime) {
			sendMessage("You can't log out while performing an action.");
			return;
		}
		if (isDead() || isDying())
			return;
		getPackets().sendLogout(this, lobby);
		finish();
		running = false;
	}

	public void forceLogout() {
		getPackets().sendLogout(this, false);
		running = false;
		realFinish();
	}

	public void idleLog() {
		incrementCount("Idle logouts");
		getPackets().sendLogout(this, true);
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
		stopAll(false, true, !(actionManager.getAction() instanceof PlayerCombat));
		if ((inCombat(10000) || getEmotesManager().isAnimating() || isLocked()) && tryCount < 6) {
			CoresManager.schedule(() -> {
				try {
					finishing = false;
					if (isDead() || isDying())
						finish(tryCount);
					else
						finish(tryCount + 1);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}, Ticks.fromSeconds(10));
			return;
		}
		realFinish();
	}

	public void realFinish() {
		getTempAttribs().setB("realFinished", true);
		if (isDead() || isDying())
			return;
		stopAll();
		cutscenesManager.logout();
		controllerManager.logout(); // checks what to do on before logout for
		house.finish();
		dungManager.finish();
		running = false;
		if (familiar != null && !familiar.isFinished())
			familiar.dissmissFamiliar(true);
		else if (pet != null)
			pet.finish();
		lastLoggedIn = System.currentTimeMillis();
		setFinished(true);
		session.setDecoder(null);
		WorldDB.getPlayers().save(this, () -> {
			LobbyCommunicator.removeWorldPlayer(this);
			World.removePlayer(this);
			World.updateEntityRegion(this);
			WorldDB.getHighscores().save(this);
			if (Settings.getConfig().isDebug())
				Logger.log(this, "Finished Player: " + getUsername());
		});
		World.updateEntityRegion(this);
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
		return skills.getLevel(Constants.HITPOINTS) * 10 + equipment.getEquipmentHpIncrease();
	}

	public String getUsername() {
		return username;
	}

	public ArrayList<String> getIPList() {
		return ipList;
	}

	public int getMessageIcon() {
		return getRights().getCrown();
	}

	public WorldEncoder getPackets() {
		return session.getEncoder(WorldEncoder.class);
	}

	public void visualizeChunk(int chunkId) {
		int[] oldChunk = MapUtils.decode(Structure.CHUNK, chunkId);
		for (int i = 0;i < 8;i++)
			getPackets().sendGroundItem(new GroundItem(new Item(14486, 1), new WorldTile((oldChunk[0] << 3) + i, (oldChunk[1] << 3), getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().sendGroundItem(new GroundItem(new Item(14486, 1), new WorldTile((oldChunk[0] << 3), (oldChunk[1] << 3) + i, getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().sendGroundItem(new GroundItem(new Item(14486, 1), new WorldTile((oldChunk[0] << 3) + i, (oldChunk[1] << 3) + 7, getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().sendGroundItem(new GroundItem(new Item(14486, 1), new WorldTile((oldChunk[0] << 3) + 7, (oldChunk[1] << 3) + i, getPlane())));
	}

	public void devisualizeChunk(int chunkId) {
		int[] oldChunk = MapUtils.decode(Structure.CHUNK, chunkId);
		for (int i = 0;i < 8;i++)
			getPackets().removeGroundItem(new GroundItem(new Item(14486, 1), new WorldTile((oldChunk[0] << 3) + i, (oldChunk[1] << 3), getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().removeGroundItem(new GroundItem(new Item(14486, 1), new WorldTile((oldChunk[0] << 3), (oldChunk[1] << 3) + i, getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().removeGroundItem(new GroundItem(new Item(14486, 1), new WorldTile((oldChunk[0] << 3) + i, (oldChunk[1] << 3) + 7, getPlane())));
		for (int i = 0;i < 8;i++)
			getPackets().removeGroundItem(new GroundItem(new Item(14486, 1), new WorldTile((oldChunk[0] << 3) + 7, (oldChunk[1] << 3) + i, getPlane())));
	}

	public void sendOptionDialogue(String question, String[] options, DialogueOptionEvent e) {
		e.setOptions(options);
		getTempAttribs().setO("pluginOption", e);
		Dialogue.sendOptionsDialogue(this, question, options);
		setCloseInterfacesEvent(() -> getTempAttribs().removeO("pluginOption"));
	}

	public void sendInputName(String question, InputStringEvent e) {
		sendInputName(question, null, e);
	}

	public void sendInputName(String question, String description, InputStringEvent e) {
		getTempAttribs().setO("pluginEnterName", e);
		getPackets().sendInputNameScript(question);
		if (description != null)
			getPackets().setIFText(1110, 70, description);
		setCloseInterfacesEvent(() -> getTempAttribs().removeO("pluginEnterName"));
	}

	public void sendInputLongText(String question, InputStringEvent e) {
		getTempAttribs().setO("pluginEnterLongText", e);
		getPackets().sendInputLongTextScript(question);
		setCloseInterfacesEvent(() -> getTempAttribs().removeO("pluginEnterLongText"));
	}

	public void sendInputInteger(String question, InputIntegerEvent e) {
		getTempAttribs().setO("pluginInteger", e);
		getPackets().sendInputIntegerScript(question);
		setCloseInterfacesEvent(() -> getTempAttribs().removeO("pluginInteger"));
	}

	public void sendInputHSL(InputIntegerEvent e) {
		getTempAttribs().setO("pluginHSL", e);
		setCloseInterfacesEvent(() -> getTempAttribs().removeO("pluginHSL"));
	}

	public void sendInputForumQFC(InputStringEvent e) {
		getTempAttribs().setO("pluginQFCD", e);
		setCloseInterfacesEvent(() -> getTempAttribs().removeO("pluginQFCD"));
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

	public int getTemporaryMoveType() {
		return temporaryMovementType;
	}

	public void setTemporaryMoveType(int temporaryMovementType) {
		this.temporaryMovementType = temporaryMovementType;
	}

	public LocalPlayerUpdate getLocalPlayerUpdate() {
		return localPlayerUpdate;
	}

	public LocalNPCUpdate getLocalNPCUpdate() {
		return localNPCUpdate;
	}

	public int getDisplayMode() {
		return displayMode;
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

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
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
		getPackets().sendRunEnergy(this.runEnergy);
	}

	public boolean isResting() {
		return resting;
	}

	public void setResting(boolean resting) {
		this.resting = resting;
		sendRunButtonConfig();
	}

	public ActionManager getActionManager() {
		return actionManager;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
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

		if (getTempAttribs().getL("SOL_SPEC") > System.currentTimeMillis() && hit.getLook() == HitLook.MELEE_DAMAGE)
			hit.setDamage((int) (hit.getDamage() * 0.5));
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
		if (target instanceof Player opp)
			if (opp.getTempAttribs().getL("SOL_SPEC") >= System.currentTimeMillis())
				target.setNextSpotAnim(new SpotAnim(2320));
		getAuraManager().onOutgoingHit(hit);
		getControllerManager().processOutgoingHit(hit, target);
	}

	@Override
	public void handlePostHit(Hit hit) {

	}

	@Override
	public void sendDeath(final Entity source) {
		incrementCount("Deaths");
		if (prayer.hasPrayersOn() && !getTempAttribs().getB("startedDuel"))
			if (prayer.active(Prayer.RETRIBUTION)) {
				setNextSpotAnim(new SpotAnim(437));
				final Player target = this;
				if (isAtMultiArea())
					for (int regionId : getMapRegionsIds()) {
						Set<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
						if (playersIndexes != null)
							for (int playerIndex : playersIndexes) {
								Player player = World.getPlayers().get(playerIndex);
								if (player == null || !player.hasStarted() || player.isDead() || player.hasFinished() || !player.withinDistance(this, 1) || !player.isCanPvp() || !target.getControllerManager().canHit(player))
									continue;
								player.applyHit(new Hit(target, Utils.getRandomInclusive((int) (skills.getLevelForXp(Constants.PRAYER) * 2.5)), HitLook.TRUE_DAMAGE));
							}
						Set<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
						if (npcsIndexes != null)
							for (int npcIndex : npcsIndexes) {
								NPC npc = World.getNPCs().get(npcIndex);
								if (npc == null || npc.isDead() || npc.hasFinished() || !npc.withinDistance(this, 1) || !npc.getDefinitions().hasAttackOption() || !target.getControllerManager().canHit(npc))
									continue;
								npc.applyHit(new Hit(target, Utils.getRandomInclusive((int) (skills.getLevelForXp(Constants.PRAYER) * 2.5)), HitLook.TRUE_DAMAGE));
							}
					}
				else if (source != null && source != this && !source.isDead() && !source.hasFinished() && source.withinDistance(this, 1))
					source.applyHit(new Hit(target, Utils.getRandomInclusive((int) (skills.getLevelForXp(Constants.PRAYER) * 2.5)), HitLook.TRUE_DAMAGE));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						World.sendSpotAnim(target, new SpotAnim(438), new WorldTile(target.getX() - 1, target.getY(), target.getPlane()));
						World.sendSpotAnim(target, new SpotAnim(438), new WorldTile(target.getX() + 1, target.getY(), target.getPlane()));
						World.sendSpotAnim(target, new SpotAnim(438), new WorldTile(target.getX(), target.getY() - 1, target.getPlane()));
						World.sendSpotAnim(target, new SpotAnim(438), new WorldTile(target.getX(), target.getY() + 1, target.getPlane()));
						World.sendSpotAnim(target, new SpotAnim(438), new WorldTile(target.getX() - 1, target.getY() - 1, target.getPlane()));
						World.sendSpotAnim(target, new SpotAnim(438), new WorldTile(target.getX() - 1, target.getY() + 1, target.getPlane()));
						World.sendSpotAnim(target, new SpotAnim(438), new WorldTile(target.getX() + 1, target.getY() - 1, target.getPlane()));
						World.sendSpotAnim(target, new SpotAnim(438), new WorldTile(target.getX() + 1, target.getY() + 1, target.getPlane()));
					}
				});
			} else if (prayer.active(Prayer.WRATH)) {
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() + 2, getPlane()), 2260, 24, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY(), getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);

				World.sendProjectile(this, new WorldTile(getX() - 2, getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY(), getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);

				World.sendProjectile(this, new WorldTile(getX(), getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX(), getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				final Player target = this;
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						setNextSpotAnim(new SpotAnim(2259));

						if (isAtMultiArea())
							for (int regionId : getMapRegionsIds()) {
								Set<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
								if (playersIndexes != null)
									for (int playerIndex : playersIndexes) {
										Player player = World.getPlayers().get(playerIndex);
										if (player == null || !player.hasStarted() || player.isDead() || player.hasFinished() || !player.isCanPvp() || !player.withinDistance(target, 2) || !target.getControllerManager().canHit(player))
											continue;
										player.applyHit(new Hit(target, Utils.getRandomInclusive((skills.getLevelForXp(Constants.PRAYER) * 3)), HitLook.TRUE_DAMAGE));
									}
								Set<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
								if (npcsIndexes != null)
									for (int npcIndex : npcsIndexes) {
										NPC npc = World.getNPCs().get(npcIndex);
										if (npc == null || npc.isDead() || npc.hasFinished() || !npc.withinDistance(target, 2) || !npc.getDefinitions().hasAttackOption() || !target.getControllerManager().canHit(npc))
											continue;
										npc.applyHit(new Hit(target, Utils.getRandomInclusive((skills.getLevelForXp(Constants.PRAYER) * 3)), HitLook.TRUE_DAMAGE));
									}
							}
						else if (source != null && source != target && !source.isDead() && !source.hasFinished() && source.withinDistance(target, 2))
							source.applyHit(new Hit(target, Utils.getRandomInclusive((skills.getLevelForXp(Constants.PRAYER) * 3)), HitLook.TRUE_DAMAGE));

						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() + 2, getY() + 2, getPlane()));
						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() + 2, getY(), getPlane()));
						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() + 2, getY() - 2, getPlane()));

						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() - 2, getY() + 2, getPlane()));
						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() - 2, getY(), getPlane()));
						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() - 2, getY() - 2, getPlane()));

						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX(), getY() + 2, getPlane()));
						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX(), getY() - 2, getPlane()));

						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() + 1, getY() + 1, getPlane()));
						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() + 1, getY() - 1, getPlane()));
						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() - 1, getY() + 1, getPlane()));
						World.sendSpotAnim(target, new SpotAnim(2260), new WorldTile(getX() - 1, getY() - 1, getPlane()));
					}
				});
			}
		refreshDyingTime();
		setNextAnimation(new Animation(-1));
		if (!controllerManager.sendDeath())
			return;
		lock(7);
		stopAll();
		if (familiar != null)
			familiar.sendDeath(this);
		WorldTile lastTile = new WorldTile(this);
		if (isAtDynamicRegion())
			lastTile = getRandomGraveyardTile();
		final WorldTile deathTile = lastTile;
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(836));
				else if (loop == 1)
					sendMessage("Oh dear, you have died.");
				else if (loop == 2) {
					reset();
					if (source instanceof Player opp && opp.hasRights(Rights.ADMIN))
						setNextWorldTile(Settings.getConfig().getPlayerRespawnTile());
					else
						controllerManager.startController(new DeathOfficeController(deathTile, hasSkull()));
				} else if (loop == 4) {
					getPackets().sendMusicEffect(90);
					unlock();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public WorldTile getRandomGraveyardTile() {
		return new WorldTile(new WorldTile(2745, 3474, 0), 4);
	}

	public void sendItemsOnDeath(Player killer, boolean dropItems) {
		Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(this, true, dropItems, prayer.isProtectingItem());
		sendItemsOnDeath(killer, new WorldTile(this), new WorldTile(this), true, slots);
	}

	public void sendItemsOnDeath(Player killer, WorldTile deathTile, WorldTile respawnTile, boolean noGravestone, Integer[][] slots) {
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

	@Override
	public boolean inCombat() {
		return attackedByDelay > System.currentTimeMillis();
	}

	public void sendItemsOnDeath(Player killer) {
		if (hasRights(Rights.ADMIN) || Settings.getConfig().isDebug())
			return;
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
		for (Item item : containedItems)
			if (ItemConstants.isTradeable(item))
				World.addGroundItem(item, getLastWorldTile(), killer == null ? this : killer, true, 60);
			else {
				ItemDegrade deg = null;
				for(ItemDegrade d : ItemDegrade.values())
					if (d.getDegradedId() == item.getId() || d.getItemId() == item.getId()) {
						deg = d;
						break;
					}
				if (deg != null && deg.getBrokenId() != -1) {
					Item broken = new Item(deg.getBrokenId(), item.getAmount());
					if (!ItemConstants.isTradeable(broken) && (killer != null && killer != this)) {
						Item money = new Item(995, 1);
						money.setAmount(item.getDefinitions().getValue());
						World.addGroundItem(money, getLastWorldTile(), killer == null ? this : killer, true, 60);
					} else
						World.addGroundItem(broken, getLastWorldTile(), killer == null ? this : killer, true, 60);
				} else {
					Item money = new Item(995, 1);
					money.setAmount(item.getDefinitions().getValue());
					World.addGroundItem(money, getLastWorldTile(), killer == null ? this : killer, true, 60);
				}
			}
		getAppearance().generateAppearanceData();
	}


	public void sendOSItemsOnDeath(Player killer) {
		if (hasRights(Rights.ADMIN) || Settings.getConfig().isDebug())
			return;
		auraManager.removeAura();
		CopyOnWriteArrayList<Item> containedItems = new CopyOnWriteArrayList<>();
		for (int i = 0; i < 14; i++)
			if (equipment.getItem(i) != null && equipment.getItem(i).getId() != -1 && equipment.getItem(i).getAmount() != -1)
				containedItems.add(new Item(equipment.getItem(i).getId(), equipment.getItem(i).getAmount()));
		for (int i = 0; i < 28; i++)
			if (inventory.getItem(i) != null && inventory.getItem(i).getId() != -1 && inventory.getItem(i).getAmount() != -1)
				containedItems.add(new Item(getInventory().getItem(i).getId(), getInventory().getItem(i).getAmount()));
		if (containedItems.isEmpty())
			return;
		int keptAmount = 0;

		keptAmount = hasSkull() ? 0 : 3;
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
			this.sendMessage(lastItem.getDefinitions().getName() + ": " + lastItem.getDefinitions().getValue());
			keptItems.add(lastItem);
			containedItems.remove(lastItem);
			lastItem = new Item(1, 1);
		}
		inventory.reset();
		equipment.reset();
		for (Item item : keptItems)
			if (item.getId() != 1)
				getInventory().addItem(item);
		for (Item item : containedItems)
			World.addGroundItem(item, getLastWorldTile(), this, false, 60);
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
		setCanPvp(canPvp, false);
	}

	public PrayerManager getPrayer() {
		return prayer;
	}

	public boolean isLocked() {
		return lockDelay >= World.getServerTicks();
	}

	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	public void lock(int ticks) {
		lockDelay = World.getServerTicks() + ticks;
	}

	public void unlock() {
		lockDelay = 0;
	}

	public void useStairs(WorldTile dest) {
		useStairs(-1, dest, 1, 2);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
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

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message) {
		useStairs(emoteId, dest, useDelay, totalDelay, message, false);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message, final boolean resetAnimation) {
		stopAll();
		lock(totalDelay);
		if (emoteId != -1)
			setNextAnimation(new Animation(emoteId));
		if (useDelay == 0)
			setNextWorldTile(dest);
		else
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isDead())
						return;
					if (resetAnimation)
						setNextAnimation(new Animation(-1));
					setNextWorldTile(dest);
					if (message != null)
						sendMessage(message);
				}
			}, useDelay - 1);
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

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
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

	public CutscenesManager getCutscenesManager() {
		return cutscenesManager;
	}

	public void sendPublicChatMessage(PublicChatMessage message) {
		for (int regionId : getMapRegionsIds()) {
			Set<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player p = World.getPlayers().get(playerIndex);
				if (p == null || !p.hasStarted() || p.hasFinished() || p.getLocalPlayerUpdate().getLocalPlayers()[getIndex()] == null)
					continue;
				p.getPackets().sendPublicMessage(this, message);
			}
		}
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
		return familiar;
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
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

	public int getMovementType() {
		if (getTemporaryMoveType() != -1)
			return getTemporaryMoveType();
		return getRun() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
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

	public void performInstantSpecial(final int weaponId) {
		int specAmt = PlayerCombat.getSpecialAmmount(weaponId);
		if (combatDefinitions.hasRingOfVigour())
			specAmt *= 0.9;
		if (combatDefinitions.getSpecialAttackPercentage() < specAmt) {
			sendMessage("You don't have enough power left.");
			combatDefinitions.drainSpec(0);
			return;
		}

		combatDefinitions.switchUsingSpecialAttack();

		switch (weaponId) {
		case 4153:
		case 14679:
			combatDefinitions.switchUsingSpecialAttack();
			Entity target = (getActionManager().getAction() instanceof PlayerCombat combat) ? combat.getTarget() : getTempAttribs().getO("last_target");
			if (target != null) {
				if (!(target instanceof NPC n && n.isForceMultiAttacked()))
					if (!target.isAtMultiArea() || !isAtMultiArea()) {
						if ((getAttackedBy() != target && inCombat()) || (target.getAttackedBy() != this && target.inCombat()))
							return;
					}
				if (!(getActionManager().getAction() instanceof PlayerCombat combat) || combat.getTarget() != target)
					getActionManager().setAction(new PlayerCombat(target));
				PlayerCombat pcb = (PlayerCombat) getActionManager().getAction();
				if (pcb == null ||!inMeleeRange(target) || !PlayerCombat.specialExecute(this))
					return;
				setNextAnimation(new Animation(weaponId == 4153 ? 1667 : 10505));
				if (weaponId == 4153)
					setNextSpotAnim(new SpotAnim(340, 0, 96 << 16));
				pcb.delayNormalHit(weaponId, getCombatDefinitions().getAttackStyle(), pcb.getMeleeHit(this, pcb.getRandomMaxHit(this, weaponId, getCombatDefinitions().getAttackStyle(), false, true, 1.0, 1.1)));
			}
			break;
		case 1377:
		case 13472:
			setNextAnimation(new Animation(1056));
			setNextSpotAnim(new SpotAnim(246));
			setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
			int defence = (int) (skills.getLevelForXp(Constants.DEFENSE) * 0.90D);
			int attack = (int) (skills.getLevelForXp(Constants.ATTACK) * 0.90D);
			int range = (int) (skills.getLevelForXp(Constants.RANGE) * 0.90D);
			int magic = (int) (skills.getLevelForXp(Constants.MAGIC) * 0.90D);
			int strength = (int) (skills.getLevelForXp(Constants.STRENGTH) * 1.2D);
			skills.set(Constants.DEFENSE, defence);
			skills.set(Constants.ATTACK, attack);
			skills.set(Constants.RANGE, range);
			skills.set(Constants.MAGIC, magic);
			skills.set(Constants.STRENGTH, strength);
			combatDefinitions.drainSpec(specAmt);
			break;
		case 35:// Excalibur
		case 8280:
		case 14632:
			setNextAnimation(new Animation(1168));
			setNextSpotAnim(new SpotAnim(247));
			setNextForceTalk(new ForceTalk("For Camelot!"));
			final boolean enhanced = weaponId == 14632;
			skills.set(Constants.DEFENSE, enhanced ? (int) (skills.getLevelForXp(Constants.DEFENSE) * 1.15D) : (skills.getLevel(Constants.DEFENSE) + 8));
			WorldTasks.schedule(new WorldTask() {
				int count = 5;

				@Override
				public void run() {
					if (isDead() || hasFinished() || getHitpoints() >= getMaxHitpoints()) {
						stop();
						return;
					}
					heal(enhanced ? 80 : 40);
					if (count-- == 0) {
						stop();
						return;
					}
				}
			}, 4, 2);
			combatDefinitions.drainSpec(specAmt);
			break;
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
			setNextAnimation(new Animation(12804));
			setNextSpotAnim(new SpotAnim(2319));// 2320
			setNextSpotAnim(new SpotAnim(2321));
			getTempAttribs().setL("SOL_SPEC", System.currentTimeMillis() + 60000);
			combatDefinitions.drainSpec(specAmt);
			break;
		}
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

	/**
	 * Gets the killedQueenBlackDragon.
	 *
	 * @return The killedQueenBlackDragon.
	 */
	public boolean isKilledQueenBlackDragon() {
		return killedQueenBlackDragon;
	}

	/**
	 * Sets the killedQueenBlackDragon.
	 *
	 * @param killedQueenBlackDragon
	 *            The killedQueenBlackDragon to set.
	 */
	public void setKilledQueenBlackDragon(boolean killedQueenBlackDragon) {
		this.killedQueenBlackDragon = killedQueenBlackDragon;
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

	public void ladder(final WorldTile toTile) {
		setNextAnimation(new Animation(828));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextWorldTile(toTile);
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
		return familiar != null;
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
		if (!isKilledQueenBlackDragon() && !Settings.getConfig().isDebug()) {
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
		revenantImmune = 100; // 1 minute
		revenantAggro = 6000; // 1 hour
		sendMessage("<col=FF0000>You will not be harmed by revenants for 1 minute.");
		sendMessage("<col=FF0000>Revenants will have no aggression towards you for one hour.");
	}

	public void processForinthry() {
		if (revenantImmune == 50)
			sendMessage("<col=FF0000>Your immunity to revenants will expire in 30 seconds!");
		if (revenantAggro == 50)
			sendMessage("<col=FF0000>Revenants will become aggressive to you again in 30 seconds!");
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

	public long getTimeSinceLastClick() {
		if (lastClick == null)
			return Long.MAX_VALUE;
		return System.currentTimeMillis() - lastClick.getTimeMillis();
	}

	public void sendGodwarsKill(NPC npc) {
		boolean dropKey = false;
		if (Utils.getRandomInclusive(500) <= 10 && npc.getDefinitions().combatLevel <= 134)
			dropKey = true;
		if (npc.getId() >= 6247 && npc.getId() <= 6259) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.SARADOMIN);
			if (dropKey)
				World.addGroundItem(new Item(20124, 1), new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), this, false, 60);
			return;
		}
		if (npc.getId() >= 6260 && npc.getId() <= 6283) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.BANDOS);
			if (dropKey)
				World.addGroundItem(new Item(20122, 1), new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), this, false, 60);
			return;
		}
		if (npc.getId() >= 6222 && npc.getId() <= 6246) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.ARMADYL);
			if (dropKey)
				World.addGroundItem(new Item(20121, 1), new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), this, false, 60);
			return;
		}
		if (npc.getId() >= 6203 && npc.getId() <= 6221) {
			((GodwarsController) getControllerManager().getController()).sendKill(GodwarsController.ZAMORAK);
			if (dropKey)
				World.addGroundItem(new Item(20123, 1), new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), this, false, 60);
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
		setNextAnimation(new Animation(anim));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextWorldTile(tile);
			}
		}, 1);
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

	public ArrayList<Player> getNearbyFCMembers(NPC npc) {
		ArrayList<Player> eligible = new ArrayList<>();
		FCData fc = FCManager.getFCData(getSocial().getCurrentFriendsChat());
		if (fc == null) {
			sendMessage("Error loading active friend's chat. Could not lootshare.");
			eligible.add(this);
			return eligible;
		}
		for (Integer pId : World.getRegion(npc.getRegionId()).getPlayerIndexes()) {
			Player player = World.getPlayers().get(pId);
			if (player == null || !player.isRunning() || !player.isLootSharing() || !fc.getUsernames().contains(player.getUsername()))
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

	public void walkToAndExecute(WorldTile startTile, Runnable event) {
		int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(), getSize(), new FixedTileStrategy(startTile.getX(), startTile.getY()), true);
		int[] bufferX = RouteFinder.getLastPathBufferX();
		int[] bufferY = RouteFinder.getLastPathBufferY();
		int last = -1;
		if (steps == -1)
			return;
		for (int i = steps - 1; i >= 0; i--)
			if (!addWalkSteps(bufferX[i], bufferY[i], 25, true, true))
				break;
		if (last != -1) {
			WorldTile tile = new WorldTile(bufferX[last], bufferY[last], getPlane());
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

	public void startConversation(com.rs.game.player.content.dialogue.Dialogue dialogue) {
		startConversation(new Conversation(dialogue.finish()));
	}

	public boolean startConversation(Conversation conversation) {
		if (conversation.getCurrent() == null)
			return false;
		this.conversation = conversation;
		this.conversation.setPlayer(this);
		conversation.start();
		return true;
	}

	public void endConversation() {
		conversation = null;
		if (getInterfaceManager().containsChatBoxInter())
			getInterfaceManager().closeChatBoxInterface();
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void sm(String string) {
		sendMessage(string);
	}

	@Override
	public boolean canMove(Direction dir) {
		return getControllerManager().canMove(dir);
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

	public ReflectionCheck getReflectionCheck(int id) {
		return reflectionChecks.get(id);
	}

	public void addReflectionCheck(ReflectionCheck reflectionCheck) {
		reflectionChecks.put(reflectionCheck.getId(), reflectionCheck);
		getSession().writeToQueue(new ReflectionCheckRequest(reflectionCheck));
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

	public HashMap<Tools, Integer> getToolbelt() {
		return toolbelt;
	}

	public void processPackets() {
		Packet packet;
		while ((packet = session.getPacketQueue().poll()) != null) {
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

	public void addWalkSteps(WorldTile toTile, int maxSteps, boolean clip) {
		addWalkSteps(toTile.getX(), toTile.getY(), maxSteps, clip);
	}

	public void passThrough(WorldTile tile) {
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

	public Clan getClan() {
		return LobbyCommunicator.getClan(getAccount().getSocial().getClanName());
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

	public InteractionManager getInteractionManager() {
		return interactionManager;
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
				getDialogueManager().execute(new SimpleMessage(), "The HEX yell color you wanted to pick cannot be longer and shorter then 6.");
			else if (Utils.containsInvalidCharacter(color.toLowerCase()) || color.contains("_"))
				getDialogueManager().execute(new SimpleMessage(), "The requested yell color can only contain numeric and regular characters.");
			else {
				setYellColor(color);
				getDialogueManager().execute(new SimpleMessage(), "Your yell color has been changed to <col=" + getYellColor() + ">" + getYellColor() + "</col>.");
			}
		});
	}

	public void promptSetYellTitle() {
		sendInputName("Enter yell title:", title -> {
			if (title.length() > 20)
				getDialogueManager().execute(new SimpleMessage(), "Your yell title cannot be longer than 20 characters.");
			else if (Utils.containsBadCharacter(title))
				getDialogueManager().execute(new SimpleMessage(), "Keep your title as only letters and numbers..");
			else {
				setYellTitle(title);
				getAppearance().generateAppearanceData();
				getDialogueManager().execute(new SimpleMessage(), "Your yell title has been changed to " + getYellTitle() + ".");
			}
		});
	}

	public void promptSetTitle() {
		sendInputName("Enter title:", title -> {
			if (title.length() > 20)
				getDialogueManager().execute(new SimpleMessage(), "Your title cannot be longer than 20 characters.");
			else if (Utils.containsBadCharacter(title))
				getDialogueManager().execute(new SimpleMessage(), "Keep your title as only letters and numbers..");
			else if (title.toLowerCase().contains("ironman") || title.toLowerCase().contains("hard"))
				getDialogueManager().execute(new SimpleMessage(), "That title is reserved for special account types.");
			else {
				setTitle(title);
				getAppearance().generateAppearanceData();
				getDialogueManager().execute(new SimpleMessage(), "Your title has been changed to " + getTitle() + ".");
			}
		});
	}

	public void promptSetTitleColor() {
		sendInputName("Enter title color in HEX: (ex: 00FF00 for green)", color -> {
			if (color.length() != 6)
				getDialogueManager().execute(new SimpleMessage(), "HEX colors are 6 characters long bud.");
			else if (Utils.containsInvalidCharacter(color.toLowerCase()) || color.contains("_"))
				getDialogueManager().execute(new SimpleMessage(), "HEX colors just contain letters and numbers bud.");
			else {
				setTitleColor(color);
				getAppearance().generateAppearanceData();
				getDialogueManager().execute(new SimpleMessage(), "Your title has been changed to " + getTitle() + ".");
			}
		});
	}

	public void promptSetTitleShade() {
		sendInputName("Enter title shade color in HEX: (ex: 00FF00 for green)", color -> {
			if (color.length() != 6)
				getDialogueManager().execute(new SimpleMessage(), "HEX colors are 6 characters long bud.");
			else if (Utils.containsInvalidCharacter(color.toLowerCase()) || color.contains("_"))
				getDialogueManager().execute(new SimpleMessage(), "HEX colors just contain letters and numbers bud.");
			else {
				setTitleShading(color);
				getAppearance().generateAppearanceData();
				getDialogueManager().execute(new SimpleMessage(), "Your title has been changed to " + getTitle() + ".");
			}
		});
	}

	public void simpleDialogue(String message) {
		startConversation(new com.rs.game.player.content.dialogue.Dialogue(new SimpleStatement(message)));
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
