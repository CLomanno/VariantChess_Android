package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.example.myfirstapp.UnratedGamePage.GameTypeConstraints;



public class chessCore {
	// TYPES AND CLASSES
	private enum pieceType {
		pawn, bishop, knight, rook, queen, king
	};

	private enum objectColor {
		black, white
	};

	private enum pieceState {
		alive, dead
	};

	private enum gameState {
		allClear, whiteCheck, whiteMate, blackCheck, blackMate, stalemate
	};

	private enum moveStatus {
		success, fail, promote
	};
	
	private enum gameType {
		standard, fischer960, bongCloud, maharaja, flip, shatranj
	};
	
	
	private class player {
		// properties
		private objectColor color;

		public objectColor getColor() {
			return color;
		}

		private piece[] pieces;

		public piece[] getPieces() {
			return pieces;
		}

		// methods

		// move: moves the piece if the move is valid; returns false otherwise
		public boolean move(piece piece, cell moveTo) throws Exception {
			if (piece == null || piece.getPieceColor() != this.color || moveTo == null
					|| moveTo == deadCell)
				return false;
			// TODO: cache this so that we're not constantly re-populating
			ArrayList<cell> availableMoves = piece.getAvailableMoves();
			if (availableMoves.contains(moveTo)) {
				moveStatus status = piece.tryMove(moveTo);

				//Maharaja doesn't promote
				if( currentGameType == GameTypeConstraints.Maharaja){
					if (status == moveStatus.success) {
						return true;
					} else
						return false;
				}
				
				//Any other Game with promotion logic
				else{
					if (status != moveStatus.promote) {
						if (status == moveStatus.success) {
							return true;
						} else
							return false;
					}
					else {
						int pawnIndex = 15;	//default to the "king"

						//showNoticeDialog( pieceType promoType );

						//Cycle through all pawns to find the promoting pawn
						for (int i = 0; i < 8; i++) {

							//Find the pawn that just promoted
							if( (this.pieces[i].type == pieceType.pawn) && (this.pieces[i].location.y == 7 || this.pieces[i].location.y == 0) ){

								//Store the correct new piece and break
								pawnIndex = i;
								break;
							}
						}
						//fake it for now
						pieceType promoType = pieceType.queen;

						if (piece.getPieceColor() == objectColor.white){
							switch (promoType) {
							case bishop:  piece = new piece(objectColor.white, promoType, board[moveTo.x][moveTo.y], new bishop(), R.drawable.white_bishop); break;
							case knight:  piece = new piece(objectColor.white, promoType, board[moveTo.x][moveTo.y], new knight(), R.drawable.white_knight); break;
							case rook:	  piece = new piece(objectColor.white, promoType, board[moveTo.x][moveTo.y], new rook(), R.drawable.white_rook);     break;
							case queen:	  piece = new piece(objectColor.white, promoType, board[moveTo.x][moveTo.y], new queen(), R.drawable.white_queen);   break;
							case pawn:
							case king:
							default:	throw new incompatiblePieceTypeConversionException(
									"Error: invalid conversion from pawn.");
							}
						}
						else{
							switch (promoType) {
							case bishop:  piece = new piece(objectColor.black, promoType, board[moveTo.x][moveTo.y], new bishop(), R.drawable.black_bishop); break;
							case knight:  piece = new piece(objectColor.black, promoType, board[moveTo.x][moveTo.y], new knight(), R.drawable.black_knight); break;
							case rook:	  piece = new piece(objectColor.black, promoType, board[moveTo.x][moveTo.y], new rook(), R.drawable.black_rook);     break;
							case queen:	  piece = new piece(objectColor.black, promoType, board[moveTo.x][moveTo.y], new queen(), R.drawable.black_queen);   break;
							case pawn:
							case king:	  
							default:	throw new incompatiblePieceTypeConversionException(
									"Error: invalid conversion from pawn.");
							}
						}

						board[moveTo.x][moveTo.y].setPiece(piece);	//Sets promo piece at new location
						piece.setPieceState(pieceState.alive);		//"Enable" promotion piece
						availableMoves = piece.getAvailableMoves();	//Get the new moves
						piece.firstMove = false;					//Disable 1st move so no weird castling happens	

						this.pieces[pawnIndex] = piece;

						return true;
					}
				}
			}
			return false;
		}

		// .ctor
		player(objectColor color, piece[] pieces) {
			this.color = color;
			this.pieces = pieces;
		}
	}
	
/*
	public class game {
	
		private gameType gameType;
		
		public gameType getGameType() {
			return gameType;
		}

	}
*/
	
	public class cell {
		private int x;

		public int getX() {
			return x;
		}

		private int y;

		public int getY() {
			return y;
		}

		private piece piece;

		public piece getPiece() {
			return piece;
		}

		// if this replaces an existing piece, its location will be set to dead
		// cell
		// under the assumption that it has been taken
		// if this is a cleanup call, pass in null
		// NOTE: deadCell never has a piece associated with it.
		public void setPiece(piece piece) {
			if (this.piece != null)
				this.piece.setPieceState(pieceState.dead);
			this.piece = piece;
		}

		cell(int x, int y) {
			this.x = x;
			this.y = y;
			this.piece = null;
		}
	}

	public class incompatiblePieceTypeConversionException extends Exception {
		public incompatiblePieceTypeConversionException(String string) {
			super(string);
		}

		private static final long serialVersionUID = 1L;
	}

	public class piece {

		// properties
		private cell location;

		public cell getLocation() {
			return location;
		}

		// state of the piece
		private pieceState state;

		public pieceState getPieceState() {
			return state;
		}

		public void setPieceState(pieceState newState) {
			state = newState;
			if (newState == pieceState.dead) {
				location.piece  = null;
				location = deadCell;
			}
		}
		
		private boolean firstMove;		//for castling
		
		private boolean pawnDoubleMove;	//for en passant
		
		private int imageResource;

		public int getImageResource() {
			return imageResource;
		}

		// color of the piece
		protected objectColor color;

		public objectColor getPieceColor() {
			return color;
		}

		// type of piece
		private pieceType type;

		public pieceType getPieceType() {
			return type;
		}

		public void setPieceType(pieceType newType)
				throws incompatiblePieceTypeConversionException {
			if (type != pieceType.pawn)
				throw new incompatiblePieceTypeConversionException(
						"Error: can only convert pawns");
			else {
				switch (newType) {
				case bishop:
					availMoves = new bishop();
					break;
				case knight:
					availMoves = new knight();
					break;
				case rook:
					availMoves = new rook();
					break;
				case queen:
					availMoves = new queen();
					break;
				case pawn:
				case king:
					throw new incompatiblePieceTypeConversionException(
							"Error: invalid conversion from pawn.");
				}

			}
		}

