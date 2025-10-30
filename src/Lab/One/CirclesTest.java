package Lab.One;

import java.util.Scanner;

enum TYPE {
    POINT,
    CIRCLE
}

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

class ObjectCanNotBeMovedException extends Exception {
    public ObjectCanNotBeMovedException(String message) {
        super(message);
    }
}

class MovableObjectNotFittableException extends Exception {
    public MovableObjectNotFittableException(String message) {
        super(message);
    }
}

interface Movable {
    void moveUp() throws ObjectCanNotBeMovedException;

    void moveDown() throws ObjectCanNotBeMovedException;

    void moveLeft() throws ObjectCanNotBeMovedException;

    void moveRight() throws ObjectCanNotBeMovedException;

    int getCurrentXPosition();

    int getCurrentYPosition();
}

class MovablePoint implements Movable {

    int x, y, xSpeed, ySpeed;

    public MovablePoint(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    private void checkPosition(int newX, int newY) throws ObjectCanNotBeMovedException {
        if (newX < 0 || newX > MovablesCollection.getxMax()
                || newY < 0 || newY > MovablesCollection.getyMax()) {
            throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", newX, newY));
        }
    }


    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        int newY = this.y + ySpeed;
        checkPosition(this.x, newY);
        this.y = newY;

    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        int newY = this.y - ySpeed;
        checkPosition(this.x, newY);
        this.y = newY;
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        int newX = this.x - xSpeed;
        checkPosition(newX, this.y);
        this.x = newX;
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
        int newX = this.x + xSpeed;
        checkPosition(newX, this.y);
        this.x = newX;
    }

    @Override
    public int getCurrentXPosition() {
        return x;
    }

    @Override
    public int getCurrentYPosition() {
        return y;
    }

    @Override
    public String toString() {
        return "Movable point with coordinates (" + x + "," + y + ")";
    }
}

class MovableCircle implements Movable {

    int radius;
    MovablePoint center;

    public MovableCircle(int radius, MovablePoint center) {
        this.radius = radius;
        this.center = center;
    }

    private void checkCenter(int newX, int newY) throws ObjectCanNotBeMovedException {
        if (newX < 0 || newX > MovablesCollection.getxMax()
                || newY < 0 || newY > MovablesCollection.getyMax()) {
            throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", newX, newY));
        }
    }


    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        int newY = this.center.y + center.ySpeed;
        checkCenter(this.center.x, newY);
        this.center.y = newY;
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
        int newY = this.center.y - center.ySpeed;
        checkCenter(this.center.x, newY);
        this.center.y = newY;
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
        int newX = this.center.x - center.xSpeed;
        checkCenter(newX, this.center.y);
        this.center.x = newX;
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
        int newX = this.center.x + center.xSpeed;
        checkCenter(newX, this.center.y);
        this.center.x = newX;
    }


    @Override
    public int getCurrentXPosition() {
        return this.center.x;
    }

    @Override
    public int getCurrentYPosition() {
        return this.center.y;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Movable circle with center coordinates (" + center.x + "," + center.y
                + ") and radius " + radius;
    }
}

class MovablesCollection {
    private Movable[] movables;
    private int size;

    private static int xMax = 0;
    private static int yMax = 0;

    public MovablesCollection(int xMax, int yMax) {
        this.movables = new Movable[0];
        this.size = 0;
        MovablesCollection.xMax = xMax;
        MovablesCollection.yMax = yMax;
    }

    public static void setxMax(int xMax) {
        MovablesCollection.xMax = xMax;
    }

    public static void setyMax(int yMax) {
        MovablesCollection.yMax = yMax;
    }

    public static int getxMax() {
        return xMax;
    }

    public static int getyMax() {
        return yMax;
    }

    private boolean doesItFit(Movable m) {
        if (m instanceof MovablePoint) {
            int x = m.getCurrentXPosition();
            int y = m.getCurrentYPosition();
            return x >= 0 && x <= xMax && y >= 0 && y <= yMax;

        } else if (m instanceof MovableCircle) {
            MovableCircle c = (MovableCircle) m;
            int cx = c.getCurrentXPosition();
            int cy = c.getCurrentYPosition();
            int r = c.radius;
            return (cx - r) >= 0 && (cx + r) <= xMax
                    && (cy - r) >= 0 && (cy + r) <= yMax;
        }
        return false;
    }


    public void addMovableObject(Movable m) {
        try {
            if (!doesItFit(m)) {
                MovableCircle c = (MovableCircle) m;
                int cx = c.getCurrentXPosition();
                int cy = c.getCurrentYPosition();
                int r = c.radius;
                String msg = "Movable circle with center (" + cx + "," + cy + ") and radius " + r +
                        " can not be fitted into the collection";
                throw new MovableObjectNotFittableException(msg);
            }

            Movable[] tmp = new Movable[size + 1];
            System.arraycopy(movables, 0, tmp, 0, size);
            tmp[size] = m;
            movables = tmp;
            size++;
        } catch (MovableObjectNotFittableException e) {
            System.out.println(e.getMessage());
        }
    }

    public void moveObjectsFromTypeWithDirection(TYPE type, DIRECTION direction) {
        for (Movable movable : movables) {
            if (type == TYPE.POINT && !(movable instanceof MovablePoint)) continue;
            if (type == TYPE.CIRCLE && !(movable instanceof MovableCircle)) continue;

            try {
                switch (direction) {
                    case UP:
                        movable.moveUp();
                        break;
                    case DOWN:
                        movable.moveDown();
                        break;
                    case LEFT:
                        movable.moveLeft();
                        break;
                    case RIGHT:
                        movable.moveRight();
                        break;
                }
            } catch (ObjectCanNotBeMovedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Collection of movable objects with size ").append(size).append(":\n");
        for (Movable m : movables) {
            sb.append(m.toString()).append("\n");
        }
        return sb.toString();
    }
}


public class CirclesTest {

    public static void main(String[] args) {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);

            if (Integer.parseInt(parts[0]) == 0) { //point
                collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
            } else { //circle
                int radius = Integer.parseInt(parts[5]);
                collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
            }

        }
        System.out.println(collection.toString());

        System.out.println("MOVE POINTS TO THE LEFT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES DOWN");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);
        System.out.println(collection.toString());

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setxMax(90);
        MovablesCollection.setyMax(90);

        System.out.println("MOVE POINTS TO THE RIGHT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);
        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES UP");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);
        System.out.println(collection.toString());


    }


}
