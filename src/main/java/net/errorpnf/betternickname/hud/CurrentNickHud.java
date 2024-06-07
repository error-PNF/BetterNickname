package net.errorpnf.betternickname.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import net.errorpnf.betternickname.config.BetterNickConfig;
import net.errorpnf.betternickname.utils.BookParser;
import net.errorpnf.betternickname.utils.IsInLobby;

/**
 * An example OneConfig HUD that is started in the config and displays text.
 *
 * @see BetterNickConfig#hud
 */
public class CurrentNickHud extends SingleTextHud {
    public CurrentNickHud() {
        super("§eGenerated Nickname", true);
    }

    @Override
    public String getText(boolean example) {
        if (BetterNickConfig.showRank) {
            if (BookParser.getCurrentRank() == null) {
                if (BookParser.getGeneratedNickname() != null) {
                    return "§f" + BookParser.getGeneratedNickname();
                }
                return "§fNot Generated";
            } else if (BookParser.getGeneratedNickname() == null){
                return "§f" + BookParser.getCurrentRank() + "Player";
            } else {
                return "§f" + BookParser.getCurrentRank() + BookParser.getGeneratedNickname();
            }
        } else {
            if (BookParser.getGeneratedNickname() == null) {
                return "§fNot Generated";
            } else {
                return "§f" + BookParser.getGeneratedNickname();
            }
        }
    }

    @Override
    public boolean shouldShow() {
        if (IsInLobby.isInLobby()) {
            return super.shouldShow() && IsInLobby.isInLobby();
        } else {
            return super.shouldShow() && IsInLobby.isInLobby();
        }
    }
}
