package deform.library.querypaths.paths.paths.compound;

import deform.library.querypaths.paths.paths.PathIndex;

public class ClosedPathIndex extends PathIndex {

	public ClosedPathIndex(PathIndex next) {
		super(next);
	}

	@Override
	public int compareTo(PathIndex o) {
		if (o instanceof ClosedPathIndex) {
			return this.next.compareTo(o.next);
		} else {
			throw new Error("Comparing incomparable pathindexes!");
		}
	}

	@Override
	public double getSimple() {
		return next.getSimple();
	}

	public String toString() {
		return "Closed(" + next.toString() + ")";
	}

}
