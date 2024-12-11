
class Projectile {
    int x, y, size = 10, speed;

    public Projectile(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void move() {
        x += speed;
    }

    public boolean isOutOfBounds() {
        return x < -size || x > 800 + size;
    }
}