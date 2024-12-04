
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore.PrivateKeyEntry;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Character {
    private BufferedImage sprite;
    private int x = 310, y = 350;
    private final int groundY = 350;
    private int velocityY = 0;
    private boolean jumping = false, movingLeft = false, movingRight = false, flip = false;
    static int stateIndex = 0; // 상태 관리 변수
    private State[] states;
    
    public Character() {
    	loadImage(getSelectCharacter());
    }
    
    public int getSelectCharacter () {
    	return Title.getCharacterValue();
    }

    private void loadImage(int selectCharacter) {
    	if(selectCharacter == 1) {
    		try {
    			this.sprite = ImageIO.read(new File("res/soldier.png"));
    			this.sprite = transformColorToTransparency(sprite, new Color(255, 255, 255));
    			soldier();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	else if (selectCharacter == 2) {
    		try {
    			this.sprite = ImageIO.read(new File("res/people.png"));
    			this.sprite = transformColorToTransparency(sprite, new Color(255, 255, 255));
    			shutgunMan();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	else {
    		try {
    			this.sprite = ImageIO.read(new File("res/batman.png"));
    			this.sprite = transformColorToTransparency(sprite, new Color(255, 255, 255));
    			batMan();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }

    private void soldier() {
        states = new State[6];

        State state = new State();
        states[0] = state; // 기본 대기 상태
        state.width = 128;
        state.height = 128;
        state.start_x = 0;
        state.start_y = 640;
        state.frame_size = 6;

        state = new State();
        states[1] = state; // 왼쪽으로 걷기
        state.width = 128;
        state.height = 128;
        state.start_x = 0;
        state.start_y = 384;
        state.frame_size = 8;

        state = new State();
        states[2] = state; // 오른쪽으로 걷기
        state.width = 128;
        state.height = 128;
        state.start_x = 0;
        state.start_y = 384;
        state.frame_size = 8;

        state = new State();
        states[3] = state; // 근접 공격
        state.width = 128;
        state.height = 128;
        state.start_x = 0;
        state.start_y = 0;
        state.frame_size = 3;

        state = new State();
        states[4] = state; // 사격
        state.width = 128;
        state.height = 128;
        state.start_x = 0;
        state.start_y = 512;
        state.frame_size = 4;

        state = new State();
        states[5] = state; // 죽음
        state.width = 128;
        state.height = 128;
        state.start_x = 0;
        state.start_y = 128;
        state.frame_size = 4;
    }

    private void shutgunMan() {
		states = new State[8];
		State state = new State();
		states[0] = state;//아무것도 안할때 숨쉬기 모션
		state.width = 128;//잘라낼 크기 x
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;//어디서부터 잘라내기 시작할지
		state.start_y = 768;
		state.frame_size = 6;//프레임 수
		
		state = new State();
		states[1] = state;//달리기 모션
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 0;
		state.frame_size = 8;
		state.stop = true;
		
		state = new State();
		states[2] = state;//달리기 모션
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 0;
		state.frame_size = 8;
		
		state = new State();
		states[3] = state;//근접공격
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 256;
		state.frame_size = 6;
		
		state = new State();
		states[4] = state;//샷건
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 128;
		state.frame_size = 12;
		
		state = new State();
		states[5] = state;//점프
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 896;
		state.frame_size = 11;
		
		state = new State();
		states[6] = state;//죽음
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 512;
		state.frame_size = 4;
		
		
		state.stop = true;	
	}
  
    private void batMan() {
		states = new State[8];
		State state = new State();
		states[0] = state;//아무것도 안할때 숨쉬기 모션
		state.width = 128;//잘라낼 크기 x
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;//어디서부터 잘라내기 시작할지
		state.start_y = 0;
		state.frame_size = 5;//프레임 수
		
		state = new State();
		states[1] = state;//달리기 모션
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 128;
		state.frame_size = 8;
		state.stop = true;
		
		state = new State();
		states[2] = state;//달리기 모션
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 128;
		state.frame_size = 8;
		
		state = new State();
		states[3] = state;//근접공격1
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 256;
		state.frame_size = 10;
		
		state = new State();
		states[4] = state;//근접공격2
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 384;
		state.frame_size = 4;
		
		state = new State();
		states[5] = state;//점프
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 640;
		state.frame_size = 8;
		
		state = new State();
		states[6] = state;//죽음
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 512;
		state.frame_size = 4;
		
		
		state.stop = true;
    }
    private BufferedImage transformColorToTransparency(BufferedImage image, Color color) {
        final int r1 = color.getRed();
        final int g1 = color.getGreen();
        final int b1 = color.getBlue();

        ImageFilter filter = new RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                int r = (rgb & 0xFF0000) >> 16;
                int g = (rgb & 0xFF00) >> 8;
                int b = (rgb & 0xFF);
                if (r == r1 && g == g1 && b == b1) {
                    return rgb & 0xFFFFFF;
                }
                return rgb;
            }
        };

        ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
        Image img = Toolkit.getDefaultToolkit().createImage(ip);
        BufferedImage dest = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dest.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return dest;
    }
    
    private State getState() {
		return states[stateIndex];
	}

    public void draw(Graphics g, Screen screen) {
		drawCharacter(getState(), g, screen);
	}
	
	private void drawCharacter(State state, Graphics g, Screen screen) {
        int ix = state.width * state.index_x + state.start_x;
        int iy = state.height * state.index_y + state.start_y;

        // Graphics2D로 변환하여 반전 처리
        Graphics2D g2d = (Graphics2D) g;

        if (flip) {  // 반전 상태가 true일 경우 (왼쪽 방향)
            g2d.scale(-1, 1);  // x축을 -1로 반전
            g2d.drawImage(sprite, -x - state.width, y,
                    -x, y + state.height,
                    ix, iy,
                    ix + state.width, iy + state.height, screen);
            g2d.scale(-1, 1);  // 원래 상태로 복구
        } else {  // 반전 상태가 false일 경우 (오른쪽 방향)
            g2d.drawImage(sprite, x, y,
                    x + state.width, y + state.height,
                    ix, iy,
                    ix + state.width,
                    iy + state.height, screen);
        }

	    
	    if (screen.getCount() % 100 == 0) {
	        if (state.index_x < state.frame_size - 1) {
	            state.index_x++;
	        } else {
	            if (!state.stop)
	                state.index_x = 0;
	            else
	                state.index_x = state.frame_size - 1;
	        }
	    }
	}

	public void update() {
        if (jumping) {
            y += velocityY;
            velocityY += 1;
            if (y >= groundY) {
                y = groundY;
                jumping = false;
            }
        }
        if (movingLeft) x = Math.max(-45 , x - 5);
        if (movingRight) x = Math.min(x + 5, 800 - 105);
    }
    
    public void actionPressed(int keyCode) {
    	switch (keyCode) {
    	case KeyEvent.VK_A:
    		stateIndex = 3; // 근접 공격
    		break;
    	case KeyEvent.VK_S:
    		stateIndex = 4; // 사격
    		break;
    	case KeyEvent.VK_D:
    		stateIndex = 5; // 죽음
    		break;
    	case KeyEvent.VK_LEFT:
    		movingLeft = true;
    		flip = true;
    		stateIndex = 1;
    		break;
    	case KeyEvent.VK_RIGHT:
    		movingRight = true;
    		flip = false;
    		stateIndex = 2;
    		break;
    	case KeyEvent.VK_UP:
    		if (!jumping) {
    			jumping = true;
    			velocityY = -15;
    		}
    		break;
    	}
    }
    
    public void actionReleased(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                movingLeft = false;
                stateIndex = 0;
                break;
            case KeyEvent.VK_RIGHT:
                movingRight = false;
                stateIndex = 0;
                break;
            case KeyEvent.VK_A:
                stateIndex = 0;
                break;
            case KeyEvent.VK_S:
                stateIndex = 0;
                break;
            case KeyEvent.VK_D:
                break;
        }
    }
}

