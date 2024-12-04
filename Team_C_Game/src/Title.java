
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

class Title extends JPanel {
    private final JFrame parentFrame;
    private JButton playButton;
    private String selectedCharacter;
    private Color characterColor; // 캐릭터 색상 저장
    private Screen screen;
    private static int characterValue;

    public Title(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Choose Your Character", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel characterPanel = createCharacterPanel();
        add(characterPanel, BorderLayout.CENTER);

        playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.BOLD, 20));
        playButton.setVisible(false);
        playButton.addActionListener(e -> {
            removeAll();
            screen = new Screen(parentFrame, characterColor);
            add(screen);
            revalidate();
            repaint();
            screen.requestFocusInWindow();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(playButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    

    private JPanel createCharacterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        String[] characterNames = {"캐릭터1", "캐릭터2", "캐릭터3"};
        Color[] characterColors = {Color.GREEN, Color.BLUE, Color.YELLOW};

        for (int i = 0; i < characterNames.length; i++) {
            String characterName = characterNames[i];
            Color color = characterColors[i];
            
            JButton button = new JButton(characterName);
            button.setPreferredSize(new Dimension(150, 150));
            button.setBackground(color);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            button.addActionListener(e -> {
                selectedCharacter = characterName;
                characterColor = color; // 선택한 캐릭터의 색상 저장
                setCharacterValue(characterColor);
                playButton.setVisible(true);
            });

            panel.add(button);
        }
        return panel;
    }
    
    private void setCharacterValue(Color color) {
		if (color.equals(Color.GREEN)) {
            characterValue = 1;
        } else if (color.equals(Color.BLUE)) {
            characterValue = 2;
        } else if (color.equals(Color.YELLOW)) {
            characterValue = 3;
        }
    }

    public static int getCharacterValue() {
        return characterValue;
    }
   
}
