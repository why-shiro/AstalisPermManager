package net.neostellar.astalisPermManager.database.dao;

import net.neostellar.astalisPermManager.database.DatabaseType;
import net.neostellar.astalisPermManager.database.dao.playerpermission.PlayerPermissionDAO;
import net.neostellar.astalisPermManager.database.dao.playerpermission.PostgresPlayerPermissionDAO;
import net.neostellar.astalisPermManager.database.dao.playerpermission.SQLitePlayerPermissionDAO;
import net.neostellar.astalisPermManager.database.dao.playerrank.PlayerRankDAO;
import net.neostellar.astalisPermManager.database.dao.playerrank.PostgresPlayerRankDAO;
import net.neostellar.astalisPermManager.database.dao.playerrank.SQLitePlayerRankDAO;

public class DAOProvider {
    private static PlayerRankDAO playerRankDAO;
    private static PlayerPermissionDAO playerPermissionDAO;

    public static void init(DatabaseType type) {
        switch (type) {
            case POSTGRESQL -> {
                playerRankDAO = new PostgresPlayerRankDAO();
                playerPermissionDAO = new PostgresPlayerPermissionDAO();
            }
            case SQLITE -> {
                playerRankDAO = new SQLitePlayerRankDAO();
                playerPermissionDAO = new SQLitePlayerPermissionDAO();
            }
        }

        playerRankDAO.createTable();
        playerPermissionDAO.createTable();
    }

    public static PlayerRankDAO getPlayerRankDAO() {
        return playerRankDAO;
    }

    public static PlayerPermissionDAO getPlayerPermissionDAO() {
        return playerPermissionDAO;
    }
}


