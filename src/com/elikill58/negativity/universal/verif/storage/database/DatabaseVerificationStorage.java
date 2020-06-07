package com.elikill58.negativity.universal.verif.storage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.dataStorage.database.DatabaseMigrator;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.Verificator;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.storage.VerificationStorage;

public class DatabaseVerificationStorage extends VerificationStorage {

	public DatabaseVerificationStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "verifications");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletableFuture<List<Verificator>> loadAllVerifications(UUID playerId) {
		Adapter ada = Adapter.getAdapter();
		NegativityPlayer np = ada.getNegativityPlayer(playerId);
		List<Verificator> list = new ArrayList<>();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_verifications WHERE uuid = ?")) {
			stm.setString(1, playerId.toString());
			ResultSet resultQuery = stm.executeQuery();
			if (resultQuery.next()) {
				Map<Cheat, VerifData> cheats = new HashMap<>();
				((JSONObject) new JSONParser().parse(resultQuery.getString("cheats"))).forEach((key, value) -> {
					Cheat c = Cheat.forKey(key.toString());
					if(c == null)
						ada.log("Cannot find cheat " + key.toString() + " for verification of " + playerId.toString());
					else {
						VerifData data = new VerifData();
						for(JSONObject dataCounterObj : (List<JSONObject>) value)
							data.addObj(dataCounterObj);
						cheats.put(c, data);
					}
				});
				List<String> result = Arrays.asList(resultQuery.getString("result").split("\n"));
				String startedBy = resultQuery.getString("startedBy");
				list.add(new Verificator(np, startedBy, cheats, result));
			}
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletableFuture<Void> saveVerification(Verificator verif) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"INSERT INTO negativity_verifications (uuid, startedBy, result, cheats) VALUES (?, ?, ?, ?)")) {
			stm.setString(1, verif.getPlayerId().toString());
			stm.setString(2, verif.getAsker());
			stm.setString(3, verif.getMessages().stream().collect(Collectors.joining("\n")));
			JSONObject jsonCheat = new JSONObject();
			verif.getCheats().forEach((cheat, verifData) -> {
				if(verifData.hasSomething()) {
					jsonCheat.put(cheat.getKey(), verifData.getAllData().values().stream().filter(DataCounter::has).map(DataCounter::print).collect(Collectors.toList()));
				} else
					jsonCheat.put(cheat.getKey(), null);
			});
			stm.setString(4, jsonCheat.toJSONString());
			stm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}
}