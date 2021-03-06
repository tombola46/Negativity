package com.elikill58.negativity.universal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.universal.adapter.Adapter;

/**
 * Contains player-related data that can be accessed when the player is offline.
 */
public final class NegativityAccount {

	private final UUID playerId;
	private String lang, playerName;
	private final Minerate minerate;
	private int mostClicksPerSecond;
	private final Map<String, Integer> warns;
	private final long creationTime;

	public NegativityAccount(UUID playerId) {
		this(playerId, null, TranslatedMessages.getDefaultLang(), new Minerate(), 0, new HashMap<>(), System.currentTimeMillis());
	}

	public NegativityAccount(UUID playerId, String playerName, String lang, Minerate minerate, int mostClicksPerSecond, Map<String, Integer> warns, long creationTime) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.lang = lang;
		this.minerate = minerate;
		this.mostClicksPerSecond = mostClicksPerSecond;
		this.warns = warns;
		this.creationTime = creationTime;
	}

	public UUID getPlayerId() {
		return playerId;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Minerate getMinerate() {
		return minerate;
	}

	public int getMostClicksPerSecond() {
		return mostClicksPerSecond;
	}

	public void setMostClicksPerSecond(int mostClicksPerSecond) {
		this.mostClicksPerSecond = mostClicksPerSecond;
	}

	public int getWarn(String cheatKey) {
		return warns.getOrDefault(cheatKey, 0);
	}

	public int getWarn(Cheat cheat) {
		return getWarn(cheat.getKey());
	}

	public void setWarnCount(Cheat cheat, int count) {
		setWarnCount(cheat.getKey(), count);
	}

	public void setWarnCount(String cheatKey, int count) {
		warns.put(cheatKey, count);
	}

	public Map<String, Integer> getAllWarns() {
		return Collections.unmodifiableMap(warns);
	}

	@NonNull
	public static NegativityAccount get(UUID accountId) {
		return Adapter.getAdapter().getAccountManager().getNow(accountId);
	}

	public long getCreationTime() {
		return creationTime;
	}
}
