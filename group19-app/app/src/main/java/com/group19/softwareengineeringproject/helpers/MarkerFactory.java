package com.group19.softwareengineeringproject.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.group19.softwareengineeringproject.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

//Not exactly based on the factory pattern but makes sense to name it like that
public final class MarkerFactory {


    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId, Bitmap thumbnail) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Drawable markerVector = ContextCompat.getDrawable(context, vectorResId);

        markerVector.setBounds(0, 0, markerVector.getIntrinsicWidth(), markerVector.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(markerVector.getIntrinsicWidth(), markerVector.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        markerVector.draw(canvas);
        if(thumbnail == null) {
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }

        Bitmap res = createCircleBitmap(thumbnail, (int)(0.75f * markerVector.getIntrinsicHeight()));
        canvas.drawBitmap(res,
                (markerVector.getIntrinsicWidth() - res.getWidth())/2,
                //To calculate this calculate size of pointy edge.
                //Then 1 - (size/maxSize); 1 - (45[height of pointy]/500[height of vector]) = 1 - 0.09 = 0.91
                (0.91f * markerVector.getIntrinsicHeight() -res.getWidth())/2, paint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private static Bitmap createCircleBitmap (Bitmap bit, int size) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        paint.setColor(0XFF000000);
        Path path = new Path();
        path.addCircle(size/2,size/2, size/2, Path.Direction.CCW);

        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bit,
                0,
                0, paint);

        canvas.drawBitmap(bit, null, new RectF(0, 0, size, size), paint);

        return output;
    }

    public static BitmapDescriptor createUserMarker (Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
