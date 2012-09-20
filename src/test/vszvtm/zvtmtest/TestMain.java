package test.vszvtm.zvtmtest;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


import fr.inria.zvtm.glyphs.VImage;
import fr.inria.zvtm.glyphs.VSwingComponent;

public class TestMain extends ViewDemo {

	static double zo;
	TestMain() {
		super();
	}

	@Override
	double zoomGlobal() {
		return 1;
	}
	

	@Override
	float zoomLens() {
		return (float)zo;
	}

	@Override
	void buildGlyphsReal() {
		try {
			Image i = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("landscape.jpg"));
			vs.addGlyph(new VImage(0,0,0,i,1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		vs.addGlyph(new VSwingComponent(new SwingLoremIpsumPanel()));
		
		
	}
	
	public static void main(String[] argv){
		// argument: magnification factor
		zo = Double.parseDouble(argv[0]);
		System.out.print(zo + " ");
		new TestMain();
	}


}

