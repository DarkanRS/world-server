package com.rs.game.content.minigames.trawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.statements.SimpleStatement;
import com.rs.game.content.transportation.FadingScreen;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.item.ItemsContainer;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.RegionUtils;
import com.rs.utils.Ticks;

@PluginEventHandler
public class FishingTrawler {

	/*
		TODO: Proper contribution meter based on wiki info
		TODO: Full contribution meter provides additional 50% fish
	 */

	private static FishingTrawler instance;

	public static final RegionUtils.Area NO_WATER_SHIP = RegionUtils.getArea(WorldTile.of(1885, 4824, 0), WorldTile.of(1892, 4826, 0));
	public static final RegionUtils.Area WATER_SHIP = RegionUtils.getArea(WorldTile.of(2013, 4824, 0), WorldTile.of(2020, 4826, 0));
	private static final RegionUtils.Area CRASHED_SHIP = RegionUtils.getArea(WorldTile.of(1950, 4824, 0), WorldTile.of(1956, 4826, 0));

	public static RegionUtils.Area SHORE = RegionUtils.getArea(WorldTile.of(2670, 3221, 0), WorldTile.of(2675, 3223, 0));

	private static final WorldTile END_TILE = WorldTile.of(2666, 3160, 0);

	private static final String[] MONTY_MESSAGES = {
			"Let's get this net full with fishies!",
        	"Blistering barnacles!",
			"The water is coming in, matey!",
			"Land lubbers!",
			"That's the stuff, fill those holes!",
			"We'll all end up in a watery grave!",
        	"My mother could bail better than that!"
	};

	private static final String[] CRASHED_MONTY_MESSAGES = {
			"You'll need all of your strength...",
        	"Keep your head above water, shipmate!",
			"Arrrgh! We've sunk!",
			"No fishes for you today!",
			"You'll be joining Davey Jones!"
	};

	private static final int REPAIRED_LEAK = 2168;
	public static final int LEAK = 2167;
	private static final int REWARDS_INTERFACE = 367;
	private static final int REWARDS_CONTAINER = 18;
	private static final int REWARDS_KEY = 102;
	public static final int SWAMP_PASTE = 1941;

	private final List<Player> lobby;
	private final List<Player> game;

	private final List<GameObject> leaks;

	private final List<NPC> montys;
	private NPC crashedMonty;

	protected final HashMap<String, Integer> activity;
	private final List<String> activityWarned;

	private boolean running;
	private boolean shutdown;
	protected boolean ripped;
	protected boolean waterShip;
	private int time;
	private int nextCheer;
	private int nextCry;

	private boolean secondMessage;

	private int totalCaught;
	protected int waterLevel;

	public FishingTrawler() {
		lobby = new ArrayList<>();
		game = new ArrayList<>();
		leaks = new ArrayList<>();
		montys = new ArrayList<>();
		activity = new HashMap<>();
		activityWarned = new ArrayList<>();
		time = Ticks.fromMinutes(1);
		spawnRepairedLeaks();
		spawnMontys();
	}

	public void tick() {
		time--;
		if(nextCheer > 0) nextCheer--;
		if(nextCry > 0) nextCry--;
		if(!secondMessage && Utils.random(10) == 5) {
			String random = CRASHED_MONTY_MESSAGES[Utils.random(CRASHED_MONTY_MESSAGES.length)];
			crashedMonty.setNextForceTalk(new ForceTalk(random));
			if(random.contains("strength")) {
				secondMessage = true;
				WorldTasks.delay(2, () -> {
					secondMessage = false;
					crashedMonty.setNextForceTalk(new ForceTalk("...to make it back to shore."));
				});
			}
		}
		if(!running && lobby.size() == 0) {
			time = Ticks.fromMinutes(1);
			return;
		}
		if (World.SYSTEM_UPDATE_DELAY > 0 && !shutdown) {
			shutdown = true;
			end();
			//force those on boat off
			//stop people getting back on
		}
		if (time == 0) {
			if (running) {
				end();
				return;
			}
			start();
		}
		if (running) {
			tickGame();
			return;
		}
		if (time == Ticks.fromSeconds(30))
			sendLobbyMessage("Trawler will leave in 30 seconds.");
		if (time == Ticks.fromSeconds(20))
			sendLobbyMessage("Trawler will leave in 20 seconds.");
		if (time == Ticks.fromSeconds(10))
			sendLobbyMessage("Trawler will leave in 10 seconds.");
	}

