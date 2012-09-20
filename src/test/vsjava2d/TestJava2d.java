package test.vsjava2d;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.UIManager;




public abstract class TestJava2d extends TestBase{

	Shape[] shapes;
	Color[] colors;
	Paint[] textures;
	
	@Override
	public void init() {
		shapes = new Shape[24];
		for(int i = 0 ; i < shapes.length; i++){
			shapes[i] = makeLetterShape((char) ('a' + i));
		}
		colors = new Color[]{ new Color(255,0,0,100), new Color(0,255,0,255), new Color(234,234,12,24), new Color(0,0,255,100), 
				new Color(128,234,12,50), new Color(23,234,12,50),new Color(23,234,175,110),
				new Color(24,52,12,80), new Color(135,75,12,200),new Color(23,234,10,200)};
		textures =new Paint[4];
		for(int i = 0 ; i < textures.length ; i++){
			try {
				BufferedImage b = 
						ImageIO.read(
								Thread.currentThread().getContextClassLoader().getResourceAsStream("tex" + (i+1) + ".jpg"));
				textures[i] = new TexturePaint(b, new Rectangle2D.Double(0,0,256,256));
			} catch (IOException e) {
				
				e.printStackTrace();
				throw new Error("Image error!");
			}
				
		}
		
	}

	private Shape makeLetterShape(char i) {
		Font f = new Font(UIManager.getDefaults().getFont("Label.font").getName(), Font.PLAIN, 16);
		FontRenderContext ctx = new FontRenderContext(null, true, true);
		GlyphVector v = f.createGlyphVector(ctx, i + "");
		return  v.getOutline();
	}

}
