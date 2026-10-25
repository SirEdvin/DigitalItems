package site.siredvin.digitalitems.testmod

import com.mojang.authlib.GameProfile
import io.netty.channel.embedded.EmbeddedChannel
import net.minecraft.network.Connection
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.CommonListenerCookie
import net.minecraft.server.network.ServerGamePacketListenerImpl
import java.util.UUID

object RematerializePlayer {
    fun create(level: ServerLevel): Pair<ServerPlayer, EmbeddedChannel> {
        val profile = GameProfile(UUID.randomUUID(), "Conservation")
        val player = ServerPlayer(level.server, level, profile, ClientInformation.createDefault())
        val connection = Connection(PacketFlow.SERVERBOUND)
        val channel = EmbeddedChannel(connection)
        player.connection = ServerGamePacketListenerImpl(level.server, connection, player, CommonListenerCookie.createInitial(profile, false))
        return player to channel
    }
}
