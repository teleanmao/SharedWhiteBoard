package whiteboard;

//Zelin Mao 1112821 COMP90015 Ass2 
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;


public class Eraser extends Draw {
	private int radius = 10;
	private Color originColor;
	public Eraser(){
		setDoubleBuffered(false);
		MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}
	
	public Eraser(Image currentImg, Graphics2D currentG2) {
		img = currentImg;
		g2 = currentG2;
		originColor = g2.getColor();
		MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}
	

	class MyMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
        	oldX = e.getX();
            oldY = e.getY();
            if (g2 != null) {
                erase(g2, oldX, oldY, radius);
                
                String trace = Integer.toString(oldX) + ","  + Integer.toString(oldY);
        		JSONObject newCommand = new JSONObject();
        		newCommand.put("Type", "Eraser");
        		newCommand.put("Points", trace);
        		newCommand.put("Radius", Integer.toString(radius));
        		sendOp(newCommand.toJSONString());
  
                repaint();
            }
        }

        public void mouseDragged(MouseEvent e) {
        	 newX = e.getX();
             newY = e.getY();
             if (g2 != null) {
             erase(g2, newX, newY, radius);
             
            String trace = Integer.toString(newX) + ","  + Integer.toString(newY);
     		JSONObject newCommand = new JSONObject();
     		newCommand.put("Type", "Eraser");
     		newCommand.put("Points", trace);
     		newCommand.put("Radius", Integer.toString(radius));
     		sendOp(newCommand.toJSONString());
     		if(status == "null") {
     			JSONObject statusCommand = new JSONObject();
     			statusCommand.put("STATUS", "Using Eraser");
     			sendOp(statusCommand.toString());
     			status = "Dragged";
     		}
     		
             repaint();
             oldX = newX;
 			 oldY = newY;
             }
        }

		@Override
		public void mouseReleased(MouseEvent e) {
			if(status != "null") {
				JSONObject statusCommand = new JSONObject();
	            statusCommand.put("STATUS", "");
	            sendOp(statusCommand.toString());
	            status = "null";
			}
            
		}
        
        
        
    }
	
	public void erase(Graphics g, int x, int y, int radius) {
		g.setColor(Color.white);
        g.fillRect(x, y, radius, radius);
        g.setColor(originColor);
    }
	
	@Override
	protected void paintComponent(Graphics g) {
		if(img == null) {
			img = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) img.getGraphics();
			originColor = g2.getColor();
			clearScreen();
		}
		g.drawImage(img, 0, 0, null);
		erase(g, newX, newY, radius);	
		
	}

}
