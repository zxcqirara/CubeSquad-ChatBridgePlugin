package dev.cha0s_f4me.chat_bridge

import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class MinecraftListener : Listener {
	private val plain = PlainTextComponentSerializer.plainText()

	@EventHandler
	suspend fun onChat(event: AsyncChatEvent) {
		val p = event.player
		val originalMessage = (event.originalMessage() as TextComponent).content()
		/*val sentMessage =*/ Discord.channel.createMessage("**<${p.name}>** $originalMessage")

		/*event.renderer { _, name, message, _ ->
			return@renderer Component.text()
				.append(
					Component.text("↩ ").color(NamedTextColor.DARK_GRAY)
						.clickEvent(ClickEvent.suggestCommand("reply ${sentMessage.id.value} "))
				)
				.append(Component.text("<"))
				.append(name)
				.append(Component.text("> "))
				.append(message)
				.build()
		}*/
	}

	@EventHandler
	suspend fun onAdvancement(event: PlayerAdvancementDoneEvent) {
		val p = event.player
		val adv = event.advancement.display ?: return
		if (!adv.doesAnnounceToChat()) return
		val advColor = adv.frame().color().value()

		val advTitle = plain.serialize(adv.title())
		val advDescription = plain.serialize(adv.description())

		Discord.channel.createEmbed {
			title = "${p.name} has made the advancement!"
			description = "**[$advTitle]**\n`$advDescription`"

			color = Color(advColor)
		}
	}

	@EventHandler
	suspend fun onDeath(event: PlayerDeathEvent) {
		val message = event.deathMessage() ?: return
		val translated = plain.serialize(message)

		Discord.channel.createEmbed {
			title = translated
			color = NamedTextColor.DARK_RED.toKordColor()
		}
	}

	@EventHandler
	suspend fun onJoin(event: PlayerJoinEvent) {
		val message = event.joinMessage() ?: return
		val translated = plain.serialize(message)

		Discord.channel.createEmbed {
			title = translated
			color = NamedTextColor.YELLOW.toKordColor()
		}
	}

	@EventHandler
	suspend fun onQuit(event: PlayerQuitEvent) {
		val message = event.quitMessage() ?: return
		val translated = plain.serialize(message)

		Discord.channel.createEmbed {
			title = translated
			color = NamedTextColor.YELLOW.toKordColor()
		}
	}
}