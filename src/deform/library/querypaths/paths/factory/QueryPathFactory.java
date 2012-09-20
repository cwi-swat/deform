package deform.library.querypaths.paths.factory;

import java.util.Arrays;
import java.util.List;

import deform.library.querypaths.paths.paths.QueryPath;
import deform.library.querypaths.paths.paths.compound.Append;
import deform.library.querypaths.paths.paths.compound.ClosedPath;
import deform.library.querypaths.paths.paths.compound.ShapeSet;
import deform.library.querypaths.paths.paths.simple.Line;
import deform.library.querypaths.paths.paths.simple.curve.CubicCurve;
import deform.library.querypaths.paths.paths.simple.curve.QuadCurve;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Interval;
import deform.library.querypaths.util.Vec;



public class QueryPathFactory {

	public static Line createLine(Vec start, Vec end) {
		return createLine(start, end, Interval.interval01);
	}

	public static Line createLine(Vec start, Vec end, Interval interval) {
		return new Line(start, end, interval);
	}

	public static QuadCurve createQuad(Vec p0, Vec p1, Vec p2) {
		return createQuad(p0, p1, p2, Interval.interval01);
	}

	public static QuadCurve createQuad(Vec p0, Vec p1, Vec p2, Interval interval) {
		return new QuadCurve(p0, p1, p2, interval);
	}

	public static CubicCurve createCubic(Vec p0, Vec p1, Vec p2, Vec p3) {
		return createCubic(p0, p1, p2, p3, Interval.interval01);
	}

	public static CubicCurve createCubic(Vec p0, Vec p1, Vec p2, Vec p3,
			Interval interval) {
		return new CubicCurve(p0, p1, p2, p3, interval);
	}

	public static QueryPath createAppendsL(List<QueryPath> paths) {
		return createAppends(paths);
	}
	
	public static QueryPath createAppends(List<QueryPath> paths) {
		if (paths.size() == 1) {
			return paths.get(0);
		}
		return Append.createAppends(paths);
	}

	public static QueryPath createAppends(QueryPath... paths) {
		return createAppends(Arrays.asList(paths));
	}

	public static ClosedPath createClosedPath(QueryPath p) {
		Vec begin = p.getStartPoint();
		Vec end = p.getEndPoint();
		if (!begin.isEq(end)) {
			p = createAppends(p, createLine(end, begin));
		}
		ClosedPath r = new ClosedPath(p);
		return r;
	}

	public static ClosedPath createClosedPath(List<QueryPath> paths) {
		return createClosedPath(createAppends(paths));

	}

	public static ClosedPath createClosedPath(QueryPath... paths) {
		return createClosedPath(Arrays.asList(paths));
	}

	public static QueryPath createSet(List<QueryPath> paths) {
		if (paths.size() == 1) {
			return paths.get(0);
		}
		return new ShapeSet(paths);
	}

	public static QueryPath createSet(QueryPath... paths) {
		return createSet(Arrays.asList(paths));
	}

}
