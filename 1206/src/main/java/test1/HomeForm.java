package test1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
//import java.sql.SQLException;



public class HomeForm extends JFrame{
    private static final String URL = "jdbc:mysql://localhost:3306/door";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "0000";
	private static String ID, Pass;
	public void setID() {
		ID = LoginForm.getID();
	}
	public void setPass() {
        Pass = LoginForm.getPass();
    }
    
    // MySQL에 데이터가 있는지 확인하는 메소드
    private boolean hasTaskData(String loggedInID) {
    boolean hasData = false;

    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
        String query = "SELECT COUNT(*) as count FROM task WHERE ID = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, loggedInID);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int count = resultSet.getInt("count");
            hasData = count > 0;
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return hasData;
}

    public HomeForm() {
        super("Door+"); //?
        setResizable(false); //창 크기 변경 불가
        setLayout(new GridBagLayout()); // 레이아웃을 GridBagLayout으로 변경
        GridBagConstraints gbc = new GridBagConstraints();

        /*JLabel nameLabel = new JLabel("반갑습니다 [...]님"); //사용자 이름
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5,270,5,0);
        gbc.anchor = GridBagConstraints.NORTH;
        add(nameLabel, gbc);*/
        
        ImageIcon icon = new ImageIcon("src/images/door.jpg"); //동의대 사진
        Image img = icon.getImage();
        JLabel imageLabel = new JLabel(new ImageIcon(img.getScaledInstance(800, 200, Image.SCALE_SMOOTH)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10,0,0,0); // 상단 여백을 줄임
        gbc.anchor = GridBagConstraints.NORTH; // 위치를 상단으로 변경
        add(imageLabel, gbc);

        ImageIcon work = new ImageIcon("src/images/work.png"); 			//버튼1 과제
        ImageIcon cyber = new ImageIcon("src/images/online.png"); 		//버튼2 사이버강의
        ImageIcon mooc = new ImageIcon("src/images/mooc.png"); 			//버튼3 Mooc
        ImageIcon calender = new ImageIcon("src/images/calender.png"); 	//버튼4 달력
        ImageIcon todo = new ImageIcon("src/images/todo.png"); 			//버튼5 할일
        ImageIcon goDoor = new ImageIcon("src/images/doorIC.png");		//버튼6 Door 입장
        
        Color doorBlue = new Color(0x006ACA);
        

        // 과제 버튼 이벤트 처리
        JButton b1 = new JButton(work);
        b1.setBackground(doorBlue);
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setID();
                setPass();

                // MySQL에 데이터가 있는지 확인
                if (hasTaskData(ID)) {
                    new TaskForm(); // 데이터가 있으면 TaskForm 호출
                    HomeForm.this.dispose();
                } else {
                    // 데이터가 없으면 Main.java와 해당 메소드들 호출
                    Main a = new Main(ID, Pass);
                    WaitingForm waitingForm = new WaitingForm();

                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            a.loginAndAccessClassroom();
                            return null;
                        }

                        @Override
                        protected void done() {
                            waitingForm.dispose();
                            new TaskForm();
                            HomeForm.this.dispose();
                        }
                    };
                    worker.execute();
                }
            }
        });
        gbc.gridx = 0; 
        gbc.gridy = 1;
        gbc.gridwidth = 1; 
        gbc.insets = new Insets(60,100,0,-100);
        add(b1, gbc);

        
        //사이버강의
        JButton b2 = new JButton(cyber);
        b2.setBackground(doorBlue);
        b2.addActionListener(new ActionListener() { // ActionListener 추가
            @Override
            public void actionPerformed(ActionEvent e) {
                setID();
                setPass();

                // MySQL에 데이터가 있는지 확인
                if (hasTaskData(ID)) {
                    new CyberForm(); // 데이터가 있으면 TaskForm 호출
                    HomeForm.this.dispose();
                } else {
                    // 데이터가 없으면 Main.java와 해당 메소드들 호출
                    CyberCrawler a = new CyberCrawler(ID, Pass);
                    WaitingForm waitingForm = new WaitingForm();

                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            a.loginAndAccessClassroom();
                            return null;
                        }

                        @Override
                        protected void done() {
                            waitingForm.dispose();
                            new CyberForm();
                            HomeForm.this.dispose();
                        }
                    };
                    worker.execute();
                }
             }
         });
        gbc.gridx = 1; 
        gbc.insets = new Insets(60,-250,0,-100);
        add(b2, gbc);
        
        
        //mooc
        JButton b3 = new JButton(mooc);
        b3.setBackground(doorBlue);
        b3.addActionListener(new ActionListener() { // ActionListener 추가
            @Override
            public void actionPerformed(ActionEvent e) {
                setID();
                setPass();

                 // MySQL에 데이터가 있는지 확인
                 if (hasTaskData(ID)) {
                     new MOOCForm(); // 데이터가 있으면 TaskForm 호출
                     HomeForm.this.dispose();
                 } else {
                     // 데이터가 없으면 Main.java와 해당 메소드들 호출
                     Main a = new Main(ID, Pass);
                     WaitingForm waitingForm = new WaitingForm();

                     SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                         @Override
                         protected Void doInBackground() throws Exception {
                             a.MoocAccessClassroom();
                             return null;
                         }

                         @Override
                         protected void done() {
                             waitingForm.dispose();
                             new MOOCForm();
                             HomeForm.this.dispose();
                         }
                     };
                     worker.execute();
                 }
             }
         });
        gbc.gridx = 2;
        gbc.insets = new Insets(60,-350,0,0);
        add(b3, gbc);
        
        
        //달력
        JButton b4 = new JButton(calender);
        b4.setBackground(doorBlue);
        b4.addActionListener(new ActionListener() { // ActionListener 추가
            @Override
            public void actionPerformed(ActionEvent e) {
            	new ScheduleCalendar();
                HomeForm.this.dispose();
            }
        });
        gbc.gridx = 0; 
        gbc.gridy = 2;
        gbc.insets = new Insets(40,100,100,-100);
        add(b4, gbc);
        
        
        //할일(todo)
        JButton b5 = new JButton(todo);
        b5.setBackground(doorBlue);
        b5.addActionListener(new ActionListener() { // ActionListener 추가
            @Override
            public void actionPerformed(ActionEvent e) {
            	new Todolist();
                HomeForm.this.dispose();
            }
        });
        gbc.gridx = 1;
        gbc.insets = new Insets(40,-250,100,-100);
        add(b5, gbc);

        
        //door 사이트
        JButton b6 = new JButton(goDoor); 
        b6.setBackground(doorBlue);
        b6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setID();
                setPass();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        OpenDoor openDoor = new OpenDoor(ID, Pass);
                        openDoor.executeLogin();
                    }
                });
                HomeForm.this.dispose();
            }
        });
        gbc.gridx = 2;
        gbc.insets = new Insets(40,-350,100,0);
        add(b6, gbc);
        

        setSize(800,600); 
        setLocationRelativeTo(null); // 프레임을 화면 중앙에 위치하도록 설정
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //전체종료1
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeForm()); //전체종료2
    }
}
