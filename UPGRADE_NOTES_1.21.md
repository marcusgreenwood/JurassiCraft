# JurassiCraft 1.21 Upgrade Notes

This document outlines the major changes made to upgrade JurassiCraft from Minecraft 1.12.2 to Minecraft 1.21 using NeoForge.

## Major Changes Made

### 1. Build System Upgrade
- **Old**: ForgeGradle 2.3 with Gradle 4.9
- **New**: NeoGradle 7.0 with Gradle 8.8
- Updated to use modern Java 21
- Replaced complex build.gradle with streamlined NeoForge setup
- Added Parchment mappings for better deobfuscation

### 2. Mod Loader Migration
- **Old**: Minecraft Forge 14.23.5.2768 for MC 1.12.2
- **New**: NeoForge 21.1.77 for MC 1.21
- Completely rewrote main mod class using modern event bus system
- Removed proxy system in favor of dist-aware event handlers

### 3. Metadata System
- **Old**: `mcmod.info` file
- **New**: `neoforge.mods.toml` file with TOML format
- Updated dependency declarations and version ranges

### 4. Command System Overhaul
- **Old**: `ICommand` interface with string-based parsing
- **New**: Brigadier command system with type-safe arguments
- Rewrote `ForceAnimationCommand` and `SpawnStructureCommand`
- Added proper command suggestions and error handling

### 5. Package Structure
- Updated imports to use modern NeoForge packages
- Replaced Forge-specific classes with NeoForge equivalents
- Updated event handling to use new event bus system

## Breaking Changes

### Dependencies Removed
- **LLibrary**: The mod previously depended on LLibrary 1.7.15, this dependency has been removed
- **HWYLA**: Removed HWYLA integration (can be re-added later as optional)

### Code Structure
- Removed proxy system (`@SidedProxy`)
- Replaced `@Mod.EventHandler` with modern event listeners
- Updated networking system (placeholder - needs implementation)
- Animation system needs complete rewrite
- Entity system needs massive updates

## What Still Needs Work

### High Priority
1. **Entity System**: All dinosaur entities need complete rewrite for modern entity system
2. **Animation System**: The old animation system using LLibrary needs replacement
3. **Networking**: Packet system needs complete rewrite for modern networking
4. **GUI System**: All GUIs need updating to modern screen system
5. **Rendering**: Block and entity rendering needs complete overhaul
6. **World Generation**: Structure generation system needs updating

### Medium Priority
1. **Items and Blocks**: Registration system needs updating to use DeferredRegister
2. **Recipes**: Recipe system needs updating for modern data-driven recipes
3. **Loot Tables**: Convert to modern JSON-based loot tables
4. **Advancements**: Update advancement system
5. **Tags**: Convert to modern tag system

### Low Priority
1. **Textures**: May need updating for new texture formats
2. **Models**: Block and item models may need adjustments
3. **Sounds**: Sound system updates
4. **Localization**: Language file updates

## Development Setup

### Requirements
- Java 21+
- IntelliJ IDEA or Eclipse with NeoGradle support
- Git

### Setup Steps
1. Clone the repository
2. Run `./gradlew build` to download dependencies
3. Import into IDE using NeoGradle
4. Run `./gradlew genEclipseRuns` or `./gradlew genIntellijRuns`
5. Use the generated run configurations

### Testing
- Use `./gradlew runClient` to test client
- Use `./gradlew runServer` to test server
- Use `./gradlew runData` to generate data

## Migration Strategy

### Phase 1: Core Framework (CURRENT)
- ✅ Update build system
- ✅ Update mod metadata
- ✅ Basic mod class structure
- ✅ Command system basics
- ⏳ Basic registries setup

### Phase 2: Core Content
- ⏳ Items and blocks registration
- ⏳ Basic entity framework
- ⏳ World generation basics
- ⏳ GUI framework

### Phase 3: Dinosaur Content
- ⏳ Dinosaur entities
- ⏳ Animation system
- ⏳ AI behaviors
- ⏳ Spawning mechanics

### Phase 4: Advanced Features
- ⏳ DNA extraction system
- ⏳ Incubation mechanics
- ⏳ Structures and world generation
- ⏳ Vehicles and advanced items

### Phase 5: Polish
- ⏳ Textures and models
- ⏳ Sounds and music
- ⏳ Balancing and testing
- ⏳ Documentation

## Known Issues

1. All dinosaur-related code is currently broken and needs rewriting
2. Animation system is not implemented
3. Networking system is placeholder
4. Most of the original mod functionality is not yet ported
5. Structure generation system needs implementation

## Contributing

When contributing to the 1.21 port:
1. Follow modern NeoForge conventions
2. Use DeferredRegister for all registrations
3. Implement proper client/server separation
4. Use modern data generation where applicable
5. Ensure compatibility with Java 21

## Resources

- [NeoForge Documentation](https://docs.neoforged.net/)
- [Minecraft 1.21 Changes](https://minecraft.wiki/w/Java_Edition_1.21)
- [Migration Primers](https://gist.github.com/ChampionAsh5357/)
- [NeoForge Community Discord](https://discord.neoforged.net/)