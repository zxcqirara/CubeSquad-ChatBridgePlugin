package dev.cha0s_f4me.chat_bridge

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit

object Discord {
	lateinit var kord: Kord
	lateinit var guild: Guild
	lateinit var channel: GuildMessageChannel

	private val mm = MiniMessage.miniMessage()

	fun initChatListener() {
		kord.on<MessageCreateEvent> {
			if (message.channelId != channel.id) return@on
			if (member == null) return@on
			if (member!!.isBot) return@on
			if (message.content.isBlank()) return@on

			val authorTag = message.author!!.tag
			val discordMessage = message.content
				.replace(Regex("<@!?(\\d{18})>")) {
					val member = runBlocking { message.getGuild().getMember(Snowflake(it.groups[1]!!.value)) }
					val id = member.id.value.toString()

					"<hover:show_text:'$id'><color:#5865F2>@${member.displayName}</color></hover>"
				}
				.replace(Regex("<@&(\\d{18})>")) {
					val role = runBlocking { message.getGuild().getRole(Snowflake(it.groups[1]!!.value)) }
					val id = role.id.value.toString()
					val hex = role.color.rgb.toString(16)

					"<hover:show_text:'$id'><color:#$hex>@${role.name}</color></hover>"
				}
				.replace(Regex("<#(\\d{18})>")) {
					val channel = runBlocking { message.getGuild().getChannel(Snowflake(it.groups[1]!!.value)) }
					val id = channel.id.value.toString()

					"<hover:show_text:'$id'><color:#5865F2>#${channel.name}</color></hover>"
				}

			val parsed = mm.deserialize(discordMessage)
			var chatMessage = text { } as Component

			if (message.referencedMessage != null) kotlin.run {
				val ref = message.referencedMessage!!
				val refAuthor = ref.author ?: return@run

				if (refAuthor.isBot) return@run
				if (ref.content.isBlank()) return@run

				chatMessage += text {
					content("┌ ")
					color(NamedTextColor.DARK_GRAY)
				}
				chatMessage += text {
					content(refAuthor.username)
					color(NamedTextColor.DARK_GRAY)
					decorate(TextDecoration.BOLD)
					hoverEvent(HoverEvent.showText(Component.text(refAuthor.tag)))
				}
				chatMessage += text {
					val content = message.referencedMessage!!.content
						.takeIf { it.isNotBlank() }

					if (content != null) {
						val len = content.length
						val parsedContent = if (Regex("\\*\\*<.+>\\*\\* .+").matches(content))
							content.replace(Regex("^\\*\\*<.+>\\*\\* "), "")
						else
							content

						val cut = if (len <= 15) parsedContent
						else if (len == 16) "$parsedContent."
						else if (len == 17) "$parsedContent.."
						else "$parsedContent..."

						content(" $cut")
					}
					else {
						content(" No content")
						decorate(TextDecoration.ITALIC)
					}

					color(NamedTextColor.DARK_GRAY)
				}

				chatMessage += text { content("\n") }
			}

			chatMessage += text {
				content("<${message.author!!.username}> ")
				color(NamedTextColor.LIGHT_PURPLE)
				hoverEvent(HoverEvent.showText(Component.text(authorTag)))
				clickEvent(ClickEvent.copyToClipboard(authorTag))
			}

			chatMessage += parsed

			Bukkit.broadcast(chatMessage)
		}
	}
}