package com.whitedavidp.unistroke_keyboard;

import com.whitedavidp.unistroke_keyboard.gesture.Prediction;

import dollar.one.DollarOneMatchResult;

class PredictionResult
{
    public static final PredictionResult Zero = new PredictionResult();

    public final double score;
    public final String name;

    public PredictionResult()
    {
        this.score = 0;
        this.name = null;
    }

    public PredictionResult(String name, double score)
    {
        this.name = name;
        this.score = score;
    }

    public PredictionResult(Prediction prediction, double scale)
    {
        this.name = prediction.name;
        this.score = prediction.score * scale;
    }

    public PredictionResult(DollarOneMatchResult prediction, double scale)
    {
        this.name = prediction.getName();
        this.score = prediction.getScore() * scale;
    }

    public PredictionResult choose(PredictionResult other)
    {
        return (score > other.score) ? this : other;
    }
}

