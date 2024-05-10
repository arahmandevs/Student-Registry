import static javax.swing.JOptionPane.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentRegister {

    private static String url = "jdbc:sqlite:students.db";
    private static Connection conn = null;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(url);
            createTable();
            fillTable();

            char choice;
            do {
                choice = showMenu();
                switch (choice) {
                    case 'N':
                        showAllName();
                        break;
                    case 'M':
                        displayAllMobileNos();
                        break;
                    case 'V':
                        showAllVerv();
                        break;
                    case 'L':
                        addStudent();
                        break;
                    case 'R':
                        editStudent();
                        break;
                    case 'S':
                        deleteStudent();
                        break;
                    case 'B':
                        backupRegistry();
                        break;
                    case 'T':
                        loadBackup();
                        break;
                    case 'A':
                        exitProgram();
                        break;
                    default:
                        showMessageDialog(null, "Ugyldig valg. Vennligst prøv igjen.");
                        break;
                }
            } while (choice != 'A');

        } catch (SQLException e) {
            showMessageDialog(null, "Feil ved tilkobling til database: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                showMessageDialog(null, "Feil ved lukking av databaseforbindelse: " + ex.getMessage());
            }
        }
    }

    private static void createTable() throws SQLException {
    	  String sql = "CREATE TABLE IF NOT EXISTS Student (" +
                  "Stnro INTEGER PRIMARY KEY," +
                  "Name TEXT NOT NULL," +
                  "Email TEXT," +
                  "Number TEXT," +
                  "P0sition TEXT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void fillTable() throws SQLException {
        // Fyll tabellen med data fra en fil (Abdi)
        try (BufferedReader reader = new BufferedReader(new FileReader("studentregister.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    int stnr = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String email = parts[2];
                    String mob = parts[3];
                    String position = parts[4];
                    addStudentToTable(stnr, name, email, mob, position);
                }
            }
        } catch (IOException e) {
            showMessageDialog(null, "Feil ved lesing fra fil " + e.getMessage());
        }
    }

    private static void addStudentToTable(int stnr, String name, String email, String mob, String position) throws SQLException {
        String sql = "SETT INN I Student (Stnr, Navn, E-post, Mobil, Stilling) VERDIER (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stnr);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, mob);
            pstmt.setString(5, position);
            pstmt.executeUpdate();
        }
    }

    private static char showMenu() {
        String out = "Velg oppgave\n"
                     + "[N]: Liste på navn\n"
                     + "[M]: Liste på mobilnummer\n"
                     + "[V]: Liste på verv\n"
                     + "[L]: Legg til ny student\n"
                     + "[R]: Rediger studentopplysninger\n"
                     + "[S]: Slett student\n"
                     + "[B]: Ta backup av registeret\n"
                     + "[T]: Last inn register fra backup\n"
                     + "[A]: Avslutt program\n"
                     + "Skriv kode for ditt valg:";
        String response = showInputDialog(out).trim().toUpperCase();
        return response.charAt(0);
    }

    private static void showAllName() {
    	// Implementer metoden for å vise alle studenter sortert etter etternavn (Abdi)
        showMessageDialog(null, "Viser alle studenter sortert etter etternavn");
    try {
        String sql = "SELECT * FROM Student ORDER BY Name";
        List<String> studentList = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                studentList.add(rs.getString("Name"));
            }
        }

        showMessageDialog(null, "Liste over studenter sortert etter navn.:\n" + String.join("\n", studentList));
    } catch (SQLException e) {
        showMessageDialog(null, "Feil ved henting av data fra databasen " + e.getMessage());
    }
}
    private static void displayAllMobileNos() {
        // Implementere metode for å vise alle studenter sortert via mobil number (Abdi)
        showMessageDialog(null, "Viser alle studenter sortert etter telefonnummer");
    try {
        String sql = "SELECT * FROM Student ORDER BY Number";
        List<String> studentList = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                studentList.add(rs.getString("Name") + " - " + rs.getString("Number"));
            }
        }

        showMessageDialog(null, "Liste over studenter sortert etter mobilnummer.:\n" + String.join("\n", studentList));
    } catch (SQLException e) {
        showMessageDialog(null, "Feil ved henting av data fra database: " + e.getMessage());
    }
}

    private static void showAllVerv() {
    	// Implementer metoden for å vise alle studenter er sortert etter verv (Abdi)
        showMessageDialog(null, "Viser alle studenter som har verv");
        try {
            String sql = "SELECT * FROM Student WHERE Position IS NOT NULL ORDER BY Position";
            List<String> studentList = new ArrayList<>();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    studentList.add(rs.getString("Name") + " - " + rs.getString("Position"));
                }
            }

            if (!studentList.isEmpty()) {
                showMessageDialog(null, "Liste over studenter sortert etter stilling (verv).\":\n" + String.join("\n", studentList));
            } else {
                showMessageDialog(null, "Ingen studenter med verv funnet..");
            }
        } catch (SQLException e) {
            showMessageDialog(null, "Feil ved henting av data fra databasen: " + e.getMessage());
        }
    }

    private static void addStudent() {
    	// Implement method to add a new student to the registry (Abdi)
        showMessageDialog(null, "Legger til en ny student");
        try {
            // Be brukeren om studentopplysninger.
            String name = showInputDialog("Skriv inn studentens navn:");
            String email = showInputDialog("Skriv inn studentens epost:");
            String number = showInputDialog("Skriv inn studentens nummer:");
            String position = showInputDialog("Skriv inn studentens stilling (optional):");

            // Forbered SQL-setning for å sette inn ny student
            String sql = "INSERT INTO Student (Name, Email, Number, Position) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, number);
                pstmt.setString(4, position);

                // Execute SQL statement to insert new student
                pstmt.executeUpdate();

                showMessageDialog(null, "Student lagt til vellykket.");
            }
        } catch (SQLException e) {
            showMessageDialog(null, "Feil ved å legge til student: " + e.getMessage());
        }
    }

    private static void editStudent() {
    	// Implementer metoden for å redigere studentinformasjon (Abdi)
        showMessageDialog(null, "Redigerer studentinformasjon");
        try {
            // Be brukeren om studentnummeret for å redigere.
            int studentNumber = Integer.parseInt(showInputDialog("Skriv inn studentnummeret for å redigere:"));

            // Sjekk om studentnummeret eksisterer
            String checkSql = "SELECT * FROM Student WHERE Strno = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, studentNumber);
                ResultSet rs = checkStmt.executeQuery();

                // Hvis studentnummeret ikke eksisterer, vis melding og returner
                if (!rs.next()) {
                    showMessageDialog(null, "Studentnummeret eksisterer ikke.");
                    return;
                }
            }

            // Be brukeren om oppdatert informasjon.
            String newName = showInputDialog("Skriv inn nytt navn (la feltet være tomt for å beholde gjeldende):");
            String newEmail = showInputDialog("Skriv inn ny e-post (la feltet være tomt for å beholde gjeldende):");
            String newNumber = showInputDialog("Skriv inn nytt mobilnummer (la feltet være tomt for å beholde gjeldende):");
            String newPosition = showInputDialog("Skriv inn ny stilling (la feltet være tomt for å beholde gjeldende):");

            // Forbered SQL-setning for å oppdatere studentinformasjon.
            StringBuilder updateSqlBuilder = new StringBuilder("UPDATE Student SET");
            List<Object> params = new ArrayList<>();

            if (!newName.isEmpty()) {
                updateSqlBuilder.append(" Name = ?,");
                params.add(newName);
            }
            if (!newEmail.isEmpty()) {
                updateSqlBuilder.append(" Email = ?,");
                params.add(newEmail);
            }
            if (!newNumber.isEmpty()) {
                updateSqlBuilder.append(" Number = ?,");
                params.add(newNumber);
            }
            if (!newPosition.isEmpty()) {
                updateSqlBuilder.append(" Position = ?,");
                params.add(newPosition);
            }

            // Fjern etterfølgende komma
            updateSqlBuilder.deleteCharAt(updateSqlBuilder.length() - 1);

            updateSqlBuilder.append(" WHERE Strno = ?");
            String updateSql = updateSqlBuilder.toString();
            params.add(studentNumber);

            // Utfør SQL-setning for å oppdatere studentinformasjon
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                int i = 1;
                for (Object param : params) {
                    updateStmt.setObject(i++, param);
                }
                updateStmt.executeUpdate();

                showMessageDialog(null, "Studentinformasjonen ble oppdatert vellykket.");
            }
        } catch (SQLException e) {
            showMessageDialog(null, "Feil ved redigering av studentinformasjon: " + e.getMessage());
        } catch (NumberFormatException e) {
            showMessageDialog(null, "Ugyldig studentnummerformat.");
        }
    }

    private static void deleteStudent() {
    	// Implementer metoden for å slette en student fra registeret (Abdi)
        showMessageDialog(null, "Sletter en student");
        try {
            // Be brukeren om studentnummeret som skal slettes
            int studentNumber = Integer.parseInt(showInputDialog("Skriv inn studentnummeret som skal slettes:"));

            // Forbered SQL-setning for å slette student
            String deleteSql = "DELETE FROM Student WHERE Strno = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, studentNumber);

                // Utfør SQL-setning for å slette student
                int rowsAffected = pstmt.executeUpdate();

                // Sjekk om noen rader ble påvirket
                if (rowsAffected > 0) {
                    showMessageDialog(null, "Studenten ble slettet vellykket.");
                } else {
                    showMessageDialog(null, "Ingen student ble funnet med det oppgitte studentnummeret.");
                }
            }
        } catch (SQLException e) {
            showMessageDialog(null, "Feil ved sletting av student: " + e.getMessage());
        } catch (NumberFormatException e) {
            showMessageDialog(null, "Ugyldig studentnummerformat.");
        }
    }

    private static void backupRegistry() {
    	// Implementer metoden for å ta en sikkerhetskopi av registeret (Abdi)
        showMessageDialog(null, "Tar en sikkerhetskopi av registeret");
        try {
            // Be brukeren om sikkerhetskopifilnavn.
            String backupFileName = showInputDialog("Skriv inn sikkerhetskopi filnavn.:");

            // Forbered SQL-setning for å velge alle studenter.
            String selectSql = "SELECT * FROM Student";

            // Opprett BufferedWriter for å skrive til sikkerhetskopi fil
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFileName))) {
                // Utfør SQL-setning for å velge alle studenter
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(selectSql)) {
                    // Skriv hver students informasjon til sikkerhetskopifilen
                    while (rs.next()) {
                        writer.write(rs.getInt("Strno") + "," +
                                rs.getString("Name") + "," +
                                rs.getString("Email") + "," +
                                rs.getString("Number") + "," +
                                rs.getString("Position"));
                        writer.newLine();
                    }
                }

                showMessageDialog(null, "Register sikkerhetskopiert vellykket");
            }
        } catch (SQLException | IOException e) {
            showMessageDialog(null, "Feil ved sikkerhetskopiering av registeret: " + e.getMessage());
        }
    }

    private static void loadBackup() {
    	// Implementer metoden for å laste registeret fra en sikkerhetskopi (Abdi)
        showMessageDialog(null, "Laster register fra sikkerhetskopi");
        try {
            // Be brukeren om sikkerhetskopifilnavn.
            String backupFileName = showInputDialog("Skriv inn sikkerhetskopi filnavn:");

            // Forbered SQL-setning for å sette inn studentdata
            String insertSql = "INSERT INTO Student (Strno, Name, Email, Number, Position) VALUES (?, ?, ?, ?, ?)";

            // Opprett BufferedReader for å lese fra sikkerhetskopi fil
            try (BufferedReader reader = new BufferedReader(new FileReader(backupFileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        int strno = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String email = parts[2];
                        String number = parts[3];
                        String position = parts[4];

                        // Forbered og utfør SQL-setning for å sette inn studentdata
                        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                            pstmt.setInt(1, strno);
                            pstmt.setString(2, name);
                            pstmt.setString(3, email);
                            pstmt.setString(4, number);
                            pstmt.setString(5, position);
                            pstmt.executeUpdate();
                        }
                    }
                }

                showMessageDialog(null, "Registeret ble lastet fra sikkerhetskopi vellykket.");
            } catch (IOException e) {
                showMessageDialog(null, "Feil ved lesing fra sikkerhetskopi fil: " + e.getMessage());
            }
        } catch (Exception e) {
            showMessageDialog(null, "Feil ved lasting av register fra sikkerhetskopi " + e.getMessage());
        }
    }

    private static void exitProgram() {
    	// Implementer metoden for å avslutte programmet (Abdi)
        showMessageDialog(null, "Avslutter programmet");
        System.exit(0);
        try {
            conn.close(); // Lukk databaseforbindelsen før du avslutter
        } catch (SQLException e) {
            showMessageDialog(null, "Feil ved lukking av databaseforbindelse: " + e.getMessage());
        } finally {
            showMessageDialog(null, "Avslutter programmet.");
            System.exit(0); // Avslutt programmet
        }
    }
}
