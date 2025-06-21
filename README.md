# Stop Growth Mod

A simple Minecraft `1.21.6` mod for [Fabric](https://fabricmc.net/) and [NeoForge](https://neoforged.net/) that allows you to **stop** and **restart** the growth of most baby entities using specific items.

> ✅ Version 1.0 supports: **Happy Ghast** only  
> 🚀 Version 2.0 supports: **Almost all baby mobs**

---

## 🧪 Features

- **Growth Freeze**  
  Feed a configured "stop item" (e.g., Fire Charge) to a baby mob to **halt its growth**.
  
- **Growth Restart**  
  Feed a configured "restart item" (e.g., Snowball) to the frozen mob to **resume growth**.

- **Persistent State**  
  Growth state is saved with the entity and can be toggled at any time.

- **Visual Feedback**  
  Particle effects (e.g. smoke, glow) show successful item interaction.

- ✅ **Full Datapack Support**  
  Easily customize the stop/restart items via datapack:  
  ➤ Just modify or add files in:  
  `data/stopgrowth/tags/items/growth_stop_food`  
  `data/stopgrowth/tags/items/growth_restart_food`  
  No Java code or mod recompilation needed.

---

## ⚙ Requirements

- [Architectury API](https://modrinth.com/mod/architectury-api) (runtime)
- [Fabric Loader](https://fabricmc.net/use/) **or** [NeoForge](https://neoforged.net/)
- [Mixin](https://github.com/SpongePowered/Mixin) (included automatically)

---

## 🧩 Technical Highlights

- Built using **Mixins** for behavior injection.
- Fully **client-server synchronized**.
- Designed for **maximum compatibility** and **datapack-driven extensibility**.

---

## 🐾 Supported Entities

- ✅ v1.0: `Happy Ghast`  
- ✅ v2.0: Nearly all baby mobs  
  *(e.g. cows, sheep, horses, villagers, dolphins, tadpoles...)*

---

## 📦 Installation

1. Install **Fabric** or **NeoForge** for Minecraft `1.21.6`.
2. Download and install **Architectury API**.
3. Place this mod’s `.jar` file into your `mods` folder.
4. Optionally, create or edit a datapack to redefine stop/restart items.
5. Launch Minecraft and test by interacting with baby mobs.

---

## 📝 License

Licensed under the **MIT License**.

---

## 📌 Roadmap

| Version | Features                                              |
|---------|-------------------------------------------------------|
| 1.0     | Initial support for Happy Ghast                       |
| 2.0     | General support for All Baby Mobs                     |
| Future  | Configurable items                                    |

