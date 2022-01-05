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
package com.rs.web;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.model.Account;
import com.rs.lib.web.APIUtil;
import com.rs.lib.web.ErrorResponse;
import com.rs.lib.web.WebAPI;
import com.rs.lib.web.dto.PacketEncoderDto;

import io.undertow.util.StatusCodes;

public class WorldAPI extends WebAPI {

	public WorldAPI() {
		super("api", Settings.getConfig().getWorldInfo().getPort()+1);
		
		this.routes.post("/players", ex -> {
			ex.dispatch(() -> {
				APIUtil.sendResponse(ex, StatusCodes.OK, World.getPlayers().size());
			});
		});
		
		this.routes.post("/updatesocial", ex -> {
			ex.dispatch(() -> {
				if (!APIUtil.authenticate(ex, Settings.getConfig().getLobbyApiKey())) {
					APIUtil.sendResponse(ex, StatusCodes.UNAUTHORIZED, new ErrorResponse("Invalid authorization key."));
					return;
				}
				APIUtil.readJSON(ex, Account.class, account -> {
					Player player = World.getPlayer(account.getUsername());
					if (player == null || player.getSession() == null)
						return;
					player.getAccount().setSocial(account.getSocial());
					APIUtil.sendResponse(ex, StatusCodes.OK, true);
				});
			});
		});
	
		this.routes.post("/sendpacket", ex -> {
			ex.dispatch(() -> {
				if (!APIUtil.authenticate(ex, Settings.getConfig().getLobbyApiKey())) {
					APIUtil.sendResponse(ex, StatusCodes.UNAUTHORIZED, new ErrorResponse("Invalid authorization key."));
					return;
				}
				APIUtil.readJSON(ex, PacketEncoderDto.class, request -> {
					Player player = World.getPlayer(request.username());
					if (player == null || player.getSession() == null)
						return;
					player.getSession().writeToQueue(request.encoders());
					APIUtil.sendResponse(ex, StatusCodes.OK, true);
				});
			});
		});
		
		addRoute(new Telemetry());
	}

}
