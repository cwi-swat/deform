/*   FILE: ViewDemo.java
 *   DATE OF CREATION:  Fri Aug 26 15:12:06 2005
 *   AUTHOR :           Emmanuel Pietriga (emmanuel.pietriga@inria.fr)
 *   MODIF:             Emmanuel Pietriga (emmanuel.pietriga@inria.fr)
 *   Copyright (c) INRIA, 2004-2011. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id: ViewDemo.java 4296 2011-03-03 10:39:57Z epietrig $
 */ 

package test.vszvtm.zvtmtest;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

import fr.inria.zvtm.animation.Animation;
import fr.inria.zvtm.animation.interpolation.SlowInSlowOutInterpolator;
import fr.inria.zvtm.engine.Camera;
import fr.inria.zvtm.engine.View;
import fr.inria.zvtm.engine.VirtualSpace;
import fr.inria.zvtm.engine.VirtualSpaceManager;
import fr.inria.zvtm.event.RepaintListener;
import fr.inria.zvtm.event.ViewListener;
import fr.inria.zvtm.lens.FSGaussianLens;

public abstract class ViewDemo {

    VirtualSpaceManager vsm;

    VirtualSpace vs;

    String mainSpaceName = "demoSpace";
    String mainViewName = "View";

    ViewListener eh;

    static short MOVE_UP=0;
    static short MOVE_DOWN=1;
    static short MOVE_LEFT=2;
    static short MOVE_RIGHT=3;
    static int ANIM_MOVE_LENGTH = 500;

    View demoView;
    Camera mCamera;

    short translucentMode = 0;
    String viewType = View.STD_VIEW;

    String tms, vts;

    ViewDemo(){
        vsm = VirtualSpaceManager.INSTANCE;
        translucentMode = 1;
        tms = "Translucency: ON";
        viewType = View.STD_VIEW;
        vts = "View type: Standard";
        init();
    }

    ProgFrame pf;

    abstract double zoomGlobal();
    abstract float zoomLens();
    
	public static int nrTests = 50;
	public static int warmup = 20;
	
    public void init(){
        vs = vsm.addVirtualSpace(mainSpaceName);
        
        Vector cameras=new Vector();
        mCamera = vs.addCamera();
        mCamera.setZoomFloor(-90);
        cameras.add(mCamera);
//        ViewPanel p = new OffscreenViewPanel(cameras);
        demoView = vsm.addFrameView(cameras, mainViewName, viewType, 1600, 1000, true);
        demoView.setBackgroundColor(Color.WHITE);
        demoView.setListener(eh);
        demoView.setAntialiasing(true);
//        demoView.setRefreshRate(0);
        demoView.setLens(new FSGaussianLens(zoomLens(), 200, 100));
//        mCamera.setZoomCeiling(zoomGlobal());
//        mCamera.setZoomFloor(zoomGlobal());
        buildGlyphs();
        long oldTime = System.currentTimeMillis();
        long times[] = new long[nrTests];
        int timesIndex = 0;
        double fps = 0;
        
        // create a image to draw to to match 0,0 up correctly.
       
            // relating to updating animations and calculating FPS
        for(int i = 0 ; i < warmup; i++){
        	demoView.repaint(new WaitForRepaint());
        	Thread.currentThread().suspend();
        }
        for(int  i = 0 ; i < 100000 ; i++){
        	long now = System.currentTimeMillis();
        	demoView.repaint(new WaitForRepaint());
				Thread.currentThread().suspend();
				long time = System.currentTimeMillis();
	        	times[timesIndex] = time - oldTime;
	        	timesIndex = (timesIndex + 1) % times.length;
	            oldTime = time;
        }
        long total = 0;
        for(int i = 0 ; i < nrTests ; i++){
        	total+=times[i];
        }
        System.out.println((double)total/nrTests);
        System.exit(0);
//        System.out.println(fps);
    }
    final Object b = new ArrayList();
    class WaitForRepaint implements RepaintListener{
    	boolean done;
    	Thread t;
    	public WaitForRepaint() {
    		t= Thread.currentThread();
		}
		@Override
		public void viewRepainted(View v) {
			t.resume();
			
		}
    	
    	
    }
	public static String txt = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n Aliquam ultrices quam rhoncus diam sollicitudin at posuere nibh\n consequat. Quisque laoreet consequat diam ac pharetra.\n Curabitur nisl enim, lacinia at placerat a, pretium eu felis.\n Cras feugiat lobortis porttitor. Suspendisse ante lectus,\n hendrerit ullamcorper lacinia sed, porta quis augue. Vestibulum\n tristique sagittis nisl, quis tempus diam tincidunt id.\n Praesent facilisis, urna non accumsan euismod, elit nunc\n pharetra velit, quis sodales nisi ligula sit amet eros.\n Maecenas condimentum viverra lacus, non feugiat nibh fermentum\n at. Pellentesque ac enim dolor. Duis a lorem ante. Curabitur\n feugiat nisl eu leo tristique pharetra. Etiam in leo eu erat interdum\n pellentesque ut non erat.\n Donec fermentum sapien eget\n risus congue a feugiat risus luctus. Sed sollicitudin velit\n ut tellus feugiat posuere.\n\nDuis urna elit, viverra quis scelerisque nec, interdum commodo\n ligula. Fusce blandit mollis metus et molestie. Cras rutrum\n ultrices diam volutpat viverra. Mauris dapibus eros ut\n sapien convallis sit amet tempor elit iaculis. Sed sit amet\n dolor dui. Phasellus elementum condimentum lacus fringilla\n convallis. Curabitur velit metus, iaculis in pulvinar eget,\n tincidunt sit amet velit. Fusce et nisl nunc. Vestibulum\n suscipit, lacus vel blandit pretium, tortor orci sodales enim,\n sit amet aliquet est dui sit amet lacus. Fusce pellentesque\n lacus sit amet mauris facilisis sollicitudin. In convallis\n nisl vitae libero mattis blandit.";

	void buildGlyphs(){
		vsm.repaint();
		buildGlyphsReal();
//		pf.destroy();
		demoView.getGlobalView(vsm.getVirtualSpace(mainSpaceName).getCamera(0), 400);
	}
	
    abstract void buildGlyphsReal();
//    	try {
//    		vs.addGlyph(new VTextLayout(txt));
//			vs.addGlyph(new VImage(ImageIO.read(new File("/home/ploeg/landscape.jpg"))));
//		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}




    void translateView(short direction){
	
    }

    void getGlobalView(){
	demoView.getGlobalView(vsm.getActiveCamera(), ViewDemo.ANIM_MOVE_LENGTH);
    }

    void getHigherView(){
	Camera c = vsm.getView(mainViewName).getCameraNumber(0);
	double alt = c.getAltitude()+c.getFocal();

	Animation altAnim = vsm.getAnimationManager().getAnimationFactory()
	    .createCameraAltAnim(ViewDemo.ANIM_MOVE_LENGTH, c, alt, true, 
				 SlowInSlowOutInterpolator.getInstance(), null);
	vsm.getAnimationManager().startAnimation(altAnim, true);
    }
    
    void getLowerView(){
	Camera c = vsm.getView(mainViewName).getCameraNumber(0);
	double alt = -(c.getAltitude()+c.getFocal())/2.0;

	Animation altAnim = vsm.getAnimationManager().getAnimationFactory()
	    .createCameraAltAnim(ViewDemo.ANIM_MOVE_LENGTH, c, alt, true, 
				 SlowInSlowOutInterpolator.getInstance(), null);
	vsm.getAnimationManager().startAnimation(altAnim, true);
    }

    void exit(){
	System.exit(0);
    }

    
}



