public class TestArtist {
    public static void main(String[] args) {
        Artist artist = new Artist("Jane Doe", 40.0, ExperienceLevel.EXPERIENCED);

        System.out.println("Artist created:");
        System.out.println(artist);

        System.out.println("\nExperience multiplier: " + artist.getExperienceMultiplier());

        artist.setHourlyRate(55.0);
        artist.setExperienceLevel(ExperienceLevel.EXPERT);

        System.out.println("\nAfter updates:");
        System.out.println(artist);
        System.out.println("New multiplier: " + artist.getExperienceMultiplier());
    }
}
