package com.elikill58.negativity.spigot.packets.protocollib;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.PacketManager;
import com.elikill58.negativity.spigot.packets.PacketType;
import com.elikill58.negativity.spigot.packets.event.PacketEvent.PacketSourceType;

public class ProtocollibPacketManager extends PacketManager {

	private final ProtocolManager protocolManager;
	
	public ProtocollibPacketManager(Plugin pl) {
		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(pl, ListenerPriority.LOWEST, PacketRegistry.getClientPacketTypes()) {
			@Override
			public void onPacketSending(PacketEvent e) {
				AbstractPacket packet = onPacketSent(PacketType.getType(e.getPacket().getHandle().getClass().getSimpleName()),
						e.getPlayer(), e.getPacket().getHandle(), e);
		        if(!e.isCancelled())
		        	e.setCancelled(packet.isCancelled());
			}

			@Override
			public void onPacketReceiving(PacketEvent e) {
				Player p = e.getPlayer();
		        if (p == null)
		            return;
		        if(e.isPlayerTemporary())
		        	return;
		        AbstractPacket packet = onPacketReceive(PacketType.getType(e.getPacket().getHandle().getClass().getSimpleName()),
		        		e.getPlayer(), e.getPacket().getHandle(), e);
		        if(!e.isCancelled())
		        	e.setCancelled(packet.isCancelled());
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(pl, ListenerPriority.LOWEST, PacketRegistry.getServerPacketTypes()) {
			@Override
			public void onPacketSending(PacketEvent e) {
				AbstractPacket packet = onPacketSent(PacketType.getType(e.getPacket().getHandle().getClass().getSimpleName()),
						e.getPlayer(), e.getPacket().getHandle(), e);
		        if(!e.isCancelled())
		        	e.setCancelled(packet.isCancelled());
			}

			@Override
			public void onPacketReceiving(PacketEvent e) {
				Player p = e.getPlayer();
		        if (p == null)
		            return;
		        if(e.isPlayerTemporary())
		        	return;
		        AbstractPacket packet = onPacketReceive(PacketType.getType(e.getPacket().getHandle().getClass().getSimpleName()),
		        		e.getPlayer(), e.getPacket().getHandle(), e);
		        if(!e.isCancelled())
		        	e.setCancelled(packet.isCancelled());
			}
		});
	}

	@Override
	public void addPlayer(Player p) {}

	@Override
	public void removePlayer(Player p) {}

	@Override
	public void clear() {}

	public AbstractPacket onPacketSent(PacketType type, Player sender, Object packet, PacketEvent event) {
		ProtocollibPacket customPacket = new ProtocollibPacket(type, packet, sender, event);
		notifyHandlersSent(PacketSourceType.PROTOCOLLIB, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(PacketType type, Player sender, Object packet, PacketEvent event) {
		ProtocollibPacket customPacket = new ProtocollibPacket(type, packet, sender, event);
		notifyHandlersReceive(PacketSourceType.PROTOCOLLIB, customPacket);
		return customPacket;
	}
}