	public void tickGame() {
		if(game.size() == 0) {
			end();
			return;
		}
		if(time % 10 == 0 && Utils.random(5) == 2) sendRandomMontyMessage();
		if(Utils.random(10) == 1) leak();
		if(!ripped && time % 10 == 0 && Utils.random(15) == 2) ripNet();
		if(!ripped && time % 10 == 0 && Utils.random(15) == 2) totalCaught++;
		int waterLevel = this.waterLevel;
		if(leaks.size() > 0 && time % 5 == 0)
			waterLevel += leaks.size();
		if(leaks.size() > 3 && nextCry == 0) {
			montys.forEach(npc -> npc.setNextAnimation(new Animation(860)));
			nextCry = 20;
		}
		if(waterLevel >= 30 && this.waterLevel < 30 && !waterShip)
			switchToWaterShip();
		if(waterLevel >= 180) crash();
		this.waterLevel = waterLevel;
		game.forEach(this::refreshInterface);
		for(Iterator<Player> it = game.iterator(); it.hasNext();) {
			Player player = it.next();
			int activity = this.activity.get(player.getUsername());
			activity -= 3;
			if(activity <= 0) {
				crash(player);
				it.remove();
				continue;
			}
			if(activity < 300 && !activityWarned.contains(player.getUsername())) {
				activityWarned.add(player.getUsername());
				player.sendMessage("<col=FF0000>Warning</col>: Your activity bar is getting low! Help out in the trawler to refill it!");
			}
			this.activity.put(player.getUsername(), activity);
		}
	}

	public void start() {
		time = Ticks.fromMinutes(12);
		totalCaught = 0;
		ripped = false;
		waterShip = false;
		waterLevel = 0;
		running = true;
		for(final Player player : lobby) {
			FadingScreen.fade(player, 2, () -> {
				player.setNextWorldTile(NO_WATER_SHIP.getRandomTile());
				player.getInterfaceManager().sendOverlay(15);
				player.getInterfaceManager().sendInterface(368);
				game.forEach(this::refreshInterface);
			});
			player.getControllerManager().startController(new FishingTrawlerGameController());
			game.add(player);
			activity.put(player.getUsername(), 1000);
		}
		lobby.clear();
	}

	public void end() {
		if (!shutdown) sendLobbyMessage("Trawler will leave in 1 minute.");
		else {
			String message = "The trawler minigame has been ended as a system update is pending. Please come back after the update.";
			sendLobbyMessage(message);
			sendGameMessage(message);
		}
		time = Ticks.fromMinutes(1);
		running = false;
		int fishCaught = game.size() == 0 ? 0 : totalCaught / game.size();
		for(GameObject leak : leaks)
			World.removeObject(leak);
		for (final Player player : game) {
			player.getInterfaceManager().removeOverlay();
			player.setNextWorldTile(END_TILE);
			player.getControllerManager().forceStop();
			player.sendMessage("Murphy turns the boat towards shore.");
			ItemsContainer<Item> items = Rewards.generateRewards(player.getSkills().getLevel(Skills.FISHING), fishCaught);
			for(Item item : items.array()) {
				if(item == null) continue;
				if(player.getTrawlerRewards() == null)
					player.setTrawlerRewards(new ItemsContainer<>(28, true));
				player.getTrawlerRewards().add(item);
			}
		}
		game.clear();
		leaks.clear();
		activity.clear();
		activityWarned.clear();
	}

	public void crash() {
		time = Ticks.fromMinutes(1);
		running = false;
		leaks.forEach(World::removeObject);
		game.forEach(this::crash);
		leaks.clear();
		game.clear();
	}

	public void crash(Player player) {
		player.getInterfaceManager().removeOverlay();
		player.setNextWorldTile(CRASHED_SHIP.getRandomTile());
		player.getControllerManager().forceStop();
		player.getControllerManager().startController(new FishingTrawlerCrashedController());
		Equipment.remove(player, Equipment.WEAPON);
		Equipment.remove(player, Equipment.SHIELD);
		player.getAppearance().setBAS(152);
	}

