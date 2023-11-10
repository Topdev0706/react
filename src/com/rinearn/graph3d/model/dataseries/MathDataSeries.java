package com.rinearn.graph3d.model.dataseries;

import com.rinearn.graph3d.config.RinearnGraph3DConfiguration;
import com.rinearn.graph3d.model.ScriptEngineMount;

import org.vcssl.nano.VnanoException;

/*
[Inheritance tree]

    AbstractDataSeries
      |
      +- ArrayDataSeries
      |
      +- MathDataSeries < This Class
          |
          +- ZxyMathDataSeries
          |
          +- XtYtZtMathDataSeries
 */


public abstract class MathDataSeries extends AbstractDataSeries {

	/** The "engine-mount", provides a script engine for computing coordinates from math expressions. */
	protected final ScriptEngineMount scriptEngineMount;

	/** The configuration container (for referring the range configuration). */
	protected final RinearnGraph3DConfiguration config;

	/** The X-coordinate values of the points of this data series. */
	protected volatile double[][] xCoordinates = null;

	/** The Y-coordinate values of the points of this data series. */
	protected volatile double[][] yCoordinates = null;

	/** The Z-coordinate values of the points of this data series. */
	protected volatile double[][] zCoordinates = null;


	/**
	 * Create an instance for generating data using the specified script engine, under the specified configuration.
	 * 
	 * @param scrioptEngineMount The "engine-mount", provides a script engine for computing coordinates from math expressions.
	 * @param config The configuration container (for referring the range configuration).
	 */
	protected MathDataSeries(ScriptEngineMount scriptEngineMount, RinearnGraph3DConfiguration config) {
		this.scriptEngineMount = scriptEngineMount;
		this.config = config;
	}


	/**
	 * Computes coordinate values from math expression(s) of this data series.
	 * 
	 * The computed coordinate values will be stored into the fields: xCoordinates, yCoordinates and zCoordinates.
	 * This abstract method is implemented by subclasses: ZxyMathDataSeries and XtYtZtMathDataSeries.
	 * 
	 * @throws VnanoException Thrown when any (typically syntactic) error has been detected for calculating math expression(s).
	 */
	public abstract void computeCoordinates() throws VnanoException;


	/**
	 * Gets the X-coordinate values of the points of this data series, in double-type.
	 * 
	 * @return The X-coordinate values.
	 */
	@Override
	public synchronized double[][] getXCoordinates() {
		if (this.xCoordinates == null) {
			throw new IllegalStateException("The X-coordinate values has not been generated yet.");
		}
		return this.xCoordinates;
	}


	/**
	 * Gets the Y-coordinate values of the points of this data series, in double-type.
	 * 
	 * @return The Y-coordinate values.
	 */
	@Override
	public synchronized double[][] getYCoordinates() {
		if (this.yCoordinates == null) {
			throw new IllegalStateException("The Y-coordinate values has not been generated yet.");
		}
		return this.yCoordinates;		
	}


	/**
	 * Gets the Z-coordinate values of the points of this data series, in double-type.
	 * 
	 * @return The Z-coordinate values.
	 */
	@Override
	public synchronized double[][] getZCoordinates() {
		if (this.zCoordinates == null) {
			throw new IllegalStateException("The Z-coordinate values has not been generated yet.");
		}
		return this.zCoordinates;
	}
}