package whiteboard;

//Zelin Mao 1112821 COMP90015 Ass2 

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;


public class Text extends Draw {
	
	public String inputStr = "";
	private String mouseStatus = "null";
	
	public Text(){
		setDoubleBuffered(false);
		MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}

	public Text(Image currentImg, Graphics2D currentG2) {
		img = currentImg;
		g2 = currentG2;
		MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}
	
	class MyMouseListener extends MouseAdapter {
		
		  public void mousePressed(MouseEvent e) {
	        	oldX = e.getX();
	            oldY = e.getY();
	            mouseStatus = "pressed";
	        }

        public void mouseReleased(MouseEvent e) {
        		newX = e.getX();
        		newY = e.getY();
                if (g2 != null) {
                	g2.drawString(inputStr, newX, newY);
                	
                	String trace = Integer.toString(newX) + ","  + Integer.toString(newY);
             		JSONObject newCommand = new JSONObject();
             		newCommand.put("Type", "Text");
             		newCommand.put("Points", trace);
             		newCommand.put("String", inputStr);
             		newCommand.put("Color", Integer.toString(g2.getColor().getRGB()));
             		sendOp(newCommand.toJSONString());
             		if(status != "null") {
             			JSONObject statusCommand = new JSONObject();
             			statusCommand.put("STATUS", "");
             			sendOp(statusCommand.toString());
             			status = "null";
             		}
             		
                	
                	repaint();
                	oldX = newX; 
                    oldY = newY;
                    mouseStatus = "null";
                }
        }
        
        public void mouseDragged(MouseEvent e) {
       	 	newX = e.getX();
            newY = e.getY();
            repaint();
            if(status == "null") {
            	JSONObject statusCommand = new JSONObject();
            	statusCommand.put("STATUS", "Writing Text");
            	sendOp(statusCommand.toString());
            	status = "Dragged";
            }
       }
    }
	
	@Override
	protected void paintComponent(Graphics g) {
		if(img == null) {
			img = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) img.getGraphics();
			clearScreen();
		}
		g.drawImage(img, 0, 0, null);	
		if(mouseStatus!="null") {
			g.setColor(g2.getColor());
			g.drawString(inputStr, newX, newY);
		}
	}

}
