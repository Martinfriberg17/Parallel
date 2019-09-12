package cloudscapes;
import java.util.concurrent.RecursiveTask;

public class AverageOf extends RecursiveTask <int []>  {
	static long startTime = 0;

		 private int lo; // arguments
		 private int hi;
		 private int [] location = new int [3]; //contains t, x, y values for the converted linear position
		 private int dimx,dimy;
		 private MyVector[][][] advection; //contains x & y wind, but also uplift for every position in the 3D grid
		  private float totalWindX= 0; 
		  private float totalWindY= 0;
		 private int numberOfElements= 0;
		  static final int SEQUENTIAL_CUTOFF=1000;
		  private float averageWindX = 0;
		  private float averageWindY = 0;
		  private float averageWind = 0;
			private int [] classificationP; // cloud type per grid point, evolving over time
		  

		    
		  AverageOf(MyVector[][][] m, int l, int h, int x, int y) { 
		    lo=l; hi=h; advection=m; dimx = x; dimy = y;
		    classificationP = new int [hi-lo];
		  }
	
			// convert linear position into 3D location in simulation grid
			void locate(int pos, int [] ind)
			{
				ind[0] = (int) pos / (dimx*dimy); // t
				ind[1] = (pos % (dimx*dimy)) / dimy; // x
				ind[2] = pos % (dimy); // y
			}


		  protected int[] compute(){// return answer - instead of run
			  if((hi-lo) < SEQUENTIAL_CUTOFF) {
				
			      for(int i=lo; i < hi; i++) { //for every element in the array
			    	  locate(i, location); //locate the place to get t, x, y coordinates, then runs the same code as the sequential part.
/*
 * Calculating the classification for every small array
 */
			    		for (int counterX = 0; counterX < 3; counterX++) { 
							for (int counterY = 0; counterY < 3; counterY++) {
								if ((location[1] + counterX - 1) >= 0 && (location[1] + counterX -1) < dimx 
										&& (location[2] + counterY - 1) >= 0 && (location[2] + counterY -1) <dimy) {
									totalWindX += advection [location[0]][location[1] + counterX-1][location[2] + counterY-1].x;
									totalWindY += advection [location[0]][location[1] + counterX-1][location[2] + counterY-1].y;
									numberOfElements ++;
								}
								
							}
						}
			    		averageWindX = totalWindX /numberOfElements;
						averageWindY = totalWindY /numberOfElements;
	averageWind = (float)Math.sqrt(Math.pow(averageWindX, 2) + Math.pow(averageWindY, 2));
						if (averageWind>Math.abs(advection[location[0]][location[1]][location[2]].u) && averageWind > 0.2) {
							classificationP[i-lo] = 1;
						}else if (Math.abs(advection[location[0]][location[1]][location[2]].u) > averageWind){
							classificationP[i-lo] = 0;
						}else {
							classificationP[i-lo] = 2;
						}
						
						totalWindX = 0;
						totalWindY = 0;
					
						numberOfElements = 0;
			    		
			    
			      }
			      return classificationP;
			  }
			  else {
				  AverageOf left = new AverageOf(advection ,lo,(hi+lo)/2, dimx, dimy);
				  AverageOf right= new AverageOf(advection,(hi+lo)/2 ,hi, dimx, dimy);
				  
				  // order of next 5 lines important
				  left.fork();
				  int [] rightAns = right.compute();
				  int[] leftAns  = left.join();
				 System.arraycopy(leftAns, 0, classificationP, 0, leftAns.length);
				 System.arraycopy(rightAns, 0, classificationP, leftAns.length, rightAns.length);

				  return classificationP;     
			  }
		 }
	}
