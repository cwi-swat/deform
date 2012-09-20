package deform.library.querypaths.paths.paths.simple.curve;

import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import deform.library.querypaths.Constants;
import deform.library.querypaths.paths.factory.QueryPathFactory;
import deform.library.querypaths.paths.paths.simple.SimplePath;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Interval;
import deform.library.querypaths.util.Tuple;
import deform.library.querypaths.util.Util;
import deform.library.querypaths.util.Vec;




public class QuadCurve extends Curve{

	
	public final Vec p0,p1,p2;
	
	public QuadCurve(Vec p0,Vec p1, Vec p2, Interval tInterval){
		this(p0,p1,p2,tInterval,null,null);
	}
	
	public QuadCurve(Vec p0,Vec p1, Vec p2, Interval tInterval,SimplePath lsimp, SimplePath rsimp) {
		super(tInterval,lsimp,rsimp);
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	private double getRoot(double x0, double x1, double x2){
		return (x0 - x1)/(x2 - 2*x1 + x0);
	}
	
	@Override
	List<Double> getXYRoots() {
		double rx = getRoot(p0.x, p1.x, p2.x);
		double ry = getRoot(p0.y, p1.y, p2.y);
		List<Double> result = new ArrayList<Double>();
		if(rx > 0 && rx < 1){
			result.add(rx);
		}
		if(ry > 0 && ry < 1){
			result.add(ry);
		}
		return result;
	}
	
	public String toString(){
		return String.format("Quad %s %s %s",p0,p1,p2);
	}

	Double find(double x0,double x1, double x2, double x){
		double a= x2 -2*x1 + x0;
		double b = 2 * (x1 - x0);
		double c = x0 - x;
		List<Double> roots = Util.findQuadraticPolynomialRoots(a, b, c);
		for(double d : roots){
			if(d >= 0 && d <= 1){
				return d;
			}
		}
		return null;
	}
	
	@Override
	Double findX(double x) {
		return find(p0.x, p1.x, p2.x, x);
	}

	@Override
	Double findY(double y) {
		return find(p0.y, p1.y, p2.y, y);
	}

	@Override
	public
	Tuple<Curve,Curve> split(double t) {
		Vec cl = p0.interpolate(t, p1);
		Vec cr = p1.interpolate(t, p2);
		Vec cm = cl.interpolate(t, cr);
		Tuple<Interval,Interval> st = tInterval.split();
		return new Tuple<Curve,Curve>( new QuadCurve(p0,cl,cm,st.l),
				 new QuadCurve(cm,cr,p2,st.r));
	}



	@Override
	protected
	SimplePath getSimplerApprox() {
		SimplePath l = QueryPathFactory.createLine(p0, p2,tInterval);
		if(getAtLocal(0.5).distanceSquared(l.getAtLocal(0.5)) <= Constants.HALF_MAX_ERROR_POW2){
			return l;
		} else {
			return this;
		}
	}

	@Override
	public Vec getAtLocal(double t) {
		double ot = 1.0 -t;
		double t2 = t * t;
		double ot2 = ot * ot;
		return new Vec(p0.x * ot2 + 2 * ot * t * p1.x + t2 * p2.x,
				p0.y * ot2 + 2 * ot * t * p1.y + t2 * p2.y);
	}

	@Override
	public Vec getTangentAtLocal(double t) {
		double ot =2*(1-t);
		double t21 = 2*t;
		return new Vec(ot * (p1.x - p0.x) + t21 * (p2.x - p1.x),
				ot * (p1.y - p0.y) + t21 * (p2.y - p1.y));
	}

	@Override
	public QuadCurve getWithAdjustedStartPoint(Vec newStartPoint) {
		return QueryPathFactory.createQuad(newStartPoint,p1,p2,tInterval);
	}
	
	@Override
	public QuadCurve getWithAdjustedEndPoint(Vec newEnd) {
		return QueryPathFactory.createQuad(p0,p1,newEnd,tInterval);
	}

	@Override
	public
	BBox makeBBox() {
		if(isMonotomous()){
			return BBox.from2Points(p0,p2);
		}
		return BBox.from3Points(p0,p1,p2);
	}

	@Override
	public int awtCurSeg(float[] coords, int x, int y) {
		coords[0] = (float)p1.x - x;
		coords[1] = (float)p1.y - y;
		coords[2] = (float)p2.x - x;
		coords[3] = (float)p2.y - y;
		return PathIterator.SEG_QUADTO;
	}


	@Override
	Curve getWithNewSimpleAndInterval(SimplePath lsimp,
			SimplePath rsimp, Interval interval) {
		return new QuadCurve(p0, p1, p2,  interval, lsimp, rsimp);
	}

	
	double findFast(double x0,double x1, double x2, double x){
		double a= x2 -2*x1 + x0;
		double b = 2 * (x1 - x0);
		double c = x0 - x;
		List<Double> roots = Util.findQuadraticPolynomialRoots(a, b, c);
		for(double d : roots){
			if(d >= 0 && d <= 1){
				return d;
			}
		}
		return Math.abs(x-x2) < Math.abs(x-x0) ? 1.0 : 0.0;
	}

	@Override
	public double findXFast(double x) {
		return findFast(p0.x, p1.x, p2.x, x);
	}
	
	@Override
	public double findYFast(double y) {
		return findFast(p0.y, p1.y, p2.y, y);
	}

}
