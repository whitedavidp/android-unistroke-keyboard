package com.whitedavidp.unistroke_keyboard;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.Prediction;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;

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
            String fileName = null;
            GestureLibrary library = null;
            
            switch(rawId)
            {
              case R.raw.gestures_alphabet:
              {
                fileName = "gestures_alphabet";
                break;
              }
              case R.raw.gestures_number:
              {
                fileName = "gestures_number";
                break;
              }
              case R.raw.gestures_special:
              {
                fileName = "gestures_special";
                break;
              }
              case R.raw.gestures_control:
              {
                fileName = "gestures_control";
                break;
              }  
              default:
              {
                fileName = null;
              }
            }
            
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
                    App.Log(fileName, "gestures for " + fileName + " loaded from storage");
                  }
                  else
                  {
                    App.Log(fileName, "gestures for " + fileName + " failed to load from storage");
                  }
                }
                else
                {
                  App.Log(fileName, "File exists: " + f.exists() + ", Can be read: " + f.canRead());
                }
              }
            }
            
            if(null == library)
            {
              App.Log(fileName, "Loading " + fileName + " from resources");
              library = GestureLibraries.fromRawResource(context, rawId);
            }
            
            // note: I fooled around with the settings shown here: https://stackoverflow.com/questions/7743462/does-having-variations-of-gestures-in-gesture-library-improve-recognition
            // with only worse results. so I leave this as-is. the libraries should be SEQUENCE_SENSITIVE by default
            library.setOrientationStyle(8);
            boolean successfulLoad = library.load();
            if(!successfulLoad)
            {
              App.Log(fileName, "Library " + fileName + " did not load properly");
            }
            
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

            ArrayList<Prediction> predictions = library.recognize(gesture);
            if (predictions.size() == 0)
            {
                return PredictionResult.Zero;
            }

            PredictionResult first = new PredictionResult(predictions.get(0), weight);

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

            PredictionResult next = new PredictionResult(predictions.get(1), weight);

            if (first.score < next.score + 0.2)
            {
                return PredictionResult.Zero;
            }

            return first;
        }
    }
}
