package deform.library.querypaths.paths.paths.compound;

import deform.library.querypaths.paths.paths.PathIndex;
import deform.library.querypaths.paths.paths.QueryPath;
import deform.library.querypaths.paths.paths.SplittablePath;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Tuple;
import deform.library.querypaths.util.Vec;

public abstract class CompoundSplittablePath extends SplittablePath {

	public final QueryPath left;
	public final QueryPath right;

	public CompoundSplittablePath(QueryPath left, QueryPath right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public BBox makeBBox() {
		return left.getBBox().union(right.getBBox());
	}

	@Override
	public Vec getAt(PathIndex t) {
		switch (((SplitIndex) t).choice) {
		case Left:
			return left.getAt(t.next);
		case Right:
			return right.getAt(t.next);
		}
		throw new Error("Unkown split choice:" + this + "\n");
	}

	@Override
	public Vec getTangentAt(PathIndex t) {
		switch (((SplitIndex) t).choice) {
		case Left:
			return left.getTangentAt(t.next);
		case Right:
			return right.getTangentAt(t.next);
		}
		throw new Error("Unkown split choice:" + this + "\n");
	}

	@Override
	public Tuple<QueryPath, QueryPath> splitSimpler() {
		return new Tuple<QueryPath, QueryPath>(left, right);
	}

	public int nrChildren() {
		return 2;
	}

	public QueryPath getChild(int i) {
		return i == 0 ? left : right;
	}

	public PathIndex minPathIndex() {
		return getLeftTransformer().transform(left.minPathIndex());
	}

	public PathIndex maxPathIndex() {
		return getRightTransformer().transform(right.maxPathIndex());
	}
}
