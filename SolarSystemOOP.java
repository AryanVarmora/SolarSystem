import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.nio.file.*;
import java.io.IOException;
// Import the separate class files
import java.util.*;
import org.apache.commons.text.similarity.LevenshteinDistance;


public class SolarSystemOOP {
    private static Sun sun;
    private static final List<String> entries = new ArrayList<>();
    private static final LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();


    public static void main(String[] args) {
        displayIntro();

        try {
            String content = new String(Files.readAllBytes(Paths.get("data.json")));
            JSONObject jsonData = new JSONObject(content);
            sun = parseSun(jsonData);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Enter the name of the planet you want information about (or type 'exit' to finish): ");
                String planetName = scanner.nextLine();
                if (planetName.equalsIgnoreCase("exit")) {
                    break;
                }
                if (!displayPlanetInfo(planetName)) {
                    String suggestedPlanet = suggestClosestPlanet(planetName);
                    int distance = levenshtein.apply(planetName.toLowerCase(), suggestedPlanet.toLowerCase());

                    if (distance < 3) {  // If suggestion is very close, auto-correct
                        System.out.println("Did you mean: " + suggestedPlanet + "? Selecting it automatically...");
                        entries.add(suggestedPlanet);
                        displayPlanetInfo(suggestedPlanet);
                    } else {
                        System.out.println("Planet not found! Did you mean: " + suggestedPlanet + "? (yes/no)");
                        String response = scanner.nextLine();
                        if (response.equalsIgnoreCase("yes")) {
                            entries.add(suggestedPlanet);
                            displayPlanetInfo(suggestedPlanet);
                        }
                    }
                } else {
                    entries.add(planetName);
                }
            }

            checkPlanetVolumeVsSun();
            scanner.close();
        } catch (IOException | JSONException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void displayIntro() {
        System.out.println("\n🌌 Welcome to the Solar System Explorer 🌍✨");
        System.out.print("Loading data");
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n");
    }

    private static Sun parseSun(JSONObject jsonData) {
        try {
            String name = jsonData.getString("Name");
            double diameter = jsonData.getDouble("Diameter");
            double circumference = (diameter * Math.PI);
            List<Planet> planets = new ArrayList<>();

            JSONArray planetsArray = jsonData.getJSONArray("Planets");
            for (int i = 0; i < planetsArray.length(); i++) {
                planets.add(parsePlanet(planetsArray.getJSONObject(i)));
            }
            return new Sun(name, diameter, circumference, planets);
        } catch (JSONException e) {
            System.err.println("Error parsing Sun data: " + e.getMessage());
            return null;
        }
    }

    private static Planet parsePlanet(JSONObject planetData) {
        String name = planetData.getString("Name");
        double distanceFromSun = planetData.optDouble("DistanceFromSun", -1);
        double orbitalPeriod = planetData.optDouble("OrbitalPeriod", (distanceFromSun > 0 ? Math.pow(distanceFromSun, 1.5) : -1));
        double diameter = planetData.optDouble("Diameter", -1);
        double circumference = planetData.optDouble("Circumference", (diameter > 0 ? Math.PI * diameter : -1));

        List<Moon> moons = new ArrayList<>();
        if (planetData.has("Moons")) {
            JSONArray moonsArray = planetData.getJSONArray("Moons");
            for (int i = 0; i < moonsArray.length(); i++) {
                moons.add(parseMoon(moonsArray.getJSONObject(i)));
            }
        }
        return new Planet(name, distanceFromSun, orbitalPeriod, diameter, circumference, moons);
    }

    private static Moon parseMoon(JSONObject moonData) {
        String name = moonData.getString("Name");
        double diameter = moonData.optDouble("Diameter", -1);
        double circumference = moonData.optDouble("Circumference", (diameter > 0 ? Math.PI * diameter : -1));
        return new Moon(name, diameter, circumference);
    }

    private static boolean displayPlanetInfo(String planetName) {
        for (Planet planet : sun.planets) {
            if (planet.name.equalsIgnoreCase(planetName)) {
                System.out.printf("\n🌍 Planet: %s\n", planet.name);
                System.out.printf("🌞 Distance from Sun: %.2f AU\n", planet.distanceFromSun);
                System.out.printf("⏳ Orbital Period: %.2f years\n", planet.orbitalPeriod);
                System.out.printf("⚪ Diameter: %.2f km\n", planet.diameter);
                System.out.printf("🔄 Circumference: %.2f km\n", planet.circumference);
                return true;
            }
        }
        return false;
    }

    private static String suggestClosestPlanet(String userInput) {
        int minDistance = Integer.MAX_VALUE;
        String closestMatch = "No suggestion available";

        for (Planet planet : sun.planets) {
            int distance = levenshtein.apply(userInput.toLowerCase(), planet.name.toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = planet.name;
            }
        }
        return closestMatch;
    }

    private static void checkPlanetVolumeVsSun() {
        double totalSelectedVolume = 0;
        for (String planetName : entries) {
            for (Planet planet : sun.planets) {
                if (planet.name.equalsIgnoreCase(planetName)) {
                    totalSelectedVolume += planet.getVolume();
                    break;
                }
            }
        }
        double sunVolume = sun.getVolume();
        System.out.println("\n🪐 Total volume of selected planets: " + totalSelectedVolume + " cubic km");
        System.out.println("☀️ Volume of the Sun: " + sunVolume + " cubic km");
        System.out.println("📦 Can the selected planets fit inside the Sun? " + (totalSelectedVolume < sunVolume));
    }
}
