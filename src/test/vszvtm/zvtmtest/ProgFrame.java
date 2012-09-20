/*   FILE: ProgFrame.java
 *   DATE OF CREATION:  Thu Dec 30 14:45:09 2004
 *   AUTHOR :           Emmanuel Pietriga (emmanuel.pietriga@inria.fr)
 *   MODIF:             Emmanuel Pietriga (emmanuel.pietriga@inria.fr)
 *   Copyright (c) INRIA, 2004-2009. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 *
 * $Id: ProgFrame.java 4274 2011-02-25 07:45:51Z epietrig $
 */ 

package test.vszvtm.zvtmtest;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/*implements a JFrame with a progress bar with methods to change the bar value and the text displayed just above it*/

public class ProgFrame extends JFrame {

    JLabel l1;
    JProgressBar jpb;

    ProgFrame(String label,String title){
	Container cpane=this.getContentPane();
	GridBagLayout gridBag=new GridBagLayout();
	GridBagConstraints constraints=new GridBagConstraints();
	constraints.fill=GridBagConstraints.HORIZONTAL;
	constraints.anchor=GridBagConstraints.WEST;
	cpane.setLayout(gridBag);
	l1=new JLabel(label);
	buildConstraints(constraints,0,0,1,1,100,50);
	gridBag.setConstraints(l1,constraints);
	cpane.add(l1);
	jpb=new JProgressBar();
	jpb.setMinimum(0);
	jpb.setMaximum(100);
	jpb.setStringPainted(false);
	buildConstraints(constraints,0,1,1,1,100,50);
	gridBag.setConstraints(jpb,constraints);
	cpane.add(jpb);
	this.setTitle(title);
	this.pack();
	Dimension screenSize=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	this.setLocation((screenSize.width-300)/2,(screenSize.height-100)/2);
	this.setSize(300,100);
	this.setVisible(true);
    }

    void setPBValue(int i){
	jpb.setValue(i);
    }

    void setLabel(String s){
	l1.setText(s);
    }

    void destroy(){
	this.setVisible(false);
	this.dispose();
    }

    void buildConstraints(GridBagConstraints gbc, int gx,int gy,int gw,int gh,int wx,int wy){
	gbc.gridx=gx;
	gbc.gridy=gy;
	gbc.gridwidth=gw;
	gbc.gridheight=gh;
	gbc.weightx=wx;
	gbc.weighty=wy;
    }

}
