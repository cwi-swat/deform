package deform.library.querypaths.paths.results.transformers;

import static deform.library.querypaths.paths.results.transformers.PITransformers.appendLeft;
import static deform.library.querypaths.paths.results.transformers.PITransformers.appendRight;
import static deform.library.querypaths.paths.results.transformers.PITransformers.setTrans;
import static deform.library.querypaths.paths.results.transformers.PITransformers.unit;

public class TupleTransformers {

	
	public static  PathIndexTupleTransformer unitTup =new PathIndexTupleTransformer(unit,unit);

	
	public static PathIndexTupleTransformer aleftLeft= left(appendLeft);
	
	public static PathIndexTupleTransformer aleftRight = left(appendRight);
	
	public static PathIndexTupleTransformer arightLeft = right(appendLeft);
	
	public static PathIndexTupleTransformer arightRight = right(appendRight);
	

	public static PathIndexTupleTransformer setLeft(int i){
		return new PathIndexTupleTransformer(setTrans(i),unit);
	}
	public static   PathIndexTupleTransformer setRight(int i){
		return new PathIndexTupleTransformer(unit,setTrans(i));
	}
	
	public static   PathIndexTupleTransformer right(IPathIndexTransformer tr){
		return new PathIndexTupleTransformer(unit, tr);
	}
	
	public static  PathIndexTupleTransformer left(IPathIndexTransformer tl){
		return new PathIndexTupleTransformer(tl, unit);
	}
}