		// methods

		public boolean isValidMove(cell moveTo) {
			
			if(moveTo.getPiece() != null )
				if( kingCastle == 1								&&		//Castle logic is good
					(moveTo.getPiece().type == pieceType.king	||		//The piece isn't a king
					 (moveTo.getPiece().type == pieceType.rook	&&		//AND the piece is the castle rook
					  (moveTo.getPiece().location.x == 6 		||		//AND the kind is going to the king castle square
					   moveTo.getPiece().location.x == 2)	 ) ) )		{}// OR queen square
				else if(moveTo.getPiece().getPieceColor() == this.color){
					return false;
				}
			
			//For Fischer 960
			if( kingCastle == 1					&&	//Castle logic is good
				moveTo == this.getLocation() ) 		//King is moving to and from the same square
					return true;
			
			// try move
			cell targetCell = board[moveTo.getX()][moveTo.getY()];
			piece oldPiece = targetCell.getPiece();
			cell sourceCell = this.getLocation();
			targetCell.setPiece(this);
			sourceCell.setPiece(null);
			this.location = targetCell;
			this.setPieceState(pieceState.alive);
			
			gameState tryState = checkForCheck(board, this.color);
						
			//revert
			sourceCell.setPiece(this);
			targetCell.setPiece(oldPiece);
			this.location = sourceCell;
			this.setPieceState(pieceState.alive);
			if(oldPiece!= null)
			{
				oldPiece.location = targetCell;
				oldPiece.setPieceState(pieceState.alive);
			}
			
			// see if move is valid
			// validity of the move is determined by whether the move puts
			// the player into check. We know that the pieces cannot move
			// in invalid patterns because this has already been checked in
			// the only caller, which should be player.move()
			if ((color == objectColor.white && tryState == gameState.whiteCheck)
					|| (color == objectColor.black && tryState == gameState.blackCheck))
				return false;
			return true;
		}

		// move takes a valid cell on the board and tries to move the
		// piece to it. if the move is successful, returns true, if not
		// returns false
		public moveStatus tryMove(cell moveTo) {

			if (!isValidMove(moveTo))
				return moveStatus.fail;
			// move is valid; apply move and check for promotion if pawn
			// empty old location
			this.location.setPiece(null);
			// update new location
			moveTo.setPiece(this);
			// update self location
			this.location = moveTo;

			//Maharaja doesn't promote pawns
			if  (currentGameType != GameTypeConstraints.Maharaja){
				if (type == pieceType.pawn) {
					return (location.y == 0 || location.y == 7) ? moveStatus.promote
							: moveStatus.success;
				}
			}

			return moveStatus.success;
		}

		// gets a list of moves that don't go off the board or cause friendly
		// fire
		private availableMoves availMoves;

		public ArrayList<cell> getAvailableMoves() {
			return availMoves.getAvailableMoves(this);
		}

		// .ctor
		piece(objectColor color, pieceType type, cell location,
				availableMoves movementPattern, int imageResource) {
			this.color 			= color;
			this.type 			= type;
			this.location 		= location;
			this.availMoves 	= movementPattern;
			this.imageResource 	= imageResource;
			this.firstMove 		= true;
			this.pawnDoubleMove = false;
		}

	}
	
