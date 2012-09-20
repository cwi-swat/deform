package deform.library.querypaths.paths.paths.compound;

import deform.library.querypaths.paths.paths.PathIndex;

public abstract class SplitIndex extends PathIndex {

	public static enum SplitChoice implements Comparable<SplitChoice> {
		Left, Right;
	}

	public final SplitChoice choice;

	public SplitIndex(SplitChoice choice, PathIndex next) {
		super(next);
		this.choice = choice;
	}

	public String toString() {
		return "Split(" + choice + ")"
				+ (next == null ? "" : "," + next.toString());
	}

	@Override
	public int compareTo(PathIndex o) {
		if (o instanceof SplitIndex) {
			SplitIndex pi = (SplitIndex) o;
			int cmp = this.choice.compareTo(pi.choice);
			if (cmp == 0) {
				return this.next.compareTo(o.next);
			} else {
				return cmp;
			}
		} else {
			throw new Error("Comparing incomparable pathindexes!" + this + " "
					+ o);
		}
	}

	@Override
	public double getSimple() {
		return next.getSimple();
	}

}
