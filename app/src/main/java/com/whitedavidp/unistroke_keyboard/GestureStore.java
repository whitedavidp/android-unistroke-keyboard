package com.whitedavidp.unistroke_keyboard;

import android.content.Context;

import com.whitedavidp.unistroke_keyboard.gesture.Gesture;
import com.whitedavidp.unistroke_keyboard.gesture.GestureLibraries;
import com.whitedavidp.unistroke_keyboard.gesture.GestureLibrary;
import com.whitedavidp.unistroke_keyboard.gesture.Prediction;

import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

class GestureStore
{
    public static final int FLAG_CATEGORY_ALPHABET = 1;
    public static final int FLAG_CATEGORY_NUMBER = 2;
    public static final int FLAG_CATEGORY_SPECIAL = 4;
    public static final int FLAG_CATEGORY_CONTROL = 8;
    public static final int FLAG_STRICT = 16;

    private final ApplicationResources resources;
    private final ArrayList<WeightedGestureLibrary> libraries = new ArrayList<>();

    public GestureStore(Context context, ApplicationResources resources)
    {
        this.resources = resources;
        Loader loader = new Loader(context);
        libraries.add(loader.load(1.0, R.raw.gestures_alphabet, FLAG_CATEGORY_ALPHABET));
        libraries.add(loader.load(1.0, R.raw.gestures_number, FLAG_CATEGORY_NUMBER));
        libraries.add(loader.load(1.0, R.raw.gestures_special, FLAG_CATEGORY_SPECIAL));
        libraries.add(loader.load(0.7, R.raw.gestures_control, FLAG_CATEGORY_CONTROL));
    }

    public PredictionResult recognize(Gesture gesture, int flags)
    {
        PredictionResult prediction = PredictionResult.Zero;
        
        // if the user manages to enter a multi-stroke, inform and ignore it
        if(gesture.getStrokesCount() > 1)
        {
          String s = App.getAppContext().getString(R.string.multi_stroke_entered);
          App.showToast(s);
          App.log("GestureStore", s);
          return prediction;
        }

        for (WeightedGestureLibrary library: libraries)
        {
            if ((flags & library.category) != 0)
            {
                prediction = prediction.choose(library.recognize(gesture, flags));
            }
        }

        if(App.isShowResultsEnabled())
        {
          String name = null;
          if(prediction.equals(PredictionResult.Zero))
          {
            name = App.getAppContext().getString(R.string.unrecognized);
          }
          else
          {
            if(prediction.score < App.getMinimumRecognitionScore())
            {
              name = App.getAppContext().getString(R.string.poor_result) + prediction.name;
            }
            else
            {
              name = prediction.name;
            }
          }

          App.displayResults(App.getAppContext().getString(R.string.recognition) + name + " / " + prediction.score);
        }
        
        return prediction;
    }

    private class Loader
    {
        private final Context context;

        public Loader(Context context)
        {
            this.context = context;
        }        

        public WeightedGestureLibrary load(double weight, int rawId, int category)
        {
            GestureLibrary library = null;           
            String fileName = App.mapGestureResourceIdToFileName(rawId);
            
            if(App.isLoadFromFilesEnabled())
            {
              // if we find a file with the expected name in the root of the device's internal storage, use it
              // note: this requires we target older sdk (as does the Gesture Builder app)
              if(null != fileName)
              {
                File f = new File(Environment.getExternalStorageDirectory(), fileName);
                if(f.exists() && f.canRead())
                {
                  library = GestureLibraries.fromFile(f);
                  if(null != library)
                  {
                    App.log(fileName, "gestures for " + fileName + " loaded from storage");
                  }
                  else
                  {
                    App.log(fileName, "gestures for " + fileName + " failed to load from storage");
                  }
                }
                else
                {
                  App.log(fileName, "File exists: " + f.exists() + ", Can be read: " + f.canRead());
                }
              }
            }
            
            if(null == library)
            {
              App.log(fileName, "Loading " + fileName + " from resources");
              library = GestureLibraries.fromRawResource(context, rawId);
            }
            
            App.preGestureLibraryLoad(library);
            
            boolean successfulLoad = library.load();
            if(!successfulLoad)
            {
              App.log(fileName, "Library " + fileName + " did not load properly");
            }

            // check the library for unexpected (but possible due to GestureBuilder) multi-strokes
            long t0 = new Date().getTime();
            Set<String> gestureNames = library.getGestureEntries();
            Iterator<String> nameIterator = gestureNames.iterator();
            while(nameIterator.hasNext())
            {
              String gestureName = nameIterator.next();
              ArrayList<Gesture> gestureEntries = library.getGestures(gestureName);
              for(int i = 0; i < gestureEntries.size(); i++)
              {
                if(gestureEntries.get(i).getStrokesCount() > 1)
                {
                  String s = App.getAppContext().getString(R.string.library_contains_multi_stroke);
                  App.showToast(s + fileName + "/" + gestureName);
                  App.log("GestureStore", s + gestureName);
                }
              }
            }
            
            App.log(fileName, "processed in " + (new Date().getTime() - t0) + " ms");
            return new WeightedGestureLibrary(library, category, weight);
        }
    }

    private class WeightedGestureLibrary
    {
        private final GestureLibrary library;
        private final int category;
        private final double weight;

        public WeightedGestureLibrary(GestureLibrary library, int category, double weight)
        {
            this.library = library;
            this.category = category;
            this.weight = weight;
        }
        
        public PredictionResult recognize(Gesture gesture, int flags)
        {
            if (gesture.getLength() < resources.getPeriodTolerance())
            {
                return new PredictionResult("period", Double.POSITIVE_INFINITY);
            }

            ArrayList<Prediction> predictions = null;
            predictions = library.recognize(gesture);
            if (predictions.size() == 0)
            {
                return PredictionResult.Zero;
            }

            PredictionResult first = null; 
            first = new PredictionResult(predictions.get(0), weight);

            if ((flags & FLAG_STRICT) == 0)
            {
                return first;
            }

            if (first.score < App.getMinimumRecognitionScore())
            {
                return PredictionResult.Zero;
            }

            if (predictions.size() == 1)
            {
                return first;
            }
            
            PredictionResult next = null;
            next = new PredictionResult(predictions.get(1), weight);

            if (first.score < next.score + 0.2)
            {
                return PredictionResult.Zero;
            }

            return first;
        }
    }
}
