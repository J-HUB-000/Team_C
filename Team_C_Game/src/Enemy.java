import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class Enemy {
    int x = 310, y = 350;
    int size, health = 2;
    private BufferedImage sprite;
    private State[] states;
    private int stateIndex = 0;
    boolean facingLeft = false;
    
    public Enemy(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        loadImage();
    }
    
    private void loadImage() {
        try {
            sprite = ImageIO.read(new File("res/zombie1.png"));
            sprite = transformColorToTransparency(sprite, new Color(255, 255, 255));
            zombie_1();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void Flip(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }
    
    private BufferedImage transformColorToTransparency(BufferedImage image, Color color) {
        final int r1 = color.getRed();
        final int g1 = color.getGreen();
        final int b1 = color.getBlue();

        ImageFilter filter = new RGBImageFilter() {
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
    	drawZombie(getState(), g, screen);
	}
    
    public void drawZombie(State state, Graphics g, Screen screen) {
    	int ix = state.width * state.index_x + state.start_x;
        int iy = state.height * state.index_y + state.start_y;

        // Graphics2D로 변환하여 반전 처리
        Graphics2D g2d = (Graphics2D) g;

        if (facingLeft) {  // 반전 상태가 true일 경우 (왼쪽 방향)
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
    
    public void updateAnimation() {
    	this.stateIndex = 0;
    }
    
    private void zombie_1() {
    	states = new State[4];
		State state = new State();
		states[0] = state;//왼쪽으로 가기 
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 480;
		state.frame_size = 8;
		
		state = new State();
		states[1] = state;//오른쪽으로 가기
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 480;
		state.frame_size = 8;
		
		state = new State();
		states[2] = state;//아래로 후려치기
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 96;
		state.frame_size = 4;
		
		state = new State();
		states[3] = state;//죽음
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 288;
		state.frame_size = 5;
		
		state.stop = true;
    }
    private void zombie_2() {
    	states = new State[8];
		State state = new State();
		states[0] = state;//아무것도 안할때 숨쉬기 모션
		state.width = 96;//잘라낼 크기 x
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;//어디서부터 잘라내기 시작할지
		state.start_y = 384;
		state.frame_size = 8;//프레임 수
		
		state = new State();
		states[1] = state;//달리기 모션
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 480;
		state.frame_size = 7;
		
		state = new State();
		states[2] = state;//달리기 모션
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 480;
		state.frame_size = 7;
		
		state = new State();
		states[3] = state;//아래로 후려치기
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 0;
		state.frame_size = 5;
		
		state = new State();
		states[4] = state;//위로 후려치기
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 192;
		state.frame_size = 5;
		
		state = new State();
		states[5] = state;//죽음
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 288;
		state.frame_size = 5;
		
		
		state.stop = true;	
    }
    private void zombie_3() {
    	states = new State[6];
		State state = new State();
		states[0] = state;//아무것도 안할때 숨쉬기 모션
		state.width = 96;//잘라낼 크기 x
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;//어디서부터 잘라내기 시작할지
		state.start_y = 384;
		state.frame_size = 5;//프레임 수
		
		state = new State();
		states[1] = state;//달리기 모션
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 480;
		state.frame_size = 7;
		state.stop = true;
		
		state = new State();
		states[2] = state;//달리기 모션
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 480;
		state.frame_size = 7;
		
		state = new State();
		states[3] = state;//아래로 후려치기
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 96;
		state.frame_size = 4;
		
		state = new State();
		states[4] = state;//위로 후려치기
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 0;
		state.frame_size = 4;
		
		state = new State();
		states[5] = state;//죽음
		state.width = 96;
		state.height = 96;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 288;
		state.frame_size = 5;
		
		
		state.stop = true;	
    }
}