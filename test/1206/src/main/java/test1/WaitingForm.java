


package test1;

import javax.swing.*;
import java.awt.*;

public class WaitingForm {
    private JFrame frame;

    public void dispose() {
        frame.dispose();
    }

    public WaitingForm() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        JLabel waitingLabel = new JLabel("잠시 기다려주세요...", SwingConstants.CENTER);
        frame.add(waitingLabel, BorderLayout.CENTER);

        // 화면 중앙에 위치시키기
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

        frame.setVisible(true);
    }

    public static void main(String[] args) {

    }
}
