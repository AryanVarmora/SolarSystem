public class Moon {
    String name;
    double diameter;
    double circumference;

    public Moon(String name, double diameter, double circumference) {
        this.name = name;
        this.diameter = (diameter > 0) ? diameter : (circumference > 0 ? circumference / Math.PI : -1);
        this.circumference = (circumference > 0) ? circumference : (diameter > 0 ? Math.PI * diameter : -1);
    }
}
