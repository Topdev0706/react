package com.rinearn.graph3d.renderer.simple;

import com.rinearn.graph3d.renderer.RinearnGraph3DLightingParameter;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * A geometric piece class representing a line.
 */
public class QuadrangleGeometricPiece extends GeometricPiece {

	/**
	 * Creates a new geometric piece representing a quadrangle consisting of points A, B, C and D.
	 * 
	 * @param aX The x coordinate value of the point A, in the scaled space.
	 * @param aY The y coordinate value of the point A, in the scaled space.
	 * @param aZ The z coordinate value of the point A, in the scaled space.
	 * @param bX The x coordinate value of the point B, in the scaled space.
	 * @param bY The y coordinate value of the point B, in the scaled space.
	 * @param bZ The z coordinate value of the point B, in the scaled space.
	 * @param cX The x coordinate value of the point C, in the scaled space.
	 * @param cY The y coordinate value of the point C, in the scaled space.
	 * @param cZ The z coordinate value of the point C, in the scaled space.
	 * @param dX The x coordinate value of the point D, in the scaled space.
	 * @param dY The y coordinate value of the point D, in the scaled space.
	 * @param dZ The z coordinate value of the point D, in the scaled space.
	 * @param z The z coordinate value of the center of the point, in the scaled space.
	 * @param radius The radius (pixels) of the point.
	 * @param color The color of the point.
	 */
	public QuadrangleGeometricPiece(double aX, double aY, double aZ,
			double bX, double bY, double bZ,
			double cX, double cY, double cZ,
			double dX, double dY, double dZ,
			Color color) {

		this.vertexCount = 5; // 4 vertex vectors + 1 normal vector
		this.scaledVertexArray = new double[][] {
			{ aX, aY, aZ }, {bX, bY, bZ}, {cX, cY, cZ}, {dX, dY, dZ},
			this.computeNormalVector(aX, aY, aZ , bX, bY, bZ, cX, cY, cZ, dX, dY, dZ)
		};

		this.transformedVertexArray = new double[this.vertexCount][3]; // [3] is X/Y/Z
		this.projectedVertexArray = new int[this.vertexCount][2];      // [2] is X/Y
		this.originalColor = color;
	}


	/**
	 * Computes the normal vector of the quadrangle of which vertices is the points A, B, C, and D.
	 * 
	 * Basically, the normal vector will be calculated for the triangle consisting of the points A, B, and C.
	 * When there is a couple of points which have the same coordinate values in the points {A, B, C}, 
	 * the normal vector will be calculated for the triangle consisting of the points A, C, and D.
	 * 
	 * @param aX The x coordinate value of the point A.
	 * @param aY The y coordinate value of the point A.
	 * @param aZ The z coordinate value of the point A.
	 * @param bX The x coordinate value of the point B.
	 * @param bY The y coordinate value of the point B.
	 * @param bZ The z coordinate value of the point B.
	 * @param cX The x coordinate value of the point C.
	 * @param cY The y coordinate value of the point C.
	 * @param cZ The z coordinate value of the point C.
	 * @return The array storing x, y, and z coordinate values of the computed normal vector.
	 */
	private double[] computeNormalVector(double aX, double aY, double aZ,
			double bX, double bY, double bZ,
			double cX, double cY, double cZ,
			double dX, double dY, double dZ) {

		// Detect whether there is a couple of points having the same coordinate values in {A, B, C}.
		boolean existsSamePointInABC =
				(aX==bX && aY==bY && aZ==bZ) || 
				(aX==cX && aY==cY && aZ==cZ) || 
				(bX==cX && bY==cY && bZ==cZ);

		// Arrays for storing the vectors of the triangle sides.
		double[] sideVectorP = new double[3];
		double[] sideVectorQ = new double[3];

		// Calculate the coordinate values of the above 'triangle side' vectors.
		if (existsSamePointInABC) {
			sideVectorP[X] = cX - aX;
			sideVectorP[Y] = cY - aY;
			sideVectorP[Z] = cZ - aZ;

			sideVectorQ[X] = dX - aX;
			sideVectorQ[Y] = dY - aY;
			sideVectorQ[Z] = dZ - aZ;
			
		} else {
			sideVectorP[X] = bX - aX;
			sideVectorP[Y] = bY - aY;
			sideVectorP[Z] = bZ - aZ;

			sideVectorQ[X] = cX - aX;
			sideVectorQ[Y] = cY - aY;
			sideVectorQ[Z] = cZ - aZ;
		}

		// Calculate the normal vector as the cross product of the 'triangle side' vectors.
		double[] normalVector = new double[3];
		normalVector[X] = sideVectorP[Y] * sideVectorQ[Z] - sideVectorP[Z] * sideVectorQ[Y];
		normalVector[Y] = sideVectorP[Z] * sideVectorQ[X] - sideVectorP[X] * sideVectorQ[Z];
		normalVector[Z] = sideVectorP[X] * sideVectorQ[Y] - sideVectorP[Y] * sideVectorQ[X];

		// Normalize the length of the normal vector.
		double normalVectorLengthRecip = 1.0 / Math.sqrt(
				normalVector[X] * normalVector[X] + normalVector[Y] * normalVector[Y] + normalVector[Z] * normalVector[Z]
		);
		normalVector[X] *= normalVectorLengthRecip;
		normalVector[Y] *= normalVectorLengthRecip;
		normalVector[Z] *= normalVectorLengthRecip;
		return normalVector;
	}


