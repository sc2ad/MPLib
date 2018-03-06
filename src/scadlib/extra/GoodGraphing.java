package scadlib.extra;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import scadlib.paths.Util;

/**
 * This class is a basic plotting class using the Java AWT interface. It has basic features which allow the user 
 * to plot multiple graphs on one figure, control axis dimensions, and specify colors.
 * 
 * This is by all means not an extensive plotter, but it will help visualize data very quickly and accurately. If
 * a more robust plotting function is required, the user is encouraged to use Excel or Matlab. The purpose of this
 * class is to be easy to use with enought automation to have nice graphs with minimal effort, but give the user
 * control over as much as possible, so they can generate the perfect chart.
 * 
 * The plotter also features the ability to capture screen shots directly from the right-click menu, this allows
 * the user to copy and paste plots into reports or other documents rather quickly.
 * 
 * This class holds an interface similar to that of Matlab. 
 * 
 * This class currently only supports scatterd line charts.
 * 
 * @author Kevin Harrilal
 * @email kevin@team2168.org
 * @version 1
 * @date 9 Sept 2014
 *
 */
 
public class GoodGraphing extends JPanel implements ClipboardOwner{
  
    
	private static final long serialVersionUID = 3205256608145459434L;
	private final int yPAD = 60; //controls how far the X- and Y- axis lines are away from the window edge
    private final int xPAD = 70; //controls how far the X- and Y- axis lines are away from the window edge
    
    private double upperXtic;
    private double lowerXtic;
    private double upperYtic;
    private double lowerYtic;
    private boolean yGrid;
    private boolean xGrid;
    
    private double yMax;
    private double yMin;
    private double xMax;
    private double xMin;
    
    private int yticCount;
    private int xticCount;
    private double xTicStepSize;
    private double yTicStepSize;
    
    boolean userSetYTic;
    boolean userSetXTic;
    
    private String xLabel;
    private String yLabel;
    private String titleLabel;
    protected static int count = 0;
    
    JPopupMenu menu = new JPopupMenu("Popup");
    
    //Link List to hold all different plots on one graph.
    private LinkedList<xyNode> link; 
    
    private int leftIndex = -1;
    private int centerIndex = -1;
    private int rightIndex = -1;
    
    private JSlider slider;
    private PathData pathData;
    

    /**
     * Constructor which Plots only Y-axis data.
     * @param yData is a array of doubles representing the Y-axis values of the data to be plotted.
     */
    public GoodGraphing(double[] yData)
    {
    	this(null,yData,Color.red);
    }
    
    public GoodGraphing(double[] yData,Color lineColor, Color marker)
    {
    	this(null,yData,lineColor,marker);
    }
    
    /**
     * Constructor which Plots chart based on provided x and y data. X and Y arrays must be of the same length.
     * @param xData is an array of doubles representing the X-axis values of the data to be plotted.
     * @param yData is an array of double representing the Y-axis values of the data to be plotted.
     */
    public GoodGraphing(double[] xData, double[] yData)
    {
    	this(xData,yData,Color.red,null);
    }
    
    /**
     * Constructor which Plots chart based on provided x and y axis data. 
     * @param data is a 2D array of doubles of size Nx2 or 2xN. The plot assumes X is the first dimension, and y data
     * is the second dimension.
     */
    public GoodGraphing(double[][] data)
    {
    	this(getXVector(data),getYVector(data),Color.red,null);
    }
    
/**
 * Constructor which plots charts based on provided x and y axis data in a single two dimensional array.
 * @param data is a 2D array of doubles of size Nx2 or 2xN. The plot assumes X is the first dimension, and y data
 * is the second dimension.
 * @param lineColor is the color the user wishes to be displayed for the line connecting each datapoint
 * @param markerColor is the color the user which to be used for the data point. Make this null if the user wishes to
 * not have datapoint markers.
 */
    public GoodGraphing(double[][] data, Color lineColor, Color markerColor)
    {
    	this(getXVector(data),getYVector(data),lineColor,markerColor);
    }
    
