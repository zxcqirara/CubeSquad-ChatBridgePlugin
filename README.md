# Introduction
Chat bridge between discord and minecraft.
Designed for private use.

# `config.yml` setting
`token` - discord bot API token, you can gen it when [creating bot](https://discord.com/developers/applications)

`guildId` - Targeted guild ID

`channelId` - discord channel (should be text) ID for bridge

# Building
```sh
./gradlew shadowJar
```
in the root dir, then you will can find compiled plugin JAR in `build/libs`

After that just move it to `plugins` server folder and start server. Then stop it, configure plugin and start second time. Done!
