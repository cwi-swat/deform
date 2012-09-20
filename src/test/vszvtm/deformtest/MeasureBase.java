package test.vszvtm.deformtest;

import java.awt.Color;
import java.awt.Graphics2D;


public abstract class MeasureBase extends demo.DemoBase{

	public static int nrTests = 50;
	public static int warmup = 20;
	
	 public void gameLoop()
	   {
	        long oldTime = System.currentTimeMillis();
	        long times[] = new long[nrTests];
	        int timesIndex = 0;
	        
	        for(int i = 0 ; i < warmup; i++){
	        	doStuff();
	        }
	        for(int  i = 0 ; i < nrTests ; i++){
	        	long now = System.currentTimeMillis();
	        	doStuff();
					long time = System.currentTimeMillis();
		        	times[timesIndex] = time - oldTime;
		        	timesIndex = (timesIndex + 1) % times.length;
		            oldTime = time;
	        }
	        long total = 0;
	        for(int i = 0 ; i < nrTests ; i++){
	        	total+=times[i];
	        }
	        System.out.printf("%f %f\n", getZoom(), (double)total/nrTests);
	        System.exit(0);
	 }
	 
	 public abstract double getZoom() ;
	void doStuff(){
		 try
         {
		  g = (Graphics2D)bufferStrategy.getDrawGraphics();
          this.g = g;
//          g.setBackground(Color.black);
//          g.clearRect(0, 0, (int)size.x,(int)size.y);
          g.setBackground(Color.white);
          g.clearRect(0, 0, (int)size.x(),(int)size.y());
          g.translate(insets.left, insets.top);
          draw(); // enter the method to draw everything

         }
          finally
          {
              g.dispose();
          }
		  bufferStrategy.show();
		 
	 }
}