    /**
     * Constructor which plots charts based on provided x and y axis data provided as separate arrays. The user can also specify the color of the adjoining line.
     * Data markers are not displayed.
     * @param xData is an array of doubles representing the X-axis values of the data to be plotted.
     * @param yData is an array of double representing the Y-axis values of the data to be plotted.
     * @param lineColor is the color the user wishes to be displayed for the line connecting each datapoint
     */
    public GoodGraphing(double[] xData, double[] yData,Color lineColor)
    {
    	this(xData,yData,lineColor,null);
    }
    

    
    /**
     * Constructor which plots charts based on provided x and y axis data, provided as separate arrays. The user 
     * can also specify the color of the adjoining line and the color of the datapoint maker.
     * @param xData is an array of doubles representing the X-axis values of the data to be plotted.
     * @param yData is an array of double representing the Y-axis values of the data to be plotted.
     * @param lineColor is the color the user wishes to be displayed for the line connecting each datapoint
     * @param markerColor is the color the user which to be used for the data point. Make this null if the user wishes to
     * not have datapoint markers.
     */
    public GoodGraphing(double[] xData, double[] yData,Color lineColor, Color markerColor)
    {
    	xLabel = "X axis";
    	yLabel = "Y axis";
    	titleLabel = "Title";
    	
       upperXtic = -Double.MAX_VALUE;
       lowerXtic = Double.MAX_VALUE;
       upperYtic = -Double.MAX_VALUE;
       lowerYtic = Double.MAX_VALUE;
       xticCount = -Integer.MAX_VALUE;
       
       this.userSetXTic = false;
       this.userSetYTic = false;
    	
    	link = new LinkedList<xyNode>();
    	
    	addData(xData, yData, lineColor,markerColor);
    	
    	count ++;
    	JFrame g = new JFrame("Figure " + count);
        g.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        g.add(this);
        slider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 1);
        slider.addChangeListener(new SliderListener(0, this));
        add(slider);
        g.setSize(600,400);
        g.setLocationByPlatform(true);
        g.setVisible(true);
         
