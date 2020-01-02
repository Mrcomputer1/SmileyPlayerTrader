[![Discord](https://img.shields.io/discord/661532592415440906?label=Support%20Discord&style=for-the-badge)](https://discord.gg/SdM6f7U)

Smiley Player Trader was created by Mrcomputer1 for [smileycreations15](https://github.com/smileycreations15)'s Minecraft server. Its purpose is to allow you to right click on a player and see a list of items they can trade.

# Usage
1. Type `/spt add` to add a product to your list
2. Hold the item you want to sell in your hand and type `/spt setproduct <id>` (it will tell you the ID when adding the product).
3. Hold the item you want to get for it in your hand and type `/spt setcost <id>`
4. If you want, you can set another item too with `/spt setcost2 <id>`
5. Enable the product in the list with `/spt enable <id>`
6. To trade with someone, right click them.

# Building
1. Type `mvn clean package` to build.
2. The `SmileyPlayerTrader-VERSION.jar` is the plugin (ignore any with `-shaded` or `original-` in the name)

# License
[MIT License](LICENSE.md)
