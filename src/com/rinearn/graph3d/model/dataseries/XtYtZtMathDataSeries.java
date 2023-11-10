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
      +- MathDataSeries
          |
          +- ZxyMathDataSeries
          |
          +- XtYtZtMathDataSeries < This Class
 */


public class XtYtZtMathDataSeries extends MathDataSeries {

	/**
	 * Create an instance for generating data using the specified script engine, under the specified configuration.
	 * 
	 * @param scrioptEngineMount The "engine-mount", provides a script engine for computing coordinates from math expressions.
	 * @param config The configuration container (for referring the range configuration).
	 */
	public XtYtZtMathDataSeries(ScriptEngineMount scriptEngineMount, RinearnGraph3DConfiguration config) {
		super(scriptEngineMount, config);
	}


	/**
	 * Computes coordinate values from the math expressions of this data series.
	 * 
	 * The computed coordinate values will be stored into the fields: xCoordinates, yCoordinates and zCoordinates.
	 * 
	 * @throws VnanoException Thrown when any (typically syntactic) error has been detected for calculating the math expressions.
	 */
	@Override
	public synchronized void computeCoordinates() throws VnanoException {
		throw new RuntimeException("Unimplemented yet.");
	}
}