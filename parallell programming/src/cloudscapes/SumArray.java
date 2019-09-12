package cloudscapes;

import java.util.concurrent.RecursiveTask;

public class SumArray extends RecursiveTask <Float>  { 
	  int lo; // arguments
	  int hi;
	  float[] arr;
	  static final int SEQUENTIAL_CUTOFF=1000;

	  int ans = 0; // result 
	    
	  SumArray(float[] a, int l, int h) { 
	    lo=l; hi=h; arr=a;
	  }


	  protected Float compute(){// return answer - instead of run
		  if((hi-lo) < SEQUENTIAL_CUTOFF) {
			  float ans = 0;
		      for(int i=lo; i < hi; i++) {
		        ans += arr[i];
		      }
		      return ans;
		  }
		  else {
			  SumArray left = new SumArray(arr,lo,(hi+lo)/2);
			  SumArray right= new SumArray(arr,(hi+lo)/2,hi);
			  
			  // order of next 4 lines
			  // essential – why?
			  left.fork();
			  float rightAns = right.compute();
			  float leftAns  = left.join();
			  return leftAns + rightAns;     
		  }
	 }
}


