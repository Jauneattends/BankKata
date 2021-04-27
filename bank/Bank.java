package bank;


import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class Bank {

    /*
        Strings de connection à la base postgres
     */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";

    /*
        Strings de connection à la base mysql, à décommenter et compléter avec votre nom de bdd et de user
     */
    // private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    // private static final String DB_USER = "root";

    private static final String DB_PASS = "1234";

    private static final String TABLE_NAME = "accounts";

    private Connection c;


    public Bank() {
        initDb();

        // TODO

    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            try (Statement s = c.createStatement()) {
                String createSql = "CREATE TABLE " + TABLE_NAME + "(nom VARCHAR(255),balance INT,threshold INT, block BOOLEAN DEFAULT FALSE);";
                s.executeUpdate(createSql);

            } catch (Exception e) {
                System.out.println(e.toString());
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void closeDb() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    void dropAllTables() {
        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                       "DROP SCHEMA public CASCADE;" +
                            "CREATE SCHEMA public;" +
                            "GRANT ALL ON SCHEMA public TO postgres;" +
                            "GRANT ALL ON SCHEMA public TO public;");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void createNewAccount(String name, int balance, int threshold) {

        Account insert = new Account(name,balance,threshold);

        System.out.println("Get nom ici : " + insert.getNom());
        System.out.println("Get balance ici : " + insert.getBalance());
        System.out.println("Get threshold ici : " + insert.getThreshold());

        if (insert.getBalance() >= 0 && insert.getThreshold() <= 0) {
            try (Statement s = c.createStatement()) {
                String createSql = "INSERT INTO " + TABLE_NAME + "(nom,balance,threshold) VALUES " +
                        "('" +
                        insert.getNom() + "'," +
                        insert.getBalance() + "," +
                        insert.getThreshold() +
                        ");";
                s.executeUpdate(createSql);

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    public String printAllAccounts() {
        try (Statement s = c.createStatement()) {
            String createSql = "SELECT * from " + TABLE_NAME;
            ResultSet res = s.executeQuery(createSql);
            String result = "";
            while (res.next()){
                result += res.getString("nom") + " | " + res.getString("balance") + " | " + res.getString("threshold") + " | " + res.getBoolean("block") + "\n";
            }
            return result;

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return "";
    }

    public void changeBalanceByName(String name, int balanceModifier) {

        try (Statement s = c.createStatement()) {
            ResultSet res = s.executeQuery("SELECT balance, block, threshold FROM accounts WHERE nom = '" + name + "'");
            res.next();
            boolean block = res.getBoolean("block");
            Integer balance = res.getInt("balance");
            Integer threshold = res.getInt("threshold");
            balance += balanceModifier;
            boolean isThresholdOK = threshold <= balance;
            if (!block && isThresholdOK) {
                try (Statement f = c.createStatement()) {
                    f.executeQuery("UPDATE accounts SET balance =" + balance + "WHERE nom = '" + name + "'");
                    System.out.println("Sql ok");
                } catch (Exception r) {
                    System.out.println("Error try again" + r);
                }
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void blockAccount(String name) {
        try (Statement s = c.createStatement()){
            ResultSet res = s.executeQuery("SELECT * FROM accounts WHERE nom = '" + name + "'");
            res.next();
            boolean block = res.getBoolean("block");    // Check if the account is already blocked
            if(!block){
                try (Statement f = c.createStatement()){
                    f.executeQuery("UPDATE accounts SET block = true WHERE nom = '" + name + "'");       //      Change the statement of the accounts on blocked
                    System.out.println("Sql ok");
                } catch (Exception d) {
                        System.out.println("Error try again" + d);
                }
            }
        } catch (Exception e) {
            System.out.println("The account doesn't exist :");
        }
    }

    // For testing purpose
    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // Getting nb column from meta data
            int nbColumns = r.getMetaData().getColumnCount();

            // while there is a next row
            while (r.next()){
                String[] currentRow = new String[nbColumns];

                // For each column in the row
                for (int i = 1 ; i <= nbColumns ; i++) {
                    currentRow[i - 1] = r.getString(i);
                }
                res += Arrays.toString(currentRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }
}
