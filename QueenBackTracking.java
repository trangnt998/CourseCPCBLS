package localsearch.exercise;

public class QueenBackTracking{
	int N;
	int [] X;
	boolean found;
	
	private void printSolution() {
		found = true;
		for(int i = 0; i < N; i++) {
			System.out.print(X[i] + " ");
		}
		System.out.println();
	}
	
	private boolean check(int v, int k) {
		for(int i = 0; i < k; i++) {
			if(X[i] == v) return false;
			if(X[i] + i == v + k) return false;
			if(X[i] - i == v - k) return false;
		}
		return true;
	}
	
	private void TRY(int k) {
		
		for(int v = 0; v < N; v++) {
			if(check(v,k) == true) {
				
				X[k] = v;
				
				if(k == N-1) printSolution();
				else
					TRY(k + 1);
			}
		}
	}
	
	private void solve(int N) {
		this.N = N;
		X = new int [N];
		found = false;
		TRY(0);
	}
	
	public static void main(String [] args) {
		QueenBackTracking q = new QueenBackTracking();
		q.solve(8);
	}
}
