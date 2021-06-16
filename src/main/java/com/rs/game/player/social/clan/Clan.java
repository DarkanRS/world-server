package com.rs.game.player.social.clan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.model.Account;
import com.rs.lib.model.ClanMember;
import com.rs.lib.model.ClanRank;
import com.rs.lib.util.Utils;

public class Clan {
	
	private String ownerName;
	private String name;
	private List<ClanMember> members;
	private Set<String> bannedUsers;
	private int timeZone;
	private boolean recruiting;
	private boolean isClanTime;
	private int worldId;
	private int clanFlag;
	private boolean guestsEnabled;
	private boolean guestsCanTalk;	
	private String threadId;
	private String motto;
	private int motifTop, motifBottom;
	private int[] motifColors;
	private int rankToKick;
	
	public Clan(String name, Account owner) {
		setDefaults();
		this.name = Utils.formatPlayerNameForDisplay(name);
		this.ownerName = owner.getUsername();
		this.members = new ArrayList<ClanMember>();
		this.bannedUsers = new HashSet<String>();
		this.addMember(owner, ClanRank.OWNER);
	}
	
	public void setDefaults() {
		recruiting = true;
		guestsEnabled = true;
		guestsCanTalk = true;
		worldId = 1;
		motifColors = Arrays.copyOf(ItemDefinitions.getDefs(20709).originalModelColors, 4);
	}

	private void addMember(Account account, ClanRank rank) {
		this.members.add(new ClanMember(account, rank));
	}
	
	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public List<ClanMember> getMembers() {
		return members;
	}

	public void setMembers(List<ClanMember> members) {
		this.members = members;
	}

	public Set<String> getBannedUsers() {
		return bannedUsers;
	}

	public void setBannedUsers(Set<String> bannedUsers) {
		this.bannedUsers = bannedUsers;
	}

	public int getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(int timeZone) {
		this.timeZone = timeZone;
	}

	public boolean isRecruiting() {
		return recruiting;
	}

	public void setRecruiting(boolean recruiting) {
		this.recruiting = recruiting;
	}

	public boolean isClanTime() {
		return isClanTime;
	}

	public void setClanTime(boolean isClanTime) {
		this.isClanTime = isClanTime;
	}

	public int getWorldId() {
		return worldId;
	}

	public void setWorldId(int worldId) {
		this.worldId = worldId;
	}

	public int getClanFlag() {
		return clanFlag;
	}

	public void setClanFlag(int clanFlag) {
		this.clanFlag = clanFlag;
	}

	public boolean isGuestsEnabled() {
		return guestsEnabled;
	}

	public void setGuestsEnabled(boolean guestsEnabled) {
		this.guestsEnabled = guestsEnabled;
	}

	public boolean isGuestsCanTalk() {
		return guestsCanTalk;
	}

	public void setGuestsCanTalk(boolean guestsCanTalk) {
		this.guestsCanTalk = guestsCanTalk;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getMotto() {
		return motto;
	}

	public void setMotto(String motto) {
		this.motto = motto;
	}

	public int getMotifTop() {
		return motifTop;
	}

	public void setMotifTop(int motifTop) {
		this.motifTop = motifTop;
	}

	public int getMotifBottom() {
		return motifBottom;
	}

	public void setMotifBottom(int motifBottom) {
		this.motifBottom = motifBottom;
	}

	public int[] getMotifColors() {
		return motifColors;
	}

	public void setMotifColors(int[] motifColors) {
		this.motifColors = motifColors;
	}

	public int getRankToKick() {
		return rankToKick;
	}

	public void setRankToKick(int rankToKick) {
		this.rankToKick = rankToKick;
	}

	public String getName() {
		return name;
	}

}
