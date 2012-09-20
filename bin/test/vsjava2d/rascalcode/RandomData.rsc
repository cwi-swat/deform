module RandomData


import util::Math;
import Boolean;
data VisDatum = 
	litShape(Affine trans, Fill fill, int shape);
	
alias VisData = list[VisDatum];
alias GradientData = list[tuple[real frac,int color]];
data Fill = fillColor(int color) |
	    linearGradient(GradientData colors) |
	    radialGradient(GradientData colors) |
	    imageFill(int img);
	    
data Affine = id() | 
	      shear(real x, real y,Affine rest) |
	      scale(real x, real y,Affine rest) |
	      rotate(real x,Affine rest) |
	      translate(real x, real y,Affine rest);
	  
public int nrShapes = 24;
public int nrImages = 4;
public int nrColors = 10;
public real minX = 0.;
public real minY = 0.;
public real maxX = 800.;
public real maxY = 600.;
public real maxShear = 10.;
public real maxZoom = 10.;
public int maxGradientLength = 8;
	      
public VisData randomVisData(int nrDatums) =  [randomVisDatum() | i <- [1..nrDatums]];

public VisDatum randomVisDatum() = litShape(arbTrans(), arbFill(), arbInt(nrShapes));

Affine arbTrans() {
	Affine result = id();
	//if(arbBool()){
	//	result = shear(arbReal()*maxShear, arbReal() * maxShear,result);
	//}
	if(arbBool()){
		result =scale(arbReal()*maxZoom, arbReal() * maxZoom,result);
	}
	if(arbBool()){
		result =rotate(arbReal()*2*PI(),result);
	}
	return translate(arbReal()*(maxX-minX) + minX, arbReal() * (maxY - minY) + minY,result);
}

Fill arbFill() {
	int i = arbInt(3);
	switch(i) {
		//case 0 : return fillColor(arbInt(nrColors));
		case 0: return linearGradient(arbGradientData());
		case 1: return radialGradient(arbGradientData());
		case 2: return imageFill(arbInt(nrImages));
	}
	throw i;
}

GradientData arbGradientData() {
	int nrColors = arbInt(maxGradientLength-2) + 2;
	return [<toReal(i)/toReal(nrColors),arbInt(nrColors)> | i <- [0..nrColors-1]];
}