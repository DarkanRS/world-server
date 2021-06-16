package com.rs.game.player.content.skills.dungeoneering;

public enum KinshipPerk {
	TANK(), //DONE
	TACTICIAN(), //DONE
	BERSERKER(), //DONE
	SNIPER(), //TODO
	KEEN_EYE(), //DONE
	DESPERADO(), //DONE
	BLAZER(), //DONE
	BLASTER(), //TODO
	BLITZER(), //DONE
	MEDIC(), //DONE
	GATHERER(), //TODO
	ARTISAN(); //TODO
	
	public int getVarbit() {
		return 8053 + ordinal();
	}
	
	public int getItemId() {
		return 18817 + ordinal();
	}
}
