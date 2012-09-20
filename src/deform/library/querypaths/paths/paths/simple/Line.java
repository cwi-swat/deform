package deform.library.querypaths.paths.paths.simple;

import static deform.library.querypaths.util.Interval.interval01;

import java.awt.geom.PathIterator;

import deform.library.querypaths.paths.factory.QueryPathFactory;
import deform.library.querypaths.paths.paths.QueryPath;
import deform.library.querypaths.paths.paths.SplittablePath;
import deform.library.querypaths.paths.paths.compound.ShapeSet;
import deform.library.querypaths.paths.results.intersections.IIntersections;
import deform.library.querypaths.paths.results.intersections.Intersections;
import deform.library.querypaths.paths.results.project.BestProject;
import deform.library.querypaths.paths.results.project.BestProjectTup;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Interval;
import deform.library.querypaths.util.Tuple;
import deform.library.querypaths.util.Util;
import deform.library.querypaths.util.Vec;



public class Line extends SimplePath {

	public final Vec start, dir, end;

	public Line(Vec start, Vec end, Interval tInterval) {
		super(tInterval);
		this.start = start;
		this.dir = end.sub(start);
		this.end = end;
	}

	public Tuple<Double, Double> intersection(Line r) {
		Vec cross = start.sub(r.start);
		double solDiv = (r.dir.y * dir.x - r.dir.x * dir.y);
		double sol = (r.dir.x * cross.y - r.dir.y * cross.x) / solDiv;
		if (sol >= 0 && sol <= 1) {
			double sol2 = (dir.x * cross.y - dir.y * cross.x) / solDiv;
			if (sol2 >= 0 && sol2 <= 1) {
				return new Tuple<Double, Double>(sol, sol2);
			}
		}
		return null;
	}

	public double distanceSquared(Vec p) {
		return getAtLocal(closestT(p)).distanceSquared(p);
	}

	public double closestT(Vec p) {
		return Util.clamp(closestTAll(p));
	}

	double closestTAll(Vec p) {
		return -(dir.y * start.y + dir.x * start.x - dir.y * p.y - dir.x * p.x)
				/ (dir.x * dir.x + dir.y * dir.y);
	}

	public double distanceSquared(Line r) {
		Tuple<Double, Double> cl = closestTs(r);
		return getAtLocal(cl.l).distanceSquared(r.getAtLocal(cl.r));
	}

	public Tuple<Double, Double> closestTs(Line r) {
		Tuple<Double, Double> res = intersection(r);
		if (res == null) {
			double prs = r.closestT(start);
			double pre = r.closestT(end);
			double pls = closestT(r.start);
			double ple = closestT(r.end);
			double drs = start.distanceSquared(r.getAtLocal(prs));
			double dre = end.distanceSquared(r.getAtLocal(pre));
			double dls = r.start.distanceSquared(getAtLocal(pls));
			double dle = r.end.distanceSquared(getAtLocal(ple));

			double min = Util.min4(drs, dre, dls, dle);
			if (min == drs) {
				return new Tuple<Double, Double>(0.0, prs);
			} else if (min == dre) {
				return new Tuple<Double, Double>(1.0, prs);
			} else if (min == dls) {
				return new Tuple<Double, Double>(pls, 0.0);
			} else {
				return new Tuple<Double, Double>(ple, 1.0);
			}
		} else {
			return res;
		}
	}

	public Vec getAtLocal(double t) {
		return start.add(dir.mul(t));
	}

	double getXAt(double t) {
		return start.x + dir.x * t;
	}

	double getYAt(double t) {
		return start.y + dir.y * t;
	}

	public Vec getTangentAtLocal(double t) {
		return dir;
	}

	private Double find(double sx, double dx, double xf) {
		if (dx == 0) {
			return null;
		} else {
			double res = findAll(sx, dx, xf);
			if (res >= 0 && res <= 1) {
				return res;
			} else {
				return null;
			}
		}
	}

	private double findAll(double sx, double dx, double xf) {
		return (xf - sx) / dx;
	}

	public double getTAtY(double y) {
		return (y - start.y) / dir.y;
	}

	public double getTAtX(double x) {
		return (x - start.x) / dir.x;
	}

	public Double findX(double x) {
		return find(start.x, dir.x, x);
	}

	public Double findY(double y) {
		return find(start.y, dir.y, y);
	}

	public double findXAll(double x) {
		return findAll(start.x, dir.x, x);
	}

	public double findYAll(double y) {
		return findAll(start.y, dir.y, y);
	}

	public double findAll(Vec v) {
		if (dir.x == 0) {
			return findYAll(v.y);
		} else {
			return findXAll(v.x);
		}
	}

	@Override
	public String toString() {
		return String.format("Line %s -> %s", start.toString(), end.toString());
	}

	public double findTForX(double x) {
		return getTAtX(x);
	}

	@Override
	public IIntersections intersection(QueryPath other) {
		return other.intersectionLLine(this);
	}

	@Override
	public IIntersections intersectionLLine(Line lhs) {
		if (dir.tanToNormal().dot(lhs.dir) == 0) {
			if (isOnSameLine(lhs)) {
				Interval intv = new Interval(findAll(lhs.start),
						findAll(lhs.end)).intersection(interval01);
				if (!intv.isEmpty()) {
					Vec v = getAtLocal(intv.low);
					IIntersections res = makeIntersectionResult(lhs,
							lhs.findAll(getAtLocal(intv.low)), intv.low);
					if (!intv.isSinglePoint()) {
						res = res.append(makeIntersectionResult(lhs,
								lhs.findAll(getAtLocal(intv.high)), intv.high));
					}
					return res;
				}
			}
			return Intersections.NoIntersections;
		} else {
			Tuple<Double, Double> res = lhs.intersection(this);
			if (res != null) {
				return makeIntersectionResult(lhs, res.l, res.r);
			} else {
				return Intersections.NoIntersections;
			}
		}
	}

