/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whitedavidp.unistroke_keyboard.gesture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

import com.whitedavidp.unistroke_keyboard.App;

/**
 * An implementation of an instance-based learner
 */

class InstanceLearner extends Learner {
    private static final Comparator<Prediction> sComparator = new Comparator<Prediction>() {
        public int compare(Prediction object1, Prediction object2) {
            double score1 = object1.score;
            double score2 = object2.score;
            if (score1 > score2) {
                return -1;
            } else if (score1 < score2) {
                return 1;
            } else {
                return 0;
            }
        }
    };
    
    private double distance(float p1X, float p1Y, float p2X, float p2Y) // Euclidean distance between two points
    {
      double dx = p2X - p1X;
      double dy = p2Y - p1Y;
      return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    ArrayList<Prediction> classify(int sequenceType, int orientationType, float[] vector) {
        ArrayList<Prediction> predictions = new ArrayList<Prediction>();
        ArrayList<Instance> instances = getInstances();
        int count = instances.size();
        TreeMap<String, Double> label2score = new TreeMap<String, Double>();
        for (int i = 0; i < count; i++) {
            Instance sample = instances.get(i);
            if (sample.vector.length != vector.length) {
                continue;
            }
                       
            double distance;
            if (sequenceType == GestureStore.SEQUENCE_SENSITIVE) {
                // try to make sure that the begin and end points are close enough to indicate proper directionality
                // this is something from my implementation of point cloud that SEEMS, if CLOSE_ENOUGH is small enough,
                // to improve false recognitions
                float gestureFirstX = vector[0];
                float gestureFirstY = vector[1];
                float gestureLastY = vector[vector.length - 1];
                float gestureLastX = vector[vector.length - 2];
                float sampleFirstX = sample.vector[0];
                float sampleFirstY = sample.vector[1];
                float sampleLastY = sample.vector[sample.vector.length - 1];
                float sampleLastX = sample.vector[sample.vector.length - 2];
                
                double distanceStart = distance(gestureFirstX, gestureFirstY, sampleFirstX, sampleFirstY);
                double distanceEnd = distance(gestureLastX, gestureLastY, sampleLastX, sampleLastY);
                
                float closeEnuf = App.getCloseEnough();
                if((distanceStart > closeEnuf) || (distanceEnd > closeEnuf))
                {
                  App.log("InstanceLearner", sample.label + " would be skipped due to not close enough");
                  if(App.getCheckCloseEnough())
                  {
                    continue;
                  }
                }
                else
                {
                  App.log("InstanceLearner", sample.label + " would not be skipped due to not close enough");
                }
                
                distance = GestureUtils.minimumCosineDistance(sample.vector, vector, orientationType);
            } else {
                distance = GestureUtils.squaredEuclideanDistance(sample.vector, vector);
            }
            double weight;
            if (distance == 0) {
                weight = Double.MAX_VALUE;
            } else {
                weight = 1 / distance;
            }
            Double score = label2score.get(sample.label);
            if (score == null || weight > score) {
                App.log("InstanceLearner", sample.label + " would not be skipped due weight: " + weight + " > " + score);
                label2score.put(sample.label, weight);
            }
            else
            {
              App.log("InstanceLearner", sample.label + " would be skipped due weight: " + weight + " > " + score);
            }
        }

//        double sum = 0;
        for (String name : label2score.keySet()) {
            double score = label2score.get(name);
//            sum += score;
            App.log("InstanceLearner", name + " being added to prediction list - score: " + score);
            predictions.add(new Prediction(name, score));
        }

        // normalize
//        for (Prediction prediction : predictions) {
//            prediction.score /= sum;
//        }

        Collections.sort(predictions, sComparator);

        return predictions;
    }
}
