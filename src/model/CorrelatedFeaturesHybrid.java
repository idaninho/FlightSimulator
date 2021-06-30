package model;

public class CorrelatedFeaturesHybrid {
    public final String feature1, feature2;
    public final float corrlation;


    public CorrelatedFeaturesHybrid(String feature1, String feature2, float corrlation) {
        this.feature1 = feature1;
        this.feature2 = feature2;
        this.corrlation = corrlation;
    }
}
