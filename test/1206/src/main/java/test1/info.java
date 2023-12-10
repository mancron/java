package test1;

import java.sql.*;

public class info {

    private static final String URL = "jdbc:mysql://localhost:3306/door"; // 링크door
    private static final String USERNAME = "root"; // 유저이름(아이디
    private static final String PASSWORD = "0000"; // 비밀번호

    public static void saveInfo(String toDoDate, String lectureName, String assignmentName, String toDoSubmit) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String getIdQuery = "SELECT doorid FROM doorp LIMIT 1";
            PreparedStatement getIdStatement = connection.prepareStatement(getIdQuery);
            ResultSet idResultSet = getIdStatement.executeQuery();

            String doorIdFromDB = null;

            if (idResultSet.next()) {
                doorIdFromDB = idResultSet.getString("doorid");

                // assignmentName 쿼리
                String getAssignmentNameQuery = "SELECT assignmentName FROM task WHERE ID = ?";
                PreparedStatement getAssignmentNameStatement = connection.prepareStatement(getAssignmentNameQuery);
                getAssignmentNameStatement.setString(1, doorIdFromDB);
                ResultSet assignmentResultSet = getAssignmentNameStatement.executeQuery();

                boolean assignmentExists = false;
                while (assignmentResultSet.next()) {
                    String assignmentNameFromDB = assignmentResultSet.getString("assignmentName");
                    if (assignmentNameFromDB.equals(assignmentName)) {
                        assignmentExists = true;
                        break;
                    }
                }

                if (assignmentExists) {
                    // assignmentName이 이미 존재함 -> toDoSubmit 값 체크 후 업데이트
                    String getToDoSubmitQuery = "SELECT toDoSubmit, ID FROM task WHERE ID = ? AND assignmentName = ?";
                    PreparedStatement getToDoSubmitStatement = connection.prepareStatement(getToDoSubmitQuery);
                    getToDoSubmitStatement.setString(1, doorIdFromDB);
                    getToDoSubmitStatement.setString(2, assignmentName);
                    ResultSet toDoSubmitResultSet = getToDoSubmitStatement.executeQuery();

                    if (toDoSubmitResultSet.next()) {
                        String toDoSubmitFromDB = toDoSubmitResultSet.getString("toDoSubmit");
                        String idFromDB = toDoSubmitResultSet.getString("ID");
                        if (!toDoSubmitFromDB.equals(toDoSubmit) && idFromDB.equals(doorIdFromDB)) {
                            // toDoSubmit이 다를 때만 업데이트
                            String updateQuery = "UPDATE task SET toDoSubmit = ? WHERE ID = ? AND assignmentName = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                            updateStatement.setString(1, toDoSubmit);
                            updateStatement.setString(2, doorIdFromDB);
                            updateStatement.setString(3, assignmentName);

                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("과제 데이터가 성공적으로 업데이트되었습니다!");
                            } else {
                                System.out.println("과제 데이터 업데이트에 실패했습니다.");
                            }
                        } else {
                            // toDoSubmit이 이미 같거나 아이디가 다르므로 업데이트하지 않음
                            System.out.println("ToDoSubmit이 이미 같거나 아이디가 다릅니다. 업데이트하지 않았습니다.");
                        }
                    }
                } else {
                    // assignmentName이 일치하지 않음 -> 삽입
                    String insertQuery = "INSERT INTO task (toDoDate, lectureName, assignmentName, toDoSubmit, ID) " +
                            "VALUES (?, ?, ?, ?, ?)";

                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, toDoDate);
                    insertStatement.setString(2, lectureName);
                    insertStatement.setString(3, assignmentName);
                    insertStatement.setString(4, toDoSubmit);
                    insertStatement.setString(5, doorIdFromDB);

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

