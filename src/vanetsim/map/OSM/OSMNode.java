package vanetsim.map.OSM;

import vanetsim.map.Node;

public final class OSMNode {

    private static double longitudeMiddle_ = 1.0;

    private static double correctionX_ = 0;

    private static double correctionY_ = 0;

    private final double latitude_;

    private final double longitude_;

    private final boolean hasTrafficSignal_;

    private String amenity_;

    public OSMNode(double latitude, double longitude) {
        latitude_ = latitude;
        longitude_ = longitude;
        hasTrafficSignal_ = false;
    }

    public OSMNode(double latitude, double longitude, boolean hasTrafficSignal) {
        latitude_ = latitude;
        longitude_ = longitude;
        hasTrafficSignal_ = hasTrafficSignal;
    }

    public OSMNode(double latitude, double longitude, String amenity) {
        latitude_ = latitude;
        longitude_ = longitude;
        hasTrafficSignal_ = false;
        amenity_ = amenity;
    }

    public static void setCorrections(double longitudeMiddle, double correctionX, double correctionY) {
        longitudeMiddle_ = longitudeMiddle;
        correctionX_ = correctionX;
        correctionY_ = correctionY;
    }

    public Node getRealNode() {
        double[] result = new double[2];
        OSMLoader.getInstance().WGS84toUTM(result, longitude_, latitude_, false, longitudeMiddle_, false);
        int x = (int) Math.round((result[0] - correctionX_) * 100);
        int y = (int) Math.round((correctionY_ - result[1]) * 100);
        return new Node(x, y, hasTrafficSignal_);
    }

    public String getAmenity_() {
        return amenity_;
    }

    public void setAmenity_(String amenity_) {
        this.amenity_ = amenity_;
    }
}
