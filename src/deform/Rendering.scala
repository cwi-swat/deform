package deform

import java.awt.Graphics2D
import java.awt.Paint
import DeformFunctions._
import deform.library._
import java.awt.geom.AffineTransform
import scala.math._
import java.awt.geom.Rectangle2D

private[deform] abstract class RenderContext[GraphicsState]{
  def render(area : AABBox, state : GraphicsState, ts : Drawing)
}



private[deform] abstract class Java2DBuffer(val t : AffineTransform ,val area : AABBox, val bufType : Int){
  val img = new java.awt.image.BufferedImage(area.widthInt,
				area.heightInt, bufType);
  val g = img.getGraphics() match {
	  	case g2: Graphics2D => g2
	  	case _ => throw new ClassCastException
	}
  val imgBuf = img.getRaster().getDataBuffer();
	g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, // Anti-alias!
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
	g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
					java.awt.RenderingHints.VALUE_RENDER_QUALITY);
	g.setTransform(t)
 def setClip(s : java.awt.Shape) = {
   g.setClip(s)
 }
 def clearClip() = {
   g.setClip(null)
 }
}

private[deform] class FillJava2DBuffer(override val t : AffineTransform,area : AABBox) extends Java2DBuffer(t,area,java.awt.image.BufferedImage.TYPE_BYTE_GRAY){
  def getPixel(index : Int) = imgBuf.getElem(index)/255.0
  def draw(aabb : AABBox, s : java.awt.geom.Path2D.Double){
    g.setColor(java.awt.Color.black);
    g.fill(RenderJava2D.AABBox2Rect(aabb))
    g.setColor(java.awt.Color.white)
    g.fill(s)
  }
}

private[deform] class ColorJava2DBuffer(override val t : AffineTransform,area : AABBox) extends Java2DBuffer(t,area,java.awt.image.BufferedImage.TYPE_4BYTE_ABGR_PRE){
    val lineIndexes = area.widthInt * 4
    def setPixel(elem : Int, c : Color) = {
      val r = elem + 3;
      val g = elem + 2;
      val b = elem + 1;
      val a = elem;
	  val nc = Color(imgBuf.getElem(r)/255.0, imgBuf.getElem(g)/255.0,imgBuf.getElem(b)/255.0, imgBuf.getElem(a)/255.0).add(c);
		imgBuf.setElem(r, (nc.red*255).toInt);
		imgBuf.setElem(g, (nc.green*255).toInt);
		imgBuf.setElem(b, (nc.blue*255).toInt);
		imgBuf.setElem(a, (nc.alpha*255).toInt);
    }
    def draw(t2 : java.awt.geom.AffineTransform, s : java.awt.geom.Path2D.Double, p : java.awt.Paint){
      t2.preConcatenate(t)
      g.setTransform(t2)
    g.setPaint(p)
    g.fill(s)
    g.setTransform(t)
  }
    def drawImage(t :  java.awt.geom.AffineTransform, img : java.awt.image.BufferedImage) = {
      g.drawImage(img,t,null)
    }
}

