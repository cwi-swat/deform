package deform.library.querypaths;


import deform.library.querypaths.paths.paths.QueryPath;

import deform.library.querypaths.paths.paths.simple.Line;
import deform.library.querypaths.util.Vec;

public final class TriangleCoordinateSystem {

	final double startX, lengthX;
	final Vec start, end;
	final Vec dirA, dirB; // must be normalized

	public TriangleCoordinateSystem(double startX, double lengthX, Vec start,
			Vec end, Vec dirA, Vec dirB) {
		this.startX = startX;
		this.lengthX = lengthX;
		this.start = start;
		this.end = end;
		this.dirA = dirA;
		this.dirB = dirB;
	}

	public static TriangleCoordinateSystem create(Line line, Vec prevNormal,
			Vec normal, Vec nextNormal) {
		Vec dirA = prevNormal.add(normal).div(
				1 + Math.abs(prevNormal.dot(normal)));
		Vec dirB = nextNormal.add(normal).div(
				1 + Math.abs(nextNormal.dot(normal)));
		return new TriangleCoordinateSystem(line.tInterval.low,
				line.tInterval.length, line.getStartPoint(),
				line.getEndPoint(), dirA, dirB);
	}

	public Vec getAt(Vec loc) {
		double relX = (loc.x - startX) / lengthX;
		Vec l = start.add(dirA.mul(loc.y));
		Vec r = end.add(dirB.mul(loc.y));
		return l.interpolate(relX, r);
	}

}
