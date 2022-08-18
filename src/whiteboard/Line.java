package whiteboard;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.json.simple.JSONObject;

//Zelin Mao 1112821 COMP90015 Ass2 


public class Line extends Draw {
	
	
	public Line(){
		setDoubleBuffered(false);
		MyMouseListener listener = new MyMouseListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
	}

	public Line(Image currentImg, Graphics2D currentG2) {
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
        }

        public void mouseDragged(MouseEvent e) {
        	 newX = e.getX();
             newY = e.getY();

             if (g2 != null) {
					// Draw a line.
					g2.drawLine(oldX, oldY, newX, newY);
					String line = genOp("Line", g2, oldX, oldY, newX, newY);
					sendOp(line);
					
					if(status == "null") {
						JSONObject statusCommand = new JSONObject();
			            statusCommand.put("STATUS", "Drawing Line");
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
	
	@Override
	protected void paintComponent(Graphics g) {
		if(img == null) {
			img = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) img.getGraphics();
			clearScreen();
		}
		g.drawImage(img, 0, 0, null);

	}
	

}


