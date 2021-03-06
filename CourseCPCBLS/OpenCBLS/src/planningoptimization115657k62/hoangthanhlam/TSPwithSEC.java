package planningoptimization115657k62.hoangthanhlam;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class TSPwithSEC {

	static {
		System.loadLibrary("jniortools");
	}
	
	int N = 5;
	int[][] c = {{0,4,2,5,6},
			{2,0,5,2,7},
			{1,2,0,6,3},
			{7,5,8,0,3},
			{1,2,4,3,0}};
	MPSolver solver;
	MPVariable[][] X;
	double inf = java.lang.Double.POSITIVE_INFINITY;
	
	private int findNext(int s) {
		for (int i = 0; i < N; i++) {
			if (i != s && X[s][i].solutionValue() > 0) {
				return i;
			}
		}
		return -1;
	}
	
	public ArrayList<Integer> extractCycle(int s) {
		ArrayList<Integer> L = new ArrayList<Integer>();
		int x = s;
		while (true) {
			L.add(x);
			x = findNext(x);
			int r = -1;
			for (int i = 0; i < L.size(); i++) {
				if (L.get(i) == x) {
					r = i;
					break;
				}
			}
			if (r != -1) {
				ArrayList<Integer> rL = new ArrayList<Integer>();
				for (int i = r; i < L.size(); i++) {
					rL.add(L.get(i));
				}
				return rL;
			}
		}
	}
	
	private void creatPro() {
		solver = new MPSolver("TSP with SEC", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		
		X = new MPVariable[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i != j) {
					X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "][" + j + "]");
				} else {
					X[i][j] = solver.makeIntVar(0, 0, "X[" + i + "][" + j + "]");
				}
			}
		}
		
		MPObjective obj = solver.objective();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				obj.setCoefficient(X[i][j], c[i][j]);
			}
		}
		
		for (int i = 0; i < N; i++) {
			MPConstraint c1 = solver.makeConstraint(0, 1);
			for (int j = 0; j < N; j++) {
				c1.setCoefficient(X[i][j], 1);
			}
			
			MPConstraint c2 = solver.makeConstraint(0, 1);
			for (int j = 0; j < N; j++) {
				c2.setCoefficient(X[j][i], 1);
			}
		}
	}
	
	private void creatSEC(HashSet<ArrayList<Integer>> S) {
		for (ArrayList<Integer> C : S) {
			MPConstraint sc = solver.makeConstraint(0, C.size() - 1);
			for (int i : C) {
				for (int j : C) {
					if (i != j) {
						sc.setCoefficient(X[i][j], 1);
					}
				}
			}
		}
	}
	
	public void solveDynamic() {
		HashSet<ArrayList<Integer>> S = new HashSet<ArrayList<Integer>>();
		boolean mark[] = new boolean[N];
		boolean found = false;
		while (!found) {
			creatPro();
			creatSEC(S);
			final ResultStatus rs = solver.solve();
			if (rs != ResultStatus.OPTIMAL) {
				System.err.println("The problem does not have an optimal solution.");
				return;
			}
			System.out.println("obj = " + solver.objective().value());
			
			for (int i = 0; i < N; i++) mark[i] = false;
			for (int s = 0; s < N; s++) {
				if (!mark[s]) {
					ArrayList<Integer> C = extractCycle(s);
					if (C.size() < N) {
						System.out.print("SubTour Deteted, C = ");
						for (int i: C) {
							System.out.print(i + " ");
						}
						System.out.println();
						S.add(C);
						for (int i: C) mark[i] = true;
					}
					else {
						System.out.println("Global tour deteted, solution found.");
						found = true;
						break;
					}
				}
			}
		}
		ArrayList<Integer> tour = extractCycle(0);
		for (int i = 0; i < tour.size(); i++) {
			System.out.print(tour.get(i) + " -> ");
		}
		System.out.println(tour.get(0));
	}
	
	public static void main(String[] args) {
		TSPwithSEC app = new TSPwithSEC();
		app.solveDynamic();
	}

}
