# kArmor Plugin

## Description
A Paper/Spigot 1.20+ Java plugin named "kArmor" (aka "Veteran Armor") that provides cosmetic particle/sound effects when wearing the full Veteran set, admin commands to give single pieces or full sets, no anvil repairs for tagged armor, and support for cosmetic enchantments.

## Requirements
- Java 17+
- Gradle 8.0+ (with shadow plugin)
- Paper API 1.20

## Installation & Build Instructions

### Setup:
```bash
cd kArmor
gradle build
```

### Output:
The compiled jar file is located at `build/libs/kArmor-1.0.0.jar`.

### Running on Paper server:
Place the jar in `plugins/` directory of your Paper server, then restart.

## Commands
- `/karmor give <player> <set> <slot>`: Give one piece (tagged)
- `/karmor giveSet <player> <set>`: Give full set
- `/karmor tagFromHand <player> <set> <slot>`: Convert item in hand to a kArmor piece without removing existing enchants/meta.
- `/karmor enchant add <cosmeticId> [level]`: Add cosmetic enchant to item in hand
- `/karmor enchant remove <cosmeticId>`: Remove cosmetic enchant from item in hand
- `/karmor enchant list`: List all cosmetic enchants on item in hand
- `/karmor reload`: Reload configuration

## Permissions
- `karmor.admin`: All commands above.

## Configurable Features
- Set definition (Veteran)
- Cosmetic effects for full set
- Cosmetic enchantments (visual-only, no gameplay effect)

## Dependencies
- Paper API 1.20
- Phoenix-API (soft-depend for player color messaging; fallback to white if absent)

## Performance & Safety
- Cosmetics tasks run only for relevant players.
- Combine set cosmetics + cosmetic enchant cosmetics each tick but cap totals per player to prevent spam.
- Clean up tasks on disable/quit.

## Acceptance Criteria
- `/karmor give <player> Veteran chest gives a correctly tagged item with configured meta; existing enchants remain if using tagFromHand`.
- Equipping all four Veteran pieces enables set particles ≤0.5s; removing any piece disables ≤0.5s.
- Anvil repairs of kArmor items are blocked.
- `/karmor enchant add AURA_FLAME on an item in hand adds a cosmetic enchant (visible in lore), preserving existing vanilla enchants and meta; /karmor enchant remove AURA_FLAME removes it; /karmor enchant list shows all cosmetic enchants on the item`.
- Messages color player names via Phoenix-API when present; safe fallback otherwise.
- No console errors on Paper 1.20–1.21.x; tasks cleaned up on disable.

## License
MIT
