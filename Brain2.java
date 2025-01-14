/**
 A simple Brain implementation.
 bestMove() iterates through all the possible x values
 and rotations to play a particular piece (there are only
 around 10-30 ways to play a piece).
 
 For each play, it uses the rateBoard() message to rate how
 good the resulting board is and it just remembers the
 play with the lowest score. Undo() is used to back-out
 each play before trying the next. To experiment with writing your own
 brain -- just subclass off LameBrain and override rateBoard().
*/

public class Brain2 implements Brain {
	/**
	 Given a piece and a board, returns a move object that represents
	 the best play for that piece, or returns null if no play is possible.
	 See the Brain interface for details.
	*/
	@Override
	public int bestMove(Board board, Piece piece, int pieceX, int pieceY, int limitHeight)  {
		
		double bestScore = 1e20;
		int bestX = 0;
		int bestY = 0;
		int sTimes = 0;
		Piece bestPiece = null;
		Piece current = piece;
		int PW = 0;
		
		// loop through all the rotations
		while (true) {
			final int yBound = limitHeight - current.getHeight()+1;
			final int xBound = board.getWidth() - current.getWidth()+1;
			
			// For current rotation, try all the possible columns
			for (int x = 0; x<xBound; x++) {
				int y = board.dropHeight(current, x);
				if (y<yBound) {	// piece does not stick up too far
					int result = board.place(current, x, y);
					if (result <= Board.PLACE_ROW_FILLED) {
						if (result == Board.PLACE_ROW_FILLED)
							{
							board.clearRows();
							sTimes = board.Diff;
							}
						double score = rateBoard(board, sTimes);
						sTimes=0;
						if (score<bestScore) {
							bestScore = score;
							bestX = x;
							bestY = y;
							bestPiece = current;
						}
					}
					
					board.undo();	// back out that play, loop around for the next
				}
			}
			PW = board.getPieceWidth(current);
			if(PW == 1)
				break;
			else if(PW==4)
			{
				bestScore = 9999999;
			}
			current = current.nextRotation();
			if (current == piece) break;	// break if back to original rotation
		}
		
		

		if (bestPiece == null) return(JTetris.DOWN);	// could not find a play at all!
		
		if(!piece.equals(bestPiece))
			return JTetris.ROTATE;
		if(bestX == pieceX)
			return JTetris.DROP;
		if(bestX < pieceX)
			return JTetris.LEFT;
		else
			return JTetris.RIGHT;
	}
	
	
	/*
	 A simple brain function.
	 Given a board, produce a number that rates
	 that board position -- larger numbers for worse boards.
	 This version just counts the height
	 and the number of "holes" in the board.
	 See Tetris-Architecture.html for brain ideas.
	*/
	public double rateBoard(Board board, int Times) {
		final int width = board.getWidth();
		final int maxHeight = board.getMaxHeight();
		
		int sumHeight = 0;
		int holes = 0;
		int lowHeight;
		
		// Count the holes, and sum up the heights
		for (int x=0; x<width; x++) {
			final int colHeight = board.getColumnHeight(x);
			sumHeight += colHeight;
			
			int y = colHeight - 2;	// addr of first possible hole
			
			while (y>=0) {
				if  (!board.getGrid(x,y)) {
					holes++;
				}
				y--;
			}
		}
		
		int diffH = maxHeight - board.lowHeight();
		
		double avgHeight = ((double)sumHeight)/width;
		
		// Add up the counts to make an overall score
		// The weights, 8, 40, etc., are just made up numbers that appear to work
		return ( maxHeight*maxHeight*maxHeight/3  + 2*holes*holes - 200*Times + 2*diffH*diffH );	
	}
}