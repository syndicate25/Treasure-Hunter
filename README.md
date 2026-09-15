# 🏹 Treasure Hunter (Minecraft 1.7.10)

Treasure Hunter is a simple utility mod that adds functionality to custom map items. When you use a treasure map, the mod consumes the item, picks a random spot within 50 blocks of you, and buries a treasure chest 2 to 5 blocks underground. It then prints the exact coordinates in your chat so you know where to go dig it up!

---

## ⚙️ Features
* **Buried Treasure:** Chests always spawn hidden just beneath the surface ground level, never floating in the air.
* **Safety First:** The mod will not spawn chests in deep oceans, lava pools, or void spaces. If you try to use a map in a dangerous spot, it resets safely and saves your map item.
* **Trapped Chests:** There is a configurable percentage chance that a chest will be a trapped variant with a block of TNT buried right underneath it!
* **Custom Config:** A configuration file (`config/treasurehunter.cfg`) is created automatically on startup so you can change the trap chances at any time.

---

## 🛠️ How to Compile the Mod
If you want to build this mod into a playable `.jar` file yourself, follow these quick steps:

1. Make sure you have **Java 8 (JDK 8)** installed on your computer.
2. Open your command prompt or terminal inside this project folder.
3. Run the clean and build commands:
   ```cmd
   .\gradlew clean
   ```
   ```cmd
   .\gradlew build
   ```
4. Once completed, your finished mod file will be waiting for you inside the folder:  
   📂 `build/libs/`

---

## 📜 License & Usage Permissions

This project is licensed under the **MIT License**. 

### What you can do with this mod:
* ✅ **Use it:** You are completely free to use this mod in any personal, public, or private modpacks.
* ✅ **Share it:** You can host it on servers or share it with your friends.
* ✅ **Modify it:** You can copy, change, or adapt the source code for your own projects.
* ⚠️ **Condition:** You must keep the original copyright notice and credit to `syndicate_25` inside the source files. 