private[deform] object RenderJava2D extends RenderContext[java.awt.Graphics] {
	def getJava2DPath(g : List[ConcreteSegment]) = {
	  val res = new java.awt.geom.Path2D.Double()
	  res.moveTo(g.head.start.x, g.head.start.y)
	   g.foreach(cg =>
	      cg match {
	        case Line(_,e) => res.lineTo(e.x,e.y)
	        case Quad(s,c,e) => res.quadTo(c.x,c.y,e.x,e.y)
	        case Cubic(s,cl,cr,e) => res.curveTo(cl.x,cl.y,cr.x,cr.y,e.x,e.y)
	      }
	    )
	    res
	}
  
	def getJava2DShape(wind : Int, s : List[List[ConcreteSegment]]) : java.awt.geom.Path2D.Double = {
	  val res = new java.awt.geom.Path2D.Double()
	  res.setWindingRule(wind)
	  s.foreach(g => {
		val nres = getJava2DPath(g)
		nres.closePath()
	    res.append(nres, false)
	  }
	 )
	 res
	}
	
	def getGeom(user:Double,s : Shape) = 
	 s match {
	  case OpShape(op, ts) => getJava2DShape(getWind(op),{
	    val s = toBezierReal(user,ts)
	    s
	  })
	  case InverseAffTransformedShape(af, OpShape(op,ts)) => 
	    val nl = toBezierReal(user,ts).map(x => x.map(p => p.map(y => af.backwards * y)))
	  getJava2DShape(getWind(op),nl)
	}
	
	
	
	def toAWTColor(c : Color) = new java.awt.Color((c.red * 255).toInt, (c.green * 255).toInt,(c.blue * 255).toInt,(c.alpha * 255).toInt)
	
	def toAWTCycleMethod(c: CycleMethod) =
	  c match {
	  case NoCycle() => java.awt.MultipleGradientPaint.CycleMethod.NO_CYCLE
	  case Reflect() => java.awt.MultipleGradientPaint.CycleMethod.REFLECT
	  case Repeat() => java.awt.MultipleGradientPaint.CycleMethod.REPEAT
	}
	
	def getNativeTexture(t : NativeTexture) = 
	  t match {
	  case FillColor(c) => toAWTColor(c)
	  case LineLinearGradient(l,fs,cycle) => 
	    new java.awt.LinearGradientPaint(l.start.x.toFloat,l.start.y.toFloat,l.end.x.toFloat,l.end.y.toFloat,
	        fs.map(x =>x._1.toFloat).toArray, fs.map(x=>toAWTColor(x._2)).toArray,toAWTCycleMethod(cycle))
	  case LinearRadialGradient(c,f,r,fs,cycle) => 
	    new java.awt.RadialGradientPaint(c.x.toFloat,c.y.toFloat,r.toFloat, f.x.toFloat,f.y.toFloat,
	        fs.map(x =>x._1.toFloat).toArray, fs.map(x=>toAWTColor(x._2)).toArray,toAWTCycleMethod(cycle))
	  case s @ImageTexture(img,a) => 
	      new java.awt.TexturePaint(img, new java.awt.geom.Rectangle2D.Double(a.x.toInt,a.y.toInt,a.x.toInt + s.width,a.y.toInt + s.height))
	}
	
	def toJava2DTransform(t : AffineTransformation) =
	  new java.awt.geom.AffineTransform(t.forward.x1.toFloat,t.forward.y1.toFloat,t.forward.x2.toFloat,
	      t.forward.y2.toFloat,t.forward.x3.toFloat,t.forward.y3.toFloat)
	
	def renderNative(err : Double, t : java.awt.geom.AffineTransform, color : ColorJava2DBuffer, ts : TexturedShape) = {
	  ts match {
	  case ImageTexturedShape(img) => color.drawImage(t,img)
	  case CTexturedShape(s, tex : NativeTexture) =>
	    color.draw(t,getGeom(err,s), getNativeTexture(tex))
	}
	}
	
	def tryRenderOne(parea : AABBox, user: Double,scale : Double, userTrans : AffineTransformation,area : AABBox, fill : FillJava2DBuffer, color : ColorJava2DBuffer, ts : TexturedShape) : Unit = 
	  ts.bbox match {
	    case Some(b) => if(area.overlaps(b)) renderOne(parea,user,scale,userTrans,area,fill,color,ts) else ()
	    case None => renderOne(parea,user,scale,userTrans,area,fill,color,ts)
	  }
	
	def AABBox2Rect(a : AABBox) =
	  new Rectangle2D.Double(a.leftUp.x,a.leftUp.y,a.width,a.height)
	
	def renderOne(parea : AABBox,user: Double, scale: Double,userTrans : AffineTransformation,area : AABBox,fill : FillJava2DBuffer, color : ColorJava2DBuffer, ts : TexturedShape ) = {
	  ts match {
	  case ImageTexturedShape(img) => renderNative(user,new AffineTransform(),color,ts)
	  case AffTransformedNativeTexShape(t,ts2) => renderNative(user,toJava2DTransform(t),color,ts2)
	  case PartiallyTransformedTexturedShape(area2,t,pt) => {
	    val ar =area2.intersect(area)
	    val clip = AABBox2Rect(ar)
	    fill.setClip(clip)
	    color.setClip(clip)
	    
	    tryRenderOne(parea,user,scale,userTrans,ar,fill,color,normalize(true,t,user,scale,pt))
	    val clip2 = new java.awt.geom.Area(AABBox2Rect(area))
	    clip2.subtract(new java.awt.geom.Area(clip))
	    fill.setClip(clip2)
	    color.setClip(clip2)
	    tryRenderOne(parea,user,scale,userTrans,ar,fill,color,normalize(IdentityTransformation(),user,scale,pt))
	    fill.clearClip()
	    color.clearClip()
	  }
	  case CTexturedShape(s : OpShape,tex : NativeTexture) => renderNative(user,new java.awt.geom.AffineTransform(),color,ts)
	  case CTexturedShape(s : OpShape, tex) => renderNonNative(user,area,parea,userTrans,fill,color,s,tex)
	}}
	
	def getWind(op : ShapeSetOperation) = op match {
	    case Union() => java.awt.geom.Path2D.WIND_NON_ZERO
	    case Symdiff() => java.awt.geom.Path2D.WIND_EVEN_ODD
	  }
	
	
	def render(area : AABBox, state : java.awt.Graphics, tsl : Drawing) ={
	  	  val scal = min(area.width,area.height) / 2
	  	  	  val err = DeformFunctions.numericError /scal
	  val errsquared = err * err
	    val toUser = composeReal(errsquared,scal,Transforms.translate(area.width / 2, area.height /2),Transforms.scale(scal))
	     val toUserAff = toUser match {
	    case AffineTransformation(a,b) => AffineTransformation(a,b)
	    case _ => throw new Error("Cast failed")
	  }
	  val fill = new FillJava2DBuffer(toJava2DTransform(toUserAff),area)
	  val color = new ColorJava2DBuffer(toJava2DTransform(toUserAff),area)
	 
	  val tsln = normalize(IdentityTransformation(),errsquared,scal,tsl)
	  val userArea= AABBox(Interval((-area.width/2) /scal,(area.width/2) /scal),Interval((-area.height/2),(area.height/2)/scal))
	  tsln.foreach(ts => tryRenderOne(area,errsquared,scal,toUserAff,userArea,fill,color,ts))
	  state.drawImage(color.img, area.xInterval.low.toInt, area.yInterval.low.toInt,null)
	}
  
  private def renderNonNative(user: Double,area : AABBox,parea : AABBox, userTrans : AffineTransformation,fill: deform.FillJava2DBuffer, color: deform.ColorJava2DBuffer, s : deform.OpShape,tex : deform.Texture): Unit = {
      val concrete = toBezierReal(user,s.cpaths)
      val aabbd = getAABB(concrete)
      val aabbr = aabbd.intersect(area)
      val aabbc = userTrans.transformBBox match { case Some(s) => s(aabbr)}
      val aabb = aabbc.intersect(parea)
      val geom = getJava2DShape(getWind(s.op),concrete)
	  fill.draw(aabbr.grow(1),geom)
	  tex match {
	    case FTexture(f,None) => 
	     renderTexJob(0,aabb.widthInt * aabb.heightInt * 10, userTrans,color,fill,aabb,parea,f)
	    case FTexture(f,Some(par)) => {
	    	val nrJobs = floor(aabb.widthInt * aabb.heightInt /par).toInt + 1
	    	(0 until nrJobs).map(x => x * par).par.map( x=>  
	    	    renderTexJob(x,x+par, userTrans,color,fill,aabb,parea,f)
	     	)
	    }
	}
  }
  
  def renderTexJob(start : Int, end : Int, userTrans : AffineTransformation,color : deform.ColorJava2DBuffer,fill: deform.FillJava2DBuffer, aabb: AABBox, parea: AABBox, f : Point => Color) = {
     var i = color.lineIndexes * aabb.yInterval.low.toInt + aabb.xInterval.low.toInt * 4
	  var j = color.area.widthInt * aabb.yInterval.low.toInt + aabb.xInterval.low.toInt
	  var endOfLineSkip = color.area.widthInt - aabb.widthInt
	  var extra =0 
	  if(endOfLineSkip > 0){
	    endOfLineSkip = endOfLineSkip - 1
	    extra = 1;
	  }
     var count = 0
	  val endOfLineSkipColor = endOfLineSkip*4
     var y = aabb.yInterval.low.toInt
	      var yh = aabb.yInterval.low.toInt + aabb.heightInt
	      if(yh < parea.heightInt) yh = yh + 1
	      var xInit = aabb.xInterval.low.toInt
	      var x = xInit
	      var xh = aabb.xInterval.low.toInt + aabb.widthInt + extra
	        while(count < end && y < yh){
	        	while(count < end && x < xh){
	        		if(count >= start && count < end) {
		        	    val px = fill.getPixel(j)
		        		if(px >= 0){
		        			color.setPixel(i,f(userTrans.backwards * Point(x+.5,y+.5))*px)
		        		}
	        		}
	        		i = i+4
	        		j = j+1
	        		x = x + 1
	        		count= count + 1
	        		
	        	}
	        	y = y + 1
	        	x = xInit
	        	i+=endOfLineSkipColor
	        	j+=endOfLineSkip
	        }
  }
	
}