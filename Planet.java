import java.util.List;

public class Planet {
    String name;
    double distanceFromSun;
    double orbitalPeriod;
    double diameter;
    double circumference;
    List<Moon> moons;

    public Planet(String name, double distanceFromSun, double orbitalPeriod, double diameter, double circumference, List<Moon> moons) {
        this.name = name;
        this.distanceFromSun = distanceFromSun;
        this.orbitalPeriod = orbitalPeriod;
        this.diameter = (diameter > 0) ? diameter : (circumference > 0 ? circumference / Math.PI : -1);
        this.circumference = (circumference > 0) ? circumference : (diameter > 0 ? Math.PI * diameter : -1);
        this.moons = moons;
    }

    public double getVolume() {
        double radius = this.diameter / 2.0;
        return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
    }
}
