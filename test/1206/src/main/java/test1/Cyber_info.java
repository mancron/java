package test1;

import java.sql.*;

//attendance          //출석
//absent            //결석
//Non_attendance  //미수강
//Before_completion   //완료전
//lectureName         //과목명

public class Cyber_info {

    private static final String URL = "jdbc:mysql://localhost:3306/door"; // 링크door
    private static final String USERNAME = "root"; // 유저이름(아이디
    private static final String PASSWORD = "0000"; // 비밀번호

    public static void saveInfo(String Cyber_attendance, String Cyber_absent, String Cyber_Non_attendance, String Cyber_Before_completion, String Cyber_assignmentName) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String getIdQuery = "SELECT doorid FROM doorp LIMIT 1";
            PreparedStatement getIdStatement = connection.prepareStatement(getIdQuery);
            ResultSet idResultSet = getIdStatement.executeQuery();

            String doorIdFromDB = null;

            if (idResultSet.next()) {
                doorIdFromDB = idResultSet.getString("doorid");

                // assignmentName이 존재하는지 확인
                String getAssignmentQuery = "SELECT Cyber_attendance, Cyber_absent, Cyber_Non_attendance, Cyber_Before_completion " +
                        "FROM cyber_task WHERE ID = ? AND Cyber_assignmentName = ?";
                PreparedStatement getAssignmentStatement = connection.prepareStatement(getAssignmentQuery);
                getAssignmentStatement.setString(1, doorIdFromDB);
                getAssignmentStatement.setString(2, Cyber_assignmentName);
                ResultSet assignmentResultSet = getAssignmentStatement.executeQuery();

                if (assignmentResultSet.next()) {
                    String attendanceFromDB = assignmentResultSet.getString("Cyber_attendance");
                    String absentFromDB = assignmentResultSet.getString("Cyber_absent");
                    String nonAttendanceFromDB = assignmentResultSet.getString("Cyber_Non_attendance");
                    String beforeCompletionFromDB = assignmentResultSet.getString("Cyber_Before_completion");

                    // Compare values and update if any field differs
                    if (!attendanceFromDB.equals(Cyber_attendance) || !absentFromDB.equals(Cyber_absent) ||
                            !nonAttendanceFromDB.equals(Cyber_Non_attendance) || !beforeCompletionFromDB.equals(Cyber_Before_completion)) {

                        String updateQuery = "UPDATE cyber_task SET Cyber_attendance = ?, Cyber_absent = ?, " +
                                "Cyber_Non_attendance = ?, Cyber_Before_completion = ? WHERE ID = ? AND Cyber_assignmentName = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, Cyber_attendance);
                        updateStatement.setString(2, Cyber_absent);
                        updateStatement.setString(3, Cyber_Non_attendance);
                        updateStatement.setString(4, Cyber_Before_completion);
                        updateStatement.setString(5, doorIdFromDB);
                        updateStatement.setString(6, Cyber_assignmentName);

                        int rowsAffected = updateStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("과제 데이터가 성공적으로 업데이트되었습니다!");
                        } else {
                            System.out.println("과제 데이터 업데이트에 실패했습니다.");
                        }
                    } else {
                        System.out.println("이미 동일한 값으로 업데이트되었습니다. 업데이트하지 않았습니다.");
                    }
                } else {
                    // assignmentName이 존재하지 않으면 삽입
                    String insertQuery = "INSERT INTO cyber_task (Cyber_attendance, Cyber_absent, " +
                            "Cyber_Non_attendance, Cyber_Before_completion, Cyber_assignmentName, ID) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, Cyber_attendance);
                    insertStatement.setString(2, Cyber_absent);
                    insertStatement.setString(3, Cyber_Non_attendance);
                    insertStatement.setString(4, Cyber_Before_completion);
                    insertStatement.setString(5, Cyber_assignmentName);
                    insertStatement.setString(6, doorIdFromDB);

                    int rowsAffected = insertStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("과제 데이터가 성공적으로 삽입되었습니다!");
                    } else {
                        System.out.println("과제 데이터 삽입에 실패했습니다.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

