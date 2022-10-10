package dev.cha0s_f4me.chat_bridge

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.getChannelOfOrNull
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent

@PrivilegedIntent
@KordExperimental
class Main : SuspendingJavaPlugin() {
	override suspend fun onEnableAsync() {
		saveDefaultConfig()

		val token = config.getString("token")
		if (token.isNullOrBlank()) {
			logger.severe("Token not found! Stopping plugin...")
			pluginLoader.disablePlugin(this)
			return
		}

		Discord.kord = Kord(token)
		Discord.initChatListener()

		launch {
			Discord.kord.login {
				intents += Intent.MessageContent
			}
		}

		val guildId = config.getString("guildId")
		if (guildId.isNullOrBlank()) {
			logger.severe("Guild ID not found! Stopping plugin...")
			pluginLoader.disablePlugin(this)
			return
		}

		val guild = Discord.kord.getGuild(Snowflake(guildId))
		if (guild == null) {
			logger.severe("Guild not found! Stopping plugin...")
			pluginLoader.disablePlugin(this)
			return
		}
		Discord.guild = guild

		val channelId = config.getString("channelId")
		if (channelId.isNullOrBlank()) {
			logger.severe("Guild ID not found! Stopping plugin...")
			pluginLoader.disablePlugin(this)
			return
		}

		val channel = guild.getChannelOfOrNull<GuildMessageChannel>(Snowflake(channelId))
		if (channel == null) {
			logger.severe("Guild not found! Stopping plugin...")
			pluginLoader.disablePlugin(this)
			return
		}
		Discord.channel = channel

		server.pluginManager.registerSuspendingEvents(MinecraftListener(), this)
		Commands.init(this)
	}
}