	private boolean isOnSameLine(Line lhs) {
		return getAtLocal(findAll(lhs.start)).isEq(lhs.start);
	}

	@Override
	public IIntersections intersectionLSet(ShapeSet lhs) {
		return lhs.intersectionLLine(this).flip();
	}

	@Override
	public IIntersections intersectionLSplittable(SplittablePath lhs) {
		return lhs.intersectionLLine(this).flip();
	}

	public boolean overlaps(BBox b) {
		return getBBox().overlaps(b);
	}

	@Override
	public BestProject project(double best, Vec p) {
		double t = closestT(p);
		if (Double.isNaN(t)) {
			System.out.println(this);
		}
		double dist = getAtLocal(t).distanceSquared(p);
		return new BestProject(dist, makeGlobalPathIndexFromLocal(t));
	}

	public BestProjectTup projectLPoint(SimplePath lhs, double lt, double x,
			double y) {
		Vec v = new Vec(x, y);
		double tr = closestT(v);
		double dist = getAtLocal(tr).distanceSquared(v);
		return makeBestProject(dist, lhs, lt, tr);
	}

	public double minDistSquaredTo(BBox b) {
		return minDistTo(b);
		// STuple<Interval> tIntervals = getTIntervalsBBox(b);
		// if(Interval.overlap(tIntervals.l,tIntervals.r)){
		// return 0;
		// } else {
		// double res = Double.POSITIVE_INFINITY;
		// if(tIntervals.l != emptyInterval){
		// Interval xyInterval =
		// new Interval( getYAt(tIntervals.l.low),
		// getYAt(tIntervals.l.high));
		// res = min(res, square(xyInterval.minDistance(b.yInterval)));
		// }
		// if(tIntervals.r != emptyInterval){
		// Interval yxInterval =
		// new Interval( getYAt(tIntervals.r.low),
		// getYAt(tIntervals.r.high));
		// res = min(res, square(yxInterval.minDistance(b.xInterval)));
		// }
		// res = min(res, distanceSquared(b.getLeftUp()));
		// res = min(res, distanceSquared(b.getLeftDown()));
		// res = min(res, distanceSquared(b.getRightUp()));
		// res = min(res, distanceSquared(b.getRightDown()));
		// return res;
		// }
	}

	@Override
	public BestProjectTup project(double best, QueryPath other) {
		return other.projectLLine(best, this);
	}

	@Override
	public BestProjectTup projectLLine(double best, Line lhs) {
		Tuple<Double, Double> tp = lhs.closestTs(this);
		double dist = lhs.getAtLocal(tp.l).distanceSquared(getAtLocal(tp.r));
		if (dist < best) {
			return makeBestProject(dist, lhs, tp.l, tp.r);
		} else {
			return BestProjectTup.noBestYet;
		}
	}

	@Override
	public BestProjectTup projectLSet(double best, ShapeSet lhs) {
		return lhs.projectLLine(best, this).flip();
	}

	public BestProjectTup projectLSplittable(double best, SplittablePath lhs) {
		return lhs.projectLLine(best, this).flip();
	}

	@Override
	public BBox makeBBox() {
		return BBox.from2Points(start, end);
	}

	@Override
	public Line getWithAdjustedStartPoint(Vec newStartPoint) {
		return QueryPathFactory.createLine(newStartPoint, getEndPoint(), tInterval);
	}

	@Override
	public Tuple<QueryPath, QueryPath> splitSimpler() {
		throw new Error("Cannot make" + this + "simpler!");
	}

	public Vec getStartPoint() {
		return start;
	}

	public Vec getEndPoint() {
		return end;
	}

	public double length() {
		return getStartPoint().distance(getEndPoint());
	}

	@Override
	public int awtCurSeg(float[] coords, int x, int y) {
		Vec end = getEndPoint();
		coords[0] = (float) end.x - x;
		coords[1] = (float) end.y - y;
		return PathIterator.SEG_LINETO;
	}


	@Override
	public Tuple<QueryPath, Double> normaliseToLength(double prevLength) {
		double nl = prevLength + length();
		return new Tuple<QueryPath, Double>(QueryPathFactory.createLine(getStartPoint(),
				getEndPoint(), new Interval(prevLength, nl)), nl);
	}

	@Override
	public Tuple<SimplePath, SimplePath> splitSimp(double t) {
		Vec mid = getAtLocal(t);
		Line left = QueryPathFactory.createLine(start, mid, new Interval(
				tInterval.low, t));
		Line right = QueryPathFactory.createLine(mid, end, new Interval(t,
				tInterval.high));
		return new Tuple<SimplePath, SimplePath>(left, right);
	}

	@Override
	public Line getWithAdjustedEndPointMono(Vec v) {
		return QueryPathFactory.createLine(start, v, tInterval);
	}

	@Override
	public double findXFast(double splitPoint) {
		return getTAtX(splitPoint);
	}

	@Override
	public SimplePath getWithAdjustedStartPointMono(Vec v) {
		return new Line(v, end, tInterval);
	}

	@Override
	public double findYFast(double splitPoint) {
		return getTAtY(splitPoint);
	}

}
