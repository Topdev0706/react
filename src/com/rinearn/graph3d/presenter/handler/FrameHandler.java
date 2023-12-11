package com.rinearn.graph3d.presenter.handler;

import com.rinearn.graph3d.model.Model;
import com.rinearn.graph3d.presenter.Presenter;
import com.rinearn.graph3d.view.View;
import com.rinearn.graph3d.view.MainWindow;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * The class handling events of the frame of the main window.
 */
public final class FrameHandler {

	/** The front-end class of "Model" layer, which provides internal logic procedures and so on. */
	private final Model model;

	/** The front-end class of "View" layer, which provides visible part of GUI without event handling. */
	private final View view;

	/** The front-end class of "Presenter" layer, which invokes Model's procedures triggered by user's action on GUI. */
	private final Presenter presenter;

	/** The flag for turning on/off the event handling feature of this instance. */
	private volatile boolean eventHandlingEnabled = true;


	/**
	 * Create a new instance handling events and API requests using the specified resources.
	 * 
	 * @param model The front-end class of "Model" layer, which provides internal logic procedures and so on.
	 * @param view The front-end class of "View" layer, which provides visible part of GUI without event handling.
	 * @param presenter The front-end class of "Presenter" layer, which handles events occurred on GUI, and API requests.
	 */
	public FrameHandler(Model model, View view, Presenter presenter) {
		this.model = model;
		this.view = view;
		this.presenter = presenter;
		JFrame frame = this.view.mainWindow.frame;

		// Add the event listener handling resizing events of the frame.
		ResizeEventListener resizeListener = new ResizeEventListener();
		frame.addComponentListener(resizeListener);

		// Add the event listener handling resizing events of the frame.
		CloseEventListener closeListener = new CloseEventListener();
		frame.addWindowListener(closeListener);
	}


	/**
	 * Turns on/off the event handling feature of this instance.
	 * 
	 * @param enabled Specify false for turning off the event handling feature (enabled by default).
	 */
	public synchronized void setEventHandlingEnabled(boolean enabled) {
		this.eventHandlingEnabled = enabled;
	}


	/**
	 * Gets whether the event handling feature of this instance is enabled.
	 * 
	 * @return Returns true if the event handling feature is enabled.
	 */
	public synchronized boolean isEventHandlingEnabled() {
		return this.eventHandlingEnabled;
	}





	// ================================================================================
	// 
	// - Event Listeners -
	//
	// ================================================================================


	/**
	 * The event listener handling resizing events of the frame.
	 */
	private class ResizeEventListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent ce) {
			if (!isEventHandlingEnabled()) {
				return;
			}
			view.mainWindow.resize();
		}
	}


	/**
	 * The event listener handling resizing events of the frame.
	 */
	private class CloseEventListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent we) {
			if (!isEventHandlingEnabled()) {
				return;
			}

			// Temporary
			System.exit(0);
		}
	}





	// ================================================================================
	// 
	// - API Listeners -
	//
	// ================================================================================


	/**
	 * Sets the location and the size of the main window.
	 * (API Implementation)
	 * 
	 * @param x The X coordinate of the left-top edge of the window.
	 * @param y The Y coordinate of the left-top edge of the window.
	 * @param width The width of the window.
	 * @param height The height of the window.
	 */
	public void setWindowBounds(int x, int y, int width, int height) {

		// Handle the API on the event-dispatcher thread.
		SetWindowBoundsAPIListener apiListener = new SetWindowBoundsAPIListener(x, y, width, height);
		if (SwingUtilities.isEventDispatchThread()) {
			apiListener.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(apiListener);
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * The class handling API requests for setting the location and the size of this window,
	 * on event-dispatcher thread.
	 */
	private final class SetWindowBoundsAPIListener implements Runnable {

		/** The X coordinate of the left-top edge of the window, to be set. */
		private final int x;

		/** The Y coordinate of the left-top edge of the window, to be set. */
		private final int y;

		/** The width of the window, to be set. */
		private final int width;

		/** The height of the window, to be set. */
		private final int height;

		/**
		 * Creates a new instance for setting the window to the specified location/size.
		 * 
		 * @param x The X coordinate of the left-top edge of the window.
		 * @param y The Y coordinate of the left-top edge of the window.
		 * @param width The width of the window.
		 * @param height The height of the window.
		 */
		public SetWindowBoundsAPIListener(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		public void run() {
			MainWindow window = view.mainWindow;
			window.setWindowBounds(this.x, this.y, this.width, this.height);
		}
	}


	/**
	 * Sets the size of the graph screen.
	 * (API Implementation)
	 * 
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public void setScreenSize(int width, int height) {

		// Handle the API on the event-dispatcher thread.
		SetScreenSizeAPIListener apiListener = new SetScreenSizeAPIListener(width, height);
		if (SwingUtilities.isEventDispatchThread()) {
			apiListener.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(apiListener);
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * The class handling API requests for setting the size of the graph screen,
	 * on event-dispatcher thread.
	 */
	private final class SetScreenSizeAPIListener implements Runnable {

		/** The width of the screen, to be set. */
		private final int width;

		/** The height of the screen, to be set. */
		private final int height;

		/**
		 * Creates a new instance for resizing the screen into the specified size.
		 * 
		 * @param width The width of the screen.
		 * @param height The height of the screen.
		 */
		public SetScreenSizeAPIListener(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public void run() {
			MainWindow window = view.mainWindow;
			window.setScreenSize(this.width, this.height);
		}
	}
}