	public void leak() {
		if(leaks.size() == 16) return;
		RegionUtils.Area shipArea = onWaterShip() ? WATER_SHIP : NO_WATER_SHIP;
		while(true) {
			int y = Utils.random(2) == 1 ? shipArea.getY() - 1 : shipArea.getY() + 2;
			int x = shipArea.getX() + Utils.random(8);
			WorldTile tile = WorldTile.of(x, y, 0);
			int rotation = tile.getY() == shipArea.getY()-1 || tile.getY() == shipArea.getY()-1 ? 3 : 1;
			GameObject object = World.getSpawnedObject(tile);
			if(object != null && object.getId() == LEAK) continue;
			GameObject leak = new GameObject(LEAK, ObjectType.SCENERY_INTERACT, rotation, tile);
			World.spawnObject(leak);
			leaks.add(leak);
			break;
		}

	}

	public void ripNet() {
		ripped = true;
		sendGameMessage("The net has ripped!");
		sendMontyMessage("Arrh! Check that net!");
	}

	public void refreshInterface(Player player) {
		player.getVars().setVar(1436, activity.get(player.getUsername()));
		player.getPackets().sendVarc(817, waterLevel);
		player.getPackets().sendVarc(818, ripped ? 1 :0);
		player.getPackets().sendVarc(819, totalCaught*2);
		player.getPackets().sendVarc(820, time / 100);
	}

	public boolean onWaterShip() {
		return waterShip;
	}

	public void switchToWaterShip() {
		waterShip = true;
		game.forEach(player -> player.setNextWorldTile(player.transform(128, 0)));
		List<WorldTile> tiles = new ArrayList<>();
		for(GameObject leak : leaks) {
			tiles.add(WorldTile.of(leak.getTile()).transform(128, 0));
			World.removeObject(leak);
		}
		leaks.clear();
		for(WorldTile tile : tiles) {
			int rotation = tile.getY() == WATER_SHIP.getY()-1 || tile.getY() == WATER_SHIP.getY()-1 ? 3 : 1;
			GameObject leak = new GameObject(LEAK, ObjectType.SCENERY_INTERACT, rotation, tile);
			World.spawnObject(leak);
			leaks.add(leak);
		}
	}

	public void cheerMonty() {
		if(nextCheer > 0) return;
		montys.forEach(npc -> npc.setNextAnimation(new Animation(862)));
		nextCheer = 20;
	}

	public void repairLeak(GameObject object) {
		leaks.remove(object);
		World.removeObject(object);
		int rotation = object.getY() == NO_WATER_SHIP.getY()-1 || object.getY() == WATER_SHIP.getY()-1 ? 3 : 1;
		World.spawnObject(new GameObject(REPAIRED_LEAK, ObjectType.SCENERY_INTERACT, rotation, WorldTile.of(object.getTile())));
	}

	public void spawnRepairedLeaks() {
		for(int x = 0; x < 8; x++) {
			WorldTile[] tiles = {
					WorldTile.of(NO_WATER_SHIP.getX()+x, NO_WATER_SHIP.getY()-1, 0),
					WorldTile.of(NO_WATER_SHIP.getX()+x, NO_WATER_SHIP.getY()+2, 0),
					WorldTile.of(WATER_SHIP.getX()+x, WATER_SHIP.getY()-1, 0),
					WorldTile.of(WATER_SHIP.getX()+x, WATER_SHIP.getY()+2, 0)
			};
			for(WorldTile tile : tiles) {
				int rotation = tile.getY() == NO_WATER_SHIP.getY()-1 || tile.getY() == WATER_SHIP.getY()-1 ? 3 : 1;
				World.spawnObject(new GameObject(REPAIRED_LEAK, ObjectType.SCENERY_INTERACT, rotation, tile));
			}
		}
	}

	public void spawnMontys() {
		montys.add(new NPC(463, WorldTile.of(1887, 4825, 0), false));
		montys.add(new NPC(463, WorldTile.of(2015, 4825, 0), false));
		crashedMonty = new NPC(463, WorldTile.of(1947, 4827, 0), false);
	}

