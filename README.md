# Marlow's crystal optimiser

Fabric client mod project for Minecraft 1.21 / Java 21.

## Build on GitHub
1. Create a new GitHub repository.
2. Upload the **contents** of this ZIP (so `build.gradle`, `settings.gradle`, `gradle.properties`, `src/`, and `.github/` are at the repository root).
3. Open the **Actions** tab. The workflow builds the mod on every push.
4. Open the successful workflow run and download the `marlows-crystal-optimiser` artifact.
5. Put the resulting JAR in `.minecraft/mods` with Fabric API installed.

## Menu and configuration
- Press **O** to open the menu.
- Open Minecraft Options -> Controls -> Key Binds to rebind the mod actions.
- Item fields accept registry IDs such as `minecraft:diamond`, `minecraft:map`, or `minecraft:player_head`.
- Settings are saved to `config/marlows-crystal-optimiser.json`.

## Current implementation boundary — please read
This is a starter implementation of the client UI, item assignment configuration, keybinds, icon, and local dropped-item visual override. It does **not yet implement the complete dispenser/lever tracking and inventory-slot illusion described in the request**. In particular, it does not yet:
- identify which of two dispensers produced a dropped item;
- reliably intercept dispenser dispense packets or spoof dispenser GUI slot counts;
- preserve a server-confirmed original item for shift-click return;
- implement the full RR rules.

The current visual override changes nearby dropped item entities on the local client and is deliberately not presented as a finished implementation of the full game logic. The server's real inventory and drops are not changed. Server updates may overwrite the visual replacement.

Do not use this build as proof that the full Paper/BJ/50-50/Odd-or-Even mechanics are complete. Those require a follow-up implementation with packet/mixin-level dispenser tracking and per-slot restoration.
