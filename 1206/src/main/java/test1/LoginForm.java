package test1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginForm extends JFrame {
    // WebDriver 객체 추가
    private static WebDriver driver;

    public static char[] password;
    public static String ID;
    private static JTextField idField;
    private static JPasswordField passwordField;
    public String storedId;
    public String storedPassword;

    public static String getID() {
        String id = idField.getText();
        return id;
    }

    public static String getPass() {
        if (passwordField != null) {
            return new String(passwordField.getPassword());
        } else {
            System.err.println("passwordField is null in getPass method.");
            return ""; // 또는 null 또는 적절한 기본값으로 변경
        }
    }

    public LoginForm() {
        setTitle("로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 520);
        setResizable(false); //임의로 창 크기 변경 불가

        JPanel panel = new JPanel();
        add(panel);

        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((windowSize.width - frameSize.width) / 2,
                (windowSize.height - frameSize.height) / 2); // 창을 화면 중앙에 위치시킴

        ImageIcon doorimg = new ImageIcon("src/images/doorplus.png");
        JLabel doorLabel = new JLabel(doorimg);
        doorLabel.setSize(doorimg.getIconWidth(), doorimg.getIconHeight());
        doorLabel.setLocation(100, 50);

        JLabel idLabel = new JLabel("아이디 :");
        idLabel.setSize(120, 30);
        idLabel.setLocation(45, 270);
        idField = new JTextField(20);
        idField.setSize(200, 30);
        idField.setLocation(110, 270);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setSize(120, 30);
        passwordLabel.setLocation(45, 315);
        passwordField = new JPasswordField(20);
        passwordField.setSize(200, 30);
        passwordField.setLocation(110, 315);

        Color doorBlue = new Color(0x006ACA);
        JButton loginButton = new JButton("로그인");
        Font buttonFont = loginButton.getFont();
        loginButton.setFont(new Font(buttonFont.getName(), Font.BOLD, 14)); //폰트크기 14pt
        loginButton.setSize(80, 45);
        loginButton.setLocation(157, 370);
        loginButton.setForeground(Color.WHITE); //글자색 흰색
        loginButton.setBackground(doorBlue); //사용자 지정 바탕색

        panel.add(doorLabel);  // doorLabel을 panel에 추가
        panel.add(idLabel);
        panel.add(idField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // 빈 라벨 추가
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // WebDriver 초기화
                System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");

                // 헤드리스 모드에 대한 Chrome 옵션 설정
                ChromeOptions options = new ChromeOptions();
                options.addArguments("headless");

                // 옵션을 사용하여 ChromeDriver 초기화
                driver = new ChromeDriver(options);

                // 로그인 페이지로 이동
                driver.get("https://door.deu.ac.kr/sso/login.aspx");
                String id = idField.getText();
                char[] password = passwordField.getPassword();
                String passwordString = new String(password);

                // 정보 저장
                password = passwordString.toCharArray();
                ID = id;

                // ID 입력 필드 찾기
                WebElement idElement = driver.findElement(By.name("userid"));
                // Password 입력 필드 찾기
                WebElement pwElement = driver.findElement(By.name("password"));
                // 로그인 버튼 찾기
                WebElement loginButton = driver.findElement(By.xpath("/html/body/form/div[2]/div[1]/div/table/tbody/tr[1]/td[3]/a"));

                // ID, Password 입력
                idElement.sendKeys(id);
                pwElement.sendKeys(new String(password));

                // 로그인 버튼 클릭
                loginButton.click();
                WebDriverWait wait = new WebDriverWait(driver, 10);

                try {
                    // 로그인 결과 확인
                    wait.until(ExpectedConditions.urlToBe("http://door.deu.ac.kr/Home/Index"));

                    // ID, Password 저장
                    CheckID.saveID(id, passwordString);

                    System.out.println("로그인 성공! HomeForm 실행");
                    // 홈 화면 표시
                    new HomeForm();
                } catch (Exception ex) {
                    System.out.println("로그인 실패. 아이디 또는 비밀번호가 올바르지 않습니다.");
                    
                    // 실패 시 경고창 표시
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "로그인 실패. 아이디 또는 비밀번호가 올바르지 않습니다.",
                            "로그인 실패", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // WebDriver 종료
                    driver.quit();
                    dispose();
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