	private interface availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece);
	}
	
	//public void promotionDialog() {
	//	   DialogFragment newFragment = new NoticeDialogFragment();
	//	    newFragment.show(getSupportFragmentManager(), "promotion");
	//	}

	private class pawn implements availableMoves {
		// only piece whose moves depend on color
		public ArrayList<cell> getAvailableMoves(piece piece) {
			
			//Maharaja needs to ignore pawns that made it all the way to the end of the board so the code doesnt crash
			if  (currentGameType == GameTypeConstraints.Maharaja){
				if (piece.getLocation() == deadCell 	||
						piece.location.y	== 0		||
						piece.location.y	== 7		 )			//Maharaja rules need to make sure it isn't looking for moves for immovable pawns
					return null;
			}
			else{
				if (piece.getLocation() == deadCell)
					return null;
			}
			
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();

			// this could be better implemented with some sort of
			// direction boolean deciding addition + subtraction, but
			// i don't feel like dicking with it, so here are two
			// separate blocks of code.

			// as standard, white is on bottom, black on top
			// white moves 6 -> 0; black moves 1 -> 7
			if (piece.getPieceColor() == objectColor.white) {
				// move forward one cell
				if (board[currX][currY - 1].getPiece() == null)
					retList.add(board[currX][currY - 1]);

				// check moving left
				if (currX > 0
						&& board[currX - 1][currY - 1].getPiece() != null
						&& board[currX - 1][currY - 1].getPiece()
								.getPieceColor() == objectColor.black)
					retList.add(board[currX - 1][currY - 1]);

				// check moving right
				if (currX < 7
						&& board[currX + 1][currY - 1].getPiece() != null
						&& board[currX + 1][currY - 1].getPiece()
								.getPieceColor() == objectColor.black)
					retList.add(board[currX + 1][currY - 1]);

				// default location, making 4 available moves, rather than 3
				if (currY == 6 && board[currX][currY - 1].getPiece() == null
						&& board[currX][currY - 2].getPiece() == null)
					retList.add(board[currX][currY - 2]);
				
				// En passant right
				if  (currY == 3 	&&
					 board[currX+1][currY].getPiece()				   != null				 &&
					 board[currX+1][currY].getPiece().getPieceColor() == objectColor.black &&
					 board[currX+1][currY].getPiece().getPieceType()   == pieceType.pawn     &&
					 board[currX+1][currY].getPiece().pawnDoubleMove   == true )
						retList.add(board[currX+1][currY - 1]);
				
				// En passant left
				if  (currY == 3 	&&
					 board[currX-1][currY].getPiece()				   != null				 &&
					 board[currX-1][currY].getPiece().getPieceColor() == objectColor.black &&
					 board[currX-1][currY].getPiece().getPieceType()   == pieceType.pawn     &&
					 board[currX-1][currY].getPiece().pawnDoubleMove   == true )
						retList.add(board[currX-1][currY - 1]);
			}
			if (piece.getPieceColor() == objectColor.black) {
				// COPY PASTA! YAAY! : (
				// move forward one cell
				if (board[currX][currY + 1].getPiece() == null)
					retList.add(board[currX][currY + 1]);

				// check moving left
				if (currX > 0
						&& board[currX - 1][currY + 1].getPiece() != null
						&& board[currX - 1][currY + 1].getPiece().getPieceColor() == objectColor.white)
					retList.add(board[currX - 1][currY + 1]);

				// check moving right
				if (currX < 7
						&& board[currX + 1][currY + 1].getPiece() != null
						&& board[currX + 1][currY + 1].getPiece().getPieceColor() == objectColor.white)
					retList.add(board[currX + 1][currY + 1]);

				// default location, making 4 available moves, rather than 3
				if (currY == 1 && board[currX][currY + 1].getPiece() == null
						&& board[currX][currY + 2].getPiece() == null)
					retList.add(board[currX][currY + 2]);
				
				// En passant right
				if  (currY == 4 	&& 
					 board[currX+1][currY].getPiece()				   != null				 &&
					 board[currX+1][currY].getPiece().getPieceColor() == objectColor.white &&
					 board[currX+1][currY].getPiece().getPieceType()   == pieceType.pawn     &&
					 board[currX+1][currY].getPiece().pawnDoubleMove   == true ) 
						retList.add(board[currX+1][currY + 1]);
				
				// En passant left
				if  (currY == 4 	&& 
					 board[currX-1][currY].getPiece()				   != null				 &&
					 board[currX-1][currY].getPiece().getPieceColor() == objectColor.white &&
					 board[currX-1][currY].getPiece().getPieceType()   == pieceType.pawn     &&
					 board[currX-1][currY].getPiece().pawnDoubleMove   == true )
						retList.add(board[currX-1][currY + 1]);
			}
			return retList;
		}
	}

	private class bishop implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
			// set i to 1 because there's no point in checking whether
			// staying in place is a valid move. that's fucking stupid
			// even with color checking
			int i = 1;

			// check the diagonal until you see a piece.
			// right, down
			while ((currX + i < 8 && currY + i < 8)
					&& (board[currX + i][currY + i].getPiece() == null || board[currX
							+ i][currY + i].getPiece().getPieceColor() != piece.getPieceColor())) {
				// if the piece is one of the opponent's, it is a valid move
				if (board[currX + i][currY + i].getPiece() != null
						&& board[currX + i][currY + i].getPiece().getPieceColor() != piece.getPieceColor()) {
					retList.add(board[currX + i][currY + i]);
					break;
				}
				retList.add(board[currX + i][currY + i]);
				i++;
			}

			i = 1;
			// right, up
			while ((currX + i < 8 && currY - i > -1)
					&& (board[currX + i][currY - i].getPiece() == null || board[currX
							+ i][currY - i].getPiece().getPieceColor() != piece.getPieceColor())) {
				if (board[currX + i][currY - i].getPiece() != null
						&& board[currX + i][currY - i].getPiece().getPieceColor() != piece.getPieceColor()) {
					retList.add(board[currX + i][currY - i]);
					break;
				}
				retList.add(board[currX + i][currY - i]);
				i++;
			}

			i = 1;
			// left, down
			while ((currX - i > -1 && currY + i < 8)
					&& (board[currX - i][currY + i].getPiece() == null || board[currX
							- i][currY + i].getPiece().getPieceColor() != piece.getPieceColor())) {
				if (board[currX - i][currY + i].getPiece() != null
						&& board[currX - i][currY + i].getPiece().getPieceColor() != piece.getPieceColor()) {
					retList.add(board[currX - i][currY + i]);
					break;
				}
				retList.add(board[currX - i][currY + i]);
				i++;
			}

			i = 1;
			// left, up
			while ((currX - i > -1 && currY - i > -1)
					&& (board[currX - i][currY - i].getPiece() == null || board[currX
							- i][currY - i].getPiece().getPieceColor() != piece.getPieceColor())) {
				if (board[currX - i][currY - i].getPiece() != null
						&& board[currX - i][currY - i].getPiece().getPieceColor() != piece.getPieceColor()) {
					retList.add(board[currX - i][currY - i]);
					break;
				}
				retList.add(board[currX - i][currY - i]);
				i++;
			}

			return retList;
		}
	}

	private class knight implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
			// the knight has 8 moves, so we'll just explicitly define them
			// since it's not that much more verbose than the alternateive
			// and much easier to write.
			// TODO: think about optimizing this. not a priority at the moment
			// like, this is a circular pattern, or check the rectangle 2 away,
			// skipping every other cell

			if (currX > 1
					&& currY > 0
					&& (board[currX - 2][currY - 1].getPiece() == null || board[currX - 2][currY - 1].getPiece().getPieceColor() != piece.getPieceColor()))
				retList.add(board[currX - 2][currY - 1]);

			if (currX > 0
					&& currY > 1
					&& (board[currX - 1][currY - 2].getPiece() == null || board[currX - 1][currY - 2].getPiece().getPieceColor() != piece.getPieceColor()))
				retList.add(board[currX - 1][currY - 2]);

			if (currX < 7
					&& currY > 1
					&& (board[currX + 1][currY - 2].getPiece() == null || board[currX + 1][currY - 2].getPiece().getPieceColor() != piece.getPieceColor()))
				retList.add(board[currX + 1][currY - 2]);

			if (currX < 6
					&& currY > 0
					&& (board[currX + 2][currY - 1].getPiece() == null || board[currX + 2][currY - 1].getPiece().getPieceColor() != piece.getPieceColor()))
				retList.add(board[currX + 2][currY - 1]);

			if (currX < 6
					&& currY < 7
					&& (board[currX + 2][currY + 1].getPiece() == null || board[currX + 2][currY + 1].getPiece().getPieceColor() != piece.getPieceColor()))
				retList.add(board[currX + 2][currY + 1]);

			if (currX < 7
					&& currY < 6
					&& (board[currX + 1][currY + 2].getPiece() == null || board[currX + 1][currY + 2].getPiece().getPieceColor() != piece.getPieceColor()))
				retList.add(board[currX + 1][currY + 2]);

			if (currX > 0
					&& currY < 6
					&& (board[currX - 1][currY + 2].getPiece() == null || board[currX - 1][currY + 2].getPiece().getPieceColor() != piece.getPieceColor()))
				retList.add(board[currX - 1][currY + 2]);

			if (currX > 1
					&& currY < 7
					&& (board[currX - 2][currY + 1].getPiece() == null || board[currX - 2][currY + 1].getPiece().getPieceColor() != piece.getPieceColor()))
				retList.add(board[currX - 2][currY + 1]);

			return retList;
		}
	}

	private class rook implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
			int i = 1;

			// right
			while (currX + i < 8
					&& (board[currX + i][currY].getPiece() == null || board[currX
							+ i][currY].getPiece().getPieceColor() != piece.getPieceColor())) {
				if (board[currX + i][currY].getPiece() != null
						&& board[currX + i][currY].getPiece().getPieceColor() != piece.getPieceColor()) {
					retList.add(board[currX + i][currY]);
					break;
				}
				retList.add(board[currX + i][currY]);
				i++;
			}

			i = 1;
			// left
			while (currX - i > -1
					&& (board[currX - i][currY].getPiece() == null || board[currX
							- i][currY].getPiece().getPieceColor() != piece.getPieceColor())) {
				if (board[currX - i][currY].getPiece() != null
						&& board[currX - i][currY].getPiece().getPieceColor() != piece.getPieceColor()) {
					retList.add(board[currX - i][currY]);
					break;
				}
				retList.add(board[currX - i][currY]);
				i++;
			}

			i = 1;
			// down
			while (currY + i < 8
					&& (board[currX][currY + i].getPiece() == null || board[currX][currY
							+ i].getPiece().getPieceColor() != piece.getPieceColor())) {
				if (board[currX][currY + i].getPiece() != null
						&& board[currX][currY + i].getPiece().getPieceColor() != piece.getPieceColor()) {
					retList.add(board[currX][currY + i]);
					break;
				}
				retList.add(board[currX][currY + i]);
				i++;
			}

			i = 1;
			// up
			while (currY - i > -1
					&& (board[currX][currY - i].getPiece() == null || board[currX][currY
							- i].getPiece().getPieceColor() != piece.getPieceColor())) {
				if (board[currX][currY - i].getPiece() != null
						&& board[currX][currY - i].getPiece().getPieceColor() != piece.getPieceColor()) {
					retList.add(board[currX][currY - i]);
					break;
				}
				retList.add(board[currX][currY - i]);
				i++;
			}
			return retList;
		}
	}

	private class queen implements availableMoves {
		private rook horizontalVerical;
		private bishop diagonal;

		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			horizontalVerical = new rook();
			diagonal = new bishop();
			ArrayList<cell> retList = horizontalVerical
					.getAvailableMoves(piece);
			ArrayList<cell> moreMoves = diagonal.getAvailableMoves(piece);
			for (int i = 0; i < moreMoves.size(); i++) {
				retList.add(moreMoves.get(i));
			}
			return retList;
		}
	}

	private class king implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
			// if the king moves and there is a check, regardless of the color
			// it is an invalid move.
			// TODO:implement king moves in a way that won't cause infinite
			// loops
			// for example: checkForCheck doesn't look at the king. That would
			// do it
			// Also: don't check for mate here.

			if (currX > 0 && currY > 0
					&& (piece.isValidMove(board[currX - 1][currY - 1])))
				retList.add(board[currX - 1][currY - 1]);

			if (currY > 0 && (piece.isValidMove(board[currX][currY - 1])))
				retList.add(board[currX][currY - 1]);

			if (currX < 7 && currY > 0
					&& (piece.isValidMove(board[currX + 1][currY - 1])))
				retList.add(board[currX + 1][currY - 1]);

			if (currX < 7 && (piece.isValidMove(board[currX + 1][currY])))
				retList.add(board[currX + 1][currY]);

			if (currX < 7 && currY < 7
					&& (piece.isValidMove(board[currX + 1][currY + 1])))
				retList.add(board[currX + 1][currY + 1]);

			if (currY < 7 && (piece.isValidMove(board[currX][currY + 1])))
				retList.add(board[currX][currY + 1]);

			if (currX > 0 && currY < 7
					&& (piece.isValidMove(board[currX - 1][currY + 1])))
				retList.add(board[currX - 1][currY + 1]);

			if (currX > 0 && piece.isValidMove(board[currX - 1][currY]))
				retList.add(board[currX - 1][currY]);
			
			//Check for castling
			if (piece.firstMove == true) {
			
				currentGameState = checkForCheck(board, piece.getPieceColor());	//Make sure you're not all ready in check
				
				//So we don't need to check for a check
				if (currentGameState != gameState.allClear )
					return retList;
				
				//Fischer960 chess castles to the same location as in standard chess, just the starting positions are changed
				if(currentGameType == GameTypeConstraints.Fischer){
					   		
					//Get the positions of the rooks and king
					int rookLocationQueen  = 0;
					int rookLocationKing   = 0;
					boolean rookFirstKing  = false;
					boolean rookFirstQueen = false;
					
					//Right now the pieces are hardcoded.
					//TODO possible change this
					if (piece.getPieceColor() == objectColor.white) {
						rookLocationKing	= white.pieces[9].location.x;
						rookLocationQueen	= white.pieces[8].location.x;
						rookFirstKing		= white.pieces[9].firstMove;
						rookFirstQueen		= white.pieces[8].firstMove;
						
					}
					else{
						rookLocationKing	= black.pieces[9].location.x;
						rookLocationQueen	= black.pieces[8].location.x;
						rookFirstKing		= black.pieces[9].firstMove;
						rookFirstQueen		= black.pieces[8].firstMove;
					}
					
					//Get the difference from the king to each side's castle location.
					//King side is (x,y)=(6,0/7) and queenside is (2,0/7)
					int kingDifKingSide  = 6 - piece.location.x; 	//Will always be >=0
					int kingDifQueenSide = 2 - piece.location.x;	//May be <0
					int rookDifKingSide  = 5 - rookLocationKing;	//+ve number means the rook is on G, or H,    -ve means C, D, or E
					int rookDifQueenSide = 3 - rookLocationQueen; 	//+ve number means the rook is on A, B, or C, -ve means D, E, or F
					int kingSideGood  = 1;
					int queenSideGood = 1;
					
				//////////////////////
				//    King side    //
				//////////////////////
					if(rookFirstKing == true) { //A is a rook and 1st move
							
						//Loop through all of the "safe" king squares
						for(int i = 1; i <= kingDifKingSide; i++){ 
							if ( kingDifKingSide == 0 )			//If the king is already on the castle square
										break;												
						
							//If the square is a rook: remove it then check if its a valid move then replace it
							if(( null           != board[currX + i][currY].getPiece())				 &&		//If the square isnt null
								(pieceType.rook == board[currX + i][currY].getPiece().getPieceType() &&		//And the piece is a rook
								 board[currX + i][currY].getPiece().location.x ==  rookLocationKing) ){		//And its the kingside castle rook
															
								//TEMPORARILLY Remove the rook
								piece removedRook = board[currX + i][currY].getPiece();
								board[currX + i][currY].setPiece(null);
								
								//Check if valid
								if( !piece.isValidMove(board[currX + i][currY]) )
									kingSideGood = 0;
									
									//replace the rook
									board[currX + i][currY].setPiece(removedRook);
									removedRook.setPieceState(pieceState.alive);
									removedRook.location = board[currX + i][currY];
									
									if( kingSideGood == 0)
										break;
							}
							else{
								//Makes sure the move isn't a check
								if( !piece.isValidMove(board[currX + i][currY]) ){
									kingSideGood = 0;
									break;
								}
							}
						} //for(int i = 0; i <= kingDifKingSide; i++){
					
						//Loop through all of the "empty" rook squares
						if (rookDifKingSide == -2){							//If the rook is to the right of the castle square on H
							//All that's left is when the rook is in the corner
							if ( (board[6][currY].getPiece() != null && board[6][currY].getPiece().type != pieceType.king )  ||	//King is at G and F is free
									(board[5][currY].getPiece() != null && board[5][currY].getPiece().type != pieceType.king ) ){ 	//king is at F and G is free
								kingSideGood = 0;
							} 	
						}
						//The rook is either at C, D, or E
						else if (rookDifKingSide > 0){
							for(int i = 1; i < rookDifKingSide;){	//Removed i++

								if ( board[rookLocationKing + i][currY].getPiece() != null ){		//If the square isnt open
									kingSideGood = 0;
									break;
								}
							}//for(int i = 0; i < rookDifKingSide; i++){
						}

						if (kingSideGood == 1){
							retList.add(board[currX + kingDifKingSide][currY]);
							kingCastle = 1;
						}
					}//if(board[rookLocationKing][currY].getPiece().firstMove == true)

				//////////////////////
				//    Queen side    //
				//////////////////////
					if(rookFirstQueen == true) {
					
						//Loop through all of the "safe" king squares
						if (kingDifQueenSide < 0){		//King is at D, E, F, or G
							for(int i = kingDifQueenSide; i <= -1; i++){ 

								//If the square is a rook: remove it then check if its a valid move then replace it
								if(( null  			!= board[currX + i][currY].getPiece())					&&	//If the square isnt null
									(pieceType.rook == board[currX + i][currY].getPiece().getPieceType() 	&&	//And the piece is a rook
									 board[currX + i][currY].getPiece().location.x ==  rookLocationQueen)	){	//And its the queenside castle rook	

									//TEMPORARILLY Remove the rook
									piece removedRook = board[currX + i][currY].getPiece();
									board[currX + i][currY].setPiece(null);
									
									//Check if valid
									if( !piece.isValidMove(board[currX + i][currY]) )
										queenSideGood = 0;
									
									//replace the rook
									board[currX + i][currY].setPiece(removedRook);
									removedRook.setPieceState(pieceState.alive);
									removedRook.location = board[currX + i][currY];
									
									if( queenSideGood == 0)
										break;
								}
								else{
									//Makes sure the move isn't a check
									if( !piece.isValidMove(board[currX + i][currY]) ){
										queenSideGood = 0;
										break;
									}
								}
							} //for(int i = 0; i <= kingDifKingSide; i++){
						}
						else if (kingDifQueenSide > 0){		//King is at B or C
							for(int i = 1; i <= kingDifQueenSide; i++){ 

								//Ignore the square if its a king or the castle rook
								if(( null           != board[currX + i][currY].getPiece())				  &&			//If the square isnt null
									(pieceType.rook == board[currX + i][currY].getPiece().getPieceType()  && 			//And the piece is a rook
											board[currX + i][currY].getPiece().location.x ==  rookLocationQueen)){		//And its the queenside castle rook
									
									//TEMPORARILLY Remove the rook
									piece removedRook = board[currX + i][currY].getPiece();
									board[currX + i][currY].setPiece(null);
									
									//Check if valid
									if( !piece.isValidMove(board[currX + i][currY]) )
										queenSideGood = 0;
									
									//replace the rook
									board[currX + i][currY].setPiece(removedRook);
									removedRook.setPieceState(pieceState.alive);
									removedRook.location = board[currX + i][currY];
									
									if( queenSideGood == 0)
										break;
								}
								else{
									//Makes sure the move isn't a check
									if( !piece.isValidMove(board[currX + i][currY]) ){
										queenSideGood = 0;
										break;
									}
								}
							} //for(int i = 0; i <= kingDifKingSide; i++){
						}
						
						//Loop through all of the "empty" rook squares
						if( rookDifQueenSide == 3 ){
							if (board[1][currY].getPiece() != null && board[1][currY].getPiece().type != pieceType.king )	//Rook is at A and B is free
								queenSideGood = 0;
						}						
						if (rookDifQueenSide >= 2 ){							//If the rook is at B
							if ( board[2][currY].getPiece() != null && board[2][currY].getPiece().type != pieceType.king ) 	//Makes sure C is free
								queenSideGood = 0;	
						}
						if (rookDifQueenSide >= 1){							//If the rook is at C
							if (board[3][currY].getPiece() != null && board[3][currY].getPiece().type != pieceType.king ) 	//Makes sure D is free
								queenSideGood = 0;	
						}
						//The rook is either at E or F
						else if (rookDifQueenSide < 0){
							for(int i = 1; i < rookDifQueenSide; i++){
								if ( board[rookLocationQueen - i][currY].getPiece() != null )		//If the square isnt open
									queenSideGood = 0;
								break;
							}//for(int i = 0; i < rookDifKingSide; i++){
						}

						if (queenSideGood == 1){
							retList.add(board[currX + kingDifQueenSide][currY]);
							kingCastle = 1;
						}
						
					}//if(board[rookLocationQueen][currY].getPiece().firstMove == true)

				}
				else{	//Not Fischer 960 chess
					//King side
					if (piece.isValidMove(board[currX + 1][currY])   	&&	//F is safe
						piece.isValidMove(board[currX + 2][currY])   	&&	//G is safe
					   (board[7][currY].getPiece().firstMove == true 	&&	//H is a rook and 1st move
						pieceType.rook == board[7][currY].getPiece().getPieceType())){
							retList.add(board[currX + 2][currY]);
							kingCastle = 1;
					}
					//Queen side
					if (piece.isValidMove(board[currX - 1][currY])   	&&	//D is safe
						piece.isValidMove(board[currX - 2][currY])   	&&	//C is safe
						board[currX - 3][currY].getPiece()   == null 	&&	//B knight is gone
				       (board[0][currY].getPiece().firstMove == true 	&&	//A is a rook and 1st move
				   		pieceType.rook == board[0][currY].getPiece().getPieceType())){
							retList.add(board[currX - 2][currY]);
							kingCastle = 1;
					}
				}
			}
			return retList;
		}
	}
		
	private class maharaja implements availableMoves {
		private rook horizontalVerical;
		private bishop diagonal;
		private knight fatL;
		//private king 	specialK;

		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			horizontalVerical = new rook();
			diagonal = new bishop();
			fatL = new knight();
			//specialK = new king();

			ArrayList<cell> retList = horizontalVerical
					.getAvailableMoves(piece);
			ArrayList<cell> moreMoves = diagonal.getAvailableMoves(piece);
			for (int i = 0; i < moreMoves.size(); i++) {
				retList.add(moreMoves.get(i));
			}

			//Knight moves
			ArrayList<cell> knightMoves = fatL.getAvailableMoves(piece);
			for (int i = 0; i < knightMoves.size(); i++) {
				retList.add(knightMoves.get(i));
			}

			return retList;
		}
	}


	private class userInterfaceBoard {
		public cell getTargetCell() {
			// TODO: write UI interaction
			cell retCell = deadCell;
			return retCell;
		}

		public cell getSourceCell() {
			// TODO: write UI interaction
			cell retCell = deadCell;
			return retCell;
		}

		public piece getPiece() {
			// TODO: i'm not sure that this should really come from the source
			// cell.
			// we'll see
			piece retPiece = getSourceCell().getPiece();
			return retPiece;
		}

		// .ctor
		userInterfaceBoard() {
		}
	}
	
	// MEMBER VARIABLES

	// this gets used for discarded pieces
	private cell deadCell;
	private cell board[][];
	private player white, black;
	private objectColor turn;
	private piece lastPieceMoved;		//Used for resetting pawnDoubleMove to fix en passant
	private gameState currentGameState;
	private int currentGameType;
	private int kingCastle;				//Used for castle logic
	private userInterfaceBoard UIboard;

	public cell[][] getBoard() {
		return board;
	}

	public objectColor getTurn() {
		return turn;
	}

	// METHODS

	// checks for check on proposed board.
	private gameState checkForCheck(cell board[][], objectColor whoseCheck) {
		piece own[];
		piece opponent[];
		
		//Unlike standard chess, Maharaja also needs to check if the King can check the maharaja
		if (currentGameType == GameTypeConstraints.Maharaja ){
			if (whoseCheck == objectColor.white) {
				own = white.getPieces();
				opponent = black.getPieces();

				for (int i = 0; i < 16; i++) {
					ArrayList<cell> moves = opponent[i].getAvailableMoves();
					if (moves != null
							&& (!moves.isEmpty() && moves.contains(own[0].getLocation()))) 
					{
						return gameState.whiteCheck;
					}
				}
			} else {
				own = black.getPieces();
				opponent = white.getPieces();

				ArrayList<cell> moves = opponent[0].getAvailableMoves();
				if (moves != null
						&& (!moves.isEmpty() && moves.contains(own[15].getLocation()))) 
				{
					return gameState.blackCheck;
				}
			}
		}
		
		//Any other game type
		else{
			if (whoseCheck == objectColor.white) {
				own = white.getPieces();
				opponent = black.getPieces();
			} else {
				own = black.getPieces();
				opponent = white.getPieces();
			}
			// i < 16 because we're not checking the king for move possibilities
			// as that would cause an infinite loop since the king checks for
			// checks in available moves;
			for (int i = 0; i < 15; i++) {
				ArrayList<cell> moves = opponent[i].getAvailableMoves();
				if (moves != null
						&& (!moves.isEmpty() && moves.contains(own[15].getLocation()))) 
				{
					if (whoseCheck == objectColor.white)
						return gameState.whiteCheck;
					else
						return gameState.blackCheck;
				}
			}
		}
		
		return gameState.allClear;
	}

	// checks for mate on the board as it exists
	// TODO: finish this. Not important at the moment.
	private gameState checkForMate() {
		// TODO: check for stalemate here
		// mate = check & no valid moves as defined above(somewhere)
		// stalemate = no available moves and no check
		ArrayList<cell> whiteMoves = new ArrayList<cell>();
		ArrayList<cell> blackMoves = new ArrayList<cell>();
		
		for (int i = 0; i < 15; i++) {
			ArrayList<cell> moves = white.getPieces()[i].getAvailableMoves();
			for(int j = 0; j < moves.size(); j++)
				whiteMoves.add(moves.get(j));
			moves = black.getPieces()[i].getAvailableMoves();
			for(int j = 0; j < moves.size(); j++)
				blackMoves.add(moves.get(j));
		}
		if(blackMoves.size() == 0)
		{
			if(checkForCheck(board, objectColor.black) == gameState.blackCheck)
				return gameState.blackMate;
			else
				return gameState.stalemate;
		}
		if(whiteMoves.size() == 0)
		{
			if(checkForCheck(board, objectColor.white) == gameState.whiteCheck)
				return gameState.whiteMate;
			else
				return gameState.stalemate;
		}
		if (checkForCheck(board, objectColor.white) == gameState.whiteCheck)
			return gameState.whiteCheck;
		if (checkForCheck(board, objectColor.black) == gameState.blackCheck)
			return gameState.blackCheck;
		return gameState.allClear;
	}

	// TODO: maybe move this into a game class
	public boolean move(int fromX, int fromY, int toX, int toY)
			throws Exception {
		player currentPlayer = turn == objectColor.white ? white : black;
		piece piece = board[fromX][fromY].getPiece();
		boolean success = currentPlayer.move(piece, board[toX][toY]);
		if (success){
			
			turn = turn == objectColor.white ? objectColor.black
					: objectColor.white;
			
			//For castling logic
			piece.firstMove      = false;
			
			//Check for en passant.  this sets the "double move" variable or removes the moved piece
			if (piece.type == pieceType.pawn){			
				if ( (fromY - toY == -2) || (fromY - toY == 2)){
					piece.pawnDoubleMove = true;
				}
				//Catches any diagional move so we still need to single out en passant moves
				else if( (fromY - toY == -1) || (fromY - toY == 1) &&
						 (fromX - toX == -1) || (fromX - toX == 1)){
					//white en passant moving upward
					if( (fromY - toY == 1) && (board[toX][toY+1].getPiece().pawnDoubleMove == true) )
						board[toX][toY+1].getPiece().setPieceState(pieceState.dead);
					//black en passant moving downward
					else if ( (fromY - toY == -1) && (board[toX][toY-1].getPiece().pawnDoubleMove == true) )
						board[toX][toY-1].getPiece().setPieceState(pieceState.dead);
				}
			}
			
			//For en passant logic.  Needs to be after the en passant capture logic to make it work
			if (lastPieceMoved == null){
				lastPieceMoved = piece;					//initialize
				//Don't need to set double move yet because there can't be an en passant on the 1st/2nd move
			}
			else{
				lastPieceMoved.pawnDoubleMove = false;	//resets last piece moved
				lastPieceMoved = piece;					//sets current piece to "last piece"
			}
			
			//Check for a castle.  All of the checking is done in king available moves, just move the rook now
			if (piece.type == pieceType.king && kingCastle == 1){
				
				//Special castle rules for Fischer Random Chess
				if(currentGameType == GameTypeConstraints.Fischer){						
					//King side castle
					if ( toX == 6){		//Moving to "G"
						//Grab the correct color rook.  The turn swap takes place above this
						if( turn == objectColor.white ) {piece = black.pieces[9];}
						else{piece = white.pieces[9];}	

						//Fix for the rook was on rank "G" and "killed" when the king moved there
						if (piece.state != pieceState.dead){
							board[piece.location.x][fromY].setPiece(null);	//Remove castle rook
						}
						board[5][fromY].setPiece(piece);				//Create castle rook at new location
						piece.setPieceState(pieceState.alive);			//"Enable"  castle rook
						piece.location = board[5][fromY];				//Fix castle rook location
					}
					//Queen side castle
					if ( toX == 2){		//Moving to "C"
						//Grab the correct color rook.  The turn swap takes place above this
						if( turn == objectColor.white ) {piece = black.pieces[8];}
						else{piece = white.pieces[8];}	

						//Fix for the rook was on rank "C" and "killed" when the king moved there
						if (piece.state != pieceState.dead){
							board[piece.location.x][fromY].setPiece(null);	//Remove castle rook
						}
						
						board[3][fromY].setPiece(piece);				//Create castle rook at new location
						piece.setPieceState(pieceState.alive);			//"Enable"  castle rook
						piece.location = board[3][fromY];				//Fix castle rook location
					}
					//Reset the castle variable.  May not be the right place
					kingCastle = 0;
				}
				else{
					//King side castle
					if (fromX - toX == -2){
						piece = board[fromX+3][fromY].getPiece();	//Grab castle rook
						board[fromX+3][fromY].setPiece(null);		//Remove castle rook
						board[fromX+1][fromY].setPiece(piece);		//Create castle rook at new location
						piece.setPieceState(pieceState.alive);		//"Enable"  castle rook
						piece.location = board[fromX+1][fromY];		//Fix castle rook location
					}
					//Queen side castle
					if (fromX - toX == 2){
						piece = board[fromX-4][fromY].getPiece();
						board[fromX-4][fromY].setPiece(null);
						board[fromX-1][fromY].setPiece(piece);
						piece.setPieceState(pieceState.alive);
						piece.location = board[fromX-1][fromY];
					}
					//Reset the castle variable.  May not be the right place
					kingCastle = 0;
				
				}//else not Fischer
			}	//if(piece.type == pieceType.king && piece.firstMove == true)
		}
		return success;
	}

	// TODO: finish this function.
	void populateBoard(cell[][] board) {
//	void populateBoard(cell[][] board, int startPosition) {
		// create player piece arrays
		// first 8 cells are pawns, then rooks, knight, bishops, queen, king
		piece blackPieces[] = new piece[16];
		piece whitePieces[] = new piece[16];

		// init the cells
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				board[x][y] = new cell(x, y);
			}
		}
		
		//Generate the random startup if its Fischer 960
		if (currentGameType == GameTypeConstraints.Fischer){
			initialPieces = generateFirstRank();
			//initialPieces = initialPieces3;		//For testing
		}
		
		//Convert initialPieces List into an array
		int[] initialArray = new int[initialPieces.size()];
		for(int i = 0; i < initialPieces.size(); i++) initialArray[i] = initialPieces.get(i);
		int rookCnt = 0;
		int bishCnt = 0;
		int kniCnt = 0;
			
		// White is on the bottom, black is on top
		// pawns
		for (int i = 0; i < 8; i++) {
			piece whitePawn = new piece(objectColor.white, pieceType.pawn,
					board[i][6], new pawn(), R.drawable.white_pawn);
			whitePieces[i] = whitePawn;
			board[i][6].setPiece(whitePawn);
			piece blackPawn = new piece(objectColor.black, pieceType.pawn,
					board[i][1], new pawn(), R.drawable.black_pawn);
			blackPieces[i] = blackPawn;
			board[i][1].setPiece(blackPawn);
		}
		
		//Place the pieces based on their location in the array
		for (int i = 0; i < 8; i++) {
			switch( initialArray[i]){
			// kings
			case 'K':
				whitePieces[15] = new piece(objectColor.white, pieceType.king,
						board[i][7], new king(), R.drawable.white_king);
				board[i][7].setPiece(whitePieces[15]);
				blackPieces[15] = new piece(objectColor.black, pieceType.king,
						board[i][0], new king(), R.drawable.black_king);
				board[i][0].setPiece(blackPieces[15]);
				break;
				
			case 'Q':
				whitePieces[14] = new piece(objectColor.white, pieceType.queen,
						board[i][7], new queen(), R.drawable.white_queen);
				board[i][7].setPiece(whitePieces[14]);
				blackPieces[14] = new piece(objectColor.black, pieceType.queen,
						board[i][0], new queen(), R.drawable.black_queen);
				board[i][0].setPiece(blackPieces[14]);
				break;
				
			case 'R':
				if ( rookCnt == 0 ){
					whitePieces[8] = new piece(objectColor.white, pieceType.rook,
						board[i][7], new rook(), R.drawable.white_rook);
					board[i][7].setPiece(whitePieces[8]);
					blackPieces[8] = new piece(objectColor.black, pieceType.rook,
						board[i][0], new rook(), R.drawable.black_rook);
					board[i][0].setPiece(blackPieces[8]);
					rookCnt = 1;
				}
				else{
					whitePieces[9] = new piece(objectColor.white, pieceType.rook,
						board[i][7], new rook(), R.drawable.white_rook);
					board[i][7].setPiece(whitePieces[9]);
					blackPieces[9] = new piece(objectColor.black, pieceType.rook,
						board[i][0], new rook(), R.drawable.black_rook);
					board[i][0].setPiece(blackPieces[9]);
				}
				break;
				
			case 'N':
				if ( kniCnt == 0 ){
					whitePieces[10] = new piece(objectColor.white, pieceType.knight,
							board[i][7], new knight(), R.drawable.white_knight);
					board[i][7].setPiece(whitePieces[10]);
					blackPieces[10] = new piece(objectColor.black, pieceType.knight,
							board[i][0], new knight(), R.drawable.black_knight);
					board[i][0].setPiece(blackPieces[10]);
					kniCnt = 1;
				}
				else{
					whitePieces[11] = new piece(objectColor.white, pieceType.knight,
							board[i][7], new knight(), R.drawable.white_knight);
					board[i][7].setPiece(whitePieces[11]);
					blackPieces[11] = new piece(objectColor.black, pieceType.knight,
							board[i][0], new knight(), R.drawable.black_knight);
					board[i][0].setPiece(blackPieces[11]);
				}
				break;
			case 'B':
				if ( bishCnt == 0 ){
					whitePieces[12] = new piece(objectColor.white, pieceType.bishop,
							board[i][7], new bishop(), R.drawable.white_bishop);
					board[i][7].setPiece(whitePieces[12]);
					blackPieces[12] = new piece(objectColor.black, pieceType.bishop,
							board[i][0], new bishop(), R.drawable.black_bishop);
					board[i][0].setPiece(blackPieces[12]);
					bishCnt = 1;
				}
				else{
					whitePieces[13] = new piece(objectColor.white, pieceType.bishop,
							board[i][7], new bishop(), R.drawable.white_bishop);
					board[i][7].setPiece(whitePieces[13]);
					blackPieces[13] = new piece(objectColor.black, pieceType.bishop,
							board[i][0], new bishop(), R.drawable.black_bishop);
					board[i][0].setPiece(blackPieces[13]);
				}
				break;		
			}
		}
		
		white = new player(objectColor.white, whitePieces);
		black = new player(objectColor.black, blackPieces);
	}
	
	void populateBoardMaharaja(cell[][] board) {
		// create player piece arrays
		// first 8 cells are pawns, then rooks, knight, bishops, queen, king, then white's maharaja
		piece blackPieces[] = new piece[16];
		piece whitePieces[] = new piece[1];

		// init the cells
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				board[x][y] = new cell(x, y);
			}
		}

		// There are only black pieces in Maharaja
		// pawns
		for (int i = 0; i < 8; i++) {
			piece blackPawn = new piece(objectColor.black, pieceType.pawn,
					board[i][1], new pawn(), R.drawable.black_pawn);
			blackPieces[i] = blackPawn;
			board[i][1].setPiece(blackPawn);
		}

		// rooks
		blackPieces[8] = new piece(objectColor.black, pieceType.rook,
				board[0][0], new rook(), R.drawable.black_rook);
		board[0][0].setPiece(blackPieces[8]);
		blackPieces[9] = new piece(objectColor.black, pieceType.rook,
				board[7][0], new rook(), R.drawable.black_rook);
		board[7][0].setPiece(blackPieces[9]);

		// knights
		blackPieces[10] = new piece(objectColor.black, pieceType.knight,
				board[1][0], new knight(), R.drawable.black_knight);
		board[1][0].setPiece(blackPieces[10]);
		blackPieces[11] = new piece(objectColor.black, pieceType.knight,
				board[6][0], new knight(), R.drawable.black_knight);
		board[6][0].setPiece(blackPieces[11]);
		
		// bishops
		blackPieces[12] = new piece(objectColor.black, pieceType.bishop,
				board[2][0], new bishop(), R.drawable.black_bishop);
		board[2][0].setPiece(blackPieces[12]);
		blackPieces[13] = new piece(objectColor.black, pieceType.bishop,
				board[5][0], new bishop(), R.drawable.black_bishop);
		board[5][0].setPiece(blackPieces[13]);
		
		// queen
		blackPieces[14] = new piece(objectColor.black, pieceType.queen,
				board[3][0], new queen(), R.drawable.black_queen);
		board[3][0].setPiece(blackPieces[14]);
		
		// king
		blackPieces[15] = new piece(objectColor.black, pieceType.king,
				board[4][0], new king(), R.drawable.black_king);
		board[4][0].setPiece(blackPieces[15]);
		
		// Maharaja
		whitePieces[0] = new piece(objectColor.white, pieceType.queen,
				board[4][7], new maharaja(), R.drawable.white_queen);
		board[4][7].setPiece(whitePieces[0]);
				
		white = new player(objectColor.white, whitePieces);
		black = new player(objectColor.black, blackPieces);
	}


	// .ctor
	chessCore() {
		// if new game
		deadCell = new cell(-1, -1);
		turn = objectColor.white;
		currentGameState = gameState.allClear;
	}

	//Sets the current Game Type
	public void setGameType(int typeOfGame){
			currentGameType =  typeOfGame;
			board = new cell[8][8];
			
			//Maharaja has weird rules that break other things so I made them seperate
			if (currentGameType == GameTypeConstraints.Maharaja){
				populateBoardMaharaja(board);
			}else{
				populateBoard(board);
			}
	}
	
//All the following code is for Fischer 960 generation
	List<Character> initialPieces = Arrays.asList('R','N','B','Q','K','B','N','R');
//	List<Character> initialPieces2 = Arrays.asList('B','R','K','R','N','N','Q','B');
//	List<Character> initialPieces3 = Arrays.asList('B','B','Q','N','N','R','K','R');
	
	List<Character> generateFirstRank(){
		do{
			Collections.shuffle(initialPieces);
		}while(!checkValid960(initialPieces.toString().replaceAll("[^\\p{Upper}]", ""))); //List.toString adds some human stuff, remove that 
 
		return initialPieces;
	}
	
	boolean checkValid960(String rank){
		if(!rank.matches(".*R.*K.*R.*")) 
			return false;			//king between rooks
			
		if(!rank.matches(".*B(..|....|......|)B.*")) 
			return false;	//all possible ways bishops can be placed
			
		return true;
	}

//End Fischer 960 generation code

}