	public void sendGameMessage(String message) {
		game.forEach(player -> player.sendMessage(message));
	}

	public void sendLobbyMessage(String message) {
		lobby.forEach(player -> player.sendMessage(message));
	}

	public void sendMontyMessage(String message) {
		montys.forEach(monty -> monty.setNextForceTalk(new ForceTalk(message)));
	}

	public void sendRandomMontyMessage() {
		String message;
		if(ripped) message = "Arrh! Check that net!";
		else if(waterLevel > 20 && waterLevel < 30) message = "You'll need to start bailing soon!";
		else message = MONTY_MESSAGES[Utils.random(MONTY_MESSAGES.length)];
		sendMontyMessage(message);
	}

	public boolean isRipped() {
		return ripped;
	}

	public void setRipped(boolean ripped) {
		this.ripped = ripped;
	}

	public boolean isWaterShip() {
		return waterShip;
	}

	public int getWaterLevel() {
		return waterLevel;
	}

	public void setWaterLevel(int waterLevel) {
		this.waterLevel = waterLevel;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setFishCaught(int fishCaught) {
		this.totalCaught = fishCaught;
	}

	public HashMap<String, Integer> getActivity() {
		return activity;
	}

	public void addActivity(Player player, int inc) {
		if(!activity.containsKey(player.getUsername())) return;
		int activity = this.activity.get(player.getUsername());
		activity += inc;
		if(activity >= 1000) activity = 1000;
		this.activity.put(player.getUsername(), activity);
	}

	public boolean isTrawlerMonty(NPC npc) {
		return montys.stream().anyMatch(n -> n.getIndex() == npc.getIndex());
	}

	public void removeLobbyPlayer(Player player) {
		lobby.remove(player);
	}

	public void removeGamePlayer(Player player) {
		game.remove(player);
	}

	public static ObjectClickHandler barrelClick = new ObjectClickHandler(true, new Object[] { 2159, 2160 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getObjectId() == 2160) {
				e.getPlayer().startConversation(new Dialogue(new SimpleStatement("I can't get a grip on it!")));
				return;
			}
			e.getPlayer().startConversation(new Dialogue(new SimpleStatement("You climb onto the floating barrel and begin to kick your way to the shore.")));
			FadingScreen.fade(e.getPlayer(), 2, () -> {
				e.getPlayer().sendMessage("You make it to the shore tired and weary.");
				e.getPlayer().applyHit(new Hit(20, Hit.HitLook.TRUE_DAMAGE));
				e.getPlayer().setNextWorldTile(SHORE.getRandomTile());
				e.getPlayer().getControllerManager().forceStop();
				e.getPlayer().getAppearance().setBAS(-1);
				e.getPlayer().endConversation();
			});
		}
	};

	public static ButtonClickHandler rewardsInterfaceHandler = new ButtonClickHandler(367) {
		@Override
		public void handle(ButtonClickEvent e) {
			Item item = e.getPlayer().getTrawlerRewards().get(e.getSlotId());
			if(item == null) return;
			if(item.getId() != e.getSlotId2()) {
				Logger.error(FishingTrawler.class, "rewardsInterfaceHandler", "Trawler item "+item.getId()+" does not match "+e.getSlotId2());
				return;
			}
			int amount = e.getPacket() == ClientPacket.IF_OP1 ? 1 : -1;
			if(e.getPacket() == ClientPacket.IF_OP3) {
				e.getPlayer().getTrawlerRewards().set(e.getSlotId(), null);
				e.getPlayer().getPackets().sendItems(REWARDS_KEY, e.getPlayer().getTrawlerRewards().array());
				return;
			}
			e.getPlayer().getTrawlerRewards().set(e.getSlotId(), amount == -1 ? null : new Item(item.getId(), item.getAmount() -  1));
			e.getPlayer().getPackets().sendItems(REWARDS_KEY, e.getPlayer().getTrawlerRewards().array());
			e.getPlayer().getInventory().addItem(item.getId(), amount == -1 ? item.getAmount() : amount);
		}
	};

