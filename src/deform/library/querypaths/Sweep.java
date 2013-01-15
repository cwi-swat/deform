package deform.library.querypaths;

import deform.Point;
import deform.library.querypaths.paths.paths.PathIndex;
import deform.library.querypaths.paths.paths.QueryPath;
import deform.library.querypaths.paths.results.project.BestProject;
import deform.library.querypaths.util.Vec;

public class Sweep  {

	QueryPath path;
	SweepTo to;
	PathIndex prevT;
	Vec prev;
	double length;

	public Sweep(QueryPath p){
		this.path = p.normaliseToLength();
		this.to = new SweepTo(path);
		this.length = to.lengths.get(to.lengths.size()-1);
	}
	
	public Vec to(Point from) {
		from = from.$times(length);
		return to.to(new Vec(from.x(),from.y()));
	}

	public Vec from(Point top) {
		Vec to = new Vec(top.x(),top.y());
		double bestd = Double.POSITIVE_INFINITY;
		if(prevT != null && prev.distanceSquared(to) < 10){
			bestd = path.getAt(prevT).distanceSquared(to);
		}
		BestProject best =path.project(bestd,to);
		prevT = best.t;
		prev = to;
		return new Vec(best.t.getSimple()/length,Math.sqrt(best.distSquared)/length);
		
	}

}
