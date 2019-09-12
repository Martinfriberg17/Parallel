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
	static float averageTimeSeqWind = 0;
	static float averageTimeSeqClass = 0;
	static float averageTimeParWind = 0;
	static float averageTimeParClass = 0;
	static float tock = 0;

	private static void tick(){
		startTime = System.currentTimeMillis();
	}

	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}

	/*First reading the data from file, then running the different methods to calculate the time it take for every method to run
	 * Writing the data to file
	 */

	public static void main(String[] args) { 
		// TODO Auto-generated method stub
		cloudData.readData("largesample_input.txt");
		System.gc();
		for(int i=0;i<115;i++) {


			tick();
			cloudData.windAverageSequential(); //calculating sequential wind average
			tock = tock();
			if (i>4) {
				averageTimeSeqWind += tock;
			}
			tick();
			cloudData.CalculateOutputCode(); //calculating sequential classification
			tock = tock();
			if (i>4) {
				averageTimeSeqClass += tock;
			}


			tick();
			cloudData.WindAverageParallel(); //calculating parallel wind average
			tock = tock();
			if (i>4) {
				averageTimeParWind += tock;
			}
			tick();
			cloudData.CalculateOutputCodeParallel(); //calculating parallel classification
			tock = tock();
			if (i>4) {
				averageTimeParClass += tock;
			}
			System.out.println(tock);
			if ((i % 10)==0) {
				System.out.println("klirr");
			}

		}

		System.out.println("Wind"+ (averageTimeSeqWind/110));
		System.out.println("Wind"+ (averageTimeSeqClass/110));
		System.out.println("class"+ (averageTimeParWind/110));
		System.out.println("Class"+ (averageTimeParClass/110));

		cloudData.writeData("outputFileP2.txt");

	}

}
