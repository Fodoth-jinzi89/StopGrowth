# Stop Growth Mod

A simple [Minecraft 1.21.6](https://www.minecraft.net/) mod for [Fabric](https://fabricmc.net/) and [NeoForge](https://neoforged.net/) that allows certain animals to **stop growing** and later **restart growth** using specific items.

> ✅ Currently supports: **Happy Ghast** only.

---

## 🧪 Features

- **Growth Freeze**:  
  - Use a **Fire Charge** to **freeze** the growth of a baby *Happy Ghast*.
- **Growth Restart**:  
  - Use a **Snowball** to **resume** its normal growth cycle.

Growth state is persistent and can be toggled back and forth by interacting with the correct item.

---

## ⚙ Requirements

- [Architectury API](https://modrinth.com/mod/architectury-api) (required at runtime)
- [Fabric Loader](https://fabricmc.net/use/) or [NeoForge](https://neoforged.net/)
- [Mixin](https://github.com/SpongePowered/Mixin) (included via loader integration)

---

## 🔧 Technical

- Uses **Mixins** to inject behavior.
- Logic is fully client-server synced.
- Designed for mod compatibility and expansion.

---

## 🐾 Supported Entities

- Happy Ghast  

---

## 📦 Installation

1. Install Fabric or NeoForge for Minecraft `1.21.6`.
2. Download and install [Architectury API](https://modrinth.com/mod/architectury).
3. Place this mod `.jar` file into your `mods` folder.
4. Launch the game and test with a *Happy Ghast*.

---

## 📁 Developer Notes

- Interfaces `AgeGrowthControl` and `HasStopFood` are used for injection and extension.
- Particle effects are included to provide visual feedback when freezing/unfreezing growth.

---

## 📝 License

MIT License

---

