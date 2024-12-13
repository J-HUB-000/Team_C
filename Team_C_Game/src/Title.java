
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

class Title extends JPanel {
    private final JFrame parentFrame;
    private JButton playButton;
    private String selectedCharacter;
    private Color characterColor; // 캐릭터 색상 저장 
    private Screen screen;
    private Image bgImage;
    private Character character = new Character();
    private static int characterValue;

    public Title(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(null);
        
        // 배경 이미지 로드
        loadBackgroundImage();

        // "Choose Your Character" 라벨 추가
        JLabel chooseLabel = new JLabel("Choose Your Character", JLabel.CENTER);
        chooseLabel.setFont(new Font("Arial", Font.BOLD, 35));
        chooseLabel.setBounds(200, 350, 400, 50); // 위치와 크기 설정
        chooseLabel.setForeground(new Color(255, 255, 255));
        add(chooseLabel);
        
        JLabel titleLabel = new JLabel("Zombie Survival", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 56));
        titleLabel.setBounds(200, 100, 600, 100); // 위치와 크기 설정
        titleLabel.setForeground(new Color(255, 255, 255));
        add(titleLabel);

        // 플레이 버튼 생성 및 위치 설정
        playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 40));
        playButton.setBounds(300, 250, 200, 80); // 위치와 크기 설정
        playButton.setVisible(false);
        playButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            Screen screen = new Screen(parentFrame, characterColor, 1); // 스테이지를 1로 초기화
            parentFrame.add(screen);
            parentFrame.revalidate();
            parentFrame.repaint();
            SwingUtilities.invokeLater(screen::requestFocusInWindow);
        });
        add(playButton);

        // 캐릭터 선택 버튼 생성 및 위치 설정
        createCharacterButtons();
    }
    

    private void createCharacterButtons() {
        Color[] characterColors = {Color.GREEN, Color.BLUE, Color.ORANGE};
        int[] buttonXPositions = {150, 350, 550}; // 버튼 위치 설정
        String[] characterImagePaths = {
                "res/character1(soldier).png",  // 캐릭터 1 이미지 경로
                "res/character2(shotgun).png",  // 캐릭터 2 이미지 경로
                "res/character3(batman).png"   // 캐릭터 3 이미지 경로
            };

        for (int i = 0; i < 3; i++) {
            Color color = characterColors[i];
            String imagePath = characterImagePaths[i];

            //버튼 생성
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(150, 150));
            button.setBackground(color);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            button.setBounds(buttonXPositions[i], 410, 120, 120); // 위치와 크기 설정
            
            //이미지 생성
            ImageIcon icon = new ImageIcon(imagePath);
            button.setIcon(icon);
            
            // 아이콘 가운데 정렬
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setVerticalAlignment(SwingConstants.CENTER);

            button.addActionListener(e -> {
                characterColor = color; // 선택한 캐릭터의 색상 저장
                setCharacterValue(characterColor);
                playButton.setVisible(true);
            });

            add(button);
        }
    }
    
    private void loadBackgroundImage() {
        try {
            ImageIcon icon = new ImageIcon("res/bg4.png");
            bgImage = icon.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 배경 이미지 그리기
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
    private void setCharacterValue(Color color) {
		if (color.equals(Color.GREEN)) {
            characterValue = 1;
        } else if (color.equals(Color.BLUE)) {
            characterValue = 2;
        } else if (color.equals(Color.ORANGE)) {
            characterValue = 3;
        }
    }

    public static int getCharacterValue() {
        return characterValue;
    }
   
}
