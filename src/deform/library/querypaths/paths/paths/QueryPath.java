package deform.library.querypaths.paths.paths;

import static deform.library.querypaths.util.Util.square;
import deform.library.querypaths.paths.paths.compound.ShapeSet;
import deform.library.querypaths.paths.paths.simple.Line;
import deform.library.querypaths.paths.paths.simple.curve.Curve;
import deform.library.querypaths.paths.results.intersections.IIntersections;
import deform.library.querypaths.paths.results.project.BestProject;
import deform.library.querypaths.paths.results.project.BestProjectTup;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Tuple;
import deform.library.querypaths.util.Vec;

public abstract class QueryPath {

	protected BBox bbox;
	private ExtraMemo extraMemo; // additional memo properties that are not used
									// that often

	public abstract BBox makeBBox();

	public BBox getBBox() {
		if (bbox == null) {
			bbox = makeBBox();
		}
		return bbox;
	}

	public abstract Vec getAt(PathIndex t);

	public abstract Vec getTangentAt(PathIndex t);

	public abstract IIntersections intersection(QueryPath other);

	public abstract IIntersections intersectionLLine(Line lhs);

	public IIntersections intersectionLCurve(Curve lhs) {
		return intersectionLSplittable(lhs);
	}

	public abstract IIntersections intersectionLSet(ShapeSet lhs);

	public abstract IIntersections intersectionLSplittable(SplittablePath lhs);

	public BestProject project(Vec p) {
		;
		return project(Double.POSITIVE_INFINITY, p);
	}
	
	public abstract BestProject project(double best, Vec p);

	public BestProjectTup project(QueryPath other) {
		return project(Double.POSITIVE_INFINITY, other);
	}

	public abstract BestProjectTup project(double best, QueryPath other);

	public abstract BestProjectTup projectLLine(double best, Line lhs);

	public BestProjectTup projectLCurve(double best, Curve lhs) {
		return projectLSplittable(best, lhs);
	}

	public abstract BestProjectTup projectLSet(double best, ShapeSet lhs);

	public abstract BestProjectTup projectLSplittable(double best,
			SplittablePath lhs);

	protected double minDistTo(BBox br) {
		BBox bl = getBBox();
		double xDist = square(bl.xInterval.minDistance(br.xInterval));
		double yDist = square(bl.yInterval.minDistance(br.yInterval));
		return xDist + yDist;
	}

	public abstract int nrChildren();

	public abstract QueryPath getChild(int i);
	
	public double length() {
		normaliseToLength();
		return extraMemo.length;
	}

	private void makeExtraMemo() {
		if (extraMemo == null) {
			extraMemo = new ExtraMemo();
		}
	}

	public QueryPath normaliseToLength() {
		makeExtraMemo();
		if (extraMemo.lengthNormalized == null) {
			Tuple<QueryPath, Double> tp = normaliseToLength(0);
			extraMemo.lengthNormalized = tp.l;
			extraMemo.length = tp.r;
		}
		return extraMemo.lengthNormalized;
	}

	public abstract Tuple<QueryPath, Double> normaliseToLength(double prevLength);

	public abstract PathIndex minPathIndex();

	public abstract PathIndex maxPathIndex();

	public Vec getStartPoint() {
		return getAt(minPathIndex());
	}

	public Vec getEndPoint() {
		return getAt(maxPathIndex());
	}

	public boolean isClosed() {
		return getStartPoint().isEq(getEndPoint());
	}


}
