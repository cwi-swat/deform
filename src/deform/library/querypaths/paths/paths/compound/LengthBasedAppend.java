package deform.library.querypaths.paths.paths.compound;

import deform.library.querypaths.paths.paths.QueryPath;
import deform.library.querypaths.paths.paths.SimplyIndexedPath;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Interval;
import deform.library.querypaths.util.Tuple;
import deform.library.querypaths.util.Vec;

public class LengthBasedAppend extends SimplyIndexedPath {

	public final SimplyIndexedPath left;
	public final SimplyIndexedPath right;

	public LengthBasedAppend(SimplyIndexedPath left, SimplyIndexedPath right,
			Interval tInterval) {
		super(tInterval);
		this.left = left;
		this.right = right;
	}

	@Override
	public BBox makeBBox() {
		return left.getBBox().union(right.getBBox());
	}

	private boolean isLeft(double t) {
		return t < left.tInterval.high;
	}

	@Override
	public Vec getAtSimply(double t) {
		if (isLeft(t)) {
			return left.getAtSimply(t);
		} else {
			return right.getAtSimply(t);
		}
	}

	@Override
	public Vec getTangentAtSimply(double t) {
		if (isLeft(t)) {
			return left.getTangentAtSimply(t);
		} else {
			return right.getTangentAtSimply(t);
		}
	}

	@Override
	public Tuple<QueryPath, QueryPath> splitSimpler() {
		return new Tuple<QueryPath, QueryPath>(left, right);
	}

	@Override
	public int nrChildren() {
		return 2;
	}

	@Override
	public QueryPath getChild(int i) {
		return i == 0 ? left : right;
	}

	@Override
	public LengthBasedAppend getWithAdjustedStartPoint(Vec newStartPoint) {
		return new LengthBasedAppend(
				left.getWithAdjustedStartPoint(newStartPoint), right, tInterval);
	}

	

	@Override
	public Tuple<QueryPath, Double> normaliseToLength(double prevLength) {
		return new Tuple<QueryPath, Double>(this, right.tInterval.high);
	}

}