	/**
	 * Transforms the coordinate values of this quadrangle.
	 * 
	 * @param matrix The transformation matrix.
	 */
	@Override
	public void transform(double[][] matrix) {

		// Short aliases of the matrix and vertices arrays.
		double[][] m = matrix;
		double[][] sv = this.scaledVertexArray;
		double[][] tv = this.transformedVertexArray;

		// Transform each vertex.
		for (int ivertex=0; ivertex<=3; ivertex++) {

			// Transform coordinate values of the vertex, where X=0, Y=1, and Z=2.
			tv[ivertex][X] = m[0][0] * sv[ivertex][X] + m[0][1] * sv[ivertex][Y] + m[0][2] * sv[ivertex][Z] + m[0][3];
			tv[ivertex][Y] = m[1][0] * sv[ivertex][X] + m[1][1] * sv[ivertex][Y] + m[1][2] * sv[ivertex][Z] + m[1][3];
			tv[ivertex][Z] = m[2][0] * sv[ivertex][X] + m[2][1] * sv[ivertex][Y] + m[2][2] * sv[ivertex][Z] + m[2][3];
		}

		// For the normal vector (ivertex = 4), transform only its direction.
		// So multiply only 3x3 part of the transformation matrix, to the normal vector.
		tv[4][X] = m[0][0] * sv[4][X] + m[0][1] * sv[4][Y] + m[0][2] * sv[4][Z];
		tv[4][Y] = m[1][0] * sv[4][X] + m[1][1] * sv[4][Y] + m[1][2] * sv[4][Z];
		tv[4][Z] = m[2][0] * sv[4][X] + m[2][1] * sv[4][Y] + m[2][2] * sv[4][Z];

		// If the normal vector is facing to the depth direction, reverse its direction.
		if (tv[4][Z] < 0) {
			tv[4][X] = -tv[4][X];
			tv[4][Y] = -tv[4][Y];
			tv[4][Z] = -tv[4][Z];
		}

		// Compute the square of the 'depth' value.
		double meanZ = (tv[0][Z] + tv[1][Z] + tv[2][Z] + tv[3][Z]) * 0.25;
		this.depthSquaredValue = meanZ * meanZ;
	}


