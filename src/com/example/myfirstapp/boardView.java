package com.example.myfirstapp;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.myfirstapp.chessCore.cell;
import com.example.myfirstapp.chessCore.piece;


public class boardView extends View
{

	// selected acts as an indicator as to  whether a cell has been selected
	// if so, the next screen event will be the selection of the move to cell
	boolean pieceSelected;
	boolean reset;
	
	// Yzero is the top point for the board, as in board is placed at [0, Yzero]
	private int fromX, fromY, toX, toY, Yzero, width;
	private chessCore core;
	public void setCore(chessCore core)
	{
		if(core != null)
			this.core = core;
	}
	
	private Resources res;
	private Canvas canvas;
	private Drawable boardImg;
	
	private int getBoardX(int x)
	{
		return x * width / 8;
	}
	
	private int getBoardY(int y)
	{
		return y * width / 8;
	}
	
	private void drawBoard(cell[][] board)
	{	
		super.invalidate();

		Drawable boardImg = getResources().getDrawable(R.drawable.chessboard2);	
		width =  canvas.getWidth(); 
		//Yzero =  (int)(canvas.getHeight() - width) / 2;	//modified to keep at the top of the screen
		Yzero =  canvas.getHeight() ;

		//Math to keep the ratio square	
		if (width >= Yzero){
			width = Yzero;
		}
		else{
			Yzero = width;
		}
		
		boardImg.setBounds(0, 0, width, Yzero);
		boardImg.draw(canvas);
		for(int x = 0; x < 8; x++)
		{
			for(int y = 0; y < 8; y++)
			{
				piece piece = board[x][y].getPiece();
				if(piece != null)
				{
					Drawable figure = res.getDrawable(piece.getImageResource());
					figure.setBounds(getBoardX(x), getBoardY(y), getBoardX(x) + width/8, getBoardY(y) + width /8);
					figure.draw(canvas);
				}
			}
		}
	}
	
	private void drawAvailableMoves(cell[][] board, int x, int y)
	{
		piece piece = board[x][y].getPiece();
		if(piece != null && piece.getPieceColor() == core.getTurn())
		{
			Drawable selection = res.getDrawable(R.drawable.selected);
			selection.setBounds(getBoardX(x), getBoardY(y), getBoardX(x) + width/8, getBoardY(y) + width /8);
			selection.draw(canvas);
			
			ArrayList<cell> availMoves = piece.getAvailableMoves();
			for(int i = 0; i < availMoves.size(); i ++)
			{
				if(piece.isValidMove(availMoves.get(i)))
				{
					cell availMove = availMoves.get(i);
					Drawable circle = res.getDrawable(R.drawable.selectioncircle);
					circle.setBounds(getBoardX(availMove.getX()), getBoardY(availMove.getY()), getBoardX(availMove.getX()) + width/8, getBoardY(availMove.getY()) + width /8);
					circle.draw(canvas);
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		// X and Y have to be integers 0-7
		// so what we need to do for x is x / (full width / 8)
		// y is similar but y - top space / (width of board / 8)
		if(!pieceSelected)
		{	
			fromX = (int)(event.getX() / (width / 8.0));
//			fromY = (int)((event.getY() - Yzero) / (width / 8.0));
			fromY = (int)(event.getY() / (width / 8.0));
			if( fromX < 0 || fromX > 7 || fromY < 0 || fromY > 7)
				return true;
		}
		else
		{
			toX = (int)(event.getX() / (width / 8.0));
			//toY = (int)((event.getY() - Yzero) / (width / 8.0));
			toY = (int)(event.getY() / (width / 8.0));
			// moving to the same place shouldn't be an issue, i don't think
			// TODO: verify this
//			if( toX < 0 || toX > 7 || toY < 0 || toY > 7 || (toX == fromX && toY == fromY)); //Lomanno changed from this from that
			if( toX < 0 || toX > 7 || toY < 0 || toY > 7 );
			else
			{
				try
				{
					core.move(fromX, fromY, toX,toY);
					pieceSelected = false;
					return true;
				}catch( Exception ex)
				{
					pieceSelected = false;
					return true;
					// TODO: handle this bizsnatch
				}
			}
		}
		pieceSelected = !pieceSelected;
		return true;
	}

	// Draw Chess board and set up logic connections
	// TODO: probably not here. Set up to and from cell selection
	// this may be related to view available moves. 
	@Override
	protected void onDraw(Canvas canvas) 
	{		
		this.canvas = canvas;
		this.drawBoard(core.getBoard());
		if(pieceSelected)
			drawAvailableMoves(core.getBoard(), fromX, fromY);
	}
	
	boardView(Context context)
	{
		super(context);
		
		pieceSelected = false;
		reset = true;
		fromX =-1;
		fromY = -1;
		toX = -1;
		toY = -1;
		res = getResources();
		
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
 
	public class CustomView extends View {

		  public CustomView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		private int stateToSave;
		  // ... variables

		  @Override
		  public Parcelable onSaveInstanceState() {

		    Bundle bundle = new Bundle();
		    bundle.putParcelable("instanceState", super.onSaveInstanceState());
		    bundle.putInt("stateToSave", this.stateToSave);
		    // ... save everything
		    return bundle;
		  }

		  @Override
		  public void onRestoreInstanceState(Parcelable state) {

		    if (state instanceof Bundle) {
		      Bundle bundle = (Bundle) state;
		      this.stateToSave = bundle.getInt("stateToSave");
		      // ... load everything
		      state = bundle.getParcelable("instanceState");
		    }
		    super.onRestoreInstanceState(state);
		  }
		}
	
	//For Parcelable
/*	 public class MyParcelable implements Parcelable {
	     private int mData;

	     public int describeContents() {
	         return 0;
	     }

	     // save object in parcel 
	     public void writeToParcel(Parcel out, int flags) {
	         out.writeInt(mData);
	     }

	     public final Parcelable.Creator<MyParcelable> CREATOR
	             = new Parcelable.Creator<MyParcelable>() {
	         public MyParcelable createFromParcel(Parcel in) {
	             return new MyParcelable(in);
	         }

	         public MyParcelable[] newArray(int size) {
	             return new MyParcelable[size];
	         }
	     };

	     // recreate object from parcel
	     private MyParcelable(Parcel in) {
	         mData = in.readInt();
	     }
	 }
	 */ 
}
