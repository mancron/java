package test1;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CyberForm extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/door";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "0000";
    DefaultTableModel model;

    public CyberForm() {
        super("사이버강좌 리스트");

        Container contentPaneTask = getContentPane();
        contentPaneTask.setLayout(null);

        String[] TaskcolumnNames = {"강의명", "출석", "결석", "미수강", "완료전"};
        // 테이블 모델 및 테이블 생성
        model = new DefaultTableModel(TaskcolumnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;  // 모든 열을 문자열로 설정
            }
        };
        JTable Tasktable = new JTable(model);
        // 강의명 열의 index를 가져옴
        int lectureColumnIndex = getLectureColumnIndex(Tasktable, "강의명");

        // 강의명 열의 너비를 늘림
        Tasktable.getColumnModel().getColumn(lectureColumnIndex).setPreferredWidth(300); // 300은 너비 값으로 조절 가능

        // 셀의 폰트와 크기 설정
        Font font = new Font("굴림", Font.PLAIN, 14);  // 폰트 및 크기 설정
        Tasktable.setFont(font);
        Tasktable.getTableHeader().setFont(font);

        // 셀의 정렬 방식 설정
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        Tasktable.setDefaultRenderer(Object.class, centerRenderer);
        // 각 셀의 높이 설정
        int rowHeight = 30;  // 원하는 높이로 설정
        Tasktable.setRowHeight(rowHeight);

        JScrollPane scrollPane = new JScrollPane(Tasktable);
        scrollPane.setBounds(10, 50, 960, 400);
        contentPaneTask.add(scrollPane);

        JButton btn = new JButton("Home");
        btn.setBounds(10, 460, 180, 30);
        contentPaneTask.add(btn);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeForm();
                setVisible(false);
            }
        });

        // 새로고침 버튼
        JButton refreshButton = new JButton("새로고침");
        refreshButton.setBounds(430, 10, 100, 30);
        // 새로고침 버튼 클릭 시 refreshAssignment 메소드 호출
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAssignment();
            }
        });

        contentPaneTask.add(refreshButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        updateTable();
    }

    // 강의명 열의 인덱스를 가져오는 메소드
    private int getLectureColumnIndex(JTable table, String columnName) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private void updateTable() {
        model.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String loggedInID = LoginForm.getID();
            String query = "SELECT ID, Cyber_attendance, Cyber_absent, Cyber_Non_attendance, Cyber_Before_completion, Cyber_assignmentName FROM cyber_task WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, loggedInID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String attendance = resultSet.getString("Cyber_attendance");
                String absent = resultSet.getString("Cyber_absent");
                String nonAttendance = resultSet.getString("Cyber_Non_attendance");
                String beforeCompletion = resultSet.getString("Cyber_Before_completion");
                String assignmentName = resultSet.getString("Cyber_assignmentName");
                String[] row = {assignmentName, attendance, absent, nonAttendance, beforeCompletion};

                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 과제 크롤링 메소드
    private void refreshAssignment() {
        CyberCrawler cyber = new CyberCrawler(LoginForm.getID(), LoginForm.getPass());
        cyber.loginAndAccessClassroom();
        updateTable();
        // 새로고침이 완료되었다는 메시지 박스 표시
        JOptionPane.showMessageDialog(this, "과제 목록이 새로고침되었습니다.", "새로고침 완료", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        new CyberForm();
    }
}
