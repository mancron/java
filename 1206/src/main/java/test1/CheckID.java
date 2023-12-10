package test1;


import java.sql.*;

public class CheckID {
    private static final String URL = "jdbc:mysql://localhost:3306/door";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "0000";

    public static void saveID(String id, String password) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String checkQuery = "SELECT COUNT(*) FROM doorp WHERE doorid = ?";//아이이 중복확인 을위한 query 
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);//아이이 중복확인을 위한 query 를 데이터베이스에 연결   
            checkStatement.setString(1, id);
            ResultSet resultSet = checkStatement.executeQuery();//resultSet에 checkStatement 의 실행값을반환함
            resultSet.next();//resultSet.next()는 ResultSet에서 다음 행으로 이동합니다. 이 메서드는 현재 행을 다음 행으로 이동시키고, 이동한 다음 행이 존재하면 true를 반환하고, 더 이상 행이 없으면 false를 반환합니다.
            int count = resultSet.getInt(1); //getInt(1)은 첫 번째 열의 값을 정수로 가져오는 메서드임 행이없으면 0 있으면 1을 count변수에 반환함

            if (count == 0) {//아이디가 없을때
                String insertQuery = "INSERT INTO doorp (doorid, password) VALUES (?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, id);
                insertStatement.setString(2, password);
                insertStatement.executeUpdate();
                System.out.println("데이터가 성공적으로 삽입되었습니다!");
            } else {//이미 입력된 아이디일때
                System.out.println("이미 존재하는 아이디입니다. 데이터를 삽입하지 않습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}