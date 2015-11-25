package framework.mobisys.netlab.framework;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by LZQ on 11/25/2015.
 */
public class IByteArray implements Parcelable {

    public static final Creator CREATOR = new Creator() {

        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public IByteArray createFromParcel(Parcel source) {
            Log.d("IByteArray", "in createFromParcel");
            return new IByteArray(source);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public IByteArray[] newArray(int size) {
            Log.d("IByteArray", "in newArray");
            return new IByteArray[size];
        }
    };
    final String TAG = "IByteArray";
    private byte[] _byte;


    public IByteArray() {
    }

    public IByteArray(Parcel in) {
        Log.d(TAG, "in IByteArray");
        readFromParcel(in);
    }

    public byte[] getByte() {
        Log.d(TAG, "in getByte");
        return _byte;
    }

    public void setByte(byte[] b) {
        Log.d(TAG, "in setByte");
        this._byte = b;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        Log.d(TAG, "in describeContents");
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "in writeToParcel");
        dest.writeInt(_byte.length);
        dest.writeByteArray(_byte);
    }

    public void readFromParcel(Parcel in) {
        Log.d(TAG, "in readFromParcel");
        _byte = new byte[in.readInt()];
        in.readByteArray(_byte);
    }
}