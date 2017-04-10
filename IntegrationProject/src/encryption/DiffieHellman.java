package encryption;

public class DiffieHellman {
	
	// Prime number mapped to their primitive roots {prime, primitive root} (p, g): 
	public static final int[][] PRIME_TO_PRIMITIVE_ROOT = new int[][]{{3, 2}, {7, 3}, {11, 2}, {13, 2}, {17, 3}, {19, 2}, {23, 5}, {29, 2}};
	
	// Secret number {1 ... 10} that may never be published
	private final static int secretNumber = 1 + (int) (Math.random() * 10);
	

	public DiffieHellman() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		System.out.println(secretNumber);
	}

}
