package temp;

import com.rinearn.graph3d.renderer.simple.SimpleRenderer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.Image;
import java.lang.reflect.InvocationTargetException;


/**
 * A temporary main class in the development of this application.
 */
public class TempMain {
	public static void main(String[] args) {
		System.out.println("Hello RINEARN Graph 3D Ver.6!");

		// Launch a temporary window for previewing the rendered images.
		TemporaryPreviewWindow tempWindow = new TemporaryPreviewWindow();

		// Instantiate a simple implementation of the rendering engine of RINEARN Graph 3D.
		SimpleRenderer renderer = new SimpleRenderer(800, 600);

		// Perform the rendering process.
		renderer.render();

		// Preview the rendered image.
		Image renderedImage = renderer.getScreenImage();
		tempWindow.preview(renderedImage);
	}


	// A temporary window class for previewing the rendered images.
	private static class TemporaryPreviewWindow {
		private JFrame frame;
		private JLabel screenLabel;
		private ImageIcon screenIcon;

		public TemporaryPreviewWindow() {

			// Initialize GUI components on the event dispatcher thread.
			try {
				SwingUtilities.invokeAndWait(new Initializer());
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		// Previews the specified image.
		public void preview(Image image) {
			this.screenIcon.setImage(image);
			this.screenLabel.repaint();
		}

		// The Runnable impl for initializing GUI components on the event dispatcher thread.
		private class Initializer implements Runnable {
			@Override
			public void run() {
				
				// Create the frame of the window.
				frame = new JFrame();
				frame.setTitle("Temporary Preview Window");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setBounds(0, 0, 800, 620);

				// Create the screen.
				screenLabel = new JLabel();
				screenIcon = new ImageIcon();
				screenLabel.setIcon(screenIcon);
				frame.getContentPane().add(screenLabel);

				// Show the window.
				screenLabel.setVisible(true);
				frame.setVisible(true);
			}
		}
	}
}