        menu(g,this);	
    }
    public GoodGraphing(double[][] leftData, double[][] centerData, double[][] rightData, double[][] nodeData, Color leftColor, Color centerColor, Color rightColor) {
    	this(getXVector(leftData), getYVector(leftData), leftColor, null);
    	addData(centerData, centerColor);
    	addData(rightData, rightColor);
    	addData(nodeData, Color.black);
    	
    	slider.setMaximum(centerData.length);
    	
    	leftIndex = 0;
    	centerIndex = 1;
    	rightIndex = 2;
    }
    public GoodGraphing(PathData pd) {
    	this(pd.path.leftPath, pd.path.smoothPath, pd.path.rightPath, pd.path.nodeOnlyPath, pd.lColor, pd.cColor, pd.rColor);
    	pathData = pd;
    }
    
    /**
     * Adds a plot to an existing figure.  
     * @param y is a array of doubles representing the Y-axis values of the data to be plotted.
     * @param lineColor is the color the user wishes to be displayed for the line connecting each datapoint
     */
    
    public void addData(double[] y, Color lineColor)
    {
    	addData(y, lineColor, null);
    }
    
    public void addData(double[] y, Color lineColor, Color marker)
    {
    	//cant add y only data unless all other data is y only data
    	for(xyNode data: link)
    		if(data.x != null)
    			throw new Error ("All previous chart series need to have only Y data arrays");
    	
    	addData(null,y,lineColor, marker);
    }
    
    public void addData(double[] x, double[] y, Color lineColor)
    {
    	addData(x,y,lineColor,null);
    }
    
    
    public void addData(double[][] data, Color lineColor)
    {
    	addData(getXVector(data),getYVector(data),lineColor,null);
    }
    
    public void addData(double[][] data, Color lineColor, Color marker)
    {
    	addData(getXVector(data),getYVector(data),lineColor,marker);
    }
    
    public void addData(double[] x, double[] y, Color lineColor, Color marker)
    { 	
    	xyNode Data = new xyNode();
    	    	
    	//copy y array into node
    	Data.y = new double[y.length];
    	Data.lineColor = lineColor;
    	
    	if(marker == null)
    		Data.lineMarker = false;
    	else
    	{
    		Data.lineMarker = true;
    		Data.markerColor = marker;
    	}
    	for(int i=0; i<y.length; i++)
    		Data.y[i] = y[i];
    	
    	//if X is not null, copy x
    	if(x != null)
    	{
        	//cant add x, and y data unless all other data has x and y data
        	
        	for(xyNode data: link)
        		if(data.x == null)
        			throw new Error ("All previous chart series need to have both X and Y data arrays");
    		
    		if(x.length != y.length)
    			throw new Error("X dimension must match Y dimension");
    		
    		Data.x = new double[x.length];
    		
    		for(int i=0; i<x.length; i++)
        		Data.x[i] = x[i];
    		
    	}
    	link.add(Data);
    }
    public void addData(int location, double[] x, double[] y, Color lineColor, Color marker)
    { 	
    	xyNode Data = new xyNode();
    	    	
    	//copy y array into node
    	Data.y = new double[y.length];
    	Data.lineColor = lineColor;
    	
    	if(marker == null)
    		Data.lineMarker = false;
    	else
    	{
    		Data.lineMarker = true;
    		Data.markerColor = marker;
    	}
    	for(int i=0; i<y.length; i++)
    		Data.y[i] = y[i];
    	
    	//if X is not null, copy x
    	if(x != null)
    	{
        	//cant add x, and y data unless all other data has x and y data
        	
        	for(xyNode data: link)
        		if(data.x == null)
        			throw new Error ("All previous chart series need to have both X and Y data arrays");
    		
    		if(x.length != y.length)
    			throw new Error("X dimension must match Y dimension");
    		
    		Data.x = new double[x.length];
    		
    		for(int i=0; i<x.length; i++)
        		Data.x[i] = x[i];
    		
    	}
    	link.add(location, Data);
    }
 
    /**
     * Main method which paints the panel and shows the figure.
     * 
     */
    @Override
    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
        Graphics2D g2 =  (Graphics2D)g;
        drawTime(slider.getValue(), g2);
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        
//        int w = getWidth();
//        int h = getHeight();
//        
//        Line2D yaxis = new Line2D.Double(xPAD, yPAD, xPAD, h-yPAD);
//        Line2D.Double xaxis = new Line2D.Double(xPAD, h-yPAD, w-xPAD, h-yPAD);
//        g2.draw(yaxis); 
//        g2.draw(xaxis);
//        
//        //find Max Y limits
//        getMinMax(link);
//        
//        //draw ticks
//        drawYTickRange(g2, yaxis, 15, yMax, yMin);
//        drawXTickRange(g2, xaxis, 15, xMax, xMin);
//        
//        //plot all data
//        plot(g2);
//        
//        //draw x and y labels
//        setXLabel(g2, xLabel);
//        setYLabel(g2, yLabel);
//        setTitle(g2, titleLabel);
    }
    private class SliderListener implements ChangeListener {
    	public int timeValue;
    	public GoodGraphing good;
    	public SliderListener(int t, GoodGraphing goog) {
    		timeValue = t;
    		good = goog;
    	}

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
        	timeValue = source.getValue();
        	good.drawTime(timeValue, (Graphics2D)good.getGraphics());
		}
    }
    
    
    public void setXTic(double lowerBound, double upperBound, double stepSize)
    {
    	this.userSetXTic = true;
    	
    	this.upperXtic=upperBound;
    	this.lowerXtic=lowerBound;
    	this.xTicStepSize = stepSize;
    }
    
    public void setYTic(double lowerBound, double upperBound, double stepSize)
    {
    	this.userSetYTic = true;
    	
    	this.upperYtic=upperBound;
    	this.lowerYtic=lowerBound;
    	this.yTicStepSize = stepSize;
    }
    
    private void plot(Graphics2D g2)
    {
    	
    	int w = super.getWidth();
    	int h = super.getHeight();
    	
    	Color tempC = g2.getColor();
    	
    	//loop through list and plot each
    	for(int i=0; i<link.size(); i++)
    	{
	        // Draw lines.
	        double xScale = (double)(w - 2*xPAD)/(upperXtic-lowerXtic);
	        double yScale = (double)(h - 2*yPAD)/(upperYtic-lowerYtic);
	        
	        for(int j = 0; j < link.get(i).y.length-1; j++) 
	        {
	        	double x1;
	        	double x2;
	        	
	            if(link.get(i).x==null)
	            {
	            	x1 = xPAD + j*xScale;
	            	x2 = xPAD + (j+1)*xScale;
	            }
	            else
	            {
	            	x1 = xPAD + xScale*link.get(i).x[j] + lowerXtic*xScale;
	            	x2 = xPAD + xScale*link.get(i).x[j+1] + lowerXtic*xScale;
	            }
	            
	            double y1 = h - yPAD - yScale*link.get(i).y[j] + lowerYtic*yScale;
	            
	            double y2 = h - yPAD - yScale*link.get(i).y[j+1] + lowerYtic*yScale;
	            g2.setPaint(link.get(i).lineColor);
	            g2.draw(new Line2D.Double(x1, y1, x2, y2));
	            
	            if(link.get(i).lineMarker)
	            {
	            	g2.setPaint(link.get(i).markerColor);
	            	g2.fill(new Ellipse2D.Double(x1-2, y1-2, 4, 4));
	            	g2.fill(new Ellipse2D.Double(x2-2, y2-2, 4, 4));
	            }
	      
	        }
	        	        	
    	}
    	
    	g2.setColor(tempC);
    }
    
    public void drawTime(int time, Graphics2D g2) {
//    	paintComponent(g2);
    	int w = getWidth();
        int h = getHeight();    
        g2.clearRect(0, 0, w, h);

    	super.paintComponent(g2);    
        
        Line2D yaxis = new Line2D.Double(xPAD, yPAD, xPAD, h-yPAD);
        Line2D.Double xaxis = new Line2D.Double(xPAD, h-yPAD, w-xPAD, h-yPAD);
        g2.draw(yaxis); 
        g2.draw(xaxis);
      //find Max Y limits
        getMinMax(link);
        
        //draw ticks
        drawYTickRange(g2, yaxis, 15, yMax, yMin);
        drawXTickRange(g2, xaxis, 15, xMax, xMin);
        
        //plot all data
        plot(g2);
        
        //draw x and y labels
        setXLabel(g2, xLabel);
        setYLabel(g2, yLabel);
        setTitle(g2, titleLabel);
        
        add(slider);
        
    	int robotLength = 32;
    	for (int i = 0; i < link.size(); i++) {
			if (leftIndex != -1) {
				if (time < link.get(i).x.length) {
					double xScale = (double)(w - 2*xPAD)/(upperXtic-lowerXtic);
			        double yScale = (double)(h - 2*yPAD)/(upperYtic-lowerYtic);
					
					int width = 2;
					int height = 2;
					int x1 = (int) (xPAD + xScale*(int)link.get(leftIndex).x[time] + lowerXtic*xScale);
					int y1 = (int) (h - yPAD - yScale*(int)link.get(leftIndex).y[time] + lowerYtic*yScale);
					int x2 = (int) (xPAD + xScale*(int)link.get(rightIndex).x[time] + lowerXtic*xScale);
					int y2 = (int) (h - yPAD - yScale*(int)link.get(rightIndex).y[time] + lowerYtic*yScale);
					g2.drawRect(x1, y1, width, height);
					g2.drawRect(x2, y2, width, height);
				}
			}
    	}
    }
    
    /**
     * need to optimize for loops
     * @param list
     */
    private void getMinMax(LinkedList<xyNode> list)
    {
    	for(xyNode node: list)
    	{
    		double tempYMax = getMax(node.y);
    		double tempYMin = getMin(node.y);
    		
    		if(tempYMin<yMin)
    			yMin = tempYMin;
    		
    		if(tempYMax>yMax)
    			yMax=tempYMax;
    		
    		if(xticCount < node.y.length)
				xticCount = node.y.length;
    		
    		
    		if(node.x != null)
    		{
        		double tempXMax = getMax(node.x);
        		double tempXMin = getMin(node.x);
        		
        		if(tempXMin<xMin)
        			xMin = tempXMin;
        		
        		if(tempXMax>xMax)
        			xMax=tempXMax;
        		
    		}
    		else
    		{
    			xMax=node.y.length-1;
    			xMin=0;
    			
    		}
    		
    	}
    	
    }
 
    private double getMax(double[] data) {
        double max = -Double.MAX_VALUE;
        for(int i = 0; i < data.length; i++) {
            if(data[i] > max)
                max = data[i];
        }
        return max;
    }
    
    private double getMin(double[] data) {
        double min = Double.MAX_VALUE;
        for(int i = 0; i < data.length; i++) {
            if(data[i] < min)
                min = data[i];
        }
        return min;
    }
    
    public void setYLabel(String s)
    {
    	yLabel = s;
    }
    
    public void setXLabel(String s)
    {
    	xLabel = s;
    }
    
    public void setTitle(String s)
    {
    	titleLabel = s;
    }
    
    private void setYLabel(Graphics2D g2, String s)
    {
    	FontMetrics fm = getFontMetrics(getFont());
    	int width = fm.stringWidth(s);
    	
    	AffineTransform temp = g2.getTransform();
    	
    	    AffineTransform at = new AffineTransform();
    	    at.setToRotation(-Math.PI /2, 10, getHeight()/2+width/2);
    	    g2.setTransform(at);
    	    
    	    //draw string in center of y axis
    	    g2.drawString(s, 10, 7+getHeight()/2+width/2);

        g2.setTransform(temp);
    	
    }
    
    private void setXLabel(Graphics2D g2, String s)
    {
    	FontMetrics fm = getFontMetrics(getFont());
    	int width = fm.stringWidth(s);
    	
    	g2.drawString(s, getWidth()/2-(width/2), getHeight()-10);
    }
    
    private void setTitle(Graphics2D g2, String s)
    {
    	FontMetrics fm = getFontMetrics(getFont());
    	
    	String[] line = s.split("\n");
    	
    	int height = xPAD/2 - ((line.length-1) * fm.getHeight()/2);
    	
    	for (int i=0; i<line.length; i++)
    	{
    		
    		int width = fm.stringWidth(line[i]);
            g2.drawString(line[i], getWidth()/2-(width/2),  height);
            height +=fm.getHeight();
            
    	}
   
    }

    
    public void yGridOn()
    {
    	yGrid=true;
    	//super.repaint();
    }
    
    public void yGridOff()
    {
    	yGrid=false;
    	//super.repaint();
    }
    
    public void xGridOn()
    {
    	xGrid=true;
    	//super.repaint();
    }
    
    public void xGridOff()
    {
    	xGrid=false;
    	//super.repaint();
    }
    
    private void drawYTickRange(Graphics2D g2, Line2D yaxis, int tickCount, double Max, double Min)
    {
    	if(!userSetYTic)
    	{
    	double range = Max - Min;
    	
    	
    	//calculate max Y and min Y tic Range
    	double unroundedTickSize = range/(tickCount-1);
    	double x = Math.ceil(Math.log10(unroundedTickSize)-1);
    	double pow10x = Math.pow(10, x);
    	yTicStepSize = Math.ceil(unroundedTickSize / pow10x) * pow10x;
    	
    	//determine min and max tick label 
        if(Min<0)
        	lowerYtic = yTicStepSize * Math.floor(Min/yTicStepSize);
        else
        	lowerYtic = yTicStepSize * Math.ceil(Min/yTicStepSize);
        
        if(Max<0)
        	upperYtic = yTicStepSize * Math.floor(1+Max/yTicStepSize);
        else
        	upperYtic = yTicStepSize * Math.ceil(1+Max/yTicStepSize);
        
    	}
    	
        
        double x0 = yaxis.getX1();
        double y0 = yaxis.getY1();
        double xf = yaxis.getX2();
        double yf = yaxis.getY2();
        
        //calculate stepsize between ticks and length of Y axis using distance formula
        int roundedTicks = (int) ((upperYtic - lowerYtic) / yTicStepSize);
        double distance = Math.sqrt(Math.pow((xf-x0), 2)+Math.pow((yf-y0), 2)) / roundedTicks;
        
        double upper = upperYtic;
        for (int i = 0; i<=roundedTicks; i++)
        {
        	double newY = y0;
        	
        	//calculate width of number for proper drawing
        	String number = new DecimalFormat("#.#").format(upper);
        	FontMetrics fm = getFontMetrics(getFont());
        	int width = fm.stringWidth(number);
   
        	g2.draw(new Line2D.Double(x0,newY, x0-10,newY));
        	g2.drawString(number, (float)x0-15-width, (float)newY+5); 
        	 
        	//add grid lines to chart
        	if(yGrid && i!=roundedTicks)
        	{

        		Stroke tempS = g2.getStroke();
        		Color tempC = g2.getColor();
        		
        		g2.setColor (Color.lightGray);
          	    g2.setStroke (new BasicStroke(
          	      1f, 
          	      BasicStroke.CAP_ROUND, 
          	      BasicStroke.JOIN_ROUND, 
          	      1f, 
          	      new float[] {5f}, 
          	      0f));
          	    
          	    g2.draw(new Line2D.Double(xPAD, newY, getWidth()-xPAD, newY));
        		
          	    g2.setColor(tempC);
          	    g2.setStroke(tempS);
        		
        	}
        	
        	  upper = upper - yTicStepSize;
        	y0 = newY + distance;
        	
        }
    }
    
    private void drawXTickRange(Graphics2D g2, Line2D xaxis, int tickCount, double Max, double Min)
    {
    	drawXTickRange(g2, xaxis, tickCount, Max, Min, 1);
    }
    
    private void drawXTickRange(Graphics2D g2, Line2D xaxis, int tickCount, double Max, double Min, double skip)
    {
    	if(!userSetXTic)
    	{
    	double range = Max - Min;
    	
    	//calculate max Y and min Y tic Range
    	double unroundedTickSize = range/(tickCount-1);
    	double x = Math.ceil(Math.log10(unroundedTickSize)-1);
    	double pow10x = Math.pow(10, x);
    	xTicStepSize = Math.ceil(unroundedTickSize / pow10x) * pow10x;
    	
    	//determine min and max tick label 
        if(Min<0)
        	lowerXtic = xTicStepSize * Math.floor(Min/xTicStepSize);
        else
        	lowerXtic = xTicStepSize * Math.ceil(Min/xTicStepSize);
        
        if(Max<0)
        	upperXtic = xTicStepSize * Math.floor(1+Max/xTicStepSize);
        else
        	upperXtic = xTicStepSize * Math.ceil(1+Max/xTicStepSize);
    	}
        
        
        double x0 = xaxis.getX1();
        double y0 = xaxis.getY1();
        double xf = xaxis.getX2();
        double yf = xaxis.getY2();
        
        //calculate stepsize between ticks and length of Y axis using distance formula
        int roundedTicks = (int) ((upperXtic - lowerXtic) / xTicStepSize);
        
        
        double distance = Math.sqrt(Math.pow((xf-x0), 2)+Math.pow((yf-y0), 2)) / roundedTicks;
        
        double lower = lowerXtic;
        for (int i = 0; i<=roundedTicks; i++)
        {
        	double newX = x0;
        	
        	//calculate width of number for proper drawing
        	String number = new DecimalFormat("#.#").format(lower);
        	FontMetrics fm = getFontMetrics( getFont() );
        	int width = fm.stringWidth(number);
        	
   
        	g2.draw(new Line2D.Double(newX,yf, newX,yf+10));
        	
        	//dont label every x tic to prevent clutter
        	if(i%skip==0)
       		g2.drawString(number, (float)(newX-(width/2.0)), (float)yf+25); 
        	
        	//add grid lines to chart
        	if(xGrid && i!=0)
        	{
        		Stroke tempS = g2.getStroke();
        		Color tempC = g2.getColor();
        		
        		g2.setColor (Color.lightGray);
          	    g2.setStroke (new BasicStroke(
          	      1f, 
          	      BasicStroke.CAP_ROUND, 
          	      BasicStroke.JOIN_ROUND, 
          	      1f, 
          	      new float[] {5f}, 
          	      0f));
          	    
          	    g2.draw(new Line2D.Double(newX, yPAD, newX, getHeight()-yPAD));
        		
          	    g2.setColor(tempC);
          	    g2.setStroke(tempS);
        		
        	}
        	
        	 
        	lower = lower + xTicStepSize;
        	x0 = newX + distance;
        }
    }
    
    public void updateData(int series, double[][] data)
    {
    	//add Data to link list
    	addData(data,null,null);
    	
    	//copy data from new to old and line styles from list to new list.
    	
    	link.get(series).x = link.getLast().x.clone();
    	link.get(series).y = link.getLast().y.clone();
    	
    	//remove last data
    	link.removeLast();
    
    	
    	
    }
    
	public static double[] getXVector(double[][] arr)
	{
		double[] temp = new double[arr.length];

		for(int i=0; i<temp.length; i++)
			temp[i] = arr[i][0];

		return temp;		
	}

	public static double[] getYVector(double[][] arr)
	{
		double[] temp = new double[arr.length];

		for(int i=0; i<temp.length; i++)
			temp[i] = arr[i][1];

		return temp;		
	}
	
	private double[] convertToRealSpace(double xPixel, double yPixel) {
		double w = getWidth();
		double h = getHeight();
		
		double xScale = (double)(w - 2*xPAD)/(upperXtic-lowerXtic);
        double yScale = (double)(h - 2*yPAD)/(upperYtic-lowerYtic);
		
        // xPixel = xPAD + xScale*REALVALUE + lowerXtic * xScale;
        // yPixel = h - yPAD - yScale*REALVALUE + lowerYtic * yScale;
        double realX = ((double)xPixel - xPAD - lowerXtic * xScale) / xScale;
        double realY = ((double)yPixel - h + yPAD - lowerYtic * yScale) / -yScale;
        return new double[]{realX, realY};
	}
	private double[] convertMouseToRealSpace(double xPixel, double yPixel) {
		double xOffset = -8;
		double yOffset = -30;
		return convertToRealSpace(xPixel+xOffset, yPixel+yOffset);
	}
	private double[] convertToVirtualSpace(double realX, double realY) {
		double w = getWidth();
		double h = getHeight();
		
		double xScale = (double)(w - 2*xPAD)/(upperXtic-lowerXtic);
        double yScale = (double)(h - 2*yPAD)/(upperYtic-lowerYtic);
		
        double xPixel = xPAD + xScale*realX + lowerXtic * xScale;
        double yPixel = h - yPAD - yScale*realY + lowerYtic * yScale;
        return new double[]{xPixel, yPixel};
	}
	private void updatePathData() {
		System.out.println("\n\n\n\n=======================");
		
		int length = 4; // Remove first 3! (left, center, right, nodePath)
		for (int i = 0; i < length; i++) {
			link.removeFirst();
		}
		System.out.println("double[][] arr = new double[][]{");
		for (int i = 0; i < pathData.waypoints.length; i++) {
			System.out.println("\t{"+pathData.waypoints[i][0]+", "+pathData.waypoints[i][1]+"},");
		}
		System.out.println("};");
		// Cleared array, now add new data
		addData(0, getXVector(pathData.path.nodeOnlyPath), getYVector(pathData.path.nodeOnlyPath), Color.black, null);
		addData(0, getXVector(pathData.path.rightPath), getYVector(pathData.path.rightPath), pathData.rColor, null);
		addData(0, getXVector(pathData.path.smoothPath), getYVector(pathData.path.smoothPath), pathData.cColor, pathData.cColor);
		addData(0, getXVector(pathData.path.leftPath), getYVector(pathData.path.leftPath), pathData.lColor, null);
//		link.add(0, new xyNode(getXVector(pathData.path.rightPath), getYVector(pathData.path.rightPath), pathData.rColor, false));
//		link.add(0, new xyNode(getXVector(pathData.path.smoothPath), getYVector(pathData.path.smoothPath), pathData.cColor, true));
//		link.add(0, new xyNode(getXVector(pathData.path.leftPath), getYVector(pathData.path.leftPath), pathData.lColor, false));
	}
	
	/**********Class for Linked List************/
    private class xyNode
    {
    	double[] x;
    	double[] y;
    	Color lineColor;
    	
    	boolean lineMarker;
    	Color markerColor;
    	
    	public xyNode()
    	{
    		x=null;
    		y=null;
    		
    		lineMarker = false;
    	}
    	public xyNode(double[] x, double[] y, Color markerColor, boolean lineMarker) {
    		this.x = x;
    		this.y = y;
    		this.markerColor = markerColor;
    		this.lineMarker = lineMarker;
    	}
    }
	
    /****Methods to Support Right Click Menu****/
    @Override
	public void lostOwnership(Clipboard    clip, Transferable transferable) 
	{
		//We must keep the object we placed on the system clipboard
		//until this method is called.
	}

    
    
    private void menu(JFrame g, final GoodGraphing p )
    {

    	g.addMouseListener(new PopupTriggerListener());
    	p.getInputMap().put(KeyStroke.getKeyStroke((char) KeyEvent.VK_T), new TimeChangeAction());
    	
    	JMenuItem item = new JMenuItem("Copy Figure");

        item.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
            
            BufferedImage i = new BufferedImage(p.getSize().width, p.getSize().height,BufferedImage.TRANSLUCENT);
            p.setOpaque(false);
            p.paint(i.createGraphics()); 
            TransferableImage trans = new TransferableImage( i );
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			c.setContents( trans, p);
          }
        });
        
        menu.add(item);
    	
        item = new JMenuItem("Desktop ScreenShot");
        item.addActionListener(new ActionListener() 
        {
          public void actionPerformed(ActionEvent e) 
          {
            System.out.println("Copy files to clipboard");
            
            try {
                Robot robot = new Robot();
                Dimension screenSize  = Toolkit.getDefaultToolkit().getScreenSize();
                Rectangle screen = new Rectangle( screenSize );
                BufferedImage i = robot.createScreenCapture( screen );
                TransferableImage trans = new TransferableImage( i );
                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                c.setContents( trans, p);
            }
            catch ( AWTException x ) {
                x.printStackTrace();
                System.exit( 1 );
            }
            
          }
        });
        
        menu.add(item);
    }

    class PopupTriggerListener extends MouseAdapter {
    	
    	int holdingIndex = -1;
    	boolean didSelect = false;
    	private final double buffer = 5;
    	
    	public void mousePressed(MouseEvent ev) {
    		if (ev.isPopupTrigger()) {
    			menu.show(ev.getComponent(), ev.getX(), ev.getY());
    		} if (ev.getButton() == MouseEvent.BUTTON3 && pathData != null) {
    			int index = getClosestWaypointIndex(buffer, convertMouseToRealSpace(ev.getX(), ev.getY()));
    			if (index != -1) {
    				// There is a waypoint here! remove it.
    				int writeInd = 0;
    				int readInd = 0;
    				System.out.println("Removing waypoint at index: "+index+" real coordinates: ("+pathData.waypoints[index][0]+", "+pathData.waypoints[index][1]+")");
    				double[][] temp = new double[pathData.waypoints.length-1][2];
    				while (writeInd < temp.length) {
    					if (readInd == index) {
    						readInd++;
    					}
    					temp[writeInd][0] = pathData.waypoints[readInd][0];
    					temp[writeInd][1] = pathData.waypoints[readInd][1];
    					writeInd++;
    					readInd++;
    				}
    					
    				System.out.println(temp.length);
    				System.out.println(pathData.waypoints.length);
    				
					pathData.waypoints = temp;
					pathData.recalculate();
					
					updatePathData();
					
					leftIndex = 0;
	    			centerIndex = 1;
	    			rightIndex = 2;
	    			drawTime(0, (Graphics2D)getGraphics());
	    			holdingIndex = -1;
	    			didSelect = true;
    			}
    		}
    	}

    	public void mouseReleased(MouseEvent ev) {
    		if (ev.isPopupTrigger()) {
    			menu.show(ev.getComponent(), ev.getX(), ev.getY());
    		} else if (ev.getButton() == MouseEvent.BUTTON1 && holdingIndex != -1 && pathData != null) {
    			double[] realCoords = convertMouseToRealSpace(ev.getX(), ev.getY());
    			System.out.println("Dropped waypoint with index: "+holdingIndex+" at real coordinate space: ("+realCoords[0]+", "+realCoords[1]+")");
    			pathData.waypoints[holdingIndex][0] = realCoords[0];
    			pathData.waypoints[holdingIndex][1] = realCoords[1];
    			
    			pathData.recalculate();
//    			plot((Graphics2D)getGraphics());
    			
    			updatePathData();
    			
    			leftIndex = 0;
    			centerIndex = 1;
    			rightIndex = 2;
    			drawTime(0, (Graphics2D)getGraphics());
    			holdingIndex = -1;
    			didSelect = true;
    		}
    	}

    	public void mouseClicked(MouseEvent ev) {
    		// Add a point to the waypoints array. Allow user to input index.
    		if (ev.getButton() == MouseEvent.BUTTON1 && ev.isControlDown() && pathData != null) {
    			// Change time for pathData
    			double time = 0;
    			while (time <= 0) {
    				try {
    					time = Double.parseDouble(JOptionPane.showInputDialog("Please enter the new time\nValid range: !0-infinity", pathData.getTime()));
    				} catch (NumberFormatException e) {
    					return;
    				}
    				catch (Exception ex) {
    					// do nothing
    				}
    			}
    			System.out.println("Updating path generator with new time: "+time);
    			pathData.updateTime(time);
    			pathData.recalculate();
    			updatePathData();
    			drawTime(0, (Graphics2D)getGraphics());
    		}
    		if (ev.getButton() == MouseEvent.BUTTON1 && ev.isShiftDown() && pathData != null && !didSelect) {
    			int index = -1;
    			while (index < 0) {
    				try {
    					index = Integer.parseInt(JOptionPane.showInputDialog("Please enter an index of where the point should be located.\nValid range: 0-infinity"));
    				} catch (NumberFormatException e) {
    					return;
    				} catch (Exception e) {
    					// do nothing
    				}
    			}
    			double[] tempS = convertMouseToRealSpace(ev.getX(), ev.getY());
    	        
    			Double[] temp = new Double[]{tempS[0], tempS[1]};
    			List<Double[]> list = Util.getDoubleList2D(pathData.waypoints);
    			if (index > list.size()) {
    				index = list.size();
    			}
    			list.add(index, temp);
    			pathData.waypoints = Util.getDoubleArr2D(list);
    			pathData.recalculate();
    			
    			updatePathData();
    			
    			leftIndex = 0;
    			centerIndex = 1;
    			rightIndex = 2;
    			drawTime(0, (Graphics2D)getGraphics());
    		} else if (ev.getButton() == MouseEvent.BUTTON1 && pathData != null && !didSelect) {
    			// Need to check to see if mouse is within a reasonable range of the waypoints.
    			// Then: need to lift the point, make it clear it is moving, and release it when it lands.
    			System.out.print("Picking up point at pixel coordinates: ("+ev.getX()+", "+ev.getY()+")");
    			double[] realCoords = convertMouseToRealSpace(ev.getX(), ev.getY());
    			System.out.println(" Real coordinates: ("+realCoords[0]+", "+realCoords[1]+")");
    			holdingIndex = getClosestWaypointIndex(buffer, realCoords);
    		}
    		didSelect = false;
    	}
    	private final int getClosestWaypointIndex(double buffer, double... realCoords) {

			for (int i = 0; i < pathData.waypoints.length; i++) {
				double[][] wp = pathData.waypoints;
				if (wp[i][0] - buffer < realCoords[0] && wp[i][0] + buffer > realCoords[0] && wp[i][1] + buffer > realCoords[1] && wp[i][1] - buffer < realCoords[1]) {
//					holdingIndex = i;
					System.out.println("Picked up point at index: "+holdingIndex+" with real-space coordinates: ("+wp[i][0]+", "+wp[i][1]+")");
					return i;
				}
			}
			return -1;
    	}
    }
    
    class TimeChangeAction extends AbstractAction {

    	public TimeChangeAction() {
    		super("Time change");
    		setEnabled(true);
    	}
		@Override
		public void actionPerformed(ActionEvent e) {
			// Change time for pathData
			double time = 0;
			while (time <= 0) {
				try {
					time = Double.parseDouble(JOptionPane.showInputDialog("Please enter the new time\nValid range: 0-infinity", pathData.getTime()));
				} catch (Exception ex) {
					// do nothing
				}
			}
			System.out.println("Updating path generator with new time: "+time);
			pathData.updateTime(time);
			pathData.recalculate();
			updatePathData();
			drawTime(0, (Graphics2D)getGraphics());
		}
    	
    }

    private class TransferableImage implements Transferable {

        Image i;

        public TransferableImage( Image i ) {
            this.i = i;
        }

        public Object getTransferData( DataFlavor flavor )
        throws UnsupportedFlavorException, IOException {
            if ( flavor.equals( DataFlavor.imageFlavor ) && i != null ) {
                return i;
            }
            else {
                throw new UnsupportedFlavorException( flavor );
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[ 1 ];
            flavors[ 0 ] = DataFlavor.imageFlavor;
            return flavors;
        }

        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for ( int i = 0; i < flavors.length; i++ ) {
                if ( flavor.equals( flavors[ i ] ) ) {
                    return true;
                }
            }

            return false;
        }
    }
}
