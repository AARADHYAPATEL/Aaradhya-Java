package Bounced;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

/**
 	Shows animated bouncing balls, some running in selfish
 	threads
 */

public class BounceSelfish
{
	public static void main(String[] args)
	{
		JFrame frame = new BounceFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

/**
 	The frame with canvas and buttons
 */
class BounceFrame extends JFrame
{
	/**
 	Constructs the frame with the canvas for showing the
 	bouncing ball and Start and Close buttons
	 */
	public BounceFrame() {
	    setSize(WIDTH, HEIGHT);
	    setTitle("BounceSelfish");
	
	    Container contentPane = getContentPane();
	    canvas = new BallCanvas();
	    contentPane.add(canvas, BorderLayout.CENTER);
	    JPanel buttonPanel = new JPanel(new FlowLayout());
	    addButton(buttonPanel, "Start", 
	    	new ActionListener() {
	        	public void actionPerformed(ActionEvent evt) {
	        		addBall(false, Color.black);
	        	}
	    	});
	
	    addButton(buttonPanel, "Selfish", 
	    	new ActionListener()
	    	{
	        	public void actionPerformed(ActionEvent evt) {
	        		addBall(true, Color.red);
	        	}
	    	});
	
	    addButton(buttonPanel, "Close", 
	    	new ActionListener() 
	    		{
	        		public void actionPerformed(ActionEvent evt) {
	        			System.exit(0);
	        		}
	    		});
	    contentPane.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	/**
 	Adds a button to a container.
 	@param c the container
 	@param title the button title
 	@param listener the action listener for the button
	*/
	public void addButton(Container c, String title,
			ActionListener listener)
	{
		JButton button = new JButton(title);
		c.add(button);
		button.addActionListener(listener);
	}
	
	/**
 	Adds a bouncing ball to the canvas and starts a thread
 	to make it bounce
 	@param priority the priority for the threads
 	@color the color for the balls
	*/
	public void addBall(boolean selfish, Color color)
	{
		Ball b = new Ball(canvas, color);
		canvas.add(b);
		BallThread thread = new BallThread(b, selfish);
		thread.start();
	}
	
	private BallCanvas canvas;
	public static final int WIDTH = 450;
	public static final int HEIGHT = 350;
}

/**
	A thread that animates a bouncing ball.
*/
class BallThread extends Thread
{
	/**
	 	Constructs the thread
	 	@aBall the ball to bounce
	 	@boolean selfishFlag true if the thread is selfish, using
	 	a busy wait instead of calling sleep
	 */
	public BallThread(Ball aBall, boolean selfishFlag)
	{
		b = aBall;
		selfish = selfishFlag;
	}
	
	public void run()
	{
		try
		{
			for (int i = 1; i <= 1000; i++)
			{
				b.move();
				if (selfish)
				{
					// busy wait for 5 milliseconds
					long t = System.currentTimeMillis();
					while (System.currentTimeMillis() < t + 5)
						;
				}
				else
					sleep(5);
			}
		}
		catch (InterruptedException excpetion)
		{
		}
	}
	
	private Ball b;
	boolean selfish;
}

class BallCanvas extends JPanel
{
/**
 	Add a ball to the canvas.
 	@param b the ball to add
 */
	public void add(Ball b)
	{
		balls.add(b);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		for (int i = 0; i < balls.size(); i++)
		{
			Ball b = (Ball)balls.get(i);
			b.draw(g2);
		}
	}	

	private ArrayList balls = new ArrayList();
}

/**
	A ball that moves and bounces off the edges of a
	component
*/
class Ball
{
		/**
	 	Constructs a ball in the upper left corner
	 	@c the component in which the ball bounces
	 	@aColor the color of the ball
		*/
	public Ball(Component c, Color aColor)
	{
		canvas = c;
		color = aColor;
	}
	
	/**
	 	Draws the ball at its current position
	 	@param g2 the graphics context
	 */
	public void draw(Graphics2D g2)
	{
		g2.setColor(color);
		g2.fill(new Ellipse2D.Double(x, y, XSIZE, YSIZE));
	}
	
	/**
	 	Moves the ball to the next position, reversing direction
	 	if it hits one of the edges
	 */
	public void move()
	{
		x += dx;
		y += dy;
		if (x < 0)
		{
			x = 0;
			dx = -dx;
		}
		if (x + XSIZE >= canvas.getWidth())
		{
			x = canvas.getWidth() - XSIZE;
			dx = -dx;
		}
		if (y < 0)
		{
			y = 0;
			dy = -dy;
		}
		if (y + YSIZE >= canvas.getHeight())
		{
			y = canvas.getHeight() - YSIZE;
			dy = -dy;
		}
		
		canvas.repaint();
	}
	
	private Component canvas;
	private Color color;
	private static final int XSIZE = 15;
	private static final int YSIZE = 15;
	private int x = 0;
	private int y = 0;
	private int dx = 2;
	private int dy = 2;
}