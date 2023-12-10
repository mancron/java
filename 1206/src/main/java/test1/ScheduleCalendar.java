package test1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public class ScheduleCalendar {
    private JFrame frame;  // 메인 프레임
    private JPanel calendarPanel;  // 달력을 표시할 패널
    private JLabel monthAndYearLabel;  // 현재 월과 년도를 표시할 레이블
    private JLabel[][] dayLabels;  // 각 날짜를 표시할 레이블 배열
    private List<Schedule> scheduleList;  // 학사 일정 리스트
    private List<Todo> todoList;
    private int currentMonth;  // 현재 월
    private int currentYear;  // 현재 년도
    private int daysInMonth;  // 현재 월의 일수

    private static final String[] MONTHS = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};
    private static final String[] DAYS_OF_WEEK = {"일", "월", "화", "수", "목", "금", "토"};

    // 데이터베이스 관련 상수
    private static final String DB_URL = "jdbc:mysql://localhost:3306/door";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "0000";
    
    
    private void loadTodoList() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT Year, Month, Date, Schedule FROM todolist")) {
             todoList =new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int year = resultSet.getInt("Year");
                int month = resultSet.getInt("Month");
                int date = resultSet.getInt("Date");
                String schedule = resultSet.getString("Schedule");
                todoList.add(new Todo(year, month, date, schedule));
            }
        } catch (SQLException e) {
            e.printStackTrace();  // 예외 발생 시 출력
        }
    }
    
    private String findTodoForDay(int year, int month, int day) {
        for (Todo todo : todoList) {
            if (todo.getYear() == (year-2000) && todo.getMonth() == month && todo.getDate() == day) {
                return todo.getSchedule();
            }
        }
        return null;
    }

    // 학사 일정 데이터를 데이터베이스에서 로드
    private void loadScheduleData() {
        scheduleList = new ArrayList<>();  // 학사 일정 리스트 생성

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT Year, Month, Date, Schedule FROM academic_schedule")) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int year = resultSet.getInt("Year");
                int month = resultSet.getInt("Month");
                int date = resultSet.getInt("Date");
                String schedule = resultSet.getString("Schedule");
                scheduleList.add(new Schedule(year, month, date, schedule));
            }
        } catch (SQLException e) {
            e.printStackTrace();  // 예외 발생 시 출력
        }
    }
    // 생성자: 초기화 작업을 수행
    public ScheduleCalendar() {
        frame = new JFrame("학사 일정");  // 메인 프레임 생성
        frame.setSize(800, 600);  // 프레임 크기 설정
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 프레임의 닫기 버튼을 눌렀을 때 프로그램이 종료되도록 설정

        JPanel headerPanel = new JPanel();  // 헤더 패널 생성
        JButton prevButton = new JButton("<<");  // 이전 월로 이동하는 버튼 생성
        prevButton.addActionListener(new ActionListener() {  // 이전 버튼 클릭 이벤트 핸들러
            @Override
            public void actionPerformed(ActionEvent e) {
                currentMonth--;  // 현재 월을 하나 뺌
                if (currentMonth < 0) {  // 현재 월이 0보다 작아지면
                    currentMonth = 11;  // 현재 월을 12월로 설정하고
                    currentYear--;  // 현재 년도를 하나 뺌
                }
                updateCalendar();  // 달력 업데이트
            }
        });

        JPanel bottomPanel = new JPanel();

        // Home 버튼 생성
        JButton homeButton = new JButton("Home");
        // Home 버튼을 패널에 추가
        bottomPanel.add(homeButton);
        // 패널을 프레임의 남쪽에 추가
        frame.add(bottomPanel, BorderLayout.SOUTH);
        homeButton.addActionListener(new ActionListener() {  // Home 버튼 클릭 이벤트 핸들러
            @Override
            public void actionPerformed(ActionEvent e) {
               new HomeForm();
               frame.setVisible(false);
            }
        });
        
        monthAndYearLabel = new JLabel("", SwingConstants.CENTER);  // 현재 월과 년도를 표시할 레이블 생성

        JButton nextButton = new JButton(">>");  // 다음 월로 이동하는 버튼 생성
        nextButton.addActionListener(new ActionListener() {  // 다음 버튼 클릭 이벤트 핸들러
            @Override
            public void actionPerformed(ActionEvent e) {
                currentMonth++;  // 현재 월을 하나 더함
                if (currentMonth > 11) {  // 현재 월이 12보다 크면
                    currentMonth = 0;  // 현재 월을 1월로 설정하고
                    currentYear++;  // 현재 년도를 하나 더함
                }
                updateCalendar();  // 달력 업데이트
            }
        });

        headerPanel.add(prevButton);  // 이전 버튼을 헤더 패널에 추가
        headerPanel.add(monthAndYearLabel);  // 월/년도 레이블을 헤더 패널에 추가
        headerPanel.add(nextButton);  // 다음 버튼을 헤더 패널에 추가

        frame.add(headerPanel, BorderLayout.NORTH);  // 헤더 패널을 프레임의 북쪽에 추가

        calendarPanel = new JPanel(new GridLayout(7, 7));  // 달력 패널 생성 (7x7 그리드 레이아웃)
        dayLabels = new JLabel[7][7];  // 각 날짜를 표시할 레이블 배열 생성
   
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if(i == 0)
                    dayLabels[i][j] = new JLabel("", SwingConstants.CENTER);  // 레이블 생성    
                else{
                    dayLabels[i][j] = new JLabel("", SwingConstants.LEFT);  // 레이블 생성
                    dayLabels[i][j].setVerticalAlignment(SwingConstants.TOP);
                }
                    
                dayLabels[i][j].setOpaque(true);  // 레이블 배경색을 활성화
                calendarPanel.add(dayLabels[i][j]);  // 레이블을 달력 패널에 추가
            }
        }

        frame.add(calendarPanel, BorderLayout.CENTER);  // 달력 패널을 프레임의 중앙에 추가

        Calendar currentDate = Calendar.getInstance();  // 현재 날짜/시간 정보를 가진 Calendar 객체 생성
        currentMonth = currentDate.get(Calendar.MONTH);  // 현재 월 가져오기
        currentYear = currentDate.get(Calendar.YEAR);// 현재 년도 가져오기

        loadScheduleData();  // 학사 일정 데이터 로드
        loadTodoList(); // todolsit 데이터 로드
        updateCalendar();  // 달력 업데이트

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);  // 프레임을 화면에 보이게 설정
    }

    // 달력 업데이트: 현재 월/년도에 맞게 달력 레이블을 업데이트하고, 학사 일정을 표시
    private void updateCalendar() {
        Calendar currentDate = new GregorianCalendar(currentYear, currentMonth, 1);
        daysInMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        monthAndYearLabel.setText(currentYear + "년 " + MONTHS[currentMonth]);
    
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                String text = "";
                if (i == 0) {
                    dayLabels[i][j].setBackground(Color.LIGHT_GRAY);
                    text = DAYS_OF_WEEK[j];
                } else {
                    int day = (i - 1) * 7 + j - currentDate.get(Calendar.DAY_OF_WEEK) + 2;
                    if (day > 0 && day <= daysInMonth) {
                        text = String.valueOf(day);
                        String schedule = findScheduleForDay(currentYear, currentMonth + 1, day);
                        if (schedule != null) text += "<br>" + schedule;
                        String todo = findTodoForDay(currentYear, currentMonth + 1, day);
                        if (todo != null) text += "<br>" + todo;
                    }
                }
                dayLabels[i][j].setFont(new Font("돋움", Font.PLAIN, 14));
                dayLabels[i][j].setText("<html>" + text + "</html>");
            }
        }
        frame.revalidate();  
        frame.repaint();  
    }

    // 특정 날짜의 학사 일정 찾기
    private String findScheduleForDay(int Year,int month, int day) {
        for (Schedule schedule : scheduleList) {  // 모든 학사 일정에 대해
            if (schedule.getYear() == (currentYear - 2000) && schedule.getMonth() == currentMonth + 1 && schedule.getDate() == day) {
                return schedule.getSchedule();  // 해당 일정 반환
            }
        }
        return null;  // 일정이 없으면 null 반환
    }

    // main 메소드: 프로그램 실행
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 한글을 지원하는 폰트로 변경
                Font font = new Font("돋움", Font.PLAIN, 14);
                UIManager.put("Button.font", font);
                UIManager.put("Label.font", font);

                new ScheduleCalendar();  // ScheduleCalendar 인스턴스 생성 및 실행
            }
        });
    }

    // 내부 클래스: 학사 일정을 표현
    private class Schedule {
        private int year;  // 년
        private int month;  // 월
        private int date;  // 일
        private String schedule;  // 일정

        // 생성자: 년, 월, 일, 일정을 초기화
        public Schedule(int year, int month, int date, String schedule) {
            this.year = year;
            this.month = month;
            this.date = date;
            this.schedule = schedule;
        }

        // 년을 반환하는 메소드
        public int getYear() {
            return year;
        }

        // 월을 반환하는 메소드
        public int getMonth() {
            return month;
        }

        // 일을 반환하는 메소드
        public int getDate() {
            return date;
        }

        // 일정을 반환하는 메소드
        public String getSchedule() {
            return schedule;
        }
    }
    
    private class Todo {
        private int year;  // 년
        private int month;  // 월
        private int date;  // 일
        private String schedule;  // 일정

        // 생성자: 년, 월, 일, 일정을 초기화
        public Todo(int year, int month, int date, String schedule) {
            this.year = year;
            this.month = month;
            this.date = date;
            this.schedule = schedule;
        }

        // 년을 반환하는 메소드
        public int getYear() {
            return year;
        }

        // 월을 반환하는 메소드
        public int getMonth() {
            return month;
        }

        // 일을 반환하는 메소드
        public int getDate() {
            return date;
        }

        // 일정을 반환하는 메소드
        public String getSchedule() {
            return schedule;
        }
    }
}