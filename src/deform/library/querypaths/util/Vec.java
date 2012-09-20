package deform.library.querypaths.util;

import deform.library.querypaths.Constants;


public final class Vec {

	public static final Vec ZeroVec = new Vec(0, 0);

	public final double x, y;

	public Vec(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double normSquared() {
		return x * x + y * y;
	}

	public double norm() {
		return Math.sqrt(normSquared());
	}

	public Vec normalize() {
		return div(norm());
	}

	public Vec add(Vec r) {
		return new Vec(x + r.x, y + r.y);
	}

	public Vec sub(Vec r) {
		return new Vec(x - r.x, y - r.y);
	}

	public Vec mirror(Vec r) {
		return new Vec(2 * x - r.x, 2 * y - r.y);
	}

	public Vec mul(double r) {
		return new Vec(x * r, y * r);
	}

	public Vec div(double r) {
		return new Vec(x / r, y / r);
	}

	public double dot(Vec r) {
		return x * r.x + y * r.y;
	}

	public Vec perpendicularCCW() {
		return new Vec(y, -x);
	}

	// this is a lefthanded coordinate system
	public Vec tanToNormal() {
		return perpendicularCW();
	}

	// this is a lefthanded coordinate system
	public Vec tanToAntiNormal() {
		return perpendicularCCW();
	}

	public Vec perpendicularCW() {
		return new Vec(-y, x);
	}

	public Vec addMul(double s, Vec add) {
		return new Vec(x + s * add.x, y + s * add.y);
	}

	public double distanceSquared(Vec other) {
		double xDist = x - other.x;
		double yDist = y - other.y;
		return xDist * xDist + yDist * yDist;
	}

	public Vec interpolate(double t, Vec other) {
		double rt = 1.0 - t;
		return new Vec(x * rt + other.x * t, y * rt + other.y * t);
	}

	public Vec negate() {
		return new Vec(-x, -y);
	}

	public boolean onSameLine(Vec other) {
		return perpendicularCW().dot(other) == 0;
	}

	// given two vectors on the same line,
	// do the vectors go in the same direction
	public boolean sameDir(Vec other) {
		if (Math.signum(x) == 0) {
			return Math.signum(y) == Math.signum(other.y);
		} else {
			return Math.signum(x) == Math.signum(other.x);
		}
	}

	public boolean isEqError(Vec other) {
		return distanceSquared(other) <= Constants.MAX_ERROR_2_POW2;
	}

	public String toString() {
		return String.format("<%3.3f,%3.3f>", x, y);
	}

	public double distance(Vec other) {
		return Math.sqrt(distanceSquared(other));
	}

	public boolean isEq(Vec vec) {
		return x == vec.x && y == vec.y;
	}

	public Vec between(Vec end) {
		return new Vec((x + end.x)/2,(y + end.y) /2);
	}

}
