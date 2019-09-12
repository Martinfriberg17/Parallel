/**
 * 
 */
package cloudscapes;

/**
 * @author martinfri0331
 *
 */
public class Main {
	static CloudData cloudData =new CloudData();
	static long startTime = 0;

	/**
	 * @param args
	 */
	private static void tick(){
		startTime = System.currentTimeMillis();
	}

	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
cloudData.readData("largesample_input.txt");
System.gc();
for(int i=0;i<15;i++) {

tick();
cloudData.windAverageSequential();
cloudData.CalculateOutputCode();
System.out.println(tock());
tick();
cloudData.WindAverageParallel();
cloudData.CalculateOutputCodeParallel();
System.out.println(tock());
System.out.println("ny");

		}
//cloudData.Control("largesample_output.txt");
cloudData.writeData("outputFileP2.txt");
		
	}

}
