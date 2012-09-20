package deform.library.querypaths.paths.paths.simple.curve;

import java.util.Collections;
import java.util.List;

import deform.library.querypaths.paths.factory.QueryPathFactory;
import deform.library.querypaths.paths.paths.QueryPath;
import deform.library.querypaths.paths.paths.compound.Append;
import deform.library.querypaths.paths.paths.compound.ShapeSet;
import deform.library.querypaths.paths.paths.simple.Line;
import deform.library.querypaths.paths.paths.simple.SimplePath;
import deform.library.querypaths.paths.results.intersections.IIntersections;
import deform.library.querypaths.paths.results.project.BestProjectTup;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Interval;
import deform.library.querypaths.util.Tuple;
import deform.library.querypaths.util.Vec;





public abstract class Curve extends SimplePath {

	Tuple<SimplePath,SimplePath> simpler;

	public Curve(Interval tInterval,SimplePath lsimp, SimplePath rsimp) {
		super(tInterval);
		if(lsimp != null){
			this.simpler = new Tuple<SimplePath, SimplePath>(lsimp,rsimp);
		}
	}

	public Curve(Interval tInterval) {
		super(tInterval);
	}

	protected List<Double> xyRoots;

	abstract List<Double> getXYRoots();

	public void setXYRoots() {
		if (xyRoots == null) {
			xyRoots = getXYRoots();
			Collections.sort(xyRoots);
		}
	}

	public Tuple<Curve,Curve> makeMonotomous() {
		Tuple<Curve,Curve> result = split(xyRoots.get(0));
		result.l.xyRoots = Collections.EMPTY_LIST;
		result.r.xyRoots = xyRoots.subList(1, xyRoots.size());
		return result;
	}

	abstract Double findX(double x);

	abstract Double findY(double y);

	public double findTForX(double x) {
		Double d = findX(x);
		if (d == null) {
			System.out.printf("Cannot find %f %s\n", x, this);
		}
		return d;
	}

	public double findTForY(double y) {
		Double d = findY(y);
		if (d == null) {
			System.out.printf("Cannot find %f %s\n", y, this);
		}
		return d;
	}

	public boolean isMonotomous() {
		setXYRoots();
		return xyRoots.size() == 0;
	}
	
	
	public Tuple<QueryPath,QueryPath> splitSimpler(){
		return (Tuple)splitSimplerCurve();
	}

	public Tuple<SimplePath,SimplePath> splitSimplerCurve() {
		if (simpler == null) {
			setXYRoots();
			if (!isMonotomous()) {
				return (Tuple) makeMonotomous();
			} else {
				Tuple<Curve,Curve> sp = split();
				simpler = new Tuple<SimplePath,SimplePath>(
						sp.l.getSimplerApprox(),
						sp.r.getSimplerApprox());
			}
		}
		return simpler;
	}

	abstract SimplePath getSimplerApprox();

	abstract Tuple<Curve,Curve> split(double t);
	
	public Tuple<SimplePath,SimplePath> splitSimp(double t){
		Tuple<Curve, Curve> sp = split(t);
		return new Tuple<SimplePath, SimplePath>(sp.l.getSimplerApprox(), sp.r.getSimplerApprox());
	}

	Tuple<Curve,Curve> split() {
		return split(0.5);
	}

	@Override
	public 
		IIntersections intersection(
			QueryPath other) {
		return other.intersectionLCurve(this);
	}

	@Override
	public IIntersections intersectionLLine(
			Line lhs) {
		return super.intersectionLLine(lhs);
	}
	
	
	@Override
	public IIntersections intersectionLSet(
			ShapeSet lhs) {
		return lhs.intersectionLCurve(this).flip();
	}

	@Override
	public 
		BestProjectTup project(
			double best, QueryPath other) {
		return other.projectLCurve(best, this);
	}

	@Override
	public BestProjectTup projectLSet(double best,
			ShapeSet lhs) {
		return lhs.projectLCurve(best, this).flip();
	}
	
	@Override
	public Tuple<QueryPath, Double> normaliseToLength(
			double prevLength) {
		Tuple<SimplePath,SimplePath> sp = splitSimplerCurve();
		Tuple<QueryPath, Double> l = sp.l.normaliseToLength(prevLength);
		Tuple<QueryPath, Double> r = sp.r.normaliseToLength(l.r);
		return new Tuple<QueryPath, Double>(
			getWithNewSimpleAndInterval(
					(SimplePath)l.l, (SimplePath)r.l, new Interval(prevLength,r.r)),
			r.r);
	}

	abstract Curve getWithNewSimpleAndInterval(
			SimplePath l, SimplePath l2, Interval interval) ;
	
	
	
	public SimplePath getWithAdjustedStartPointMono(Vec v ) {
		Curve c = (Curve) getWithAdjustedStartPoint(v);
		c.xyRoots = Collections.EMPTY_LIST;
		return c;
	}

	public SimplePath getWithAdjustedEndPointMono(Vec v){
		Curve c = getWithAdjustedEndPoint(v);
		c.xyRoots = Collections.EMPTY_LIST;
		return c;
	}

	public abstract Curve getWithAdjustedEndPoint(Vec newEnd) ;

}
