package net.errorpnf.betternickname.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import net.errorpnf.betternickname.BetterNickname;
import net.errorpnf.betternickname.commands.BetterNickCommand;
import net.errorpnf.betternickname.hud.CurrentNickHud;
import net.errorpnf.betternickname.utils.AutoReroll;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class BetterNickConfig extends Config {
    @Switch(
            name = "Show Rank",
            description = "Whether or not to show the rank in the HUD.",
            category = "BetterNick HUD",
            subcategory = "HUD Options"
    )
    public static boolean showRank = true; // The default value for the boolean Switch.

    @HUD(
            name = "Current Nick", category = "BetterNick HUD", subcategory = "HUD"
    )
    public CurrentNickHud hud = new CurrentNickHud();

    @Text(
            name = "Reroll Until Nickname Contains: ",
            description = "Rerolls the nickname until a nickname with the\ntext you input is found. (Case sensitive)",
            category = "Auto-Reroll",
            subcategory = "Auto-Reroll Settings",
            placeholder = "Enter the text you want to search for in the nickname.",
            size = 2
    )
    public static String matchText =  "";

    @KeyBind(
            name = "Toggle Auto-Reroll",
            description = "Toggles the Auto-Reroll on and off when pressed.",
            category = "Auto-Reroll",
            subcategory = "Auto-Reroll Settings"
    )
    public static OneKeyBind autoRerollKeybind = new OneKeyBind(UKeyboard.KEY_Y);

    @Switch(
            name = "Auto-Claim Name",
            description = "Automatically claims the name when found while using Auto-Reroll.",
            category = "Auto-Reroll",
            subcategory = "Auto-Reroll Settings"
    )
    public static boolean autoClaimName = true;

    @Slider(
            name = "Time Between Reroll",
            description = "Time before it rerolls the nickname again (in seconds).",
            category = "Auto-Reroll",
            subcategory = "Auto-Reroll Settings",
            min = 1,
            max = 10,
            step = 1
    )
    public static int rerollDelay = 3;

    @KeyBind(
            name = "Reroll Nick",
            description = "Rerolls your nickname when pressed.",
            category = "Keybinds",
            subcategory = "Keybinds"
    )
    public static OneKeyBind rerollNickKeybind = new OneKeyBind(UKeyboard.KEY_NONE);

    @KeyBind(
            name = "Claim Generated Name",
            description = "Claims the generated name.",
            category = "Keybinds",
            subcategory = "Keybinds"
    )
    public static OneKeyBind claimNameKeybind = new OneKeyBind(UKeyboard.KEY_NONE);

    public BetterNickConfig() {
        super(new Mod(BetterNickname.NAME, ModType.UTIL_QOL), BetterNickname.MODID + ".json");
        initialize();
        registerKeyBind(autoRerollKeybind, AutoReroll::toggleAutoReroll);
        registerKeyBind(rerollNickKeybind, BetterNickCommand::rerollNick);
        registerKeyBind(claimNameKeybind, BetterNickCommand::claimNick);
        save();
    }
}

