package deform.library.querypaths.util;

public class Tuple<L, R> {
	public final L l;
	public final R r;

	public Tuple(L l, R r) {
		this.l = l;
		this.r = r;
	}

	public final Tuple<R, L> flip() {
		return new Tuple<R, L>(r, l);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			Tuple to = (Tuple) obj;
			return to.l == l && to.r == r;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return l.hashCode() ^ r.hashCode();
	}

	public String toString() {
		return String.format("<%s,%s>", l.toString(), r.toString());
	}
}
