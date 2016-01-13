package com.example.myfirstapp;

import android.os.Parcel;
import android.os.Parcelable;

public class parcableBoard implements Parcelable{
	//For Parcelable
	     private int mData;

	     public int describeContents() {
	         return 0;
	     }

	     /** save object in parcel */
	     public void writeToParcel(Parcel out, int flags) {
	         out.writeInt(mData);
	     }

	     public final Parcelable.Creator<parcableBoard> CREATOR
	             = new Parcelable.Creator<parcableBoard>() {
	         public parcableBoard createFromParcel(Parcel in) {
	             return new parcableBoard(in);
	         }

	         public parcableBoard[] newArray(int size) {
	             return new parcableBoard[size];
	         }
	     };

	     /** recreate object from parcel */
	     private parcableBoard(Parcel in) {
	         mData = in.readInt();
	         
	     }
}
