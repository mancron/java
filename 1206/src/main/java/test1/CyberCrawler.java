package test1;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.ArrayList;
import java.util.List;

public class CyberCrawler {
    private String studentNumber;
    private String password;
    private ChromeDriver driver;
    private List<String> lectureInfoList;  // 크롤링한 강의 정보를 담을 리스트

    public CyberCrawler(String studentNumber, String password) {
        this.studentNumber = studentNumber;
        this.password = password;
        lectureInfoList = new ArrayList<>();
    }

    public void loginAndAccessClassroom() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");
        driver = new ChromeDriver(options);

        driver.navigate().to("https://door.deu.ac.kr/sso/login.aspx");
        sleep(500);

        // WebDriver에서 JavaScriptExecutor로 변환(명시적 형변환)
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

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

            List<WebElement> attendanceIm = driver.findElements(By.xpath("//img[@src='/Content/images/common/icon_LecRoom02_03.gif']"));
            List<WebElement> absentIm = driver.findElements(By.xpath("//img[@src='/Content/images/common/icon_LecRoom02_01.gif']"));
            List<WebElement> Non_attendanceIm = driver.findElements(By.xpath("//img[@src='/Content/images/common/BT_LecRoom01_05.gif']"));
            List<WebElement> Before_completionIm = driver.findElements(By.xpath("//img[@src='/Content/images/common/icon_LecRoom02_02.gif']"));

            if((attendanceIm.size()+absentIm.size()+ Non_attendanceIm.size()+Before_completionIm.size())> 0)
            {
                System.out.println(lectureName + "의 출석: "+ attendanceIm.size());            //출석
                System.out.println(lectureName + "의 결석: "+ absentIm.size());                //결석
                System.out.println(lectureName + "의 미수강: "+ Non_attendanceIm.size());       //미수강
                System.out.println(lectureName + "의 완료전: "+ Before_completionIm.size()); //완료전
                
                String Cyber_attendance= Integer.toString(attendanceIm.size());
                String Cyber_absent= Integer.toString(absentIm.size());
                String Cyber_Non_attendance= Integer.toString(Non_attendanceIm.size());
                String Cyber_Before_completion= Integer.toString(Before_completionIm.size());
                String Cyber_assignmentName = lectureName;
                
                Cyber_info.saveInfo(Cyber_attendance, Cyber_absent, Cyber_Non_attendance, Cyber_Before_completion, Cyber_assignmentName);
            }
            
            // 뒤로 가기를 실행
            jsExecutor.executeScript("window.history.go(-1);");
        } 
        driver.quit();
    }
    
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printLectureInfo() {
        System.out.println("===== 크롤링한 강의 정보 =====");
        for (String info : lectureInfoList) {
            System.out.println(info);
        }
    }

    public static void main(String[] args) {
        CyberCrawler crawler = new CyberCrawler("20222943", "@hs030101");
        crawler.loginAndAccessClassroom();
        crawler.printLectureInfo();
    }
}