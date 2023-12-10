package test1;

import javax.swing.*;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OpenDoor extends JFrame{
    // 인스턴스 변수
    private static String tid;
    private static String tpw;

    // 생성자
    public OpenDoor(String id, String pw) {
        tid = id;
        tpw = pw;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void executeLogin() {
        // 크롬 드라이버의 경로를 설정
        System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");

        // 크롬 드라이버를 초기화
        WebDriver Odriver = new ChromeDriver();

        // 웹 사이트에 접속
        Odriver.get("https://door.deu.ac.kr/sso/login.aspx");
        Odriver.manage().window().maximize();

        // ID 입력 필드 찾기
        WebElement idElement = Odriver.findElement(By.name("userid"));
        // Password 입력 필드 찾기
        WebElement pwElement = Odriver.findElement(By.name("password"));
        // 로그인 버튼 찾기
        WebElement loginButton = Odriver.findElement(By.xpath("/html/body/form/div[2]/div[1]/div/table/tbody/tr[1]/td[3]/a"));

        // ID, Password 입력
        idElement.sendKeys(tid);
        pwElement.sendKeys(new String(tpw));

        loginButton.click();
        WebDriverWait wait = new WebDriverWait(Odriver, 10);

        try {
            wait.until(ExpectedConditions.urlToBe("http://door.deu.ac.kr/Home/Index"));
            System.out.println("웹 사이트 로그인 성공!");
            // 창이 열려 있는지 확인
            while(true) {
            	if (!isWindowOpen(Odriver, Odriver.getWindowHandle())) {
                    System.out.println("웹 닫힘");
                    break;
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	//Odriver.quit();
            dispose();
            new HomeForm();
        }
    }	
    private boolean isWindowOpen(WebDriver driver, String windowHandle) {
        Set<String> windowHandles = driver.getWindowHandles();
        return windowHandles.contains(windowHandle);
    } //원도우 창이 닫힌 경우, false 반환
}