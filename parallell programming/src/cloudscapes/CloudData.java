package cloudscapes;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

import cloudscapes.SumArray;

import java.io.FileWriter;
import java.io.PrintWriter;


public class CloudData {

	static final ForkJoinPool fjPool = new ForkJoinPool();
	private MyVector [][][] advection;// in-plane regular grid of wind vectors, that evolve over time
	private MyVector averageWind;
	private float [] arrX;
	private float [] arrY;
	private float [] arrU;
	float windX;
	float windY;
	private int [][][] classification; // cloud type per grid point, evolving over time
	private int [] classificationP; //cloud type per grid point, evolving over time for parallel
	private int [] classificationP_file; //cloud type per grid point, evolving over time read from file

	private static int dimx, dimy, dimt; // data dimensions
	
	static int dim(){
		return dimt*dimx*dimy;
	}

	static float sum(float[] arr){
		return fjPool.invoke(new SumArray(arr,0,arr.length));
	}

	static int [] average(MyVector [][][] matrix, int dim){
		return fjPool.invoke(new AverageOf(matrix,0, dim, dimx, dimy));
	}


	public void WindAverageParallel() {// calculates the average wind in x & y directions
	 float sumArrX = sum(arrX);
	float	 sumArrY = sum(arrY);
	windX = sumArrX/dim();
	windY = sumArrY/dim();

	}

	public void CalculateOutputCodeParallel () { //calculates the classification array through parallel programming

		classificationP = new int [dimt*dimx*dimy];
		classificationP = average(advection, dim());
	}

	public void Control (String fileName) { //Controls so that the sequential and parallel classification matrices are the same
		try {
			
		Scanner sc = new Scanner(new File(fileName), "UTF-8");
		int waste;
		 waste=sc.nextInt();
		 waste=sc.nextInt();
	 waste=sc.nextInt();
	 float waste1;
	 float waste2;
	 waste1 = sc.nextFloat();
	 waste1 = sc.nextFloat();


		classificationP_file = new int [dimt*dimx*dimy];
		int place= 0;
		for(int t = 0; t < dimt; t++) {
			for(int x = 0; x < dimx; x++) {
				for(int y = 0; y < dimy; y++){

					classificationP_file[place] = sc.nextInt();

					place ++;
				}
			
			}
		}
		sc.close();
		}
		catch (IOException e){ 
			System.out.println("Unable to open input file "+fileName);
			e.printStackTrace();
		}
		catch (java.util.InputMismatchException e){ 
			System.out.println("Malformed input file "+fileName);
			e.printStackTrace();
		}
	
		int counter = 0;
		for(int t = 0; t < dimt; t++) {
			for(int x = 0; x < dimx; x++) {
				for(int y = 0; y < dimy; y++){
					if (classificationP_file[counter] == classificationP[counter]) {
					}else {
						System.out.println("wrong");
						System.out.println(counter);

				}
					counter++;
				}
					
				}
					
				}

	}


	public void windAverageSequential(){

		float totalWindY = 0;
		float totalWindX = 0;
		float averageWindX = 0;
		float averageWindY = 0;
		for(int t = 0; t < dimt; t++) {
			for(int x = 0; x < dimx; x++) {
				for(int y = 0; y < dimy; y++){
					totalWindY += advection[t][x][y].y;
					totalWindX += advection[t][x][y].x;
				}
			}
		}
		averageWindX = totalWindX/(float)dim();
		averageWindY = totalWindY/(float)dim();
		averageWind = new MyVector();
		averageWind.x = averageWindX;
		averageWind.y = averageWindY;
	}

	public void CalculateOutputCode () {
		int numberOfElements = 0;
		double totalWindX = 0;
		double totalWindY = 0;
		double averageWindX;
		double averageWindY;
		double averageWind;
		
		for(int t = 0; t < dimt; t++) {
			for(int x = 0; x < dimx; x++) {
				for(int y = 0; y < dimy; y++){
					for (int counterX = 0; counterX < 3; counterX++) {
						for (int counterY = 0; counterY < 3; counterY++) {
							if ((x + counterX - 1) >= 0 && (x + counterX -1) < dimx 
									&& (y + counterY - 1) >= 0 && (y + counterY -1) <dimy) {
								totalWindX += advection [t][x + counterX-1][y + counterY-1].x;
								totalWindY += advection [t][x + counterX-1][y + counterY-1].y;
								numberOfElements ++;
							}

						}
					}
					averageWindX = totalWindX /numberOfElements;
					averageWindY = totalWindY /numberOfElements;
					averageWind = Math.sqrt(Math.pow(averageWindX, 2) + Math.pow(averageWindY, 2));
					if (averageWind>Math.abs(advection[t][x][y].u) && averageWind > 0.2) {
						classification[t][x][y] = 1;
					}else if (Math.abs(advection[t][x][y].u) > averageWind){
						classification[t][x][y] = 0;
					}else {
						classification[t][x][y] = 2;
					}
					totalWindX = 0;
					totalWindY = 0;
					numberOfElements = 0;
				}
			}
		}
	}



	// read cloud simulation data from file
	void readData(String fileName){ 
		try{ 
			Scanner sc = new Scanner(new File(fileName), "UTF-8");

			// input grid dimensions and simulation duration in timesteps
			dimt = sc.nextInt();
			dimx = sc.nextInt(); 
			dimy = sc.nextInt();
			arrX = new float [dimt*dimx*dimy];
			arrY = new float [dimt*dimx*dimy];
			arrU = new float [dimt*dimx*dimy];

			// initialize and load advection (wind direction and strength, advection now also has uplift)
			advection = new MyVector[dimt][dimx][dimy];
			int place = 0;
			for(int t = 0; t < dimt; t++)
				for(int x = 0; x < dimx; x++)
					for(int y = 0; y < dimy; y++){

						advection[t][x][y] = new MyVector();
						advection[t][x][y].x = sc.nextFloat();
						arrX[place] = advection[t][x][y].x;
						advection[t][x][y].y = sc.nextFloat();
						arrY[place] = advection[t][x][y].y;
						advection[t][x][y].u  = sc.nextFloat();
						arrU[place] = advection[t][x][y].u;

						place ++;
					}

			classification = new int[dimt][dimx][dimy];
			sc.close(); 
		} 
		catch (IOException e){ 
			System.out.println("Unable to open input file "+fileName);
			e.printStackTrace();
		}
		catch (java.util.InputMismatchException e){ 
			System.out.println("Malformed input file "+fileName);
			e.printStackTrace();
		}
	}


	// write classification output to file
	void writeData(String fileName/** MyVector wind**/){
		try{ 

			FileWriter fileWriter = new FileWriter(fileName);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.printf("%d %d %d\n", dimt, dimx, dimy);
			printWriter.printf("%f %f\n", windX , windY);
			for(int t = 0; t < dim(); t++){

				printWriter.printf("%d ", classificationP[t]);

				if ((t % ((dimx)*(dimy)))==0 && t!=0) {
					printWriter.printf("\n");
				}
			}

			printWriter.close();
		}
		catch (IOException e){
			System.out.println("Unable to open output file "+fileName);
			e.printStackTrace();
		}
	}
}