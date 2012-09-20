module GenerateJava2DCode

import RandomData;
import List;
import IO;

list[tuple[int,list[VisDatum]]] split(int splitSize,list[VisDatum] dat)=
	[<i,slice(dat,i*splitSize,splitSize)> | i <- [0..(size(dat)/splitSize)-1]];

public str awtCode(int nr) = awtCode(randomVisData(nr));

public void writeAWTCode(){
	writeFile(|file:///export/scratch1/ploeg/workspace/meta-blitztests/src/Java2dActual.java|,awtCode(3000));
}


public str awtCode(VisData dd) {
	return "
		import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;

		
		import javax.imageio.ImageIO;
		import javax.swing.UIManager;
	public class Java2dActual extends TestJava2d{
	public void draw(Graphics2D g) {
	' AffineTransform prev = null;
	'<for(i<- [0..size(dd)-1]) {> drawSomething<(i)>(g);  
	'<}> 
	'}
	
	'<for(i<- [0..size(dd)-1]) {> <generateDatumCode(i,dd[i])>  
	'<}> 
	  public static void main(String[] argv){
	  	new Java2dActual();
	  }
	  
		void drawDeform() {
			
		}
	 }";
}




str generateDatumCode(int i,VisDatum d) =
	"void drawSomething<i>(Graphics2D g){
	' 	AffineTransform prev = g.getTransform();
	' 	<setPaintCode(d.fill)>
	' 	<setAffine(d.trans)>
	' 	g.fill(shapes[<d.shape>]);
	' 	g.setTransform(prev);
	}";
	
str setAffine(id()) = "";
str setAffine(shear(x, y,rest)) = 
	"g.shear(<x>,<y>);
	'<setAffine(rest)>";
str setAffine(scale(x, y,rest)) = 
	"g.scale(<x>,<y>);
	'<setAffine(rest)>";
str setAffine(rotate(x,rest)) = 
	"g.rotate(<x>);
	'<setAffine(rest)>";
str setAffine(translate(x, y,rest)) = 
	"g.translate(<x>,<y>);
	'<setAffine(rest)>";

str setPaintCode(fillColor(i)) = "g.setPaint(colors[<i>]);";
str setPaintCode(linearGradient(colors)) =
	"g.setPaint(new LinearGradientPaint(0.0f,0.0f,1.0f,0.0f,
	'	new float[]{<intercalate(",", ["<f>f" | <f,_> <- colors])>}, 
	'	new Color[]{<intercalate(",", ["colors[<i>]" | <_,i> <- colors])>}
	'	,MultipleGradientPaint.CycleMethod.REFLECT)); ";
str setPaintCode(radialGradient(colors)) =
	"g.setPaint( new RadialGradientPaint(<0>f,<0>f,1f,
	'	new float[]{<intercalate(",", ["<f>f" | <f,_> <- colors])>}, 
	'	new Color[]{<intercalate(",", ["colors[<i>]" | <_,i> <- colors])>}
	'	,MultipleGradientPaint.CycleMethod.REFLECT)); ";
str setPaintCode(imageFill(color)) =
	"g.setPaint(textures[<color>]);";