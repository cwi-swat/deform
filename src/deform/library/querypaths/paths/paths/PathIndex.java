package deform.library.querypaths.paths.paths;

public abstract class PathIndex implements Comparable<PathIndex> {

	public final PathIndex next;

	public PathIndex(PathIndex next) {
		this.next = next;
	}

	public abstract double getSimple();

	public boolean isEq(PathIndex other) {
		return compareTo(other) == 0;
	}
}