	public static ObjectClickHandler rewardsNetClick = new ObjectClickHandler(new Object[] { 2166 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getPlayer().getTrawlerRewards() == null || e.getPlayer().getTrawlerRewards().isEmpty()) {
				e.getPlayer().playerDialogue(HeadE.CONFUSED, "I better not steal other people's fish!");
				return;
			}
			IFEvents params = new IFEvents(REWARDS_INTERFACE, REWARDS_CONTAINER, 0, 27)
					.enableRightClickOptions(0, 1, 2, 3)
					.enableDrag();
			e.getPlayer().getInterfaceManager().sendInterface(REWARDS_INTERFACE);
			e.getPlayer().getPackets().sendItems(REWARDS_KEY, e.getPlayer().getTrawlerRewards());
			e.getPlayer().getPackets().setIFEvents(params);
			e.getPlayer().getPackets().sendInterSetItemsOptionsScript(REWARDS_INTERFACE, REWARDS_CONTAINER, REWARDS_KEY, 4, 7, "Withdraw-1", "Withdraw-all", "Discard-all", "Examine");
			e.getPlayer().setCloseInterfacesEvent(() -> {
				for(final Item item : e.getPlayer().getTrawlerRewards().array()) {
					if(item == null) continue;
					World.addGroundItem(item, WorldTile.of(e.getPlayer().getTile()), e.getPlayer(), false, 180);
				}
				e.getPlayer().getTrawlerRewards().clear();
			});
		}
	};

	public static ObjectClickHandler gangplankClick = new ObjectClickHandler(true, new Object[]{ 2178, 2179 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getPlane() == 1) {
				e.getPlayer().lock();
				instance.lobby.remove(e.getPlayer());
				e.getPlayer().getControllerManager().forceStop();
				e.getPlayer().setNextWorldTile(WorldTile.of(2674, 3170, 0));
				WorldTasks.schedule(new WorldTask() {
					private boolean tick;
					private boolean run;
					@Override
					public void run() {
						if(!tick) {
							run = e.getPlayer().getRun();
							e.getPlayer().setRunHidden(false);
							e.getPlayer().addWalkSteps(WorldTile.of(2676, 3170, 0), 10, false);
							tick = true;
							return;
						}
						stop();
						e.getPlayer().setRunHidden(run);
						e.getPlayer().unlock();
					}
				}, 0, 1);
			} else {
				if(e.getPlayer().getSkills().getLevelForXp(Skills.FISHING) < 15) {
					e.getPlayer().startConversation(new Dialogue(new SimpleStatement("You need at least 15 fishing to be able to go out on the ship with Murphy.")));
					return;
				}
				e.getPlayer().lock();
				WorldTile toTile = WorldTile.of(2674, 3170, 0);
				boolean run = e.getPlayer().getRun();
				e.getPlayer().setRunHidden(false);
				e.getPlayer().addWalkSteps(toTile, 20, false);
				WorldTasks.schedule(new WorldTask() {
					private boolean tick;
					@Override
					public void run() {
						if(!tick) {
							instance.lobby.add(e.getPlayer());
							e.getPlayer().getControllerManager().startController(new FishingTrawlerLobbyController());
							e.getPlayer().setNextWorldTile(WorldTile.of(2673, 3170, 1));
							e.getPlayer().setRunHidden(run);
							e.getPlayer().unlock();
							if(!instance.running) {
								e.getPlayer().startConversation(new Dialogue(new SimpleStatement("Trawler will leave in 1 minute.", "If you have a team get them on board now!")));
								e.getPlayer().sendMessage("Trawler will leave in 1 minute.");
							} else {
								e.getPlayer().startConversation(new Dialogue(new SimpleStatement("There is already someone on a fishing trip!", "Try back in a few minutes.")));
								e.getPlayer().sendMessage("There is already someone on a fishing trip!");
								e.getPlayer().sendMessage("Try back in a few minutes.");
							}
							tick = true;
							return;
						}
						e.getPlayer().addWalkSteps(WorldTile.of(2672, 3170, 1), 10, true);
						stop();
					}
				}, 2, 0);
			}
		}
	};

	@ServerStartupEvent
	public static void init() {
		instance = new FishingTrawler();
		WorldTasks.schedule(1, 0, () -> instance.tick());
	}

	public static FishingTrawler getInstance() {
		return instance;
	}

}
