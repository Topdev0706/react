package com.rinearn.graph3d.model.dataseries;

import com.rinearn.graph3d.config.RinearnGraph3DConfiguration;
import com.rinearn.graph3d.model.ScriptEngineMount;

import org.vcssl.nano.VnanoException;

import java.math.BigDecimal;

/*
[Inheritance tree]

    AbstractDataSeries
      |
      +- ArrayDataSeries
      |
      +- ExpressionDataSeries
          |
          +- ZxyExpressionDataSeries < This Class
          |
          +- XtYtZtExpressionDataSeries
 */


public class ZxyExpressionDataSeries extends ExpressionDataSeries {

	/** The math expression of "z(x,y)". */
	private final String zExpression;

	/** The number of discretized X-coordinates. */
	private final int xDiscretizationCount;

	/** The number of discretized Y-coordinates. */
	private final int yDiscretizationCount;


	/**
	 * Create an instance for generating data using the specified script engine, under the specified configuration.
	 * 
	 * @param zExpressionThe math expression of "z(x,y)".
	 * @param xDiscretizationCount The number of discretized X-coordinates.
	 * @param yDiscretizationCount The number of discretized Y-coordinates.
	 * @param scrioptEngineMount The "engine-mount", provides a script engine for computing coordinates from math expressions.
	 * @param config The configuration container (for referring the range configuration).
	 */
	public ZxyExpressionDataSeries(
			String zExpression, int xDiscretizationCount, int yDiscretizationCount,
			ScriptEngineMount scriptEngineMount, RinearnGraph3DConfiguration config) {

		super(scriptEngineMount, config);
		this.zExpression = zExpression;
		this.xDiscretizationCount = xDiscretizationCount;
		this.yDiscretizationCount = yDiscretizationCount;
	}


	/**
	 * Computes coordinate values from the math expression of this data series.
	 * 
	 * The computed coordinate values will be stored into the fields: xCoordinates, yCoordinates and zCoordinates.
	 * 
	 * @throws VnanoException Thrown when any (typically syntactic) error has been detected for calculating the math expression.
	 */
	@Override
	public synchronized void computeCoordinates() throws VnanoException {

		// Prepare some values to discretize X and Y coordinates.
		int xN = this.xDiscretizationCount;
		int yN = this.yDiscretizationCount;
		double xMax = super.config.getRangeConfiguration().getXRangeConfiguration().getMaximum().doubleValue();
		double xMin = super.config.getRangeConfiguration().getXRangeConfiguration().getMinimum().doubleValue();
		double yMax = super.config.getRangeConfiguration().getYRangeConfiguration().getMaximum().doubleValue();
		double yMin = super.config.getRangeConfiguration().getYRangeConfiguration().getMinimum().doubleValue();
		double xDelta = (xMax - xMin) / (xN - 1);
		double yDelta = (yMax - yMin) / (yN - 1);

		// Allocate coordinate arrays.
		super.xCoordinates = new double[xN][yN];
		super.yCoordinates = new double[xN][yN];
		super.zCoordinates = new double[xN][yN];

		// Compute coordinate values, and store them into the above coordinate arrays.
		for (int ix=0; ix<xN; ix++) {
			for (int iy=0; iy<yN; iy++) {
				double x = (ix == xN - 1) ? xMax : (xMin + xDelta * ix);
				double y = (iy == yN - 1) ? yMax : (yMin + yDelta * iy);
				double z = super.scriptEngineMount.calculateMathExpression(this.zExpression, x, y);

				super.xCoordinates[ix][iy] = x;
				super.yCoordinates[ix][iy] = y;
				super.zCoordinates[ix][iy] = z;
			}
		}
	}
}
