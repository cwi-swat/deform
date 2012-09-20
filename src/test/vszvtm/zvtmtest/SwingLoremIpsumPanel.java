package test.vszvtm.zvtmtest;


import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.UIManager;



public class SwingLoremIpsumPanel extends JPanel{
	
	public static String txt = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sed turpis sed felis vestibulum pretium.\nPraesent at elit vitae odio eleifend tristique in sit amet sem. In cursus condimentum enim eget\nmalesuada. Donec iaculis, velit non tempor egestas, tellus lacus viverra orci, sed ultricies lorem magna";
	Shape theText;
	Paint p;
	
	BufferedImage normal, zoomed;
	double zoom = 8;
	public SwingLoremIpsumPanel() {
		setSize(1600,1000);
		
		Font f = new Font(UIManager.getDefaults().getFont("Label.font").getName(), Font.PLAIN, 30);
		FontRenderContext ctx = new FontRenderContext(null, true, true);
		LineMetrics lm = f.getLineMetrics(txt, ctx);
		double height = lm.getHeight();
		String[] lines = txt.split("\\n");
		int i = 0;
		Path2D res = new Path2D.Double();
		res.setWindingRule(Path2D.WIND_EVEN_ODD);
		for(String line : lines){
			if(line.trim().isEmpty()){
				continue;
			}
			GlyphVector v = f.createGlyphVector(ctx, line);
			PathIterator it = v.getOutline().getPathIterator(AffineTransform.getTranslateInstance(0,i * height));
			res.append(it, false);
			i++;
		}
		theText = res;
	}
	
	@Override
	protected void paintComponent(Graphics g){
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.translate(0, 470.8203125);
		
		g2.fill(theText);

	}
	
}
