package test1;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    private String studentNumber; // 학번
    private String password; // 비번
    public static ArrayList<String> assList = new ArrayList<String>(); // 과제 정보를 받는 리스트
    public static ArrayList<String> MooCList = new ArrayList<String>(); // mooc 정보를 받는 리스트
  
    // 과제 리스트 반환
    public static ArrayList<String> assgetList() {
       return assList;
    }
    // mooc 리스트 반환
    public static ArrayList<String> MoocgetList() {
       return MooCList;
    }
    
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Main() {}
    
    public Main(String studentNumber, String password) {
        this.studentNumber = studentNumber;
        this.password = password;
    }

    // 과제 크롤링 메소드
    public void loginAndAccessClassroom() {
        // 크롬 접근 제어가 가능한 드라이버 다운로드 위치
        System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");
        
        // ChromeOptions 객체를 생성
        ChromeOptions options = new ChromeOptions();
        // headless 모드를 활성화
        options.addArguments("headless");

        // ChromeDriver를 생성할 때 ChromeOptions를 전달
        WebDriver driver = new ChromeDriver(options);

        // WebDriver에서 JavaScriptExecutor로 변환(명시적 형변환)
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        // 처음 접속할 웹 페이지 주소
        driver.get("https://door.deu.ac.kr/sso/login.aspx");

        // 들어갈 때 조금의 로딩시간이 있기에 기다리기
        sleep(500);

        // 아이디 창에 미리 입력해둔 아이디 기입하기
        WebElement studentNumberElement = driver.findElement(By.name("userid"));
        studentNumberElement.clear();
        studentNumberElement.sendKeys(studentNumber);

        // 비밀번호 창에 미리 입력해둔 비밀번호 기입하기
        WebElement passwordElement = driver.findElement(By.name("password"));
        passwordElement.clear();
        passwordElement.sendKeys(password);

        // 로그인 글씨 입력하기
        WebElement loginButton = driver.findElement(By.xpath("//a[contains(text(), '로그인')]"));
       loginButton.click();

        // 강의실에 접근
        WebElement link = driver.findElement(By.cssSelector("a[href='/MyPage']"));
        // JavaScript를 이용하여 '강의실' 링크 클릭
        jsExecutor.executeScript("arguments[0].click();", link);

        // 정규과정 강의목록 tbody의 개수를 리스트에 담고 tbody의 입장만 클릭
        List<WebElement> AssignmententerButtons = driver.findElements(By.xpath("//*[@id=\"wrap\"]/div[2]/div[3]/div[3]/table/tbody/tr"));
        int AssignmentnumOfButtons = AssignmententerButtons.size(); // 입장의 개수를 구하는 실제 함수

        for (int i = 0; i < AssignmentnumOfButtons-1; i++) {
            // 페이지가 로드된 후에 각 '입장' 이미지 찾기
            AssignmententerButtons = driver.findElements(By.xpath("//img[@alt='입장']"));
            
            // JavaScript를 이용하여 '입장' 이미지 클릭
            jsExecutor.executeScript("arguments[0].click();", AssignmententerButtons.get(i));
            
            // 페이지가 바뀌기 때문에 이때도 쉬어준다.
            sleep(500);
            
            WebElement currentMenuTitleElement = driver.findElement(By.xpath("//*[@id='CurrentMenuTitle']")); // 강의명 추출
            String lectureName = currentMenuTitleElement.getText();

            // 각 강의의 '과제' 클릭 => id가 따로 없기때문에 xpath를 이용하여 그대로 가져오기.
            WebElement element = driver.findElement(By.xpath("//span[contains(@class, 'font_13_333') and contains(text(), '과제')]"));
            //'과제' 클릭
            jsExecutor.executeScript("arguments[0].click();", element);
            
            //개인과제의 개수를 확인
            List<WebElement> assignment = driver.findElements(By.xpath("//td[contains(text(), '개인과제')]"));
            int numOfAssignment = assignment.size(); // 개인과제의 개수를 구하는 실제 함수

            if(numOfAssignment > 0) { // 개인과제를 돌면서 과제명 추출하는 반복문
                for (int j = 2; j <numOfAssignment+2;j++){
                    String nameXpath = "//*[@id='sub_content2']/div/table/tbody/tr[" + j +  "]/td[3]/a"; // xpath를 통해 과제명 찾기
                    WebElement assignmentNameElement = driver.findElement(By.xpath(nameXpath));
                    String assignmentName = assignmentNameElement.getText(); // 과제명 추출

                    String dateXpath = "//*[@id='sub_content2']/div/table/tbody/tr[" + j + "]/td[4]"; // xpath를 통해 날짜 찾기
                    WebElement assignmentDateElement = driver.findElement(By.xpath(dateXpath));
                    String assignmentDate = assignmentDateElement.getText(); // 날짜 추출
                    
                    String submitXpath = "//*[@id='sub_content2']/div/table/tbody/tr[" + j + "]/td[5]"; // xpath를 통해 제출 여부 찾기
                    WebElement assignmentSubmitElement = driver.findElement(By.xpath(submitXpath));
                    String assignmentSubmit = assignmentSubmitElement.getText(); // 제출 / 미제출 추출
                    String toDoSubmit = assignmentSubmit; // 제출 여부

                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yy-MM-dd HH:mm"); // 날짜 포맷을 기한날짜만으로 바꾸기
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yy년 M월 dd일");
            
                        String[] dateParts = assignmentDate.split("~"); // ~을 전후로 분리
                        String endDateText = dateParts[1]; // 기한 날짜 까지만 사용
            
                        Date endDate = inputFormat.parse(endDateText); // 기존 기준 기한 날짜의 포맷을 입력

                        String toDoDate = outputFormat.format(endDate); // 간소화된 날짜 포맷으로 변경
                        assList.add(toDoDate + " : " + lectureName + " : " + assignmentName + " / " + toDoSubmit); // 과제의 모든 리스트 더하기
                        info.saveInfo(toDoDate,lectureName,assignmentName,toDoSubmit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            // 페이지가 바뀌기 때문에 이때도 쉬어준다.
            sleep(500);

            // 뒤로 가기를 실행
            jsExecutor.executeScript("window.history.go(-2);");
        } 
        driver.quit();
    }

    // MooC 크롤링
    public void MoocAccessClassroom(){
        // 크롬 접근 제어가 가능한 드라이버 다운로드 위치
        System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");
        
        // ChromeOptions 객체를 생성
        ChromeOptions options = new ChromeOptions();
        // headless 모드를 활성화
        options.addArguments("headless");

        // ChromeDriver를 생성할 때 ChromeOptions를 전달
        WebDriver driver = new ChromeDriver(options);

        // WebDriver에서 JavaScriptExecutor로 변환(명시적 형변환)
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        // 처음 접속할 웹 페이지 주소
        driver.get("https://door.deu.ac.kr/sso/login.aspx");

        // 들어갈 때 조금의 로딩시간이 있기에 기다리기
        sleep(500);

        // 아이디 창에 미리 입력해둔 아이디 기입하기
        WebElement studentNumberElement = driver.findElement(By.name("userid"));
        studentNumberElement.clear();
        studentNumberElement.sendKeys(studentNumber);

        // 비밀번호 창에 미리 입력해둔 비밀번호 기입하기
        WebElement passwordElement = driver.findElement(By.name("password"));
        passwordElement.clear();
        passwordElement.sendKeys(password);

        // 로그인 글씨 입력하기
        WebElement loginButton = driver.findElement(By.xpath("//a[contains(text(), '로그인')]"));
        loginButton.click();

        // 강의실에 접근
        WebElement link = driver.findElement(By.cssSelector("a[href='/MyPage']"));
        // JavaScript를 이용하여 '강의실' 링크 클릭
        jsExecutor.executeScript("arguments[0].click();", link);
        
        // 일반교육과정 강의목록 tbody의 개수를 리스트에 담고 tbody의 입장만 클릭
        List<WebElement> MooCButtonsSize = driver.findElements(By.xpath("//*[@id=\"wrap\"]/div[2]/div[3]/div[6]/table/tbody/tr"));
        int MooCnumOfButtons = MooCButtonsSize.size(); // 입장의 개수를 구하는 실제 함수

        for (int i = 0; i < MooCnumOfButtons-1; i++) {
            String MenuTitleElement = "//*[@id='wrap']/div[2]/div[3]/div[6]/table/tbody/tr["+(i+2)+"]/td[2]/a";
            WebElement currentMenuTitleElement = driver.findElement(By.xpath(MenuTitleElement)); // 강의명 추출
            String lectureName = currentMenuTitleElement.getText();
            
            List<WebElement> MooCButtons = driver.findElements(By.xpath("//*[@id='wrap']/div[2]/div[3]/div[6]/table/tbody/tr["+ (i+1) +"]"));

            String MooCButtonXpath = "//*[@id='wrap']/div[2]/div[3]/div[6]/table/tbody/tr[" + (i+2) + "]/td[4]/a";

            // 페이지가 로드된 후에 각 '입장' 이미지 찾기
            MooCButtons = driver.findElements(By.xpath(MooCButtonXpath));
            // JavaScript를 이용하여 '입장' 이미지 클릭
            jsExecutor.executeScript("arguments[0].click();", MooCButtons.get(0));
            
            // 페이지가 바뀌기 때문에 이때도 쉬어준다.
            sleep(500);
            
            // 출석해야하는 영상 개수 확인
            List<WebElement> MooCattendance = driver.findElements(By.xpath("//*[@id=\"gvListTB\"]/tbody/tr"));
            int numOfattendance = MooCattendance.size(); // 출석 개수를 구하는 실제 함수

            int namenumber = 3;
            int datenumber = 6;

            for (int j = 1; j <numOfattendance+1;j++){
                // 주차 차시에 따른 tbody/tr/td 를 확인하기 위한 tbody 배열 
                String countXpath = "//*[@id='gvListTB']/tbody/tr[" + j + "]/td";
                List<WebElement> tbodyTdCount = driver.findElements(By.xpath(countXpath));
                int numOfCount = tbodyTdCount.size();
                
                String MooCName;
                String dateXpath; WebElement MooCDateElement; String MooCDate;
                String altXpath; WebElement MooCaltElement; String altText;

                // n주차 1차시 테이블
                if(numOfCount == 10){
                    String nameXpath = "//*[@id='gvListTB']/tbody/tr[" + j + "]/td[" + namenumber + "]/a"; // xpath를 통해 강의주제 찾기
                    WebElement MooCNameElement = driver.findElement(By.xpath(nameXpath));
                    MooCName = MooCNameElement.getText(); // 강의주제 추출

                    dateXpath = "//*[@id='gvListTB']/tbody/tr[" + j + "]/td[" + datenumber + "]"; // xpath를 통해 날짜 찾기
                    MooCDateElement = driver.findElement(By.xpath(dateXpath));
                    MooCDate = MooCDateElement.getText(); // 수업기간 추출

                    altXpath = "//*[@id='gvListTB']/tbody/tr[" + j + "]/td[8]/a/img"; // 이미지의 alt 속성 값 가져오기
                    MooCaltElement = driver.findElement(By.xpath(altXpath));     // 이미지의 alt 속성 값으로 출석, 미수강, 결석 확인하기
                    altText = MooCaltElement.getAttribute("alt");                  
                }
                else{   //n주차 2,3차시 테이블
                    String nameXpath = "//*[@id='gvListTB']/tbody/tr[" + j + "]/td[" + (namenumber-1) + "]/a"; // xpath를 통해 강의주제 찾기
                    WebElement MooCNameElement = driver.findElement(By.xpath(nameXpath));
                    MooCName = MooCNameElement.getText(); // 강의주제 추출
                        
                    dateXpath = "//*[@id='gvListTB']/tbody/tr[" + j + "]/td[" + (datenumber-1) + "]"; // xpath를 통해 날짜 찾기
                    MooCDateElement = driver.findElement(By.xpath(dateXpath));
                    MooCDate = MooCDateElement.getText(); // 수업기간 추출

                    altXpath = "//*[@id='gvListTB']/tbody/tr[" + j + "]/td[7]/a/img"; // 이미지의 alt 속성 값 가져오기
                    MooCaltElement = driver.findElement(By.xpath(altXpath));     // 이미지의 alt 속성 값으로 출석, 미수강, 결석 확인하기
                    altText = MooCaltElement.getAttribute("alt");
                }
                
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd"); // 날짜 포맷을 기한날짜만으로 바꾸기
                    SimpleDateFormat outputFormat = new SimpleDateFormat("M월 dd일");
        
                    String[] dateParts = MooCDate.split(" ~ "); // ~을 전후로 분리
                    String startDateText = dateParts[0];// 날짜 비교를 위한 시작 날짜 확인
                    String endDateText = dateParts[1]; // 기한 날짜 까지만 사용

                    Date startDate = inputFormat.parse(startDateText);
                    Date endDate = inputFormat.parse(endDateText); // 기존 기준 기한 날짜의 포맷을 입력
                    String toDoDate = outputFormat.format(endDate); // 간소화된 날짜 포맷으로 변경
                    try{
                        int comparison = startDate.compareTo(endDate);
                        if(comparison<0){
                            MooCList.add("23년 "+toDoDate + " : " + lectureName + " : " + MooCName + " / " + altText); // Mooc 영상명 및 출결 상태 더하기
                            Mooc_info.saveInfo(toDoDate,lectureName,MooCName,altText);
                        }
                        else{
                            MooCList.add("24년 "+toDoDate + " : " + lectureName + " : " + MooCName + " / " + altText); // Mooc 영상명 및 출결 상태 더하기
                            Mooc_info.saveInfo(toDoDate,lectureName,MooCName,altText);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }               
                    //info.saveInfo(toDoDate,lectureName,MooCName,altText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // 뒤로 가기를 실행
            jsExecutor.executeScript("window.history.go(-1);");
            jsExecutor.executeScript("window.history.go();");
            // 페이지가 바뀌기 때문에 이때도 쉬어준다.
            sleep(500);
        }
        driver.quit();
    }
    public static void main(String[] args) {
       new LoginForm();
    }
}