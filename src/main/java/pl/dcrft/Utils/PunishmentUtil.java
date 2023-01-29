package pl.dcrft.Utils;

import net.kyori.adventure.text.Component;
import pl.dcrft.Managers.DatabaseManager;
import pl.dcrft.Managers.LanguageManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PunishmentUtil {

    public static List<Component> getPunishments(String p) {
        List<Component> lore = new ArrayList<>();
        try {
            final Statement pun = DatabaseManager.connection.createStatement();
            final ResultSet punishments = pun.executeQuery(
                    " SELECT * FROM bm_players" +
                            " LEFT JOIN (SELECT player_id AS p_id, reason AS ban_reason, expires AS ban_expires, created AS ban_created FROM bm_player_bans) AS bans" +
                            " ON bm_players.id = bans.p_id" +
                            " LEFT JOIN (SELECT player_id AS p_id, reason AS warn_reason, expires AS warn_expires, created AS warn_created FROM bm_player_warnings) AS warns" +
                            " ON bm_players.id = warns.p_id" +
                            " LEFT JOIN (SELECT player_id AS p_id, reason AS mute_reason, expires AS mute_expires, created AS mute_created FROM bm_player_mutes) AS mutes" +
                            " ON bm_players.id = mutes.p_id" +
                            " WHERE bm_players.name = '" + p + "'");

            if (!punishments.next()) {
                return null;
            } else {

                List<String> bans = new ArrayList<>();
                List<String> warns = new ArrayList<>();
                List<String> mutes = new ArrayList<>();

                bans.add(punishments.getString("ban_reason"));
                warns.add(punishments.getString("warn_reason"));
                mutes.add(punishments.getString("mute_reason"));

                while (punishments.next()) {
                    warns.add(punishments.getString("warn_reason"));
                }

                if (bans.get(0) != null)
                    lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.bans")));
                for (String s : bans) {
                    lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.color") + s));
                }

                if (warns.get(0) != null)
                    lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.warns")));
                for (String s : warns) {
                    lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.color") + s));
                }

                if (mutes.get(0) != null)
                    lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.mutes")));
                for (String s : mutes) {
                    lore.add(Component.text(LanguageManager.getMessage("statistics.punishments.color") + s));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lore;
        }
    }

