package com.rs.utils.music;

public class Voice {
	String category;
	int[] voiceIDs;

	public Voice(String category, int[] voiceIDs) {
		this.category = category;
		this.voiceIDs = voiceIDs;
	}

	public String getCategory() {
		return category;
	}

	public int[] getVoiceIDs() {
		return voiceIDs;
	}
}
