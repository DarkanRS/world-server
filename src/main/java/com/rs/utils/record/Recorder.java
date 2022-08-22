package com.rs.utils.record;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Utils;
import com.rs.utils.record.impl.ClickHW;
import com.rs.utils.record.impl.MouseMove;

public class Recorder {	
	private Player player;
	private Deque<RecordedAction> actions = new ArrayDeque<>();
	private int sizeLimit = 100;
	
	public Recorder(Player player) {
		this.player = player;
		this.sizeLimit = 100;
	}
	
	public void record(RecordedAction action) {
		player.refreshIdleTime();
		actions.offerLast(action);
		if (actions.size() > sizeLimit)
			actions.poll();
	}

	public long getTicksSinceLastAction() {
		if (actions.peekLast() == null)
			return Long.MAX_VALUE;
		return World.getServerTicks() - actions.peekLast().getTick();
	}

	public int getSizeLimit() {
		return sizeLimit;
	}

	public void setSizeLimit(int sizeLimit) {
		actions.clear();
		this.sizeLimit = sizeLimit;
	}
	
	public static void showConcatenatedActions(Player player, String... displayNames) {
		List<SimpleEntry<String, RecordedAction>> actions = getActionsFor(displayNames);
		if (actions == null) {
			player.sendMessage("Actions not found for one or more players.");
			return;
		}
		player.getPackets().sendDevConsoleMessage("Past actions for:  " + Utils.concat(displayNames));
		SimpleEntry<String, RecordedAction> prev = null;
		long startTime = actions.get(0).getValue().getTimeLogged();
		for (SimpleEntry<String, RecordedAction> action : actions) {
			consoleLogAction(player, action, prev, startTime);
			prev = action;
		}
	}
	
	private static List<SimpleEntry<String, RecordedAction>> getActionsFor(String... displayNames) {
		List<SimpleEntry<String, RecordedAction>> actions = new ArrayList<>();
		for (String name : displayNames) {
			Player target = World.getPlayerByDisplay(name);
			if (target == null || target.getRecorder() == null)
				return null;
			for (RecordedAction click : target.getRecorder().actions)
				actions.add(new SimpleEntry<String, RecordedAction>(name, click));
		}
		if (actions.isEmpty())
			return null;
		actions.sort((e1, e2) -> e1.getValue().compareTo(e2.getValue()));
		return actions;
	}

	public static void watchPlayers(Player player, String... displayNames) {
		Set<RecordedAction> printed = new HashSet<>();
		List<SimpleEntry<String, RecordedAction>> initialActions = getActionsFor(displayNames);
		if (initialActions == null || initialActions.isEmpty())
			return;
		long startTime = initialActions.get(0).getValue().getTimeLogged();
		player.getPackets().sendDevConsoleMessage("Starting watch for: " + Arrays.toString(displayNames));
		WorldTasks.scheduleTimer(tick -> {
			if (player == null || !player.isRunning() || player.hasFinished())
				return false;
			List<SimpleEntry<String, RecordedAction>> actions = getActionsFor(displayNames);
			player.getNSV().setO("lastWatchActionLogged", initialActions.get(0));
			if (actions == null || actions.isEmpty() || player.getNSV().getB("stopWatchActionLoop")) {
				player.getPackets().sendDevConsoleMessage("Watching stopped. Player went offline.");
				player.getNSV().removeB("stopWatchActionLoop");
				return false;
			}
			for (SimpleEntry<String, RecordedAction> action : actions) {
				if (printed.contains(action.getValue()))
					continue;
				consoleLogAction(player, action, player.getNSV().getO("lastWatchActionLogged"), startTime);
				player.getNSV().setO("lastWatchActionLogged", action);
				printed.add(action.getValue());
			}
			return true;
		});
	}
	
	private static void consoleLogAction(Player player, SimpleEntry<String, RecordedAction> action, SimpleEntry<String, RecordedAction> prev, long startTime) {
		boolean flag = false;
		if (prev != null) {
			if (Math.abs((int) (action.getValue().getTimeLogged() - prev.getValue().getTimeLogged())) < 300 && !prev.getKey().equals(action.getKey()))
				flag = true;
		}
		if (action.getValue() instanceof ClickHW hw && !hw.isHardware())
			flag = true;
		if (action.getValue() instanceof MouseMove mm && mm.containsSoftwareClicks())
			flag = true;
		player.getPackets().sendDevConsoleMessage((flag ? "<shad=000000><col=FF0000>" : "") + "[" + formatTimePrecise((action.getValue().getTimeLogged() - startTime)) + "] " + action.getKey() + ": " + action.getValue().toString());
	}

	private static String formatTimePrecise(long time) {
		int millis = (int) (time % 1000);
		int seconds = (int) (time / 1000L);
		int minutes = (int) (seconds / 60);
		seconds = seconds % 60;
		minutes = minutes % 60;
		StringBuilder string = new StringBuilder();
		string.append(String.format("%02d", minutes));
		string.append(":" + String.format("%02d", seconds));
		string.append(":" + String.format("%04d", millis));
		return string.toString();
	}
}
