package test1;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;

public class Todolist extends JFrame {
	 private static final String URL = "jdbc:mysql://localhost:3306/door"; // 링크door
	    private static final String USERNAME = "root"; // 유저이름(아이디
	    private static final String PASSWORD = "0000"; // 비밀번호
    private JTextArea toDoTextArea;

    public Todolist() {
        super("To-Do List");

        setLayout(new BorderLayout());

        // 패널 생성
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(80, 50, 0, 50); // 위쪽 여백 80, 좌우 여백 50
        gbc.gridwidth = 1; // 각 요소의 너비

        // 현재 년도 구하기
        int currentYear = LocalDate.now().getYear();

        // 년 월 일을 선택할 스피너 추가
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear % 100, 0, 99, 1));
        JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));

        // 할 일을 입력할 텍스트 필드 추가
        JTextField toDoTextField = new JTextField();

        // 버튼 추가
        JButton addButton = new JButton("추가");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int year = (int) yearSpinner.getValue();
                int month = (int) monthSpinner.getValue();
                int day = (int) daySpinner.getValue();

                // 두 자리 숫자로 입력된 연도를 4자리 연도로 변경
                if (year < 100) {
                    year += currentYear - (currentYear % 100); // 예: 23 -> 2023으로 변경
                }

                // 입력된 날짜 유효성 검증
                if (!isValidDate(year, month, day)) {
                    JOptionPane.showMessageDialog(Todolist.this, "입력된 날짜는 유효하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String toDo = toDoTextField.getText().trim();

                // 날짜와 할 일을 출력하는 텍스트 에리어에 추가
                toDoTextArea.append(String.format("%d년 %d월 %d일: %s%n", year, month, day, toDo));

                // 데이터베이스에 입력
                try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                    String sql = "INSERT INTO todolist (Year, Month, Date, Schedule) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setInt(1, year-2000);
                        statement.setInt(2, month);
                        statement.setInt(3, day);
                        statement.setString(4, toDo);
                        statement.executeUpdate();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Todolist.this, "데이터베이스 연결 또는 쿼리 실행 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }

                toDoTextField.setText("");
            }
        });

     // 패널에 구성 요소 추가
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(20, 50, 0, 50);
        inputPanel.add(new JLabel("년도"), gbc);
        gbc.gridx = 1; inputPanel.add(new JLabel("월"), gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("일"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(7, 50, 0, 50);
        inputPanel.add(yearSpinner, gbc);
        gbc.gridx = 1; inputPanel.add(monthSpinner, gbc);
        gbc.gridx = 2; inputPanel.add(daySpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(30, 0, 0, 20);
        inputPanel.add(new JLabel("할 일"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; // 1행의 1,2열에 걸치도록JTextField toDoTextField = new JTextField();
        gbc.insets = new Insets(30, -70, 0, 0);
        toDoTextField.setPreferredSize(new Dimension(280, 30)); // 너비 280, 높이 30으로 설정
        inputPanel.add(toDoTextField, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 1; // 2행 전체에 걸치도록
        gbc.insets = new Insets(15, 10, 25, 10);
        inputPanel.add(addButton, gbc);

        // 스크롤 가능한 텍스트 에리어 생성
        toDoTextArea = new JTextArea();
        toDoTextArea.setEditable(false); // 편집 불가능하도록 설정
        toDoTextArea.setMargin(new Insets(0, 20, 0, 0));
        JScrollPane scrollPane = new JScrollPane(toDoTextArea);

        // 프레임에 패널과 스크롤 패인 추가
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeForm();
                Todolist.this.dispose();
            }
        });
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 입력된 날짜가 유효한지 확인하는 메소드
    private boolean isValidDate(int year, int month, int day) {
        try {
            LocalDate.of(year, month, day);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Todolist();
            }
        });
    }
}
