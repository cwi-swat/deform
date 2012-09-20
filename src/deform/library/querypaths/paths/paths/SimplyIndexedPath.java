package deform.library.querypaths.paths.paths;

import static deform.library.querypaths.paths.results.transformers.TupleTransformers.unitTup;
import deform.library.querypaths.paths.paths.simple.SimplePathIndex;
import deform.library.querypaths.paths.results.transformers.IPathIndexTransformer;
import deform.library.querypaths.paths.results.transformers.PITransformers;
import deform.library.querypaths.paths.results.transformers.PathIndexTupleTransformer;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Interval;
import deform.library.querypaths.util.Vec;

public abstract class SimplyIndexedPath extends SplittablePath {

	public Interval tInterval;

	public SimplyIndexedPath(Interval tInterval) {
		this.tInterval = tInterval;
	}

	public abstract Vec getAtSimply(double t);

	public abstract Vec getTangentAtSimply(double t);

	@Override
	public Vec getAt(PathIndex p) {
		return getAtSimply(((SimplePathIndex) p).t);
	}

	@Override
	public Vec getTangentAt(PathIndex p) {
		return getTangentAtSimply(((SimplePathIndex) p).t);
	}

	@Override
	public IPathIndexTransformer getLeftTransformer() {
		return PITransformers.unit;
	}

	@Override
	public IPathIndexTransformer getRightTransformer() {
		return PITransformers.unit;
	}

	@Override
	public PathIndexTupleTransformer getLeftLeftTransformer() {
		return unitTup;
	}

	@Override
	public PathIndexTupleTransformer getLeftRightTransformer() {
		return unitTup;
	}

	@Override
	public PathIndexTupleTransformer getRightLeftTransformer() {
		return unitTup;
	}

	@Override
	public PathIndexTupleTransformer getRightRightTransformer() {
		return unitTup;
	}


	public abstract SimplyIndexedPath getWithAdjustedStartPoint(
			Vec newStartPoint);

	public SimplePathIndex minPathIndex() {
		return new SimplePathIndex(tInterval.low);
	}

	public SimplePathIndex maxPathIndex() {
		return new SimplePathIndex(tInterval.high);
	}

}
