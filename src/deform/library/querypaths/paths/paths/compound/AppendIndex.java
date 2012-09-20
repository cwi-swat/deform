package deform.library.querypaths.paths.paths.compound;

import deform.library.querypaths.paths.paths.PathIndex;

public class AppendIndex extends SplitIndex {

	public AppendIndex(SplitChoice choice, PathIndex next) {
		super(choice, next);
	}

	public String toString() {
		return "Append(" + choice + ")"
				+ (next == null ? "" : "," + next.toString());
	}

}
