![NeMuelch-HideBodyParts](https://github.com/JR1811/hide-body-parts-fabric/assets/36027822/c0093a2b-7af2-422c-82d0-bd110fe2ea39)

# Hide Body Parts <img src="https://github.com/JR1811/hide-body-parts-fabric/assets/36027822/846db018-1f7d-4ea8-8dae-773da0377be2"  width="35" height="35">

Utility mod for Fabric Minecraft.

## Usage

Use the custom NBT data to hide bodyparts from the players.

### Custom NBT data

Use the `hidebodyparts.missing_part` NBT Key to hide body parts. The entry contains the hidden part and a player name. This can be used to leave data on who took the part away.

Currently availble Bodyparts:

| Body Part | name |
|---|---|
| HEAD | "head" |
| BODY | "body" |
| RIGHT ARM | "r_arm" |
| LEFT ARM | "l_arm" |
| RIGHT LEG | "r_leg" |
| LEFT LEG | "l_leg" |

### Commands

Toggle body parts: `/hide bodyPart changeEntry bodyPartName target`

Reset all body parts to visible: `/hide bodyPart removeAllEntries target`

---

### Official Release pages

- [Github](https://github.com/JR1811/hide-body-parts-fabric/releases)
- [Modrinth](https://modrinth.com/mod/hide-body-parts)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/hide-body-parts)

