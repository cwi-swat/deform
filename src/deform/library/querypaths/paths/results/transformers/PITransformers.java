package deform.library.querypaths.paths.results.transformers;

import deform.library.querypaths.paths.paths.PathIndex;
import deform.library.querypaths.paths.paths.compound.AppendIndex;
import deform.library.querypaths.paths.paths.compound.ClosedPathIndex;
import deform.library.querypaths.paths.paths.compound.SetIndex;
import deform.library.querypaths.paths.paths.compound.SplitIndex;

public class PITransformers{

	public static IPathIndexTransformer unit = new UnitPITransformer();
	public static IPathIndexTransformer appendLeft = new AppendPITransformer(SplitIndex.SplitChoice.Left);
	public static IPathIndexTransformer appendRight = new AppendPITransformer(SplitIndex.SplitChoice.Right);
	public static IPathIndexTransformer closedT(PathIndex min, PathIndex max) {
		return new ClosedPITransformer(min,max);
	}
	public static IPathIndexTransformer setTrans(int i){
		return new SetPITransformer(i);
	}
	
	public static class UnitPITransformer implements IPathIndexTransformer {
		public PathIndex transform(PathIndex p){
			return p;
		}
		public boolean doesNothing(){
			return true;
		}
	}
	
	
	public static class AppendPITransformer implements IPathIndexTransformer {
		
		final SplitIndex.SplitChoice choice;
		
		public AppendPITransformer(SplitIndex.SplitChoice choice) {
			this.choice = choice;
		}
		public AppendIndex transform(PathIndex p){
			return new AppendIndex(choice, p);
		}
		public boolean doesNothing(){
			return false;
		}
	}
	
	public static class SetPITransformer implements IPathIndexTransformer {
		
		final int choice;
		
		public SetPITransformer(int choice) {
			this.choice = choice;
		}
		public SetIndex transform(PathIndex p){
			return new SetIndex(choice, p);
		}
		public boolean doesNothing(){
			return false;
		}
	}
	
	public static class ClosedPITransformer implements IPathIndexTransformer{
		// this transformer transforms the maximum pathindex to the minimum pathindex
		// in addidition to prepending closed,
		// the reason for this is that we need intersection involving 
		// the same pathindex point in groupintersections
		// however the min and max of a closedpath are actually the same
		// point. Thus to save confusing code, we map the max to the min here
		// 
		final PathIndex min, max;
		
		public ClosedPITransformer(PathIndex min, PathIndex max) {
			this.min = min;
			this.max = max;
		}
		public ClosedPathIndex transform(PathIndex p){
			if(p == null){
				return null;
			}
			if(p.isEq(max)){
				p = min;
			}
			return new ClosedPathIndex(p);
		}
		public boolean doesNothing(){
			return false;
		}
	}
}
