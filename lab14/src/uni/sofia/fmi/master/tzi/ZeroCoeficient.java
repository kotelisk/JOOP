package uni.sofia.fmi.master.tzi;

public class ZeroCoeficient extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ZeroCoeficient(){
		super("The second power coeficient is 0.");
	}
}
