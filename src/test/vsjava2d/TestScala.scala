package test.vsjava2d
import deform.library._
import deform.Drawing
import deform.Library._
import deform.AABBox
import deform.Interval
import java.awt.Graphics2D
import deform.Shape
import deform.Library.Colors._
import deform.Library.Textures._
import deform.Color
import deform.Texture

class TestScala extends TestBase{

  var shapes = null : Array[Shape]
  var colors = null : Array[Color]
  var textures = null : Array[Texture]
  
  def makeLetterShape(c : Char) = {
    Shapes.text(c.toString(),16)
  }
  
  override def init() = {
		shapes =new Array(24)
		for(i <- 0 until 24){
			shapes.update(i,makeLetterShape(('a'.toInt + i).toChar));
		}
		colors = List( byteColor(255,0,0,100), byteColor(0,255,0,255), byteColor(234,234,12,24), byteColor(0,0,255,100), 
				byteColor(128,234,12,50), byteColor(23,234,12,50),byteColor(23,234,175,110),
				byteColor(24,52,12,80), byteColor(135,75,12,200),byteColor(23,234,10,200)).toArray;
		textures = List(imageTexture("tex1.jpg"),imageTexture("tex2.jpg"),imageTexture("tex3.jpg"),imageTexture("tex4.jpg")).toArray
  }
  
//  ovedef draw(g : Graphics2D) : Unit = {}
  
  def draw(tsl : Drawing) : Unit = 
    render(AABBox(Interval(0,size.x),Interval(0,size.y)),this.g,tsl)
}