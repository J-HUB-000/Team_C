
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.*;
import java.awt.*;

class Title extends JPanel {
    private final JFrame parentFrame;
    private JButton playButton;
    private String selectedCharacter;
    private Color characterColor;
    private Image bgImage;

    // 생성자
    public Title(JFrame parentFrame, Color characterColor) {
        this.parentFrame = parentFrame;
        this.characterColor = characterColor;
        setLayout(null); // 레이아웃을 null로 설정하여 절대 위치 사용

        // 배경 이미지 로드
        loadBackgroundImage();

        // "Choose Your Character" 라벨 추가
        JLabel titleLabel = new JLabel("Choose Your Character", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(200, 100, 400, 50); // 위치와 크기 설정
        titleLabel.setForeground(new Color(243, 228, 91));
        add(titleLabel);

        // 플레이 버튼 생성 및 위치 설정
        playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 20));
        playButton.setBounds(350, 500, 100, 50); // 위치와 크기 설정
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

    private void loadBackgroundImage() {
        try {
            ImageIcon icon = new ImageIcon("res/bg4.png");
            bgImage = icon.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCharacterButtons() {
        String[] characterNames = {"캐릭터1", "캐릭터2", "캐릭터3"};
        Color[] characterColors = {Color.GREEN, Color.BLUE, Color.YELLOW};
        int[] buttonXPositions = {150, 350, 550}; // 버튼 위치 설정

        for (int i = 0; i < characterNames.length; i++) {
            String characterName = characterNames[i];
            Color color = characterColors[i];

            JButton button = new JButton(characterName);
            button.setPreferredSize(new Dimension(150, 150));
            button.setBackground(color);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            button.setBounds(buttonXPositions[i], 300, 150, 150); // 위치와 크기 설정

            button.addActionListener(e -> {
                selectedCharacter = characterName;
                characterColor = color; // 선택한 캐릭터의 색상 저장
                playButton.setVisible(true);
            });

            add(button);
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
}