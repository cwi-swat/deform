package deform.library.querypaths.paths.results.project;

import deform.library.querypaths.paths.paths.PathIndex;
import deform.library.querypaths.paths.results.transformers.IPathIndexTransformer;




public class BestProject{

	public static final BestProject noBestYet = new BestProject();
	
	public final double distSquared;
	public final PathIndex t;
	
	public BestProject(){
		this.distSquared = Double.POSITIVE_INFINITY;
		this.t = null;
	}
	
	public BestProject(double distSquared, PathIndex t) {
		this.distSquared = distSquared;
		this.t = t;
	}
	
	public BestProject(double bestDistSqrd) {
		this.distSquared = bestDistSqrd;
		this.t = null;
	}

	public BestProject choose(BestProject rhs){
		if(distSquared == rhs.distSquared){
			if(t != null) {
				return this;
			} else {
				return rhs;
			}
		}
		if(distSquared < rhs.distSquared){
			return this;
		} else {
			return rhs;
		}
	}
	
	public
	BestProject transform(IPathIndexTransformer trans){
		if(trans.doesNothing() || t == null){
			return this;
		} else {
			return new BestProject(distSquared, trans.transform(t));
		}
	}
	
	public String toString(){
		return "BestProject("+distSquared + "," + t + ")";
	}
	
	
}