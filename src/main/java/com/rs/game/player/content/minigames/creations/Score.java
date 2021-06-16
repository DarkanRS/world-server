package com.rs.game.player.content.minigames.creations;

import java.util.List;

public class Score {

	/**
	 * Name of the player that this score belongs to. (stored here so if player goes
	 * offline it's still possible to show name in highscores interface)
	 */
	private String name;
	/**
	 * Team of the player.
	 */
	private boolean team;
	/**
	 * Gathering score.
	 */
	private int gathering;
	/**
	 * Processing score.
	 */
	private int processing;
	/**
	 * Depositing score.
	 */
	private int depositing;
	/**
	 * Withdrawing score.
	 */
	private int withdrawing;
	/**
	 * Damaging score.
	 */
	private int damaging;
	/**
	 * Amount of players killed.
	 */
	private int killed;
	/**
	 * Amount of deaths.
	 */
	private int died;

	public Score(String name, boolean team) {
		this.name = name;
		this.team = team;
	}

	public void updateGathering(int delta) {
		gathering += delta;
	}

	public void updateProcessing(int delta) {
		processing += delta;
	}

	public void updateDepositing(int delta) {
		depositing += delta;
	}

	public void updateWithdrawing(int delta) {
		withdrawing += delta;
	}

	public void updateDamaging(int delta) {
		damaging += delta;
	}

	public void updateKilled(int delta) {
		killed += delta;
	}

	public void updateDied(int delta) {
		died += delta;
	}

	/**
	 * Calculate's base total score (excludes kill/death calculation).
	 */
	public int total(boolean winBoost) {
		int tot = gathering + processing + ((depositing - withdrawing) * 2) + damaging;
		if (winBoost)
			tot = (int) (tot * 1.1D);
		return tot;
	}

	public String getName() {
		return name;
	}

	public boolean getTeam() {
		return team;
	}

	public int getGathering() {
		return gathering;
	}

	public int getProcessing() {
		return processing;
	}

	public int getDepositing() {
		return depositing;
	}

	public int getWithdrawing() {
		return withdrawing;
	}

	public int getDamaging() {
		return damaging;
	}

	public int getKilled() {
		return killed;
	}

	public int getDied() {
		return died;
	}

	public static int totalXP(List<Score> scores, boolean team, boolean winner) {
		int total = 0;
		for (Score score : scores)
			if (score.getTeam() == team)
				total += score.total(winner);
		return total;
	}

	public static Score highestTotal(List<Score> scores, int winnerTeam) {
		Score highest = null;
		int highests = Integer.MIN_VALUE;
		for (Score score : scores) {
			int total = score.total((score.getTeam() ? 2 : 1) == winnerTeam);
			if (highest == null || total > highests) {
				highest = score;
				highests = total;
			}
		}
		return highest;
	}

	public static Score lowestTotal(List<Score> scores, int winnerTeam) {
		Score lowest = null;
		int lowests = Integer.MAX_VALUE;
		for (Score score : scores) {
			int total = score.total((score.getTeam() ? 2 : 1) == winnerTeam);
			if (lowest == null || total < lowests) {
				lowest = score;
				lowests = total;
			}
		}
		return lowest;
	}

	public static Score mostKills(List<Score> scores) {
		Score most = null;
		int mosts = Integer.MIN_VALUE;
		for (Score score : scores) {
			int s = score.getKilled();
			if (most == null || s > mosts) {
				most = score;
				mosts = s;
			}
		}
		return most;
	}

	public static Score mostDeaths(List<Score> scores) {
		Score most = null;
		int mosts = Integer.MIN_VALUE;
		for (Score score : scores) {
			int s = score.getDied();
			if (most == null || s > mosts) {
				most = score;
				mosts = s;
			}
		}
		return most;
	}

	public static Score mostGathered(List<Score> scores) {
		Score most = null;
		int mosts = Integer.MIN_VALUE;
		for (Score score : scores) {
			int s = score.getGathering();
			if (most == null || s > mosts) {
				most = score;
				mosts = s;
			}
		}
		return most;
	}

	public static Score mostProcessed(List<Score> scores) {
		Score most = null;
		int mosts = Integer.MIN_VALUE;
		for (Score score : scores) {
			int s = score.getProcessing();
			if (most == null || s > mosts) {
				most = score;
				mosts = s;
			}
		}
		return most;
	}

	public static Score mostDeposited(List<Score> scores) {
		Score most = null;
		int mosts = Integer.MIN_VALUE;
		for (Score score : scores) {
			int s = (score.getDepositing() - score.getWithdrawing()) * 2;
			if (most == null || s > mosts) {
				most = score;
				mosts = s;
			}
		}
		return most;
	}

	public static Score mostDamaged(List<Score> scores) {
		Score most = null;
		int mosts = Integer.MIN_VALUE;
		for (Score score : scores) {
			int s = score.getDamaging();
			if (most == null || s > mosts) {
				most = score;
				mosts = s;
			}
		}
		return most;
	}
}
