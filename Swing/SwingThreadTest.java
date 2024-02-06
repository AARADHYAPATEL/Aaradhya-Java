package Threads;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 	This program demonstrates that a thread that
 	runs in parallel with the event dispatch thread
 	can cause errors in Swing components.
 */

public class SwingThreadTest
{
	public static void main(String[] args)
	{
		SwingThreadFrame frame = new SwingThreadFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.show();
		
	}
}

/**
 	This frame has two buttons to fill a combo box from a
 	separate thread. The "Good" button uses the event queue,
 	the "Bad" button modifies the combo box directly.
 */
class SwingThreadFrame extends JFrame
{
	public SwingThreadFrame()
	{
		setTitle("SwingThread");
		setSize(WIDTH, HEIGHT);
		
		final JComboBox combo = new JComboBox();
		
		JPanel p = new JPanel();
		p.add(combo);
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton b = new JButton("Good");
		b.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						combo.showPopup();
						new GoodWorkerThread(combo).start();
					}
				});
		p = new JPanel();
		p.add(b);
		b = new JButton("Bad");
		b.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					combo.showPopup();
					new BadWorkerThread(combo).start();
				}
			});
		p.add(b);
		getContentPane().add(p, BorderLayout.NORTH);
	}
	
	public static final int WIDTH = 450;
	public static final int HEIGHT = 300;
}

/**
 	This thread modifies a combo box by randomly adding
 	and removing number. This can result in errors because
 	the combo box is not synchronizing and the event dispatch
 	thread accesses the combo box to repaint it.
 */
class BadWorkerThread extends Thread
{
	public BadWorkerThread(JComboBox aCombo)
	{
		combo = aCombo;
		generator = new Random();
	}
	
	public void run()
	{
		try
		{
			while (!interrupted())
			{
				int i = Math.abs(generator.nextInt());
				if (i % 2 == 0)
					combo.insertItemAt(new Integer(i), 0);
				else if (combo.getItemCount() > 0)
					combo.removeItemAt(i % combo.getItemCount());
				
				sleep(1);
			}
		}
		catch (InterruptedException exception) {}
	}
	
	private JComboBox combo;
	private Random generator;
}

/**
 	This thread modifies a combo box by randomly adding
 	and removing numbers. In order to ensure that the
 	combo box is nor corrupted, the editing operations are
 	forwarded to the event dispatch thread.
 */
class GoodWorkerThread extends Thread
{
	public GoodWorkerThread(JComboBox aCombo)
	{
		combo = aCombo;
		generator = new Random();
	}
	
	public void run()
	{
		try
		{
			while (!interrupted())
			{
				EventQueue.invokeLater(new
					Runnable()
					{
						public void run()
						{
							int i = Math.abs(generator.nextInt());
							
							if (i % 2 == 0)
								combo.insertItemAt(new Integer(i), 0);
							else if (combo.getItemCount() > 0)
							combo.removeItemAt(i % combo.getItemCount());
						}
					});
				Thread.sleep(1);
			}
		}
		catch (InterruptedException exception) {}
	}
	
	private JComboBox combo;
	private Random generator;
}
