package dev.cha0s_f4me.chat_bridge

import cloud.commandframework.arguments.preprocessor.RegexPreprocessor
import cloud.commandframework.arguments.standard.LongArgument
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.reply
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.extra.kotlin.plus
import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.CommandSender

@KordExperimental
@PrivilegedIntent
object Commands {
	fun init(plugin: Main) {
		val manager = PaperCommandManager(
			plugin,
			AsynchronousCommandExecutionCoordinator.simpleCoordinator(),
			{ it }, { it }
		)

		if (manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION))
			manager.registerAsynchronousCompletions()

		if (manager.queryCapability(CloudBukkitCapabilities.BRIGADIER))
			manager.registerBrigadier()

		manager.command(
			manager.commandBuilder("mention")
				.argument(StringArgument.quoted("member"))
				.argument(StringArgument.greedy("content"))
				.handler { ctx ->
					val argumentMember = ctx.get<String>("member")
					val member = runBlocking { Discord.guild.getMembers(argumentMember, 1).firstOrNull() }

					if (member == null) {
						ctx.sender.sendMessage(text {
							content("Invalid member!")
							color(NamedTextColor.RED)
						})

						return@handler
					}

					val content = ctx.get<String>("content")

					runBlocking {
						Discord.channel.createMessage("""
								**<${ctx.sender.name}>** ${member.mention} $content
							""".trimIndent())
					}

					var message = text { } as Component

					message += text {
						content("<${ctx.sender.name}> ")
					}

					message += text {
						content("@${member.displayName}")
						color(TextColor.color(0x5865F2))
					}

					message += text {
						content(" $content")
					}

					ctx.sender.sendMessage(message)
				}
		)

		val replyPrep = RegexPreprocessor
			.of<CommandSender>("\\d{18}")
			.andThen { it }

		manager.command(
			manager.commandBuilder("reply")
				.argument(
					LongArgument.of<CommandSender?>("message")
						.addPreprocessor(replyPrep)
				)
				.argument(StringArgument.greedy("content"))
				.handler { ctx ->
					val messageId = ctx.get<Long>("message")
					val content = ctx.get<String>("content")

					val message = runBlocking { Discord.channel.getMessageOrNull(Snowflake(messageId)) }

					if (message == null) {
						ctx.sender.sendMessage(text {
							content("Invalid channel!")
							color(NamedTextColor.RED)
						})

						return@handler
					}

					runBlocking { message.reply { this.content = "**<${ctx.sender.name}>** $content" } }
				}
		)
	}
}