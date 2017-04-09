

public class Square {
	int x, y;
	int hCost;
	int gCost;
	int fCost;
	Square parentOfCurrentSquare;
	
	Square (int x,int y){
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}

}
