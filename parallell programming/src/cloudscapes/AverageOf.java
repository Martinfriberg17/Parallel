package cloudscapes;
import java.util.concurrent.RecursiveTask;

public class oldAvergae extends RecursiveTask <int []>  {
	static long startTime = 0;

		 private int lo; // arguments
		 private int hi;
		 private int [] location = new int [3];
		 private int dimx,dimy;
		 private MyVector[][][] advection;
		  CloudData clouddata;
		  private float totalWindX= 0;
		  private float totalWindY= 0;
		 private int numberOfElements= 0;
		  static final int SEQUENTIAL_CUTOFF=60000;
		  private float averageWindX = 0;
		  private float averageWindY = 0;
		  private float averageWind = 0;
			private int [] classificationP; // cloud type per grid point, evolving over time

		  

		  private float ans = 0; // result 
		    
		  oldAvergae(MyVector[][][] m, int l, int h, int x, int y) { 
		    lo=l; hi=h; advection=m; dimx = x; dimy = y;
		    classificationP = new int [hi-lo];
		  }
			private static void tick(){
				startTime = System.currentTimeMillis();
			}
			private static float tock(){
				return (System.currentTimeMillis() - startTime) / 1000.0f; 
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
				  ans = 0;
				
			      for(int i=0; i < hi-lo; i++) { // tror att den försöker lägga första index på plats 2621440
			    	  locate(i, location);

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
							classificationP[i] = 1;
						}else if (Math.abs(advection[location[0]][location[1]][location[2]].u) > averageWind){
							classificationP[i] = 0;
						}else {
							classificationP[i] = 2;
						}
						
						totalWindX = 0;
						totalWindY = 0;
					
						numberOfElements = 0;
			    		
			    
			      }
			      return classificationP;
			  }
			  else {
				  oldAvergae left = new oldAvergae(advection ,lo,(hi+lo)/2, dimx, dimy);
				  oldAvergae right= new oldAvergae(advection,(hi+lo)/2 ,hi, dimx, dimy);
				  
				  // order of next 4 lines
				  // essential – why?
				  left.fork();
				  int [] rightAns = right.compute();
				  int[] leftAns  = left.join();
				 System.arraycopy(leftAns, 0, classificationP, 0, leftAns.length);
				 System.arraycopy(rightAns, 0, classificationP, leftAns.length, rightAns.length);

				  return classificationP;     
			  }
		 }
	}
