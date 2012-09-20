package deform.library.querypaths.paths.results.transformers;

import deform.library.querypaths.paths.paths.PathIndex;

public interface IPathIndexTransformer {
	
	PathIndex transform(PathIndex p);
	boolean doesNothing();

}
