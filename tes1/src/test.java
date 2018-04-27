
public class test {
	public int factorial(int n) {
		int total = 0;
		if(n==0) return total;
		if (n>0) {
			total = n * factorial(n-1);
			
		}
		return total;
	}
}