	/**
	 * Shades the color.
	 * 
	 * @param lightingParameter The object storing parameters for lighting and shading.
	 */
	@Override
	public void shade(RinearnGraph3DLightingParameter lightingParameter) {

		// Prepare aliases of the normal vector,
		// and the direction vector pointing to the light source (hereinafter referred to as 'light vector').
		double[] normalVector = this.transformedVertexArray[4];
		double[] lightVector = {
				lightingParameter.getLightSourceDirectionX(),
				lightingParameter.getLightSourceDirectionY(),
				lightingParameter.getLightSourceDirectionZ()
		};

		// Calculate the value of 'directional product',
		// which is the inner product between the normal vector and the light vector.
		double directionalProduct =
				normalVector[X] * lightVector[X] + 
				normalVector[Y] * lightVector[Y] + 
				normalVector[Z] * lightVector[Z];

		// Calculate the angle between the normal vector and the light vector,
		// and normalize it into the range [0.0, 1.0].
		double directionalAngle = Math.acos(directionalProduct);
		double normalizedDirectionalAngle = directionalAngle / Math.PI;

		// If the value of 'directional product' is negative, replace it by 0.
		double directionalProductPositive = (0 <= directionalProduct) ? directionalProduct : 0.0;

		// Calculate the brightness contributed by ambient, diffuse, and diffractive reflections.
		double baseBrightness = 
				lightingParameter.getAmbientReflectionStrength() +
				lightingParameter.getDiffuseReflectionStrength() * directionalProductPositive +
				lightingParameter.getDiffractiveReflectionStrength() * normalizedDirectionalAngle;

		// Calculate the brightness contributed by specular reflection.
		double specularBrightness = 0.0;
		double specularAngle = lightingParameter.getSpecularReflectionAngle();
		if (directionalAngle < specularAngle) {
			specularBrightness = lightingParameter.getSpecularReflectionStrength() *
					Math.cos(0.5 * Math.PI * directionalAngle / specularAngle);
		}

		// Convert the RGBA components of the original color to double-type values, in range [0.0, 1.0].
		double recip255 = 1.0 / 255.0;
		double r = this.originalColor.getRed() * recip255;
		double g = this.originalColor.getGreen() * recip255;
		double b = this.originalColor.getBlue() * recip255;
		double a = this.originalColor.getAlpha() * recip255;

		// Blend the RGBA components based on the calculated brightnesses.
		r = r * baseBrightness * (1.0 - specularBrightness) + specularBrightness;
		g = g * baseBrightness * (1.0 - specularBrightness) + specularBrightness;
		b = b * baseBrightness * (1.0 - specularBrightness) + specularBrightness;
		a = a + specularBrightness;

		// Calculated RGBA components may exceed the range [0.0, 1.0], so crop values.
		r = Math.min(Math.max(r, 0.0), 1.0);
		g = Math.min(Math.max(g, 0.0), 1.0);
		b = Math.min(Math.max(b, 0.0), 1.0);
		a = Math.min(Math.max(a, 0.0), 1.0);
		this.onscreenColor = new Color((float)r, (float)g, (float)b, (float)a);
	}


	/**
	 * Computes the projected screen coordinate values of this quadrangle.
	 * 
	 * @param screenWidth The width (pixels) of the screen.
	 * @param screenHeight The height (pixels) of the screen.
	 * @param screenOffsetX The X-offset value (positive for shifting rightward) of the screen center.
	 * @param screenOffsetY The Y-offset value (positive for shifting upward) of the screen center.
	 * @param magnification The magnification of the conversion from lengths in 3D space to pixels.
	 */
	@Override
	public void project(int screenWidth, int screenHeight, int screenOffsetX, int screenOffsetY, double magnification) {

		// Compute the project coordinates on the screen.
		// (The origin is the left-top edge of the screen.)
		int screenCenterX = screenWidth >> 1 + screenOffsetX; // bit-shifting instead of dividing by 2.
		int screenCenterY = screenHeight >> 1 - screenOffsetY;

		// Short aliases of the vertices arrays.
		double[][] tv = this.transformedVertexArray;
		int[][] pv = this.projectedVertexArray;

		// Project each vertex.
		for (int ivertex=0; ivertex<this.vertexCount; ivertex++) {
			double projectionRatio = magnification / -tv[ivertex][Z]; // Z takes a negative value for the depth direction.
			pv[ivertex][X] = screenCenterX + (int)(tv[ivertex][X] * projectionRatio);
			pv[ivertex][Y] = screenCenterY - (int)(tv[ivertex][Y] * projectionRatio);
		}
	}


	/**
	 * Draws this quadrangle.
	 * 
	 * @param graphics The Graphics2D instance for drawing shapes to the screen image.
	 */
	@Override
	public void draw(Graphics2D graphics) {
		graphics.setColor(this.onscreenColor);

		int[][] pv = this.projectedVertexArray;
		int[] xArray = { pv[0][X], pv[1][X], pv[2][X], pv[3][X] };
		int[] yArray = { pv[0][Y], pv[1][Y], pv[2][Y], pv[3][Y] };
		graphics.fillPolygon(xArray, yArray, 4);
	}
}
