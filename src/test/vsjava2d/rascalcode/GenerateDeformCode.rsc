module GenerateDeformCode


import RandomData;
import List;
import IO;
import GenerateJava2DCode;

list[tuple[int,list[VisDatum]]] split(int splitSize,list[VisDatum] dat)=
	[<i,slice(dat,i*splitSize,splitSize)> | i <- [0..(size(dat)/splitSize)-1]];

public void writeDeformCode(){
	VisData d = randomVisData(400);
	writeFile(|file:///export/scratch1/ploeg/workspace/meta-blitztests/src/Java2dActual.java|,awtCode(d));
	writeFile(|file:///export/scratch1/ploeg/workspace/meta-blitztests/src/DeformActual.scala|,deformCode(d));
}


public str deformCode(VisData dd) {
	return "
import deform.Library._
import deform.Library.Transforms._
import deform.Library.Shapes._
import deform.Library.Textures._
import deform.Library.TexturedShapes._
import deform.Library.Colors._
import deform.Library.Transforms._
import deform._

	class DeformActual extends TestScala{
	 def drawDeform() : Unit = {
	' draw(scale(1.0/350) ** translate(-700.0.-400.0)) drawing(<intercalate(",",["drawSomething<i>" | i <- [0..size(dd)-1]])>))
	'}
	
	'<for(i<- [0..size(dd)-1]) {> <generateDatumCode(i,dd[i])>  
	'<}> 
	  def main(args: Array[String]): Unit = {
     new DeformActual()
   }
	 }";
}



str generateDatumCode(int i,VisDatum d) =
	" def drawSomething<i> = 
	' <affineAll(d.trans)> **
	'	fill(shapes(<d.shape>),
	' <setPaintCode(d.fill)>);
	' ";
	
str setRest(id()) = "";
default str setRest(x) = ".compose(<affine(x)>)";	

str affineAll(Affine f) = "(" + intercalate("**",affine(f)) + ")";

list[str] affine(id()) = [];
list[str]  affine(shear(x, y,rest)) = 
	["shear(<x>,<y>)",*affine(rest)];
list[str]  affine(scale(x, y,rest)) = 
	["scale(<x>,<y>)", *affine(rest)];
list[str]  affine(rotate(x,rest)) = 
	["rotate(<-x>)", *affine(rest)];
list[str]  affine(translate(x, y,rest)) = 
	["translate(<x>,<y>)", *affine(rest)];

str setPaintCode(fillColor(i)) = "fillColor(colors(<i>))";
str setPaintCode(linearGradient(colors)) =
	"lineGradient(Point(0,0),Point(1,0),List(<intercalate(",", ["(<f>,colors(<i>))" | <f,i> <- colors])>))";
str setPaintCode(radialGradient(colors)) =
	"circleGradient(Point(0,0),Point(0,0),1,List(<intercalate(",", ["(<f>,colors(<i>))" | <f,i> <- colors])>))";
str setPaintCode(imageFill(color)) =
	"textures(<color>)";