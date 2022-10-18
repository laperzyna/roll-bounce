package A2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RollBounce extends JPanel implements ActionListener {

    protected Timer tm;

    // TODO: Consider removing the next two lines (coordinates for two balls)
    protected int width;
    protected int height;
    protected int ball_radius;
    protected int balls;
    protected int x1, y1;
    protected int x2, y2;
    protected int minspeed;
    protected int maxspeed;
    protected int gravity;
    protected int friction;
    protected String listType;
    protected List<Ball> ballList;


    /**
     * Is a public class that contains information about a ball. 
     * It includes the color of the ball, assigns a random starting position 
     * of the ball using the x and y values, and assigns the ball's x and y velocity.
     * @param color calls on the array of colors to assign a ball a color
     * @param x     assigns a random x, or starting position position to the ball
     * @param y     similar to x, assigns a random y/starting position to the ball
     * @param xVelocity assigns the ball its velocity using minspeed and maxspeed +1 taken from the prop file
     * @param yVelocity assigns the ball's yVelocity to 0 because the ball is being dropped from the top
     * @param width     Is value taken of width from prop file
     * @param height    Is value taken of height from prop file
     * @param minspeed  Is value taken of minspeed from prop file
     * @param maxspeed  Is value taken of maxspeed from prop file
     **/
    private class Ball {
        Color color;
        int x, y;
        int xVelocity;
        int yVelocity;
        public Ball(Color color){
            this.color = color;
            this.x = ThreadLocalRandom.current().nextInt(0, width);
            this.y = ThreadLocalRandom.current().nextInt(0, height);
            this.xVelocity = ThreadLocalRandom.current().nextInt(minspeed, maxspeed + 1);
            this.yVelocity = 0;
        }
    }

    /**
     * This method takes in the propertyFileName in order to get the 
     * values from the RollBounce.prop file. It creates a new property 
     * that is able to get and hold each value. There are getters used 
     * here in order to get each value in the prop file.
     * @param prop New property to get values from prop file
     * @param width getter for window_width
     * @param height getter for window_height
     * @param balls getter for number of balls
     * @param ball_radius getter for ball_radius
     * @param minspeed getter for minspeed
     * @param maxspeed getter for maxspeed
     * @param listType getter for type of list used
     * @param gravity getter for gravity
     * @param friction getter for friction
     * @param ballList initialized list of either ArrayList or LinkedList
     * @param color array of every ball color
     * @param ball used to assign a color to each ball in order to not skip or duplicate a color
     **/
    public RollBounce (String propertyFileName) {
        // TODO: insert your code to read from configuration file here.
        // TODO: handle missing file
        Properties prop = new Properties();
        try {
            ClassLoader classLoader = RollBounce.class.getClassLoader();

            URL res = Objects.requireNonNull(classLoader.getResource(propertyFileName),
                "Can't find configuration file app.config");

            InputStream is = new FileInputStream(res.getFile());

            prop.load(is);

            this.width = Integer.parseInt(prop.getProperty("window_width"));
            this.height = Integer.parseInt(prop.getProperty("window_height"));
            this.balls = Integer.parseInt(prop.getProperty("balls"));
            this.ball_radius = Integer.parseInt(prop.getProperty("ball_radius"));
            this.minspeed = Integer.parseInt(prop.getProperty("minspeed"));
            this.maxspeed = Integer.parseInt(prop.getProperty("maxspeed"));
            this.listType = prop.getProperty("list");
            this.gravity = Integer.parseInt(prop.getProperty("gravity"));
            this.friction = Integer.parseInt(prop.getProperty("friction"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (listType.equals("arraylist")){
            ballList = new ArrayList<Ball>();
        } else {
            ballList = new LinkedList<Ball>();
        }
        tm = new Timer(75, this); // TODO: Replace the first argument with delay with value from config file.

        // TODO: Consider removing the next two lines (coordinates) for random starting locations.
        // x1 = 100; y1 = 50;
        // x2 = 200; y2 = 400;
        Color[] colors = {Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.RED, Color.GREEN, Color.YELLOW};
        for (int i = 0; i < balls; i++){
            Ball ball = new Ball(colors[i % colors.length]);
            ballList.add(ball);
        }
    }

    /**
     * This method's purpose is to assign each ball a color, size,
     * and position on the canvas.
     * @param ball used to get a ball and it's characteristics
     * @param ballList used to store and keep track of each ball
     **/
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Probably best you leave this as is.

        // TODO: Paint each ball. Here's how to paint two balls, one after the other:

        for (Ball ball : ballList){
            g.setColor(ball.color);
            g.fillOval(ball.x, ball.y, ball_radius, ball_radius);
        }

        // Recommend you leave the next line as is
        tm.start();
    }
    /**
     * This method's purpose is to apply friction to each ball when it 
     * has bounced on any side of the canvas. This method is a void method 
     * and is called in the actionPreformed method to apply friction.
     * @param yVelocity used to apply yVelocity to a ball when bounced
     * @param xVelocity used to apply xVelocity to a ball when bounced
     * @param friction used to apply friction to a ball when bounced
     **/
    private void applyFriction(Ball ball) {
        ball.yVelocity -= friction;
        if(ball.xVelocity > 0) {
            ball.xVelocity -= friction;
        } else if (ball.xVelocity < 0){
            ball.xVelocity += friction;
        }
    }
    /**
     * This method's purpose is to allow the ball to bounce. It checks 
     * when the ball has bounced on any side of the canvas and calls on the
     * applyFriction method when it does
     * @param ball used to get a ball and it's characteristics
     * @param ballList used to store and keep track of each ball
     * @param yVelocity used to apply yVelocity to a ball when bounced
     * @param xVelocity used to apply xVelocity to a ball when bounced
     * @param gravity  used to set the gravity of a ball
     * @param height used to check if ball bounced or reached the top of the canvas
     * @param width used to check if ball bounced or reached the side of the canvas
     * @param newX  used to make sure balls bounce down and have the correct velocity and gravity once bounced
     **/
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO: Change the location of each ball. Here's an example of them moving across the screen:
        //       ... but to be clear, you should change this.
        // These two "if" statements keep the balls on the screen in case they go off one side.
        //making sure the balls stay inside the canvas
        for (Ball ball : ballList) {
            ball.yVelocity += gravity;
            if (ball.y == height || ball.y == 0) {
                ball.yVelocity = -ball.yVelocity;
                applyFriction(ball);
            }
            if (ball.x == 0 || ball.x == width) {
                ball.xVelocity = -ball.xVelocity;
                applyFriction(ball);
            }
            //makes sure balls bounce down
            int newX = ball.x + ball.xVelocity;
            if (newX < 0) {
                newX = 0;
            } else if (newX > width) {
                newX = width;
            }
            ball.x = newX;
            int newY = ball.y + ball.yVelocity;
            if (newY < 0) {
                newY = 0;
            } else if (newY > height) {
                newY = height;
            }
            ball.y = newY;

        }
        // Keep this at the end of the function (no matter what you do above):
        repaint();
    }
    /**
     * This is a getter function that returns the width value
     * @return width
     **/
    public int getWidth(){
        return width;
    }
    /**
     * This is a getter function that returns the height value
     * @return height
     **/
    public int getHeight(){
        return height;
    }

    public static void main(String[] args) {
        RollBounce rb = new RollBounce(args[0]);
        JFrame jf = new JFrame();
        jf.setTitle("Roll Bounce");
        jf.setSize(rb.getWidth(), rb.getHeight()); // TODO: Replace with the size from configuration!
        jf.add(rb);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

