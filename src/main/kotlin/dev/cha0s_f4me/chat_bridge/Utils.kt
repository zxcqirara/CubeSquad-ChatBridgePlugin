package dev.cha0s_f4me.chat_bridge

import dev.kord.common.Color
import net.kyori.adventure.text.format.NamedTextColor

fun NamedTextColor.toKordColor() = Color(
	this.red(), this.green(), this.blue()
)