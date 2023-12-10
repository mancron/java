package test1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.sql.*;

public class TaskForm extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/door";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "0000";
    DefaultTableModel model;
    JCheckBox deadlinePassedCheckBox;
    JCheckBox notSubmittedCheckBox;

    public TaskForm() {
        super("과제 리스트");

        Container contentPaneTask = getContentPane();
        contentPaneTask.setLayout(null);

        String[] TaskcolumnNames = {"제출 마감일", "강의명", "과목명", "제출 여부"};
        model = new DefaultTableModel(TaskcolumnNames, 0);
        JTable Tasktable = new JTable(model);
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

        deadlinePassedCheckBox = new JCheckBox("제출 마감일 지난 것 생략");
        deadlinePassedCheckBox.setBounds(10, 10, 200, 30);
        deadlinePassedCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateTable();
            }
        });

        notSubmittedCheckBox = new JCheckBox("미제출 과제만 표시");
        notSubmittedCheckBox.setBounds(220, 10, 200, 30);
        notSubmittedCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateTable();
            }
        });

        contentPaneTask.add(deadlinePassedCheckBox);
        contentPaneTask.add(notSubmittedCheckBox);

        // 새로고침 버튼
        JButton refreshButton = new JButton("새로고침");
        refreshButton.setBounds(430, 10, 100, 30);
        // 새로고침 버튼 클릭 시 refreshAssignment 메소드 호출
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAssignment();
        }

        // 과제 크롤링 메소드
        private void refreshAssignment() {
            Main main = new Main(LoginForm.getID(), LoginForm.getPass());
            main.loginAndAccessClassroom();
            updateTable();
            // 새로고침이 완료되었다는 메시지 박스 표시
            JOptionPane.showMessageDialog(contentPaneTask, "과제 목록이 새로고침되었습니다.", "새로고침 완료", JOptionPane.INFORMATION_MESSAGE);
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

    private void updateTable() {
        model.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String loggedInID = LoginForm.getID();
            String query = "SELECT toDoDate, lectureName, assignmentName, toDoSubmit FROM task WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, loggedInID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String deadline = resultSet.getString("toDoDate");
                String lecture = resultSet.getString("lectureName");
                String subject = resultSet.getString("assignmentName");
                String submission = resultSet.getString("toDoSubmit");
                String[] row = {deadline, lecture, subject, submission};

                if (deadlinePassedCheckBox.isSelected() && isDeadlinePassed(deadline)) {
                    continue;
                }

                if (notSubmittedCheckBox.isSelected() && !isNotSubmitted(submission)) {
                    continue;
                }

                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isDeadlinePassed(String task) {
        String[] parts = task.split(":|/");
        String deadlineString = parts[0].trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 M월 d일", Locale.KOREAN);
        TemporalAccessor temporalAccessor = formatter.parse(deadlineString);
        LocalDate deadline = LocalDate.from(temporalAccessor);

        return deadline.isBefore(LocalDate.now());
    }

    private boolean isNotSubmitted(String assignment) {
        String[] parts = assignment.split(":|/");
        String assignmentSubmit = parts[parts.length - 1].trim();
        return assignmentSubmit.equals("미제출");
    }
}
