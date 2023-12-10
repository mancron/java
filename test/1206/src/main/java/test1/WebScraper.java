package test1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.sql.*;


public class WebScraper {
	  private static final String URL = "jdbc:mysql://localhost:3306/door"; // 링크door
	    private static final String USERNAME = "root"; // 유저이름(아이디
	    private static final String PASSWORD = "0000"; // 비밀번호
	    
	    
    public WebScraper() {
        // 크롬 드라이버 경로 설정
        System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");

        // 크롬 옵션 설정: 브라우저를 띄우지 않고 백그라운드에서 동작하도록 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        // WebDriver 객체 생성
        WebDriver driver = new ChromeDriver(options);

        try {
            // 대상 웹 페이지 접속
            driver.get("https://www.deu.ac.kr/www/academic_calendar");

            // 1학기 학사일정 리스트 추출
            List<WebElement> elements1 = driver.findElements(By.xpath("//*[@id=\"iLayoutSubContent_uppnl\"]/div/div[3]/table/tbody/tr/td[1]/ul/li"));

            // 2학기 학사일정 리스트 추출
            List<WebElement> elements2 = driver.findElements(By.xpath("//*[@id=\"iLayoutSubContent_uppnl\"]/div/div[3]/table/tbody/tr/td[2]/ul/li"));

            // 날짜 형식 지정
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 M월 d일");
            
            Connection connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);

            // 현재 연도 가져오기 (두 자리 숫자로 변환)
            String yearPrefix = String.valueOf(LocalDate.now().getYear()).substring(2, 4);
            int year = Integer.parseInt(yearPrefix);

            // 1학기 학사일정 출력 및 콘솔에 표시
            for (WebElement element : elements1) {
                String text = element.getText();
                String[] parts = text.split(":");
                String schedule = parts[1].trim();

                if (parts[0].contains("~")) {
                    String[] dateRange = parts[0].trim().split("~");
                    dateRange[0] = dateRange[0].trim().replace(" ", "").replace("월", "월 ");
                    if (!dateRange[0].trim().endsWith("일")) dateRange[0] += "일";
                    dateRange[1] = dateRange[1].trim().replace(" ", "").replace("월", "월 ");
                    if (!dateRange[1].trim().endsWith("일")) dateRange[1] += "일";
                
                    // Extract the month from the start date if it's missing in the end date
                    if (!dateRange[1].contains("월")) {
                        String month = dateRange[0].split("월")[0];
                        dateRange[1] = month + "월 " + dateRange[1];
                    }
                    try {
                        LocalDate startDate = LocalDate.parse(year + "년 " + dateRange[0], formatter);
                        LocalDate endDate = LocalDate.parse(year + "년 " + dateRange[1], formatter);

                        // 데이터베이스에 이미 해당 정보가 있는지 확인
                        PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM academic_schedule WHERE Year = ? AND Month = ? AND Date = ?");
                        selectStatement.setInt(1, year);
                        selectStatement.setInt(2, startDate.getMonthValue());
                        selectStatement.setInt(3, startDate.getDayOfMonth());
                        ResultSet resultSet = selectStatement.executeQuery();

                        if (!resultSet.next()) { // 해당 날짜에 정보가 없으면 새로 삽입
                            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                                // 데이터베이스에 학사일정 정보 저장
                                PreparedStatement statement = connection.prepareStatement("INSERT INTO academic_schedule (Year, Month, Date, Schedule) VALUES (?, ?, ?, ?)");
                                statement.setInt(1, year);
                                statement.setInt(2, date.getMonthValue());
                                statement.setInt(3, date.getDayOfMonth());
                                statement.setString(4, schedule);
                                statement.executeUpdate();
                            }
                        }
                    }catch (DateTimeParseException | SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    String dateStr = parts[0].trim();

                    dateStr = dateStr.replace(" ", "").replace("월", "월 ");
                    if (!dateStr.trim().endsWith("일")) dateStr += "일";

                    try {
                        LocalDate date = LocalDate.parse(year + "년 " + dateStr, formatter);

                        // 데이터베이스에 이미 해당 정보가 있는지 확인
                        PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM academic_schedule WHERE Year = ? AND Month = ? AND Date = ?");
                        selectStatement.setInt(1, year);
                        selectStatement.setInt(2, date.getMonthValue());
                        selectStatement.setInt(3, date.getDayOfMonth());
                        ResultSet resultSet = selectStatement.executeQuery();

                        if (!resultSet.next()) { // 해당 날짜에 정보가 없으면 새로 삽입
                            // 데이터베이스에 학사일정 정보 저장
                            PreparedStatement statement = connection.prepareStatement("INSERT INTO academic_schedule (Year, Month, Date, Schedule) VALUES (?, ?, ?, ?)");
                            statement.setInt(1, year);
                            statement.setInt(2, date.getMonthValue());
                            statement.setInt(3, date.getDayOfMonth());
                            statement.setString(4, schedule);
                            statement.executeUpdate();
                        }
                    } catch (DateTimeParseException | SQLException e) {
                        e.printStackTrace();
                        }
                }
            }

            // 2학기 학사일정 출력 및 콘솔에 표시
            for (WebElement element : elements2) {
                String text = element.getText();
                String[] parts = text.split(":");
                String schedule = parts[1].trim();

                if (parts[0].contains("~")) {
                    String[] dateRange = parts[0].trim().split("~");
                    dateRange[0] = dateRange[0].trim().replace(" ", "").replace("월", "월 ");
                    if (!dateRange[0].trim().endsWith("일")) dateRange[0] += "일";
                    dateRange[1] = dateRange[1].trim().replace(" ", "").replace("월", "월 ");
                    if (!dateRange[1].trim().endsWith("일")) dateRange[1] += "일";
                
                    // Extract the month from the start date if it's missing in the end date
                    if (!dateRange[1].contains("월")) {
                        String month = dateRange[0].split("월")[0];
                        dateRange[1] = month + "월 " + dateRange[1];
                    }
                    
                    try {
                        LocalDate startDate = LocalDate.parse(year + "년 " + dateRange[0], formatter);
                        LocalDate endDate = LocalDate.parse(year + "년 " + dateRange[1], formatter);

                        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                            // 데이터베이스에 이미 해당 정보가 있는지 확인
                            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM academic_schedule WHERE Year = ? AND Month = ? AND Date = ?");
                            selectStatement.setInt(1, year);
                            selectStatement.setInt(2, date.getMonthValue());
                            selectStatement.setInt(3, date.getDayOfMonth());
                            ResultSet resultSet = selectStatement.executeQuery();

                            if (!resultSet.next()) { // 해당 날짜에 정보가 없으면 새로 삽입
                                // 데이터베이스에 학사일정 정보 저장
                                PreparedStatement statement = connection.prepareStatement("INSERT INTO academic_schedule (Year, Month, Date, Schedule) VALUES (?, ?, ?, ?)");
                                statement.setInt(1, year);
                                statement.setInt(2, date.getMonthValue());
                                statement.setInt(3, date.getDayOfMonth());
                                statement.setString(4, schedule);
                                statement.executeUpdate();
                            }
                        }
                    } catch (DateTimeParseException | SQLException e) {
                        e.printStackTrace();
                        }
                } else {
                    String dateStr = parts[0].trim();

                    dateStr = dateStr.replace(" ", "").replace("월", "월 ");
                    if (!dateStr.trim().endsWith("일")) dateStr += "일";

                    try {
                        LocalDate date = LocalDate.parse(year + "년 " + dateStr, formatter);

                        // 데이터베이스에 이미 해당 정보가 있는지 확인
                        PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM academic_schedule WHERE Year = ? AND Month = ? AND Date = ?");
                        selectStatement.setInt(1, year);
                        selectStatement.setInt(2, date.getMonthValue());
                        selectStatement.setInt(3, date.getDayOfMonth());
                        ResultSet resultSet = selectStatement.executeQuery();

                        if (!resultSet.next()) { // 해당 날짜에 정보가 없으면 새로 삽입
                            // 데이터베이스에 학사일정 정보 저장
                            PreparedStatement statement = connection.prepareStatement("INSERT INTO academic_schedule (Year, Month, Date, Schedule) VALUES (?, ?, ?, ?)");
                            statement.setInt(1, year);
                            statement.setInt(2, date.getMonthValue());
                            statement.setInt(3, date.getDayOfMonth());
                            statement.setString(4, schedule);
                            statement.executeUpdate();
                        }
                    } catch (DateTimeParseException | SQLException e) {
                        e.printStackTrace();                  
                        }
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 출력
            e.printStackTrace();
        } finally {
            // WebDriver 리소스 정리
            driver.quit();
        }
    }

    public static void main(String[] args) {
        new WebScraper();
    }
}