import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class SolarSystemProcedural {
    private static final List<String> entries = new ArrayList<>();
    private static final List<String> planetNames = new ArrayList<>();

    public static void main(String[] args) {
        try {
            displayIntro();

            String content = new String(Files.readAllBytes(Paths.get("data.json"))); // Ensure JSON file exists
            JSONObject jsonData = new JSONObject(content);

            String sunName = jsonData.getString("Name");
            double sunDiameter = jsonData.getDouble("Diameter");
            double sunCircumference = sunDiameter * Math.PI;

            JSONArray planetsArray = jsonData.getJSONArray("Planets");

            for (int i = 0; i < planetsArray.length(); i++) {
                planetNames.add(planetsArray.getJSONObject(i).getString("Name"));
            }

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Enter the name of the planet you want information about (or type 'exit' to finish): ");
                String planetName = scanner.nextLine();
                if (planetName.equalsIgnoreCase("exit")) {
                    break;
                }
                entries.add(planetName);
                displayPlanetInfo(planetsArray, planetName);
            }

            checkPlanetVolumeVsSun(planetsArray, sunDiameter);
            scanner.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
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

    private static void displayPlanetInfo(JSONArray planetsArray, String planetName) {
        for (int i = 0; i < planetsArray.length(); i++) {
            JSONObject planet = planetsArray.getJSONObject(i);
            if (planet.getString("Name").equalsIgnoreCase(planetName)) {
                double distanceFromSun = planet.optDouble("DistanceFromSun", -1);
                double orbitalPeriod = planet.optDouble("OrbitalPeriod", (distanceFromSun > 0 ? Math.pow(distanceFromSun, 1.5) : -1));
                double diameter = planet.optDouble("Diameter", -1);
                double circumference = planet.optDouble("Circumference", (diameter > 0 ? Math.PI * diameter : -1));

                System.out.printf("\nPlanet: %s\n", planetName);
                System.out.printf("Distance from Sun: %.2f AU\n", distanceFromSun);
                System.out.printf("Orbital Period: %.2f years\n", orbitalPeriod);
                System.out.printf("Diameter: %.2f km\n", diameter);
                System.out.printf("Circumference: %.2f km\n", circumference);

                if (planet.has("Moons")) {
                    JSONArray moonsArray = planet.getJSONArray("Moons");
                    System.out.println("Moons:");
                    for (int j = 0; j < moonsArray.length(); j++) {
                        JSONObject moon = moonsArray.getJSONObject(j);
                        String moonName = moon.getString("Name");
                        double moonDiameter = moon.optDouble("Diameter", -1);
                        double moonCircumference = moon.optDouble("Circumference", (moonDiameter > 0 ? Math.PI * moonDiameter : -1));

                        System.out.printf("  Moon: %s\n", moonName);
                        System.out.printf("  Diameter: %.2f km\n", moonDiameter);
                        System.out.printf("  Circumference: %.2f km\n", moonCircumference);
                    }
                }
                return;
            }
        }
        System.out.println("Planet not found! Did you mean: " + suggestClosestPlanet(planetName) + "?");
    }

    private static void checkPlanetVolumeVsSun(JSONArray planetsArray, double sunDiameter) {
        double totalSelectedVolume = 0;
        for (String planetName : entries) {
            for (int i = 0; i < planetsArray.length(); i++) {
                JSONObject planet = planetsArray.getJSONObject(i);
                if (planet.getString("Name").equalsIgnoreCase(planetName)) {
                    double diameter = planet.optDouble("Diameter", -1);
                    if (diameter > 0) {
                        totalSelectedVolume += calculateVolume(diameter);
                    }
                    break;
                }
            }
        }
        double sunVolume = calculateVolume(sunDiameter);

        System.out.println("\nTotal volume of selected planets: " + totalSelectedVolume + " cubic km");
        System.out.println("Volume of the Sun: " + sunVolume + " cubic km");
        System.out.println("Can the selected planets fit inside the Sun? " + (totalSelectedVolume < sunVolume));
    }

    private static double calculateVolume(double diameter) {
        double radius = diameter / 2.0;
        return (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
    }

    private static String suggestClosestPlanet(String input) {
        int minDistance = Integer.MAX_VALUE;
        String closestMatch = "unknown";
        for (String planet : planetNames) {
            int distance = levenshteinDistance(input.toLowerCase(), planet.toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = planet;
            }
        }
        return closestMatch;
    }

    private static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else dp[i][j] = Math.min(dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
            }
        }
        return dp[a.length()][b.length()];
    }
}
