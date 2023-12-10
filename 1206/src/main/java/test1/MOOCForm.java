package test1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Locale;

public class MOOCForm extends JFrame {
   private static final String URL = "jdbc:mysql://localhost:3306/door";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "0000";
    ArrayList<String> list;  // Mooc 리스트 저장
    DefaultTableModel model;  // 테이블 모델
    JCheckBox deadlinePassedCheckBox;  // 제출 마감일이 지난 과제를 숨길지 결정하는 체크박스
    JCheckBox notSubmittedCheckBox;  // 미제출 과제만 보여줄지 결정하는 체크박스

    public MOOCForm() {
        super("MooC 출석 여부");
        list = Main.MoocgetList();  // 과제 리스트 가져오기

        Container contentPaneMooc = getContentPane();
        contentPaneMooc.setLayout(null);

        String[] MooCcolumnNames = {"수업 마감일", "강의주제", "강의명", "출결 상태"};
        model = new DefaultTableModel(MooCcolumnNames, 0);
        JTable MooCtable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(MooCtable);
        scrollPane.setBounds(10, 50, 960, 400);
        contentPaneMooc.add(scrollPane);

        JButton btn = new JButton("Home");
        btn.setBounds(10, 460, 180, 30);
        contentPaneMooc.add(btn);

        // Home 버튼 클릭 시 HomeForm을 보여주고 현재 폼을 닫음
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeForm();
                setVisible(false);
            }
        });

        deadlinePassedCheckBox = new JCheckBox("수업 마감일 지난 것 생략");
        deadlinePassedCheckBox.setBounds(10, 10, 200, 30);
        deadlinePassedCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateTable();  // 체크박스 상태 변경 시 테이블 업데이트
            }
        });

        notSubmittedCheckBox = new JCheckBox("미출결만 표시");
        notSubmittedCheckBox.setBounds(220, 10, 200, 30);
        notSubmittedCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateTable();  // 체크박스 상태 변경 시 테이블 업데이트
            }
        });

        JButton refreshButton = new JButton("새로고침");
        refreshButton.setBounds(430, 10, 100, 30);

        // 새로고침 버튼 클릭 시 테이블 업데이트
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAssignment();
            }
        });

        contentPaneMooc.add(deadlinePassedCheckBox);
        contentPaneMooc.add(notSubmittedCheckBox);
        contentPaneMooc.add(refreshButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        updateTable();  // 초기 테이블 업데이트
    }

    // 과제 크롤링 메소드
    private void refreshAssignment() {
        Main main = new Main(LoginForm.getID(), LoginForm.getPass());
        main.MoocAccessClassroom();
        updateTable();
        // 새로고침이 완료되었다는 메시지 박스 표시
        JOptionPane.showMessageDialog(this, "MOOC 목록이 새로고침되었습니다.", "새로고침 완료", JOptionPane.INFORMATION_MESSAGE);
    }

    // 체크박스 상태에 따라 테이블 데이터를 업데이트
    private void updateTable() {
        model.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String loggedInID = LoginForm.getID();
            String query = "SELECT mooc_toDoDate, mooc_lectureName, mooc_assignmentName, mooc_Attendance FROM mooc_task WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, loggedInID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String deadline = resultSet.getString("mooc_toDoDate");
                String lecture = resultSet.getString("mooc_lectureName");
                String subject = resultSet.getString("mooc_assignmentName");
                String submission = resultSet.getString("mooc_Attendance");
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

    // 제출 마감일이 지났는지 확인
    private boolean isDeadlinePassed(String task) {
        String[] parts = task.split(":|/");
        String deadlineString = parts[0].trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 M월 d일", Locale.KOREAN);
        TemporalAccessor temporalAccessor = formatter.parse(deadlineString);
        LocalDate deadline = LocalDate.from(temporalAccessor);

        return deadline.isBefore(LocalDate.now());
    }

    // 미제출인지 확인
    private boolean isNotSubmitted(String task) {
        String[] parts = task.split(":|/");
        String submission = parts[parts.length - 1].trim();

        return submission.equals("미수강");
    }

    public static void main(String[] args) {
        new MOOCForm();
    }
}
