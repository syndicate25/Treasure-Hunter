# Minecraft Forge 1.7.10 Mod Template

A starter template for building a Minecraft Forge mod for **Minecraft 1.7.10** using **Gradle**.

## Features

- Minecraft Forge 1.7.10 project structure
- Gradle-based build setup
- Ready for mod development
- Mixin support
- MIT licensed

## Requirements

- Java Development Kit (JDK) 8

## Getting Started

### 1. Copy the repository

#### Use this template

Click on the `Use this template` button above to create a new repository from this repository.  
Then clone your own repository.

#### OR: Clone this repository

```bash
git clone https://github.com/AlphaConqueror/Minecraft-Forge-1.7.10-Mod-Template
```

### 2. Open the project

Open the project in your IDE as a Gradle project.

### 3. Setup Decomp Workspace

```bash
./gradlew setupDecompWorkspace
```

> [!NOTE]  
> If you get an error like this:
> > Task :makeStart FAILED  
> > [ant:javac] warning: [options] bootstrap class path not set in conjunction with -source 6  
> > [ant:javac] error: Source option 6 is no longer supported. Use 7 or later.  
> > [ant:javac] error: Target option 6 is no longer supported. Use 7 or later.
>
> you are using the wrong JDK version.

### 4. Configure mod metadata

Update the following project settings with your mod information:

- Mod ID
- Mod name
- Mod description
- Mod version
- Author name(s)
- Minecraft version

found in the `gradle.properties` file.

### 5. Build the project

Use Gradle to build the project:

```bash
./gradlew build
```

## Helpful resources

[mcmod.info File Guide and Help](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/modification-development/2405990-mcmod-info-file-guide-and-help)  
[Mixin Examples](https://wiki.fabricmc.net/tutorial:mixin_examples)

## License

This project is licensed under the **MIT License**.
