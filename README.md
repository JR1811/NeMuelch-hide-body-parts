# Hide Body Parts

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

Reset all bofy parts to visible: `/hide bodyPart removeAllEntries target`
