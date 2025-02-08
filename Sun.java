import java.util.List;

public class Sun {
    String name;
    double diameter;
    double circumference;
    List<Planet> planets;

    public Sun(String name, double diameter, double circumference, List<Planet> planets) {
        this.name = name;
        this.diameter = diameter;
        this.circumference = circumference;
        this.planets = planets;
    }

    public double getVolume() {
        double radius = this.diameter / 2.0;
        return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
    }
}
