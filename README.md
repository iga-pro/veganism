<div align="center">

# 🌿 Veganism

**A Fabric mod that turns Minecraft into a militant vegan world.**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1%20%7C%201.21.1-62B47A?logo=minecraft&logoColor=white)](#supported-versions)
[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-DBD0B4?logo=fabric)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-GPL--3.0-blue.svg)](LICENSE)
[![Modrinth](https://img.shields.io/badge/Download-Modrinth-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/mod/veganism)
[![CurseForge](https://img.shields.io/badge/Download-Curse%20Forge-ff6a39?logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/veganism-by-iga)

</div>

---

## ✨ What is this?

**Veganism** is a chaotic, lore-driven survival mod. Animals fight back. Kill too many of them and you summon the **Spirit of Animals** — a custom Wither boss that curses you with **Veganism**, after which eating meat makes you violently sick.

It's a joke mod with real mechanics. Use it for a hardcore "vegan challenge", a friendly-fire server gag, or just to terrify your friends.

## 🔥 Features

- **Animal Revenge** — kill any mob and nearby mobs chase you down, dealing damage on contact (scaled by difficulty) with particles and angry chat lines.
- **Screaming animals** — slain animals "scream" (screaming-goat sound) with a burst of red particles.
- **The Spirit of Animals** — kill enough animals (default **3**) and a custom Wither boss "Дух животных / Spirit of Animals" spawns, locked onto you. It hugs the ground and hunts.
  - On death it drops **oak leaves** + spawns 4–7 random farm animals — **no Nether Star**.
  - Reduced, configurable XP drop (default **5**, vanilla Wither is 50).
- **The Veganism curse** — the Spirit's skulls deal little damage and don't break blocks, but inflict the **Veganism** status effect (5 min, lvl 2) with a custom green-leaf icon.
  - While cursed, eating **non-vegan food** gives you no hunger back — just **Nausea + Hunger**.
  - **Milk does not cure it.**
- **Vegan bonus** — go long enough without eating meat (default **5 min** streak) and the animals bless you with a "Clean Conscience" buff (Regeneration + Speed). Eating meat resets the streak.
- **Advancements** — a full advancement tab with **6 goals** (First Blood → Spirit of the Animals → Cursed → Vengeance, plus Clean Conscience), localized in **English and Russian**.
- **Adaptive language** — all chat lines, the boss name, advancements and the config UI are in **English or Russian**, auto-detected from your game language (overridable in settings).
- **In-game config** — full **Cloth Config** GUI via **Mod Menu** or the `/prop` command (particle multiplier, Spirit HP, animal kill threshold, Spirit XP, vegan bonus, language, …).

## 📦 Supported versions

| Minecraft | Mod Loader | Branch | Java |
|-----------|-----------|--------|------|
| **1.21.1** | Fabric | [`1.21.1`](https://github.com/iga-pro/veganism/tree/1.21.1) | 21 |
| **1.20.1** | Fabric | [`main`](https://github.com/iga-pro/veganism/tree/main) | 17 |

Download the build that matches your Minecraft version.

## 🚀 Installation

1. Install [**Fabric Loader**](https://fabricmc.net/use/) for your Minecraft version.
2. Drop these into your `mods` folder:
   - **Veganism** (this mod)
   - [**Fabric API**](https://modrinth.com/mod/fabric-api)
   - [**Cloth Config**](https://modrinth.com/mod/cloth-config) *(required)*
   - [**Mod Menu**](https://modrinth.com/mod/modmenu) *(optional — adds the settings button)*
3. Launch the matching Fabric profile.

> Settings work in singleplayer. On a dedicated server they are not synced to clients.

## ⚙️ Configuration

Open settings via **Mod Menu → Veganism**, or run `/prop` in-game. Tweak particle density, the Spirit's HP and XP, how many animals trigger the Spirit, the language mode, and more.

## 🛠️ Building from source

Requires **JDK 21**.

```bash
# Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
.\gradlew.bat build

# the jar lands in build/libs/veganism-1.1.0.jar
```

Pick the branch for the version you want first (`git checkout 1.21.1` or `git checkout main`).

## 📜 License

Licensed under the [**GNU General Public License v3.0**](LICENSE).
