package deform.library.querypaths.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import deform.library.querypaths.Constants;


public class Util {

	public static <T> int floorBinarySearch(List<T> elems, T toFind,
			Comparator<T> comp) {
		int min = -1;
		int max = elems.size();
		while (max - min > 1) {
			int mid = (max + min) / 2;
			if (comp.compare(toFind, elems.get(mid)) < 0)
				max = mid;
			else
				min = mid;
		}
		return min;
	}

	public static int floorBinarySearch(double[] elems, double toFind) {
		int min = -1;
		int max = elems.length;
		while (max - min > 1) {
			int mid = (max + min) / 2;
			if (toFind - elems[mid] < 0)
				max = mid;
			else
				min = mid;
		}
		return min;
	}

	public static double max(double... ds) {
		double max = Double.NEGATIVE_INFINITY;
		for (double d : ds) {
			if (d > max) {
				max = d;
			}
		}
		return max;
	}

	public static double min(double... ds) {
		double max = Double.POSITIVE_INFINITY;
		for (double d : ds) {
			if (d < max) {
				max = d;
			}
		}
		return max;
	}

	public static double[] merge(double[] l, double[] r) {
		double[] res = new double[l.length + r.length];
		int nr = 0;
		int li = 0, ri = 0;
		while (li != l.length || ri != r.length) {
			double diff = l[li] - r[ri];
			if (diff < 0) {
				res[nr++] = l[li];
				li++;
			} else if (diff > 0) {
				res[nr++] = r[ri];
				ri++;
			} else {
				res[nr++] = r[ri];
				li++;
				ri++;
			}
		}
		if (nr != res.length) {
			return Arrays.copyOf(res, nr);
		} else {
			return res;
		}
	}

	public static <T extends Comparable<T>> List<T> merge(List<T> l, List<T> r) {
		if (r.isEmpty()) {
			return l;
		} else if (l.isEmpty()) {
			return r;
		}
		List<T> res = new ArrayList<T>();
		int li = 0, ri = 0;
		while (li != l.size() || ri != r.size()) {
			double diff;
			if (li == l.size()) {
				diff = 1;
			} else if (ri == r.size()) {
				diff = -1;
			} else {
				diff = l.get(li).compareTo(r.get(ri));
			}
			if (diff < 0) {
				res.add(l.get(li));
				li++;
			} else if (diff > 0) {
				res.add(r.get(ri));
				ri++;
			} else {
				res.add(l.get(li));
				li++;
				ri++;
			}
		}
		return res;
	}

	public static double min4(double a, double b, double c, double d) {
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	public static double max4(double a, double b, double c, double d) {
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	public static int mod(int a, int b) {
		int res = a % b;
		if (res < 0) {
			return b + a;
		} else {
			return res;
		}
	}

	public static double mod(double a, int b) {
		double res = a % b;
		if (res < 0) {
			return b + a;
		} else {
			return res;
		}
	}

	public static List<Double> findQuadraticPolynomialRoots(double a, double b,
			double c) {
		if (Math.abs(a) <= Constants.MAX_ERROR) {
			List<Double> res = new ArrayList<Double>(1);
			if (b == 0) {
				if (c == 0) {
					res.add(0.0);
				}
				return res;
			}
			res.add(-c / b);
			return res;
		}
		double discriminant = b * b - 4 * a * c;
		if (discriminant < 0) {
			return new ArrayList<Double>(0);
		} else if (discriminant == 0) {
			double sol = -b / (2 * a);
			List<Double> res = new ArrayList<Double>(1);
			res.add(sol);
			return res;
		} else {
			List<Double> res = new ArrayList<Double>(2);
			double ta = 2 * a;
			discriminant = Math.sqrt(discriminant);
			double psol = (-b + discriminant) / ta;
			double nsol = (-b - discriminant) / ta;
			if (psol > nsol) {
				res.add(nsol);
				res.add(psol);
			} else {
				res.add(psol);
				res.add(nsol);
			}
			return res;
		}
	}

	public static double clamp(double t) {
		if (t > 1) {
			return 1;
		} else if (t < 0) {
			return 0;
		} else {
			return t;
		}
	}

	public static boolean intervalsOverlap(double as, double ae, double bs,
			double be) {
		if (as > ae) {
			double tmp = as;
			as = ae;
			ae = tmp;
		}
		if (bs > be) {
			double tmp = bs;
			bs = be;
			be = tmp;
		}
		return !(be < as || bs > ae);
	}

	public static void transitiveClosure(boolean[][] predicate) {
		for (int k = 0; k < predicate.length; k++) {
			for (int i = 0; i < predicate.length; i++) {
				for (int j = 0; j < predicate.length; j++) {
					predicate[i][j] = predicate[i][j]
							|| (predicate[i][k] && predicate[k][j]);
				}
			}
		}

	}

	public static List<Integer> natListTill(int size) {
		List<Integer> result = new ArrayList<Integer>(size);
		for (int i = 0; i < size; i++) {
			result.add(i);
		}
		return result;
	}

	//
	// public static List<STuple<Integer>> natPairs(int sizea, int sizeb){
	// List<STuple<Integer>> result = new
	// ArrayList<STuple<Integer>>(sizea*sizeb);
	// for(int i = 0; i < sizea; i++){
	// for(int j = 0; j < sizeb; j++){
	// result.add(new STuple<Integer>(i, j));
	// }
	// }
	// return result;
	// }

	public static double[] reverse(double[] splitsX) {
		double[] result = new double[splitsX.length];
		for (int i = 0; i < splitsX.length; i++) {
			result[splitsX.length - 1 - i] = splitsX[i];
		}
		return result;
	}

	public static List<Integer> getChoices(List<Integer> sortedY,
			List<Integer> choices, int max) {
		boolean[] choicesb = new boolean[max];
		for (int i : choices) {
			choicesb[i] = true;
		}
		List<Integer> result = new ArrayList<Integer>();
		for (int i : sortedY) {
			if (choicesb[i]) {
				result.add(i);
			}
		}
		return result;

	}

	public static double square(double d) {
		return d * d;
	}

	public static double divD(int start, int length) {
		return (double) start / (double) length;
	}

}
