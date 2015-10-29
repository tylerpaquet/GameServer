package game;

public class Game {

	private final int byteMax = 128;
	private int p1x,p1y,p2x,p2y;
	
	public Game(){
		p1x = 200;
		p1y = 350;
		p2x = 500;
		p2y = 350;
	}
	
	public void update(){
		
	}
	
	public void action(int player,byte action){
		int dx = 0;
		int dy = 0;
		//System.out.println("ACTION ON SERVER!");
		
		switch(action){
		case 1:
			dx = 0;
			dy = -10;
			break;
		case 2:
			dx = 10;
			dy = 0;
			break;
		case 3:
			dx = 0;
			dy = 10;
			break;
		case 4:
			dx = -10;
			dy = 0;
			break;
		}
		
		if(player == 1){
			p1x += dx;
			p1y += dy;
		}else{
			p2x += dx;
			p2y += dy;
		}
		
		//System.out.println("(" + p1x + "," + p1y + ")   ("+p2x + "," +p2y + ")");
	}
	
	public void setBuffer(byte[] buf){
		//P1x
		buf[0] = (byte)(p1x / byteMax);
		buf[1] = (byte)(p1x % byteMax);
		
		//p1y
		buf[2] = (byte)(p1y / byteMax);
		buf[3] = (byte)(p1y % byteMax);
		
		//p2x
		buf[4] = (byte)(p2x / byteMax);
		buf[5] = (byte)(p2x % byteMax);
		
		//p2y
		buf[6] = (byte)(p2y / byteMax);
		buf[7] = (byte)(p2y % byteMax);
	}
}
