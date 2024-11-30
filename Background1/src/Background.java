import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Background extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Clip bgmClip;
    private float alpha = 0.0f; // 투명도 값 (0.0 = 완전 투명, 1.0 = 완전 불투명)
    private Timer fadeTimer;

    public Background() {
        setTitle("Zombie Survival Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // CardLayout으로 스테이지 전환 관리
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Stage 1 설정
        JPanel stage1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 배경 이미지를 설정
                ImageIcon background = new ImageIcon("res/bg1.jpg");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        stage1.setLayout(null);

        JButton nextStageButton = new JButton("Next Stage");
        nextStageButton.setBounds(350, 500, 100, 50);
        // 버튼을 눌렀을 때 애니메이션 효과를 통해 다음 스테이지로 이동
        nextStageButton.addActionListener(e -> startFadeTransition("stage2"));
        stage1.add(nextStageButton);

        mainPanel.add(stage1, "stage1");

        // Stage 2 설정
        JPanel stage2 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 2번째 배경 이미지 설정
                ImageIcon background = new ImageIcon("res/bg2.jpg");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        stage2.setLayout(null);

        JButton backButton = new JButton("Back to Stage 1");
        backButton.setBounds(350, 500, 150, 50);
        // 버튼을 눌렀을 때 애니메이션 효과를 통해 이전 스테이지로 이동
        backButton.addActionListener(e -> startFadeTransition("stage1"));
        stage2.add(backButton);

        mainPanel.add(stage2, "stage2");

        add(mainPanel);

        // 초기 배경 음악 재생
        playBGM("res/bgm1.mp3");
    }

    /**
     * 스테이지 전환 애니메이션을 시작하는 메서드.
     * @param targetStage 전환하려는 스테이지의 이름
     */
    private void startFadeTransition(String targetStage) {
        alpha = 0.0f; // 투명도를 초기화
        fadeTimer = new Timer(30, new ActionListener() { // 30ms 간격으로 호출
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f; // 투명도를 점진적으로 증가
                if (alpha >= 1.0f) { // 투명도가 완전히 불투명해지면
                    alpha = 1.0f;
                    fadeTimer.stop(); // 애니메이션 종료
                    changeStage(targetStage); // 스테이지 전환
                    startFadeIn(); // 다음 스테이지에서 페이드 인 애니메이션 시작
                }
                repaint(); // 화면 갱신
            }
        });
        fadeTimer.start();
    }

    /**
     * 페이드 인 애니메이션을 시작하는 메서드.
     */
    private void startFadeIn() {
        alpha = 1.0f; // 투명도를 완전 불투명으로 초기화
        fadeTimer = new Timer(30, new ActionListener() { // 30ms 간격으로 호출
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.05f; // 투명도를 점진적으로 감소
                if (alpha <= 0.0f) { // 투명도가 완전히 투명해지면
                    alpha = 0.0f;
                    fadeTimer.stop(); // 애니메이션 종료
                }
                repaint(); // 화면 갱신
            }
        });
        fadeTimer.start();
    }

    /**
     * 스테이지를 전환하는 메서드.
     * @param stageName 전환하려는 스테이지 이름
     */
    private void changeStage(String stageName) {
        cardLayout.show(mainPanel, stageName);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (alpha > 0.0f) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(Color.BLACK); // 전환 시 화면을 검게 덮음
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * 배경 음악을 재생하는 메서드.
     * @param bgmPath 재생할 BGM 파일 경로
     */
    private void playBGM(String bgmPath) {
        try {
            File audioFile = new File(bgmPath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 배경 음악을 중지하는 메서드.
     */
    private void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Background game = new Background();
            game.setVisible(true);
        });
    }
}
